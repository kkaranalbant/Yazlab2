/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.service;

import com.kaan.smarteventplanningplatform.dto.request.LoginRequest;
import com.kaan.smarteventplanningplatform.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 *
 * @author kaan
 */
@Service
public class LoginService {

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;

    private UserService userService;

    public LoginService(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Transactional
    public void login(LoginRequest loginRequest, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        User user = (User) userService.loadUserByUsername(loginRequest.username());
        String token = jwtService.getTokenByUserId(user.getId());
        Cookie cookie = new Cookie("Authorization", "Bearer+" + token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);
        String cookieHeader = response.getHeader("Set-Cookie");
        response.setHeader("Set-Cookie", cookieHeader + "; SameSite=None");
    }
}
