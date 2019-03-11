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
package org.isoron.uhabits.core.commands;


import ToggleRepetitionCommand.Record;
import junit.framework.Assert;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.models.Habit;
import org.isoron.uhabits.core.models.Timestamp;
import org.junit.Test;


public class ToggleRepetitionCommandTest extends BaseUnitTest {
    private ToggleRepetitionCommand command;

    private Habit habit;

    private Timestamp today;

    @Test
    public void testExecuteUndoRedo() {
        Assert.assertTrue(habit.getRepetitions().containsTimestamp(today));
        command.execute();
        Assert.assertFalse(habit.getRepetitions().containsTimestamp(today));
        command.undo();
        Assert.assertTrue(habit.getRepetitions().containsTimestamp(today));
        command.execute();
        Assert.assertFalse(habit.getRepetitions().containsTimestamp(today));
    }

    @Test
    public void testRecord() {
        ToggleRepetitionCommand.Record rec = command.toRecord();
        ToggleRepetitionCommand other = rec.toCommand(habitList);
        MatcherAssert.assertThat(command.getId(), Matchers.equalTo(other.getId()));
        MatcherAssert.assertThat(command.timestamp, Matchers.equalTo(other.timestamp));
        MatcherAssert.assertThat(command.habit, Matchers.equalTo(other.habit));
    }
}

