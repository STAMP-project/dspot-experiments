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


import ColumnType.DOUBLE;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.selection.Selection;


/**
 * Unit tests for the NumberColumn class
 */
public class NumberColumnTest {
    private static final double MISSING = DoubleColumnType.missingValueIndicator();

    private static final DoublePredicate isPositiveOrZeroD = ( d) -> d >= 0;

    private static final DoublePredicate isNegativeD = ( d) -> d < 0;

    private static final DoubleFunction<String> toStringD = ( d) -> String.valueOf(d);

    private static final DoubleBinaryOperator sumD = ( d1, d2) -> d1 + d2;

    @Test
    public void testPercentiles() {
        IntColumn c = IntColumn.indexColumn("t", 99, 1);
        IntColumn c2 = c.copy();
        c2.appendCell("");
        Assertions.assertEquals(50, c.median(), 1.0E-5);
        Assertions.assertEquals(50, c2.median(), 1.0E-5);
        Assertions.assertEquals(50, AggregateFunctions.median.summarize(c), 1.0E-5);
        Assertions.assertEquals(25, c.quartile1(), 1.0E-5);
        Assertions.assertEquals(25, c2.quartile1(), 1.0E-5);
        Assertions.assertEquals(25, AggregateFunctions.quartile1.summarize(c), 1.0E-5);
        Assertions.assertEquals(75, c.quartile3(), 1.0E-5);
        Assertions.assertEquals(75, c2.quartile3(), 1.0E-5);
        Assertions.assertEquals(75, AggregateFunctions.quartile3.summarize(c), 1.0E-5);
        Assertions.assertEquals(90, AggregateFunctions.percentile90.summarize(c), 1.0E-5);
        Assertions.assertEquals(5, c2.percentile(5), 1.0E-5);
        Assertions.assertEquals(5, c.percentile(5), 1.0E-5);
        Assertions.assertEquals(5, AggregateFunctions.percentile(c, 5.0), 1.0E-5);
        Assertions.assertEquals(95, AggregateFunctions.percentile95.summarize(c), 1.0E-5);
        Assertions.assertEquals(99, AggregateFunctions.percentile99.summarize(c), 1.0E-5);
    }

    @Test
    public void testSummarize() {
        IntColumn c = IntColumn.indexColumn("t", 99, 1);
        IntColumn c2 = c.copy();
        c2.appendCell("");
        double c2Variance = c2.variance();
        double cVariance = StatUtils.variance(c.asDoubleArray());
        Assertions.assertEquals(cVariance, c2Variance, 1.0E-5);
        Assertions.assertEquals(StatUtils.sumLog(c.asDoubleArray()), c2.sumOfLogs(), 1.0E-5);
        Assertions.assertEquals(StatUtils.sumSq(c.asDoubleArray()), c2.sumOfSquares(), 1.0E-5);
        Assertions.assertEquals(StatUtils.geometricMean(c.asDoubleArray()), c2.geometricMean(), 1.0E-5);
        Assertions.assertEquals(StatUtils.product(c.asDoubleArray()), c2.product(), 1.0E-5);
        Assertions.assertEquals(StatUtils.populationVariance(c.asDoubleArray()), c2.populationVariance(), 1.0E-5);
        Assertions.assertEquals(getQuadraticMean(), c2.quadraticMean(), 1.0E-5);
        Assertions.assertEquals(getStandardDeviation(), c2.standardDeviation(), 1.0E-5);
        Assertions.assertEquals(getKurtosis(), c2.kurtosis(), 1.0E-5);
        Assertions.assertEquals(getSkewness(), c2.skewness(), 1.0E-5);
        Assertions.assertEquals(StatUtils.variance(c.asDoubleArray()), c.variance(), 1.0E-5);
        Assertions.assertEquals(StatUtils.sumLog(c.asDoubleArray()), c.sumOfLogs(), 1.0E-5);
        Assertions.assertEquals(StatUtils.sumSq(c.asDoubleArray()), c.sumOfSquares(), 1.0E-5);
        Assertions.assertEquals(StatUtils.geometricMean(c.asDoubleArray()), c.geometricMean(), 1.0E-5);
        Assertions.assertEquals(StatUtils.product(c.asDoubleArray()), c.product(), 1.0E-5);
        Assertions.assertEquals(StatUtils.populationVariance(c.asDoubleArray()), c.populationVariance(), 1.0E-5);
        Assertions.assertEquals(getQuadraticMean(), c.quadraticMean(), 1.0E-5);
        Assertions.assertEquals(getStandardDeviation(), c.standardDeviation(), 1.0E-5);
        Assertions.assertEquals(getKurtosis(), c.kurtosis(), 1.0E-5);
        Assertions.assertEquals(getSkewness(), c.skewness(), 1.0E-5);
    }

