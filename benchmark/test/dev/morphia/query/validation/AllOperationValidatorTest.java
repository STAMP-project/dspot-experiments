package dev.morphia.query.validation;


import dev.morphia.query.FilterOperator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AllOperationValidatorTest {
    @Test
    public void shouldAllowAllOperatorForIterableMapAndArrayValues() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = AllOperationValidator.getInstance().apply(null, FilterOperator.ALL, new int[0], validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowAllOperatorForIterableValues() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = AllOperationValidator.getInstance().apply(null, FilterOperator.ALL, Collections.emptySet(), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowAllOperatorForListValues() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = AllOperationValidator.getInstance().apply(null, FilterOperator.ALL, Arrays.asList(1, 2), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowAllOperatorForMapValues() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = AllOperationValidator.getInstance().apply(null, FilterOperator.ALL, new HashMap<String, String>(), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldGiveAValidationErrorForANullValue() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = AllOperationValidator.getInstance().apply(null, FilterOperator.ALL, null, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
        Assert.assertThat(validationFailures.get(0).toString(), CoreMatchers.containsString("value cannot be null"));
    }

    @Test
    public void shouldGiveAValidationErrorForAValueThatIsNotAnArrayIterableOrMap() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = AllOperationValidator.getInstance().apply(null, FilterOperator.ALL, "invalid value", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
        Assert.assertThat(validationFailures.get(0).toString(), CoreMatchers.containsString("should be an array, an Iterable, or a Map"));
    }

    @Test
    public void shouldNotApplyValidationIfOperatorIsNotAllOperation() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = AllOperationValidator.getInstance().apply(null, FilterOperator.EQUAL, "invalid value", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(false));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }
}

