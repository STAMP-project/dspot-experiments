package water.rapids.ast.prims.mungers;


import Vec.T_CAT;
import Vec.T_NUM;
import org.junit.Assert;
import org.junit.Test;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.rapids.Rapids;
import water.rapids.Session;
import water.rapids.Val;
import water.rapids.vals.ValFrame;


public class AstPivotTest extends TestUtil {
    @Test
    public void TestPivot() {
        Scope.enter();
        try {
            Session sess = new Session();
            Frame fr = Scope.track(new TestFrameBuilder().withName("$fr", sess).withColNames("index", "col", "value").withVecTypes(T_NUM, T_CAT, T_NUM).withDataForCol(0, TestUtil.ar(1, 2, 3, 4, 2, 4)).withDataForCol(1, TestUtil.ar("a", "a", "a", "a", "b", "b")).withDataForCol(2, TestUtil.ard(10.1, 10.2, 10.3, 10.4, 20.1, 22.2)).build());
            Val val = Rapids.exec("(pivot $fr 'index' 'col' 'value')", sess);
            Assert.assertTrue((val instanceof ValFrame));
            Frame res = Scope.track(val.getFrame());
            // check the first column. should be all unique indexes in order
            TestUtil.assertVecEquals(res.vec(0), TestUtil.dvec(1.0, 2.0, 3.0, 4.0), 0.0);
            // next column is "a" values in correct order
            TestUtil.assertVecEquals(res.vec(1), TestUtil.dvec(10.1, 10.2, 10.3, 10.4), 0.0);
            // last column is "b" values
            TestUtil.assertVecEquals(res.vec(2), TestUtil.dvec(Double.NaN, 20.1, Double.NaN, 22.2), 0.0);
        } finally {
            Scope.exit();
        }
    }
}

