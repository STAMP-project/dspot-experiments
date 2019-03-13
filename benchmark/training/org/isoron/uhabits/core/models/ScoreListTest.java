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


import DateUtils.TruncateField.MONTH;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Test;


public class ScoreListTest extends BaseUnitTest {
    private static final double E = 1.0E-6;

    private Habit habit;

    @Test
    public void test_getAll() {
        toggleRepetitions(0, 20);
        double[] expectedValues = new double[]{ 0.655747, 0.636894, 0.617008, 0.596033, 0.57391, 0.550574, 0.525961, 0.5, 0.472617, 0.443734, 0.41327, 0.381137, 0.347244, 0.311495, 0.273788, 0.234017, 0.192067, 0.14782, 0.101149, 0.051922 };
        int i = 0;
        for (Score s : habit.getScores())
            MatcherAssert.assertThat(s.getValue(), closeTo(expectedValues[(i++)], ScoreListTest.E));

    }

    @Test
    public void test_getTodayValue() {
        toggleRepetitions(0, 20);
        double actual = habit.getScores().getTodayValue();
        MatcherAssert.assertThat(actual, closeTo(0.655747, ScoreListTest.E));
    }

    @Test
    public void test_getValue() {
        toggleRepetitions(0, 20);
        double[] expectedValues = new double[]{ 0.655747, 0.636894, 0.617008, 0.596033, 0.57391, 0.550574, 0.525961, 0.5, 0.472617, 0.443734, 0.41327, 0.381137, 0.347244, 0.311495, 0.273788, 0.234017, 0.192067, 0.14782, 0.101149, 0.051922, 0.0, 0.0, 0.0 };
        ScoreList scores = habit.getScores();
        Timestamp current = DateUtils.getToday();
        for (double expectedValue : expectedValues) {
            MatcherAssert.assertThat(scores.getValue(current), closeTo(expectedValue, ScoreListTest.E));
            current = current.minus(1);
        }
    }

    @Test
    public void test_getValues() {
        toggleRepetitions(0, 20);
        Timestamp today = DateUtils.getToday();
        Timestamp from = today.minus(4);
        Timestamp to = today.minus(2);
        double[] expected = new double[]{ 0.617008, 0.596033, 0.573909 };
        double[] actual = habit.getScores().getValues(from, to);
        MatcherAssert.assertThat(actual.length, IsEqual.equalTo(expected.length));
        for (int i = 0; i < (actual.length); i++)
            MatcherAssert.assertThat(actual[i], closeTo(expected[i], ScoreListTest.E));

    }

    @Test
    public void test_groupBy() {
        Habit habit = fixtures.createLongHabit();
        List<Score> list = habit.getScores().groupBy(MONTH);
        MatcherAssert.assertThat(list.size(), IsEqual.equalTo(5));
        MatcherAssert.assertThat(list.get(0).getValue(), closeTo(0.653659, ScoreListTest.E));
        MatcherAssert.assertThat(list.get(1).getValue(), closeTo(0.622715, ScoreListTest.E));
        MatcherAssert.assertThat(list.get(2).getValue(), closeTo(0.520997, ScoreListTest.E));
    }

    @Test
    public void test_invalidateNewerThan() {
        MatcherAssert.assertThat(habit.getScores().getTodayValue(), closeTo(0.0, ScoreListTest.E));
        toggleRepetitions(0, 2);
        MatcherAssert.assertThat(habit.getScores().getTodayValue(), closeTo(0.101149, ScoreListTest.E));
        habit.setFrequency(new Frequency(1, 2));
        habit.getScores().invalidateNewerThan(new Timestamp(0));
        MatcherAssert.assertThat(habit.getScores().getTodayValue(), closeTo(0.051922, ScoreListTest.E));
    }

    @Test
    public void test_writeCSV() throws IOException {
        Habit habit = fixtures.createShortHabit();
        String expectedCSV = "2015-01-25,0.2654\n2015-01-24,0.2389\n" + ((("2015-01-23,0.2475\n2015-01-22,0.2203\n" + "2015-01-21,0.1921\n2015-01-20,0.1628\n") + "2015-01-19,0.1325\n2015-01-18,0.1011\n") + "2015-01-17,0.0686\n2015-01-16,0.0349\n");
        StringWriter writer = new StringWriter();
        habit.getScores().writeCSV(writer);
        MatcherAssert.assertThat(writer.toString(), IsEqual.equalTo(expectedCSV));
    }
}

