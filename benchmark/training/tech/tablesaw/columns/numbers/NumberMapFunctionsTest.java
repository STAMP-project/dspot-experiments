package tech.tablesaw.columns.numbers;


import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;


public class NumberMapFunctionsTest {
    private static final String LINE_END = System.lineSeparator();

    @Test
    public void testNormalize() {
        double[] values = new double[]{ 4, 12, 9, 7, 8, 1, 3, 8, 9, 11 };
        DoubleColumn test = DoubleColumn.create("test", values);
        DoubleColumn result = test.normalize();
        Assertions.assertEquals(0, result.mean(), 0.01);
        Assertions.assertEquals(1, result.standardDeviation(), 0.01);
    }

    @Test
    public void testAsRatio() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };// sums to 10

        DoubleColumn test = DoubleColumn.create("test", values);
        DoubleColumn result = test.asRatio();
        Assertions.assertEquals(0.4, result.get(0), 0.01);
        Assertions.assertEquals(0.1, result.get(1), 0.01);
        Assertions.assertEquals(0.2, result.get(3), 0.01);
    }

    @Test
    public void testAsPercent() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };// sums to 10

        DoubleColumn test = DoubleColumn.create("test", values);
        DoubleColumn result = test.asPercent();
        Assertions.assertEquals(40, result.get(0), 0.01);
        Assertions.assertEquals(10, result.get(1), 0.01);
        Assertions.assertEquals(20, result.get(3), 0.01);
    }

    @Test
    public void testAdd() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };
        DoubleColumn test = DoubleColumn.create("test", values);
        DoubleColumn result = test.add(4);
        Assertions.assertEquals(8, result.get(0), 0.01);
        Assertions.assertEquals(5, result.get(1), 0.01);
        Assertions.assertEquals(6, result.get(3), 0.01);
    }

    @Test
    public void testAdd2() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };
        double[] values2 = new double[]{ 4, 1, 1, 2, 2 };
        DoubleColumn test = DoubleColumn.create("test", values);
        DoubleColumn test2 = DoubleColumn.create("test2", values2);
        DoubleColumn result = test.add(test2);
        Assertions.assertEquals(8, result.get(0), 0.01);
        Assertions.assertEquals(2, result.get(1), 0.01);
        Assertions.assertEquals(4, result.get(3), 0.01);
    }

    @Test
    public void testSubtract() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };
        DoubleColumn test = DoubleColumn.create("test", values);
        DoubleColumn result = test.subtract(4);
        Assertions.assertEquals(0, result.get(0), 0.01);
        Assertions.assertEquals((-3), result.get(1), 0.01);
        Assertions.assertEquals((-2), result.get(3), 0.01);
    }

    @Test
    public void testSubtract2() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };
        double[] values2 = new double[]{ 4, 1, 1, 2, 2 };
        DoubleColumn test = DoubleColumn.create("test", values);
        DoubleColumn test2 = DoubleColumn.create("test2", values2);
        DoubleColumn result = test.subtract(test2);
        Assertions.assertEquals(0, result.get(0), 0.01);
        Assertions.assertEquals(0, result.get(1), 0.01);
        Assertions.assertEquals(0, result.get(3), 0.01);
    }

    @Test
    public void testMultiply() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };
        DoubleColumn test = DoubleColumn.create("test", values);
        DoubleColumn result = test.multiply(4);
        Assertions.assertEquals(16, result.get(0), 0.01);
        Assertions.assertEquals(4, result.get(1), 0.01);
        Assertions.assertEquals(8, result.get(3), 0.01);
    }

    @Test
    public void testMultiply2() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };
        double[] values2 = new double[]{ 4, 1, 1, 2, 2 };
        DoubleColumn test = DoubleColumn.create("test", values);
        DoubleColumn test2 = DoubleColumn.create("test2", values2);
        DoubleColumn result = test.multiply(test2);
        Assertions.assertEquals(16, result.get(0), 0.01);
        Assertions.assertEquals(1, result.get(1), 0.01);
        Assertions.assertEquals(4, result.get(3), 0.01);
    }

    @Test
    public void testDivide() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };
        DoubleColumn test = DoubleColumn.create("test", values);
        DoubleColumn result = test.divide(2);
        Assertions.assertEquals(2, result.get(0), 0.01);
        Assertions.assertEquals(0.5, result.get(1), 0.01);
        Assertions.assertEquals(1.0, result.get(3), 0.01);
    }

    @Test
    public void testDivide2() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };
        double[] values2 = new double[]{ 4, 1, 1, 2, 2 };
        DoubleColumn test = DoubleColumn.create("test", values);
        DoubleColumn test2 = DoubleColumn.create("test2", values2);
        DoubleColumn result = test.divide(test2);
        Assertions.assertEquals(1, result.get(0), 0.01);
        Assertions.assertEquals(1, result.get(1), 0.01);
        Assertions.assertEquals(1, result.get(3), 0.01);
    }

    @Test
    public void lag() {
        IntColumn n1 = IntColumn.indexColumn("index", 4, 0);
        Table t = Table.create("tst");
        t.addColumns(n1, n1.lag((-2)));
        Assertions.assertEquals((((((((((((("            tst            " + (NumberMapFunctionsTest.LINE_END)) + " index  |  index lag(-2)  |") + (NumberMapFunctionsTest.LINE_END)) + "---------------------------") + (NumberMapFunctionsTest.LINE_END)) + "     0  |              2  |") + (NumberMapFunctionsTest.LINE_END)) + "     1  |              3  |") + (NumberMapFunctionsTest.LINE_END)) + "     2  |                 |") + (NumberMapFunctionsTest.LINE_END)) + "     3  |                 |"), t.print());
    }

    @Test
    public void lead() {
        IntColumn n1 = IntColumn.indexColumn("index", 4, 0);
        Table t = Table.create("tst");
        t.addColumns(n1, n1.lead(1));
        Assertions.assertEquals((((((((((((("            tst            " + (NumberMapFunctionsTest.LINE_END)) + " index  |  index lead(1)  |") + (NumberMapFunctionsTest.LINE_END)) + "---------------------------") + (NumberMapFunctionsTest.LINE_END)) + "     0  |              1  |") + (NumberMapFunctionsTest.LINE_END)) + "     1  |              2  |") + (NumberMapFunctionsTest.LINE_END)) + "     2  |              3  |") + (NumberMapFunctionsTest.LINE_END)) + "     3  |                 |"), t.print());
    }

    @Test
    public void testNeg() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10000));
        }
        DoubleColumn newDoubles = doubles.neg();
        Assertions.assertFalse(newDoubles.isEmpty());
        Assertions.assertEquals((0 - (doubles.get(0))), newDoubles.get(0), 1.0E-4);
    }

    @Test
    public void testRoundInt() {
        double[] values = new double[]{ 4.4, 1.9, 1.5, 2.3, 2.0 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        DoubleColumn newDoubles = doubles.roundInt();
        Assertions.assertEquals(4, newDoubles.get(0), 1.0E-4);
        Assertions.assertEquals(2, newDoubles.get(1), 1.0E-4);
        Assertions.assertEquals(2, newDoubles.get(2), 1.0E-4);
        Assertions.assertEquals(2, newDoubles.get(3), 1.0E-4);
        Assertions.assertEquals(2, newDoubles.get(4), 1.0E-4);
    }

    @Test
    public void testMod() {
        double[] values = new double[]{ 4, 1, 1, 2, 2 };
        double[] values2 = new double[]{ 4, 1, 1, 2, 2 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        DoubleColumn otherDoubles = DoubleColumn.create("otherDoubles", values2);
        DoubleColumn newDoubles = doubles.remainder(otherDoubles);
        Assertions.assertEquals(0, newDoubles.get(0), 0.001);
    }

    @Test
    public void testSquareAndSqrt() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10000));
        }
        DoubleColumn newDoubles = doubles.square();
        DoubleColumn revert = newDoubles.sqrt();
        for (int i = 0; i < (doubles.size()); i++) {
            Assertions.assertEquals(doubles.get(i), revert.get(i), 0.01);
        }
    }

    @Test
    public void testCubeAndCbrt() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10000));
        }
        DoubleColumn newDoubles = doubles.cube();
        DoubleColumn revert = newDoubles.cubeRoot();
        for (int i = 0; i < (doubles.size()); i++) {
            Assertions.assertEquals(doubles.get(i), revert.get(i), 0.01);
        }
    }

    @Test
    public void testLog1p() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10000));
        }
        DoubleColumn newDoubles = doubles.log1p();
        Assertions.assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testAbs() {
        double[] values = new double[]{ 4.4, -1.9, -1.5, 2.3, 0.0 };
        DoubleColumn doubles = DoubleColumn.create("doubles", values);
        DoubleColumn newDoubles = doubles.abs();
        Assertions.assertEquals(4.4, newDoubles.get(0), 1.0E-4);
        Assertions.assertEquals(1.9, newDoubles.get(1), 1.0E-4);
        Assertions.assertEquals(1.5, newDoubles.get(2), 1.0E-4);
        Assertions.assertEquals(2.3, newDoubles.get(3), 1.0E-4);
        Assertions.assertEquals(0, newDoubles.get(4), 1.0E-4);
    }

    @Test
    public void testRound() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10000));
        }
        DoubleColumn newDoubles = doubles.round();
        Assertions.assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testLogN() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10000));
        }
        DoubleColumn newDoubles = doubles.logN();
        Assertions.assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testLog10() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10000));
        }
        DoubleColumn newDoubles = doubles.log10();
        Assertions.assertFalse(newDoubles.isEmpty());
    }
}

