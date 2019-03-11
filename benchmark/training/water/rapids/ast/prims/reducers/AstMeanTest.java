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
 * Test the AstMean.java class
 */
public class AstMeanTest extends TestUtil {
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
    public void testAstMeanGeneralStructure() {
        AstMean a = new AstMean();
        String[] args = a.args();
        Assert.assertEquals(3, args.length);
        String example = a.example();
        Assert.assertTrue(example.startsWith("(mean "));
        String description = a.description();
        Assert.assertTrue("Description for AstMean is too short", ((description.length()) > 100));
    }

    @Test
    public void testColumnwiseMeanWithoutNaRm() {
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("I", "D", "DD", "DN", "T", "S", "C"), TestUtil.aro(AstMeanTest.vi1, AstMeanTest.vd1, AstMeanTest.vd2, AstMeanTest.vd3, AstMeanTest.vt1, AstMeanTest.vs1, AstMeanTest.vc2)));
        Val val1 = Rapids.exec((("(mean " + (fr._key)) + " 0 0)"));
        Assert.assertTrue((val1 instanceof ValFrame));
        Frame res = AstMeanTest.register(val1.getFrame());
        Assert.assertArrayEquals(fr.names(), res.names());
        Assert.assertArrayEquals(TestUtil.ar(T_NUM, T_NUM, T_NUM, T_NUM, T_TIME, T_NUM, T_NUM), res.types());
        AstMeanTest.assertRowFrameEquals(TestUtil.ard(0.0, 4.0, 0.6, Double.NaN, 1.000003E7, Double.NaN, Double.NaN), res);
    }

    @Test
    public void testColumnwiseMeanWithNaRm() {
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("I", "D", "DD", "DN", "T", "S", "C"), TestUtil.aro(AstMeanTest.vi1, AstMeanTest.vd1, AstMeanTest.vd2, AstMeanTest.vd3, AstMeanTest.vt1, AstMeanTest.vs1, AstMeanTest.vc2)));
        Val val = Rapids.exec((("(mean " + (fr._key)) + " 1 0)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstMeanTest.register(val.getFrame());
        Assert.assertArrayEquals(fr.names(), res.names());
        Assert.assertArrayEquals(TestUtil.ar(T_NUM, T_NUM, T_NUM, T_NUM, T_TIME, T_NUM, T_NUM), res.types());
        AstMeanTest.assertRowFrameEquals(TestUtil.ard(0.0, 4.0, 0.6, 2.0, 1.000003E7, Double.NaN, Double.NaN), res);
    }

    @Test
    public void testColumnwiseMeanOnEmptyFrame() {
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make()));
        Val val = Rapids.exec((("(mean " + (fr._key)) + " 0 0)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstMeanTest.register(val.getFrame());
        Assert.assertEquals(res.numCols(), 0);
        Assert.assertEquals(res.numRows(), 0);
    }

    @Test
    public void testColumnwiseMeanBinaryVec() {
        Assert.assertTrue(((AstMeanTest.vc1.isBinary()) && (!(AstMeanTest.vc2.isBinary()))));
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("C1", "C2"), TestUtil.aro(AstMeanTest.vc1, AstMeanTest.vc2)));
        Val val = Rapids.exec((("(mean " + (fr._key)) + " 1 0)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstMeanTest.register(val.getFrame());
        Assert.assertArrayEquals(fr.names(), res.names());
        Assert.assertArrayEquals(TestUtil.ar(T_NUM, T_NUM), res.types());
        AstMeanTest.assertRowFrameEquals(TestUtil.ard(0.6, Double.NaN), res);
    }

    @Test
    public void testRowwiseMeanWithoutNaRm() {
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("i1", "d1", "d2", "d3"), TestUtil.aro(AstMeanTest.vi1, AstMeanTest.vd1, AstMeanTest.vd2, AstMeanTest.vd3)));
        Val val = Rapids.exec((("(mean " + (fr._key)) + " 0 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstMeanTest.register(val.getFrame());
        AstMeanTest.assertColFrameEquals(TestUtil.ard((1.7 / 4), (2.9 / 4), Double.NaN, (10.3 / 4), Double.NaN), res);
        Assert.assertEquals("mean", res.name(0));
    }

    @Test
    public void testRowwiseMeanWithoutNaRmAndNonnumericColumn() {
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("i1", "d1", "d2", "d3", "s1"), TestUtil.aro(AstMeanTest.vi1, AstMeanTest.vd1, AstMeanTest.vd2, AstMeanTest.vd3, AstMeanTest.vs1)));
        Val val = Rapids.exec((("(mean " + (fr._key)) + " 0 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstMeanTest.register(val.getFrame());
        AstMeanTest.assertColFrameEquals(TestUtil.ard(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN), res);
        Assert.assertEquals("mean", res.name(0));
    }

    @Test
    public void testRowwiseMeanWithNaRm() {
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("i1", "d1", "d2", "d3", "s1"), TestUtil.aro(AstMeanTest.vi1, AstMeanTest.vd1, AstMeanTest.vd2, AstMeanTest.vd3, AstMeanTest.vs1)));
        Val val = Rapids.exec((("(mean " + (fr._key)) + " 1 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstMeanTest.register(val.getFrame());
        Assert.assertEquals("Unexpected column name", "mean", res.name(0));
        Assert.assertEquals("Unexpected column type", T_NUM, res.types()[0]);
        AstMeanTest.assertColFrameEquals(TestUtil.ard((1.7 / 4), (2.9 / 4), (4.1 / 3), (10.3 / 4), (10.0 / 3)), res);
    }

    @Test
    public void testRowwiseMeanOnFrameWithTimeColumnsOnly() {
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("t1", "s", "t2"), TestUtil.aro(AstMeanTest.vt1, AstMeanTest.vs1, AstMeanTest.vt2)));
        Val val = Rapids.exec((("(mean " + (fr._key)) + " 1 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstMeanTest.register(val.getFrame());
        Assert.assertEquals("Unexpected column name", "mean", res.name(0));
        Assert.assertEquals("Unexpected column type", T_TIME, res.types()[0]);
        AstMeanTest.assertColFrameEquals(TestUtil.ard(15000000, 15000020, 15000030, 15000040, 15000060), res);
    }

    @Test
    public void testRowwiseMeanOnFrameWithTimeAndNumericColumn() {
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("t1", "i1"), TestUtil.aro(AstMeanTest.vt1, AstMeanTest.vi1)));
        Val val = Rapids.exec((("(mean " + (fr._key)) + " 1 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstMeanTest.register(val.getFrame());
        AstMeanTest.assertColFrameEquals(TestUtil.ard((-1), (-2), 0, 2, 1), res);
    }

    @Test
    public void testRowwiseMeanOnEmptyFrame() {
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make()));
        Val val = Rapids.exec((("(mean " + (fr._key)) + " 0 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstMeanTest.register(val.getFrame());
        Assert.assertEquals(res.numCols(), 0);
        Assert.assertEquals(res.numRows(), 0);
    }

    @Test
    public void testRowwiseMeanOnFrameWithNonnumericColumnsOnly() {
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("c1", "s1"), TestUtil.aro(AstMeanTest.vc2, AstMeanTest.vs1)));
        Val val = Rapids.exec((("(mean " + (fr._key)) + " 1 1)"));
        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstMeanTest.register(val.getFrame());
        Assert.assertEquals("Unexpected column name", "mean", res.name(0));
        Assert.assertEquals("Unexpected column type", T_NUM, res.types()[0]);
        AstMeanTest.assertColFrameEquals(TestUtil.ard(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN), res);
    }

    @Test
    public void testBadFirstArgument() {
        try {
            Rapids.exec((("(mean " + (AstMeanTest.vi1._key)) + " 1 0)"));
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Rapids.exec("(mean hello 1 0)");
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Rapids.exec("(mean 2 1 0)");
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testValRowArgument() {
        Frame fr = AstMeanTest.register(new Frame(Key.<Frame>make(), TestUtil.ar("i1", "d1", "d2", "d3"), TestUtil.aro(AstMeanTest.vi1, AstMeanTest.vd1, AstMeanTest.vd2, AstMeanTest.vd3)));
        Val val = Rapids.exec((("(apply " + (fr._key)) + " 1 {x . (mean x 1)})"));// skip NAs

        Assert.assertTrue((val instanceof ValFrame));
        Frame res = AstMeanTest.register(val.getFrame());
        AstMeanTest.assertColFrameEquals(TestUtil.ard((1.7 / 4), (2.9 / 4), (4.1 / 3), (10.3 / 4), (10.0 / 3)), res);
        Val val2 = Rapids.exec((("(apply " + (fr._key)) + " 1 {x . (mean x 0)})"));// do not skip NAs

        Assert.assertTrue((val2 instanceof ValFrame));
        Frame res2 = AstMeanTest.register(val2.getFrame());
        AstMeanTest.assertColFrameEquals(TestUtil.ard((1.7 / 4), (2.9 / 4), Double.NaN, (10.3 / 4), Double.NaN), res2);
    }
}

