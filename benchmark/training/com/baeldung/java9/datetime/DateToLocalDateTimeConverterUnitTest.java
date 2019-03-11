/**
 *
 */
package com.baeldung.java9.datetime;


import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnits for {@link DateToLocalDateTimeConverter} class.
 *
 * @author abialas
 */
public class DateToLocalDateTimeConverterUnitTest {
    @Test
    public void shouldReturn10thNovember2010time8hour20minWhenConvertViaInstant() {
        // given
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 10, 10, 8, 20);
        Date dateToConvert = calendar.getTime();
        // when
        LocalDateTime localDateTime = DateToLocalDateTimeConverter.convertToLocalDateTimeViaInstant(dateToConvert);
        // then
        Assert.assertEquals(2010, localDateTime.get(ChronoField.YEAR));
        Assert.assertEquals(11, localDateTime.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(10, localDateTime.get(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(8, localDateTime.get(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(20, localDateTime.get(ChronoField.MINUTE_OF_HOUR));
    }

    @Test
    public void shouldReturn10thNovember2010time8hour20minWhenConvertViaMiliseconds() {
        // given
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 10, 10, 8, 20);
        Date dateToConvert = calendar.getTime();
        // when
        LocalDateTime localDateTime = DateToLocalDateTimeConverter.convertToLocalDateTimeViaMilisecond(dateToConvert);
        // then
        Assert.assertEquals(2010, localDateTime.get(ChronoField.YEAR));
        Assert.assertEquals(11, localDateTime.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(10, localDateTime.get(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(8, localDateTime.get(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(20, localDateTime.get(ChronoField.MINUTE_OF_HOUR));
    }

    @Test
    public void shouldReturn10thNovember2010time8hour20minWhenConvertViaSqlTimestamp() {
        // given
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 10, 10, 8, 20);
        Date dateToConvert = calendar.getTime();
        // when
        LocalDateTime localDateTime = DateToLocalDateTimeConverter.convertToLocalDateTimeViaSqlTimestamp(dateToConvert);
        // then
        Assert.assertEquals(2010, localDateTime.get(ChronoField.YEAR));
        Assert.assertEquals(11, localDateTime.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(10, localDateTime.get(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(8, localDateTime.get(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(20, localDateTime.get(ChronoField.MINUTE_OF_HOUR));
    }

    @Test
    public void shouldReturn10thNovember2010time8hour20minWhenConvertToLocalDateTime() {
        // given
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 10, 10, 8, 20);
        Date dateToConvert = calendar.getTime();
        // when
        LocalDateTime localDateTime = DateToLocalDateTimeConverter.convertToLocalDateTime(dateToConvert);
        // then
        Assert.assertEquals(2010, localDateTime.get(ChronoField.YEAR));
        Assert.assertEquals(11, localDateTime.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(10, localDateTime.get(ChronoField.DAY_OF_MONTH));
        Assert.assertEquals(8, localDateTime.get(ChronoField.HOUR_OF_DAY));
        Assert.assertEquals(20, localDateTime.get(ChronoField.MINUTE_OF_HOUR));
    }
}

