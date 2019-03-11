package water;


import Vec.T_STR;
import org.junit.Assert;
import org.junit.Test;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;


/**
 * We need to make sure that our tools for testing are reliable as well
 */
public class TestUtilTest extends TestUtil {
    @Test
    public void asFactor() {
        Scope.enter();
        try {
            Frame fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_STR).withDataForCol(0, TestUtil.ar("yes", "no")).build();
            Scope.track(fr);
            Assert.assertTrue(fr.vec(0).isString());
            Frame res = asFactor(fr, "ColA");
            Assert.assertTrue(res.vec(0).isCategorical());
            Scope.track(res);
        } finally {
            Scope.exit();
        }
    }
}

