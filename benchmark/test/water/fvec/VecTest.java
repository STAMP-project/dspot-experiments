package water.fvec;


import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;

import static H2O.LOG_CHK;


/**
 * This test tests stability of Vec API.
 */
public class VecTest extends TestUtil {
    final int CHUNK_SZ = 1 << (LOG_CHK);

    /**
     * Test toEnum call to return correct domain.
     */
    @Test
    public void testToEnum() {
        testToEnumDomainMatch(TestUtil.vec(0, 1, 0, 1), TestUtil.ar("0", "1"));
        testToEnumDomainMatch(TestUtil.vec(1, 2, 3, 4, 5, 6, 7), TestUtil.ar("1", "2", "3", "4", "5", "6", "7"));
        testToEnumDomainMatch(TestUtil.vec((-1), 0, 1, 2, 3, 4, 5, 6), TestUtil.ar("-1", "0", "1", "2", "3", "4", "5", "6"));
    }

    @Test
    public void testChangeDomain() {
        VecTest.testChangeDomainImpl();
    }

    // Test HEX-1819
    @Test
    public void testMakeConSeq() {
        Vec v;
        v = Vec.makeConSeq(51966, CHUNK_SZ);
        Assert.assertTrue(((v.at(234)) == 51966));
        Assert.assertTrue(((v._espc.length) == 2));
        Assert.assertTrue((((v._espc[0]) == 0) && ((v._espc[1]) == (CHUNK_SZ))));
        v.remove(new Futures()).blockForPending();
        v = Vec.makeConSeq(51966, (2 * (CHUNK_SZ)));
        Assert.assertTrue(((v.at(234)) == 51966));
        Assert.assertTrue(((v.at(((2 * (CHUNK_SZ)) - 1))) == 51966));
        Assert.assertTrue(((v._espc.length) == 3));
        Assert.assertTrue(((((v._espc[0]) == 0) && ((v._espc[1]) == (CHUNK_SZ))) && ((v._espc[2]) == ((CHUNK_SZ) * 2))));
        v.remove(new Futures()).blockForPending();
        v = Vec.makeConSeq(51966, ((2 * (CHUNK_SZ)) + 1));
        Assert.assertTrue(((v.at(234)) == 51966));
        Assert.assertTrue(((v.at((2 * (CHUNK_SZ)))) == 51966));
        Assert.assertTrue(((v._espc.length) == 4));
        Assert.assertTrue((((((v._espc[0]) == 0) && ((v._espc[1]) == (CHUNK_SZ))) && ((v._espc[2]) == ((CHUNK_SZ) * 2))) && ((v._espc[3]) == (((CHUNK_SZ) * 2) + 1))));
        v.remove(new Futures()).blockForPending();
        v = Vec.makeConSeq(51966, (3 * (CHUNK_SZ)));
        Assert.assertTrue(((v.at(234)) == 51966));
        Assert.assertTrue(((v.at(((3 * (CHUNK_SZ)) - 1))) == 51966));
        Assert.assertTrue(((v._espc.length) == 4));
        Assert.assertTrue((((((v._espc[0]) == 0) && ((v._espc[1]) == (CHUNK_SZ))) && ((v._espc[2]) == ((CHUNK_SZ) * 2))) && ((v._espc[3]) == ((CHUNK_SZ) * 3))));
        v.remove(new Futures()).blockForPending();
    }

    // Test HEX-1819
    @Test
    public void testMakeSeq() {
        Vec v = Vec.makeSeq((3 * (CHUNK_SZ)));
        Assert.assertTrue(((v.at(0)) == 1));
        Assert.assertTrue(((v.at(234)) == 235));
        Assert.assertTrue(((v.at((2 * (CHUNK_SZ)))) == ((2 * (CHUNK_SZ)) + 1)));
        Assert.assertTrue(((v._espc.length) == 4));
        Assert.assertTrue((((((v._espc[0]) == 0) && ((v._espc[1]) == (CHUNK_SZ))) && ((v._espc[2]) == ((CHUNK_SZ) * 2))) && ((v._espc[3]) == ((CHUNK_SZ) * 3))));
        v.remove(new Futures()).blockForPending();
    }
}

