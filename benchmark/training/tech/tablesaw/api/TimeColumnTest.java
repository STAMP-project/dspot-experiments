/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.tablesaw.api;


import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.times.PackedLocalTime;
import tech.tablesaw.columns.times.TimeColumnType;
import tech.tablesaw.selection.Selection;


public class TimeColumnTest {
    private TimeColumn column1;

    @Test
    public void testMaxAndMin() {
        column1.appendCell("05:15:30");
        column1.appendCell("10:15:30");
        column1.appendCell("07:04:02");
        Assertions.assertEquals(LocalTime.of(5, 15, 30), column1.min());
        Assertions.assertEquals(LocalTime.of(10, 15, 30), column1.max());
    }

    @Test
    public void testContains() {
        column1.appendCell("05:15:30");
        column1.appendCell("10:15:30");
        column1.appendCell("07:04:02");
        Assertions.assertTrue(column1.contains(LocalTime.of(5, 15, 30)));
        Assertions.assertTrue(column1.contains(LocalTime.of(10, 15, 30)));
        Assertions.assertFalse(column1.contains(LocalTime.of(9, 15, 30)));
    }

    @Test
    public void testTopAndBottom() {
        fillLargerColumn();
        List<LocalTime> top = column1.top(3);
        List<LocalTime> bottom = column1.bottom(3);
        Assertions.assertTrue(bottom.contains(LocalTime.of(0, 4, 2)));
        Assertions.assertTrue(bottom.contains(LocalTime.of(3, 6, 2)));
        Assertions.assertTrue(bottom.contains(LocalTime.of(4, 4, 2)));
        Assertions.assertEquals(3, bottom.size());
        Assertions.assertTrue(top.contains(LocalTime.of(18, 4, 2)));
        Assertions.assertTrue(top.contains(LocalTime.of(14, 4, 2)));
        Assertions.assertTrue(top.contains(LocalTime.of(15, 4, 2)));
        Assertions.assertEquals(3, top.size());
    }

    @Test
    public void testSorting() {
        fillLargerColumn();
        List<LocalTime> top = column1.top(3);
        column1.sortAscending();
        Column<?> first = column1.first(3);
        TimeColumn timeColumn = ((TimeColumn) (first));
        List<LocalTime> sortedA = timeColumn.asList();
        column1.sortDescending();
        List<LocalTime> sortedD = column1.first(3).asList();
        Assertions.assertNull(sortedA.get(0));
        Assertions.assertEquals(LocalTime.of(0, 4, 2), sortedA.get(1));
        Assertions.assertEquals(LocalTime.of(3, 6, 2), sortedA.get(2));
        Assertions.assertEquals(top, sortedD);
    }

    @Test
    public void testAppendColumn() {
        column1.appendInternal(PackedLocalTime.of(5, 15, 30));
        column1.appendInternal(PackedLocalTime.of(10, 15, 30));
        column1.appendInternal(PackedLocalTime.of(7, 4, 2));
        column1.appendInternal(PackedLocalTime.of(4, 4, 2));
        column1.appendInternal(PackedLocalTime.of(18, 4, 2));
        TimeColumn column2 = TimeColumn.create("TC2");
        column2.appendInternal(PackedLocalTime.of(15, 4, 2));
        column2.appendInternal(PackedLocalTime.of(14, 4, 2));
        column2.appendInternal(PackedLocalTime.of(0, 4, 2));
        column2.appendInternal(PackedLocalTime.of(3, 6, 2));
        column2.appendInternal(PackedLocalTime.of(11, 4, 2));
        column1.append(column2);
        Assertions.assertEquals(10, column1.size());
        Assertions.assertTrue(column1.contains(LocalTime.of(14, 4, 2)));
    }

    @Test
    public void testAppendCell() {
        column1.appendCell("10:15:30");
        column1.appendCell("11:30:00");
        column1.appendCell("14:00:00");
        column1.appendCell("18:15:30");
        Assertions.assertEquals(4, column1.size());
    }

    @Test
    public void testSet() {
        column1.appendCell("10:15:30");
        column1.appendCell("11:30:00");
        column1.appendCell("14:00:00");
        column1.appendCell("18:15:30");
        column1.set(column1.isBeforeNoon(), LocalTime.NOON);
        Assertions.assertEquals(LocalTime.NOON, column1.get(0));
        Assertions.assertEquals(LocalTime.NOON, column1.get(1));
        Assertions.assertNotEquals(LocalTime.NOON, column1.get(2));
        Assertions.assertNotEquals(LocalTime.NOON, column1.get(3));
    }

