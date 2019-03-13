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


import Habit.NUMBER_HABIT;
import ModelObservable.Listener;
import Timestamp.ZERO;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.NonNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class RepetitionListTest extends BaseUnitTest {
    @NonNull
    private RepetitionList reps;

    @NonNull
    private Habit habit;

    private Timestamp today;

    private long day;

    @NonNull
    private Listener listener;

    @Test
    public void test_contains() {
        Assert.assertTrue(reps.containsTimestamp(today));
        Assert.assertTrue(reps.containsTimestamp(today.minus(2)));
        Assert.assertTrue(reps.containsTimestamp(today.minus(3)));
        TestCase.assertFalse(reps.containsTimestamp(today.minus(1)));
        TestCase.assertFalse(reps.containsTimestamp(today.minus(4)));
    }

    @Test
    public void test_getOldest() {
        Repetition rep = reps.getOldest();
        MatcherAssert.assertThat(rep.getTimestamp(), IsEqual.equalTo(today.minus(7)));
    }

    @Test
    public void test_getWeekDayFrequency() {
        habit = fixtures.createEmptyHabit();
        reps = habit.getRepetitions();
        Random random = new Random(123L);
        Integer[][] weekdayCount = new Integer[12][7];
        Integer[] monthCount = new Integer[12];
        Arrays.fill(monthCount, 0);
        for (Integer[] row : weekdayCount)
            Arrays.fill(row, 0);

        GregorianCalendar day = DateUtils.getStartOfTodayCalendar();
        // Sets the current date to the end of November
        day.set(2015, Calendar.NOVEMBER, 30, 12, 0, 0);
        DateUtils.setFixedLocalTime(day.getTimeInMillis());
        // Add repetitions randomly from January to December
        day.set(2015, Calendar.JANUARY, 1, 0, 0, 0);
        for (int i = 0; i < 365; i++) {
            if (random.nextBoolean()) {
                int month = day.get(Calendar.MONTH);
                int week = (day.get(Calendar.DAY_OF_WEEK)) % 7;
                // Leave the month of March empty, to check that it returns null
                if (month == (Calendar.MARCH))
                    continue;

                reps.toggle(new Timestamp(day));
                // Repetitions in December should not be counted
                if (month == (Calendar.DECEMBER))
                    continue;

                (weekdayCount[month][week])++;
                (monthCount[month])++;
            }
            day.add(Calendar.DAY_OF_YEAR, 1);
        }
        HashMap<Timestamp, Integer[]> freq = reps.getWeekdayFrequency();
        // Repetitions until November should be counted correctly
        for (int month = 0; month < 11; month++) {
            day.set(2015, month, 1, 0, 0, 0);
            Integer[] actualCount = freq.get(new Timestamp(day));
            if ((monthCount[month]) == 0)
                MatcherAssert.assertThat(actualCount, IsEqual.equalTo(null));
            else
                MatcherAssert.assertThat(actualCount, IsEqual.equalTo(weekdayCount[month]));

        }
        // Repetitions in December should be discarded
        day.set(2015, Calendar.DECEMBER, 1, 0, 0, 0);
        MatcherAssert.assertThat(freq.get(new Timestamp(day)), IsEqual.equalTo(null));
    }

    @Test
    public void test_toggle() {
        Assert.assertTrue(reps.containsTimestamp(today));
        reps.toggle(today);
        TestCase.assertFalse(reps.containsTimestamp(today));
        Mockito.verify(listener).onModelChange();
        Mockito.reset(listener);
        TestCase.assertFalse(reps.containsTimestamp(today.minus(1)));
        reps.toggle(today.minus(1));
        Assert.assertTrue(reps.containsTimestamp(today.minus(1)));
        Mockito.verify(listener).onModelChange();
        Mockito.reset(listener);
        habit.setType(NUMBER_HABIT);
        reps.toggle(today, 100);
        Repetition check = reps.getByTimestamp(today);
        Assert.assertNotNull(check);
        MatcherAssert.assertThat(check.getValue(), IsEqual.equalTo(100));
        Mockito.verify(listener).onModelChange();
        Mockito.reset(listener);
        reps.toggle(today, 500);
        check = reps.getByTimestamp(today);
        Assert.assertNotNull(check);
        MatcherAssert.assertThat(check.getValue(), IsEqual.equalTo(500));
        Mockito.verify(listener, Mockito.times(2)).onModelChange();
        Mockito.reset(listener);
    }

    @Test
    public void testToString() throws Exception {
        Repetition rep = new Repetition(ZERO.plus(100), 20);
        MatcherAssert.assertThat(rep.toString(), IsEqual.equalTo("{timestamp: 1970-04-11, value: 20}"));
    }
}

