package hex.deeplearning;


import DeepLearningParameters.Activation;
import DeepLearningParameters.Activation.Maxout;
import DeepLearningParameters.Activation.MaxoutWithDropout;
import DeepLearningParameters.Activation.Rectifier;
import DeepLearningParameters.Activation.RectifierWithDropout;
import DeepLearningParameters.Activation.Tanh;
import DeepLearningParameters.Activation.TanhWithDropout;
import DeepLearningParameters.InitialWeightDistribution;
import DeepLearningParameters.Loss;
import FrameUtils.CategoricalEigenEncoder;
import Model.Parameters.CategoricalEncodingScheme;
import ScoreKeeper.StoppingMetric;
import hex.ModelMetricsBinomial;
import hex.deeplearning.DeepLearningModel.DeepLearningParameters;
import hex.genmodel.utils.DistributionFamily;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import water.exceptions.H2OIllegalArgumentException;
import water.exceptions.H2OModelBuilderIllegalArgumentException;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.fvec.Vec;

import static hex.TestUtil.<init>;


public class DeepLearningTest extends TestUtil {
    abstract static class PrepData {
        abstract int prep(Frame fr);
    }

    @Test
    public void testClassIris1() throws Throwable {
        // iris ntree=1
        basicDLTest_Classification("./smalldata/iris/iris.csv", "iris.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return (fr.numCols()) - 1;
            }
        }, 1, ard(ard(27, 16, 7), ard(0, 4, 46), ard(0, 3, 47)), DeepLearningTest.s("Iris-setosa", "Iris-versicolor", "Iris-virginica"), Rectifier);
    }

    @Test
    public void testClassIris5() throws Throwable {
        // iris ntree=50
        basicDLTest_Classification("./smalldata/iris/iris.csv", "iris5.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return (fr.numCols()) - 1;
            }
        }, 5, ard(ard(50, 0, 0), ard(0, 39, 11), ard(0, 8, 42)), DeepLearningTest.s("Iris-setosa", "Iris-versicolor", "Iris-virginica"), Rectifier);
    }

    @Test
    public void testClassCars1() throws Throwable {
        // cars ntree=1
        basicDLTest_Classification("./smalldata/junit/cars.csv", "cars.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("name").remove();
                return fr.find("cylinders");
            }
        }, 1, ard(ard(0, 4, 0, 0, 0), ard(0, 193, 5, 9, 0), ard(0, 2, 1, 0, 0), ard(0, 65, 3, 16, 0), ard(0, 11, 0, 7, 90)), DeepLearningTest.s("3", "4", "5", "6", "8"), Rectifier);
    }

    @Test
    public void testClassCars5() throws Throwable {
        basicDLTest_Classification("./smalldata/junit/cars.csv", "cars5.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("name").remove();
                return fr.find("cylinders");
            }
        }, 5, ard(ard(0, 4, 0, 0, 0), ard(0, 206, 0, 1, 0), ard(0, 2, 0, 1, 0), ard(0, 14, 0, 69, 1), ard(0, 0, 0, 6, 102)), DeepLearningTest.s("3", "4", "5", "6", "8"), Rectifier);
    }

    @Test
    public void testConstantCols() throws Throwable {
        try {
            basicDLTest_Classification("./smalldata/poker/poker100", "poker.hex", new DeepLearningTest.PrepData() {
                @Override
                int prep(Frame fr) {
                    for (int i = 0; i < 7; i++) {
                        Vec v = fr.remove(3);
                        if (v != null)
                            v.remove();

                    }
                    return 3;
                }
            }, 1, null, null, Rectifier);
            Assert.fail();
        } catch (H2OModelBuilderIllegalArgumentException iae) {
            /* pass */
        }
    }

    @Test
    public void testBadData() throws Throwable {
        basicDLTest_Classification("./smalldata/junit/drf_infinities.csv", "infinitys.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("DateofBirth");
            }
        }, 1, ard(ard(0, 17), ard(0, 17)), DeepLearningTest.s("0", "1"), Rectifier);
    }

    @Test
    public void testCreditProstate1() throws Throwable {
        basicDLTest_Classification("./smalldata/logreg/prostate.csv", "prostate.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
            }
        }, 1, ard(ard(97, 130), ard(28, 125)), DeepLearningTest.s("0", "1"), Rectifier);
    }

    @Test
    public void testCreditProstateReLUDropout() throws Throwable {
        basicDLTest_Classification("./smalldata/logreg/prostate.csv", "prostateReLUDropout.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
            }
        }, 1, ard(ard(4, 223), ard(0, 153)), DeepLearningTest.s("0", "1"), RectifierWithDropout);
    }

    @Test
    public void testCreditProstateTanh() throws Throwable {
        basicDLTest_Classification("./smalldata/logreg/prostate.csv", "prostateTanh.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
            }
        }, 1, ard(ard(141, 86), ard(25, 128)), DeepLearningTest.s("0", "1"), Tanh);
    }

    @Test
    public void testCreditProstateTanhDropout() throws Throwable {
        basicDLTest_Classification("./smalldata/logreg/prostate.csv", "prostateTanhDropout.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
            }
        }, 1, ard(ard(110, 117), ard(23, 130)), DeepLearningTest.s("0", "1"), TanhWithDropout);
    }

    @Test
    public void testCreditProstateMaxout() throws Throwable {
        basicDLTest_Classification("./smalldata/logreg/prostate.csv", "prostateMaxout.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
            }
        }, 100, ard(ard(189, 38), ard(30, 123)), DeepLearningTest.s("0", "1"), Maxout);
    }

    @Test
    public void testCreditProstateMaxoutDropout() throws Throwable {
        basicDLTest_Classification("./smalldata/logreg/prostate.csv", "prostateMaxoutDropout.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("CAPSULE");
            }
        }, 100, ard(ard(183, 44), ard(40, 113)), DeepLearningTest.s("0", "1"), MaxoutWithDropout);
    }

    @Test
    public void testCreditProstateRegression1() throws Throwable {
        basicDLTest_Regression("./smalldata/logreg/prostate.csv", "prostateRegression.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
            }
        }, 1, 46.26952683659, Rectifier);
    }

    @Test
    public void testCreditProstateRegressionTanh() throws Throwable {
        basicDLTest_Regression("./smalldata/logreg/prostate.csv", "prostateRegressionTanh.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
            }
        }, 1, 43.457087913127, Tanh);
    }

    @Test
    public void testCreditProstateRegressionMaxout() throws Throwable {
        basicDLTest_Regression("./smalldata/logreg/prostate.csv", "prostateRegressionMaxout.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
            }
        }, 100, 32.81408434266, Maxout);
    }

    @Test
    public void testCreditProstateRegression5() throws Throwable {
        basicDLTest_Regression("./smalldata/logreg/prostate.csv", "prostateRegression5.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
            }
        }, 5, 41.8498354737908, Rectifier);
    }

    @Test
    public void testCreditProstateRegression50() throws Throwable {
        basicDLTest_Regression("./smalldata/logreg/prostate.csv", "prostateRegression50.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                fr.remove("ID").remove();
                return fr.find("AGE");
            }
        }, 50, 37.93380250522667, Rectifier);
    }

    @Test
    public void testAlphabet() throws Throwable {
        basicDLTest_Classification("./smalldata/gbm_test/alphabet_cattest.csv", "alphabetClassification.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("y");
            }
        }, 10, ard(ard(2080, 0), ard(0, 2080)), DeepLearningTest.s("0", "1"), Rectifier);
    }

    @Test
    public void testAlphabetRegression() throws Throwable {
        basicDLTest_Regression("./smalldata/gbm_test/alphabet_cattest.csv", "alphabetRegression.hex", new DeepLearningTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return fr.find("y");
            }
        }, 10, 4.975570190016591E-6, Rectifier);
    }

    @Test
    public void elasticAveragingTrivial() {
        DeepLearningParameters dl;
        Frame frTrain;
        int N = 2;
        DeepLearningModel[] models = new DeepLearningModel[N];
        dl = new DeepLearningParameters();
        Scope.enter();
        try {
            for (int i = 0; i < N; ++i) {
                frTrain = parse_test_file("./smalldata/covtype/covtype.20k.data");
                Vec resp = frTrain.lastVec().toCategoricalVec();
                frTrain.remove(((frTrain.vecs().length) - 1)).remove();
                frTrain.add("Response", resp);
                DKV.put(frTrain);
                dl._train = frTrain._key;
                dl._response_column = lastVecName();
                dl._export_weights_and_biases = true;
                dl._hidden = new int[]{ 17, 11 };
                dl._quiet_mode = false;
                // make it reproducible
                dl._seed = 1234;
                dl._reproducible = true;
                // only do one M/R iteration, and there's no elastic average yet - so the two paths below should be identical
                dl._epochs = 1;
                dl._train_samples_per_iteration = -1;
                if (i == 0) {
                    // no elastic averaging
                    dl._elastic_averaging = false;
                    dl._elastic_averaging_moving_rate = 0.5;// ignored

                    dl._elastic_averaging_regularization = 0.9;// ignored

                } else {
                    // no-op elastic averaging
                    dl._elastic_averaging = true;// go different path, but don't really do anything because of epochs=1 and train_samples_per_iteration=-1

                    dl._elastic_averaging_moving_rate = 0.5;// doesn't matter, it's not used since we only do one M/R iteration and there's no time average

                    dl._elastic_averaging_regularization = 0.1;// doesn't matter, since elastic average isn't yet available in first iteration

                }
                // Invoke DL and block till the end
                DeepLearning job = new DeepLearning(dl);
                // Get the model
                models[i] = job.trainModel().get();
                frTrain.remove();
            }
            for (int i = 0; i < N; ++i) {
                Log.info(models[i]._output._training_metrics.cm().table().toString());
                Assert.assertEquals(models[i]._output._training_metrics._MSE, models[0]._output._training_metrics._MSE, 1.0E-6);
            }
        } finally {
            for (int i = 0; i < N; ++i)
                if ((models[i]) != null)
                    models[i].delete();


            Scope.exit();
        }
    }

    @Test
    public void testNoRowWeights() {
        Frame tfr = null;
        Frame vfr = null;
        Frame pred = null;
        Frame fr2 = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/no_weights.csv");
            DKV.put(tfr);
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._reproducible = true;
            parms._seed = 912559;
            parms._l1 = 0.1;
            parms._epochs = 1;
            parms._hidden = new int[]{ 1 };
            parms._classification_stop = -1;
            // Build a first model; all remaining models should be equal
            DeepLearningModel dl = trainModel().get();
            pred = dl.score(parms.train());
            hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
            Assert.assertEquals(0.7592592592592592, mm.auc_obj()._auc, 1.0E-8);
            double mse = dl._output._training_metrics.mse();
            Assert.assertEquals(0.314813341867078, mse, 1.0E-8);
            Assert.assertTrue(dl.testJavaScoring(tfr, (fr2 = dl.score(tfr)), 1.0E-5));
            dl.delete();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (pred != null)
                pred.remove();

            if (fr2 != null)
                fr2.remove();

        }
        Scope.exit();
    }

    @Test
    public void testRowWeightsOne() {
        Frame tfr = null;
        Frame vfr = null;
        Frame pred = null;
        Frame fr2 = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights_all_ones.csv");
            DKV.put(tfr);
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._weights_column = "weight";
            parms._reproducible = true;
            parms._seed = 912559;
            parms._classification_stop = -1;
            parms._l1 = 0.1;
            parms._hidden = new int[]{ 1 };
            parms._epochs = 1;
            // Build a first model; all remaining models should be equal
            DeepLearningModel dl = trainModel().get();
            pred = dl.score(parms.train());
            hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
            Assert.assertEquals(0.7592592592592592, mm.auc_obj()._auc, 1.0E-8);
            double mse = dl._output._training_metrics.mse();
            Assert.assertEquals(0.3148133418670781, mse, 1.0E-8);// Note: better results than non-shuffled

            // assertTrue(dl.testJavaScoring(tfr, fr2=dl.score(tfr, 1e-5)); //PUBDEV-1900
            dl.delete();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (pred != null)
                pred.remove();

            if (fr2 != null)
                fr2.remove();

        }
        Scope.exit();
    }

    @Test
    public void testNoRowWeightsShuffled() {
        Frame tfr = null;
        Frame vfr = null;
        Frame pred = null;
        Frame fr2 = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/no_weights_shuffled.csv");
            DKV.put(tfr);
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._reproducible = true;
            parms._seed = 912559;
            parms._l1 = 0.1;
            parms._epochs = 1;
            parms._hidden = new int[]{ 1 };
            parms._classification_stop = -1;
            // Build a first model; all remaining models should be equal
            DeepLearningModel dl = trainModel().get();
            pred = dl.score(parms.train());
            hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
            Assert.assertEquals(0.7222222222222222, mm.auc_obj()._auc, 1.0E-8);
            double mse = dl._output._training_metrics.mse();
            Assert.assertEquals(0.31643071339946, mse, 1.0E-8);
            Assert.assertTrue(dl.testJavaScoring(tfr, (fr2 = dl.score(tfr)), 1.0E-5));
            dl.delete();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (pred != null)
                pred.remove();

            if (fr2 != null)
                fr2.remove();

        }
        Scope.exit();
    }

    @Test
    public void testRowWeights() {
        Frame tfr = null;
        Frame pred = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/junit/weights.csv");
            DKV.put(tfr);
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._weights_column = "weight";
            parms._reproducible = true;
            parms._seed = 912559;
            parms._classification_stop = -1;
            parms._l1 = 0.1;
            parms._hidden = new int[]{ 1 };
            parms._epochs = 10;
            // Build a first model; all remaining models should be equal
            DeepLearningModel dl = trainModel().get();
            pred = dl.score(parms.train());
            hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
            Assert.assertEquals(0.7592592592592592, mm.auc_obj()._auc, 1.0E-8);
            double mse = dl._output._training_metrics.mse();
            Assert.assertEquals(0.3116490253190556, mse, 1.0E-8);
            // Assert.assertTrue(dl.testJavaScoring(tfr,fr2=dl.score(tfr),1e-5)); //PUBDEV-1900
            dl.delete();
        } finally {
            if (tfr != null)
                tfr.remove();

            if (pred != null)
                pred.remove();

        }
        Scope.exit();
    }

    static class PrintEntries extends MRTask<DeepLearningTest.PrintEntries> {
        @Override
        public void map(Chunk[] cs) {
            StringBuilder sb = new StringBuilder();
            for (int r = 0; r < (cs[0].len()); ++r) {
                sb.append((("Row " + ((cs[0].start()) + r)) + ": "));
                for (int i = 0; i < (cs.length); ++i) {
                    // response
                    if (i == 0)
                        sb.append((("response: " + (_fr.vec(i).domain()[((int) (cs[i].at8(r)))])) + " "));

                    if ((cs[i].atd(r)) != 0) {
                        sb.append((((i + ":") + (cs[i].atd(r))) + " "));
                    }
                }
                sb.append("\n");
            }
            Log.info(sb);
        }
    }

    // just a simple sanity check - not a golden test
    @Test
    public void testLossFunctions() {
        Frame tfr = null;
        Frame vfr = null;
        Frame fr2 = null;
        DeepLearningModel dl = null;
        for (DeepLearningParameters.Loss loss : new DeepLearningParameters.Loss[]{ Loss.Automatic, Loss.Quadratic, Loss.Huber, Loss.Absolute, Loss.Quantile }) {
            Scope.enter();
            try {
                tfr = parse_test_file("smalldata/glm_test/cancar_logIn.csv");
                for (String s : new String[]{ "Merit", "Class" }) {
                    Scope.track(tfr.replace(tfr.find(s), tfr.vec(s).toCategoricalVec()));
                }
                DKV.put(tfr);
                DeepLearningParameters parms = new DeepLearningParameters();
                parms._train = tfr._key;
                parms._epochs = 1;
                parms._reproducible = true;
                parms._hidden = new int[]{ 50, 50 };
                parms._response_column = "Cost";
                parms._seed = 912559;
                parms._loss = loss;
                // Build a first model; all remaining models should be equal
                DeepLearning job = new DeepLearning(parms);
                dl = job.trainModel().get();
                ModelMetricsRegression mm = ((ModelMetricsRegression) (dl._output._training_metrics));
                if ((loss == (Loss.Automatic)) || (loss == (Loss.Quadratic)))
                    Assert.assertEquals(mm._mean_residual_deviance, mm._MSE, 1.0E-6);
                else
                    Assert.assertTrue(((mm._mean_residual_deviance) != (mm._MSE)));

                Assert.assertTrue(dl.testJavaScoring(tfr, (fr2 = dl.score(tfr)), 1.0E-5));
            } finally {
                if (tfr != null)
                    tfr.remove();

                if (dl != null)
                    dl.delete();

                if (fr2 != null)
                    fr2.remove();

                Scope.exit();
            }
        }
    }

    @Test
    public void testDistributions() {
        Frame tfr = null;
        Frame vfr = null;
        Frame fr2 = null;
        DeepLearningModel dl = null;
        for (DistributionFamily dist : new DistributionFamily[]{ AUTO, gaussian, poisson, gamma, tweedie }) {
            Scope.enter();
            try {
                tfr = parse_test_file("smalldata/glm_test/cancar_logIn.csv");
                for (String s : new String[]{ "Merit", "Class" }) {
                    Scope.track(tfr.replace(tfr.find(s), tfr.vec(s).toCategoricalVec()));
                }
                DKV.put(tfr);
                DeepLearningParameters parms = new DeepLearningParameters();
                parms._train = tfr._key;
                parms._epochs = 1;
                parms._reproducible = true;
                parms._hidden = new int[]{ 50, 50 };
                parms._response_column = "Cost";
                parms._seed = 912559;
                parms._distribution = dist;
                // Build a first model; all remaining models should be equal
                DeepLearning job = new DeepLearning(parms);
                dl = job.trainModel().get();
                ModelMetricsRegression mm = ((ModelMetricsRegression) (dl._output._training_metrics));
                if ((dist == (gaussian)) || (dist == (AUTO)))
                    Assert.assertEquals(mm._mean_residual_deviance, mm._MSE, 1.0E-6);
                else
                    Assert.assertTrue(((mm._mean_residual_deviance) != (mm._MSE)));

                Assert.assertTrue(dl.testJavaScoring(tfr, (fr2 = dl.score(tfr)), 1.0E-5));
            } finally {
                if (tfr != null)
                    tfr.remove();

                if (dl != null)
                    dl.delete();

                if (fr2 != null)
                    fr2.delete();

                Scope.exit();
            }
        }
    }

    @Test
    public void testAutoEncoder() {
        Frame tfr = null;
        Frame vfr = null;
        Frame fr2 = null;
        DeepLearningModel dl = null;
        Scope.enter();
        try {
            tfr = parse_test_file("smalldata/glm_test/cancar_logIn.csv");
            for (String s : new String[]{ "Merit", "Class" }) {
                Scope.track(tfr.replace(tfr.find(s), tfr.vec(s).toCategoricalVec()));
            }
            DKV.put(tfr);
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._epochs = 100;
            parms._reproducible = true;
            parms._hidden = new int[]{ 5, 5, 5 };
            parms._response_column = "Cost";
            parms._seed = 912559;
            parms._autoencoder = true;
            parms._input_dropout_ratio = 0.1;
            parms._activation = Activation.Tanh;
            // Build a first model; all remaining models should be equal
            dl = trainModel().get();
            ModelMetricsAutoEncoder mm = ((ModelMetricsAutoEncoder) (dl._output._training_metrics));
            Assert.assertEquals(0.0712931422088762, mm._MSE, 0.01);
            Assert.assertTrue(dl.testJavaScoring(tfr, (fr2 = dl.score(tfr)), 1.0E-5));
        } finally {
            if (tfr != null)
                tfr.remove();

            if (dl != null)
                dl.delete();

            if (fr2 != null)
                fr2.delete();

            Scope.exit();
        }
    }

    @Test
    public void testNumericalExplosion() {
        for (boolean ae : new boolean[]{ true, false }) {
            Frame tfr = null;
            DeepLearningModel dl = null;
            Frame pred = null;
            try {
                tfr = parse_test_file("./smalldata/junit/two_spiral.csv");
                for (String s : new String[]{ "Class" }) {
                    Vec resp = tfr.vec(s).toCategoricalVec();
                    tfr.remove(s).remove();
                    tfr.add(s, resp);
                    DKV.put(tfr);
                }
                DeepLearningParameters parms = new DeepLearningParameters();
                parms._train = tfr._key;
                parms._epochs = 100;
                parms._response_column = "Class";
                parms._autoencoder = ae;
                parms._reproducible = true;
                parms._train_samples_per_iteration = 10;
                parms._hidden = new int[]{ 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
                parms._initial_weight_distribution = InitialWeightDistribution.Uniform;
                parms._initial_weight_scale = 1.0E20;
                parms._seed = 912559;
                parms._max_w2 = 1.0E20F;
                // Build a first model; all remaining models should be equal
                DeepLearning job = new DeepLearning(parms);
                try {
                    dl = job.trainModel().get();
                    Assert.fail("Should toss exception instead of reaching here");
                } catch (RuntimeException de) {
                    // catch anything - might be a NPE during cleanup
                    // assertTrue(de.getMessage().contains("Trying to predict with an unstable model."));
                }
                dl = DKV.getGet(job.dest());
                try {
                    pred = dl.score(tfr);
                    Assert.fail("Should toss exception instead of reaching here");
                } catch (RuntimeException ex) {
                    // OK
                }
                Assert.assertTrue(dl.model_info().isUnstable());
                Assert.assertTrue(dl._output._job.isCrashed());
            } finally {
                if (tfr != null)
                    tfr.delete();

                if (dl != null)
                    dl.delete();

                if (pred != null)
                    pred.delete();

            }
        }
    }

    @Test
    public void testEarlyStopping() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            tfr = parse_test_file("./smalldata/junit/two_spiral.csv");
            for (String s : new String[]{ "Class" }) {
                Vec resp = tfr.vec(s).toCategoricalVec();
                tfr.remove(s).remove();
                tfr.add(s, resp);
                DKV.put(tfr);
            }
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._epochs = 100;
            parms._response_column = "Class";
            parms._reproducible = true;
            parms._classification_stop = 0.7;
            parms._score_duty_cycle = 1;
            parms._score_interval = 0;
            parms._hidden = new int[]{ 100, 100 };
            parms._seed = 912559;
            // Build a first model; all remaining models should be equal
            dl = trainModel().get();
            Assert.assertTrue(dl.stopped_early);
            Assert.assertTrue(((dl.epoch_counter) < 100));
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testVarimp() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            tfr = parse_test_file("./smalldata/iris/iris.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._epochs = 100;
            parms._response_column = "C5";
            parms._reproducible = true;
            parms._classification_stop = 0.7;
            parms._score_duty_cycle = 1;
            parms._score_interval = 0;
            parms._hidden = new int[]{ 100, 100 };
            parms._seed = 912559;
            parms._variable_importances = true;
            // Build a first model; all remaining models should be equal
            dl = trainModel().get();
            Assert.assertTrue(((dl.varImp()._varimp) != null));
            Log.info(dl.model_info().toStringAll());// for code coverage only

            Assert.assertTrue(((ArrayUtils.minValue(dl.varImp()._varimp)) > 0.5));// all features matter

            Assert.assertTrue(((ArrayUtils.maxValue(dl.varImp()._varimp)) <= 1));// all features matter

        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testCheckpointSameEpochs() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        DeepLearningModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/iris/iris.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._epochs = 10;
            parms._response_column = "C5";
            parms._reproducible = true;
            parms._hidden = new int[]{ 2, 2 };
            parms._seed = 912559;
            parms._variable_importances = true;
            dl = trainModel().get();
            DeepLearningParameters parms2 = ((DeepLearningParameters) (parms.clone()));
            parms2._epochs = 10;
            parms2._checkpoint = dl._key;
            try {
                dl2 = trainModel().get();
                Assert.fail("Should toss exception instead of reaching here");
            } catch (H2OIllegalArgumentException ex) {
            }
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

            if (dl2 != null)
                dl2.delete();

        }
    }

    @Test
    public void testCheckpointBackwards() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        DeepLearningModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/iris/iris.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._epochs = 10;
            parms._response_column = "C5";
            parms._reproducible = true;
            parms._hidden = new int[]{ 2, 2 };
            parms._seed = 912559;
            parms._variable_importances = true;
            dl = trainModel().get();
            DeepLearningParameters parms2 = ((DeepLearningParameters) (parms.clone()));
            parms2._epochs = 9;
            parms2._checkpoint = dl._key;
            try {
                dl2 = trainModel().get();
                Assert.fail("Should toss exception instead of reaching here");
            } catch (H2OIllegalArgumentException ex) {
            }
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

            if (dl2 != null)
                dl2.delete();

        }
    }

    @Test
    public void testConvergenceLogloss() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        DeepLearningModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/iris/iris.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._epochs = 1000000;
            parms._response_column = "C5";
            parms._reproducible = true;
            parms._hidden = new int[]{ 2, 2 };
            parms._seed = 912559;
            parms._variable_importances = true;
            parms._score_duty_cycle = 0.1;
            parms._score_interval = 0;
            parms._classification_stop = -1;// don't stop based on absolute classification error

            parms._stopping_rounds = 5;// don't stop based on absolute classification error

            parms._stopping_metric = StoppingMetric.logloss;// don't stop based on absolute classification error

            parms._stopping_tolerance = 0.03;
            dl = trainModel().get();
            Assert.assertTrue(((dl.epoch_counter) < (parms._epochs)));
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

            if (dl2 != null)
                dl2.delete();

        }
    }

    @Test
    public void testConvergenceMisclassification() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        DeepLearningModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/iris/iris.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._epochs = 1000000;
            parms._response_column = "C5";
            parms._reproducible = true;
            parms._hidden = new int[]{ 2, 2 };
            parms._seed = 912559;
            parms._variable_importances = true;
            parms._score_duty_cycle = 1.0;
            parms._score_interval = 0;
            parms._classification_stop = -1;// don't stop based on absolute classification error

            parms._stopping_rounds = 2;// don't stop based on absolute classification error

            parms._stopping_metric = StoppingMetric.misclassification;// don't stop based on absolute classification error

            parms._stopping_tolerance = 0.0;
            dl = trainModel().get();
            Assert.assertTrue(((dl.epoch_counter) < (parms._epochs)));
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

            if (dl2 != null)
                dl2.delete();

        }
    }

    @Test
    public void testConvergenceDeviance() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        DeepLearningModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/logreg/prostate.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._epochs = 1000000;
            parms._response_column = "AGE";
            parms._reproducible = true;
            parms._hidden = new int[]{ 2, 2 };
            parms._seed = 912559;
            parms._variable_importances = true;
            parms._score_duty_cycle = 1.0;
            parms._score_interval = 0;
            parms._classification_stop = -1;// don't stop based on absolute classification error

            parms._stopping_rounds = 2;// don't stop based on absolute classification error

            parms._stopping_metric = StoppingMetric.deviance;// don't stop based on absolute classification error

            parms._stopping_tolerance = 0.0;
            dl = trainModel().get();
            Assert.assertTrue(((dl.epoch_counter) < (parms._epochs)));
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

            if (dl2 != null)
                dl2.delete();

        }
    }

    @Test
    public void testConvergenceAUC() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        DeepLearningModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/logreg/prostate.csv");
            for (String s : new String[]{ "CAPSULE" }) {
                Vec resp = tfr.vec(s).toCategoricalVec();
                tfr.remove(s).remove();
                tfr.add(s, resp);
                DKV.put(tfr);
            }
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._epochs = 1000000;
            parms._response_column = "CAPSULE";
            parms._reproducible = true;
            parms._hidden = new int[]{ 2, 2 };
            parms._seed = 912559;
            parms._variable_importances = true;
            parms._score_duty_cycle = 1.0;
            parms._score_interval = 0;
            parms._classification_stop = -1;// don't stop based on absolute classification error

            parms._stopping_rounds = 2;// don't stop based on absolute classification error

            parms._stopping_metric = StoppingMetric.AUC;// don't stop based on absolute classification error

            parms._stopping_tolerance = 0.0;
            dl = trainModel().get();
            Assert.assertTrue(((dl.epoch_counter) < (parms._epochs)));
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

            if (dl2 != null)
                dl2.delete();

        }
    }

    @Test
    public void testNoHiddenLayerRegression() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        DeepLearningModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/logreg/prostate.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._epochs = 1000;
            parms._response_column = "AGE";
            parms._hidden = new int[]{  };
            dl = trainModel().get();
            Frame res = dl.score(tfr);
            Assert.assertTrue(dl.testJavaScoring(tfr, res, 1.0E-5));
            res.remove();
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

            if (dl2 != null)
                dl2.delete();

        }
    }

    @Test
    public void testNoHiddenLayerClassification() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        DeepLearningModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/logreg/prostate.csv");
            for (String s : new String[]{ "CAPSULE" }) {
                Vec resp = tfr.vec(s).toCategoricalVec();
                tfr.remove(s).remove();
                tfr.add(s, resp);
                DKV.put(tfr);
            }
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._epochs = 1000;
            parms._response_column = "CAPSULE";
            parms._hidden = null;// that works too, not just empty array

            dl = trainModel().get();
            Frame res = dl.score(tfr);
            Assert.assertTrue(dl.testJavaScoring(tfr, res, 1.0E-5));
            res.remove();
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

            if (dl2 != null)
                dl2.delete();

        }
    }

    @Test
    public void testCrossValidation() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._nfolds = 4;
            dl = trainModel().get();
            Assert.assertEquals(12.959355363801334, dl._output._training_metrics._MSE, 1.0E-6);
            Assert.assertEquals(17.296871012606317, dl._output._cross_validation_metrics._MSE, 1.0E-6);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testMiniBatch1() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._mini_batch_size = 1;
            dl = trainModel().get();
            Assert.assertEquals(12.938076268040659, dl._output._training_metrics._MSE, 1.0E-6);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testMiniBatch50() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._mini_batch_size = 50;
            dl = trainModel().get();
            Assert.assertEquals(12.938076268040659, dl._output._training_metrics._MSE, 1.0E-6);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testPretrainedAE() {
        Frame tfr = null;
        DeepLearningModel dl1 = null;
        DeepLearningModel dl2 = null;
        DeepLearningModel ae = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            Vec r = tfr.remove("chas");
            tfr.add("chas", r.toCategoricalVec());
            DKV.put(tfr);
            r.remove();
            // train unsupervised AE
            Key<DeepLearningModel> key = Key.make("ae_model");
            {
                DeepLearningParameters parms = new DeepLearningParameters();
                parms._train = tfr._key;
                parms._ignored_columns = new String[]{ "chas" };
                parms._activation = Activation.TanhWithDropout;
                parms._reproducible = true;
                parms._hidden = new int[]{ 20, 20 };
                parms._input_dropout_ratio = 0.1;
                parms._hidden_dropout_ratios = new double[]{ 0.2, 0.1 };
                parms._autoencoder = true;
                parms._seed = 912559;
                ae = trainModel().get();
                // test POJO
                Frame res = ae.score(tfr);
                Assert.assertTrue(ae.testJavaScoring(tfr, res, 1.0E-5));
                res.remove();
            }
            // train supervised DL model
            {
                DeepLearningParameters parms = new DeepLearningParameters();
                parms._train = tfr._key;
                parms._response_column = "chas";
                parms._activation = Activation.TanhWithDropout;
                parms._reproducible = true;
                parms._hidden = new int[]{ 20, 20 };
                parms._input_dropout_ratio = 0.1;
                parms._hidden_dropout_ratios = new double[]{ 0.2, 0.1 };
                parms._seed = 912557;
                parms._pretrained_autoencoder = key;
                parms._rate_decay = 1.0;
                parms._adaptive_rate = false;
                parms._rate_annealing = 0.001;
                parms._loss = Loss.CrossEntropy;
                dl1 = trainModel().get();
                // test POJO
                Frame res = dl1.score(tfr);
                Assert.assertTrue(dl1.testJavaScoring(tfr, res, 1.0E-5));
                res.remove();
            }
            // train DL model from scratch
            {
                DeepLearningParameters parms = new DeepLearningParameters();
                parms._train = tfr._key;
                parms._response_column = "chas";
                parms._activation = Activation.TanhWithDropout;
                parms._reproducible = true;
                parms._hidden = new int[]{ 20, 20 };
                parms._input_dropout_ratio = 0.1;
                parms._hidden_dropout_ratios = new double[]{ 0.2, 0.1 };
                parms._seed = 912557;
                parms._rate_decay = 1.0;
                parms._adaptive_rate = false;
                parms._rate_annealing = 0.001;
                dl2 = trainModel().get();
                // test POJO
                Frame res = dl2.score(tfr);
                Assert.assertTrue(dl2.testJavaScoring(tfr, res, 1.0E-5));
                res.remove();
            }
            Log.info(("pretrained  : MSE=" + (dl1._output._training_metrics.mse())));
            Log.info(("from scratch: MSE=" + (dl2._output._training_metrics.mse())));
            // Assert.assertTrue(dl1._output._training_metrics.mse() < dl2._output._training_metrics.mse());
        } finally {
            if (tfr != null)
                tfr.delete();

            if (ae != null)
                ae.delete();

            if (dl1 != null)
                dl1.delete();

            if (dl2 != null)
                dl2.delete();

        }
    }

    @Test
    public void testInitialWeightsAndBiases() {
        Frame tfr = null;
        DeepLearningModel dl1 = null;
        DeepLearningModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            // train DL model from scratch
            {
                DeepLearningParameters parms = new DeepLearningParameters();
                parms._train = tfr._key;
                parms._response_column = tfr.lastVecName();
                parms._activation = Activation.Tanh;
                parms._reproducible = true;
                parms._hidden = new int[]{ 20, 20 };
                parms._seed = 912557;
                parms._export_weights_and_biases = true;
                dl1 = trainModel().get();
            }
            // train DL model starting from weights/biases from first model
            {
                DeepLearningParameters parms = new DeepLearningParameters();
                parms._train = tfr._key;
                parms._response_column = tfr.lastVecName();
                parms._activation = Activation.Tanh;
                parms._reproducible = true;
                parms._hidden = new int[]{ 20, 20 };
                parms._seed = 912557;
                parms._initial_weights = dl1._output.weights;
                parms._initial_biases = dl1._output.biases;
                parms._epochs = 0;
                dl2 = trainModel().get();
            }
            Log.info(("dl1  : MSE=" + (dl1._output._training_metrics.mse())));
            Log.info(("dl2  : MSE=" + (dl2._output._training_metrics.mse())));
            Assert.assertTrue(((Math.abs(((dl1._output._training_metrics.mse()) - (dl2._output._training_metrics.mse())))) < 1.0E-6));
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl1 != null)
                dl1.delete();

            if (dl2 != null)
                dl2.delete();

            for (Key f : dl1._output.weights)
                f.remove();

            for (Key f : dl1._output.biases)
                f.remove();

        }
    }

    @Test
    public void testInitialWeightsAndBiasesPartial() {
        Frame tfr = null;
        DeepLearningModel dl1 = null;
        DeepLearningModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            // train DL model from scratch
            {
                DeepLearningParameters parms = new DeepLearningParameters();
                parms._train = tfr._key;
                parms._response_column = tfr.lastVecName();
                parms._activation = Activation.Tanh;
                parms._reproducible = true;
                parms._hidden = new int[]{ 20, 20 };
                parms._seed = 912557;
                parms._export_weights_and_biases = true;
                dl1 = trainModel().get();
            }
            // train DL model starting from weights/biases from first model
            {
                DeepLearningParameters parms = new DeepLearningParameters();
                parms._train = tfr._key;
                parms._response_column = tfr.lastVecName();
                parms._activation = Activation.Tanh;
                parms._reproducible = true;
                parms._hidden = new int[]{ 20, 20 };
                parms._seed = 912557;
                parms._initial_weights = dl1._output.weights;
                parms._initial_biases = dl1._output.biases;
                parms._initial_weights[1].remove();
                parms._initial_weights[1] = null;
                parms._initial_biases[0].remove();
                parms._initial_biases[0] = null;
                parms._epochs = 10;
                dl2 = trainModel().get();
            }
            Log.info(("dl1  : MSE=" + (dl1._output._training_metrics.mse())));
            Log.info(("dl2  : MSE=" + (dl2._output._training_metrics.mse())));
            // the second model is better since it got warm-started at least partially
            Assert.assertTrue(((dl1._output._training_metrics.mse()) > (dl2._output._training_metrics.mse())));
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl1 != null)
                dl1.delete();

            if (dl2 != null)
                dl2.delete();

            for (Key f : dl1._output.weights)
                if (f != null)
                    f.remove();


            for (Key f : dl1._output.biases)
                if (f != null)
                    f.remove();


        }
    }

    @Test
    public void testLaplace() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._distribution = laplace;
            dl = trainModel().get();
            /* MAE */
            Assert.assertEquals(2.31398, ((ModelMetricsRegression) (dl._output._training_metrics))._mean_residual_deviance, 1.0E-5);
            Assert.assertEquals(14.889, ((ModelMetricsRegression) (dl._output._training_metrics))._MSE, 0.001);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testGaussian() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._distribution = gaussian;
            dl = trainModel().get();
            /* MSE */
            Assert.assertEquals(12.93808, ((ModelMetricsRegression) (dl._output._training_metrics))._mean_residual_deviance, 1.0E-5);
            /* MSE */
            Assert.assertEquals(12.93808, ((ModelMetricsRegression) (dl._output._training_metrics))._MSE, 1.0E-5);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testHuberDeltaLarge() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._distribution = huber;
            parms._huber_alpha = 1;// just like gaussian

            dl = trainModel().get();
            /* MSE */
            Assert.assertEquals(12.93808, ((ModelMetricsRegression) (dl._output._training_metrics))._mean_residual_deviance, 0.7);
            /* MSE */
            Assert.assertEquals(12.93808, ((ModelMetricsRegression) (dl._output._training_metrics))._MSE, 0.7);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testHuberDeltaTiny() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._distribution = huber;
            parms._huber_alpha = 0.01;
            // more like Laplace, but different slope and different prefactor -> so can't compare deviance 1:1
            dl = trainModel().get();
            double delta = 0.011996;
            // can compute huber loss from MAE since no obs weights
            Assert.assertEquals((((2 * 2.31398)/* MAE */
             - delta) * delta), ((ModelMetricsRegression) (dl._output._training_metrics))._mean_residual_deviance, 0.02);
            Assert.assertEquals(19.856, ((ModelMetricsRegression) (dl._output._training_metrics))._MSE, 0.001);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testHuber() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            tfr = parse_test_file("./smalldata/gbm_test/BostonHousing.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._response_column = tfr.lastVecName();
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._distribution = huber;
            dl = trainModel().get();
            Assert.assertEquals(6.4964976811, ((ModelMetricsRegression) (dl._output._training_metrics))._mean_residual_deviance, 1.0E-5);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testCategoricalEncodingAUTO() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            tfr = parse_test_file("./smalldata/junit/titanic_alt.csv");
            Vec v = tfr.remove("survived");
            tfr.add("survived", v.toCategoricalVec());
            v.remove();
            DKV.put(tfr);
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._valid = tfr._key;
            parms._response_column = "survived";
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._nfolds = 3;
            parms._distribution = bernoulli;
            parms._categorical_encoding = CategoricalEncodingScheme.AUTO;
            dl = trainModel().get();
            Assert.assertEquals(0.97329, ((ModelMetricsBinomial) (dl._output._training_metrics))._auc._auc, 0.001);
            Assert.assertEquals(0.97329, ((ModelMetricsBinomial) (dl._output._validation_metrics))._auc._auc, 0.001);
            Assert.assertEquals(0.93152, ((ModelMetricsBinomial) (dl._output._cross_validation_metrics))._auc._auc, 0.001);
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testCategoricalEncodingBinary() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            String response = "survived";
            tfr = parse_test_file("./smalldata/junit/titanic_alt.csv");
            if (tfr.vec(response).isBinary()) {
                Vec v = tfr.remove(response);
                tfr.add(response, v.toCategoricalVec());
                v.remove();
            }
            DKV.put(tfr);
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._valid = tfr._key;
            parms._response_column = response;
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._nfolds = 3;
            parms._categorical_encoding = CategoricalEncodingScheme.Binary;
            dl = trainModel().get();
            Assert.assertEquals(0.94696, ((ModelMetricsBinomial) (dl._output._training_metrics))._auc._auc, 1.0E-4);
            Assert.assertEquals(0.94696, ((ModelMetricsBinomial) (dl._output._validation_metrics))._auc._auc, 1.0E-4);
            Assert.assertEquals(0.86556613, ((ModelMetricsBinomial) (dl._output._cross_validation_metrics))._auc._auc, 1.0E-4);
            int auc_row = Arrays.binarySearch(dl._output._cross_validation_metrics_summary.getRowHeaders(), "auc");
            Assert.assertEquals(0.86556613, Double.parseDouble(((String) (dl._output._cross_validation_metrics_summary.get(auc_row, 0)))), 0.01);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testCategoricalEncodingEigen() {
        Frame tfr = null;
        Frame vfr = null;
        DeepLearningModel dl = null;
        try {
            String response = "survived";
            tfr = parse_test_file("./smalldata/junit/titanic_alt.csv");
            vfr = parse_test_file("./smalldata/junit/titanic_alt.csv");
            if (tfr.vec(response).isBinary()) {
                Vec v = tfr.remove(response);
                tfr.add(response, v.toCategoricalVec());
                v.remove();
            }
            if (vfr.vec(response).isBinary()) {
                Vec v = vfr.remove(response);
                vfr.add(response, v.toCategoricalVec());
                v.remove();
            }
            DKV.put(tfr);
            DKV.put(vfr);
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._valid = vfr._key;
            parms._response_column = response;
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._categorical_encoding = CategoricalEncodingScheme.Eigen;
            parms._score_training_samples = 0;
            dl = trainModel().get();
            Assert.assertEquals(((ModelMetricsBinomial) (dl._output._training_metrics))._logloss, ((ModelMetricsBinomial) (dl._output._validation_metrics))._logloss, 1.0E-8);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (vfr != null)
                vfr.remove();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testCategoricalEncodingEigenCV() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            String response = "survived";
            tfr = parse_test_file("./smalldata/junit/titanic_alt.csv");
            if (tfr.vec(response).isBinary()) {
                Vec v = tfr.remove(response);
                tfr.add(response, v.toCategoricalVec());
                v.remove();
            }
            DKV.put(tfr);
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._valid = tfr._key;
            parms._response_column = response;
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._nfolds = 3;
            parms._categorical_encoding = CategoricalEncodingScheme.Eigen;
            parms._score_training_samples = 0;
            dl = trainModel().get();
            Assert.assertEquals(0.9521718170580964, ((ModelMetricsBinomial) (dl._output._training_metrics))._auc._auc, 1.0E-4);
            Assert.assertEquals(0.9521656365883807, ((ModelMetricsBinomial) (dl._output._validation_metrics))._auc._auc, 1.0E-4);
            Assert.assertEquals(0.9115080346106303, ((ModelMetricsBinomial) (dl._output._cross_validation_metrics))._auc._auc, 1.0E-4);
            int auc_row = Arrays.binarySearch(dl._output._cross_validation_metrics_summary.getRowHeaders(), "auc");
            Assert.assertEquals(0.913637, Double.parseDouble(((String) (dl._output._cross_validation_metrics_summary.get(auc_row, 0)))), 1.0E-4);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testCategoricalEncodingRegressionHuber() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        try {
            String response = "age";
            tfr = parse_test_file("./smalldata/junit/titanic_alt.csv");
            if (tfr.vec(response).isBinary()) {
                Vec v = tfr.remove(response);
                tfr.add(response, v.toCategoricalVec());
                v.remove();
            }
            DKV.put(tfr);
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = tfr._key;
            parms._valid = tfr._key;
            parms._response_column = response;
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            parms._nfolds = 3;
            parms._distribution = huber;
            parms._categorical_encoding = CategoricalEncodingScheme.Binary;
            dl = trainModel().get();
            Assert.assertEquals(87.26206135855, ((ModelMetricsRegression) (dl._output._training_metrics))._mean_residual_deviance, 1.0E-4);
            Assert.assertEquals(87.26206135855, ((ModelMetricsRegression) (dl._output._validation_metrics))._mean_residual_deviance, 1.0E-4);
            Assert.assertEquals(117.8014, ((ModelMetricsRegression) (dl._output._cross_validation_metrics))._mean_residual_deviance, 1.0E-4);
            int mean_residual_deviance_row = Arrays.binarySearch(dl._output._cross_validation_metrics_summary.getRowHeaders(), "mean_residual_deviance");
            Assert.assertEquals(117.8014, Double.parseDouble(((String) (dl._output._cross_validation_metrics_summary.get(mean_residual_deviance_row, 0)))), 1);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (dl != null)
                dl.deleteCrossValidationModels();

            if (dl != null)
                dl.delete();

        }
    }

    @Test
    public void testMultinomial() {
        Frame train = null;
        Frame preds = null;
        DeepLearningModel model = null;
        Scope.enter();
        try {
            train = parse_test_file("./smalldata/junit/titanic_alt.csv");
            Vec v = train.remove("pclass");
            train.add("pclass", v.toCategoricalVec());
            v.remove();
            DKV.put(train);
            DeepLearningParameters p = new DeepLearningParameters();
            p._train = train._key;
            p._response_column = "pclass";// last column is the response

            p._activation = Activation.RectifierWithDropout;
            p._hidden = new int[]{ 50, 50 };
            p._epochs = 1;
            p._adaptive_rate = false;
            p._rate = 0.005;
            p._sparse = true;
            model = trainModel().get();
            preds = model.score(train);
            preds.remove(0);// remove label, keep only probs

            Vec labels = train.vec("pclass");// actual

            String[] fullDomain = train.vec("pclass").domain();// actual

            ModelMetricsMultinomial mm = ModelMetricsMultinomial.make(preds, labels, fullDomain);
            Log.info(mm.toString());
        } finally {
            if (model != null)
                model.delete();

            if (preds != null)
                preds.remove();

            if (train != null)
                train.remove();

            Scope.exit();
        }
    }

    @Test
    public void testBinomial() {
        Frame train = null;
        Frame preds = null;
        Frame small = null;
        Frame large = null;
        DeepLearningModel model = null;
        Scope.enter();
        try {
            train = parse_test_file("./smalldata/junit/titanic_alt.csv");
            Vec v = train.remove("survived");
            train.add("survived", v.toCategoricalVec());
            v.remove();
            DKV.put(train);
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = train._key;
            parms._response_column = "survived";
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._seed = 912559;
            model = trainModel().get();
            FrameSplitter fs = new FrameSplitter(train, new double[]{ 0.002 }, new Key[]{ Key.make("small"), Key.make("large") }, null);
            fs.compute2();
            small = fs.getResult()[0];
            large = fs.getResult()[1];
            preds = model.score(small);
            Vec labels = small.vec("survived");// actual

            String[] fullDomain = train.vec("survived").domain();// actual

            ModelMetricsBinomial mm = ModelMetricsBinomial.make(preds.vec(2), labels, fullDomain);
            Log.info(mm.toString());
            mm = ModelMetricsBinomial.make(preds.vec(2), labels, new String[]{ "0", "1" });
            Log.info(mm.toString());
            mm = ModelMetricsBinomial.make(preds.vec(2), labels);
            Log.info(mm.toString());
            try {
                mm = ModelMetricsBinomial.make(preds.vec(2), labels, new String[]{ "a", "b" });
                Log.info(mm.toString());
                Assert.assertFalse(true);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (model != null)
                model.delete();

            if (preds != null)
                preds.remove();

            if (train != null)
                train.remove();

            if (small != null)
                small.delete();

            if (large != null)
                large.delete();

            Scope.exit();
        }
    }

    @Test
    public void testRegression() {
        Frame train = null;
        Frame preds = null;
        DeepLearningModel model = null;
        Scope.enter();
        try {
            train = parse_test_file("./smalldata/junit/titanic_alt.csv");
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = train._key;
            parms._response_column = "age";
            parms._reproducible = true;
            parms._hidden = new int[]{ 20, 20 };
            parms._distribution = laplace;
            parms._seed = 912559;
            model = trainModel().get();
            preds = model.score(train);
            Vec targets = train.vec("age");// actual

            ModelMetricsRegression mm = ModelMetricsRegression.make(preds.vec(0), targets, parms._distribution);
            Log.info(mm.toString());
            mm = ModelMetricsRegression.make(preds.vec(0), targets, gaussian);
            Log.info(mm.toString());
            mm = ModelMetricsRegression.make(preds.vec(0), targets, poisson);
            Log.info(mm.toString());
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (model != null)
                model.delete();

            if (preds != null)
                preds.remove();

            if (train != null)
                train.remove();

            Scope.exit();
        }
    }

    // NOTE: This test has nothing to do with Deep Learning, except that it uses the Deep Learning infrastructure to get access to the EigenVec computation logic
    @Test
    public void testEigenEncodingLogic() {
        int numNoncatColumns = 1;
        int[] catSizes = new int[]{ 16 };
        String[] catNames = new String[]{ "sixteen" };
        Assert.assertEquals(catSizes.length, catNames.length);
        int totalExpectedColumns = numNoncatColumns + (catSizes.length);
        double[] expectedMean = new double[]{ 0.0453 };// to test reproducibility

        Key<Frame> frameKey = Key.make();
        CreateFrame cf = new CreateFrame(frameKey);
        cf.rows = 100000;
        cf.cols = numNoncatColumns;
        cf.categorical_fraction = 0.0;
        cf.seed = 1234;
        cf.integer_fraction = 0.3;
        cf.binary_fraction = 0.1;
        cf.time_fraction = 0.2;
        cf.string_fraction = 0.1;
        Frame mainFrame = cf.execImpl().get();
        assert mainFrame != null : "Unable to create a frame";
        Frame[] auxFrames = new Frame[catSizes.length];
        Frame transformedFrame = null;
        try {
            for (int i = 0; i < (catSizes.length); ++i) {
                CreateFrame ccf = new CreateFrame();
                ccf.rows = 100000;
                ccf.cols = 1;
                ccf.categorical_fraction = 1;
                ccf.integer_fraction = 0;
                ccf.binary_fraction = 0;
                ccf.seed = 1234;
                ccf.time_fraction = 0;
                ccf.string_fraction = 0;
                ccf.factors = catSizes[i];
                auxFrames[i] = ccf.execImpl().get();
                auxFrames[i]._names[0] = catNames[i];
                mainFrame.add(auxFrames[i]);
            }
            Log.info(mainFrame, 0, 100);
            FrameUtils.CategoricalEigenEncoder cbed = new FrameUtils.CategoricalEigenEncoder(getToEigenVec(), mainFrame, null);
            transformedFrame = cbed.exec().get();
            assert transformedFrame != null : "Unable to transform a frame";
            Assert.assertEquals("Wrong number of columns after converting to eigen encoding", totalExpectedColumns, transformedFrame.numCols());
            for (int i = 0; i < numNoncatColumns; ++i) {
                Assert.assertEquals(mainFrame.name(i), transformedFrame.name(i));
                Assert.assertEquals(mainFrame.types()[i], transformedFrame.types()[i]);
            }
            for (int i = numNoncatColumns; i < (transformedFrame.numCols()); i++) {
                Assert.assertTrue((("A categorical column should be transformed into one numeric one (col " + i) + ")"), transformedFrame.vec(i).isNumeric());
                Assert.assertEquals("Transformed categorical column should carry the name of the original column", transformedFrame.name(i), ((mainFrame.name(i)) + ".Eigen"));
                Assert.assertEquals("Transformed categorical column should have the correct mean value", expectedMean[(i - numNoncatColumns)], transformedFrame.vec(i).mean(), 5.0E-4);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        } finally {
            mainFrame.delete();
            if (transformedFrame != null)
                transformedFrame.delete();

            for (Frame f : auxFrames)
                if (f != null)
                    f.delete();


        }
    }

    // Check that the restarted model honors overwrite_with_best_model
    @Test
    public void testCheckpointOverwriteWithBestModel() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        DeepLearningModel dl2 = null;
        Frame train = null;
        Frame valid = null;
        try {
            tfr = parse_test_file("./smalldata/iris/iris.csv");
            FrameSplitter fs = new FrameSplitter(tfr, new double[]{ 0.8 }, new Key[]{ Key.make("train"), Key.make("valid") }, null);
            fs.compute2();
            train = fs.getResult()[0];
            valid = fs.getResult()[1];
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = train._key;
            parms._valid = valid._key;
            parms._epochs = 1;
            parms._response_column = "C5";
            parms._reproducible = true;
            parms._hidden = new int[]{ 50, 50 };
            parms._seed = 912559;
            parms._train_samples_per_iteration = 0;
            parms._score_duty_cycle = 1;
            parms._score_interval = 0;
            parms._stopping_rounds = 0;
            parms._overwrite_with_best_model = true;
            dl = trainModel().get();
            double ll1 = logloss();
            DeepLearningParameters parms2 = ((DeepLearningParameters) (parms.clone()));
            parms2._epochs = 10;
            parms2._checkpoint = dl._key;
            dl2 = trainModel().get();
            double ll2 = logloss();
            Assert.assertTrue((ll2 <= ll1));
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

            if (dl2 != null)
                dl2.delete();

            if (train != null)
                train.delete();

            if (valid != null)
                valid.delete();

        }
    }

    // Check that the restarted model honors the previous model as a best model so far
    @Test
    public void testCheckpointOverwriteWithBestModel2() {
        Frame tfr = null;
        DeepLearningModel dl = null;
        DeepLearningModel dl2 = null;
        Frame train = null;
        Frame valid = null;
        try {
            tfr = parse_test_file("./smalldata/iris/iris.csv");
            FrameSplitter fs = new FrameSplitter(tfr, new double[]{ 0.8 }, new Key[]{ Key.make("train"), Key.make("valid") }, null);
            fs.compute2();
            train = fs.getResult()[0];
            valid = fs.getResult()[1];
            DeepLearningParameters parms = new DeepLearningParameters();
            parms._train = train._key;
            parms._valid = valid._key;
            parms._epochs = 10;
            parms._response_column = "C5";
            parms._reproducible = true;
            parms._hidden = new int[]{ 50, 50 };
            parms._seed = 912559;
            parms._train_samples_per_iteration = 0;
            parms._score_duty_cycle = 1;
            parms._score_interval = 0;
            parms._stopping_rounds = 0;
            parms._overwrite_with_best_model = true;
            dl = trainModel().get();
            double ll1 = logloss();
            DeepLearningParameters parms2 = ((DeepLearningParameters) (parms.clone()));
            parms2._epochs = 20;
            parms2._checkpoint = dl._key;
            dl2 = trainModel().get();
            double ll2 = logloss();
            Assert.assertTrue((ll2 <= ll1));
        } finally {
            if (tfr != null)
                tfr.delete();

            if (dl != null)
                dl.delete();

            if (dl2 != null)
                dl2.delete();

            if (train != null)
                train.delete();

            if (valid != null)
                valid.delete();

        }
    }
}

