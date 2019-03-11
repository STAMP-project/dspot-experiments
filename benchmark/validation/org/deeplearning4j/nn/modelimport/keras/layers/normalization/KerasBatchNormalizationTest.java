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
package org.deeplearning4j.nn.modelimport.keras.layers.normalization;


import java.util.Map;
import org.deeplearning4j.nn.modelimport.keras.config.Keras1LayerConfiguration;
import org.deeplearning4j.nn.modelimport.keras.config.Keras2LayerConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;


/**
 *
 *
 * @author Max Pumperla
 */
public class KerasBatchNormalizationTest {
    public static final String PARAM_NAME_BETA = "beta";

    private final String LAYER_NAME = "batch_norm_layer";

    private Integer keras1 = 1;

    private Integer keras2 = 2;

    private Keras1LayerConfiguration conf1 = new Keras1LayerConfiguration();

    private Keras2LayerConfiguration conf2 = new Keras2LayerConfiguration();

    @Test
    public void testBatchnormLayer() throws Exception {
        buildBatchNormalizationLayer(conf1, keras1);
        buildBatchNormalizationLayer(conf2, keras2);
    }

    @Test
    public void testSetWeights() throws Exception {
        Map<String, INDArray> weights = weightsWithoutGamma();
        KerasBatchNormalization batchNormalization = new KerasBatchNormalization(keras2);
        batchNormalization.setScale(false);
        batchNormalization.setWeights(weights);
        int size = batchNormalization.getWeights().size();
        Assert.assertEquals(4, size);
    }
}

