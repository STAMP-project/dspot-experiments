package water.udf;


import Predicate.NOT_NULL;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import water.udf.specialized.Enums;
import water.util.StringUtils;
import water.util.fp.PureFunctions;


/**
 * Test for UDF
 */
public class UdfTest extends UdfTestBase {
    @Test
    public void testIsNA() throws Exception {
        Column<Double> c = sines();
        Assert.assertFalse(c.apply(10).isNaN());
        Double x11 = c.apply(11);
        Assert.assertTrue(x11.isNaN());
        Assert.assertTrue(c.apply(19).isNaN());
        Assert.assertFalse(c.apply(20).isNaN());
        Assert.assertFalse(c.isNA(10));
        Assert.assertTrue(c.isNA(11));
        Assert.assertTrue(c.isNA(19));
        Assert.assertFalse(c.isNA(20));
    }

    @Test
    public void testOfDoubles() throws Exception {
        Column<Double> c = five_x();
        Assert.assertEquals(0.0, c.apply(0), 1.0E-6);
        Assert.assertEquals(210.0, c.apply(42), 1.0E-6);
        Assert.assertEquals(100000.0, c.apply(20000), 1.0E-6);
    }

    @Test
    public void testOfStrings() throws Exception {
        Column<String> c = UdfTestBase.willDrop(Strings.newColumn((1 << 20), new water.util.fp.Function<Long, String>() {
            public String apply(Long i) {
                return i == 42 ? null : ("<<" + i) + ">>";
            }
        }));
        Assert.assertEquals("<<0>>", c.apply(0));
        Assert.assertEquals(null, c.apply(42));
        Assert.assertEquals("<<2016>>", c.apply(2016));
        Column<String> materialized = Strings.materialize(c);
        for (int i = 0; i < 100000; i++) {
            Assert.assertEquals(c.apply(i), materialized.apply(i));
        }
    }

    @Test
    public void testOfEnums() throws Exception {
        Column<Integer> c = UdfTestBase.willDrop(Enums.enums(new String[]{ "Red", "White", "Blue" }).newColumn((1 << 20), new water.util.fp.Function<Long, Integer>() {
            public Integer apply(Long i) {
                return ((int) (i % 3));
            }
        }));
        Assert.assertEquals(0, c.apply(0).intValue());
        Assert.assertEquals(0, c.apply(42).intValue());
        Assert.assertEquals(1, c.apply(100).intValue());
        Assert.assertEquals(2, c.apply(20000).intValue());
        Column<Integer> materialized = Enums.enums(new String[]{ "Red", "White", "Blue" }).materialize(c);
        for (int i = 0; i < 100000; i++) {
            Assert.assertEquals(c.apply(i), materialized.apply(i));
        }
    }

    @Test
    public void testOfDates() throws Exception {
        Column<Date> c = UdfTestBase.willDrop(Dates.newColumn((1 << 20), new water.util.fp.Function<Long, Date>() {
            public Date apply(Long i) {
                return new Date(((i * 3600000L) * 24));
            }
        }));
        Assert.assertEquals(new Date(0), c.apply(0));
        Assert.assertEquals(new Date((((258 * 24) * 3600) * 1000L)), c.apply(258));
        Column<Date> materialized = Dates.materialize(c);
        for (int i = 0; i < 100000; i++) {
            Assert.assertEquals(c.apply(i), materialized.apply(i));
        }
    }

