package water.udf;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import water.udf.specialized.Enums;
import water.util.fp.PureFunctions;


/**
 * Test for UDF
 */
public class SerializabilityTest extends UdfTestBase {
    @Test
    public void testDoubleColumnSerializable() throws Exception {
        Column<Double> c = someDoubles();
        checkSerialization(c);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDateColumnSerializable() throws Exception {
        Column<Date> c = UdfTestBase.willDrop(Dates.newColumn(7, new water.util.fp.Function<Long, Date>() {
            public Date apply(Long i) {
                return new Date(((i * 3600000L) * 24));
            }
        }));
        checkSerialization(c);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStringColumnSerializable() throws Exception {
        Column<String> c = UdfTestBase.willDrop(Strings.newColumn(7, new water.util.fp.Function<Long, String>() {
            public String apply(Long i) {
                return ("<<" + i) + ">>";
            }
        }));
        checkSerialization(c);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEnumColumnSerializable() throws Exception {
        Column<Integer> c = UdfTestBase.willDrop(Enums.enums(new String[]{ "Red", "White", "Blue" }).newColumn(7, new water.util.fp.Function<Long, Integer>() {
            public Integer apply(Long i) {
                return ((int) (i % 3));
            }
        }));
        checkSerialization(c);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void tesFunColumnSerializable() throws Exception {
        Column<Double> source = someDoubles();
        Column<Double> c = UdfTestBase.willDrop(new FunColumn(PureFunctions.SQUARE, source));
        checkSerialization(c);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void tesFun2ColumnSerializable() throws Exception {
        Column<Double> x = someDoubles();
        Column<Double> c = UdfTestBase.willDrop(new Fun2Column(PureFunctions.PLUS, x, x));
        checkSerialization(c);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void tesFun3ColumnSerializable() throws Exception {
        Column<Double> x = someDoubles();
        Column<Double> y = someDoubles();
        Column<Double> c = UdfTestBase.willDrop(new Fun3Column(PureFunctions.X2_PLUS_Y2_PLUS_Z2, x, y, x));
        checkSerialization(c);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void tesFoldingSerializable() throws Exception {
        Column<Double> x = someDoubles();
        Column<Double> y = someDoubles();
        Column<Double> c = UdfTestBase.willDrop(new FoldingColumn(PureFunctions.SUM_OF_SQUARES, x, y, x));
        checkSerialization(c);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnfoldingColumnSerializable() throws Exception {
        Column<String> source = UdfTestBase.willDrop(Strings.newColumn(Arrays.asList("line 1; line 2; lin 3".split("; "))));
        // produce another (virtual) column that stores a list of strings as a row value
        Column<List<String>> c = new UnfoldingColumn(PureFunctions.splitBy(","), source, 10);
        checkSerialization(c);
    }
}

