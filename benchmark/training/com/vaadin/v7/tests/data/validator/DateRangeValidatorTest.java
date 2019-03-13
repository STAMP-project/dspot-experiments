package com.vaadin.v7.tests.data.validator;


import com.vaadin.v7.data.validator.DateRangeValidator;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


public class DateRangeValidatorTest {
    Calendar startDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.ENGLISH);

    Calendar endDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.ENGLISH);

    private DateRangeValidator cleanValidator;

    private DateRangeValidator minValidator;

    private DateRangeValidator maxValidator;

    private DateRangeValidator minMaxValidator;

    @Test
    public void testNullValue() {
        Assert.assertTrue("Didn't accept null", cleanValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", maxValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    @Test
    public void testMinValue() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.ENGLISH);
        cal.setTime(startDate.getTime());
        cal.add(Calendar.SECOND, 1);
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid(cal.getTime()));
        Assert.assertTrue("Didn't accept valid value", minValidator.isValid(cal.getTime()));
        cal.add(Calendar.SECOND, (-3));
        Assert.assertFalse("Accepted too small value", minValidator.isValid(cal.getTime()));
    }

    @Test
    public void testMaxValue() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.ENGLISH);
        cal.setTime(endDate.getTime());
        cal.add(Calendar.SECOND, (-1));
        Assert.assertTrue("Validator without ranges didn't accept value", cleanValidator.isValid(cal.getTime()));
        Assert.assertTrue("Didn't accept valid value", maxValidator.isValid(cal.getTime()));
        cal.add(Calendar.SECOND, 2);
        Assert.assertFalse("Accepted too large value", maxValidator.isValid(cal.getTime()));
    }

    @Test
    public void testMinMaxValue() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.ENGLISH);
        cal.setTime(endDate.getTime());
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(cal.getTime()));
        cal.add(Calendar.SECOND, 1);
        Assert.assertFalse("Accepted too large value", minMaxValidator.isValid(cal.getTime()));
        cal.setTime(startDate.getTime());
        Assert.assertTrue("Didn't accept valid value", minMaxValidator.isValid(cal.getTime()));
        cal.add(Calendar.SECOND, (-1));
        Assert.assertFalse("Accepted too small value", minMaxValidator.isValid(cal.getTime()));
    }
}

