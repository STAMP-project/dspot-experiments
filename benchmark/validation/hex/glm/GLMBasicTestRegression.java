package hex.glm;


import DeepLearningModel.DeepLearningParameters.MissingValuesHandling;
import Solver.GRADIENT_DESCENT_LH;
import Solver.GRADIENT_DESCENT_SQERR;
import hex.ModelMetricsRegressionGLM;
import hex.glm.GLMModel.GLMParameters;
import hex.glm.GLMModel.GLMParameters.Family;
import hex.glm.GLMModel.GLMParameters.Solver;
import hex.hex.ModelMetricsRegressionGLM;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.TestUtil;
import water.exceptions.H2OIllegalArgumentException;
import water.exceptions.H2OModelBuilderIllegalArgumentException;
import water.fvec.Frame;
import water.fvec.Vec;


/**
 * Created by tomasnykodym on 6/4/15.
 */
public class GLMBasicTestRegression extends TestUtil {
    static Frame _canCarTrain;

    static Frame _earinf;

    static Frame _weighted;

    static Frame _upsampled;

    static Vec _merit;

    static Vec _class;

    static Frame _prostateTrain;

    static Frame _airlines;

    static Frame _airlinesMM;

    @Test
    public void testSingleCatNoIcpt() {
        Vec cat = Vec.makeVec(new long[]{ 1, 1, 1, 0, 0 }, new String[]{ "black", "red" }, Vec.newKey());
        Vec res = Vec.makeVec(new double[]{ 1, 1, 0, 0, 0 }, cat.group().addVec());
        Frame fr = new Frame(Key.<Frame>make("fr"), new String[]{ "x", "y" }, new Vec[]{ cat, res });
        DKV.put(fr);
        GLMParameters parms = new GLMParameters();
        parms._train = fr._key;
        parms._alpha = new double[]{ 0 };
        parms._response_column = "y";
        parms._intercept = false;
        parms._family = Family.binomial;
        // just make sure it runs
        GLMModel model = trainModel().get();
        Map<String, Double> coefs = model.coefficients();
        System.out.println(("coefs = " + coefs));
        Assert.assertEquals(coefs.get("Intercept"), 0, 0);
        Assert.assertEquals(4.2744474, residual_deviance(), 1.0E-4);
        System.out.println();
        model.delete();
        fr.delete();
    }

    @Test
    public void testSparse() {
        double[] exp_coefs = new double[]{ 0.0233151691783671, -0.00543776852277619, -0.0137359312181047, 0.00770037200907652, 0.0328856331139761, -0.0242845468071283, -0.0101698117745265, 0.00868844870137727, 3.49121513384513E-4, -0.0106962199761512, -0.00705001448025939, 0.00821574637914086, 0.00601015905212279, 0.0021278467162546, -0.0233079168835112, 0.00535473896013676, -0.00897667301004576, 0.00788272228017582, 0.00237442711371947, -0.013136425134371, 0.00134003869245749, 0.0240118046676911, 6.07214787933269E-4, -0.0112908513868027, 4.43119443631777E-4, 0.00749330452744921, -0.00558704122833295, 5.33036850835694E-4, 0.0130008059852934, -4.40634889376063E-5, -0.00580285872202347, 0.0117029111583238, -0.00685480666428133, 0.00809526311326634, -0.0088567165389072, -0.0363126456378731, -0.00267237519808936, -0.01669554043682, 0.00556943053195684, 0.0178196407614288, -9.03204442155076E-4, -0.0085363297586185, -0.00421147221966977, -0.00828702756129772, 0.017027928644479, 0.00710126315700672, 0.019819043342772, -0.0165232485929677, 0.00439570108491533, 0.0188325734374437, 0.00799712968759025, -0.0100388875424171, -0.0062415137856855, -0.00258013659839137, -6.58516379178382E-5, 0.0135032332096949, -0.00776869619293087, -0.00544035128543343, -0.0110626226606883, -0.00768490011210769, -0.00684181016695251, -0.0144627862333649, -0.0262830557415184, -0.0102290180164706, 0.00368252955770187, 0.015824495748353, 0.00383484095683782, 0.0151193905626625, -0.00615077094420626, 0.0142842231522414, 0.00150448184871646, 0.0521491615912011, 0.0128661232226479, 0.00225580439739044, -0.0117476427864401, -0.0059792656068627, 7.87012740598272E-4, 0.00255419488737936, 0.00406033118385186, 0.0102551045653601, 0.00423949002681056, -0.0116986428989079, 0.00232448128787425, -0.00296198808290357, -0.00793738689381332, -7.71158906679964E-4, 0.00435708760153937, -0.0138922325725763, 0.00264561130131037, -0.0156128295187466, -0.0102023187068811, 0.0074744189329328, 0.0102377749189598, -0.0304739969497646, 0.00692556661464647, 0.00151065993974025, 0.0133704258946895, -0.0167391228441308, 0.0111804482435337, -0.0062469732087272, -0.00930165243463748, -0.00418698783410104, 0.00190918091726462, 0.00632982717347925, -0.00277608255480933, -0.00175463261672652, -0.00267223587651978, -0.00329264073314718, 9.60091877616874E-4, -0.00946014799557438, -0.0112302467393988, -0.00870512647578646, -0.00238582834931644, -0.0100845163232815, -0.00675861103174491, -6.89229731411459E-4, 0.0127651588318169, -0.0062753105816655, -0.00240575758827749, 0.00439570108491531, 0.00934971690544427, -0.0184380964678117, -0.00474253892124699, 0.00522916014066936, -0.0105148336464531, 0.0088372219244051, 0.0100429095740915, -0.0107657032259033, -0.00512476269437683, -0.00558487620671732, -6.37298812579742E-4, -0.00118460090105795, -0.00369801350318738, -0.00556276860695209, 0.00789011470305446, -0.00248367841256358, 0.00677762904717052, -0.00640135771848287, 0.00797532960057465, -0.00117508910987595, 9.86931150778778E-4, -0.0148237721063735, 0.0053001635341953, -0.0139698571439444, -0.0172255105183439, -0.0177416268392445, -0.0107062660197562, -0.00735448768491512, -0.00418482390542493, 0.00933957546887131, -0.00761657876743367, 0.0107862806984669, 6.99667442150322E-5, -0.00151054027221715, 0.00941377216029456, 0.0112882845381545, 0.0014423575345095, 0.00845773223444363, -0.00675939077916714, -0.00329806028742896, 2.76998824889068E-4, 0.00206337643122044, -0.00173085772672239, 0.00169616445468346, 0.00281297187309321, -0.0152343998246272, 0.0126261762792184, -2.24959505615703E-4, -0.00476466349783071, -0.0102541605421868, -5.61674281900828E-4, 0.00367777757696579, -9.60272764476094E-4, 0.00255704179717728, -6.96266184051808E-4, 0.0470920125432207, 0.0115016691642458, -0.00287666464467251, -0.00132912286075637, 0.00201932482935891, 0.00119899092739739, 0.00380417340899902, -0.00394363983208331, -0.00294543812868618, -1.77894150438862E-5, -0.00455002740798846, 6.13307426862812E-4, 0.00348274063618593, 0.00161877234851832, 0.0231608701706833, -0.00390062462708628, 0.00244047437999614, -0.00143984617445982, -0.00221831741496412, -0.00744853810342609, -0.00575689075773469, -0.00567890661011033, 0.00384589889309526, -0.00173241442296732, -0.00526995531653655, -0.00310819786514896, 0.00740596461822877, -0.0790037392468225, 0.0239744234187787, 0.0514310481067108, 0.034335426530007, 0.0254604884688754, 0.0531375235023675, -0.0228335779154641, 0.546865402727144 };
        GLMModel model1 = null;
        GLMParameters parms = new GLMParameters(Family.gaussian);
        GLMBasicTestRegression._airlinesMM.add("weights", GLMBasicTestRegression._airlinesMM.anyVec().makeCon(1.0));
        DKV.put(GLMBasicTestRegression._airlinesMM._key, GLMBasicTestRegression._airlinesMM);
        parms._weights_column = "weights";
        parms._train = GLMBasicTestRegression._airlinesMM._key;
        parms._lambda = new double[]{ 0.01 };
        parms._alpha = new double[]{ 0 };
        parms._solver = Solver.IRLSM;
        parms._ignored_columns = new String[]{ "C1" };
        // parms._remove_collinear_columns = true;
        parms._response_column = "IsDepDelayed";
        parms._standardize = true;
        parms._objective_epsilon = 0;
        parms._gradient_epsilon = 1.0E-10;
        parms._max_iterations = 1000;
        parms._missing_values_handling = MissingValuesHandling.Skip;
        try {
            model1 = trainModel().get();
            for (int i = 0; i < (model1._output._coefficient_names.length); ++i)
                Assert.assertEquals(exp_coefs[i], model1._output.getNormBeta()[i], ((Math.abs(exp_coefs[i])) * 1.0E-8));

        } finally {
            if (model1 != null)
                model1.delete();

        }
    }

    @Test
    public void testWeights() {
        GLMModel model1 = null;
        GLMModel model2 = null;
        GLMParameters parms = new GLMParameters(Family.gaussian);
        parms._train = GLMBasicTestRegression._weighted._key;
        parms._ignored_columns = new String[]{ GLMBasicTestRegression._weighted.name(0) };
        parms._response_column = GLMBasicTestRegression._weighted.name(1);
        parms._standardize = true;
        parms._objective_epsilon = 0;
        parms._gradient_epsilon = 1.0E-10;
        parms._max_iterations = 1000;
        for (Solver s : GLMParameters.Solver.values()) {
            // if(s != Solver.IRLSM)continue; //fixme: does not pass for other than IRLSM now
            if ((s.equals(GRADIENT_DESCENT_SQERR)) || (s.equals(GRADIENT_DESCENT_LH)))
                continue;
            // only used for ordinal regression

            System.out.println("===============================================================");
            System.out.println(("Solver = " + s));
            System.out.println("===============================================================");
            try {
                parms._lambda = new double[]{ 1.0E-5 };
                parms._alpha = null;
                parms._train = GLMBasicTestRegression._weighted._key;
                parms._solver = s;
                parms._weights_column = "weights";
                model1 = trainModel().get();
                HashMap<String, Double> coefs1 = model1.coefficients();
                System.out.println(("coefs1 = " + coefs1));
                parms._train = GLMBasicTestRegression._upsampled._key;
                parms._weights_column = null;
                parms._lambda = new double[]{ 1.0E-5 };
                parms._alpha = null;
                model2 = trainModel().get();
                HashMap<String, Double> coefs2 = model2.coefficients();
                System.out.println(("coefs2 = " + coefs2));
                System.out.println(((("mse1 = " + (model1._output._training_metrics.mse())) + ", mse2 = ") + (model2._output._training_metrics.mse())));
                System.out.println(model1._output._training_metrics);
                System.out.println(model2._output._training_metrics);
                Assert.assertEquals(model2._output._training_metrics.mse(), model1._output._training_metrics.mse(), 1.0E-4);
            } finally {
                if (model1 != null)
                    model1.delete();

                if (model2 != null)
                    model2.delete();

            }
        }
    }

