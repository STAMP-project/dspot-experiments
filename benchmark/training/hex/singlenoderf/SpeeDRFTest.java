package hex.singlenoderf;


import Job.JobState;
import Tree.SelectStatType;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.Log;


// public static void main(String[] Args) {
// kaggle_credit();
// covtype();
// }
public class SpeeDRFTest extends TestUtil {
    // Test kaggle/creditsample-test data
    @Test
    public void kaggle_credit() {
        Key destTrain = Key.make("credit");
        Frame fr = TestUtil.parseFrame(destTrain, "smalldata/kaggle/creditsample-training.csv.gz");
        // Check parsed dataset
        final int n = 1;
        Assert.assertEquals("Number of chunks", n, fr.anyVec().nChunks());
        Assert.assertEquals("Number of rows", 150000, fr.numRows());
        Assert.assertEquals("Number of cols", 12, fr.numCols());
        // setup DRF values
        Vec response = fr.vecs()[1];
        int[] ignored_cols = new int[]{ 6 };
        SpeeDRF spdrf = new SpeeDRF();
        spdrf.source = fr;
        spdrf.response = response;
        spdrf.ignored_cols = ignored_cols;
        spdrf.ntrees = 3;
        spdrf.max_depth = 30;
        spdrf.select_stat_type = SelectStatType.GINI;
        spdrf.seed = 42;
        Log.info("Invoking the SpeeDRF task.");
        spdrf.invoke();
        SpeeDRFModel m = UKV.get(spdrf.dest());
        Assert.assertTrue(((m.get_params().state) == (JobState.DONE)));// HEX-1817

        testHTML(m);
        Assert.assertEquals("Number of classes", 2, m.classes());
        Assert.assertEquals("Number of trees", 3, m.size());
        m.delete();
        fr.delete();
    }

    @Test
    public void covtype() {
        Frame fr = TestUtil.parseFrame(Key.make("covtype.hex"), "smalldata/covtype/covtype.20k.data");
        // Key okey = loadAndParseFile("covtype.hex", "../datasets/UCI/UCI-large/covtype/covtype.data");
        // Key okey = loadAndParseFile("covtype.hex", "/home/0xdiag/datasets/standard/covtype.data");
        // Key okey = loadAndParseFile("mnist.hex", "/home/0xdiag/datasets/mnist/mnist8m.csv");
        // setup default values for DRF
        Vec response = fr.vecs()[54];
        SpeeDRF spdrf = new SpeeDRF();
        spdrf.source = fr;
        spdrf.response = response;
        spdrf.ntrees = 8;
        spdrf.max_depth = 999;
        spdrf.select_stat_type = SelectStatType.ENTROPY;
        spdrf.seed = 42;
        spdrf.invoke();
        SpeeDRFModel m = UKV.get(spdrf.dest());
        Assert.assertTrue(((m.get_params().state) == (JobState.DONE)));// HEX-1817

        testHTML(m);
        Assert.assertEquals("Number of classes", 7, m.classes());
        Assert.assertEquals("Number of trees", 8, m.size());
        m.delete();
        fr.delete();
    }
}

