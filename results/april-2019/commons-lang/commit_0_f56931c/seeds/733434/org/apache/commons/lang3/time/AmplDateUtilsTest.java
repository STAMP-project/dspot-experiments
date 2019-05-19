package org.apache.commons.lang3.time;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.lang3.test.SystemDefaultsSwitch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


public class AmplDateUtilsTest {
    private static Date BASE_DATE;

    @BeforeClass
    public static void classSetup() {
        final GregorianCalendar cal = new GregorianCalendar(2000, 6, 5, 4, 3, 2);
        cal.set(Calendar.MILLISECOND, 1);
        AmplDateUtilsTest.BASE_DATE = cal.getTime();
    }

    @Rule
    public SystemDefaultsSwitch defaults = new SystemDefaultsSwitch();

    private DateFormat dateParser = null;

    private DateFormat dateTimeParser = null;

    private Date dateAmPm1 = null;

    private Date dateAmPm2 = null;

    private Date dateAmPm3 = null;

    private Date dateAmPm4 = null;

    private Date date0 = null;

    private Date date1 = null;

    private Date date2 = null;

    private Date date3 = null;

    private Date date4 = null;

    private Date date5 = null;

    private Date date6 = null;

    private Date date7 = null;

    private Date date8 = null;

    private Calendar calAmPm1 = null;

    private Calendar calAmPm2 = null;

    private Calendar calAmPm3 = null;

    private Calendar calAmPm4 = null;

    private Calendar cal1 = null;

    private Calendar cal2 = null;

    private Calendar cal3 = null;

    private Calendar cal4 = null;

    private Calendar cal5 = null;

    private Calendar cal6 = null;

    private Calendar cal7 = null;

    private Calendar cal8 = null;

    private TimeZone zone = null;

    private TimeZone defaultZone = null;

    @Before
    public void setUp() throws Exception {
        dateParser = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        dateTimeParser = new SimpleDateFormat("MMM dd, yyyy H:mm:ss.SSS", Locale.ENGLISH);
        dateAmPm1 = dateTimeParser.parse("February 3, 2002 01:10:00.000");
        dateAmPm2 = dateTimeParser.parse("February 3, 2002 11:10:00.000");
        dateAmPm3 = dateTimeParser.parse("February 3, 2002 13:10:00.000");
        dateAmPm4 = dateTimeParser.parse("February 3, 2002 19:10:00.000");
        date0 = dateTimeParser.parse("February 3, 2002 12:34:56.789");
        date1 = dateTimeParser.parse("February 12, 2002 12:34:56.789");
        date2 = dateTimeParser.parse("November 18, 2001 1:23:11.321");
        defaultZone = TimeZone.getDefault();
        zone = TimeZone.getTimeZone("MET");
        try {
            TimeZone.setDefault(zone);
            dateTimeParser.setTimeZone(zone);
            date3 = dateTimeParser.parse("March 30, 2003 05:30:45.000");
            date4 = dateTimeParser.parse("March 30, 2003 01:10:00.000");
            date5 = dateTimeParser.parse("March 30, 2003 01:40:00.000");
            date6 = dateTimeParser.parse("March 30, 2003 02:10:00.000");
            date7 = dateTimeParser.parse("March 30, 2003 02:40:00.000");
            date8 = dateTimeParser.parse("October 26, 2003 05:30:45.000");
        } finally {
            dateTimeParser.setTimeZone(defaultZone);
            TimeZone.setDefault(defaultZone);
        }
        calAmPm1 = Calendar.getInstance();
        calAmPm1.setTime(dateAmPm1);
        calAmPm2 = Calendar.getInstance();
        calAmPm2.setTime(dateAmPm2);
        calAmPm3 = Calendar.getInstance();
        calAmPm3.setTime(dateAmPm3);
        calAmPm4 = Calendar.getInstance();
        calAmPm4.setTime(dateAmPm4);
        cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        try {
            TimeZone.setDefault(zone);
            cal3 = Calendar.getInstance();
            cal3.setTime(date3);
            cal4 = Calendar.getInstance();
            cal4.setTime(date4);
            cal5 = Calendar.getInstance();
            cal5.setTime(date5);
            cal6 = Calendar.getInstance();
            cal6.setTime(date6);
            cal7 = Calendar.getInstance();
            cal7.setTime(date7);
            cal8 = Calendar.getInstance();
            cal8.setTime(date8);
        } finally {
            TimeZone.setDefault(defaultZone);
        }
    }

    private void assertDate(final Date date, final int year, final int month, final int day, final int hour, final int min, final int sec, final int mil) throws Exception {
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        Assert.assertEquals(year, cal.get(Calendar.YEAR));
        Assert.assertEquals(month, cal.get(Calendar.MONTH));
        Assert.assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(hour, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(min, cal.get(Calendar.MINUTE));
        Assert.assertEquals(sec, cal.get(Calendar.SECOND));
        Assert.assertEquals(mil, cal.get(Calendar.MILLISECOND));
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testLang530() throws ParseException {
        final Date d = new Date();
        final String isoDateStr = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(d);
        Assert.assertEquals("2019-05-19T01:23:16+02:00", isoDateStr);
        final Date d2 = DateUtils.parseDate(isoDateStr, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern());
        d.getTime();
        long long_0 = (d2.getTime()) + ((d.getTime()) % 1000);
        Assert.assertEquals("2019-05-19T01:23:16+02:00", isoDateStr);
    }

    private static void assertWeekIterator(final Iterator<?> it, final Calendar start) {
        final Calendar end = ((Calendar) (start.clone()));
        end.add(Calendar.DATE, 6);
        AmplDateUtilsTest.assertWeekIterator(it, start, end);
    }

    private static void assertWeekIterator(final Iterator<?> it, final Date start, final Date end) {
        final Calendar calStart = Calendar.getInstance();
        calStart.setTime(start);
        final Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(end);
        AmplDateUtilsTest.assertWeekIterator(it, calStart, calEnd);
    }

    private static void assertWeekIterator(final Iterator<?> it, final Calendar start, final Calendar end) {
        Calendar cal = ((Calendar) (it.next()));
        AmplDateUtilsTest.assertCalendarsEquals("", start, cal, 0);
        Calendar last = null;
        int count = 1;
        while (it.hasNext()) {
            AmplDateUtilsTest.assertCalendarsEquals("", cal, DateUtils.truncate(cal, Calendar.DATE), 0);
            last = cal;
            cal = ((Calendar) (it.next()));
            count++;
            last.add(Calendar.DATE, 1);
            AmplDateUtilsTest.assertCalendarsEquals("", last, cal, 0);
        } 
        Assert.assertFalse((("There were " + count) + " days in this iterator"), ((count % 7) != 0));
        AmplDateUtilsTest.assertCalendarsEquals("", end, cal, 0);
    }

    private static void assertCalendarsEquals(final String message, final Calendar cal1, final Calendar cal2, final long delta) {
        Assert.assertFalse(((((message + " expected ") + (cal1.getTime())) + " but got ") + (cal2.getTime())), ((Math.abs(((cal1.getTime().getTime()) - (cal2.getTime().getTime())))) > delta));
    }
}

