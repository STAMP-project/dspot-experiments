package hex.deeplearning;


import DeepLearningParameters.Activation;
import DeepLearningParameters.Loss;
import hex.deeplearning.DeepLearningModel.DeepLearningParameters;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;
import water.fvec.Frame;
import water.fvec.NFSFileVec;
import water.parser.ParseDataset;
import water.util.FrameUtils;
import water.util.Log;

import static water.TestUtil.<init>;


public class DeepLearningReproducibilityTest extends TestUtil {
    @Test
    public void run() {
        NFSFileVec ff = TestUtil.makeNfsFileVec("smalldata/junit/weather.csv");
        Frame golden = ParseDataset.parse(Key.make("golden.hex"), ff._key);
        DeepLearningModel mymodel = null;
        Frame train = null;
        Frame test = null;
        Frame data = null;
        Map<Integer, Float> repeatErrs = new TreeMap<>();
        int N = 3;
        StringBuilder sb = new StringBuilder();
        float repro_error = 0;
        for (boolean repro : new boolean[]{ true, false }) {
            Scope.enter();
            Frame[] preds = new Frame[N];
            long[] checksums = new long[N];
            double[] numbers = new double[N];
            for (int repeat = 0; repeat < N; ++repeat) {
                try {
                    NFSFileVec file = TestUtil.makeNfsFileVec("smalldata/junit/weather.csv");
                    data = ParseDataset.parse(Key.make("data.hex"), file._key);
                    Assert.assertTrue(TestUtil.isBitIdentical(data, golden));// test parser consistency

                    // Create holdout test data on clean data (before adding missing values)
                    train = data;
                    test = data;
                    // Build a regularized DL model with polluted training data, score on clean validation set
                    DeepLearningParameters p = new DeepLearningParameters();
                    p._train = train._key;
                    p._valid = test._key;
                    p._response_column = train.names()[((train.names().length) - 1)];
                    int ci = (train.names().length) - 1;
                    Scope.track(train.replace(ci, train.vecs()[ci].toCategoricalVec()));
                    DKV.put(train);
                    p._ignored_columns = new String[]{ "EvapMM", "RISK_MM" };// for weather data

                    p._activation = Activation.RectifierWithDropout;
                    p._hidden = new int[]{ 32, 58 };
                    p._l1 = 1.0E-5;
                    p._l2 = 3.0E-5;
                    p._seed = 48830;
                    p._loss = Loss.CrossEntropy;
                    p._input_dropout_ratio = 0.2;
                    p._train_samples_per_iteration = 3;
                    p._hidden_dropout_ratios = new double[]{ 0.4, 0.1 };
                    p._epochs = 1.32;
                    // p._nfolds = 2;
                    p._quiet_mode = true;
                    p._reproducible = repro;
                    DeepLearning dl = new DeepLearning(p);
                    mymodel = dl.trainModel().get();
                    // Extract the scoring on validation set from the model
                    preds[repeat] = mymodel.score(test);
                    for (int i = 0; i < 5; ++i) {
                        Frame tmp = mymodel.score(test);
                        Assert.assertTrue((((("Prediction #" + i) + " for repeat #") + repeat) + " differs!"), TestUtil.isBitIdentical(preds[repeat], tmp));
                        tmp.delete();
                    }
                    Log.info(("Prediction:\n" + (FrameUtils.chunkSummary(preds[repeat]).toString())));
                    numbers[repeat] = mymodel.model_info().get_weights(0).get(23, 4);
                    checksums[repeat] = mymodel.model_info().checksum_impl();// check that the model state is consistent

                    repeatErrs.put(repeat, mymodel.loss());
                } finally {
                    // cleanup
                    if (mymodel != null) {
                        mymodel.delete();
                    }
                    if (train != null)
                        train.delete();

                    if (test != null)
                        test.delete();

                    if (data != null)
                        data.delete();

                }
            }
            sb.append("Reproducibility: ").append((repro ? "on" : "off")).append("\n");
            sb.append("Repeat # --> Validation Loss\n");
            for (String s : Arrays.toString(repeatErrs.entrySet().toArray()).split(","))
                sb.append(s.replace("=", " --> ")).append("\n");

            sb.append('\n');
            Log.info(sb.toString());
            try {
                if (repro) {
                    // check reproducibility
                    for (double error : numbers)
                        Assert.assertTrue(Arrays.toString(numbers), (error == (numbers[0])));

                    for (Float error : repeatErrs.values())
                        Assert.assertTrue(error.equals(repeatErrs.get(0)));

                    for (long cs : checksums)
                        Assert.assertTrue((cs == (checksums[0])));

                    for (Frame f : preds) {
                        // assertTrue(TestUtil.isBitIdentical(f, preds[0])); // PUBDEV-892: This should have passed all the time
                        for (int i = 0; i < (f.vecs().length); ++i) {
                            TestUtil.assertVecEquals(f.vecs()[i], preds[0].vecs()[i], 1.0E-5);// PUBDEV-892: This tolerance should be 1e-15

                        }
                    }
                    repro_error = repeatErrs.get(0);
                } else {
                    // check standard deviation of non-reproducible mode
                    double mean = 0;
                    for (Float error : repeatErrs.values()) {
                        mean += error;
                    }
                    mean /= N;
                    // check non-reproducibility (Hogwild! will never reproduce)
                    for (int i = 1; i < N; ++i)
                        Assert.assertTrue(((repeatErrs.get(i)) != (repeatErrs.get(0))));

                    Log.info(("mean error: " + mean));
                    double stddev = 0;
                    for (Float error : repeatErrs.values()) {
                        stddev += (error - mean) * (error - mean);
                    }
                    stddev /= N;
                    stddev = Math.sqrt(stddev);
                    Log.info(("standard deviation: " + stddev));
                    // assertTrue(stddev < 0.3 / Math.sqrt(N));
                    Log.info((("difference to reproducible mode: " + ((Math.abs((mean - repro_error))) / stddev)) + " standard deviations"));
                }
            } finally {
                for (Frame f : preds)
                    if (f != null)
                        f.delete();


            }
            Scope.exit();
        }
        golden.delete();
    }
}

