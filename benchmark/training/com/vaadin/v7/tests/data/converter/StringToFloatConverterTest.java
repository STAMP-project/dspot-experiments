package com.vaadin.v7.tests.data.converter;


import com.vaadin.v7.data.util.converter.StringToFloatConverter;
import org.junit.Assert;
import org.junit.Test;


public class StringToFloatConverterTest {
    StringToFloatConverter converter = new StringToFloatConverter();

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null, converter.convertToModel(null, Float.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals(null, converter.convertToModel("", Float.class, null));
    }

    @Test
    public void testValueConversion() {
        Assert.assertEquals(Float.valueOf(10), converter.convertToModel("10", Float.class, null));
    }
}

