package com.vaadin.v7.tests.data.converter;


import com.vaadin.v7.data.util.converter.DateToSqlDateConverter;
import java.sql.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class DateToSqlDateConverterTest {
    DateToSqlDateConverter converter = new DateToSqlDateConverter();

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null, converter.convertToModel(null, Date.class, null));
    }

    @Test
    public void testValueConversion() {
        java.util.Date testDate = new java.util.Date(100, 0, 1);
        long time = testDate.getTime();
        Assert.assertEquals(testDate, converter.convertToModel(new Date(time), Date.class, Locale.ENGLISH));
    }
}

