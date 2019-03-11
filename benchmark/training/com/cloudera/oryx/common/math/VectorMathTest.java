/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.common.math;


import com.cloudera.oryx.common.OryxTest;
import com.cloudera.oryx.common.random.RandomManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Utility class with simple vector-related operations.
 */
public final class VectorMathTest extends OryxTest {
    private static final float[] VEC1 = new float[]{ 1.0F, 0.5F, -3.5F };

    private static final float[] VEC2 = new float[]{ 0.0F, -10.3F, -3.0F };

    private static final double[] VEC1D = new double[]{ 1.0, 0.5, -3.5 };

    private static final double[] VEC2D = new double[]{ 0.0, -10.3, -3.0 };

    @Test
    public void testDotFF() {
        Assert.assertEquals(5.35, VectorMath.dot(VectorMathTest.VEC1, VectorMathTest.VEC2), OryxTest.FLOAT_EPSILON);
    }

    @Test
    public void testParseVector() {
        OryxTest.assertArrayEquals(new double[]{ -1.0, 2.01, 3.5 }, VectorMath.parseVector(new String[]{ "-1.0", "2.01", "3.5" }));
    }

    @Test
    public void testSmall() {
        float[] a = new float[]{ 1.0E-24F };
        Assert.assertEquals((1.0E-24 * 1.0E-24), VectorMath.dot(a, a));
    }

    @Test
    public void testNormF() {
        Assert.assertEquals(0.0, VectorMath.norm(new float[]{ 0.0F }), OryxTest.FLOAT_EPSILON);
        Assert.assertEquals(3.674234614174767, VectorMath.norm(VectorMathTest.VEC1), OryxTest.FLOAT_EPSILON);
        Assert.assertEquals(10.72800074571213, VectorMath.norm(VectorMathTest.VEC2), OryxTest.FLOAT_EPSILON);
    }

    @Test
    public void testNormD() {
        Assert.assertEquals(0.0, VectorMath.norm(new double[]{ 0.0 }), OryxTest.DOUBLE_EPSILON);
        Assert.assertEquals(3.674234614174767, VectorMath.norm(VectorMathTest.VEC1D), OryxTest.DOUBLE_EPSILON);
        Assert.assertEquals(10.72800074571213, VectorMath.norm(VectorMathTest.VEC2D), OryxTest.DOUBLE_EPSILON);
    }

    @Test
    public void testCosineSimilarity() {
        Assert.assertEquals(0.13572757431921362, VectorMath.cosineSimilarity(VectorMathTest.VEC1, VectorMathTest.VEC2, VectorMath.norm(VectorMathTest.VEC2)));
        Assert.assertEquals(1.0, VectorMath.cosineSimilarity(VectorMathTest.VEC1, VectorMathTest.VEC1, VectorMath.norm(VectorMathTest.VEC1)));
        float[] bigVec = new float[1000];
        Arrays.fill(bigVec, 3.1415925E20F);
        float[] smallVec = new float[1000];
        Arrays.fill(smallVec, (-3.1415925E-20F));
        Assert.assertEquals((-1.0), VectorMath.cosineSimilarity(bigVec, smallVec, VectorMath.norm(smallVec)));
    }

    @Test
    public void testTransposeTimesSelf() {
        Map<Integer, float[]> a = new HashMap<>();
        a.put((-1), new float[]{ 1.3F, -2.0F, 3.0F });
        a.put(1, new float[]{ 2.0F, 0.0F, 5.0F });
        a.put(3, new float[]{ 0.0F, -1.5F, 5.5F });
        double[] ata = VectorMath.transposeTimesSelf(a.values());
        double[] expected = new double[]{ 5.69, -2.6, 13.9, 6.25, -14.25, 64.25 };
        Assert.assertArrayEquals(expected, ata, OryxTest.FLOAT_EPSILON);
    }

    @Test
    public void testNullTranspose() {
        Assert.assertNull(VectorMath.transposeTimesSelf(null));
        Assert.assertNull(VectorMath.transposeTimesSelf(Collections.emptyList()));
    }

    @Test
    public void testRandomF() {
        RandomGenerator random = RandomManager.getRandom();
        float[] vec1 = VectorMath.randomVectorF(10, random);
        float[] vec2 = VectorMath.randomVectorF(10, random);
        Assert.assertEquals(10, vec1.length);
        Assert.assertEquals(10, vec2.length);
        Assert.assertFalse(Arrays.equals(vec1, vec2));
    }
}

