package hex.tree.gbm;


import DistributionFamily.bernoulli;
import DistributionFamily.gamma;
import DistributionFamily.multinomial;
import DistributionFamily.poisson;
import DistributionFamily.tweedie;
import GBMModel.GBMParameters;
import Model.LeafNodeAssignment.LeafNodeAssignmentType.Node_ID;
import Model.LeafNodeAssignment.LeafNodeAssignmentType.Path;
import Model.Parameters.CategoricalEncodingScheme;
import Model.Parameters.FoldAssignmentScheme;
import ScoreKeeper.StoppingMetric;
import SharedTreeModel.SharedTreeParameters.HistogramType;
import hex.ModelMetricsBinomial;
import hex.ModelMetricsMultinomial;
import hex.genmodel.algos.gbm.GbmMojoModel;
import hex.genmodel.algos.tree.SharedTreeNode;
import hex.genmodel.algos.tree.SharedTreeSubgraph;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;
import hex.genmodel.utils.DistributionFamily;
import hex.tree.SharedTreeModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import water.api.StreamingSchema;
import water.exceptions.H2OModelBuilderIllegalArgumentException;
import water.fvec.FVecTest;
import water.parser.BufferedString;
import water.parser.ParseDataset;

import static hex.TestUtil.<init>;


public class GBMTest extends TestUtil {
    @Rule
    public transient ExpectedException expectedException = ExpectedException.none();

    private abstract class PrepData {
        abstract int prep(Frame fr);
    }

    static final String[] ignored_aircols = new String[]{ "DepTime", "ArrTime", "AirTime", "ArrDelay", "DepDelay", "TaxiIn", "TaxiOut", "Cancelled", "CancellationCode", "Diverted", "CarrierDelay", "WeatherDelay", "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsDepDelayed" };

    @Test
    public void testGBMRegressionGaussian() {
        GBMModel gbm = null;
        Frame fr = null;
        Frame fr2 = null;
        try {
            fr = parse_test_file("./smalldata/gbm_test/Mfgdata_gaussian_GBM_testing.csv");
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = fr._key;
            parms._distribution = gaussian;
            parms._response_column = fr._names[1];// Row in col 0, dependent in col 1, predictor in col 2

            parms._ntrees = 1;
            parms._max_depth = 1;
            parms._min_rows = 1;
            parms._nbins = 20;
            // Drop ColV2 0 (row), keep 1 (response), keep col 2 (only predictor), drop remaining cols
            String[] xcols = parms._ignored_columns = new String[(fr.numCols()) - 2];
            xcols[0] = fr._names[0];
            System.arraycopy(fr._names, 3, xcols, 1, ((fr.numCols()) - 3));
            parms._learn_rate = 1.0F;
            parms._score_each_iteration = true;
            GBM job = new GBM(parms);
            gbm = job.trainModel().get();
            Assert.assertTrue(job.isStopped());// HEX-1817

            // Done building model; produce a score column with predictions
            fr2 = gbm.score(fr);
            // job.response() can be used in place of fr.vecs()[1] but it has been rebalanced
            double sq_err = new MathUtils.SquareError().doAll(fr.vecs()[1], fr2.vecs()[0])._sum;
            double mse = sq_err / (fr2.numRows());
            Assert.assertEquals(79152.12337641386, mse, 0.1);
            Assert.assertEquals(79152.12337641386, gbm._output._scored_train[1]._mse, 0.1);
            Assert.assertEquals(79152.12337641386, gbm._output._scored_train[1]._mean_residual_deviance, 0.1);
        } finally {
            if (fr != null)
                fr.remove();

            if (fr2 != null)
                fr2.remove();

            if (gbm != null)
                gbm.remove();

        }
    }

