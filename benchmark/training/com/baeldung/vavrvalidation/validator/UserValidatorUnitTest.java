package com.baeldung.vavrvalidation.validator;


import io.vavr.control.Validation.Invalid;
import io.vavr.control.Validation.Valid;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class UserValidatorUnitTest {
    @Test
    public void givenValidUserParams_whenValidated_thenInstanceOfValid() {
        UserValidator userValidator = new UserValidator();
        Assert.assertThat(userValidator.validateUser("John", "john@domain.com"), CoreMatchers.instanceOf(Valid.class));
    }

    @Test
    public void givenInvalidUserParams_whenValidated_thenInstanceOfInvalid() {
        UserValidator userValidator = new UserValidator();
        Assert.assertThat(userValidator.validateUser("John", "no-email"), CoreMatchers.instanceOf(Invalid.class));
    }
}

