package com.vaadin.v7.tests.data.validator;


import com.vaadin.v7.data.validator.LongRangeValidator;
import org.junit.Assert;
import org.junit.Test;


public class LongRangeValidatorTest {
    private LongRangeValidator cleanValidator = new LongRangeValidator("no values", null, null);

    private LongRangeValidator minValidator = new LongRangeValidator("no values", 10L, null);

    private LongRangeValidator maxValidator = new LongRangeValidator("no values", null, 100L);

    private LongRangeValidator minMaxValidator = new LongRangeValidator("no values", 10L, 100L);

    @Test
    public void testNullValue() {
        Assert.assertTrue("Didn't accept null", cleanValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", maxValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    @Test
    public void testMinValue() {
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid((-15L)));
        Assert.assertTrue("Didn't accept valid value", minValidator.isValid(15L));
        Assert.assertFalse("Accepted too small value", minValidator.isValid(9L));
    }

    @Test
    public void testMaxValue() {
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid(1120L));
        Assert.assertTrue("Didn't accept valid value", maxValidator.isValid(15L));
        Assert.assertFalse("Accepted too large value", maxValidator.isValid(120L));
    }

    @Test
    public void testMinMaxValue() {
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(15L));
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(99L));
        Assert.assertFalse("Accepted too small value", minMaxValidator.isValid(9L));
        Assert.assertFalse("Accepted too large value", minMaxValidator.isValid(110L));
    }
}

