package water.fvec;


import C16Chunk._HI_NA;
import C16Chunk._LO_NA;
import java.util.Arrays;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;

import static C16Chunk._HI_NA;
import static C16Chunk._LO_NA;


public class C16ChunkTest extends TestUtil {
    UUID[] sampleVals = new UUID[]{ C16ChunkTest.u(6, 6), C16ChunkTest.u(_LO_NA, ((_HI_NA) + 1)), C16ChunkTest.u(_LO_NA, ((_HI_NA) - 1)), C16ChunkTest.u(((_LO_NA) + 1), _HI_NA), C16ChunkTest.u(((_LO_NA) - 1), _HI_NA), C16ChunkTest.u(((Long.MIN_VALUE) + 1), 0), C16ChunkTest.u(((Long.MIN_VALUE) + 1), 1L), C16ChunkTest.u(((Long.MIN_VALUE) + 1), (-1L)), C16ChunkTest.u(((Long.MIN_VALUE) + 1), ((Long.MIN_VALUE) + 1)), C16ChunkTest.u(0L, ((Long.MIN_VALUE) + 1)), C16ChunkTest.u(1L, ((Long.MIN_VALUE) + 1)), C16ChunkTest.u((-1L), ((Long.MIN_VALUE) + 1)), C16ChunkTest.u(((Long.MAX_VALUE) - 1), 0), C16ChunkTest.u(((Long.MAX_VALUE) - 1), 1L), C16ChunkTest.u(((Long.MAX_VALUE) - 1), (-1L)), C16ChunkTest.u(0L, ((Long.MAX_VALUE) - 1)), C16ChunkTest.u(1L, ((Long.MAX_VALUE) - 1)), C16ChunkTest.u((-1L), ((Long.MAX_VALUE) - 1)), C16ChunkTest.u(((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1)), C16ChunkTest.u(Long.MAX_VALUE, 0), C16ChunkTest.u(Long.MAX_VALUE, 1L), C16ChunkTest.u(Long.MAX_VALUE, (-1L)), C16ChunkTest.u(0L, Long.MAX_VALUE), C16ChunkTest.u(1L, Long.MAX_VALUE), C16ChunkTest.u((-1L), Long.MAX_VALUE), C16ChunkTest.u(Long.MAX_VALUE, Long.MAX_VALUE), C16ChunkTest.u(0L, 0), C16ChunkTest.u(0L, 1L), C16ChunkTest.u(0L, (-1L)), C16ChunkTest.u(1L, 0L), C16ChunkTest.u((-1L), 0L), C16ChunkTest.u(1L, 0L), C16ChunkTest.u(1, 1L), C16ChunkTest.u(1, (-1L)), C16ChunkTest.u((-1L), 1L), C16ChunkTest.u(12312421425L, 12312421426L), C16ChunkTest.u(23523523423L, 23523523424L), C16ChunkTest.u((-823048234L), (-823048235L)), C16ChunkTest.u((-123123L), (-123124L)) };

    @Test
    public void test_inflate_impl() {
        for (int l = 0; l < 2; ++l) {
            boolean haveNA = l == 1;
            NewChunk nc = buildTestData(haveNA);
            int len = nc.len();
            Chunk cc = nc.compress();
            Assert.assertEquals((((sampleVals.length) + 1) + l), cc._len);
            Assert.assertTrue((cc instanceof C16Chunk));
            checkChunk(cc, l, haveNA);
            nc = cc.extractRows(new NewChunk(null, 0), 0, len);
            Assert.assertEquals((((sampleVals.length) + 1) + l), nc._len);
            checkChunk(nc, l, haveNA);
            Chunk cc2 = nc.compress();
            Assert.assertEquals((((sampleVals.length) + 1) + l), cc._len);
            Assert.assertTrue((cc2 instanceof C16Chunk));
            checkChunk(cc2, l, haveNA);
            Assert.assertTrue(Arrays.equals(cc._mem, cc2._mem));
        }
    }

    @Test
    public void test_illegal_values() {
        Chunk cc = buildTestData(false).compress();
        try {
            cc.set_impl(4, _LO_NA, _HI_NA);
            Assert.fail("Expected a failure on adding an illegal value");
        } catch (IllegalArgumentException iae) {
            // as expected
        }
    }
}

