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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Test;

import static E.delta;
import static E.limit;
import static E.value;


public class TestSummation {
    static final Random RANDOM = new Random();

    static final BigInteger TWO = BigInteger.valueOf(2);

    private static final double DOUBLE_DELTA = 1.0E-9F;

    @Test
    public void testSubtract() {
        final Summation sigma = TestSummation.newSummation(3, 10000, 20);
        final int size = 10;
        final List<Summation> parts = Arrays.asList(sigma.partition(size));
        Collections.sort(parts);
        TestSummation.runTestSubtract(sigma, new ArrayList<Summation>());
        TestSummation.runTestSubtract(sigma, parts);
        for (int n = 1; n < size; n++) {
            for (int j = 0; j < 10; j++) {
                final List<Summation> diff = new ArrayList<Summation>(parts);
                for (int i = 0; i < n; i++)
                    diff.remove(TestSummation.RANDOM.nextInt(diff.size()));

                // /        Collections.sort(diff);
                TestSummation.runTestSubtract(sigma, diff);
            }
        }
    }

    static class Summation2 extends Summation {
        Summation2(ArithmeticProgression N, ArithmeticProgression E) {
            super(N, E);
        }

        final TestModular.Montgomery2 m2 = new TestModular.Montgomery2();

        double compute_montgomery2() {
            long e = value;
            long n = N.value;
            double s = 0;
            for (; e > (limit); e += delta) {
                set(n);
                s = Modular.addMod(s, ((m2.mod2(e)) / ((double) (n))));
                n += N.delta;
            }
            return s;
        }

        double compute_modBigInteger() {
            long e = value;
            long n = N.value;
            double s = 0;
            for (; e > (limit); e += delta) {
                s = Modular.addMod(s, ((TestModular.modBigInteger(e, n)) / ((double) (n))));
                n += N.delta;
            }
            return s;
        }

        double compute_modPow() {
            long e = value;
            long n = N.value;
            double s = 0;
            for (; e > (limit); e += delta) {
                s = Modular.addMod(s, ((TestSummation.TWO.modPow(BigInteger.valueOf(e), BigInteger.valueOf(n)).doubleValue()) / n));
                n += N.delta;
            }
            return s;
        }
    }
}

