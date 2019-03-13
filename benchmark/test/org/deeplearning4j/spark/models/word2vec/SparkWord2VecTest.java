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
package org.deeplearning4j.spark.models.word2vec;


import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.deeplearning4j.models.sequencevectors.sequence.ShallowSequenceElement;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for new Spark Word2Vec implementation
 *
 * @author raver119@gmail.com
 */
public class SparkWord2VecTest {
    private static List<String> sentences;

    private JavaSparkContext sc;

    @Test
    public void testStringsTokenization1() throws Exception {
        JavaRDD<String> rddSentences = sc.parallelize(SparkWord2VecTest.sentences);
        SparkWord2Vec word2Vec = new SparkWord2Vec();
        word2Vec.fitSentences(rddSentences);
        VocabCache<ShallowSequenceElement> vocabCache = word2Vec.getShallowVocabCache();
        Assert.assertNotEquals(null, vocabCache);
        Assert.assertEquals(9, vocabCache.numWords());
        Assert.assertEquals(2.0, vocabCache.wordFor(SequenceElement.getLongHash("one")).getElementFrequency(), 1.0E-5);
        Assert.assertEquals(1.0, vocabCache.wordFor(SequenceElement.getLongHash("two")).getElementFrequency(), 1.0E-5);
    }
}

