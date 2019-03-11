package hex;


import org.junit.Test;
import water.MRTask;
import water.Scope;
import water.TestUtil;
import water.fvec.Chunk;
import water.util.ArrayUtils;


public class ConfusionMatrixTest extends TestUtil {
    final boolean debug = false;

    @Test
    public void testIdenticalVectors() {
        try {
            Scope.enter();
            simpleCMTest("smalldata/junit/cm/v1.csv", "smalldata/junit/cm/v1.csv", TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ard(TestUtil.ard(2, 0, 0), TestUtil.ard(0, 2, 0), TestUtil.ard(0, 0, 1)), debug);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testVectorAlignment() {
        simpleCMTest("smalldata/junit/cm/v1.csv", "smalldata/junit/cm/v2.csv", TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ard(TestUtil.ard(1, 1, 0), TestUtil.ard(0, 1, 1), TestUtil.ard(0, 0, 1)), debug);
    }

    /**
     * Negative test testing expected exception if two vectors
     * of different lengths are provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDifferentLengthVectors() {
        simpleCMTest("smalldata/junit/cm/v1.csv", "smalldata/junit/cm/v3.csv", TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ard(TestUtil.ard(1, 1, 0), TestUtil.ard(0, 1, 1), TestUtil.ard(0, 0, 1)), debug);
    }

    @Test
    public void testDifferentDomains() {
        simpleCMTest("smalldata/junit/cm/v1.csv", "smalldata/junit/cm/v4.csv", TestUtil.ar("A", "B", "C"), TestUtil.ar("B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ard(TestUtil.ard(0, 2, 0), TestUtil.ard(0, 0, 2), TestUtil.ard(0, 0, 1)), debug);
        simpleCMTest("smalldata/junit/cm/v2.csv", "smalldata/junit/cm/v4.csv", TestUtil.ar("A", "B", "C"), TestUtil.ar("B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ard(TestUtil.ard(0, 1, 0), TestUtil.ard(0, 1, 1), TestUtil.ard(0, 0, 2)), debug);
    }

    @Test
    public void testSimpleNumericVectors() {
        simpleCMTest("smalldata/junit/cm/v1n.csv", "smalldata/junit/cm/v1n.csv", TestUtil.ar("0", "1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ard(TestUtil.ard(2, 0, 0), TestUtil.ard(0, 2, 0), TestUtil.ard(0, 0, 1)), debug);
        simpleCMTest("smalldata/junit/cm/v1n.csv", "smalldata/junit/cm/v2n.csv", TestUtil.ar("0", "1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ard(TestUtil.ard(1, 1, 0), TestUtil.ard(0, 1, 1), TestUtil.ard(0, 0, 1)), debug);
    }

    @Test
    public void testDifferentDomainsNumericVectors() {
        simpleCMTest("smalldata/junit/cm/v1n.csv", "smalldata/junit/cm/v4n.csv", TestUtil.ar("0", "1", "2"), TestUtil.ar("1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ard(TestUtil.ard(0, 2, 0), TestUtil.ard(0, 0, 2), TestUtil.ard(0, 0, 1)), debug);
        simpleCMTest("smalldata/junit/cm/v2n.csv", "smalldata/junit/cm/v4n.csv", TestUtil.ar("0", "1", "2"), TestUtil.ar("1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ard(TestUtil.ard(0, 1, 0), TestUtil.ard(0, 1, 1), TestUtil.ard(0, 0, 2)), debug);
    }

    /**
     * Test for PUB-216:
     * The case when vector domain is set to a value (0~A, 1~B, 2~C), but actual values stored in
     * vector references only a subset of domain (1~B, 2~C). The TransfVec was using minimum from
     * vector (i.e., value 1) to compute transformation but minimum was wrong since it should be 0.
     */
    @Test
    public void testBadModelPrect() {
        simpleCMTest(ArrayUtils.frame("v1", TestUtil.vec(TestUtil.ar("A", "B", "C"), TestUtil.ari(0, 0, 1, 1, 2))), ArrayUtils.frame("v1", TestUtil.vec(TestUtil.ar("A", "B", "C"), TestUtil.ari(1, 1, 2, 2, 2))), TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ard(TestUtil.ard(0, 2, 0), TestUtil.ard(0, 0, 2), TestUtil.ard(0, 0, 1)), debug);
    }

    @Test
    public void testBadModelPrect2() {
        simpleCMTest(ArrayUtils.frame("v1", TestUtil.vec(TestUtil.ar("-1", "0", "1"), TestUtil.ari(0, 0, 1, 1, 2))), ArrayUtils.frame("v1", TestUtil.vec(TestUtil.ar("0", "1"), TestUtil.ari(0, 0, 1, 1, 1))), TestUtil.ar("-1", "0", "1"), TestUtil.ar("0", "1"), TestUtil.ar("-1", "0", "1"), TestUtil.ard(TestUtil.ard(0, 2, 0), TestUtil.ard(0, 0, 2), TestUtil.ard(0, 0, 1)), debug);
    }

    private static class CMBuilder extends MRTask<ConfusionMatrixTest.CMBuilder> {
        final int _len;

        /* actuals */
        /* predicted */
        double[][] _arr;

        CMBuilder(int len) {
            _len = len;
        }

        @Override
        public void map(Chunk ca, Chunk cp) {
            // After adapting frames, the Actuals have all the levels in the
            // prediction results, plus any extras the model was never trained on.
            // i.e., Actual levels are at least as big as the predicted levels.
            _arr = new double[_len][_len];
            for (int i = 0; i < (ca._len); i++)
                if (!(ca.isNA(i)))
                    (_arr[((int) (ca.at8(i)))][((int) (cp.at8(i)))])++;


        }

        @Override
        public void reduce(ConfusionMatrixTest.CMBuilder cm) {
            ArrayUtils.add(_arr, cm._arr);
        }
    }
}

