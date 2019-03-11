package tech.tablesaw.aggregate;


import AggregateFunctions.mean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;


public class PivotTableTest {
    @Test
    public void pivot() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").missingValueIndicator(":").build());
        t.addColumns(t.dateColumn("date").year());
        Table pivot = PivotTable.pivot(t, t.categoricalColumn("who"), t.categoricalColumn("date year"), t.numberColumn("approval"), mean);
        Assertions.assertTrue(pivot.columnNames().contains("who"));
        Assertions.assertTrue(pivot.columnNames().contains("2001"));
        Assertions.assertTrue(pivot.columnNames().contains("2002"));
        Assertions.assertTrue(pivot.columnNames().contains("2003"));
        Assertions.assertTrue(pivot.columnNames().contains("2004"));
        Assertions.assertEquals(6, pivot.rowCount());
    }
}

