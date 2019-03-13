/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.math.primitives.vector;


import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.math.primitives.vector.impl.SparseVector;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class SparseVectorConstructorTest {
    /**
     *
     */
    private static final int IMPOSSIBLE_SIZE = -1;

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void negativeSizeTest() {
        Assert.assertEquals("Negative size.", SparseVectorConstructorTest.IMPOSSIBLE_SIZE, new SparseVector((-1), 1).size());
    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void zeroSizeTest() {
        Assert.assertEquals("0 size.", SparseVectorConstructorTest.IMPOSSIBLE_SIZE, new SparseVector(0, 1).size());
    }

    /**
     *
     */
    @Test
    public void primitiveTest() {
        Assert.assertEquals("1 size, random access.", 1, size());
        Assert.assertEquals("1 size, sequential access.", 1, size());
    }

    /**
     *
     */
    @Test
    public void noParamsCtorTest() {
        Assert.assertNotNull(new SparseVector().nonZeroSpliterator());
    }

    /**
     *
     */
    @Test
    public void mapCtorTest() {
        Map<Integer, Double> map = new HashMap<Integer, Double>() {
            {
                put(1, 1.0);
            }
        };
        Assert.assertTrue("Copy true", new SparseVector(map, true).isRandomAccess());
        Assert.assertTrue("Copy false", new SparseVector(map, false).isRandomAccess());
    }
}

