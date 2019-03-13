package com.vaadin.tests.data.converter;


import com.vaadin.data.ValueContext;
import java.util.Date;
import org.junit.Test;


public class DateToLongConverterTest extends AbstractConverterTest {
    @Override
    @Test
    public void testNullConversion() {
        assertValue(null, getConverter().convertToModel(null, new ValueContext()));
    }

    @Test
    public void testValueConversion() {
        Date d = new Date(100, 0, 1);
        assertValue(Long.valueOf((946677600000L + ((((d.getTimezoneOffset()) + 120) * 60) * 1000L))), getConverter().convertToModel(d, new ValueContext()));
    }
}

