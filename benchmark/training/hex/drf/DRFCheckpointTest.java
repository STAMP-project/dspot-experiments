package hex.drf;


import hex.drf.DRF.DRFModel;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;


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

    /**
     * Test if reconstructed initial frame match the last iteration
     * of DRF model builder.
     *
     * <p>This test verify regression model.</p>
     */
    @Test
    public void testCheckpointReconstruction4Regression() {
        testCheckPointReconstruction("smalldata/logreg/prostate.csv", 8, false, 5, 3);
    }

    private enum WhereToCollect {

        NONE,
        AFTER_BUILD,
        AFTER_RECONSTRUCTION;}

    // Helper class with a hook to collect tree cols
    static class DRFWithHooks extends DRF {
        DRFCheckpointTest.WhereToCollect collectPoint;

        public float[][] treesCols;

        @Override
        protected void initWorkFrame(DRFModel initialModel, Frame fr) {
            super.initWorkFrame(initialModel, fr);
            if ((collectPoint) == (DRFCheckpointTest.WhereToCollect.AFTER_RECONSTRUCTION))
                treesCols = collectTreeCols(fr);

        }

        // Collect ntrees temporary results in expensive way
        @Override
        protected void cleanUp(Frame fr, Timer t_build) {
            if ((collectPoint) == (DRFCheckpointTest.WhereToCollect.AFTER_BUILD))
                treesCols = collectTreeCols(fr);

            super.cleanUp(fr, t_build);
        }

        private float[][] collectTreeCols(Frame fr) {
            float[][] r = new float[((int) (_nrows))][_nclass];
            for (int c = 0; c < (_nclass); c++) {
                Vec ctree = vec_tree(fr, c);
                for (int row = 0; row < (_nrows); row++) {
                    r[row][c] = ctree.at8(row);
                }
            }
            return r;
        }
    }
}

