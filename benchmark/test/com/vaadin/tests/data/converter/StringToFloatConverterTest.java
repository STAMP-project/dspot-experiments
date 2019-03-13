package com.vaadin.tests.data.converter;


import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToFloatConverter;
import org.junit.Assert;
import org.junit.Test;


public class StringToFloatConverterTest extends AbstractStringConverterTest {
    @Override
    @Test
    public void testNullConversion() {
        assertValue(null, getConverter().convertToModel(null, new ValueContext()));
    }

    @Override
    @Test
    public void testEmptyStringConversion() {
        assertValue(null, getConverter().convertToModel("", new ValueContext()));
    }

    @Test
    public void testValueConversion() {
        assertValue(Float.valueOf(10), getConverter().convertToModel("10", new ValueContext()));
    }

    @Test
    public void customEmptyValue() {
        StringToFloatConverter converter = new StringToFloatConverter(((float) (0.0)), getErrorMessage());
        assertValue(((float) (0.0)), converter.convertToModel("", new ValueContext()));
        Assert.assertEquals("0", converter.convertToPresentation(((float) (0.0)), new ValueContext()));
    }
}

