package water.fvec;


import org.junit.Assert;
import org.junit.Test;
import water.Scope;
import water.TestUtil;
import water.util.Log;
import water.util.PrettyPrint;


/**
 * *
 * This test is written to measure the speed with which NewChunks are written for various data types:
 * integer, long and double
 */
public class NewChunkSpeedTest extends TestUtil {
    int rowNumber = 1000000;

    int rowInterval = 1000;

    double tolerance = 1.0E-10;

    int numberLoops = 2;

    @Test
    public void testParseDoublesConst() {
        double startTime = System.currentTimeMillis();
        for (int index = 0; index < (numberLoops); index++)
            testsForDoubles(true, false, false);

        double endTime = ((System.currentTimeMillis()) - startTime) * 0.001;// change time to seconds

        Log.info("New Chunk test for constant doubles:", (((" time(s) taken for " + (numberLoops)) + " loops is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
    }

    @Test
    public void testParseBigDoublesConst() {
        double startTime = System.currentTimeMillis();
        for (int index = 0; index < (numberLoops); index++)
            testsForDoubles(true, true, false);

        double endTime = (System.currentTimeMillis()) - startTime;// change time to seconds

        Log.info("New Chunk test for big constant doubles:", (((" time taken for " + (numberLoops)) + " loops is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
    }

    @Test
    public void testParseFloatsConst() {
        double startTime = System.currentTimeMillis();
        for (int index = 0; index < (numberLoops); index++)
            testsForDoubles(true, false, true);

        double endTime = (System.currentTimeMillis()) - startTime;// change time to seconds

        Log.info("New Chunk test for constant floats:", (((" time taken for " + (numberLoops)) + " loops is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
    }

    @Test
    public void testParseFloats() {
        double startTime = System.currentTimeMillis();
        for (int index = 0; index < (numberLoops); index++)
            testsForDoubles(false, false, true);

        double endTime = (System.currentTimeMillis()) - startTime;// change time to seconds

        Log.info("New Chunk test for floats:", (((" time taken for " + (numberLoops)) + " loops is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
    }

    @Test
    public void testParseDoubles() {
        double startTime = System.currentTimeMillis();
        for (int index = 0; index < (numberLoops); index++)
            testsForDoubles(false, false, false);

        double endTime = (System.currentTimeMillis()) - startTime;// change time to seconds

        Log.info("New Chunk test for doubles:", (((" time taken for " + (numberLoops)) + " loops is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
    }

    @Test
    public void testParseBigDoubles() {
        double startTime = System.currentTimeMillis();
        for (int index = 0; index < (numberLoops); index++)
            testsForDoubles(false, true, false);

        double endTime = (System.currentTimeMillis()) - startTime;// change time to seconds

        Log.info("New Chunk test for big doubles:", (((" time taken for " + (numberLoops)) + " loops is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
    }

    @Test
    public void testParseInteger() {
        double startTime = System.currentTimeMillis();
        for (int index = 0; index < (numberLoops); index++)
            testsForIntegers(false);

        double endTime = (System.currentTimeMillis()) - startTime;// change time to seconds

        Log.info("New Chunk test for integers:", (((" time taken for " + (numberLoops)) + " loops is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
    }

    @Test
    public void testParseIntegerConst() {
        double startTime = System.currentTimeMillis();
        for (int index = 0; index < (numberLoops); index++)
            testsForIntegers(true);

        double endTime = (System.currentTimeMillis()) - startTime;// change time to seconds

        Log.info("New Chunk test for constant integer:", (((" time taken for " + (numberLoops)) + " loops is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
    }

    // todo: This should be changed to test after Spencer PR is in.
    @Test
    public void testParseLong() {
        double startTime = System.currentTimeMillis();
        for (int index = 0; index < (numberLoops); index++)
            testsForLongs(false);

        double endTime = (System.currentTimeMillis()) - startTime;// change time to seconds

        Log.info("************************************************");
        Log.info("New Chunk test for longs:", (((" time taken for " + (numberLoops)) + " is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
    }

    @Test
    public void testParseLongConsts() {
        double startTime = System.currentTimeMillis();
        for (int index = 0; index < (numberLoops); index++)
            testsForLongs(true);

        double endTime = (System.currentTimeMillis()) - startTime;// change time to seconds

        Log.info("New Chunk test for constant longs:", (((" time taken for " + (numberLoops)) + " is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
    }

    @Test
    public void testParseLongMAXMINV() {
        double startTime = System.currentTimeMillis();
        for (int index = 0; index < (numberLoops); index++) {
            testsForLongsMaxMin(Long.MAX_VALUE);// read in Long.MAX_VALUE

            testsForLongsMaxMin(Long.MIN_VALUE);// read in Long.MIN_VALUE

        }
        double endTime = (System.currentTimeMillis()) - startTime;// change time to seconds

        Log.info("New Chunk test for constant longs:", (((" time taken for " + (numberLoops)) + " is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
    }

    @Test
    public void testParseDataFromFiles() {
        String[] filenames = new String[]{ "smalldata/jira/floatVals.csv", "smalldata/jira/integerVals.csv", "smalldata/jira/longVals.csv", "smalldata/jira/doubleVals.csv", "smalldata/jira/bigDoubleVals.csv" };
        int numLoops = 5 * (numberLoops);
        for (int index = 0; index < (filenames.length); index++) {
            double startTime = System.currentTimeMillis();
            for (int loop = 0; loop < numLoops; loop++) {
                Scope.enter();
                try {
                    Frame f = TestUtil.parse_test_file(filenames[index]);
                    Assert.assertTrue(((f.numRows()) == 100000));
                    Scope.track(f);
                } finally {
                    Scope.exit();
                }
            }
            double endTime = (System.currentTimeMillis()) - startTime;// change time to seconds

            Log.info("*******************************************************");
            Log.info(((((("Parsing: " + (filenames[index])) + " time taken for ") + numLoops) + " loops is ") + (PrettyPrint.msecs(((long) (endTime)), false))));
        }
    }
}

