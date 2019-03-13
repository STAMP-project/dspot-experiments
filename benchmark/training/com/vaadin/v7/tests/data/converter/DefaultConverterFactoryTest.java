package com.vaadin.v7.tests.data.converter;


import com.vaadin.v7.data.util.converter.DefaultConverterFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.junit.Test;


public class DefaultConverterFactoryTest {
    private DefaultConverterFactory factory = new DefaultConverterFactory();

    @Test
    public void stringToBigDecimal() {
        assertConverter("14", new BigDecimal("14"));
    }

    @Test
    public void stringToBigInteger() {
        assertConverter("14", new BigInteger("14"));
    }

    @Test
    public void stringToDouble() {
        assertConverter("14", new Double("14"));
    }

    @Test
    public void stringToFloat() {
        assertConverter("14", new Float("14"));
    }

    @Test
    public void stringToInteger() {
        assertConverter("14", new Integer("14"));
    }

    @Test
    public void stringToLong() {
        assertConverter("14", new Long("14"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void stringToDate() {
        assertConverter("Oct 12, 2014 12:00:00 AM", new Date((2014 - 1900), (10 - 1), 12));
    }

    @Test
    public void sqlDateToDate() {
        long l = 1413071210000L;
        assertConverter(new java.sql.Date(l), new Date(l));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void longToDate() {
        Date d = new Date((2014 - 1900), (10 - 1), 12);
        assertConverter((1413061200000L + ((((d.getTimezoneOffset()) + 180) * 60) * 1000L)), d);
    }

    public enum Foo {

        BAR,
        BAZ;}

    @Test
    public void stringToEnum() {
        assertConverter("Bar", DefaultConverterFactoryTest.Foo.BAR);
    }

    @Test
    public void stringToShort() {
        assertConverter("14", new Short("14"));
    }

    @Test
    public void stringToByte() {
        assertConverter("14", new Byte("14"));
    }
}

