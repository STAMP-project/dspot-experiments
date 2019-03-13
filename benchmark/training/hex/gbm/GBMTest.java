package hex.gbm;


import DException.DistributedException;
import Family.bernoulli;
import Job.JobState;
import hex.gbm.GBM.Family;
import hex.gbm.GBM.GBMModel;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.api.ConfusionMatrix;
import water.util.Log;

import static water.MRTask2.<init>;


public class GBMTest extends TestUtil {
    private abstract class PrepData {
        abstract int prep(Frame fr);
    }

    static final String[] ignored_aircols = new String[]{ "DepTime", "ArrTime", "AirTime", "ArrDelay", "DepDelay", "TaxiIn", "TaxiOut", "Cancelled", "CancellationCode", "Diverted", "CarrierDelay", "WeatherDelay", "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsDepDelayed" };

    @Test
    public void testGBMRegression() {
        File file = TestUtil.find_test_file("./smalldata/gbm_test/Mfgdata_gaussian_GBM_testing.csv");
        Key fkey = NFSFileVec.make(file);
        Key dest = Key.make("mfg.hex");
        GBM gbm = new GBM();
        // The Builder
        GBM.GBMModel gbmmodel = null;// The Model

        try {
            Frame fr = gbm.source = ParseDataset2.parse(dest, new Key[]{ fkey });
            UKV.remove(fkey);
            gbm.classification = false;// Regression

            gbm.family = Family.AUTO;
            gbm.response = fr.vecs()[1];// Row in col 0, dependent in col 1, predictor in col 2

            gbm.ntrees = 1;
            gbm.max_depth = 1;
            gbm.min_rows = 1;
            gbm.nbins = 20;
            gbm.cols = new int[]{ 2 };// Just column 2

            gbm.validation = null;
            gbm.learn_rate = 1.0F;
            gbm.score_each_iteration = true;
            gbm.invoke();
            gbmmodel = UKV.get(gbm.dest());
            Assert.assertTrue(((gbmmodel.get_params().state) == (JobState.DONE)));// HEX-1817

            Frame preds = gbm.score(gbm.source);
            double sq_err = new GBMTest.CompErr().doAll(gbm.response, preds.vecs()[0])._sum;
            double mse = sq_err / (preds.numRows());
            Assert.assertEquals(79152.1233, mse, 0.1);
            preds.delete();
        } finally {
            gbm.source.delete();
            // Remove original hex frame key
            if ((gbm.validation) != null)
                gbm.validation.delete();
            // Remove validation dataset if specified

            if (gbmmodel != null)
                gbmmodel.delete();
            // Remove the model

            gbm.remove();// Remove GBM Job

        }
    }

    private static class CompErr extends MRTask2<GBMTest.CompErr> {
        double _sum;

        @Override
        public void map(Chunk resp, Chunk pred) {
            double sum = 0;
            for (int i = 0; i < (resp._len); i++) {
                double err = (resp.at(i)) - (pred.at(i));
                sum += err * err;
            }
            _sum = sum;
        }

        @Override
        public void reduce(GBMTest.CompErr ce) {
            _sum += ce._sum;
        }
    }

