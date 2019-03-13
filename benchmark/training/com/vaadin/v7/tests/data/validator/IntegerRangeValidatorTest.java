package com.vaadin.v7.tests.data.validator;


import com.vaadin.v7.data.validator.IntegerRangeValidator;
import org.junit.Assert;
import org.junit.Test;


public class IntegerRangeValidatorTest {
    private IntegerRangeValidator cleanValidator = new IntegerRangeValidator("no values", null, null);

    private IntegerRangeValidator minValidator = new IntegerRangeValidator("no values", 10, null);

    private IntegerRangeValidator maxValidator = new IntegerRangeValidator("no values", null, 100);

    private IntegerRangeValidator minMaxValidator = new IntegerRangeValidator("no values", 10, 100);

    @Test
    public void testNullValue() {
        Assert.assertTrue("Didn't accept null", cleanValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", maxValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    @Test
    public void testMinValue() {
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid((-15)));
        Assert.assertTrue("Didn't accept valid value", minValidator.isValid(15));
        Assert.assertFalse("Accepted too small value", minValidator.isValid(9));
    }

    @Test
    public void testMaxValue() {
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid(1120));
        Assert.assertTrue("Didn't accept valid value", maxValidator.isValid(15));
        Assert.assertFalse("Accepted too large value", maxValidator.isValid(120));
    }

    @Test
    public void testMinMaxValue() {
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(15));
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(99));
        Assert.assertFalse("Accepted too small value", minMaxValidator.isValid(9));
        Assert.assertFalse("Accepted too large value", minMaxValidator.isValid(110));
    }
}

