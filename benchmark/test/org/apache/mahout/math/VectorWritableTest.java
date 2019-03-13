/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.mahout.math;


import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.annotations.Repeat;
import org.junit.Test;


public final class VectorWritableTest extends RandomizedTest {
    private static final int MAX_VECTOR_SIZE = 100;

    @Test
    @Repeat(iterations = 20)
    public void testViewSequentialAccessSparseVectorWritable() throws Exception {
        Vector v = new SequentialAccessSparseVector(VectorWritableTest.MAX_VECTOR_SIZE);
        createRandom(v);
        Vector view = new VectorView(v, 0, v.size());
        VectorWritableTest.doTestVectorWritableEquals(view);
    }

    @Test
    @Repeat(iterations = 20)
    public void testSequentialAccessSparseVectorWritable() throws Exception {
        Vector v = new SequentialAccessSparseVector(VectorWritableTest.MAX_VECTOR_SIZE);
        createRandom(v);
        VectorWritableTest.doTestVectorWritableEquals(v);
    }

    @Test
    @Repeat(iterations = 20)
    public void testRandomAccessSparseVectorWritable() throws Exception {
        Vector v = new RandomAccessSparseVector(VectorWritableTest.MAX_VECTOR_SIZE);
        createRandom(v);
        VectorWritableTest.doTestVectorWritableEquals(v);
    }

    @Test
    @Repeat(iterations = 20)
    public void testDenseVectorWritable() throws Exception {
        Vector v = new DenseVector(VectorWritableTest.MAX_VECTOR_SIZE);
        createRandom(v);
        VectorWritableTest.doTestVectorWritableEquals(v);
    }

    @Test
    @Repeat(iterations = 20)
    public void testNamedVectorWritable() throws Exception {
        Vector v = new DenseVector(VectorWritableTest.MAX_VECTOR_SIZE);
        v = new NamedVector(v, "Victor");
        createRandom(v);
        VectorWritableTest.doTestVectorWritableEquals(v);
    }
}

