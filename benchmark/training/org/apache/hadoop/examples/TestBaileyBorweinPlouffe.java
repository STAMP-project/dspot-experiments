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
package org.apache.hadoop.examples;


import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for BaileyBorweinPlouffe
 */
public class TestBaileyBorweinPlouffe {
    @Test
    public void testMod() {
        final BigInteger TWO = BigInteger.ONE.add(BigInteger.ONE);
        for (long n = 3; n < 100; n++) {
            for (long e = 1; e < 100; e++) {
                final long r = TWO.modPow(BigInteger.valueOf(e), BigInteger.valueOf(n)).longValue();
                Assert.assertEquals(((("e=" + e) + ", n=") + n), r, BaileyBorweinPlouffe.mod(e, n));
            }
        }
    }

    @Test
    public void testHexDigit() {
        final long[] answers = new long[]{ 17398, 41736, 10679, 18929, 35528, 13802 };
        long d = 1;
        for (int i = 0; i < (answers.length); i++) {
            Assert.assertEquals(("d=" + d), answers[i], BaileyBorweinPlouffe.hexDigits(d));
            d *= 10;
        }
        Assert.assertEquals(9279L, BaileyBorweinPlouffe.hexDigits(0));
    }
}