    @Test
    public void createFromNumbers() {
        List<Number> numberList = new ArrayList<>();
        numberList.add(4);
        DoubleColumn column = DoubleColumn.create("test", numberList);
        Assertions.assertEquals(4.0, column.get(0), 0.001);
        DoubleColumn column1 = DoubleColumn.create("T", numberList.toArray(new Number[numberList.size()]));
        Assertions.assertEquals(4.0, column1.get(0), 0.001);
        float[] floats = new float[1];
        floats[0] = 4.0F;
        DoubleColumn column2 = DoubleColumn.create("T", floats);
        Assertions.assertEquals(4.0, column2.get(0), 0.001);
        int[] ints = new int[1];
        ints[0] = 4;
        DoubleColumn column3 = DoubleColumn.create("T", ints);
        Assertions.assertEquals(4.0, column3.get(0), 0.001);
        long[] longs = new long[1];
        longs[0] = 4000000000L;
        DoubleColumn column4 = DoubleColumn.create("T", longs);
        Assertions.assertEquals(4.0E9, column4.get(0), 0.001);
    }

    @Test
    public void testDoubleIsIn() {
        int[] originalValues = new int[]{ 32, 42, 40, 57, 52, -2 };
        double[] inValues = new double[]{ 10, -2, 57, -5 };
        DoubleColumn initial = DoubleColumn.create("Test", originalValues.length);
        Table t = Table.create("t", initial);
        for (int value : originalValues) {
            initial.append(value);
        }
        Selection filter = t.numberColumn("Test").isIn(inValues);
        Table result = t.where(filter);
        Assertions.assertNotNull(result);
    }

    @Test
    public void testCorrelation() {
        double[] x = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        double[] y = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        DoubleColumn xCol = DoubleColumn.create("x", x);
        DoubleColumn yCol = DoubleColumn.create("y", y);
        double resultP = xCol.pearsons(yCol);
        double resultS = xCol.spearmans(yCol);
        double resultK = xCol.kendalls(yCol);
        Assertions.assertEquals(new PearsonsCorrelation().correlation(x, y), resultP, 1.0E-4);
        Assertions.assertEquals(new SpearmansCorrelation().correlation(x, y), resultS, 1.0E-4);
        Assertions.assertEquals(new KendallsCorrelation().correlation(x, y), resultK, 1.0E-4);
    }

    @Test
    public void testCorrelation2() {
        double[] x = new double[]{ 1, 2, 3, 4, 5, 6, 7, Double.NaN, 9, 10 };
        double[] y = new double[]{ 1, 2, 3, Double.NaN, 5, 6, 7, 8, 9, 10 };
        DoubleColumn xCol = DoubleColumn.create("x", x);
        DoubleColumn yCol = DoubleColumn.create("y", y);
        double resultP = xCol.pearsons(yCol);
        double resultK = xCol.kendalls(yCol);
        Assertions.assertEquals(new PearsonsCorrelation().correlation(x, y), resultP, 1.0E-4);
        Assertions.assertEquals(new KendallsCorrelation().correlation(x, y), resultK, 1.0E-4);
    }

    @Test
    public void testBetweenExclusive() {
        int[] originalValues = new int[]{ 32, 42, 40, 57, 52, -2 };
        IntColumn initial = IntColumn.create("Test", originalValues);
        Table t = Table.create("t", initial);
        Selection filter = t.numberColumn("Test").isBetweenExclusive(42, 57);
        Table result = t.where(filter);
        Assertions.assertEquals(1, result.rowCount());
        Assertions.assertEquals("52", result.getString(0, "Test"));
    }