    @Test
    public void testOffset() {
        GLMModel model1 = null;
        GLMModel model2 = null;
        GLMParameters parms = new GLMParameters(Family.gaussian);
        parms._train = GLMBasicTestRegression._weighted._key;
        parms._ignored_columns = new String[]{ GLMBasicTestRegression._weighted.name(0) };
        parms._response_column = GLMBasicTestRegression._weighted.name(1);
        parms._standardize = true;
        parms._objective_epsilon = 0;
        parms._gradient_epsilon = 1.0E-10;
        parms._max_iterations = 1000;
        Solver s = Solver.IRLSM;
        try {
            parms._lambda = new double[]{ 0 };
            parms._alpha = new double[]{ 0 };
            parms._train = GLMBasicTestRegression._weighted._key;
            parms._solver = s;
            parms._offset_column = "C20";
            parms._compute_p_values = true;
            parms._standardize = false;
            model1 = trainModel().get();
            HashMap<String, Double> coefs1 = model1.coefficients();
            System.out.println(("coefs1 = " + coefs1));
            /**
             * Call:
             * glm(formula = C2 ~ . - C1 - C20, data = data, offset = data$C20)
             *
             * Deviance Residuals:
             * Min      1Q  Median      3Q     Max
             * -3.444  -0.821  -0.021   0.878   2.801
             *
             * Coefficients:
             * Estimate Std. Error t value Pr(>|t|)
             * (Intercept) -0.026928   0.479281  -0.056   0.9553
             * C3          -0.064657   0.144517  -0.447   0.6558
             * C4          -0.076132   0.163746  -0.465   0.6432
             * C5           0.397962   0.161458   2.465   0.0158 *
             * C6           0.119644   0.173165   0.691   0.4916
             * C7          -0.124615   0.151145  -0.824   0.4121
             * C8           0.142455   0.164912   0.864   0.3902
             * C9           0.087358   0.158266   0.552   0.5825
             * C10         -0.012873   0.155429  -0.083   0.9342
             * C11          0.277392   0.181299   1.530   0.1299
             * C12          0.004988   0.170290   0.029   0.9767
             * C13         -0.091400   0.172910  -0.529   0.5985
             * C14         -0.248876   0.177311  -1.404   0.1643
             * C15          0.053598   0.167305   0.320   0.7495
             * C16          0.156302   0.157823   0.990   0.3249
             * C17          0.296317   0.167453   1.770   0.0806 .
             * C18          0.013306   0.162185   0.082   0.9348
             * C19          0.115939   0.160250   0.723   0.4715
             * weights     -0.005771   0.303477  -0.019   0.9849
             */
            double[] expected_coefs = new double[]{ -0.064656782, -0.07613188, 0.397962147, 0.119644094, -0.124614842, 0.142455018, 0.087357855, -0.012872522, 0.277392182, 0.004987961, -0.091400128, -0.24887597, 0.053597896, 0.15630178, 0.296317472, 0.013306398, 0.115938809, -0.005771429, -0.026928297 };
            double[] expected_pvals = new double[]{ 0.65578062, 0.64322317, 0.01582348, 0.49158786, 0.41209217, 0.39023637, 0.58248959, 0.93419972, 0.12990598, 0.97670462, 0.59852911, 0.16425679, 0.74951951, 0.32494727, 0.08056447, 0.93481349, 0.47146503, 0.98487376, 0.95533301 };
            double[] actual_coefs = model1.beta();
            double[] actual_pvals = model1._output.pValues();
            for (int i = 0; i < (expected_coefs.length); ++i) {
                Assert.assertEquals(expected_coefs[i], actual_coefs[i], 1.0E-4);
                Assert.assertEquals(expected_pvals[i], actual_pvals[i], 1.0E-4);
            }
        } finally {
            if (model1 != null)
                model1.delete();

            if (model2 != null)
                model2.delete();

        }
    }

    @Test
    public void testTweedie() {
        GLMModel model = null;
        Frame scoreTrain = null;
        // --------------------------------------  R examples output ----------------------------------------------------------------
        // Call:  glm(formula = Infections ~ ., family = tweedie(0), data = D)
        // 
        // Coefficients:
        // (Intercept)      SwimmerOccas  LocationNonBeach          Age20-24          Age25-29          SexMale
        // 0.8910            0.8221                 0.7266           -0.5033           -0.2679         -0.1056
        // 
        // Degrees of Freedom: 286 Total (i.e. Null);  281 Residual
        // Null Deviance:	    1564
        // Residual Deviance: 1469 	AIC: NA
        // Call:  glm(formula = Infections ~ ., family = tweedie(1), data = D)
        // 
        // Coefficients:
        // (Intercept)      SwimmerOccas  LocationNonBeach          Age20-24          Age25-29           SexMale
        // -0.12261           0.61149           0.53454          -0.37442          -0.18973          -0.08985
        // 
        // Degrees of Freedom: 286 Total (i.e. Null);  281 Residual
        // Null Deviance:	    824.5
        // Residual Deviance: 755.4 	AIC: NA
        // Call:  glm(formula = Infections ~ ., family = tweedie(1.25), data = D)
        // 
        // Coefficients:
        // (Intercept)      SwimmerOccas  LocationNonBeach          Age20-24          Age25-29           SexMale
        // 1.02964          -0.14079          -0.12200           0.08502           0.04269           0.02105
        // 
        // Degrees of Freedom: 286 Total (i.e. Null);  281 Residual
        // Null Deviance:	    834.2
        // Residual Deviance: 770.8 	AIC: NA
        // Call:  glm(formula = Infections ~ ., family = tweedie(1.5), data = D)
        // 
        // Coefficients:
        // (Intercept)      SwimmerOccas  LocationNonBeach          Age20-24          Age25-29           SexMale
        // 1.05665          -0.25891          -0.22185           0.15325           0.07624           0.03908
        // 
        // Degrees of Freedom: 286 Total (i.e. Null);  281 Residual
        // Null Deviance:	    967
        // Residual Deviance: 908.9 	AIC: NA
        // Call:  glm(formula = Infections ~ ., family = tweedie(1.75), data = D)
        // 
        // Coefficients:
        // (Intercept)      SwimmerOccas  LocationNonBeach          Age20-24          Age25-29           SexMale
        // 1.08076          -0.35690          -0.30154           0.20556           0.10122           0.05375
        // 
        // Degrees of Freedom: 286 Total (i.e. Null);  281 Residual
        // Null Deviance:	    1518
        // Residual Deviance: 1465 	AIC: NA
        // Call:  glm(formula = Infections ~ ., family = tweedie(2), data = D)
        // 
        // Coefficients:
        // (Intercept)      SwimmerOccas  LocationNonBeach          Age20-24          Age25-29           SexMale
        // 1.10230          -0.43751          -0.36337           0.24318           0.11830           0.06467
        // 
        // Degrees of Freedom: 286 Total (i.e. Null);  281 Residual
        // Null Deviance:	    964.4
        // Residual Deviance: 915.7 	AIC: NA
        // ---------------------------------------------------------------------------------------------------------------------------
        String[] cfs1 = new String[]{ "Intercept", "Swimmer.Occas", "Location.NonBeach", "Age.20-24", "Age.25-29", "Sex.Male" };
        double[][] vals = new double[][]{ new double[]{ 0.891, 0.8221, 0.7266, -0.5033, -0.2679, -0.1056 }, new double[]{ -0.12261, 0.61149, 0.53454, -0.37442, -0.18973, -0.08985 }, new double[]{ 1.02964, -0.14079, -0.122, 0.08502, 0.04269, 0.02105 }, new double[]{ 1.05665, -0.25891, -0.22185, 0.15325, 0.07624, 0.03908 }, new double[]{ 1.08076, -0.3569, -0.30154, 0.20556, 0.10122, 0.05375 }, new double[]{ 1.1023, -0.43751, -0.36337, 0.24318, 0.1183, 0.06467 } };
        int dof = 286;
        int res_dof = 281;
        double[] nullDev = new double[]{ 1564, 824.5, 834.2, 967.0, 1518, 964.4 };
        double[] resDev = new double[]{ 1469, 755.4, 770.8, 908.9, 1465, 915.7 };
        double[] varPow = new double[]{ 0, 1.0, 1.25, 1.5, 1.75, 2.0 };
        GLMParameters parms = new GLMParameters(Family.tweedie);
        parms._train = GLMBasicTestRegression._earinf._key;
        parms._ignored_columns = new String[]{  };
        // "response_column":"Claims","offset_column":"logInsured"
        parms._response_column = "Infections";
        parms._standardize = false;
        parms._lambda = new double[]{ 0 };
        parms._alpha = new double[]{ 0 };
        parms._gradient_epsilon = 1.0E-10;
        parms._max_iterations = 1000;
        parms._objective_epsilon = 0;
        parms._beta_epsilon = 1.0E-6;
        for (int x = 0; x < (varPow.length); ++x) {
            double p = varPow[x];
            parms._tweedie_variance_power = p;
            parms._tweedie_link_power = 1 - p;
            /* new Solver[]{Solver.IRLSM} */
            for (Solver s : GLMParameters.Solver.values()) {
                if (((s == (Solver.COORDINATE_DESCENT_NAIVE)) || (s.equals(GRADIENT_DESCENT_LH))) || (s.equals(GRADIENT_DESCENT_SQERR)))
                    continue;
                // ignore for now, has trouble with zero columns

                try {
                    parms._solver = s;
                    model = trainModel().get();
                    HashMap<String, Double> coefs = model.coefficients();
                    System.out.println(("coefs = " + coefs));
                    for (int i = 0; i < (cfs1.length); ++i)
                        Assert.assertEquals(vals[x][i], coefs.get(cfs1[i]), 1.0E-4);

                    Assert.assertEquals(nullDev[x], GLMTest.nullDeviance(model), (5.0E-4 * (nullDev[x])));
                    Assert.assertEquals(resDev[x], GLMTest.residualDeviance(model), (5.0E-4 * (resDev[x])));
                    Assert.assertEquals(dof, GLMTest.nullDOF(model), 0);
                    Assert.assertEquals(res_dof, GLMTest.resDOF(model), 0);
                    // test scoring
                    scoreTrain = model.score(GLMBasicTestRegression._earinf);
                    Assert.assertTrue(model.testJavaScoring(GLMBasicTestRegression._earinf, scoreTrain, 1.0E-8));
                    hex.ModelMetricsRegressionGLM mmTrain = ((ModelMetricsRegressionGLM) (hex.ModelMetricsRegression.getFromDKV(model, GLMBasicTestRegression._earinf)));
                    Assert.assertEquals(model._output._training_metrics._MSE, mmTrain._MSE, 1.0E-8);
                    Assert.assertEquals(GLMTest.residualDeviance(model), mmTrain._resDev, 1.0E-8);
                    Assert.assertEquals(GLMTest.nullDeviance(model), mmTrain._nullDev, 1.0E-8);
                } finally {
                    if (model != null)
                        model.delete();

                    if (scoreTrain != null)
                        scoreTrain.delete();

                }
            }
        }
    }

