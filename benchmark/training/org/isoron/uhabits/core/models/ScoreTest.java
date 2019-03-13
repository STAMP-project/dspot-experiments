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


import Timestamp.ZERO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Test;


public class ScoreTest extends BaseUnitTest {
    private static final double E = 1.0E-6;

    @Test
    public void test_compute_withDailyHabit() {
        int check = 1;
        double freq = 1.0;
        MatcherAssert.assertThat(compute(freq, 0, check), closeTo(0.051922, ScoreTest.E));
        MatcherAssert.assertThat(compute(freq, 0.5, check), closeTo(0.525961, ScoreTest.E));
        MatcherAssert.assertThat(compute(freq, 0.75, check), closeTo(0.762981, ScoreTest.E));
        check = 0;
        MatcherAssert.assertThat(compute(freq, 0, check), closeTo(0, ScoreTest.E));
        MatcherAssert.assertThat(compute(freq, 0.5, check), closeTo(0.474039, ScoreTest.E));
        MatcherAssert.assertThat(compute(freq, 0.75, check), closeTo(0.711058, ScoreTest.E));
    }

    @Test
    public void test_compute_withNonDailyHabit() {
        int check = 1;
        double freq = 1 / 3.0;
        MatcherAssert.assertThat(compute(freq, 0, check), closeTo(0.017616, ScoreTest.E));
        MatcherAssert.assertThat(compute(freq, 0.5, check), closeTo(0.508808, ScoreTest.E));
        MatcherAssert.assertThat(compute(freq, 0.75, check), closeTo(0.754404, ScoreTest.E));
        check = 0;
        MatcherAssert.assertThat(compute(freq, 0, check), closeTo(0.0, ScoreTest.E));
        MatcherAssert.assertThat(compute(freq, 0.5, check), closeTo(0.491192, ScoreTest.E));
        MatcherAssert.assertThat(compute(freq, 0.75, check), closeTo(0.736788, ScoreTest.E));
    }

    @Test
    public void testToString() throws Exception {
        Score.Score score = new Score.Score(ZERO.plus(100), 150.0);
        MatcherAssert.assertThat(score.toString(), IsEqual.equalTo("{timestamp: 1970-04-11, value: 150.0}"));
    }
}

