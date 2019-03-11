package water.rapids.ast.prims.mungers;


import Vec.T_NUM;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.fvec.Vec;
import water.rapids.Rapids;
import water.rapids.Val;


public class AstGroupTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void TestGroup() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "ColC").withVecTypes(T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(1, 1, 1, 2, 2, 2)).withDataForCol(1, TestUtil.ard(1, 1, 2, 1, 2, 2)).withDataForCol(2, TestUtil.ar(4, 5, 6, 7, 2, 6)).withChunkLayout(2, 2, 2).build();
        String tree = "(GB testFrame [0, 1] sum 2 \"all\" nrow 1 \"all\")";
        Val val = Rapids.exec(tree);
        Frame res = val.getFrame();
        Vec resVec = res.vec(2);
        Vec expected = TestUtil.vec(9, 6, 7, 8);
        TestUtil.assertVecEquals(expected, resVec, 1.0E-5);
        resVec.remove();
        expected.remove();
        res.delete();
    }
}

