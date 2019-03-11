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


import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.models.Timestamp;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HintListTest extends BaseUnitTest {
    private HintList hintList;

    private String[] hints;

    @Mock
    private Preferences prefs;

    private Timestamp today;

    private Timestamp yesterday;

    @Test
    public void pop() throws Exception {
        Mockito.when(prefs.getLastHintNumber()).thenReturn((-1));
        MatcherAssert.assertThat(hintList.pop(), equalTo("hint1"));
        Mockito.verify(prefs).updateLastHint(0, today);
        Mockito.when(prefs.getLastHintNumber()).thenReturn(2);
        Assert.assertNull(hintList.pop());
    }

    @Test
    public void shouldShow() throws Exception {
        Mockito.when(prefs.getLastHintTimestamp()).thenReturn(today);
        TestCase.assertFalse(hintList.shouldShow());
        Mockito.when(prefs.getLastHintTimestamp()).thenReturn(yesterday);
        Assert.assertTrue(hintList.shouldShow());
    }
}

