package hex.grep;


import GrepModel.GrepParameters;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Job;
import water.Key;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.NFSFileVec;
import water.fvec.Vec;


public class GrepTest extends TestUtil {
    @Test
    public void testIris() {
        GrepModel kmm = null;
        Frame fr = null;
        try {
            // TODO: fix with original regex
            // String regex = "Iris-versicolor";
            String regex = "ver..c\\wl[ob]r";
            NFSFileVec nfs = TestUtil.makeNfsFileVec("smalldata/iris/iris_wheader.csv");
            DKV.put((fr = new Frame(Key.<Frame>make(), new String[]{ "text" }, new Vec[]{ nfs })));
            // long now = System.nanoTime();
            GrepModel.GrepParameters parms = new GrepModel.GrepParameters();
            parms._train = fr._key;
            parms._regex = regex;
            Job<GrepModel> job = trainModel();
            kmm = job.get();
            // final long dt = System.nanoTime() - now;
            // System.out.println(dt);
            String[] matches = kmm._output._matches;
            Assert.assertEquals("Number of matches", 50, matches.length);
            for (int i = 0; i < (matches.length); i++) {
                Assert.assertEquals(("Wrong @" + i), "versicolor", matches[i]);
            }
            job.remove();
        } finally {
            if (fr != null)
                fr.remove();

            if (kmm != null)
                kmm.delete();

        }
    }
}