    @Test
    public void testBasicGBM() {
        // Regression tests
        basicGBM("./smalldata/junit/cars.csv", new GBMTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return ~(fr.find("economy (mpg)"));
            }
        }, false, gaussian);
        basicGBM("./smalldata/junit/cars.csv", new GBMTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return ~(fr.find("economy (mpg)"));
            }
        }, false, poisson);
        basicGBM("./smalldata/junit/cars.csv", new GBMTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return ~(fr.find("economy (mpg)"));
            }
        }, false, gamma);
        basicGBM("./smalldata/junit/cars.csv", new GBMTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return ~(fr.find("economy (mpg)"));
            }
        }, false, tweedie);
        // Classification tests
        basicGBM("./smalldata/junit/test_tree.csv", new GBMTest.PrepData() {
            int prep(Frame fr) {
                return 1;
            }
        }, false, multinomial);
        basicGBM("./smalldata/junit/test_tree_minmax.csv", new GBMTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("response");
            }
        }, false, bernoulli);
        basicGBM("./smalldata/logreg/prostate.csv", new GBMTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
            }
        }, false, bernoulli);
        basicGBM("./smalldata/logreg/prostate.csv", new GBMTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
            }
        }, false, multinomial);
        basicGBM("./smalldata/junit/cars.csv", new GBMTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("name").remove();
                return fr.find("cylinders");
            }
        }, false, multinomial);
        basicGBM("./smalldata/gbm_test/alphabet_cattest.csv", new GBMTest.PrepData() {
            int prep(Frame fr) {
                return fr.find("y");
            }
        }, false, bernoulli);
        // basicGBM("./smalldata/gbm_test/alphabet_cattest.csv",
        // new PrepData() { int prep(Frame fr) { return fr.find("y"); }
        // },
        // false, DistributionFamily.modified_huber);
        basicGBM("./smalldata/airlines/allyears2k_headers.zip", new GBMTest.PrepData() {
            int prep(Frame fr) {
                for (String s : GBMTest.ignored_aircols)
                    fr.remove(s).remove();

                return fr.find("IsArrDelayed");
            }
        }, false, bernoulli);
        // // Bigger Tests
        // basicGBM("../datasets/98LRN.CSV",
        // new PrepData() { int prep(Frame fr ) {
        // fr.remove("CONTROLN").remove();
        // fr.remove("TARGET_D").remove();
        // return fr.find("TARGET_B"); }});
        // basicGBM("../datasets/UCI/UCI-large/covtype/covtype.data",
        // new PrepData() { int prep(Frame fr) { return fr.numCols()-1; } });
    }

    @Test
    public void testBasicGBMFamily() {
        Scope.enter();
        // Classification with Bernoulli family
        basicGBM("./smalldata/logreg/prostate.csv", new GBMTest.PrepData() {
            int prep(Frame fr) {
                fr.remove("ID").remove();// Remove not-predictive ID

                int ci = fr.find("RACE");// Change RACE to categorical

                Scope.track(fr.replace(ci, fr.vecs()[ci].toCategoricalVec()));
                return fr.find("CAPSULE");// Prostate: predict on CAPSULE

            }
        }, false, bernoulli);
        Scope.exit();
    }

    // Test-on-Train.  Slow test, needed to build a good model.
    @Test
    public void testGBMTrainTest() {
        GBMModel gbm = null;
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        try {
            Scope.enter();
            parms._valid = parse_test_file("smalldata/gbm_test/ecology_eval.csv")._key;
            Frame train = parse_test_file("smalldata/gbm_test/ecology_model.csv");
            train.remove("Site").remove();// Remove unique ID

            int ci = train.find("Angaus");// Convert response to categorical

            Scope.track(train.replace(ci, train.vecs()[ci].toCategoricalVec()));
            DKV.put(train);
            // Update frame after hacking it
            parms._train = train._key;
            parms._response_column = "Angaus";// Train on the outcome

            parms._ntrees = 5;
            parms._max_depth = 5;
            parms._min_rows = 10;
            parms._nbins = 100;
            parms._learn_rate = 0.2F;
            parms._distribution = DistributionFamily.multinomial;
            gbm = trainModel().get();
            hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(gbm, parms.valid());
            double auc = mm._auc._auc;
            Assert.assertTrue(((0.83 <= auc) && (auc < 0.87)));// Sanely good model

            double[][] cm = mm._auc.defaultCM();
            Assert.assertArrayEquals(ard(ard(349, 44), ard(43, 64)), cm);
        } finally {
            parms._train.remove();
            parms._valid.remove();
            if (gbm != null)
                gbm.delete();

            Scope.exit();
        }
    }

    // Predict with no actual, after training
    @Test
    public void testGBMPredict() {
        GBMModel gbm = null;
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        Frame pred = null;
        Frame res = null;
        Scope.enter();
        try {
            Frame train = parse_test_file("smalldata/gbm_test/ecology_model.csv");
            train.remove("Site").remove();// Remove unique ID

            int ci = train.find("Angaus");
            Scope.track(train.replace(ci, train.vecs()[ci].toCategoricalVec()));// Convert response 'Angaus' to categorical

            DKV.put(train);
            // Update frame after hacking it
            parms._train = train._key;
            parms._response_column = "Angaus";// Train on the outcome

            parms._distribution = DistributionFamily.multinomial;
            gbm = trainModel().get();
            pred = parse_test_file("smalldata/gbm_test/ecology_eval.csv");
            pred.remove("Angaus").remove();// No response column during scoring

            res = gbm.score(pred);
            // Build a POJO, validate same results
            Assert.assertTrue(gbm.testJavaScoring(pred, res, 1.0E-15));
        } finally {
            parms._train.remove();
            if (gbm != null)
                gbm.delete();

            if (pred != null)
                pred.remove();

            if (res != null)
                res.remove();

            Scope.exit();
        }
    }

    // Scoring should output original probabilities and probabilities calibrated by Platt Scaling
    @Test
    public void testGBMPredictWithCalibration() {
        GBMModel gbm = null;
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        Scope.enter();
        try {
            Frame train = parse_test_file("smalldata/gbm_test/ecology_model.csv");
            Frame calib = parse_test_file("smalldata/gbm_test/ecology_eval.csv");
            // Fix training set
            train.remove("Site").remove();// Remove unique ID

            Scope.track(train.vec("Angaus"));
            train.replace(train.find("Angaus"), train.vecs()[train.find("Angaus")].toCategoricalVec());
            Scope.track(train);
            DKV.put(train);// Update frame after hacking it

            // Fix calibration set (the same way as training)
            Scope.track(calib.vec("Angaus"));
            calib.replace(calib.find("Angaus"), calib.vecs()[calib.find("Angaus")].toCategoricalVec());
            Scope.track(calib);
            DKV.put(calib);// Update frame after hacking it

            parms._train = train._key;
            parms._calibrate_model = true;
            parms._calibration_frame = calib._key;
            parms._response_column = "Angaus";// Train on the outcome

            parms._distribution = DistributionFamily.multinomial;
            gbm = trainModel().get();
            Frame pred = parse_test_file("smalldata/gbm_test/ecology_eval.csv");
            pred.remove("Angaus").remove();// No response column during scoring

            Scope.track(pred);
            Frame res = Scope.track(gbm.score(pred));
            Assert.assertArrayEquals(new String[]{ "predict", "p0", "p1", "cal_p0", "cal_p1" }, res._names);
            Assert.assertEquals(res.vec("cal_p0").mean(), 0.786, 1.0E-4);
            Assert.assertEquals(res.vec("cal_p1").mean(), 0.2139, 1.0E-4);
        } finally {
            if (gbm != null)
                gbm.remove();

            Scope.exit();
        }
    }

    // Adapt a trained model to a test dataset with different categoricals
    @Test
    public void testModelAdaptMultinomial() {
        GBMModel gbm = null;
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        try {
            Scope.enter();
            Frame v;
            parms._train = parse_test_file("smalldata/junit/mixcat_train.csv")._key;
            parms._valid = (v = parse_test_file("smalldata/junit/mixcat_test.csv"))._key;
            parms._response_column = "Response";// Train on the outcome

            parms._ntrees = 1;// Build a CART tree - 1 tree, full learn rate, down to 1 row

            parms._learn_rate = 1.0F;
            parms._min_rows = 1;
            parms._distribution = DistributionFamily.multinomial;
            gbm = trainModel().get();
            Frame res = gbm.score(v);
            int[] ps = new int[((int) (v.numRows()))];
            Vec.Reader vr = res.vecs()[0].new Reader();
            for (int i = 0; i < (ps.length); i++)
                ps[i] = ((int) (vr.at8(i)));

            // Expected predictions are X,X,Y,Y,X,Y,Z,X,Y
            // Never predicts W, the extra class in the test set.
            // Badly predicts Z because 1 tree does not pick up that feature#2 can also
            // be used to predict Z, and instead relies on factor C which does not appear
            // in the test set.
            Assert.assertArrayEquals("", ps, new int[]{ 1, 1, 2, 2, 1, 2, 3, 1, 2 });
            hex.ModelMetricsMultinomial mm = hex.ModelMetricsMultinomial.getFromDKV(gbm, parms.valid());
            // Build a POJO, validate same results
            Assert.assertTrue(gbm.testJavaScoring(v, res, 1.0E-15));
            res.remove();
        } finally {
            parms._train.remove();
            parms._valid.remove();
            if (gbm != null)
                gbm.delete();

            Scope.exit();
        }
    }

    @Test
    public void testPredictLeafNodeAssignment() {
        Scope.enter();
        try {
            final Key<Frame> target = Key.make();
            Frame train = Scope.track(parse_test_file("smalldata/gbm_test/ecology_model.csv"));
            train.remove("Site").remove();// Remove unique ID

            int ci = train.find("Angaus");
            Scope.track(train.replace(ci, train.vecs()[ci].toCategoricalVec()));// Convert response 'Angaus' to categorical

            DKV.put(train);
            // Update frame after hacking it
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = train._key;
            parms._response_column = "Angaus";// Train on the outcome

            parms._distribution = DistributionFamily.multinomial;
            GBMModel gbm = ((GBMModel) (Scope.track_generic(trainModel().get())));
            Frame pred = Scope.track(parse_test_file("smalldata/gbm_test/ecology_eval.csv"));
            pred.remove("Angaus").remove();// No response column during scoring

            Frame nodeIds = Scope.track(gbm.scoreLeafNodeAssignment(pred, Node_ID, target));
            Frame nodePaths = Scope.track(gbm.scoreLeafNodeAssignment(pred, Path, target));
            Assert.assertArrayEquals(nodePaths._names, nodeIds._names);
            Assert.assertEquals(nodePaths.numRows(), nodeIds.numRows());
            for (int i = 0; i < (nodePaths.numCols()); i++) {
                String[] paths = nodePaths.vec(i).domain();
                Vec.Reader pathVecRdr = nodePaths.vec(i).new Reader();
                Vec.Reader nodeIdVecRdr = nodeIds.vec(i).new Reader();
                SharedTreeSubgraph tree = gbm.getSharedTreeSubgraph(i, 0);
                for (long j = 0; j < (nodePaths.numRows()); j++) {
                    String path = paths[((int) (pathVecRdr.at8(j)))];
                    int nodeId = ((int) (nodeIdVecRdr.at8(j)));
                    SharedTreeNode node = tree.walkNodes(path);
                    Assert.assertEquals(node.getNodeNumber(), nodeId);
                }
            }
        } finally {
            Scope.exit();
        }
    }

    /**
     * Staged predictions test (prediction probabilities of trees per iteration) - binomial data.
     */
    @Test
    public void testPredictStagedProbabilitiesBinomial() {
        Scope.enter();
        try {
            final Key<Frame> target = Key.make();
            Frame train = Scope.track(parse_test_file("smalldata/gbm_test/ecology_model.csv"));
            train.remove("Site").remove();// Remove unique ID

            int ci = train.find("Angaus");
            Scope.track(train.replace(ci, train.vecs()[ci].toCategoricalVec()));// Convert response 'Angaus' to categorical

            DKV.put(train);
            // Update frame after hacking it
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = train._key;
            parms._response_column = "Angaus";// Train on the outcome

            parms._distribution = DistributionFamily.bernoulli;
            GBMModel gbm = ((GBMModel) (Scope.track_generic(trainModel().get())));
            Frame stagedProbabilities = Scope.track(gbm.scoreStagedPredictions(train, target));
            Frame predictions = gbm.score(train);
            try {
                GbmMojoModel mojoModel = ((GbmMojoModel) (gbm.toMojo()));
                EasyPredictModelWrapper model = new EasyPredictModelWrapper(new EasyPredictModelWrapper.Config().setModel(mojoModel).setEnableStagedProbabilities(true));
                // test for the first 10 rows in training data
                for (int r = 0; r < 10; r++) {
                    double[] stagedProbabilitiesRow = new double[stagedProbabilities.numCols()];
                    for (int c = 0; c < (stagedProbabilities.numCols()); c++) {
                        stagedProbabilitiesRow[c] = stagedProbabilities.vec(c).at(r);
                    }
                    RowData tmpRow = new RowData();
                    BufferedString bStr = new BufferedString();
                    for (int c = 0; c < (train.numCols()); c++) {
                        if (train.vec(c).isCategorical()) {
                            tmpRow.put(train.names()[c], train.vec(c).atStr(bStr, r).toString());
                        } else {
                            tmpRow.put(train.names()[c], train.vec(c).at(r));
                        }
                    }
                    BinomialModelPrediction tmpPrediction = model.predictBinomial(tmpRow);
                    double[] mojoStageProbabilitiesRow = tmpPrediction.stageProbabilities;
                    Assert.assertArrayEquals(stagedProbabilitiesRow, mojoStageProbabilitiesRow, 1.0E-15);
                    double final_prediction = predictions.vec(1).at(r);
                    Assert.assertEquals(final_prediction, stagedProbabilitiesRow[((stagedProbabilitiesRow.length) - 1)], 1.0E-15);
                }
            } catch (IOException | PredictException ex) {
                Assert.fail(ex.toString());
            } finally {
                gbm.delete();
                if (stagedProbabilities != null)
                    stagedProbabilities.delete();

                if (predictions != null)
                    predictions.delete();

            }
        } finally {
            Scope.exit();
        }
    }

    /**
     * Staged predictions test (prediction probabilities of trees per iteration) - multinomial data.
     */
    @Test
    public void testPredictStagedProbabilitiesMultinomial() {
        Scope.enter();
        try {
            final Key<Frame> target = Key.make();
            Frame train = Scope.track(parse_test_file("/smalldata/logreg/prostate.csv"));
            train.remove("ID").remove();// Remove unique ID

            int ci = train.find("RACE");
            Scope.track(train.replace(ci, train.vecs()[ci].toCategoricalVec()));// Convert response 'Angaus' to categorical

            DKV.put(train);
            // Update frame after hacking it
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = train._key;
            parms._response_column = "RACE";// Train on the outcome

            parms._distribution = DistributionFamily.multinomial;
            GBMModel gbm = ((GBMModel) (Scope.track_generic(trainModel().get())));
            Frame stagedProbabilities = Scope.track(gbm.scoreStagedPredictions(train, target));
            Frame predictions = gbm.score(train);
            try {
                GbmMojoModel mojoModel = ((GbmMojoModel) (gbm.toMojo()));
                EasyPredictModelWrapper model = new EasyPredictModelWrapper(new EasyPredictModelWrapper.Config().setModel(mojoModel).setEnableStagedProbabilities(true));
                // test for the first 10 rows in training data
                for (int r = 0; r < 10; r++) {
                    double[] stagedProbabilitiesRow = new double[stagedProbabilities.numCols()];
                    for (int c = 0; c < (stagedProbabilities.numCols()); c++) {
                        stagedProbabilitiesRow[c] = stagedProbabilities.vec(c).at(r);
                    }
                    RowData tmpRow = new RowData();
                    BufferedString bStr = new BufferedString();
                    for (int c = 0; c < (train.numCols()); c++) {
                        if (train.vec(c).isCategorical()) {
                            tmpRow.put(train.names()[c], train.vec(c).atStr(bStr, r).toString());
                        } else {
                            tmpRow.put(train.names()[c], train.vec(c).at(r));
                        }
                    }
                    MultinomialModelPrediction tmpPrediction = model.predictMultinomial(tmpRow);
                    double[] mojoStageProbabilitiesRow = tmpPrediction.stageProbabilities;
                    Assert.assertArrayEquals(stagedProbabilitiesRow, mojoStageProbabilitiesRow, 1.0E-15);
                    double[] final_prediction = new double[]{ predictions.vec(1).at(r), predictions.vec(2).at(r), predictions.vec(3).at(r) };
                    Assert.assertArrayEquals(final_prediction, Arrays.copyOfRange(stagedProbabilitiesRow, ((stagedProbabilitiesRow.length) - 3), stagedProbabilitiesRow.length), 1.0E-15);
                }
            } catch (IOException | PredictException ex) {
                Assert.fail(ex.toString());
            } finally {
                gbm.delete();
                if (stagedProbabilities != null)
                    stagedProbabilities.delete();

                if (predictions != null)
                    predictions.delete();

            }
        } finally {
            Scope.exit();
        }
    }

    // A test of locking the input dataset during model building.
    @Test
    public void testModelLock() {
        Scope.enter();
        try {
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            Frame fr = Scope.track(parse_test_file("smalldata/gbm_test/ecology_model.csv"));
            fr.remove("Site").remove();
            // Remove unique ID
            int ci = fr.find("Angaus");
            Scope.track(fr.replace(ci, fr.vecs()[ci].toCategoricalVec()));// Convert response 'Angaus' to categorical

            DKV.put(fr);
            // Update after hacking
            parms._train = fr._key;
            parms._response_column = "Angaus";// Train on the outcome

            parms._ntrees = 50;
            parms._max_depth = 10;
            parms._min_rows = 1;
            parms._learn_rate = 0.2F;
            parms._seed = 42L;
            parms._distribution = DistributionFamily.multinomial;
            GBM gbm = new GBM(parms);
            gbm.trainModel();
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }// just in case

            boolean delete_ok = false;
            try {
                Log.info("Trying illegal frame delete.");
                fr.delete();
                // Attempted delete while model-build is active
                delete_ok = true;
                Log.err((("Frame " + (fr._key)) + " was deleted while it should have been locked!"));
            } catch (IllegalArgumentException ignore) {
            } catch (RuntimeException re) {
                Assert.assertTrue(((re.getCause()) instanceof IllegalArgumentException));
            }
            Log.info("Getting model");// in order to clean it up

            Scope.track_generic(gbm.get());
            Assert.assertTrue(gbm.isStopped());// HEX-1817

            Assert.assertFalse((("Frame " + (fr._key)) + " was deleted while it should have been locked!"), delete_ok);
        } finally {
            Scope.exit();
        }
    }

    // MSE generated by GBM with/without validation dataset should be same
    @Test
    public void testModelScoreKeeperEqualityOnProstateBernoulli() {
        final GBMTest.PrepData prostatePrep = new GBMTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
            }
        };
        ScoreKeeper[] scoredWithoutVal = basicGBM("./smalldata/logreg/prostate.csv", prostatePrep, false, bernoulli)._scored_train;
        ScoreKeeper[] scoredWithVal = basicGBM("./smalldata/logreg/prostate.csv", prostatePrep, true, bernoulli)._scored_valid;
        Assert.assertArrayEquals("GBM has to report same list of MSEs for run without/with validation dataset (which is equal to training data)", scoredWithoutVal, scoredWithVal);
    }

    @Test
    public void testModelScoreKeeperEqualityOnProstateGaussian() {
        final GBMTest.PrepData prostatePrep = new GBMTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return ~(fr.find("CAPSULE"));
            }
        };
        ScoreKeeper[] scoredWithoutVal = basicGBM("./smalldata/logreg/prostate.csv", prostatePrep, false, gaussian)._scored_train;
        ScoreKeeper[] scoredWithVal = basicGBM("./smalldata/logreg/prostate.csv", prostatePrep, true, gaussian)._scored_valid;
        Assert.assertArrayEquals("GBM has to report same list of MSEs for run without/with validation dataset (which is equal to training data)", scoredWithoutVal, scoredWithVal);
    }

    @Test
    public void testModelScoreKeeperEqualityOnProstateQuasibinomial() {
        final GBMTest.PrepData prostatePrep = new GBMTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return ~(fr.find("CAPSULE"));
            }
        };
        ScoreKeeper[] scoredWithoutVal = basicGBM("./smalldata/logreg/prostate.csv", prostatePrep, false, quasibinomial)._scored_train;
        ScoreKeeper[] scoredWithVal = basicGBM("./smalldata/logreg/prostate.csv", prostatePrep, true, quasibinomial)._scored_valid;
        Assert.assertArrayEquals("GBM has to report same list of MSEs for run without/with validation dataset (which is equal to training data)", scoredWithoutVal, scoredWithVal);
    }

    @Test
    public void testModelScoreKeeperEqualityOnProstateMultinomial() {
        final GBMTest.PrepData prostatePrep = new GBMTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("RACE");
            }
        };
        ScoreKeeper[] scoredWithoutVal = basicGBM("./smalldata/logreg/prostate.csv", prostatePrep, false, multinomial)._scored_train;
        ScoreKeeper[] scoredWithVal = basicGBM("./smalldata/logreg/prostate.csv", prostatePrep, true, multinomial)._scored_valid;
        Assert.assertArrayEquals("GBM has to report same list of MSEs for run without/with validation dataset (which is equal to training data)", scoredWithoutVal, scoredWithVal);
    }

    @Test
    public void testModelScoreKeeperEqualityOnTitanicGaussian() {
        final GBMTest.PrepData titanicPrep = new GBMTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("age");
            }
        };
        ScoreKeeper[] scoredWithoutVal = basicGBM("./smalldata/junit/titanic_alt.csv", titanicPrep, false, gaussian)._scored_train;
        ScoreKeeper[] scoredWithVal = basicGBM("./smalldata/junit/titanic_alt.csv", titanicPrep, true, gaussian)._scored_valid;
        Assert.assertArrayEquals("GBM has to report same list of MSEs for run without/with validation dataset (which is equal to training data)", scoredWithoutVal, scoredWithVal);
    }

    @Test
    public void testModelScoreKeeperEqualityOnTitanicBernoulli() {
        final GBMTest.PrepData titanicPrep = new GBMTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("survived");
            }
        };
        ScoreKeeper[] scoredWithoutVal = basicGBM("./smalldata/junit/titanic_alt.csv", titanicPrep, false, bernoulli)._scored_train;
        ScoreKeeper[] scoredWithVal = basicGBM("./smalldata/junit/titanic_alt.csv", titanicPrep, true, bernoulli)._scored_valid;
        Assert.assertArrayEquals("GBM has to report same list of MSEs for run without/with validation dataset (which is equal to training data)", scoredWithoutVal, scoredWithVal);
    }

    @Test
    public void testModelScoreKeeperEqualityOnTitanicQuasibinomial() {
        final GBMTest.PrepData titanicPrep = new GBMTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("survived");
            }
        };
        ScoreKeeper[] scoredWithoutVal = basicGBM("./smalldata/junit/titanic_alt.csv", titanicPrep, false, quasibinomial)._scored_train;
        ScoreKeeper[] scoredWithVal = basicGBM("./smalldata/junit/titanic_alt.csv", titanicPrep, true, quasibinomial)._scored_valid;
        Assert.assertArrayEquals("GBM has to report same list of MSEs for run without/with validation dataset (which is equal to training data)", scoredWithoutVal, scoredWithVal);
    }

    @Test
    public void testModelScoreKeeperEqualityOnTitanicMultinomial() {
        final GBMTest.PrepData titanicPrep = new GBMTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("survived");
            }
        };
        ScoreKeeper[] scoredWithoutVal = basicGBM("./smalldata/junit/titanic_alt.csv", titanicPrep, false, multinomial)._scored_train;
        ScoreKeeper[] scoredWithVal = basicGBM("./smalldata/junit/titanic_alt.csv", titanicPrep, true, multinomial)._scored_valid;
        Assert.assertArrayEquals("GBM has to report same list of MSEs for run without/with validation dataset (which is equal to training data)", scoredWithoutVal, scoredWithVal);
    }

    @Test
    public void testBigCat() {
        final GBMTest.PrepData prep = new GBMTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("y");
            }
        };
        basicGBM("./smalldata/gbm_test/50_cattest_test.csv", prep, false, bernoulli);
        basicGBM("./smalldata/gbm_test/50_cattest_train.csv", prep, false, bernoulli);
        basicGBM("./smalldata/gbm_test/swpreds_1000x3.csv", prep, false, bernoulli);
    }

    // HEXDEV-194: Check reproducibility for the same # of chunks (i.e., same # of nodes) and same parameters
    @Test
    public void testReprodubility() {
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
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                parms._train = tfr._key;
                parms._response_column = "C55";
                parms._nbins = 1000;
                parms._ntrees = 5;
                parms._max_depth = 8;
                parms._learn_rate = 0.1F;
                parms._min_rows = 10;
                // parms._distribution = Family.multinomial;
                parms._distribution = gaussian;
                // Build a first model; all remaining models should be equal
                GBMModel gbm = trainModel().get();
                Assert.assertEquals(gbm._output._ntrees, parms._ntrees);
                mses[i] = gbm._output._scored_train[((gbm._output._scored_train.length) - 1)]._mse;
                gbm.delete();
            }
        } finally {
            if (tfr != null)
                tfr.remove();

        }
        Scope.exit();
        for (double mse : mses)
            System.out.println(mse);

        for (double mse : mses)
            Assert.assertEquals(mse, mses[0], 1.0E-15);

    }

    // PUBDEV-557: Test dependency on # nodes (for small number of bins, but fixed number of chunks)
    @Test
    public void testReprodubilityAirline() {
        Frame tfr = null;
        final int N = 5;
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
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                parms._train = tfr._key;
                parms._response_column = "IsDepDelayed";
                parms._nbins = 10;
                parms._nbins_cats = 500;
                parms._ntrees = 7;
                parms._max_depth = 5;
                parms._min_rows = 10;
                parms._distribution = DistributionFamily.bernoulli;
                parms._balance_classes = true;
                parms._seed = 0;
                // Build a first model; all remaining models should be equal
                GBMModel gbm = trainModel().get();
                Assert.assertEquals(gbm._output._ntrees, parms._ntrees);
                mses[i] = gbm._output._scored_train[((gbm._output._scored_train.length) - 1)]._mse;
                gbm.delete();
            }
        } finally {
            if (tfr != null)
                tfr.remove();

        }
        Scope.exit();
        System.out.println("MSEs start");
        for (double d : mses)
            System.out.println(d);

        System.out.println("MSEs End");
        System.out.flush();
        for (double mse : mses)
            Assert.assertEquals(0.21694215729861027, mse, 1.0E-8);
        // check for the same result on 1 nodes and 5 nodes (will only work with enough chunks), mse, 1e-8); //check for the same result on 1 nodes and 5 nodes (will only work with enough chunks)

    }

    @Test
    public void testReprodubilityAirlineSingleNode() {
        Frame tfr = null;
        final int N = 10;
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
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                parms._train = tfr._key;
                parms._response_column = "IsDepDelayed";
                parms._nbins = 10;
                parms._nbins_cats = 500;
                parms._ntrees = 7;
                parms._max_depth = 5;
                parms._min_rows = 10;
                parms._distribution = DistributionFamily.bernoulli;
                parms._balance_classes = true;
                parms._seed = 0;
                parms._build_tree_one_node = true;
                // Build a first model; all remaining models should be equal
                GBMModel gbm = trainModel().get();
                Assert.assertEquals(gbm._output._ntrees, parms._ntrees);
                mses[i] = gbm._output._scored_train[((gbm._output._scored_train.length) - 1)]._mse;
                gbm.delete();
            }
        } finally {
            if (tfr != null)
                tfr.remove();

        }
        Scope.exit();
        System.out.println("MSE");
        for (double d : mses)
            System.out.println(d);

        for (double mse : mses)
            Assert.assertEquals(0.21694215729861027, mse, 1.0E-8);
        // check for the same result on 1 nodes and 5 nodes (will only work with enough chunks)

    }

    // HEXDEV-223
    @Test
    public void testCategorical() {
        Frame tfr = null;
        final int N = 1;
        double[] mses = new double[N];
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/gbm_test/alphabet_cattest.csv");
            Scope.track(tfr.replace(1, tfr.vecs()[1].toCategoricalVec()));
            DKV.put(tfr);
            for (int i = 0; i < N; ++i) {
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                parms._train = tfr._key;
                parms._response_column = "y";
                parms._ntrees = 1;
                parms._max_depth = 1;
                parms._learn_rate = 1;
                parms._distribution = DistributionFamily.bernoulli;
                // Build a first model; all remaining models should be equal
                GBMModel gbm = trainModel().get();
                Assert.assertEquals(gbm._output._ntrees, parms._ntrees);
                hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(gbm, parms.train());
                double auc = mm._auc._auc;
                Assert.assertTrue((1 == auc));
                mses[i] = gbm._output._scored_train[((gbm._output._scored_train.length) - 1)]._mse;
                gbm.delete();
            }
        } finally {
            if (tfr != null)
                tfr.remove();

        }
        Scope.exit();
        for (double mse : mses)
            Assert.assertEquals(0.0142093, mse, 1.0E-6);

    }

    static double _AUC = 1;

    static double _MSE = 0.24850374695598948;

    static double _LogLoss = 0.690155;

    @Test
    public void testNoRowWeights() {
        Frame tfr = null;
        Frame vfr = null;
        GBMModel gbm = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/no_weights.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._seed = 912559;
            parms._min_rows = 1;
            parms._ntrees = 3;
            parms._learn_rate = 0.001F;
            // Build a first model; all remaining models should be equal
            gbm = trainModel().get();
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (gbm._output._training_metrics));
            Assert.assertEquals(GBMTest._AUC, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm.logloss(), 1.0E-6);
            Frame pred = gbm.score(parms.train());
            hex.ModelMetricsBinomial mm2 = hex.ModelMetricsBinomial.getFromDKV(gbm, parms.train());
            Assert.assertEquals(GBMTest._AUC, mm2.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm2.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm2.logloss(), 1.0E-6);
            pred.remove();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (gbm != null)
                gbm.delete();

            Scope.exit();
        }
    }

    @Test
    public void testRowWeightsOne() {
        Frame tfr = null;
        Frame vfr = null;
        Scope.enter();
        GBMModel gbm = null;
        try {
            tfr = parse_test_file("smalldata/junit/weights_all_ones.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._weights_column = "weight";
            parms._seed = 912559;
            parms._min_rows = 1;
            parms._max_depth = 2;
            parms._ntrees = 3;
            parms._learn_rate = 0.001F;
            // Build a first model; all remaining models should be equal
            gbm = trainModel().get();
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (gbm._output._training_metrics));
            Assert.assertEquals(GBMTest._AUC, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm.logloss(), 1.0E-6);
            Frame pred = gbm.score(parms.train());
            hex.ModelMetricsBinomial mm2 = hex.ModelMetricsBinomial.getFromDKV(gbm, parms.train());
            Assert.assertEquals(GBMTest._AUC, mm2.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm2.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm2.logloss(), 1.0E-6);
            pred.remove();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (gbm != null)
                gbm.delete();

            Scope.exit();
        }
    }

    @Test
    public void testRowWeightsTwo() {
        Frame tfr = null;
        Frame vfr = null;
        Scope.enter();
        GBMModel gbm = null;
        try {
            tfr = parse_test_file("smalldata/junit/weights_all_twos.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._weights_column = "weight";
            parms._seed = 912559;
            parms._min_rows = 2;// Must be adapted to the weights

            parms._max_depth = 2;
            parms._ntrees = 3;
            parms._learn_rate = 0.001F;
            // Build a first model; all remaining models should be equal
            gbm = trainModel().get();
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (gbm._output._training_metrics));
            Assert.assertEquals(GBMTest._AUC, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm.logloss(), 1.0E-6);
            Frame pred = gbm.score(parms.train());
            hex.ModelMetricsBinomial mm2 = hex.ModelMetricsBinomial.getFromDKV(gbm, parms.train());
            Assert.assertEquals(GBMTest._AUC, mm2.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm2.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm2.logloss(), 1.0E-6);
            pred.remove();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (gbm != null)
                gbm.delete();

            Scope.exit();
        }
    }

    @Test
    public void testRowWeightsTiny() {
        Frame tfr = null;
        Frame vfr = null;
        Scope.enter();
        GBMModel gbm = null;
        try {
            tfr = parse_test_file("smalldata/junit/weights_all_tiny.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._weights_column = "weight";
            parms._seed = 912559;
            parms._min_rows = 0.01242;// Must be adapted to the weights

            parms._max_depth = 2;
            parms._ntrees = 3;
            parms._learn_rate = 0.001F;
            // Build a first model; all remaining models should be equal
            gbm = trainModel().get();
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (gbm._output._training_metrics));
            Assert.assertEquals(GBMTest._AUC, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm.logloss(), 1.0E-6);
            Frame pred = gbm.score(parms.train());
            hex.ModelMetricsBinomial mm2 = hex.ModelMetricsBinomial.getFromDKV(gbm, parms.train());
            Assert.assertEquals(GBMTest._AUC, mm2.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm2.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm2.logloss(), 1.0E-6);
            pred.remove();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (gbm != null)
                gbm.delete();

            Scope.exit();
        }
    }

    @Test
    public void testNoRowWeightsShuffled() {
        Frame tfr = null;
        Frame vfr = null;
        GBMModel gbm = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/no_weights_shuffled.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._seed = 912559;
            parms._min_rows = 1;
            parms._max_depth = 2;
            parms._ntrees = 3;
            parms._learn_rate = 0.001F;
            // Build a first model; all remaining models should be equal
            gbm = trainModel().get();
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (gbm._output._training_metrics));
            Assert.assertEquals(GBMTest._AUC, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm.logloss(), 1.0E-6);
            Frame pred = gbm.score(parms.train());
            hex.ModelMetricsBinomial mm2 = hex.ModelMetricsBinomial.getFromDKV(gbm, parms.train());
            Assert.assertEquals(GBMTest._AUC, mm2.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm2.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm2.logloss(), 1.0E-6);
            pred.remove();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (gbm != null)
                gbm.delete();

            Scope.exit();
        }
    }

    @Test
    public void testRowWeights() {
        Frame tfr = null;
        Frame vfr = null;
        GBMModel gbm = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._weights_column = "weight";
            parms._seed = 912559;
            parms._min_rows = 1;
            parms._max_depth = 2;
            parms._ntrees = 3;
            parms._learn_rate = 0.001F;
            // Build a first model; all remaining models should be equal
            gbm = trainModel().get();
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (gbm._output._training_metrics));
            Assert.assertEquals(GBMTest._AUC, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm.logloss(), 1.0E-6);
            Frame pred = gbm.score(parms.train());
            hex.ModelMetricsBinomial mm2 = hex.ModelMetricsBinomial.getFromDKV(gbm, parms.train());
            Assert.assertEquals(GBMTest._AUC, mm2.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(GBMTest._MSE, mm2.mse(), 1.0E-8);
            Assert.assertEquals(GBMTest._LogLoss, mm2.logloss(), 1.0E-6);
            pred.remove();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (gbm != null)
                gbm.delete();

            Scope.exit();
        }
    }

    @Test
    public void testNFold() {
        Frame tfr = null;
        Frame vfr = null;
        GBMModel gbm = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._weights_column = "weight";
            parms._seed = 123;
            parms._min_rows = 1;
            parms._max_depth = 2;
            parms._nfolds = 2;
            parms._ntrees = 3;
            parms._learn_rate = 0.001F;
            parms._keep_cross_validation_predictions = true;
            // Build a first model; all remaining models should be equal
            gbm = trainModel().get();
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (gbm._output._cross_validation_metrics));
            Assert.assertEquals(0.6296296296296297, mm.auc_obj()._auc, 1.0E-8);
            Assert.assertEquals(0.28640022521234304, mm.mse(), 1.0E-8);
            Assert.assertEquals(0.7674117059335286, mm.logloss(), 1.0E-6);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (gbm != null) {
                gbm.deleteCrossValidationModels();
                gbm.delete();
                for (Key k : gbm._output._cross_validation_predictions)
                    k.remove();

                gbm._output._cross_validation_holdout_predictions_frame_id.remove();
            }
            Scope.exit();
        }
    }

    @Test
    public void testNfoldsOneVsRest() {
        Frame tfr = null;
        GBMModel gbm1 = null;
        GBMModel gbm2 = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._min_rows = 1;
            parms._max_depth = 2;
            parms._nfolds = ((int) (tfr.numRows()));
            parms._fold_assignment = FoldAssignmentScheme.Modulo;
            parms._ntrees = 3;
            parms._seed = 12345;
            parms._learn_rate = 0.001F;
            gbm1 = trainModel().get();
            // parms._nfolds = (int) tfr.numRows() + 1; //This is now an error
            gbm2 = trainModel().get();
            ModelMetricsBinomial mm1 = ((ModelMetricsBinomial) (gbm1._output._cross_validation_metrics));
            ModelMetricsBinomial mm2 = ((ModelMetricsBinomial) (gbm2._output._cross_validation_metrics));
            Assert.assertEquals(mm1.auc_obj()._auc, mm2.auc_obj()._auc, 1.0E-12);
            Assert.assertEquals(mm1.mse(), mm2.mse(), 1.0E-12);
            // assertEquals(mm1.r2(), mm2.r2(), 1e-12);
            Assert.assertEquals(mm1.logloss(), mm2.logloss(), 1.0E-12);
            // TODO: add check: the correct number of individual models were built. PUBDEV-1690
        } finally {
            if (tfr != null)
                tfr.remove();

            if (gbm1 != null) {
                gbm1.deleteCrossValidationModels();
                gbm1.delete();
            }
            if (gbm2 != null) {
                gbm2.deleteCrossValidationModels();
                gbm2.delete();
            }
            Scope.exit();
        }
    }

    @Test
    public void testNfoldsInvalidValues() {
        Frame tfr = null;
        GBMModel gbm1 = null;
        GBMModel gbm2 = null;
        GBMModel gbm3 = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._min_rows = 1;
            parms._seed = 12345;
            parms._max_depth = 2;
            parms._ntrees = 3;
            parms._learn_rate = 0.001F;
            parms._nfolds = 0;
            gbm1 = trainModel().get();
            parms._nfolds = 1;
            try {
                Log.info("Trying nfolds==1.");
                gbm2 = trainModel().get();
                Assert.fail("Should toss H2OModelBuilderIllegalArgumentException instead of reaching here");
            } catch (H2OModelBuilderIllegalArgumentException e) {
            }
            parms._nfolds = -99;
            try {
                Log.info("Trying nfolds==-99.");
                gbm3 = trainModel().get();
                Assert.fail("Should toss H2OModelBuilderIllegalArgumentException instead of reaching here");
            } catch (H2OModelBuilderIllegalArgumentException e) {
            }
        } finally {
            if (tfr != null)
                tfr.remove();

            if (gbm1 != null)
                gbm1.delete();

            if (gbm2 != null)
                gbm2.delete();

            if (gbm3 != null)
                gbm3.delete();

            Scope.exit();
        }
    }

    @Test
    public void testNfoldsCVAndValidation() {
        Frame tfr = null;
        Frame vfr = null;
        GBMModel gbm = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights.csv");
            vfr = parse_test_file("smalldata/junit/weights.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._valid = vfr._key;
            parms._response_column = "response";
            parms._seed = 12345;
            parms._min_rows = 1;
            parms._max_depth = 2;
            parms._nfolds = 3;
            parms._ntrees = 3;
            parms._learn_rate = 0.001F;
            try {
                Log.info("Trying N-fold cross-validation AND Validation dataset provided.");
                gbm = trainModel().get();
            } catch (H2OModelBuilderIllegalArgumentException e) {
                Assert.fail("Should not toss H2OModelBuilderIllegalArgumentException.");
            }
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (gbm != null) {
                gbm.deleteCrossValidationModels();
                gbm.delete();
            }
            Scope.exit();
        }
    }

    @Test
    public void testNfoldsConsecutiveModelsSame() {
        Frame tfr = null;
        Vec old = null;
        GBMModel gbm1 = null;
        GBMModel gbm2 = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/cars_20mpg.csv");
            tfr.remove("name").remove();// Remove unique id

            tfr.remove("economy").remove();
            old = tfr.remove("economy_20mpg");
            tfr.add("economy_20mpg", old.toCategoricalVec());// response to last column

            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "economy_20mpg";
            parms._min_rows = 1;
            parms._seed = 12345;
            parms._max_depth = 2;
            parms._nfolds = 3;
            parms._ntrees = 3;
            parms._learn_rate = 0.001F;
            gbm1 = trainModel().get();
            gbm2 = trainModel().get();
            ModelMetricsBinomial mm1 = ((ModelMetricsBinomial) (gbm1._output._cross_validation_metrics));
            ModelMetricsBinomial mm2 = ((ModelMetricsBinomial) (gbm2._output._cross_validation_metrics));
            Assert.assertEquals(mm1.auc_obj()._auc, mm2.auc_obj()._auc, 1.0E-12);
            Assert.assertEquals(mm1.mse(), mm2.mse(), 1.0E-12);
            // assertEquals(mm1.r2(), mm2.r2(), 1e-12);
            Assert.assertEquals(mm1.logloss(), mm2.logloss(), 1.0E-12);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (old != null)
                old.remove();

            if (gbm1 != null) {
                gbm1.deleteCrossValidationModels();
                gbm1.delete();
            }
            if (gbm2 != null) {
                gbm2.deleteCrossValidationModels();
                gbm2.delete();
            }
            Scope.exit();
        }
    }

    @Test
    public void testNfoldsColumn() {
        Frame tfr = null;
        GBMModel gbm1 = null;
        try {
            tfr = parse_test_file("smalldata/junit/cars_20mpg.csv");
            tfr.remove("name").remove();// Remove unique id

            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "economy_20mpg";
            parms._fold_column = "cylinders";
            Vec old = tfr.remove("cylinders");
            tfr.add("cylinders", old.toCategoricalVec());
            DKV.put(tfr);
            parms._ntrees = 10;
            parms._keep_cross_validation_fold_assignment = true;
            parms._keep_cross_validation_models = true;
            GBM job1 = new GBM(parms);
            gbm1 = job1.trainModel().get();
            Assert.assertTrue(((gbm1._output._cross_validation_models.length) == 5));
            old.remove();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (gbm1 != null) {
                gbm1.deleteCrossValidationModels();
                gbm1.delete();
                gbm1._output._cross_validation_fold_assignment_frame_id.remove();
            }
        }
    }

    @Test
    public void testNfoldsColumnNumbersFrom0() {
        Frame tfr = null;
        Vec old = null;
        GBMModel gbm1 = null;
        try {
            tfr = parse_test_file("smalldata/junit/cars_20mpg.csv");
            tfr.remove("name").remove();// Remove unique id

            new MRTask() {
                @Override
                public void map(Chunk c) {
                    for (int i = 0; i < (c.len()); ++i) {
                        if ((c.at8(i)) == 3)
                            c.set(i, 0);

                        if ((c.at8(i)) == 4)
                            c.set(i, 1);

                        if ((c.at8(i)) == 5)
                            c.set(i, 2);

                        if ((c.at8(i)) == 6)
                            c.set(i, 3);

                        if ((c.at8(i)) == 8)
                            c.set(i, 4);

                    }
                }
            }.doAll(tfr.vec("cylinders"));
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "economy_20mpg";
            parms._fold_column = "cylinders";
            parms._ntrees = 10;
            parms._keep_cross_validation_models = true;
            GBM job1 = new GBM(parms);
            gbm1 = job1.trainModel().get();
            Assert.assertTrue(((gbm1._output._cross_validation_models.length) == 5));
        } finally {
            if (tfr != null)
                tfr.remove();

            if (old != null)
                old.remove();

            if (gbm1 != null) {
                gbm1.deleteCrossValidationModels();
                gbm1.delete();
            }
        }
    }

    @Test
    public void testNfoldsColumnCategorical() {
        Frame tfr = null;
        Vec old = null;
        GBMModel gbm1 = null;
        try {
            tfr = parse_test_file("smalldata/junit/cars_20mpg.csv");
            tfr.remove("name").remove();// Remove unique id

            old = tfr.remove("cylinders");
            tfr.add("folds", old.toCategoricalVec());
            old.remove();
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "economy_20mpg";
            parms._fold_column = "folds";
            parms._ntrees = 10;
            parms._keep_cross_validation_models = true;
            GBM job1 = new GBM(parms);
            gbm1 = job1.trainModel().get();
            Assert.assertTrue(((gbm1._output._cross_validation_models.length) == 5));
        } finally {
            if (tfr != null)
                tfr.remove();

            if (old != null)
                old.remove();

            if (gbm1 != null) {
                gbm1.deleteCrossValidationModels();
                gbm1.delete();
            }
        }
    }

    @Test
    public void testNFoldAirline() {
        Frame tfr = null;
        Frame vfr = null;
        GBMModel gbm = null;
        Scope.enter();
        try {
            tfr = parse_test_file("./smalldata/airlines/allyears2k_headers.zip");
            for (String s : new String[]{ "DepTime", "ArrTime", "ActualElapsedTime", "AirTime", "ArrDelay", "DepDelay", "Cancelled", "CancellationCode", "CarrierDelay", "WeatherDelay", "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsArrDelayed" }) {
                tfr.remove(s).remove();
            }
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "IsDepDelayed";
            parms._seed = 234;
            parms._min_rows = 2;
            parms._nfolds = 3;
            parms._max_depth = 5;
            parms._ntrees = 5;
            // Build a first model; all remaining models should be equal
            gbm = trainModel().get();
            ModelMetricsBinomial mm = ((ModelMetricsBinomial) (gbm._output._cross_validation_metrics));
            Assert.assertEquals(0.7309795467719639, mm.auc_obj()._auc, 1.0E-4);// 1 node

            Assert.assertEquals(0.22511756378273942, mm.mse(), 1.0E-4);
            Assert.assertEquals(0.6425515048581261, mm.logloss(), 1.0E-4);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (gbm != null) {
                gbm.deleteCrossValidationModels();
                gbm.delete();
            }
            Scope.exit();
        }
    }

    // just a simple sanity check - not a golden test
    @Test
    public void testDistributions() {
        Frame tfr = null;
        Frame vfr = null;
        Frame res = null;
        GBMModel gbm = null;
        for (DistributionFamily dist : new DistributionFamily[]{ DistributionFamily.AUTO, gaussian, DistributionFamily.poisson, DistributionFamily.gamma, DistributionFamily.tweedie }) {
            Scope.enter();
            try {
                tfr = parse_test_file("smalldata/glm_test/cancar_logIn.csv");
                vfr = parse_test_file("smalldata/glm_test/cancar_logIn.csv");
                for (String s : new String[]{ "Merit", "Class" }) {
                    Scope.track(tfr.replace(tfr.find(s), tfr.vec(s).toCategoricalVec()));
                    Scope.track(vfr.replace(vfr.find(s), vfr.vec(s).toCategoricalVec()));
                }
                DKV.put(tfr);
                DKV.put(vfr);
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                parms._train = tfr._key;
                parms._response_column = "Cost";
                parms._seed = 912559;
                parms._distribution = dist;
                parms._min_rows = 1;
                parms._ntrees = 30;
                // parms._offset_column = "logInsured"; //POJO scoring not supported for offsets
                parms._learn_rate = 0.001F;
                // Build a first model; all remaining models should be equal
                gbm = trainModel().get();
                res = gbm.score(vfr);
                Assert.assertTrue(gbm.testJavaScoring(vfr, res, 1.0E-15));
                res.remove();
                ModelMetricsRegression mm = ((ModelMetricsRegression) (gbm._output._training_metrics));
            } finally {
                if (tfr != null)
                    tfr.remove();

                if (vfr != null)
                    vfr.remove();

                if (res != null)
                    res.remove();

                if (gbm != null)
                    gbm.delete();

                Scope.exit();
            }
        }
    }

    @Test
    public void testStochasticGBM() {
        Frame tfr = null;
        Frame vfr = null;
        GBMModel gbm = null;
        float[] sample_rates = new float[]{ 0.2F, 0.4F, 0.6F, 0.8F, 1.0F };
        float[] col_sample_rates = new float[]{ 0.2F, 0.4F, 0.6F, 0.8F, 1.0F };
        Map<Double, Pair<Float, Float>> hm = new TreeMap<>();
        for (float sample_rate : sample_rates) {
            for (float col_sample_rate : col_sample_rates) {
                Scope.enter();
                try {
                    tfr = parse_test_file("./smalldata/gbm_test/ecology_model.csv");
                    DKV.put(tfr);
                    GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                    parms._train = tfr._key;
                    parms._response_column = "Angaus";// regression

                    parms._seed = 123;
                    parms._min_rows = 2;
                    parms._max_depth = 10;
                    parms._ntrees = 2;
                    parms._col_sample_rate = col_sample_rate;
                    parms._sample_rate = sample_rate;
                    // Build a first model; all remaining models should be equal
                    gbm = trainModel().get();
                    ModelMetricsRegression mm = ((ModelMetricsRegression) (gbm._output._training_metrics));
                    hm.put(mm.mse(), new Pair(sample_rate, col_sample_rate));
                } finally {
                    if (tfr != null)
                        tfr.remove();

                    if (vfr != null)
                        vfr.remove();

                    if (gbm != null)
                        gbm.delete();

                    Scope.exit();
                }
            }
        }
        double fullDataMSE = hm.entrySet().iterator().next().getKey();
        Iterator<Map.Entry<Double, Pair<Float, Float>>> it;
        int i = 0;
        Pair<Float, Float> last = null;
        // iterator over results (min to max MSE) - best to worst
        for (it = hm.entrySet().iterator(); it.hasNext(); ++i) {
            Map.Entry<Double, Pair<Float, Float>> n = it.next();
            if (i > 0)
                Assert.assertTrue(((n.getKey()) > fullDataMSE));
            // any sampling should make training set MSE worse

            Log.info((((((("MSE: " + (n.getKey())) + ", ") + ", row sample: ") + (_1())) + ", col sample: ") + (_2())));
            last = n.getValue();
        }
        // worst training MSE should belong to the most sampled case
        Assert.assertTrue(((last._1()) == (sample_rates[0])));
        Assert.assertTrue(((last._2()) == (col_sample_rates[0])));
    }

    @Test
    public void testStochasticGBMHoldout() {
        Frame tfr = null;
        Key[] ksplits = new Key[0];
        try {
            tfr = parse_test_file("./smalldata/gbm_test/ecology_model.csv");
            SplitFrame sf = new SplitFrame(tfr, new double[]{ 0.5, 0.5 }, new Key[]{ Key.make("train.hex"), Key.make("test.hex") });
            // Invoke the job
            sf.exec().get();
            ksplits = sf._destination_frames;
            GBMModel gbm = null;
            float[] sample_rates = new float[]{ 0.2F, 0.4F, 0.8F, 1.0F };
            float[] col_sample_rates = new float[]{ 0.4F, 0.8F, 1.0F };
            float[] col_sample_rates_per_tree = new float[]{ 0.4F, 0.6F, 1.0F };
            Map<Double, Triple<Float>> hm = new TreeMap<>();
            for (float sample_rate : sample_rates) {
                for (float col_sample_rate : col_sample_rates) {
                    for (float col_sample_rate_per_tree : col_sample_rates_per_tree) {
                        Scope.enter();
                        try {
                            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                            parms._train = ksplits[0];
                            parms._valid = ksplits[1];
                            parms._response_column = "Angaus";// regression

                            parms._seed = 42;
                            parms._min_rows = 2;
                            parms._max_depth = 12;
                            parms._ntrees = 6;
                            parms._col_sample_rate = col_sample_rate;
                            parms._col_sample_rate_per_tree = col_sample_rate_per_tree;
                            parms._sample_rate = sample_rate;
                            // Build a first model; all remaining models should be equal
                            gbm = trainModel().get();
                            // too slow, but passes (now)
                            // // Build a POJO, validate same results
                            // Frame pred = gbm.score(tfr);
                            // Assert.assertTrue(gbm.testJavaScoring(tfr,pred,1e-15));
                            // pred.remove();
                            ModelMetricsRegression mm = ((ModelMetricsRegression) (gbm._output._validation_metrics));
                            hm.put(mm.mse(), new Triple(sample_rate, col_sample_rate, col_sample_rate_per_tree));
                        } finally {
                            if (gbm != null)
                                gbm.delete();

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

    // PUBDEV-2476 Check reproducibility for the same # of chunks (i.e., same # of nodes) and same parameters
    @Test
    public void testChunks() {
        Frame tfr;
        int[] chunks = new int[]{ 1, 2, 2, 39, 39, 500 };
        final int N = chunks.length;
        double[] mses = new double[N];
        for (int i = 0; i < N; ++i) {
            Scope.enter();
            // Load data, hack frames
            tfr = parse_test_file("smalldata/covtype/covtype.20k.data");
            // rebalance to a given number of chunks
            Key dest = Key.make("df.rebalanced.hex");
            RebalanceDataSet rb = new RebalanceDataSet(tfr, dest, chunks[i]);
            H2O.submitTask(rb);
            rb.join();
            tfr.delete();
            tfr = DKV.get(dest).get();
            Assert.assertEquals(tfr.vec(0).nChunks(), chunks[i]);
            // Scope.track(tfr.replace(54, tfr.vecs()[54].toCategoricalVec())._key);
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "C55";
            parms._seed = 1234;
            parms._auto_rebalance = false;
            parms._col_sample_rate_per_tree = 0.5F;
            parms._col_sample_rate = 0.3F;
            parms._ntrees = 5;
            parms._max_depth = 5;
            // Build a first model; all remaining models should be equal
            GBM job = new GBM(parms);
            GBMModel drf = job.trainModel().get();
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

    @Test
    public void testLaplace2() {
        GBMModel gbm = null;
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        Frame pred = null;
        Frame res = null;
        Scope.enter();
        try {
            Frame train = parse_test_file("smalldata/gbm_test/ecology_model.csv");
            train.remove("Site").remove();// Remove unique ID

            train.remove("Method").remove();// Remove categorical

            DKV.put(train);
            // Update frame after hacking it
            parms._train = train._key;
            parms._response_column = "DSDist";// Train on the outcome

            parms._distribution = laplace;
            parms._sample_rate = 0.6F;
            parms._col_sample_rate = 0.8F;
            parms._col_sample_rate_per_tree = 0.8F;
            parms._seed = 1234;
            GBM job = new GBM(parms);
            gbm = job.trainModel().get();
            pred = parse_test_file("smalldata/gbm_test/ecology_eval.csv");
            res = gbm.score(pred);
            // Build a POJO, validate same results
            Assert.assertTrue(gbm.testJavaScoring(pred, res, 1.0E-15));
            Assert.assertTrue(((Math.abs(((((ModelMetricsRegression) (gbm._output._training_metrics))._mean_residual_deviance) - 23.05805))) < 1.0E-4));
        } finally {
            parms._train.remove();
            if (gbm != null)
                gbm.delete();

            if (pred != null)
                pred.remove();

            if (res != null)
                res.remove();

            Scope.exit();
        }
    }

    @Test
    public void testQuantileRegression() {
        GBMModel gbm = null;
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        Frame pred = null;
        Frame res = null;
        Scope.enter();
        try {
            Frame train = parse_test_file("smalldata/gbm_test/ecology_model.csv");
            train.remove("Site").remove();// Remove unique ID

            train.remove("Method").remove();// Remove categorical

            DKV.put(train);
            // Update frame after hacking it
            parms._train = train._key;
            parms._response_column = "DSDist";// Train on the outcome

            parms._distribution = DistributionFamily.quantile;
            parms._quantile_alpha = 0.4;
            parms._sample_rate = 0.6F;
            parms._col_sample_rate = 0.8F;
            parms._col_sample_rate_per_tree = 0.8F;
            parms._seed = 1234;
            GBM job = new GBM(parms);
            gbm = job.trainModel().get();
            pred = parse_test_file("smalldata/gbm_test/ecology_eval.csv");
            res = gbm.score(pred);
            // Build a POJO, validate same results
            Assert.assertTrue(gbm.testJavaScoring(pred, res, 1.0E-15));
            Assert.assertEquals(10.69611, ((ModelMetricsRegression) (gbm._output._training_metrics))._mean_residual_deviance, 0.1);
        } finally {
            parms._train.remove();
            if (gbm != null)
                gbm.delete();

            if (pred != null)
                pred.remove();

            if (res != null)
                res.remove();

            Scope.exit();
        }
    }

    @Test
    public void missingAndUnseenValues() {
        GBMModel gbm = null;
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        Frame train = null;
        Frame test = null;
        Frame train_preds = null;
        Frame test_preds = null;
        Scope.enter();
        try {
            {
                CreateFrame cf = new CreateFrame();
                cf.rows = 100;
                cf.cols = 10;
                cf.integer_range = 1000;
                cf.categorical_fraction = 1.0;
                cf.integer_fraction = 0.0;
                cf.binary_fraction = 0.0;
                cf.time_fraction = 0.0;
                cf.string_fraction = 0.0;
                cf.binary_ones_fraction = 0.0;
                cf.missing_fraction = 0.2;
                cf.factors = 3;
                cf.response_factors = 2;
                cf.positive_response = false;
                cf.has_response = true;
                cf.seed = 1235;
                cf.seed_for_column_types = 1234;
                train = cf.execImpl().get();
            }
            {
                CreateFrame cf = new CreateFrame();
                cf.rows = 100;
                cf.cols = 10;
                cf.integer_range = 1000;
                cf.categorical_fraction = 1.0;
                cf.integer_fraction = 0.0;
                cf.binary_fraction = 0.0;
                cf.time_fraction = 0.0;
                cf.string_fraction = 0.0;
                cf.binary_ones_fraction = 0.0;
                cf.missing_fraction = 0.2;
                cf.factors = 3;
                cf.response_factors = 2;
                cf.positive_response = false;
                cf.has_response = true;
                cf.seed = 4321;// different test set

                cf.seed_for_column_types = 1234;
                test = cf.execImpl().get();
            }
            parms._train = train._key;
            parms._response_column = "response";// Train on the outcome

            parms._distribution = DistributionFamily.multinomial;
            parms._max_depth = 20;
            parms._min_rows = 1;
            parms._ntrees = 5;
            parms._seed = 1;
            GBM job = new GBM(parms);
            gbm = job.trainModel().get();
            train_preds = gbm.score(train);
            test_preds = gbm.score(test);
            // Build a POJO, validate same results
            Assert.assertTrue(gbm.testJavaScoring(train, train_preds, 1.0E-15));
            Key old = gbm._key;
            gbm._key = Key.make(((gbm._key) + "ha"));
            Assert.assertTrue(gbm.testJavaScoring(test, test_preds, 1.0E-15));
            DKV.remove(old);
        } finally {
            if (gbm != null)
                gbm.delete();

            if (train != null)
                train.remove();

            if (test != null)
                test.remove();

            if (train_preds != null)
                train_preds.remove();

            if (test_preds != null)
                test_preds.remove();

            Scope.exit();
        }
    }

    @Test
    public void minSplitImprovement() {
        Frame tfr = null;
        Key[] ksplits = null;
        GBMModel gbm = null;
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
            double[] msi = new double[]{ 0, 0.1 };
            final int N = msi.length;
            double[] loglosses = new double[N];
            for (int i = 0; i < N; ++i) {
                // Load data, hack frames
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                parms._train = ksplits[0];
                parms._valid = ksplits[1];
                parms._response_column = tfr.names()[resp];
                parms._learn_rate = 0.05F;
                parms._min_split_improvement = msi[i];
                parms._ntrees = 10;
                parms._score_tree_interval = parms._ntrees;
                parms._max_depth = 5;
                GBM job = new GBM(parms);
                gbm = job.trainModel().get();
                loglosses[i] = gbm._output._scored_valid[((gbm._output._scored_valid.length) - 1)]._logloss;
                if (gbm != null)
                    gbm.delete();

            }
            for (int i = 0; i < (msi.length); ++i) {
                Log.info(((("min_split_improvement: " + (msi[i])) + " -> validation logloss: ") + (loglosses[i])));
            }
            int idx = ArrayUtils.minIndex(loglosses);
            Log.info(("Optimal min_split_improvement: " + (msi[idx])));
            Assert.assertTrue((0 == idx));
        } finally {
            if (gbm != null)
                gbm.delete();

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
        GBMModel gbm = null;
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
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                parms._train = ksplits[0];
                parms._valid = ksplits[1];
                parms._response_column = tfr.names()[resp];
                parms._learn_rate = 0.05F;
                parms._histogram_type = histoType[i];
                parms._ntrees = 10;
                parms._score_tree_interval = parms._ntrees;
                parms._max_depth = 5;
                parms._seed = -557121554;
                GBM job = new GBM(parms);
                gbm = job.trainModel().get();
                loglosses[i] = gbm._output._scored_valid[((gbm._output._scored_valid.length) - 1)]._logloss;
                if (gbm != null)
                    gbm.delete();

            }
            for (int i = 0; i < (histoType.length); ++i) {
                Log.info(((("histoType: " + (histoType[i])) + " -> validation logloss: ") + (loglosses[i])));
            }
            int idx = ArrayUtils.minIndex(loglosses);
            Log.info(("Optimal randomization: " + (histoType[idx])));
            Assert.assertTrue((4 == idx));
        } finally {
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
        GBMModel gbm = null;
        try {
            Scope.enter();
            tfr = parse_test_file("smalldata/covtype/covtype.20k.data");
            int resp = 54;
            Scope.track(tfr.replace(resp, tfr.vecs()[resp].toCategoricalVec()));
            DKV.put(tfr);
            SplitFrame sf = new SplitFrame(tfr, new double[]{ 0.5, 0.5 }, new Key[]{ Key.make("train.hex"), Key.make("valid.hex") });
            // Invoke the job
            sf.exec().get();
            ksplits = sf._destination_frames;
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = ksplits[0];
            parms._valid = ksplits[1];
            parms._response_column = tfr.names()[resp];
            parms._learn_rate = 0.05F;
            parms._min_split_improvement = 1.0E-5;
            parms._ntrees = 10;
            parms._score_tree_interval = parms._ntrees;
            parms._max_depth = 5;
            parms._sample_rate_per_class = new double[]{ 0.1F, 0.1F, 0.2F, 0.4F, 1.0F, 0.3F, 0.2F };
            GBM job = new GBM(parms);
            gbm = job.trainModel().get();
            if (gbm != null)
                gbm.delete();

        } finally {
            if (gbm != null)
                gbm.delete();

            if (tfr != null)
                tfr.delete();

            if ((ksplits[0]) != null)
                ksplits[0].remove();

            if ((ksplits[1]) != null)
                ksplits[1].remove();

            Scope.exit();
        }
    }

    // PUBDEV-2822
    @Test
    public void testNA() {
        String xy = ",0\n1,0\n2,0\n3,0\n4,-10\n,0";
        Key tr = Key.make("train");
        Frame df = ParseDataset.parse(tr, FVecTest.makeByteVec(Key.make("xy"), xy));
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        parms._train = tr;
        parms._response_column = "C2";
        parms._min_rows = 1;
        parms._learn_rate = 1;
        parms._ntrees = 1;
        GBM job = new GBM(parms);
        GBMModel gbm = job.trainModel().get();
        Frame preds = gbm.score(df);
        Log.info(df);
        Log.info(preds);
        Assert.assertTrue(gbm.testJavaScoring(df, preds, 1.0E-15));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(0)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(1)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(2)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(3)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(4)) - (-10)))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(5)) - 0))) < 1.0E-6));
        preds.remove();
        gbm.remove();
        df.remove();
    }

    // PUBDEV-2822
    @Test
    public void testNARight() {
        String xy = ",10\n1,0\n2,0\n3,0\n4,10\n,10";
        Key tr = Key.make("train");
        Frame df = ParseDataset.parse(tr, FVecTest.makeByteVec(Key.make("xy"), xy));
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        parms._train = tr;
        parms._response_column = "C2";
        parms._min_rows = 1;
        parms._learn_rate = 1;
        parms._ntrees = 1;
        GBM job = new GBM(parms);
        GBMModel gbm = job.trainModel().get();
        Frame preds = gbm.score(df);
        Log.info(df);
        Log.info(preds);
        Assert.assertTrue(gbm.testJavaScoring(df, preds, 1.0E-15));
        Assert.assertTrue(((preds.vec(0).at(0)) == 10));
        Assert.assertTrue(((preds.vec(0).at(1)) == 0));
        Assert.assertTrue(((preds.vec(0).at(2)) == 0));
        Assert.assertTrue(((preds.vec(0).at(3)) == 0));
        Assert.assertTrue(((preds.vec(0).at(4)) == 10));
        Assert.assertTrue(((preds.vec(0).at(5)) == 10));
        preds.remove();
        gbm.remove();
        df.remove();
    }

    // PUBDEV-2822
    @Test
    public void testNALeft() {
        String xy = ",0\n1,0\n2,0\n3,0\n4,10\n,0";
        Key tr = Key.make("train");
        Frame df = ParseDataset.parse(tr, FVecTest.makeByteVec(Key.make("xy"), xy));
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        parms._train = tr;
        parms._response_column = "C2";
        parms._min_rows = 1;
        parms._learn_rate = 1;
        parms._ntrees = 1;
        GBM job = new GBM(parms);
        GBMModel gbm = job.trainModel().get();
        Frame preds = gbm.score(df);
        Log.info(df);
        Log.info(preds);
        Assert.assertTrue(gbm.testJavaScoring(df, preds, 1.0E-15));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(0)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(1)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(2)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(3)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(4)) - 10))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(5)) - 0))) < 1.0E-6));
        preds.remove();
        gbm.remove();
        df.remove();
    }

    // PUBDEV-2822
    @Test
    public void testNAvsRest() {
        String xy = ",5\n1,0\n2,0\n3,0\n4,0\n,3";
        Key tr = Key.make("train");
        Frame df = ParseDataset.parse(tr, FVecTest.makeByteVec(Key.make("xy"), xy));
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        parms._train = tr;
        parms._response_column = "C2";
        parms._min_rows = 1;
        parms._learn_rate = 1;
        parms._ntrees = 1;
        GBM job = new GBM(parms);
        GBMModel gbm = job.trainModel().get();
        Frame preds = gbm.score(df);
        Log.info(df);
        Log.info(preds);
        Assert.assertTrue(gbm.testJavaScoring(df, preds, 1.0E-15));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(0)) - 4))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(1)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(2)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(3)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(4)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(5)) - 4))) < 1.0E-6));
        preds.remove();
        gbm.remove();
        df.remove();
    }

    // PUBDEV-2822
    @Test
    public void testOnevsRest() {
        String xy = "-9,5\n1,0\n2,0\n3,0\n4,0\n-9,3";
        Key tr = Key.make("train");
        Frame df = ParseDataset.parse(tr, FVecTest.makeByteVec(Key.make("xy"), xy));
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        parms._train = tr;
        parms._response_column = "C2";
        parms._min_rows = 1;
        parms._learn_rate = 1;
        parms._ntrees = 1;
        GBM job = new GBM(parms);
        GBMModel gbm = job.trainModel().get();
        Frame preds = gbm.score(df);
        Log.info(df);
        Log.info(preds);
        Assert.assertTrue(gbm.testJavaScoring(df, preds, 1.0E-15));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(0)) - 4))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(1)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(2)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(3)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(4)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(5)) - 4))) < 1.0E-6));
        preds.remove();
        gbm.remove();
        df.remove();
    }

    // PUBDEV-2822
    @Test
    public void testNACategorical() {
        String xy = ",0\nA,0\nB,0\nA,0\nD,-10\n,0";
        Key tr = Key.make("train");
        Frame df = ParseDataset.parse(tr, FVecTest.makeByteVec(Key.make("xy"), xy));
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        parms._train = tr;
        parms._response_column = "C2";
        parms._min_rows = 1;
        parms._learn_rate = 1;
        parms._ntrees = 1;
        GBM job = new GBM(parms);
        GBMModel gbm = job.trainModel().get();
        Log.info(df.toTwoDimTable());
        Frame preds = gbm.score(df);
        Log.info(preds.toTwoDimTable());
        Assert.assertTrue(gbm.testJavaScoring(df, preds, 1.0E-15));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(0)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(1)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(2)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(3)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(4)) - (-10)))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(5)) - 0))) < 1.0E-6));
        preds.remove();
        gbm.remove();
        df.remove();
    }

    // PUBDEV-2822
    @Test
    public void testNARightCategorical() {
        String xy = ",10\nA,0\nB,0\nA,0\n4,10\n,10";
        Key tr = Key.make("train");
        Frame df = ParseDataset.parse(tr, FVecTest.makeByteVec(Key.make("xy"), xy));
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        parms._train = tr;
        parms._response_column = "C2";
        parms._min_rows = 1;
        parms._learn_rate = 1;
        parms._ntrees = 1;
        GBM job = new GBM(parms);
        GBMModel gbm = job.trainModel().get();
        Frame preds = gbm.score(df);
        Log.info(df);
        Log.info(preds);
        Assert.assertTrue(gbm.testJavaScoring(df, preds, 1.0E-15));
        Assert.assertTrue(((preds.vec(0).at(0)) == 10));
        Assert.assertTrue(((preds.vec(0).at(1)) == 0));
        Assert.assertTrue(((preds.vec(0).at(2)) == 0));
        Assert.assertTrue(((preds.vec(0).at(3)) == 0));
        Assert.assertTrue(((preds.vec(0).at(4)) == 10));
        Assert.assertTrue(((preds.vec(0).at(5)) == 10));
        preds.remove();
        gbm.remove();
        df.remove();
    }

    // PUBDEV-2822
    @Test
    public void testNALeftCategorical() {
        String xy = ",0\nA,0\nB,0\nA,0\nD,10\n,0";
        Key tr = Key.make("train");
        Frame df = ParseDataset.parse(tr, FVecTest.makeByteVec(Key.make("xy"), xy));
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        parms._train = tr;
        parms._response_column = "C2";
        parms._min_rows = 1;
        parms._learn_rate = 1;
        parms._ntrees = 1;
        GBM job = new GBM(parms);
        GBMModel gbm = job.trainModel().get();
        Frame preds = gbm.score(df);
        Log.info(df);
        Log.info(preds);
        Assert.assertTrue(gbm.testJavaScoring(df, preds, 1.0E-15));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(0)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(1)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(2)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(3)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(4)) - 10))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(5)) - 0))) < 1.0E-6));
        preds.remove();
        gbm.remove();
        df.remove();
    }

    // PUBDEV-2822
    @Test
    public void testNAvsRestCategorical() {
        String xy = ",5\nA,0\nB,0\nA,0\nD,0\n,3";
        Key tr = Key.make("train");
        Frame df = ParseDataset.parse(tr, FVecTest.makeByteVec(Key.make("xy"), xy));
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        parms._train = tr;
        parms._response_column = "C2";
        parms._min_rows = 1;
        parms._learn_rate = 1;
        parms._ntrees = 1;
        GBM job = new GBM(parms);
        GBMModel gbm = job.trainModel().get();
        Frame preds = gbm.score(df);
        Log.info(df);
        Log.info(preds);
        Assert.assertTrue(gbm.testJavaScoring(df, preds, 1.0E-15));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(0)) - 4))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(1)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(2)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(3)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(4)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(5)) - 4))) < 1.0E-6));
        preds.remove();
        gbm.remove();
        df.remove();
    }

    // PUBDEV-2822
    @Test
    public void testUnseenNACategorical() {
        String xy = "B,-5\nA,0\nB,0\nA,0\nD,0\nA,3";
        Key tr = Key.make("train");
        Frame df = ParseDataset.parse(tr, FVecTest.makeByteVec(Key.make("xy"), xy));
        String test = ",5\n,0\nB,0\n,0\nE,0\n,3";
        Key te = Key.make("test");
        Frame df2 = ParseDataset.parse(te, FVecTest.makeByteVec(Key.make("te"), test));
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        parms._train = tr;
        parms._response_column = "C2";
        parms._min_rows = 1;
        parms._learn_rate = 1;
        parms._ntrees = 1;
        GBM job = new GBM(parms);
        GBMModel gbm = job.trainModel().get();
        Scope.enter();// AdaptTestTrain leaks when it does inplace Vec adaptation, need a Scope to catch that stuff

        Frame preds = gbm.score(df);
        Frame preds2 = gbm.score(df2);
        Log.info(df);
        Log.info(preds);
        Log.info(df2);
        Log.info(preds2);
        Assert.assertTrue(gbm.testJavaScoring(df, preds, 1.0E-15));
        Assert.assertTrue(gbm.testJavaScoring(df2, preds2, 1.0E-15));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(0)) - (-2.5)))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(1)) - 1))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(2)) - (-2.5)))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(3)) - 1))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(4)) - 0))) < 1.0E-6));
        Assert.assertTrue(((Math.abs(((preds.vec(0).at(5)) - 1))) < 1.0E-6));
        preds.remove();
        preds2.remove();
        gbm.remove();
        df.remove();
        df2.remove();
        Scope.exit();
    }

    @Test
    public void unseenMissing() {
        GBMModel gbm = null;
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        Frame train = null;
        Frame test = null;
        Frame train_preds = null;
        Frame test_preds = null;
        Scope.enter();
        try {
            {
                CreateFrame cf = new CreateFrame();
                cf.rows = 100;
                cf.cols = 10;
                cf.integer_range = 1000;
                cf.categorical_fraction = 1.0;
                cf.integer_fraction = 0.0;
                cf.binary_fraction = 0.0;
                cf.time_fraction = 0.0;
                cf.string_fraction = 0.0;
                cf.binary_ones_fraction = 0.0;
                cf.missing_fraction = 0.0;
                cf.factors = 3;
                cf.response_factors = 2;
                cf.positive_response = false;
                cf.has_response = true;
                cf.seed = 1235;
                cf.seed_for_column_types = 1234;
                train = cf.execImpl().get();
            }
            {
                CreateFrame cf = new CreateFrame();
                cf.rows = 100;
                cf.cols = 10;
                cf.integer_range = 1000;
                cf.categorical_fraction = 1.0;
                cf.integer_fraction = 0.0;
                cf.binary_fraction = 0.0;
                cf.time_fraction = 0.0;
                cf.string_fraction = 0.0;
                cf.binary_ones_fraction = 0.0;
                cf.missing_fraction = 0.8;
                cf.factors = 3;
                cf.response_factors = 2;
                cf.positive_response = false;
                cf.has_response = true;
                cf.seed = 4321;// different test set

                cf.seed_for_column_types = 1234;
                test = cf.execImpl().get();
            }
            parms._train = train._key;
            parms._response_column = "response";// Train on the outcome

            parms._distribution = DistributionFamily.multinomial;
            parms._max_depth = 20;
            parms._min_rows = 1;
            parms._ntrees = 5;
            parms._seed = 1;
            GBM job = new GBM(parms);
            gbm = job.trainModel().get();
            train_preds = gbm.score(train);
            test_preds = gbm.score(test);
            // Build a POJO, validate same results
            Assert.assertTrue(gbm.testJavaScoring(train, train_preds, 1.0E-15));
            Key old = gbm._key;
            gbm._key = Key.make(((gbm._key) + "ha"));
            Assert.assertTrue(gbm.testJavaScoring(test, test_preds, 1.0E-15));
            DKV.remove(old);
        } finally {
            if (gbm != null)
                gbm.delete();

            if (train != null)
                train.remove();

            if (test != null)
                test.remove();

            if (train_preds != null)
                train_preds.remove();

            if (test_preds != null)
                test_preds.remove();

            Scope.exit();
        }
    }

    // PUBDEV-3066
    @Test
    public void testAnnealingStop() {
        Frame tfr = null;
        final int N = 1;
        Scope.enter();
        try {
            // Load data, hack frames
            tfr = parse_test_file("./smalldata/airlines/allyears2k_headers.zip");
            for (String s : new String[]{ "DepTime", "ArrTime", "ActualElapsedTime", "AirTime", "ArrDelay", "DepDelay", "Cancelled", "CancellationCode", "CarrierDelay", "WeatherDelay", "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsArrDelayed" }) {
                tfr.remove(s).remove();
            }
            DKV.put(tfr);
            for (int i = 0; i < N; ++i) {
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                parms._train = tfr._key;
                parms._response_column = "IsDepDelayed";
                parms._nbins = 10;
                parms._nbins_cats = 500;
                parms._ntrees = 100;
                parms._learn_rate_annealing = 0.5;
                parms._max_depth = 5;
                parms._min_rows = 10;
                parms._distribution = DistributionFamily.bernoulli;
                parms._balance_classes = true;
                parms._seed = 0;
                // Build a first model; all remaining models should be equal
                GBMModel gbm = trainModel().get();
                Assert.assertNotEquals(gbm._output._ntrees, parms._ntrees);
                gbm.delete();
            }
        } finally {
            if (tfr != null)
                tfr.remove();

        }
        Scope.exit();
    }

    @Test
    public void testHuber2() {
        GBMModel gbm = null;
        GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
        Frame pred = null;
        Frame res = null;
        Scope.enter();
        try {
            Frame train = parse_test_file("smalldata/gbm_test/ecology_model.csv");
            train.remove("Site").remove();// Remove unique ID

            train.remove("Method").remove();// Remove categorical

            DKV.put(train);
            // Update frame after hacking it
            parms._train = train._key;
            parms._response_column = "DSDist";// Train on the outcome

            parms._distribution = huber;
            parms._huber_alpha = 0.5;
            parms._sample_rate = 0.6F;
            parms._col_sample_rate = 0.8F;
            parms._col_sample_rate_per_tree = 0.8F;
            parms._seed = 1234;
            GBM job = new GBM(parms);
            gbm = job.trainModel().get();
            pred = parse_test_file("smalldata/gbm_test/ecology_eval.csv");
            res = gbm.score(pred);
            // Build a POJO, validate same results
            Assert.assertTrue(gbm.testJavaScoring(pred, res, 1.0E-15));
            Assert.assertEquals(1485, ((ModelMetricsRegression) (gbm._output._training_metrics))._MSE, 50);
            Assert.assertTrue(((Math.abs(((((ModelMetricsRegression) (gbm._output._training_metrics))._mean_residual_deviance) - 256.88))) < 1));
        } finally {
            parms._train.remove();
            if (gbm != null)
                gbm.delete();

            if (pred != null)
                pred.remove();

            if (res != null)
                res.remove();

            Scope.exit();
        }
    }

    @Test
    public void testLaplace() {
        Frame tfr = null;
        GBMModel gbm = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._seed = 912559;
            parms._distribution = laplace;
            gbm = trainModel().get();
            Assert.assertEquals(8.05716257, ((ModelMetricsRegression) (gbm._output._training_metrics))._MSE, 1.0E-5);
            /* MAE */
            Assert.assertEquals(1.42298, ((ModelMetricsRegression) (gbm._output._training_metrics))._mean_residual_deviance, 1.0E-5);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (gbm != null)
                gbm.deleteCrossValidationModels();

            if (gbm != null)
                gbm.delete();

        }
    }

    @Test
    public void testGaussian() {
        Frame tfr = null;
        GBMModel gbm = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._seed = 912559;
            parms._distribution = gaussian;
            gbm = trainModel().get();
            Assert.assertEquals(2.9423857564, ((ModelMetricsRegression) (gbm._output._training_metrics))._MSE, 1.0E-5);
            Assert.assertEquals(2.9423857564, ((ModelMetricsRegression) (gbm._output._training_metrics))._mean_residual_deviance, 1.0E-5);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (gbm != null)
                gbm.deleteCrossValidationModels();

            if (gbm != null)
                gbm.delete();

        }
    }

    @Test
    public void testHuberDeltaLarge() {
        Frame tfr = null;
        GBMModel gbm = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._seed = 912559;
            parms._distribution = huber;
            parms._huber_alpha = 1;// nothing is an outlier - same as gaussian

            gbm = trainModel().get();
            Assert.assertEquals(2.9423857564, ((ModelMetricsRegression) (gbm._output._training_metrics))._MSE, 0.01);
            // huber loss with delta -> max(error) goes to MSE
            Assert.assertEquals(2.9423857564, ((ModelMetricsRegression) (gbm._output._training_metrics))._mean_residual_deviance, 0.01);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (gbm != null)
                gbm.deleteCrossValidationModels();

            if (gbm != null)
                gbm.delete();

        }
    }

    @Test
    public void testHuberDeltaTiny() {
        Frame tfr = null;
        GBMModel gbm = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._seed = 912559;
            parms._distribution = huber;
            parms._huber_alpha = 0.01;// everything is an outlier and we should get laplace loss

            gbm = trainModel().get();
            Assert.assertEquals(8.05716257, ((ModelMetricsRegression) (gbm._output._training_metrics))._MSE, 0.3);
            // Huber loss can be derived from MAE since no obs weights
            double delta = 0.0047234;// hardcoded from output

            double MAE = 1.42298;// see laplace above

            Assert.assertEquals((((2 * MAE) - delta) * delta), ((ModelMetricsRegression) (gbm._output._training_metrics))._mean_residual_deviance, 0.1);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (gbm != null)
                gbm.deleteCrossValidationModels();

            if (gbm != null)
                gbm.delete();

        }
    }

    @Test
    public void testHuber() {
        Frame tfr = null;
        GBMModel gbm = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._seed = 912559;
            parms._distribution = huber;
            parms._huber_alpha = 0.9;// that's the default

            gbm = trainModel().get();
            Assert.assertEquals(4.447062185, ((ModelMetricsRegression) (gbm._output._training_metrics))._MSE, 1.0E-5);
            Assert.assertEquals(1.962926332, ((ModelMetricsRegression) (gbm._output._training_metrics))._mean_residual_deviance, 1.0E-4);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (gbm != null)
                gbm.deleteCrossValidationModels();

            if (gbm != null)
                gbm.delete();

        }
    }

    @Test
    public void testHuberNoise() {
        Frame tfr = null;
        GBMModel gbm = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._seed = 912559;
            parms._distribution = huber;
            parms._huber_alpha = 0.9;// that's the default

            parms._pred_noise_bandwidth = 0.2;
            gbm = trainModel().get();
            Assert.assertEquals(4.8056900203, ((ModelMetricsRegression) (gbm._output._training_metrics))._MSE, 1.0E-5);
            Assert.assertEquals(2.0080997, ((ModelMetricsRegression) (gbm._output._training_metrics))._mean_residual_deviance, 1.0E-4);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (gbm != null)
                gbm.deleteCrossValidationModels();

            if (gbm != null)
                gbm.delete();

        }
    }

    @Test
    public void testDeviances() {
        for (DistributionFamily dist : DistributionFamily.values()) {
            if (((dist == (modified_huber)) || (dist == (quasibinomial))) || (dist == (ordinal)))
                continue;

            Frame tfr = null;
            Frame res = null;
            Frame preds = null;
            GBMModel gbm = null;
            try {
                tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                parms._train = tfr._key;
                String resp = tfr.lastVecName();
                if (((dist == (modified_huber)) || (dist == (bernoulli))) || (dist == (multinomial))) {
                    resp = (dist == (multinomial)) ? "rad" : "chas";
                    Vec v = tfr.remove(resp);
                    tfr.add(resp, v.toCategoricalVec());
                    v.remove();
                    DKV.put(tfr);
                }
                parms._response_column = resp;
                parms._distribution = dist;
                gbm = trainModel().get();
                preds = gbm.score(tfr);
                res = gbm.computeDeviances(tfr, preds, "myDeviances");
                double meanDeviance = res.anyVec().mean();
                if ((gbm._output.nclasses()) == 2)
                    Assert.assertEquals(meanDeviance, ((ModelMetricsBinomial) (gbm._output._training_metrics))._logloss, (1.0E-6 * (Math.abs(meanDeviance))));
                else
                    if ((gbm._output.nclasses()) > 2)
                        Assert.assertEquals(meanDeviance, ((ModelMetricsMultinomial) (gbm._output._training_metrics))._logloss, (1.0E-6 * (Math.abs(meanDeviance))));
                    else
                        Assert.assertEquals(meanDeviance, ((ModelMetricsRegression) (gbm._output._training_metrics))._mean_residual_deviance, (1.0E-6 * (Math.abs(meanDeviance))));


            } finally {
                if (tfr != null)
                    tfr.delete();

                if (res != null)
                    res.delete();

                if (preds != null)
                    preds.delete();

                if (gbm != null)
                    gbm.delete();

            }
        }
    }

    @Test
    public void testCatEncoding() {
        for (Model.Parameters.CategoricalEncodingScheme c : CategoricalEncodingScheme.values()) {
            if (c != (CategoricalEncodingScheme.AUTO))
                continue;

            Frame tfr = null;
            GBMModel gbm = null;
            Frame fr2 = null;
            try {
                tfr = parse_test_file("./smalldata/junit/weather.csv");
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                parms._train = tfr._key;
                parms._response_column = tfr.lastVecName();
                parms._ntrees = 5;
                parms._categorical_encoding = c;
                gbm = trainModel().get();
                // Done building model; produce a score column with predictions
                fr2 = gbm.score(tfr);
                // Build a POJO, validate same results
                Assert.assertTrue(gbm.testJavaScoring(tfr, fr2, 1.0E-15));
            } finally {
                if (tfr != null)
                    tfr.delete();

                if (fr2 != null)
                    fr2.delete();

                if (gbm != null)
                    gbm.deleteCrossValidationModels();

                if (gbm != null)
                    gbm.delete();

            }
        }
    }

    @Test
    public void testCatEncodingCV() {
        for (Model.Parameters.CategoricalEncodingScheme c : CategoricalEncodingScheme.values()) {
            if (c != (CategoricalEncodingScheme.AUTO))
                continue;

            Frame tfr = null;
            GBMModel gbm = null;
            try {
                tfr = parse_test_file("./smalldata/junit/weather.csv");
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                parms._train = tfr._key;
                parms._response_column = tfr.lastVecName();
                parms._ntrees = 5;
                parms._categorical_encoding = c;
                parms._nfolds = 3;
                gbm = trainModel().get();
            } finally {
                if (tfr != null)
                    tfr.delete();

                if (gbm != null)
                    gbm.deleteCrossValidationModels();

                if (gbm != null)
                    gbm.delete();

            }
        }
    }

    // A test of the validity of categorical splits
    @Test
    public void testCategoricalSplits() throws FileNotFoundException {
        Frame fr = null;
        GBMModel model = null;
        Scope.enter();
        try {
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            fr = parse_test_file("smalldata/gbm_test/ecology_model.csv");
            fr.remove("Site").remove();
            fr.remove("SegSumT").remove();
            fr.remove("SegTSeas").remove();
            fr.remove("SegLowFlow").remove();
            fr.remove("DSDist").remove();
            fr.remove("DSMaxSlope").remove();
            fr.remove("USAvgT").remove();
            fr.remove("USRainDays").remove();
            fr.remove("USSlope").remove();
            // fr.remove("USNative").remove();
            fr.remove("DSDam").remove();
            // fr.remove("LocSed").remove();
            fr.remove("Method").remove();
            int ci = fr.find("Angaus");
            Scope.track(fr.replace(ci, fr.vecs()[ci].toCategoricalVec()));// Convert response 'Angaus' to categorical

            DKV.put(fr);
            parms._train = fr._key;
            parms._response_column = "Angaus";
            parms._ntrees = 1;
            parms._min_rows = 10;
            parms._max_depth = 13;
            parms._distribution = DistributionFamily.multinomial;
            model = trainModel().get();
            // StreamingSchema ss = new StreamingSchema(model.getMojo(), "model.zip");
            // FileOutputStream fos = new FileOutputStream("model.zip");
            // ss.getStreamWriter().writeTo(fos);
        } finally {
            if (model != null)
                model.delete();

            if (fr != null)
                fr.remove();

            Scope.exit();
        }
    }

    // A test of the validity of categorical splits
    @Test
    public void testCategoricalSplits2() throws FileNotFoundException {
        Frame fr = null;
        GBMModel model = null;
        Scope.enter();
        try {
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            fr = parse_test_file("smalldata/airlines/allyears2k_headers.zip");
            Frame fr2 = new Frame(Key.<Frame>make(), new String[]{ "C", "R" }, new Vec[]{ fr.vec("Origin"), fr.vec("IsDepDelayed") });
            int ci = fr2.find("R");
            Scope.track(fr2.replace(ci, fr2.vecs()[ci].toCategoricalVec()));// Convert response 'Angaus' to categorical

            DKV.put(fr2);
            parms._train = fr2._key;
            parms._response_column = "R";
            parms._ntrees = 1;
            parms._min_rows = 1000;
            parms._max_depth = 4;
            parms._distribution = DistributionFamily.bernoulli;
            model = trainModel().get();
            DKV.remove(fr2._key);
            // StreamingSchema ss = new StreamingSchema(model.getMojo(), "model.zip");
            // FileOutputStream fos = new FileOutputStream("model.zip");
            // ss.getStreamWriter().writeTo(fos);
        } finally {
            if (model != null)
                model.delete();

            if (fr != null)
                fr.remove();

            Scope.exit();
        }
    }

    @Test
    public void highCardinalityLowNbinsCats() {
        highCardinality(2000);
    }

    @Test
    public void highCardinalityHighNbinsCats() {
        highCardinality(6000);
    }

    @Test
    public void lowCardinality() throws IOException {
        for (boolean sort_cats : new boolean[]{ true, false }) {
            int[] vals = new int[]{ 2, 10, 20, 25, 26, 27, 100 };
            double[] maes = new double[vals.length];
            int i = 0;
            for (int nbins_cats : vals) {
                GBMModel model = null;
                GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                Frame train;
                Frame train_preds = null;
                Scope.enter();
                train = parse_test_file("smalldata/gbm_test/alphabet_cattest.csv");
                try {
                    parms._train = train._key;
                    parms._response_column = "y";// Train on the outcome

                    parms._max_depth = 2;
                    parms._min_rows = 1;
                    parms._ntrees = 1;
                    parms._learn_rate = 1;
                    parms._nbins_cats = nbins_cats;
                    if (sort_cats)
                        parms._categorical_encoding = CategoricalEncodingScheme.SortByResponse;

                    GBM job = new GBM(parms);
                    model = job.trainModel().get();
                    StreamingSchema ss = new StreamingSchema(model.getMojo(), "model.zip");
                    FileOutputStream fos = new FileOutputStream("model.zip");
                    ss.getStreamWriter().writeTo(fos);
                    train_preds = model.score(train);
                    Assert.assertTrue(model.testJavaScoring(train, train_preds, 1.0E-15));
                    double mae = ModelMetricsRegression.make(train_preds.vec(0), train.vec("y"), gaussian).mae();
                    Log.info(("Train MAE: " + mae));
                    maes[(i++)] = mae;
                    if ((nbins_cats >= 25) || sort_cats)
                        Assert.assertEquals(0, mae, 1.0E-8);
                    else// sorting of categoricals is enough

                        Assert.assertTrue((mae > 0));

                } finally {
                    if (model != null)
                        model.delete();

                    if (train != null)
                        train.remove();

                    if (train_preds != null)
                        train_preds.remove();

                    new File("model.zip").delete();
                    Scope.exit();
                }
            }
            Log.info(Arrays.toString(vals));
            Log.info(Arrays.toString(maes));
        }
    }

    @Test
    public void RegressionCars() {
        Frame tfr = null;
        Frame trainFrame = null;
        Frame testFrame = null;
        Frame preds = null;
        GBMModel model = null;
        Scope.enter();
        try {
            // Parse frame into H2O
            tfr = parse_test_file("./smalldata/junit/cars.csv");
            DKV.put(tfr);
            // split into train/test
            SplitFrame sf = new SplitFrame(tfr, new double[]{ 0.7, 0.3 }, null);
            sf.exec().get();
            Key[] ksplits = sf._destination_frames;
            trainFrame = ((Frame) (ksplits[0].get()));
            testFrame = ((Frame) (ksplits[1].get()));
            // define special columns
            // String response = "cylinders"; // passes
            String response = "economy (mpg)";// fails

            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = trainFrame._key;
            parms._valid = testFrame._key;
            parms._response_column = response;
            parms._ignored_columns = new String[]{ "name" };
            // parms._dmatrix_type = GBMModel.GBMParameters.DMatrixType.dense;
            // parms._backend = GBMModel.GBMParameters.Backend.cpu;
            // parms._tree_method = GBMModel.GBMParameters.TreeMethod.exact;
            model = trainModel().get();
            Log.info(model);
            preds = model.score(testFrame);
            Assert.assertTrue(model.testJavaScoring(testFrame, preds, 1.0E-6));
            Assert.assertEquals(mae(), mae(), 1.0E-5);
            Assert.assertTrue(((preds.anyVec().sigma()) > 0));
        } finally {
            Scope.exit();
            if (trainFrame != null)
                trainFrame.remove();

            if (testFrame != null)
                testFrame.remove();

            if (tfr != null)
                tfr.remove();

            if (preds != null)
                preds.remove();

            if (model != null) {
                model.delete();
            }
        }
    }

    @Test
    public void testCustomEarlyStoppingValidation() {
        try {
            Scope.enter();
            Frame training = Scope.track(parse_test_file("./smalldata/junit/cars.csv"));
            String response = "economy (mpg)";
            ScoreKeeper[] customStoppingMetrics = new ScoreKeeper.StoppingMetric[]{ StoppingMetric.custom, StoppingMetric.custom_increasing };
            for (ScoreKeeper.StoppingMetric stoppingMetric : customStoppingMetrics) {
                GBMModel model = null;
                try {
                    GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
                    parms._train = training._key;
                    parms._response_column = response;
                    parms._ignored_columns = new String[]{ "name" };
                    parms._stopping_rounds = 2;
                    parms._stopping_metric = stoppingMetric;
                    model = trainModel().get();
                    Assert.fail(("Custom stopping " + " shouldn't work without a custom metric"));
                } catch (H2OModelBuilderIllegalArgumentException e) {
                    if (((e.getMessage()) == null) || (!(e.getMessage().contains(("ERRR on field: _stopping_metric: " + "Custom metric function needs to be defined in order to use it for early stopping."))))) {
                        throw e;
                    }
                    // suppress the expected exception
                } finally {
                    if (model != null) {
                        model.delete();
                    }
                }
            }
        } finally {
            Scope.exit();
        }
    }

    // PUBDEV-3482
    @Test
    public void testQuasibinomial() {
        Scope.enter();
        // test it behaves like binomial on binary data
        GBMModel model = null;
        GBMModel model2 = null;
        GBMModel model3 = null;
        Frame fr = parse_test_file("smalldata/glm_test/prostate_cat_replaced.csv");
        // turn numeric response 0/1 into a categorical factor
        Frame preds = null;
        Frame preds2 = null;
        Frame preds3 = null;
        Vec r = fr.vec("CAPSULE").toCategoricalVec();
        fr.remove("CAPSULE").remove();
        fr.add("CAPSULE", r);
        DKV.put(fr);
        // same dataset, but keep numeric response 0/1
        Frame fr2 = parse_test_file("smalldata/glm_test/prostate_cat_replaced.csv");
        // same dataset, but make numeric response 0/2, can only be handled by quasibinomial
        Frame fr3 = parse_test_file("smalldata/glm_test/prostate_cat_replaced.csv");
        new MRTask() {
            @Override
            public void map(Chunk[] cs) {
                for (int i = 0; i < (cs[0]._len); ++i) {
                    cs[1].set(i, ((cs[1].at8(i)) == 1 ? 2 : 0));
                }
            }
        }.doAll(fr3);
        try {
            GBMModel.GBMParameters params = new GBMModel.GBMParameters();
            params._response_column = "CAPSULE";
            params._ignored_columns = new String[]{ "ID" };
            params._seed = 1234;
            params._ntrees = 500;
            params._nfolds = 3;
            params._learn_rate = 0.01;
            params._min_rows = 1;
            params._min_split_improvement = 0;
            params._stopping_rounds = 10;
            params._stopping_tolerance = 0;
            // binomial - categorical response, optimize logloss
            params._train = fr._key;
            params._distribution = DistributionFamily.bernoulli;
            GBM gbm = new GBM(params);
            model = gbm.trainModel().get();
            preds = model.score(fr);
            // quasibinomial - numeric response 0/1, minimize deviance (negative log-likelihood)
            params._distribution = DistributionFamily.quasibinomial;
            params._train = fr2._key;
            GBM gbm2 = new GBM(params);
            model2 = gbm2.trainModel().get();
            preds2 = model2.score(fr2);
            // quasibinomial - numeric response 0/20, minimize deviance (negative log-likelihood)
            params._distribution = DistributionFamily.quasibinomial;
            params._train = fr3._key;
            GBM gbm3 = new GBM(params);
            model3 = gbm3.trainModel().get();
            preds3 = model3.score(fr3);
            // Done building model; produce a score column with predictions
            if (preds != null)
                Log.info(preds.toTwoDimTable());

            if (preds2 != null)
                Log.info(preds2.toTwoDimTable());

            if (preds3 != null)
                Log.info(preds3.toTwoDimTable());

            // compare training metrics of both models
            if ((model != null) && (model2 != null)) {
                Assert.assertEquals(logloss(), logloss(), 0.002);
                // compare CV metrics of both models
                Assert.assertEquals(logloss(), logloss(), 0.001);
            }
            // Build a POJO/MOJO, validate same results
            if (model2 != null)
                Assert.assertTrue(model2.testJavaScoring(fr2, preds2, 1.0E-15));

            if (model3 != null)
                Assert.assertTrue(model3.testJavaScoring(fr3, preds3, 1.0E-15));

            // compare training predictions of both models (just compare probs)
            if ((preds != null) && (preds2 != null)) {
                preds.remove(0);
                preds2.remove(0);
                Assert.assertTrue(isIdenticalUpToRelTolerance(preds, preds2, 0.01));
            }
        } finally {
            if (preds != null)
                preds.delete();

            if (preds2 != null)
                preds2.delete();

            if (preds3 != null)
                preds3.delete();

            if (fr != null)
                fr.delete();

            if (fr2 != null)
                fr2.delete();

            if (fr3 != null)
                fr3.delete();

            if (model != null) {
                model.deleteCrossValidationModels();
                model.delete();
            }
            if (model2 != null) {
                model2.deleteCrossValidationModels();
                model2.delete();
            }
            if (model3 != null) {
                model3.deleteCrossValidationModels();
                model3.delete();
            }
            Scope.exit();
        }
    }

    @Test
    public void testMonotoneConstraintsInverse() {
        Scope.enter();
        try {
            final String response = "power (hp)";
            Frame f = parse_test_file("smalldata/junit/cars.csv");
            f.replace(f.find(response), f.vecs()[f.find("cylinders")].toNumericVec()).remove();
            DKV.put(Scope.track(f));
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._response_column = response;
            parms._train = f._key;
            parms._ignored_columns = new String[]{ "name" };
            parms._seed = 42;
            GBMModel.GBMParameters noConstrParams = ((GBMModel.GBMParameters) (parms.clone()));
            GBMModel noConstrModel = trainModel().get();
            Scope.track_generic(noConstrModel);
            Assert.assertTrue(((noConstrModel._output._varimp.toMap().get("cylinders")) > 0));
            GBMModel.GBMParameters constrParams = ((GBMModel.GBMParameters) (parms.clone()));
            constrParams._monotone_constraints = new KeyValue[]{ new KeyValue("cylinders", (-1)) };
            GBMModel constrModel = trainModel().get();
            Scope.track_generic(constrModel);
            // we essentially eliminated the effect of the feature by setting an inverted constraint
            Assert.assertEquals(constrModel._output._varimp.toMap().get("cylinders"), 0, 0);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testMonotoneConstraintsProstate() {
        try {
            Scope.enter();
            Frame f = Scope.track(parse_test_file("smalldata/logreg/prostate.csv"));
            f.replace(f.find("CAPSULE"), f.vec("CAPSULE").toNumericVec());
            DKV.put(f);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._response_column = "CAPSULE";
            parms._train = f._key;
            parms._monotone_constraints = new KeyValue[]{ new KeyValue("AGE", 1) };
            parms._ignored_columns = new String[]{ "ID" };
            parms._ntrees = 50;
            parms._seed = 42;
            String[] uniqueAges = Scope.track(f.vec("AGE").toCategoricalVec()).domain();
            GBMModel model = trainModel().get();
            Scope.track_generic(model);
            Vec lastPreds = null;
            for (String ageStr : uniqueAges) {
                final int age = Integer.parseInt(ageStr);
                new MRTask() {
                    @Override
                    public void map(Chunk c) {
                        for (int i = 0; i < (c._len); i++)
                            c.set(i, age);

                    }
                }.doAll(f.vec("AGE"));
                Assert.assertEquals(age, f.vec("AGE").min(), 0);
                Assert.assertEquals(age, f.vec("AGE").max(), 0);
                Vec currentPreds = Scope.track(model.score(f)).anyVec();
                if (lastPreds != null)
                    for (int i = 0; i < (lastPreds.length()); i++) {
                        Assert.assertTrue(((("age=" + age) + ", id=") + (f.vec("ID").at8(i))), ((lastPreds.at(i)) <= (currentPreds.at(i))));
                    }

                lastPreds = currentPreds;
            }
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testMonotoneConstraintsUnsupported() {
        try {
            Scope.enter();
            Frame f = Scope.track(parse_test_file("smalldata/logreg/prostate.csv"));
            f.replace(f.find("CAPSULE"), f.vec("CAPSULE").toCategoricalVec());
            DKV.put(f);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._response_column = "CAPSULE";
            parms._train = f._key;
            parms._monotone_constraints = new KeyValue[]{ new KeyValue("AGE", 1) };
            expectedException.expectMessage(("ERRR on field: _monotone_constraints: " + "Monotone constraints are only supported for Gaussian distribution, your distribution: bernoulli."));
            GBMModel model = trainModel().get();
            Scope.track_generic(model);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testMonotoneConstraintSingleSplit() {
        GBMTest.checkMonotonic(2);
    }

    @Test
    public void testMonotoneConstraintMultiSplit() {
        GBMTest.checkMonotonic(5);
    }
}

