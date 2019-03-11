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


import ChangeHabitColorCommand.Record;
import java.util.LinkedList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Test;


public class ChangeHabitColorCommandTest extends BaseUnitTest {
    private ChangeHabitColorCommand command;

    private LinkedList<Habit> selected;

    @Test
    public void testExecuteUndoRedo() {
        checkOriginalColors();
        command.execute();
        checkNewColors();
        command.undo();
        checkOriginalColors();
        command.execute();
        checkNewColors();
    }

    @Test
    public void testRecord() {
        ChangeHabitColorCommand.Record rec = command.toRecord();
        ChangeHabitColorCommand other = rec.toCommand(habitList);
        MatcherAssert.assertThat(other.getId(), CoreMatchers.equalTo(command.getId()));
        MatcherAssert.assertThat(other.newColor, CoreMatchers.equalTo(command.newColor));
        MatcherAssert.assertThat(other.selected, CoreMatchers.equalTo(command.selected));
    }
}

