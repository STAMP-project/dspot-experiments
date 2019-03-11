package water.rapids.ast.prims.advmath;


import Vec.T_CAT;
import Vec.T_NUM;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.fvec.Vec;
import water.rapids.Rapids;
import water.rapids.Val;


public class AstUniqueTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void UniqueCategoricalTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "ColC").withVecTypes(T_NUM, T_NUM, T_CAT).withDataForCol(0, TestUtil.ard(1, 1)).withDataForCol(1, TestUtil.ard(1, 1)).withDataForCol(2, TestUtil.ar("3", "6")).build();
        String tree = "(unique (cols testFrame [2]))";
        Val val = Rapids.exec(tree);
        Frame res = val.getFrame();
        Assert.assertEquals(2, res.numRows());
        Assert.assertEquals("3", res.vec(0).stringAt(0));
        Assert.assertEquals("6", res.vec(0).stringAt(1));
        res.delete();
    }

    @Test
    public void UniqueNumericalTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "ColC").withVecTypes(T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(1, 1)).withDataForCol(1, TestUtil.ard(1, 1)).withDataForCol(2, TestUtil.ar(3, 6)).build();
        String tree = "(unique (cols testFrame [2]))";
        Val val = Rapids.exec(tree);
        Frame res = val.getFrame();
        Vec expected = TestUtil.vec(6, 3);
        TestUtil.assertVecEquals(expected, res.vec(0), 1.0E-6);// TODO Why order of 6 and 3 is as if it is desc. sorted ?

        expected.remove();
        res.delete();
    }
}

