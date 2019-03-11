package water.api;


import org.junit.Assert;
import org.junit.Test;


public class HitRatioTest {
    @Test
    public void testHits() {
        long[] hits = new long[4];
        int actual_label = 3;
        int[] pred_labels = new int[]{ 0, 3, 2, 1 };
        HitRatio.updateHits(hits, actual_label, pred_labels);
        Assert.assertTrue(((hits[0]) == 0));
        Assert.assertTrue(((hits[1]) == 1));
        Assert.assertTrue(((hits[2]) == 1));
        Assert.assertTrue(((hits[3]) == 1));
        actual_label = 0;
        pred_labels = new int[]{ 1, 2, 3, 0 };
        HitRatio.updateHits(hits, actual_label, pred_labels);
        Assert.assertTrue(((hits[0]) == (0 + 0)));
        Assert.assertTrue(((hits[1]) == (1 + 0)));
        Assert.assertTrue(((hits[2]) == (1 + 0)));
        Assert.assertTrue(((hits[3]) == (1 + 1)));
        actual_label = 1;
        pred_labels = new int[]{ 0, 3, 1, 2 };
        HitRatio.updateHits(hits, actual_label, pred_labels);
        Assert.assertTrue(((hits[0]) == ((0 + 0) + 0)));
        Assert.assertTrue(((hits[1]) == ((1 + 0) + 0)));
        Assert.assertTrue(((hits[2]) == ((1 + 0) + 1)));
        Assert.assertTrue(((hits[3]) == ((1 + 1) + 1)));
    }
}

