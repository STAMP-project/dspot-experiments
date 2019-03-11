package water.rapids.ast.prims.advmath;


import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.TestUtil;
import water.fvec.Frame;
import water.rapids.Rapids;
import water.rapids.Val;
import water.util.ArrayUtils;


public class StratifiedSplitTest extends TestUtil {
    private static Frame f = null;

    private static Frame fr1 = null;

    private static Frame fanimal = null;

    private static Frame fr2 = null;

    @Test
    public void testStratifiedSampling() {
        StratifiedSplitTest.f = ArrayUtils.frame("response", TestUtil.vec(TestUtil.ari(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1)));
        StratifiedSplitTest.fanimal = ArrayUtils.frame("response", TestUtil.vec(TestUtil.ar("dog", "cat"), TestUtil.ari(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1)));
        StratifiedSplitTest.f = new Frame(StratifiedSplitTest.f);
        StratifiedSplitTest.fanimal = new Frame(StratifiedSplitTest.fanimal);
        StratifiedSplitTest.f._key = Key.make();
        StratifiedSplitTest.fanimal._key = Key.make();
        DKV.put(StratifiedSplitTest.f);
        DKV.put(StratifiedSplitTest.fanimal);
        Val res1 = Rapids.exec((("(h2o.random_stratified_split (cols_py " + (StratifiedSplitTest.f._key)) + " 0) 0.3333333 123)"));// 

        StratifiedSplitTest.fr1 = res1.getFrame();
        Assert.assertEquals(StratifiedSplitTest.fr1.vec(0).at8(0), 1);// minority class should be in the test split

        Assert.assertEquals(StratifiedSplitTest.fr1.vec(0).at8(11), 0);// minority class should be in the train split

        Assert.assertEquals(StratifiedSplitTest.fr1.vec(0).mean(), (1.0 / 3.0), 1.0E-5);// minority class should be in the train split

        // test categorical
        Val res2 = Rapids.exec((("(h2o.random_stratified_split (cols_py " + (StratifiedSplitTest.fanimal._key)) + " 0) 0.3333333 123)"));// 

        StratifiedSplitTest.fr2 = res2.getFrame();
        Assert.assertEquals(StratifiedSplitTest.fr2.vec(0).at8(0), 1);// minority class should be in the test split

        Assert.assertEquals(StratifiedSplitTest.fr2.vec(0).at8(11), 0);// minority class should be in the test split

        Assert.assertEquals(StratifiedSplitTest.fr2.vec(0).mean(), (1.0 / 3.0), 1.0E-5);// minority class should be in the test split

    }
}

