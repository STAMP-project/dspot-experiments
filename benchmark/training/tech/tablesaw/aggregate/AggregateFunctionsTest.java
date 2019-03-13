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
package tech.tablesaw.aggregate;


import org.apache.commons.math3.stat.StatUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.table.SelectionTableSliceGroup;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;


public class AggregateFunctionsTest {
    private Table table;

    @Test
    public void testGroupMean() {
        StringColumn byColumn = table.stringColumn("who");
        TableSliceGroup group = StandardTableSliceGroup.create(table, byColumn);
        Table result = group.aggregate("approval", AggregateFunctions.mean, AggregateFunctions.stdDev);
        Assertions.assertEquals(3, result.columnCount());
        Assertions.assertEquals("who", result.column(0).name());
        Assertions.assertEquals(6, result.rowCount());
        Assertions.assertEquals("65.671875", result.getUnformatted(0, 1));
        Assertions.assertEquals("10.648876067826901", result.getUnformatted(0, 2));
    }

    @Test
    public void testDateMin() {
        StringColumn byColumn = table.dateColumn("date").yearQuarter();
        Table result = table.summarize("approval", "date", AggregateFunctions.mean, AggregateFunctions.earliestDate).by(byColumn);
        Assertions.assertEquals(3, result.columnCount());
        Assertions.assertEquals(13, result.rowCount());
    }

    @Test
    public void testBooleanAggregateFunctions() {
        boolean[] values = new boolean[]{ true, false };
        BooleanColumn bc = BooleanColumn.create("test", values);
        Assertions.assertTrue(AggregateFunctions.anyTrue.summarize(bc));
        Assertions.assertFalse(AggregateFunctions.noneTrue.summarize(bc));
        Assertions.assertFalse(AggregateFunctions.allTrue.summarize(bc));
    }

    @Test
    public void testGroupMean2() {
        Table result = table.summarize("approval", AggregateFunctions.mean, AggregateFunctions.stdDev).apply();
        Assertions.assertEquals(2, result.columnCount());
    }

    @Test
    public void testApplyWithNonNumericResults() {
        Table result = table.summarize("date", AggregateFunctions.earliestDate, AggregateFunctions.latestDate).apply();
        Assertions.assertEquals(2, result.columnCount());
    }

    @Test
    public void testGroupMean3() {
        Summarizer function = table.summarize("approval", AggregateFunctions.mean, AggregateFunctions.stdDev);
        Table result = function.by("Group", 10);
        Assertions.assertEquals(32, result.rowCount());
    }

    @Test
    public void testGroupMean4() {
        table.addColumns(table.numberColumn("approval").cube());
        table.column(3).setName("cubed");
        Table result = table.summarize("approval", "cubed", AggregateFunctions.mean, AggregateFunctions.stdDev).apply();
        Assertions.assertEquals(4, result.columnCount());
    }

    @Test
    public void testGroupMeanByStep() {
        TableSliceGroup group = SelectionTableSliceGroup.create(table, "Step", 5);
        Table result = group.aggregate("approval", AggregateFunctions.mean, AggregateFunctions.stdDev);
        Assertions.assertEquals(3, result.columnCount());
        Assertions.assertEquals("53.6", result.getUnformatted(0, 1));
        Assertions.assertEquals("2.5099800796022267", result.getUnformatted(0, 2));
    }

    @Test
    public void testSummaryWithACalculatedColumn() {
        Summarizer summarizer = new Summarizer(table, table.dateColumn("date").year(), AggregateFunctions.mean);
        Table t = summarizer.apply();
        double avg = t.doubleColumn(0).get(0);
        Assertions.assertTrue(((avg > 2002) && (avg < 2003)));
    }

    @Test
    public void test2ColumnGroupMean() {
        StringColumn byColumn1 = table.stringColumn("who");
        DateColumn byColumn2 = table.dateColumn("date");
        Table result = table.summarize("approval", AggregateFunctions.mean, AggregateFunctions.sum).by(byColumn1, byColumn2);
        Assertions.assertEquals(4, result.columnCount());
        Assertions.assertEquals("who", result.column(0).name());
        Assertions.assertEquals(323, result.rowCount());
        Assertions.assertEquals("46.0", result.getUnformatted(0, 2));
    }

