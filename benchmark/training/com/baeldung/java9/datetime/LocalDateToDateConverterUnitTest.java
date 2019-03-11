/**
 *
 */
package com.baeldung.java9.datetime;


import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnits for {@link LocalDateToDateConverter} class.
 *
 * @author abialas
 */
public class LocalDateToDateConverterUnitTest {
    @Test
    public void shouldReturn10thNovember2010WhenConvertViaInstant() {
        // given
        LocalDate dateToConvert = LocalDate.of(2010, 11, 10);
        // when
        Date date = LocalDateToDateConverter.convertToDateViaInstant(dateToConvert);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // then
        Assert.assertEquals(2010, calendar.get(Calendar.YEAR));
        Assert.assertEquals(10, calendar.get(Calendar.MONTH));
        Assert.assertEquals(10, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void shouldReturn10thNovember2010WhenConvertViaSqlDate() {
        // given
        LocalDate dateToConvert = LocalDate.of(2010, 11, 10);
        // when
        Date date = LocalDateToDateConverter.convertToDateViaSqlDate(dateToConvert);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // then
        Assert.assertEquals(2010, calendar.get(Calendar.YEAR));
        Assert.assertEquals(10, calendar.get(Calendar.MONTH));
        Assert.assertEquals(10, calendar.get(Calendar.DAY_OF_MONTH));
    }
}

