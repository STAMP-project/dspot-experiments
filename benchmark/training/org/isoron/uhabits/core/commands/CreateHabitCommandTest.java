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


import CreateHabitCommand.Record;
import junit.framework.Assert;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.models.Habit;
import org.junit.Test;


public class CreateHabitCommandTest extends BaseUnitTest {
    private CreateHabitCommand command;

    private Habit model;

    @Test
    public void testExecuteUndoRedo() {
        Assert.assertTrue(habitList.isEmpty());
        command.execute();
        MatcherAssert.assertThat(habitList.size(), CoreMatchers.equalTo(1));
        Habit habit = habitList.getByPosition(0);
        Long id = habit.getId();
        MatcherAssert.assertThat(habit.getName(), CoreMatchers.equalTo(model.getName()));
        command.undo();
        Assert.assertTrue(habitList.isEmpty());
        command.execute();
        MatcherAssert.assertThat(habitList.size(), CoreMatchers.equalTo(1));
        habit = habitList.getByPosition(0);
        Long newId = habit.getId();
        MatcherAssert.assertThat(id, CoreMatchers.equalTo(newId));
        MatcherAssert.assertThat(habit.getName(), CoreMatchers.equalTo(model.getName()));
    }

    @Test
    public void testRecord() {
        command.execute();
        CreateHabitCommand.Record rec = command.toRecord();
        CreateHabitCommand other = rec.toCommand(modelFactory, habitList);
        MatcherAssert.assertThat(other.getId(), CoreMatchers.equalTo(command.getId()));
        MatcherAssert.assertThat(other.savedId, CoreMatchers.equalTo(command.savedId));
        MatcherAssert.assertThat(other.model.getData(), CoreMatchers.equalTo(command.model.getData()));
    }
}

