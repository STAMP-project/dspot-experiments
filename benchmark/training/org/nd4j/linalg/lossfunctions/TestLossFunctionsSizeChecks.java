package org.nd4j.linalg.lossfunctions;


import org.junit.Test;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;


/**
 * This is intended to ensure that if the incorrect size of data is given
 * to the loss functions, that the report this fact through an appropriate
 * exception.  This functionality used to be performed at the 'deeplearning4j'
 * level, but it was discovered that many loss functions perform mappings
 * involving 'label' sizes different than 'output' sizes.  Such an example
 * would be the Bishop Mixture Density Network.  Hence the testing for
 * loss function size was moved to being the responsibility of the loss function
 * to enforce.
 *
 * @author Jonathan S. Arney.
 */
public class TestLossFunctionsSizeChecks {
    @Test
    public void testL2() {
        LossFunction[] lossFunctionList = new LossFunction[]{ LossFunction.MSE, LossFunction.L1, LossFunction.EXPLL, LossFunction.XENT, LossFunction.MCXENT, LossFunction.SQUARED_LOSS, LossFunction.RECONSTRUCTION_CROSSENTROPY, LossFunction.NEGATIVELOGLIKELIHOOD, LossFunction.COSINE_PROXIMITY, LossFunction.HINGE, LossFunction.SQUARED_HINGE, LossFunction.KL_DIVERGENCE, LossFunction.MEAN_ABSOLUTE_ERROR, LossFunction.L2, LossFunction.MEAN_ABSOLUTE_PERCENTAGE_ERROR, LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR, LossFunction.POISSON };
        testLossFunctions(lossFunctionList);
    }
}

