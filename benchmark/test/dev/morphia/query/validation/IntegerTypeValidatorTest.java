package dev.morphia.query.validation;


import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class IntegerTypeValidatorTest {
    @Test
    public void shouldAllowIntValueWhenTypeIsInt() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(int.class, 1, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowIntValueWhenTypeIsInteger() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(Integer.class, 1, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowIntegerValueWhenTypeIsInt() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(int.class, new Integer(1), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowIntegerValueWhenTypeIsInteger() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(Integer.class, new Integer(1), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldNotApplyValidationIfTypeIsNotIntegerOrLong() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(String.class, new Integer(1), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(false));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldRejectLongValueIfTypeIsInteger() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(Integer.class, 1L, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
    }

    @Test
    public void shouldRejectNonIntegerValueIfTypeIsInteger() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(Integer.class, "some non int value", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
    }
}

