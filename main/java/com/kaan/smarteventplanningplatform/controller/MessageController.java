/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.controller;

import com.kaan.smarteventplanningplatform.dto.request.message.MessageIdRequest;
import com.kaan.smarteventplanningplatform.dto.request.message.MessageSendingRequest;
import com.kaan.smarteventplanningplatform.dto.request.message.MessageUpdatingRequest;
import com.kaan.smarteventplanningplatform.dto.response.message.MessageResponse;
import com.kaan.smarteventplanningplatform.service.JwtService;
import com.kaan.smarteventplanningplatform.service.MessageService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author kaan
 */
@RestController
@RequestMapping("/v1/message")
public class MessageController {

    private MessageService messageService;

    private JwtService jwtService;

    public MessageController(MessageService messageService, JwtService jwtService) {
        this.messageService = messageService;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/send")
    public ResponseEntity<String> add(@RequestBody MessageSendingRequest messageSendingRequest, @CookieValue("Authorization") String encodedJwt) {
        messageService.add(messageSendingRequest, jwtService.getUserIdByNotParsedToken(encodedJwt));
        return ResponseEntity.ok("Successful Sending Process");
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody MessageIdRequest messageIdRequest, @CookieValue("Authorization") String encodedJwt) {
        messageService.delete(messageIdRequest, jwtService.getUserIdByNotParsedToken(encodedJwt));
        return ResponseEntity.ok("Successful Deleting Process");
    }
    
    @DeleteMapping("/delete-exec")
    public ResponseEntity<String> deleteForExecutive(@RequestBody MessageIdRequest messageIdRequest, @RequestParam Long userId) {
        messageService.delete(messageIdRequest, userId);
        return ResponseEntity.ok("Successful Deleting Process");
    }
    
    @GetMapping("/get")
    public ResponseEntity<MessageResponse> getForFrontEnd(@RequestBody MessageIdRequest messageIdRequest) {
        return ResponseEntity.ok(messageService.getForFrontEnd(messageIdRequest));
    }
    
    @GetMapping("/get-all")
    public ResponseEntity<List<MessageResponse>> getAllForEvent(@RequestParam Long eventId) {
        return ResponseEntity.ok(messageService.getAllForEvent(eventId));
    }
    
    @GetMapping("/get-all-exec")
    public ResponseEntity<List<MessageResponse>> getAllForUserExecutive(@RequestParam Long userId) {
        return ResponseEntity.ok(messageService.getAllForUser(userId));
    }
    
    @GetMapping("/get-all-user")
    public ResponseEntity<List<MessageResponse>> getAllForUser(@CookieValue("Authorization") String encodedJwt) {
        return ResponseEntity.ok(messageService.getAllForUser(jwtService.getUserIdByNotParsedToken(encodedJwt)));
    }
    
    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody MessageUpdatingRequest messageUpdatingRequest, @CookieValue("Authorization") String encodedJwt) {
        messageService.update(messageUpdatingRequest, jwtService.getUserIdByNotParsedToken(encodedJwt));
        return ResponseEntity.ok("Successful Updating Process");
    }

}
