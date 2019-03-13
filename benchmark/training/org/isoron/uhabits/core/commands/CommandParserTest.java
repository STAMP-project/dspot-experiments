/**
 * Copyright (C) 2016 ?linson Santos Xavier <isoron@gmail.com>
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


import Timestamp.ZERO;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.NonNull;
import org.isoron.uhabits.core.models.Habit;
import org.junit.Test;


public class CommandParserTest extends BaseUnitTest {
    @NonNull
    private CommandParser parser;

    private Habit habit;

    private List<Habit> selected;

    @Test
    public void testDecodeArchiveCommand() throws JSONException {
        ArchiveHabitsCommand original;
        ArchiveHabitsCommand decoded;
        original = new ArchiveHabitsCommand(habitList, selected);
        decoded = ((ArchiveHabitsCommand) (parser.parse(original.toJson())));
        MatcherAssert.assertThat(decoded.getId(), CoreMatchers.equalTo(original.getId()));
        MatcherAssert.assertThat(decoded.selected, CoreMatchers.equalTo(original.selected));
    }

    @Test
    public void testDecodeChangeColorCommand() throws JSONException {
        ChangeHabitColorCommand original;
        ChangeHabitColorCommand decoded;
        original = new ChangeHabitColorCommand(habitList, selected, 20);
        decoded = ((ChangeHabitColorCommand) (parser.parse(original.toJson())));
        MatcherAssert.assertThat(decoded.getId(), CoreMatchers.equalTo(original.getId()));
        MatcherAssert.assertThat(decoded.newColor, CoreMatchers.equalTo(original.newColor));
        MatcherAssert.assertThat(decoded.selected, CoreMatchers.equalTo(original.selected));
    }

    @Test
    public void testDecodeCreateHabitCommand() throws JSONException {
        Habit model = modelFactory.buildHabit();
        model.setName("JSON");
        CreateHabitCommand original;
        CreateHabitCommand decoded;
        original = new CreateHabitCommand(modelFactory, habitList, model);
        original.execute();
        decoded = ((CreateHabitCommand) (parser.parse(original.toJson())));
        MatcherAssert.assertThat(decoded.getId(), CoreMatchers.equalTo(original.getId()));
        MatcherAssert.assertThat(decoded.savedId, CoreMatchers.equalTo(original.savedId));
        MatcherAssert.assertThat(decoded.model.getData(), CoreMatchers.equalTo(model.getData()));
    }

    @Test
    public void testDecodeCreateRepCommand() throws JSONException {
        CreateRepetitionCommand original;
        CreateRepetitionCommand decoded;
        original = new CreateRepetitionCommand(habit, ZERO.plus(100), 5);
        decoded = ((CreateRepetitionCommand) (parser.parse(original.toJson())));
        MatcherAssert.assertThat(decoded.getId(), CoreMatchers.equalTo(original.getId()));
        MatcherAssert.assertThat(decoded.timestamp, CoreMatchers.equalTo(original.timestamp));
        MatcherAssert.assertThat(decoded.value, CoreMatchers.equalTo(original.value));
        MatcherAssert.assertThat(decoded.habit, CoreMatchers.equalTo(original.habit));
    }

    @Test
    public void testDecodeDeleteCommand() throws JSONException {
        DeleteHabitsCommand original;
        DeleteHabitsCommand decoded;
        original = new DeleteHabitsCommand(habitList, selected);
        decoded = ((DeleteHabitsCommand) (parser.parse(original.toJson())));
        MatcherAssert.assertThat(decoded.getId(), CoreMatchers.equalTo(original.getId()));
        MatcherAssert.assertThat(decoded.selected, CoreMatchers.equalTo(original.selected));
    }

    @Test
    public void testDecodeEditHabitCommand() throws JSONException {
        Habit modified = modelFactory.buildHabit();
        modified.setName("Edited JSON");
        modified.setColor(2);
        EditHabitCommand original;
        EditHabitCommand decoded;
        original = new EditHabitCommand(modelFactory, habitList, habit, modified);
        original.execute();
        decoded = ((EditHabitCommand) (parser.parse(original.toJson())));
        MatcherAssert.assertThat(decoded.getId(), CoreMatchers.equalTo(original.getId()));
        MatcherAssert.assertThat(decoded.savedId, CoreMatchers.equalTo(original.savedId));
        MatcherAssert.assertThat(decoded.modified.getData(), CoreMatchers.equalTo(modified.getData()));
    }

    @Test
    public void testDecodeToggleCommand() throws JSONException {
        ToggleRepetitionCommand original;
        ToggleRepetitionCommand decoded;
        original = new ToggleRepetitionCommand(habitList, habit, ZERO.plus(100));
        decoded = ((ToggleRepetitionCommand) (parser.parse(original.toJson())));
        MatcherAssert.assertThat(decoded.getId(), CoreMatchers.equalTo(original.getId()));
        MatcherAssert.assertThat(decoded.timestamp, CoreMatchers.equalTo(original.timestamp));
        MatcherAssert.assertThat(decoded.habit, CoreMatchers.equalTo(original.habit));
    }

    @Test
    public void testDecodeUnarchiveCommand() throws JSONException {
        UnarchiveHabitsCommand original;
        UnarchiveHabitsCommand decoded;
        original = new UnarchiveHabitsCommand(habitList, selected);
        decoded = ((UnarchiveHabitsCommand) (parser.parse(original.toJson())));
        MatcherAssert.assertThat(decoded.getId(), CoreMatchers.equalTo(original.getId()));
        MatcherAssert.assertThat(decoded.selected, CoreMatchers.equalTo(original.selected));
    }
}

