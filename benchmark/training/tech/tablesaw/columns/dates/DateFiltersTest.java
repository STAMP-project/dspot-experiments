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
package tech.tablesaw.columns.dates;


import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;


public class DateFiltersTest {
    private DateColumn localDateColumn = DateColumn.create("testing");

    private Table table = Table.create("test");

    @Test
    public void testDow() {
        Assertions.assertTrue(localDateColumn.isSunday().contains(0));
        Assertions.assertTrue(localDateColumn.isMonday().contains(1));
        Assertions.assertTrue(localDateColumn.isTuesday().contains(2));
        Assertions.assertTrue(localDateColumn.isWednesday().contains(3));
        Assertions.assertTrue(localDateColumn.isThursday().contains(4));
        Assertions.assertTrue(localDateColumn.isFriday().contains(7));
        Assertions.assertTrue(localDateColumn.isSaturday().contains(8));
    }

    @Test
    public void testIsFirstDayOfTheMonth() {
        Selection selection = localDateColumn.isFirstDayOfMonth();
        Assertions.assertFalse(selection.contains(0));
        Assertions.assertFalse(selection.contains(1));
        Assertions.assertTrue(selection.contains(2));
        Assertions.assertTrue(selection.contains(5));
        Assertions.assertFalse(selection.contains(6));
    }

    @Test
    public void testIsLastDayOfTheMonth() {
        Selection selection = localDateColumn.isLastDayOfMonth();
        Assertions.assertFalse(selection.contains(0));
        Assertions.assertTrue(selection.contains(1));
        Assertions.assertFalse(selection.contains(2));
    }

    @Test
    public void testIsInYear() {
        Selection selection = localDateColumn.isInYear(2016);
        Assertions.assertTrue(selection.contains(0));
        Assertions.assertTrue(selection.contains(1));
        Assertions.assertTrue(selection.contains(2));
        selection = localDateColumn.isInYear(2015);
        Assertions.assertFalse(selection.contains(0));
        Assertions.assertFalse(selection.contains(1));
        Assertions.assertFalse(selection.contains(2));
    }

