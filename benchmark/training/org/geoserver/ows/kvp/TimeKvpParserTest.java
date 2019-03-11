/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows.kvp;


import TimeParser.UTC_TZ;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.geotools.util.DateRange;

import static TimeParser.MILLIS_IN_DAY;


/**
 * Test for the time parameter in a WMS request.
 *
 * @author Cedric Briancon
 * @author Simone Giannecchini, GeoSolutions SAS
 * @author Jonathan Meyer, Applied Information Sciences, jon@gisjedi.com
 */
public class TimeKvpParserTest extends TestCase {
    /**
     * A time period for testing.
     */
    private static final String PERIOD = "2007-01-01T12Z/2007-01-31T12Z/P1DT12H";

    private static final String CONTINUOUS_PERIOD = "2007-01-01T12Z/2007-01-31T12Z";

    private static final String CONTINUOUS_PERIOD_TIME_DURATION = "2007-01-01T12Z/P1DT1H";

    private static final String CONTINUOUS_PERIOD_INVALID_DURATION = "P1D/P1DT1H";

    private static final String CONTINUOUS_RELATIVE_PERIOD_H = "PT2H/PRESENT";

    private static final String CONTINUOUS_RELATIVE_PERIOD_D = "P10D/PRESENT";

    private static final String CONTINUOUS_RELATIVE_PERIOD_W = "P2W/PRESENT";

    /**
     * Format of dates.
     */
    private static final DateFormat format;

    static {
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH'Z'");
        TimeKvpParserTest.format.setTimeZone(UTC_TZ);
    }

    public void testReducedAccuracyYear() throws Exception {
        Calendar c = new GregorianCalendar();
        c.setTimeZone(UTC_TZ);
        DateRange year = ((DateRange) (TimeParser.getFuzzyDate("2000")));
        c.clear();
        c.set(Calendar.YEAR, 2000);
        TimeKvpParserTest.assertRangeStarts(year, c.getTime());
        c.set(Calendar.YEAR, 2001);
        c.add(Calendar.MILLISECOND, (-1));
        TimeKvpParserTest.assertRangeEnds(year, c.getTime());
        year = ((DateRange) (TimeParser.getFuzzyDate("2001")));
        c.clear();
        c.set(Calendar.YEAR, 2001);
        TimeKvpParserTest.assertRangeStarts(year, c.getTime());
        c.set(Calendar.YEAR, 2002);
        c.add(Calendar.MILLISECOND, (-1));
        TimeKvpParserTest.assertRangeEnds(year, c.getTime());
        year = ((DateRange) (TimeParser.getFuzzyDate("-6052")));
        c.clear();
        c.set(Calendar.ERA, GregorianCalendar.BC);
        c.set(Calendar.YEAR, 6053);
        TimeKvpParserTest.assertRangeStarts(year, c.getTime());
        c.set(Calendar.YEAR, 6052);
        c.add(Calendar.MILLISECOND, (-1));
        TimeKvpParserTest.assertRangeEnds(year, c.getTime());
    }

