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


import HabitCardListCache.Listener;
import java.util.Collections;
import junit.framework.Assert;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Test;
import org.mockito.Mockito;


public class HabitCardListCacheTest extends BaseUnitTest {
    private HabitCardListCache cache;

    private Listener listener;

    @Test
    public void testCommandListener_all() {
        MatcherAssert.assertThat(cache.getHabitCount(), IsEqual.equalTo(10));
        Habit h = habitList.getByPosition(0);
        commandRunner.execute(new DeleteHabitsCommand(habitList, Collections.singletonList(h)), null);
        Mockito.verify(listener).onItemRemoved(0);
        Mockito.verify(listener).onRefreshFinished();
        MatcherAssert.assertThat(cache.getHabitCount(), IsEqual.equalTo(9));
    }

    @Test
    public void testCommandListener_single() {
        Habit h2 = habitList.getByPosition(2);
        Timestamp today = DateUtils.getToday();
        commandRunner.execute(new ToggleRepetitionCommand(habitList, h2, today), h2.getId());
        Mockito.verify(listener).onItemChanged(2);
        Mockito.verify(listener).onRefreshFinished();
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testGet() {
        MatcherAssert.assertThat(cache.getHabitCount(), IsEqual.equalTo(10));
        Habit h = habitList.getByPosition(3);
        Assert.assertNotNull(h.getId());
        double score = h.getScores().getTodayValue();
        MatcherAssert.assertThat(cache.getHabitByPosition(3), IsEqual.equalTo(h));
        MatcherAssert.assertThat(cache.getScore(h.getId()), IsEqual.equalTo(score));
        Timestamp today = DateUtils.getToday();
        int[] actualCheckmarks = cache.getCheckmarks(h.getId());
        int[] expectedCheckmarks = h.getCheckmarks().getValues(today.minus(9), today);
        MatcherAssert.assertThat(actualCheckmarks, IsEqual.equalTo(expectedCheckmarks));
    }

    @Test
    public void testRemoval() {
        removeHabitAt(0);
        removeHabitAt(3);
        cache.refreshAllHabits();
        Mockito.verify(listener).onItemRemoved(0);
        Mockito.verify(listener).onItemRemoved(3);
        Mockito.verify(listener).onRefreshFinished();
        MatcherAssert.assertThat(cache.getHabitCount(), IsEqual.equalTo(8));
    }

    @Test
    public void testRefreshWithNoChanges() {
        cache.refreshAllHabits();
        Mockito.verify(listener).onRefreshFinished();
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testReorder_onCache() {
        Habit h2 = cache.getHabitByPosition(2);
        Habit h3 = cache.getHabitByPosition(3);
        Habit h7 = cache.getHabitByPosition(7);
        cache.reorder(2, 7);
        MatcherAssert.assertThat(cache.getHabitByPosition(2), IsEqual.equalTo(h3));
        MatcherAssert.assertThat(cache.getHabitByPosition(7), IsEqual.equalTo(h2));
        MatcherAssert.assertThat(cache.getHabitByPosition(6), IsEqual.equalTo(h7));
        Mockito.verify(listener).onItemMoved(2, 7);
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testReorder_onList() {
        Habit h2 = habitList.getByPosition(2);
        Habit h3 = habitList.getByPosition(3);
        Habit h7 = habitList.getByPosition(7);
        MatcherAssert.assertThat(cache.getHabitByPosition(2), IsEqual.equalTo(h2));
        MatcherAssert.assertThat(cache.getHabitByPosition(7), IsEqual.equalTo(h7));
        Mockito.reset(listener);
        habitList.reorder(h2, h7);
        cache.refreshAllHabits();
        MatcherAssert.assertThat(cache.getHabitByPosition(2), IsEqual.equalTo(h3));
        MatcherAssert.assertThat(cache.getHabitByPosition(7), IsEqual.equalTo(h2));
        MatcherAssert.assertThat(cache.getHabitByPosition(6), IsEqual.equalTo(h7));
        Mockito.verify(listener).onItemMoved(3, 2);
        Mockito.verify(listener).onItemMoved(4, 3);
        Mockito.verify(listener).onItemMoved(5, 4);
        Mockito.verify(listener).onItemMoved(6, 5);
        Mockito.verify(listener).onItemMoved(7, 6);
        Mockito.verify(listener).onRefreshFinished();
        Mockito.verifyNoMoreInteractions(listener);
    }
}

