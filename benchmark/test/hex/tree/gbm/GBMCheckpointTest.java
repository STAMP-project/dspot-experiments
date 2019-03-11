package hex.tree.gbm;


import org.junit.Test;
import water.TestUtil;


public class GBMCheckpointTest extends TestUtil {
    @Test
    public void testCheckpointReconstruction4Multinomial() {
        testCheckPointReconstruction("smalldata/iris/iris.csv", 4, true, 5, 3);
    }

    @Test
    public void testCheckpointReconstruction4Multinomial2() {
        testCheckPointReconstruction("smalldata/junit/cars_20mpg.csv", 1, true, 5, 3);
    }

    @Test
    public void testCheckpointReconstruction4Binomial() {
        testCheckPointReconstruction("smalldata/logreg/prostate.csv", 1, true, 5, 3);
    }

    @Test
    public void testCheckpointReconstruction4Binomial2() {
        testCheckPointReconstruction("smalldata/junit/cars_20mpg.csv", 7, true, 2, 2);
    }

    @Test
    public void testCheckpointReconstruction4Regression() {
        testCheckPointReconstruction("smalldata/logreg/prostate.csv", 8, false, 5, 3);
    }

    @Test
    public void testCheckpointReconstruction4Regression2() {
        testCheckPointReconstruction("smalldata/junit/cars_20mpg.csv", 1, false, 5, 3);
    }
}