    @Test
    public void testPoissonWithOffset() {
        GLMModel model = null;
        Frame scoreTrain = null;
        // Call:  glm(formula = formula, family = poisson, data = D)
        // 
        // Coefficients:
        // (Intercept)       Merit1       Merit2       Merit3       Class2       Class3       Class4       Class5
        // -2.0357          -0.1378      -0.2207      -0.4930       0.2998       0.4691       0.5259       0.2156
        // 
        // Degrees of Freedom: 19 Total (i.e. Null);  12 Residual
        // Null Deviance:	    33850
        // Residual Deviance: 579.5 	AIC: 805.9
        String[] cfs1 = new String[]{ "Intercept", "Merit.1", "Merit.2", "Merit.3", "Class.2", "Class.3", "Class.4", "Class.5" };
        double[] vals = new double[]{ -2.0357, -0.1378, -0.2207, -0.493, 0.2998, 0.4691, 0.5259, 0.2156 };
        GLMParameters parms = new GLMParameters(Family.poisson);
        parms._train = GLMBasicTestRegression._canCarTrain._key;
        parms._ignored_columns = new String[]{ "Insured", "Premium", "Cost" };
        // "response_column":"Claims","offset_column":"logInsured"
        parms._response_column = "Claims";
        parms._offset_column = "logInsured";
        parms._standardize = false;
        parms._lambda = new double[]{ 0 };
        parms._alpha = new double[]{ 0 };
        parms._objective_epsilon = 0;
        parms._beta_epsilon = 1.0E-6;
        parms._gradient_epsilon = 1.0E-10;
        parms._max_iterations = 1000;
        for (Solver s : GLMParameters.Solver.values()) {
            if (((s == (Solver.COORDINATE_DESCENT_NAIVE)) || (s.equals(GRADIENT_DESCENT_LH))) || (s.equals(GRADIENT_DESCENT_SQERR)))
                continue;
            // skip for now, does not handle zero columns (introduced by extra missing bucket with no missing in the dataset)

            try {
                parms._solver = s;
                model = trainModel().get();
                HashMap<String, Double> coefs = model.coefficients();
                System.out.println(("coefs = " + coefs));
                for (int i = 0; i < (cfs1.length); ++i)
                    Assert.assertEquals(vals[i], coefs.get(cfs1[i]), 1.0E-4);

                Assert.assertEquals(33850, GLMTest.nullDeviance(model), 5);
                Assert.assertEquals(579.5, GLMTest.residualDeviance(model), (1.0E-4 * 579.5));
                Assert.assertEquals(19, GLMTest.nullDOF(model), 0);
                Assert.assertEquals(12, GLMTest.resDOF(model), 0);
                Assert.assertEquals(805.9, GLMTest.aic(model), (1.0E-4 * 805.9));
                // test scoring
                try {
                    Frame fr = new Frame(GLMBasicTestRegression._canCarTrain.names(), GLMBasicTestRegression._canCarTrain.vecs());
                    fr.remove(parms._offset_column);
                    scoreTrain = model.score(fr);
                    Assert.assertTrue("shoul've thrown IAE", false);
                } catch (IllegalArgumentException iae) {
                    Assert.assertTrue(iae.getMessage().contains("Test/Validation dataset is missing offset column"));
                }
                scoreTrain = model.score(GLMBasicTestRegression._canCarTrain);
                hex.ModelMetricsRegressionGLM mmTrain = ((ModelMetricsRegressionGLM) (hex.ModelMetricsRegression.getFromDKV(model, GLMBasicTestRegression._canCarTrain)));
                Assert.assertEquals(model._output._training_metrics._MSE, mmTrain._MSE, 1.0E-8);
                Assert.assertEquals(GLMTest.residualDeviance(model), mmTrain._resDev, 1.0E-8);
                Assert.assertEquals(GLMTest.nullDeviance(model), mmTrain._nullDev, 1.0E-8);
            } finally {
                if (model != null)
                    model.delete();

                if (scoreTrain != null)
                    scoreTrain.delete();

            }
        }
    }

    static double[] tweedie_se_fit = new double[]{ 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0613489490790154, 0.0925127769415089, 0.0925127769415089, 0.0925127769415089, 0.0925127769415089, 0.0987894311775416, 0.0987894311775416, 0.0987894311775416, 0.0987894311775416, 0.0987894311775416, 0.0987894311775416, 0.0987894311775416, 0.0987894311775416, 0.0987894311775416, 0.0987894311775416, 0.0987894311775416, 0.0987894311775416, 0.100083115466028, 0.100083115466028, 0.100083115466028, 0.100083115466028, 0.100083115466028, 0.100083115466028, 0.100083115466028, 0.100083115466028, 0.100083115466028, 0.100083115466028, 0.100083115466028, 0.100884077314708, 0.100884077314708, 0.100884077314708, 0.100884077314708, 0.100884077314708, 0.100884077314708, 0.100884077314708, 0.115835959352225, 0.115835959352225, 0.115835959352225, 0.115835959352225, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.0841383955582187, 0.110599707082871, 0.110599707082871, 0.110599707082871, 0.110599707082871, 0.111858985562116, 0.111858985562116, 0.111858985562116, 0.111858985562116, 0.111858985562116, 0.111858985562116, 0.111858985562116, 0.111858985562116, 0.111858985562116, 0.111858985562116, 0.111858985562116, 0.111858985562116, 0.111858985562116, 0.114576682598884, 0.114576682598884, 0.114576682598884, 0.114576682598884, 0.114576682598884, 0.114576682598884, 0.114576682598884, 0.114576682598884, 0.114576682598884, 0.114576682598884, 0.115282471068922, 0.115282471068922, 0.115282471068922, 0.115282471068922, 0.115282471068922, 0.115282471068922, 0.115282471068922, 0.115282471068922, 0.129955861024206, 0.129955861024206, 0.129955861024206, 0.129955861024206, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0858288225981346, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.0959405152884533, 0.121964163974085, 0.121964163974085, 0.121964163974085, 0.121964163974085, 0.121964163974085, 0.121964163974085, 0.121964163974085, 0.110343150778848, 0.110343150778848, 0.110343150778848, 0.110343150778848, 0.110343150778848, 0.110343150778848, 0.110343150778848, 0.110343150778848, 0.110343150778848, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.108157177224978, 0.109459685499218, 0.109459685499218, 0.109459685499218, 0.109459685499218, 0.109459685499218, 0.109459685499218, 0.109459685499218, 0.109459685499218, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.100845471768361, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.111202113814401, 0.130828072545958, 0.130828072545958, 0.130828072545958, 0.130828072545958, 0.130828072545958, 0.130828072545958, 0.130828072545958, 0.121550168454726, 0.121550168454726, 0.121550168454726, 0.121550168454726, 0.121550168454726, 0.121550168454726, 0.121550168454726, 0.121550168454726, 0.121550168454726, 0.121550168454726, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.119574547454639, 0.122227760425649, 0.122227760425649, 0.122227760425649, 0.122227760425649, 0.122227760425649, 0.122227760425649 };

