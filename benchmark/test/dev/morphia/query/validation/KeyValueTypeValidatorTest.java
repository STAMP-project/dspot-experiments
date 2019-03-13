package dev.morphia.query.validation;


import dev.morphia.entities.SimpleEntity;
import java.util.ArrayList;
import org.bson.types.ObjectId;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class KeyValueTypeValidatorTest {
    @Test
    public void shouldAllowTypeThatMatchesKeyKindWhenValueIsAKey() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = KeyValueTypeValidator.getInstance().apply(Integer.class, new dev.morphia.Key<Number>(Integer.class, "Integer", new ObjectId()), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldNotValidateWhenValueIsNotAKey() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = KeyValueTypeValidator.getInstance().apply(String.class, new SimpleEntity(), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(false));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldRejectTypeThatDoesNotMatchKeyKindWhenValueIsAKey() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = KeyValueTypeValidator.getInstance().apply(String.class, new dev.morphia.Key<Number>(Integer.class, "Integer", new ObjectId()), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
    }
}

