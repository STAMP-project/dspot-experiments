/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.transforms.windowing;


import CalendarWindows.DaysWindows;
import CalendarWindows.YearsWindows;
import DateTimeConstants.WEDNESDAY;
import Duration.ZERO;
import GlobalWindow.INSTANCE;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.beam.sdk.testing.WindowFnTestUtils;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.display.DisplayDataMatchers;
import org.apache.beam.sdk.transforms.windowing.CalendarWindows.MonthsWindows;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for CalendarWindows WindowFn.
 */
@RunWith(JUnit4.class)
public class CalendarWindowsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDays() throws Exception {
        Map<IntervalWindow, Set<String>> expected = new HashMap<>();
        final List<Long> timestamps = Arrays.asList(CalendarWindowsTest.makeTimestamp(2014, 1, 1, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2014, 1, 1, 23, 59).getMillis(), CalendarWindowsTest.makeTimestamp(2014, 1, 2, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2014, 1, 2, 5, 5).getMillis(), CalendarWindowsTest.makeTimestamp(2015, 1, 1, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2015, 1, 1, 5, 5).getMillis());
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2014, 1, 1, 0, 0), CalendarWindowsTest.makeTimestamp(2014, 1, 2, 0, 0)), WindowFnTestUtils.set(timestamps.get(0), timestamps.get(1)));
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2014, 1, 2, 0, 0), CalendarWindowsTest.makeTimestamp(2014, 1, 3, 0, 0)), WindowFnTestUtils.set(timestamps.get(2), timestamps.get(3)));
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2015, 1, 1, 0, 0), CalendarWindowsTest.makeTimestamp(2015, 1, 2, 0, 0)), WindowFnTestUtils.set(timestamps.get(4), timestamps.get(5)));
        Assert.assertEquals(expected, WindowFnTestUtils.runWindowFn(CalendarWindows.days(1), timestamps));
    }

    @Test
    public void testDaysCompatibility() throws IncompatibleWindowException {
        CalendarWindows.DaysWindows daysWindows = CalendarWindows.days(10);
        daysWindows.verifyCompatibility(CalendarWindows.days(10));
        thrown.expect(IncompatibleWindowException.class);
        daysWindows.verifyCompatibility(CalendarWindows.days(9));
    }

    @Test
    public void testWeeks() throws Exception {
        Map<IntervalWindow, Set<String>> expected = new HashMap<>();
        final List<Long> timestamps = Arrays.asList(CalendarWindowsTest.makeTimestamp(2014, 1, 1, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2014, 1, 5, 5, 5).getMillis(), CalendarWindowsTest.makeTimestamp(2014, 1, 8, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2014, 1, 12, 5, 5).getMillis(), CalendarWindowsTest.makeTimestamp(2015, 1, 1, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2015, 1, 6, 5, 5).getMillis());
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2014, 1, 1, 0, 0), CalendarWindowsTest.makeTimestamp(2014, 1, 8, 0, 0)), WindowFnTestUtils.set(timestamps.get(0), timestamps.get(1)));
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2014, 1, 8, 0, 0), CalendarWindowsTest.makeTimestamp(2014, 1, 15, 0, 0)), WindowFnTestUtils.set(timestamps.get(2), timestamps.get(3)));
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2014, 12, 31, 0, 0), CalendarWindowsTest.makeTimestamp(2015, 1, 7, 0, 0)), WindowFnTestUtils.set(timestamps.get(4), timestamps.get(5)));
        Assert.assertEquals(expected, WindowFnTestUtils.runWindowFn(CalendarWindows.weeks(1, WEDNESDAY), timestamps));
    }

    @Test
    public void testMonths() throws Exception {
        Map<IntervalWindow, Set<String>> expected = new HashMap<>();
        final List<Long> timestamps = Arrays.asList(CalendarWindowsTest.makeTimestamp(2014, 1, 1, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2014, 1, 31, 5, 5).getMillis(), CalendarWindowsTest.makeTimestamp(2014, 2, 1, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2014, 2, 15, 5, 5).getMillis(), CalendarWindowsTest.makeTimestamp(2015, 1, 1, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2015, 1, 31, 5, 5).getMillis());
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2014, 1, 1, 0, 0), CalendarWindowsTest.makeTimestamp(2014, 2, 1, 0, 0)), WindowFnTestUtils.set(timestamps.get(0), timestamps.get(1)));
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2014, 2, 1, 0, 0), CalendarWindowsTest.makeTimestamp(2014, 3, 1, 0, 0)), WindowFnTestUtils.set(timestamps.get(2), timestamps.get(3)));
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2015, 1, 1, 0, 0), CalendarWindowsTest.makeTimestamp(2015, 2, 1, 0, 0)), WindowFnTestUtils.set(timestamps.get(4), timestamps.get(5)));
        Assert.assertEquals(expected, WindowFnTestUtils.runWindowFn(CalendarWindows.months(1), timestamps));
    }

    @Test
    public void testMonthsCompatibility() throws IncompatibleWindowException {
        CalendarWindows.MonthsWindows monthsWindows = CalendarWindows.months(10).beginningOnDay(15);
        monthsWindows.verifyCompatibility(CalendarWindows.months(10).beginningOnDay(15));
        thrown.expect(IncompatibleWindowException.class);
        monthsWindows.verifyCompatibility(CalendarWindows.months(10).beginningOnDay(30));
    }

    @Test
    public void testMultiMonths() throws Exception {
        Map<IntervalWindow, Set<String>> expected = new HashMap<>();
        final List<Long> timestamps = Arrays.asList(CalendarWindowsTest.makeTimestamp(2014, 3, 5, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2014, 10, 4, 23, 59).getMillis(), CalendarWindowsTest.makeTimestamp(2014, 10, 5, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2015, 3, 1, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2016, 1, 5, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2016, 1, 31, 5, 5).getMillis());
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2014, 3, 5, 0, 0), CalendarWindowsTest.makeTimestamp(2014, 10, 5, 0, 0)), WindowFnTestUtils.set(timestamps.get(0), timestamps.get(1)));
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2014, 10, 5, 0, 0), CalendarWindowsTest.makeTimestamp(2015, 5, 5, 0, 0)), WindowFnTestUtils.set(timestamps.get(2), timestamps.get(3)));
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2015, 12, 5, 0, 0), CalendarWindowsTest.makeTimestamp(2016, 7, 5, 0, 0)), WindowFnTestUtils.set(timestamps.get(4), timestamps.get(5)));
        Assert.assertEquals(expected, WindowFnTestUtils.runWindowFn(CalendarWindows.months(7).withStartingMonth(2014, 3).beginningOnDay(5), timestamps));
    }

    @Test
    public void testYears() throws Exception {
        Map<IntervalWindow, Set<String>> expected = new HashMap<>();
        final List<Long> timestamps = Arrays.asList(CalendarWindowsTest.makeTimestamp(2000, 5, 5, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2010, 5, 4, 23, 59).getMillis(), CalendarWindowsTest.makeTimestamp(2010, 5, 5, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2015, 3, 1, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2052, 1, 5, 0, 0).getMillis(), CalendarWindowsTest.makeTimestamp(2060, 5, 4, 5, 5).getMillis());
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2000, 5, 5, 0, 0), CalendarWindowsTest.makeTimestamp(2010, 5, 5, 0, 0)), WindowFnTestUtils.set(timestamps.get(0), timestamps.get(1)));
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2010, 5, 5, 0, 0), CalendarWindowsTest.makeTimestamp(2020, 5, 5, 0, 0)), WindowFnTestUtils.set(timestamps.get(2), timestamps.get(3)));
        expected.put(new IntervalWindow(CalendarWindowsTest.makeTimestamp(2050, 5, 5, 0, 0), CalendarWindowsTest.makeTimestamp(2060, 5, 5, 0, 0)), WindowFnTestUtils.set(timestamps.get(4), timestamps.get(5)));
        Assert.assertEquals(expected, WindowFnTestUtils.runWindowFn(CalendarWindows.years(10).withStartingYear(2000).beginningOnDay(5, 5), timestamps));
    }

    @Test
    public void testYearsCompatibility() throws IncompatibleWindowException {
        CalendarWindows.YearsWindows yearsWindows = CalendarWindows.years(2017).beginningOnDay(1, 1);
        yearsWindows.verifyCompatibility(CalendarWindows.years(2017).beginningOnDay(1, 1));
        thrown.expect(IncompatibleWindowException.class);
        yearsWindows.verifyCompatibility(CalendarWindows.years(2017).beginningOnDay(1, 2));
    }

    @Test
    public void testTimeZone() throws Exception {
        Map<IntervalWindow, Set<String>> expected = new HashMap<>();
        DateTimeZone timeZone = DateTimeZone.forID("America/Los_Angeles");
        final List<Long> timestamps = Arrays.asList(getMillis(), getMillis(), getMillis(), getMillis());
        expected.put(new IntervalWindow(toInstant(), toInstant()), WindowFnTestUtils.set(timestamps.get(0), timestamps.get(1)));
        expected.put(new IntervalWindow(toInstant(), toInstant()), WindowFnTestUtils.set(timestamps.get(2), timestamps.get(3)));
        Assert.assertEquals(expected, WindowFnTestUtils.runWindowFn(CalendarWindows.days(1).withTimeZone(timeZone), timestamps));
    }

    @Test
    public void testDefaultWindowMappingFn() {
        MonthsWindows windowFn = CalendarWindows.months(2);
        WindowMappingFn<?> mapping = windowFn.getDefaultWindowMappingFn();
        MatcherAssert.assertThat(mapping.getSideInputWindow(new BoundedWindow() {
            @Override
            public Instant maxTimestamp() {
                return new Instant(100L);
            }
        }), Matchers.equalTo(windowFn.assignWindow(new Instant(100L))));
        MatcherAssert.assertThat(mapping.maximumLookback(), Matchers.equalTo(ZERO));
    }

    @Test
    public void testDefaultWindowMappingFnGlobal() {
        MonthsWindows windowFn = CalendarWindows.months(2);
        WindowMappingFn<?> mapping = windowFn.getDefaultWindowMappingFn();
        thrown.expect(IllegalArgumentException.class);
        mapping.getSideInputWindow(INSTANCE);
    }

    @Test
    public void testDisplayData() {
        DateTimeZone timeZone = DateTimeZone.forID("America/Los_Angeles");
        Instant jan1 = new org.joda.time.DateTime(1990, 1, 1, 0, 0, timeZone).toInstant();
        CalendarWindows.DaysWindows daysWindow = CalendarWindows.days(5).withStartingDay(1990, 1, 1).withTimeZone(timeZone);
        DisplayData daysDisplayData = DisplayData.from(daysWindow);
        MatcherAssert.assertThat(daysDisplayData, DisplayDataMatchers.hasDisplayItem("numDays", 5));
        MatcherAssert.assertThat(daysDisplayData, DisplayDataMatchers.hasDisplayItem("startDate", jan1));
        CalendarWindows.MonthsWindows monthsWindow = CalendarWindows.months(2).withStartingMonth(1990, 1).withTimeZone(timeZone);
        DisplayData monthsDisplayData = DisplayData.from(monthsWindow);
        MatcherAssert.assertThat(monthsDisplayData, DisplayDataMatchers.hasDisplayItem("numMonths", 2));
        MatcherAssert.assertThat(monthsDisplayData, DisplayDataMatchers.hasDisplayItem("startDate", jan1));
        CalendarWindows.YearsWindows yearsWindow = CalendarWindows.years(4).withStartingYear(1990).withTimeZone(timeZone);
        DisplayData yearsDisplayData = DisplayData.from(yearsWindow);
        MatcherAssert.assertThat(yearsDisplayData, DisplayDataMatchers.hasDisplayItem("numYears", 4));
        MatcherAssert.assertThat(yearsDisplayData, DisplayDataMatchers.hasDisplayItem("startDate", jan1));
    }
}

