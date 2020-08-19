package com.skillbox.socialnetwork.main.service.impl;

import com.skillbox.socialnetwork.main.aspect.MethodLogWithTime;
import com.skillbox.socialnetwork.main.dto.files.request.FileRequestDto;
import com.skillbox.socialnetwork.main.dto.files.response.FileResponseDto;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.dto.universal.Response;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.exception.InvalidRequestException;
import com.skillbox.socialnetwork.main.model.UploadFile;
import com.skillbox.socialnetwork.main.model.enumerated.FileType;
import com.skillbox.socialnetwork.main.repository.FileRepository;
import com.skillbox.socialnetwork.main.security.jwt.JwtTokenProvider;
import com.skillbox.socialnetwork.main.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final PersonServiceImpl personService;
    @Value("${upload.path}")
    private String uploadPath;

    public FileServiceImpl(FileRepository fileRepository, PersonServiceImpl personService, JwtTokenProvider jwtTokenProvider) {
        this.fileRepository = fileRepository;
        this.personService = personService;
    }

    @MethodLogWithTime
    @Override
    public Response saveFile(String userEmail, FileRequestDto fileRequest) throws IOException {
        switch (fileRequest.getType()) {
            case "IMAGE": {
                return ResponseFactory.getBaseResponse(saveImage(userEmail, fileRequest.getFile()));
            }
            default:
                throw new InvalidRequestException("Неизвестный тип файла");
        }
    }

    @Override
    public Dto saveImage(String userEmail, MultipartFile file) throws IOException {
        File resizeDir = new File(uploadPath + "/img/resize");
        File fullDir = new File(uploadPath + "/img/full");
        if (!resizeDir.exists()) {
            resizeDir.mkdirs();
        }
        if (!fullDir.exists()) {
            fullDir.mkdirs();
        }

        String id = UUID.randomUUID().toString();
        int ownerId = personService.findByEmail(userEmail).getId();
        String fullFileName = file.getOriginalFilename();
        String fileName = fullFileName.substring(0, fullFileName.lastIndexOf("."));
        String fileFormat = fullFileName.substring(fullFileName.lastIndexOf(".") + 1);

        String relativeFilePath = uploadPath + "/img/resize/" + id + "." + fileName + "." + fileFormat;
        String rawFileURL = uploadPath + "/img/full/" + id + "." + fileName + "." + fileFormat;
        long bytes = file.getSize();
        FileType fileType = FileType.IMAGE;

        File fullFile = new File(rawFileURL);

        file.transferTo(fullFile);
        resizeImage(fullFile, relativeFilePath, fileFormat);

        relativeFilePath = "/img/resize/" + id + "." + fullFileName;
        rawFileURL = "/img/full/" + id + "." + fullFileName;


        UploadFile uploadFile = new UploadFile();
        uploadFile.setId(id);
        uploadFile.setOwnerId(ownerId);
        uploadFile.setFileName(fileName);
        uploadFile.setRelativeFilePath(relativeFilePath);
        uploadFile.setRawFileURL(rawFileURL);
        uploadFile.setFileFormat(fileFormat);
        uploadFile.setBytes(bytes);
        uploadFile.setFileFormat(fileFormat);
        uploadFile.setFileType(fileType);
        fileRepository.save(uploadFile);


        return new FileResponseDto(id, ownerId, fileName, relativeFilePath,
                rawFileURL, fileFormat, bytes, fileType, new Date());
    }

    @Override
    public void resizeImage(File file, String dstFolder, String fileFormat) throws IOException {
        BufferedImage image = ImageIO.read(file);

        int newWidth = 300;
        int newHeight = (int) Math.round(
                image.getHeight() / (image.getWidth() / (double) newWidth)
        );

        BufferedImage scaledImg = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH,
                newWidth * 4, newHeight * 4, Scalr.OP_ANTIALIAS);
        BufferedImage scaledImg2 = Scalr.resize(scaledImg, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH,
                newWidth, newHeight, Scalr.OP_ANTIALIAS);

        File newFile = new File(dstFolder);
        ImageIO.write(scaledImg2, fileFormat, newFile);
    }
}
