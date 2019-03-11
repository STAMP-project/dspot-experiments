package com.baeldung.string.interview;


import org.junit.Assert;
import org.junit.Test;


public class StringChangeCaseUnitTest {
    @Test
    public void givenString_whenChangingToUppercase_thenCaseChanged() {
        String s = "Welcome to Baeldung!";
        Assert.assertEquals("WELCOME TO BAELDUNG!", s.toUpperCase());
    }

    @Test
    public void givenString_whenChangingToLowerrcase_thenCaseChanged() {
        String s = "Welcome to Baeldung!";
        Assert.assertEquals("welcome to baeldung!", s.toLowerCase());
    }
}

