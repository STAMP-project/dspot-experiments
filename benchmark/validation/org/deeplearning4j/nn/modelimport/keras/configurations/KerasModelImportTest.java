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


import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test import of Keras models.
 */
@Slf4j
public class KerasModelImportTest {
    ClassLoader classLoader = KerasModelImportTest.class.getClassLoader();

    @Test
    public void testH5WithoutTensorflowScope() throws Exception {
        MultiLayerNetwork model = loadModel("modelimport/keras/tfscope/model.h5");
        Assert.assertNotNull(model);
    }

    @Test
    public void testH5WithTensorflowScope() throws Exception {
        MultiLayerNetwork model = loadModel("modelimport/keras/tfscope/model.h5.with.tensorflow.scope");
        Assert.assertNotNull(model);
    }

    @Test
    public void testWeightAndJsonWithoutTensorflowScope() throws Exception {
        MultiLayerNetwork model = loadModel("modelimport/keras/tfscope/model.json", "modelimport/keras/tfscope/model.weight");
        Assert.assertNotNull(model);
    }

    @Test
    public void testWeightAndJsonWithTensorflowScope() throws Exception {
        MultiLayerNetwork model = loadModel("modelimport/keras/tfscope/model.json.with.tensorflow.scope", "modelimport/keras/tfscope/model.weight.with.tensorflow.scope");
        Assert.assertNotNull(model);
    }
}

