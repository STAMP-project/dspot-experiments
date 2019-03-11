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


import DeleteHabitsCommand.Record;
import java.util.LinkedList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DeleteHabitsCommandTest extends BaseUnitTest {
    private DeleteHabitsCommand command;

    private LinkedList<Habit> selected;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testExecuteUndoRedo() {
        MatcherAssert.assertThat(habitList.size(), CoreMatchers.equalTo(4));
        command.execute();
        MatcherAssert.assertThat(habitList.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(habitList.getByPosition(0).getName(), CoreMatchers.equalTo("extra"));
        thrown.expect(UnsupportedOperationException.class);
        command.undo();
    }

    @Test
    public void testRecord() {
        DeleteHabitsCommand.Record rec = command.toRecord();
        DeleteHabitsCommand other = rec.toCommand(habitList);
        MatcherAssert.assertThat(other.getId(), CoreMatchers.equalTo(command.getId()));
        MatcherAssert.assertThat(other.selected, CoreMatchers.equalTo(command.selected));
    }
}

