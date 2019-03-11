package tech.tablesaw.columns.numbers;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;


public class NumberFiltersTest {
    @Test
    public void testIsEqualTo() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        Selection selection = doubles.isEqualTo(1.0);
        Assertions.assertEquals(1, selection.get(0));
        Assertions.assertEquals(2, selection.get(1));
        Assertions.assertEquals(2, selection.size());
    }

    @Test
    public void testIsNotEqualTo() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        Selection selection = doubles.isNotEqualTo(1.0);
        Assertions.assertEquals(0, selection.get(0));
        Assertions.assertEquals(3, selection.get(1));
        Assertions.assertEquals(4, selection.get(2));
        Assertions.assertEquals(3, selection.size());
    }

    @Test
    public void testIsZero() {
        double[] values = new double[]{ 4, 0, -1 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        Selection selection = doubles.isZero();
        Assertions.assertEquals(1, selection.get(0));
        Assertions.assertEquals(1, selection.size());
    }

    @Test
    public void testIsPositive() {
        double[] values = new double[]{ 4, 0, -1 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        Selection selection = doubles.isPositive();
        Assertions.assertEquals(0, selection.get(0));
        Assertions.assertEquals(1, selection.size());
    }

    @Test
    public void testIsNegative() {
        double[] values = new double[]{ 4, 0, -1.0E-5 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        Selection selection = doubles.isNegative();
        Assertions.assertEquals(2, selection.get(0));
        Assertions.assertEquals(1, selection.size());
    }

    @Test
    public void testIsNonNegative() {
        double[] values = new double[]{ 4, 0, -1.0E-5 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        Selection selection = doubles.isNonNegative();
        Assertions.assertEquals(0, selection.get(0));
        Assertions.assertEquals(1, selection.get(1));
        Assertions.assertEquals(2, selection.size());
    }

    @Test
    public void testIsGreaterThanOrEqualTo() {
        double[] values = new double[]{ 4, 0, -1.0E-5 };
        double[] otherValues = new double[]{ 4, -1.3, 1.0E-5, Double.NaN };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        Selection selection = doubles.isGreaterThanOrEqualTo(0.0);
        Assertions.assertEquals(0, selection.get(0));
        Assertions.assertEquals(1, selection.get(1));
        Assertions.assertEquals(2, selection.size());
        DoubleColumn others = DoubleColumn.create("others", otherValues);
        Selection selection1 = doubles.isGreaterThanOrEqualTo(others);
        Assertions.assertEquals(0, selection1.get(0));
        Assertions.assertEquals(1, selection1.get(1));
        Assertions.assertEquals(2, selection1.size());
    }

    @Test
    public void testIsLessThanOrEqualTo() {
        double[] values = new double[]{ 4, 0, -1.0E-5 };
        double[] otherValues = new double[]{ 4, -1.3, 1.0E-5, Double.NaN };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        Selection selection = doubles.isLessThanOrEqualTo(0.0);
        Assertions.assertEquals(1, selection.get(0));
        Assertions.assertEquals(2, selection.get(1));
        Assertions.assertEquals(2, selection.size());
        DoubleColumn others = DoubleColumn.create("others", otherValues);
        Selection selection1 = doubles.isLessThanOrEqualTo(others);
        Assertions.assertEquals(0, selection1.get(0));
        Assertions.assertEquals(2, selection1.get(1));
        Assertions.assertEquals(2, selection1.size());
    }

    @Test
    public void testIsLessThan() {
        double[] values = new double[]{ 4, 0, -1.0E-5, 5.0 };
        double[] values2 = new double[]{ 4, 11, -3.00001, 5.1 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        DoubleColumn doubles2 = DoubleColumn.create("doubles2", values2);
        Selection selection = doubles.isLessThan(doubles2);
        Assertions.assertEquals(1, selection.get(0));
        Assertions.assertEquals(3, selection.get(1));
        Assertions.assertEquals(2, selection.size());
    }

    @Test
    public void testIsGreaterThan() {
        double[] values = new double[]{ 4, 0, -1.0E-5, 5.0 };
        double[] otherValues = new double[]{ 4, -1.3, 1.0E-5, Double.NaN };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        Selection selection = doubles.isGreaterThan(0);
        Assertions.assertEquals(0, selection.get(0));
        Assertions.assertEquals(3, selection.get(1));
        Assertions.assertEquals(2, selection.size());
        DoubleColumn others = DoubleColumn.create("others", otherValues);
        Selection selection1 = doubles.isGreaterThan(others);
        Assertions.assertEquals(1, selection1.get(0));
        Assertions.assertEquals(1, selection1.size());
    }

    @Test
    public void testIsEqualTo1() {
        double[] values = new double[]{ 4, 0, -1.0E-5, 5.0, 4.44443 };
        double[] values2 = new double[]{ 4, 11, -3.00001, 5.1, 4.44443 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        DoubleColumn doubles2 = DoubleColumn.create("doubles2", values2);
        Selection selection = doubles.isEqualTo(doubles2);
        Assertions.assertEquals(0, selection.get(0));
        Assertions.assertEquals(4, selection.get(1));
        Assertions.assertEquals(2, selection.size());
    }

    @Test
    public void testIsNotEqualTo1() {
        double[] values = new double[]{ 4, 0, -1.0E-5, 5.0, 4.44443 };
        double[] values2 = new double[]{ 4, 11, -3.00001, 5.1, 4.44443 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        DoubleColumn doubles2 = DoubleColumn.create("doubles2", values2);
        Selection selection = doubles.isNotEqualTo(doubles2);
        Assertions.assertEquals(1, selection.get(0));
        Assertions.assertEquals(2, selection.get(1));
        Assertions.assertEquals(3, selection.get(2));
        Assertions.assertEquals(3, selection.size());
        Selection selection1 = doubles.isNotEqualTo(doubles2);
        Assertions.assertEquals(1, selection1.get(0));
        Assertions.assertEquals(2, selection1.get(1));
        Assertions.assertEquals(3, selection1.get(2));
        Assertions.assertEquals(3, selection1.size());
    }

    @Test
    public void testIsMissing() {
        double[] values = new double[]{ 4, 1, Double.NaN, 2, 2 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        Selection selection = doubles.isMissing();
        Assertions.assertEquals(2, selection.get(0));
        Assertions.assertEquals(1, selection.size());
    }

    @Test
    public void testIsNotMissing() {
        double[] values = new double[]{ 4, 1, Double.NaN, 2, 2 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        Selection selection = doubles.isNotMissing();
        Assertions.assertEquals(0, selection.get(0));
        Assertions.assertEquals(1, selection.get(1));
        Assertions.assertEquals(4, selection.size());
    }

    @Test
    public void testNotIn() {
        double[] values = new double[]{ 4, 1, Double.NaN, 2, 2 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        double[] comparison = new double[]{ 1, 2 };
        Selection selection = doubles.isNotIn(comparison);
        Assertions.assertEquals(0, selection.get(0));
        Assertions.assertEquals(2, selection.get(1));
        Assertions.assertEquals(2, selection.size());
    }

    @Test
    public void testIsBetweenInclusive() throws Exception {
        Table bush = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
        Table result = bush.where(bush.numberColumn("approval").isBetweenInclusive(0, 49));
        Assertions.assertEquals(10, result.rowCount());
    }
}

