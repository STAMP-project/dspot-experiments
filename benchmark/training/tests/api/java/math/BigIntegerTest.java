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
package tests.api.java.math;


import java.math.BigInteger;
import java.util.Random;
import junit.framework.TestCase;


public class BigIntegerTest extends TestCase {
    BigInteger minusTwo = new BigInteger("-2", 10);

    BigInteger minusOne = new BigInteger("-1", 10);

    BigInteger zero = new BigInteger("0", 10);

    BigInteger one = new BigInteger("1", 10);

    BigInteger two = new BigInteger("2", 10);

    BigInteger ten = new BigInteger("10", 10);

    BigInteger sixteen = new BigInteger("16", 10);

    BigInteger oneThousand = new BigInteger("1000", 10);

    BigInteger aZillion = new BigInteger("100000000000000000000000000000000000000000000000000", 10);

    BigInteger twoToTheTen = new BigInteger("1024", 10);

    BigInteger twoToTheSeventy = two.pow(70);

    Random rand = new Random();

    BigInteger bi;

    BigInteger bi1;

    BigInteger bi2;

    BigInteger bi3;

    BigInteger bi11;

    BigInteger bi22;

    BigInteger bi33;

    BigInteger bi12;

    BigInteger bi23;

    BigInteger bi13;

    BigInteger largePos;

    BigInteger smallPos;

    BigInteger largeNeg;

    BigInteger smallNeg;

    BigInteger[][] booleanPairs;

