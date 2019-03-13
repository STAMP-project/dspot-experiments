package water.fvec;


import NewChunk.Value;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class CX0ChunkTest extends TestUtil {
    @Test
    public void test_inflate_impl() {
        NewChunk nc = new NewChunk(null, 0);
        int[] vals = new int[]{ 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        for (int v : vals)
            nc.addNum(v, 0);

        Chunk cc = nc.compress();
        Assert.assertEquals(vals.length, cc.len());
        Assert.assertTrue((cc instanceof CX0Chunk));
        for (int i = 0; i < (vals.length); ++i)
            Assert.assertEquals(vals[i], cc.at80(i));

        nc = new NewChunk(null, 0);
        cc.inflate_impl(nc);
        nc.values(0, nc.len());
        Assert.assertEquals(vals.length, nc.len());
        Assert.assertEquals(2, nc.sparseLen());
        Iterator<NewChunk.Value> it = nc.values(0, vals.length);
        Assert.assertTrue(((it.next().rowId0()) == 3));
        Assert.assertTrue(((it.next().rowId0()) == 101));
        Assert.assertTrue((!(it.hasNext())));
        for (int i = 0; i < (vals.length); ++i)
            Assert.assertEquals(vals[i], nc.at80(i));

        for (int i = 0; i < (vals.length); ++i)
            Assert.assertEquals(vals[i], nc.at8(i));

        Chunk cc2 = nc.compress();
        Assert.assertEquals(vals.length, cc.len());
        Assert.assertTrue((cc2 instanceof CX0Chunk));
        for (int i = 0; i < (vals.length); ++i)
            Assert.assertEquals(vals[i], cc2.at80(i));

        for (int i = 0; i < (vals.length); ++i)
            Assert.assertEquals(vals[i], cc2.at8(i));

        Assert.assertTrue(Arrays.equals(cc._mem, cc2._mem));
    }
}

