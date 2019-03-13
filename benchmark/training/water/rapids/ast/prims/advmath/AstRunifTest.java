package water.rapids.ast.prims.advmath;


import Vec.T_NUM;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.fvec.Vec;
import water.rapids.Rapids;
import water.rapids.Val;


public class AstRunifTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void RunifTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(0, 0, 0)).build();
        String tree = "(h2o.runif testFrame 1234.0 )";
        Val val = Rapids.exec(tree);
        Frame res = val.getFrame();
        Vec expected = TestUtil.dvec(0.73257, 0.27102, 0.63133);
        TestUtil.assertVecEquals(expected, res.vec(0), 1.0E-5);
        // TODO pass more values into the column and check whether distribution of randomly generated values is correct
        expected.remove();
        res.delete();
    }
}

