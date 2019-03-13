package dev.morphia.query.validation;


import dev.morphia.query.FilterOperator;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ExistsOperationValidatorTest {
    @Test
    public void shouldAllowBooleanValuesForExistsOperator() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ExistsOperationValidator.getInstance().apply(null, FilterOperator.EXISTS, Boolean.TRUE, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowPrimitiveBooleanValuesForExistsOperator() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ExistsOperationValidator.getInstance().apply(null, FilterOperator.EXISTS, true, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldNotApplyValidationIfOperatorIsNotExistsOperator() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ExistsOperationValidator.getInstance().apply(null, FilterOperator.ALL, true, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(false));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldRejectNonBooleanValuesForExistsOperator() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ExistsOperationValidator.getInstance().apply(null, FilterOperator.EXISTS, "value", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
    }
}

