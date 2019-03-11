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
package org.deeplearning4j.spark.models.sequencevectors;


import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.deeplearning4j.models.sequencevectors.sequence.ShallowSequenceElement;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.primitives.Counter;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class SparkSequenceVectorsTest {
    protected static List<Sequence<VocabWord>> sequencesCyclic;

    private JavaSparkContext sc;

    @Test
    public void testFrequenciesCount() throws Exception {
        JavaRDD<Sequence<VocabWord>> sequences = sc.parallelize(SparkSequenceVectorsTest.sequencesCyclic);
        SparkSequenceVectors<VocabWord> seqVec = new SparkSequenceVectors();
        seqVec.fitSequences(sequences);
        Counter<Long> counter = seqVec.getCounter();
        // element "0" should have frequency of 20
        Assert.assertEquals(20, counter.getCount(0L), 1.0E-5);
        // elements 1 - 9 should have frequencies of 10
        for (int e = 1; e < ((SparkSequenceVectorsTest.sequencesCyclic.get(0).getElements().size()) - 1); e++) {
            Assert.assertEquals(10, counter.getCount(SparkSequenceVectorsTest.sequencesCyclic.get(0).getElementByIndex(e).getStorageId()), 1.0E-5);
        }
        VocabCache<ShallowSequenceElement> shallowVocab = seqVec.getShallowVocabCache();
        Assert.assertEquals(10, shallowVocab.numWords());
        ShallowSequenceElement zero = shallowVocab.tokenFor(0L);
        ShallowSequenceElement first = shallowVocab.tokenFor(1L);
        Assert.assertNotEquals(null, zero);
        Assert.assertEquals(20.0, zero.getElementFrequency(), 1.0E-5);
        Assert.assertEquals(0, zero.getIndex());
        Assert.assertEquals(10.0, first.getElementFrequency(), 1.0E-5);
    }
}

