package tech.tablesaw.table;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;


public class TableSliceTest {
    private Table source;

    @Test
    public void column() {
        TableSlice slice = new TableSlice(source, Selection.withRange(0, 4));
        Assertions.assertEquals(source.column(1).name(), slice.column(1).name());
        Assertions.assertTrue(((source.rowCount()) > (slice.column(1).size())));
        Assertions.assertEquals(source.column("date").name(), slice.column("date").name());
        Assertions.assertTrue(((source.rowCount()) > (slice.column("date").size())));
        Assertions.assertEquals(slice.column(1).size(), slice.column("date").size());
        Assertions.assertEquals(4, slice.column("date").size());
    }

    @Test
    public void columnCount() {
        TableSlice slice = new TableSlice(source, Selection.withRange(0, source.rowCount()));
        Assertions.assertEquals(source.columnCount(), slice.columnCount());
    }

    @Test
    public void rowCount() {
        TableSlice slice = new TableSlice(source, Selection.withRange(0, source.rowCount()));
        Assertions.assertEquals(source.rowCount(), slice.rowCount());
        TableSlice slice1 = new TableSlice(source, Selection.withRange(0, 100));
        Assertions.assertEquals(100, slice1.rowCount());
    }

    @Test
    public void columns() {
        TableSlice slice = new TableSlice(source, Selection.withRange(0, source.rowCount()));
        Assertions.assertEquals(source.columns(), slice.columns());
    }

    @Test
    public void columnIndex() {
        TableSlice slice = new TableSlice(source, Selection.withRange(0, source.rowCount()));
        Assertions.assertEquals(source.columnIndex("who"), slice.columnIndex("who"));
        Column<?> who = source.column("who");
        Assertions.assertEquals(source.columnIndex(who), slice.columnIndex(who));
    }

    @Test
    public void get() {
        TableSlice slice = new TableSlice(source, Selection.withRange(10, source.rowCount()));
        Assertions.assertNotNull(slice.get(0, 1));
        Assertions.assertEquals(source.get(10, 1), slice.get(0, 1));
    }

    @Test
    public void name() {
        TableSlice slice = new TableSlice(source, Selection.withRange(0, source.rowCount()));
        Assertions.assertEquals(source.name(), slice.name());
    }

    @Test
    public void clear() {
        TableSlice slice = new TableSlice(source, Selection.withRange(0, source.rowCount()));
        slice.clear();
        Assertions.assertTrue(slice.isEmpty());
        Assertions.assertFalse(source.isEmpty());
    }

    @Test
    public void columnNames() {
        TableSlice slice = new TableSlice(source, Selection.withRange(0, source.rowCount()));
        Assertions.assertEquals(source.columnNames(), slice.columnNames());
    }

    @Test
    public void addColumn() {
        UnsupportedOperationException thrown = Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            TableSlice slice = new TableSlice(source, Selection.withRange(0, source.rowCount()));
            slice.addColumns(StringColumn.create("test"));
        });
        Assertions.assertTrue(thrown.getMessage().contains("Class TableSlice does not support the addColumns operation"));
    }

    @Test
    public void removeColumns() {
        UnsupportedOperationException thrown = Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            TableSlice slice = new TableSlice(source, Selection.withRange(0, source.rowCount()));
            slice.removeColumns("who");
        });
        Assertions.assertTrue(thrown.getMessage().contains("Class TableSlice does not support the removeColumns operation"));
    }

    @Test
    public void first() {
        TableSlice slice = new TableSlice(source, Selection.withRange(2, 12));
        Table first = slice.first(5);
        Assertions.assertEquals(first.get(0, 1), slice.get(0, 1));
        Assertions.assertEquals(first.get(0, 1), source.get(2, 1));
    }

    @Test
    public void setName() {
        TableSlice slice = new TableSlice(source, Selection.withRange(0, source.rowCount()));
        slice.setName("foo");
        Assertions.assertEquals("foo", slice.name());
        Assertions.assertNotEquals("foo", source.name());
    }

    @Test
    public void print() {
        TableSlice slice = new TableSlice(source, Selection.withRange(0, source.rowCount()));
        Assertions.assertEquals(source.print(), slice.print());
    }

    @Test
    public void asTable() {
        TableSlice slice = new TableSlice(source, Selection.withRange(1, 11));
        Table t = slice.asTable();
        Assertions.assertEquals(10, t.rowCount());
        Assertions.assertEquals(source.get(1, 1), t.get(0, 1));
    }

    @Test
    public void reduce() throws Exception {
        source = Table.read().csv("../data/bush.csv");
        TableSlice slice = new TableSlice(source, Selection.with(2));
        Assertions.assertEquals(58.0, slice.reduce("approval", AggregateFunctions.sum), 1.0E-4);
    }
}

