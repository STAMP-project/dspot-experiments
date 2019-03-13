package water.fvec;


import org.junit.Test;
import water.TestUtil;


/**
 * Created by tomasnykodym on 3/28/14.
 */
public class SparseTest extends TestUtil {
    @Test
    public void testDouble() {
        runTest(new double[]{ 2.7182, 3.14, 42 }, Double.NaN, 123.45, CXDChunk.class, CXDChunk.class, C8DChunk.class);
    }

    @Test
    public void testBinary() {
        runTest(new double[]{ 1, 1, 1 }, 1, 1, CX0Chunk.class, CX0Chunk.class, CBSChunk.class);
        runTest(new double[]{ 1, 1, 1 }, Double.NaN, 1, CX0Chunk.class, CXIChunk.class, CBSChunk.class);
    }

    @Test
    public void testInt() {
        runTest(new double[]{ 1, 2, Double.NaN }, 4, 5, CXIChunk.class, CXIChunk.class, C1Chunk.class);
        runTest(new double[]{ 1, 2000, Double.NaN, 3 }, 4, 5, CXIChunk.class, CXIChunk.class, C2Chunk.class);
        runTest(new double[]{ Double.NaN, 2000, 3 }, 400000, 5, CXIChunk.class, CXIChunk.class, C4Chunk.class);
        runTest(new double[]{ 1, Double.NaN, 2000, 3 }, Double.NaN, 1.0E10, CXIChunk.class, CXIChunk.class, C8Chunk.class);
    }
}

