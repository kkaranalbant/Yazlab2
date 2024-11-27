/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.dto.response;

import com.kaan.smarteventplanningplatform.model.Gender;
import java.time.LocalDate;

/**
 *
 * @author kaan
 */
public record UserResponse(Long id, String name, String lastname, String email,
         String username, LocalDate birthDate, Gender gender,
         String Location, String phoneNumber, byte[] image) {

}
