/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.range;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Combinatorial tests for {@link ByteKeyRange#interpolateKey}, which also checks {@link ByteKeyRange#estimateFractionForKey} by converting the interpolated keys back to fractions.
 */
@RunWith(Parameterized.class)
public class ByteKeyRangeInterpolateKeyTest {
    private static final ByteKey[] TEST_KEYS = ByteKeyRangeTest.RANGE_TEST_KEYS;

    @Parameterized.Parameter
    public ByteKeyRange range;

    @Test
    public void testInterpolateKeyAndEstimateFraction() {
        double delta = 1.0E-7;
        double[] testFractions = new double[]{ 0.01, 0.1, 0.123, 0.2, 0.3, 0.45738, 0.5, 0.6, 0.7182, 0.8, 0.95, 0.97, 0.99 };
        ByteKey last = range.getStartKey();
        for (double fraction : testFractions) {
            String message = Double.toString(fraction);
            try {
                ByteKey key = range.interpolateKey(fraction);
                Assert.assertThat(message, key, Matchers.greaterThanOrEqualTo(last));
                Assert.assertThat(message, range.estimateFractionForKey(key), Matchers.closeTo(fraction, delta));
                last = key;
            } catch (IllegalStateException e) {
                Assert.assertThat(message, e.getMessage(), Matchers.containsString("near-empty ByteKeyRange"));
            }
        }
    }
}

