package com.skillbox.socialnetwork.main.dto.files.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.model.enumerated.FileType;
import lombok.Data;

import java.util.Date;

@Data
public class FileResponseDto implements Dto {
    private String id;

    private int ownerId;

    private String fileName;

    private String relativeFilePath;

    @JsonProperty("raw_file_URL")
    private String rawFileURL;

    private String fileFormat;

    private long bytes;

    private FileType fileType;

    private Date createdAt;

    public FileResponseDto(String id, int ownerId, String fileName, String relativeFilePath,
                           String rawFileURL, String fileFormat, long bytes,
                           FileType fileType, Date createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.fileName = fileName;
        this.relativeFilePath = relativeFilePath;
        this.rawFileURL = rawFileURL;
        this.fileFormat = fileFormat;
        this.bytes = bytes;
        this.fileType = fileType;
        this.createdAt = createdAt;
    }

    public FileResponseDto() {
    }
}
