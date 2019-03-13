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
package org.deeplearning4j.nn.modelimport.keras.layers.wrappers;


import org.deeplearning4j.nn.modelimport.keras.config.Keras1LayerConfiguration;
import org.deeplearning4j.nn.modelimport.keras.config.Keras2LayerConfiguration;
import org.deeplearning4j.nn.weights.WeightInit;
import org.junit.Test;


/**
 *
 *
 * @author Max Pumperla
 */
public class KerasBidirectionalTest {
    private final String ACTIVATION_KERAS = "linear";

    private final String ACTIVATION_DL4J = "identity";

    private final String LAYER_NAME = "bidirectional_layer";

    private final String INIT_KERAS = "glorot_normal";

    private final WeightInit INIT_DL4J = WeightInit.XAVIER;

    private final double L1_REGULARIZATION = 0.01;

    private final double L2_REGULARIZATION = 0.02;

    private final double DROPOUT_KERAS = 0.3;

    private final double DROPOUT_DL4J = 1 - (DROPOUT_KERAS);

    private final int N_OUT = 13;

    private final String mode = "sum";

    private Integer keras1 = 1;

    private Integer keras2 = 2;

    private Keras1LayerConfiguration conf1 = new Keras1LayerConfiguration();

    private Keras2LayerConfiguration conf2 = new Keras2LayerConfiguration();

    @Test
    public void testLstmLayer() throws Exception {
        buildLstmLayer(conf1, keras1);
        buildLstmLayer(conf2, keras2);
    }
}

