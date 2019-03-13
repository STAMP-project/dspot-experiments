package hex.deeplearning;


import DeepLearningParameters.Activation;
import DeepLearningParameters.InitialWeightDistribution;
import DeepLearningParameters.Loss;
import ScoreKeeper.StoppingMetric;
import hex.ModelMetricsBinomial;
import hex.deeplearning.DeepLearningModel.DeepLearningParameters;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.NFSFileVec;
import water.parser.ParseDataset;
import water.util.Log;


public class DeepLearningSpiralsTest extends TestUtil {
    @Test
    public void run() {
        Scope.enter();
        NFSFileVec nfs = TestUtil.makeNfsFileVec("smalldata/junit/two_spiral.csv");
        Frame frame = ParseDataset.parse(Key.make(), nfs._key);
        Log.info(frame);
        int resp = (frame.names().length) - 1;
        for (boolean sparse : new boolean[]{ true, false }) {
            for (boolean col_major : new boolean[]{ false }) {
                if ((!sparse) && col_major)
                    continue;

                Key model_id = Key.make();
                // build the model
                {
                    DeepLearningParameters p = new DeepLearningParameters();
                    p._epochs = 5000;
                    p._hidden = new int[]{ 100 };
                    p._sparse = sparse;
                    p._col_major = col_major;
                    p._activation = Activation.Tanh;
                    p._initial_weight_distribution = InitialWeightDistribution.Normal;
                    p._initial_weight_scale = 2.5;
                    p._loss = Loss.CrossEntropy;
                    p._train = frame._key;
                    p._response_column = frame.names()[resp];
                    Scope.track(frame.replace(resp, frame.vecs()[resp].toCategoricalVec()));// Convert response to categorical

                    DKV.put(frame);
                    p._rho = 0.99;
                    p._epsilon = 0.005;
                    p._classification_stop = 0;// stop when reaching 0 classification error on training data

                    p._train_samples_per_iteration = 10000;
                    p._stopping_rounds = 5;
                    p._stopping_metric = StoppingMetric.misclassification;
                    p._score_each_iteration = true;
                    p._reproducible = true;
                    p._seed = 1234;
                    trainModel().get();
                }
                // score and check result
                {
                    DeepLearningModel mymodel = DKV.getGet(model_id);
                    Frame pred = mymodel.score(frame);
                    ModelMetricsBinomial mm = ModelMetricsBinomial.getFromDKV(mymodel, frame);
                    double error = mm._auc.defaultErr();
                    Log.info(("Error: " + error));
                    if (error > 0.1) {
                        Assert.fail((("Test classification error is not <= 0.1, but " + error) + "."));
                    }
                    Assert.assertTrue(mymodel.testJavaScoring(frame, pred, 1.0E-6));
                    pred.delete();
                    mymodel.delete();
                }
            }
        }
        frame.delete();
        Scope.exit();
    }
}

