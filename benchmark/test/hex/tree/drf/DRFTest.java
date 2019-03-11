package hex.tree.drf;


import DRFModel.DRFParameters;
import Model.Parameters.FoldAssignmentScheme;
import SharedTreeModel.SharedTreeParameters.HistogramType;
import hex.ModelMetricsBinomial;
import hex.ModelMetricsRegression;
import hex.SplitFrame;
import hex.hex.ModelMetricsBinomial;
import hex.tree.SharedTreeModel;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;
import water.exceptions.H2OModelBuilderIllegalArgumentException;
import water.fvec.Frame;
import water.fvec.RebalanceDataSet;
import water.fvec.Vec;
import water.util.ArrayUtils;
import water.util.Log;
import water.util.Triple;
import water.util.VecUtils;

import static water.TestUtil.<init>;


public class DRFTest extends TestUtil {
    abstract static class PrepData {
        abstract int prep(Frame fr);
    }

    @Test
    public void testClassIris1() throws Throwable {
        // iris ntree=1
        // the DRF should  use only subset of rows since it is using oob validation
        basicDRFTestOOBE_Classification("./smalldata/iris/iris.csv", "iris.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return (fr.numCols()) - 1;
            }
        }, 1, 20, 1, 20, ard(ard(15, 0, 0), ard(0, 18, 0), ard(0, 1, 17)), DRFTest.s("Iris-setosa", "Iris-versicolor", "Iris-virginica"));
    }

    @Test
    public void testClassIris5() throws Throwable {
        // iris ntree=50
        basicDRFTestOOBE_Classification("./smalldata/iris/iris.csv", "iris5.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return (fr.numCols()) - 1;
            }
        }, 5, 20, 1, 20, ard(ard(43, 0, 0), ard(0, 37, 4), ard(0, 4, 39)), DRFTest.s("Iris-setosa", "Iris-versicolor", "Iris-virginica"));
    }

    @Test
    public void testClassCars1() throws Throwable {
        // cars ntree=1
        basicDRFTestOOBE_Classification("./smalldata/junit/cars.csv", "cars.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("name").remove();
                return fr.find("cylinders");
            }
        }, 1, 20, 1, 20, ard(ard(0, 2, 0, 0, 0), ard(0, 58, 6, 4, 0), ard(0, 1, 0, 0, 0), ard(1, 3, 4, 25, 1), ard(0, 0, 0, 2, 37)), DRFTest.s("3", "4", "5", "6", "8"));
    }

    @Test
    public void testClassCars5() throws Throwable {
        basicDRFTestOOBE_Classification("./smalldata/junit/cars.csv", "cars5.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("name").remove();
                return fr.find("cylinders");
            }
        }, 5, 20, 1, 20, ard(ard(1, 2, 0, 0, 0), ard(0, 177, 1, 5, 0), ard(0, 2, 0, 0, 0), ard(0, 6, 1, 67, 1), ard(0, 0, 0, 2, 84)), DRFTest.s("3", "4", "5", "6", "8"));
    }

    @Test
    public void testConstantCols() throws Throwable {
        try {
            basicDRFTestOOBE_Classification("./smalldata/poker/poker100", "poker.hex", new DRFTest.PrepData() {
                @Override
                int prep(Frame fr) {
                    for (int i = 0; i < 7; i++) {
                        fr.remove(3).remove();
                    }
                    return 3;
                }
            }, 1, 20, 1, 20, null, null);
            Assert.fail();
        } catch (H2OModelBuilderIllegalArgumentException iae) {
            /* pass */
        }
    }

    @Test
    public void testCreditProstate1() throws Throwable {
        basicDRFTestOOBE_Classification("./smalldata/logreg/prostate.csv", "prostate.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
            }
        }, 1, 20, 1, 20, ard(ard(0, 70), ard(0, 59)), DRFTest.s("0", "1"));
    }

    @Test
    public void testCreditProstateRegression1() throws Throwable {
        basicDRFTestOOBE_Regression("./smalldata/logreg/prostate.csv", "prostateRegression.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
            }
        }, 1, 20, 1, 10, 63.13182273942728);
    }

    @Test
    public void testCreditProstateRegression5() throws Throwable {
        basicDRFTestOOBE_Regression("./smalldata/logreg/prostate.csv", "prostateRegression5.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
            }
        }, 5, 20, 1, 10, 59.713095855920244);
    }

    @Test
    public void testCreditProstateRegression50() throws Throwable {
        basicDRFTestOOBE_Regression("./smalldata/logreg/prostate.csv", "prostateRegression50.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
            }
        }, 50, 20, 1, 10, 46.88452885668735);
    }

    @Test
    public void testCzechboard() throws Throwable {
        basicDRFTestOOBE_Classification("./smalldata/gbm_test/czechboard_300x300.csv", "czechboard_300x300.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                Vec resp = fr.remove("C2");
                fr.add("C2", VecUtils.toCategoricalVec(resp));
                resp.remove();
                return fr.find("C3");
            }
        }, 50, 20, 1, 20, ard(ard(0, 45000), ard(0, 45000)), DRFTest.s("0", "1"));
    }

    @Test
    public void test30kUnseenLevels() throws Throwable {
        // ntrees
        // bins
        // min_rows
        // max_depth
        basicDRFTestOOBE_Regression("./smalldata/gbm_test/30k_cattest.csv", "cat30k", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("C3");
            }
        }, 50, 20, 10, 5, 0.25040633586487);
    }

    @Test
    public void testProstate() throws Throwable {
        // ntrees
        // bins
        // min_rows
        // max_depth
        basicDRFTestOOBE_Classification("./smalldata/prostate/prostate.csv.zip", "prostate2.zip.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                String[] names = fr.names().clone();
                Vec[] en = fr.remove(new int[]{ 1, 4, 5, 8 });
                fr.add(names[1], VecUtils.toCategoricalVec(en[0]));// CAPSULE

                fr.add(names[4], VecUtils.toCategoricalVec(en[1]));// DPROS

                fr.add(names[5], VecUtils.toCategoricalVec(en[2]));// DCAPS

                fr.add(names[8], VecUtils.toCategoricalVec(en[3]));// GLEASON

                for (Vec v : en)
                    v.remove();

                fr.remove(0).remove();// drop ID

                return 4;// CAPSULE

            }
        }, 4, 2, 1, 1, null, DRFTest.s("0", "1"));
    }

    @Test
    public void testAlphabet() throws Throwable {
        basicDRFTestOOBE_Classification("./smalldata/gbm_test/alphabet_cattest.csv", "alphabetClassification.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("y");
            }
        }, 1, 20, 1, 20, ard(ard(670, 0), ard(0, 703)), DRFTest.s("0", "1"));
    }

    @Test
    public void testAlphabetRegression() throws Throwable {
        basicDRFTestOOBE_Regression("./smalldata/gbm_test/alphabet_cattest.csv", "alphabetRegression.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("y");
            }
        }, 1, 20, 1, 10, 0.0);
    }

    @Test
    public void testAlphabetRegression2() throws Throwable {
        // enough bins to resolve the alphabet
        // depth 1 is enough since nbins_cats == nbins == 26 (enough)
        basicDRFTestOOBE_Regression("./smalldata/gbm_test/alphabet_cattest.csv", "alphabetRegression2.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("y");
            }
        }, 1, 26, 1, 1, 0.0);
    }

    @Test
    public void testAlphabetRegression3() throws Throwable {
        // not enough bins to resolve the alphabet
        // depth 1 is not enough since nbins_cats == nbins < 26
        basicDRFTestOOBE_Regression("./smalldata/gbm_test/alphabet_cattest.csv", "alphabetRegression3.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("y");
            }
        }, 1, 25, 1, 1, 0.24007225096411577);
    }

    // PUBDEV-2476 Check reproducibility for the same # of chunks (i.e., same # of nodes) and same parameters
    @Test
    public void testChunks() {
        Frame tfr;
        final int N = 4;
        double[] mses = new double[N];
        int[] chunks = new int[]{ 1, 13, 19, 39, 500 };
        for (int i = 0; i < N; ++i) {
            Scope.enter();
            // Load data, hack frames
            tfr = parse_test_file("smalldata/covtype/covtype.20k.data");
            // rebalance to 256 chunks
            Key dest = Key.make("df.rebalanced.hex");
            RebalanceDataSet rb = new RebalanceDataSet(tfr, dest, chunks[i]);
            H2O.submitTask(rb);
            rb.join();
            tfr.delete();
            tfr = DKV.get(dest).get();
            Scope.track(tfr.replace(54, tfr.vecs()[54].toCategoricalVec()));
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "C55";
            parms._ntrees = 10;
            parms._seed = 1234;
            parms._auto_rebalance = false;
            // Build a first model; all remaining models should be equal
            DRF job = new DRF(parms);
            DRFModel drf = job.trainModel().get();
            Assert.assertEquals(drf._output._ntrees, parms._ntrees);
            mses[i] = drf._output._scored_train[((drf._output._scored_train.length) - 1)]._mse;
            drf.delete();
            if (tfr != null)
                tfr.remove();

            Scope.exit();
        }
        for (int i = 0; i < (mses.length); ++i) {
            Log.info(((("trial: " + i) + " -> MSE: ") + (mses[i])));
        }
        for (double mse : mses)
            Assert.assertEquals(mse, mses[0], 1.0E-10);

    }

    // 
    @Test
    public void testReproducibility() {
        Frame tfr = null;
        final int N = 5;
        double[] mses = new double[N];
        Scope.enter();
        try {
            // Load data, hack frames
            tfr = parse_test_file("smalldata/covtype/covtype.20k.data");
            // rebalance to 256 chunks
            Key dest = Key.make("df.rebalanced.hex");
            RebalanceDataSet rb = new RebalanceDataSet(tfr, dest, 256);
            H2O.submitTask(rb);
            rb.join();
            tfr.delete();
            tfr = DKV.get(dest).get();
            // Scope.track(tfr.replace(54, tfr.vecs()[54].toCategoricalVec())._key);
            // DKV.put(tfr);
            for (int i = 0; i < N; ++i) {
                DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
                parms._train = tfr._key;
                parms._response_column = "C55";
                parms._nbins = 1000;
                parms._ntrees = 1;
                parms._max_depth = 8;
                parms._mtries = -1;
                parms._min_rows = 10;
                parms._seed = 1234;
                // Build a first model; all remaining models should be equal
                DRFModel drf = trainModel().get();
                Assert.assertEquals(drf._output._ntrees, parms._ntrees);
                mses[i] = drf._output._scored_train[((drf._output._scored_train.length) - 1)]._mse;
                drf.delete();
            }
        } finally {
            if (tfr != null)
                tfr.remove();

        }
        Scope.exit();
        for (int i = 0; i < (mses.length); ++i) {
            Log.info(((("trial: " + i) + " -> MSE: ") + (mses[i])));
        }
        for (double mse : mses)
            Assert.assertEquals(mse, mses[0], 1.0E-15);

    }

    // PUBDEV-557 Test dependency on # nodes (for small number of bins, but fixed number of chunks)
    @Test
    public void testReproducibilityAirline() {
        Frame tfr = null;
        final int N = 1;
        double[] mses = new double[N];
        Scope.enter();
        try {
            // Load data, hack frames
            tfr = parse_test_file("./smalldata/airlines/allyears2k_headers.zip");
            // rebalance to fixed number of chunks
            Key dest = Key.make("df.rebalanced.hex");
            RebalanceDataSet rb = new RebalanceDataSet(tfr, dest, 256);
            H2O.submitTask(rb);
            rb.join();
            tfr.delete();
            tfr = DKV.get(dest).get();
            // Scope.track(tfr.replace(54, tfr.vecs()[54].toCategoricalVec())._key);
            // DKV.put(tfr);
            for (String s : new String[]{ "DepTime", "ArrTime", "ActualElapsedTime", "AirTime", "ArrDelay", "DepDelay", "Cancelled", "CancellationCode", "CarrierDelay", "WeatherDelay", "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsArrDelayed" }) {
                tfr.remove(s).remove();
            }
            DKV.put(tfr);
            for (int i = 0; i < N; ++i) {
                DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
                parms._train = tfr._key;
                parms._response_column = "IsDepDelayed";
                parms._nbins = 10;
                parms._nbins_cats = 1024;
                parms._ntrees = 7;
                parms._max_depth = 10;
                parms._binomial_double_trees = false;
                parms._mtries = -1;
                parms._min_rows = 1;
                parms._sample_rate = 0.632F;// Simulated sampling with replacement

                parms._balance_classes = true;
                parms._seed = (1L << 32) | 2;
                // Build a first model; all remaining models should be equal
                DRFModel drf = trainModel().get();
                Assert.assertEquals(drf._output._ntrees, parms._ntrees);
                mses[i] = drf._output._training_metrics.mse();
                drf.delete();
            }
        } finally {
            if (tfr != null)
                tfr.remove();

        }
        Scope.exit();
        for (int i = 0; i < (mses.length); ++i) {
            Log.info(((("trial: " + i) + " -> MSE: ") + (mses[i])));
        }
        for (int i = 0; i < (mses.length); ++i) {
            Assert.assertEquals(0.20377446328850304, mses[i], 1.0E-4);// check for the same result on 1 nodes and 5 nodes

        }
    }

    static double _AUC = 1.0;

    static double _MSE = 0.041294642857142856;

    static double _LogLoss = 0.14472835908293025;

    @Test
    public void testNoRowWeights() {
        Frame tfr = null;
        Frame vfr = null;
        DRFModel drf = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/no_weights.csv");
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._seed = 234;
            parms._min_rows = 1;
            parms._max_depth = 2;
            parms._ntrees = 3;
            // Build a first model; all remaining models should be equal
            drf = trainModel().get();
            // OOB
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (drf._output._training_metrics));
            Assert.assertEquals(DRFTest._AUC, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(DRFTest._MSE, mm.mse(), 1.0E-8);
            Assert.assertEquals(DRFTest._LogLoss, mm.logloss(), 1.0E-6);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (drf != null)
                drf.remove();

            Scope.exit();
        }
    }

    @Test
    public void testRowWeightsOne() {
        Frame tfr = null;
        Frame vfr = null;
        DRFModel drf = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights_all_ones.csv");
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._weights_column = "weight";
            parms._seed = 234;
            parms._min_rows = 1;
            parms._max_depth = 2;
            parms._ntrees = 3;
            // Build a first model; all remaining models should be equal
            drf = trainModel().get();
            // OOB
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (drf._output._training_metrics));
            Assert.assertEquals(DRFTest._AUC, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(DRFTest._MSE, mm.mse(), 1.0E-8);
            Assert.assertEquals(DRFTest._LogLoss, mm.logloss(), 1.0E-6);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (drf != null)
                drf.delete();

            Scope.exit();
        }
    }

    @Test
    public void testRowWeightsTwo() {
        Frame tfr = null;
        Frame vfr = null;
        DRFModel drf = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights_all_twos.csv");
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._weights_column = "weight";
            parms._seed = 234;
            parms._min_rows = 2;// in terms of weighted rows

            parms._max_depth = 2;
            parms._ntrees = 3;
            // Build a first model; all remaining models should be equal
            drf = trainModel().get();
            // OOB
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (drf._output._training_metrics));
            Assert.assertEquals(DRFTest._AUC, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(DRFTest._MSE, mm.mse(), 1.0E-8);
            Assert.assertEquals(DRFTest._LogLoss, mm.logloss(), 1.0E-6);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (drf != null)
                drf.delete();

            Scope.exit();
        }
    }

    @Test
    public void testRowWeightsTiny() {
        Frame tfr = null;
        Frame vfr = null;
        DRFModel drf = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights_all_tiny.csv");
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._weights_column = "weight";
            parms._seed = 234;
            parms._min_rows = 0.01242;// in terms of weighted rows

            parms._max_depth = 2;
            parms._ntrees = 3;
            // Build a first model; all remaining models should be equal
            drf = trainModel().get();
            // OOB
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (drf._output._training_metrics));
            Assert.assertEquals(DRFTest._AUC, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(DRFTest._MSE, mm.mse(), 1.0E-8);
            Assert.assertEquals(DRFTest._LogLoss, mm.logloss(), 1.0E-6);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (drf != null)
                drf.delete();

            Scope.exit();
        }
    }

    @Test
    public void testNoRowWeightsShuffled() {
        Frame tfr = null;
        Frame vfr = null;
        DRFModel drf = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/no_weights_shuffled.csv");
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._seed = 234;
            parms._min_rows = 1;
            parms._max_depth = 2;
            parms._ntrees = 3;
            // Build a first model; all remaining models should be equal
            drf = trainModel().get();
            // OOB
            // Shuffling changes the row sampling -> results differ
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (drf._output._training_metrics));
            Assert.assertEquals(1.0, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(0.029017857142857144, mm.mse(), 1.0E-8);
            Assert.assertEquals(0.10824081452821664, mm.logloss(), 1.0E-6);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (drf != null)
                drf.delete();

            Scope.exit();
        }
    }

    @Test
    public void testRowWeights() {
        Frame tfr = null;
        Frame vfr = null;
        DRFModel drf = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights.csv");
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._weights_column = "weight";
            parms._seed = 234;
            parms._min_rows = 1;
            parms._max_depth = 2;
            parms._ntrees = 3;
            // Build a first model; all remaining models should be equal
            drf = trainModel().get();
            // OOB
            // Reduced number of rows changes the row sampling -> results differ
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (drf._output._training_metrics));
            Assert.assertEquals(1.0, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(0.05823863636363636, mm.mse(), 1.0E-8);
            Assert.assertEquals(0.21035264541934587, mm.logloss(), 1.0E-6);
            // test set scoring (on the same dataset, but without normalizing the weights)
            Frame pred = drf.score(parms.train());
            hex.ModelMetricsBinomial mm2 = hex.ModelMetricsBinomial.getFromDKV(drf, parms.train());
            // Non-OOB
            Assert.assertEquals(1, mm2.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(0.0154320987654321, mm2.mse(), 1.0E-8);
            Assert.assertEquals(0.08349430638608361, mm2.logloss(), 1.0E-8);
            pred.remove();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (drf != null)
                drf.delete();

            Scope.exit();
        }
    }

    @Test
    public void testNFoldBalanceClasses() {
        Frame tfr = null;
        Frame vfr = null;
        DRFModel drf = null;
        Scope.enter();
        try {
            tfr = parse_test_file("./smalldata/airlines/allyears2k_headers.zip");
            for (String s : new String[]{ "DepTime", "ArrTime", "ActualElapsedTime", "AirTime", "ArrDelay", "DepDelay", "Cancelled", "CancellationCode", "CarrierDelay", "WeatherDelay", "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsArrDelayed" }) {
                tfr.remove(s).remove();
            }
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "IsDepDelayed";
            parms._seed = 234;
            parms._min_rows = 2;
            parms._nfolds = 3;
            parms._max_depth = 5;
            parms._balance_classes = true;
            parms._ntrees = 5;
            // Build a first model; all remaining models should be equal
            drf = trainModel().get();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (drf != null) {
                drf.deleteCrossValidationModels();
                drf.delete();
            }
            Scope.exit();
        }
    }

    @Test
    public void testNfoldsOneVsRest() {
        Frame tfr = null;
        DRFModel drf1 = null;
        DRFModel drf2 = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights.csv");
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._seed = 9999;
            parms._min_rows = 2;
            parms._nfolds = ((int) (tfr.numRows()));
            parms._fold_assignment = FoldAssignmentScheme.Modulo;
            parms._max_depth = 5;
            parms._ntrees = 5;
            drf1 = trainModel().get();
            // parms._nfolds = (int) tfr.numRows() + 1; //this is now an error
            drf2 = trainModel().get();
            ModelMetricsBinomial mm1 = ((ModelMetricsBinomial) (drf1._output._cross_validation_metrics));
            ModelMetricsBinomial mm2 = ((ModelMetricsBinomial) (drf2._output._cross_validation_metrics));
            Assert.assertEquals(mm1.auc_obj()._auc, mm2.auc_obj()._auc, 1.0E-12);
            Assert.assertEquals(mm1.mse(), mm2.mse(), 1.0E-12);
            Assert.assertEquals(mm1.logloss(), mm2.logloss(), 1.0E-12);
            // TODO: add check: the correct number of individual models were built. PUBDEV-1690
        } finally {
            if (tfr != null)
                tfr.remove();

            if (drf1 != null) {
                drf1.deleteCrossValidationModels();
                drf1.delete();
            }
            if (drf2 != null) {
                drf2.deleteCrossValidationModels();
                drf2.delete();
            }
            Scope.exit();
        }
    }

    @Test
    public void testNfoldsInvalidValues() {
        Frame tfr = null;
        DRFModel drf1 = null;
        DRFModel drf2 = null;
        DRFModel drf3 = null;
        Scope.enter();
        try {
            tfr = parse_test_file("./smalldata/airlines/allyears2k_headers.zip");
            for (String s : new String[]{ "DepTime", "ArrTime", "ActualElapsedTime", "AirTime", "ArrDelay", "DepDelay", "Cancelled", "CancellationCode", "CarrierDelay", "WeatherDelay", "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsArrDelayed" }) {
                tfr.remove(s).remove();
            }
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "IsDepDelayed";
            parms._seed = 234;
            parms._min_rows = 2;
            parms._max_depth = 5;
            parms._ntrees = 5;
            parms._nfolds = 0;
            drf1 = trainModel().get();
            parms._nfolds = 1;
            try {
                Log.info("Trying nfolds==1.");
                drf2 = trainModel().get();
                Assert.fail("Should toss H2OModelBuilderIllegalArgumentException instead of reaching here");
            } catch (H2OModelBuilderIllegalArgumentException e) {
            }
            parms._nfolds = -99;
            try {
                Log.info("Trying nfolds==-99.");
                drf3 = trainModel().get();
                Assert.fail("Should toss H2OModelBuilderIllegalArgumentException instead of reaching here");
            } catch (H2OModelBuilderIllegalArgumentException e) {
            }
        } finally {
            if (tfr != null)
                tfr.remove();

            if (drf1 != null)
                drf1.delete();

            if (drf2 != null)
                drf2.delete();

            if (drf3 != null)
                drf3.delete();

            Scope.exit();
        }
    }

    @Test
    public void testNfoldsCVAndValidation() {
        Frame tfr = null;
        Frame vfr = null;
        DRFModel drf = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights.csv");
            vfr = parse_test_file("smalldata/junit/weights.csv");
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._valid = vfr._key;
            parms._response_column = "response";
            parms._min_rows = 2;
            parms._max_depth = 2;
            parms._nfolds = 2;
            parms._ntrees = 3;
            parms._seed = 11233;
            try {
                Log.info("Trying N-fold cross-validation AND Validation dataset provided.");
                drf = trainModel().get();
            } catch (H2OModelBuilderIllegalArgumentException e) {
                Assert.fail("Should not toss H2OModelBuilderIllegalArgumentException.");
            }
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (drf != null) {
                drf.deleteCrossValidationModels();
                drf.delete();
            }
            Scope.exit();
        }
    }

    @Test
    public void testNfoldsConsecutiveModelsSame() {
        Frame tfr = null;
        Vec old = null;
        DRFModel drf1 = null;
        DRFModel drf2 = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/cars_20mpg.csv");
            tfr.remove("name").remove();// Remove unique id

            tfr.remove("economy").remove();
            old = tfr.remove("economy_20mpg");
            tfr.add("economy_20mpg", VecUtils.toCategoricalVec(old));// response to last column

            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "economy_20mpg";
            parms._min_rows = 2;
            parms._max_depth = 2;
            parms._nfolds = 3;
            parms._ntrees = 3;
            parms._seed = 77777;
            drf1 = trainModel().get();
            drf2 = trainModel().get();
            ModelMetricsBinomial mm1 = ((ModelMetricsBinomial) (drf1._output._cross_validation_metrics));
            ModelMetricsBinomial mm2 = ((ModelMetricsBinomial) (drf2._output._cross_validation_metrics));
            Assert.assertEquals(mm1.auc_obj()._auc, mm2.auc_obj()._auc, 1.0E-12);
            Assert.assertEquals(mm1.mse(), mm2.mse(), 1.0E-12);
            Assert.assertEquals(mm1.logloss(), mm2.logloss(), 1.0E-12);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (old != null)
                old.remove();

            if (drf1 != null) {
                drf1.deleteCrossValidationModels();
                drf1.delete();
            }
            if (drf2 != null) {
                drf2.deleteCrossValidationModels();
                drf2.delete();
            }
            Scope.exit();
        }
    }

    @Test
    public void testMTrys() {
        Frame tfr = null;
        Vec old = null;
        DRFModel drf1 = null;
        for (int i = 1; i <= 6; ++i) {
            Scope.enter();
            try {
                tfr = parse_test_file("smalldata/junit/cars_20mpg.csv");
                tfr.remove("name").remove();// Remove unique id

                tfr.remove("economy").remove();
                old = tfr.remove("economy_20mpg");
                tfr.add("economy_20mpg", VecUtils.toCategoricalVec(old));// response to last column

                DKV.put(tfr);
                DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
                parms._train = tfr._key;
                parms._response_column = "economy_20mpg";
                parms._min_rows = 2;
                parms._ntrees = 5;
                parms._max_depth = 5;
                parms._nfolds = 3;
                parms._mtries = i;
                drf1 = trainModel().get();
                ModelMetricsBinomial mm1 = ((ModelMetricsBinomial) (drf1._output._cross_validation_metrics));
                Assert.assertTrue(((mm1._auc) != null));
            } finally {
                if (tfr != null)
                    tfr.remove();

                if (old != null)
                    old.remove();

                if (drf1 != null) {
                    drf1.deleteCrossValidationModels();
                    drf1.delete();
                }
                Scope.exit();
            }
        }
    }

    @Test
    public void testMTryNegTwo() {
        Frame tfr = null;
        Vec old = null;
        DRFModel drf1 = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/cars_20mpg.csv");
            tfr.remove("name").remove();// Remove unique id

            tfr.remove("economy").remove();
            old = tfr.remove("economy_20mpg");
            tfr.add("economy_20mpg", VecUtils.toCategoricalVec(old));// response to last column

            tfr.add("constantCol", tfr.anyVec().makeCon(1));// DRF should not honor constant cols but still use all cols for split when mtries=-2

            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "economy_20mpg";
            parms._ignored_columns = new String[]{ "year" };// Test to see if ignored column is not passed to DRF

            parms._min_rows = 2;
            parms._ntrees = 5;
            parms._max_depth = 5;
            parms._nfolds = 3;
            parms._mtries = -2;
            drf1 = trainModel().get();
            ModelMetricsBinomial mm1 = ((ModelMetricsBinomial) (drf1._output._cross_validation_metrics));
            Assert.assertTrue(((mm1._auc) != null));
        } finally {
            if (tfr != null)
                tfr.remove();

            if (old != null)
                old.remove();

            if (drf1 != null) {
                drf1.deleteCrossValidationModels();
                drf1.delete();
            }
            Scope.exit();
        }
    }

    @Test
    public void testStochasticDRFEquivalent() {
        Frame tfr = null;
        Frame vfr = null;
        DRFModel drf = null;
        Scope.enter();
        try {
            tfr = parse_test_file("./smalldata/junit/cars.csv");
            for (String s : new String[]{ "name" }) {
                tfr.remove(s).remove();
            }
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "cylinders";// regression

            parms._seed = 234;
            parms._min_rows = 2;
            parms._max_depth = 5;
            parms._ntrees = 5;
            parms._mtries = 3;
            parms._sample_rate = 0.5F;
            // Build a first model; all remaining models should be equal
            drf = trainModel().get();
            ModelMetricsRegression mm = ((ModelMetricsRegression) (drf._output._training_metrics));
            Assert.assertEquals(0.12358322821934015, mm.mse(), 1.0E-4);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (drf != null)
                drf.delete();

            Scope.exit();
        }
    }

    @Test
    public void testColSamplingPerTree() {
        Frame tfr = null;
        Key[] ksplits = new Key[0];
        try {
            tfr = parse_test_file("./smalldata/gbm_test/ecology_model.csv");
            SplitFrame sf = new SplitFrame(tfr, new double[]{ 0.5, 0.5 }, new Key[]{ Key.make("train.hex"), Key.make("test.hex") });
            // Invoke the job
            sf.exec().get();
            ksplits = sf._destination_frames;
            DRFModel drf = null;
            float[] sample_rates = new float[]{ 0.2F, 0.4F, 0.6F, 0.8F, 1.0F };
            float[] col_sample_rates = new float[]{ 0.4F, 0.6F, 0.8F, 1.0F };
            float[] col_sample_rates_per_tree = new float[]{ 0.4F, 0.6F, 0.8F, 1.0F };
            Map<Double, Triple<Float>> hm = new TreeMap<>();
            for (float sample_rate : sample_rates) {
                for (float col_sample_rate : col_sample_rates) {
                    for (float col_sample_rate_per_tree : col_sample_rates_per_tree) {
                        Scope.enter();
                        try {
                            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
                            parms._train = ksplits[0];
                            parms._valid = ksplits[1];
                            parms._response_column = "Angaus";// regression

                            parms._seed = 12345;
                            parms._min_rows = 1;
                            parms._max_depth = 15;
                            parms._ntrees = 2;
                            parms._mtries = Math.max(1, ((int) (col_sample_rate * ((tfr.numCols()) - 1))));
                            parms._col_sample_rate_per_tree = col_sample_rate_per_tree;
                            parms._sample_rate = sample_rate;
                            // Build a first model; all remaining models should be equal
                            DRF job = new DRF(parms);
                            drf = job.trainModel().get();
                            // too slow, but passes (now)
                            // // Build a POJO, validate same results
                            // Frame pred = drf.score(tfr);
                            // Assert.assertTrue(drf.testJavaScoring(tfr,pred,1e-15));
                            // pred.remove();
                            ModelMetricsRegression mm = ((ModelMetricsRegression) (drf._output._validation_metrics));
                            hm.put(mm.mse(), new Triple(sample_rate, col_sample_rate, col_sample_rate_per_tree));
                        } finally {
                            if (drf != null)
                                drf.delete();

                            Scope.exit();
                        }
                    }
                }
            }
            Iterator<Map.Entry<Double, Triple<Float>>> it;
            Triple<Float> last = null;
            // iterator over results (min to max MSE) - best to worst
            for (it = hm.entrySet().iterator(); it.hasNext();) {
                Map.Entry<Double, Triple<Float>> n = it.next();
                Log.info(((((((("MSE: " + (n.getKey())) + ", row sample: ") + (n.getValue().v1)) + ", col sample: ") + (n.getValue().v2)) + ", col sample per tree: ") + (n.getValue().v3)));
                last = n.getValue();
            }
            // worst validation MSE should belong to the most overfit case (1.0, 1.0, 1.0)
            // Assert.assertTrue(last.v1==sample_rates[sample_rates.length-1]);
            // Assert.assertTrue(last.v2==col_sample_rates[col_sample_rates.length-1]);
            // Assert.assertTrue(last.v3==col_sample_rates_per_tree[col_sample_rates_per_tree.length-1]);
        } finally {
            if (tfr != null)
                tfr.remove();

            for (Key k : ksplits)
                if (k != null)
                    k.remove();


        }
    }

    @Test
    public void minSplitImprovement() {
        Frame tfr = null;
        Key[] ksplits = null;
        DRFModel drf = null;
        try {
            Scope.enter();
            tfr = parse_test_file("smalldata/covtype/covtype.20k.data");
            int resp = 54;
            // tfr = parse_test_file("bigdata/laptop/mnist/train.csv.gz");
            // int resp = 784;
            Scope.track(tfr.replace(resp, tfr.vecs()[resp].toCategoricalVec()));
            DKV.put(tfr);
            SplitFrame sf = new SplitFrame(tfr, new double[]{ 0.5, 0.5 }, new Key[]{ Key.make("train.hex"), Key.make("valid.hex") });
            // Invoke the job
            sf.exec().get();
            ksplits = sf._destination_frames;
            double[] msi = new double[]{ 0, 1.0E-10, 1.0E-8, 1.0E-6, 1.0E-4, 0.01 };
            final int N = msi.length;
            double[] loglosses = new double[N];
            for (int i = 0; i < N; ++i) {
                // Load data, hack frames
                DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
                parms._train = ksplits[0];
                parms._valid = ksplits[1];
                parms._response_column = tfr.names()[resp];
                parms._min_split_improvement = msi[i];
                parms._ntrees = 20;
                parms._score_tree_interval = parms._ntrees;
                parms._max_depth = 15;
                parms._seed = 1234;
                DRF job = new DRF(parms);
                drf = job.trainModel().get();
                loglosses[i] = drf._output._scored_valid[((drf._output._scored_valid.length) - 1)]._logloss;
                if (drf != null)
                    drf.delete();

            }
            for (int i = 0; i < (msi.length); ++i) {
                Log.info(((("min_split_improvement: " + (msi[i])) + " -> validation logloss: ") + (loglosses[i])));
            }
            int idx = ArrayUtils.minIndex(loglosses);
            Log.info(("Optimal min_split_improvement: " + (msi[idx])));
            Assert.assertTrue((0 != idx));
        } finally {
            if (drf != null)
                drf.delete();

            if (tfr != null)
                tfr.delete();

            if ((ksplits[0]) != null)
                ksplits[0].remove();

            if ((ksplits[1]) != null)
                ksplits[1].remove();

            Scope.exit();
        }
    }

    @Test
    public void histoTypes() {
        Frame tfr = null;
        Key[] ksplits = null;
        DRFModel drf = null;
        try {
            Scope.enter();
            tfr = parse_test_file("smalldata/covtype/covtype.20k.data");
            int resp = 54;
            // tfr = parse_test_file("bigdata/laptop/mnist/train.csv.gz");
            // int resp = 784;
            Scope.track(tfr.replace(resp, tfr.vecs()[resp].toCategoricalVec()));
            DKV.put(tfr);
            SplitFrame sf = new SplitFrame(tfr, new double[]{ 0.5, 0.5 }, new Key[]{ Key.make("train.hex"), Key.make("valid.hex") });
            // Invoke the job
            sf.exec().get();
            ksplits = sf._destination_frames;
            SharedTreeModel[] histoType = HistogramType.values();
            final int N = histoType.length;
            double[] loglosses = new double[N];
            for (int i = 0; i < N; ++i) {
                // Load data, hack frames
                DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
                parms._train = ksplits[0];
                parms._valid = ksplits[1];
                parms._response_column = tfr.names()[resp];
                parms._histogram_type = histoType[i];
                parms._ntrees = 10;
                parms._score_tree_interval = parms._ntrees;
                parms._max_depth = 10;
                parms._seed = 12345;
                parms._nbins = 20;
                parms._nbins_top_level = 20;
                DRF job = new DRF(parms);
                drf = job.trainModel().get();
                loglosses[i] = drf._output._scored_valid[((drf._output._scored_valid.length) - 1)]._logloss;
                if (drf != null)
                    drf.delete();

            }
            for (int i = 0; i < (histoType.length); ++i) {
                Log.info(((("histoType: " + (histoType[i])) + " -> validation logloss: ") + (loglosses[i])));
            }
            int idx = ArrayUtils.minIndex(loglosses);
            Log.info(("Optimal randomization: " + (histoType[idx])));
            Assert.assertTrue((4 == idx));// Quantiles are best

        } finally {
            if (drf != null)
                drf.delete();

            if (tfr != null)
                tfr.delete();

            if ((ksplits[0]) != null)
                ksplits[0].remove();

            if ((ksplits[1]) != null)
                ksplits[1].remove();

            Scope.exit();
        }
    }

    @Test
    public void sampleRatePerClass() {
        Frame tfr = null;
        Key[] ksplits = null;
        DRFModel drf = null;
        try {
            Scope.enter();
            tfr = parse_test_file("smalldata/covtype/covtype.20k.data");
            int resp = 54;
            // tfr = parse_test_file("bigdata/laptop/mnist/train.csv.gz");
            // int resp = 784;
            Scope.track(tfr.replace(resp, tfr.vecs()[resp].toCategoricalVec()));
            DKV.put(tfr);
            SplitFrame sf = new SplitFrame(tfr, new double[]{ 0.5, 0.5 }, new Key[]{ Key.make("train.hex"), Key.make("valid.hex") });
            // Invoke the job
            sf.exec().get();
            ksplits = sf._destination_frames;
            // Load data, hack frames
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = ksplits[0];
            parms._valid = ksplits[1];
            parms._response_column = tfr.names()[resp];
            parms._min_split_improvement = 1.0E-5;
            parms._ntrees = 20;
            parms._score_tree_interval = parms._ntrees;
            parms._max_depth = 15;
            parms._seed = 1234;
            parms._sample_rate_per_class = new double[]{ 0.1F, 0.1F, 0.2F, 0.4F, 1.0F, 0.3F, 0.2F };
            DRF job = new DRF(parms);
            drf = job.trainModel().get();
            if (drf != null)
                drf.delete();

        } finally {
            if (drf != null)
                drf.delete();

            if (tfr != null)
                tfr.delete();

            if ((ksplits[0]) != null)
                ksplits[0].remove();

            if ((ksplits[1]) != null)
                ksplits[1].remove();

            Scope.exit();
        }
    }

    @Test
    public void testConstantResponse() {
        Scope.enter();
        Frame tfr = null;
        DRFModel drf = null;
        try {
            tfr = parse_test_file(Key.make("iris.hex"), "./smalldata/iris/iris.csv");
            tfr.add("constantCol", tfr.anyVec().makeCon(1));
            DKV.put(tfr);
            DRFModel.DRFParameters parms = new DRFModel.DRFParameters();
            parms._train = tfr._key;
            parms._response_column = "constantCol";
            parms._ntrees = 1;
            parms._max_depth = 3;
            parms._seed = 12;
            parms._check_constant_response = false;// Allow constant response column

            // Build model
            drf = trainModel().get();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (drf != null)
                drf.remove();

        }
        Scope.exit();
    }
}

