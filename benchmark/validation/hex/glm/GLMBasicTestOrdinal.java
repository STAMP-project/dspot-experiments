package hex.glm;


import GLMModel.GLMParameters;
import GLMModel.GLMParameters.Family;
import GLMModel.GLMParameters.Family.ordinal;
import GLMModel.GLMParameters.Solver.AUTO;
import GLMModel.GLMParameters.Solver.GRADIENT_DESCENT_SQERR;
import java.util.Random;
import org.junit.Test;
import water.fvec.Frame;
import water.util.Log;

import static water.TestUtil.<init>;


// Want to test the following:
// 1. make sure gradient calculation is correct
// 2. for Binomial, compare ordinal result with the one for binomial
// 3. make sure h2o predict, mojo and pojo predict all agrees
public class GLMBasicTestOrdinal extends TestUtil {
    private static final double _tol = 1.0E-10;// threshold for comparison


    // Ordinal regression with class = 2 defaults to binomial.  Hence, they should have the same gradients at the
    // beginning of a run.
    @Test
    public void testCheckGradientBinomial() {
        try {
            Scope.enter();
            Frame trainBinomialEnum = parse_test_file("smalldata/glm_ordinal_logit/ordinal_binomial_training_set_enum_small.csv");
            convert2Enum(trainBinomialEnum, new int[]{ 0, 1, 2, 3, 4, 5, 6, 34 });// convert enum columns

            Frame trainBinomial = parse_test_file("smalldata/glm_ordinal_logit/ordinal_binomial_training_set_small.csv");
            convert2Enum(trainBinomial, new int[]{ 34 });
            Scope.track(trainBinomialEnum);
            Scope.track(trainBinomial);
            checkGradientWithBinomial(trainBinomial, 34, "C35");// only numerical columns

            checkGradientWithBinomial(trainBinomialEnum, 34, "C35");// with enum and numerical columns

        } finally {
            Scope.exit();
        }
    }

    // test and make sure the h2opredict, pojo and mojo predict agrees with multinomial dataset that includes
    // both enum and numerical datasets
    @Test
    public void testOrdinalPredMojoPojo() {
        testOrdinalMojoPojo(AUTO);
        testOrdinalMojoPojo(GRADIENT_DESCENT_SQERR);
    }

    // test ordinal regression with few iterations to make sure our gradient calculation and update is correct
    // for ordinals with multinomial data.  Ordinal regression coefficients are compared with ones calcluated using
    // alternate calculation without the distributed framework.  The datasets contains only numerical columns.
    @Test
    public void testOrdinalMultinomial() {
        try {
            Scope.enter();
            Frame trainMultinomial = Scope.track(parse_test_file("smalldata/glm_ordinal_logit/ordinal_multinomial_training_set_small.csv"));
            convert2Enum(trainMultinomial, new int[]{ 25 });
            final int iterNum = (new Random().nextInt(10)) + 2;// number of iterations to test

            Log.info(("testOrdinalMultinomial will use iterNum = " + iterNum));
            GLMModel.GLMParameters paramsO = new GLMModel.GLMParameters(Family.ordinal, ordinal.defaultLink, new double[]{ 0 }, new double[]{ 0 }, 0, 0);
            paramsO._train = trainMultinomial._key;
            paramsO._lambda_search = false;
            paramsO._response_column = "C26";
            paramsO._lambda = new double[]{ 1.0E-6 };// no regularization here

            paramsO._alpha = new double[]{ 1.0E-5 };// l1pen

            paramsO._objective_epsilon = 1.0E-6;
            paramsO._beta_epsilon = 1.0E-4;
            paramsO._max_iterations = iterNum;
            paramsO._standardize = false;
            paramsO._seed = 987654321;
            paramsO._obj_reg = 1.0E-7;
            GLMModel model = trainModel().get();
            Scope.track_generic(model);
            double[] interceptPDF = model._ymu;
            double[][] coeffs = model._output._global_beta_multinomial;// class by npred

            double[] beta = new double[(coeffs[0].length) - 1];// coefficients not including the intercepts

            double[] icpt = new double[(coeffs.length) - 1];// all the intercepts

            updateOrdinalCoeff(trainMultinomial, 25, paramsO, interceptPDF, coeffs[0].length, Integer.parseInt(model._output._model_summary.getCellValues()[0][5].toString()), beta, icpt);
            compareMultCoeffs(coeffs, beta, icpt);// compare and check coefficients agree

        } finally {
            Scope.exit();
        }
    }
}

