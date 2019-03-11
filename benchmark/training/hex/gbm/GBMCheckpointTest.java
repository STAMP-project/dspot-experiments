package hex.gbm;


import hex.gbm.GBM.GBMModel;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;


public class GBMCheckpointTest extends TestUtil {
    // Test for multinomial
    @Test
    public void testCheckpointReconstruction4Multinomial() {
        testCheckPointReconstruction("smalldata/iris/iris.csv", 4, true, 5, 3);
    }

    // Binomial model checkpointing
    @Test
    public void testCheckpointReconstruction4Binomial() {
        testCheckPointReconstruction("smalldata/logreg/prostate.csv", 1, true, 5, 3);
    }

    // And then test regression
    @Test
    public void testCheckpointReconstruction4Regression() {
        testCheckPointReconstruction("smalldata/logreg/prostate.csv", 8, false, 5, 3);
    }

    private enum WhereToCollect {

        NONE,
        AFTER_BUILD,
        AFTER_RECONSTRUCTION;}

    static class GBMWithHooks extends GBM {
        GBMCheckpointTest.WhereToCollect collectPoint;

        public float[][] treesCols;

        @Override
        protected void initWorkFrame(GBMModel initialModel, Frame fr) {
            super.initWorkFrame(initialModel, fr);
            if ((collectPoint) == (GBMCheckpointTest.WhereToCollect.AFTER_RECONSTRUCTION)) {
                // debugPrintTreeColumns(fr);
                treesCols = collectTreeCols(fr);
            }
        }

        // Collect ntrees temporary results in expensive way
        @Override
        protected void cleanUp(Frame fr, Timer t_build) {
            if ((collectPoint) == (GBMCheckpointTest.WhereToCollect.AFTER_BUILD)) {
                // debugPrintTreeColumns(fr);
                treesCols = collectTreeCols(fr);
            }
            super.cleanUp(fr, t_build);
        }

        private float[][] collectTreeCols(Frame fr) {
            float[][] r = new float[((int) (_nrows))][_nclass];
            for (int c = 0; c < (_nclass); c++) {
                Vec ctree = vec_tree(fr, c);
                for (int row = 0; row < (_nrows); row++) {
                    r[row][c] = ((float) (ctree.at(row)));
                }
            }
            return r;
        }
    }
}

