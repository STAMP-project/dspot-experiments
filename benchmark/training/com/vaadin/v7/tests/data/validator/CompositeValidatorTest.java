package com.vaadin.v7.tests.data.validator;


import Validator.InvalidValueException;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.CompositeValidator;
import com.vaadin.v7.data.validator.CompositeValidator.CombinationMode;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.data.validator.RegexpValidator;
import org.junit.Assert;
import org.junit.Test;


public class CompositeValidatorTest {
    CompositeValidator and = new CompositeValidator(CombinationMode.AND, "One validator not valid");

    CompositeValidator or = new CompositeValidator(CombinationMode.OR, "No validators are valid");

    EmailValidator email = new EmailValidator("Faulty email");

    RegexpValidator regex = new RegexpValidator("@mail.com", false, "Partial match validator error");

    @Test
    public void testCorrectValue() {
        String testString = "user@mail.com";
        Assert.assertTrue(email.isValid(testString));
        Assert.assertTrue(regex.isValid(testString));
        try {
            // notNull.validate(null);
            // fail("expected null to fail with an exception");
            and.validate(testString);
        } catch (Validator ex) {
            // assertEquals("Null not accepted", ex.getMessage());
            Assert.fail("And validator should be valid");
        }
        try {
            or.validate(testString);
        } catch (Validator ex) {
            // assertEquals("Null not accepted", ex.getMessage());
            Assert.fail("And validator should be valid");
        }
    }

    @Test
    public void testCorrectRegex() {
        String testString = "@mail.com";
        Assert.assertFalse((testString + " should not validate"), email.isValid(testString));
        Assert.assertTrue((testString + "should validate"), regex.isValid(testString));
        try {
            // notNull.validate(null);
            and.validate(testString);
            Assert.fail("expected and to fail with an exception");
        } catch (Validator ex) {
            Assert.assertEquals("Faulty email", ex.getMessage());
            // fail("And validator should be valid");
        }
        try {
            or.validate(testString);
        } catch (Validator ex) {
            // assertEquals("Null not accepted", ex.getMessage());
            Assert.fail("Or validator should be valid");
        }
    }

    @Test
    public void testCorrectEmail() {
        String testString = "user@gmail.com";
        Assert.assertTrue((testString + " should validate"), email.isValid(testString));
        Assert.assertFalse((testString + " should not validate"), regex.isValid(testString));
        try {
            and.validate(testString);
            Assert.fail("expected and to fail with an exception");
        } catch (Validator ex) {
            Assert.assertEquals("Partial match validator error", ex.getMessage());
        }
        try {
            or.validate(testString);
        } catch (Validator ex) {
            Assert.fail("Or validator should be valid");
        }
    }

    @Test
    public void testBothFaulty() {
        String testString = "gmail.com";
        Assert.assertFalse((testString + " should not validate"), email.isValid(testString));
        Assert.assertFalse((testString + " should not validate"), regex.isValid(testString));
        try {
            and.validate(testString);
            Assert.fail("expected and to fail with an exception");
        } catch (Validator ex) {
            Assert.assertEquals("Faulty email", ex.getMessage());
        }
        try {
            or.validate(testString);
            Assert.fail("expected or to fail with an exception");
        } catch (Validator ex) {
            Assert.assertEquals("No validators are valid", ex.getMessage());
        }
    }
}

