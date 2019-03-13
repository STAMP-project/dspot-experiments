package water.fvec;


import NewChunk.Value;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class CXIChunkTest extends TestUtil {
    @Test
    public void test_inflate_impl() {
        for (int l = 0; l < 2; ++l) {
            NewChunk nc = new NewChunk(null, 0);
            int[] vals = new int[]{ 0, 0, 0, Integer.MAX_VALUE, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, (Integer.MIN_VALUE) + 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            if (l == 1)
                nc.addNA();

            for (int v : vals)
                nc.addNum(v, 0);

            nc.addNA();
            Chunk cc = nc.compress();
            Assert.assertEquals((((vals.length) + 1) + l), cc.len());
            Assert.assertTrue((cc instanceof CXIChunk));
            if (l == 1) {
                Assert.assertTrue(cc.isNA0(0));
                Assert.assertTrue(cc.isNA(0));
            }
            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc.at80((i + l)));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc.at8((i + l)));

            Assert.assertTrue(cc.isNA0(((vals.length) + l)));
            Assert.assertTrue(cc.isNA(((vals.length) + l)));
            nc = new NewChunk(null, 0);
            cc.inflate_impl(nc);
            nc.values(0, nc.len());
            Assert.assertEquals((((vals.length) + l) + 1), nc.len());
            Assert.assertEquals(((2 + 1) + l), nc.sparseLen());
            Iterator<NewChunk.Value> it = nc.values(0, (((vals.length) + 1) + l));
            if (l == 1)
                Assert.assertTrue(((it.next().rowId0()) == 0));

            Assert.assertTrue(((it.next().rowId0()) == (3 + l)));
            Assert.assertTrue(((it.next().rowId0()) == (101 + l)));
            Assert.assertTrue(((it.next().rowId0()) == ((vals.length) + l)));
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
            Assert.assertTrue((cc2 instanceof CXIChunk));
            if (l == 1) {
                Assert.assertTrue(cc2.isNA0(0));
                Assert.assertTrue(cc2.isNA(0));
            }
            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc2.at80((i + l)));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc2.at8((i + l)));

            Assert.assertTrue(cc2.isNA0(((vals.length) + l)));
            Assert.assertTrue(cc2.isNA(((vals.length) + l)));
            Assert.assertTrue(Arrays.equals(cc._mem, cc2._mem));
        }
    }
}

