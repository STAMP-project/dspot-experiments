package com.vaadin.tests.data.converter;


import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractStringConverterTest extends AbstractConverterTest {
    @Test
    public void testEmptyStringConversion() {
        assertValue("Null value was converted incorrectly", null, getConverter().convertToModel("", new ValueContext()));
    }

    @Test
    public void testErrorMessage() {
        Result<?> result = getConverter().convertToModel("abc", new ValueContext());
        Assert.assertTrue(result.isError());
        Assert.assertEquals(getErrorMessage(), result.getMessage().get());
    }
}

