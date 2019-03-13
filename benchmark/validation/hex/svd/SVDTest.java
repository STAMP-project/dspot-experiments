package hex.svd;


import DataInfo.TransformType;
import FrameUtils.MissingInserter;
import SVDParameters.Method;
import hex.SplitFrame;
import hex.svd.SVDModel.SVDParameters;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.FrameUtils;


public class SVDTest extends TestUtil {
    public static final double TOLERANCE = 1.0E-6;

    @Test
    public void testArrests() throws InterruptedException, ExecutionException {
        // Expected right singular values and vectors
        double[] d_expected = new double[]{ 1419.0613951, 194.82584611, 45.66133763, 18.06955662 };
        double[][] v_expected = ard(ard((-0.04239181), 0.01616262, (-0.06588426), 0.99679535), ard((-0.94395706), 0.3206858, 0.0665517, (-0.04094568)), ard((-0.30842767), (-0.93845891), 0.15496743, 0.01234261), ard((-0.10963744), (-0.12725666), (-0.98347101), (-0.06760284)));
        SVDModel model = null;
        Frame train = null;
        try {
            train = parse_test_file(Key.make("arrests.hex"), "smalldata/pca_test/USArrests.csv");
            SVDModel.SVDParameters parms = new SVDModel.SVDParameters();
            parms._train = train._key;
            parms._nv = 4;
            parms._seed = 1234;
            parms._only_v = false;
            parms._transform = TransformType.NONE;
            parms._svd_method = Method.GramSVD;
            parms._save_v_frame = false;
            model = trainModel().get();
            TestUtil.checkEigvec(v_expected, model._output._v, SVDTest.TOLERANCE);
            Assert.assertArrayEquals(d_expected, model._output._d, SVDTest.TOLERANCE);
        } finally {
            if (train != null)
                train.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testArrestsOnlyV() throws InterruptedException, ExecutionException {
        // Expected right singular vectors
        double[][] svec = ard(ard((-0.04239181), 0.01616262, (-0.06588426), 0.99679535), ard((-0.94395706), 0.3206858, 0.0665517, (-0.04094568)), ard((-0.30842767), (-0.93845891), 0.15496743, 0.01234261), ard((-0.10963744), (-0.12725666), (-0.98347101), (-0.06760284)));
        SVDModel model = null;
        Frame train = null;
        try {
            train = parse_test_file(Key.make("arrests.hex"), "smalldata/pca_test/USArrests.csv");
            SVDModel.SVDParameters parms = new SVDModel.SVDParameters();
            parms._train = train._key;
            parms._nv = 4;
            parms._seed = 1234;
            parms._only_v = true;
            parms._transform = TransformType.NONE;
            parms._svd_method = Method.Power;
            parms._save_v_frame = false;
            model = trainModel().get();
            TestUtil.checkEigvec(svec, model._output._v, SVDTest.TOLERANCE);
            assert (model._output._d) == null;
        } finally {
            if (train != null)
                train.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testArrestsScoring() throws InterruptedException, ExecutionException {
        double[] stddev = new double[]{ 202.7230564, 27.8322637, 6.5230482, 2.5813652 };
        double[][] eigvec = ard(ard((-0.04239181), 0.01616262, (-0.06588426), 0.99679535), ard((-0.94395706), 0.3206858, 0.0665517, (-0.04094568)), ard((-0.30842767), (-0.93845891), 0.15496743, 0.01234261), ard((-0.10963744), (-0.12725666), (-0.98347101), (-0.06760284)));
        SVDModel model = null;
        Frame train = null;
        Frame score = null;
        Frame scoreR = null;
        try {
            train = parse_test_file(Key.make("arrests.hex"), "smalldata/pca_test/USArrests.csv");
            SVDModel.SVDParameters parms = new SVDModel.SVDParameters();
            parms._train = train._key;
            parms._nv = 4;
            parms._transform = TransformType.NONE;
            parms._svd_method = Method.Power;
            parms._only_v = false;
            parms._keep_u = false;
            parms._save_v_frame = false;
            model = trainModel().get();
            boolean[] flippedEig = TestUtil.checkEigvec(eigvec, model._output._v, SVDTest.TOLERANCE);
            score = model.score(train);
            scoreR = parse_test_file(Key.make("scoreR.hex"), "smalldata/pca_test/USArrests_PCAscore.csv");
            TestUtil.checkProjection(scoreR, score, SVDTest.TOLERANCE, flippedEig);// Flipped cols must match those from eigenvectors

        } finally {
            if (train != null)
                train.delete();

            if (score != null)
                score.delete();

            if (scoreR != null)
                scoreR.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testArrestsProb() throws InterruptedException, ExecutionException {
        // Expected right singular values and vectors
        double[] d_expected = new double[]{ 11.024148, 6.964086, 4.179904, 2.915146 };
        double[][] v_expected = ard(ard((-0.5358995), 0.4181809, (-0.3412327), 0.6492278), ard((-0.5831836), 0.1879856, (-0.2681484), (-0.74340748)), ard((-0.2781909), (-0.8728062), (-0.3780158), 0.13387773), ard((-0.5434321), (-0.1673186), 0.8177779, 0.08902432));
        SVDModel model = null;
        Frame train = null;
        Frame score = null;
        try {
            train = parse_test_file(Key.make("arrests.hex"), "smalldata/pca_test/USArrests.csv");
            SVDModel.SVDParameters parms = new SVDModel.SVDParameters();
            parms._train = train._key;
            parms._nv = 4;
            parms._keep_u = true;
            parms._transform = TransformType.STANDARDIZE;
            parms._svd_method = Method.Randomized;
            parms._max_iterations = 4;
            parms._save_v_frame = false;
            model = trainModel().get();
            Assert.assertArrayEquals(d_expected, model._output._d, SVDTest.TOLERANCE);
            TestUtil.checkEigvec(v_expected, model._output._v, SVDTest.TOLERANCE);
            score = model.score(train);
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
    public void testIrisGram() throws InterruptedException, ExecutionException {
        // Expected right singular values and vectors
        double[] d_expected = new double[]{ 96.2090445, 19.0425654, 7.2250378, 3.1636131, 1.8816739, 1.1451307, 0.5820806 };
        double[][] v_expected = ard(ard((-0.03169051), (-0.3230586), 0.185100382, (-0.12336685), (-0.14867156), 0.75932119, (-0.496462912)), ard((-0.04289677), 0.04037565, (-0.780961964), 0.19727933, 0.07251338, (-0.12216945), (-0.572298338)), ard((-0.05019689), 0.16836717, 0.551432201, (-0.07122329), 0.08454116, (-0.4832701), (-0.647522462)), ard((-0.74915107), (-0.2662942), (-0.101102186), (-0.48920057), 0.3245846, (-0.09176909), 0.067412858), ard((-0.37877011), (-0.5063606), 0.142219195, 0.69081642, (-0.26312992), (-0.17811871), 0.041411296), ard((-0.51177078), 0.65945159, (-0.005079934), 0.048819, (-0.52128288), 0.17038367, 0.006223427), ard((-0.16742875), 0.32166036, 0.145893901, 0.47102115, 0.72052968, 0.32523458, 0.020389463));
        SVDModel model = null;
        Frame train = null;
        try {
            train = parse_test_file(Key.make("iris.hex"), "smalldata/iris/iris_wheader.csv");
            SVDModel.SVDParameters parms = new SVDModel.SVDParameters();
            parms._train = train._key;
            parms._nv = 7;
            parms._use_all_factor_levels = true;
            parms._keep_u = true;
            parms._transform = TransformType.NONE;
            parms._svd_method = Method.GramSVD;
            parms._save_v_frame = false;
            model = trainModel().get();
            TestUtil.checkEigvec(v_expected, model._output._v, SVDTest.TOLERANCE);
            Assert.assertArrayEquals(d_expected, model._output._d, SVDTest.TOLERANCE);
        } finally {
            if (train != null)
                train.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testIrisSVDScore() throws InterruptedException, ExecutionException {
        // Expected right singular values and vectors
        double[] d_expected = new double[]{ 96.2090445, 19.0425654, 7.2250378, 3.1636131, 1.8816739, 1.1451307, 0.5820806 };
        double[][] v_expected = ard(ard((-0.03169051), (-0.3230586), 0.185100382, (-0.12336685), (-0.14867156), 0.75932119, (-0.496462912)), ard((-0.04289677), 0.04037565, (-0.780961964), 0.19727933, 0.07251338, (-0.12216945), (-0.572298338)), ard((-0.05019689), 0.16836717, 0.551432201, (-0.07122329), 0.08454116, (-0.4832701), (-0.647522462)), ard((-0.74915107), (-0.2662942), (-0.101102186), (-0.48920057), 0.3245846, (-0.09176909), 0.067412858), ard((-0.37877011), (-0.5063606), 0.142219195, 0.69081642, (-0.26312992), (-0.17811871), 0.041411296), ard((-0.51177078), 0.65945159, (-0.005079934), 0.048819, (-0.52128288), 0.17038367, 0.006223427), ard((-0.16742875), 0.32166036, 0.145893901, 0.47102115, 0.72052968, 0.32523458, 0.020389463));
        SVDModel model = null;
        Frame train = null;
        Frame score = null;
        try {
            train = parse_test_file(Key.make("iris.hex"), "smalldata/iris/iris_wheader.csv");
            SVDModel.SVDParameters parms = new SVDModel.SVDParameters();
            parms._train = train._key;
            parms._nv = 7;
            parms._use_all_factor_levels = true;
            parms._transform = TransformType.NONE;
            parms._svd_method = Method.Power;
            parms._save_v_frame = false;
            model = trainModel().get();
            TestUtil.checkEigvec(v_expected, model._output._v, SVDTest.TOLERANCE);
            Assert.assertArrayEquals(d_expected, model._output._d, SVDTest.TOLERANCE);
            score = model.score(train);
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
    public void testIrisSplitScoring() throws InterruptedException, ExecutionException {
        SVDModel model = null;
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
            SVDModel.SVDParameters parms = new SVDModel.SVDParameters();
            parms._train = ksplits[0];
            parms._valid = ksplits[1];
            parms._nv = 4;
            parms._max_iterations = 1000;
            parms._svd_method = Method.Power;
            parms._save_v_frame = false;
            model = trainModel().get();
            // Done building model; produce a score column with cluster choices
            fr2 = model.score(te);
            Assert.assertTrue(model.testJavaScoring(te, fr2, 1.0E-5));
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
    public void testIrisProb() throws InterruptedException, ExecutionException {
        // Expected right singular values and vectors
        double[] d_expected = new double[]{ 96.2090445, 19.0425654, 7.2250378, 3.1636131, 1.8816739, 1.1451307, 0.5820806 };
        double[][] v_expected = ard(ard((-0.03169051), (-0.3230586), 0.185100382, (-0.12336685), (-0.14867156), 0.75932119, (-0.496462912)), ard((-0.04289677), 0.04037565, (-0.780961964), 0.19727933, 0.07251338, (-0.12216945), (-0.572298338)), ard((-0.05019689), 0.16836717, 0.551432201, (-0.07122329), 0.08454116, (-0.4832701), (-0.647522462)), ard((-0.74915107), (-0.2662942), (-0.101102186), (-0.48920057), 0.3245846, (-0.09176909), 0.067412858), ard((-0.37877011), (-0.5063606), 0.142219195, 0.69081642, (-0.26312992), (-0.17811871), 0.041411296), ard((-0.51177078), 0.65945159, (-0.005079934), 0.048819, (-0.52128288), 0.17038367, 0.006223427), ard((-0.16742875), 0.32166036, 0.145893901, 0.47102115, 0.72052968, 0.32523458, 0.020389463));
        SVDModel model = null;
        Frame train = null;
        Frame score = null;
        try {
            train = parse_test_file(Key.make("iris.hex"), "smalldata/iris/iris_wheader.csv");
            SVDModel.SVDParameters parms = new SVDModel.SVDParameters();
            parms._train = train._key;
            parms._nv = 7;
            parms._use_all_factor_levels = true;
            parms._keep_u = false;
            parms._transform = TransformType.NONE;
            parms._svd_method = Method.Randomized;
            parms._max_iterations = 7;
            parms._save_v_frame = false;
            model = trainModel().get();
            TestUtil.checkEigvec(v_expected, model._output._v, SVDTest.TOLERANCE);
            Assert.assertArrayEquals(d_expected, model._output._d, SVDTest.TOLERANCE);
            score = model.score(train);
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
    public void testBenignProb() throws InterruptedException, ExecutionException {
        // Expected singular values
        double[] d_expected = new double[]{ 450.809529, 212.934956, 155.60826, 64.528823, 52.334624 };
        SVDModel model = null;
        Frame train = null;
        Frame score = null;
        try {
            train = parse_test_file(Key.make("benign.hex"), "smalldata/logreg/benign.csv");
            SVDModel.SVDParameters parms = new SVDModel.SVDParameters();
            parms._train = train._key;
            parms._nv = 5;
            parms._keep_u = true;
            parms._transform = TransformType.DEMEAN;
            parms._svd_method = Method.Randomized;
            parms._impute_missing = true;
            parms._max_iterations = 20;
            parms._save_v_frame = false;
            model = trainModel().get();
            // Assert.assertArrayEquals(d_expected, model._output._d, 1e-4);
            score = model.score(train);
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
    public void testProstateMissingProb() throws InterruptedException, ExecutionException {
        long seed = 1234;
        Frame train = null;
        Frame score = null;
        SVDModel model = null;
        try {
            train = parse_test_file(Key.make("prostate.hex"), "smalldata/prostate/prostate_cat.csv");
            // Add missing values to the training data
            Frame frtmp = new Frame(Key.<Frame>make(), train.names(), train.vecs());
            DKV.put(frtmp._key, frtmp);// Need to put the frame (to be modified) into DKV for MissingInserter to pick up

            FrameUtils.MissingInserter j = new FrameUtils.MissingInserter(frtmp._key, seed, 0.25);
            j.execImpl().get();// MissingInserter is non-blocking, must block here explicitly

            DKV.remove(frtmp._key);// Delete the frame header (not the data)

            SVDParameters parms = new SVDParameters();
            parms._train = train._key;
            parms._nv = 8;
            parms._only_v = false;
            parms._keep_u = true;
            parms._svd_method = Method.Randomized;
            parms._impute_missing = true;
            parms._max_iterations = 20;
            parms._save_v_frame = false;
            model = trainModel().get();
            score = model.score(train);
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
    public void testIVVSum() {
        double[][] res = ard(ard(1, 2, 3), ard(2, 5, 6), ard(3, 6, 9));
        double[] v = new double[]{ 7, 8, 9 };
        double[][] xvv = ard(ard((-48), (-54), (-60)), ard((-54), (-59), (-66)), ard((-60), (-66), (-72)));
        SVD.updateIVVSum(res, v);
        Assert.assertArrayEquals(xvv, res);
    }

    /* Make sure POJO works if the model is only built from categorical variables (no numeric columns) */
    @Test
    public void testCatOnlyPUBDEV3988() throws InterruptedException, ExecutionException {
        SVDModel model = null;
        Frame train = null;
        Frame score = null;
        try {
            train = parse_test_file(Key.make("prostate_cat.hex"), "smalldata/prostate/prostate_cat.csv");
            for (int i = (train.numCols()) - 1; i > 0; i--) {
                Vec v = train.vec(i);
                if ((v.get_type()) != (Vec.T_CAT)) {
                    train.remove(i);
                    Vec.remove(v._key);
                }
            }
            DKV.put(train);
            SVDParameters parms = new SVDParameters();
            parms._train = train._key;
            parms._nv = 2;
            parms._only_v = false;
            parms._keep_u = true;
            parms._svd_method = Method.Randomized;
            parms._impute_missing = true;
            parms._max_iterations = 20;
            parms._save_v_frame = false;
            model = trainModel().get();
            score = model.score(train);
            // Build a POJO, check results with original SVD
            Assert.assertTrue(model.testJavaScoring(train, score, SVDTest.TOLERANCE));
        } finally {
            if (train != null)
                train.delete();

            if (score != null)
                score.delete();

            if (model != null)
                model.delete();

        }
    }
}

