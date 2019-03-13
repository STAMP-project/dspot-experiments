package com.vaadin.v7.tests.server.validation;


import com.vaadin.v7.data.validator.IntegerRangeValidator;
import org.junit.Assert;
import org.junit.Test;


public class RangeValidatorTest {
    // This test uses IntegerRangeValidator for simplicity.
    // IntegerRangeValidator contains no code so we really are testing
    // RangeValidator
    @Test
    public void testMinValueNonInclusive() {
        IntegerRangeValidator iv = new IntegerRangeValidator("Failed", 0, 10);
        iv.setMinValueIncluded(false);
        Assert.assertFalse(iv.isValid(0));
        Assert.assertTrue(iv.isValid(10));
        Assert.assertFalse(iv.isValid(11));
        Assert.assertFalse(iv.isValid((-1)));
    }

    @Test
    public void testMinMaxValuesInclusive() {
        IntegerRangeValidator iv = new IntegerRangeValidator("Failed", 0, 10);
        Assert.assertTrue(iv.isValid(0));
        Assert.assertTrue(iv.isValid(1));
        Assert.assertTrue(iv.isValid(10));
        Assert.assertFalse(iv.isValid(11));
        Assert.assertFalse(iv.isValid((-1)));
    }

    @Test
    public void testMaxValueNonInclusive() {
        IntegerRangeValidator iv = new IntegerRangeValidator("Failed", 0, 10);
        iv.setMaxValueIncluded(false);
        Assert.assertTrue(iv.isValid(0));
        Assert.assertTrue(iv.isValid(9));
        Assert.assertFalse(iv.isValid(10));
        Assert.assertFalse(iv.isValid(11));
        Assert.assertFalse(iv.isValid((-1)));
    }

    @Test
    public void testMinMaxValuesNonInclusive() {
        IntegerRangeValidator iv = new IntegerRangeValidator("Failed", 0, 10);
        iv.setMinValueIncluded(false);
        iv.setMaxValueIncluded(false);
        Assert.assertFalse(iv.isValid(0));
        Assert.assertTrue(iv.isValid(1));
        Assert.assertTrue(iv.isValid(9));
        Assert.assertFalse(iv.isValid(10));
        Assert.assertFalse(iv.isValid(11));
        Assert.assertFalse(iv.isValid((-1)));
    }
}

