package water.fvec;


import org.junit.Test;
import water.TestUtil;
import water.util.Utils;


public class TransfVecTest extends TestUtil {
    /**
     * Verifies that {@link Model#getDomainMapping(String[], String[], boolean)} returns
     *  correct values.
     */
    @Test
    public void testModelMappingCall() {
        testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar(TestUtil.ari(0, 1, 2), TestUtil.ari(0, 1, 2)));
        testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("B", "C"), TestUtil.ar(TestUtil.ari(0, 1), TestUtil.ari(1, 2)));
        testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("B"), TestUtil.ar(TestUtil.ari(0), TestUtil.ari(1)));
        testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C", "D"), TestUtil.ar(TestUtil.ari(0, 1, 2), TestUtil.ari(0, 1, 2)));
        testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("B", "C", "D"), TestUtil.ar(TestUtil.ari(0, 1), TestUtil.ari(1, 2)));
        testModelMapping(TestUtil.ar("A", "B", "C"), TestUtil.ar("B", "D"), TestUtil.ar(TestUtil.ari(0), TestUtil.ari(1)));
    }

    @Test
    public void testMappingComposition() {
        // expecting composed mapping
        // <- 2nd mapping
        assertEqualMapping(TestUtil.ar(TestUtil.ari(1), TestUtil.ari(0)), // <- 1st mapping
        Utils.compose(TestUtil.ar(TestUtil.ari((-1), 1), null), TestUtil.ar(TestUtil.ari(1, 2), TestUtil.ari(0, 1))));
        // expecting composed mapping
        // <- 2nd mapping
        assertEqualMapping(TestUtil.ar(TestUtil.ari(1, 2, 3, 4, 5, 6), TestUtil.ari(0, 1, 2, 3, 4, 5)), // <- 1st mapping
        Utils.compose(TestUtil.ar(TestUtil.ari((-1), 1, 2, 3, 4, 5, 6), null), TestUtil.ar(TestUtil.ari(1, 2, 3, 4, 5, 6), TestUtil.ari(0, 1, 2, 3, 4, 5))));
    }
}

