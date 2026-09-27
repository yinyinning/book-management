package com.teamdev.bookmanagement.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext){
        if (value==null||value.isBlank()){
            return true;
        }
        boolean hasUpper=value.chars().anyMatch(Character::isUpperCase);
        boolean hasLower=value.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit=value.chars().anyMatch(Character::isDigit);
        return hasUpper&&hasLower&&hasDigit;
    }
}
