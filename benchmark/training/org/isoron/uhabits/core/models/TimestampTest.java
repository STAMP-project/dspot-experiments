/**
 * Copyright (C) 2015-2017 ?linson Santos Xavier <isoron@gmail.com>
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


import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;


public class TimestampTest extends BaseUnitTest {
    @Test
    public void testCompare() throws Exception {
        Timestamp t1 = DateUtils.getToday();
        Timestamp t2 = t1.minus(1);
        Timestamp t3 = t1.plus(3);
        MatcherAssert.assertThat(t1.compare(t2), greaterThan(0));
        MatcherAssert.assertThat(t1.compare(t1), equalTo(0));
        MatcherAssert.assertThat(t1.compare(t3), lessThan(0));
        Assert.assertTrue(t1.isNewerThan(t2));
        TestCase.assertFalse(t1.isNewerThan(t1));
        TestCase.assertFalse(t2.isNewerThan(t1));
        Assert.assertTrue(t2.isOlderThan(t1));
        TestCase.assertFalse(t1.isOlderThan(t2));
    }

    @Test
    public void testDaysUntil() throws Exception {
        Timestamp t = DateUtils.getToday();
        MatcherAssert.assertThat(t.daysUntil(t), equalTo(0));
        MatcherAssert.assertThat(t.daysUntil(t.plus(1)), equalTo(1));
        MatcherAssert.assertThat(t.daysUntil(t.plus(3)), equalTo(3));
        MatcherAssert.assertThat(t.daysUntil(t.plus(300)), equalTo(300));
        MatcherAssert.assertThat(t.daysUntil(t.minus(1)), equalTo((-1)));
        MatcherAssert.assertThat(t.daysUntil(t.minus(3)), equalTo((-3)));
        MatcherAssert.assertThat(t.daysUntil(t.minus(300)), equalTo((-300)));
    }
}

