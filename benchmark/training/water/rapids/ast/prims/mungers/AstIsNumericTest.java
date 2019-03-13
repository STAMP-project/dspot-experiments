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


public class AstIsNumericTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void IsNumericTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB").withVecTypes(T_NUM, T_STR).withDataForCol(0, TestUtil.ard(1, 1)).withDataForCol(1, TestUtil.ar("1", "2")).build();
        String tree = "(is.numeric (cols testFrame [0.0] ) )";
        Val val = Rapids.exec(tree);
        double[] results = val.getNums();
        Assert.assertEquals(1.0, results[0], 1.0E-5);
        String tree2 = "(is.numeric (cols testFrame [1.0] ) )";
        Val val2 = Rapids.exec(tree2);
        double[] results2 = val2.getNums();
        Assert.assertEquals(0.0, results2[0], 1.0E-5);
    }
}

