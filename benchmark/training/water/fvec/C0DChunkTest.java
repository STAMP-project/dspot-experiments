package water.fvec;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class C0DChunkTest extends TestUtil {
    @Test
    public void test_inflate_impl() {
        final int K = 1 << 16;
        for (Double d : new Double[]{ 3.14159265358, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.MAX_VALUE, Double.NaN }) {
            NewChunk nc = new NewChunk(null, 0);
            for (int i = 0; i < K; ++i)
                nc.addNum(d);

            Assert.assertEquals(K, nc._len);
            Assert.assertEquals((Double.isNaN(d) ? 0 : K), nc._sparseLen);
            Assert.assertEquals(K, nc.sparseLenZero());
            Assert.assertEquals((Double.isNaN(d) ? 0 : K), nc.sparseLenNA());
            int len = nc.len();
            Chunk cc = nc.compress();
            Assert.assertEquals(K, cc._len);
            Assert.assertTrue((cc instanceof C0DChunk));
            for (int i = 0; i < K; ++i)
                Assert.assertEquals(d, cc.atd(i), Math.ulp(d));

            for (int i = 0; i < K; ++i)
                Assert.assertEquals(d, cc.at_abs(i), Math.ulp(d));

            double[] sparsevals = new double[cc.sparseLenZero()];
            int[] sparseids = new int[cc.sparseLenZero()];
            cc.getSparseDoubles(sparsevals, sparseids);
            for (int i = 0; i < (sparsevals.length); ++i) {
                if (cc.isNA(sparseids[i]))
                    Assert.assertTrue(Double.isNaN(sparsevals[i]));
                else
                    Assert.assertTrue(((cc.atd(sparseids[i])) == (sparsevals[i])));

            }
            double[] densevals = new double[cc.len()];
            cc.getDoubles(densevals, 0, cc.len());
            for (int i = 0; i < (densevals.length); ++i) {
                if (cc.isNA(i))
                    Assert.assertTrue(Double.isNaN(densevals[i]));
                else
                    Assert.assertTrue(((cc.atd(i)) == (densevals[i])));

            }
            nc = cc.extractRows(new NewChunk(null, 0), 0, len);
            Assert.assertEquals(K, nc._len);
            Assert.assertEquals((Double.isNaN(d) ? 0 : K), nc._sparseLen);
            Assert.assertEquals(K, nc.sparseLenZero());
            Assert.assertEquals((Double.isNaN(d) ? 0 : K), nc.sparseLenNA());
            for (int i = 0; i < K; ++i)
                Assert.assertEquals(d, nc.atd(i), Math.ulp(d));

            for (int i = 0; i < K; ++i)
                Assert.assertEquals(d, nc.at_abs(i), Math.ulp(d));

            Chunk cc2 = nc.compress();
            Assert.assertEquals(K, cc2._len);
            Assert.assertTrue((cc2 instanceof C0DChunk));
            for (int i = 0; i < K; ++i)
                Assert.assertEquals(d, cc2.atd(i), Math.ulp(d));

            for (int i = 0; i < K; ++i)
                Assert.assertEquals(d, cc2.at_abs(i), Math.ulp(d));

            Assert.assertTrue(Arrays.equals(cc._mem, cc2._mem));
        }
    }
}

