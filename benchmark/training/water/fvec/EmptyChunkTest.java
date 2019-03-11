package water.fvec;


import org.junit.Test;
import water.TestUtil;


/**
 * Testing empty chunks.
 *
 * This test simulates workflow used from Sparkling Water.
 * But it also serves to test chunk layouts with 0-length chunks.
 */
public class EmptyChunkTest extends TestUtil {
    /**
     * Scenario: chunk(2-rows)|chunk(0 rows)|chunk(2-rows)|chunk(0 rows) - trailing empty chunk should be removed
     */
    @Test
    public void testEmptyChunk0() {
        String fname = "test0.hex";
        long[] chunkLayout = TestUtil.ar(2L, 0L, 2L, 0L);
        long[] expectedLayout = TestUtil.ar(2L, 0L, 2L);
        testScenario(fname, chunkLayout, expectedLayout);
    }

    /**
     * Scenario: chunk(0-rows)|chunk(0 rows)|chunk(0-rows)|chunk(2 rows)
     */
    @Test
    public void testEmptyChunk1() {
        String fname = "EmptyChunkTest1.hex";
        long[] chunkLayout = TestUtil.ar(0L, 0L, 0L, 2L);
        testScenario(fname, chunkLayout);
    }

    /**
     * Scenario: chunk(2-rows)|chunk(0 rows)|chunk(0-rows)|chunk(2 rows)
     */
    @Test
    public void testEmptyChunk2() {
        String fname = "test2.hex";
        long[] chunkLayout = TestUtil.ar(2L, 0L, 0L, 2L);
        testScenario(fname, chunkLayout);
    }
}

