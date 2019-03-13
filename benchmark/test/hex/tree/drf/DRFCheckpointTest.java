package hex.tree.drf;


import org.junit.Test;
import water.TestUtil;
import water.exceptions.H2OIllegalArgumentException;


public class DRFCheckpointTest extends TestUtil {
    /**
     * Test if reconstructed initial frame match the last iteration
     * of DRF model builder.
     *
     * <p>This test verify multinominal model.</p>
     */
    @Test
    public void testCheckpointReconstruction4Multinomial() {
        testCheckPointReconstruction("smalldata/iris/iris.csv", 4, true, 5, 3);
    }

    @Test
    public void testCheckpointReconstruction4Multinomial2() {
        testCheckPointReconstruction("smalldata/junit/cars_20mpg.csv", 1, true, 5, 3);
    }

    /**
     * Test if reconstructed initial frame match the last iteration
     * of DRF model builder.
     *
     * <p>This test verify binominal model.</p>
     */
    @Test
    public void testCheckpointReconstruction4Binomial() {
        testCheckPointReconstruction("smalldata/logreg/prostate.csv", 1, true, 5, 3);
    }

    @Test
    public void testCheckpointReconstruction4Binomial2() {
        testCheckPointReconstruction("smalldata/junit/cars_20mpg.csv", 7, true, 1, 1);
    }

    /**
     * Test throwing the right exception if non-modifiable parameter is specified.
     */
    @Test(expected = H2OIllegalArgumentException.class)
    public void testCheckpointWrongParams() {
        testCheckPointReconstruction("smalldata/iris/iris.csv", 4, true, 5, 3, 0.2F, 0.67F);
    }

    /**
     * Test if reconstructed initial frame match the last iteration
     * of DRF model builder.
     *
     * <p>This test verify regression model.</p>
     */
    @Test
    public void testCheckpointReconstruction4Regression() {
        testCheckPointReconstruction("smalldata/logreg/prostate.csv", 8, false, 4, 3);
    }

    @Test
    public void testCheckpointReconstruction4Regression2() {
        testCheckPointReconstruction("smalldata/junit/cars_20mpg.csv", 1, false, 4, 3);
    }
}

