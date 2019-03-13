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


public class AstRowSliceTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void GettingLogicMaskTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "ColC").withVecTypes(T_NUM, T_NUM, T_STR).withDataForCol(0, TestUtil.ard(1, 1)).withDataForCol(1, TestUtil.ard(1, 1)).withDataForCol(2, TestUtil.ar(null, "6")).build();
        String tree = "(!! (is.na (cols testFrame [2.0] ) ) )";
        Val val = Rapids.exec(tree);
        Frame res = val.getFrame();
        // Check that row with NA in target column will be mapped into 0, otherwise 1
        Assert.assertEquals(0, res.vec(0).at(0), 1.0E-5);
        Assert.assertEquals(1, res.vec(0).at(1), 1.0E-5);
        res.delete();
    }

    @Test
    public void FilteringOutByBooleanMaskTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "ColC").withVecTypes(T_NUM, T_NUM, T_STR).withDataForCol(0, TestUtil.ard(1, 1)).withDataForCol(1, TestUtil.ard(1, 1)).withDataForCol(2, TestUtil.ar(null, "6")).build();
        String tree = "(rows testFrame (!! (is.na (cols testFrame [2.0] ) ) ) )";
        Val val = Rapids.exec(tree);
        Frame res = val.getFrame();
        Assert.assertEquals(1, res.numRows());
        Assert.assertEquals("6", res.vec(2).stringAt(0));
        res.delete();
    }

    @Test
    public void RowSliceWithRangeTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "ColC").withVecTypes(T_NUM, T_NUM, T_STR).withDataForCol(0, TestUtil.ard(1, 1)).withDataForCol(1, TestUtil.ard(1, 1)).withDataForCol(2, TestUtil.ar(null, "6")).build();
        String tree = "(rows testFrame [1:2] )";
        Val val = Rapids.exec(tree);
        Frame res = val.getFrame();
        printOutFrameAsTable(res, false, 100);
        Assert.assertEquals(1, res.numRows());
        Assert.assertEquals("6", res.vec(2).stringAt(0));
        res.delete();
    }

    @Test
    public void filterByTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_STR).withDataForCol(0, TestUtil.ar("SAN", "SFO")).build();
        Frame res = filterBy(fr, 0, "SAN");
        Assert.assertEquals("SAN", res.vec(0).stringAt(0));
        res.delete();
    }

    @Test
    public void filterOutByTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_STR).withDataForCol(0, TestUtil.ar("SAN", "SFO")).build();
        Frame res = filterOutBy(fr, 0, "SAN");
        Assert.assertEquals("SFO", res.vec(0).stringAt(0));
        res.delete();
    }
}

