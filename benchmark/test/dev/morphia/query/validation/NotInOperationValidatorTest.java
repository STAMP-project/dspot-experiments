package dev.morphia.query.validation;


import dev.morphia.query.FilterOperator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class NotInOperationValidatorTest {
    @Test
    public void shouldAllowNotInOperatorForArrayListValues() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        ArrayList<Integer> arrayList = new ArrayList<Integer>(Arrays.asList(1, 2));
        boolean validationApplied = NotInOperationValidator.getInstance().apply(null, FilterOperator.NOT_IN, arrayList, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowNotInOperatorForArrayValues() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = NotInOperationValidator.getInstance().apply(null, FilterOperator.NOT_IN, new int[0], validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowNotInOperatorForIterableValues() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = NotInOperationValidator.getInstance().apply(null, FilterOperator.NOT_IN, Collections.emptySet(), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowNotInOperatorForListValues() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = NotInOperationValidator.getInstance().apply(null, FilterOperator.NOT_IN, Arrays.asList(1, 2), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowNotInOperatorForMapValues() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = NotInOperationValidator.getInstance().apply(null, FilterOperator.NOT_IN, new HashMap<String, String>(), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldNotApplyForOperatorThatIsNotANotInOperator() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = NotInOperationValidator.getInstance().apply(null, FilterOperator.EQUAL, "value", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(false));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldRejectNullValues() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = NotInOperationValidator.getInstance().apply(null, FilterOperator.NOT_IN, null, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
        Assert.assertThat(validationFailures.get(0).toString(), CoreMatchers.containsString("value cannot be null"));
    }

    @Test
    public void shouldRejectValuesThatAreNotTheCorrectType() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = NotInOperationValidator.getInstance().apply(null, FilterOperator.NOT_IN, "value", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
    }
}

