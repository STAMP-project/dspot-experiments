package water.rapids.ast.prims.advmath;


import Vec.T_CAT;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.rapids.Rapids;
import water.rapids.Session;
import water.rapids.Val;
import water.rapids.vals.ValFrame;


public class AstFillNATest extends TestUtil {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void forwardFillNAInCategoricalColumnTest() {
        Scope.enter();
        try {
            Session sess = new Session();
            Frame fr = Scope.track(new TestFrameBuilder().withName("testFrame", sess).withColNames("ColA").withVecTypes(T_CAT).withDataForCol(0, TestUtil.ar("a", "b", null)).build());
            Val val = Rapids.exec("(h2o.fillna testFrame 'forward' 0 1)", sess);
            Assert.assertTrue((val instanceof ValFrame));
            Frame res = Scope.track(val.getFrame());
            System.out.println(res.toTwoDimTable().toString());
            Assert.assertEquals(1, res.vec(0).at(2), 1.0E-5);
        } finally {
            Scope.exit();
        }
    }
}

