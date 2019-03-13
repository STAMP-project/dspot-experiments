package dev.morphia.query.validation;


import dev.morphia.query.FilterOperator;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ModOperationValidatorTest {
    @Test
    public void shouldAllowModOperatorForArrayOfTwoIntegerValues() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ModOperationValidator.getInstance().apply(null, FilterOperator.MOD, new int[2], validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldNotApplyValidationWithANonModOperator() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ModOperationValidator.getInstance().apply(null, FilterOperator.EQUAL, new int[2], validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(false));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldNotErrorIfModOperatorIsUsedWithZeroLengthArrayOfIntegerValues() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ModOperationValidator.getInstance().apply(null, FilterOperator.MOD, new int[0], validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
        Assert.assertThat(validationFailures.get(0).toString(), CoreMatchers.containsString("should be an array with two integer elements"));
    }

    @Test
    public void shouldRejectModOperatorWithNonArrayValue() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ModOperationValidator.getInstance().apply(null, FilterOperator.MOD, "Not an array", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
        Assert.assertThat(validationFailures.get(0).toString(), CoreMatchers.containsString("should be an integer array"));
    }

    @Test
    public void shouldRejectModOperatorWithNonIntegerArray() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ModOperationValidator.getInstance().apply(null, FilterOperator.MOD, new String[]{ "1", "2" }, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
        Assert.assertThat(validationFailures.get(0).toString(), CoreMatchers.containsString("Array value needs to contain integers for $mod"));
    }

    @Test
    public void shouldRejectNullValues() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ModOperationValidator.getInstance().apply(null, FilterOperator.MOD, null, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
        Assert.assertThat(validationFailures.get(0).toString(), CoreMatchers.containsString("value cannot be null"));
    }
}

