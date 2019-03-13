package hex.splitframe;


import org.junit.Assert;
import org.junit.Test;
import water.MRTask;
import water.TestUtil;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.fvec.FrameTestUtil;
import water.fvec.NewChunk;
import water.fvec.Vec;
import water.parser.BufferedString;


/**
 * Tests for shuffle split frame.
 */
public class ShuffleSplitFrameTest extends TestUtil {
    /**
     * Reported as PUBDEV-452
     */
    @Test
    public void testShuffleSplitOnStringColumn() {
        long[] chunkLayout = ar(2L, 2L, 3L);
        String[][] data = ar(ar("A", "B"), ar(null, "C"), ar("D", "E", "F"));
        Frame f = FrameTestUtil.createFrame("ShuffleSplitTest1.hex", chunkLayout, data);
        ShuffleSplitFrameTest.testScenario(f, flat(data));
        chunkLayout = ar(3L, 3L);
        data = ar(ar("A", null, "B"), ar("C", "D", "E"));
        f = FrameTestUtil.createFrame("test2.hex", chunkLayout, data);
        ShuffleSplitFrameTest.testScenario(f, flat(data));
    }

    /* this test makes sure that the rows of the split frames are preserved (including UUID) */
    @Test
    public void testShuffleSplitWithMultipleColumns() {
        long[] chunkLayout = ar(2L, 2L, 3L);
        String[][] data = ar(ar("1", "2"), ar(null, "3"), ar("4", "5", "6"));
        Frame f = null;
        Frame tmpFrm = FrameTestUtil.createFrame("ShuffleSplitMCTest1.hex", chunkLayout, data);
        try {
            f = new MRTask() {
                @Override
                public void map(Chunk[] cs, NewChunk[] ncs) {
                    for (int i = 0; i < (cs[0]._len); i++) {
                        BufferedString bs = cs[0].atStr(new BufferedString(), i);
                        int val = (bs == null) ? 0 : Integer.parseInt(bs.toString());
                        ncs[0].addStr(bs);
                        ncs[1].addNum(val);
                        ncs[2].addNum(i);
                        ncs[3].addUUID(i, val);
                    }
                }
            }.doAll(new byte[]{ Vec.T_STR, Vec.T_NUM, Vec.T_NUM, Vec.T_UUID }, tmpFrm).outputFrame();
        } finally {
            tmpFrm.delete();
        }
        ShuffleSplitFrameTest.testScenario(f, flat(data), new MRTask() {
            @Override
            public void map(Chunk[] cs) {
                for (int i = 0; i < (cs[0]._len); i++) {
                    BufferedString bs = cs[0].atStr(new BufferedString(), i);
                    int expectedVal = (bs == null) ? 0 : Integer.parseInt(bs.toString());
                    int expectedIndex = ((int) (cs[2].atd(i)));
                    Assert.assertEquals(((double) (expectedVal)), cs[1].atd(i), 1.0E-5);
                    Assert.assertEquals(expectedIndex, ((int) (cs[3].at16l(i))));
                    Assert.assertEquals(expectedVal, ((int) (cs[3].at16h(i))));
                }
            }
        });
    }
}

