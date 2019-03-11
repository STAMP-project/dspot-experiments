package hex.drf;


import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.Utils;


public class DRFModelAdaptTest extends TestUtil {
    private abstract class PrepData {
        abstract Vec prep(Frame fr);

        int needAdaptation(Frame fr) {
            return fr.numCols();
        }
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
    // @Ignore
    @Test
    public void testModelAdapt1() {
        testModelAdaptation("./smalldata/test/classifier/coldom_train_1.csv", "./smalldata/test/classifier/coldom_test_1.csv", new DRFModelAdaptTest.PrepData() {
            @Override
            Vec prep(Frame fr) {
                return fr.vecs()[((fr.numCols()) - 1)];
            }
        }, true);
    }

    /**
     * The scenario:
     *  - test data contains an input column which contains more enum values than the same column in train data.
     *  A - 0
     *  B - 1    B - 0                                   B - 1
     *  C - 2    X - 1    mapping should remap it into:  X - NA
     *  D - 3
     */
    // @Ignore
    @Test
    public void testModelAdapt1_2() {
        testModelAdaptation("./smalldata/test/classifier/coldom_train_1.csv", "./smalldata/test/classifier/coldom_test_1_2.csv", new DRFModelAdaptTest.PrepData() {
            @Override
            Vec prep(Frame fr) {
                return fr.vecs()[((fr.numCols()) - 1)];
            }
        }, true);
    }

    // @Ignore
    @Test
    public void testModelAdapt2() {
        testModelAdaptation("./smalldata/test/classifier/coldom_train_2.csv", "./smalldata/test/classifier/coldom_test_2.csv", new DRFModelAdaptTest.PrepData() {
            @Override
            Vec prep(Frame fr) {
                return fr.vecs()[fr.find("R")];
            }

            @Override
            int needAdaptation(Frame fr) {
                return 0;
            }
        }, true);
    }

    /**
     * Test adaptation of numeric values in response column.
     */
    // @Ignore
    @Test
    public void testModelAdapt3() {
        testModelAdaptation("./smalldata/test/classifier/coldom_train_3.csv", "./smalldata/test/classifier/coldom_test_3.csv", new DRFModelAdaptTest.PrepData() {
            @Override
            Vec prep(Frame fr) {
                return fr.vecs()[((fr.numCols()) - 1)];
            }
        }, false);
    }

    @Test
    public void testBasics_1() {
        // Simple domain mapping
        Assert.assertArrayEquals(DRFModelAdaptTest.a(0, 1, 2, 3), Utils.mapping(DRFModelAdaptTest.a(0, 1, 2, 3)));
        Assert.assertArrayEquals(DRFModelAdaptTest.a(0, 1, 2, (-1), 3), Utils.mapping(DRFModelAdaptTest.a(0, 1, 2, 4)));
        Assert.assertArrayEquals(DRFModelAdaptTest.a(0, (-1), 1), Utils.mapping(DRFModelAdaptTest.a((-1), 1)));
        Assert.assertArrayEquals(DRFModelAdaptTest.a(0, (-1), 1, (-1), 2), Utils.mapping(DRFModelAdaptTest.a((-1), 1, 3)));
    }

    @Test
    public void testBasics_2() {
        Assert.assertArrayEquals(DRFModelAdaptTest.a(2, 30, 400, 5000), Utils.compose(Utils.mapping(DRFModelAdaptTest.a(0, 1, 2, 3)), DRFModelAdaptTest.a(2, 30, 400, 5000)));
        Assert.assertArrayEquals(DRFModelAdaptTest.a(2, 30, 400, (-1), 5000), Utils.compose(Utils.mapping(DRFModelAdaptTest.a(0, 1, 2, 4)), DRFModelAdaptTest.a(2, 30, 400, 5000)));
        Assert.assertArrayEquals(DRFModelAdaptTest.a(2, (-1), 30), Utils.compose(Utils.mapping(DRFModelAdaptTest.a((-1), 1)), DRFModelAdaptTest.a(2, 30, 400, 5000)));
        Assert.assertArrayEquals(DRFModelAdaptTest.a(2, (-1), 30, (-1), 400), Utils.compose(Utils.mapping(DRFModelAdaptTest.a((-1), 1, 3)), DRFModelAdaptTest.a(2, 30, 400, 5000)));
    }
}

