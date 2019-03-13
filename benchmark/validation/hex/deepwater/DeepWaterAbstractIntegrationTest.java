package hex.deepwater;


import DeepWaterParameters.Activation;
import DeepWaterParameters.Network.googlenet;
import DeepWaterParameters.Network.vgg;
import DeepWaterParameters.ProblemType;
import Model.Parameters.CategoricalEncodingScheme.AUTO;
import Model.Parameters.CategoricalEncodingScheme.Binary;
import Model.Parameters.CategoricalEncodingScheme.Eigen;
import Model.Parameters.CategoricalEncodingScheme.OneHotExplicit;
import Model.Parameters.CategoricalEncodingScheme.OneHotInternal;
import deepwater.backends.BackendModel;
import deepwater.backends.BackendTrain;
import hex.FrameSplitter;
import hex.splitframe.ShuffleSplitFrame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;
import water.exceptions.H2OIllegalArgumentException;
import water.fvec.Frame;
import water.fvec.NFSFileVec;
import water.fvec.Vec;
import water.parser.ParseDataset;
import water.util.FileUtils;
import water.util.Log;
import water.util.StringUtils;
import water.util.TwoDimTable;

import static Network.Network.vgg;
import static water.TestUtil.<init>;


public abstract class DeepWaterAbstractIntegrationTest extends TestUtil {
    protected BackendTrain backend;

    @Test
    public void memoryLeakTest() {
        DeepWaterModel m = null;
        Frame tr = null;
        int counter = 3;
        while ((counter--) > 0) {
            try {
                DeepWaterParameters p = new DeepWaterParameters();
                p._backend = getBackend();
                p._train = (tr = parse_test_file("bigdata/laptop/deepwater/imagenet/cat_dog_mouse.csv"))._key;
                p._response_column = "C2";
                p._network = vgg;
                p._learning_rate = 1.0E-4;
                p._mini_batch_size = 8;
                p._train_samples_per_iteration = 8;
                p._epochs = 0.001;
                m = trainModel().get();
                Log.info(m);
            } finally {
                if (m != null)
                    m.delete();

                if (tr != null)
                    tr.remove();

            }
        } 
    }

    @Test
    public void trainSamplesPerIteration0() {
        trainSamplesPerIteration(0, 3);
    }

    @Test
    public void trainSamplesPerIteration_auto() {
        trainSamplesPerIteration((-2), 1);
    }

    @Test
    public void trainSamplesPerIteration_neg1() {
        trainSamplesPerIteration((-1), 3);
    }

    @Test
    public void trainSamplesPerIteration_32() {
        trainSamplesPerIteration(32, 26);
    }

    @Test
    public void trainSamplesPerIteration_1000() {
        trainSamplesPerIteration(1000, 1);
    }

    @Test
    public void overWriteWithBestModel() {
        DeepWaterModel m = null;
        Frame tr = null;
        try {
            DeepWaterParameters p = new DeepWaterParameters();
            p._backend = getBackend();
            p._train = (tr = parse_test_file("bigdata/laptop/deepwater/imagenet/cat_dog_mouse.csv"))._key;
            p._response_column = "C2";
            p._epochs = 50;
            p._learning_rate = 0.01;
            p._momentum_start = 0.5;
            p._momentum_stable = 0.5;
            p._stopping_rounds = 0;
            p._image_shape = new int[]{ 28, 28 };
            p._network = lenet;
            p._problem_type = ProblemType.image;
            // score a lot
            p._train_samples_per_iteration = p._mini_batch_size;
            p._score_duty_cycle = 1;
            p._score_interval = 0;
            p._overwrite_with_best_model = true;
            m = trainModel().get();
            Log.info(m);
            Assert.assertTrue(((logloss()) < 2));
        } finally {
            if (m != null)
                m.remove();

            if (tr != null)
                tr.remove();

        }
    }

    @Test
    public void convergenceInceptionColor() {
        checkConvergence(3, inception_bn, 150);
    }

    @Test
    public void convergenceInceptionGrayScale() {
        checkConvergence(1, inception_bn, 150);
    }

    @Test
    public void convergenceGoogleNetColor() {
        checkConvergence(3, googlenet, 150);
    }

    @Test
    public void convergenceGoogleNetGrayScale() {
        checkConvergence(1, googlenet, 150);
    }

    @Test
    public void convergenceLenetColor() {
        checkConvergence(3, lenet, 300);
    }

    @Test
    public void convergenceLenetGrayScale() {
        checkConvergence(1, lenet, 150);
    }

    @Test
    public void convergenceVGGColor() {
        checkConvergence(3, vgg, 150);
    }

    @Test
    public void convergenceVGGGrayScale() {
        checkConvergence(1, vgg, 150);
    }

    @Test
    public void convergenceResnetColor() {
        checkConvergence(3, resnet, 150);
    }

