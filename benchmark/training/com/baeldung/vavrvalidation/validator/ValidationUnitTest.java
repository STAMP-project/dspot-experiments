package com.baeldung.vavrvalidation.validator;


import com.baeldung.vavrvalidation.model.User;
import io.vavr.control.Either.Right;
import io.vavr.control.Validation.Invalid;
import io.vavr.control.Validation.Valid;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ValidationUnitTest {
    private static UserValidator userValidator;

    @Test
    public void givenInvalidUserParams_whenValidated_thenInvalidInstance() {
        Assert.assertThat(ValidationUnitTest.userValidator.validateUser("John", "no-email"), CoreMatchers.instanceOf(Invalid.class));
    }

    @Test
    public void givenValidUserParams_whenValidated_thenValidInstance() {
        Assert.assertThat(ValidationUnitTest.userValidator.validateUser("John", "john@domain.com"), CoreMatchers.instanceOf(Valid.class));
    }

    @Test
    public void givenInvalidUserParams_whenValidated_thenTrueWithIsInvalidMethod() {
        Assert.assertTrue(ValidationUnitTest.userValidator.validateUser("John", "no-email").isInvalid());
    }

    @Test
    public void givenValidUserParams_whenValidated_thenTrueWithIsValidMethod() {
        Assert.assertTrue(ValidationUnitTest.userValidator.validateUser("John", "john@domain.com").isValid());
    }

    @Test
    public void givenInValidUserParams_withGetErrorMethod_thenGetErrorMessages() {
        Assert.assertEquals("Name contains invalid characters, Email must be a well-formed email address", ValidationUnitTest.userValidator.validateUser(" ", "no-email").getError().intersperse(", ").fold("", String::concat));
    }

    @Test
    public void givenValidUserParams_withGetMethod_thenGetUserInstance() {
        Assert.assertThat(ValidationUnitTest.userValidator.validateUser("John", "john@domain.com").get(), CoreMatchers.instanceOf(User.class));
    }

    @Test
    public void givenValidUserParams_withtoEitherMethod_thenRightInstance() {
        Assert.assertThat(ValidationUnitTest.userValidator.validateUser("John", "john@domain.com").toEither(), CoreMatchers.instanceOf(Right.class));
    }

    @Test
    public void givenValidUserParams_withFoldMethodWithListlengthUserHashCode_thenEqualstoParamsLength() {
        Assert.assertEquals(2, ((int) (ValidationUnitTest.userValidator.validateUser(" ", " ").fold(Seq::length, User::hashCode))));
    }
}

