package com.vaadin.tests.data.converter;


import com.vaadin.data.ValueContext;
import java.util.Date;
import java.util.Locale;
import org.junit.Test;


public class StringToDateConverterTest extends AbstractConverterTest {
    @Test
    public void testEmptyStringConversion() {
        assertValue(null, getConverter().convertToModel("", new ValueContext()));
    }

    @Test
    public void testValueConversion() {
        assertValue(new Date(100, 0, 1), getConverter().convertToModel("Jan 1, 2000 12:00:00 AM", new ValueContext(Locale.ENGLISH)));
    }
}

