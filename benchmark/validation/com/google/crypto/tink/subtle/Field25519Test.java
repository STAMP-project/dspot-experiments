/**
 * Copyright 2017 Google Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.google.crypto.tink.subtle;


import java.math.BigInteger;
import java.security.SecureRandom;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link Field25519}.
 *
 * <p>TODO(quannguyen): Add more tests. Note that several functions assume that the inputs are in
 * reduced forms, so testing them don't guarantee that its uses are safe. There may be integer
 * overflow when they aren't used correctly.
 */
@RunWith(JUnit4.class)
public final class Field25519Test {
    /**
     * The idea of basic tests is simple. We generate random numbers, make computations with
     * Field25519 and compare the results with Java BigInteger.
     */
    private static final int NUM_BASIC_TESTS = 1024;

    private static final SecureRandom rand = new SecureRandom();

    private static final BigInteger P = BigInteger.valueOf(2).pow(255).subtract(BigInteger.valueOf(19));

    BigInteger[] x = new BigInteger[Field25519Test.NUM_BASIC_TESTS];

    BigInteger[] y = new BigInteger[Field25519Test.NUM_BASIC_TESTS];

    @Test
    public void testBasicSum() {
        for (int i = 0; i < (Field25519Test.NUM_BASIC_TESTS); i++) {
            BigInteger expectedResult = x[i].add(y[i]).mod(Field25519Test.P);
            byte[] xBytes = toLittleEndian(x[i]);
            byte[] yBytes = toLittleEndian(y[i]);
            long[] output = new long[((Field25519.LIMB_CNT) * 2) + 1];
            Field25519.sum(output, Field25519.expand(xBytes), Field25519.expand(yBytes));
            Field25519.reduceCoefficients(output);
            BigInteger result = new BigInteger(reverse(Field25519.contract(output)));
            Assert.assertEquals(((("Sum x[i] + y[i]: " + (x[i])) + "+") + (y[i])), expectedResult, result);
        }
    }

    @Test
    public void testBasicSub() {
        for (int i = 0; i < (Field25519Test.NUM_BASIC_TESTS); i++) {
            BigInteger expectedResult = x[i].subtract(y[i]).mod(Field25519Test.P);
            byte[] xBytes = toLittleEndian(x[i]);
            byte[] yBytes = toLittleEndian(y[i]);
            long[] output = new long[((Field25519.LIMB_CNT) * 2) + 1];
            Field25519.sub(output, Field25519.expand(xBytes), Field25519.expand(yBytes));
            Field25519.reduceCoefficients(output);
            BigInteger result = new BigInteger(reverse(Field25519.contract(output)));
            Assert.assertEquals(((("Subtraction x[i] - y[i]: " + (x[i])) + "-") + (y[i])), expectedResult, result);
        }
    }

    @Test
    public void testBasicProduct() {
        for (int i = 0; i < (Field25519Test.NUM_BASIC_TESTS); i++) {
            BigInteger expectedResult = x[i].multiply(y[i]).mod(Field25519Test.P);
            byte[] xBytes = toLittleEndian(x[i]);
            byte[] yBytes = toLittleEndian(y[i]);
            long[] output = new long[((Field25519.LIMB_CNT) * 2) + 1];
            Field25519.product(output, Field25519.expand(xBytes), Field25519.expand(yBytes));
            Field25519.reduceSizeByModularReduction(output);
            Field25519.reduceCoefficients(output);
            BigInteger result = new BigInteger(reverse(Field25519.contract(output)));
            Assert.assertEquals(((("Product x[i] * y[i]: " + (x[i])) + "*") + (y[i])), expectedResult, result);
        }
    }

    @Test
    public void testBasicMult() {
        for (int i = 0; i < (Field25519Test.NUM_BASIC_TESTS); i++) {
            BigInteger expectedResult = x[i].multiply(y[i]).mod(Field25519Test.P);
            byte[] xBytes = toLittleEndian(x[i]);
            byte[] yBytes = toLittleEndian(y[i]);
            long[] output = new long[((Field25519.LIMB_CNT) * 2) + 1];
            Field25519.mult(output, Field25519.expand(xBytes), Field25519.expand(yBytes));
            BigInteger result = new BigInteger(reverse(Field25519.contract(output)));
            Assert.assertEquals(((("Multiplication x[i] * y[i]: " + (x[i])) + "*") + (y[i])), expectedResult, result);
        }
    }

    @Test
    public void testBasicScalarProduct() {
        final long scalar = 121665;
        for (int i = 0; i < (Field25519Test.NUM_BASIC_TESTS); i++) {
            BigInteger expectedResult = x[i].multiply(BigInteger.valueOf(scalar)).mod(Field25519Test.P);
            byte[] xBytes = toLittleEndian(x[i]);
            long[] output = new long[((Field25519.LIMB_CNT) * 2) + 1];
            Field25519.scalarProduct(output, Field25519.expand(xBytes), scalar);
            Field25519.reduceSizeByModularReduction(output);
            Field25519.reduceCoefficients(output);
            BigInteger result = new BigInteger(reverse(Field25519.contract(output)));
            Assert.assertEquals(((("Scalar product x[i] * 10 " + (x[i])) + "*") + 10), expectedResult, result);
        }
    }

    @Test
    public void testBasicSquare() {
        for (int i = 0; i < (Field25519Test.NUM_BASIC_TESTS); i++) {
            BigInteger expectedResult = x[i].multiply(x[i]).mod(Field25519Test.P);
            byte[] xBytes = toLittleEndian(x[i]);
            long[] output = new long[((Field25519.LIMB_CNT) * 2) + 1];
            Field25519.square(output, Field25519.expand(xBytes));
            Field25519.reduceSizeByModularReduction(output);
            Field25519.reduceCoefficients(output);
            BigInteger result = new BigInteger(reverse(Field25519.contract(output)));
            Assert.assertEquals(((("Square x[i] * x[i]: " + (x[i])) + "*") + (x[i])), expectedResult, result);
        }
    }

    @Test
    public void testBasicInverse() {
        for (int i = 0; i < (Field25519Test.NUM_BASIC_TESTS); i++) {
            BigInteger expectedResult = x[i].modInverse(Field25519Test.P);
            byte[] xBytes = toLittleEndian(x[i]);
            long[] output = new long[((Field25519.LIMB_CNT) * 2) + 1];
            Field25519.inverse(output, Field25519.expand(xBytes));
            BigInteger result = new BigInteger(reverse(Field25519.contract(output)));
            Assert.assertEquals(("Inverse: x[i]^(-1) mod P: " + (x[i])), expectedResult, result);
        }
    }

    @Test
    public void testContractExpand() {
        for (int i = 0; i < (Field25519Test.NUM_BASIC_TESTS); i++) {
            byte[] xBytes = toLittleEndian(x[i]);
            byte[] result = Field25519.contract(Field25519.expand(xBytes));
            Assert.assertArrayEquals(xBytes, result);
        }
    }
}

