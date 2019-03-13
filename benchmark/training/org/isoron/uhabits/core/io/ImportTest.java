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
package org.isoron.uhabits.core.io;


import Frequency.DAILY;
import java.io.IOException;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;


public class ImportTest extends BaseUnitTest {
    @Test
    public void testHabitBullCSV() throws IOException {
        importFromFile("habitbull.csv");
        MatcherAssert.assertThat(habitList.size(), IsEqual.equalTo(4));
        Habit habit = habitList.getByPosition(0);
        MatcherAssert.assertThat(habit.getName(), IsEqual.equalTo("Breed dragons"));
        MatcherAssert.assertThat(habit.getDescription(), IsEqual.equalTo("with love and fire"));
        MatcherAssert.assertThat(habit.getFrequency(), IsEqual.equalTo(DAILY));
        Assert.assertTrue(containsRepetition(habit, 2016, 3, 18));
        Assert.assertTrue(containsRepetition(habit, 2016, 3, 19));
        TestCase.assertFalse(containsRepetition(habit, 2016, 3, 20));
    }

    @Test
    public void testLoopDB() throws IOException {
        importFromFile("loop.db");
        MatcherAssert.assertThat(habitList.size(), IsEqual.equalTo(9));
        Habit habit = habitList.getByPosition(0);
        MatcherAssert.assertThat(habit.getName(), IsEqual.equalTo("Wake up early"));
        MatcherAssert.assertThat(habit.getFrequency(), IsEqual.equalTo(THREE_TIMES_PER_WEEK));
        Assert.assertTrue(containsRepetition(habit, 2016, 3, 14));
        Assert.assertTrue(containsRepetition(habit, 2016, 3, 16));
        TestCase.assertFalse(containsRepetition(habit, 2016, 3, 17));
    }

    @Test
    public void testRewireDB() throws IOException {
        importFromFile("rewire.db");
        MatcherAssert.assertThat(habitList.size(), IsEqual.equalTo(3));
        Habit habit = habitList.getByPosition(0);
        MatcherAssert.assertThat(habit.getName(), IsEqual.equalTo("Wake up early"));
        MatcherAssert.assertThat(habit.getFrequency(), IsEqual.equalTo(THREE_TIMES_PER_WEEK));
        TestCase.assertFalse(habit.hasReminder());
        TestCase.assertFalse(containsRepetition(habit, 2015, 12, 31));
        Assert.assertTrue(containsRepetition(habit, 2016, 1, 18));
        Assert.assertTrue(containsRepetition(habit, 2016, 1, 28));
        TestCase.assertFalse(containsRepetition(habit, 2016, 3, 10));
        habit = habitList.getByPosition(1);
        MatcherAssert.assertThat(habit.getName(), IsEqual.equalTo("brush teeth"));
        MatcherAssert.assertThat(habit.getFrequency(), IsEqual.equalTo(THREE_TIMES_PER_WEEK));
        MatcherAssert.assertThat(habit.hasReminder(), IsEqual.equalTo(true));
        Reminder reminder = habit.getReminder();
        MatcherAssert.assertThat(reminder.getHour(), IsEqual.equalTo(8));
        MatcherAssert.assertThat(reminder.getMinute(), IsEqual.equalTo(0));
        boolean[] reminderDays = new boolean[]{ false, true, true, true, true, true, false };
        MatcherAssert.assertThat(reminder.getDays().toArray(), IsEqual.equalTo(reminderDays));
    }

    @Test
    public void testTickmateDB() throws IOException {
        importFromFile("tickmate.db");
        MatcherAssert.assertThat(habitList.size(), IsEqual.equalTo(3));
        Habit h = habitList.getByPosition(0);
        MatcherAssert.assertThat(h.getName(), IsEqual.equalTo("Vegan"));
        Assert.assertTrue(containsRepetition(h, 2016, 1, 24));
        Assert.assertTrue(containsRepetition(h, 2016, 2, 5));
        Assert.assertTrue(containsRepetition(h, 2016, 3, 18));
        TestCase.assertFalse(containsRepetition(h, 2016, 3, 14));
    }
}

