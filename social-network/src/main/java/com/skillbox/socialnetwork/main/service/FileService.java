package com.skillbox.socialnetwork.main.service;

import com.skillbox.socialnetwork.main.dto.files.request.FileRequestDto;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.dto.universal.Response;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileService {
    Response saveFile(String userEmail, FileRequestDto fileRequest) throws IOException;

    Dto saveImage(String userEmail, MultipartFile file) throws IOException;

    void resizeImage(File file, String dstFolder, String fileFormat) throws IOException;
}
