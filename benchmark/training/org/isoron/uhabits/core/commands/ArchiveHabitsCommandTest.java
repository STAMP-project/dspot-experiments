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


import ArchiveHabitsCommand.Record;
import junit.framework.Assert;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.models.Habit;
import org.junit.Test;


public class ArchiveHabitsCommandTest extends BaseUnitTest {
    private ArchiveHabitsCommand command;

    private Habit habit;

    @Test
    public void testExecuteUndoRedo() {
        Assert.assertFalse(habit.isArchived());
        command.execute();
        Assert.assertTrue(habit.isArchived());
        command.undo();
        Assert.assertFalse(habit.isArchived());
        command.execute();
        Assert.assertTrue(habit.isArchived());
    }

    @Test
    public void testRecord() {
        ArchiveHabitsCommand.Record rec = command.toRecord();
        ArchiveHabitsCommand other = rec.toCommand(habitList);
        MatcherAssert.assertThat(other.selected, equalTo(command.selected));
        MatcherAssert.assertThat(other.getId(), equalTo(command.getId()));
    }
}

