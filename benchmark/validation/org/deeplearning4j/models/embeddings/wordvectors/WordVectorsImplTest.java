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
package org.deeplearning4j.models.embeddings.wordvectors;


import com.google.common.collect.Lists;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


public class WordVectorsImplTest {
    private VocabCache vocabCache;

    private WeightLookupTable weightLookupTable;

    private WordVectorsImpl<SequenceElement> wordVectors;

    @Test
    public void getWordVectors_HaveTwoWordsNotInVocabAndOneIn_ExpectAllNonWordsRemoved() {
        INDArray wordVector = Nd4j.create(1, 1);
        wordVector.putScalar(0, 5);
        Mockito.when(vocabCache.indexOf("word")).thenReturn(0);
        Mockito.when(vocabCache.containsWord("word")).thenReturn(true);
        Mockito.when(weightLookupTable.getWeights()).thenReturn(wordVector);
        wordVectors.setVocab(vocabCache);
        wordVectors.setLookupTable(weightLookupTable);
        INDArray indArray = wordVectors.getWordVectors(Lists.newArrayList("word", "here", "is"));
        Assert.assertEquals(wordVector, indArray);
    }
}

