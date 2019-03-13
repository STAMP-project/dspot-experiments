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
package org.isoron.uhabits.core.models.sqlite;


import ModelObservable.Listener;
import java.util.ArrayList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.models.HabitList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class SQLiteHabitListTest extends BaseUnitTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private SQLiteHabitList habitList;

    private Repository<HabitRecord> repository;

    private Listener listener;

    private ArrayList<Habit> habitsArray;

    private HabitList activeHabits;

    private HabitList reminderHabits;

    @Test
    public void testAdd_withDuplicate() {
        Habit habit = modelFactory.buildHabit();
        habitList.add(habit);
        Mockito.verify(listener).onModelChange();
        exception.expect(IllegalArgumentException.class);
        habitList.add(habit);
    }

    @Test
    public void testAdd_withId() {
        Habit habit = modelFactory.buildHabit();
        habit.setName("Hello world with id");
        habit.setId(12300L);
        habitList.add(habit);
        MatcherAssert.assertThat(habit.getId(), CoreMatchers.equalTo(12300L));
        HabitRecord record = repository.find(12300L);
        Assert.assertNotNull(record);
        MatcherAssert.assertThat(record.name, CoreMatchers.equalTo(habit.getName()));
    }

    @Test
    public void testAdd_withoutId() {
        Habit habit = modelFactory.buildHabit();
        habit.setName("Hello world");
        Assert.assertNull(habit.getId());
        habitList.add(habit);
        Assert.assertNotNull(habit.getId());
        HabitRecord record = repository.find(habit.getId());
        Assert.assertNotNull(record);
        MatcherAssert.assertThat(record.name, CoreMatchers.equalTo(habit.getName()));
    }

    @Test
    public void testSize() {
        MatcherAssert.assertThat(habitList.size(), CoreMatchers.equalTo(10));
    }

    @Test
    public void testGetById() {
        Habit h1 = habitList.getById(0);
        Assert.assertNotNull(h1);
        MatcherAssert.assertThat(h1.getName(), CoreMatchers.equalTo("habit 0"));
        Habit h2 = habitList.getById(0);
        Assert.assertNotNull(h2);
        MatcherAssert.assertThat(h1, CoreMatchers.equalTo(h2));
    }

    @Test
    public void testGetById_withInvalid() {
        long invalidId = 9183792001L;
        Habit h1 = habitList.getById(invalidId);
        Assert.assertNull(h1);
    }

    @Test
    public void testGetByPosition() {
        Habit h = habitList.getByPosition(5);
        Assert.assertNotNull(h);
        MatcherAssert.assertThat(h.getName(), CoreMatchers.equalTo("habit 5"));
    }

    @Test
    public void testIndexOf() {
        Habit h1 = habitList.getByPosition(5);
        Assert.assertNotNull(h1);
        MatcherAssert.assertThat(habitList.indexOf(h1), CoreMatchers.equalTo(5));
        Habit h2 = modelFactory.buildHabit();
        MatcherAssert.assertThat(habitList.indexOf(h2), CoreMatchers.equalTo((-1)));
        h2.setId(1000L);
        MatcherAssert.assertThat(habitList.indexOf(h2), CoreMatchers.equalTo((-1)));
    }

    @Test
    public void testRemove() throws Exception {
        Habit h = habitList.getByPosition(2);
        habitList.remove(h);
        MatcherAssert.assertThat(habitList.indexOf(h), CoreMatchers.equalTo((-1)));
        HabitRecord rec = repository.find(2L);
        Assert.assertNull(rec);
        rec = repository.find(3L);
        Assert.assertNotNull(rec);
        MatcherAssert.assertThat(rec.position, CoreMatchers.equalTo(2));
    }

    @Test
    public void testReorder() {
        Habit habit3 = habitList.getById(3);
        Habit habit4 = habitList.getById(4);
        Assert.assertNotNull(habit3);
        Assert.assertNotNull(habit4);
        habitList.reorder(habit4, habit3);
        HabitRecord record3 = repository.find(3L);
        Assert.assertNotNull(record3);
        MatcherAssert.assertThat(record3.position, CoreMatchers.equalTo(4));
        HabitRecord record4 = repository.find(4L);
        Assert.assertNotNull(record4);
        MatcherAssert.assertThat(record4.position, CoreMatchers.equalTo(3));
    }
}

