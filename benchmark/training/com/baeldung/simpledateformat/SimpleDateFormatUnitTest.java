package com.baeldung.simpledateformat;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


public class SimpleDateFormatUnitTest {
    private static final Logger logger = Logger.getLogger(SimpleDateFormatUnitTest.class.getName());

    @Test
    public void givenSpecificDate_whenFormatted_thenCheckFormatCorrect() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Assert.assertEquals("24-05-1977", formatter.format(new Date(233345223232L)));
    }

    @Test
    public void givenSpecificDate_whenFormattedUsingDateFormat_thenCheckFormatCorrect() throws Exception {
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
        Assert.assertEquals("5/24/77", formatter.format(new Date(233345223232L)));
    }

    @Test
    public void givenStringDate_whenParsed_thenCheckDateCorrect() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        Date myDate = new Date(233276400000L);
        Date parsedDate = formatter.parse("24-05-1977");
        Assert.assertEquals(myDate.getTime(), parsedDate.getTime());
    }

    @Test
    public void givenFranceLocale_whenFormatted_thenCheckFormatCorrect() throws Exception {
        SimpleDateFormat franceDateFormatter = new SimpleDateFormat("EEEEE dd-MMMMMMM-yyyy", Locale.FRANCE);
        Date myWednesday = new Date(1539341312904L);
        Assert.assertTrue(franceDateFormatter.format(myWednesday).startsWith("vendredi"));
    }

    @Test
    public void given2TimeZones_whenFormatted_thenCheckTimeDifference() throws Exception {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd-MMM-yy HH:mm:ssZ");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        SimpleDateFormatUnitTest.logger.info(simpleDateFormat.format(now));
        // change the date format
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        SimpleDateFormatUnitTest.logger.info(simpleDateFormat.format(now));
    }
}

