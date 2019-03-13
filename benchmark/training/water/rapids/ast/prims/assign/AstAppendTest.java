package water.rapids.ast.prims.assign;


import Vec.T_NUM;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.rapids.Rapids;
import water.rapids.Val;


public class AstAppendTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void AppendColumnTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB").withVecTypes(T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(1, 2)).withDataForCol(1, TestUtil.ard(2, 5)).build();
        String tree = "( append testFrame ( / (cols testFrame [0]) (cols testFrame [1])) 'appended' )";
        Val val = Rapids.exec(tree);
        Frame res = val.getFrame();
        Assert.assertEquals(2, res.numRows());
        Assert.assertEquals(0.5, res.vec(2).at(0L), 1.0E-6);
        Assert.assertEquals(0.4, res.vec(2).at(1L), 1.0E-6);
        res.delete();
    }
}

