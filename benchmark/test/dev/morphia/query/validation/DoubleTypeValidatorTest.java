package dev.morphia.query.validation;


import java.util.ArrayList;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DoubleTypeValidatorTest {
    @Test
    public void shouldAllowDoubleTypeWithDoublePrimitiveValue() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = DoubleTypeValidator.getInstance().apply(Double.class, 2.2, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowDoubleTypeWithDoubleValue() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = DoubleTypeValidator.getInstance().apply(Double.class, new Double(2.2), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowValuesOfIntegerIfTypeIsDouble() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = DoubleTypeValidator.getInstance().apply(Double.class, new Integer(1), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowValuesOfIntegerIfTypeIsPrimitiveDouble() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = DoubleTypeValidator.getInstance().apply(double.class, new Integer(1), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowValuesOfLongIfTypeIsPrimitiveDouble() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = DoubleTypeValidator.getInstance().apply(double.class, new Long(1), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldNotApplyValidationIfTypeIsNotDouble() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = DoubleTypeValidator.getInstance().apply(String.class, new Long(1), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(false));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldOnlyValuesOfLongIfTypeIsDouble() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = DoubleTypeValidator.getInstance().apply(Double.class, new Long(1), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldRejectValueThatIsNotApplicableToDouble() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = DoubleTypeValidator.getInstance().apply(Double.class, "something not a double", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
    }
}