    // // All UUID functionality is currently disabled
    // @Test
    // public void testOfUUIDs() throws Exception {
    // Column<UUID> c = willDrop(UUIDs.newColumn(1 << 20, new Function<Long, UUID>() {
    // public UUID apply(Long i) {
    // return new UUID(i * 7, i * 13);
    // }
    // }));
    // assertEquals(new UUID(0, 0), c.apply(0));
    // assertEquals(new UUID(258*7, 258*13), c.apply(258));
    // 
    // Column<UUID> materialized = UUIDs.materialize(c);
    // 
    // for (int i = 0; i < 100000; i++) {
    // assertEquals(c.apply(i), materialized.apply(i));
    // }
    // }
    @Test
    public void testOfEnumFun() throws Exception {
        final String[] domain = new String[]{ "Red", "White", "Blue" };
        Column<Integer> x = UdfTestBase.willDrop(Enums.enums(domain).newColumn((1 << 20), new water.util.fp.Function<Long, Integer>() {
            public Integer apply(Long i) {
                return ((int) (i % 3));
            }
        }));
        Column<String> y = new FunColumn(new water.util.fp.Function<Integer, String>() {
            public String apply(Integer i) {
                return domain[i];
            }
        }, x);
        Assert.assertEquals("Red", y.apply(0));
        Assert.assertEquals("Red", y.apply(42));
        Assert.assertEquals("White", y.apply(100));
        Assert.assertEquals("Blue", y.apply(20000));
    }

    @Test
    public void testOfSquares() throws Exception {
        Column<Double> x = five_x();
        Column<Double> y = new FunColumn(PureFunctions.SQUARE, x);
        Assert.assertEquals(0.0, y.apply(0), 1.0E-6);
        Assert.assertEquals(44100.0, y.apply(42), 1.0E-6);
        Assert.assertEquals(1.0E10, y.apply(20000), 1.0E-6);
    }

    @Test
    public void testIsFunNA() throws Exception {
        Column<Double> x = sines();
        Column<Double> y = new FunColumn(PureFunctions.SQUARE, x);
        Assert.assertFalse(y.isNA(10));
        Assert.assertTrue(y.isNA(11));
        Assert.assertTrue(y.isNA(19));
        Assert.assertFalse(y.isNA(20));
        Assert.assertEquals(0.295958969093304, y.apply(10), 1.0E-4);
    }

    @Test
    public void testFun2() throws Exception {
        Column<Double> x = five_x();
        Column<Double> y = sines();
        Column<Double> y2 = UdfTestBase.willDrop(new FunColumn(PureFunctions.SQUARE, y));
        Column<Double> z1 = UdfTestBase.willDrop(new Fun2Column(PureFunctions.PLUS, x, y2));
        Column<Double> z2 = UdfTestBase.willDrop(new Fun2Column(PureFunctions.X2_PLUS_Y2, x, y));
        Assert.assertEquals(0.0, z1.apply(0), 1.0E-6);
        Assert.assertEquals(210.84001174779368, z1.apply(42), 1.0E-6);
        Assert.assertEquals(100000.3387062632, z1.apply(20000), 1.0E-6);
        Assert.assertEquals(0.0, z2.apply(0), 1.0E-6);
        Assert.assertEquals(44100.840011747794, z2.apply(42), 1.0E-6);
        Assert.assertEquals(1.0000000000338707E10, z2.apply(20000), 1.0E-6);
        Column<Double> materialized = UdfTestBase.willDrop(Doubles.materialize(z2));
        for (int i = 0; i < 100000; i++) {
            Double expected = z2.apply(i);
            Assert.assertTrue(((z2.isNA(i)) == (materialized.isNA(i))));
            // the following exposes a problem. nulls being returned.
            if (expected == null)
                Assert.assertTrue((("At " + i) + ":"), materialized.isNA(i));

            Double actual = materialized.apply(i);
            if (!(z2.isNA(i)))
                Assert.assertEquals(expected, actual, 1.0E-4);

        }
    }

    @Test
    public void testFun2Compatibility() throws Exception {
        Column<Double> x = five_x();
        Column<Double> y = sinesShort();
        Column<Double> z = UdfTestBase.willDrop(Doubles.newColumn((1 << 20), new water.util.fp.Function<Long, Double>() {
            public Double apply(Long i) {
                return Math.sin((i * 1.0E-4));
            }
        }));
        try {
            Column<Double> z1 = new Fun2Column(PureFunctions.PLUS, x, y);
            Assert.fail("Column incompatibility should be detected");
        } catch (AssertionError ae) {
            // as designed
        }
        try {
            Column<Double> r = new Fun3Column(PureFunctions.X2_PLUS_Y2_PLUS_Z2, x, y, z);
            Assert.fail("Column incompatibility should be detected");
        } catch (AssertionError ae) {
            // as designed
        }
        try {
            Column<Double> r = new Fun3Column(PureFunctions.X2_PLUS_Y2_PLUS_Z2, x, z, y);
            Assert.fail("Column incompatibility should be detected");
        } catch (AssertionError ae) {
            // as designed
        }
    }

