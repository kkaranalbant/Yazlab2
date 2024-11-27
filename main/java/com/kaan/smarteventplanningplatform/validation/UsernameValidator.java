/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 *
 * @author kaan
 */
public class UsernameValidator implements ConstraintValidator<Username, String> {

    private static final int minLength;

    private static final int maxLength;
    
    static {
        minLength = 6;

        maxLength = 20;
    }

    @Override
    public boolean isValid(String t, ConstraintValidatorContext cvc) {
        if (t.length() > maxLength || t.length() < minLength) {
            return false ;
        }
        boolean isAllNotSameCharacter = false ;
        for (int i = 0; (i < t.length() - 1)&&!isAllNotSameCharacter; i++) {
            if (t.charAt(i) != t.charAt(i+1)) {
                isAllNotSameCharacter = true ;
            }
        }
        return isAllNotSameCharacter ;
    }

}

