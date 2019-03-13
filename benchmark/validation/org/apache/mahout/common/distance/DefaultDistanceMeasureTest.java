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
package org.apache.mahout.common.distance;


import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.junit.Test;


public abstract class DefaultDistanceMeasureTest extends MahoutTestCase {
    @Test
    public void testMeasure() {
        DistanceMeasure distanceMeasure = distanceMeasureFactory();
        Vector[] vectors = new Vector[]{ new DenseVector(new double[]{ 1, 1, 1, 1, 1, 1 }), new DenseVector(new double[]{ 2, 2, 2, 2, 2, 2 }), new DenseVector(new double[]{ 6, 6, 6, 6, 6, 6 }), new DenseVector(new double[]{ -1, -1, -1, -1, -1, -1 }) };
        DefaultDistanceMeasureTest.compare(distanceMeasure, vectors);
        vectors = new Vector[4];
        vectors[0] = new RandomAccessSparseVector(5);
        vectors[0].setQuick(0, 1);
        vectors[0].setQuick(3, 1);
        vectors[0].setQuick(4, 1);
        vectors[1] = new RandomAccessSparseVector(5);
        vectors[1].setQuick(0, 2);
        vectors[1].setQuick(3, 2);
        vectors[1].setQuick(4, 2);
        vectors[2] = new RandomAccessSparseVector(5);
        vectors[2].setQuick(0, 6);
        vectors[2].setQuick(3, 6);
        vectors[2].setQuick(4, 6);
        vectors[3] = new RandomAccessSparseVector(5);
        DefaultDistanceMeasureTest.compare(distanceMeasure, vectors);
    }
}

