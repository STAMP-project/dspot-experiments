package water.fvec;


import java.util.Random;
import junit.framework.Assert;
import org.junit.Test;
import water.TestUtil;


public class VecStatsTest extends TestUtil {
    @Test
    public void test() {
        Random random = new Random();
        Vec[] vecs = new Vec[1];
        AppendableVec vec = new AppendableVec(Vec.newKey());
        for (int i = 0; i < 2; i++) {
            NewChunk chunk = new NewChunk(vec, i);
            for (int r = 0; r < 1000; r++)
                chunk.addNum(random.nextInt(1000));

            chunk.close(i, null);
        }
        vecs[0] = vec.close(null);
        Frame frame = new Frame(null, vecs);
        // Make sure we test the multi-chunk case
        assert (frame.vecs()[0].nChunks()) > 1;
        long rows = frame.numRows();
        Vec v = frame.vecs()[0];
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double mean = 0;
        double sigma = 0;
        for (int r = 0; r < rows; r++) {
            double d = v.at(r);
            if (d < min)
                min = d;

            if (d > max)
                max = d;

            mean += d;
        }
        mean /= rows;
        for (int r = 0; r < rows; r++) {
            double d = v.at(r);
            sigma += (d - mean) * (d - mean);
        }
        sigma = Math.sqrt((sigma / (rows - 1)));
        double epsilon = 1.0E-9;
        Assert.assertEquals(max, v.max(), epsilon);
        Assert.assertEquals(min, v.min(), epsilon);
        Assert.assertEquals(mean, v.mean(), epsilon);
        Assert.assertEquals(sigma, v.sigma(), epsilon);
        frame.delete();
    }
}

