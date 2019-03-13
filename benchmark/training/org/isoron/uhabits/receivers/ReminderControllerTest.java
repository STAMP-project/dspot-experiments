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
package org.isoron.uhabits.receivers;


import Timestamp.ZERO;
import org.isoron.uhabits.BaseAndroidJVMTest;
import org.junit.Test;
import org.mockito.Mockito;


public class ReminderControllerTest extends BaseAndroidJVMTest {
    private ReminderController controller;

    private ReminderScheduler reminderScheduler;

    private NotificationTray notificationTray;

    private Preferences preferences;

    @Test
    public void testOnDismiss() throws Exception {
        Mockito.verifyNoMoreInteractions(reminderScheduler);
        Mockito.verifyNoMoreInteractions(notificationTray);
        Mockito.verifyNoMoreInteractions(preferences);
    }

    @Test
    public void testOnSnooze() throws Exception {
        Habit habit = Mockito.mock(Habit.class);
        long now = timestamp(2015, 1, 1);
        long nowTz = DateUtils.applyTimezone(now);
        DateUtils.setFixedLocalTime(now);
        Mockito.when(preferences.getSnoozeInterval()).thenReturn(15L);
        controller.onSnoozePressed(habit, null);
        Mockito.verify(reminderScheduler).scheduleMinutesFromNow(habit, 15L);
        Mockito.verify(notificationTray).cancel(habit);
    }

    @Test
    public void testOnShowReminder() throws Exception {
        Habit habit = Mockito.mock(Habit.class);
        controller.onShowReminder(habit, ZERO.plus(100), 456);
        Mockito.verify(notificationTray).show(habit, ZERO.plus(100), 456);
        Mockito.verify(reminderScheduler).scheduleAll();
    }

    @Test
    public void testOnBootCompleted() throws Exception {
        controller.onBootCompleted();
        Mockito.verify(reminderScheduler).scheduleAll();
    }
}

