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


import Habit.AT_LEAST;
import Habit.AT_MOST;
import Habit.HabitData;
import Habit.NUMBER_HABIT;
import Warning.NONFINAL_FIELDS;
import org.hamcrest.CoreMatchers;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static WeekdayList.EVERY_DAY;


public class HabitTest extends BaseUnitTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testConstructor_default() {
        Habit habit = modelFactory.buildHabit();
        Assert.assertFalse(habit.isArchived());
        Assert.assertThat(habit.hasReminder(), CoreMatchers.is(false));
        Assert.assertNotNull(habit.getStreaks());
        Assert.assertNotNull(habit.getScores());
        Assert.assertNotNull(habit.getRepetitions());
        Assert.assertNotNull(habit.getCheckmarks());
    }

    @Test
    public void test_copyAttributes() {
        Habit model = modelFactory.buildHabit();
        model.setArchived(true);
        model.setColor(0);
        model.setFrequency(new Frequency(10, 20));
        model.setReminder(new Reminder(8, 30, new WeekdayList(1)));
        Habit habit = modelFactory.buildHabit();
        habit.copyFrom(model);
        Assert.assertThat(habit.isArchived(), CoreMatchers.is(model.isArchived()));
        Assert.assertThat(habit.getColor(), CoreMatchers.is(model.getColor()));
        Assert.assertThat(habit.getFrequency(), CoreMatchers.equalTo(model.getFrequency()));
        Assert.assertThat(habit.getReminder(), CoreMatchers.equalTo(model.getReminder()));
    }

    @Test
    public void test_hasReminder_clearReminder() {
        Habit h = modelFactory.buildHabit();
        Assert.assertThat(h.hasReminder(), CoreMatchers.is(false));
        h.setReminder(new Reminder(8, 30, EVERY_DAY));
        Assert.assertThat(h.hasReminder(), CoreMatchers.is(true));
        h.clearReminder();
        Assert.assertThat(h.hasReminder(), CoreMatchers.is(false));
    }

    @Test
    public void test_isCompleted() throws Exception {
        Habit h = modelFactory.buildHabit();
        Assert.assertFalse(h.isCompletedToday());
        h.getRepetitions().toggle(getToday());
        Assert.assertTrue(h.isCompletedToday());
    }

    @Test
    public void test_isCompleted_numerical() throws Exception {
        Habit h = modelFactory.buildHabit();
        h.setType(NUMBER_HABIT);
        h.setTargetType(AT_LEAST);
        h.setTargetValue(100.0);
        Assert.assertFalse(h.isCompletedToday());
        h.getRepetitions().toggle(getToday(), 200);
        Assert.assertTrue(h.isCompletedToday());
        h.getRepetitions().toggle(getToday(), 100);
        Assert.assertTrue(h.isCompletedToday());
        h.getRepetitions().toggle(getToday(), 50);
        Assert.assertFalse(h.isCompletedToday());
        h.setTargetType(AT_MOST);
        h.getRepetitions().toggle(getToday(), 200);
        Assert.assertFalse(h.isCompletedToday());
        h.getRepetitions().toggle(getToday(), 100);
        Assert.assertTrue(h.isCompletedToday());
        h.getRepetitions().toggle(getToday(), 50);
        Assert.assertTrue(h.isCompletedToday());
    }

    @Test
    public void testURI() throws Exception {
        Assert.assertTrue(habitList.isEmpty());
        Habit h = modelFactory.buildHabit();
        habitList.add(h);
        Assert.assertThat(h.getId(), CoreMatchers.equalTo(0L));
        Assert.assertThat(h.getUriString(), CoreMatchers.equalTo("content://org.isoron.uhabits/habit/0"));
    }

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(HabitData.class).suppress(NONFINAL_FIELDS).verify();
        EqualsVerifier.forClass(Repetition.class).verify();
        EqualsVerifier.forClass(Score.class).verify();
        EqualsVerifier.forClass(Streak.class).verify();
        EqualsVerifier.forClass(Reminder.class).verify();
        EqualsVerifier.forClass(WeekdayList.class).verify();
    }

    @Test
    public void testToString() throws Exception {
        Habit h = modelFactory.buildHabit();
        h.setReminder(new Reminder(22, 30, EVERY_DAY));
        String expected = "{id: <null>, data: {name: , description: ," + (((((" frequency: {numerator: 3, denominator: 7}," + " color: 8, archived: false, targetType: 0,") + " targetValue: 100.0, type: 0, unit: ,") + " reminder: {hour: 22, minute: 30,") + " days: {weekdays: [true,true,true,true,true,true,true]}},") + " position: 0}}");
        Assert.assertThat(h.toString(), CoreMatchers.equalTo(expected));
    }
}

