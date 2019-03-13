package dev.morphia.query.validation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DefaultTypeValidatorTest {
    @Test
    public void shouldAllowTypesThatAreSuperclasses() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = DefaultTypeValidator.getInstance().apply(Map.class, new HashMap(), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowTypesThatMatchTheClassOfTheValue() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // expect
        Assert.assertThat(DefaultTypeValidator.getInstance().apply(String.class, "some String", validationFailures), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowTypesThatTheRealTypeOfTheValue() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // given
        List<Integer> valueAsList = Arrays.asList(1);
        Assert.assertThat(DefaultTypeValidator.getInstance().apply(ArrayList.class, valueAsList, validationFailures), CoreMatchers.is(true));
    }

    @Test
    public void shouldRejectTypesAndValuesThatDoNotMatch() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = DefaultTypeValidator.getInstance().apply(String.class, 1, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
    }
}

