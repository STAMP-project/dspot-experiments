/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 *
 *
 * @author Alexander Y. Kleymenov
 * @version $Revision$
 */
package org.apache.harmony.crypto.tests.javax.crypto.spec;


import javax.crypto.spec.DHGenParameterSpec;
import junit.framework.TestCase;


/**
 *
 */
public class DHGenParameterSpecTest extends TestCase {
    /**
     * DHGenParameterSpec class testing. Tests the equivalence of
     * parameters specified in the constructor with the values returned
     * by getters.
     */
    public void testDHGenParameterSpec() {
        int[] primes = new int[]{ Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE };
        int[] exponents = new int[]{ Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE };
        for (int i = 0; i < (primes.length); i++) {
            DHGenParameterSpec ps = new DHGenParameterSpec(primes[i], exponents[i]);
            TestCase.assertEquals(("The value returned by getPrimeSize() must be " + "equal to the value specified in the constructor"), ps.getPrimeSize(), primes[i]);
            TestCase.assertEquals(("The value returned by getExponentSize() must be " + "equal to the value specified in the constructor"), ps.getPrimeSize(), exponents[i]);
        }
    }
}

