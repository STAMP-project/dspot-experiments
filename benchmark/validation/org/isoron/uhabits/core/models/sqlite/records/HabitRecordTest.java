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
package org.isoron.uhabits.core.models.sqlite.records;


import Frequency.DAILY;
import Frequency.THREE_TIMES_PER_WEEK;
import Habit.AT_LEAST;
import Habit.NUMBER_HABIT;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Test;

import static WeekdayList.EVERY_DAY;


public class HabitRecordTest extends BaseUnitTest {
    @Test
    public void testCopyRestore1() {
        Habit original = modelFactory.buildHabit();
        original.setName("Hello world");
        original.setDescription("Did you greet the world today?");
        original.setColor(1);
        original.setArchived(true);
        original.setFrequency(THREE_TIMES_PER_WEEK);
        original.setReminder(new Reminder(8, 30, EVERY_DAY));
        original.setId(1000L);
        original.setPosition(20);
        HabitRecord record = new HabitRecord();
        record.copyFrom(original);
        Habit duplicate = modelFactory.buildHabit();
        record.copyTo(duplicate);
        MatcherAssert.assertThat(original.getData(), IsEqual.equalTo(duplicate.getData()));
    }

    @Test
    public void testCopyRestore2() {
        Habit original = modelFactory.buildHabit();
        original.setName("Hello world");
        original.setDescription("Did you greet the world today?");
        original.setColor(5);
        original.setArchived(false);
        original.setFrequency(DAILY);
        original.setReminder(null);
        original.setId(1L);
        original.setPosition(15);
        original.setType(NUMBER_HABIT);
        original.setTargetValue(100);
        original.setTargetType(AT_LEAST);
        original.setUnit("miles");
        HabitRecord record = new HabitRecord();
        record.copyFrom(original);
        Habit duplicate = modelFactory.buildHabit();
        record.copyTo(duplicate);
        MatcherAssert.assertThat(original.getData(), IsEqual.equalTo(duplicate.getData()));
    }
}

