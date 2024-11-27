/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.dto.request.user;

import com.kaan.smarteventplanningplatform.validation.Password;

/**
 *
 * @author kaan
 */
public record PasswordResetRequest (@Password String password){
    
}
