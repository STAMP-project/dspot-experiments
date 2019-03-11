package org.junit.runners.model;


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class InvalidTestClassErrorTest {
    @Test
    public void invalidTestClassErrorShouldListAllValidationErrorsInItsMessage() {
        InvalidTestClassError sut = new InvalidTestClassError(InvalidTestClassErrorTest.SampleTestClass.class, Arrays.asList(new Throwable("validation error 1"), new Throwable("validation error 2")));
        MatcherAssert.assertThat(sut.getMessage(), CoreMatchers.equalTo((((("Invalid test class '" + (InvalidTestClassErrorTest.SampleTestClass.class.getName())) + "':") + "\n  1. validation error 1") + "\n  2. validation error 2")));
    }

    private static class SampleTestClass {}
}

