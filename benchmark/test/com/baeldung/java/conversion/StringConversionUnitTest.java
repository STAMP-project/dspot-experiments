package com.baeldung.java.conversion;


import com.baeldung.datetime.UseLocalDateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.junit.Assert;
import org.junit.Test;


public class StringConversionUnitTest {
    @Test
    public void whenConvertedToInt_thenCorrect() {
        String beforeConvStr = "1";
        int afterConvInt = 1;
        Assert.assertEquals(Integer.parseInt(beforeConvStr), afterConvInt);
    }

    @Test
    public void whenConvertedToInteger_thenCorrect() {
        String beforeConvStr = "12";
        Integer afterConvInteger = 12;
        Assert.assertEquals(Integer.valueOf(beforeConvStr).equals(afterConvInteger), true);
    }

    @Test
    public void whenConvertedTolong_thenCorrect() {
        String beforeConvStr = "12345";
        long afterConvLongPrimitive = 12345;
        Assert.assertEquals(Long.parseLong(beforeConvStr), afterConvLongPrimitive);
    }

    @Test
    public void whenConvertedToLong_thenCorrect() {
        String beforeConvStr = "14567";
        Long afterConvLong = 14567L;
        Assert.assertEquals(Long.valueOf(beforeConvStr).equals(afterConvLong), true);
    }

    @Test
    public void whenConvertedTodouble_thenCorrect() {
        String beforeConvStr = "1.4";
        double afterConvDoublePrimitive = 1.4;
        Assert.assertEquals(Double.parseDouble(beforeConvStr), afterConvDoublePrimitive, 0.0);
    }

    @Test
    public void whenConvertedToDouble_thenCorrect() {
        String beforeConvStr = "145.67";
        double afterConvDouble = 145.67;
        Assert.assertEquals(Double.valueOf(beforeConvStr).equals(afterConvDouble), true);
    }

    @Test
    public void whenConvertedToByteArr_thenCorrect() {
        String beforeConvStr = "abc";
        byte[] afterConvByteArr = new byte[]{ 'a', 'b', 'c' };
        Assert.assertEquals(Arrays.equals(beforeConvStr.getBytes(), afterConvByteArr), true);
    }

    @Test
    public void whenConvertedToboolean_thenCorrect() {
        String beforeConvStr = "true";
        boolean afterConvBooleanPrimitive = true;
        Assert.assertEquals(Boolean.parseBoolean(beforeConvStr), afterConvBooleanPrimitive);
    }

    @Test
    public void whenConvertedToBoolean_thenCorrect() {
        String beforeConvStr = "true";
        Boolean afterConvBoolean = true;
        Assert.assertEquals(Boolean.valueOf(beforeConvStr), afterConvBoolean);
    }

    @Test
    public void whenConvertedToCharArr_thenCorrect() {
        String beforeConvStr = "hello";
        char[] afterConvCharArr = new char[]{ 'h', 'e', 'l', 'l', 'o' };
        Assert.assertEquals(Arrays.equals(beforeConvStr.toCharArray(), afterConvCharArr), true);
    }

    @Test
    public void whenConvertedToDate_thenCorrect() throws ParseException {
        String beforeConvStr = "15/10/2013";
        int afterConvCalendarDay = 15;
        int afterConvCalendarMonth = 9;
        int afterConvCalendarYear = 2013;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy");
        Date afterConvDate = formatter.parse(beforeConvStr);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(afterConvDate);
        Assert.assertEquals(calendar.get(Calendar.DAY_OF_MONTH), afterConvCalendarDay);
        Assert.assertEquals(calendar.get(Calendar.MONTH), afterConvCalendarMonth);
        Assert.assertEquals(calendar.get(Calendar.YEAR), afterConvCalendarYear);
    }

    @Test
    public void whenConvertedToLocalDateTime_thenCorrect() {
        String str = "2007-12-03T10:15:30";
        int afterConvCalendarDay = 3;
        Month afterConvCalendarMonth = Month.DECEMBER;
        int afterConvCalendarYear = 2007;
        LocalDateTime afterConvDate = new UseLocalDateTime().getLocalDateTimeUsingParseMethod(str);
        Assert.assertEquals(afterConvDate.getDayOfMonth(), afterConvCalendarDay);
        Assert.assertEquals(afterConvDate.getMonth(), afterConvCalendarMonth);
        Assert.assertEquals(afterConvDate.getYear(), afterConvCalendarYear);
    }
}

