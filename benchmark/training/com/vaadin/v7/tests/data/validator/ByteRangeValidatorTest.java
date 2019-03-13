package com.vaadin.v7.tests.data.validator;


import com.vaadin.v7.data.validator.ByteRangeValidator;
import org.junit.Assert;
import org.junit.Test;


public class ByteRangeValidatorTest {
    private ByteRangeValidator cleanValidator = new ByteRangeValidator("no values", null, null);

    private ByteRangeValidator minValidator = new ByteRangeValidator("no values", ((byte) (10)), null);

    private ByteRangeValidator maxValidator = new ByteRangeValidator("no values", null, ((byte) (100)));

    private ByteRangeValidator minMaxValidator = new ByteRangeValidator("no values", ((byte) (10)), ((byte) (100)));

    @Test
    public void testNullValue() {
        Assert.assertTrue("Didn't accept null", cleanValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", maxValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    @Test
    public void testMinValue() {
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid(((byte) (-15))));
        Assert.assertTrue("Didn't accept valid value", minValidator.isValid(((byte) (15))));
        Assert.assertFalse("Accepted too small value", minValidator.isValid(((byte) (9))));
    }

    @Test
    public void testMaxValue() {
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid(((byte) (112))));
        Assert.assertTrue("Didn't accept valid value", maxValidator.isValid(((byte) (15))));
        Assert.assertFalse("Accepted too large value", maxValidator.isValid(((byte) (120))));
    }

    @Test
    public void testMinMaxValue() {
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(((byte) (15))));
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(((byte) (99))));
        Assert.assertFalse("Accepted too small value", minMaxValidator.isValid(((byte) (9))));
        Assert.assertFalse("Accepted too large value", minMaxValidator.isValid(((byte) (110))));
    }
}