    @Test
    public void convergenceResnetGrayScale() {
        checkConvergence(1, resnet, 150);
    }

    @Test
    public void convergenceAlexnetColor() {
        checkConvergence(3, alexnet, 150);
    }

    @Test
    public void convergenceAlexnetGrayScale() {
        checkConvergence(1, alexnet, 150);
    }

    @Test
    public void reproInitialDistributionNegativeTest() {
        final int REPS = 3;
        double[] values = new double[REPS];
        for (int i = 0; i < REPS; ++i) {
            DeepWaterModel m = null;
            Frame tr = null;
            try {
                DeepWaterParameters p = new DeepWaterParameters();
                p._backend = getBackend();
                p._train = (tr = parse_test_file("bigdata/laptop/deepwater/imagenet/cat_dog_mouse.csv"))._key;
                p._response_column = "C2";
                p._learning_rate = 0;// no updates to original weights

                p._seed = i;
                p._epochs = 1;// for some reason, can't use 0 epochs

                p._channels = 1;
                p._train_samples_per_iteration = 0;
                m = trainModel().get();
                Log.info(m);
                values[i] = ((hex.ModelMetricsMultinomial) (m._output._training_metrics)).logloss();
            } finally {
                if (m != null)
                    m.delete();

                if (tr != null)
                    tr.remove();

            }
        }
        for (int i = 1; i < REPS; ++i)
            Assert.assertNotEquals(values[0], values[i], (1.0E-5 * (values[0])));

    }

    @Test
    public void settingModelInfoAlexnet() {
        settingModelInfo(alexnet);
    }

    @Test
    public void settingModelInfoLenet() {
        settingModelInfo(lenet);
    }

    @Test
    public void settingModelInfoVGG() {
        settingModelInfo(vgg);
    }

    @Test
    public void settingModelInfoInception() {
        settingModelInfo(inception_bn);
    }

    @Test
    public void settingModelInfoResnet() {
        settingModelInfo(resnet);
    }

    @Test
    public void deepWaterLoadSaveTestAlexnet() {
        deepWaterLoadSaveTest(alexnet);
    }

    @Test
    public void deepWaterLoadSaveTestLenet() {
        deepWaterLoadSaveTest(lenet);
    }

    @Test
    public void deepWaterLoadSaveTestVGG() {
        deepWaterLoadSaveTest(vgg);
    }

    @Test
    public void deepWaterLoadSaveTestInception() {
        deepWaterLoadSaveTest(inception_bn);
    }

    @Test
    public void deepWaterLoadSaveTestResnet() {
        deepWaterLoadSaveTest(resnet);
    }

    @Test
    public void deepWaterCV() {
        DeepWaterModel m = null;
        Frame tr = null;
        Frame preds = null;
        try {
            DeepWaterParameters p = new DeepWaterParameters();
            p._backend = getBackend();
            p._train = (tr = parse_test_file("bigdata/laptop/deepwater/imagenet/cat_dog_mouse.csv"))._key;
            p._response_column = "C2";
            p._network = lenet;
            p._nfolds = 3;
            p._epochs = 2;
            m = trainModel().get();
            preds = m.score(p._train.get());
            Assert.assertTrue(m.testJavaScoring(p._train.get(), preds, 0.001));
            Log.info(m);
        } finally {
            if (m != null)
                m.deleteCrossValidationModels();

            if (m != null)
                m.delete();

            if (tr != null)
                tr.remove();

            if (preds != null)
                preds.remove();

        }
    }

    @Test
    public void deepWaterCVRegression() {
        DeepWaterModel m = null;
        Frame tr = null;
        Frame preds = null;
        try {
            DeepWaterParameters p = new DeepWaterParameters();
            p._backend = getBackend();
            p._train = (tr = parse_test_file("bigdata/laptop/deepwater/imagenet/cat_dog_mouse.csv"))._key;
            p._response_column = "C2";
            for (String col : new String[]{ p._response_column }) {
                Vec v = tr.remove(col);
                tr.add(col, v.toNumericVec());
                v.remove();
            }
            DKV.put(tr);
            p._network = lenet;
            p._nfolds = 3;
            p._epochs = 2;
            m = trainModel().get();
            preds = m.score(p._train.get());
            Assert.assertTrue(m.testJavaScoring(p._train.get(), preds, 0.001));
            Log.info(m);
        } finally {
            if (m != null)
                m.deleteCrossValidationModels();

            if (m != null)
                m.delete();

            if (tr != null)
                tr.remove();

            if (preds != null)
                preds.remove();

        }
    }

    @Test
    public void restoreStateAlexnet() {
        restoreState(alexnet);
    }

    @Test
    public void restoreStateLenet() {
        restoreState(lenet);
    }

    @Test
    public void restoreStateVGG() {
        restoreState(vgg);
    }

    @Test
    public void restoreStateInception() {
        restoreState(inception_bn);
    }