    @Test
    public void testGetMonthValue() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        Month[] months = Month.values();
        DateColumn dateColumn = DateColumn.create("test");
        for (int i = 0; i < (months.length); i++) {
            dateColumn.append(date);
            date = date.plusMonths(1);
        }
        StringColumn month = dateColumn.month();
        IntColumn monthValue = dateColumn.monthValue();
        for (int i = 0; i < (months.length); i++) {
            Assertions.assertEquals(months[i].name(), month.get(i).toUpperCase());
            Assertions.assertEquals((i + 1), monthValue.get(i), 0.001);
        }
        Assertions.assertTrue(dateColumn.isInJanuary().contains(0));
        Assertions.assertTrue(dateColumn.isInFebruary().contains(1));
        Assertions.assertTrue(dateColumn.isInMarch().contains(2));
        Assertions.assertTrue(dateColumn.isInApril().contains(3));
        Assertions.assertTrue(dateColumn.isInMay().contains(4));
        Assertions.assertTrue(dateColumn.isInJune().contains(5));
        Assertions.assertTrue(dateColumn.isInJuly().contains(6));
        Assertions.assertTrue(dateColumn.isInAugust().contains(7));
        Assertions.assertTrue(dateColumn.isInSeptember().contains(8));
        Assertions.assertTrue(dateColumn.isInOctober().contains(9));
        Assertions.assertTrue(dateColumn.isInNovember().contains(10));
        Assertions.assertTrue(dateColumn.isInDecember().contains(11));
        Assertions.assertTrue(dateColumn.isInQ1().contains(2));
        Assertions.assertTrue(dateColumn.isInQ2().contains(4));
        Assertions.assertTrue(dateColumn.isInQ3().contains(8));
        Assertions.assertTrue(dateColumn.isInQ4().contains(11));
        Table t = Table.create("Test");
        t.addColumns(dateColumn);
        IntColumn index = IntColumn.indexColumn("index", t.rowCount(), 0);
        t.addColumns(index);
        Assertions.assertTrue(t.where(t.dateColumn("test").isInJanuary()).intColumn("index").contains(0));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInFebruary()).intColumn("index").contains(1));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInMarch()).intColumn("index").contains(2));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInApril()).intColumn("index").contains(3));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInMay()).intColumn("index").contains(4));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInJune()).intColumn("index").contains(5));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInJuly()).intColumn("index").contains(6));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInAugust()).intColumn("index").contains(7));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInSeptember()).intColumn("index").contains(8));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInOctober()).intColumn("index").contains(9));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInNovember()).intColumn("index").contains(10));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInDecember()).intColumn("index").contains(11));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInQ1()).intColumn("index").contains(2));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInQ2()).intColumn("index").contains(4));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInQ3()).intColumn("index").contains(8));
        Assertions.assertTrue(t.where(t.dateColumn("test").isInQ4()).intColumn("index").contains(11));
    }

    @Test
    public void testComparison() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        int packed = PackedLocalDate.pack(date);
        DateColumn dateColumn = DateColumn.create("test");
        int before = PackedLocalDate.minusDays(1, packed);
        int after = PackedLocalDate.plusDays(1, packed);
        LocalDate beforeDate = PackedLocalDate.asLocalDate(before);
        LocalDate afterDate = PackedLocalDate.asLocalDate(after);
        dateColumn.appendInternal(before);
        dateColumn.appendInternal(packed);
        dateColumn.appendInternal(after);
        Assertions.assertTrue(dateColumn.isBefore(packed).contains(0));
        Assertions.assertTrue(dateColumn.isBefore(date).contains(0));
        Assertions.assertTrue(dateColumn.isOnOrBefore(date).contains(0));
        Assertions.assertTrue(dateColumn.isEqualTo(packed).contains(1));
        Assertions.assertTrue(dateColumn.isEqualTo(date).contains(1));
        Assertions.assertTrue(dateColumn.isOnOrBefore(date).contains(1));
        Assertions.assertTrue(dateColumn.isOnOrAfter(date).contains(1));
        Assertions.assertFalse(dateColumn.isOnOrBefore(date).contains(2));
        Assertions.assertTrue(dateColumn.isAfter(packed).contains(2));
        Assertions.assertTrue(dateColumn.isAfter(date).contains(2));
        Assertions.assertTrue(dateColumn.isOnOrAfter(date).contains(2));
        Assertions.assertTrue(dateColumn.isBetweenExcluding(beforeDate, afterDate).contains(1));
        Assertions.assertTrue(dateColumn.isBetweenIncluding(beforeDate, afterDate).contains(2));
        Assertions.assertTrue(dateColumn.isBetweenIncluding(beforeDate, afterDate).contains(0));
        Assertions.assertFalse(dateColumn.isBetweenExcluding(beforeDate, afterDate).contains(2));
        Assertions.assertFalse(dateColumn.isBetweenExcluding(beforeDate, afterDate).contains(0));
        IntColumn index = IntColumn.indexColumn("index", dateColumn.size(), 0);
        Table t = Table.create("test", dateColumn, index);
        Assertions.assertTrue(t.where(dateColumn.isBefore(packed)).intColumn("index").contains(0));
        Assertions.assertTrue(t.where(dateColumn.isEqualTo(packed)).intColumn("index").contains(1));
        Assertions.assertTrue(t.where(dateColumn.isAfter(packed)).intColumn("index").contains(2));
        Assertions.assertTrue(t.where(t.dateColumn("test").isBetweenExcluding(beforeDate, afterDate)).intColumn("index").contains(1));
        Assertions.assertTrue(t.where(t.dateColumn("test").isBetweenIncluding(beforeDate, afterDate)).intColumn("index").contains(2));
        Assertions.assertTrue(t.where(t.dateColumn("test").isBetweenIncluding(beforeDate, afterDate)).intColumn("index").contains(0));
        Assertions.assertFalse(t.where(t.dateColumn("test").isBetweenExcluding(beforeDate, afterDate)).intColumn("index").contains(2));
        Assertions.assertFalse(t.where(t.dateColumn("test").isBetweenExcluding(beforeDate, afterDate)).intColumn("index").contains(0));
    }

    @Test
    public void testIsMissing() {
        DateColumn column = DateColumn.create("test");
        column.append(LocalDate.now());
        column.appendInternal(DateColumnType.missingValueIndicator());
        Assertions.assertTrue(column.isMissing().contains(1));
        Assertions.assertTrue(column.isNotMissing().contains(0));
        Assertions.assertTrue(column.isNotMissing().contains(0));
        Assertions.assertTrue(column.isMissing().contains(1));
    }

    @Test
    public void testColumnComparisons() {
        LocalDate dateTime = LocalDate.of(2015, 1, 25);
        DateColumn dateColumn = DateColumn.create("test");
        LocalDate beforeDate = dateTime.minusDays(1);
        LocalDate afterDate = dateTime.plusDays(1);
        dateColumn.append(beforeDate);
        dateColumn.append(dateTime);
        dateColumn.append(afterDate);
        DateColumn same = DateColumn.create("same");
        same.append(beforeDate);
        same.append(dateTime);
        same.append(afterDate);
        DateColumn before = DateColumn.create("before");
        before.append(beforeDate.minusDays(1));
        before.append(dateTime.minusDays(1));
        before.append(afterDate.minusDays(1));
        DateColumn after = DateColumn.create("after");
        after.append(beforeDate.plusDays(1));
        after.append(dateTime.plusDays(1));
        after.append(afterDate.plusDays(1));
        Table t = Table.create("test", dateColumn, same, before, after);
        Assertions.assertTrue(dateColumn.isOnOrAfter(same).contains(0));
        Assertions.assertTrue(t.dateColumn("test").isOnOrAfter(same).contains(0));
        Assertions.assertTrue(t.dateColumn("test").isOnOrAfter(t.dateColumn("same")).contains(0));
        Assertions.assertTrue(dateColumn.isOnOrBefore(same).contains(0));
        Assertions.assertTrue(t.dateColumn("test").isOnOrBefore(same).contains(0));
        Assertions.assertTrue(t.dateColumn("test").isOnOrBefore(t.dateColumn("same")).contains(0));
        Assertions.assertTrue(dateColumn.isEqualTo(same).contains(0));
        Assertions.assertTrue(t.dateColumn("test").isEqualTo(same).contains(0));
        Assertions.assertTrue(t.dateColumn("test").isEqualTo(t.dateColumn("same")).contains(0));
        Assertions.assertTrue(dateColumn.isBefore(after).contains(0));
        Assertions.assertFalse(dateColumn.isOnOrAfter(after).contains(0));
        Assertions.assertTrue(t.dateColumn("test").isBefore(after).contains(0));
        Assertions.assertTrue(t.dateColumn("test").isBefore(t.dateColumn("after")).contains(0));
        Assertions.assertTrue(dateColumn.isAfter(before).contains(0));
        Assertions.assertFalse(dateColumn.isOnOrBefore(before).contains(0));
        Assertions.assertTrue(t.dateColumn("test").isAfter(before).contains(0));
        Assertions.assertTrue(t.dateColumn("test").isAfter(t.dateColumn("before")).contains(0));
        Assertions.assertFalse(dateColumn.isNotEqualTo(same).contains(0));
        Assertions.assertFalse(t.dateColumn("test").isNotEqualTo(same).contains(0));
        Assertions.assertFalse(t.dateColumn("test").isNotEqualTo(t.dateColumn("same")).contains(0));
        Assertions.assertTrue(dateColumn.isOnOrBefore(same).contains(0));
        Assertions.assertTrue(dateColumn.isOnOrBefore(after).contains(0));
        Assertions.assertFalse(dateColumn.isOnOrBefore(before).contains(0));
        Assertions.assertTrue(dateColumn.isNotEqualTo(before).contains(0));
        Assertions.assertTrue(dateColumn.isOnOrAfter(same).contains(1));
        Assertions.assertTrue(dateColumn.isOnOrAfter(before).contains(2));
        Assertions.assertFalse(dateColumn.isOnOrAfter(after).contains(2));
        Assertions.assertTrue(dateColumn.isNotEqualTo(after).contains(0));
        /* assertTrue(t.dateColumn("test")
        .isOnOrAfter(t.dateColumn("same")).contains(0));
        assertTrue(t.dateColumn("test")
        .isOnOrAfter(same).contains(0));

        assertFalse(t.dateColumn("test")
        .isOnOrAfter(t.dateColumn("after")).contains(0));
        assertFalse(t.dateColumn("test")
        .isOnOrAfter(after).contains(0));

        assertTrue(t.dateColumn("test")
        .isOnOrBefore(t.dateColumn("same")).contains(0));
        assertTrue(t.dateColumn("test")
        .isOnOrBefore(same).contains(0));

        assertTrue(t.dateColumn("test")
        .isOnOrBefore(t.dateColumn("after")).contains(0));
        assertTrue(t.dateColumn("test")
        .isOnOrBefore(after).contains(0));

        assertFalse(t.dateColumn("test")
        .isOnOrBefore(t.dateColumn("before")).contains(0));
        assertFalse(t.dateColumn("test")
        .isOnOrBefore(before).contains(0));
         */
        Assertions.assertTrue(t.dateColumn("test").isNotEqualTo(t.dateColumn("before")).contains(0));
        Assertions.assertTrue(t.dateColumn("test").isNotEqualTo(before).contains(0));
        Assertions.assertFalse(t.dateColumn("test").isNotEqualTo(t.dateColumn("same")).contains(0));
        Assertions.assertFalse(t.dateColumn("test").isNotEqualTo(same).contains(0));
    }
}

