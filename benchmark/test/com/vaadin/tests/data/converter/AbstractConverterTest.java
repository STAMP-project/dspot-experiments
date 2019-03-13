package com.vaadin.tests.data.converter;


import com.vaadin.data.ValueContext;
import org.junit.Test;


public abstract class AbstractConverterTest {
    @Test
    public void testNullConversion() {
        assertValue(null, getConverter().convertToModel(null, new ValueContext()));
    }
}

