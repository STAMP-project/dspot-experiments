package dev.morphia.query.validation;


import dev.morphia.Key;
import dev.morphia.entities.SimpleEntity;
import java.util.ArrayList;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class EntityAnnotatedValueValidatorTest {
    @Test
    public void shouldAllowValueWithEntityAnnotationAndTypeOfKey() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = EntityAnnotatedValueValidator.getInstance().apply(Key.class, new SimpleEntity(), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldNotValidateValueWithEntityAnnotationAndNonKeyType() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = EntityAnnotatedValueValidator.getInstance().apply(String.class, new SimpleEntity(), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(false));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldRejectValueWithoutEntityAnnotationAndTypeOfKey() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = EntityAnnotatedValueValidator.getInstance().apply(Key.class, "value", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
    }
}

