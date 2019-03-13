package hex;


import DRFModel.DRFParameters;
import DeepLearningModel.DeepLearningParameters;
import GBMModel.GBMParameters;
import GLMModel.GLMParameters;
import hex.deeplearning.DeepLearning;
import hex.deeplearning.DeepLearningModel;
import hex.genmodel.utils.DistributionFamily;
import hex.glm.GLM;
import hex.glm.GLMModel;
import hex.tree.drf.DRF;
import hex.tree.drf.DRFModel;
import hex.tree.gbm.GBM;
import hex.tree.gbm.GBMModel;
import org.junit.Test;
import water.fvec.Frame;
import water.fvec.Vec;
import water.rapids.ast.prims.advmath.AstKFold;

import static water.TestUtil.<init>;


/**
 * This test is intended to corroborate the documented description of cross-validated
 * predictions as a result of model building. These datasets have identifiers of the form
 *         *_cv_1, *_cv_2, ..., *_cv_n
 *
 * This test makes GBM, DRF, GLM, and DL models with a randomized fold column, and it
 * checks that each *_cv_n contain predictions consistent with the fold column on the
 * original frame.
 */
public class XValPredictionsCheck extends TestUtil {
    @Test
    public void testXValPredictions() {
        final int nfolds = 3;
        Frame tfr = null;
        try {
            // Load data, hack frames
            tfr = parse_test_file("smalldata/iris/iris_wheader.csv");
            Frame foldId = new Frame(new String[]{ "foldId" }, new Vec[]{ AstKFold.kfoldColumn(tfr.vec("class").makeZero(), nfolds, 543216789) });
            tfr.add(foldId);
            DKV.put(tfr);
            // GBM
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "class";
            parms._ntrees = 1;
            parms._max_depth = 1;
            parms._fold_column = "foldId";
            parms._distribution = DistributionFamily.multinomial;
            parms._keep_cross_validation_predictions = true;
            GBM job = new GBM(parms);
            GBMModel gbm = job.trainModel().get();
            checkModel(gbm, foldId.anyVec(), 3);
            // DRF
            DRFModel.DRFParameters parmsDRF = new DRFModel.DRFParameters();
            parmsDRF._train = tfr._key;
            parmsDRF._response_column = "class";
            parmsDRF._ntrees = 1;
            parmsDRF._max_depth = 1;
            parmsDRF._fold_column = "foldId";
            parmsDRF._distribution = DistributionFamily.multinomial;
            parmsDRF._keep_cross_validation_predictions = true;
            DRF drfJob = new DRF(parmsDRF);
            DRFModel drf = drfJob.trainModel().get();
            checkModel(drf, foldId.anyVec(), 3);
            // GLM
            GLMModel.GLMParameters parmsGLM = new GLMModel.GLMParameters();
            parmsGLM._train = tfr._key;
            parmsGLM._response_column = "sepal_len";
            parmsGLM._fold_column = "foldId";
            parmsGLM._keep_cross_validation_predictions = true;
            GLM glmJob = new GLM(parmsGLM);
            GLMModel glm = glmJob.trainModel().get();
            checkModel(glm, foldId.anyVec(), 1);
            // DL
            DeepLearningModel.DeepLearningParameters parmsDL = new DeepLearningModel.DeepLearningParameters();
            parmsDL._train = tfr._key;
            parmsDL._response_column = "class";
            parmsDL._hidden = new int[]{ 1 };
            parmsDL._epochs = 1;
            parmsDL._fold_column = "foldId";
            parmsDL._keep_cross_validation_predictions = true;
            DeepLearning dlJob = new DeepLearning(parmsDL);
            DeepLearningModel dl = dlJob.trainModel().get();
            checkModel(dl, foldId.anyVec(), 3);
        } finally {
            if (tfr != null)
                tfr.remove();

        }
    }
}

