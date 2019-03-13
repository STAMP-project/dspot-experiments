package dev.morphia.query.validation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ListValueValidatorTest {
    @Test
    public void shouldAllowSubclassesOfList() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ListValueValidator.getInstance().apply(null, new ArrayList<String>(), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldAllowValuesOfList() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        List<Integer> list = Arrays.asList(1, 2);
        boolean validationApplied = ListValueValidator.getInstance().apply(null, list, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldNotApplyIfValueIsNotAList() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = ListValueValidator.getInstance().apply(null, new HashMap<String, Object>(), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(false));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }
}