    @Test
    public void testPValuesTweedie() {
        // Call:
        // glm(formula = Infections ~ ., family = tweedie(var.power = 1.5),
        // data = D)
        // 
        // Deviance Residuals:
        // Min       1Q   Median       3Q      Max
        // -2.6355  -2.0931  -1.8183   0.5046   4.9458
        // 
        // Coefficients:
        // Estimate Std. Error t value Pr(>|t|)
        // (Intercept)       1.05665    0.11120   9.502  < 2e-16 ***
        // SwimmerOccas     -0.25891    0.08455  -3.062  0.00241 **
        // LocationNonBeach -0.22185    0.08393  -2.643  0.00867 **
        // Age20-24          0.15325    0.10041   1.526  0.12808
        // Age25-29          0.07624    0.10099   0.755  0.45096
        // SexMale           0.03908    0.08619   0.453  0.65058
        // ---
        // Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
        // 
        // (Dispersion parameter for Tweedie family taken to be 2.896306)
        // 
        // Null deviance: 967.05  on 286  degrees of freedom
        // Residual deviance: 908.86  on 281  degrees of freedom
        // AIC: NA
        // 
        // Number of Fisher Scoring iterations: 7
        double[] sderr_exp = new double[]{ 0.11120211, 0.08454967, 0.08393315, 0.1004115, 0.10099231, 0.0861896 };
        double[] zvals_exp = new double[]{ 9.5021062, -3.0622693, -2.6431794, 1.5262357, 0.7548661, 0.4534433 };
        double[] pvals_exp = new double[]{ 9.5084E-19, 0.002409514, 0.008674149, 0.1280759, 0.4509615, 0.6505795 };
        GLMParameters parms = new GLMParameters(Family.tweedie);
        parms._tweedie_variance_power = 1.5;
        parms._tweedie_link_power = 1 - (parms._tweedie_variance_power);
        parms._train = GLMBasicTestRegression._earinf._key;
        parms._standardize = false;
        parms._lambda = new double[]{ 0 };
        parms._alpha = new double[]{ 0 };
        parms._response_column = "Infections";
        parms._compute_p_values = true;
        parms._objective_epsilon = 0;
        parms._missing_values_handling = MissingValuesHandling.Skip;
        GLMModel model = null;
        Frame predict = null;
        try {
            model = trainModel().get();
            String[] names_expected = new String[]{ "Intercept", "Swimmer.Occas", "Location.NonBeach", "Age.20-24", "Age.25-29", "Sex.Male" };
            String[] names_actual = model._output.coefficientNames();
            HashMap<String, Integer> coefMap = new HashMap<>();
            for (int i = 0; i < (names_expected.length); ++i)
                coefMap.put(names_expected[i], i);

            double[] stder_actual = model._output.stdErr();
            double[] zvals_actual = model._output.zValues();
            double[] pvals_actual = model._output.pValues();
            for (int i = 0; i < (sderr_exp.length); ++i) {
                int id = coefMap.get(names_actual[i]);
                Assert.assertEquals(sderr_exp[id], stder_actual[i], ((sderr_exp[id]) * 0.001));
                Assert.assertEquals(zvals_exp[id], zvals_actual[i], ((Math.abs(zvals_exp[id])) * 0.001));
                Assert.assertEquals(pvals_exp[id], pvals_actual[i], ((Math.max(1.0E-8, pvals_exp[id])) * 0.005));
            }
            predict = model.score(parms._train.get());
            Vec.Reader r = predict.vec("StdErr").new Reader();
            for (int i = 0; i < 10; i++)
                System.out.println((((GLMBasicTestRegression.tweedie_se_fit[i]) + " ?=? ") + (r.at(i))));

            for (int i = 0; i < (GLMBasicTestRegression.tweedie_se_fit.length); ++i)
                Assert.assertEquals(GLMBasicTestRegression.tweedie_se_fit[i], r.at(i), 1.0E-4);

        } finally {
            if (model != null)
                model.delete();

            if (predict != null)
                predict.delete();

        }
    }

    static double[] poisson_se_fit = new double[]{ 0.00214595071236062, 0.00743699599697046, 0.00543894401842774, 0.00655714683196705, 0.0110212478876686, 0.0075848798597348, 0.0145966442532301, 0.0119334418854485, 0.0119310044426751, 0.0206323555670128, 0.00651512689814114, 0.0126291877824898, 0.0101512423391255, 0.0125677132679544, 0.0177401092625854, 0.0050285508709862, 0.00984147775616493, 0.0100843481643067, 0.00920309580050661, 0.0135853678325585 };

    @Test
    public void testPValuesPoisson() {
        // Coefficients:
        // Estimate Std. Error z value Pr(>|z|)
        // (Intercept) -1.279e+00  3.481e-01  -3.673 0.000239 ***
        // Merit1      -1.498e-01  2.972e-02  -5.040 4.64e-07 ***
        // Merit2      -2.364e-01  3.859e-02  -6.127 8.96e-10 ***
        // Merit3      -3.197e-01  5.095e-02  -6.274 3.52e-10 ***
        // Class2       6.899e-02  8.006e-02   0.862 0.388785
        // Class3       2.892e-01  6.333e-02   4.566 4.97e-06 ***
        // Class4       2.708e-01  4.911e-02   5.515 3.49e-08 ***
        // Class5      -4.468e-02  1.048e-01  -0.427 0.669732
        // Insured      1.617e-06  5.069e-07   3.191 0.001420 **
        // Premium     -3.630e-05  1.087e-05  -3.339 0.000840 ***
        // Cost         2.021e-05  6.869e-06   2.943 0.003252 **
        // logInsured   9.390e-01  2.622e-02  35.806  < 2e-16 ***
        // ---
        // Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
        // 
        // (Dispersion parameter for poisson family taken to be 1)
        // 
        // Null deviance: 961181.685  on 19  degrees of freedom
        // Residual deviance:     42.671  on  8  degrees of freedom
        // AIC: 277.08
        double[] sderr_exp = new double[]{ 0.3480733, 0.02972063, 0.03858825, 0.0509526, 0.08005579, 0.06332867, 0.0491069, 0.1047531, 5.068602E-7, 1.086939E-5, 6.869142E-6, 0.0262237 };
        double[] zvals_exp = new double[]{ -3.6734577, -5.0404946, -6.1269397, -6.2739848, 0.861822, 4.5662083, 5.5148904, -0.4265158, 3.1906387, -3.3392867, 2.9428291, 35.8061272 };
        double[] pvals_exp = new double[]{ 2.392903E-4, 4.643302E-7, 8.95854E-10, 3.519228E-10, 0.3887855, 4.966252E-6, 3.489974E-8, 0.6697321, 0.001419587, 8.399383E-4, 0.003252279, 8.867127E-281 };
        GLMParameters parms = new GLMParameters(Family.poisson);
        parms._train = GLMBasicTestRegression._canCarTrain._key;
        parms._standardize = false;
        parms._lambda = new double[]{ 0 };
        parms._alpha = new double[]{ 0 };
        parms._response_column = "Claims";
        parms._compute_p_values = true;
        parms._objective_epsilon = 0;
        parms._missing_values_handling = MissingValuesHandling.Skip;
        GLMModel model = null;
        Frame predict = null;
        try {
            model = trainModel().get();
            predict = model.score(parms._train.get());
            String[] names_expected = new String[]{ "Intercept", "Merit.1", "Merit.2", "Merit.3", "Class.2", "Class.3", "Class.4", "Class.5", "Insured", "Premium", "Cost", "logInsured" };
            String[] names_actual = model._output.coefficientNames();
            HashMap<String, Integer> coefMap = new HashMap<>();
            for (int i = 0; i < (names_expected.length); ++i)
                coefMap.put(names_expected[i], i);

            double[] stder_actual = model._output.stdErr();
            double[] zvals_actual = model._output.zValues();
            double[] pvals_actual = model._output.pValues();
            for (int i = 0; i < (sderr_exp.length); ++i) {
                int id = coefMap.get(names_actual[i]);
                Assert.assertEquals(sderr_exp[id], stder_actual[i], ((sderr_exp[id]) * 1.0E-4));
                Assert.assertEquals(zvals_exp[id], zvals_actual[i], ((Math.abs(zvals_exp[id])) * 1.0E-4));
                Assert.assertEquals(pvals_exp[id], pvals_actual[i], Math.max(1.0E-15, ((pvals_exp[id]) * 0.001)));
            }
            Vec.Reader r = predict.vec("StdErr").new Reader();
            for (int i = 0; i < 10; i++)
                System.out.println((((fit_se[i]) + " ?=? ") + (r.at(i))));

            for (int i = 0; i < (GLMBasicTestRegression.poisson_se_fit.length); ++i)
                Assert.assertEquals(GLMBasicTestRegression.poisson_se_fit[i], r.at(i), 1.0E-4);

        } finally {
            if (model != null)
                model.delete();

            if (predict != null)
                predict.delete();

        }
    }

