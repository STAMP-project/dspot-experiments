package hex.glm;


import DataInfo.TransformType;
import DeepLearningModel.DeepLearningParameters.MissingValuesHandling;
import Family.multinomial;
import GLMParameters.Link;
import Vec.Writer;
import hex.CreateFrame;
import hex.DataInfo;
import hex.ModelMetricsBinomialGLM.ModelMetricsMultinomialGLM;
import hex.SplitFrame;
import hex.glm.GLMModel.GLMParameters;
import hex.glm.GLMModel.GLMParameters.Family;
import hex.glm.GLMModel.GLMParameters.Solver;
import java.util.Random;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import water.exceptions.H2OIllegalArgumentException;
import water.fvec.Frame;
import water.fvec.Vec;

import static water.TestUtil.<init>;


/**
 * Created by tomasnykodym on 10/28/15.
 */
public class GLMBasicTestMultinomial extends TestUtil {
    static Frame _covtype;

    static Frame _train;

    static Frame _test;

    double _tol = 1.0E-10;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testMultinomialPredMojoPojo() {
        try {
            Scope.enter();
            CreateFrame cf = new CreateFrame();
            Random generator = new Random();
            int numRows = ((generator.nextInt(10000)) + 15000) + 200;
            int numCols = (generator.nextInt(17)) + 3;
            int response_factors = (generator.nextInt(7)) + 3;
            cf.rows = numRows;
            cf.cols = numCols;
            cf.factors = 10;
            cf.has_response = true;
            cf.response_factors = response_factors;
            cf.positive_response = true;
            cf.missing_fraction = 0;
            cf.seed = System.currentTimeMillis();
            System.out.println(((((((("Createframe parameters: rows: " + numRows) + " cols:") + numCols) + " response number:") + response_factors) + " seed: ") + (cf.seed)));
            Frame trainMultinomial = Scope.track(cf.execImpl().get());
            SplitFrame sf = new SplitFrame(trainMultinomial, new double[]{ 0.8, 0.2 }, new Key[]{ Key.make("train.hex"), Key.make("test.hex") });
            sf.exec().get();
            Key[] ksplits = sf._destination_frames;
            Frame tr = DKV.get(ksplits[0]).get();
            Frame te = DKV.get(ksplits[1]).get();
            Scope.track(tr);
            Scope.track(te);
            GLMModel.GLMParameters paramsO = new GLMModel.GLMParameters(Family.multinomial, multinomial.defaultLink, new double[]{ 0 }, new double[]{ 0 }, 0, 0);
            paramsO._train = tr._key;
            paramsO._lambda_search = false;
            paramsO._response_column = "response";
            paramsO._lambda = new double[]{ 0 };
            paramsO._alpha = new double[]{ 0.001 };// l1pen

            paramsO._objective_epsilon = 1.0E-6;
            paramsO._beta_epsilon = 1.0E-4;
            paramsO._standardize = false;
            GLMModel model = trainModel().get();
            Scope.track_generic(model);
            Frame pred = model.score(te);
            Scope.track(pred);
            Assert.assertTrue(model.testJavaScoring(te, pred, _tol));
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testCovtypeNoIntercept() {
        GLMParameters params = new GLMParameters(Family.multinomial);
        GLMModel model = null;
        Frame preds = null;
        Vec weights = GLMBasicTestMultinomial._covtype.anyVec().makeCon(1);
        Key k = Key.<Frame>make("cov_with_weights");
        Frame f = new Frame(k, GLMBasicTestMultinomial._covtype.names(), GLMBasicTestMultinomial._covtype.vecs());
        f.add("weights", weights);
        DKV.put(f);
        try {
            params._response_column = "C55";
            params._train = k;
            params._valid = GLMBasicTestMultinomial._covtype._key;
            params._objective_epsilon = 1.0E-6;
            params._beta_epsilon = 1.0E-4;
            params._weights_column = "weights";
            params._missing_values_handling = MissingValuesHandling.Skip;
            params._intercept = false;
            double[] alpha = new double[]{ 0, 0.5, 0.1 };
            Solver s = Solver.L_BFGS;
            System.out.println(("solver = " + s));
            params._solver = s;
            params._max_iterations = 5000;
            for (int i = 0; i < (alpha.length); ++i) {
                params._alpha = new double[]{ alpha[i] };
                // params._lambda[0] = lambda[i];
                model = trainModel().get();
                System.out.println(model.coefficients());
                // Assert.assertEquals(0,model.coefficients().get("Intercept"),0);
                double[][] bs = model._output.getNormBetaMultinomial();
                for (double[] b : bs)
                    Assert.assertEquals(0, b[((b.length) - 1)], 0);

                System.out.println(model._output._model_summary);
                System.out.println(model._output._training_metrics);
                System.out.println(model._output._validation_metrics);
                preds = model.score(GLMBasicTestMultinomial._covtype);
                ModelMetricsMultinomialGLM mmTrain = ((ModelMetricsMultinomialGLM) (hex.ModelMetricsMultinomial.getFromDKV(model, GLMBasicTestMultinomial._covtype)));
                Assert.assertTrue(model._output._training_metrics.equals(mmTrain));
                model.delete();
                model = null;
                preds.delete();
                preds = null;
            }
        } finally {
            weights.remove();
            DKV.remove(k);
            if (model != null)
                model.delete();

            if (preds != null)
                preds.delete();

        }
    }

    @Test
    public void testCovtypeBasic() {
        GLMParameters params = new GLMParameters(Family.multinomial);
        GLMModel model = null;
        Frame preds = null;
        Vec weights = GLMBasicTestMultinomial._covtype.anyVec().makeCon(1);
        Key k = Key.<Frame>make("cov_with_weights");
        Frame f = new Frame(k, GLMBasicTestMultinomial._covtype.names(), GLMBasicTestMultinomial._covtype.vecs());
        f.add("weights", weights);
        DKV.put(f);
        try {
            params._response_column = "C55";
            params._train = k;
            params._valid = GLMBasicTestMultinomial._covtype._key;
            params._lambda = new double[]{ 4.881E-5 };
            params._alpha = new double[]{ 1 };
            params._objective_epsilon = 1.0E-6;
            params._beta_epsilon = 1.0E-4;
            params._weights_column = "weights";
            params._missing_values_handling = MissingValuesHandling.Skip;
            double[] alpha = new double[]{ 1 };
            double[] expected_deviance = new double[]{ 25499.76 };
            double[] lambda = new double[]{ 2.54475E-5 };
            for (Solver s : new Solver[]{ Solver.IRLSM, Solver.COORDINATE_DESCENT, Solver.L_BFGS }) {
                System.out.println(("solver = " + s));
                params._solver = s;
                params._max_iterations = ((params._solver) == (Solver.L_BFGS)) ? 300 : 10;
                for (int i = 0; i < (alpha.length); ++i) {
                    params._alpha[0] = alpha[i];
                    params._lambda[0] = lambda[i];
                    model = trainModel().get();
                    System.out.println(model._output._model_summary);
                    System.out.println(model._output._training_metrics);
                    System.out.println(model._output._validation_metrics);
                    Assert.assertTrue(model._output._training_metrics.equals(model._output._validation_metrics));
                    Assert.assertTrue(((((ModelMetricsMultinomialGLM) (model._output._training_metrics))._resDev) <= ((expected_deviance[i]) * 1.1)));
                    preds = model.score(GLMBasicTestMultinomial._covtype);
                    ModelMetricsMultinomialGLM mmTrain = ((ModelMetricsMultinomialGLM) (hex.ModelMetricsMultinomial.getFromDKV(model, GLMBasicTestMultinomial._covtype)));
                    Assert.assertTrue(model._output._training_metrics.equals(mmTrain));
                    model.delete();
                    model = null;
                    preds.delete();
                    preds = null;
                }
            }
        } finally {
            weights.remove();
            DKV.remove(k);
            if (model != null)
                model.delete();

            if (preds != null)
                preds.delete();

        }
    }

    /* I have manually derived the coefficient updates for COD and they are more accurate than what is currently
    implemented because I update all the probabilities after a coefficient has been changed.  In reality, this will
    be very slow and an approximation may be more appropriate.  The coefficients generated here is the golden standard.
     */
    @Test
    public void testCODGradients() {
        Scope.enter();
        Frame train;
        GLMParameters params = new GLMParameters(Family.multinomial);
        GLMModel model = null;
        double[] oldGLMCoeffs = new double[]{ 0.059094274726151426, 0.013361781886804975, -0.00798977427248744, 0.007467359562151555, 0.06737827548293934, -1.002393430927568, -0.04066511294457045, -0.018960901996125427, 0.07330281133353159, -0.02285669809606731, 0.002805290931441751, -1.1394632268347782, 0.021976767313534512, 0.01013967640490087, -0.03999288928633559, 0.012385348397898913, -0.0017922461738315199, -1.159667420372168 };
        try {
            train = parse_test_file("smalldata/glm_test/multinomial_3_class.csv");
            Scope.track(train);
            params._response_column = "response";
            params._train = train._key;
            params._lambda = new double[]{ 4.881E-5 };
            params._alpha = new double[]{ 0.5 };
            params._objective_epsilon = 1.0E-6;
            params._beta_epsilon = 1.0E-4;
            params._max_iterations = 1;// one iteration

            params._seed = 12345;// don't think this one matters but set it anyway

            Solver s = Solver.COORDINATE_DESCENT;
            System.out.println(("solver = " + s));
            params._solver = s;
            model = trainModel().get();
            Scope.track_generic(model);
            DataInfo tinfo = /* weights */
            /* offset */
            /* fold */
            new DataInfo(train.clone(), null, 0, true, TransformType.STANDARDIZE, TransformType.NONE, false, false, false, false, false, false);
            double[] manualCoeff = getCODCoeff(train, params._alpha[0], params._lambda[0], model._ymu, tinfo);
            Scope.track_generic(tinfo);
            compareGLMCoeffs(manualCoeff, model._output._submodels[0].beta, 0.02);// compare two sets of coeffs

            compareGLMCoeffs(model._output._submodels[0].beta, oldGLMCoeffs, 1.0E-10);// compare to original GLM

        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testCovtypeMinActivePredictors() {
        GLMParameters params = new GLMParameters(Family.multinomial);
        GLMModel model = null;
        Frame preds = null;
        try {
            params._response_column = "C55";
            params._train = GLMBasicTestMultinomial._covtype._key;
            params._valid = GLMBasicTestMultinomial._covtype._key;
            params._lambda = new double[]{ 4.881E-5 };
            params._alpha = new double[]{ 1 };
            params._objective_epsilon = 1.0E-6;
            params._beta_epsilon = 1.0E-4;
            params._max_active_predictors = 50;
            params._max_iterations = 10;
            double[] alpha = new double[]{ 0.99 };
            double expected_deviance = 33000;
            double[] lambda = new double[]{ 2.54475E-5 };
            Solver s = Solver.COORDINATE_DESCENT;
            System.out.println(("solver = " + s));
            params._solver = s;
            model = trainModel().get();
            System.out.println(model._output._model_summary);
            System.out.println(model._output._training_metrics);
            System.out.println(model._output._validation_metrics);
            System.out.println(((("rank = " + (model._output.rank())) + ", max active preds = ") + ((params._max_active_predictors) + (model._output.nclasses()))));
            Assert.assertTrue(((model._output.rank()) <= ((params._max_active_predictors) + (model._output.nclasses()))));
            Assert.assertTrue(model._output._training_metrics.equals(model._output._validation_metrics));
            Assert.assertTrue(((((ModelMetricsMultinomialGLM) (model._output._training_metrics))._resDev) <= (expected_deviance * 1.1)));
            preds = model.score(GLMBasicTestMultinomial._covtype);
            ModelMetricsMultinomialGLM mmTrain = ((ModelMetricsMultinomialGLM) (hex.ModelMetricsMultinomial.getFromDKV(model, GLMBasicTestMultinomial._covtype)));
            Assert.assertTrue(model._output._training_metrics.equals(mmTrain));
            model.delete();
            model = null;
            preds.delete();
            preds = null;
        } finally {
            if (model != null)
                model.delete();

            if (preds != null)
                preds.delete();

        }
    }

    @Test
    public void testCovtypeLS() {
        GLMParameters params = new GLMParameters(Family.multinomial);
        GLMModel model = null;
        Frame preds = null;
        try {
            double expected_deviance = 33000;
            params._nlambdas = 3;
            params._response_column = "C55";
            params._train = GLMBasicTestMultinomial._covtype._key;
            params._valid = GLMBasicTestMultinomial._covtype._key;
            params._alpha = new double[]{ 0.99 };
            params._objective_epsilon = 1.0E-6;
            params._beta_epsilon = 1.0E-4;
            params._max_active_predictors = 50;
            params._max_iterations = 500;
            params._solver = Solver.AUTO;
            params._lambda_search = true;
            model = trainModel().get();
            System.out.println(model._output._training_metrics);
            System.out.println(model._output._validation_metrics);
            Assert.assertTrue(model._output._training_metrics.equals(model._output._validation_metrics));
            preds = model.score(GLMBasicTestMultinomial._covtype);
            ModelMetricsMultinomialGLM mmTrain = ((ModelMetricsMultinomialGLM) (hex.ModelMetricsMultinomial.getFromDKV(model, GLMBasicTestMultinomial._covtype)));
            Assert.assertTrue(model._output._training_metrics.equals(mmTrain));
            Assert.assertTrue(((((ModelMetricsMultinomialGLM) (model._output._training_metrics))._resDev) <= expected_deviance));
            System.out.println(model._output._model_summary);
            model.delete();
            model = null;
            preds.delete();
            preds = null;
        } finally {
            if (model != null)
                model.delete();

            if (preds != null)
                preds.delete();

        }
    }

    @Test
    public void testCovtypeNAs() {
        GLMParameters params = new GLMParameters(Family.multinomial);
        GLMModel model = null;
        Frame preds = null;
        Frame covtype_subset = null;
        Frame covtype_copy = null;
        try {
            double expected_deviance = 26000;
            covtype_copy = GLMBasicTestMultinomial._covtype.deepCopy("covtype_copy");
            DKV.put(covtype_copy);
            Vec.Writer w = covtype_copy.vec(54).open();
            w.setNA(10);
            w.setNA(20);
            w.setNA(30);
            w.close();
            covtype_subset = new Frame(Key.<Frame>make("covtype_subset"), new String[]{ "C51", "C52", "C53", "C54", "C55" }, covtype_copy.vecs(new int[]{ 50, 51, 52, 53, 54 }));
            DKV.put(covtype_subset);
            // params._nlambdas = 3;
            params._response_column = "C55";
            params._train = covtype_copy._key;
            params._valid = covtype_copy._key;
            params._alpha = new double[]{ 0.99 };
            params._objective_epsilon = 1.0E-6;
            params._beta_epsilon = 1.0E-4;
            params._max_active_predictors = 50;
            params._max_iterations = 500;
            params._solver = Solver.L_BFGS;
            params._missing_values_handling = MissingValuesHandling.Skip;
            // params._lambda_search = true;
            model = trainModel().get();
            Assert.assertEquals((((covtype_copy.numRows()) - 3) - 1), model._nullDOF);
            System.out.println(model._output._training_metrics);
            System.out.println(model._output._validation_metrics);
            Assert.assertTrue(model._output._training_metrics.equals(model._output._validation_metrics));
            preds = model.score(covtype_copy);
            ModelMetricsMultinomialGLM mmTrain = ((ModelMetricsMultinomialGLM) (hex.ModelMetricsMultinomial.getFromDKV(model, covtype_copy)));
            Assert.assertTrue(model._output._training_metrics.equals(mmTrain));
            Assert.assertTrue(((((ModelMetricsMultinomialGLM) (model._output._training_metrics))._resDev) <= expected_deviance));
            System.out.println(model._output._model_summary);
            model.delete();
            model = null;
            preds.delete();
            preds = null;
            // now run the same on the subset
            params._train = covtype_subset._key;
            model = trainModel().get();
            Assert.assertEquals((((covtype_copy.numRows()) - 3) - 1), model._nullDOF);
            System.out.println(model._output._training_metrics);
            System.out.println(model._output._validation_metrics);
            Assert.assertTrue(model._output._training_metrics.equals(model._output._validation_metrics));
            preds = model.score(GLMBasicTestMultinomial._covtype);
            System.out.println(model._output._model_summary);
            Assert.assertTrue(((((ModelMetricsMultinomialGLM) (model._output._training_metrics))._resDev) <= 66000));
            model.delete();
            model = null;
            preds.delete();
            preds = null;
        } finally {
            if (covtype_subset != null)
                covtype_subset.delete();

            if (covtype_copy != null)
                covtype_copy.delete();

            if (model != null)
                model.delete();

            if (preds != null)
                preds.delete();

        }
    }

    @Test
    public void testNaiveCoordinateDescent() {
        expectedException.expect(H2OIllegalArgumentException.class);
        expectedException.expectMessage("Naive coordinate descent is not supported for multinomial.");
        GLMParameters params = new GLMParameters(Family.multinomial);
        params._solver = Solver.COORDINATE_DESCENT_NAIVE;
        // Should throw exception with information about unsupported message
        new GLM(params);
    }

    @Test
    public void testNaiveCoordinateDescent_families() {
        GLMParameters params = new GLMParameters(Family.binomial);
        params._solver = Solver.COORDINATE_DESCENT_NAIVE;
        final Family[] families = new Family[]{ Family.binomial, Family.gaussian, Family.gamma, Family.tweedie, Family.poisson, Family.ordinal, Family.quasibinomial };
        GLMParameters[] linkingfuncs = new Link[]{ Link.logit, Link.identity, Link.log, Link.tweedie, Link.log, Link.ologit, Link.logit };
        for (int i = 0; i < (families.length); i++) {
            params._family = families[i];
            params._link = linkingfuncs[i];
            new GLM(params);
        }
    }
}

