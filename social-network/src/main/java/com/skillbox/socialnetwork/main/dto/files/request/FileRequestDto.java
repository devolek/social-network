package com.skillbox.socialnetwork.main.dto.files.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileRequestDto {
    private String type;
    private MultipartFile file;
}
