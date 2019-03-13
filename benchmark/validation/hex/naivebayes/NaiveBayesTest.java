package hex.naivebayes;


import hex.SplitFrame;
import hex.naivebayes.NaiveBayesModel.NaiveBayesParameters;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;


public class NaiveBayesTest extends TestUtil {
    @Test
    public void testIris() throws InterruptedException, ExecutionException {
        NaiveBayesModel model = null;
        Frame train = null;
        Frame score = null;
        try {
            train = parse_test_file(Key.make("iris_wheader.hex"), "smalldata/iris/iris_wheader.csv");
            NaiveBayesParameters parms = new NaiveBayesParameters();
            parms._train = train._key;
            parms._laplace = 0;
            parms._response_column = train._names[4];
            parms._compute_metrics = false;
            model = trainModel().get();
            // Done building model; produce a score column with class assignments
            score = model.score(train);
            Assert.assertTrue(model.testJavaScoring(train, score, 1.0E-6));
        } finally {
            if (train != null)
                train.delete();

            if (score != null)
                score.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testIrisValidation() throws InterruptedException, ExecutionException {
        NaiveBayesModel model = null;
        Frame fr = null;
        Frame fr2 = null;
        Frame tr = null;
        Frame te = null;
        try {
            fr = parse_test_file("smalldata/iris/iris_wheader.csv");
            SplitFrame sf = new SplitFrame(fr, new double[]{ 0.5, 0.5 }, new Key[]{ Key.make("train.hex"), Key.make("test.hex") });
            // Invoke the job
            sf.exec().get();
            Key[] ksplits = sf._destination_frames;
            tr = DKV.get(ksplits[0]).get();
            te = DKV.get(ksplits[1]).get();
            NaiveBayesParameters parms = new NaiveBayesParameters();
            parms._train = ksplits[0];
            parms._valid = ksplits[1];
            parms._laplace = 0.01;// Need Laplace smoothing

            parms._response_column = fr._names[4];
            parms._compute_metrics = true;
            model = trainModel().get();
            // Done building model; produce a score column with class assignments
            fr2 = model.score(te);
            Assert.assertTrue(model.testJavaScoring(te, fr2, 1.0E-6));
        } finally {
            if (fr != null)
                fr.delete();

            if (fr2 != null)
                fr2.delete();

            if (tr != null)
                tr.delete();

            if (te != null)
                te.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testProstate() throws InterruptedException, ExecutionException {
        NaiveBayesModel model = null;
        Frame train = null;
        Frame score = null;
        final int[] cats = new int[]{ 1, 3, 4, 5 };// Categoricals: CAPSULE, RACE, DPROS, DCAPS

        try {
            Scope.enter();
            train = parse_test_file(Key.make("prostate.hex"), "smalldata/logreg/prostate.csv");
            for (int i = 0; i < (cats.length); i++)
                Scope.track(train.replace(cats[i], train.vec(cats[i]).toCategoricalVec()));

            train.remove("ID").remove();
            DKV.put(train._key, train);
            NaiveBayesParameters parms = new NaiveBayesParameters();
            parms._train = train._key;
            parms._laplace = 0;
            parms._response_column = train._names[0];
            parms._compute_metrics = true;
            model = trainModel().get();
            // Done building model; produce a score column with class assignments
            score = model.score(train);
            Assert.assertTrue(model.testJavaScoring(train, score, 1.0E-6));
        } finally {
            if (train != null)
                train.delete();

            if (score != null)
                score.delete();

            if (model != null)
                model.delete();

            Scope.exit();
        }
    }

    @Test
    public void testCovtype() throws InterruptedException, ExecutionException {
        NaiveBayesModel model = null;
        Frame train = null;
        Frame score = null;
        try {
            Scope.enter();
            train = parse_test_file(Key.make("covtype.hex"), "smalldata/covtype/covtype.20k.data");
            Scope.track(train.replace(54, train.vecs()[54].toCategoricalVec()));// Change response to categorical

            DKV.put(train);
            NaiveBayesParameters parms = new NaiveBayesParameters();
            parms._train = train._key;
            parms._laplace = 0;
            parms._response_column = train._names[54];
            parms._compute_metrics = false;
            model = trainModel().get();
            // Done building model; produce a score column with class assignments
            score = model.score(train);
            Assert.assertTrue(model.testJavaScoring(train, score, 1.0E-6));
        } finally {
            if (train != null)
                train.delete();

            if (score != null)
                score.delete();

            if (model != null)
                model.delete();

            Scope.exit();
        }
    }
}

