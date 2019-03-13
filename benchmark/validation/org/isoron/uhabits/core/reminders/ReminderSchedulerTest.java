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
package org.isoron.uhabits.core.reminders;


import ReminderScheduler.SystemScheduler;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.models.Habit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static WeekdayList.EVERY_DAY;


@RunWith(MockitoJUnitRunner.class)
public class ReminderSchedulerTest extends BaseUnitTest {
    private Habit habit;

    private ReminderScheduler reminderScheduler;

    @Mock
    private SystemScheduler sys;

    @Test
    public void testScheduleAll() {
        long now = unixTime(2015, 1, 26, 13, 0);
        DateUtils.setFixedLocalTime(now);
        Habit h1 = fixtures.createEmptyHabit();
        Habit h2 = fixtures.createEmptyHabit();
        Habit h3 = fixtures.createEmptyHabit();
        h1.setReminder(new Reminder(8, 30, EVERY_DAY));
        h2.setReminder(new Reminder(18, 30, EVERY_DAY));
        h3.setReminder(null);
        habitList.add(h1);
        habitList.add(h2);
        habitList.add(h3);
        reminderScheduler.scheduleAll();
        Mockito.verify(sys).scheduleShowReminder(ArgumentMatchers.eq(unixTime(2015, 1, 27, 12, 30)), ArgumentMatchers.eq(h1), ArgumentMatchers.anyLong());
        Mockito.verify(sys).scheduleShowReminder(ArgumentMatchers.eq(unixTime(2015, 1, 26, 22, 30)), ArgumentMatchers.eq(h2), ArgumentMatchers.anyLong());
        Mockito.verifyNoMoreInteractions(sys);
    }

    @Test
    public void testSchedule_atSpecificTime() {
        long atTime = unixTime(2015, 1, 30, 11, 30);
        long expectedCheckmarkTime = unixTime(2015, 1, 30, 0, 0);
        habit.setReminder(new Reminder(8, 30, EVERY_DAY));
        scheduleAndVerify(atTime, expectedCheckmarkTime, atTime);
    }

    @Test
    public void testSchedule_laterToday() {
        long now = unixTime(2015, 1, 26, 6, 30);
        DateUtils.setFixedLocalTime(now);
        long expectedCheckmarkTime = unixTime(2015, 1, 26, 0, 0);
        long expectedReminderTime = unixTime(2015, 1, 26, 12, 30);
        habit.setReminder(new Reminder(8, 30, EVERY_DAY));
        scheduleAndVerify(null, expectedCheckmarkTime, expectedReminderTime);
    }

    @Test
    public void testSchedule_tomorrow() {
        long now = unixTime(2015, 1, 26, 13, 0);
        DateUtils.setFixedLocalTime(now);
        long expectedCheckmarkTime = unixTime(2015, 1, 27, 0, 0);
        long expectedReminderTime = unixTime(2015, 1, 27, 12, 30);
        habit.setReminder(new Reminder(8, 30, EVERY_DAY));
        scheduleAndVerify(null, expectedCheckmarkTime, expectedReminderTime);
    }

    @Test
    public void testSchedule_withoutReminder() {
        reminderScheduler.schedule(habit);
        Mockito.verifyZeroInteractions(sys);
    }
}