    @Test
    public void restoreStateResnet() {
        restoreState(resnet);
    }

    @Test
    public void trainLoop() throws InterruptedException {
        int batch_size = 64;
        BackendModel m = buildLENET();
        float[] data = new float[((28 * 28) * 1) * batch_size];
        float[] labels = new float[batch_size];
        int count = 0;
        while ((count++) < 1000) {
            Log.info(("Iteration: " + count));
            backend.train(m, data, labels);
        } 
    }

    @Test
    public void saveLoop() throws IOException {
        BackendModel m = buildLENET();
        File f = File.createTempFile("saveLoop", ".tmp");
        for (int count = 0; count < 3; count++) {
            Log.info(("Iteration: " + count));
            backend.saveParam(m, f.getAbsolutePath());
        }
        backend.deleteSavedParam(f.getAbsolutePath());
    }

    @Test
    public void predictLoop() {
        BackendModel m = buildLENET();
        int batch_size = 64;
        float[] data = new float[((28 * 28) * 1) * batch_size];
        int count = 0;
        while ((count++) < 3) {
            Log.info(("Iteration: " + count));
            backend.predict(m, data);
        } 
    }

    @Test
    public void trainPredictLoop() {
        int batch_size = 64;
        BackendModel m = buildLENET();
        float[] data = new float[((28 * 28) * 1) * batch_size];
        float[] labels = new float[batch_size];
        int count = 0;
        while ((count++) < 1000) {
            Log.info(("Iteration: " + count));
            backend.train(m, data, labels);
            float[] p = backend.predict(m, data);
        } 
    }

    @Test
    public void scoreLoop() {
        DeepWaterParameters p = new DeepWaterParameters();
        Frame tr;
        p._backend = getBackend();
        p._train = (tr = parse_test_file("bigdata/laptop/deepwater/imagenet/cat_dog_mouse.csv"))._key;
        p._network = lenet;
        p._response_column = "C2";
        p._mini_batch_size = 4;
        p._train_samples_per_iteration = p._mini_batch_size;
        p._learning_rate = 0.0;
        p._seed = 12345;
        p._epochs = 0.01;
        p._quiet_mode = true;
        DeepWater j = new DeepWater(p);
        DeepWaterModel m = j.trainModel().get();
        int count = 0;
        while ((count++) < 100) {
            Log.info(("Iteration: " + count));
            // turn the second model into the first model
            m.doScoring(tr, null, j._job._key, m.iterations, true);
        } 
        tr.remove();
        m.remove();
    }

    // @Test public void imageToPixels() throws IOException {
    // final File imgFile = find_test_file("smalldata/deepwater/imagenet/test2.jpg");
    // final float[] dest = new float[28*28*3];
    // int count=0;
    // Futures fs = new Futures();
    // while(count++<10000)
    // fs.add(H2O.submitTask(
    // new H2O.H2OCountedCompleter() {
    // @Override
    // public void compute2() {
    // try {
    // util.img2pixels(imgFile.toString(), 28, 28, 3, dest, 0, null);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // tryComplete();
    // }
    // }));
    // fs.blockForPending();
    // }
    @Test
    public void prostateClassification() {
        Frame tr = null;
        DeepWaterModel m = null;
        try {
            DeepWaterParameters p = new DeepWaterParameters();
            p._backend = getBackend();
            p._train = (tr = parse_test_file("smalldata/prostate/prostate.csv"))._key;
            p._response_column = "CAPSULE";
            p._ignored_columns = new String[]{ "ID" };
            for (String col : new String[]{ "RACE", "DPROS", "DCAPS", "CAPSULE", "GLEASON" }) {
                Vec v = tr.remove(col);
                tr.add(col, v.toCategoricalVec());
                v.remove();
            }
            DKV.put(tr);
            p._seed = 1234;
            p._epochs = 500;
            DeepWater j = new DeepWater(p);
            m = j.trainModel().get();
            Assert.assertTrue(((m._output._training_metrics.auc_obj()._auc) > 0.9));
        } finally {
            if (tr != null)
                tr.remove();

            if (m != null)
                m.remove();

        }
    }

    @Test
    public void prostateRegression() {
        Frame tr = null;
        Frame preds = null;
        DeepWaterModel m = null;
        try {
            DeepWaterParameters p = new DeepWaterParameters();
            p._backend = getBackend();
            p._train = (tr = parse_test_file("smalldata/prostate/prostate.csv"))._key;
            p._response_column = "AGE";
            p._ignored_columns = new String[]{ "ID" };
            for (String col : new String[]{ "RACE", "DPROS", "DCAPS", "CAPSULE", "GLEASON" }) {
                Vec v = tr.remove(col);
                tr.add(col, v.toCategoricalVec());
                v.remove();
            }
            DKV.put(tr);
            p._seed = 1234;
            p._epochs = 1000;
            // p._epochs = 2000;
            // p._learning_rate = 0.005; //5e-7;
            // p._momentum_start = 0.9;
            DeepWater j = new DeepWater(p);
            m = j.trainModel().get();
            Assert.assertTrue(((m._output._training_metrics.rmse()) < 5));
            preds = m.score(p._train.get());
            Assert.assertTrue(m.testJavaScoring(p._train.get(), preds, 0.001));
        } finally {
            if (tr != null)
                tr.remove();

            if (m != null)
                m.remove();

            if (preds != null)
                preds.remove();

        }
    }

