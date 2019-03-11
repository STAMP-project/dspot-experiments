package water.rapids.ast.prims.assign;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import water.TestUtil;
import water.rapids.Rapids;
import water.rapids.Val;
import water.rapids.vals.ValFrame;


@RunWith(Parameterized.class)
public class AstRectangleFrameSliceAssignTest extends TestUtil {
    @Parameterized.Parameter
    public int _nRows;

    @Test
    public void testAssignFrameSlice() throws Exception {
        final Frame data = TestUtil.parse_test_file(Key.make("data"), "smalldata/airlines/allyears2k_headers.zip");
        Frame output = null;
        try {
            String rapids = ((("(tmp= tst (:= data (rows (cols data [8.0, 11.0] ) [10000.0:" + (_nRows)) + ".0] ) [8.0, 11.0] [0.0:") + (_nRows)) + ".0] ) )";
            Val val = Rapids.exec(rapids);
            if (val instanceof ValFrame) {
                output = val.getFrame();
                // categorical column
                String[] expectedCats = AstRecAssignTestUtils.catVec2array(data.vec(8));
                System.arraycopy(expectedCats, 10000, expectedCats, 0, _nRows);
                String[] actualCats = AstRecAssignTestUtils.catVec2array(output.vec(8));
                Assert.assertArrayEquals(expectedCats, actualCats);
                // numerical column
                double[] expected = AstRecAssignTestUtils.vec2array(data.vec(11));
                System.arraycopy(expected, 10000, expected, 0, _nRows);
                double[] actual = AstRecAssignTestUtils.vec2array(output.vec(11));
                Assert.assertArrayEquals(expected, actual, 1.0E-4);
            }
        } finally {
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }

    @Test
    public void testAssignFrameSlice_domainsDiffer() throws Exception {
        final Frame data = TestUtil.parse_test_file(Key.make("data"), "smalldata/airlines/allyears2k_headers.zip");
        Frame output = null;
        try {
            String rapids = ((("(tmp= tst (:= data (rows (cols data [8.0] ) [10000.0:" + (_nRows)) + ".0] ) [16.0] [0.0:") + (_nRows)) + ".0] ) )";
            Val val = Rapids.exec(rapids);
            if (val instanceof ValFrame)
                output = val.getFrame();

            Assert.fail("No exception was thrown, IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Cannot assign to a categorical column with a different domain; source column UniqueCarrier, target column Origin", e.getMessage());
        } finally {
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }

    @Test
    public void testAssignFrameSlice_strings() throws Exception {
        Vec strVec = AstRecAssignTestUtils.seqStrVec(10000, 5, 7, 8, 10000);
        final Frame data = new Frame(Key.<Frame>make("data"), null, new Vec[]{ strVec });
        DKV.put(data._key, data);
        Frame output = null;
        try {
            String rapids = ((("(tmp= tst (:= data (rows (cols data [0.0] ) [10000.0:" + (_nRows)) + ".0] ) [0.0] [0.0:") + (_nRows)) + ".0] ) )";
            Val val = Rapids.exec(rapids);
            if (val instanceof ValFrame) {
                output = val.getFrame();
                String[] expected = AstRecAssignTestUtils.strVec2array(strVec);
                System.arraycopy(expected, 10000, expected, 0, _nRows);
                String[] actual = AstRecAssignTestUtils.strVec2array(output.vec(0));
                Assert.assertArrayEquals(expected, actual);
            }
        } finally {
            strVec.remove();
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }
}

