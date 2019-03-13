package com.vaadin.v7.tests.data.validator;


import Validator.InvalidValueException;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.NullValidator;
import org.junit.Assert;
import org.junit.Test;


public class NullValidatorTest {
    NullValidator notNull = new NullValidator("Null not accepted", false);

    NullValidator onlyNull = new NullValidator("Only null accepted", true);

    @Test
    public void testNullValue() {
        try {
            notNull.validate(null);
            Assert.fail("expected null to fail with an exception");
        } catch (Validator ex) {
            Assert.assertEquals("Null not accepted", ex.getMessage());
        }
        try {
            onlyNull.validate(null);
        } catch (Validator ex) {
            Assert.fail("onlyNull should not throw exception for null");
        }
    }

    @Test
    public void testNonNullValue() {
        try {
            onlyNull.validate("Not a null value");
            Assert.fail("expected onlyNull validator to fail with an exception");
        } catch (Validator ex) {
            Assert.assertEquals("Only null accepted", ex.getMessage());
        }
        try {
            notNull.validate("Not a null value");
        } catch (Validator ex) {
            Assert.fail("notNull should not throw exception for \"Not a null value\"");
        }
    }
}

