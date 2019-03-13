package water.rapids.ast.prims.reducers;


import java.util.Random;
import org.junit.Test;
import water.Key;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.util.Log;


/**
 * Test the AstTopN.java class
 */
public class AstTopNTest extends TestUtil {
    static Frame _train;// store training data


    double _tolerance = 1.0E-12;

    public Random _rand = new Random();

    // --------------------------------------------------------------------------------------------------------------------
    // Tests
    // --------------------------------------------------------------------------------------------------------------------
    /**
     * Loading in a dataset containing data from -1000000 to 1000000 multiplied by 1.1 as the float column in column 1.
     * The other column (column 0) is a long data type with maximum data value at 2^63.
     */
    @Test
    public void TestTopBottomN() {
        Scope.enter();
        double[] checkPercent = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };// , 11, 12, 13, 14, 15, 17, 19}; // complete test

        int numRuns = 1;
        double testPercent = 0;
        // store test percentage
        Frame topLong = null;
        Frame topFloat = null;
        Frame bottomLong = null;
        Frame bottomFloat = null;
        // load in the datasets with the answers
        AstTopNTest._train = TestUtil.parse_test_file(Key.make("topbottom"), "smalldata/jira/TopBottomN.csv.zip");
        topFloat = TestUtil.parse_test_file(Key.make("top20"), "smalldata/jira/Top20Per.csv.zip");
        topLong = topFloat.extractFrame(0, 1);
        bottomFloat = TestUtil.parse_test_file(Key.make("bottom20"), "smalldata/jira/Bottom20Per.csv.zip");
        bottomLong = bottomFloat.extractFrame(0, 1);
        Scope.track(AstTopNTest._train);
        Scope.track(topLong);
        Scope.track(topFloat);
        Scope.track(bottomFloat);
        Scope.track(bottomLong);
        try {
            for (int index = 0; index < numRuns; index++) {
                // randomly choose 4 percentages to test
                testPercent = checkPercent[_rand.nextInt(checkPercent.length)];
                int testNo = _rand.nextInt(4);
                Log.info(("Percentage is " + testPercent));
                if (testNo == 0) {
                    Log.info("Testing top N long.");
                    testTopBottom(topLong, testPercent, 1, "0", _tolerance);
                }
                if (testNo == 1) {
                    Log.info("Testing top N float.");
                    testTopBottom(topFloat, testPercent, 1, "1", _tolerance);// test top % Float

                }
                if (testNo == 2) {
                    Log.info("Testing bottom N long.");
                    testTopBottom(bottomLong, testPercent, (-1), "0", _tolerance);// test bottom % Long

                }
                if (testNo == 3) {
                    Log.info("Testing bottom N float.");
                    testTopBottom(bottomFloat, testPercent, (-1), "1", _tolerance);// test bottom % Float

                }
            }
        } finally {
            Scope.exit();
        }
    }
}

