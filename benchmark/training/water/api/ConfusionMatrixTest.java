package water.api;


import org.junit.Test;
import water.TestUtil;


public class ConfusionMatrixTest extends TestUtil {
    final boolean debug = false;

    @Test
    public void testIdenticalVectors() {
        simpleCMTest("smalldata/test/cm/v1.csv", "smalldata/test/cm/v1.csv", TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar(TestUtil.ar(2L, 0L, 0L, 0L), TestUtil.ar(0L, 2L, 0L, 0L), TestUtil.ar(0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
    }

    @Test
    public void testVectorAlignment() {
        simpleCMTest("smalldata/test/cm/v1.csv", "smalldata/test/cm/v2.csv", TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar(TestUtil.ar(1L, 1L, 0L, 0L), TestUtil.ar(0L, 1L, 1L, 0L), TestUtil.ar(0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
    }

    /**
     * Negative test testing expected exception if two vectors
     * of different lengths are provided.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDifferentLenghtVectors() {
        simpleCMTest("smalldata/test/cm/v1.csv", "smalldata/test/cm/v3.csv", TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar(TestUtil.ar(1L, 1L, 0L, 0L), TestUtil.ar(0L, 1L, 1L, 0L), TestUtil.ar(0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
    }

    @Test
    public void testDifferentDomains() {
        simpleCMTest("smalldata/test/cm/v1.csv", "smalldata/test/cm/v4.csv", TestUtil.ar("A", "B", "C"), TestUtil.ar("B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar(TestUtil.ar(0L, 2L, 0L, 0L), TestUtil.ar(0L, 0L, 2L, 0L), TestUtil.ar(0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
        simpleCMTest("smalldata/test/cm/v4.csv", "smalldata/test/cm/v1.csv", TestUtil.ar("B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar(TestUtil.ar(0L, 0L, 0L, 0L), TestUtil.ar(2L, 0L, 0L, 0L), TestUtil.ar(0L, 2L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
        simpleCMTest("smalldata/test/cm/v2.csv", "smalldata/test/cm/v4.csv", TestUtil.ar("A", "B", "C"), TestUtil.ar("B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar(TestUtil.ar(0L, 1L, 0L, 0L), TestUtil.ar(0L, 1L, 1L, 0L), TestUtil.ar(0L, 0L, 2L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
    }

    @Test
    public void testSimpleNumericVectors() {
        simpleCMTest("smalldata/test/cm/v1n.csv", "smalldata/test/cm/v1n.csv", TestUtil.ar("0", "1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ar(TestUtil.ar(2L, 0L, 0L, 0L), TestUtil.ar(0L, 2L, 0L, 0L), TestUtil.ar(0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
        simpleCMTest("smalldata/test/cm/v1n.csv", "smalldata/test/cm/v2n.csv", TestUtil.ar("0", "1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ar(TestUtil.ar(1L, 1L, 0L, 0L), TestUtil.ar(0L, 1L, 1L, 0L), TestUtil.ar(0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
    }

    @Test
    public void testDifferentDomainsNumericVectors() {
        simpleCMTest("smalldata/test/cm/v1n.csv", "smalldata/test/cm/v4n.csv", TestUtil.ar("0", "1", "2"), TestUtil.ar("1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ar(TestUtil.ar(0L, 2L, 0L, 0L), TestUtil.ar(0L, 0L, 2L, 0L), TestUtil.ar(0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
        simpleCMTest("smalldata/test/cm/v4n.csv", "smalldata/test/cm/v1n.csv", TestUtil.ar("1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ar(TestUtil.ar(0L, 0L, 0L, 0L), TestUtil.ar(2L, 0L, 0L, 0L), TestUtil.ar(0L, 2L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
        simpleCMTest("smalldata/test/cm/v2n.csv", "smalldata/test/cm/v4n.csv", TestUtil.ar("0", "1", "2"), TestUtil.ar("1", "2"), TestUtil.ar("0", "1", "2"), TestUtil.ar(TestUtil.ar(0L, 1L, 0L, 0L), TestUtil.ar(0L, 1L, 1L, 0L), TestUtil.ar(0L, 0L, 2L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
    }

    /**
     * Test for PUB-216:
     * The case when vector domain is set to a value (0~A, 1~B, 2~C), but actual values stored in
     * vector references only a subset of domain (1~B, 2~C). The TransfVec was using minimum from
     * vector (i.e., value 1) to compute transformation but minimum was wrong since it should be 0.
     */
    @Test
    public void testBadModelPrect() {
        simpleCMTest(TestUtil.frame("v1", TestUtil.vec(TestUtil.ar("A", "B", "C"), TestUtil.ari(0, 0, 1, 1, 2))), TestUtil.frame("v2", TestUtil.vec(TestUtil.ar("A", "B", "C"), TestUtil.ari(1, 1, 2, 2, 2))), TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar("A", "B", "C"), TestUtil.ar(TestUtil.ar(0L, 2L, 0L, 0L), TestUtil.ar(0L, 0L, 2L, 0L), TestUtil.ar(0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
        simpleCMTest(TestUtil.frame("v1", TestUtil.vec(TestUtil.ar("B", "C"), TestUtil.ari(0, 0, 1, 1))), TestUtil.frame("v2", TestUtil.vec(TestUtil.ar("A", "B"), TestUtil.ari(1, 1, 0, 0))), TestUtil.ar("B", "C"), TestUtil.ar("A", "B"), TestUtil.ar("A", "B", "C"), // A
        // B
        // C
        // NA
        TestUtil.ar(TestUtil.ar(0L, 0L, 0L, 0L), TestUtil.ar(0L, 2L, 0L, 0L), TestUtil.ar(2L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
    }

    @Test
    public void testBadModelPrect2() {
        simpleCMTest(TestUtil.frame("v1", TestUtil.vec(TestUtil.ari((-1), (-1), 0, 0, 1))), TestUtil.frame("v2", TestUtil.vec(TestUtil.ari(0, 0, 1, 1, 1))), TestUtil.ar("-1", "0", "1"), TestUtil.ar("0", "1"), TestUtil.ar("-1", "0", "1"), TestUtil.ar(TestUtil.ar(0L, 2L, 0L, 0L), TestUtil.ar(0L, 0L, 2L, 0L), TestUtil.ar(0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
        simpleCMTest(TestUtil.frame("v1", TestUtil.vec(TestUtil.ari((-1), (-1), 0, 0))), TestUtil.frame("v2", TestUtil.vec(TestUtil.ari(1, 1, 0, 0))), TestUtil.ar("-1", "0"), TestUtil.ar("0", "1"), TestUtil.ar("-1", "0", "1"), TestUtil.ar(TestUtil.ar(0L, 0L, 2L, 0L), TestUtil.ar(0L, 2L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L)), debug);
        // The case found by Nidhi on modified covtype dataset
        simpleCMTest(TestUtil.frame("v1", TestUtil.vec(TestUtil.ari(1, 2, 3, 4, 5, 6, 7))), TestUtil.frame("v2", TestUtil.vec(TestUtil.ari(1, 2, 3, 4, 5, 6, (-1)))), TestUtil.ar("1", "2", "3", "4", "5", "6", "7"), TestUtil.ar("-1", "1", "2", "3", "4", "5", "6"), TestUtil.ar("-1", "1", "2", "3", "4", "5", "6", "7"), // "-1"
        // "1"
        // "2"
        // "3"
        // "4"
        // "5"
        // "6"
        // "7"
        // "NAs"
        TestUtil.ar(TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 1L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 1L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 1L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 1L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L, 1L, 0L, 0L), TestUtil.ar(1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L)), debug);
        // Another case
        simpleCMTest(TestUtil.frame("v1", TestUtil.vec(TestUtil.ari(7, 8, 9, 10, 11))), TestUtil.frame("v2", TestUtil.vec(TestUtil.ari(7, 8, 13, 10, 11))), TestUtil.ar("7", "8", "9", "10", "11"), TestUtil.ar("7", "8", "10", "11", "13"), TestUtil.ar("7", "8", "9", "10", "11", "13"), // "7"
        // "8"
        // "9"
        // "10"
        // "11"
        // "13"
        // "NAs"
        TestUtil.ar(TestUtil.ar(1L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 1L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 1L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 1L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L, 0L)), debug);
        // Mixed case
        simpleCMTest(TestUtil.frame("v1", TestUtil.vec(TestUtil.ar("-1", "1", "A"), TestUtil.ari(0, 1, 2))), TestUtil.frame("v2", TestUtil.vec(TestUtil.ar("0", "1", "B"), TestUtil.ari(0, 1, 2))), TestUtil.ar("-1", "1", "A"), TestUtil.ar("0", "1", "B"), TestUtil.ar("-1", "0", "1", "A", "B"), // "-1"
        // "0"
        // "1"
        // "A"
        // "B"
        // "NAs"
        TestUtil.ar(TestUtil.ar(0L, 1L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 1L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L)), false);
        // Mixed case with change of numeric ordering 1, 10, 9 -> 1,9,10
        simpleCMTest(TestUtil.frame("v1", TestUtil.vec(TestUtil.ar("-1", "1", "10", "9", "A"), TestUtil.ari(0, 1, 2, 3, 4))), TestUtil.frame("v2", TestUtil.vec(TestUtil.ar("0", "2", "8", "9", "B"), TestUtil.ari(0, 1, 2, 3, 4))), TestUtil.ar("-1", "1", "10", "9", "A"), TestUtil.ar("0", "2", "8", "9", "B"), TestUtil.ar("-1", "0", "1", "2", "8", "9", "10", "A", "B"), // "-1"
        // "0"
        // "1"
        // "2"
        // "8"
        // "9"
        // "10"
        // "A"
        // "B"
        // "NAs"
        TestUtil.ar(TestUtil.ar(0L, 1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 1L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 1L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 1L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 1L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L), TestUtil.ar(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L)), debug);
    }
}

