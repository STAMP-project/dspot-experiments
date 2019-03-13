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
package org.apache.commons.lang3.math;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.math.IEEE754rUtils}.
 */
public class IEEE754rUtilsTest {
    @Test
    public void testLang381() {
        Assertions.assertEquals(1.2, IEEE754rUtils.min(1.2, 2.5, Double.NaN), 0.01);
        Assertions.assertEquals(2.5, IEEE754rUtils.max(1.2, 2.5, Double.NaN), 0.01);
        Assertions.assertTrue(Double.isNaN(IEEE754rUtils.max(Double.NaN, Double.NaN, Double.NaN)));
        Assertions.assertEquals(1.2F, IEEE754rUtils.min(1.2F, 2.5F, Float.NaN), 0.01);
        Assertions.assertEquals(2.5F, IEEE754rUtils.max(1.2F, 2.5F, Float.NaN), 0.01);
        Assertions.assertTrue(Float.isNaN(IEEE754rUtils.max(Float.NaN, Float.NaN, Float.NaN)));
        final double[] a = new double[]{ 1.2, Double.NaN, 3.7, 27.0, 42.0, Double.NaN };
        Assertions.assertEquals(42.0, IEEE754rUtils.max(a), 0.01);
        Assertions.assertEquals(1.2, IEEE754rUtils.min(a), 0.01);
        final double[] b = new double[]{ Double.NaN, 1.2, Double.NaN, 3.7, 27.0, 42.0, Double.NaN };
        Assertions.assertEquals(42.0, IEEE754rUtils.max(b), 0.01);
        Assertions.assertEquals(1.2, IEEE754rUtils.min(b), 0.01);
        final float[] aF = new float[]{ 1.2F, Float.NaN, 3.7F, 27.0F, 42.0F, Float.NaN };
        Assertions.assertEquals(1.2F, IEEE754rUtils.min(aF), 0.01);
        Assertions.assertEquals(42.0F, IEEE754rUtils.max(aF), 0.01);
        final float[] bF = new float[]{ Float.NaN, 1.2F, Float.NaN, 3.7F, 27.0F, 42.0F, Float.NaN };
        Assertions.assertEquals(1.2F, IEEE754rUtils.min(bF), 0.01);
        Assertions.assertEquals(42.0F, IEEE754rUtils.max(bF), 0.01);
    }

    @Test
    public void testEnforceExceptions() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> IEEE754rUtils.min(((float[]) (null))), "IllegalArgumentException expected for null input");
        Assertions.assertThrows(IllegalArgumentException.class, () -> IEEE754rUtils.min(), "IllegalArgumentException expected for empty input");
        Assertions.assertThrows(IllegalArgumentException.class, () -> IEEE754rUtils.max(((float[]) (null))), "IllegalArgumentException expected for null input");
        Assertions.assertThrows(IllegalArgumentException.class, IEEE754rUtils::max, "IllegalArgumentException expected for empty input");
        Assertions.assertThrows(IllegalArgumentException.class, () -> IEEE754rUtils.min(((double[]) (null))), "IllegalArgumentException expected for null input");
        Assertions.assertThrows(IllegalArgumentException.class, () -> IEEE754rUtils.min(new double[0]), "IllegalArgumentException expected for empty input");
        Assertions.assertThrows(IllegalArgumentException.class, () -> IEEE754rUtils.max(((double[]) (null))), "IllegalArgumentException expected for null input");
        Assertions.assertThrows(IllegalArgumentException.class, () -> IEEE754rUtils.max(new double[0]), "IllegalArgumentException expected for empty input");
    }

    @Test
    public void testConstructorExists() {
        new IEEE754rUtils();
    }
}

