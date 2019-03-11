package hex.deeplearning;


import org.junit.Ignore;
import org.junit.Test;
import water.TestUtil;


public class DeepLearningProstateTest extends TestUtil {
    @Test
    public void run() throws Exception {
        runFraction(2.0E-5F);
    }

    public static class Mid extends DeepLearningProstateTest {
        @Test
        @Ignore
        public void run() throws Exception {
            runFraction(0.01F);
        }// for nightly tests

    }

    public static class Short extends DeepLearningProstateTest {
        @Test
        @Ignore
        public void run() throws Exception {
            runFraction(0.001F);
        }
    }
}

