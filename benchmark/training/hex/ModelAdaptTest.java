package hex;


import ModelMetrics.MetricBuilder;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.ArrayUtils;

import static Model.Output.<init>;


public class ModelAdaptTest extends TestUtil {
    // Private junk model class to test Adaption logic
    private static class AModel extends Model {
        AModel(Key key, Parameters p, Output o) {
            super(key, p, o);
        }

        /* nclasses+1 */
        @Override
        protected double[] score0(double[] data, /* ncols */
        double[] preds) {
            throw H2O.unimpl();
        }

        @Override
        public MetricBuilder makeMetricBuilder(String[] domain) {
            throw H2O.unimpl();
        }

        static class AParms extends Model.Parameters {
            public String algoName() {
                return "A";
            }

            public String fullName() {
                return "A";
            }

            public String javaName() {
                return ModelAdaptTest.AModel.class.getName();
            }

            @Override
            public long progressUnits() {
                return 0;
            }
        }

        static class AOutput extends Model.Output {}
    }

    @Test
    public void testModelAdaptMultinomial() {
        Frame trn = TestUtil.parse_test_file("smalldata/junit/mixcat_train.csv");
        ModelAdaptTest.AModel.AParms p = new ModelAdaptTest.AModel.AParms();
        ModelAdaptTest.AModel.AOutput o = new ModelAdaptTest.AModel.AOutput();
        o.setNames(trn.names());
        o._domains = trn.domains();
        trn.remove();
        ModelAdaptTest.AModel am = new ModelAdaptTest.AModel(Key.make(), p, o);
        Frame tst = TestUtil.parse_test_file("smalldata/junit/mixcat_test.csv");
        Frame adapt = new Frame(tst);
        String[] warns = am.adaptTestForTrain(adapt, true, true);
        Assert.assertTrue(((ArrayUtils.find(warns, "Test/Validation dataset column 'Feature_1' has levels not trained on: [D]")) != (-1)));
        Assert.assertTrue(((ArrayUtils.find(warns, "Test/Validation dataset is missing column 'Const': substituting in a column of NaN")) != (-1)));
        Assert.assertTrue(((ArrayUtils.find(warns, "Test/Validation dataset is missing column 'Useless': substituting in a column of NaN")) != (-1)));
        Assert.assertTrue(((ArrayUtils.find(warns, "Test/Validation dataset column 'Response' has levels not trained on: [W]")) != (-1)));
        // Feature_1: merged test & train domains
        Assert.assertArrayEquals(adapt.vec("Feature_1").domain(), new String[]{ "A", "B", "C", "D" });
        // Const: all NAs
        Assert.assertTrue(adapt.vec("Const").isBad());
        // Useless: all NAs
        Assert.assertTrue(adapt.vec("Useless").isBad());
        // Response: merged test & train domains
        Assert.assertArrayEquals(adapt.vec("Response").domain(), new String[]{ "X", "Y", "Z", "W" });
        Frame.deleteTempFrameAndItsNonSharedVecs(adapt, tst);
        tst.remove();
    }

    // If the train set has a categorical, and the test set column is all missing
    // then by-default it is treated as a numeric column (no domain).  Verify that
    // we make an empty domain mapping
    @Test
    public void testModelAdaptMissing() {
        ModelAdaptTest.AModel.AParms p = new ModelAdaptTest.AModel.AParms();
        ModelAdaptTest.AModel.AOutput o = new ModelAdaptTest.AModel.AOutput();
        Vec cat = TestUtil.vec(new String[]{ "A", "B" }, 0, 1, 0, 1);
        Frame trn = new Frame();
        trn.add("cat", cat);
        o.setNames(trn.names());
        o._domains = trn.domains();
        trn.remove();
        ModelAdaptTest.AModel am = new ModelAdaptTest.AModel(Key.make(), p, o);
        Frame tst = new Frame();
        tst.add("cat", cat.makeCon(Double.NaN));// All NAN/missing column

        Frame adapt = new Frame(tst);
        String[] warns = am.adaptTestForTrain(adapt, true, true);
        Assert.assertTrue(((warns.length) == 0));// No errors during adaption

        Frame.deleteTempFrameAndItsNonSharedVecs(adapt, tst);
        tst.remove();
    }

    // If the train set has a categorical, and the test set column is numeric
    // then convert it to a categorical
    @Test
    public void testModelAdaptConvert() {
        ModelAdaptTest.AModel.AParms p = new ModelAdaptTest.AModel.AParms();
        ModelAdaptTest.AModel.AOutput o = new ModelAdaptTest.AModel.AOutput();
        Frame trn = new Frame();
        trn.add("dog", TestUtil.vec(new String[]{ "A", "B" }, 0, 1, 0, 1));
        o.setNames(trn.names());
        o._domains = trn.domains();
        trn.remove();
        ModelAdaptTest.AModel am = new ModelAdaptTest.AModel(Key.make(), p, o);
        Frame tst = new Frame();
        tst.add("dog", TestUtil.vec(2, 3, 2, 3));
        Frame adapt = new Frame(tst);
        boolean saw_iae = false;
        try {
            am.adaptTestForTrain(adapt, true, true);
        } catch (IllegalArgumentException iae) {
            saw_iae = true;
        }
        Assert.assertTrue(saw_iae);
        Frame.deleteTempFrameAndItsNonSharedVecs(adapt, tst);
        tst.remove();
    }
}

