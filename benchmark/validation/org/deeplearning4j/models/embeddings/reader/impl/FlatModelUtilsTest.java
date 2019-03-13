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
package org.deeplearning4j.models.embeddings.reader.impl;


import java.util.Collection;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * These are temporary tests and will be removed after issue is solved.
 *
 * @author raver119@gmail.com
 */
@Ignore
public class FlatModelUtilsTest {
    private Word2Vec vec;

    private static final Logger log = LoggerFactory.getLogger(FlatModelUtilsTest.class);

    @Test
    public void testWordsNearestFlat1() throws Exception {
        vec.setModelUtils(new FlatModelUtils<org.deeplearning4j.models.word2vec.VocabWord>());
        Collection<String> list = vec.wordsNearest("energy", 10);
        FlatModelUtilsTest.log.info("Flat model results:");
        FlatModelUtilsTest.printWords("energy", list, vec);
    }

    @Test
    public void testWordsNearestBasic1() throws Exception {
        // WordVectors vec = WordVectorSerializer.loadTxtVectors(new File("/ext/Temp/Models/model.dat_trans"));
        vec.setModelUtils(new BasicModelUtils<org.deeplearning4j.models.word2vec.VocabWord>());
        String target = "energy";
        INDArray arr1 = vec.getWordVectorMatrix(target).dup();
        System.out.println(("[-]: " + arr1));
        System.out.println(("[+]: " + (Transforms.unitVec(arr1))));
        Collection<String> list = vec.wordsNearest(target, 10);
        FlatModelUtilsTest.log.info("Transpose model results:");
        FlatModelUtilsTest.printWords(target, list, vec);
        list = vec.wordsNearest(target, 10);
        FlatModelUtilsTest.log.info("Transpose model results 2:");
        FlatModelUtilsTest.printWords(target, list, vec);
        list = vec.wordsNearest(target, 10);
        FlatModelUtilsTest.log.info("Transpose model results 3:");
        FlatModelUtilsTest.printWords(target, list, vec);
        INDArray arr2 = vec.getWordVectorMatrix(target).dup();
        Assert.assertEquals(arr1, arr2);
    }
}

