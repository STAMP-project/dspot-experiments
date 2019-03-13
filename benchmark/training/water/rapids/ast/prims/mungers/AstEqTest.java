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


public class AstEqTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void IsNaTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB").withVecTypes(T_NUM, T_STR).withDataForCol(0, TestUtil.ard(1, 2)).withDataForCol(1, TestUtil.ar("1", "3")).build();
        String tree = "(== (cols testFrame [0.0] ) 1 )";
        Val val = Rapids.exec(tree);
        Frame results = val.getFrame();
        Assert.assertTrue(((results.numRows()) == 2));
        Assert.assertEquals(1, results.vec(0).at(0), 1.0E-5);
        Assert.assertEquals(0, results.vec(0).at(1), 1.0E-5);
        results.delete();
    }
}

