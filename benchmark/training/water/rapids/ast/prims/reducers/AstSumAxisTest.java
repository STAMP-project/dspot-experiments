package water.rapids.ast.prims.reducers;


import Vec.T_NUM;
import Vec.T_TIME;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import water.Key;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;
import water.rapids.Rapids;
import water.rapids.Val;
import water.rapids.vals.ValFrame;


/**
 * Test the AstSumAxis.java class
 */
public class AstSumAxisTest extends TestUtil {
    private static Vec vi1;

    private static Vec vd1;

    private static Vec vd2;

    private static Vec vd3;

    private static Vec vs1;

    private static Vec vt1;

    private static Vec vt2;

    private static Vec vc1;

    private static Vec vc2;

    private static ArrayList<Frame> allFrames;

    // --------------------------------------------------------------------------------------------------------------------
    // Tests
    // --------------------------------------------------------------------------------------------------------------------
    @Test
    public void testAstSumGeneralStructure() {
        AstSumAxis a = new AstSumAxis();
        String[] args = a.args();
        Assert.assertEquals(3, args.length);
        String example = a.example();
        Assert.assertTrue(example.startsWith("(sumaxis "));
        String description = a.description();
        Assert.assertTrue("Description for AstSum is too short", ((description.length()) > 100));
    }