    @Test
    public void testIsLessThan() {
        int size = 1000000;
        Table table = Table.create("t");
        DoubleColumn numberColumn = DoubleColumn.create("test", size);
        table.addColumns(numberColumn);
        for (int i = 0; i < size; i++) {
            numberColumn.set(i, Math.random());
        }
        Selection results = numberColumn.isLessThan(0.5F);
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (results.contains(i)) {
                count++;
            }
        }
        // Probabilistic answer.
        Assertions.assertTrue((count < 575000));
        Assertions.assertTrue((count > 425000));
    }

    @Test
    public void testNumberFormat1() {
        DoubleColumn numberColumn = DoubleColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.currency("en", "US"));
        Assertions.assertEquals("$48,392.29", numberColumn.getString(0));
    }

    @Test
    public void testNumberFormat2() {
        DoubleColumn numberColumn = DoubleColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.intsWithGrouping());
        Assertions.assertEquals("48,392", numberColumn.getString(0));
    }

    @Test
    public void testAsString() {
        DoubleColumn numberColumn = DoubleColumn.create("test");
        numberColumn.append(48392.2932);
        StringColumn sc = numberColumn.asStringColumn();
        Assertions.assertEquals("test strings", sc.name());
        Assertions.assertEquals("48392.2932", sc.get(0));
    }

    @Test
    public void testNumberFormat3() {
        DoubleColumn numberColumn = DoubleColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.ints());
        Assertions.assertEquals("48392", numberColumn.getString(0));
    }

    @Test
    public void testNumberFormat4() {
        DoubleColumn numberColumn = DoubleColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.fixedWithGrouping(3));
        Assertions.assertEquals("48,392.293", numberColumn.getString(0));
    }

    @Test
    public void testNumberFormat5() {
        DoubleColumn numberColumn = DoubleColumn.create("test");
        numberColumn.append(0.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.percent(1));
        Assertions.assertEquals("29.3%", numberColumn.getString(0));
    }

    @Test
    public void testIndexColumn() {
        IntColumn numberColumn = IntColumn.indexColumn("index", 12424, 0);
        Assertions.assertEquals("12423", numberColumn.getString(((numberColumn.size()) - 1)));
    }

    @Test
    public void testIsGreaterThan() {
        int size = 1000000;
        Table table = Table.create("t");
        DoubleColumn numberColumn = DoubleColumn.create("test", size);
        table.addColumns(numberColumn);
        for (int i = 0; i < size; i++) {
            numberColumn.set(i, Math.random());
        }
        Selection results = numberColumn.isGreaterThan(0.5F);
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (results.contains(i)) {
                count++;
            }
        }
        // Probabilistic answer.
        Assertions.assertTrue((count < 575000));
        Assertions.assertTrue((count > 425000));
    }

    @Test
    public void testSort() {
        int records = 1000000;
        DoubleColumn numberColumn = DoubleColumn.create("test", records);
        for (int i = 0; i < records; i++) {
            numberColumn.set(i, Math.random());
        }
        numberColumn.sortAscending();
        double last = Double.NEGATIVE_INFINITY;
        for (double n : numberColumn) {
            Assertions.assertTrue((n >= last));
            last = n;
        }
        numberColumn.sortDescending();
        last = Double.POSITIVE_INFINITY;
        for (double n : numberColumn) {
            Assertions.assertTrue((n <= last));
            last = n;
        }
        records = 10;
        numberColumn = DoubleColumn.create("test", records);
        for (int i = 0; i < records; i++) {
            numberColumn.set(i, Math.random());
        }
        numberColumn.sortDescending();
        last = Double.POSITIVE_INFINITY;
        for (double n : numberColumn) {
            Assertions.assertTrue((n <= last));
            last = n;
        }
    }

    @Test
    public void testMaxAndMin() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.set(i, RandomUtils.nextDouble(0, 10000));
        }
        NumericColumn<?> doubles1 = doubles.top(50);
        NumericColumn<?> doubles2 = doubles.bottom(50);
        double[] doublesA = new double[50];
        double[] doublesB = new double[50];
        for (int i = 0; i < (doubles1.size()); i++) {
            doublesA[i] = doubles1.getDouble(i);
        }
        for (int i = 0; i < (doubles2.size()); i++) {
            doublesB[i] = doubles2.getDouble(i);
        }
        // the smallest item in the max set is >= the largest in the min set
        Assertions.assertTrue(((StatUtils.min(doublesA)) >= (StatUtils.max(doublesB))));
    }

    @Test
    public void testClear() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.set(i, RandomUtils.nextDouble(0, 10000));
        }
        Assertions.assertFalse(doubles.isEmpty());
        doubles.clear();
        Assertions.assertTrue(doubles.isEmpty());
    }

    @Test
    public void testCountMissing() {
        DoubleColumn doubles = DoubleColumn.create("doubles");
        for (int i = 0; i < 10; i++) {
            doubles.append(RandomUtils.nextDouble(0, 1000));
        }
        Assertions.assertEquals(0, doubles.countMissing());
        doubles.clear();
        for (int i = 0; i < 10; i++) {
            doubles.append(NumberColumnTest.MISSING);
        }
        Assertions.assertEquals(10, doubles.countMissing());
    }

    @Test
    public void testCountUnique() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 10);
        double[] uniques = new double[]{ 0.0F, 1.0E-8F, -1.0E-6F, 92923.3F, 24252, 23442.0F, 2252, 2342.0F };
        for (double unique : uniques) {
            doubles.append(unique);
        }
        Assertions.assertEquals(uniques.length, doubles.countUnique());
        doubles.clear();
        double[] notUniques = new double[]{ 0.0F, 1.0E-8F, -1.0E-6F, 92923.3F, 24252, 23442.0F, 2252, 2342.0F, 0.0F };
        for (double notUnique : notUniques) {
            doubles.append(notUnique);
        }
        Assertions.assertEquals(((notUniques.length) - 1), doubles.countUnique());
    }

    @Test
    public void testUnique() {
        DoubleColumn doubles = DoubleColumn.create("doubles");
        double[] uniques = new double[]{ 0.0F, 1.0E-8F, -1.0E-6F, 92923.3F, 24252, 23442.0F, 2252, 2342.0F };
        for (double unique : uniques) {
            doubles.append(unique);
        }
        Assertions.assertEquals(uniques.length, doubles.unique().size());
        doubles.clear();
        double[] notUniques = new double[]{ 0.0F, 1.0E-8F, -1.0E-6F, 92923.3F, 24252, 23442.0F, 2252, 2342.0F, 0.0F };
        for (double notUnique : notUniques) {
            doubles.append(notUnique);
        }
        Assertions.assertEquals(((notUniques.length) - 1), doubles.unique().size());
    }

    @Test
    public void testIsMissingAndIsNotMissing() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 10);
        for (int i = 0; i < 10; i++) {
            doubles.set(i, RandomUtils.nextDouble(0, 1000));
        }
        Assertions.assertEquals(0, doubles.isMissing().size());
        Assertions.assertEquals(10, doubles.isNotMissing().size());
        doubles.clear();
        for (int i = 0; i < 10; i++) {
            doubles.append(NumberColumnTest.MISSING);
        }
        Assertions.assertEquals(10, doubles.isMissing().size());
        Assertions.assertEquals(0, doubles.isNotMissing().size());
    }

    @Test
    public void testEmptyCopy() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10000));
        }
        DoubleColumn empty = doubles.emptyCopy();
        Assertions.assertTrue(empty.isEmpty());
        Assertions.assertEquals(doubles.name(), empty.name());
    }

    @Test
    public void appendObject() {
        DoubleColumn doubles = DoubleColumn.create("doubles");
        doubles.appendObj(BigDecimal.valueOf(1));
        Assertions.assertEquals(1.0, doubles.get(0), 1.0E-5);
    }

    @Test
    public void testSize() {
        DoubleColumn doubles = DoubleColumn.create("doubles");
        Assertions.assertEquals(0, doubles.size());
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10000));
        }
        Assertions.assertEquals(100, doubles.size());
        doubles.clear();
        Assertions.assertEquals(0, doubles.size());
    }

    @Test
    public void testType() {
        DoubleColumn doubles = DoubleColumn.create("doubles", 100);
        Assertions.assertEquals(DOUBLE, doubles.type());
    }

    @Test
    public void testDifference() {
        double[] originalValues = new double[]{ 32, 42, 40, 57, 52 };
        double[] expectedValues = new double[]{ NumberColumnTest.MISSING, 10, -2, 17, -5 };
        Assertions.assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    @Test
    public void testDifferenceMissingValuesInColumn() {
        double[] originalValues = new double[]{ 32, 42, NumberColumnTest.MISSING, 57, 52 };
        double[] expectedValues = new double[]{ NumberColumnTest.MISSING, 10, NumberColumnTest.MISSING, NumberColumnTest.MISSING, -5 };
        Assertions.assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    @Test
    public void testDifferenceEmptyColumn() {
        DoubleColumn initial = DoubleColumn.create("Test");
        DoubleColumn difference = initial.difference();
        Assertions.assertEquals(0, difference.size(), "Expecting empty data set.");
    }

    @Test
    public void testCumSum() {
        double[] originalValues = new double[]{ 32, 42, NumberColumnTest.MISSING, 57, 52, -10, 0 };
        double[] expectedValues = new double[]{ 32, 74, 74, 131, 183, 173, 173 };
        DoubleColumn initial = DoubleColumn.create("Test", originalValues);
        DoubleColumn csum = initial.cumSum();
        Assertions.assertEquals(expectedValues.length, csum.size(), "Both sets of data should be the same size.");
        for (int index = 0; index < (csum.size()); index++) {
            double actual = csum.get(index);
            Assertions.assertEquals(expectedValues[index], actual, 0, (("cumSum() operation at index:" + index) + " failed"));
        }
    }

    @Test
    public void testCumProd() {
        double[] originalValues = new double[]{ 1, 2, NumberColumnTest.MISSING, 3, 4 };
        double[] expectedValues = new double[]{ 1, 2, 2, 6, 24 };
        DoubleColumn initial = DoubleColumn.create("Test", originalValues);
        DoubleColumn cprod = initial.cumProd();
        Assertions.assertEquals(expectedValues.length, cprod.size(), "Both sets of data should be the same size.");
        for (int index = 0; index < (cprod.size()); index++) {
            double actual = cprod.get(index);
            Assertions.assertEquals(expectedValues[index], actual, 0, (("cumProd() operation at index:" + index) + " failed"));
        }
    }

    @Test
    public void testSubtract2Columns() {
        double[] col1Values = new double[]{ 32.5, NumberColumnTest.MISSING, 42, 57, 52 };
        double[] col2Values = new double[]{ 32, 42, 38.67, NumberColumnTest.MISSING, 52.01 };
        double[] expected = new double[]{ 0.5, NumberColumnTest.MISSING, 3.33, NumberColumnTest.MISSING, -0.01 };
        DoubleColumn col1 = DoubleColumn.create("1", col1Values);
        DoubleColumn col2 = DoubleColumn.create("2", col2Values);
        DoubleColumn difference = col1.subtract(col2);
        Assertions.assertTrue(validateEquality(expected, difference));
        // change order to verify size of returned column
        difference = col2.subtract(col1);
        expected = new double[]{ -0.5, NumberColumnTest.MISSING, -3.33, NumberColumnTest.MISSING, 0.01 };
        Assertions.assertTrue(validateEquality(expected, difference));
    }

    @Test
    public void testPctChange() {
        double[] originalValues = new double[]{ 10, 12, 13 };
        double[] expectedValues = new double[]{ NumberColumnTest.MISSING, 0.2, 0.083333 };
        DoubleColumn initial = DoubleColumn.create("Test", originalValues);
        DoubleColumn pctChange = initial.pctChange();
        Assertions.assertEquals(expectedValues.length, pctChange.size(), "Both sets of data should be the same size.");
        for (int index = 0; index < (pctChange.size()); index++) {
            double actual = pctChange.get(index);
            Assertions.assertEquals(expectedValues[index], actual, 1.0E-4, (("pctChange() operation at index:" + index) + " failed"));
        }
    }

    // Functional methods
    @Test
    public void testCountAtLeast() {
        Assertions.assertEquals(2, DoubleColumn.create("t1", new double[]{ 0, 1, 2 }).count(NumberColumnTest.isPositiveOrZeroD, 2));
        Assertions.assertEquals(0, DoubleColumn.create("t1", new double[]{ 0, 1, 2 }).count(NumberColumnTest.isNegativeD, 2));
    }

    @Test
    public void testCount() {
        Assertions.assertEquals(3, DoubleColumn.create("t1", new double[]{ 0, 1, 2 }).count(NumberColumnTest.isPositiveOrZeroD));
        Assertions.assertEquals(0, DoubleColumn.create("t1", new double[]{ 0, 1, 2 }).count(NumberColumnTest.isNegativeD));
    }

    @Test
    public void testAllMatch() {
        Assertions.assertTrue(DoubleColumn.create("t1", new double[]{ 0, 1, 2 }).allMatch(NumberColumnTest.isPositiveOrZeroD));
        Assertions.assertFalse(DoubleColumn.create("t1", new double[]{ -1, 0, 1 }).allMatch(NumberColumnTest.isPositiveOrZeroD));
        Assertions.assertFalse(DoubleColumn.create("t1", new double[]{ 1, 0, -1 }).allMatch(NumberColumnTest.isPositiveOrZeroD));
    }

    @Test
    public void testAnyMatch() {
        Assertions.assertTrue(DoubleColumn.create("t1", new double[]{ 0, 1, 2 }).anyMatch(NumberColumnTest.isPositiveOrZeroD));
        Assertions.assertTrue(DoubleColumn.create("t1", new double[]{ -1, 0, -1 }).anyMatch(NumberColumnTest.isPositiveOrZeroD));
        Assertions.assertFalse(DoubleColumn.create("t1", new double[]{ 0, 1, 2 }).anyMatch(NumberColumnTest.isNegativeD));
    }

    @Test
    public void noneMatch() {
        Assertions.assertTrue(DoubleColumn.create("t1", new double[]{ 0, 1, 2 }).noneMatch(NumberColumnTest.isNegativeD));
        Assertions.assertFalse(DoubleColumn.create("t1", new double[]{ -1, 0, 1 }).noneMatch(NumberColumnTest.isNegativeD));
        Assertions.assertFalse(DoubleColumn.create("t1", new double[]{ 1, 0, -1 }).noneMatch(NumberColumnTest.isNegativeD));
    }

    @Test
    public void testFilter() {
        Column<Double> filtered = DoubleColumn.create("t1", new double[]{ -1, 0, 1 }).filter(NumberColumnTest.isPositiveOrZeroD);
        check(filtered, 0.0, 1.0);
    }

    @Test
    public void testMapInto() {
        check(DoubleColumn.create("t1", new double[]{ -1, 0, 1 }).mapInto(NumberColumnTest.toStringD, StringColumn.create("result")), "-1.0", "0.0", "1.0");
    }

    @Test
    public void testMaxDoubleComparator() {
        Assertions.assertEquals(Double.valueOf(1.0), DoubleColumn.create("t1", new double[]{ -1, 0, 1 }).max(Double::compare).get());
        Assertions.assertFalse(DoubleColumn.create("t1").max(( d1, d2) -> ((int) (d1 - d2))).isPresent());
    }

    @Test
    public void testMinDoubleComparator() {
        Assertions.assertEquals(Double.valueOf((-1.0)), DoubleColumn.create("t1", new double[]{ -1, 0, 1 }).min(Double::compare).get());
        Assertions.assertFalse(DoubleColumn.create("t1").min(( d1, d2) -> ((int) (d1 - d2))).isPresent());
    }

    @Test
    public void testReduceTDoubleBinaryOperator() {
        Assertions.assertEquals(1.0, DoubleColumn.create("t1", new double[]{ -1, 0, 1 }).reduce(1.0, NumberColumnTest.sumD), 0.0);
    }

    @Test
    public void testReduceDoubleBinaryOperator() {
        Assertions.assertEquals(Double.valueOf(0.0), DoubleColumn.create("t1", new double[]{ -1, 0, 1 }).reduce(NumberColumnTest.sumD).get());
        Assertions.assertFalse(DoubleColumn.create("t1", new double[]{  }).reduce(NumberColumnTest.sumD).isPresent());
    }
}

