package water.rapids;


import org.junit.Assert;
import org.junit.Test;
import water.Key;
import water.Keyed;
import water.TestUtil;
import water.fvec.Frame;


public class ZGroupByMedianTest extends TestUtil {
    // Note that if this median test runs before the testSplitCats and/or testGroupbyTableSpeed,
    // we will encounter the leaked key errors. This has been captured in JIRA PUBDEV-PUBDEV-5090.
    @Test
    public void testGroupbyMedian() {
        Frame fr = null;
        String tree = "(GB hex [0] median 1 \"all\")";// Group-By on col 0 median of col 1

        double[] correct_median = new double[]{ 0.49851096435701053, 0.5018318704735285, 0.5018723436256065, 0.5052896538751508, 0.4988730254120379 };// order may not be correct

        try {
            // fr = chkTree(tree, "smalldata/jira/pubdev_4727_median.csv");
            fr = chkTree(tree, "smalldata/jira/pubdev_4727_junit_data.csv");
            for (int index = 0; index < (fr.numRows()); index++) {
                // compare with correct medians
                Assert.assertTrue(((Math.abs(((correct_median[((int) (fr.vec(0).at(index)))]) - (fr.vec(1).at(index))))) < 1.0E-12));
            }
        } finally {
            if (fr != null)
                fr.delete();

            Keyed.remove(Key.make("hex"));
        }
    }
}

