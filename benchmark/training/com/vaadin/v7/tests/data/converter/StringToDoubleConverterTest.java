package com.vaadin.v7.tests.data.converter;


import com.vaadin.v7.data.util.converter.StringToDoubleConverter;
import org.junit.Assert;
import org.junit.Test;


public class StringToDoubleConverterTest {
    StringToDoubleConverter converter = new StringToDoubleConverter();

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null, converter.convertToModel(null, Double.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals(null, converter.convertToModel("", Double.class, null));
    }

    @Test
    public void testValueConversion() {
        Double value = converter.convertToModel("10", Double.class, null);
        Assert.assertEquals(10.0, value, 0.01);
    }
}

