package com.vaadin.v7.tests.data.validator;


import com.vaadin.v7.data.validator.FloatRangeValidator;
import org.junit.Assert;
import org.junit.Test;


public class FloatRangeValidatorTest {
    private FloatRangeValidator cleanValidator = new FloatRangeValidator("no values", null, null);

    private FloatRangeValidator minValidator = new FloatRangeValidator("no values", 10.1F, null);

    private FloatRangeValidator maxValidator = new FloatRangeValidator("no values", null, 100.1F);

    private FloatRangeValidator minMaxValidator = new FloatRangeValidator("no values", 10.5F, 100.5F);

    @Test
    public void testNullValue() {
        Assert.assertTrue("Didn't accept null", cleanValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", maxValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    @Test
    public void testMinValue() {
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid((-15.0F)));
        Assert.assertTrue("Didn't accept valid value", minValidator.isValid(10.1F));
        Assert.assertFalse("Accepted too small value", minValidator.isValid(10.0F));
    }

    @Test
    public void testMaxValue() {
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid(1120.0F));
        Assert.assertTrue("Didn't accept valid value", maxValidator.isValid(15.0F));
        Assert.assertFalse("Accepted too large value", maxValidator.isValid(100.6F));
    }

    @Test
    public void testMinMaxValue() {
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(10.5F));
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(100.5F));
        Assert.assertFalse("Accepted too small value", minMaxValidator.isValid(10.4F));
        Assert.assertFalse("Accepted too large value", minMaxValidator.isValid(100.6F));
    }
}

