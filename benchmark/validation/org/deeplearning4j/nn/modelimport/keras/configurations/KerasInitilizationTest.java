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
package org.deeplearning4j.nn.modelimport.keras.configurations;


import org.deeplearning4j.nn.modelimport.keras.config.Keras1LayerConfiguration;
import org.deeplearning4j.nn.modelimport.keras.config.Keras2LayerConfiguration;
import org.deeplearning4j.nn.weights.IWeightInit;
import org.junit.Test;


public class KerasInitilizationTest {
    private double minValue = -0.2;

    private double maxValue = 0.2;

    private double mean = 0.0;

    private double stdDev = 0.2;

    private double value = 42.0;

    private double gain = 0.2;

    private Keras1LayerConfiguration conf1 = new Keras1LayerConfiguration();

    private Keras2LayerConfiguration conf2 = new Keras2LayerConfiguration();

    @Test
    public void testInitializers() throws Exception {
        Integer keras1 = 1;
        Integer keras2 = 2;
        String[] keras1Inits = initializers(conf1);
        String[] keras2Inits = initializers(conf2);
        IWeightInit[] dl4jInits = dl4jInitializers();
        for (int i = 0; i < ((dl4jInits.length) - 1); i++) {
            initilizationDenseLayer(conf1, keras1, keras1Inits[i], dl4jInits[i]);
            initilizationDenseLayer(conf2, keras2, keras2Inits[i], dl4jInits[i]);
            initilizationDenseLayer(conf2, keras2, keras2Inits[((dl4jInits.length) - 1)], dl4jInits[((dl4jInits.length) - 1)]);
        }
    }
}

