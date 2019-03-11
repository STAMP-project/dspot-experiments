package hex;


import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.H2O;
import water.Key;
import water.TestUtil;
import water.fvec.Frame;
import water.util.ArrayUtils;
import water.util.FrameUtils;


public class FrameSplitterTest extends TestUtil {
    @Test
    public void splitTinyFrame() {
        Frame dataset = null;
        double[] ratios = TestUtil.ard(0.5F);
        Frame[] splits = null;
        try {
            dataset = ArrayUtils.frame(TestUtil.ar("COL1"), TestUtil.ear(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
            FrameSplitter fs = new FrameSplitter(dataset, ratios, FrameUtils.generateNumKeys(dataset._key, ((ratios.length) + 1)), null);
            H2O.submitTask(fs).join();
            splits = fs.getResult();
            Assert.assertEquals("The expected number of split frames is ratios.length+1", ((ratios.length) + 1), splits.length);
            for (Frame f : splits)
                Assert.assertEquals("The expected number of rows in partition.", 5, f.numRows());

        } finally {
            // cleanup
            if (dataset != null)
                dataset.delete();

            if (splits != null)
                for (Frame sf : splits)
                    if (sf != null)
                        sf.delete();



        }
    }

    /**
     * Test for [PUB-410] - AIOOBE in NewChunk if there are NA in String chunks.
     */
    @Test
    public void splitStringFrame() {
        // NA in data
        String fname = "FrameSplitterTest1.hex";
        long[] chunkLayout = TestUtil.ar(2L, 2L, 3L);
        String[][] data = TestUtil.ar(TestUtil.ar("A", "B"), TestUtil.ar(null, "C"), TestUtil.ar("D", "E", "F"));
        FrameSplitterTest.testScenario(fname, chunkLayout, data);
        // NAs everywhere
        fname = "test2.hex";
        chunkLayout = TestUtil.ar(2L, 1L);
        data = TestUtil.ar(TestUtil.ar(((String) (null)), ((String) (null))), TestUtil.ar(((String) (null))));
        FrameSplitterTest.testScenario(fname, chunkLayout, data);
        // NAs at the end of chunks
        fname = "test3.hex";
        chunkLayout = TestUtil.ar(2L, 2L, 2L);
        data = TestUtil.ar(TestUtil.ar("A", "B"), TestUtil.ar("C", null), TestUtil.ar(((String) (null)), ((String) (null))));
        FrameSplitterTest.testScenario(fname, chunkLayout, data);
    }

    @Test
    public void splitStringFramePUBDEV468() {
        // NAs at the end of chunks
        String fname = "test4.hex";
        long[] chunkLayout = TestUtil.ar(3L, 3L);
        String[][] data = TestUtil.ar(TestUtil.ar("A", null, "B"), TestUtil.ar("C", "D", "E"));
        FrameSplitterTest.testScenario(fname, chunkLayout, data);
    }

    @Test
    public void computeEspcTest() {
        // Split inside chunk
        long[] espc = TestUtil.ar(0L, 2297L, 4591, 7000L);
        double[] ratios = TestUtil.ard(0.5F);
        long[][] result = FrameSplitter.computeEspcPerSplit(espc, espc[((espc.length) - 1)], ratios);
        Assert.assertArrayEquals(TestUtil.ar(TestUtil.ar(0L, 2297L, 3500L), TestUtil.ar(0L, 1091L, 3500L)), result);
        // Split inside chunk #2
        espc = TestUtil.ar(0L, 1500L, 3000L, 4500L, 7000L);
        ratios = TestUtil.ard(0.5F);
        result = FrameSplitter.computeEspcPerSplit(espc, espc[((espc.length) - 1)], ratios);
        Assert.assertArrayEquals(TestUtil.ar(TestUtil.ar(0L, 1500L, 3000L, 3500L), TestUtil.ar(0L, 1000L, 3500L)), result);
        // Split on chunk boundary
        espc = TestUtil.ar(0L, 1500L, 3500L, 4500L, 7000L);
        ratios = TestUtil.ard(0.5F);
        result = FrameSplitter.computeEspcPerSplit(espc, espc[((espc.length) - 1)], ratios);
        Assert.assertArrayEquals(TestUtil.ar(TestUtil.ar(0L, 1500L, 3500L), TestUtil.ar(0L, 1000L, 3500L)), result);
    }

    @Test
    public void test() {
        // Load data
        Frame f = TestUtil.parse_test_file(Key.make("iris.csv"), "smalldata/iris/iris.csv");
        long numRows = f.numRows();
        Assert.assertEquals(150, numRows);
        // Perform frame split via API
        try {
            SplitFrame sf = new SplitFrame(f, new double[]{ 0.5, 0.5 }, new Key[]{ Key.make("train.hex"), Key.make("test.hex") });
            // Invoke the job
            sf.exec().get();
            Assert.assertTrue("The job is not in STOPPED state, but in ", sf._job.isStopped());
            Key[] ksplits = sf._destination_frames;
            Frame[] fsplits = new Frame[ksplits.length];
            for (int i = 0; i < (ksplits.length); i++)
                fsplits[i] = DKV.get(ksplits[i]).get();

            Assert.assertEquals("Number of splits", 2, ksplits.length);
            Assert.assertEquals("1. split 75rows", 75, fsplits[0].numRows());
            Assert.assertEquals("2. split 75rows", 75, fsplits[1].numRows());
            fsplits[0].delete();
            fsplits[1].delete();
        } finally {
            f.delete();
        }
    }
}

