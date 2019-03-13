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
package org.deeplearning4j.nn.modelimport.keras.preprocessing.text;


import java.io.IOException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Import Keras Tokenizer
 *
 * @author Max Pumperla
 */
public class TokenizerImportTest {
    ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void importTest() throws IOException, InvalidKerasConfigurationException {
        String path = "modelimport/keras/preprocessing/tokenizer.json";
        ClassPathResource configResource = new ClassPathResource(path, classLoader);
        KerasTokenizer tokenizer = KerasTokenizer.fromJson(configResource.getFile().getAbsolutePath());
        Assert.assertEquals(100, tokenizer.getNumWords().intValue());
        Assert.assertTrue(tokenizer.isLower());
        Assert.assertEquals(" ", tokenizer.getSplit());
        Assert.assertFalse(tokenizer.isCharLevel());
        Assert.assertEquals(0, tokenizer.getDocumentCount().intValue());
    }

    @Test
    public void importNumWordsNullTest() throws IOException, InvalidKerasConfigurationException {
        String path = "modelimport/keras/preprocessing/tokenizer_num_words_null.json";
        ClassPathResource configResource = new ClassPathResource(path, classLoader);
        KerasTokenizer tokenizer = KerasTokenizer.fromJson(configResource.getFile().getAbsolutePath());
        Assert.assertNull(tokenizer.getNumWords());
        Assert.assertTrue(tokenizer.isLower());
        Assert.assertEquals(" ", tokenizer.getSplit());
        Assert.assertFalse(tokenizer.isCharLevel());
        Assert.assertEquals(0, tokenizer.getDocumentCount().intValue());
    }
}

