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
package org.deeplearning4j.nn.modelimport.keras.layers.embeddings;


import DefaultParamInitializer.WEIGHT_KEY;
import java.util.Collections;
import org.deeplearning4j.nn.modelimport.keras.config.Keras1LayerConfiguration;
import org.deeplearning4j.nn.modelimport.keras.config.Keras2LayerConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author Max Pumperla
 */
public class KerasEmbeddingTest {
    private final String LAYER_NAME = "embedding_sequence_layer";

    private final String INIT_KERAS = "glorot_normal";

    private final int[] INPUT_SHAPE = new int[]{ 100, 20 };

    private static final boolean[] MASK_ZERO = new boolean[]{ false, true };

    private Integer keras1 = 1;

    private Integer keras2 = 2;

    private Keras1LayerConfiguration conf1 = new Keras1LayerConfiguration();

    private Keras2LayerConfiguration conf2 = new Keras2LayerConfiguration();

    @Test
    public void testEmbeddingLayer() throws Exception {
        for (boolean mz : KerasEmbeddingTest.MASK_ZERO) {
            buildEmbeddingLayer(conf1, keras1, mz);
            buildEmbeddingLayer(conf2, keras2, mz);
        }
    }

    @Test
    public void testEmbeddingLayerSetWeightsMaskZero() throws Exception {
        // GIVEN keras embedding with mask zero true
        KerasEmbedding embedding = buildEmbeddingLayer(conf1, keras1, true);
        // WHEN
        embedding.setWeights(Collections.singletonMap(conf1.getLAYER_FIELD_EMBEDDING_WEIGHTS(), Nd4j.ones(INPUT_SHAPE)));
        // THEN first row is set to zeros
        INDArray weights = embedding.getWeights().get(WEIGHT_KEY);
        Assert.assertEquals(embedding.getWeights().get(WEIGHT_KEY).columns(), INPUT_SHAPE[1]);
    }
}

