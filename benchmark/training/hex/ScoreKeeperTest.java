package hex;


import ScoreKeeper.StoppingMetric;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.util.Log;


public class ScoreKeeperTest extends TestUtil {
    @Test
    public void testConvergenceScoringHistory() {
        Random rng = new Random(12648430);
        int count = 0;
        while ((count++) < 100) {
            boolean moreIsBetter = rng.nextBoolean();
            ScoreKeeper.StoppingMetric metric = (moreIsBetter) ? StoppingMetric.AUC : StoppingMetric.logloss;
            double tol = (rng.nextFloat()) * 0.1;
            int N = 5 + (rng.nextInt(10));
            double[] values = new double[N];
            for (int i = 0; i < N; ++i) {
                // random walk around linearly increasing (or decreasing) values around 20 (not around 0 to avoid zero-crossing issues)
                values[i] = (moreIsBetter ? 10 + (((double) (i)) / N) : 10 - (((double) (i)) / N)) + ((rng.nextGaussian()) * 0.33);
            }
            ScoreKeeper[] sk = ScoreKeeperTest.fillScoreKeeperArray(values, moreIsBetter);
            Log.info();
            Log.info(("series: " + (Arrays.toString(values))));
            Log.info(("moreIsBetter: " + moreIsBetter));
            Log.info(("relative tolerance: " + tol));
            for (int k = (values.length) - 1; k > 0; k--) {
                boolean c = /* verbose */
                ScoreKeeperTest.stopEarly(values, k, tol, moreIsBetter, false);
                boolean d = /* classification */
                /* verbose */
                ScoreKeeper.stopEarly(sk, k, true, metric, tol, "JUnit's", false);
                // for debugging
                // Log.info("Checking for stopping condition with k=" + k + ": " + c + " " + d);
                if (c || d)
                    Log.info(("Stopped for k=" + k));

                // if (!c && !d && k==1) Log.info("Still improving.");
                // if (d!=c) {
                // Log.info("k="+ k);
                // Log.info("tol="+ tol);
                // Log.info("moreIsBetter="+ moreIsBetter);
                // stopEarly(values, k, tol, moreIsBetter, true /*verbose*/);
                // ScoreKeeper.stopEarly(sk, k, true /*classification*/, metric, tol, "JUnit", true /*verbose*/);
                // }
                Assert.assertTrue(((((("For k=" + k) + ", JUnit: ") + c) + ", ScoreKeeper: ") + d), (c == d));
            }
        } 
    }

    @Test
    public void testGridSearch() {
        Random rng = new Random(912559);
        int count = 0;
        while ((count++) < 100) {
            final boolean moreIsBetter = rng.nextBoolean();
            Double[] Dvalues;
            double tol;
            if (true) {
                // option 1: random values
                int N = 5 + (rng.nextInt(10));
                tol = (rng.nextDouble()) * 0.1;
                Dvalues = new Double[N];
                for (int i = 0; i < N; ++i)
                    Dvalues[i] = 10 + (rng.nextDouble());
                // every grid search models has a random score between 10 and 11 (not around 0 to avoid zero-crossing issues)

            } else {
                // option 2: manual values
                tol = 0;
                Dvalues = new Double[]{ 0.91, 0.92, 0.95, 0.94, 0.93 };// in order of occurrence

            }
            // sort to get "leaderboard"
            Arrays.sort(Dvalues, new Comparator<Double>() {
                @Override
                public int compare(Double o1, Double o2) {
                    int val = ((o1.doubleValue()) < (o2.doubleValue())) ? 1 : (o1.doubleValue()) == (o2.doubleValue()) ? 0 : -1;
                    if (moreIsBetter)
                        val = -val;

                    return val;
                }
            });
            double[] values = new double[Dvalues.length];
            for (int i = 0; i < (values.length); ++i)
                values[i] = Dvalues[i].doubleValue();

            Log.info(("Sorted values (leaderboard) - rightmost is best: " + (Arrays.toString(values))));
            for (int k = 1; k < (values.length); ++k) {
                Log.info(("Testing k=" + k));
                ScoreKeeper.StoppingMetric metric = (moreIsBetter) ? StoppingMetric.AUC : StoppingMetric.logloss;
                ScoreKeeper[] sk = ScoreKeeperTest.fillScoreKeeperArray(values, moreIsBetter);
                boolean c = /* verbose */
                ScoreKeeperTest.stopEarly(values, k, tol, moreIsBetter, true);
                boolean d = /* classification */
                /* verbose */
                ScoreKeeper.stopEarly(sk, k, true, metric, tol, "JUnit's", true);
                Assert.assertTrue(((((("For k=" + k) + ", JUnit: ") + c) + ", ScoreKeeper: ") + d), (c == d));
            }
        } 
    }
}

