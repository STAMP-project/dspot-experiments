/**
 *
 */
package com.baeldung.java9.datetime;


import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnits for {@link LocalDateTimeToDateConverter} class.
 *
 * @author abialas
 */
public class LocalDateTimeToDateConverterUnitTest {
    @Test
    public void shouldReturn10thNovember2010time8hour20minWhenConvertViaInstant() {
        // given
        LocalDateTime dateToConvert = LocalDateTime.of(2010, 11, 10, 8, 20);
        // when
        Date date = LocalDateTimeToDateConverter.convertToDateViaInstant(dateToConvert);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // then
        Assert.assertEquals(2010, calendar.get(Calendar.YEAR));
        Assert.assertEquals(10, calendar.get(Calendar.MONTH));
        Assert.assertEquals(10, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(8, calendar.get(Calendar.HOUR));
        Assert.assertEquals(20, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
    }

    @Test
    public void shouldReturn10thNovember2010WhenConvertViaSqlTimestamp() {
        // given
        LocalDateTime dateToConvert = LocalDateTime.of(2010, 11, 10, 8, 20);
        // when
        Date date = LocalDateTimeToDateConverter.convertToDateViaSqlTimestamp(dateToConvert);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // then
        Assert.assertEquals(2010, calendar.get(Calendar.YEAR));
        Assert.assertEquals(10, calendar.get(Calendar.MONTH));
        Assert.assertEquals(10, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(8, calendar.get(Calendar.HOUR));
        Assert.assertEquals(20, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
    }
}

