/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
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

