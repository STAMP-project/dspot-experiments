package com.baeldung.validation;


import com.baeldung.model.User;
import java.io.File;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Assert;
import org.junit.Test;


public class ValidationIntegrationTest {
    private static ValidatorFactory validatorFactory;

    private static Validator validator;

    @Test
    public void givenUser_whenValidate_thenValidationViolations() {
        User user = new User("ana@yahoo.com", "pass", "nameTooLong_______________", 15);
        Set<ConstraintViolation<User>> violations = ValidationIntegrationTest.validator.validate(user);
        Assert.assertTrue("no violations", ((violations.size()) > 0));
    }

    @Test
    public void givenInvalidAge_whenValidateProperty_thenConstraintViolation() {
        User user = new User("ana@yahoo.com", "pass", "Ana", 12);
        Set<ConstraintViolation<User>> propertyViolations = ValidationIntegrationTest.validator.validateProperty(user, "age");
        Assert.assertEquals("size is not 1", 1, propertyViolations.size());
    }

    @Test
    public void givenValidAge_whenValidateValue_thenNoConstraintViolation() {
        User user = new User("ana@yahoo.com", "pass", "Ana", 18);
        Set<ConstraintViolation<User>> valueViolations = ValidationIntegrationTest.validator.validateValue(User.class, "age", 20);
        Assert.assertEquals("size is not 0", 0, valueViolations.size());
    }

    @Test
    public void whenValidateNonJSR_thenCorrect() {
        User user = new User("ana@yahoo.com", "pass", "Ana", 20);
        user.setCardNumber("1234");
        user.setIban("1234");
        user.setWebsite("10.0.2.50");
        user.setMainDirectory(new File("."));
        Set<ConstraintViolation<User>> violations = ValidationIntegrationTest.validator.validateProperty(user, "iban");
        Assert.assertEquals("size is not 1", 1, violations.size());
        violations = ValidationIntegrationTest.validator.validateProperty(user, "website");
        Assert.assertEquals("size is not 0", 0, violations.size());
        violations = ValidationIntegrationTest.validator.validateProperty(user, "mainDirectory");
        Assert.assertEquals("size is not 0", 0, violations.size());
    }

    @Test
    public void givenInvalidPassword_whenValidatePassword_thenConstraintViolation() {
        User user = new User("ana@yahoo.com", "password", "Ana", 20);
        Set<ConstraintViolation<User>> violations = ValidationIntegrationTest.validator.validateProperty(user, "password");
        Assert.assertEquals("message incorrect", "Invalid password", violations.iterator().next().getMessage());
    }

    @Test
    public void givenValidPassword_whenValidatePassword_thenNoConstraintViolation() {
        User user = new User("ana@yahoo.com", "password#", "Ana", 20);
        Set<ConstraintViolation<User>> violations = ValidationIntegrationTest.validator.validateProperty(user, "password");
        Assert.assertEquals("size is not 0", 0, violations.size());
    }
}

