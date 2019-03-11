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
package org.isoron.uhabits.core.ui.screens.habits.list;


import ListHabitsSelectionMenuBehavior.Adapter;
import ListHabitsSelectionMenuBehavior.Screen;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.models.Habit;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ListHabitsSelectionMenuBehaviorTest extends BaseUnitTest {
    @Mock
    private Screen screen;

    @Mock
    private Adapter adapter;

    private ListHabitsSelectionMenuBehavior behavior;

    private Habit habit1;

    private Habit habit2;

    private Habit habit3;

    @Captor
    private ArgumentCaptor<OnColorPickedCallback> colorPickerCallback;

    @Captor
    private ArgumentCaptor<OnConfirmedCallback> deleteCallback;

    @Test
    public void canArchive() throws Exception {
        Mockito.when(adapter.getSelected()).thenReturn(Arrays.asList(habit1, habit2));
        TestCase.assertFalse(behavior.canArchive());
        Mockito.when(adapter.getSelected()).thenReturn(Arrays.asList(habit2, habit3));
        TestCase.assertTrue(behavior.canArchive());
    }

    @Test
    public void canEdit() throws Exception {
        Mockito.when(adapter.getSelected()).thenReturn(Collections.singletonList(habit1));
        TestCase.assertTrue(behavior.canEdit());
        Mockito.when(adapter.getSelected()).thenReturn(Arrays.asList(habit1, habit2));
        TestCase.assertFalse(behavior.canEdit());
    }

    @Test
    public void canUnarchive() throws Exception {
        Mockito.when(adapter.getSelected()).thenReturn(Arrays.asList(habit1, habit2));
        TestCase.assertFalse(behavior.canUnarchive());
        Mockito.when(adapter.getSelected()).thenReturn(Collections.singletonList(habit1));
        TestCase.assertTrue(behavior.canUnarchive());
    }

    @Test
    public void onArchiveHabits() throws Exception {
        TestCase.assertFalse(habit2.isArchived());
        Mockito.when(adapter.getSelected()).thenReturn(Collections.singletonList(habit2));
        behavior.onArchiveHabits();
        TestCase.assertTrue(habit2.isArchived());
    }

    @Test
    public void onChangeColor() throws Exception {
        MatcherAssert.assertThat(habit1.getColor(), equalTo(8));
        MatcherAssert.assertThat(habit2.getColor(), equalTo(8));
        Mockito.when(adapter.getSelected()).thenReturn(Arrays.asList(habit1, habit2));
        behavior.onChangeColor();
        Mockito.verify(screen).showColorPicker(ArgumentMatchers.eq(8), colorPickerCallback.capture());
        colorPickerCallback.getValue().onColorPicked(30);
        MatcherAssert.assertThat(habit1.getColor(), equalTo(30));
    }

    @Test
    public void onDeleteHabits() throws Exception {
        Long id = habit1.getId();
        TestCase.assertNotNull(id);
        TestCase.assertNotNull(habitList.getById(id));
        Mockito.when(adapter.getSelected()).thenReturn(Collections.singletonList(habit1));
        behavior.onDeleteHabits();
        Mockito.verify(screen).showDeleteConfirmationScreen(deleteCallback.capture());
        deleteCallback.getValue().onConfirmed();
        TestCase.assertNull(habitList.getById(id));
    }

    @Test
    public void onEditHabits() throws Exception {
        List<Habit> selected = Arrays.asList(habit1, habit2);
        Mockito.when(adapter.getSelected()).thenReturn(selected);
        behavior.onEditHabits();
        Mockito.verify(screen).showEditHabitsScreen(selected);
    }

    @Test
    public void onUnarchiveHabits() throws Exception {
        TestCase.assertTrue(habit1.isArchived());
        Mockito.when(adapter.getSelected()).thenReturn(Collections.singletonList(habit1));
        behavior.onUnarchiveHabits();
        TestCase.assertFalse(habit1.isArchived());
    }
}

