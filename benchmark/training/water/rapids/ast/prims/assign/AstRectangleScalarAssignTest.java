package water.rapids.ast.prims.assign;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import water.TestUtil;
import water.fvec.Frame;
import water.rapids.Rapids;
import water.rapids.Val;
import water.rapids.vals.ValFrame;


@RunWith(Parameterized.class)
public class AstRectangleScalarAssignTest extends TestUtil {
    @Parameterized.Parameter
    public int _nRows;

    @Parameterized.Parameter(1)
    public int _offset;

    @Parameterized.Parameter(2)
    public String _description;// not used


    @Test
    public void testEnumAssign() throws Exception {
        final Frame data = makeTestFrame();
        Frame output = null;
        try {
            String[] expectedCats = AstRecAssignTestUtils.catVec2array(data.vec(2));
            for (int i = 0; i < (_nRows); i++)
                expectedCats[(i + (_offset))] = "c2";

            String rapids = ((("(tmp= tst (:= data \"c2\" [2] [" + (_offset)) + ":") + (_nRows)) + "]))";
            Val val = Rapids.exec(rapids);
            output = val.getFrame();
            String[] actualCats = AstRecAssignTestUtils.catVec2array(output.vec(2));
            Assert.assertArrayEquals(expectedCats, actualCats);
        } finally {
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }

    @Test
    public void testInvalidEnumAssign() throws Exception {
        final Frame data = makeTestFrame();
        Frame output = null;
        try {
            String rapids = ((("(tmp= tst (:= data \"Invalid\" [2] [" + (_offset)) + "") + (_nRows)) + "]))";
            Val val = Rapids.exec(rapids);
            if (val instanceof ValFrame)
                output = val.getFrame();

            Assert.fail("Invalid categorical value shouldn't be assigned");
        } catch (Exception e) {
            Assert.assertEquals("Cannot assign value Invalid into a vector of type Enum.", e.getMessage());
        } finally {
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }

    @Test
    public void testStringAssign() throws Exception {
        final Frame data = makeTestFrame();
        Frame output = null;
        try {
            String[] expectedStrs = AstRecAssignTestUtils.strVec2array(data.vec(1));
            for (int i = 0; i < (_nRows); i++)
                expectedStrs[(i + (_offset))] = "New Value";

            String rapids = ((("(tmp= tst (:= data \"New Value\" [1] [" + (_offset)) + ":") + (_nRows)) + "]))";
            Val val = Rapids.exec(rapids);
            output = val.getFrame();
            String[] actualStrs = AstRecAssignTestUtils.strVec2array(output.vec(1));
            Assert.assertArrayEquals(expectedStrs, actualStrs);
        } finally {
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }

    @Test
    public void testNumberAssign() throws Exception {
        final Frame data = makeTestFrame();
        Frame output = null;
        try {
            double[] expected = AstRecAssignTestUtils.vec2array(data.vec(0));
            for (int i = 0; i < (_nRows); i++)
                expected[(i + (_offset))] = 42.0;

            String rapids = ((("(tmp= tst (:= data 42.0 [0] [" + (_offset)) + ":") + (_nRows)) + "]))";
            Val val = Rapids.exec(rapids);
            output = val.getFrame();
            double[] actual = AstRecAssignTestUtils.vec2array(output.vec(0));
            Assert.assertArrayEquals(expected, actual, 1.0E-4);
        } finally {
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }

    @Test
    public void testNAAssign() throws Exception {
        final Frame data = makeTestFrame();
        Frame output = null;
        try {
            double[] expectedNums = AstRecAssignTestUtils.vec2array(data.vec(0));
            String[] expectedCats = AstRecAssignTestUtils.catVec2array(data.vec(2));
            for (int i = 0; i < (_nRows); i++) {
                expectedNums[(i + (_offset))] = Double.NaN;
                expectedCats[(i + (_offset))] = null;
            }
            String rapids = ((("(tmp= tst (:= data NA [0,2] [" + (_offset)) + ":") + (_nRows)) + "]))";
            Val val = Rapids.exec(rapids);
            output = val.getFrame();
            double[] actualNums = AstRecAssignTestUtils.vec2array(output.vec(0));
            Assert.assertArrayEquals(expectedNums, actualNums, 1.0E-4);
            String[] actualCats = AstRecAssignTestUtils.catVec2array(output.vec(2));
            Assert.assertArrayEquals(expectedCats, actualCats);
        } finally {
            data.delete();
            if (output != null) {
                output.delete();
            }
        }
    }
}

