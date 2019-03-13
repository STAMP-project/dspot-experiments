package com.vaadin.v7.tests.data.validator;


import com.vaadin.v7.data.validator.ShortRangeValidator;
import org.junit.Assert;
import org.junit.Test;


public class ShortRangeValidatorTest {
    private ShortRangeValidator cleanValidator = new ShortRangeValidator("no values", null, null);

    private ShortRangeValidator minValidator = new ShortRangeValidator("no values", ((short) (10)), null);

    private ShortRangeValidator maxValidator = new ShortRangeValidator("no values", null, ((short) (100)));

    private ShortRangeValidator minMaxValidator = new ShortRangeValidator("no values", ((short) (10)), ((short) (100)));

    @Test
    public void testNullValue() {
        Assert.assertTrue("Didn't accept null", cleanValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", maxValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    @Test
    public void testMinValue() {
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid(((short) (-15))));
        Assert.assertTrue("Didn't accept valid value", minValidator.isValid(((short) (15))));
        Assert.assertFalse("Accepted too small value", minValidator.isValid(((short) (9))));
    }

    @Test
    public void testMaxValue() {
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid(((short) (1120))));
        Assert.assertTrue("Didn't accept valid value", maxValidator.isValid(((short) (15))));
        Assert.assertFalse("Accepted too large value", maxValidator.isValid(((short) (120))));
    }

    @Test
    public void testMinMaxValue() {
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(((short) (15))));
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(((short) (99))));
        Assert.assertFalse("Accepted too small value", minMaxValidator.isValid(((short) (9))));
        Assert.assertFalse("Accepted too large value", minMaxValidator.isValid(((short) (110))));
    }
}

