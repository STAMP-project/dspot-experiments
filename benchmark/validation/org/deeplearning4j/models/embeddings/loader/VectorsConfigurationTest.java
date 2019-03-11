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
package org.deeplearning4j.models.embeddings.loader;


import java.io.File;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.UimaSentenceIterator;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by fartovii on 21.11.15.
 */
public class VectorsConfigurationTest {
    protected static final Logger log = LoggerFactory.getLogger(VectorsConfigurationTest.class);

    @Test
    public void testFromJson() throws Exception {
        VectorsConfiguration configuration = new VectorsConfiguration();
        configuration.setHugeModelExpected(true);
        configuration.setWindow(5);
        configuration.setIterations(3);
        configuration.setLayersSize(200);
        configuration.setLearningRate(1.4);
        configuration.setSampling(5.0E-4);
        configuration.setMinLearningRate(0.25);
        configuration.setEpochs(1);
        String json = configuration.toJson();
        VectorsConfigurationTest.log.info(("Conf. JSON: " + json));
        VectorsConfiguration configuration2 = VectorsConfiguration.fromJson(json);
        Assert.assertEquals(configuration, configuration2);
    }

    @Test
    public void testFromW2V() throws Exception {
        VectorsConfiguration configuration = new VectorsConfiguration();
        configuration.setHugeModelExpected(true);
        configuration.setWindow(5);
        configuration.setIterations(3);
        configuration.setLayersSize(200);
        configuration.setLearningRate(1.4);
        configuration.setSampling(5.0E-4);
        configuration.setMinLearningRate(0.25);
        configuration.setEpochs(1);
        File inputFile = new ClassPathResource("/big/raw_sentences.txt").getFile();
        SentenceIterator iter = UimaSentenceIterator.createWithPath(inputFile.getAbsolutePath());
        Word2Vec vec = new Word2Vec.Builder(configuration).iterate(iter).build();
        VectorsConfiguration configuration2 = vec.getConfiguration();
        Assert.assertEquals(configuration, configuration2);
    }
}

