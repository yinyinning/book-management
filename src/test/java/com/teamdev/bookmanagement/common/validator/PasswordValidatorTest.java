package com.teamdev.bookmanagement.common.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    private final PasswordValidator validator=new PasswordValidator();

    @Test
    void isValid_正常_返回true() {
        assertTrue(validator.isValid("Abc123",null));
    }

    @Test
    void isValid_null_返回true() {
        assertTrue(validator.isValid(null,null));
    }

    @Test
    void isValid_空白_返回true() {
        assertTrue(validator.isValid("   ",null));
    }

    @Test
    void isValid_无大写_返回false() {
        assertFalse(validator.isValid("abc123",null));
    }

    @Test
    void isValid_无小写_返回false() {
        assertFalse(validator.isValid("ABC123",null));
    }

    @Test
    void isValid_无数字_返回false() {
        assertFalse(validator.isValid("AbcAbc",null));
    }
}