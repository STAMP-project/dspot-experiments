package water.fvec;


import Vec.Writer;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class NewVectorTest extends TestUtil {
    static final double EPSILON = 1.0E-6;

    // Test that various collections of parsed numbers compress as expected.
    @Test
    public void testCompression() {
        // A simple no-compress
        testImpl(new long[]{ 120, 12, 120 }, new int[]{ 0, 1, 0 }, C0LChunk.class, false);
        // A simple no-compress
        testImpl(new long[]{ 122, 3, 44 }, new int[]{ 0, 0, 0 }, C1NChunk.class, false);
        // A simple compressed boolean vector
        testImpl(new long[]{ 1, 0, 1 }, new int[]{ 0, 0, 0 }, CBSChunk.class, false);
        // 12.2, -3.0, 4.4 ==> 122e-1, -30e-1, 44e-1
        testImpl(new long[]{ 0, 128, 255 }, new int[]{ 0, 0, 0 }, C1NChunk.class, false);
        // Scaled-byte compression
        // 12.2, -3.0, 4.4 ==> 122e-1, -30e-1, 44e-1
        testImpl(new long[]{ 0, Long.MAX_VALUE, 128, 254 }, new int[]{ 0, Integer.MIN_VALUE, 0, 0 }, C1Chunk.class, false);
        // 12.2, -3.0, 4.4 ==> 122e-1, -30e-1, 44e-1
        testImpl(new long[]{ 0, Long.MAX_VALUE, 128, 255 }, new int[]{ 0, Integer.MIN_VALUE, 0, 0 }, C2Chunk.class, false);
        // 12.2, -3.0, 4.4 ==> 122e-1, -30e-1, 44e-1
        testImpl(new long[]{ 0, Long.MAX_VALUE, 128, 255, (Short.MAX_VALUE) - 1 }, new int[]{ 0, Integer.MIN_VALUE, 0, 0, 0 }, C2Chunk.class, false);
        // Scaled-byte compression
        // 12.2, -3.0, 4.4 ==> 122e-1, -30e-1, 44e-1
        testImpl(new long[]{ 122, -3, 44 }, new int[]{ -1, 0, -1 }, C1SChunk.class, true);
        // Positive-scale byte compression
        // 1000, 2000, 3000 ==> 1e3, 2e3, 3e3
        testImpl(new long[]{ 0, 10, 254 }, new int[]{ 0, 0, 0 }, C1NChunk.class, false);
        // Positive-scale byte compression
        // 1000, 2000, 3000 ==> 1e3, 2e3, 3e3
        testImpl(new long[]{ 1000, 200, 30 }, new int[]{ 0, 1, 2 }, C1SChunk.class, false);
        // A simple no-compress short
        testImpl(new long[]{ 1000, 200, 32767, -32767, 32 }, new int[]{ 0, 1, 0, 0, 3 }, C2Chunk.class, false);
        // Scaled-byte compression
        // 50100, 50101, 50123, 49999
        testImpl(new long[]{ 50100, 50101, 50123, 49999 }, new int[]{ 0, 0, 0, 0 }, C1SChunk.class, false);
        // Scaled-byte compression
        // 51000, 50101, 50123, 49999
        testImpl(new long[]{ 51000, 50101, 50123, 49999 }, new int[]{ 0, 0, 0, 0 }, C2SChunk.class, false);
        // Scaled-short compression
        // 50100.0, 50100.1, 50123, 49999
        testImpl(new long[]{ 501000, 501001, 50123, 49999 }, new int[]{ -1, -1, 0, 0 }, C2SChunk.class, true);
        // Integers
        testImpl(new long[]{ 123456, 2345678, 34567890 }, new int[]{ 0, 0, 0 }, C4Chunk.class, false);
        // // Floats
        testImpl(new long[]{ 1234, 2345, 314 }, new int[]{ -1, -5, -2 }, C4SChunk.class, true);
        // Doubles
        testImpl(new long[]{ 1234, 2345678, 31415 }, new int[]{ 40, 10, -40 }, C8DChunk.class, true);
        testImpl(new long[]{ -581504, -477862, 342349 }, new int[]{ -5, -18, -5 }, C8DChunk.class, true);
    }

    // Testing writes to an existing Chunk causing inflation
    @Test
    public void testWrites() {
        Key key = Vec.newKey();
        AppendableVec av = new AppendableVec(key);
        NewChunk nv = new NewChunk(av, 0);
        nv._ls = new long[]{ 0, 0, 0, 0 };// A 4-row chunk

        nv._xs = new int[]{ 0, 0, 0, 0 };
        nv._len = nv._sparseLen = nv._ls.length;
        long[] ls = nv._ls;
        nv.close(0, null);
        Futures fs = new Futures();
        Vec vec = av.close(fs);
        fs.blockForPending();
        Assert.assertEquals(nv._len, vec.length());
        // Compression returns the expected constant-compression-type:
        Chunk c0 = vec.chunkForChunkIdx(0);
        Assert.assertTrue((("Found chunk class " + (c0.getClass())) + " but expected C0LChunk"), (c0 instanceof C0LChunk));
        Assert.assertEquals(false, c0.hasFloat());
        // Also, we can decompress correctly
        for (int i = 0; i < (ls.length); i++)
            Assert.assertEquals(0, c0.at0(i), ((c0.at0(i)) * (NewVectorTest.EPSILON)));

        // Now write a zero into slot 0
        vec.set(0, 0);
        Assert.assertEquals(0, vec.at8(0));
        Chunk c1 = vec.chunkForChunkIdx(0);
        Assert.assertTrue((("Found chunk class " + (c1.getClass())) + " but expected C0LChunk"), (c1 instanceof C0LChunk));
        // Now write a one into slot 1; chunk should inflate into boolean vector.
        vec.set(1, 1);
        Assert.assertEquals(1, vec.at8(1));// Immediate visibility in current thread

        Chunk c2 = vec.chunkForChunkIdx(0);// Look again at the installed chunk

        Assert.assertTrue((("Found chunk class " + (c2.getClass())) + " but expected CBSChunk"), (c2 instanceof CBSChunk));
        // Now write a two into slot 2; chunk should inflate into byte vector
        vec.set(2, 2);
        Assert.assertEquals(2, vec.at8(2));// Immediate visibility in current thread

        Chunk c3 = vec.chunkForChunkIdx(0);// Look again at the installed chunk

        Assert.assertTrue((("Found chunk class " + (c3.getClass())) + " but expected C1NChunk"), (c3 instanceof C1NChunk));
        vec.set(3, 3);
        Assert.assertEquals(3, vec.at8(3));// Immediate visibility in current thread

        Chunk c4 = vec.chunkForChunkIdx(0);// Look again at the installed chunk

        Assert.assertTrue((("Found chunk class " + (c4.getClass())) + " but expected C1NChunk"), (c4 instanceof C1NChunk));
        // Now doing the same for multiple writes, close() only at the end for better speed
        Vec.Writer vw = vec.open();
        vw.set(1, 4);
        vw.set(2, 5);
        vw.set(3, 6);
        vw.close();// update chunks in DKV

        Assert.assertEquals(4, vec.at8(1));
        Assert.assertEquals(5, vec.at8(2));
        Assert.assertEquals(6, vec.at8(3));
        UKV.remove(av._key);
    }
}

