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


import ModelObservable.Listener;
import Timestamp.ZERO;
import java.util.List;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Test;
import org.mockito.Mockito;


public class StreakListTest extends BaseUnitTest {
    private Habit habit;

    private StreakList streaks;

    private long day;

    private Timestamp today;

    private Listener listener;

    @Test
    public void testFindBeginning_withEmptyHistory() {
        Habit habit2 = fixtures.createEmptyHabit();
        Timestamp beginning = habit2.getStreaks().findBeginning();
        TestCase.assertNull(beginning);
    }

    @Test
    public void testFindBeginning_withLongHistory() {
        streaks.rebuild();
        streaks.invalidateNewerThan(new Timestamp(0));
        MatcherAssert.assertThat(streaks.findBeginning(), IsEqual.equalTo(today.minus(120)));
    }

    @Test
    public void testGetAll() throws Exception {
        List<Streak> all = streaks.getAll();
        MatcherAssert.assertThat(all.size(), IsEqual.equalTo(22));
        MatcherAssert.assertThat(all.get(3).getEnd(), IsEqual.equalTo(today.minus(7)));
        MatcherAssert.assertThat(all.get(3).getStart(), IsEqual.equalTo(today.minus(10)));
        MatcherAssert.assertThat(all.get(17).getEnd(), IsEqual.equalTo(today.minus(89)));
        MatcherAssert.assertThat(all.get(17).getStart(), IsEqual.equalTo(today.minus(91)));
    }

    @Test
    public void testGetBest() throws Exception {
        List<Streak> best = streaks.getBest(4);
        MatcherAssert.assertThat(best.size(), IsEqual.equalTo(4));
        MatcherAssert.assertThat(best.get(0).getLength(), IsEqual.equalTo(4));
        MatcherAssert.assertThat(best.get(1).getLength(), IsEqual.equalTo(3));
        MatcherAssert.assertThat(best.get(2).getLength(), IsEqual.equalTo(5));
        MatcherAssert.assertThat(best.get(3).getLength(), IsEqual.equalTo(6));
        best = streaks.getBest(2);
        MatcherAssert.assertThat(best.size(), IsEqual.equalTo(2));
        MatcherAssert.assertThat(best.get(0).getLength(), IsEqual.equalTo(5));
        MatcherAssert.assertThat(best.get(1).getLength(), IsEqual.equalTo(6));
    }

    @Test
    public void testInvalidateNewer() {
        Streak s = streaks.getNewestComputed();
        MatcherAssert.assertThat(s.getEnd(), IsEqual.equalTo(today));
        streaks.invalidateNewerThan(today.minus(8));
        Mockito.verify(listener).onModelChange();
        s = streaks.getNewestComputed();
        TestCase.assertNull(s);
    }

    @Test
    public void testToString() throws Exception {
        Timestamp time = ZERO.plus(100);
        Streak streak = new Streak(time, time.plus(10));
        MatcherAssert.assertThat(streak.toString(), IsEqual.equalTo("{start: 1970-04-11, end: 1970-04-21}"));
    }
}

