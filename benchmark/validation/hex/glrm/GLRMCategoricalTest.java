package hex.glrm;


import DataInfo.TransformType;
import hex.DataInfo;
import hex.ModelMetrics;
import hex.genmodel.algos.glrm.GlrmInitialization;
import hex.genmodel.algos.glrm.GlrmLoss;
import hex.genmodel.algos.glrm.GlrmRegularizer;
import hex.glrm.GLRMModel.GLRMParameters;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.util.ArrayUtils;
import water.util.Log;


public class GLRMCategoricalTest extends TestUtil {
    public final double TOLERANCE = 1.0E-6;

    @Test
    public void testCategoricalIris() throws InterruptedException, ExecutionException {
        GLRMModel model = null;
        Frame train = null;
        try {
            train = parse_test_file(Key.make("iris.hex"), "smalldata/iris/iris_wheader.csv");
            GLRMParameters parms = new GLRMParameters();
            parms._train = train._key;
            parms._k = 4;
            parms._loss = GlrmLoss.Absolute;
            parms._init = GlrmInitialization.SVD;
            parms._transform = TransformType.NONE;
            parms._recover_svd = true;
            parms._max_iterations = 1000;
            model = trainModel().get();
            Log.info(((("Iteration " + (model._output._iterations)) + ": Objective value = ") + (model._output._objective)));
            model.score(train).delete();
            ModelMetricsGLRM mm = ((ModelMetricsGLRM) (ModelMetrics.getFromDKV(model, train)));
            Log.info(((("Numeric Sum of Squared Error = " + (mm._numerr)) + "\tCategorical Misclassification Error = ") + (mm._caterr)));
        } finally {
            if (train != null)
                train.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testCategoricalProstate() throws InterruptedException, ExecutionException {
        GLRMModel model = null;
        Frame train = null;
        final int[] cats = new int[]{ 1, 3, 4, 5 };// Categoricals: CAPSULE, RACE, DPROS, DCAPS

        try {
            Scope.enter();
            train = parse_test_file(Key.make("prostate.hex"), "smalldata/logreg/prostate.csv");
            for (int i = 0; i < (cats.length); i++)
                Scope.track(train.replace(cats[i], train.vec(cats[i]).toCategoricalVec()));

            train.remove("ID").remove();
            DKV.put(train._key, train);
            GLRMParameters parms = new GLRMParameters();
            parms._train = train._key;
            parms._k = 8;
            parms._gamma_x = parms._gamma_y = 0.1;
            parms._regularization_x = GlrmRegularizer.Quadratic;
            parms._regularization_y = GlrmRegularizer.Quadratic;
            parms._init = GlrmInitialization.PlusPlus;
            parms._transform = TransformType.STANDARDIZE;
            parms._recover_svd = false;
            parms._max_iterations = 200;
            model = trainModel().get();
            Log.info(((("Iteration " + (model._output._iterations)) + ": Objective value = ") + (model._output._objective)));
            model.score(train).delete();
            ModelMetricsGLRM mm = ((ModelMetricsGLRM) (ModelMetrics.getFromDKV(model, train)));
            Log.info(((("Numeric Sum of Squared Error = " + (mm._numerr)) + "\tCategorical Misclassification Error = ") + (mm._caterr)));
        } finally {
            if (train != null)
                train.delete();

            if (model != null)
                model.delete();

            Scope.exit();
        }
    }

    @Test
    public void testLosses() throws InterruptedException, ExecutionException {
        long seed = 912559;
        Random rng = new Random(seed);
        Frame train = null;
        final int[] cats = new int[]{ 1, 3, 4, 5 };// Categoricals: CAPSULE, RACE, DPROS, DCAPS

        final GlrmRegularizer[] regs = new GlrmRegularizer[]{ GlrmRegularizer.Quadratic, GlrmRegularizer.L1, GlrmRegularizer.NonNegative, GlrmRegularizer.OneSparse, GlrmRegularizer.UnitOneSparse, GlrmRegularizer.Simplex };
        Scope.enter();
        try {
            train = parse_test_file(Key.make("prostate.hex"), "smalldata/logreg/prostate.csv");
            for (int i = 0; i < (cats.length); i++)
                Scope.track(train.replace(cats[i], train.vec(cats[i]).toCategoricalVec()));

            train.remove("ID").remove();
            DKV.put(train._key, train);
            for (GlrmLoss loss : new GlrmLoss[]{ GlrmLoss.Quadratic, GlrmLoss.Absolute, GlrmLoss.Huber, GlrmLoss.Poisson }) {
                for (GlrmLoss multiloss : new GlrmLoss[]{ GlrmLoss.Categorical, GlrmLoss.Ordinal }) {
                    GLRMModel model = null;
                    try {
                        Scope.enter();
                        long myseed = rng.nextLong();
                        Log.info(("GLRM using seed = " + myseed));
                        GLRMParameters parms = new GLRMParameters();
                        parms._train = train._key;
                        parms._transform = TransformType.NONE;
                        parms._k = 5;
                        parms._loss = loss;
                        parms._multi_loss = multiloss;
                        parms._init = GlrmInitialization.SVD;
                        parms._regularization_x = regs[rng.nextInt(regs.length)];
                        parms._regularization_y = regs[rng.nextInt(regs.length)];
                        parms._gamma_x = Math.abs(rng.nextDouble());
                        parms._gamma_y = Math.abs(rng.nextDouble());
                        parms._recover_svd = false;
                        parms._seed = myseed;
                        parms._verbose = false;
                        parms._max_iterations = 500;
                        model = trainModel().get();
                        Log.info(((("Iteration " + (model._output._iterations)) + ": Objective value = ") + (model._output._objective)));
                        model.score(train).delete();
                        ModelMetricsGLRM mm = ((ModelMetricsGLRM) (ModelMetrics.getFromDKV(model, train)));
                        Log.info(((("Numeric Sum of Squared Error = " + (mm._numerr)) + "\tCategorical Misclassification Error = ") + (mm._caterr)));
                    } finally {
                        if (model != null)
                            model.delete();

                        Scope.exit();
                    }
                }
            }
        } finally {
            if (train != null)
                train.delete();

            Scope.exit();
        }
    }

    @Test
    public void testSetColumnLossCats() throws InterruptedException, ExecutionException {
        GLRMModel model = null;
        Frame train = null;
        final int[] cats = new int[]{ 1, 3, 4, 5 };// Categoricals: CAPSULE, RACE, DPROS, DCAPS

        Scope.enter();
        try {
            train = parse_test_file(Key.make("prostate.hex"), "smalldata/logreg/prostate.csv");
            for (int i = 0; i < (cats.length); i++)
                Scope.track(train.replace(cats[i], train.vec(cats[i]).toCategoricalVec()));

            train.remove("ID").remove();
            DKV.put(train._key, train);
            GLRMParameters parms = new GLRMParameters();
            parms._train = train._key;
            parms._k = 12;
            parms._loss = GlrmLoss.Quadratic;
            parms._multi_loss = GlrmLoss.Categorical;
            parms._loss_by_col = new GlrmLoss[]{ GlrmLoss.Ordinal, GlrmLoss.Poisson, GlrmLoss.Absolute };
            parms._loss_by_col_idx = new int[]{ 3/* DPROS */
            , 1/* AGE */
            , 6/* VOL */
             };
            parms._init = GlrmInitialization.PlusPlus;
            parms._min_step_size = 1.0E-5;
            parms._recover_svd = false;
            parms._max_iterations = 2000;
            model = trainModel().get();
            Log.info(((("Iteration " + (model._output._iterations)) + ": Objective value = ") + (model._output._objective)));
            GLRMTest.checkLossbyCol(parms, model);
            model.score(train).delete();
            ModelMetricsGLRM mm = ((ModelMetricsGLRM) (ModelMetrics.getFromDKV(model, train)));
            Log.info(((("Numeric Sum of Squared Error = " + (mm._numerr)) + "\tCategorical Misclassification Error = ") + (mm._caterr)));
        } finally {
            if (train != null)
                train.delete();

            if (model != null)
                model.delete();

            Scope.exit();
        }
    }

    @Test
    public void testExpandCatsIris() throws InterruptedException, ExecutionException {
        double[][] iris = ard(ard(6.3, 2.5, 4.9, 1.5, 1), ard(5.7, 2.8, 4.5, 1.3, 1), ard(5.6, 2.8, 4.9, 2.0, 2), ard(5.0, 3.4, 1.6, 0.4, 0), ard(6.0, 2.2, 5.0, 1.5, 2));
        double[][] iris_expandR = ard(ard(0, 1, 0, 6.3, 2.5, 4.9, 1.5), ard(0, 1, 0, 5.7, 2.8, 4.5, 1.3), ard(0, 0, 1, 5.6, 2.8, 4.9, 2.0), ard(1, 0, 0, 5.0, 3.4, 1.6, 0.4), ard(0, 0, 1, 6.0, 2.2, 5.0, 1.5));
        String[] iris_cols = new String[]{ "sepal_len", "sepal_wid", "petal_len", "petal_wid", "class" };
        String[][] iris_domains = new String[][]{ null, null, null, null, new String[]{ "setosa", "versicolor", "virginica" } };
        Frame fr = null;
        try {
            fr = parse_test_file(Key.make("iris.hex"), "smalldata/iris/iris_wheader.csv");
            DataInfo dinfo = /* weights */
            /* offset */
            /* fold */
            new DataInfo(fr, null, 0, true, TransformType.NONE, TransformType.NONE, false, false, false, false, false, false);
            Log.info((("Original matrix:\n" + (GLRMCategoricalTest.colFormat(iris_cols, "%8.7s"))) + (ArrayUtils.pprint(iris))));
            double[][] iris_perm = ArrayUtils.permuteCols(iris, dinfo._permutation);
            Log.info((("Permuted matrix:\n" + (GLRMCategoricalTest.colFormat(iris_cols, "%8.7s", dinfo._permutation))) + (ArrayUtils.pprint(iris_perm))));
            double[][] iris_exp = GLRM.expandCats(iris_perm, dinfo);
            Log.info((("Expanded matrix:\n" + (GLRMCategoricalTest.colExpFormat(iris_cols, iris_domains, "%8.7s", dinfo._permutation))) + (ArrayUtils.pprint(iris_exp))));
            Assert.assertArrayEquals(iris_expandR, iris_exp);
        } finally {
            if (fr != null)
                fr.delete();

        }
    }

    @Test
    public void testExpandCatsProstate() throws InterruptedException, ExecutionException {
        double[][] prostate = ard(ard(0, 71, 1, 0, 0, 4.8, 14.0, 7), ard(1, 70, 1, 1, 0, 8.4, 21.8, 5), ard(0, 73, 1, 3, 0, 10.0, 27.4, 6), ard(1, 68, 1, 0, 0, 6.7, 16.7, 6));
        double[][] pros_expandR = ard(ard(1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 71, 4.8, 14.0, 7), ard(0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 70, 8.4, 21.8, 5), ard(0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 73, 10.0, 27.4, 6), ard(1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 68, 6.7, 16.7, 6));
        String[] pros_cols = new String[]{ "Capsule", "Age", "Race", "Dpros", "Dcaps", "PSA", "Vol", "Gleason" };
        String[][] pros_domains = new String[][]{ new String[]{ "No", "Yes" }, null, new String[]{ "Other", "White", "Black" }, new String[]{ "None", "UniLeft", "UniRight", "Bilobar" }, new String[]{ "No", "Yes" }, null, null, null };
        final int[] cats = new int[]{ 1, 3, 4, 5 };// Categoricals: CAPSULE, RACE, DPROS, DCAPS

        Frame fr = null;
        try {
            Scope.enter();
            fr = parse_test_file(Key.make("prostate.hex"), "smalldata/logreg/prostate.csv");
            for (int i = 0; i < (cats.length); i++)
                Scope.track(fr.replace(cats[i], fr.vec(cats[i]).toCategoricalVec()));

            fr.remove("ID").remove();
            DKV.put(fr._key, fr);
            DataInfo dinfo = /* weights */
            /* offset */
            /* fold */
            new DataInfo(fr, null, 0, true, TransformType.NONE, TransformType.NONE, false, false, false, false, false, false);
            Log.info((("Original matrix:\n" + (GLRMCategoricalTest.colFormat(pros_cols, "%8.7s"))) + (ArrayUtils.pprint(prostate))));
            double[][] pros_perm = ArrayUtils.permuteCols(prostate, dinfo._permutation);
            Log.info((("Permuted matrix:\n" + (GLRMCategoricalTest.colFormat(pros_cols, "%8.7s", dinfo._permutation))) + (ArrayUtils.pprint(pros_perm))));
            double[][] pros_exp = GLRM.expandCats(pros_perm, dinfo);
            Log.info((("Expanded matrix:\n" + (GLRMCategoricalTest.colExpFormat(pros_cols, pros_domains, "%8.7s", dinfo._permutation))) + (ArrayUtils.pprint(pros_exp))));
            Assert.assertArrayEquals(pros_expandR, pros_exp);
        } finally {
            if (fr != null)
                fr.delete();

            Scope.exit();
        }
    }
}