    @Test
    public void testFun2CompatibilityWithConst() throws Exception {
        Column<Double> x = five_x();
        Column<Double> y = Doubles.constColumn(42.0, (1 << 20));
        Column<Double> z = UdfTestBase.willDrop(Doubles.newColumn((1 << 20), new water.util.fp.Function<Long, Double>() {
            public Double apply(Long i) {
                return Math.sin((i * 1.0E-4));
            }
        }));
        try {
            Column<Double> z1 = new Fun2Column(PureFunctions.PLUS, x, y);
            Assert.fail("Column incompatibility should be detected");
        } catch (AssertionError ae) {
            // as designed
        }
        try {
            Column<Double> r = new Fun3Column(PureFunctions.X2_PLUS_Y2_PLUS_Z2, x, y, z);
            Assert.fail("Column incompatibility should be detected");
        } catch (AssertionError ae) {
            // as designed
        }
        try {
            Column<Double> r = new Fun3Column(PureFunctions.X2_PLUS_Y2_PLUS_Z2, x, z, y);
            Assert.fail("Column incompatibility should be detected");
        } catch (AssertionError ae) {
            // as designed
        }
    }

    @Test
    public void testFun3() throws Exception {
        Column<Double> x = UdfTestBase.willDrop(Doubles.newColumn((1 << 20), new water.util.fp.Function<Long, Double>() {
            public Double apply(Long i) {
                return (Math.cos((i * 1.0E-4))) * (Math.cos((i * 1.0E-7)));
            }
        }));
        Column<Double> y = UdfTestBase.willDrop(Doubles.newColumn((1 << 20), new water.util.fp.Function<Long, Double>() {
            public Double apply(Long i) {
                return (Math.cos((i * 1.0E-4))) * (Math.sin((i * 1.0E-7)));
            }
        }));
        Column<Double> z = UdfTestBase.willDrop(Doubles.newColumn((1 << 20), new water.util.fp.Function<Long, Double>() {
            public Double apply(Long i) {
                return Math.sin((i * 1.0E-4));
            }
        }));
        Column<Double> r = new Fun3Column(PureFunctions.X2_PLUS_Y2_PLUS_Z2, x, y, z);
        for (int i = 0; i < 100000; i++) {
            Assert.assertEquals(1.0, r.apply((i * 10)), 1.0E-4);
        }
        Column<Double> materialized = Doubles.materialize(r);
        for (int i = 0; i < 100000; i++) {
            Assert.assertEquals(r.apply(i), materialized.apply(i), 1.0E-4);
        }
    }

    @Test
    public void testFoldingColumn() throws Exception {
        Column<Double> x = UdfTestBase.willDrop(Doubles.newColumn((1 << 20), new water.util.fp.Function<Long, Double>() {
            public Double apply(Long i) {
                return (Math.cos((i * 1.0E-4))) * (Math.cos((i * 1.0E-7)));
            }
        }));
        Column<Double> y = UdfTestBase.willDrop(Doubles.newColumn((1 << 20), new water.util.fp.Function<Long, Double>() {
            public Double apply(Long i) {
                return (Math.cos((i * 1.0E-4))) * (Math.sin((i * 1.0E-7)));
            }
        }));
        Column<Double> z = UdfTestBase.willDrop(Doubles.newColumn((1 << 20), new water.util.fp.Function<Long, Double>() {
            public Double apply(Long i) {
                return Math.sin((i * 1.0E-4));
            }
        }));
        Column<Double> r = new FoldingColumn(PureFunctions.SUM_OF_SQUARES, x, y, z);
        for (int i = 0; i < 100000; i++) {
            Assert.assertEquals(1.0, r.apply((i * 10)), 1.0E-4);
        }
        Column<Double> x1 = new FoldingColumn(PureFunctions.SUM_OF_SQUARES, x);
        for (int i = 0; i < 100000; i++) {
            double xi = x.apply(i);
            Assert.assertEquals((xi * xi), x1.apply(i), 1.0E-4);
        }
        try {
            Column<Double> x0 = new FoldingColumn(PureFunctions.SUM_OF_SQUARES);
            Assert.fail("This should have failed - no empty foldings");
        } catch (AssertionError ae) {
            // good, good!
        }
        Column<Double> materialized = Doubles.materialize(r);
        for (int i = 0; i < 100000; i++) {
            Assert.assertEquals(r.apply(i), materialized.apply(i), 1.0E-4);
        }
    }

