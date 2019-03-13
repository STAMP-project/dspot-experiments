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


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.BaseAndroidJVMTest;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class WidgetControllerTest extends BaseAndroidJVMTest {
    private WidgetBehavior controller;

    private CommandRunner commandRunner;

    private Habit habit;

    private Timestamp today;

    private NotificationTray notificationTray;

    @Test
    public void testOnAddRepetition_whenChecked() throws Exception {
        habit.getRepetitions().toggle(today);
        int todayValue = habit.getCheckmarks().getTodayValue();
        MatcherAssert.assertThat(todayValue, IsEqual.equalTo(CHECKED_EXPLICITLY));
        controller.onAddRepetition(habit, today);
        Mockito.verifyZeroInteractions(commandRunner);
    }

    @Test
    public void testOnAddRepetition_whenUnchecked() throws Exception {
        int todayValue = habit.getCheckmarks().getTodayValue();
        MatcherAssert.assertThat(todayValue, IsEqual.equalTo(UNCHECKED));
        controller.onAddRepetition(habit, today);
        Mockito.verify(commandRunner).execute(ArgumentMatchers.any(), ArgumentMatchers.isNull());
        Mockito.verify(notificationTray).cancel(habit);
    }

    @Test
    public void testOnRemoveRepetition_whenChecked() throws Exception {
        habit.getRepetitions().toggle(today);
        int todayValue = habit.getCheckmarks().getTodayValue();
        MatcherAssert.assertThat(todayValue, IsEqual.equalTo(CHECKED_EXPLICITLY));
        controller.onRemoveRepetition(habit, today);
        Mockito.verify(commandRunner).execute(ArgumentMatchers.any(), ArgumentMatchers.isNull());
    }

    @Test
    public void testOnRemoveRepetition_whenUnchecked() throws Exception {
        int todayValue = habit.getCheckmarks().getTodayValue();
        MatcherAssert.assertThat(todayValue, IsEqual.equalTo(UNCHECKED));
        controller.onRemoveRepetition(habit, today);
        Mockito.verifyZeroInteractions(commandRunner);
    }

    @Test
    public void testOnToggleRepetition() throws Exception {
        controller.onToggleRepetition(habit, today);
        Mockito.verify(commandRunner).execute(ArgumentMatchers.any(), ArgumentMatchers.isNull());
    }
}

