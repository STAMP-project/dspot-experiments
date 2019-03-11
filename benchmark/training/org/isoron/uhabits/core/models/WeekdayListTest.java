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


import WeekdayList.EVERY_DAY;
import junit.framework.Assert;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Test;


public class WeekdayListTest extends BaseUnitTest {
    @Test
    public void test() {
        int daysInt = 124;
        boolean[] daysArray = new boolean[]{ false, false, true, true, true, true, true };
        WeekdayList list = new WeekdayList(daysArray);
        MatcherAssert.assertThat(list.toArray(), IsEqual.equalTo(daysArray));
        MatcherAssert.assertThat(list.toInteger(), IsEqual.equalTo(daysInt));
        list = new WeekdayList(daysInt);
        MatcherAssert.assertThat(list.toArray(), IsEqual.equalTo(daysArray));
        MatcherAssert.assertThat(list.toInteger(), IsEqual.equalTo(daysInt));
    }

    @Test
    public void testEmpty() {
        WeekdayList list = new WeekdayList(0);
        Assert.assertTrue(list.isEmpty());
        Assert.assertFalse(EVERY_DAY.isEmpty());
    }
}

