package water.fvec;


import Vec.T_STR;
import org.junit.Assert;
import org.junit.Test;
import water.Futures;
import water.MRTask;
import water.Scope;
import water.TestUtil;

import static FileVec.DFLT_CHUNK_SIZE;
import static Vec.TYPE_STR;
import static Vec.T_NUM;


/**
 * This test tests stability of Vec API.
 */
public class VecTest extends TestUtil {
    /**
     * Test toCategoricalVec call to return correct domain.
     */
    @Test
    public void testToCategorical() {
        testToCategoricalDomainMatch(TestUtil.vec(0, 1, 0, 1), TestUtil.ar("0", "1"));
        testToCategoricalDomainMatch(TestUtil.vec(1, 2, 3, 4, 5, 6, 7), TestUtil.ar("1", "2", "3", "4", "5", "6", "7"));
        testToCategoricalDomainMatch(TestUtil.vec(0, 1, 2, 99, 4, 5, 6), TestUtil.ar("0", "1", "2", "4", "5", "6", "99"));
    }

    @Test
    public void testCalculatingDomainOnNumericalVecReturnsNull() {
        Vec vec = TestUtil.vec(0, 1, 0, 1);
        Assert.assertTrue("Should be numerical vector", vec.get_type_str().equals(TYPE_STR[T_NUM]));
        String[] domains = vec.domain();
        Assert.assertArrayEquals(null, domains);
        vec.remove();
    }

    @Test
    public void makeCopy() {
        Vec copyOfVec = null;
        Vec expected = null;
        try {
            Scope.enter();
            Vec originalVec = TestUtil.vec(1, 2, 3, 4, 5);
            copyOfVec = originalVec.makeCopy();
            Scope.untrack(copyOfVec._key);
            Scope.exit();
            expected = TestUtil.vec(1, 2, 3, 4, 5);
            TestUtil.assertVecEquals(expected, copyOfVec, 1.0E-5);
        } finally {
            if (copyOfVec != null)
                copyOfVec.remove();

            if (expected != null)
                expected.remove();

        }
    }

    @Test
    public void testMakeConSeq() {
        Vec v;
        v = Vec.makeCon(51966, (2 * (DFLT_CHUNK_SIZE)), false);
        Assert.assertTrue(((v.at(234)) == 51966));
        Assert.assertTrue(((v.espc().length) == 3));
        Assert.assertTrue((((v.espc()[0]) == 0) && ((v.espc()[1]) == (DFLT_CHUNK_SIZE))));
        v.remove(new Futures()).blockForPending();
        v = Vec.makeCon(51966, (3 * (DFLT_CHUNK_SIZE)), false);
        Assert.assertTrue(((v.at(234)) == 51966));
        Assert.assertTrue(((v.at(((3 * (DFLT_CHUNK_SIZE)) - 1))) == 51966));
        Assert.assertTrue(((v.espc().length) == 4));
        Assert.assertTrue(((((v.espc()[0]) == 0) && ((v.espc()[1]) == (DFLT_CHUNK_SIZE))) && ((v.espc()[2]) == ((DFLT_CHUNK_SIZE) * 2))));
        v.remove(new Futures()).blockForPending();
        v = Vec.makeCon(51966, ((3 * (DFLT_CHUNK_SIZE)) + 1), false);
        Assert.assertTrue(((v.at(234)) == 51966));
        Assert.assertTrue(((v.at((3 * (DFLT_CHUNK_SIZE)))) == 51966));
        Assert.assertTrue(((v.espc().length) == 4));
        Assert.assertTrue((((((v.espc()[0]) == 0) && ((v.espc()[1]) == (DFLT_CHUNK_SIZE))) && ((v.espc()[2]) == ((DFLT_CHUNK_SIZE) * 2))) && ((v.espc()[3]) == (((DFLT_CHUNK_SIZE) * 3) + 1))));
        v.remove(new Futures()).blockForPending();
        v = Vec.makeCon(51966, (4 * (DFLT_CHUNK_SIZE)), false);
        Assert.assertTrue(((v.at(234)) == 51966));
        Assert.assertTrue(((v.at(((4 * (DFLT_CHUNK_SIZE)) - 1))) == 51966));
        Assert.assertTrue(((v.espc().length) == 5));
        Assert.assertTrue((((((v.espc()[0]) == 0) && ((v.espc()[1]) == (DFLT_CHUNK_SIZE))) && ((v.espc()[2]) == ((DFLT_CHUNK_SIZE) * 2))) && ((v.espc()[3]) == ((DFLT_CHUNK_SIZE) * 3))));
        v.remove(new Futures()).blockForPending();
    }

    @Test
    public void testMakeSeq() {
        Vec v = Vec.makeSeq((3 * (DFLT_CHUNK_SIZE)), false);
        Assert.assertTrue(((v.at(0)) == 1));
        Assert.assertTrue(((v.at(234)) == 235));
        Assert.assertTrue(((v.at((2 * (DFLT_CHUNK_SIZE)))) == ((2 * (DFLT_CHUNK_SIZE)) + 1)));
        Assert.assertTrue(((v.espc().length) == 4));
        Assert.assertTrue(((((v.espc()[0]) == 0) && ((v.espc()[1]) == (DFLT_CHUNK_SIZE))) && ((v.espc()[2]) == ((DFLT_CHUNK_SIZE) * 2))));
        v.remove(new Futures()).blockForPending();
    }

    @Test
    public void testMakeConStr() {
        Vec source = Vec.makeSeq((2 * (DFLT_CHUNK_SIZE)), false);
        Vec con = source.makeCon(T_STR);
        // check rollup possible
        Assert.assertEquals(0.0, con.base(), 0);
        // set each row unique value
        new MRTask() {
            @Override
            public void map(Chunk c) {
                long firstRow = c._vec.espc()[c.cidx()];
                for (int row = 0; row < (c._len); row++) {
                    c.set(row, ("row_" + (firstRow + row)));
                }
            }
        }.doAll(con);
        // set row values are correct strings
        for (int row = 0; row < (con.length()); row++) {
            Assert.assertEquals((row + "th row has expected value"), ("row_" + row), con.stringAt(row));
        }
        // check rollup possible
        Assert.assertEquals(1, con.sparseRatio(), 0);
        source.remove(new Futures()).blockForPending();
        con.remove(new Futures()).blockForPending();
    }
}

