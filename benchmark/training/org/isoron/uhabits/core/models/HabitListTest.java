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
package org.isoron.uhabits.core.models;


import Frequency.DAILY;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.HabitList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@SuppressWarnings("JavaDoc")
public class HabitListTest extends BaseUnitTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ArrayList<Habit> habitsArray;

    private HabitList activeHabits;

    private HabitList reminderHabits;

    @Test
    public void testSize() {
        MatcherAssert.assertThat(habitList.size(), CoreMatchers.equalTo(10));
        MatcherAssert.assertThat(activeHabits.size(), CoreMatchers.equalTo(6));
        MatcherAssert.assertThat(reminderHabits.size(), CoreMatchers.equalTo(4));
    }

    @Test
    public void testGetByPosition() {
        MatcherAssert.assertThat(habitList.getByPosition(0), CoreMatchers.equalTo(habitsArray.get(0)));
        MatcherAssert.assertThat(habitList.getByPosition(3), CoreMatchers.equalTo(habitsArray.get(3)));
        MatcherAssert.assertThat(habitList.getByPosition(9), CoreMatchers.equalTo(habitsArray.get(9)));
        MatcherAssert.assertThat(activeHabits.getByPosition(0), CoreMatchers.equalTo(habitsArray.get(2)));
        MatcherAssert.assertThat(reminderHabits.getByPosition(1), CoreMatchers.equalTo(habitsArray.get(3)));
    }

    @Test
    public void testGetById() {
        Habit habit1 = habitsArray.get(0);
        Habit habit2 = habitList.getById(habit1.getId());
        MatcherAssert.assertThat(habit1, CoreMatchers.equalTo(habit2));
    }

    @Test
    public void testGetById_withInvalidId() {
        Assert.assertNull(habitList.getById(100L));
    }

    @Test
    public void testOrdering() {
        HabitList list = modelFactory.buildHabitList();
        Habit h1 = fixtures.createEmptyHabit();
        h1.setName("A Habit");
        h1.setColor(2);
        h1.setPosition(1);
        Habit h2 = fixtures.createEmptyHabit();
        h2.setName("B Habit");
        h2.setColor(2);
        h2.setPosition(3);
        Habit h3 = fixtures.createEmptyHabit();
        h3.setName("C Habit");
        h3.setColor(0);
        h3.setPosition(0);
        Habit h4 = fixtures.createEmptyHabit();
        h4.setName("D Habit");
        h4.setColor(1);
        h4.setPosition(2);
        list.add(h3);
        list.add(h1);
        list.add(h4);
        list.add(h2);
        list.setOrder(BY_POSITION);
        MatcherAssert.assertThat(list.getByPosition(0), CoreMatchers.equalTo(h3));
        MatcherAssert.assertThat(list.getByPosition(1), CoreMatchers.equalTo(h1));
        MatcherAssert.assertThat(list.getByPosition(2), CoreMatchers.equalTo(h4));
        MatcherAssert.assertThat(list.getByPosition(3), CoreMatchers.equalTo(h2));
        list.setOrder(BY_NAME);
        MatcherAssert.assertThat(list.getByPosition(0), CoreMatchers.equalTo(h1));
        MatcherAssert.assertThat(list.getByPosition(1), CoreMatchers.equalTo(h2));
        MatcherAssert.assertThat(list.getByPosition(2), CoreMatchers.equalTo(h3));
        MatcherAssert.assertThat(list.getByPosition(3), CoreMatchers.equalTo(h4));
        list.remove(h1);
        list.add(h1);
        MatcherAssert.assertThat(list.getByPosition(0), CoreMatchers.equalTo(h1));
        list.setOrder(BY_COLOR);
        MatcherAssert.assertThat(list.getByPosition(0), CoreMatchers.equalTo(h3));
        MatcherAssert.assertThat(list.getByPosition(1), CoreMatchers.equalTo(h4));
        MatcherAssert.assertThat(list.getByPosition(2), CoreMatchers.equalTo(h1));
        MatcherAssert.assertThat(list.getByPosition(3), CoreMatchers.equalTo(h2));
        list.setOrder(BY_POSITION);
        MatcherAssert.assertThat(list.getByPosition(0), CoreMatchers.equalTo(h3));
        MatcherAssert.assertThat(list.getByPosition(1), CoreMatchers.equalTo(h1));
        MatcherAssert.assertThat(list.getByPosition(2), CoreMatchers.equalTo(h4));
        MatcherAssert.assertThat(list.getByPosition(3), CoreMatchers.equalTo(h2));
    }

    @Test
    public void testReorder() {
        int[][] operations = new int[][]{ new int[]{ 5, 2 }, new int[]{ 3, 7 }, new int[]{ 4, 4 }, new int[]{ 8, 3 } };
        int[][] expectedSequence = new int[][]{ new int[]{ 0, 1, 5, 2, 3, 4, 6, 7, 8, 9 }, new int[]{ 0, 1, 5, 2, 4, 6, 7, 3, 8, 9 }, new int[]{ 0, 1, 5, 2, 4, 6, 7, 3, 8, 9 }, new int[]{ 0, 1, 5, 2, 4, 6, 7, 8, 3, 9 } };
        for (int i = 0; i < (operations.length); i++) {
            Habit fromHabit = habitsArray.get(operations[i][0]);
            Habit toHabit = habitsArray.get(operations[i][1]);
            habitList.reorder(fromHabit, toHabit);
            int[] actualSequence = new int[10];
            for (int j = 0; j < 10; j++) {
                Habit h = habitList.getByPosition(j);
                MatcherAssert.assertThat(h.getPosition(), CoreMatchers.equalTo(j));
                actualSequence[j] = Math.toIntExact(h.getId());
            }
            MatcherAssert.assertThat(actualSequence, CoreMatchers.equalTo(expectedSequence[i]));
        }
        MatcherAssert.assertThat(activeHabits.indexOf(habitsArray.get(5)), CoreMatchers.equalTo(0));
        MatcherAssert.assertThat(activeHabits.indexOf(habitsArray.get(2)), CoreMatchers.equalTo(1));
    }

    @Test
    public void testReorder_withInvalidArguments() throws Exception {
        Habit h1 = habitsArray.get(0);
        Habit h2 = fixtures.createEmptyHabit();
        thrown.expect(IllegalArgumentException.class);
        habitList.reorder(h1, h2);
    }

    @Test
    public void testWriteCSV() throws IOException {
        HabitList list = modelFactory.buildHabitList();
        Habit h1 = fixtures.createEmptyHabit();
        h1.setName("Meditate");
        h1.setDescription("Did you meditate this morning?");
        h1.setFrequency(DAILY);
        h1.setColor(3);
        Habit h2 = fixtures.createEmptyHabit();
        h2.setName("Wake up early");
        h2.setDescription("Did you wake up before 6am?");
        h2.setFrequency(new Frequency(2, 3));
        h2.setColor(5);
        list.add(h1);
        list.add(h2);
        String expectedCSV = "Position,Name,Description,NumRepetitions,Interval,Color\n" + ("001,Meditate,Did you meditate this morning?,1,1,#FF8F00\n" + "002,Wake up early,Did you wake up before 6am?,2,3,#AFB42B\n");
        StringWriter writer = new StringWriter();
        list.writeCSV(writer);
        MatcherAssert.assertThat(writer.toString(), CoreMatchers.equalTo(expectedCSV));
    }

    @Test
    public void testAdd() throws Exception {
        Habit h1 = fixtures.createEmptyHabit();
        TestCase.assertFalse(h1.isArchived());
        Assert.assertNull(h1.getId());
        MatcherAssert.assertThat(habitList.indexOf(h1), CoreMatchers.equalTo((-1)));
        habitList.add(h1);
        Assert.assertNotNull(h1.getId());
        MatcherAssert.assertThat(habitList.indexOf(h1), CoreMatchers.not(CoreMatchers.equalTo((-1))));
        MatcherAssert.assertThat(activeHabits.indexOf(h1), CoreMatchers.not(CoreMatchers.equalTo((-1))));
    }

    @Test
    public void testAdd_withFilteredList() throws Exception {
        thrown.expect(IllegalStateException.class);
        activeHabits.add(fixtures.createEmptyHabit());
    }

    @Test
    public void testRemove_onFilteredList() throws Exception {
        thrown.expect(IllegalStateException.class);
        activeHabits.remove(fixtures.createEmptyHabit());
    }

    @Test
    public void testReorder_onFilteredList() throws Exception {
        Habit h1 = fixtures.createEmptyHabit();
        Habit h2 = fixtures.createEmptyHabit();
        thrown.expect(IllegalStateException.class);
        activeHabits.reorder(h1, h2);
    }

    @Test
    public void testReorder_onSortedList() throws Exception {
        habitList.setOrder(BY_SCORE);
        Habit h1 = habitsArray.get(1);
        Habit h2 = habitsArray.get(2);
        thrown.expect(IllegalStateException.class);
        habitList.reorder(h1, h2);
    }
}

