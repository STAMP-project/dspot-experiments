package hex;


import DeepLearning.Activation;
import DeepLearning.InitialWeightDistribution;
import DeepLearning.Loss;
import hex.deeplearning.DeepLearning;
import hex.deeplearning.DeepLearningModel;
import junit.framework.Assert;
import org.junit.Test;
import water.Key;
import water.TestUtil;
import water.UKV;
import water.fvec.Frame;
import water.fvec.NFSFileVec;
import water.fvec.ParseDataset2;


public class DeepLearningSpiralsTest extends TestUtil {
    @Test
    public void run() {
        Key file = NFSFileVec.make(TestUtil.find_test_file("smalldata/neural/two_spiral.data"));
        Frame frame = ParseDataset2.parse(Key.make(), new Key[]{ file });
        Key dest = Key.make("spirals2");
        for (boolean sparse : new boolean[]{ true, false }) {
            for (boolean col_major : new boolean[]{ false }) {
                if ((!sparse) && col_major)
                    continue;

                // build the model
                {
                    DeepLearning p = new DeepLearning();
                    p.seed = 47806;
                    p.epochs = 10000;
                    p.hidden = new int[]{ 100 };
                    p.sparse = sparse;
                    p.col_major = col_major;
                    p.activation = Activation.Tanh;
                    p.max_w2 = Float.POSITIVE_INFINITY;
                    p.l1 = 0;
                    p.l2 = 0;
                    p.initial_weight_distribution = InitialWeightDistribution.Normal;
                    p.initial_weight_scale = 2.5;
                    p.loss = Loss.CrossEntropy;
                    p.source = frame;
                    p.response = frame.lastVec();
                    p.validation = null;
                    p.score_interval = 2;
                    p.ignored_cols = null;
                    p.train_samples_per_iteration = 0;// sync once per period

                    p.quiet_mode = true;
                    p.fast_mode = true;
                    p.ignore_const_cols = true;
                    p.nesterov_accelerated_gradient = true;
                    p.classification = true;
                    p.diagnostics = true;
                    p.expert_mode = true;
                    p.score_training_samples = 1000;
                    p.score_validation_samples = 10000;
                    p.shuffle_training_data = false;
                    p.force_load_balance = false;
                    p.replicate_training_data = false;
                    p.destination_key = dest;
                    p.adaptive_rate = true;
                    p.reproducible = true;
                    p.rho = 0.99;
                    p.epsilon = 0.005;
                    p.invoke();
                }
                // score and check result
                {
                    DeepLearningModel mymodel = UKV.get(dest);
                    double error = mymodel.error();
                    if (error >= 0.025) {
                        Assert.fail((("Classification error is not less than 0.025, but " + error) + "."));
                    }
                    mymodel.delete();
                    mymodel.delete_best_model();
                }
            }
        }
        frame.delete();
    }
}