    @Test
    public void testBasicGBM() {
        // Regression tests
        basicGBM("./smalldata/cars.csv", "cars.hex", new GBMTest.PrepData() {
            int prep(Frame fr) {
                UKV.remove(fr.remove("name")._key);
                return ~(fr.find("economy (mpg)"));
            }
        });
        // Classification tests
        basicGBM("./smalldata/test/test_tree.csv", "tree.hex", new GBMTest.PrepData() {
            int prep(Frame fr) {
                return 1;
            }
        });
        basicGBM("./smalldata/test/test_tree_minmax.csv", "tree_minmax.hex", new GBMTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("response");
            }
        });
        basicGBM("./smalldata/logreg/prostate.csv", "prostate.hex", new GBMTest.PrepData() {
            int prep(Frame fr) {
                Assert.assertEquals(380, fr.numRows());
                // Remove patient ID vector
                UKV.remove(fr.remove("ID")._key);
                // Prostate: predict on CAPSULE
                return fr.find("CAPSULE");
            }
        });
        basicGBM("./smalldata/cars.csv", "cars.hex", new GBMTest.PrepData() {
            int prep(Frame fr) {
                UKV.remove(fr.remove("name")._key);
                return fr.find("cylinders");
            }
        });
        basicGBM("./smalldata/airlines/allyears2k_headers.zip", "air.hex", new GBMTest.PrepData() {
            int prep(Frame fr) {
                for (String s : GBMTest.ignored_aircols)
                    UKV.remove(fr.remove(s)._key);

                return fr.find("IsArrDelayed");
            }
        });
        // basicGBM("../datasets/UCI/UCI-large/covtype/covtype.data","covtype.hex",
        // new PrepData() {
        // int prep(Frame fr) {
        // assertEquals(581012,fr.numRows());
        // for( int ign : IGNS )
        // UKV.remove(fr.remove("C"+Integer.toString(ign))._key);
        // // Covtype: predict on last column
        // return fr.numCols()-1;
        // }
        // });
    }

    @Test
    public void testBasicGBMFamily() {
        Scope.enter();
        // Classification with Bernoulli family
        basicGBM("./smalldata/logreg/prostate.csv", "prostate.hex", new GBMTest.PrepData() {
            int prep(Frame fr) {
                Assert.assertEquals(380, fr.numRows());
                // Remove patient ID vector
                UKV.remove(fr.remove("ID")._key);
                // Change CAPSULE and RACE to categoricals
                Scope.track(fr.factor(fr.find("CAPSULE"))._key);
                Scope.track(fr.factor(fr.find("RACE"))._key);
                // Prostate: predict on CAPSULE
                return fr.find("CAPSULE");
            }
        }, bernoulli);
        Scope.exit();
    }

    // Test-on-Train.  Slow test, needed to build a good model.
    @Test
    public void testGBMTrainTest() {
        File file1 = TestUtil.find_test_file("smalldata/gbm_test/ecology_model.csv");
        if (file1 == null)
            return;
        // Silently ignore if file not found

        Key fkey1 = NFSFileVec.make(file1);
        Key dest1 = Key.make("train.hex");
        File file2 = TestUtil.find_test_file("smalldata/gbm_test/ecology_eval.csv");
        Key fkey2 = NFSFileVec.make(file2);
        Key dest2 = Key.make("test.hex");
        GBM gbm = new GBM();
        // The Builder
        GBM.GBMModel gbmmodel = null;// The Model

        Frame ftest = null;
        Frame fpreds = null;
        try {
            Frame fr = ParseDataset2.parse(dest1, new Key[]{ fkey1 });
            UKV.remove(fr.remove("Site")._key);// Remove unique ID; too predictive

            gbm.response = fr.vecs()[fr.find("Angaus")];// Train on the outcome

            gbm.source = fr;
            gbm.ntrees = 5;
            gbm.max_depth = 10;
            gbm.learn_rate = 0.2F;
            gbm.min_rows = 10;
            gbm.nbins = 100;
            gbm.invoke();
            gbmmodel = UKV.get(gbm.dest());
            testHTML(gbmmodel);
            Assert.assertTrue(((gbmmodel.get_params().state) == (JobState.DONE)));// HEX-1817

            // Test on the train data
            ftest = ParseDataset2.parse(dest2, new Key[]{ fkey2 });
            fpreds = gbm.score(ftest);
            // Build a confusion matrix
            ConfusionMatrix CM = new ConfusionMatrix();
            CM.actual = ftest;
            CM.vactual = ftest.vecs()[ftest.find("Angaus")];
            CM.predict = fpreds;
            CM.vpredict = fpreds.vecs()[fpreds.find("predict")];
            CM.invoke();
            // Start it, do it
            StringBuilder sb = new StringBuilder();
            CM.toASCII(sb);
            System.out.println(sb);
        } finally {
            gbm.source.delete();// Remove the original hex frame key

            if (ftest != null)
                ftest.delete();

            if (fpreds != null)
                fpreds.delete();

            if (gbmmodel != null)
                gbmmodel.delete();
            // Remove the model

            UKV.remove(gbm.response._key);
            gbm.remove();// Remove GBM Job

        }
    }

    // Adapt a trained model to a test dataset with different enums
    @Test
    public void testModelAdapt() {
        File file1 = TestUtil.find_test_file("./smalldata/kaggle/KDDTrain.arff.gz");
        Key fkey1 = NFSFileVec.make(file1);
        Key dest1 = Key.make("KDDTrain.hex");
        File file2 = TestUtil.find_test_file("./smalldata/kaggle/KDDTest.arff.gz");
        Key fkey2 = NFSFileVec.make(file2);
        Key dest2 = Key.make("KDDTest.hex");
        GBM gbm = new GBM();
        GBM.GBMModel gbmmodel = null;// The Model

        try {
            gbm.source = ParseDataset2.parse(dest1, new Key[]{ fkey1 });
            gbm.response = gbm.source.vecs()[41];// Response is col 41

            gbm.ntrees = 2;
            gbm.max_depth = 8;
            gbm.learn_rate = 0.2F;
            gbm.min_rows = 10;
            gbm.nbins = 50;
            gbm.invoke();
            gbmmodel = UKV.get(gbm.dest());
            testHTML(gbmmodel);
            Assert.assertTrue(((gbmmodel.get_params().state) == (JobState.DONE)));// HEX-1817

            // The test data set has a few more enums than the train
            Frame ftest = ParseDataset2.parse(dest2, new Key[]{ fkey2 });
            Frame preds = gbm.score(ftest);
            ftest.delete();
            preds.delete();
        } finally {
            if (gbmmodel != null)
                gbmmodel.delete();
            // Remove the model

            gbm.source.delete();// Remove original hex frame key

            UKV.remove(gbm.response._key);
            gbm.remove();// Remove GBM Job

        }
    }

    // A test of locking the input dataset during model building.
    @Test
    public void testModelLock() {
        GBM gbm = new GBM();
        try {
            Frame fr = gbm.source = TestUtil.parseFrame(Key.make("air.hex"), "./smalldata/airlines/allyears2k_headers.zip");
            for (String s : GBMTest.ignored_aircols)
                UKV.remove(fr.remove(s)._key);

            int idx = fr.find("IsArrDelayed");
            gbm.response = fr.vecs()[idx];
            gbm.ntrees = 10;
            gbm.max_depth = 5;
            gbm.min_rows = 1;
            gbm.nbins = 20;
            gbm.cols = new int[fr.numCols()];
            for (int i = 0; i < (gbm.cols.length); i++)
                gbm.cols[i] = i;

            gbm.learn_rate = 0.2F;
            gbm.fork();
            try {
                Thread.sleep(100);
            } catch (Exception ignore) {
            }
            try {
                fr.delete();
                // Attempted delete while model-build is active
                throw H2O.fail();// Should toss IAE instead of reaching here

            } catch (IllegalArgumentException ignore) {
            } catch (DException de) {
                Assert.assertTrue(de.getMessage().contains("java.lang.IllegalArgumentException"));
            }
            GBM.GBMModel model = gbm.get();
            Assert.assertTrue(((model.get_params().state) == (JobState.DONE)));// HEX-1817

            if (model != null)
                model.delete();

        } finally {
            if ((gbm.source) != null)
                gbm.source.delete(gbm.self(), 0.0F);
            // Remove original hex frame key

            gbm.remove();// Remove GBM Job

        }
    }

    // MSE generated by GBM with/without validation dataset should be same
    @Test
    public void testModelMSEEqualityOnProstate() {
        final GBMTest.PrepData prostatePrep = new GBMTest.PrepData() {
            @Override
            int prep(Frame fr) {
                Assert.assertEquals(380, fr.numRows());
                // Remove patient ID vector
                UKV.remove(fr.remove("ID")._key);
                // Prostate: predict on CAPSULE
                return fr.find("CAPSULE");
            }
        };
        double[] mseWithoutVal = basicGBM("./smalldata/logreg/prostate.csv", "prostate.hex", prostatePrep, false).errs;
        double[] mseWithVal = basicGBM("./smalldata/logreg/prostate.csv", "prostate.hex", prostatePrep, true).errs;
        Assert.assertArrayEquals("GBM has to report same list of MSEs for run without/with validation dataset (which is equal to training data)", mseWithoutVal, mseWithVal, 1.0E-4);
    }

    @Test
    public void testReproducibility() {
        Frame tfr = null;
        final int N = 5;
        double[] mses = new double[N];
        Scope.enter();
        try {
            // Load data, hack frames
            tfr = TestUtil.parseFrame(Key.make("air.hex"), "./smalldata/covtype/covtype.20k.data");
            // rebalance to 256 chunks
            Key dest = Key.make("df.rebalanced.hex");
            RebalanceDataSet rb = new RebalanceDataSet(tfr, dest, 256);
            H2O.submitTask(rb);
            rb.join();
            tfr.delete();
            tfr = DKV.get(dest).get();
            for (int i = 0; i < N; ++i) {
                GBM parms = new GBM();
                parms.source = tfr;
                parms.response = tfr.lastVec();
                parms.nbins = 1000;
                parms.ntrees = 1;
                parms.max_depth = 8;
                parms.learn_rate = 0.1;
                parms.min_rows = 10;
                parms.family = Family.AUTO;
                parms.classification = false;
                // Build a first model; all remaining models should be equal
                GBMModel gbm = parms.fork().get();
                mses[i] = gbm.mse();
                gbm.delete();
            }
        } finally {
            if (tfr != null)
                tfr.delete();

        }
        Scope.exit();
        for (int i = 0; i < (mses.length); ++i) {
            Log.info(((("trial: " + i) + " -> mse: ") + (mses[i])));
        }
        for (int i = 0; i < (mses.length); ++i) {
            Assert.assertEquals(mses[i], mses[0], 1.0E-15);
        }
    }
}