    @Test
    public void imageURLs() {
        Frame tr = null;
        Frame preds = null;
        DeepWaterModel m = null;
        try {
            DeepWaterParameters p = new DeepWaterParameters();
            p._backend = getBackend();
            p._train = (tr = parse_test_file("smalldata/deepwater/imagenet/binomial_image_urls.csv"))._key;
            p._response_column = "C2";
            p._network = lenet;
            p._epochs = 500;
            p._seed = 1234;
            DeepWater j = new DeepWater(p);
            m = j.trainModel().get();
            Assert.assertTrue(((m._output._training_metrics.auc_obj()._auc) > 0.85));
            preds = m.score(p._train.get());
            Assert.assertTrue(m.testJavaScoring(p._train.get(), preds, 0.001, 1.0E-5, 1));
        } finally {
            if (tr != null)
                tr.remove();

            if (preds != null)
                preds.remove();

            if (m != null)
                m.remove();

        }
    }

    @Test
    public void categorical() {
        Frame tr = null;
        DeepWaterModel m = null;
        try {
            DeepWaterParameters p = new DeepWaterParameters();
            p._backend = getBackend();
            p._train = (tr = parse_test_file("smalldata/gbm_test/alphabet_cattest.csv"))._key;
            p._response_column = "y";
            for (String col : new String[]{ "y" }) {
                Vec v = tr.remove(col);
                tr.add(col, v.toCategoricalVec());
                v.remove();
            }
            DKV.put(tr);
            DeepWater j = new DeepWater(p);
            m = j.trainModel().get();
            Assert.assertTrue(((m._output._training_metrics.auc_obj()._auc) > 0.9));
        } finally {
            if (tr != null)
                tr.remove();

            if (m != null)
                m.remove();

        }
    }

    @Test
    public void MNISTLenet() {
        Frame tr = null;
        Frame va = null;
        DeepWaterModel m = null;
        try {
            DeepWaterParameters p = new DeepWaterParameters();
            File file = FileUtils.locateFile("bigdata/laptop/mnist/train.csv.gz");
            File valid = FileUtils.locateFile("bigdata/laptop/mnist/test.csv.gz");
            if (file != null) {
                p._response_column = "C785";
                NFSFileVec trainfv = NFSFileVec.make(file);
                tr = ParseDataset.parse(Key.make(), trainfv._key);
                NFSFileVec validfv = NFSFileVec.make(valid);
                va = ParseDataset.parse(Key.make(), validfv._key);
                for (String col : new String[]{ p._response_column }) {
                    Vec v = tr.remove(col);
                    tr.add(col, v.toCategoricalVec());
                    v.remove();
                    v = va.remove(col);
                    va.add(col, v.toCategoricalVec());
                    v.remove();
                }
                DKV.put(tr);
                DKV.put(va);
                p._backend = getBackend();
                p._train = tr._key;
                p._valid = va._key;
                p._image_shape = new int[]{ 28, 28 };
                p._ignore_const_cols = false;// to keep it 28x28

                p._channels = 1;
                p._network = lenet;
                DeepWater j = new DeepWater(p);
                m = j.trainModel().get();
                Assert.assertTrue(((mean_per_class_error()) < 0.05));
            }
        } finally {
            if (tr != null)
                tr.remove();

            if (va != null)
                va.remove();

            if (m != null)
                m.remove();

        }
    }

    @Test
    public void MNISTSparse() {
        Frame tr = null;
        Frame va = null;
        DeepWaterModel m = null;
        try {
            DeepWaterParameters p = new DeepWaterParameters();
            File file = FileUtils.locateFile("bigdata/laptop/mnist/train.csv.gz");
            File valid = FileUtils.locateFile("bigdata/laptop/mnist/test.csv.gz");
            if (file != null) {
                p._response_column = "C785";
                NFSFileVec trainfv = NFSFileVec.make(file);
                tr = ParseDataset.parse(Key.make(), trainfv._key);
                NFSFileVec validfv = NFSFileVec.make(valid);
                va = ParseDataset.parse(Key.make(), validfv._key);
                for (String col : new String[]{ p._response_column }) {
                    Vec v = tr.remove(col);
                    tr.add(col, v.toCategoricalVec());
                    v.remove();
                    v = va.remove(col);
                    va.add(col, v.toCategoricalVec());
                    v.remove();
                }
                DKV.put(tr);
                DKV.put(va);
                p._backend = getBackend();
                p._train = tr._key;
                p._valid = va._key;
                p._learning_rate = 0.005;
                p._hidden = new int[]{ 500, 500 };
                p._sparse = true;
                DeepWater j = new DeepWater(p);
                m = j.trainModel().get();
                Assert.assertTrue(((mean_per_class_error()) < 0.05));
            }
        } finally {
            if (tr != null)
                tr.remove();

            if (va != null)
                va.remove();

            if (m != null)
                m.remove();

        }
    }

