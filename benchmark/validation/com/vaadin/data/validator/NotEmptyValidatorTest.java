package com.vaadin.data.validator;


import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class NotEmptyValidatorTest {
    @Test
    public void nullValueIsDisallowed() {
        NotEmptyValidator<String> validator = new NotEmptyValidator<>("foo");
        ValidationResult result = validator.apply(null, new ValueContext());
        Assert.assertTrue(result.isError());
        Assert.assertEquals("foo", result.getErrorMessage());
    }

    @Test
    public void emptyValueIsDisallowed() {
        NotEmptyValidator<String> validator = new NotEmptyValidator<>("foo");
        ValidationResult result = validator.apply("", new ValueContext());
        Assert.assertTrue(result.isError());
        Assert.assertEquals("foo", result.getErrorMessage());
    }

    @Test
    public void nonNullValueIsAllowed() {
        NotEmptyValidator<Object> validator = new NotEmptyValidator<>("foo");
        Object value = new Object();
        ValidationResult result = validator.apply(value, new ValueContext());
        Assert.assertFalse(result.isError());
        Assert.assertFalse(result.isError());
    }
}

