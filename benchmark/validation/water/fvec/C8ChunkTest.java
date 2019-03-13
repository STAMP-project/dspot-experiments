package water.fvec;


import NewChunk.Value;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class C8ChunkTest extends TestUtil {
    @Test
    public void test_inflate_impl() {
        for (int l = 0; l < 2; ++l) {
            NewChunk nc = new NewChunk(null, 0);
            long[] vals = new long[]{ (Long.MIN_VALUE) + 1, Integer.MIN_VALUE, 0, Integer.MAX_VALUE, Long.MAX_VALUE };
            if (l == 1)
                nc.addNA();

            for (long v : vals)
                nc.addNum(v, 0);

            nc.addNA();// -9223372036854775808l

            Chunk cc = nc.compress();
            Assert.assertEquals((((vals.length) + 1) + l), cc.len());
            Assert.assertTrue((cc instanceof C8Chunk));
            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc.at80((l + i)));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc.at8((l + i)));

            Assert.assertTrue(cc.isNA0(((vals.length) + l)));
            Assert.assertTrue(cc.isNA(((vals.length) + l)));
            nc = new NewChunk(null, 0);
            cc.inflate_impl(nc);
            nc.values(0, nc.len());
            if (l == 1)
                Assert.assertTrue(cc.isNA0(0));

            Assert.assertEquals((((vals.length) + l) + 1), nc.sparseLen());
            Assert.assertEquals((((vals.length) + l) + 1), nc.len());
            Iterator<NewChunk.Value> it = nc.values(0, (((vals.length) + 1) + l));
            for (int i = 0; i < (((vals.length) + 1) + l); ++i)
                Assert.assertTrue(((it.next().rowId0()) == i));

            Assert.assertTrue((!(it.hasNext())));
            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], nc.at80((l + i)));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], nc.at8((l + i)));

            Assert.assertTrue(cc.isNA0(((vals.length) + l)));
            Chunk cc2 = nc.compress();
            Assert.assertEquals((((vals.length) + 1) + l), cc.len());
            Assert.assertTrue((cc2 instanceof C8Chunk));
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

