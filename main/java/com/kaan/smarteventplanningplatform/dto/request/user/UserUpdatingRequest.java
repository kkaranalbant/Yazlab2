/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.dto.request.user;

import com.kaan.smarteventplanningplatform.model.Gender;
import com.kaan.smarteventplanningplatform.validation.BirthDate;
import com.kaan.smarteventplanningplatform.validation.Username;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;

/**
 *
 * @author kaan
 */
public record UserUpdatingRequest (String name , String lastname , @Email String email 
        , @Username String username , @BirthDate LocalDate birthDate 
        , Gender gender , @NotEmpty String location , @NotEmpty String phoneNumber , byte [] image) {
    
}
