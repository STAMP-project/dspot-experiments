package hex.pca;


import DataInfo.TransformType;
import FrameUtils.MissingInserter;
import PCAParameters.Method;
import hex.DataInfo;
import hex.SplitFrame;
import hex.pca.PCAModel.PCAParameters;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import water.DKV;
import water.Key;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.ArrayUtils;
import water.util.FrameUtils;

import static PCAModel.PCAParameters.Method.GramSVD;
import static PCAModel.PCAParameters.Method.Randomized;


@RunWith(Parameterized.class)
public class PCATest extends TestUtil {
    public static final double TOLERANCE = 1.0E-6;

    private PCAParameters pcaParameters;

    @Parameterized.Parameter
    public hex.pca.PCAImplementation PCAImplementation;

    @Test
    public void testArrestsScoring() throws InterruptedException, ExecutionException {
        // Results with original training frame
        double[] stddev = new double[]{ 202.7230564, 27.8322637, 6.5230482, 2.5813652 };
        double[][] eigvec = ard(ard((-0.04239181), 0.01616262, (-0.06588426), 0.99679535), ard((-0.94395706), 0.3206858, 0.0665517, (-0.04094568)), ard((-0.30842767), (-0.93845891), 0.15496743, 0.01234261), ard((-0.10963744), (-0.12725666), (-0.98347101), (-0.06760284)));
        PCAModel model = null;
        Frame train = null;
        Frame score = null;
        Frame scoreR = null;
        try {
            train = parse_test_file(Key.make("arrests.hex"), "smalldata/pca_test/USArrests.csv");
            pcaParameters._train = train._key;
            pcaParameters._k = 4;
            pcaParameters._transform = TransformType.NONE;
            pcaParameters._pca_method = Method.GramSVD;
            model = trainModel().get();
            TestUtil.checkStddev(stddev, model._output._std_deviation, 1.0E-5);
            boolean[] flippedEig = TestUtil.checkEigvec(eigvec, model._output._eigenvectors, 1.0E-5);
            score = model.score(train);
            scoreR = parse_test_file(Key.make("scoreR.hex"), "smalldata/pca_test/USArrests_PCAscore.csv");
            // TestUtil.checkProjection(scoreR, score, TOLERANCE, flippedEig);    // Flipped cols must match those from eigenvectors
            TestUtil.checkProjection(scoreR, score, 1.0E-6, flippedEig);// Flipped cols must match those from eigenvectors

            // Build a POJO, validate same results
            Assert.assertTrue(model.testJavaScoring(train, score, 1.0E-5));
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
    public void testIrisSplitScoring() throws InterruptedException, ExecutionException {
        PCAModel model = null;
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
            pcaParameters._train = ksplits[0];
            pcaParameters._valid = ksplits[1];
            pcaParameters._k = 4;
            pcaParameters._max_iterations = 1000;
            pcaParameters._pca_method = Method.GramSVD;
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
    public void testImputeMissing() throws InterruptedException, ExecutionException {
        Frame train = null;
        double missing_fraction = 0.75;
        long seed = 12345;
        try {
            train = parse_test_file(Key.make("arrests.hex"), "smalldata/pca_test/USArrests.csv");
            // Add missing values to the training data
            if (missing_fraction > 0) {
                Frame frtmp = new Frame(Key.<Frame>make(), train.names(), train.vecs());
                DKV.put(frtmp._key, frtmp);// Need to put the frame (to be modified) into DKV for MissingInserter to pick up

                FrameUtils.MissingInserter j = new FrameUtils.MissingInserter(frtmp._key, seed, missing_fraction);
                j.execImpl().get();// MissingInserter is non-blocking, must block here explicitly

                DKV.remove(frtmp._key);// Delete the frame header (not the data)

            }
            pcaParameters._train = train._key;
            pcaParameters._k = 4;
            pcaParameters._transform = TransformType.NONE;
            pcaParameters._pca_method = GramSVD;
            pcaParameters._impute_missing = true;// Don't skip rows with NA entries, but impute using mean of column

            pcaParameters._seed = seed;
            PCAModel pca = null;
            pca = trainModel().get();
            if (pca != null)
                pca.remove();

        } finally {
            if (train != null)
                train.delete();

        }
    }

    /* Make sure POJO works if the model is only built from categorical variables (no numeric columns) */
    @Test
    public void testCatOnlyPUBDEV3988() throws InterruptedException, ExecutionException {
        PCAModel model = null;
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
            pcaParameters._train = train._key;
            pcaParameters._k = 2;
            pcaParameters._transform = TransformType.STANDARDIZE;
            pcaParameters._use_all_factor_levels = true;
            pcaParameters._pca_method = Method.GramSVD;
            pcaParameters._impute_missing = false;
            pcaParameters._seed = 12345;
            PCA pcaParms = new PCA(pcaParameters);
            model = pcaParms.trainModel().get();// get normal data

            score = model.score(train);
            // Build a POJO, check results with original PCA
            Assert.assertTrue(model.testJavaScoring(train, score, PCATest.TOLERANCE));
        } finally {
            if (train != null)
                train.delete();

            if (score != null)
                score.delete();

            if (model != null)
                model.delete();

        }
    }

    /* Quick test to make sure changes made to PCA for rank deficient matrices do not cause leakage. */
    @Test
    public void testPUBDEV3500NoLeakage() throws InterruptedException, ExecutionException {
        Scope.enter();
        Frame train = null;
        try {
            train = parse_test_file(Key.make("prostate_cat.hex"), "smalldata/prostate/prostate_cat.csv");
            Scope.track(train);
            pcaParameters._train = train._key;
            pcaParameters._k = 3;
            pcaParameters._transform = TransformType.NONE;
            pcaParameters._pca_method = Randomized;
            pcaParameters._impute_missing = true;// Don't skip rows with NA entries, but impute using mean of column

            pcaParameters._seed = 12345;
            pcaParameters._use_all_factor_levels = true;
            PCAModel pca = null;
            pca = trainModel().get();
            Scope.track_generic(pca);
            Assert.assertTrue(((pca._parms._k) == (pca._output._std_deviation.length)));
        } finally {
            Scope.exit();
        }
    }

    /* This test will make sure that when the test dataset contains columns that are different from
    the training dataset, a warning should be generated to warn the user.
     */
    @Test
    public void testIrisScoreWarning() throws InterruptedException, ExecutionException {
        PCAModel model = null;
        Frame fr = null;
        Frame fr2 = null;
        Frame tr = null;
        Frame te = null;
        Scope.enter();
        try {
            fr = parse_test_file("smalldata/iris/iris_wheader.csv");
            tr = parse_test_file("smalldata/iris/iris_wheader_bad_cnames.csv");
            Scope.track(fr);
            Scope.track(tr);
            PCAModel.PCAParameters parms = new PCAModel.PCAParameters();
            parms._train = fr._key;
            // parms._valid = tr._key;
            parms._k = 4;
            parms._max_iterations = 1000;
            parms._pca_method = Method.GramSVD;
            model = trainModel().get();
            Scope.track_generic(model);
            // Done building model; produce a score column with cluster choices
            fr2 = model.score(tr);
            Scope.track(fr2);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testArrests() throws InterruptedException, ExecutionException {
        // Results with de-meaned training frame
        double[] stddev = new double[]{ 83.7324, 14.212402, 6.489426, 2.48279 };
        double[][] eigvec = ard(ard(0.04170432, (-0.04482166), 0.07989066, (-0.99492173)), ard(0.99522128, (-0.05876003), (-0.06756974), 0.0389383), ard(0.04633575, 0.97685748, (-0.20054629), (-0.05816914)), ard(0.0751555, 0.20071807, 0.97408059, 0.07232502));
        // Results with standardized training frame
        double[] stddev_std = new double[]{ 1.5748783, 0.9948694, 0.5971291, 0.4164494 };
        double[][] eigvec_std = ard(ard((-0.5358995), 0.4181809, (-0.3412327), 0.6492278), ard((-0.5831836), 0.1879856, (-0.2681484), (-0.74340748)), ard((-0.2781909), (-0.8728062), (-0.3780158), 0.13387773), ard((-0.5434321), (-0.1673186), 0.8177779, 0.08902432));
        Frame train = null;
        try {
            train = parse_test_file(Key.make("arrests.hex"), "smalldata/pca_test/USArrests.csv");// TODO: Move this outside loop

            for (DataInfo.TransformType std : new DataInfo.TransformType[]{ TransformType.DEMEAN, TransformType.STANDARDIZE }) {
                PCAModel model = null;
                try {
                    pcaParameters._train = train._key;
                    pcaParameters._k = 4;
                    pcaParameters._transform = std;
                    pcaParameters._max_iterations = 1000;
                    pcaParameters._pca_method = Method.Power;
                    model = trainModel().get();
                    if (std == (TransformType.DEMEAN)) {
                        TestUtil.checkStddev(stddev, model._output._std_deviation, PCATest.TOLERANCE);
                        TestUtil.checkEigvec(eigvec, model._output._eigenvectors, PCATest.TOLERANCE);
                    } else
                        if (std == (TransformType.STANDARDIZE)) {
                            TestUtil.checkStddev(stddev_std, model._output._std_deviation, PCATest.TOLERANCE);
                            TestUtil.checkEigvec(eigvec_std, model._output._eigenvectors, PCATest.TOLERANCE);
                        }

                } finally {
                    if (model != null)
                        model.delete();

                }
            }
        } finally {
            if (train != null)
                train.delete();

        }
    }

    @Test
    public void testIrisScoring() throws InterruptedException, ExecutionException {
        // Results with original training frame
        double[] stddev = new double[]{ 7.88175203, 1.56002774, 0.59189816, 0.25917329, 0.15415273, 0.09381276, 0.0476859 };
        double[][] eigvec = ard(ard((-0.03169051), (-0.3230586), 0.185100382, (-0.12336685), (-0.14867156), 0.75932119, (-0.496462912)), ard((-0.04289677), 0.04037565, (-0.780961964), 0.19727933, 0.07251338, (-0.12216945), (-0.572298338)), ard((-0.05019689), 0.16836717, 0.551432201, (-0.07122329), 0.08454116, (-0.4832701), (-0.647522462)), ard((-0.74915107), (-0.2662942), (-0.101102186), (-0.48920057), 0.3245846, (-0.09176909), 0.067412858), ard((-0.37877011), (-0.5063606), 0.142219195, 0.69081642, (-0.26312992), (-0.17811871), 0.041411296), ard((-0.51177078), 0.65945159, (-0.005079934), 0.048819, (-0.52128288), 0.17038367, 0.006223427), ard((-0.16742875), 0.32166036, 0.145893901, 0.47102115, 0.72052968, 0.32523458, 0.020389463));
        PCAModel model = null;
        Frame train = null;
        Frame score = null;
        Frame scoreR = null;
        try {
            train = parse_test_file(Key.make("iris.hex"), "smalldata/iris/iris_wheader.csv");
            pcaParameters._train = train._key;
            pcaParameters._k = 7;
            pcaParameters._transform = TransformType.NONE;
            pcaParameters._use_all_factor_levels = true;
            pcaParameters._pca_method = Method.Power;
            model = trainModel().get();
            TestUtil.checkStddev(stddev, model._output._std_deviation, 1.0E-5);
            boolean[] flippedEig = TestUtil.checkEigvec(eigvec, model._output._eigenvectors, 1.0E-5);
            score = model.score(train);
            scoreR = parse_test_file(Key.make("scoreR.hex"), "smalldata/pca_test/iris_PCAscore.csv");
            TestUtil.checkProjection(scoreR, score, PCATest.TOLERANCE, flippedEig);// Flipped cols must match those from eigenvectors

            // Build a POJO, validate same results
            Assert.assertTrue(model.testJavaScoring(train, score, 1.0E-5));
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
    public void testGram() {
        double[][] x = ard(ard(1, 2, 3), ard(4, 5, 6));
        double[][] xgram = ard(ard(17, 22, 27), ard(22, 29, 36), ard(27, 36, 45));// X'X

        double[][] xtgram = ard(ard(14, 32), ard(32, 77));// (X')'X' = XX'

        double[][] xgram_glrm = ArrayUtils.formGram(x, false);
        double[][] xtgram_glrm = ArrayUtils.formGram(x, true);
        Assert.assertArrayEquals(xgram, xgram_glrm);
        Assert.assertArrayEquals(xtgram, xtgram_glrm);
    }
}

