package com.vaadin.v7.tests.data.validator;


import com.vaadin.v7.data.validator.RegexpValidator;
import org.junit.Assert;
import org.junit.Test;


public class RegexpValidatorTest {
    private RegexpValidator completeValidator = new RegexpValidator("pattern", true, "Complete match validator error");

    private RegexpValidator partialValidator = new RegexpValidator("pattern", false, "Partial match validator error");

    @Test
    public void testRegexpValidatorWithNull() {
        Assert.assertTrue(completeValidator.isValid(null));
        Assert.assertTrue(partialValidator.isValid(null));
    }

    @Test
    public void testRegexpValidatorWithEmptyString() {
        Assert.assertTrue(completeValidator.isValid(""));
        Assert.assertTrue(partialValidator.isValid(""));
    }

    @Test
    public void testCompleteRegexpValidatorWithFaultyString() {
        Assert.assertFalse(completeValidator.isValid("mismatch"));
        Assert.assertFalse(completeValidator.isValid("pattern2"));
        Assert.assertFalse(completeValidator.isValid("1pattern"));
    }

    @Test
    public void testCompleteRegexpValidatorWithOkString() {
        Assert.assertTrue(completeValidator.isValid("pattern"));
    }

    @Test
    public void testPartialRegexpValidatorWithFaultyString() {
        Assert.assertFalse(partialValidator.isValid("mismatch"));
    }

    @Test
    public void testPartialRegexpValidatorWithOkString() {
        Assert.assertTrue(partialValidator.isValid("pattern"));
        Assert.assertTrue(partialValidator.isValid("1pattern"));
        Assert.assertTrue(partialValidator.isValid("pattern2"));
        Assert.assertTrue(partialValidator.isValid("1pattern2"));
    }
}

