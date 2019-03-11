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


import EditHabitCommand.Record;
import Frequency.TWO_TIMES_PER_WEEK;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.models.Habit;
import org.junit.Test;


public class EditHabitCommandTest extends BaseUnitTest {
    private EditHabitCommand command;

    private Habit habit;

    private Habit modified;

    @Test
    public void testExecuteUndoRedo() {
        command = new EditHabitCommand(modelFactory, habitList, habit, modified);
        double originalScore = habit.getScores().getTodayValue();
        MatcherAssert.assertThat(habit.getName(), equalTo("original"));
        command.execute();
        MatcherAssert.assertThat(habit.getName(), equalTo("modified"));
        MatcherAssert.assertThat(habit.getScores().getTodayValue(), equalTo(originalScore));
        command.undo();
        MatcherAssert.assertThat(habit.getName(), equalTo("original"));
        MatcherAssert.assertThat(habit.getScores().getTodayValue(), equalTo(originalScore));
        command.execute();
        MatcherAssert.assertThat(habit.getName(), equalTo("modified"));
        MatcherAssert.assertThat(habit.getScores().getTodayValue(), equalTo(originalScore));
    }

    @Test
    public void testExecuteUndoRedo_withModifiedInterval() {
        modified.setFrequency(TWO_TIMES_PER_WEEK);
        command = new EditHabitCommand(modelFactory, habitList, habit, modified);
        double originalScore = habit.getScores().getTodayValue();
        MatcherAssert.assertThat(habit.getName(), equalTo("original"));
        command.execute();
        MatcherAssert.assertThat(habit.getName(), equalTo("modified"));
        MatcherAssert.assertThat(habit.getScores().getTodayValue(), lessThan(originalScore));
        command.undo();
        MatcherAssert.assertThat(habit.getName(), equalTo("original"));
        MatcherAssert.assertThat(habit.getScores().getTodayValue(), equalTo(originalScore));
        command.execute();
        MatcherAssert.assertThat(habit.getName(), equalTo("modified"));
        MatcherAssert.assertThat(habit.getScores().getTodayValue(), lessThan(originalScore));
    }

    @Test
    public void testRecord() {
        command = new EditHabitCommand(modelFactory, habitList, habit, modified);
        EditHabitCommand.Record rec = command.toRecord();
        EditHabitCommand other = rec.toCommand(modelFactory, habitList);
        MatcherAssert.assertThat(other.getId(), equalTo(command.getId()));
        MatcherAssert.assertThat(other.savedId, equalTo(command.savedId));
        MatcherAssert.assertThat(other.original.getData(), equalTo(command.original.getData()));
        MatcherAssert.assertThat(other.modified.getData(), equalTo(command.modified.getData()));
    }
}

