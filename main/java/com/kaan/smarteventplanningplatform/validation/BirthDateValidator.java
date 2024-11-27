/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 *
 * @author kaan
 */
public class BirthDateValidator implements ConstraintValidator<BirthDate, LocalDate> {

    private int ageBound;

    @Override
    public void initialize(BirthDate constraintAnnotation) {
        ageBound = constraintAnnotation.age();
    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext cvc) {

        LocalDate now = LocalDate.now();
        boolean isAgeGreaterThanBound = false;
        if (now.getMonthValue() > birthDate.getMonthValue()) {
            isAgeGreaterThanBound = now.getYear() - birthDate.getYear() > ageBound;
        } else if (now.getMonthValue() == birthDate.getMonthValue()) {
            isAgeGreaterThanBound = now.getYear() - birthDate.getYear() > ageBound && now.getDayOfMonth() >= birthDate.getDayOfMonth();
        } else {
            isAgeGreaterThanBound = now.getYear() - birthDate.getYear() >= (ageBound + 1);
        }
        return isAgeGreaterThanBound;
    }

}
