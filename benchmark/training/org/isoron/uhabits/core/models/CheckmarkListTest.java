/**
 * Copyright (C) 2017 ?linson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isoron.uhabits.core.models;


import CheckmarkList.Interval;
import Frequency.DAILY;
import Frequency.TWO_TIMES_PER_WEEK;
import Frequency.WEEKLY;
import Timestamp.ZERO;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Test;


public class CheckmarkListTest extends BaseUnitTest {
    private long dayLength;

    private Timestamp today;

    private Habit nonDailyHabit;

    private Habit emptyHabit;

    private Habit numericalHabit;

    @Test
    public void test_buildCheckmarksFromIntervals_1() throws Exception {
        Repetition[] reps = new Repetition[]{ new Repetition(day(10), CHECKED_EXPLICITLY), new Repetition(day(5), CHECKED_EXPLICITLY), new Repetition(day(2), CHECKED_EXPLICITLY), new Repetition(day(1), CHECKED_EXPLICITLY) };
        ArrayList<CheckmarkList.Interval> intervals = new ArrayList<>();
        intervals.add(new CheckmarkList.Interval(day(10), day(8), day(8)));
        intervals.add(new CheckmarkList.Interval(day(6), day(5), day(4)));
        intervals.add(new CheckmarkList.Interval(day(2), day(2), day(1)));
        List<Checkmark.Checkmark> expected = new ArrayList<>();
        expected.add(new Checkmark.Checkmark(day(0), UNCHECKED));
        expected.add(new Checkmark.Checkmark(day(1), CHECKED_EXPLICITLY));
        expected.add(new Checkmark.Checkmark(day(2), CHECKED_EXPLICITLY));
        expected.add(new Checkmark.Checkmark(day(3), UNCHECKED));
        expected.add(new Checkmark.Checkmark(day(4), CHECKED_IMPLICITLY));
        expected.add(new Checkmark.Checkmark(day(5), CHECKED_EXPLICITLY));
        expected.add(new Checkmark.Checkmark(day(6), CHECKED_IMPLICITLY));
        expected.add(new Checkmark.Checkmark(day(7), UNCHECKED));
        expected.add(new Checkmark.Checkmark(day(8), CHECKED_IMPLICITLY));
        expected.add(new Checkmark.Checkmark(day(9), CHECKED_IMPLICITLY));
        expected.add(new Checkmark.Checkmark(day(10), CHECKED_EXPLICITLY));
        List<Checkmark.Checkmark> actual = CheckmarkList.buildCheckmarksFromIntervals(reps, intervals);
        MatcherAssert.assertThat(actual, IsEqual.equalTo(expected));
    }

    @Test
    public void test_buildCheckmarksFromIntervals_2() throws Exception {
        Repetition[] reps = new Repetition[]{ new Repetition(day(0), CHECKED_EXPLICITLY) };
        ArrayList<CheckmarkList.Interval> intervals = new ArrayList<>();
        intervals.add(new CheckmarkList.Interval(day(0), day(0), day((-10))));
        List<Checkmark.Checkmark> expected = new ArrayList<>();
        expected.add(new Checkmark.Checkmark(day(0), CHECKED_EXPLICITLY));
        List<Checkmark.Checkmark> actual = CheckmarkList.buildCheckmarksFromIntervals(reps, intervals);
        MatcherAssert.assertThat(actual, IsEqual.equalTo(expected));
    }

    @Test
    public void test_buildIntervals_1() throws Exception {
        Repetition[] reps = new Repetition[]{ new Repetition(day(23), CHECKED_EXPLICITLY), new Repetition(day(18), CHECKED_EXPLICITLY), new Repetition(day(8), CHECKED_EXPLICITLY) };
        ArrayList<CheckmarkList.Interval> expected = new ArrayList<>();
        expected.add(new CheckmarkList.Interval(day(23), day(23), day(17)));
        expected.add(new CheckmarkList.Interval(day(18), day(18), day(12)));
        expected.add(new CheckmarkList.Interval(day(8), day(8), day(2)));
        ArrayList<CheckmarkList.Interval> actual;
        actual = CheckmarkList.buildIntervals(WEEKLY, reps);
        MatcherAssert.assertThat(actual, IsEqual.equalTo(expected));
    }

    @Test
    public void test_buildIntervals_2() throws Exception {
        Repetition[] reps = new Repetition[]{ new Repetition(day(23), CHECKED_EXPLICITLY), new Repetition(day(18), CHECKED_EXPLICITLY), new Repetition(day(8), CHECKED_EXPLICITLY) };
        ArrayList<CheckmarkList.Interval> expected = new ArrayList<>();
        expected.add(new CheckmarkList.Interval(day(23), day(23), day(23)));
        expected.add(new CheckmarkList.Interval(day(18), day(18), day(18)));
        expected.add(new CheckmarkList.Interval(day(8), day(8), day(8)));
        ArrayList<CheckmarkList.Interval> actual;
        actual = CheckmarkList.buildIntervals(DAILY, reps);
        MatcherAssert.assertThat(actual, IsEqual.equalTo(expected));
    }

    @Test
    public void test_buildIntervals_3() throws Exception {
        Repetition[] reps = new Repetition[]{ new Repetition(day(23), CHECKED_EXPLICITLY), new Repetition(day(22), CHECKED_EXPLICITLY), new Repetition(day(18), CHECKED_EXPLICITLY), new Repetition(day(15), CHECKED_EXPLICITLY), new Repetition(day(8), CHECKED_EXPLICITLY) };
        ArrayList<CheckmarkList.Interval> expected = new ArrayList<>();
        expected.add(new CheckmarkList.Interval(day(23), day(22), day(17)));
        expected.add(new CheckmarkList.Interval(day(22), day(18), day(16)));
        expected.add(new CheckmarkList.Interval(day(18), day(15), day(12)));
        ArrayList<CheckmarkList.Interval> actual;
        actual = CheckmarkList.buildIntervals(TWO_TIMES_PER_WEEK, reps);
        MatcherAssert.assertThat(actual, IsEqual.equalTo(expected));
    }

    @Test
    public void test_getAllValues_moveBackwardsInTime() {
        travelInTime((-3));
        int[] expectedValues = new int[]{ CHECKED_EXPLICITLY, CHECKED_EXPLICITLY, CHECKED_EXPLICITLY, CHECKED_IMPLICITLY, CHECKED_IMPLICITLY, CHECKED_EXPLICITLY, CHECKED_EXPLICITLY };
        int[] actualValues = nonDailyHabit.getCheckmarks().getAllValues();
        MatcherAssert.assertThat(actualValues, IsEqual.equalTo(expectedValues));
    }

    @Test
    public void test_getAllValues_moveForwardInTime() {
        travelInTime(3);
        int[] expectedValues = new int[]{ UNCHECKED, UNCHECKED, UNCHECKED, CHECKED_EXPLICITLY, UNCHECKED, CHECKED_IMPLICITLY, CHECKED_EXPLICITLY, CHECKED_EXPLICITLY, CHECKED_EXPLICITLY, CHECKED_IMPLICITLY, CHECKED_IMPLICITLY, CHECKED_EXPLICITLY, CHECKED_EXPLICITLY };
        int[] actualValues = nonDailyHabit.getCheckmarks().getAllValues();
        MatcherAssert.assertThat(actualValues, IsEqual.equalTo(expectedValues));
    }

    @Test
    public void test_getAllValues_withEmptyHabit() {
        int[] expectedValues = new int[0];
        int[] actualValues = emptyHabit.getCheckmarks().getAllValues();
        MatcherAssert.assertThat(actualValues, IsEqual.equalTo(expectedValues));
    }

    @Test
    public void test_getAllValues_withNonDailyHabit() {
        int[] expectedValues = new int[]{ CHECKED_EXPLICITLY, UNCHECKED, CHECKED_IMPLICITLY, CHECKED_EXPLICITLY, CHECKED_EXPLICITLY, CHECKED_EXPLICITLY, CHECKED_IMPLICITLY, CHECKED_IMPLICITLY, CHECKED_EXPLICITLY, CHECKED_EXPLICITLY };
        int[] actualValues = nonDailyHabit.getCheckmarks().getAllValues();
        MatcherAssert.assertThat(actualValues, IsEqual.equalTo(expectedValues));
    }

    @Test
    public void test_getByInterval_withNumericalHabits() throws Exception {
        CheckmarkList checkmarks = numericalHabit.getCheckmarks();
        List<Checkmark.Checkmark> expected = Arrays.asList(new Checkmark.Checkmark(day(1), 200), new Checkmark.Checkmark(day(2), 0), new Checkmark.Checkmark(day(3), 300), new Checkmark.Checkmark(day(4), 0), new Checkmark.Checkmark(day(5), 400));
        List<Checkmark.Checkmark> actual = checkmarks.getByInterval(day(5), day(1));
        MatcherAssert.assertThat(actual, IsEqual.equalTo(expected));
    }

    @Test
    public void test_getTodayValue() {
        CheckmarkList checkmarks = nonDailyHabit.getCheckmarks();
        travelInTime((-1));
        MatcherAssert.assertThat(checkmarks.getTodayValue(), IsEqual.equalTo(UNCHECKED));
        travelInTime(0);
        MatcherAssert.assertThat(checkmarks.getTodayValue(), IsEqual.equalTo(CHECKED_EXPLICITLY));
        travelInTime(1);
        MatcherAssert.assertThat(checkmarks.getTodayValue(), IsEqual.equalTo(UNCHECKED));
    }

    @Test
    public void test_getValues_withInvalidInterval() {
        int[] values = nonDailyHabit.getCheckmarks().getValues(new Timestamp(0L).plus(100), new Timestamp(0L));
        MatcherAssert.assertThat(values, IsEqual.equalTo(new int[0]));
    }

    @Test
    public void test_getValues_withValidInterval() {
        Timestamp from = today.minus(15);
        Timestamp to = today.minus(5);
        int[] expectedValues = new int[]{ CHECKED_EXPLICITLY, CHECKED_IMPLICITLY, CHECKED_IMPLICITLY, CHECKED_EXPLICITLY, CHECKED_EXPLICITLY, UNCHECKED, UNCHECKED, UNCHECKED, UNCHECKED, UNCHECKED, UNCHECKED };
        int[] actualValues = nonDailyHabit.getCheckmarks().getValues(from, to);
        MatcherAssert.assertThat(actualValues, IsEqual.equalTo(expectedValues));
    }

    @Test
    public void test_snapIntervalsTogether_1() throws Exception {
        ArrayList<CheckmarkList.Interval> original = new ArrayList<>();
        original.add(new CheckmarkList.Interval(day(40), day(40), day(34)));
        original.add(new CheckmarkList.Interval(day(25), day(25), day(19)));
        original.add(new CheckmarkList.Interval(day(16), day(16), day(10)));
        original.add(new CheckmarkList.Interval(day(8), day(8), day(2)));
        ArrayList<CheckmarkList.Interval> expected = new ArrayList<>();
        expected.add(new CheckmarkList.Interval(day(40), day(40), day(34)));
        expected.add(new CheckmarkList.Interval(day(25), day(25), day(19)));
        expected.add(new CheckmarkList.Interval(day(18), day(16), day(12)));
        expected.add(new CheckmarkList.Interval(day(11), day(8), day(5)));
        CheckmarkList.snapIntervalsTogether(original);
        MatcherAssert.assertThat(original, IsEqual.equalTo(expected));
    }

    @Test
    public void test_writeCSV() throws IOException {
        String expectedCSV = "2015-01-25,2\n2015-01-24,0\n2015-01-23,1\n" + (("2015-01-22,2\n2015-01-21,2\n2015-01-20,2\n" + "2015-01-19,1\n2015-01-18,1\n2015-01-17,2\n") + "2015-01-16,2\n");
        StringWriter writer = new StringWriter();
        nonDailyHabit.getCheckmarks().writeCSV(writer);
        MatcherAssert.assertThat(writer.toString(), IsEqual.equalTo(expectedCSV));
    }

    @Test
    public void testToString() throws Exception {
        Timestamp t = ZERO.plus(100);
        Checkmark.Checkmark checkmark = new Checkmark.Checkmark(t, 2);
        MatcherAssert.assertThat(checkmark.toString(), IsEqual.equalTo("{timestamp: 1970-04-11, value: 2}"));
        CheckmarkList.Interval interval = new CheckmarkList.Interval(t, t.plus(1), t.plus(2));
        MatcherAssert.assertThat(interval.toString(), IsEqual.equalTo("{begin: 1970-04-11, center: 1970-04-12, end: 1970-04-13}"));
    }

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(Checkmark.Checkmark.class).verify();
        EqualsVerifier.forClass(Timestamp.class).verify();
        EqualsVerifier.forClass(Interval.class).verify();
    }

    @Test
    public void testGroupBy() throws Exception {
        Habit habit = fixtures.createLongNumericalHabit(timestamp(2014, Calendar.JUNE, 1));
        CheckmarkList checkmarks = habit.getCheckmarks();
        List<Checkmark.Checkmark> byMonth = checkmarks.groupBy(MONTH);
        MatcherAssert.assertThat(byMonth.size(), IsEqual.equalTo(25));// from 2013-01-01 to 2015-01-01

        MatcherAssert.assertThat(byMonth.get(0), IsEqual.equalTo(new Checkmark.Checkmark(timestamp(2015, Calendar.JANUARY, 1), 0)));
        MatcherAssert.assertThat(byMonth.get(6), IsEqual.equalTo(new Checkmark.Checkmark(timestamp(2014, Calendar.JULY, 1), 0)));
        MatcherAssert.assertThat(byMonth.get(12), IsEqual.equalTo(new Checkmark.Checkmark(timestamp(2014, Calendar.JANUARY, 1), 1706)));
        MatcherAssert.assertThat(byMonth.get(18), IsEqual.equalTo(new Checkmark.Checkmark(timestamp(2013, Calendar.JULY, 1), 1379)));
        List<Checkmark.Checkmark> byQuarter = checkmarks.groupBy(QUARTER);
        MatcherAssert.assertThat(byQuarter.size(), IsEqual.equalTo(9));// from 2013-Q1 to 2015-Q1

        MatcherAssert.assertThat(byQuarter.get(0), IsEqual.equalTo(new Checkmark.Checkmark(timestamp(2015, Calendar.JANUARY, 1), 0)));
        MatcherAssert.assertThat(byQuarter.get(4), IsEqual.equalTo(new Checkmark.Checkmark(timestamp(2014, Calendar.JANUARY, 1), 4964)));
        MatcherAssert.assertThat(byQuarter.get(8), IsEqual.equalTo(new Checkmark.Checkmark(timestamp(2013, Calendar.JANUARY, 1), 4975)));
        List<Checkmark.Checkmark> byYear = checkmarks.groupBy(YEAR);
        MatcherAssert.assertThat(byYear.size(), IsEqual.equalTo(3));// from 2013 to 2015

        MatcherAssert.assertThat(byYear.get(0), IsEqual.equalTo(new Checkmark.Checkmark(timestamp(2015, Calendar.JANUARY, 1), 0)));
        MatcherAssert.assertThat(byYear.get(1), IsEqual.equalTo(new Checkmark.Checkmark(timestamp(2014, Calendar.JANUARY, 1), 8227)));
        MatcherAssert.assertThat(byYear.get(2), IsEqual.equalTo(new Checkmark.Checkmark(timestamp(2013, Calendar.JANUARY, 1), 16172)));
    }
}

