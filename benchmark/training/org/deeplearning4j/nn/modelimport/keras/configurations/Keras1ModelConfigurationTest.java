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
import org.junit.Test;


/**
 * Unit tests for Keras1 model configuration import.
 *
 * @author Max Pumperla
 */
@Slf4j
public class Keras1ModelConfigurationTest {
    private ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void imdbLstmTfSequentialConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/imdb_lstm_tf_keras_1_config.json", true);
    }

    @Test
    public void imdbLstmThSequentialConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/imdb_lstm_th_keras_1_config.json", true);
    }

    @Test
    public void mnistMlpTfSequentialConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/mnist_mlp_tf_keras_1_config.json", true);
    }

    @Test
    public void mnistMlpThSequentialConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/mnist_mlp_th_keras_1_config.json", true);
    }

    @Test
    public void mnistCnnTfSequentialConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/mnist_cnn_tf_keras_1_config.json", true);
    }

    @Test
    public void mnistCnnNoBiasTfSequentialConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/mnist_cnn_no_bias_tf_config.json", true);
    }

    @Test
    public void mnistCnnThSequentialConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/mnist_cnn_th_keras_1_config.json", true);
    }

    @Test
    public void mlpSequentialConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/mlp_config.json", true);
    }

    @Test
    public void mlpConstraintsConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/mnist_mlp_constraint_tf_keras_1_config.json", true);
    }

    @Test
    public void reshapeMlpConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/mnist_mlp_reshape_tf_keras_1_config.json", true);
    }

    @Test
    public void reshapeCnnConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/mnist_cnn_reshape_tf_keras_1_config.json", true);
    }

    @Test
    public void mlpFapiConfigTest() throws Exception {
        runModelConfigTest("modelimport/keras/configs/keras1/mlp_fapi_config.json");
    }

    @Test
    public void mlpFapiMultiLossConfigTest() throws Exception {
        runModelConfigTest("modelimport/keras/configs/keras1/mlp_fapi_multiloss_config.json");
    }

    @Test
    public void yoloConfigTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/yolo_model.json", true);
    }

    @Test
    public void cnnTfTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/cnn_tf_config.json", true);
    }

    @Test
    public void cnnThTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/cnn_th_config.json", true);
    }

    @Test
    public void lstmFixedLenTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/lstm_tddense_config.json", false);
    }

    @Test
    public void mnistCnnTfTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/mnist_cnn_tf_config.json", true);
    }

    @Test
    public void mnistMlpTfTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/mnist_mlp_tf_config.json", true);
    }

    @Test
    public void embeddingConv1DTfTest() throws Exception {
        runSequentialConfigTest("modelimport/keras/configs/keras1/keras1_tf_embedding_conv1d_config.json", true);
    }
}