    @Test
    public void testComplexSummarizing() {
        table.addColumns(table.numberColumn("approval").cube());
        table.column(3).setName("cubed");
        StringColumn byColumn1 = table.stringColumn("who");
        StringColumn byColumn2 = table.dateColumn("date").yearMonth();
        Table result = table.summarize("approval", "cubed", AggregateFunctions.mean, AggregateFunctions.sum).by(byColumn1, byColumn2);
        Assertions.assertEquals(6, result.columnCount());
        Assertions.assertEquals("who", result.column(0).name());
        Assertions.assertEquals("date year & month", result.column(1).name());
    }

    @Test
    public void testMultipleColumnTypes() {
        boolean[] args = new boolean[]{ true, false, true, false };
        BooleanColumn booleanColumn = BooleanColumn.create("b", args);
        double[] numbers = new double[]{ 1, 2, 3, 4 };
        DoubleColumn numberColumn = DoubleColumn.create("n", numbers);
        String[] strings = new String[]{ "M", "F", "M", "F" };
        StringColumn stringColumn = StringColumn.create("s", strings);
        Table table = Table.create("test", booleanColumn, numberColumn);
        table.summarize(booleanColumn, numberColumn, AggregateFunctions.countTrue, AggregateFunctions.standardDeviation).by(stringColumn);
    }

    @Test
    public void testMultipleColumnTypesWithApply() {
        boolean[] args = new boolean[]{ true, false, true, false };
        BooleanColumn booleanColumn = BooleanColumn.create("b", args);
        double[] numbers = new double[]{ 1, 2, 3, 4 };
        DoubleColumn numberColumn = DoubleColumn.create("n", numbers);
        String[] strings = new String[]{ "M", "F", "M", "F" };
        StringColumn stringColumn = StringColumn.create("s", strings);
        Table table = Table.create("test", booleanColumn, numberColumn, stringColumn);
        Table summarized = table.summarize(booleanColumn, numberColumn, AggregateFunctions.countTrue, AggregateFunctions.standardDeviation).apply();
        Assertions.assertEquals(1.2909944487358056, summarized.doubleColumn(1).get(0), 1.0E-5);
    }

    @Test
    public void testBooleanFunctions() {
        BooleanColumn c = BooleanColumn.create("test");
        c.append(true);
        c.appendCell("");
        c.append(false);
        Assertions.assertEquals(1, AggregateFunctions.countTrue.summarize(c), 1.0E-4);
        Assertions.assertEquals(1, AggregateFunctions.countFalse.summarize(c), 1.0E-4);
        Assertions.assertEquals(0.5, AggregateFunctions.proportionFalse.summarize(c), 1.0E-4);
        Assertions.assertEquals(0.5, AggregateFunctions.proportionTrue.summarize(c), 1.0E-4);
        Assertions.assertEquals(1, AggregateFunctions.countMissing.summarize(c), 1.0E-4);
        Assertions.assertEquals(3, AggregateFunctions.countWithMissing.summarize(c), 1.0E-4);
        Assertions.assertEquals(2, AggregateFunctions.countUnique.summarize(c), 1.0E-4);
    }

    @Test
    public void testPercentileFunctions() {
        double[] values = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        DoubleColumn c = DoubleColumn.create("test", values);
        c.appendCell("");
        Assertions.assertEquals(1, AggregateFunctions.countMissing.summarize(c), 1.0E-4);
        Assertions.assertEquals(11, AggregateFunctions.countWithMissing.summarize(c), 1.0E-4);
        Assertions.assertEquals(StatUtils.percentile(values, 90), AggregateFunctions.percentile90.summarize(c), 1.0E-4);
        Assertions.assertEquals(StatUtils.percentile(values, 95), AggregateFunctions.percentile95.summarize(c), 1.0E-4);
        Assertions.assertEquals(StatUtils.percentile(values, 99), AggregateFunctions.percentile99.summarize(c), 1.0E-4);
        Assertions.assertEquals(10, AggregateFunctions.countUnique.summarize(c), 1.0E-4);
    }
}

