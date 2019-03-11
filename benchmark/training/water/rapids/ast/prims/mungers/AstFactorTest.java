package water.rapids.ast.prims.mungers;


import Vec.T_NUM;
import Vec.T_STR;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.rapids.Rapids;
import water.rapids.Val;


public class AstFactorTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void asFactorForDataSetTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(0, 1)).build();
        Assert.assertFalse(fr.vec(0).isCategorical());
        String tree = "(as.factor testFrame)";
        Val val = Rapids.exec(tree);
        Frame res = val.getFrame();
        Assert.assertTrue(res.vec(0).isCategorical());
        res.delete();
    }

    @Test
    public void asFactorForVecTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB").withVecTypes(T_STR, T_NUM).withDataForCol(0, TestUtil.ar("yes", "no")).withDataForCol(1, TestUtil.ard(1, 2)).build();
        Assert.assertTrue(fr.vec(0).isString());
        Assert.assertFalse(fr.vec(0).isCategorical());
        Assert.assertTrue(fr.vec(1).isNumeric());
        String tree = "(:= testFrame (as.factor (cols testFrame [0])) [0] [])";
        Val val = Rapids.exec(tree);
        Frame res = val.getFrame();
        Assert.assertTrue(res.vec(0).isCategorical());
        Assert.assertTrue(res.vec(1).isNumeric());
        res.delete();
    }
}

