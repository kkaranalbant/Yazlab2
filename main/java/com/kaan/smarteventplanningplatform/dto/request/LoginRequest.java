/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author kaan
 */
public record LoginRequest (@NotBlank String username , @NotBlank String password) {
    
}
