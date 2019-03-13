package water.rapids.ast.prims.mungers;


import Vec.T_NUM;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.fvec.Vec;
import water.rapids.Rapids;
import water.rapids.Val;


public class AstRBindTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void TestRBind() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "ColC").withVecTypes(T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(1)).withDataForCol(1, TestUtil.ard(1)).withDataForCol(2, TestUtil.ar(5)).build();
        Frame fr2 = new TestFrameBuilder().withName("testFrame2").withColNames("ColA", "ColB", "ColC").withVecTypes(T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(2)).withDataForCol(1, TestUtil.ard(2)).withDataForCol(2, TestUtil.ar(6)).build();
        String tree = "(rbind testFrame testFrame2)";
        Val val = Rapids.exec(tree);
        Frame unionFrame = val.getFrame();
        Vec resVec = unionFrame.vec(2);
        printOutFrameAsTable(fr, false, 10);
        Assert.assertEquals(2, unionFrame.numRows());
        Assert.assertEquals(5L, resVec.at(0), 1.0E-5);
        Assert.assertEquals(6L, resVec.at(1), 1.0E-5);
        resVec.remove();
        fr.delete();
        fr2.delete();
        unionFrame.delete();
    }
}

