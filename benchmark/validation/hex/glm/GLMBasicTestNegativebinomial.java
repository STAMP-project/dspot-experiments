package hex.glm;


import DataInfo.TransformType;
import GLMModel.GLMParameters;
import GLMModel.GLMParameters.Family;
import GLMModel.GLMParameters.Family.negativebinomial;
import GLMTask.GLMGradientTask;
import GLMTask.GLMIterationTask;
import hex.DataInfo;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import water.fvec.Frame;
import water.fvec.Vec;

import static water.TestUtil.<init>;


public class GLMBasicTestNegativebinomial extends TestUtil {
    // test and compare mojo/pojo/predict values
    @Test
    public void testMojoPojoPredict() {
        GLMModel model;
        Frame tfr;
        Frame pred;
        try {
            Scope.enter();
            tfr = createData(5000, 11, 0.4, 0.5, 0);
            Vec v = tfr.remove("response");
            tfr.add("response", v.toNumericVec());
            Scope.track(v);
            Scope.track(tfr);
            DKV.put(tfr);
            GLMModel.GLMParameters params = new GLMModel.GLMParameters(Family.negativebinomial, negativebinomial.defaultLink, new double[]{ 0 }, new double[]{ 0 }, 0, 0);
            params._train = tfr._key;
            params._lambda = new double[]{ 0 };
            params._use_all_factor_levels = true;
            params._standardize = false;
            params._theta = 0.5;
            params._response_column = "response";
            GLM glm = new GLM(params);
            model = glm.trainModel().get();
            pred = model.score(tfr);
            Scope.track_generic(pred);
            Scope.track_generic(model);
            Assert.assertTrue(model.testJavaScoring(tfr, pred, 1.0E-6));
        } finally {
            Scope.exit();
        }
    }

    // Test gradient/hessian/likelhood generation for negativebinomial with log link with the following paper:
    // Negative Binomial Regression, Chapter 326, NCSS.com
    // 
    // This test is abandoned for now until the bug in DataInfo is corrected.
    @Test
    public void testGradientLikelihoodTask() {
        GLMModel model;
        Frame tfr;
        DataInfo dinfo = null;
        try {
            Scope.enter();
            tfr = createData(500, 8, 0, 0.5, 0);
            Vec v = tfr.remove("response");
            Scope.track(tfr);
            DKV.put(tfr);
            Scope.track(v);
            tfr.add("response", v.toNumericVec());
            GLMModel.GLMParameters params = new GLMModel.GLMParameters(Family.negativebinomial, negativebinomial.defaultLink, new double[]{ 0 }, new double[]{ 0 }, 0, 0);
            params._train = tfr._key;
            params._lambda = new double[]{ 0 };
            params._use_all_factor_levels = true;
            params._standardize = false;
            params._theta = 0.5;
            params._response_column = "response";
            params._obj_reg = 1.0;
            dinfo = new DataInfo(tfr, null, 1, ((params._use_all_factor_levels) || (params._lambda_search)), (params._standardize ? TransformType.STANDARDIZE : TransformType.NONE), TransformType.NONE, true, false, false, false, false, false);
            DKV.put(dinfo._key, dinfo);
            Scope.track_generic(dinfo);
            // randomly generate GLM coefficients
            int betaSize = (dinfo.fullN()) + 1;
            double[] beta = MemoryManager.malloc8d(betaSize);
            Random rnd = new Random(987654321);
            for (int i = 0; i < (beta.length); ++i)
                beta[i] = 1 - (2 * (rnd.nextDouble()));

            // invoke Gradient task to calculate GLM gradients, hessian and likelihood
            GLMTask.GLMGradientTask grtGen = new GLMTask.GLMNegativeBinomialGradientTask(null, dinfo, params, params._lambda[0], beta).doAll(dinfo._adaptedFrame);
            // invoke task to generate the gradient and the hessian
            GLMTask.GLMIterationTask heg = new GLMTask.GLMIterationTask(null, dinfo, new hex.glm.GLMModel.GLMWeightsFun(params), beta).doAll(dinfo._adaptedFrame);
            double[][] hessian = new double[betaSize][];
            for (int i = 0; i < betaSize; i++) {
                hessian[i] = new double[betaSize];
            }
            double[] gradient = new double[betaSize];
            double manualLLH = manualGradientNHess(tfr, beta, hessian, gradient, params._theta);
            Assert.assertTrue("Likelihood from GLMIterationTask and GLMGradientTask should equal but not...", ((Math.abs(((grtGen._likelihood) - (heg._likelihood)))) < 1.0E-10));
            Assert.assertTrue("Likelihood from GLMIterationTask and Manual calculation should equal but not...", ((Math.abs(((grtGen._likelihood) - manualLLH))) < 1.0E-10));
            compareArrays(grtGen._gradient, gradient, 1.0E-10, true);// compare gradients

            double[][] hess = heg.getGram().getXX();
            for (int index = 0; index < betaSize; index++)
                compareArrays(hess[index], hessian[index], 1.0E-10, false);

        } finally {
            Scope.exit();
            DKV.remove(dinfo._key);
            if (dinfo != null)
                dinfo.remove();

        }
    }
}