    @Test
    public void testFoldingColumnCompatibility() throws Exception {
        Column<Double> x = UdfTestBase.willDrop(Doubles.newColumn((1 << 20), new water.util.fp.Function<Long, Double>() {
            public Double apply(Long i) {
                return (Math.cos((i * 1.0E-4))) * (Math.cos((i * 1.0E-7)));
            }
        }));
        Column<Double> y = UdfTestBase.willDrop(Doubles.newColumn((1 << 20), new water.util.fp.Function<Long, Double>() {
            public Double apply(Long i) {
                return (Math.cos((i * 1.0E-4))) * (Math.sin((i * 1.0E-7)));
            }
        }));
        Column<Double> z = sinesShort();
        try {
            Column<Double> r = new FoldingColumn(PureFunctions.SUM_OF_SQUARES, x, y, z);
            Assert.fail("Should have failed on incompatibility");
        } catch (AssertionError ae) {
            // as expected
        }
    }

    // test how file can be unfolded into multiple columns
    @Test
    public void testUnfoldingColumn() throws IOException {
        // here's the file
        File file = getFile("smalldata/chicago/chicagoAllWeather.csv");
        // get all its lines
        final List<String> lines = Files.readLines(file, Charset.defaultCharset());
        // store it in H2O, with typed column as a wrapper (core H2O storage is a type-unaware Vec class)
        Column<String> source = UdfTestBase.willDrop(Strings.newColumn(lines));
        // produce another (virtual) column that stores a list of strings as a row value
        Column<List<String>> split = new UnfoldingColumn(PureFunctions.splitBy(","), source, 10);
        // now check that we have the right data
        for (int i = 0; i < (lines.size()); i++) {
            // since we specified width (10), the rest of the list is filled with nulls; have to ignore them.
            // It's important to have the same width for the whole frame.
            String actual = StringUtils.join(" ", NOT_NULL.filter(split.apply(i)));
            // so, have we lost any data?
            Assert.assertEquals(lines.get(i).replaceAll("\\,", " ").trim(), actual);
        }
    }

    @Test
    public void testUnfoldingFrame() throws IOException {
        File file = getFile("smalldata/chicago/chicagoAllWeather.csv");
        final List<String> lines = Files.readLines(file, Charset.defaultCharset());
        Column<String> source = UdfTestBase.willDrop(Strings.newColumn(lines));
        Column<List<String>> split = new UnfoldingColumn(PureFunctions.splitBy(","), source, 10);
        UnfoldingFrame<String> frame = new UnfoldingFrame(Strings, split.size(), split, 11);
        List<DataColumn<String>> columns = frame.materialize();
        for (int i = 0; i < (lines.size()); i++) {
            List<String> fromColumns = new ArrayList<>(10);
            for (int j = 0; j < 10; j++) {
                String value = columns.get(j).get(i);
                if (value != null)
                    fromColumns.add(value);

            }
            String actual = StringUtils.join(" ", fromColumns);
            Assert.assertEquals(lines.get(i).replaceAll("\\,", " ").trim(), actual);
        }
        Assert.assertTrue("Need to align the result", columns.get(5).isCompatibleWith(source));
    }
}

