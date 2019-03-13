package hex.deeplearning;


import org.junit.Ignore;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;


public class DeepLearningIrisTest extends TestUtil {
    static final String PATH = "smalldata/iris/iris.csv";

    Frame _train;

    Frame _test;

    // Default run is the short run
    @Test
    public void run() throws Exception {
        runFraction(0.05F);
    }

    public static class Long extends DeepLearningIrisTest {
        @Test
        @Ignore
        public void run() throws Exception {
            runFraction(0.1F);
        }
    }

    public static class Short extends DeepLearningIrisTest {
        @Test
        @Ignore
        public void run() throws Exception {
            runFraction(0.05F);
        }
    }
}

