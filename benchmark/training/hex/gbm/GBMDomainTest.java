package hex.gbm;


import org.junit.Test;
import water.TestUtil;
import water.Vec;


public class GBMDomainTest extends TestUtil {
    private abstract class PrepData {
        abstract Vec prep(Frame fr);
    }

    /**
     * The scenario:
     *  - test data contains an input column which contains less enum values than the same column in train data.
     *  In this case we should provide correct values mapping:
     *  A - 0
     *  B - 1    B - 0                                   B - 1
     *  C - 2    D - 1    mapping should remap it into:  D - 3
     *  D - 3
     */
    @Test
    public void testModelAdapt() {
        runAndScoreGBM("./smalldata/test/classifier/coldom_train_1.csv", "./smalldata/test/classifier/coldom_test_1.csv", new GBMDomainTest.PrepData() {
            @Override
            Vec prep(Frame fr) {
                return fr.vecs()[((fr.numCols()) - 1)];
            }
        });
    }

    /**
     * The scenario:
     *  - test data contains an input column which contains more enum values than the same column in train data.
     *  A - 0
     *  B - 1    B - 0                                   B - 1
     *  C - 2    X - 1    mapping should remap it into:  X - NA
     *  D - 3
     */
    @Test
    public void testModelAdapt2() {
        runAndScoreGBM("./smalldata/test/classifier/coldom_train_1.csv", "./smalldata/test/classifier/coldom_test_1_2.csv", new GBMDomainTest.PrepData() {
            @Override
            Vec prep(Frame fr) {
                return fr.vecs()[((fr.numCols()) - 1)];
            }
        });
    }
}

