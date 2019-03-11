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
package org.apache.hadoop.examples.pi.math;


import java.math.BigInteger;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class TestModular {
    private static final Random RANDOM = new Random();

    private static final BigInteger TWO = BigInteger.valueOf(2);

    static final int DIV_VALID_BIT = 32;

    static final long DIV_LIMIT = 1L << (TestModular.DIV_VALID_BIT);

    @Test
    public void testDiv() {
        for (long n = 2; n < 100; n++)
            for (long r = 1; r < n; r++) {
                final long a = TestModular.div(0, r, n);
                final long b = ((long) (((r * 1.0) / n) * (1L << (TestModular.DIV_VALID_BIT))));
                final String s = String.format("r=%d, n=%d, a=%X, b=%X", r, n, a, b);
                Assert.assertEquals(s, b, a);
            }

    }

    static class Montgomery2 extends Montgomery {
        /**
         * Compute 2^y mod N for N odd.
         */
        long mod2(final long y) {
            long r0 = (R) - (N);
            long r1 = r0 << 1;
            if (r1 >= (N))
                r1 -= N;

            for (long mask = Long.highestOneBit(y); mask > 0; mask >>>= 1) {
                if ((mask & y) == 0) {
                    r1 = product.m(r0, r1);
                    r0 = product.m(r0, r0);
                } else {
                    r0 = product.m(r0, r1);
                    r1 = product.m(r1, r1);
                }
            }
            return product.m(r0, 1);
        }
    }
}

