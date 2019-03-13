package water.fvec;


import NewChunk.Value;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


/**
 * Test for CBSChunk implementation.
 *
 * The objective of the test is to verify compression method, not the H2O environment.
 *
 * NOTE: The test is attempt to not require H2O infrastructure to run.
 * It tries to use Mockito (perhaps PowerMock in the future) to wrap
 * expected results. In this case expectation is little bit missused
 * since it is used to avoid DKV call.
 */
public class CBSChunkTest extends TestUtil {
    // Test two bits per value compression used for case with NAs
    // used for data containing NAs
    @Test
    public void test2BPV() {
        // Simple case only compressing 2*3bits into 1byte including 1 NA
        testImpl(new long[]{ 0, Long.MAX_VALUE, 1 }, new int[]{ 0, Integer.MIN_VALUE, 0 }, 2, 2, 1, 1);
        // Filling whole byte, one NA
        testImpl(new long[]{ 1, Long.MAX_VALUE, 0, 1 }, new int[]{ 0, Integer.MIN_VALUE, 0, 0 }, 2, 0, 1, 1);
        // crossing the border of two bytes by 4bits, one NA
        testImpl(new long[]{ 1, 0, Long.MAX_VALUE, 1, 0, 0 }, new int[]{ 0, 0, Integer.MIN_VALUE, 0, 0, 0 }, 2, 4, 2, 1);
        // Two full bytes, 5 NAs
        testImpl(new long[]{ Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, 1, 0, Long.MAX_VALUE, 1, Long.MAX_VALUE }, new int[]{ Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0, Integer.MIN_VALUE, 0, Integer.MIN_VALUE }, 2, 0, 2, 5);
    }

    @Test
    public void test_inflate_impl() {
        for (int l = 0; l < 2; ++l) {
            NewChunk nc = new NewChunk(null, 0);
            int[] vals = new int[]{ 0, 1, 0, 1, 0, 0, 1 };
            if (l == 1)
                nc.addNA();

            for (int v : vals)
                nc.addNum(v);

            nc.addNA();
            Chunk cc = nc.compress();
            Assert.assertEquals((((vals.length) + 1) + l), cc.len());
            Assert.assertTrue((cc instanceof CBSChunk));
            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc.at80((l + i)));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc.at8((l + i)));

            Assert.assertTrue(cc.isNA0(((vals.length) + l)));
            Assert.assertTrue(cc.isNA(((vals.length) + l)));
            nc = new NewChunk(null, 0);
            cc.inflate_impl(nc);
            nc.values(0, nc.len());
            Assert.assertEquals((((vals.length) + l) + 1), nc.sparseLen());
            Assert.assertEquals((((vals.length) + l) + 1), nc.len());
            Iterator<NewChunk.Value> it = nc.values(0, (((vals.length) + 1) + l));
            for (int i = 0; i < (((vals.length) + 1) + l); ++i)
                Assert.assertTrue(((it.next().rowId0()) == i));

            Assert.assertTrue((!(it.hasNext())));
            if (l == 1) {
                Assert.assertTrue(nc.isNA0(0));
                Assert.assertTrue(nc.isNA(0));
            }
            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], nc.at80((l + i)));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], nc.at8((l + i)));

            Assert.assertTrue(nc.isNA0(((vals.length) + l)));
            Assert.assertTrue(nc.isNA(((vals.length) + l)));
            Chunk cc2 = nc.compress();
            Assert.assertEquals((((vals.length) + 1) + l), cc.len());
            Assert.assertTrue((cc2 instanceof CBSChunk));
            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc2.at80((l + i)));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc2.at8((l + i)));

            Assert.assertTrue(cc2.isNA0(((vals.length) + l)));
            Assert.assertTrue(cc2.isNA(((vals.length) + l)));
            Assert.assertTrue(Arrays.equals(cc._mem, cc2._mem));
        }
    }
}

