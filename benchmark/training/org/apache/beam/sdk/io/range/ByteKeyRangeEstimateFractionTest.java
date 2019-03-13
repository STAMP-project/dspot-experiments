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
 * A combinatorial test of {@link ByteKeyRange#estimateFractionForKey(ByteKey)}.
 */
@RunWith(Parameterized.class)
public class ByteKeyRangeEstimateFractionTest {
    private static final ByteKey[] TEST_KEYS = ByteKeyRangeTest.RANGE_TEST_KEYS;

    @Parameterized.Parameter(0)
    public int i;

    @Parameterized.Parameter(1)
    public int k;

    @Test
    public void testEstimateFractionForKey() {
        double last = 0.0;
        ByteKeyRange range = ByteKeyRange.of(ByteKeyRangeEstimateFractionTest.TEST_KEYS[i], ByteKeyRangeEstimateFractionTest.TEST_KEYS[k]);
        for (int j = i; j < (k); ++j) {
            ByteKey key = ByteKeyRangeEstimateFractionTest.TEST_KEYS[j];
            if (key.isEmpty()) {
                // Cannot compute progress for unspecified key
                continue;
            }
            double fraction = range.estimateFractionForKey(key);
            Assert.assertThat(fraction, Matchers.greaterThanOrEqualTo(last));
            last = fraction;
        }
    }
}

