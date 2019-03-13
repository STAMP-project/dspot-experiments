package hex;


import AUC2.ThresholdCriterion.absolute_mcc;
import AUC2.ThresholdCriterion.f1;
import AUC2.ThresholdCriterion.precision;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;


public class AUCTest extends TestUtil {
    @Test
    public void testAUC0() {
        double auc0 = AUC2.perfectAUC(new double[]{ 0, 0.5, 0.5, 1 }, new double[]{ 0, 0, 1, 1 });
        Assert.assertEquals(0.875, auc0, 1.0E-7);
        // Flip the tied actuals
        double auc1 = AUC2.perfectAUC(new double[]{ 0, 0.5, 0.5, 1 }, new double[]{ 0, 1, 0, 1 });
        Assert.assertEquals(0.875, auc1, 1.0E-7);
        // Area is 10/12 (TPS=4, FPS=3, so area is 4x3 or 12 units; 10 are under).
        double auc2 = AUC2.perfectAUC(new double[]{ 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7 }, new double[]{ 0, 0, 1, 1, 0, 1, 1 });
        Assert.assertEquals(0.8333333, auc2, 1.0E-7);
        // Sorted probabilities.  At threshold 1e-6 flips from false to true, on
        // average.  However, there are a lot of random choices at 1e-3.
        double[] probs = new double[]{ 1.0E-8, 1.0E-7, 1.0E-6, 1.0E-5, 1.0E-4, 0.001, 0.001, 0.001, 0.001, 0.001, 0.001, 0.001, 0.001, 0.001, 0.001, 0.01, 0.1 };
        double[] actls = new double[]{ 0, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1 };
        // Positives & Negatives
        int P = 0;
        for (double a : actls)
            P += ((int) (a));

        int N = (actls.length) - P;
        System.out.println(((("P=" + P) + ", N=") + N));
        // Compute TP & FP for all thresholds
        double[] thresh = new double[]{ 0.1, 0.01, 0.001 + 1.0E-9, 0.001, 0.001 - 1.0E-9, 1.0E-4, 1.0E-5, 1.0E-6, 1.0E-7, 1.0E-8, 0 };
        int[] tp = new int[thresh.length];
        int[] fp = new int[thresh.length];
        int[] tn = new int[thresh.length];
        int[] fn = new int[thresh.length];
        for (int i = 0; i < (probs.length); i++) {
            for (int t = 0; t < (thresh.length); t++) {
                // Not interested if below threshold
                if ((probs[i]) >= (thresh[t]))
                    if ((actls[i]) == 0.0)
                        (fp[t])++;
                    else// False positive

                        (tp[t])++;

                // False negative
                else// True  positive

                    if ((actls[i]) == 0.0)
                        (tn[t])++;
                    else// True  negative

                        (fn[t])++;

                // False negative

            }
        }
        System.out.println(Arrays.toString(tp));
        System.out.println(Arrays.toString(fp));
        System.out.println(Arrays.toString(fn));
        System.out.println(Arrays.toString(tn));
        for (int i = 0; i < (tp.length); i++)
            System.out.print((((("{" + (((double) (tp[i])) / P)) + ",") + (((double) (fp[i])) / N)) + "} "));

        System.out.println();
        // The AUC for this dataset, according to R's ROCR package, is 0.6363636363
        Assert.assertEquals(AUCTest.doAUC(probs, actls), 0.636363636363, 1.0E-5);
        Assert.assertEquals(AUC2.perfectAUC(probs, actls), 0.636363636363, 1.0E-7);
        // Shuffle, check again
        AUCTest.swap(0, 5, probs, actls);
        AUCTest.swap(1, 6, probs, actls);
        AUCTest.swap(7, 15, probs, actls);
        Assert.assertEquals(AUCTest.doAUC(probs, actls), 0.636363636363, 1.0E-5);
        Assert.assertEquals(AUC2.perfectAUC(probs, actls), 0.636363636363, 1.0E-7);
        // Now from a large test file
        double ROCR_auc = 0.7244389;
        Frame fr = TestUtil.parse_test_file("smalldata/junit/auc.csv.gz");
        // Slow; used to confirm the accuracy as we increase bin counts
        // for( int i=10; i<1000; i+=10 ) {
        // AUC2 auc = new AUC2(i,fr.vec("V1"),fr.vec("V2"));
        // System.out.println("bins="+i+", aucERR="+Math.abs(auc._auc-ROCR_auc)/ROCR_auc);
        // Assert.assertEquals(fr.numRows(), auc._p+auc._n);
        // }
        double aucp = AUC2.perfectAUC(fr.vec("V1"), fr.vec("V2"));
        Assert.assertEquals(ROCR_auc, aucp, 1.0E-4);
        AUC2 auc = new AUC2(fr.vec("V1"), fr.vec("V2"));
        Assert.assertEquals(ROCR_auc, auc._auc, 1.0E-4);
        Assert.assertEquals(1.0, precision.max_criterion(auc), 1.0E-4);
        double ROCR_max_abs_mcc = 0.4553512;
        Assert.assertEquals(ROCR_max_abs_mcc, absolute_mcc.max_criterion(auc), 0.001);
        double ROCR_f1 = 0.9920445;// same as ROCR "f" with alpha=0, or alternative beta=1

        Assert.assertEquals(ROCR_f1, f1.max_criterion(auc), 1.0E-4);
        fr.remove();
    }
}