    double[] fit_se = new double[]{ 0.0696804506329891, 0.101281071858829, 0.10408152157582, 0.111268172403792, 0.0864807746842875, 0.115236360340542, 0.12221727704254, 0.124866677872014, 0.128339261626057, 0.0979129217095552, 0.110152626457565, 0.0990293981783657, 0.0797922311345053, 0.0792463216677848, 0.111426861426166, 0.0738441273359852, 0.120203649843966, 0.121693896359032, 0.0761455129888811, 0.26100084308246, 0.0720456904900178, 0.081097918838208, 0.0789702474051714, 0.0808142534572709, 0.105472082060368, 0.0840482368559722, 0.063378670290797, 0.105606912248152, 0.100466329601425, 0.0612898868494135, 0.109616337415397, 0.0701794746336352, 0.0640797475112887, 0.0673121686325539, 0.0634803009724142, 0.0640736894596905, 0.136481170989144, 0.263714808298936, 0.108661073153972, 0.0679992878660712, 0.0692653698520285, 0.0578825801685848, 0.0692824549011659, 0.158918750046892, 0.105821946814859, 0.062539478239644, 0.0645559360159139, 0.0675850464571084, 0.0747554134125586, 0.0615564429388638, 0.0654697687695094, 0.0917602397221548, 0.0585224587976278, 0.0778560274291999, 0.0680261708103141, 0.0958827924243588, 0.058974124112217, 0.072913090525014, 0.0689795760272738, 0.0713170788962477, 0.065706257508678, 0.128042541188024, 0.0649749667059613, 0.0613806345806654, 0.0750782449165757, 0.100075191507371, 0.0690401878038698, 0.0663993405278943, 0.0722234100213727, 0.114421672443619, 0.110357013874037, 0.0642985002654091, 0.0671725856291289, 0.063523258944993, 0.0715597141096345, 0.0646566408141189, 0.0633033140683379, 0.0670491504275652, 0.0603642211488642, 0.0560144665111521, 0.0671727628266449, 0.0738384805508671, 0.1247199741748, 0.0554223809418321, 0.0650037579647878, 0.0727634600806498, 0.0575637383983063, 0.0616609512372853, 0.0682789218401665, 0.0966026797905161, 0.12463988175174, 0.108735909355295, 0.0640657895777542, 0.0691809892888932, 0.0805455198436419, 0.0723317376403597, 0.0782641712930961, 0.104008893620461, 0.0854140524746924, 0.0495807108524011, 0.0520203427103241, 0.0629693638202253, 0.054824519906118, 0.0664522679377852, 0.0709937504956703, 0.0522528199125061, 0.116792628883851, 0.127959068214529, 0.0588829864765987, 0.0938071273144982, 0.0638448982296692, 0.095474139348608, 0.0636920146973271, 0.102824928294982, 0.0546954905581237, 0.0957477006105716, 0.0516295701222635, 0.0679538008921464, 0.067911254988675, 0.11772719691146, 0.0626934169760874, 0.0755070350639548, 0.0581558616336498, 0.0873377370618371, 0.0654538358047351, 0.0693235931850606, 0.0962317603498954, 0.0552842877956681, 0.077459867942534, 0.0626998557114978, 0.0531665050182605, 0.0495451968026518, 0.0531904147974664, 0.0773863775170239, 0.0570467158542459, 0.0615088358357168, 0.0653655052453002, 0.0958225208725932, 0.0821004080317487, 0.0554118772903184, 0.062705388445474, 0.115252227824609, 0.0930756784532364, 0.0856558971929684, 0.0976473251692103, 0.0710701529636323, 0.050750991917379, 0.0564411256187975, 0.0775449777496427, 0.115494288850098, 0.0682381145402218, 0.0515555125627838, 0.0670040023710206, 0.0712685707513018, 0.0532727639007648, 0.0546917068101745, 0.0717446129579534, 0.0801494525268998, 0.0472679272457015, 0.0730855772596969, 0.0656353433724242, 0.0670760966162116, 0.086126622468753, 0.0867455394873098, 0.117762705091036, 0.0552308514888129, 0.0567599016061833, 0.0761215691699384, 0.0699603827190508, 0.0611526828602172, 0.0665649473386548, 0.068400044275874, 0.052851970203728, 0.0947351046167158, 0.075626919466335, 0.0986954326552911, 0.158600667788559, 0.0997971513046435, 0.0558735275034329, 0.055050981781157, 0.0543870270651114, 0.0885427466948035, 0.0912282011735491, 0.0501764251426058, 0.065519936856806, 0.126597731978782, 0.0571871738429555, 0.0601312366784372, 0.134633469314707, 0.0636293392600048, 0.0822701720606341, 0.0998238113866312, 0.264894832552688, 0.0649884289638972, 0.0708677960760423, 0.0806790608308702, 0.101119270743047, 0.0550422649696084, 0.0648419030353994, 0.0558608594291732, 0.0526453344402306, 0.0610806341536575, 0.0609287420297426, 0.092034974197124, 0.0654686061501201, 0.0876869833195622, 0.0632671529428891, 0.0537964056797385, 0.0578936987690603, 0.0610938723761382, 0.0639353712535906, 0.168259497679141, 0.0962447000659448, 0.0651311057869981, 0.057491792983237, 0.0833792244616626, 0.086830040351315, 0.0774225857956364, 0.0664132437698603, 0.0574733178794812, 0.0647095391454638, 0.0608749267574728, 0.0534737593003958, 0.0864207446374423, 0.0630820817777617, 0.07226313326455, 0.0657499714305992, 0.121316445806293, 0.05853768423366, 0.0768645928103155, 0.0561109648914227, 0.0746288344339693, 0.0657484453780335, 0.0969340921119936, 0.0794439588324644, 0.0748828899207881, 0.100497037474176, 0.10675969143369, 0.0684810175839798, 0.0824837244664557, 0.0892814658999665, 0.0573638625958212, 0.0646309493356802, 0.0940910063257154, 0.0673435846353654, 0.0957497261909759, 0.0664402337808255, 0.0781546316899442, 0.0742122328375746, 0.0582089765051909, 0.0781545857991108, 0.104152580875285, 0.0711435121130216, 0.0983829670734453, 0.0815684611863238, 0.102263743443002, 0.0936000092997729, 0.128533232616524, 0.0641557720833701, 0.111115887875877, 0.0638681893568514, 0.101074063878806, 0.06424466347809, 0.064441266436105, 0.110618016393452, 0.0712315373586064, 0.0657094575123701, 0.0705967310833688, 0.068439218729386, 0.103666086457174, 0.0787150533390872, 0.107851546439191, 0.142558987347935, 0.0756230725139849, 0.0812011758847381, 0.0710836161067677, 0.0662009215101577, 0.130219300771016, 0.0951028456739751, 0.0774634922652527, 0.100986990070013, 0.0810216431052252, 0.0836836265752558, 0.0897897867952456, 0.174853086617412, 0.0750505478534531, 0.105468755484224, 0.102115887997378, 0.102894682905793, 0.0651020673618454 };

