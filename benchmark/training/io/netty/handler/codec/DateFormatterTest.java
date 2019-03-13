/**
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec;


import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class DateFormatterTest {
    /**
     * This date is set at "06 Nov 1994 08:49:37 GMT", from
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html">examples in RFC documentation</a>
     */
    private static final long TIMESTAMP = 784111777000L;

    private static final Date DATE = new Date(DateFormatterTest.TIMESTAMP);

    @Test
    public void testParseWithSingleDigitDay() {
        Assert.assertEquals(DateFormatterTest.DATE, parseHttpDate("Sun, 6 Nov 1994 08:49:37 GMT"));
    }

    @Test
    public void testParseWithDoubleDigitDay() {
        Assert.assertEquals(DateFormatterTest.DATE, parseHttpDate("Sun, 06 Nov 1994 08:49:37 GMT"));
    }

    @Test
    public void testParseWithDashSeparatorSingleDigitDay() {
        Assert.assertEquals(DateFormatterTest.DATE, parseHttpDate("Sunday, 06-Nov-94 08:49:37 GMT"));
    }

    @Test
    public void testParseWithSingleDoubleDigitDay() {
        Assert.assertEquals(DateFormatterTest.DATE, parseHttpDate("Sunday, 6-Nov-94 08:49:37 GMT"));
    }

    @Test
    public void testParseWithoutGMT() {
        Assert.assertEquals(DateFormatterTest.DATE, parseHttpDate("Sun Nov 6 08:49:37 1994"));
    }

    @Test
    public void testParseWithFunkyTimezone() {
        Assert.assertEquals(DateFormatterTest.DATE, parseHttpDate("Sun Nov 6 08:49:37 1994 -0000"));
    }

    @Test
    public void testParseWithSingleDigitHourMinutesAndSecond() {
        Assert.assertEquals(DateFormatterTest.DATE, parseHttpDate("Sunday, 6-Nov-94 8:49:37 GMT"));
    }

    @Test
    public void testParseWithSingleDigitTime() {
        Assert.assertEquals(DateFormatterTest.DATE, parseHttpDate("Sunday, 6 Nov 1994 8:49:37 GMT"));
        Date _08_09_37 = new Date(((DateFormatterTest.TIMESTAMP) - ((40 * 60) * 1000)));
        Assert.assertEquals(_08_09_37, parseHttpDate("Sunday, 6 Nov 1994 8:9:37 GMT"));
        Assert.assertEquals(_08_09_37, parseHttpDate("Sunday, 6 Nov 1994 8:09:37 GMT"));
        Date _08_09_07 = new Date(((DateFormatterTest.TIMESTAMP) - (((40 * 60) + 30) * 1000)));
        Assert.assertEquals(_08_09_07, parseHttpDate("Sunday, 6 Nov 1994 8:9:7 GMT"));
        Assert.assertEquals(_08_09_07, parseHttpDate("Sunday, 6 Nov 1994 8:9:07 GMT"));
    }

    @Test
    public void testParseMidnight() {
        Assert.assertEquals(new Date(784080000000L), parseHttpDate("Sunday, 6 Nov 1994 00:00:00 GMT"));
    }

    @Test
    public void testParseInvalidInput() {
        // missing field
        Assert.assertNull(parseHttpDate("Sun, Nov 1994 08:49:37 GMT"));
        Assert.assertNull(parseHttpDate("Sun, 6 1994 08:49:37 GMT"));
        Assert.assertNull(parseHttpDate("Sun, 6 Nov 08:49:37 GMT"));
        Assert.assertNull(parseHttpDate("Sun, 6 Nov 1994 :49:37 GMT"));
        Assert.assertNull(parseHttpDate("Sun, 6 Nov 1994 49:37 GMT"));
        Assert.assertNull(parseHttpDate("Sun, 6 Nov 1994 08::37 GMT"));
        Assert.assertNull(parseHttpDate("Sun, 6 Nov 1994 08:37 GMT"));
        Assert.assertNull(parseHttpDate("Sun, 6 Nov 1994 08:49: GMT"));
        Assert.assertNull(parseHttpDate("Sun, 6 Nov 1994 08:49 GMT"));
        // invalid value
        Assert.assertNull(parseHttpDate("Sun, 6 FOO 1994 08:49:37 GMT"));
        Assert.assertNull(parseHttpDate("Sun, 36 Nov 1994 08:49:37 GMT"));
        Assert.assertNull(parseHttpDate("Sun, 6 Nov 1994 28:49:37 GMT"));
        Assert.assertNull(parseHttpDate("Sun, 6 Nov 1994 08:69:37 GMT"));
        Assert.assertNull(parseHttpDate("Sun, 6 Nov 1994 08:49:67 GMT"));
        // wrong number of digits in timestamp
        Assert.assertNull(parseHttpDate("Sunday, 6 Nov 1994 0:0:000 GMT"));
        Assert.assertNull(parseHttpDate("Sunday, 6 Nov 1994 0:000:0 GMT"));
        Assert.assertNull(parseHttpDate("Sunday, 6 Nov 1994 000:0:0 GMT"));
    }

    @Test
    public void testFormat() {
        Assert.assertEquals("Sun, 6 Nov 1994 08:49:37 GMT", Assert.format(DateFormatterTest.DATE));
    }

    @Test
    public void testParseAllMonths() {
        Assert.assertEquals(Calendar.JANUARY, DateFormatterTest.getMonth(parseHttpDate("Sun, 6 Jan 1994 08:49:37 GMT")));
        Assert.assertEquals(Calendar.FEBRUARY, DateFormatterTest.getMonth(parseHttpDate("Sun, 6 Feb 1994 08:49:37 GMT")));
        Assert.assertEquals(Calendar.MARCH, DateFormatterTest.getMonth(parseHttpDate("Sun, 6 Mar 1994 08:49:37 GMT")));
        Assert.assertEquals(Calendar.APRIL, DateFormatterTest.getMonth(parseHttpDate("Sun, 6 Apr 1994 08:49:37 GMT")));
        Assert.assertEquals(Calendar.MAY, DateFormatterTest.getMonth(parseHttpDate("Sun, 6 May 1994 08:49:37 GMT")));
        Assert.assertEquals(Calendar.JUNE, DateFormatterTest.getMonth(parseHttpDate("Sun, 6 Jun 1994 08:49:37 GMT")));
        Assert.assertEquals(Calendar.JULY, DateFormatterTest.getMonth(parseHttpDate("Sun, 6 Jul 1994 08:49:37 GMT")));
        Assert.assertEquals(Calendar.AUGUST, DateFormatterTest.getMonth(parseHttpDate("Sun, 6 Aug 1994 08:49:37 GMT")));
        Assert.assertEquals(Calendar.SEPTEMBER, DateFormatterTest.getMonth(parseHttpDate("Sun, 6 Sep 1994 08:49:37 GMT")));
        Assert.assertEquals(Calendar.OCTOBER, DateFormatterTest.getMonth(parseHttpDate("Sun Oct 6 08:49:37 1994")));
        Assert.assertEquals(Calendar.NOVEMBER, DateFormatterTest.getMonth(parseHttpDate("Sun Nov 6 08:49:37 1994")));
        Assert.assertEquals(Calendar.DECEMBER, DateFormatterTest.getMonth(parseHttpDate("Sun Dec 6 08:49:37 1994")));
    }
}

