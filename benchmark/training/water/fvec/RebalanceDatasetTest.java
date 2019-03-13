package water.fvec;


import Vec.T_NUM;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.parser.ParseDataset;
import water.util.FrameUtils;
import water.util.Log;


public class RebalanceDatasetTest extends TestUtil {
    @Test
    public void testProstate() {
        NFSFileVec[] nfs = new NFSFileVec[]{ TestUtil.makeNfsFileVec("smalldata/logreg/prostate.csv"), TestUtil.makeNfsFileVec("smalldata/covtype/covtype.20k.data"), TestUtil.makeNfsFileVec("smalldata/chicago/chicagoCrimes10k.csv.zip") };
        // NFSFileVec.make(find_test_file("bigdata/laptop/usecases/cup98VAL_z.csv"))};
        for (NFSFileVec fv : nfs) {
            Frame fr = ParseDataset.parse(Key.make(), fv._key);
            Key rebalancedKey = Key.make("rebalanced");
            int[] trials = new int[]{ 380, 1, 3, 8, 9, 12, 256, 16, 32, 64, 11, 13 };
            for (int i : trials) {
                Frame rebalanced = null;
                try {
                    Scope.enter();
                    RebalanceDataSet rb = new RebalanceDataSet(fr, rebalancedKey, i);
                    H2O.submitTask(rb);
                    rb.join();
                    rebalanced = DKV.get(rebalancedKey).get();
                    ParseDataset.logParseResults(rebalanced);
                    Assert.assertEquals(rebalanced.numRows(), fr.numRows());
                    Assert.assertEquals(rebalanced.anyVec().nChunks(), i);
                    Assert.assertTrue(TestUtil.isIdenticalUpToRelTolerance(fr, rebalanced, 1.0E-10));
                    Log.info((("Rebalanced into " + i) + " chunks:"));
                    Log.info(FrameUtils.chunkSummary(rebalanced).toString());
                } finally {
                    if (rebalanced != null)
                        rebalanced.delete();

                    Scope.exit();
                }
            }
            if (fr != null)
                fr.delete();

        }
    }

    @Test
    public void testEmptyPubDev5873() {
        Scope.enter();
        try {
            final Frame f = new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(Double.NaN, 1, 2, 3, 4, 5.6, 7)).withChunkLayout(2, 2, 0, 0, 2, 1).build();
            Scope.track(f);
            Assert.assertEquals(0, f.anyVec().chunkForChunkIdx(2)._len);
            Key<Frame> key = Key.make("rebalanced");
            RebalanceDataSet rb = new RebalanceDataSet(f, key, 3);
            H2O.submitTask(rb);
            rb.join();
            Frame rebalanced = key.get();
            Assert.assertNotNull(rebalanced);// no exception, successful completion

            Scope.track(rebalanced);
        } finally {
            Scope.exit();
        }
    }
}

