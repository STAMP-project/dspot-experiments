/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.math.hadoop.similarity.cooccurrence.measures;


import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;


public class VectorSimilarityMeasuresTest extends MahoutTestCase {
    @Test
    public void testCooccurrenceCountSimilarity() {
        double similarity = VectorSimilarityMeasuresTest.distributedSimilarity(new double[]{ 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0 }, new double[]{ 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1 }, CooccurrenceCountSimilarity.class);
        assertEquals(5.0, similarity, 0);
    }

    @Test
    public void testTanimotoCoefficientSimilarity() {
        double similarity = VectorSimilarityMeasuresTest.distributedSimilarity(new double[]{ 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0 }, new double[]{ 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1 }, TanimotoCoefficientSimilarity.class);
        assertEquals(0.454545455, similarity, MahoutTestCase.EPSILON);
    }

    @Test
    public void testCityblockSimilarity() {
        double similarity = VectorSimilarityMeasuresTest.distributedSimilarity(new double[]{ 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0 }, new double[]{ 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1 }, CityBlockSimilarity.class);
        assertEquals(0.142857143, similarity, MahoutTestCase.EPSILON);
    }

    @Test
    public void testLoglikelihoodSimilarity() {
        double similarity = VectorSimilarityMeasuresTest.distributedSimilarity(new double[]{ 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0 }, new double[]{ 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1 }, LoglikelihoodSimilarity.class);
        assertEquals(0.03320155369284261, similarity, MahoutTestCase.EPSILON);
    }

    @Test
    public void testCosineSimilarity() {
        double similarity = VectorSimilarityMeasuresTest.distributedSimilarity(new double[]{ 0, 2, 0, 0, 8, 3, 0, 6, 0, 1, 2, 2, 0 }, new double[]{ 3, 0, 0, 0, 7, 0, 2, 2, 1, 3, 2, 1, 1 }, CosineSimilarity.class);
        assertEquals(0.769846046, similarity, MahoutTestCase.EPSILON);
    }

    @Test
    public void testPearsonCorrelationSimilarity() {
        double similarity = VectorSimilarityMeasuresTest.distributedSimilarity(new double[]{ 0, 2, 0, 0, 8, 3, 0, 6, 0, 1, 1, 2, 1 }, new double[]{ 3, 0, 0, 0, 7, 0, 2, 2, 1, 3, 2, 4, 3 }, PearsonCorrelationSimilarity.class);
        assertEquals(0.5303300858899108, similarity, MahoutTestCase.EPSILON);
    }

    @Test
    public void testEuclideanDistanceSimilarity() {
        double similarity = VectorSimilarityMeasuresTest.distributedSimilarity(new double[]{ 0, 2, 0, 0, 8, 3, 0, 6, 0, 1, 1, 2, 1 }, new double[]{ 3, 0, 0, 0, 7, 0, 2, 2, 1, 3, 2, 4, 4 }, EuclideanDistanceSimilarity.class);
        assertEquals(0.11268865367232477, similarity, MahoutTestCase.EPSILON);
    }
}

