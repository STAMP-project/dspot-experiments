/**
 *
 */
package com.baeldung.java9.datetime;


import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnits for {@link DateToLocalDateConverter} class.
 *
 * @author abialas
 */
public class DateToLocalDateConverterUnitTest {
    @Test
    public void shouldReturn10thNovember2010WhenConvertViaInstant() {
        // given
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 10, 10);
        Date dateToConvert = calendar.getTime();
        // when
        LocalDate localDate = DateToLocalDateConverter.convertToLocalDateViaInstant(dateToConvert);
        // then
        Assert.assertEquals(2010, localDate.get(ChronoField.YEAR));
        Assert.assertEquals(11, localDate.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(10, localDate.get(ChronoField.DAY_OF_MONTH));
    }

    @Test
    public void shouldReturn10thNovember2010WhenConvertViaMiliseconds() {
        // given
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 10, 10);
        Date dateToConvert = calendar.getTime();
        // when
        LocalDate localDate = DateToLocalDateConverter.convertToLocalDateViaMilisecond(dateToConvert);
        // then
        Assert.assertEquals(2010, localDate.get(ChronoField.YEAR));
        Assert.assertEquals(11, localDate.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(10, localDate.get(ChronoField.DAY_OF_MONTH));
    }

    @Test
    public void shouldReturn10thNovember2010WhenConvertViaSqlDate() {
        // given
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 10, 10);
        Date dateToConvert = calendar.getTime();
        // when
        LocalDate localDate = DateToLocalDateConverter.convertToLocalDateViaSqlDate(dateToConvert);
        // then
        Assert.assertEquals(2010, localDate.get(ChronoField.YEAR));
        Assert.assertEquals(11, localDate.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(10, localDate.get(ChronoField.DAY_OF_MONTH));
    }

    @Test
    public void shouldReturn10thNovember2010WhenConvertToLocalDate() {
        // given
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 10, 10);
        Date dateToConvert = calendar.getTime();
        // when
        LocalDate localDateTime = DateToLocalDateConverter.convertToLocalDate(dateToConvert);
        // then
        Assert.assertEquals(2010, localDateTime.get(ChronoField.YEAR));
        Assert.assertEquals(11, localDateTime.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(10, localDateTime.get(ChronoField.DAY_OF_MONTH));
    }
}

