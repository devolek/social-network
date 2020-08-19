package com.skillbox.socialnetwork.main.controller;

import com.skillbox.socialnetwork.main.dto.dialog.DialogAddRequest;
import com.skillbox.socialnetwork.main.dto.dialog.LongpollHistoryRequest;
import com.skillbox.socialnetwork.main.dto.dialog.response.MessageTextDto;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.service.AuthService;
import com.skillbox.socialnetwork.main.service.DialogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dialogs")
public class DialogController {

    private final DialogService dialogService;
    private final AuthService authService;

    @Autowired
    public DialogController(DialogService dialogService, AuthService authService)
    {
        this.dialogService = dialogService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<BaseResponseList> getAllMessages(
            @RequestParam(name = "query", defaultValue = "", required = false) String query,
            @RequestParam(name = "offset", defaultValue = "0", required = false) Integer offset,
            @RequestParam(name = "itemPerPage", defaultValue = "20", required = false) Integer limit,
            @RequestHeader(name = "Authorization") String token
    ) {
        return ResponseEntity.ok(dialogService.getDialogs(query, offset, limit, authService.getAuthorizedUser(token)));
    }

    @PostMapping
    public ResponseEntity<?> addDialog(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody DialogAddRequest userIds)
    {
        return ResponseEntity.ok(dialogService.addDialog(userIds, authService.getAuthorizedUser(token)));
    }

    @GetMapping("unreaded")
    public ResponseEntity<?> getUnreadMessages(
            @RequestHeader(name = "Authorization") String token)
    {
        return ResponseEntity.ok(dialogService.getUnreadMessages(authService.getAuthorizedUser(token)));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteDialog(@PathVariable int id)
    {
        return ResponseEntity.ok(dialogService.deleteDialog(id));
    }

    @PutMapping("{id}/users")
    public ResponseEntity<?> addUserToDialog(@PathVariable int id, DialogAddRequest request)
    {
        return ResponseEntity.ok(dialogService.addUsersToDialog(request));
    }

    @DeleteMapping("{id}/users")
    public ResponseEntity<BaseResponse> deleteUsersFromDialog(@PathVariable int id, DialogAddRequest request)
    {
        return ResponseEntity.ok(dialogService.deleteUsersFromDialog(id, request));
    }

    @GetMapping("{id}/messages")
    public ResponseEntity<BaseResponseList> getDialogMessages(
            @PathVariable int id,
            @RequestParam(name = "query", defaultValue = "", required = false) String query,
            @RequestParam(name = "fromMessageId", defaultValue = "0", required = false) Integer fromMessageId,
            @RequestParam(name = "offset", defaultValue = "0", required = false) Integer offset,
            @RequestParam(name = "itemPerPage", defaultValue = "20", required = false) Integer limit,
            @RequestHeader(name = "Authorization") String token)
    {
        return ResponseEntity.ok(dialogService.getMessagesFromDialog(id, query, offset, limit, fromMessageId, authService.getAuthorizedUser(token)));
    }

    @PostMapping("{id}/messages")
    public ResponseEntity<BaseResponse> addMessage(
            @PathVariable int id,
            @RequestBody MessageTextDto message,
            @RequestHeader("Authorization") String token)
    {
        return ResponseEntity.ok(dialogService.addMessage(id, message, authService.getAuthorizedUser(token)));
    }

    @DeleteMapping("{dialogId}/messages/{messageId}")
    public ResponseEntity<BaseResponse> deleteMessage(
            @PathVariable int dialogId,
            @PathVariable int messageId)
    {
        return ResponseEntity.ok(dialogService.deleteMessage(dialogId, messageId));
    }

    @PutMapping("{dialogId}/messages/{messageId}")
    public ResponseEntity<BaseResponse> editMessage(
            @PathVariable int dialogId,
            @PathVariable int messageId,
            @RequestBody MessageTextDto request,
            @RequestHeader(name = "Authorization") String token)
    {
        return ResponseEntity.ok(dialogService.editMessage(dialogId, messageId, request, authService.getAuthorizedUser(token)));
    }

    @PutMapping("{dialogId}/messages/{messageId}/recover")
    public ResponseEntity<BaseResponse> recoverMessage(
            @PathVariable int dialogId,
            @PathVariable int messageId)
    {
        return ResponseEntity.ok(dialogService.recoverMessage(dialogId, messageId));
    }

    @PutMapping("{dialogId}/messages/{messageId}/read")
    public ResponseEntity<BaseResponse> readMessage(
            @PathVariable int dialogId,
            @PathVariable int messageId,
            @RequestHeader(name = "Authorization") String token)
    {
        return ResponseEntity.ok(dialogService.markMessageAsRead(dialogId, messageId, authService.getAuthorizedUser(token)));
    }

    @GetMapping("{dialogId}/activity/{userId}")
    public ResponseEntity<BaseResponse> getActivityStatus(
            @PathVariable int dialogId,
            @PathVariable int userId)
    {
        return ResponseEntity.ok(dialogService.getUserActivityStatus(dialogId, userId));
    }

    @PostMapping("{dialogId}/activity/{userId}")
    public ResponseEntity<BaseResponse> changeActivityStatus(
            @PathVariable int dialogId,
            @PathVariable int userId)
    {
        return ResponseEntity.ok(dialogService.changeUserActivityStatus(dialogId, userId));
    }

    @GetMapping("longpoll")
    public ResponseEntity<BaseResponse> getLongPoll()
    {
        return ResponseEntity.ok(dialogService.getLongPollCredentials());
    }

    @PostMapping("longpoll/history")
    public ResponseEntity<BaseResponse> changeLongPollHistory(
            @RequestBody LongpollHistoryRequest request)
    {
        return ResponseEntity.ok(dialogService.getLongPollHistory(request));
    }
}