    @Test
    public void testColumnwisesumWithoutNaRm() {
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("I", "D", "DD", "DN", "T", "S", "C"), TestUtil.aro(AstSumAxisTest.vi1, AstSumAxisTest.vd1, AstSumAxisTest.vd2, AstSumAxisTest.vd3, AstSumAxisTest.vt1, AstSumAxisTest.vs1, AstSumAxisTest.vc2)));
        Val val1 = Rapids.exec((("(sumaxis " + (fr._key)) + " 0 0)"));
        Assert.assertTrue((val1 instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val1.getFrame());
        Assert.assertArrayEquals(fr.names(), res.names());
        Assert.assertArrayEquals(TestUtil.ar(T_NUM, T_NUM, T_NUM, T_NUM, T_TIME, T_NUM, T_NUM), res.types());
        AstSumAxisTest.assertRowFrameEquals(TestUtil.ard(0.0, 20.0, 3.0, Double.NaN, 5.000015E7, Double.NaN, Double.NaN), res);
    }

    @Test
    public void testColumnwiseSumWithNaRm() {
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("I", "D", "DD", "DN", "T", "S", "C"), TestUtil.aro(AstSumAxisTest.vi1, AstSumAxisTest.vd1, AstSumAxisTest.vd2, AstSumAxisTest.vd3, AstSumAxisTest.vt1, AstSumAxisTest.vs1, AstSumAxisTest.vc2)));
        Val val = Rapids.exec((("(sumaxis " + (fr._key)) + " 1 0)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val.getFrame());
        Assert.assertArrayEquals(fr.names(), res.names());
        Assert.assertArrayEquals(TestUtil.ar(T_NUM, T_NUM, T_NUM, T_NUM, T_TIME, T_NUM, T_NUM), res.types());
        AstSumAxisTest.assertRowFrameEquals(TestUtil.ard(0.0, 20.0, 3.0, 6.0, 5.000015E7, Double.NaN, Double.NaN), res);
    }

    @Test
    public void testColumnwisesumOnEmptyFrame() {
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make()));
        Val val = Rapids.exec((("(sumaxis " + (fr._key)) + " 0 0)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val.getFrame());
        Assert.assertEquals(res.numCols(), 0);
        Assert.assertEquals(res.numRows(), 0);
    }

    @Test
    public void testColumnwisesumBinaryVec() {
        Assert.assertTrue(((AstSumAxisTest.vc1.isBinary()) && (!(AstSumAxisTest.vc2.isBinary()))));
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("C1", "C2"), TestUtil.aro(AstSumAxisTest.vc1, AstSumAxisTest.vc2)));
        Val val = Rapids.exec((("(sumaxis " + (fr._key)) + " 1 0)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val.getFrame());
        Assert.assertArrayEquals(fr.names(), res.names());
        Assert.assertArrayEquals(TestUtil.ar(T_NUM, T_NUM), res.types());
        AstSumAxisTest.assertRowFrameEquals(TestUtil.ard(3.0, Double.NaN), res);
    }

    @Test
    public void testRowwisesumWithoutNaRm() {
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("i1", "d1", "d2", "d3"), TestUtil.aro(AstSumAxisTest.vi1, AstSumAxisTest.vd1, AstSumAxisTest.vd2, AstSumAxisTest.vd3)));
        Val val = Rapids.exec((("(sumaxis " + (fr._key)) + " 0 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val.getFrame());
        AstSumAxisTest.assertColFrameEquals(TestUtil.ard(1.7, 2.9, Double.NaN, 10.3, Double.NaN), res);
        Assert.assertEquals("sum", res.name(0));
    }

    @Test
    public void testRowwisesumWithoutNaRmAndNonnumericColumn() {
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("i1", "d1", "d2", "d3", "s1"), TestUtil.aro(AstSumAxisTest.vi1, AstSumAxisTest.vd1, AstSumAxisTest.vd2, AstSumAxisTest.vd3, AstSumAxisTest.vs1)));
        Val val = Rapids.exec((("(sumaxis " + (fr._key)) + " 0 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val.getFrame());
        AstSumAxisTest.assertColFrameEquals(TestUtil.ard(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN), res);
        Assert.assertEquals("sum", res.name(0));
    }

    @Test
    public void testRowwisesumWithNaRm() {
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("i1", "d1", "d2", "d3", "s1"), TestUtil.aro(AstSumAxisTest.vi1, AstSumAxisTest.vd1, AstSumAxisTest.vd2, AstSumAxisTest.vd3, AstSumAxisTest.vs1)));
        Val val = Rapids.exec((("(sumaxis " + (fr._key)) + " 1 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val.getFrame());
        Assert.assertEquals("Unexpected column name", "sum", res.name(0));
        Assert.assertEquals("Unexpected column type", T_NUM, res.types()[0]);
        AstSumAxisTest.assertColFrameEquals(TestUtil.ard(1.7, 2.9, 4.1, 10.3, 10.0), res);
    }

    @Test
    public void testRowwisesumOnFrameWithTimeColumnsOnly() {
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("t1", "s", "t2"), TestUtil.aro(AstSumAxisTest.vt1, AstSumAxisTest.vs1, AstSumAxisTest.vt2)));
        Val val = Rapids.exec((("(sumaxis " + (fr._key)) + " 1 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val.getFrame());
        Assert.assertEquals("Unexpected column name", "sum", res.name(0));
        Assert.assertEquals("Unexpected column type", T_TIME, res.types()[0]);
        AstSumAxisTest.assertColFrameEquals(TestUtil.ard(30000000, 30000040, 30000060, 30000080, 30000120), res);
    }

    @Test
    public void testRowwisesumOnFrameWithTimeandNumericColumn() {
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("t1", "i1"), TestUtil.aro(AstSumAxisTest.vt1, AstSumAxisTest.vi1)));
        Val val = Rapids.exec((("(sumaxis " + (fr._key)) + " 1 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val.getFrame());
        AstSumAxisTest.assertColFrameEquals(TestUtil.ard((-1), (-2), 0, 2, 1), res);
    }

    @Test
    public void testRowwisesumOnEmptyFrame() {
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make()));
        Val val = Rapids.exec((("(sumaxis " + (fr._key)) + " 0 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val.getFrame());
        Assert.assertEquals(res.numCols(), 0);
        Assert.assertEquals(res.numRows(), 0);
    }

    @Test
    public void testRowwisesumOnFrameWithNonnumericColumnsOnly() {
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("c1", "s1"), TestUtil.aro(AstSumAxisTest.vc2, AstSumAxisTest.vs1)));
        Val val = Rapids.exec((("(sumaxis " + (fr._key)) + " 1 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val.getFrame());
        Assert.assertEquals("Unexpected column name", "sum", res.name(0));
        Assert.assertEquals("Unexpected column type", T_NUM, res.types()[0]);
        AstSumAxisTest.assertColFrameEquals(TestUtil.ard(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN), res);
    }

    @Test
    public void testBadFirstArgument() {
        try {
            Rapids.exec((("(sumaxis " + (AstSumAxisTest.vi1._key)) + " 1 0)"));
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Rapids.exec("(sum hello 1 0)");
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Rapids.exec("(sum 2 1 0)");
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testValRowArgument() {
        Frame fr = AstSumAxisTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("i1", "d1", "d2", "d3"), TestUtil.aro(AstSumAxisTest.vi1, AstSumAxisTest.vd1, AstSumAxisTest.vd2, AstSumAxisTest.vd3)));
        Val val = Rapids.exec((("(apply " + (fr._key)) + " 1 {x . (sumaxis x 1)})"));// skip NAs

        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstSumAxisTest.register(val.getFrame());
        AstSumAxisTest.assertColFrameEquals(TestUtil.ard(1.7, 2.9, 4.1, 10.3, 10.0), res);
        Val val2 = Rapids.exec((("(apply " + (fr._key)) + " 1 {x . (sumaxis x 0)})"));// do not skip NAs

        Assert.assertTrue((val2 instanceof ValFrame));
        Frame res2 = AstSumAxisTest.register(val2.getFrame());
        AstSumAxisTest.assertColFrameEquals(TestUtil.ard(1.7, 2.9, Double.NaN, 10.3, Double.NaN), res2);
    }
}