    public void testReducedAccuracyHour() throws Exception {
        Calendar c = new GregorianCalendar();
        c.setTimeZone(UTC_TZ);
        c.clear();
        DateRange hour = ((DateRange) (TimeParser.getFuzzyDate("2000-04-04T12Z")));
        c.set(Calendar.YEAR, 2000);
        c.set(Calendar.MONTH, 3);// 0-indexed

        c.set(Calendar.DAY_OF_MONTH, 4);
        c.set(Calendar.HOUR_OF_DAY, 12);
        TimeKvpParserTest.assertRangeStarts(hour, c.getTime());
        c.add(Calendar.HOUR_OF_DAY, 1);
        c.add(Calendar.MILLISECOND, (-1));
        TimeKvpParserTest.assertRangeEnds(hour, c.getTime());
        hour = ((DateRange) (TimeParser.getFuzzyDate("2005-12-31T23Z")));// selected due to leapsecond at 23:59:60 UTC

        c.clear();
        c.set(Calendar.YEAR, 2005);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH, 31);
        c.set(Calendar.HOUR_OF_DAY, 23);
        TimeKvpParserTest.assertRangeStarts(hour, c.getTime());
        c.add(Calendar.HOUR_OF_DAY, 1);
        c.add(Calendar.MILLISECOND, (-1));
        TimeKvpParserTest.assertRangeEnds(hour, c.getTime());
        hour = ((DateRange) (TimeParser.getFuzzyDate("-25-06-08T17Z")));
        c.clear();
        c.set(Calendar.ERA, GregorianCalendar.BC);
        c.set(Calendar.YEAR, 26);
        c.set(Calendar.MONTH, 5);
        c.set(Calendar.DAY_OF_MONTH, 8);
        c.set(Calendar.HOUR_OF_DAY, 17);
        TimeKvpParserTest.assertRangeStarts(hour, c.getTime());
        c.add(Calendar.HOUR_OF_DAY, 1);
        c.add(Calendar.MILLISECOND, (-1));
        TimeKvpParserTest.assertRangeEnds(hour, c.getTime());
    }

    public void testReducedAccuracyMilliseconds() throws Exception {
        Calendar c = new GregorianCalendar();
        c.setTimeZone(UTC_TZ);
        c.clear();
        Date instant = ((Date) (TimeParser.getFuzzyDate("2000-04-04T12:00:00.000Z")));
        c.set(Calendar.YEAR, 2000);
        c.set(Calendar.MONTH, 3);// 0-indexed

        c.set(Calendar.DAY_OF_MONTH, 4);
        c.set(Calendar.HOUR_OF_DAY, 12);
        TestCase.assertEquals(instant, c.getTime());
        instant = ((Date) (TimeParser.getFuzzyDate("2005-12-31T23:59:60.000Z")));// selected due to leapsecond at

        // 23:59:60 UTC
        c.clear();
        c.set(Calendar.YEAR, 2005);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH, 31);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 60);
        TestCase.assertEquals(instant, c.getTime());
        instant = ((Date) (TimeParser.getFuzzyDate("-25-06-08T17:15:00.123Z")));
        c.clear();
        c.set(Calendar.ERA, GregorianCalendar.BC);
        c.set(Calendar.YEAR, 26);
        c.set(Calendar.MONTH, 5);
        c.set(Calendar.DAY_OF_MONTH, 8);
        c.set(Calendar.HOUR_OF_DAY, 17);
        c.set(Calendar.MINUTE, 15);
        c.set(Calendar.MILLISECOND, 123);
        TestCase.assertEquals(instant, c.getTime());
    }

    /**
     * Tests only the increment part of the time parameter.
     *
     * @throws ParseException
     * 		if the string can't be parsed.
     */
    public void testPeriod() throws ParseException {
        final long millisInDay = MILLIS_IN_DAY;
        TestCase.assertEquals(millisInDay, TimeParser.parsePeriod("P1D"));
        TestCase.assertEquals((3 * millisInDay), TimeParser.parsePeriod("P3D"));
        TestCase.assertEquals((14 * millisInDay), TimeParser.parsePeriod("P2W"));
        TestCase.assertEquals((8 * millisInDay), TimeParser.parsePeriod("P1W1D"));
        TestCase.assertEquals(millisInDay, TimeParser.parsePeriod("PT24H"));
        TestCase.assertEquals(Math.round((1.5 * millisInDay)), TimeParser.parsePeriod("P1.5D"));
    }

    /**
     * Compares the dates obtained by parsing the time parameter with the expected values.
     *
     * @throws ParseException
     * 		if the string can't be parsed.
     */
    public void testInterval() throws ParseException {
        TimeKvpParser timeKvpParser = new TimeKvpParser("TIME");
        List l = new ArrayList(((Collection) (timeKvpParser.parse(TimeKvpParserTest.PERIOD))));
        // Verify that the list contains at least one element.
        TestCase.assertFalse(l.isEmpty());
        TimeKvpParserTest.assertInstant(TimeKvpParserTest.format.parse("2007-01-01T12Z"), l.get(0));
        TimeKvpParserTest.assertInstant(TimeKvpParserTest.format.parse("2007-01-03T00Z"), l.get(1));
        TimeKvpParserTest.assertInstant(TimeKvpParserTest.format.parse("2007-01-04T12Z"), l.get(2));
        TimeKvpParserTest.assertInstant(TimeKvpParserTest.format.parse("2007-01-06T00Z"), l.get(3));
        TimeKvpParserTest.assertInstant(TimeKvpParserTest.format.parse("2007-01-07T12Z"), l.get(4));
        TimeKvpParserTest.assertInstant(TimeKvpParserTest.format.parse("2007-01-09T00Z"), l.get(5));
        TimeKvpParserTest.assertInstant(TimeKvpParserTest.format.parse("2007-01-10T12Z"), l.get(6));
        TimeKvpParserTest.assertInstant(TimeKvpParserTest.format.parse("2007-01-12T00Z"), l.get(7));
        l = new ArrayList(((Collection) (timeKvpParser.parse("2007-01-01T12Z/2007-01-01T13Z/PT10M"))));
        // Verify that the list contains at least one element.
        TestCase.assertFalse(l.isEmpty());
        TestCase.assertEquals(12, l.size());
        TimeKvpParserTest.assertInstant(TimeKvpParserTest.format.parse("2007-01-01T12Z"), l.get(0));
    }

    public void testContinuousInterval() throws ParseException {
        TimeKvpParser timeKvpParser = new TimeKvpParser("TIME");
        List l = new ArrayList(((Collection) (timeKvpParser.parse(TimeKvpParserTest.CONTINUOUS_PERIOD))));
        // Verify that the list contains at least one element.
        TestCase.assertFalse(l.isEmpty());
        TestCase.assertTrue(((l.get(0)) instanceof DateRange));
        final DateRange range = ((DateRange) (l.get(0)));
        TestCase.assertEquals(TimeKvpParserTest.format.parse("2007-01-01T12Z"), range.getMinValue());
        Date end = TimeKvpParserTest.format.parse("2007-01-31T13Z");
        end.setTime(((end.getTime()) - 1));
        TestCase.assertEquals(end, range.getMaxValue());
    }

    public void testContinuousIntervalDuration() throws ParseException {
        TimeKvpParser timeKvpParser = new TimeKvpParser("TIME");
        List l = new ArrayList(((Collection) (timeKvpParser.parse(TimeKvpParserTest.CONTINUOUS_PERIOD_TIME_DURATION))));
        // Verify that the list contains at least one element.
        TestCase.assertFalse(l.isEmpty());
        TestCase.assertTrue(((l.get(0)) instanceof DateRange));
        final DateRange range = ((DateRange) (l.get(0)));
        TestCase.assertEquals(TimeKvpParserTest.format.parse("2007-01-01T12Z"), range.getMinValue());
        Date end = TimeKvpParserTest.format.parse("2007-01-02T13Z");
        TestCase.assertEquals(end, range.getMaxValue());
    }

    public void testInvalidDualDuration() throws ParseException {
        TimeKvpParser timeKvpParser = new TimeKvpParser("TIME");
        boolean exception = false;
        try {
            timeKvpParser.parse(TimeKvpParserTest.CONTINUOUS_PERIOD_INVALID_DURATION);
            // Verify that an exception was encountered for the invalid duration
            TestCase.fail("No exception thrown for invalid duration");
        } catch (ParseException ex) {
            TestCase.assertTrue(ex.getMessage().startsWith("Invalid time period"));
        }
    }

    public void testMixedValues() throws ParseException {
        TimeKvpParser timeKvpParser = new TimeKvpParser("TIME");
        List l = new ArrayList(((Collection) (timeKvpParser.parse(((TimeKvpParserTest.CONTINUOUS_PERIOD) + ",2007-02-01T12Z")))));
        // Verify that the list contains at least one element.
        TestCase.assertFalse(l.isEmpty());
        TestCase.assertTrue(((l.get(0)) instanceof DateRange));
        final DateRange range = ((DateRange) (l.get(0)));
        TestCase.assertEquals(TimeKvpParserTest.format.parse("2007-01-01T12Z"), range.getMinValue());
        Date end = TimeKvpParserTest.format.parse("2007-01-31T13Z");
        end.setTime(((end.getTime()) - 1));
        TestCase.assertEquals(end, range.getMaxValue());
        TimeKvpParserTest.assertRange(((DateRange) (l.get(1))), TimeKvpParserTest.format.parse("2007-02-01T12Z"), TimeKvpParserTest.format.parse("2007-02-01T13Z"));
    }

    public void testInclusions() throws ParseException {
        TimeKvpParser timeKvpParser = new TimeKvpParser("TIME");
        List l = new ArrayList(((Collection) (timeKvpParser.parse((((TimeKvpParserTest.CONTINUOUS_PERIOD) + ",2007-01-29T12Z,") + "2007-01-12T12Z,2007-01-17T12Z,2007-01-01T12Z/2007-01-15T12Z")))));
        // Verify that the list contains at least one element.
        TestCase.assertFalse(l.isEmpty());
        TestCase.assertTrue(((l.size()) == 1));
        TestCase.assertTrue(((l.get(0)) instanceof DateRange));
        final DateRange range = ((DateRange) (l.get(0)));
        TimeKvpParserTest.assertRange(range, TimeKvpParserTest.format.parse("2007-01-01T12Z"), TimeKvpParserTest.format.parse("2007-01-31T13Z"));
    }

    public void testOrderedValues() throws Exception {
        TimeKvpParser timeKvpParser = new TimeKvpParser("TIME");
        List l = new ArrayList(((Collection) (timeKvpParser.parse(("2007-01-29T12Z,2007-01-12T12Z," + "2007-01-17T12Z,2007-01-01T12Z,2007-01-05T12Z")))));
        // Verify that the list contains at least one element.
        TestCase.assertFalse(l.isEmpty());
        TestCase.assertTrue(((l.size()) == 5));
        TimeKvpParserTest.assertRange(((DateRange) (l.get(0))), TimeKvpParserTest.format.parse("2007-01-01T12Z"), TimeKvpParserTest.format.parse("2007-01-01T13Z"));
        TimeKvpParserTest.assertRange(((DateRange) (l.get(1))), TimeKvpParserTest.format.parse("2007-01-05T12Z"), TimeKvpParserTest.format.parse("2007-01-05T13Z"));
        TimeKvpParserTest.assertRange(((DateRange) (l.get(2))), TimeKvpParserTest.format.parse("2007-01-12T12Z"), TimeKvpParserTest.format.parse("2007-01-12T13Z"));
        TimeKvpParserTest.assertRange(((DateRange) (l.get(3))), TimeKvpParserTest.format.parse("2007-01-17T12Z"), TimeKvpParserTest.format.parse("2007-01-17T13Z"));
        TimeKvpParserTest.assertRange(((DateRange) (l.get(4))), TimeKvpParserTest.format.parse("2007-01-29T12Z"), TimeKvpParserTest.format.parse("2007-01-29T13Z"));
    }

    public void testNegativeYearCompliance() throws Exception {
        TimeKvpParser timeKvpParser = new TimeKvpParser("TIME");
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        // base assertion - 0001 is year 1
        DateRange date = ((DateRange) (((List) (timeKvpParser.parse("01-06-01"))).get(0)));
        cal.setTime(date.getMinValue());
        TestCase.assertEquals(1, cal.get(Calendar.YEAR));
        TestCase.assertEquals(GregorianCalendar.AD, cal.get(Calendar.ERA));
        date = ((DateRange) (((List) (timeKvpParser.parse("00-06-01"))).get(0)));
        cal.setTime(date.getMinValue());
        // calendar calls it year 1, ISO spec means it's year 0
        // but we're just parsing here...
        TestCase.assertEquals(1, cal.get(Calendar.YEAR));
        TestCase.assertEquals(GregorianCalendar.BC, cal.get(Calendar.ERA));
        // so, the next year should be 2
        date = ((DateRange) (((List) (timeKvpParser.parse("-01-06-01"))).get(0)));
        cal.setTime(date.getMinValue());
        TestCase.assertEquals(2, cal.get(Calendar.YEAR));
        TestCase.assertEquals(GregorianCalendar.BC, cal.get(Calendar.ERA));
        // now, big negative year compliance (see the spec, appendix D 2.2, pp 57-58)
        date = ((DateRange) (((List) (timeKvpParser.parse("-18000-06-01"))).get(0)));
        cal.setTime(date.getMinValue());
        TestCase.assertEquals(18001, cal.get(Calendar.YEAR));
        TestCase.assertEquals(GregorianCalendar.BC, cal.get(Calendar.ERA));
    }
}

