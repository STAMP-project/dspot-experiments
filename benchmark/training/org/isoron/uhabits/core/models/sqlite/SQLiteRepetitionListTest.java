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


import java.util.List;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.models.Habit;
import org.isoron.uhabits.core.models.Timestamp;
import org.junit.Test;


public class SQLiteRepetitionListTest extends BaseUnitTest {
    private Habit habit;

    private Timestamp today;

    private RepetitionList repetitions;

    private long day;

    private Repository<RepetitionRecord> repository;

    @Test
    public void testAdd() {
        RepetitionRecord record = getByTimestamp(today.plus(1));
        TestCase.assertNull(record);
        Repetition rep = new Repetition(today.plus(1), CHECKED_EXPLICITLY);
        habit.getRepetitions().add(rep);
        record = getByTimestamp(today.plus(1));
        TestCase.assertNotNull(record);
        MatcherAssert.assertThat(record.value, IsEqual.equalTo(CHECKED_EXPLICITLY));
    }

    @Test
    public void testGetByInterval() {
        List<Repetition> reps = repetitions.getByInterval(today.minus(10), today);
        MatcherAssert.assertThat(reps.size(), IsEqual.equalTo(8));
        MatcherAssert.assertThat(reps.get(0).getTimestamp(), IsEqual.equalTo(today.minus(10)));
        MatcherAssert.assertThat(reps.get(4).getTimestamp(), IsEqual.equalTo(today.minus(5)));
        MatcherAssert.assertThat(reps.get(5).getTimestamp(), IsEqual.equalTo(today.minus(3)));
    }

    @Test
    public void testGetByTimestamp() {
        Repetition rep = repetitions.getByTimestamp(today);
        TestCase.assertNotNull(rep);
        MatcherAssert.assertThat(rep.getTimestamp(), IsEqual.equalTo(today));
        rep = repetitions.getByTimestamp(today.minus(2));
        TestCase.assertNull(rep);
    }

    @Test
    public void testGetOldest() {
        Repetition rep = repetitions.getOldest();
        TestCase.assertNotNull(rep);
        MatcherAssert.assertThat(rep.getTimestamp(), IsEqual.equalTo(today.minus(120)));
    }

    @Test
    public void testGetOldest_withEmptyHabit() {
        Habit empty = fixtures.createEmptyHabit();
        Repetition rep = empty.getRepetitions().getOldest();
        TestCase.assertNull(rep);
    }

    @Test
    public void testRemove() {
        RepetitionRecord record = getByTimestamp(today);
        TestCase.assertNotNull(record);
        Repetition rep = record.toRepetition();
        repetitions.remove(rep);
        record = getByTimestamp(today);
        TestCase.assertNull(record);
    }
}

