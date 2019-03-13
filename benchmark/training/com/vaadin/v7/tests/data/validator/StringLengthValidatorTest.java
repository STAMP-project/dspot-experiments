package com.vaadin.v7.tests.data.validator;


import com.vaadin.v7.data.validator.StringLengthValidator;
import org.junit.Assert;
import org.junit.Test;


public class StringLengthValidatorTest {
    private StringLengthValidator validator = new StringLengthValidator("Error");

    private StringLengthValidator validatorNoNull = new StringLengthValidator("Error", 1, 5, false);

    private StringLengthValidator validatorMinValue = new StringLengthValidator("Error", 5, null, true);

    private StringLengthValidator validatorMaxValue = new StringLengthValidator("Error", null, 15, true);

    @Test
    public void testValidatorWithNull() {
        Assert.assertTrue("Didn't accept null", validator.isValid(null));
        Assert.assertTrue("Didn't accept null", validatorMinValue.isValid(null));
    }

    @Test
    public void testValidatorNotAcceptingNull() {
        Assert.assertFalse("Accepted null", validatorNoNull.isValid(null));
    }

    @Test
    public void testEmptyString() {
        Assert.assertTrue("Didn't accept empty String", validator.isValid(""));
        Assert.assertTrue("Didn't accept empty String", validatorMaxValue.isValid(""));
        Assert.assertFalse("Accepted empty string even though has lower bound of 1", validatorNoNull.isValid(""));
        Assert.assertFalse("Accepted empty string even though has lower bound of 5", validatorMinValue.isValid(""));
    }

    @Test
    public void testTooLongString() {
        Assert.assertFalse("Too long string was accepted", validatorNoNull.isValid("This string is too long"));
        Assert.assertFalse("Too long string was accepted", validatorMaxValue.isValid("This string is too long"));
    }

    @Test
    public void testNoUpperBound() {
        Assert.assertTrue("String not accepted even though no upper bound", validatorMinValue.isValid("This is a really long string to test that no upper bound exists"));
    }

    @Test
    public void testNoLowerBound() {
        Assert.assertTrue("Didn't accept short string", validatorMaxValue.isValid(""));
        Assert.assertTrue("Didn't accept short string", validatorMaxValue.isValid("1"));
    }

    @Test
    public void testStringLengthValidatorWithOkStringLength() {
        Assert.assertTrue("Didn't accept string of correct length", validatorNoNull.isValid("OK!"));
        Assert.assertTrue("Didn't accept string of correct length", validatorMaxValue.isValid("OK!"));
    }

    @Test
    public void testTooShortStringLength() {
        Assert.assertFalse("Accepted a string that was too short.", validatorMinValue.isValid("shot"));
    }
}

