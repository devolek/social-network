package com.skillbox.socialnetwork.main.controller;

import com.skillbox.socialnetwork.main.dto.files.request.FileRequestDto;
import com.skillbox.socialnetwork.main.dto.profile.request.ContactSupportRequestDto;
import com.skillbox.socialnetwork.main.security.jwt.JwtUser;
import com.skillbox.socialnetwork.main.service.FileService;
import com.skillbox.socialnetwork.main.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class ApiGeneralController {
    private final FileService fileService;
    private final ProfileService profileService;

    @Autowired
    public ApiGeneralController(FileService fileService, ProfileService profileService) {
        this.fileService = fileService;
        this.profileService = profileService;
    }


    @PostMapping("/storage")
    public ResponseEntity<?> saveFile(@AuthenticationPrincipal JwtUser user,
                                      FileRequestDto fileRequest) throws IOException {
        return ResponseEntity.ok(fileService.saveFile(user.getEmail(), fileRequest));
    }

    @PostMapping("/support/message")
    public ResponseEntity<?> contactSupport(@RequestBody ContactSupportRequestDto requestDto) {
        return ResponseEntity.ok(profileService.sendMessageToSupport(requestDto));
    }
}
