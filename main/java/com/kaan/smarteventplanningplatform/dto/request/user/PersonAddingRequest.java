/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.dto.request.user;

import com.kaan.smarteventplanningplatform.model.Gender;
import com.kaan.smarteventplanningplatform.validation.BirthDate;
import com.kaan.smarteventplanningplatform.validation.Password;
import com.kaan.smarteventplanningplatform.validation.Username;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 *
 * @author kaan
 */
public record PersonAddingRequest(@Size(min = 2, message = "Please Enter At Least 2 Character For Name")
        String name,
        @Size(min = 2, message = "Please Enter At Least 2 Character For Lastname")
        String lastname, @Email
        String email, @Username
        String username,
        @Password
        String password,
        String rePassword, @BirthDate(age = 13)
        LocalDate birthDate, Gender gender, String location, String phoneNumber, byte[] image) {

}