    @Test
    public void MNISTHinton() {
        Frame tr = null;
        Frame va = null;
        DeepWaterModel m = null;
        try {
            DeepWaterParameters p = new DeepWaterParameters();
            File file = FileUtils.locateFile("bigdata/laptop/mnist/train.csv.gz");
            File valid = FileUtils.locateFile("bigdata/laptop/mnist/test.csv.gz");
            if (file != null) {
                p._response_column = "C785";
                NFSFileVec trainfv = NFSFileVec.make(file);
                tr = ParseDataset.parse(Key.make(), trainfv._key);
                NFSFileVec validfv = NFSFileVec.make(valid);
                va = ParseDataset.parse(Key.make(), validfv._key);
                for (String col : new String[]{ p._response_column }) {
                    Vec v = tr.remove(col);
                    tr.add(col, v.toCategoricalVec());
                    v.remove();
                    v = va.remove(col);
                    va.add(col, v.toCategoricalVec());
                    v.remove();
                }
                DKV.put(tr);
                DKV.put(va);
                p._backend = getBackend();
                p._hidden = new int[]{ 1024, 1024, 2048 };
                p._input_dropout_ratio = 0.1;
                p._hidden_dropout_ratios = new double[]{ 0.5, 0.5, 0.5 };
                p._stopping_rounds = 0;
                p._learning_rate = 0.001;
                p._mini_batch_size = 32;
                p._epochs = 20;
                p._train = tr._key;
                p._valid = va._key;
                DeepWater j = new DeepWater(p);
                m = j.trainModel().get();
                Assert.assertTrue(((mean_per_class_error()) < 0.05));
            }
        } finally {
            if (tr != null)
                tr.remove();

            if (va != null)
                va.remove();

            if (m != null)
                m.remove();

        }
    }

    @Test
    public void Airlines() {
        Frame tr = null;
        DeepWaterModel m = null;
        Frame[] splits = null;
        try {
            DeepWaterParameters p = new DeepWaterParameters();
            File file = FileUtils.locateFile("smalldata/airlines/allyears2k_headers.zip");
            if (file != null) {
                p._response_column = "IsDepDelayed";
                p._ignored_columns = new String[]{ "DepTime", "ArrTime", "Cancelled", "CancellationCode", "Diverted", "CarrierDelay", "WeatherDelay", "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsArrDelayed" };
                NFSFileVec trainfv = NFSFileVec.make(file);
                tr = ParseDataset.parse(Key.make(), trainfv._key);
                for (String col : new String[]{ p._response_column, "UniqueCarrier", "Origin", "Dest" }) {
                    Vec v = tr.remove(col);
                    tr.add(col, v.toCategoricalVec());
                    v.remove();
                }
                DKV.put(tr);
                double[] ratios = ard(0.5, 0.5);
                Key[] keys = aro(Key.make("test.hex"), Key.make("train.hex"));
                splits = ShuffleSplitFrame.shuffleSplitFrame(tr, keys, ratios, 42);
                p._backend = getBackend();
                p._train = keys[0];
                p._valid = keys[1];
                DeepWater j = new DeepWater(p);
                m = j.trainModel().get();
                Assert.assertTrue(((auc()) > 0.65));
            }
        } finally {
            if (tr != null)
                tr.remove();

            if (m != null)
                m.remove();

            if (splits != null)
                for (Frame s : splits)
                    s.remove();


        }
    }

    @Test
    public void MOJOTestImageLenet() {
        MOJOTestImage(lenet);
    }

    @Test
    public void MOJOTestImageInception() {
        MOJOTestImage(inception_bn);
    }

    @Test
    public void MOJOTestImageAlexnet() {
        MOJOTestImage(alexnet);
    }

    @Test
    public void MOJOTestImageVGG() {
        MOJOTestImage(vgg);
    }

    @Test
    public void MOJOTestNumericNonStandardized() {
        MOJOTest(AUTO, false, false);
    }

    @Test
    public void MOJOTestNumeric() {
        MOJOTest(AUTO, false, true);
    }

    @Test
    public void MOJOTestCatInternal() {
        MOJOTest(OneHotInternal, true, true);
    }

    @Test
    public void MOJOTestCatExplicit() {
        MOJOTest(OneHotExplicit, true, true);
    }