    @Test
    public void testPValuesGaussian() {
        // 1) NON-STANDARDIZED
        // summary(m)
        // 
        // Call:
        // glm(formula = CAPSULE ~ ., family = gaussian, data = D)
        // 
        // Deviance Residuals:
        // Min       1Q   Median       3Q      Max
        // -0.8394  -0.3162  -0.1113   0.3771   0.9447
        // 
        // Coefficients:
        // Estimate Std. Error t value Pr(>|t|)
        // (Intercept) -0.6870832  0.4035941  -1.702  0.08980 .
        // ID         0.0003081  0.0002387   1.291  0.19791
        // AGE         -0.0006005  0.0040246  -0.149  0.88150
        // RACER2      -0.0147733  0.2511007  -0.059  0.95313
        // RACER3      -0.1456993  0.2593492  -0.562  0.57471
        // DPROSb       0.1462512  0.0657117   2.226  0.02684 *
        // DPROSc       0.2297207  0.0713659   3.219  0.00144 **
        // DPROSd       0.1144974  0.0937208   1.222  0.22286
        // DCAPSb       0.1430945  0.0888124   1.611  0.10827
        // PSA          0.0047237  0.0015060   3.137  0.00189 **
        // VOL         -0.0019401  0.0013920  -1.394  0.16449
        // GLEASON      0.1438776  0.0273259   5.265 2.81e-07 ***
        // ---
        // Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
        // 
        // (Dispersion parameter for gaussian family taken to be 0.1823264)
        // 
        // Null deviance: 69.600  on 289  degrees of freedom
        // Residual deviance: 50.687  on 278  degrees of freedom
        // AIC: 343.16
        // 
        // Number of Fisher Scoring iterations: 2
        GLMParameters params = new GLMParameters(Family.gaussian);
        params._response_column = "CAPSULE";
        params._standardize = false;
        params._train = GLMBasicTestRegression._prostateTrain._key;
        params._compute_p_values = true;
        params._lambda = new double[]{ 0 };
        params._missing_values_handling = MissingValuesHandling.Skip;
        try {
            params._solver = Solver.L_BFGS;
            trainModel().get();
            Assert.assertFalse("should've thrown, p-values only supported with IRLSM", true);
        } catch (H2OModelBuilderIllegalArgumentException t) {
        }
        boolean naive_descent_exception_thrown = false;
        try {
            params._solver = Solver.COORDINATE_DESCENT_NAIVE;
            trainModel().get();
            Assert.assertFalse("should've thrown, p-values only supported with IRLSM", true);
        } catch (H2OIllegalArgumentException t) {
            naive_descent_exception_thrown = true;
        }
        Assert.assertTrue(naive_descent_exception_thrown);
        try {
            params._solver = Solver.COORDINATE_DESCENT;
            trainModel().get();
            Assert.assertFalse("should've thrown, p-values only supported with IRLSM", true);
        } catch (H2OModelBuilderIllegalArgumentException t) {
        }
        params._solver = Solver.IRLSM;
        GLM glm = new GLM(params);
        try {
            params._lambda = new double[]{ 1 };
            glm.trainModel().get();
            Assert.assertFalse("should've thrown, p-values only supported with no regularization", true);
        } catch (H2OModelBuilderIllegalArgumentException t) {
        }
        params._lambda = new double[]{ 0 };
        try {
            params._lambda_search = true;
            glm.trainModel().get();
            Assert.assertFalse("should've thrown, p-values only supported with no regularization (i.e. no lambda search)", true);
        } catch (H2OModelBuilderIllegalArgumentException t) {
        }
        params._lambda_search = false;
        GLMModel model = null;
        Frame predict = null;
        try {
            model = trainModel().get();
            String[] names_expected = new String[]{ "Intercept", "ID", "AGE", "RACE.R2", "RACE.R3", "DPROS.b", "DPROS.c", "DPROS.d", "DCAPS.b", "PSA", "VOL", "GLEASON" };
            double[] stder_expected = new double[]{ 0.4035941476, 2.387281E-4, 0.004024552, 0.251100712, 0.2593492335, 0.0657117271, 0.0713659021, 0.0937207659, 0.0888124376, 0.0015060289, 0.0013919737, 0.0273258788 };
            double[] zvals_expected = new double[]{ -1.70241133, 1.29061005, -0.14920829, -0.05883397, -0.56178799, 2.22564893, 3.21891333, 1.22168646, 1.61119882, 3.136508, -1.39379859, 5.26524961 };
            double[] pvals_expected = new double[]{ 0.0897961, 0.1979113, 0.8814975, 0.9531266, 0.5747131, 0.02683977, 0.001439295, 0.2228612, 0.1082711, 0.00189321, 0.1644916, 2.805776E-7 };
            String[] names_actual = model._output.coefficientNames();
            HashMap<String, Integer> coefMap = new HashMap<>();
            for (int i = 0; i < (names_expected.length); ++i)
                coefMap.put(names_expected[i], i);

            double[] stder_actual = model._output.stdErr();
            double[] zvals_actual = model._output.zValues();
            double[] pvals_actual = model._output.pValues();
            for (int i = 0; i < (stder_expected.length); ++i) {
                int id = coefMap.get(names_actual[i]);
                Assert.assertEquals(stder_expected[id], stder_actual[i], ((stder_expected[id]) * 1.0E-5));
                Assert.assertEquals(zvals_expected[id], zvals_actual[i], ((Math.abs(zvals_expected[id])) * 1.0E-5));
                Assert.assertEquals(pvals_expected[id], pvals_actual[i], ((pvals_expected[id]) * 0.001));
            }
            predict = model.score(params._train.get());
            Vec.Reader r = predict.vec("StdErr").new Reader();
            for (int i = 0; i < 10; i++)
                System.out.println((((fit_se[i]) + " ?=? ") + (r.at(i))));

            for (int i = 0; i < (fit_se.length); ++i)
                Assert.assertEquals(fit_se[i], r.at(i), 1.0E-4);

        } finally {
            if (model != null)
                model.delete();

            if (predict != null)
                predict.delete();

        }
        // 2) STANDARDIZED
        // Call:
        // glm(formula = CAPSULE ~ ., family = binomial, data = Dstd)
        // 
        // Deviance Residuals:
        // Min       1Q   Median       3Q      Max
        // -2.0601  -0.8079  -0.4491   0.8933   2.2877
        // 
        // Coefficients:
        // Estimate Std. Error z value Pr(>|z|)
        // (Intercept) -1.28045    1.56879  -0.816  0.41438
        // ID           0.19054    0.15341   1.242  0.21420
        // AGE         -0.02118    0.14498  -0.146  0.88384
        // RACER2       0.06831    1.54240   0.044  0.96468
        // RACER3      -0.74113    1.58272  -0.468  0.63959
        // DPROSb       0.88833    0.39509   2.248  0.02455 *
        // DPROSc       1.30594    0.41620   3.138  0.00170 **
        // DPROSd       0.78440    0.54265   1.446  0.14832
        // DCAPSb       0.61237    0.51796   1.182  0.23710
        // PSA          0.60917    0.22447   2.714  0.00665 **
        // VOL         -0.18130    0.16204  -1.119  0.26320
        // GLEASON      0.91751    0.19633   4.673 2.96e-06 ***
        // ---
        // Signif. codes:  0 '***' 0.001 '**' 0.01 '*' 0.05 '.' 0.1 ' ' 1
        // 
        // (Dispersion parameter for binomial family taken to be 1)
        // 
        // Null deviance: 390.35  on 289  degrees of freedom
        // Residual deviance: 297.65  on 278  degrees of freedom
        // AIC: 321.65
        // 
        // Number of Fisher Scoring iterations: 5
        // Estimate Std. Error     z value     Pr(>|z|)
        // (Intercept) -1.28045434  1.5687858 -0.81620723 4.143816e-01
        // ID           0.19054396  0.1534062  1.24208800 2.142041e-01
        // AGE         -0.02118315  0.1449847 -0.14610616 8.838376e-01
        // RACER2       0.06830776  1.5423974  0.04428674 9.646758e-01
        // RACER3      -0.74113331  1.5827190 -0.46826589 6.395945e-01
        // DPROSb       0.88832948  0.3950883  2.24843259 2.454862e-02
        // DPROSc       1.30594011  0.4161974  3.13779030 1.702266e-03
        // DPROSd       0.78440312  0.5426512  1.44550154 1.483171e-01
        // DCAPSb       0.61237150  0.5179591  1.18227779 2.370955e-01
        // PSA          0.60917093  0.2244733  2.71377864 6.652060e-03
        // VOL         -0.18129997  0.1620383 -1.11887108 2.631951e-01
        // GLEASON      0.91750972  0.1963285  4.67333842 2.963429e-06
        params._standardize = true;
        try {
            model = trainModel().get();
            String[] names_expected = new String[]{ "Intercept", "ID", "AGE", "RACE.R2", "RACE.R3", "DPROS.b", "DPROS.c", "DPROS.d", "DCAPS.b", "PSA", "VOL", "GLEASON" };
            // do not compare std_err here, depends on the coefficients
            // double[] stder_expected = new double[]{1.5687858,   0.1534062,   0.1449847,   1.5423974, 1.5827190,   0.3950883,   0.4161974,  0.5426512,   0.5179591,   0.2244733, 0.1620383,   0.1963285};
            // double[] zvals_expected = new double[]{1.14158283,  1.29061005, -0.14920829, -0.05883397, -0.56178799, 2.22564893,  3.21891333,  1.22168646,  1.61119882,  3.13650800, -1.39379859,  5.26524961 };
            // double[] pvals_expected = new double[]{2.546098e-01, 1.979113e-01, 8.814975e-01, 9.531266e-01, 5.747131e-01, 2.683977e-02, 1.439295e-03, 2.228612e-01, 1.082711e-01, 1.893210e-03, 1.644916e-01, 2.805776e-07 };
            double[] stder_expected = new double[]{ 0.4035941476, 2.387281E-4, 0.004024552, 0.251100712, 0.2593492335, 0.0657117271, 0.0713659021, 0.0937207659, 0.0888124376, 0.0015060289, 0.0013919737, 0.0273258788 };
            double[] zvals_expected = new double[]{ -1.70241133, 1.29061005, -0.14920829, -0.05883397, -0.56178799, 2.22564893, 3.21891333, 1.22168646, 1.61119882, 3.136508, -1.39379859, 5.26524961 };
            double[] pvals_expected = new double[]{ 0.0897961, 0.1979113, 0.8814975, 0.9531266, 0.5747131, 0.02683977, 0.001439295, 0.2228612, 0.1082711, 0.00189321, 0.1644916, 2.805776E-7 };
            String[] names_actual = model._output.coefficientNames();
            HashMap<String, Integer> coefMap = new HashMap<>();
            for (int i = 0; i < (names_expected.length); ++i)
                coefMap.put(names_expected[i], i);

            double[] zvals_actual = model._output.zValues();
            double[] pvals_actual = model._output.pValues();
            for (int i = 0; i < (zvals_expected.length); ++i) {
                int id = coefMap.get(names_actual[i]);
                Assert.assertEquals(zvals_expected[id], zvals_actual[i], ((Math.abs(zvals_expected[id])) * 1.0E-5));
                Assert.assertEquals(pvals_expected[id], pvals_actual[i], ((pvals_expected[id]) * 0.001));
            }
            predict = model.score(params._train.get());
            Vec.Reader r = predict.vec("StdErr").new Reader();
            for (int i = 0; i < 10; i++)
                System.out.println((((fit_se[i]) + " ?=? ") + (r.at(i))));

            for (int i = 0; i < (fit_se.length); ++i)
                Assert.assertEquals(fit_se[i], r.at(i), 1.0E-4);

        } finally {
            if (model != null)
                model.delete();

            if (predict != null)
                predict.delete();

        }
        // Airlines (has collinear columns)
        params._standardize = true;
        params._remove_collinear_columns = true;
        params._train = GLMBasicTestRegression._airlines._key;
        params._response_column = "IsDepDelayed";
        params._ignored_columns = new String[]{ "IsDepDelayed_REC" };
        try {
            model = trainModel().get();
            String[] names_expected = new String[]{ "Intercept", "fYearf1988", "fYearf1989", "fYearf1990", "fYearf1991", "fYearf1992", "fYearf1993", "fYearf1994", "fYearf1995", "fYearf1996", "fYearf1997", "fYearf1998", "fYearf1999", "fYearf2000", "fDayofMonthf10", "fDayofMonthf11", "fDayofMonthf12", "fDayofMonthf13", "fDayofMonthf14", "fDayofMonthf15", "fDayofMonthf16", "fDayofMonthf17", "fDayofMonthf18", "fDayofMonthf19", "fDayofMonthf2", "fDayofMonthf20", "fDayofMonthf21", "fDayofMonthf22", "fDayofMonthf23", "fDayofMonthf24", "fDayofMonthf25", "fDayofMonthf26", "fDayofMonthf27", "fDayofMonthf28", "fDayofMonthf29", "fDayofMonthf3", "fDayofMonthf30", "fDayofMonthf31", "fDayofMonthf4", "fDayofMonthf5", "fDayofMonthf6", "fDayofMonthf7", "fDayofMonthf8", "fDayofMonthf9", "fDayOfWeekf2", "fDayOfWeekf3", "fDayOfWeekf4", "fDayOfWeekf5", "fDayOfWeekf6", "fDayOfWeekf7", "DepTime", "ArrTime", "UniqueCarrierCO", "UniqueCarrierDL", "UniqueCarrierHP", "UniqueCarrierPI", "UniqueCarrierTW", "UniqueCarrierUA", "UniqueCarrierUS", "UniqueCarrierWN", "OriginABQ", "OriginACY", "OriginALB", "OriginATL", "OriginAUS", "OriginAVP", "OriginBDL", "OriginBGM", "OriginBHM", "OriginBNA", "OriginBOS", "OriginBTV", "OriginBUF", "OriginBUR", "OriginBWI", "OriginCAE", "OriginCHO", "OriginCHS", "OriginCLE", "OriginCLT", "OriginCMH", "OriginCOS", "OriginCRW", "OriginCVG", "OriginDAY", "OriginDCA", "OriginDEN", "OriginDFW", "OriginDSM", "OriginDTW", "OriginERI", "OriginEWR", "OriginFLL", "OriginGSO", "OriginHNL", "OriginIAD", "OriginIAH", "OriginICT", "OriginIND", "OriginISP", "OriginJAX", "OriginJFK", "OriginLAS", "OriginLAX", "OriginLEX", "OriginLGA", "OriginLIH", "OriginLYH", "OriginMCI", "OriginMCO", "OriginMDT", "OriginMDW", "OriginMFR", "OriginMHT", "OriginMIA", "OriginMKE", "OriginMLB", "OriginMRY", "OriginMSP", "OriginMSY", "OriginMYR", "OriginOAK", "OriginOGG", "OriginOMA", "OriginORD", "OriginORF", "OriginPBI", "OriginPHF", "OriginPHL", "OriginPHX", "OriginPIT", "OriginPSP", "OriginPVD", "OriginPWM", "OriginRDU", "OriginRIC", "OriginRNO", "OriginROA", "OriginROC", "OriginRSW", "OriginSAN", "OriginSBN", "OriginSCK", "OriginSDF", "OriginSEA", "OriginSFO", "OriginSJC", "OriginSJU", "OriginSLC", "OriginSMF", "OriginSNA", "OriginSRQ", "OriginSTL", "OriginSTX", "OriginSWF", "OriginSYR", "OriginTLH", "OriginTPA", "OriginTRI", "OriginTUS", "OriginTYS", "OriginUCA", "DestABQ", "DestACY", "DestALB", "DestATL", "DestAVP", "DestBDL", "DestBGM", "DestBNA", "DestBOS", "DestBTV", "DestBUF", "DestBUR", "DestBWI", "DestCAE", "DestCAK", "DestCHA", "DestCHS", "DestCLE", "DestCLT", "DestCMH", "DestDAY", "DestDCA", "DestDEN", "DestDFW", "DestDTW", "DestELM", "DestERI", "DestEWR", "DestFAT", "DestFAY", "DestFLL", "DestFNT", "DestGEG", "DestGRR", "DestGSO", "DestGSP", "DestHNL", "DestHTS", "DestIAD", "DestIAH", "DestICT", "DestIND", "DestISP", "DestJAX", "DestJFK", "DestKOA", "DestLAS", "DestLAX", "DestLEX", "DestLGA", "DestLIH", "DestLYH", "DestMCI", "DestMCO", "DestMDT", "DestMDW", "DestMHT", "DestMIA", "DestMRY", "DestMSY", "DestOAJ", "DestOAK", "DestOGG", "DestOMA", "DestORD", "DestORF", "DestORH", "DestPBI", "DestPDX", "DestPHF", "DestPHL", "DestPHX", "DestPIT", "DestPSP", "DestPVD", "DestRDU", "DestRIC", "DestRNO", "DestROA", "DestROC", "DestRSW", "DestSAN", "DestSCK", "DestSDF", "DestSEA", "DestSFO", "DestSJC", "DestSMF", "DestSNA", "DestSTL", "DestSWF", "DestSYR", "DestTOL", "DestTPA", "DestTUS", "DestUCA", "Distance" };
            double[] exp_coefs = new double[]{ 0.3383044, -0.1168214, -0.4405621, -0.3365341, -0.4925256, -0.5374542, -0.4149143, -0.2694969, -0.2991095, -0.2776553, -0.2921466, -0.4336252, -0.3597812, -0.3812643, 0.01024025, 0.02549787, 0.03877628, 0.01650942, -0.02981043, -0.01167855, 0.01025499, -0.004574083, -0.02502898, -0.05803535, 0.07679039, -0.05247306, -0.05918685, -0.03339667, -0.02885718, -0.04225694, -0.07500997, -0.05145179, -0.07093373, -0.05634115, -0.03643811, 0.1284665, -0.08150175, -0.04724434, 0.1511024, 0.05498057, 0.0441163, 0.01278961, 0.007276038, 0.04672048, -0.02128594, 0.01629933, 0.03721499, 0.05933446, -0.02303705, 0.01141451, 1.258241E-4, 1.271866E-5, 0.07155502, 0.144499, -0.08685535, -0.02602512, 0.4227022, 0.2639493, 0.2600565, 0.05409442, 0.05106308, -0.1993041, 0.5663324, 0.2524168, -0.08032071, 0.01959854, 0.3110741, 0.2711911, -0.1480432, 0.02711969, 0.1298365, 0.3051547, 0.1747017, -0.006282101, 0.1542743, -0.3037726, 0.3808392, 0.1829607, 0.04841763, 0.09353007, 0.2154611, 0.06469679, -0.1950998, 0.07957484, 0.2430247, 0.01942201, 0.05701321, 0.2770389, 0.1497383, 0.04943089, 0.2598871, 0.0593068, 0.3748394, 0.04204685, -0.3574776, 0.02153817, -0.1719974, 0.480682, 0.2678204, 0.04266956, 0.06340217, -0.01536324, -0.01294344, 0.1985872, 0.4831069, 0.2726364, -0.4813763, 0.4199029, 0.3054954, 0.178433, -0.02500409, 0.002978489, -0.09356699, 0.124628, 0.2858306, -0.06533971, -0.1403327, -0.3924693, 0.05947271, -0.007903152, -0.2135489, -0.1454085, -0.2049959, 0.170425, 0.1826566, 0.1896976, 0.2541375, -0.09746707, 0.1990703, 0.09068512, 0.2848977, 0.3409567, 0.08689141, -0.06294297, 0.02402344, 0.09583028, 0.4207585, 0.209637, 0.2184863, 0.1316822, 0.04863172, 0.4918303, -0.07990361, -0.04499847, 0.06140887, 0.07329919, -0.1658663, 0.1850334, -0.2165094, -0.1054388, 0.08943775, 0.3809166, -0.09766444, 0.2645371, -0.05147078, 0.2323637, -0.3746418, 0.1841517, -0.2121584, -0.01888144, -0.08009574, 0.1801828, 0.1216036, 0.00412319, -0.04747419, -0.1001471, 0.03611426, 0.1427218, -0.1154052, -0.2388724, -0.008097489, -0.0332189, -0.08470654, 0.008609431, 0.02278746, 0.2959335, -0.08363623, -0.1736324, 0.2140292, -0.1252043, 0.02086573, 0.07549936, -0.2339204, 0.1009014, 0.1396302, -0.2180753, -0.01118935, -0.3345582, -0.1490167, -0.005455654, -0.02884281, -0.07778542, 0.1481921, -0.09387787, 0.2894362, -0.2599589, 0.1210906, 0.0172167, 0.06271491, -0.507702, 0.2524418, -0.1146321, -0.341803, -0.007056448, -0.1948121, -0.1716377, -0.05915873, 0.3465761, -0.03964155, 0.09297146, 0.06840982, -0.02694979, 0.3489802, 0.4473631, 0.09045849, 0.1195621, 8.137467E-4, -0.08754947, 0.02089706, 0.002676953, -0.1381342, 0.05200934, 0.2208028, -0.1096369, 0.4753661, 0.02876296, 0.02256874, -0.0923127, 0.02507403, 0.1529442, -0.0217319, -0.1180872, -0.03305849, 0.1091687, 0.09174085, -0.06172636, 0.05983764, 0.1094581, 0.1537772, 0.1117601, -0.09674298, 0.03111324, 0.1404767, -0.004243193, 0.09218955, 0.2554272, -0.04434348, 0.1222306, 0.01960349, 0.1308767, -0.002830042, -0.03212863, -0.1035897, -0.02828326, -0.2452788, 0.05876054, 0.06094385, -0.06242541, 5.535717E-5 };
            double[] stder_expected = new double[]{ 0.08262325, 0.01960654, 0.05784259, 0.05211346, 0.05351436, 0.05364119, 0.05377681, 0.05361611, 0.0548021, 0.0591653, 0.05924352, 0.05947477, 0.05684859, 0.06015367, 0.02359873, 0.02364261, 0.02366028, 0.02346965, 0.02331776, 0.02348358, 0.02366537, 0.02371736, 0.02353753, 0.02345702, 0.02360676, 0.02353096, 0.02352809, 0.02354292, 0.02381824, 0.02360087, 0.02357901, 0.02352439, 0.0233382, 0.0234815, 0.02349408, 0.02388143, 0.02363605, 0.02369714, 0.02384589, 0.02360301, 0.02346261, 0.02365805, 0.02377684, 0.02374369, 0.01093338, 0.01091722, 0.01094858, 0.01089616, 0.01127837, 0.01099223, 1.24315E-5, 1.193431E-5, 0.06185154, 0.05842257, 0.0479784, 0.04082146, 0.06764477, 0.04904281, 0.04661126, 0.04949252, 0.0719463, 0.1080608, 0.1000542, 0.07206225, 0.06866783, 0.09183712, 0.08937756, 0.09509039, 0.1101394, 0.0733384, 0.06976195, 0.1139758, 0.07902871, 0.06688118, 0.06842836, 0.1228471, 0.1290408, 0.08980176, 0.06808851, 0.07095243, 0.06932701, 0.07036599, 0.1021726, 0.0756629, 0.07743516, 0.07012655, 0.06722331, 0.07756484, 0.2146603, 0.08390956, 0.1138773, 0.06896196, 0.08394126, 0.07983643, 0.08101956, 0.08960544, 0.08278554, 0.2417453, 0.06988129, 0.1085592, 0.0927458, 0.1206031, 0.07400875, 0.06750358, 0.1107047, 0.06957462, 0.1139873, 0.1340117, 0.07976223, 0.06979235, 0.07837532, 0.1285433, 0.1334371, 0.1198966, 0.08332708, 0.1229658, 0.1149044, 0.1130423, 0.1090638, 0.0840653, 0.09600642, 0.07247142, 0.1140837, 0.09506082, 0.06926602, 0.07590418, 0.07459985, 0.128707, 0.06815592, 0.07411458, 0.06592406, 0.09179115, 0.07223151, 0.07670526, 0.07764917, 0.07343286, 0.1999711, 0.1175572, 0.07108214, 0.07409246, 0.06847739, 0.2476394, 0.1080218, 0.1120317, 0.08137946, 0.0675466, 0.07897969, 0.078673, 0.1044366, 0.08260141, 0.07542126, 0.1116638, 0.07481728, 0.1126226, 0.1286945, 0.07009628, 0.1346972, 0.06941736, 0.1228611, 0.07884636, 0.1089254, 0.117896, 0.06487494, 0.1141428, 0.06337383, 0.1044082, 0.09881149, 0.06748862, 0.07802332, 0.07989152, 0.04877654, 0.08606809, 0.06446482, 0.0527663, 0.05072148, 0.1073048, 0.1054882, 0.2695275, 0.08023848, 0.0566585, 0.05273383, 0.0609645, 0.0790702, 0.0526107, 0.0518043, 0.1142093, 0.05580208, 0.2354317, 0.2681434, 0.05047968, 0.1029695, 0.07947606, 0.0616762, 0.12601, 0.1094464, 0.1044411, 0.06861138, 0.1122694, 0.06168966, 0.1033369, 0.09571271, 0.0595864, 0.1168745, 0.04831583, 0.07683862, 0.07909215, 0.0839785, 0.1069573, 0.05494288, 0.04744649, 0.2133179, 0.05407477, 0.1070343, 0.1207816, 0.05898603, 0.05647888, 0.107607, 0.07977657, 0.2690687, 0.1077435, 0.3279724, 0.1140342, 0.1154527, 0.05419787, 0.1098867, 0.1049436, 0.05082173, 0.06118521, 0.2107675, 0.0775813, 0.07001571, 0.1073186, 0.0496334, 0.05394587, 0.04612111, 0.07909675, 0.07081853, 0.07685204, 0.1132175, 0.06811432, 0.1231347, 0.07004574, 0.1089064, 0.05191893, 0.2689951, 0.3267575, 0.1008663, 0.04802894, 0.06230837, 0.1109208, 0.06627911, 0.08130255, 0.1094653, 0.05568541, 0.09874917, 0.05701293, 0.07421695, 0.139304, 8.828166E-6 };
            double[] zvals_expected = new double[]{ 4.094542787, -5.958287216, -7.616568859, -6.457719779, -9.203616729, -10.019431514, -7.715486715, -5.026416071, -5.457993778, -4.69287333, -4.931283164, -7.290910329, -6.328761834, -6.338172537, 0.433932435, 1.078470756, 1.638876645, 0.703437006, -1.278442927, -0.497307097, 0.433333243, -0.192857977, -1.063364622, -2.474114538, 3.252898021, -2.229957876, -2.515581926, -1.4185443, -1.211558333, -1.790482149, -3.181217772, -2.187168299, -3.039383181, -2.399384279, -1.550948532, 5.37934743, -3.448197372, -1.993672779, 6.336619935, 2.329387922, 1.880280495, 0.540602946, 0.306013647, 1.967700936, -1.946876164, 1.492992904, 3.39907, 5.445447763, -2.042586155, 1.038415698, 10.121391544, 1.065722732, 1.156883305, 2.473342141, -1.810301011, -0.637535148, 6.248852336, 5.382017137, 5.579262483, 1.092981554, 0.7097387, -1.844369286, 5.660253498, 3.50276059, -1.169699226, 0.213405474, 3.480449353, 2.851929432, -1.344144285, 0.36978843, 1.861136098, 2.677364258, 2.210610981, -0.093929275, 2.254537133, -2.472769164, 2.951309213, 2.037384116, 0.711098412, 1.318208131, 3.107896103, 0.9194327, -1.909512146, 1.051702242, 3.138428148, 0.276956565, 0.848116635, 3.571707066, 0.697559521, 0.589097196, 2.282167858, 0.859992866, 4.465496733, 0.526662485, -4.4122379, 0.240366792, -2.077626172, 1.988382134, 3.832504877, 0.393053401, 0.683612236, -0.127386732, -0.174890682, 2.941876192, 4.363924639, 3.918618005, -4.22306923, 3.133329974, 3.830075787, 2.556626718, -0.319030122, 0.023171093, -0.701206599, 1.039462023, 3.430224574, -0.531365033, -1.221299087, -3.471879279, 0.545302258, -0.094012061, -2.224318698, -2.006425249, -1.796891224, 1.792799985, 2.637030567, 2.499172484, 3.406675572, -0.757278429, 2.920806636, 1.223580021, 4.32160403, 3.714483644, 1.202957243, -0.8205821, 0.309384395, 1.30500541, 2.104096487, 1.78327625, 3.073716393, 1.777268003, 0.710186572, 1.986074319, -0.739698581, -0.401658318, 0.754599117, 1.085164836, -2.100113836, 2.351929605, -2.073117895, -1.276477554, 1.185842715, 3.411279361, -1.305372693, 2.34888119, -0.39994546, 3.314921843, -2.781362443, 2.652819094, -1.726814586, -0.239471355, -0.735326303, 1.528320564, 1.874431642, 0.036123072, -0.749113504, -0.959187862, 0.365486433, 2.114753848, -1.479111228, -2.989960091, -0.166011968, -0.385960754, -1.31399638, 0.163161548, 0.449266356, 2.757878211, -0.792848866, -0.644210444, 2.667413046, -2.209806237, 0.39568007, 1.238415063, -2.958388604, 1.917886602, 2.695339429, -1.909435881, -0.200518466, -1.421041172, -0.555735022, -0.108076244, -0.280110165, -0.978727707, 2.40274372, -0.745003303, 2.644547495, -2.489047558, 1.764876031, 0.153351678, 1.016619439, -4.913074631, 2.637495147, -1.923795851, -2.92453117, -0.14604835, -2.535340379, -2.170097247, -0.704450917, 3.2403229, -0.721504747, 1.959501429, 0.320694226, -0.498380144, 3.260451771, 3.703901572, 1.533557842, 2.116934588, 0.007562213, -1.097433427, 0.077664399, 0.02484561, -0.421176265, 0.45608559, 1.91249564, -2.022900342, 4.325966158, 0.27408025, 0.444076651, -1.508741999, 0.118965357, 1.971405595, -0.310386063, -1.100341487, -0.666053319, 2.023671705, 1.989128987, -0.780390564, 0.844943233, 1.424270602, 1.358245515, 1.640771855, -0.785668134, 0.444184574, 1.289884997, -0.081727285, 0.342718282, 0.781702452, -0.439626096, 2.544935912, 0.314620525, 1.179911368, -0.042698859, -0.395173744, -0.946324367, -0.507911593, -2.483856451, 1.030652857, 0.821158118, -0.448123713, 6.270517743 };
            double[] pvals_expected = new double[]{ 4.243779E-5, 2.584251E-9, 2.700448E-14, 1.083124E-10, 3.733573E-20, 1.392306E-23, 1.251677E-14, 5.032991E-7, 4.862783E-8, 2.708701E-6, 8.223295E-7, 3.173337E-13, 2.514741E-10, 2.366114E-10, 0.6643414, 0.2808345, 0.101252, 0.4817902, 0.2011056, 0.618977, 0.6647766, 0.8470718, 0.2876273, 0.0133635, 0.001143912, 0.02575939, 0.01189004, 0.1560448, 0.2256933, 0.07338895, 0.001468427, 0.02873979, 0.002373166, 0.01643019, 0.1209271, 7.545084E-8, 5.653029E-4, 0.04619905, 2.39003E-10, 0.01984672, 0.06008188, 0.5887863, 0.7595969, 0.04911388, 0.05156115, 0.1354521, 6.772461E-4, 5.217869E-8, 0.04110425, 0.299087, 4.958814E-24, 0.2865596, 0.2473315, 0.01339242, 0.07026154, 0.5237824, 4.203831E-10, 7.434137E-8, 2.441273E-8, 0.2744128, 0.477873, 0.06514157, 1.52862E-8, 4.612949E-4, 0.2421336, 0.8310125, 5.014564E-4, 0.00434916, 0.1789144, 0.7115434, 0.0627371, 0.007425404, 0.02707212, 0.9251661, 0.02417131, 0.0134139, 0.003167338, 0.04162244, 0.4770301, 0.1874465, 0.001886429, 0.3578785, 0.05620789, 0.2929467, 0.001700608, 0.7818158, 0.3963814, 3.55351E-4, 0.4854594, 0.5558016, 0.02248808, 0.3898015, 8.024485E-6, 0.5984328, 1.0275E-5, 0.810048, 0.03775434, 0.04678071, 1.271662E-4, 0.6942835, 0.4942266, 0.8986354, 0.861167, 0.0032654, 1.282792E-5, 8.930334E-5, 2.418793E-5, 0.001730416, 1.284274E-4, 0.01057531, 0.7497064, 0.981514, 0.4831808, 0.2986003, 6.040895E-4, 0.5951707, 0.2219847, 5.17732E-4, 0.5855507, 0.9251004, 0.02613621, 0.04482202, 0.07236537, 0.07301742, 0.008368881, 0.01245495, 6.586634E-4, 0.4488905, 0.003494493, 0.2211226, 1.555177E-5, 2.040773E-4, 0.2290047, 0.4118924, 0.7570318, 0.1919034, 0.03538034, 0.07455389, 0.002116458, 0.07553673, 0.4775953, 0.04703635, 0.4594901, 0.6879391, 0.4504969, 0.2778595, 0.03572917, 0.01868429, 0.03817188, 0.201799, 0.2356961, 6.476464E-4, 0.1917784, 0.01883792, 0.6892002, 9.180365E-4, 0.00541732, 0.007987482, 0.08421375, 0.8107421, 0.4621479, 0.1264461, 0.06088301, 0.9711845, 0.4537961, 0.3374737, 0.7147515, 0.03446114, 0.1391236, 0.002792949, 0.8681489, 0.6995291, 0.1888599, 0.8703926, 0.6532436, 0.005822158, 0.4278737, 0.5194451, 0.007648862, 0.02712795, 0.6923446, 0.2155742, 0.003095516, 0.05513717, 0.007036561, 0.05621772, 0.8410768, 0.1553177, 0.5783972, 0.9139361, 0.7793954, 0.3277243, 0.01628008, 0.456277, 0.00818531, 0.01281526, 0.07759723, 0.8781222, 0.3093447, 9.024527E-7, 0.00835743, 0.05439191, 0.00345296, 0.8838844, 0.01124006, 0.03000919, 0.4811588, 0.001195559, 0.470606, 0.05006557, 0.7484449, 0.6182207, 0.001113888, 2.127814E-4, 0.1251516, 0.03427559, 0.9939663, 0.2724629, 0.9380957, 0.9801783, 0.6736301, 0.6483325, 0.05582446, 0.04309441, 1.524737E-5, 0.7840253, 0.6569911, 0.1313778, 0.9053038, 0.04868889, 0.7562701, 0.2711943, 0.5053834, 0.04301493, 0.04669822, 0.4351687, 0.3981509, 0.1543811, 0.1743985, 0.1008578, 0.4320696, 0.6569131, 0.1971029, 0.9348643, 0.7318134, 0.4343971, 0.6602119, 0.01093594, 0.7530525, 0.2380471, 0.9659419, 0.6927182, 0.3439926, 0.61152, 0.01300354, 0.302714, 0.4115643, 0.6540679, 3.6594E-10 };
            double[] stder_actual = model._output.stdErr();
            double[] zvals_actual = model._output.zValues();
            double[] pvals_actual = model._output.pValues();
            String[] names_actual = model._output.coefficientNames();
            HashMap<String, Integer> coefMap = new HashMap<>();
            for (int i = 0; i < (names_expected.length); ++i)
                coefMap.put(names_expected[i], i);

            double[] coefs_actual = model._output._global_beta;
            for (int i = 0; i < (exp_coefs.length); ++i) {
                String s = GLMBasicTestRegression.removeDot(names_actual[i]);
                if (!(coefMap.containsKey(s))) {
                    // removed col, check we removed it too
                    Assert.assertTrue((((coefs_actual[i]) == 0) && (Double.isNaN(zvals_actual[i]))));
                    System.out.println(("found removed col " + s));
                } else {
                    int id = coefMap.get(s);
                    Assert.assertEquals(exp_coefs[id], coefs_actual[i], 1.0E-4);
                    Assert.assertEquals(stder_expected[id], stder_actual[i], Math.abs(((stder_expected[id]) * 1.0E-4)));
                    Assert.assertEquals(zvals_expected[id], zvals_actual[i], Math.abs(((zvals_expected[id]) * 1.0E-4)));
                    Assert.assertEquals(pvals_expected[id], pvals_actual[i], ((pvals_expected[id]) * 1.0E-4));
                }
            }
            predict = model.score(GLMBasicTestRegression._airlines);
        } finally {
            if (model != null)
                model.delete();

            if (predict != null)
                predict.delete();

        }
    }
}

