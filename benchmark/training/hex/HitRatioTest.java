package hex;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class HitRatioTest {
    @Test
    public void testHits() {
        double[] hits = new double[4];
        double[] pred_dist;
        int actual_label;
        // No ties
        // top 1
        Arrays.fill(hits, 0);
        actual_label = 0;
        pred_dist = new double[]{ 0, 0.4F, 0.1F, 0.2F, 0.3F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue(Arrays.equals(hits, new double[]{ 1, 0, 0, 0 }));
        // top-2
        Arrays.fill(hits, 0);
        actual_label = 3;
        pred_dist = new double[]{ 0, 0.4F, 0.1F, 0.2F, 0.3F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue(Arrays.equals(hits, new double[]{ 0, 1, 0, 0 }));
        // top-2
        Arrays.fill(hits, 0);
        actual_label = 0;
        pred_dist = new double[]{ 3, 0.3F, 0.2F, 0.1F, 0.4F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue(Arrays.equals(hits, new double[]{ 0, 1, 0, 0 }));
        // top-3
        Arrays.fill(hits, 0);
        actual_label = 1;
        pred_dist = new double[]{ 3, 0.3F, 0.2F, 0.1F, 0.4F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue(Arrays.equals(hits, new double[]{ 0, 0, 1, 0 }));
        // top-4
        Arrays.fill(hits, 0);
        actual_label = 1;
        pred_dist = new double[]{ 0, 0.4F, 0.1F, 0.3F, 0.2F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue(Arrays.equals(hits, new double[]{ 0, 0, 0, 1 }));
        // 2 Ties
        // actual 1, predicted 0, but tie-break -> top-2 is hit
        Arrays.fill(hits, 0);
        actual_label = 1;
        pred_dist = new double[]{ 0, 0.3F, 0.3F, 0.2F, 0.2F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue(Arrays.equals(hits, new double[]{ 0, 1, 0, 0 }));
        // top-2 or top-3
        Arrays.fill(hits, 0);
        actual_label = 1;
        pred_dist = new double[]{ 2, 0.3F, 0.3F, 0.35F, 0.05F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue(((Arrays.equals(hits, new double[]{ 0, 1, 0, 0 })) || (Arrays.equals(hits, new double[]{ 0, 0, 1, 0 }))));
        // top-3 or top-4
        Arrays.fill(hits, 0);
        actual_label = 1;
        pred_dist = new double[]{ 0, 0.3F, 0.1F, 0.2F, 0.1F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue(((Arrays.equals(hits, new double[]{ 0, 0, 1, 0 })) || (Arrays.equals(hits, new double[]{ 0, 0, 0, 1 }))));
        // 3 Ties
        Arrays.fill(hits, 0);
        actual_label = 1;
        pred_dist = new double[]{ 3, 0.3F, 0.3F, 0.1F, 0.3F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue((((Arrays.equals(hits, new double[]{ 1, 0, 0, 0 })) || (Arrays.equals(hits, new double[]{ 0, 1, 0, 0 }))) || (Arrays.equals(hits, new double[]{ 0, 0, 1, 0 }))));
        Arrays.fill(hits, 0);
        actual_label = 1;
        pred_dist = new double[]{ 3, 0.1F, 0.1F, 0.1F, 0.7F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue((((Arrays.equals(hits, new double[]{ 0, 1, 0, 0 })) || (Arrays.equals(hits, new double[]{ 0, 0, 1, 0 }))) || (Arrays.equals(hits, new double[]{ 0, 0, 0, 1 }))));
        Arrays.fill(hits, 0);
        actual_label = 2;
        pred_dist = new double[]{ 3, 0.1F, 0.1F, 0.1F, 0.7F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue((((Arrays.equals(hits, new double[]{ 0, 1, 0, 0 })) || (Arrays.equals(hits, new double[]{ 0, 0, 1, 0 }))) || (Arrays.equals(hits, new double[]{ 0, 0, 0, 1 }))));
        // 4 Ties
        Arrays.fill(hits, 0);
        actual_label = 1;
        pred_dist = new double[]{ 2, 0.25F, 0.25F, 0.25F, 0.25F };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue(((((Arrays.equals(hits, new double[]{ 1, 0, 0, 0 })) || (Arrays.equals(hits, new double[]{ 0, 1, 0, 0 }))) || (Arrays.equals(hits, new double[]{ 0, 0, 1, 0 }))) || (Arrays.equals(hits, new double[]{ 0, 0, 0, 1 }))));
        // more predictions than K=4, and actual is outside of top-K
        Arrays.fill(hits, 0);
        actual_label = 1;
        pred_dist = new double[]{ 4, 0.15, 0.1, 0.1, 0.25, 0.3, 0.15, 0.2, 0.2 };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue(Arrays.equals(hits, new double[]{ 0, 0, 0, 0 }));
        // more predictions than K=4, and actual is just inside of top-K
        Arrays.fill(hits, 0);
        actual_label = 6;
        pred_dist = new double[]{ 4, 0.15, 0.1, 0.1, 0.25, 0.3, 0.15, 0.2, 0.2 };
        ModelMetricsMultinomial.updateHits(1, actual_label, pred_dist, hits);
        Assert.assertTrue(((Arrays.equals(hits, new double[]{ 0, 0, 1, 0 })) || (Arrays.equals(hits, new double[]{ 0, 0, 0, 1 }))));
    }
}

