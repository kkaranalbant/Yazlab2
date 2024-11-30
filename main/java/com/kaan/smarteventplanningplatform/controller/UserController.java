/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.controller;

import com.kaan.smarteventplanningplatform.dto.request.PasswordUpdatingRequest;
import com.kaan.smarteventplanningplatform.dto.request.user.PersonAddingRequest;
import com.kaan.smarteventplanningplatform.dto.request.user.PasswordResetControlRequest;
import com.kaan.smarteventplanningplatform.dto.request.user.PasswordResetRequest;
import com.kaan.smarteventplanningplatform.dto.request.user.UserUpdatingRequest;
import com.kaan.smarteventplanningplatform.dto.response.UserResponse;
import com.kaan.smarteventplanningplatform.service.JwtService;
import com.kaan.smarteventplanningplatform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author kaan
 */
@RestController
@RequestMapping("/v1/user")
public class UserController {

    private UserService userService;
    private JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    //Admin icin
    @GetMapping("/get-exec")
    public ResponseEntity<UserResponse> getUserExecutive(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    //digerileri icin
    @GetMapping("/get")
    public ResponseEntity<UserResponse> getUserNormal(@CookieValue("Authorization") String encodedJwt) {
        Long actorId = jwtService.getUserIdByNotParsedToken(encodedJwt);
        return ResponseEntity.ok(userService.getUserByIdForOutside(actorId, actorId));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody PersonAddingRequest personAddingRequest) {
        userService.addUser(personAddingRequest);
        return ResponseEntity.ok("You have successfully registered");
    }

    @GetMapping("/lock")
    public ResponseEntity<String> lockUserAccount(@RequestParam Long userId) {
        userService.lockUserAccount(userId);
        return ResponseEntity.ok("Account Locked");
    }

    @GetMapping("/unlock")
    public ResponseEntity<String> unlockUserAccount(@RequestParam Long userId) {
        userService.unlockUserAccount(userId);
        return ResponseEntity.ok("Account Locked");
    }

    @PostMapping("/update-pass")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdatingRequest passwordUpdatingRequest, @CookieValue("Authorization") String encodedJwt) {
        userService.updatePassword(jwtService.getUserIdByNotParsedToken(encodedJwt), passwordUpdatingRequest);
        return ResponseEntity.ok("You Updated Password");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateAccountForNormalUsers(@RequestBody UserUpdatingRequest userUpdatingRequest, @CookieValue("Authorization") String encodedJwt) {
        userService.update(jwtService.getUserIdByNotParsedToken(encodedJwt), userUpdatingRequest);
        return ResponseEntity.ok("Successful Updating Process");
    }

    @PostMapping("/update-exec")
    public ResponseEntity<String> updateAccountForAdmin(@RequestBody UserUpdatingRequest userUpdatingRequest, @RequestParam Long userId) {
        userService.update(userId, userUpdatingRequest);
        return ResponseEntity.ok("Successful Updating Process");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@CookieValue("Authorization") String encodedJwt) {
        userService.deleteAccount(jwtService.getUserIdByNotParsedToken(encodedJwt));
        return ResponseEntity.ok("Successful");
    }

    @PostMapping("/delete-exec")
    public ResponseEntity<String> deleteAccountForAdmin(@RequestParam Long userId) {
        userService.deleteAccount(userId);
        return ResponseEntity.ok("Successful");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> passwordResetControl(@RequestBody PasswordResetControlRequest passwordResetControlRequest) {
        userService.sendPasswordResetLink(passwordResetControlRequest);
        return ResponseEntity.ok("We sent a recovery url to your email");
    }

    @GetMapping("/reset-password")
    public ModelAndView getPasswordResetPanel(@RequestParam String verify) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("verify", verify);
        mv.setViewName("recovery");
        return mv;
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        userService.resetPassword(passwordResetRequest);
        return ResponseEntity.ok("You Changed Your Password");
    }

}