    @Test
    public void testAppendCell2() {
        column1.appendCell("12:18:03 AM");
        column1.appendCell("8:18:03 AM");
        column1.appendCell("12:18:03 AM");
        Assertions.assertEquals(3, column1.size());
    }

    @Test
    public void copy() {
        fillLargerColumn();
        TimeColumn column2 = column1.copy();
        for (int i = 0; i < (column1.size()); i++) {
            Assertions.assertEquals(column2.getIntInternal(i), column1.getIntInternal(i));
        }
        Assertions.assertEquals(column1.name(), column2.name());
    }

    @Test
    public void clear() {
        fillLargerColumn();
        Assertions.assertEquals(11, column1.size());
        column1.clear();
        Assertions.assertEquals(0, column1.size());
    }

    @Test
    public void summary() {
        fillLargerColumn();
        Table t = column1.summary();
        Assertions.assertEquals("11", t.getString(0, "Value"));
        Assertions.assertEquals("1", t.getString(1, "Value"));
        Assertions.assertEquals("00:04:02", t.getString(2, "Value"));
        Assertions.assertEquals("18:04:02", t.getString(3, "Value"));
    }

    @Test
    public void asBytesAndByteSize() {
        fillLargerColumn();
        Assertions.assertEquals(4, column1.byteSize());
        Assertions.assertEquals(column1.getPackedTime(0), ByteBuffer.wrap(column1.asBytes(0)).getInt());
    }

    @Test
    public void countMissing() {
        fillLargerColumn();
        column1.appendInternal(TimeColumnType.missingValueIndicator());
        column1.appendInternal(TimeColumnType.missingValueIndicator());
        Assertions.assertEquals(3, column1.countMissing());
    }

    @Test
    public void isMissingIsNotMissing() {
        fillLargerColumn();
        column1.appendInternal(TimeColumnType.missingValueIndicator());
        column1.appendInternal(TimeColumnType.missingValueIndicator());
        Selection s = column1.isMissing();
        Assertions.assertEquals(3, s.size());
        Selection s2 = column1.isNotMissing();
        Assertions.assertEquals(10, s2.size());
    }

    @Test
    public void countUnique() {
        fillLargerColumn();
        column1.appendInternal(TimeColumnType.missingValueIndicator());
        Assertions.assertEquals(10, column1.countUnique());
    }

    @Test
    public void lag() {
        fillLargerColumn();
        TimeColumn column2 = column1.lag(2);
        Table t = Table.create("t");
        t.addColumns(column1, column2);
        for (int i = 0; i < ((column1.size()) - 2); i++) {
            Assertions.assertEquals(column2.getIntInternal((i + 2)), column1.getIntInternal(i));
        }
    }

    @Test
    public void lead() {
        fillLargerColumn();
        TimeColumn column2 = column1.lead(2);
        Table t = Table.create("t");
        t.addColumns(column1, column2);
        for (int i = 0; i < ((column1.size()) - 2); i++) {
            Assertions.assertEquals(column2.getIntInternal(i), column1.getIntInternal((i + 2)));
        }
    }

    @Test
    public void minuteOfDay() {
        fillLargerColumn();
        IntColumn column2 = column1.minuteOfDay();
        for (int i = 0; i < ((column1.size()) - 2); i++) {
            Assertions.assertEquals(column2.get(i), PackedLocalTime.getMinuteOfDay(column1.getPackedTime(i)), 1.0E-4);
        }
    }

    @Test
    public void secondOfDay() {
        fillLargerColumn();
        IntColumn column2 = column1.secondOfDay();
        for (int i = 0; i < ((column1.size()) - 2); i++) {
            Assertions.assertEquals(column2.get(i), PackedLocalTime.getSecondOfDay(column1.getPackedTime(i)), 1.0E-4);
        }
    }

    @Test
    public void testPlusHours() {
        fillColumn();
        TimeColumn column2 = column1.plusHours(3);
        IntColumn numberColumn = column2.differenceInHours(column1);
        int expected = -3;
        assertMinAndMaxEquals(expected, numberColumn);
    }