    /**
     *
     *
     * @unknown java.math.BigInteger#BigInteger(int, java.util.Random)
     */
    public void test_ConstructorILjava_util_Random() {
        // regression test for HARMONY-1047
        try {
            new BigInteger(Integer.MAX_VALUE, ((Random) (null)));
            TestCase.fail("NegativeArraySizeException expected");
        } catch (NegativeArraySizeException e) {
            // PASSED
        }
        bi = new BigInteger(70, rand);
        bi2 = new BigInteger(70, rand);
        TestCase.assertTrue("Random number is negative", ((bi.compareTo(zero)) >= 0));
        TestCase.assertTrue("Random number is too big", ((bi.compareTo(twoToTheSeventy)) < 0));
        TestCase.assertTrue("Two random numbers in a row are the same (might not be a bug but it very likely is)", (!(bi.equals(bi2))));
        TestCase.assertTrue("Not zero", new BigInteger(0, rand).equals(BigInteger.ZERO));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#BigInteger(int, int, java.util.Random)
     */
    public void test_ConstructorIILjava_util_Random() {
        bi = new BigInteger(10, 5, rand);
        bi2 = new BigInteger(10, 5, rand);
        TestCase.assertTrue("Random number one is negative", ((bi.compareTo(zero)) >= 0));
        TestCase.assertTrue("Random number one is too big", ((bi.compareTo(twoToTheTen)) < 0));
        TestCase.assertTrue("Random number two is negative", ((bi2.compareTo(zero)) >= 0));
        TestCase.assertTrue("Random number two is too big", ((bi2.compareTo(twoToTheTen)) < 0));
        Random rand = new Random();
        BigInteger bi;
        int[] certainty = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, Integer.MIN_VALUE, (Integer.MIN_VALUE) + 1, -2, -1 };
        for (int i = 2; i <= 20; i++) {
            for (int c = 0; c < (certainty.length); c++) {
                bi = new BigInteger(i, c, rand);// Create BigInteger

                TestCase.assertTrue("Bit length incorrect", ((bi.bitLength()) == i));
            }
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#BigInteger(byte[])
     */
    public void test_Constructor$B() {
        byte[] myByteArray;
        myByteArray = new byte[]{ ((byte) (0)), ((byte) (255)), ((byte) (254)) };
        bi = new BigInteger(myByteArray);
        TestCase.assertTrue("Incorrect value for pos number", bi.equals(BigInteger.ZERO.setBit(16).subtract(two)));
        myByteArray = new byte[]{ ((byte) (255)), ((byte) (254)) };
        bi = new BigInteger(myByteArray);
        TestCase.assertTrue("Incorrect value for neg number", bi.equals(minusTwo));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#BigInteger(int, byte[])
     */
    public void test_ConstructorI$B() {
        byte[] myByteArray;
        myByteArray = new byte[]{ ((byte) (255)), ((byte) (254)) };
        bi = new BigInteger(1, myByteArray);
        TestCase.assertTrue("Incorrect value for pos number", bi.equals(BigInteger.ZERO.setBit(16).subtract(two)));
        bi = new BigInteger((-1), myByteArray);
        TestCase.assertTrue("Incorrect value for neg number", bi.equals(BigInteger.ZERO.setBit(16).subtract(two).negate()));
        myByteArray = new byte[]{ ((byte) (0)), ((byte) (0)) };
        bi = new BigInteger(0, myByteArray);
        TestCase.assertTrue("Incorrect value for zero", bi.equals(zero));
        myByteArray = new byte[]{ ((byte) (1)) };
        try {
            new BigInteger(0, myByteArray);
            TestCase.fail("Failed to throw NumberFormatException");
        } catch (NumberFormatException e) {
            // correct
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#BigInteger(java.lang.String)
     */
    public void test_constructor_String_empty() {
        try {
            new BigInteger("");
            TestCase.fail("Expected NumberFormatException for new BigInteger(\"\")");
        } catch (NumberFormatException e) {
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#toByteArray()
     */
    public void test_toByteArray() {
        byte[] myByteArray;
        byte[] anotherByteArray;
        myByteArray = new byte[]{ 97, 33, 120, 124, 50, 2, 0, 0, 0, 12, 124, 42 };
        anotherByteArray = new BigInteger(myByteArray).toByteArray();
        TestCase.assertTrue("Incorrect byte array returned", ((myByteArray.length) == (anotherByteArray.length)));
        for (int counter = (myByteArray.length) - 1; counter >= 0; counter--) {
            TestCase.assertTrue("Incorrect values in returned byte array", ((myByteArray[counter]) == (anotherByteArray[counter])));
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#isProbablePrime(int)
     */
    public void test_isProbablePrimeI() {
        int fails = 0;
        bi = new BigInteger(20, 20, rand);
        if (!(bi.isProbablePrime(17))) {
            fails++;
        }
        bi = new BigInteger("4", 10);
        if (bi.isProbablePrime(17)) {
            TestCase.fail(("isProbablePrime failed for: " + (bi)));
        }
        bi = BigInteger.valueOf((17L * 13L));
        if (bi.isProbablePrime(17)) {
            TestCase.fail(("isProbablePrime failed for: " + (bi)));
        }
        for (long a = 2; a < 1000; a++) {
            if (isPrime(a)) {
                TestCase.assertTrue("false negative on prime number <1000", BigInteger.valueOf(a).isProbablePrime(5));
            } else
                if (BigInteger.valueOf(a).isProbablePrime(17)) {
                    System.out.println(("isProbablePrime failed for: " + a));
                    fails++;
                }

        }
        for (int a = 0; a < 1000; a++) {
            bi = BigInteger.valueOf(rand.nextInt(1000000)).multiply(BigInteger.valueOf(rand.nextInt(1000000)));
            if (bi.isProbablePrime(17)) {
                System.out.println(("isProbablePrime failed for: " + (bi)));
                fails++;
            }
        }
        for (int a = 0; a < 200; a++) {
            bi = new BigInteger(70, rand).multiply(new BigInteger(70, rand));
            if (bi.isProbablePrime(17)) {
                System.out.println(("isProbablePrime failed for: " + (bi)));
                fails++;
            }
        }
        TestCase.assertTrue("Too many false positives - may indicate a problem", (fails <= 1));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object() {
        TestCase.assertTrue("0=0", zero.equals(BigInteger.valueOf(0)));
        TestCase.assertTrue("-123=-123", BigInteger.valueOf((-123)).equals(BigInteger.valueOf((-123))));
        TestCase.assertTrue("0=1", (!(zero.equals(one))));
        TestCase.assertTrue("0=-1", (!(zero.equals(minusOne))));
        TestCase.assertTrue("1=-1", (!(one.equals(minusOne))));
        TestCase.assertTrue("bi3=bi3", bi3.equals(bi3));
        TestCase.assertTrue("bi3=copy of bi3", bi3.equals(bi3.negate().negate()));
        TestCase.assertTrue("bi3=bi2", (!(bi3.equals(bi2))));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#compareTo(java.math.BigInteger)
     */
    public void test_compareToLjava_math_BigInteger() {
        TestCase.assertTrue("Smaller number returned >= 0", ((one.compareTo(two)) < 0));
        TestCase.assertTrue("Larger number returned >= 0", ((two.compareTo(one)) > 0));
        TestCase.assertTrue("Equal numbers did not return 0", ((one.compareTo(one)) == 0));
        TestCase.assertTrue("Neg number messed things up", ((two.negate().compareTo(one)) < 0));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#intValue()
     */
    public void test_intValue() {
        TestCase.assertTrue("Incorrect intValue for 2**70", ((twoToTheSeventy.intValue()) == 0));
        TestCase.assertTrue("Incorrect intValue for 2", ((two.intValue()) == 2));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#longValue()
     */
    public void test_longValue() {
        TestCase.assertTrue("Incorrect longValue for 2**70", ((twoToTheSeventy.longValue()) == 0));
        TestCase.assertTrue("Incorrect longValue for 2", ((two.longValue()) == 2));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#valueOf(long)
     */
    public void test_valueOfJ() {
        TestCase.assertTrue("Incurred number returned for 2", BigInteger.valueOf(2L).equals(two));
        TestCase.assertTrue("Incurred number returned for 200", BigInteger.valueOf(200L).equals(BigInteger.valueOf(139).add(BigInteger.valueOf(61))));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#add(java.math.BigInteger)
     */
    public void test_addLjava_math_BigInteger() {
        TestCase.assertTrue("Incorrect sum--wanted a zillion", aZillion.add(aZillion).add(aZillion.negate()).equals(aZillion));
        TestCase.assertTrue("0+0", zero.add(zero).equals(zero));
        TestCase.assertTrue("0+1", zero.add(one).equals(one));
        TestCase.assertTrue("1+0", one.add(zero).equals(one));
        TestCase.assertTrue("1+1", one.add(one).equals(two));
        TestCase.assertTrue("0+(-1)", zero.add(minusOne).equals(minusOne));
        TestCase.assertTrue("(-1)+0", minusOne.add(zero).equals(minusOne));
        TestCase.assertTrue("(-1)+(-1)", minusOne.add(minusOne).equals(minusTwo));
        TestCase.assertTrue("1+(-1)", one.add(minusOne).equals(zero));
        TestCase.assertTrue("(-1)+1", minusOne.add(one).equals(zero));
        for (int i = 0; i < 200; i++) {
            BigInteger midbit = zero.setBit(i);
            TestCase.assertTrue(("add fails to carry on bit " + i), midbit.add(midbit).equals(zero.setBit((i + 1))));
        }
        BigInteger bi2p3 = bi2.add(bi3);
        BigInteger bi3p2 = bi3.add(bi2);
        TestCase.assertTrue("bi2p3=bi3p2", bi2p3.equals(bi3p2));
        // add large positive + small positive
        // add large positive + small negative
        // add large negative + small positive
        // add large negative + small negative
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#negate()
     */
    public void test_negate() {
        TestCase.assertTrue("Single negation of zero did not result in zero", zero.negate().equals(zero));
        TestCase.assertTrue("Single negation resulted in original nonzero number", (!(aZillion.negate().equals(aZillion))));
        TestCase.assertTrue("Double negation did not result in original number", aZillion.negate().negate().equals(aZillion));
        TestCase.assertTrue("0.neg", zero.negate().equals(zero));
        TestCase.assertTrue("1.neg", one.negate().equals(minusOne));
        TestCase.assertTrue("2.neg", two.negate().equals(minusTwo));
        TestCase.assertTrue("-1.neg", minusOne.negate().equals(one));
        TestCase.assertTrue("-2.neg", minusTwo.negate().equals(two));
        TestCase.assertTrue("0x62EB40FEF85AA9EBL*2.neg", BigInteger.valueOf((7127862299076504043L * 2)).negate().equals(BigInteger.valueOf(((-7127862299076504043L) * 2))));
        for (int i = 0; i < 200; i++) {
            BigInteger midbit = zero.setBit(i);
            BigInteger negate = midbit.negate();
            TestCase.assertTrue("negate negate", negate.negate().equals(midbit));
            TestCase.assertTrue(("neg fails on bit " + i), midbit.negate().add(midbit).equals(zero));
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#signum()
     */
    public void test_signum() {
        TestCase.assertTrue("Wrong positive signum", ((two.signum()) == 1));
        TestCase.assertTrue("Wrong zero signum", ((zero.signum()) == 0));
        TestCase.assertTrue("Wrong neg zero signum", ((zero.negate().signum()) == 0));
        TestCase.assertTrue("Wrong neg signum", ((two.negate().signum()) == (-1)));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#abs()
     */
    public void test_abs() {
        TestCase.assertTrue("Invalid number returned for zillion", aZillion.negate().abs().equals(aZillion.abs()));
        TestCase.assertTrue("Invalid number returned for zero neg", zero.negate().abs().equals(zero));
        TestCase.assertTrue("Invalid number returned for zero", zero.abs().equals(zero));
        TestCase.assertTrue("Invalid number returned for two", two.negate().abs().equals(two));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#pow(int)
     */
    public void test_powI() {
        TestCase.assertTrue("Incorrect exponent returned for 2**10", two.pow(10).equals(twoToTheTen));
        TestCase.assertTrue("Incorrect exponent returned for 2**70", two.pow(30).multiply(two.pow(40)).equals(twoToTheSeventy));
        TestCase.assertTrue("Incorrect exponent returned for 10**50", ten.pow(50).equals(aZillion));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#modInverse(java.math.BigInteger)
     */
    public void test_modInverseLjava_math_BigInteger() {
        BigInteger a = zero;
        BigInteger mod;
        BigInteger inv;
        for (int j = 3; j < 50; j++) {
            mod = BigInteger.valueOf(j);
            for (int i = (-j) + 1; i < j; i++) {
                try {
                    a = BigInteger.valueOf(i);
                    inv = a.modInverse(mod);
                    TestCase.assertTrue(((((("bad inverse: " + a) + " inv mod ") + mod) + " equals ") + inv), one.equals(a.multiply(inv).mod(mod)));
                    TestCase.assertTrue(((((("inverse greater than modulo: " + a) + " inv mod ") + mod) + " equals ") + inv), ((inv.compareTo(mod)) < 0));
                    TestCase.assertTrue(((((("inverse less than zero: " + a) + " inv mod ") + mod) + " equals ") + inv), ((inv.compareTo(BigInteger.ZERO)) >= 0));
                } catch (ArithmeticException e) {
                    TestCase.assertTrue(((("should have found inverse for " + a) + " mod ") + mod), (!(one.equals(a.gcd(mod)))));
                }
            }
        }
        for (int j = 1; j < 10; j++) {
            mod = bi2.add(BigInteger.valueOf(j));
            for (int i = 0; i < 20; i++) {
                try {
                    a = bi3.add(BigInteger.valueOf(i));
                    inv = a.modInverse(mod);
                    TestCase.assertTrue(((((("bad inverse: " + a) + " inv mod ") + mod) + " equals ") + inv), one.equals(a.multiply(inv).mod(mod)));
                    TestCase.assertTrue(((((("inverse greater than modulo: " + a) + " inv mod ") + mod) + " equals ") + inv), ((inv.compareTo(mod)) < 0));
                    TestCase.assertTrue(((((("inverse less than zero: " + a) + " inv mod ") + mod) + " equals ") + inv), ((inv.compareTo(BigInteger.ZERO)) >= 0));
                } catch (ArithmeticException e) {
                    TestCase.assertTrue(((("should have found inverse for " + a) + " mod ") + mod), (!(one.equals(a.gcd(mod)))));
                }
            }
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#shiftRight(int)
     */
    public void test_shiftRightI() {
        TestCase.assertTrue("1 >> 0", BigInteger.valueOf(1).shiftRight(0).equals(BigInteger.ONE));
        TestCase.assertTrue("1 >> 1", BigInteger.valueOf(1).shiftRight(1).equals(BigInteger.ZERO));
        TestCase.assertTrue("1 >> 63", BigInteger.valueOf(1).shiftRight(63).equals(BigInteger.ZERO));
        TestCase.assertTrue("1 >> 64", BigInteger.valueOf(1).shiftRight(64).equals(BigInteger.ZERO));
        TestCase.assertTrue("1 >> 65", BigInteger.valueOf(1).shiftRight(65).equals(BigInteger.ZERO));
        TestCase.assertTrue("1 >> 1000", BigInteger.valueOf(1).shiftRight(1000).equals(BigInteger.ZERO));
        TestCase.assertTrue("-1 >> 0", BigInteger.valueOf((-1)).shiftRight(0).equals(minusOne));
        TestCase.assertTrue("-1 >> 1", BigInteger.valueOf((-1)).shiftRight(1).equals(minusOne));
        TestCase.assertTrue("-1 >> 63", BigInteger.valueOf((-1)).shiftRight(63).equals(minusOne));
        TestCase.assertTrue("-1 >> 64", BigInteger.valueOf((-1)).shiftRight(64).equals(minusOne));
        TestCase.assertTrue("-1 >> 65", BigInteger.valueOf((-1)).shiftRight(65).equals(minusOne));
        TestCase.assertTrue("-1 >> 1000", BigInteger.valueOf((-1)).shiftRight(1000).equals(minusOne));
        BigInteger a = BigInteger.ONE;
        BigInteger c = bi3;
        BigInteger E = bi3.negate();
        BigInteger e = E;
        for (int i = 0; i < 200; i++) {
            BigInteger b = BigInteger.ZERO.setBit(i);
            TestCase.assertTrue("a==b", a.equals(b));
            a = a.shiftLeft(1);
            TestCase.assertTrue("a non-neg", ((a.signum()) >= 0));
            BigInteger d = bi3.shiftRight(i);
            TestCase.assertTrue("c==d", c.equals(d));
            c = c.shiftRight(1);
            TestCase.assertTrue(">>1 == /2", d.divide(two).equals(c));
            TestCase.assertTrue("c non-neg", ((c.signum()) >= 0));
            BigInteger f = E.shiftRight(i);
            TestCase.assertTrue("e==f", e.equals(f));
            e = e.shiftRight(1);
            TestCase.assertTrue(">>1 == /2", f.subtract(one).divide(two).equals(e));
            TestCase.assertTrue("e negative", ((e.signum()) == (-1)));
            TestCase.assertTrue("b >> i", b.shiftRight(i).equals(one));
            TestCase.assertTrue("b >> i+1", b.shiftRight((i + 1)).equals(zero));
            TestCase.assertTrue("b >> i-1", b.shiftRight((i - 1)).equals(two));
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#shiftLeft(int)
     */
    public void test_shiftLeftI() {
        TestCase.assertTrue("1 << 0", one.shiftLeft(0).equals(one));
        TestCase.assertTrue("1 << 1", one.shiftLeft(1).equals(two));
        TestCase.assertTrue("1 << 63", one.shiftLeft(63).equals(new BigInteger("8000000000000000", 16)));
        TestCase.assertTrue("1 << 64", one.shiftLeft(64).equals(new BigInteger("10000000000000000", 16)));
        TestCase.assertTrue("1 << 65", one.shiftLeft(65).equals(new BigInteger("20000000000000000", 16)));
        TestCase.assertTrue("-1 << 0", minusOne.shiftLeft(0).equals(minusOne));
        TestCase.assertTrue("-1 << 1", minusOne.shiftLeft(1).equals(minusTwo));
        TestCase.assertTrue("-1 << 63", minusOne.shiftLeft(63).equals(new BigInteger("-9223372036854775808")));
        TestCase.assertTrue("-1 << 64", minusOne.shiftLeft(64).equals(new BigInteger("-18446744073709551616")));
        TestCase.assertTrue("-1 << 65", minusOne.shiftLeft(65).equals(new BigInteger("-36893488147419103232")));
        BigInteger a = bi3;
        BigInteger c = minusOne;
        for (int i = 0; i < 200; i++) {
            BigInteger b = bi3.shiftLeft(i);
            TestCase.assertTrue("a==b", a.equals(b));
            TestCase.assertTrue("a >> i == bi3", a.shiftRight(i).equals(bi3));
            a = a.shiftLeft(1);
            TestCase.assertTrue("<<1 == *2", b.multiply(two).equals(a));
            TestCase.assertTrue("a non-neg", ((a.signum()) >= 0));
            TestCase.assertTrue("a.bitCount==b.bitCount", ((a.bitCount()) == (b.bitCount())));
            BigInteger d = minusOne.shiftLeft(i);
            TestCase.assertTrue("c==d", c.equals(d));
            c = c.shiftLeft(1);
            TestCase.assertTrue("<<1 == *2 negative", d.multiply(two).equals(c));
            TestCase.assertTrue("c negative", ((c.signum()) == (-1)));
            TestCase.assertTrue("d >> i == minusOne", d.shiftRight(i).equals(minusOne));
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#multiply(java.math.BigInteger)
     */
    public void test_multiplyLjava_math_BigInteger() {
        TestCase.assertTrue("Incorrect sum--wanted three zillion", aZillion.add(aZillion).add(aZillion).equals(aZillion.multiply(new BigInteger("3", 10))));
        TestCase.assertTrue("0*0", zero.multiply(zero).equals(zero));
        TestCase.assertTrue("0*1", zero.multiply(one).equals(zero));
        TestCase.assertTrue("1*0", one.multiply(zero).equals(zero));
        TestCase.assertTrue("1*1", one.multiply(one).equals(one));
        TestCase.assertTrue("0*(-1)", zero.multiply(minusOne).equals(zero));
        TestCase.assertTrue("(-1)*0", minusOne.multiply(zero).equals(zero));
        TestCase.assertTrue("(-1)*(-1)", minusOne.multiply(minusOne).equals(one));
        TestCase.assertTrue("1*(-1)", one.multiply(minusOne).equals(minusOne));
        TestCase.assertTrue("(-1)*1", minusOne.multiply(one).equals(minusOne));
        testAllMults(bi1, bi1, bi11);
        testAllMults(bi2, bi2, bi22);
        testAllMults(bi3, bi3, bi33);
        testAllMults(bi1, bi2, bi12);
        testAllMults(bi1, bi3, bi13);
        testAllMults(bi2, bi3, bi23);
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#divide(java.math.BigInteger)
     */
    public void test_divideLjava_math_BigInteger() {
        testAllDivs(bi33, bi3);
        testAllDivs(bi22, bi2);
        testAllDivs(bi11, bi1);
        testAllDivs(bi13, bi1);
        testAllDivs(bi13, bi3);
        testAllDivs(bi12, bi1);
        testAllDivs(bi12, bi2);
        testAllDivs(bi23, bi2);
        testAllDivs(bi23, bi3);
        testAllDivs(largePos, bi1);
        testAllDivs(largePos, bi2);
        testAllDivs(largePos, bi3);
        testAllDivs(largeNeg, bi1);
        testAllDivs(largeNeg, bi2);
        testAllDivs(largeNeg, bi3);
        testAllDivs(largeNeg, largePos);
        testAllDivs(largePos, largeNeg);
        testAllDivs(bi3, bi3);
        testAllDivs(bi2, bi2);
        testAllDivs(bi1, bi1);
        testDivRanges(bi1);
        testDivRanges(bi2);
        testDivRanges(bi3);
        testDivRanges(smallPos);
        testDivRanges(largePos);
        testDivRanges(new BigInteger("62EB40FEF85AA9EB", 16));
        testAllDivs(BigInteger.valueOf(876209345852L), BigInteger.valueOf(7402403685L));
        try {
            largePos.divide(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            bi1.divide(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            bi3.negate().divide(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            zero.divide(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#remainder(java.math.BigInteger)
     */
    public void test_remainderLjava_math_BigInteger() {
        try {
            largePos.remainder(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            bi1.remainder(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            bi3.negate().remainder(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            zero.remainder(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#mod(java.math.BigInteger)
     */
    public void test_modLjava_math_BigInteger() {
        try {
            largePos.mod(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            bi1.mod(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            bi3.negate().mod(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            zero.mod(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#divideAndRemainder(java.math.BigInteger)
     */
    public void test_divideAndRemainderLjava_math_BigInteger() {
        try {
            largePos.divideAndRemainder(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            bi1.divideAndRemainder(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            bi3.negate().divideAndRemainder(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
        try {
            zero.divideAndRemainder(zero);
            TestCase.fail("ArithmeticException expected");
        } catch (ArithmeticException e) {
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#BigInteger(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        TestCase.assertTrue("new(0)", new BigInteger("0").equals(BigInteger.valueOf(0)));
        TestCase.assertTrue("new(1)", new BigInteger("1").equals(BigInteger.valueOf(1)));
        TestCase.assertTrue("new(12345678901234)", new BigInteger("12345678901234").equals(BigInteger.valueOf(12345678901234L)));
        TestCase.assertTrue("new(-1)", new BigInteger("-1").equals(BigInteger.valueOf((-1))));
        TestCase.assertTrue("new(-12345678901234)", new BigInteger("-12345678901234").equals(BigInteger.valueOf((-12345678901234L))));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#BigInteger(java.lang.String, int)
     */
    public void test_ConstructorLjava_lang_StringI() {
        TestCase.assertTrue("new(0,16)", new BigInteger("0", 16).equals(BigInteger.valueOf(0)));
        TestCase.assertTrue("new(1,16)", new BigInteger("1", 16).equals(BigInteger.valueOf(1)));
        TestCase.assertTrue("new(ABF345678901234,16)", new BigInteger("ABF345678901234", 16).equals(BigInteger.valueOf(774395206925554228L)));
        TestCase.assertTrue("new(abf345678901234,16)", new BigInteger("abf345678901234", 16).equals(BigInteger.valueOf(774395206925554228L)));
        TestCase.assertTrue("new(-1,16)", new BigInteger("-1", 16).equals(BigInteger.valueOf((-1))));
        TestCase.assertTrue("new(-ABF345678901234,16)", new BigInteger("-ABF345678901234", 16).equals(BigInteger.valueOf((-774395206925554228L))));
        TestCase.assertTrue("new(-abf345678901234,16)", new BigInteger("-abf345678901234", 16).equals(BigInteger.valueOf((-774395206925554228L))));
        TestCase.assertTrue("new(-101010101,2)", new BigInteger("-101010101", 2).equals(BigInteger.valueOf((-341))));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#toString()
     */
    public void test_toString() {
        TestCase.assertTrue("0.toString", "0".equals(BigInteger.valueOf(0).toString()));
        TestCase.assertTrue("1.toString", "1".equals(BigInteger.valueOf(1).toString()));
        TestCase.assertTrue("12345678901234.toString", "12345678901234".equals(BigInteger.valueOf(12345678901234L).toString()));
        TestCase.assertTrue("-1.toString", "-1".equals(BigInteger.valueOf((-1)).toString()));
        TestCase.assertTrue("-12345678901234.toString", "-12345678901234".equals(BigInteger.valueOf((-12345678901234L)).toString()));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#toString(int)
     */
    public void test_toStringI() {
        TestCase.assertTrue("0.toString(16)", "0".equals(BigInteger.valueOf(0).toString(16)));
        TestCase.assertTrue("1.toString(16)", "1".equals(BigInteger.valueOf(1).toString(16)));
        TestCase.assertTrue("ABF345678901234.toString(16)", "abf345678901234".equals(BigInteger.valueOf(774395206925554228L).toString(16)));
        TestCase.assertTrue("-1.toString(16)", "-1".equals(BigInteger.valueOf((-1)).toString(16)));
        TestCase.assertTrue("-ABF345678901234.toString(16)", "-abf345678901234".equals(BigInteger.valueOf((-774395206925554228L)).toString(16)));
        TestCase.assertTrue("-101010101.toString(2)", "-101010101".equals(BigInteger.valueOf((-341)).toString(2)));
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#and(java.math.BigInteger)
     */
    public void test_andLjava_math_BigInteger() {
        for (BigInteger[] element : booleanPairs) {
            BigInteger i1 = element[0];
            BigInteger i2 = element[1];
            BigInteger res = i1.and(i2);
            TestCase.assertTrue("symmetry of and", res.equals(i2.and(i1)));
            int len = (Math.max(i1.bitLength(), i2.bitLength())) + 66;
            for (int i = 0; i < len; i++) {
                TestCase.assertTrue("and", (((i1.testBit(i)) && (i2.testBit(i))) == (res.testBit(i))));
            }
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#or(java.math.BigInteger)
     */
    public void test_orLjava_math_BigInteger() {
        for (BigInteger[] element : booleanPairs) {
            BigInteger i1 = element[0];
            BigInteger i2 = element[1];
            BigInteger res = i1.or(i2);
            TestCase.assertTrue("symmetry of or", res.equals(i2.or(i1)));
            int len = (Math.max(i1.bitLength(), i2.bitLength())) + 66;
            for (int i = 0; i < len; i++) {
                TestCase.assertTrue("or", (((i1.testBit(i)) || (i2.testBit(i))) == (res.testBit(i))));
            }
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#xor(java.math.BigInteger)
     */
    public void test_xorLjava_math_BigInteger() {
        for (BigInteger[] element : booleanPairs) {
            BigInteger i1 = element[0];
            BigInteger i2 = element[1];
            BigInteger res = i1.xor(i2);
            TestCase.assertTrue("symmetry of xor", res.equals(i2.xor(i1)));
            int len = (Math.max(i1.bitLength(), i2.bitLength())) + 66;
            for (int i = 0; i < len; i++) {
                TestCase.assertTrue("xor", (((i1.testBit(i)) ^ (i2.testBit(i))) == (res.testBit(i))));
            }
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#not()
     */
    public void test_not() {
        for (BigInteger[] element : booleanPairs) {
            BigInteger i1 = element[0];
            BigInteger res = i1.not();
            int len = (i1.bitLength()) + 66;
            for (int i = 0; i < len; i++) {
                TestCase.assertTrue("not", ((!(i1.testBit(i))) == (res.testBit(i))));
            }
        }
    }

    /**
     *
     *
     * @unknown java.math.BigInteger#andNot(java.math.BigInteger)
     */
    public void test_andNotLjava_math_BigInteger() {
        for (BigInteger[] element : booleanPairs) {
            BigInteger i1 = element[0];
            BigInteger i2 = element[1];
            BigInteger res = i1.andNot(i2);
            int len = (Math.max(i1.bitLength(), i2.bitLength())) + 66;
            for (int i = 0; i < len; i++) {
                TestCase.assertTrue("andNot", (((i1.testBit(i)) && (!(i2.testBit(i)))) == (res.testBit(i))));
            }
            // asymmetrical
            i1 = element[1];
            i2 = element[0];
            res = i1.andNot(i2);
            for (int i = 0; i < len; i++) {
                TestCase.assertTrue("andNot reversed", (((i1.testBit(i)) && (!(i2.testBit(i)))) == (res.testBit(i))));
            }
        }
        // regression for HARMONY-4653
        try {
            BigInteger.ZERO.andNot(null);
            TestCase.fail("should throw NPE");
        } catch (Exception e) {
            // expected
        }
        BigInteger bi = new BigInteger(0, new byte[]{  });
        TestCase.assertEquals(BigInteger.ZERO, bi.andNot(BigInteger.ZERO));
    }

    public void testClone() {
        // Regression test for HARMONY-1770
        BigIntegerTest.MyBigInteger myBigInteger = new BigIntegerTest.MyBigInteger("12345");
        myBigInteger = ((BigIntegerTest.MyBigInteger) (myBigInteger.clone()));
    }

    static class MyBigInteger extends BigInteger implements Cloneable {
        public MyBigInteger(String val) {
            super(val);
        }

        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }
}

