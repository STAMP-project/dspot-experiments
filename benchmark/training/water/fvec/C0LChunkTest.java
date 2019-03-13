package water.fvec;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class C0LChunkTest extends TestUtil {
    @Test
    public void test_inflate_impl() {
        final int K = 1 << 18;
        for (long l : new long[]{ (Long.MIN_VALUE) + 1, Long.MAX_VALUE, 23420384L, 0L, -23423423400023L/* , 8234234028823049934L this would overflow the double mantissa */
         }) {
            NewChunk nc = new NewChunk(null, 0);
            for (int i = 0; i < K; ++i)
                nc.addNum(l, 0);

            Assert.assertEquals(K, nc._len);
            if (l != 0L)
                Assert.assertEquals((l == 0L ? 0 : K), nc._sparseLen);
            // special case for sparse length

            int len = nc.len();
            Chunk cc = nc.compress();
            Assert.assertEquals(K, cc._len);
            Assert.assertTrue((cc instanceof C0LChunk));
            for (int i = 0; i < K; ++i)
                Assert.assertEquals(l, cc.at8(i));

            double[] sparsevals = new double[cc.sparseLenZero()];
            int[] sparseids = new int[cc.sparseLenZero()];
            cc.getSparseDoubles(sparsevals, sparseids);
            for (int i = 0; i < (sparsevals.length); ++i) {
                if (cc.isNA(sparseids[i]))
                    Assert.assertTrue(Double.isNaN(sparsevals[i]));
                else
                    Assert.assertTrue(((cc.at8(sparseids[i])) == (sparsevals[i])));

            }
            double[] densevals = new double[cc.len()];
            cc.getDoubles(densevals, 0, cc.len());
            for (int i = 0; i < (densevals.length); ++i) {
                if (cc.isNA(i))
                    Assert.assertTrue(Double.isNaN(densevals[i]));
                else
                    Assert.assertTrue(((cc.atd(i)) == (densevals[i])));

            }
            nc = new NewChunk(null, 0);
            cc.extractRows(nc, 0, len);
            Assert.assertEquals(K, nc._len);
            Assert.assertEquals((l == 0 ? 0 : K), nc._sparseLen);
            for (int i = 0; i < K; ++i)
                Assert.assertEquals(l, nc.at8(i));

            Chunk cc2 = nc.compress();
            Assert.assertEquals(K, cc2._len);
            Assert.assertTrue((cc2 instanceof C0LChunk));
            for (int i = 0; i < K; ++i)
                Assert.assertEquals(l, cc2.at8(i));

            Assert.assertTrue(Arrays.equals(cc._mem, cc2._mem));
        }
    }
}

