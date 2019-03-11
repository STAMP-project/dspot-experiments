package tech.tablesaw.columns.numbers;


import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;


public class NumberFillersTest {
    @Test
    public void testFromToBy() {
        assertContentEquals(DoubleColumn.create("doubles", new double[5]).fillWith(range(1.0, 12.0, 2.5)), 1.0, 3.5, 6.0, 8.5, 11.0);
        assertContentEquals(DoubleColumn.create("doubles", new double[5]).fillWith(range(1.0, 7.0, 2.5)), 1.0, 3.5, 6.0, 1.0, 3.5);
    }

    @Test
    public void testFromTo() {
        assertContentEquals(DoubleColumn.create("doubles", new double[5]).fillWith(range(1.0, 6.0)), 1.0, 2.0, 3.0, 4.0, 5.0);
        assertContentEquals(DoubleColumn.create("doubles", new double[5]).fillWith(range(1.0, 4.0)), 1.0, 2.0, 3.0, 1.0, 2.0);
    }

    @Test
    public void testFromByCount() {
        assertContentEquals(DoubleColumn.create("doubles", new double[5]).fillWith(range(1.0, 2.5, 5)), 1.0, 3.5, 6.0, 8.5, 11.0);
        assertContentEquals(DoubleColumn.create("doubles", new double[5]).fillWith(range(1.0, 2.5, 3)), 1.0, 3.5, 6.0, 1.0, 3.5);
    }

    @Test
    public void testFromCount() {
        assertContentEquals(DoubleColumn.create("doubles", new double[5]).fillWith(range(1.0, 5)), 1.0, 2.0, 3.0, 4.0, 5.0);
        assertContentEquals(DoubleColumn.create("doubles", new double[5]).fillWith(range(1.0, 3)), 1.0, 2.0, 3.0, 1.0, 2.0);
    }
}

