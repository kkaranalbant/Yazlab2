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
public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final int minLength;

    private static final int maxLength;

    private static final char[] specialCharacters;

    static {
        minLength = 8;
        maxLength = 24;
        specialCharacters = new char[]{'@', '[', ']', '.', '{', '}', '!', '#', '$', '&'};
    }

    @Override
    public boolean isValid(String t, ConstraintValidatorContext cvc) {
        if (minLength > t.length() || t.length() > maxLength) {
            return false;
        }
        boolean foundSpecialCharacter = false;
        for (char character : t.toCharArray()) {
            for (char specialCharacter : specialCharacters) {
                if (character == specialCharacter) {
                    foundSpecialCharacter = true;
                    break;
                }
            }
            if (foundSpecialCharacter) {
                break;
            }
        }
        return foundSpecialCharacter;
    }

}

