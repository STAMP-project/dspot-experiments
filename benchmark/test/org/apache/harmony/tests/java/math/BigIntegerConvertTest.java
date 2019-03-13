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
 * @author Elena Semukhina
 */
package org.apache.harmony.tests.java.math;


import java.math.BigInteger;
import junit.framework.TestCase;


/**
 * Class:   java.math.BigInteger
 * Methods: intValue, longValue, toByteArray(), valueOf(long val),
 * floatValue(), doubleValue()
 */
public class BigIntegerConvertTest extends TestCase {
    /**
     * Return the double value of ZERO.
     */
    public void testDoubleValueZero() {
        String a = "0";
        double result = 0.0;
        double aNumber = new BigInteger(a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a double value.
     * The number's length is less than 64 bits.
     */
    public void testDoubleValuePositive1() {
        String a = "27467238945";
        double result = 2.7467238945E10;
        double aNumber = new BigInteger(a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a double value.
     * The number's bit length is inside [63, 1024].
     */
    public void testDoubleValuePositive2() {
        String a = "2746723894572364578265426346273456972";
        double result = 2.7467238945723645E36;
        double aNumber = new BigInteger(a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a negative number to a double value.
     * The number's bit length is less than 64 bits.
     */
    public void testDoubleValueNegative1() {
        String a = "-27467238945";
        double result = -2.7467238945E10;
        double aNumber = new BigInteger(a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a negative number to a double value.
     * The number's bit length is inside [63, 1024].
     */
    public void testDoubleValueNegative2() {
        String a = "-2746723894572364578265426346273456972";
        double result = -2.7467238945723645E36;
        double aNumber = new BigInteger(a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a double value.
     * Rounding is needed.
     * The rounding bit is 1 and the next bit to the left is 1.
     */
    public void testDoubleValuePosRounded1() {
        byte[] a = new byte[]{ -128, 1, 2, 3, 4, 5, 60, 23, 1, -3, -5 };
        int aSign = 1;
        double result = 1.54747264387948E26;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a double value.
     * Rounding is needed.
     * The rounding bit is 1 and the next bit to the left is 0
     * but some of dropped bits are 1s.
     */
    public void testDoubleValuePosRounded2() {
        byte[] a = new byte[]{ -128, 1, 2, 3, 4, 5, 36, 23, 1, -3, -5 };
        int aSign = 1;
        double result = 1.547472643879479E26;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a double value.
     * Rounding is NOT needed.
     */
    public void testDoubleValuePosNotRounded() {
        byte[] a = new byte[]{ -128, 1, 2, 3, 4, 5, -128, 23, 1, -3, -5 };
        int aSign = 1;
        double result = 1.5474726438794828E26;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a double value.
     * Rounding is needed.
     */
    public void testDoubleValueNegRounded1() {
        byte[] a = new byte[]{ -128, 1, 2, 3, 4, 5, 60, 23, 1, -3, -5 };
        int aSign = -1;
        double result = -1.54747264387948E26;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a double value.
     * Rounding is needed.
     * The rounding bit is 1 and the next bit to the left is 0
     * but some of dropped bits are 1s.
     */
    public void testDoubleValueNegRounded2() {
        byte[] a = new byte[]{ -128, 1, 2, 3, 4, 5, 36, 23, 1, -3, -5 };
        int aSign = -1;
        double result = -1.547472643879479E26;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a double value.
     * Rounding is NOT needed.
     */
    public void testDoubleValueNegNotRounded() {
        byte[] a = new byte[]{ -128, 1, 2, 3, 4, 5, -128, 23, 1, -3, -5 };
        int aSign = -1;
        double result = -1.5474726438794828E26;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a double value.
     * The exponent is 1023 and the mantissa is all 1s.
     * The rounding bit is 0.
     * The result is Double.MAX_VALUE.
     */
    public void testDoubleValuePosMaxValue() {
        byte[] a = new byte[]{ 0, -1, -1, -1, -1, -1, -1, -8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        int aSign = 1;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == (Double.MAX_VALUE)));
    }

    /**
     * Convert a negative number to a double value.
     * The exponent is 1023 and the mantissa is all 1s.
     * The result is -Double.MAX_VALUE.
     */
    public void testDoubleValueNegMaxValue() {
        byte[] a = new byte[]{ 0, -1, -1, -1, -1, -1, -1, -8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        int aSign = -1;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == (-(Double.MAX_VALUE))));
    }

    /**
     * Convert a positive number to a double value.
     * The exponent is 1023 and the mantissa is all 1s.
     * The rounding bit is 1.
     * The result is Double.POSITIVE_INFINITY.
     */
    public void testDoubleValuePositiveInfinity1() {
        byte[] a = new byte[]{ -1, -1, -1, -1, -1, -1, -1, -8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        int aSign = 1;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == (Double.POSITIVE_INFINITY)));
    }

    /**
     * Convert a positive number to a double value.
     * The number's bit length is greater than 1024.
     */
    public void testDoubleValuePositiveInfinity2() {
        String a = "2746723894572364578265426346273456972283746872364768676747462342342342342342342342323423423423423423426767456345745293762384756238475634563456845634568934568347586346578648576478568456457634875673845678456786587345873645767456834756745763457863485768475678465783456702897830296720476846578634576384567845678346573465786457863";
        double aNumber = new BigInteger(a).doubleValue();
        TestCase.assertTrue((aNumber == (Double.POSITIVE_INFINITY)));
    }

    /**
     * Convert a negative number to a double value.
     * The number's bit length is greater than 1024.
     */
    public void testDoubleValueNegativeInfinity1() {
        String a = "-2746723894572364578265426346273456972283746872364768676747462342342342342342342342323423423423423423426767456345745293762384756238475634563456845634568934568347586346578648576478568456457634875673845678456786587345873645767456834756745763457863485768475678465783456702897830296720476846578634576384567845678346573465786457863";
        double aNumber = new BigInteger(a).doubleValue();
        TestCase.assertTrue((aNumber == (Double.NEGATIVE_INFINITY)));
    }

    /**
     * Convert a negative number to a double value.
     * The exponent is 1023 and the mantissa is all 0s.
     * The rounding bit is 0.
     * The result is Double.NEGATIVE_INFINITY.
     */
    public void testDoubleValueNegativeInfinity2() {
        byte[] a = new byte[]{ -1, -1, -1, -1, -1, -1, -1, -8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        int aSign = -1;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == (Double.NEGATIVE_INFINITY)));
    }

    /**
     * Convert a positive number to a double value.
     * The exponent is 1023 and the mantissa is all 0s
     * but the 54th bit (implicit) is 1.
     */
    public void testDoubleValuePosMantissaIsZero() {
        byte[] a = new byte[]{ -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        int aSign = 1;
        double result = 8.98846567431158E307;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a double value.
     * The exponent is 1023 and the mantissa is all 0s
     * but the 54th bit (implicit) is 1.
     */
    public void testDoubleValueNegMantissaIsZero() {
        byte[] a = new byte[]{ -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        int aSign = -1;
        double aNumber = new BigInteger(aSign, a).doubleValue();
        TestCase.assertTrue((aNumber == (-8.98846567431158E307)));
    }

    /**
     * Return the float value of ZERO.
     */
    public void testFloatValueZero() {
        String a = "0";
        float result = 0.0F;
        float aNumber = new BigInteger(a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a float value.
     * The number's length is less than 32 bits.
     */
    public void testFloatValuePositive1() {
        String a = "27467238";
        float result = 2.7467238E7F;
        float aNumber = new BigInteger(a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a float value.
     * The number's bit length is inside [32, 127].
     */
    public void testFloatValuePositive2() {
        String a = "27467238945723645782";
        float result = 2.7467239E19F;
        float aNumber = new BigInteger(a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a negative number to a float value.
     * The number's bit length is less than 32 bits.
     */
    public void testFloatValueNegative1() {
        String a = "-27467238";
        float result = -2.7467238E7F;
        float aNumber = new BigInteger(a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a negative number to a doufloatble value.
     * The number's bit length is inside [63, 1024].
     */
    public void testFloatValueNegative2() {
        String a = "-27467238945723645782";
        float result = -2.7467239E19F;
        float aNumber = new BigInteger(a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a float value.
     * Rounding is needed.
     * The rounding bit is 1 and the next bit to the left is 1.
     */
    public void testFloatValuePosRounded1() {
        byte[] a = new byte[]{ -128, 1, -1, -4, 4, 5, 60, 23, 1, -3, -5 };
        int aSign = 1;
        float result = 1.5475195E26F;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a float value.
     * Rounding is needed.
     * The rounding bit is 1 and the next bit to the left is 0
     * but some of dropped bits are 1s.
     */
    public void testFloatValuePosRounded2() {
        byte[] a = new byte[]{ -128, 1, 2, -128, 4, 5, 60, 23, 1, -3, -5 };
        int aSign = 1;
        float result = 1.5474728E26F;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a float value.
     * Rounding is NOT needed.
     */
    public void testFloatValuePosNotRounded() {
        byte[] a = new byte[]{ -128, 1, 2, 3, 4, 5, 60, 23, 1, -3, -5 };
        int aSign = 1;
        float result = 1.5474726E26F;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a float value.
     * Rounding is needed.
     */
    public void testFloatValueNegRounded1() {
        byte[] a = new byte[]{ -128, 1, -1, -4, 4, 5, 60, 23, 1, -3, -5 };
        int aSign = -1;
        float result = -1.5475195E26F;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a float value.
     * Rounding is needed.
     * The rounding bit is 1 and the next bit to the left is 0
     * but some of dropped bits are 1s.
     */
    public void testFloatValueNegRounded2() {
        byte[] a = new byte[]{ -128, 1, 2, -128, 4, 5, 60, 23, 1, -3, -5 };
        int aSign = -1;
        float result = -1.5474728E26F;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a float value.
     * Rounding is NOT needed.
     */
    public void testFloatValueNegNotRounded() {
        byte[] a = new byte[]{ -128, 1, 2, 3, 4, 5, 60, 23, 1, -3, -5 };
        int aSign = -1;
        float result = -1.5474726E26F;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a float value.
     * The exponent is 1023 and the mantissa is all 1s.
     * The rounding bit is 0.
     * The result is Float.MAX_VALUE.
     */
    public void testFloatValuePosMaxValue() {
        byte[] a = new byte[]{ 0, -1, -1, -1, 0, -1, -1, -8, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        int aSign = 1;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == (Float.MAX_VALUE)));
    }

    /**
     * Convert a negative number to a float value.
     * The exponent is 1023 and the mantissa is all 1s.
     * The rounding bit is 0.
     * The result is -Float.MAX_VALUE.
     */
    public void testFloatValueNegMaxValue() {
        byte[] a = new byte[]{ 0, -1, -1, -1, 0, -1, -1, -8, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        int aSign = -1;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == (-(Float.MAX_VALUE))));
    }

    /**
     * Convert a positive number to a float value.
     * The exponent is 1023 and the mantissa is all 1s.
     * The rounding bit is 1.
     * The result is Float.POSITIVE_INFINITY.
     */
    public void testFloatValuePositiveInfinity1() {
        byte[] a = new byte[]{ 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        int aSign = 1;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == (Float.POSITIVE_INFINITY)));
    }

    /**
     * Convert a positive number to a float value.
     * The number's bit length is greater than 127.
     */
    public void testFloatValuePositiveInfinity2() {
        String a = "2746723894572364578265426346273456972283746872364768676747462342342342342342342342323423423423423423426767456345745293762384756238475634563456845634568934568347586346578648576478568456457634875673845678456786587345873645767456834756745763457863485768475678465783456702897830296720476846578634576384567845678346573465786457863";
        float aNumber = new BigInteger(a).floatValue();
        TestCase.assertTrue((aNumber == (Float.POSITIVE_INFINITY)));
    }

    /**
     * Convert a negative number to a float value.
     * The number's bit length is greater than 127.
     */
    public void testFloatValueNegativeInfinity1() {
        String a = "-2746723894572364578265426346273456972283746872364768676747462342342342342342342342323423423423423423426767456345745293762384756238475634563456845634568934568347586346578648576478568456457634875673845678456786587345873645767456834756745763457863485768475678465783456702897830296720476846578634576384567845678346573465786457863";
        float aNumber = new BigInteger(a).floatValue();
        TestCase.assertTrue((aNumber == (Float.NEGATIVE_INFINITY)));
    }

    /**
     * Convert a negative number to a float value.
     * The exponent is 1023 and the mantissa is all 0s.
     * The rounding bit is 0.
     * The result is Float.NEGATIVE_INFINITY.
     */
    public void testFloatValueNegativeInfinity2() {
        byte[] a = new byte[]{ 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        int aSign = -1;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == (Float.NEGATIVE_INFINITY)));
    }

    /**
     * Convert a positive number to a float value.
     * The exponent is 1023 and the mantissa is all 0s
     * but the 54th bit (implicit) is 1.
     */
    public void testFloatValuePosMantissaIsZero() {
        byte[] a = new byte[]{ -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        int aSign = 1;
        float result = 1.7014118E38F;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive number to a double value.
     * The exponent is 1023 and the mantissa is all 0s
     * but the 54th bit (implicit) is 1.
     */
    public void testFloatValueNegMantissaIsZero() {
        byte[] a = new byte[]{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        int aSign = -1;
        float aNumber = new BigInteger(aSign, a).floatValue();
        TestCase.assertTrue((aNumber == (Float.NEGATIVE_INFINITY)));
    }

    /**
     * Convert a negative number to a float value.
     * The number's bit length is less than 32 bits.
     */
    public void testFloatValueBug2482() {
        String a = "2147483649";
        float result = 2.14748365E9F;
        float aNumber = new BigInteger(a).floatValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a positive BigInteger to an integer value.
     * The low digit is positive
     */
    public void testIntValuePositive1() {
        byte[] aBytes = new byte[]{ 12, 56, 100, -2, -76, 89, 45, 91, 3 };
        int resInt = 1496144643;
        int aNumber = new BigInteger(aBytes).intValue();
        TestCase.assertTrue((aNumber == resInt));
    }

    /**
     * Convert a positive BigInteger to an integer value.
     * The low digit is positive
     */
    public void testIntValuePositive2() {
        byte[] aBytes = new byte[]{ 12, 56, 100 };
        int resInt = 800868;
        int aNumber = new BigInteger(aBytes).intValue();
        TestCase.assertTrue((aNumber == resInt));
    }

    /**
     * Convert a positive BigInteger to an integer value.
     * The low digit is negative.
     */
    public void testIntValuePositive3() {
        byte[] aBytes = new byte[]{ 56, 13, 78, -12, -5, 56, 100 };
        int sign = 1;
        int resInt = -184862620;
        int aNumber = new BigInteger(sign, aBytes).intValue();
        TestCase.assertTrue((aNumber == resInt));
    }

    /**
     * Convert a negative BigInteger to an integer value.
     * The low digit is negative.
     */
    public void testIntValueNegative1() {
        byte[] aBytes = new byte[]{ 12, 56, 100, -2, -76, -128, 45, 91, 3 };
        int sign = -1;
        int resInt = 2144511229;
        int aNumber = new BigInteger(sign, aBytes).intValue();
        TestCase.assertTrue((aNumber == resInt));
    }

    /**
     * Convert a negative BigInteger to an integer value.
     * The low digit is negative.
     */
    public void testIntValueNegative2() {
        byte[] aBytes = new byte[]{ -12, 56, 100 };
        int result = -771996;
        int aNumber = new BigInteger(aBytes).intValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a negative BigInteger to an integer value.
     * The low digit is positive.
     */
    public void testIntValueNegative3() {
        byte[] aBytes = new byte[]{ 12, 56, 100, -2, -76, 127, 45, 91, 3 };
        int sign = -1;
        int resInt = -2133678851;
        int aNumber = new BigInteger(sign, aBytes).intValue();
        TestCase.assertTrue((aNumber == resInt));
    }

    /**
     * Convert a BigInteger to a positive long value
     * The BigInteger is longer than int.
     */
    public void testLongValuePositive1() {
        byte[] aBytes = new byte[]{ 12, 56, 100, -2, -76, 89, 45, 91, 3, 120, -34, -12, 45, 98 };
        long result = 3268209772258930018L;
        long aNumber = new BigInteger(aBytes).longValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a number to a positive long value
     * The number fits in a long.
     */
    public void testLongValuePositive2() {
        byte[] aBytes = new byte[]{ 12, 56, 100, 18, -105, 34, -18, 45 };
        long result = 880563758158769709L;
        long aNumber = new BigInteger(aBytes).longValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a number to a negative long value
     * The BigInteger is longer than int.
     */
    public void testLongValueNegative1() {
        byte[] aBytes = new byte[]{ 12, -1, 100, -2, -76, -128, 45, 91, 3 };
        long result = -43630045168837885L;
        long aNumber = new BigInteger(aBytes).longValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * Convert a number to a negative long value
     * The number fits in a long.
     */
    public void testLongValueNegative2() {
        byte[] aBytes = new byte[]{ -12, 56, 100, 45, -101, 45, 98 };
        long result = -3315696807498398L;
        long aNumber = new BigInteger(aBytes).longValue();
        TestCase.assertTrue((aNumber == result));
    }

    /**
     * valueOf (long val): convert Integer.MAX_VALUE to a BigInteger.
     */
    public void testValueOfIntegerMax() {
        long longVal = Integer.MAX_VALUE;
        BigInteger aNumber = BigInteger.valueOf(longVal);
        byte[] rBytes = new byte[]{ 127, -1, -1, -1 };
        byte[] resBytes = new byte[rBytes.length];
        resBytes = aNumber.toByteArray();
        for (int i = 0; i < (resBytes.length); i++) {
            TestCase.assertTrue(((resBytes[i]) == (rBytes[i])));
        }
        TestCase.assertEquals("incorrect sign", 1, aNumber.signum());
    }

    /**
     * valueOf (long val): convert Integer.MIN_VALUE to a BigInteger.
     */
    public void testValueOfIntegerMin() {
        long longVal = Integer.MIN_VALUE;
        BigInteger aNumber = BigInteger.valueOf(longVal);
        byte[] rBytes = new byte[]{ -128, 0, 0, 0 };
        byte[] resBytes = new byte[rBytes.length];
        resBytes = aNumber.toByteArray();
        for (int i = 0; i < (resBytes.length); i++) {
            TestCase.assertTrue(((resBytes[i]) == (rBytes[i])));
        }
        TestCase.assertEquals("incorrect sign", (-1), aNumber.signum());
    }

    /**
     * valueOf (long val): convert Long.MAX_VALUE to a BigInteger.
     */
    public void testValueOfLongMax() {
        long longVal = Long.MAX_VALUE;
        BigInteger aNumber = BigInteger.valueOf(longVal);
        byte[] rBytes = new byte[]{ 127, -1, -1, -1, -1, -1, -1, -1 };
        byte[] resBytes = new byte[rBytes.length];
        resBytes = aNumber.toByteArray();
        for (int i = 0; i < (resBytes.length); i++) {
            TestCase.assertTrue(((resBytes[i]) == (rBytes[i])));
        }
        TestCase.assertEquals("incorrect sign", 1, aNumber.signum());
    }

    /**
     * valueOf (long val): convert Long.MIN_VALUE to a BigInteger.
     */
    public void testValueOfLongMin() {
        long longVal = Long.MIN_VALUE;
        BigInteger aNumber = BigInteger.valueOf(longVal);
        byte[] rBytes = new byte[]{ -128, 0, 0, 0, 0, 0, 0, 0 };
        byte[] resBytes = new byte[rBytes.length];
        resBytes = aNumber.toByteArray();
        for (int i = 0; i < (resBytes.length); i++) {
            TestCase.assertTrue(((resBytes[i]) == (rBytes[i])));
        }
        TestCase.assertEquals("incorrect sign", (-1), aNumber.signum());
    }

    /**
     * valueOf (long val): convert a positive long value to a BigInteger.
     */
    public void testValueOfLongPositive1() {
        long longVal = 268209772258930018L;
        BigInteger aNumber = BigInteger.valueOf(longVal);
        byte[] rBytes = new byte[]{ 3, -72, -33, 93, -24, -56, 45, 98 };
        byte[] resBytes = new byte[rBytes.length];
        resBytes = aNumber.toByteArray();
        for (int i = 0; i < (resBytes.length); i++) {
            TestCase.assertTrue(((resBytes[i]) == (rBytes[i])));
        }
        TestCase.assertEquals("incorrect sign", 1, aNumber.signum());
    }

    /**
     * valueOf (long val): convert a positive long value to a BigInteger.
     * The long value fits in integer.
     */
    public void testValueOfLongPositive2() {
        long longVal = 58930018L;
        BigInteger aNumber = BigInteger.valueOf(longVal);
        byte[] rBytes = new byte[]{ 3, -125, 51, 98 };
        byte[] resBytes = new byte[rBytes.length];
        resBytes = aNumber.toByteArray();
        for (int i = 0; i < (resBytes.length); i++) {
            TestCase.assertTrue(((resBytes[i]) == (rBytes[i])));
        }
        TestCase.assertEquals("incorrect sign", 1, aNumber.signum());
    }

    /**
     * valueOf (long val): convert a negative long value to a BigInteger.
     */
    public void testValueOfLongNegative1() {
        long longVal = -268209772258930018L;
        BigInteger aNumber = BigInteger.valueOf(longVal);
        byte[] rBytes = new byte[]{ -4, 71, 32, -94, 23, 55, -46, -98 };
        byte[] resBytes = new byte[rBytes.length];
        resBytes = aNumber.toByteArray();
        for (int i = 0; i < (resBytes.length); i++) {
            TestCase.assertTrue(((resBytes[i]) == (rBytes[i])));
        }
        TestCase.assertEquals("incorrect sign", (-1), aNumber.signum());
    }

    /**
     * valueOf (long val): convert a negative long value to a BigInteger.
     * The long value fits in integer.
     */
    public void testValueOfLongNegative2() {
        long longVal = -58930018L;
        BigInteger aNumber = BigInteger.valueOf(longVal);
        byte[] rBytes = new byte[]{ -4, 124, -52, -98 };
        byte[] resBytes = new byte[rBytes.length];
        resBytes = aNumber.toByteArray();
        for (int i = 0; i < (resBytes.length); i++) {
            TestCase.assertTrue(((resBytes[i]) == (rBytes[i])));
        }
        TestCase.assertEquals("incorrect sign", (-1), aNumber.signum());
    }

    /**
     * valueOf (long val): convert a zero long value to a BigInteger.
     */
    public void testValueOfLongZero() {
        long longVal = 0L;
        BigInteger aNumber = BigInteger.valueOf(longVal);
        byte[] rBytes = new byte[]{ 0 };
        byte[] resBytes = new byte[rBytes.length];
        resBytes = aNumber.toByteArray();
        for (int i = 0; i < (resBytes.length); i++) {
            TestCase.assertTrue(((resBytes[i]) == (rBytes[i])));
        }
        TestCase.assertEquals("incorrect sign", 0, aNumber.signum());
    }
}