    @Test
    public void MOJOTestCatEigen() {
        MOJOTest(Eigen, true, true);
    }

    @Test
    public void MOJOTestCatBinary() {
        MOJOTest(Binary, true, true);
    }

    @Test
    public void testCheckpointForwards() {
        Frame tfr = null;
        DeepWaterModel dl = null;
        DeepWaterModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/iris/iris.csv");
            DeepWaterParameters p = new DeepWaterParameters();
            p._backend = getBackend();
            p._train = tfr._key;
            p._epochs = 10;
            p._response_column = "C5";
            p._hidden = new int[]{ 2, 2 };
            p._seed = 912559;
            p._stopping_rounds = 0;
            dl = trainModel().get();
            DeepWaterParameters parms2 = ((DeepWaterParameters) (p.clone()));
            parms2._epochs = 20;
            parms2._checkpoint = dl._key;
            dl2 = trainModel().get();
            Assert.assertTrue(((dl2.epoch_counter) > 20));
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
        DeepWaterModel dl = null;
        DeepWaterModel dl2 = null;
        try {
            tfr = parse_test_file("./smalldata/iris/iris.csv");
            DeepWaterParameters p = new DeepWaterParameters();
            p._backend = getBackend();
            p._train = tfr._key;
            p._epochs = 10;
            p._response_column = "C5";
            p._hidden = new int[]{ 2, 2 };
            p._seed = 912559;
            dl = trainModel().get();
            DeepWaterParameters parms2 = ((DeepWaterParameters) (p.clone()));
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
    public void checkpointReporting() {
        Scope.enter();
        Frame frame = null;
        try {
            File file = FileUtils.locateFile("smalldata/logreg/prostate.csv");
            NFSFileVec trainfv = NFSFileVec.make(file);
            frame = ParseDataset.parse(Key.make(), trainfv._key);
            DeepWaterParameters p = new DeepWaterParameters();
            // populate model parameters
            p._backend = getBackend();
            p._train = frame._key;
            p._response_column = "CAPSULE";// last column is the response

            p._activation = Activation.Rectifier;
            p._epochs = 4;
            p._train_samples_per_iteration = -1;
            p._mini_batch_size = 1;
            p._score_duty_cycle = 1;
            p._score_interval = 0;
            p._overwrite_with_best_model = false;
            p._seed = 1234;
            // Convert response 'C785' to categorical (digits 1 to 10)
            int ci = frame.find("CAPSULE");
            Scope.track(frame.replace(ci, frame.vecs()[ci].toCategoricalVec()));
            DKV.put(frame);
            long start = System.currentTimeMillis();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }// to avoid rounding issues with printed time stamp (1 second resolution)

            DeepWaterModel model = trainModel().get();
            long sleepTime = 5;// seconds

            try {
                Thread.sleep((sleepTime * 1000));
            } catch (InterruptedException ex) {
            }
            // checkpoint restart after sleep
            DeepWaterParameters p2 = ((DeepWaterParameters) (p.clone()));
            p2._checkpoint = model._key;
            p2._epochs *= 2;
            DeepWaterModel model2 = null;
            try {
                model2 = trainModel().get();
                long end = System.currentTimeMillis();
                TwoDimTable table = model2._output._scoring_history;
                double priorDurationDouble = 0;
                long priorTimeStampLong = 0;
                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                for (int i = 0; i < (table.getRowDim()); ++i) {
                    // Check that timestamp is correct, and growing monotonically
                    String timestamp = ((String) (table.get(i, 0)));
                    long timeStampLong = fmt.parseMillis(timestamp);
                    Assert.assertTrue("Timestamp must be later than outside timer start", (timeStampLong >= start));
                    Assert.assertTrue("Timestamp must be earlier than outside timer end", (timeStampLong <= end));
                    Assert.assertTrue("Timestamp must increase", (timeStampLong >= priorTimeStampLong));
                    priorTimeStampLong = timeStampLong;
                    // Check that duration is growing monotonically
                    String duration = ((String) (table.get(i, 1)));
                    duration = duration.substring(0, ((duration.length()) - 4));// "x.xxxx sec"

                    try {
                        double durationDouble = Double.parseDouble(duration);
                        Assert.assertTrue(("Duration must be >0: " + durationDouble), (durationDouble >= 0));
                        Assert.assertTrue(((("Duration must increase: " + priorDurationDouble) + " -> ") + durationDouble), (durationDouble >= priorDurationDouble));
                        Assert.assertTrue("Duration cannot be more than outside timer delta", (durationDouble <= ((end - start) / 1000.0)));
                        priorDurationDouble = durationDouble;
                    } catch (NumberFormatException ex) {
                        // skip
                    }
                    // Check that epoch counting is good
                    Assert.assertTrue("Epoch counter must be contiguous", (((Double) (table.get(i, 3))) == i));// 1 epoch per step

                    Assert.assertTrue("Iteration counter must match epochs", (((Integer) (table.get(i, 4))) == i));// 1 iteration per step

                }
                try {
                    // Check that duration doesn't see the sleep
                    String durationBefore = ((String) (table.get(((int) (p._epochs)), 1)));
                    durationBefore = durationBefore.substring(0, ((durationBefore.length()) - 4));
                    String durationAfter = ((String) (table.get(((int) ((p._epochs) + 1)), 1)));
                    durationAfter = durationAfter.substring(0, ((durationAfter.length()) - 4));
                    Assert.assertTrue("Duration must be smooth", (((Double.parseDouble(durationAfter)) - (Double.parseDouble(durationBefore))) < (sleepTime + 1)));
                    // Check that time stamp does see the sleep
                    String timeStampBefore = ((String) (table.get(((int) (p._epochs)), 0)));
                    long timeStampBeforeLong = fmt.parseMillis(timeStampBefore);
                    String timeStampAfter = ((String) (table.get(((int) ((p._epochs) + 1)), 0)));
                    long timeStampAfterLong = fmt.parseMillis(timeStampAfter);
                    Assert.assertTrue("Time stamp must experience a delay", ((timeStampAfterLong - timeStampBeforeLong) >= ((sleepTime - 1/* rounding */
                    ) * 1000)));
                    // Check that the training speed is similar before and after checkpoint restart
                    String speedBefore = ((String) (table.get(((int) (p._epochs)), 2)));
                    speedBefore = speedBefore.substring(0, ((speedBefore.length()) - 9));
                    double speedBeforeDouble = Double.parseDouble(speedBefore);
                    String speedAfter = ((String) (table.get(((int) ((p._epochs) + 1)), 2)));
                    speedAfter = speedAfter.substring(0, ((speedAfter.length()) - 9));
                    double speedAfterDouble = Double.parseDouble(speedAfter);
                    Assert.assertTrue("Speed shouldn't change more than 50%", (((Math.abs((speedAfterDouble - speedBeforeDouble))) / speedBeforeDouble) < 0.5));// expect less than 50% change in speed

                } catch (NumberFormatException ex) {
                    // skip runtimes > 1 minute (too hard to parse into seconds here...).
                }
            } finally {
                if (model != null)
                    model.delete();

                if (model2 != null)
                    model2.delete();

            }
        } finally {
            if (frame != null)
                frame.remove();

            Scope.exit();
        }
    }

    @Test
    public void testNumericalExplosion() {
        for (boolean ae : new boolean[]{ // true,
        false }) {
            Frame tfr = null;
            DeepWaterModel dl = null;
            Frame pred = null;
            try {
                tfr = parse_test_file("./smalldata/junit/two_spiral.csv");
                for (String s : new String[]{ "Class" }) {
                    Vec resp = tfr.vec(s).toCategoricalVec();
                    tfr.remove(s).remove();
                    tfr.add(s, resp);
                    DKV.put(tfr);
                }
                DeepWaterParameters parms = new DeepWaterParameters();
                parms._backend = getBackend();
                parms._train = tfr._key;
                parms._epochs = 100;
                parms._response_column = "Class";
                parms._autoencoder = ae;
                parms._train_samples_per_iteration = 10;
                parms._hidden = new int[]{ 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
                parms._learning_rate = 1.0E10;
                parms._standardize = false;
                // Build a first model; all remaining models should be equal
                DeepWater job = new DeepWater(parms);
                try {
                    dl = job.trainModel().get();
                    Assert.fail("Should toss exception instead of reaching here");
                } catch (RuntimeException de) {
                    // OK
                }
                dl = DKV.getGet(job.dest());
                try {
                    pred = dl.score(tfr);
                    Assert.fail("Should toss exception instead of reaching here");
                } catch (RuntimeException ex) {
                    // OK
                }
                try {
                    dl.getMojo();
                    Assert.fail("Should toss exception instead of reaching here");
                } catch (RuntimeException ex) {
                    System.err.println(ex.getMessage());
                    // OK
                }
                Assert.assertTrue(dl.model_info()._unstable);
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

    // ------- Text conversions
    @Test
    public void textsToArrayTest() throws IOException {
        ArrayList<String> texts = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        texts.add("the rock is destined to be the 21st century\'s new \" conan \" and that he\'s going to make a splash even greater than arnold schwarzenegger , jean-claud van damme or steven segal .");
        texts.add("the gorgeously elaborate continuation of \" the lord of the rings \" trilogy is so huge that a column of words cannot adequately describe co-writer/director peter jackson\'s expanded vision of j . r . r . tolkien\'s middle-earth .");
        texts.add("effective but too-tepid biopic");
        labels.add("pos");
        labels.add("pos");
        labels.add("pos");
        texts.add("simplistic , silly and tedious .");
        texts.add("it's so laddish and juvenile , only teenage boys could possibly find it funny .");
        texts.add("exploitative and largely devoid of the depth or sophistication that would make watching such a graphic treatment of the crimes bearable .");
        labels.add("neg");
        labels.add("neg");
        labels.add("neg");
        ArrayList<int[]> coded = StringUtils.texts2array(texts);
        // System.out.println(coded);
        for (int[] a : coded) {
            System.out.println(Arrays.toString(a));
        }
        System.out.println(((("rows " + (coded.size())) + " cols ") + (coded.get(0).length)));
        Assert.assertEquals(6, coded.size());
        Assert.assertEquals(38, coded.get(0).length);
    }

    /* public ArrayList<int[]> texts2arrayOnehot(ArrayList<String> texts) {
    int maxlen = 0;
    int index = 0;
    Map<String, Integer> dict = new HashMap<>();
    dict.put(PADDING_SYMBOL, index);
    index += 1;
    for (String text : texts) {
    String[] tokens = tokenize(text);
    for (String token : tokens) {
    if (!dict.containsKey(token)) {
    dict.put(token, index);
    index += 1;
    }
    }
    int len = tokens.length;
    if (len > maxlen) maxlen = len;
    }
    System.out.println(dict);
    System.out.println("maxlen " + maxlen);
    System.out.println("dict size " + dict.size());
    Assert.assertEquals(38, maxlen);
    Assert.assertEquals(88, index);
    Assert.assertEquals(88, dict.size());

    ArrayList<int[]> array = new ArrayList<>();
    for (String text: texts) {
    ArrayList<int[]> data = tokensToArray(tokenize(text), maxlen, dict);
    System.out.println(text);
    System.out.println(" rows " + data.size() + "  cols " + data.get(0).length);
    //for (int[] x : data) {
    //  System.out.println(Arrays.toString(x));
    //}
    array.addAll(data);
    }
    return array;
    }
     */
    @Test
    public void testCheckpointOverwriteWithBestModel() {
        Frame tfr = null;
        DeepWaterModel dl = null;
        DeepWaterModel dl2 = null;
        Frame train = null;
        Frame valid = null;
        try {
            tfr = parse_test_file("./smalldata/iris/iris.csv");
            FrameSplitter fs = new FrameSplitter(tfr, new double[]{ 0.8 }, new Key[]{ Key.make("train"), Key.make("valid") }, null);
            fs.compute2();
            train = fs.getResult()[0];
            valid = fs.getResult()[1];
            DeepWaterParameters parms = new DeepWaterParameters();
            parms._backend = getBackend();
            parms._train = train._key;
            parms._valid = valid._key;
            parms._epochs = 1;
            parms._response_column = "C5";
            parms._hidden = new int[]{ 50, 50 };
            parms._seed = 912559;
            parms._train_samples_per_iteration = 0;
            parms._score_duty_cycle = 1;
            parms._score_interval = 0;
            parms._stopping_rounds = 0;
            parms._overwrite_with_best_model = true;
            dl = trainModel().get();
            double ll1 = ((hex.ModelMetricsMultinomial) (dl._output._validation_metrics)).logloss();
            DeepWaterParameters parms2 = ((DeepWaterParameters) (parms.clone()));
            parms2._epochs = 10;
            parms2._checkpoint = dl._key;
            dl2 = trainModel().get();
            double ll2 = ((hex.ModelMetricsMultinomial) (dl2._output._validation_metrics)).logloss();
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
        DeepWaterModel dl = null;
        DeepWaterModel dl2 = null;
        Frame train = null;
        Frame valid = null;
        try {
            tfr = parse_test_file("./smalldata/iris/iris.csv");
            FrameSplitter fs = new FrameSplitter(tfr, new double[]{ 0.8 }, new Key[]{ Key.make("train"), Key.make("valid") }, null);
            fs.compute2();
            train = fs.getResult()[0];
            valid = fs.getResult()[1];
            DeepWaterParameters parms = new DeepWaterParameters();
            parms._backend = getBackend();
            parms._train = train._key;
            parms._valid = valid._key;
            parms._epochs = 10;
            parms._response_column = "C5";
            parms._hidden = new int[]{ 50, 50 };
            parms._seed = 912559;
            parms._train_samples_per_iteration = 0;
            parms._score_duty_cycle = 1;
            parms._score_interval = 0;
            parms._stopping_rounds = 0;
            parms._overwrite_with_best_model = true;
            dl = trainModel().get();
            double ll1 = ((hex.ModelMetricsMultinomial) (dl._output._validation_metrics)).logloss();
            DeepWaterParameters parms2 = ((DeepWaterParameters) (parms.clone()));
            parms2._epochs = 20;
            parms2._checkpoint = dl._key;
            dl2 = trainModel().get();
            double ll2 = ((hex.ModelMetricsMultinomial) (dl2._output._validation_metrics)).logloss();
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

