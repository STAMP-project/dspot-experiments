package hex.example;


import ExampleModel.ExampleParameters;
import org.junit.Test;
import water.Job;
import water.TestUtil;
import water.fvec.Frame;


public class ExampleTest extends TestUtil {
    @Test
    public void testIris() {
        ExampleModel emm = null;
        Frame fr = null;
        try {
            long start = System.currentTimeMillis();
            System.out.println("Start Parse");
            fr = parse_test_file("smalldata/iris/iris_wheader.csv");
            System.out.println(("Done Parse: " + ((System.currentTimeMillis()) - start)));
            ExampleModel.ExampleParameters parms = new ExampleModel.ExampleParameters();
            parms._train = fr._key;
            parms._max_iterations = 10;
            parms._response_column = "class";
            Job<ExampleModel> job = trainModel();
            emm = job.get();
            job.remove();
        } finally {
            if (fr != null)
                fr.remove();

            if (emm != null)
                emm.delete();

        }
    }
}

