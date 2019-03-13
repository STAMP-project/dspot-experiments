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
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class DenseVectorConstructorTest {
    /**
     *
     */
    private static final int IMPOSSIBLE_SIZE = -1;

    /**
     *
     */
    @Test(expected = UnsupportedOperationException.class)
    public void mapInvalidArgsTest() {
        Assert.assertEquals("Expect exception due to invalid args.", DenseVectorConstructorTest.IMPOSSIBLE_SIZE, new DenseVector(new HashMap<String, Object>() {
            {
                put("invalid", 99);
            }
        }).size());
    }

    /**
     *
     */
    @Test(expected = UnsupportedOperationException.class)
    public void mapMissingArgsTest() {
        final Map<String, Object> test = new HashMap<String, Object>() {
            {
                put("arr", new double[0]);
                put("shallowCopyMissing", "whatever");
            }
        };
        Assert.assertEquals("Expect exception due to missing args.", DenseVectorConstructorTest.IMPOSSIBLE_SIZE, new DenseVector(test).size());
    }

    /**
     *
     */
    @Test(expected = ClassCastException.class)
    public void mapInvalidArrTypeTest() {
        final Map<String, Object> test = new HashMap<String, Object>() {
            {
                put("size", "whatever");
            }
        };
        Assert.assertEquals("Expect exception due to invalid arr type.", DenseVectorConstructorTest.IMPOSSIBLE_SIZE, new DenseVector(test).size());
    }

    /**
     *
     */
    @Test(expected = UnsupportedOperationException.class)
    public void mapInvalidCopyTypeTest() {
        final Map<String, Object> test = new HashMap<String, Object>() {
            {
                put("arr", new double[0]);
                put("shallowCopy", 0);
            }
        };
        Assert.assertEquals("Expect exception due to invalid copy type.", DenseVectorConstructorTest.IMPOSSIBLE_SIZE, new DenseVector(test).size());
    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void mapNullTest() {
        // noinspection ConstantConditions
        Assert.assertEquals("Null map args.", DenseVectorConstructorTest.IMPOSSIBLE_SIZE, new DenseVector(((Map<String, Object>) (null))).size());
    }

    /**
     *
     */
    @Test
    public void mapTest() {
        Assert.assertEquals("Size from args.", 99, new DenseVector(new HashMap<String, Object>() {
            {
                put("size", 99);
            }
        }).size());
        final double[] test = new double[99];
        Assert.assertEquals("Size from array in args.", test.length, new DenseVector(new HashMap<String, Object>() {
            {
                put("arr", test);
                put("copy", false);
            }
        }).size());
        Assert.assertEquals("Size from array in args, shallow copy.", test.length, new DenseVector(new HashMap<String, Object>() {
            {
                put("arr", test);
                put("copy", true);
            }
        }).size());
    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void negativeSizeTest() {
        Assert.assertEquals("Negative size.", DenseVectorConstructorTest.IMPOSSIBLE_SIZE, new DenseVector((-1)).size());
    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void nullCopyTest() {
        Assert.assertEquals("Null array to non-shallow copy.", DenseVectorConstructorTest.IMPOSSIBLE_SIZE, new DenseVector(null, false).size());
    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void nullDefaultCopyTest() {
        Assert.assertEquals("Null array default copy.", DenseVectorConstructorTest.IMPOSSIBLE_SIZE, new DenseVector(((double[]) (null))).size());
    }

    /**
     *
     */
    @Test(expected = NullPointerException.class)
    public void defaultConstructorTest() {
        Assert.assertEquals("Default constructor.", DenseVectorConstructorTest.IMPOSSIBLE_SIZE, new DenseVector().size());
    }

    /**
     *
     */
    @Test(expected = AssertionError.class)
    public void nullArrShallowCopyTest() {
        Assert.assertEquals("Null array shallow copy.", DenseVectorConstructorTest.IMPOSSIBLE_SIZE, new DenseVector(null, true).size());
    }

    /**
     *
     */
    @Test
    public void primitiveTest() {
        Assert.assertEquals("0 size shallow copy.", 0, new DenseVector(new double[0], true).size());
        Assert.assertEquals("0 size.", 0, new DenseVector(new double[0], false).size());
        Assert.assertEquals("1 size shallow copy.", 1, new DenseVector(new double[1], true).size());
        Assert.assertEquals("1 size.", 1, new DenseVector(new double[1], false).size());
        Assert.assertEquals("0 size default copy.", 0, new DenseVector(new double[0]).size());
        Assert.assertEquals("1 size default copy.", 1, new DenseVector(new double[1]).size());
    }
}

