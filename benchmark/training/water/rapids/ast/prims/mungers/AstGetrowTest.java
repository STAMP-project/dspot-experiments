package water.rapids.ast.prims.mungers;


import Vec.T_CAT;
import Vec.T_STR;
import Vec.T_TIME;
import Vec.T_UUID;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;
import water.rapids.Rapids;
import water.rapids.Val;
import water.rapids.vals.ValRow;
import water.util.ArrayUtils;


/**
 *
 */
public class AstGetrowTest extends TestUtil {
    /**
     * Test that in normal case the result has the correct type and value.
     */
    @Test
    public void TestGetrow() {
        Frame f = null;
        try {
            f = ArrayUtils.frame(TestUtil.ar("A", "B", "C", "D", "E"), TestUtil.ard(1.0, (-3), 12, 1000000, Double.NaN));
            Val v = Rapids.exec((("(getrow " + (f._key)) + ")"));
            Assert.assertTrue((v instanceof ValRow));
            double[] row = v.getRow();
            Assert.assertEquals(row.length, 5);
            Assert.assertArrayEquals(TestUtil.ard(1.0, (-3), 12, 1000000, Double.NaN), row, 1.0E-8);
        } finally {
            if (f != null)
                f.delete();

        }
    }

    /**
     * Test that an exception is thrown when number of rows in the frame is > 1.
     */
    @Test
    public void TestGetrow2() {
        Frame f = null;
        try {
            f = ArrayUtils.frame(TestUtil.ard((-3), 4), TestUtil.ard(0, 1));
            Val v2 = null;
            try {
                v2 = Rapids.exec((("(getrow " + (f._key)) + ")"));
            } catch (IllegalArgumentException ignored) {
            }
            Assert.assertNull("getrow is allowed only for single-row frames", v2);
        } finally {
            if (f != null)
                f.delete();

        }
    }

    /**
     * Test columns of various types
     */
    @Test
    public void TestGetrow3() {
        Frame f = null;
        Vec[] vv = null;
        try {
            f = ArrayUtils.frame(TestUtil.ar("D1", "D2"), TestUtil.ard(0, 1));
            vv = f.vec(0).makeCons(5, 0, TestUtil.ar(TestUtil.ar("N", "Y"), TestUtil.ar("a", "b", "c"), null, null, null), TestUtil.ar(T_CAT, T_CAT, T_TIME, T_STR, T_UUID));
            f.add(TestUtil.ar("C1", "C2", "T1", "S1", "U1"), vv);
            Val v = Rapids.exec((("(getrow " + (f._key)) + ")"));
            Assert.assertTrue((v instanceof ValRow));
            double[] row = v.getRow();
            Assert.assertEquals(7, row.length);
            Assert.assertArrayEquals(TestUtil.ard(0, 1, Double.NaN, Double.NaN, 0, Double.NaN, Double.NaN), row, 1.0E-8);
        } finally {
            if (f != null)
                f.delete();

            if (vv != null)
                for (Vec v : vv)
                    v.remove();


        }
    }
}