    @Test
    public void testTruncatedTo() {
        fillColumn();
        TimeColumn column2 = column1.truncatedTo(ChronoUnit.HOURS);
        Assertions.assertEquals(column1.get(0).getHour(), column2.get(0).getHour());
        Assertions.assertEquals(0, column2.get(0).getMinute());
        Assertions.assertEquals(0, column2.get(0).getSecond());
        Assertions.assertEquals(0, column2.get(0).getNano());
        Assertions.assertEquals(TimeColumnType.missingValueIndicator(), column2.getIntInternal(2));
    }

    @Test
    public void testWithHour() {
        fillColumn();
        TimeColumn column2 = column1.withHour(3);
        Assertions.assertEquals(3, column2.hour().min(), 0.001);
        Assertions.assertEquals(3, column2.hour().max(), 0.001);
    }

    @Test
    public void testWithMinute() {
        fillColumn();
        TimeColumn column2 = column1.withMinute(3);
        Assertions.assertEquals(3, column2.minute().min(), 0.001);
        Assertions.assertEquals(3, column2.minute().max(), 0.001);
    }

    @Test
    public void testWithSecond() {
        fillColumn();
        TimeColumn column2 = column1.withSecond(3);
        Assertions.assertEquals(3, column2.second().min(), 0.001);
        Assertions.assertEquals(3, column2.second().max(), 0.001);
    }

    @Test
    public void testSecond() {
        fillColumn();
        IntColumn second = column1.second();
        Assertions.assertEquals(2, second.get(0), 0.001);
        Assertions.assertEquals(30, second.get(1), 0.001);
        Assertions.assertEquals(IntColumnType.missingValueIndicator(), second.get(2), 0.001);
    }

    @Test
    public void testMinute() {
        fillColumn();
        IntColumn minute = column1.minute();
        Assertions.assertEquals(4, minute.get(0), 0.001);
        Assertions.assertEquals(15, minute.get(1), 0.001);
        Assertions.assertEquals(IntColumnType.missingValueIndicator(), minute.get(2), 0.001);
    }

    @Test
    public void testWithMillisecond() {
        fillColumn();
        TimeColumn column2 = column1.withMillisecond(3);
        Assertions.assertEquals(3, column2.milliseconds().min(), 0.001);
        Assertions.assertEquals(3, column2.milliseconds().max(), 0.001);
    }

    @Test
    public void testMinusHours() {
        fillColumn();
        TimeColumn column2 = column1.minusHours(0);
        IntColumn numberColumn = column2.differenceInHours(column1);
        int expected = 0;
        assertMinAndMaxEquals(expected, numberColumn);
    }

    @Test
    public void testPlusMinutes() {
        fillColumn();
        TimeColumn column2 = column1.plusMinutes(30);
        IntColumn numberColumn = column2.differenceInMinutes(column1);
        int expected = -30;
        assertMinAndMaxEquals(expected, numberColumn);
    }

    @Test
    public void testMinusMinutes() {
        fillColumn();
        TimeColumn column2 = column1.minusMinutes(30);
        IntColumn numberColumn = column2.differenceInMinutes(column1);
        int expected = 30;
        assertMinAndMaxEquals(expected, numberColumn);
    }

    @Test
    public void testPlusSeconds() {
        fillColumn();
        TimeColumn column2 = column1.plusSeconds(101);
        IntColumn numberColumn = column2.differenceInSeconds(column1);
        int expected = -101;
        assertMinAndMaxEquals(expected, numberColumn);
    }

    @Test
    public void testMinusSeconds() {
        fillColumn();
        TimeColumn column2 = column1.minusSeconds(101);
        IntColumn numberColumn = column2.differenceInSeconds(column1);
        int expected = 101;
        assertMinAndMaxEquals(expected, numberColumn);
    }

    @Test
    public void testPlusMilliseconds() {
        fillColumn();
        TimeColumn column2 = column1.plusMilliseconds(101);
        IntColumn numberColumn = column2.differenceInMilliseconds(column1);
        int expected = -101;
        assertMinAndMaxEquals(expected, numberColumn);
    }

    @Test
    public void testMinusMilliseconds() {
        fillColumn();
        TimeColumn column2 = column1.minusMilliseconds(101);
        IntColumn numberColumn = column2.differenceInMilliseconds(column1);
        int expected = 101;
        assertMinAndMaxEquals(expected, numberColumn);
    }

    @Test
    public void testNull() {
        TimeColumn col = TimeColumn.create("Game time");
        col.appendCell(null);
        Assertions.assertNull(col.get(0));
    }
}

