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


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.math.NumberUtils}.
 */
public class NumberUtilsTest {
    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new NumberUtils());
        final Constructor<?>[] cons = NumberUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(NumberUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(NumberUtils.class.getModifiers()));
    }

    // ---------------------------------------------------------------------
    /**
     * Test for {@link NumberUtils#toInt(String)}.
     */
    @Test
    public void testToIntString() {
        Assertions.assertEquals(12345, NumberUtils.toInt("12345"), "toInt(String) 1 failed");
        Assertions.assertEquals(0, NumberUtils.toInt("abc"), "toInt(String) 2 failed");
        Assertions.assertEquals(0, NumberUtils.toInt(""), "toInt(empty) failed");
        Assertions.assertEquals(0, NumberUtils.toInt(null), "toInt(null) failed");
    }

    /**
     * Test for {@link NumberUtils#toInt(String, int)}.
     */
    @Test
    public void testToIntStringI() {
        Assertions.assertEquals(12345, NumberUtils.toInt("12345", 5), "toInt(String, int) 1 failed");
        Assertions.assertEquals(5, NumberUtils.toInt("1234.5", 5), "toInt(String, int) 2 failed");
    }

    /**
     * Test for {@link NumberUtils#toLong(String)}.
     */
    @Test
    public void testToLongString() {
        Assertions.assertEquals(12345L, NumberUtils.toLong("12345"), "toLong(String) 1 failed");
        Assertions.assertEquals(0L, NumberUtils.toLong("abc"), "toLong(String) 2 failed");
        Assertions.assertEquals(0L, NumberUtils.toLong("1L"), "toLong(String) 3 failed");
        Assertions.assertEquals(0L, NumberUtils.toLong("1l"), "toLong(String) 4 failed");
        Assertions.assertEquals(NumberUtils.toLong(((Long.MAX_VALUE) + "")), Long.MAX_VALUE, "toLong(Long.MAX_VALUE) failed");
        Assertions.assertEquals(NumberUtils.toLong(((Long.MIN_VALUE) + "")), Long.MIN_VALUE, "toLong(Long.MIN_VALUE) failed");
        Assertions.assertEquals(0L, NumberUtils.toLong(""), "toLong(empty) failed");
        Assertions.assertEquals(0L, NumberUtils.toLong(null), "toLong(null) failed");
    }

    /**
     * Test for {@link NumberUtils#toLong(String, long)}.
     */
    @Test
    public void testToLongStringL() {
        Assertions.assertEquals(12345L, NumberUtils.toLong("12345", 5L), "toLong(String, long) 1 failed");
        Assertions.assertEquals(5L, NumberUtils.toLong("1234.5", 5L), "toLong(String, long) 2 failed");
    }

    /**
     * Test for {@link NumberUtils#toFloat(String)}.
     */
    @Test
    public void testToFloatString() {
        Assertions.assertEquals(NumberUtils.toFloat("-1.2345"), (-1.2345F), "toFloat(String) 1 failed");
        Assertions.assertEquals(1.2345F, NumberUtils.toFloat("1.2345"), "toFloat(String) 2 failed");
        Assertions.assertEquals(0.0F, NumberUtils.toFloat("abc"), "toFloat(String) 3 failed");
        // LANG-1060
        Assertions.assertEquals(NumberUtils.toFloat("-001.2345"), (-1.2345F), "toFloat(String) 4 failed");
        Assertions.assertEquals(1.2345F, NumberUtils.toFloat("+001.2345"), "toFloat(String) 5 failed");
        Assertions.assertEquals(1.2345F, NumberUtils.toFloat("001.2345"), "toFloat(String) 6 failed");
        Assertions.assertEquals(0.0F, NumberUtils.toFloat("000.00"), "toFloat(String) 7 failed");
        Assertions.assertEquals(NumberUtils.toFloat(((Float.MAX_VALUE) + "")), Float.MAX_VALUE, "toFloat(Float.MAX_VALUE) failed");
        Assertions.assertEquals(NumberUtils.toFloat(((Float.MIN_VALUE) + "")), Float.MIN_VALUE, "toFloat(Float.MIN_VALUE) failed");
        Assertions.assertEquals(0.0F, NumberUtils.toFloat(""), "toFloat(empty) failed");
        Assertions.assertEquals(0.0F, NumberUtils.toFloat(null), "toFloat(null) failed");
    }

    /**
     * Test for {@link NumberUtils#toFloat(String, float)}.
     */
    @Test
    public void testToFloatStringF() {
        Assertions.assertEquals(1.2345F, NumberUtils.toFloat("1.2345", 5.1F), "toFloat(String, int) 1 failed");
        Assertions.assertEquals(5.0F, NumberUtils.toFloat("a", 5.0F), "toFloat(String, int) 2 failed");
        // LANG-1060
        Assertions.assertEquals(5.0F, NumberUtils.toFloat("-001Z.2345", 5.0F), "toFloat(String, int) 3 failed");
        Assertions.assertEquals(5.0F, NumberUtils.toFloat("+001AB.2345", 5.0F), "toFloat(String, int) 4 failed");
        Assertions.assertEquals(5.0F, NumberUtils.toFloat("001Z.2345", 5.0F), "toFloat(String, int) 5 failed");
    }

    /**
     * Test for {(@link NumberUtils#createNumber(String)}
     */
    @Test
    public void testStringCreateNumberEnsureNoPrecisionLoss() {
        final String shouldBeFloat = "1.23";
        final String shouldBeDouble = "3.40282354e+38";
        final String shouldBeBigDecimal = "1.797693134862315759e+308";
        Assertions.assertTrue(((NumberUtils.createNumber(shouldBeFloat)) instanceof Float));
        Assertions.assertTrue(((NumberUtils.createNumber(shouldBeDouble)) instanceof Double));
        Assertions.assertTrue(((NumberUtils.createNumber(shouldBeBigDecimal)) instanceof BigDecimal));
        // LANG-1060
        Assertions.assertTrue(((NumberUtils.createNumber("001.12")) instanceof Float));
        Assertions.assertTrue(((NumberUtils.createNumber("-001.12")) instanceof Float));
        Assertions.assertTrue(((NumberUtils.createNumber("+001.12")) instanceof Float));
        Assertions.assertTrue(((NumberUtils.createNumber("003.40282354e+38")) instanceof Double));
        Assertions.assertTrue(((NumberUtils.createNumber("-003.40282354e+38")) instanceof Double));
        Assertions.assertTrue(((NumberUtils.createNumber("+003.40282354e+38")) instanceof Double));
        Assertions.assertTrue(((NumberUtils.createNumber("0001.797693134862315759e+308")) instanceof BigDecimal));
        Assertions.assertTrue(((NumberUtils.createNumber("-001.797693134862315759e+308")) instanceof BigDecimal));
        Assertions.assertTrue(((NumberUtils.createNumber("+001.797693134862315759e+308")) instanceof BigDecimal));
    }

    /**
     * Test for {@link NumberUtils#toDouble(String)}.
     */
    @Test
    public void testStringToDoubleString() {
        Assertions.assertEquals(NumberUtils.toDouble("-1.2345"), (-1.2345), "toDouble(String) 1 failed");
        Assertions.assertEquals(1.2345, NumberUtils.toDouble("1.2345"), "toDouble(String) 2 failed");
        Assertions.assertEquals(0.0, NumberUtils.toDouble("abc"), "toDouble(String) 3 failed");
        // LANG-1060
        Assertions.assertEquals(NumberUtils.toDouble("-001.2345"), (-1.2345), "toDouble(String) 4 failed");
        Assertions.assertEquals(1.2345, NumberUtils.toDouble("+001.2345"), "toDouble(String) 5 failed");
        Assertions.assertEquals(1.2345, NumberUtils.toDouble("001.2345"), "toDouble(String) 6 failed");
        Assertions.assertEquals(0.0, NumberUtils.toDouble("000.00000"), "toDouble(String) 7 failed");
        Assertions.assertEquals(NumberUtils.toDouble(((Double.MAX_VALUE) + "")), Double.MAX_VALUE, "toDouble(Double.MAX_VALUE) failed");
        Assertions.assertEquals(NumberUtils.toDouble(((Double.MIN_VALUE) + "")), Double.MIN_VALUE, "toDouble(Double.MIN_VALUE) failed");
        Assertions.assertEquals(0.0, NumberUtils.toDouble(""), "toDouble(empty) failed");
        Assertions.assertEquals(0.0, NumberUtils.toDouble(((String) (null))), "toDouble(null) failed");
    }

    /**
     * Test for {@link NumberUtils#toDouble(String, double)}.
     */
    @Test
    public void testStringToDoubleStringD() {
        Assertions.assertEquals(1.2345, NumberUtils.toDouble("1.2345", 5.1), "toDouble(String, int) 1 failed");
        Assertions.assertEquals(5.0, NumberUtils.toDouble("a", 5.0), "toDouble(String, int) 2 failed");
        // LANG-1060
        Assertions.assertEquals(1.2345, NumberUtils.toDouble("001.2345", 5.1), "toDouble(String, int) 3 failed");
        Assertions.assertEquals(NumberUtils.toDouble("-001.2345", 5.1), (-1.2345), "toDouble(String, int) 4 failed");
        Assertions.assertEquals(1.2345, NumberUtils.toDouble("+001.2345", 5.1), "toDouble(String, int) 5 failed");
        Assertions.assertEquals(0.0, NumberUtils.toDouble("000.00", 5.1), "toDouble(String, int) 7 failed");
    }

    /**
     * Test for {@link NumberUtils#toDouble(BigDecimal)}
     */
    @Test
    public void testBigIntegerToDoubleBigInteger() {
        Assertions.assertEquals(0.0, NumberUtils.toDouble(((BigDecimal) (null))), "toDouble(BigInteger) 1 failed");
        Assertions.assertEquals(8.5, NumberUtils.toDouble(BigDecimal.valueOf(8.5)), "toDouble(BigInteger) 2 failed");
    }

    /**
     * Test for {@link NumberUtils#toDouble(BigDecimal, double)}
     */
    @Test
    public void testBigIntegerToDoubleBigIntegerD() {
        Assertions.assertEquals(1.1, NumberUtils.toDouble(((BigDecimal) (null)), 1.1), "toDouble(BigInteger) 1 failed");
        Assertions.assertEquals(8.5, NumberUtils.toDouble(BigDecimal.valueOf(8.5), 1.1), "toDouble(BigInteger) 2 failed");
    }

    /**
     * Test for {@link NumberUtils#toByte(String)}.
     */
    @Test
    public void testToByteString() {
        Assertions.assertEquals(123, NumberUtils.toByte("123"), "toByte(String) 1 failed");
        Assertions.assertEquals(0, NumberUtils.toByte("abc"), "toByte(String) 2 failed");
        Assertions.assertEquals(0, NumberUtils.toByte(""), "toByte(empty) failed");
        Assertions.assertEquals(0, NumberUtils.toByte(null), "toByte(null) failed");
    }

    /**
     * Test for {@link NumberUtils#toByte(String, byte)}.
     */
    @Test
    public void testToByteStringI() {
        Assertions.assertEquals(123, NumberUtils.toByte("123", ((byte) (5))), "toByte(String, byte) 1 failed");
        Assertions.assertEquals(5, NumberUtils.toByte("12.3", ((byte) (5))), "toByte(String, byte) 2 failed");
    }

    /**
     * Test for {@link NumberUtils#toShort(String)}.
     */
    @Test
    public void testToShortString() {
        Assertions.assertEquals(12345, NumberUtils.toShort("12345"), "toShort(String) 1 failed");
        Assertions.assertEquals(0, NumberUtils.toShort("abc"), "toShort(String) 2 failed");
        Assertions.assertEquals(0, NumberUtils.toShort(""), "toShort(empty) failed");
        Assertions.assertEquals(0, NumberUtils.toShort(null), "toShort(null) failed");
    }

    /**
     * Test for {@link NumberUtils#toShort(String, short)}.
     */
    @Test
    public void testToShortStringI() {
        Assertions.assertEquals(12345, NumberUtils.toShort("12345", ((short) (5))), "toShort(String, short) 1 failed");
        Assertions.assertEquals(5, NumberUtils.toShort("1234.5", ((short) (5))), "toShort(String, short) 2 failed");
    }

    /**
     * Test for {@link NumberUtils#toScaledBigDecimal(BigDecimal)}.
     */
    @Test
    public void testToScaledBigDecimalBigDecimal() {
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(BigDecimal.valueOf(123.456)), BigDecimal.valueOf(123.46), "toScaledBigDecimal(BigDecimal) 1 failed");
        // Test RoudingMode.HALF_EVEN default rounding.
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(BigDecimal.valueOf(23.515)), BigDecimal.valueOf(23.52), "toScaledBigDecimal(BigDecimal) 2 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(BigDecimal.valueOf(23.525)), BigDecimal.valueOf(23.52), "toScaledBigDecimal(BigDecimal) 3 failed");
        Assertions.assertEquals("2352.00", NumberUtils.toScaledBigDecimal(BigDecimal.valueOf(23.525)).multiply(BigDecimal.valueOf(100)).toString(), "toScaledBigDecimal(BigDecimal) 4 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(((BigDecimal) (null))), BigDecimal.ZERO, "toScaledBigDecimal(BigDecimal) 5 failed");
    }

    /**
     * Test for {@link NumberUtils#toScaledBigDecimal(BigDecimal, int, RoundingMode)}.
     */
    @Test
    public void testToScaledBigDecimalBigDecimalIRM() {
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(BigDecimal.valueOf(123.456), 1, RoundingMode.CEILING), BigDecimal.valueOf(123.5), "toScaledBigDecimal(BigDecimal, int, RoudingMode) 1 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(BigDecimal.valueOf(23.5159), 3, RoundingMode.FLOOR), BigDecimal.valueOf(23.515), "toScaledBigDecimal(BigDecimal, int, RoudingMode) 2 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(BigDecimal.valueOf(23.525), 2, RoundingMode.HALF_UP), BigDecimal.valueOf(23.53), "toScaledBigDecimal(BigDecimal, int, RoudingMode) 3 failed");
        Assertions.assertEquals("23521.0000", NumberUtils.toScaledBigDecimal(BigDecimal.valueOf(23.521), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(1000)).toString(), "toScaledBigDecimal(BigDecimal, int, RoudingMode) 4 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(((BigDecimal) (null)), 2, RoundingMode.HALF_UP), BigDecimal.ZERO, "toScaledBigDecimal(BigDecimal, int, RoudingMode) 5 failed");
    }

    /**
     * Test for {@link NumberUtils#toScaledBigDecimal(Float)}.
     */
    @Test
    public void testToScaledBigDecimalFloat() {
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Float.valueOf(123.456F)), BigDecimal.valueOf(123.46), "toScaledBigDecimal(Float) 1 failed");
        // Test RoudingMode.HALF_EVEN default rounding.
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Float.valueOf(23.515F)), BigDecimal.valueOf(23.51), "toScaledBigDecimal(Float) 2 failed");
        // Note. NumberUtils.toScaledBigDecimal(Float.valueOf(23.515f)).equals(BigDecimal.valueOf(23.51))
        // because of roundoff error. It is ok.
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Float.valueOf(23.525F)), BigDecimal.valueOf(23.52), "toScaledBigDecimal(Float) 3 failed");
        Assertions.assertEquals("2352.00", NumberUtils.toScaledBigDecimal(Float.valueOf(23.525F)).multiply(BigDecimal.valueOf(100)).toString(), "toScaledBigDecimal(Float) 4 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(((Float) (null))), BigDecimal.ZERO, "toScaledBigDecimal(Float) 5 failed");
    }

    /**
     * Test for {@link NumberUtils#toScaledBigDecimal(Float, int, RoundingMode)}.
     */
    @Test
    public void testToScaledBigDecimalFloatIRM() {
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Float.valueOf(123.456F), 1, RoundingMode.CEILING), BigDecimal.valueOf(123.5), "toScaledBigDecimal(Float, int, RoudingMode) 1 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Float.valueOf(23.5159F), 3, RoundingMode.FLOOR), BigDecimal.valueOf(23.515), "toScaledBigDecimal(Float, int, RoudingMode) 2 failed");
        // The following happens due to roundoff error. We're ok with this.
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Float.valueOf(23.525F), 2, RoundingMode.HALF_UP), BigDecimal.valueOf(23.52), "toScaledBigDecimal(Float, int, RoudingMode) 3 failed");
        Assertions.assertEquals("23521.0000", NumberUtils.toScaledBigDecimal(Float.valueOf(23.521F), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(1000)).toString(), "toScaledBigDecimal(Float, int, RoudingMode) 4 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(((Float) (null)), 2, RoundingMode.HALF_UP), BigDecimal.ZERO, "toScaledBigDecimal(Float, int, RoudingMode) 5 failed");
    }

    /**
     * Test for {@link NumberUtils#toScaledBigDecimal(Double)}.
     */
    @Test
    public void testToScaledBigDecimalDouble() {
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Double.valueOf(123.456)), BigDecimal.valueOf(123.46), "toScaledBigDecimal(Double) 1 failed");
        // Test RoudingMode.HALF_EVEN default rounding.
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Double.valueOf(23.515)), BigDecimal.valueOf(23.52), "toScaledBigDecimal(Double) 2 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Double.valueOf(23.525)), BigDecimal.valueOf(23.52), "toScaledBigDecimal(Double) 3 failed");
        Assertions.assertEquals("2352.00", NumberUtils.toScaledBigDecimal(Double.valueOf(23.525)).multiply(BigDecimal.valueOf(100)).toString(), "toScaledBigDecimal(Double) 4 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(((Double) (null))), BigDecimal.ZERO, "toScaledBigDecimal(Double) 5 failed");
    }

    /**
     * Test for {@link NumberUtils#toScaledBigDecimal(Double, int, RoundingMode)}.
     */
    @Test
    public void testToScaledBigDecimalDoubleIRM() {
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Double.valueOf(123.456), 1, RoundingMode.CEILING), BigDecimal.valueOf(123.5), "toScaledBigDecimal(Double, int, RoudingMode) 1 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Double.valueOf(23.5159), 3, RoundingMode.FLOOR), BigDecimal.valueOf(23.515), "toScaledBigDecimal(Double, int, RoudingMode) 2 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(Double.valueOf(23.525), 2, RoundingMode.HALF_UP), BigDecimal.valueOf(23.53), "toScaledBigDecimal(Double, int, RoudingMode) 3 failed");
        Assertions.assertEquals("23521.0000", NumberUtils.toScaledBigDecimal(Double.valueOf(23.521), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(1000)).toString(), "toScaledBigDecimal(Double, int, RoudingMode) 4 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(((Double) (null)), 2, RoundingMode.HALF_UP), BigDecimal.ZERO, "toScaledBigDecimal(Double, int, RoudingMode) 5 failed");
    }

    /**
     * Test for {@link NumberUtils#toScaledBigDecimal(Double)}.
     */
    @Test
    public void testToScaledBigDecimalString() {
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal("123.456"), BigDecimal.valueOf(123.46), "toScaledBigDecimal(String) 1 failed");
        // Test RoudingMode.HALF_EVEN default rounding.
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal("23.515"), BigDecimal.valueOf(23.52), "toScaledBigDecimal(String) 2 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal("23.525"), BigDecimal.valueOf(23.52), "toScaledBigDecimal(String) 3 failed");
        Assertions.assertEquals("2352.00", NumberUtils.toScaledBigDecimal("23.525").multiply(BigDecimal.valueOf(100)).toString(), "toScaledBigDecimal(String) 4 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(((String) (null))), BigDecimal.ZERO, "toScaledBigDecimal(String) 5 failed");
    }

    /**
     * Test for {@link NumberUtils#toScaledBigDecimal(Double, int, RoundingMode)}.
     */
    @Test
    public void testToScaledBigDecimalStringIRM() {
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal("123.456", 1, RoundingMode.CEILING), BigDecimal.valueOf(123.5), "toScaledBigDecimal(String, int, RoudingMode) 1 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal("23.5159", 3, RoundingMode.FLOOR), BigDecimal.valueOf(23.515), "toScaledBigDecimal(String, int, RoudingMode) 2 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal("23.525", 2, RoundingMode.HALF_UP), BigDecimal.valueOf(23.53), "toScaledBigDecimal(String, int, RoudingMode) 3 failed");
        Assertions.assertEquals("23521.0000", NumberUtils.toScaledBigDecimal("23.521", 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(1000)).toString(), "toScaledBigDecimal(String, int, RoudingMode) 4 failed");
        Assertions.assertEquals(NumberUtils.toScaledBigDecimal(((String) (null)), 2, RoundingMode.HALF_UP), BigDecimal.ZERO, "toScaledBigDecimal(String, int, RoudingMode) 5 failed");
    }

    @Test
    public void testCreateNumber() {
        // a lot of things can go wrong
        Assertions.assertEquals(Float.valueOf("1234.5"), NumberUtils.createNumber("1234.5"), "createNumber(String) 1 failed");
        Assertions.assertEquals(Integer.valueOf("12345"), NumberUtils.createNumber("12345"), "createNumber(String) 2 failed");
        Assertions.assertEquals(Double.valueOf("1234.5"), NumberUtils.createNumber("1234.5D"), "createNumber(String) 3 failed");
        Assertions.assertEquals(Double.valueOf("1234.5"), NumberUtils.createNumber("1234.5d"), "createNumber(String) 3 failed");
        Assertions.assertEquals(Float.valueOf("1234.5"), NumberUtils.createNumber("1234.5F"), "createNumber(String) 4 failed");
        Assertions.assertEquals(Float.valueOf("1234.5"), NumberUtils.createNumber("1234.5f"), "createNumber(String) 4 failed");
        Assertions.assertEquals(Long.valueOf(((Integer.MAX_VALUE) + 1L)), NumberUtils.createNumber(("" + ((Integer.MAX_VALUE) + 1L))), "createNumber(String) 5 failed");
        Assertions.assertEquals(Long.valueOf(12345), NumberUtils.createNumber("12345L"), "createNumber(String) 6 failed");
        Assertions.assertEquals(Long.valueOf(12345), NumberUtils.createNumber("12345l"), "createNumber(String) 6 failed");
        Assertions.assertEquals(Float.valueOf("-1234.5"), NumberUtils.createNumber("-1234.5"), "createNumber(String) 7 failed");
        Assertions.assertEquals(Integer.valueOf("-12345"), NumberUtils.createNumber("-12345"), "createNumber(String) 8 failed");
        Assertions.assertEquals(64222, NumberUtils.createNumber("0xFADE").intValue(), "createNumber(String) 9a failed");
        Assertions.assertEquals(64222, NumberUtils.createNumber("0Xfade").intValue(), "createNumber(String) 9b failed");
        Assertions.assertEquals((-64222), NumberUtils.createNumber("-0xFADE").intValue(), "createNumber(String) 10a failed");
        Assertions.assertEquals((-64222), NumberUtils.createNumber("-0Xfade").intValue(), "createNumber(String) 10b failed");
        Assertions.assertEquals(Double.valueOf("1.1E200"), NumberUtils.createNumber("1.1E200"), "createNumber(String) 11 failed");
        Assertions.assertEquals(Float.valueOf("1.1E20"), NumberUtils.createNumber("1.1E20"), "createNumber(String) 12 failed");
        Assertions.assertEquals(Double.valueOf("-1.1E200"), NumberUtils.createNumber("-1.1E200"), "createNumber(String) 13 failed");
        Assertions.assertEquals(Double.valueOf("1.1E-200"), NumberUtils.createNumber("1.1E-200"), "createNumber(String) 14 failed");
        Assertions.assertNull(NumberUtils.createNumber(null), "createNumber(null) failed");
        Assertions.assertEquals(new BigInteger("12345678901234567890"), NumberUtils.createNumber("12345678901234567890L"), "createNumber(String) failed");
        Assertions.assertEquals(new BigDecimal("1.1E-700"), NumberUtils.createNumber("1.1E-700F"), "createNumber(String) 15 failed");
        Assertions.assertEquals(Long.valueOf(("10" + (Integer.MAX_VALUE))), NumberUtils.createNumber((("10" + (Integer.MAX_VALUE)) + "L")), "createNumber(String) 16 failed");
        Assertions.assertEquals(Long.valueOf(("10" + (Integer.MAX_VALUE))), NumberUtils.createNumber(("10" + (Integer.MAX_VALUE))), "createNumber(String) 17 failed");
        Assertions.assertEquals(new BigInteger(("10" + (Long.MAX_VALUE))), NumberUtils.createNumber(("10" + (Long.MAX_VALUE))), "createNumber(String) 18 failed");
        // LANG-521
        Assertions.assertEquals(Float.valueOf("2."), NumberUtils.createNumber("2."), "createNumber(String) LANG-521 failed");
        // LANG-638
        Assertions.assertFalse(checkCreateNumber("1eE"), "createNumber(String) succeeded");
        // LANG-693
        Assertions.assertEquals(Double.valueOf(Double.MAX_VALUE), NumberUtils.createNumber(("" + (Double.MAX_VALUE))), "createNumber(String) LANG-693 failed");
        // LANG-822
        // ensure that the underlying negative number would create a BigDecimal
        final Number bigNum = NumberUtils.createNumber("-1.1E-700F");
        Assertions.assertNotNull(bigNum);
        Assertions.assertEquals(BigDecimal.class, bigNum.getClass());
        // LANG-1018
        Assertions.assertEquals(Double.valueOf("-160952.54"), NumberUtils.createNumber("-160952.54"), "createNumber(String) LANG-1018 failed");
        // LANG-1187
        Assertions.assertEquals(Double.valueOf("6264583.33"), NumberUtils.createNumber("6264583.33"), "createNumber(String) LANG-1187 failed");
        // LANG-1215
        Assertions.assertEquals(Double.valueOf("193343.82"), NumberUtils.createNumber("193343.82"), "createNumber(String) LANG-1215 failed");
        // LANG-1060
        Assertions.assertEquals(Double.valueOf("001234.5678"), NumberUtils.createNumber("001234.5678"), "createNumber(String) LANG-1060a failed");
        Assertions.assertEquals(Double.valueOf("+001234.5678"), NumberUtils.createNumber("+001234.5678"), "createNumber(String) LANG-1060b failed");
        Assertions.assertEquals(Double.valueOf("-001234.5678"), NumberUtils.createNumber("-001234.5678"), "createNumber(String) LANG-1060c failed");
        Assertions.assertEquals(Double.valueOf("0000.00000"), NumberUtils.createNumber("0000.00000d"), "createNumber(String) LANG-1060d failed");
        Assertions.assertEquals(Float.valueOf("001234.56"), NumberUtils.createNumber("001234.56"), "createNumber(String) LANG-1060e failed");
        Assertions.assertEquals(Float.valueOf("+001234.56"), NumberUtils.createNumber("+001234.56"), "createNumber(String) LANG-1060f failed");
        Assertions.assertEquals(Float.valueOf("-001234.56"), NumberUtils.createNumber("-001234.56"), "createNumber(String) LANG-1060g failed");
        Assertions.assertEquals(Float.valueOf("0000.10"), NumberUtils.createNumber("0000.10"), "createNumber(String) LANG-1060h failed");
        Assertions.assertEquals(Float.valueOf("001.1E20"), NumberUtils.createNumber("001.1E20"), "createNumber(String) LANG-1060i failed");
        Assertions.assertEquals(Float.valueOf("+001.1E20"), NumberUtils.createNumber("+001.1E20"), "createNumber(String) LANG-1060j failed");
        Assertions.assertEquals(Float.valueOf("-001.1E20"), NumberUtils.createNumber("-001.1E20"), "createNumber(String) LANG-1060k failed");
        Assertions.assertEquals(Double.valueOf("001.1E200"), NumberUtils.createNumber("001.1E200"), "createNumber(String) LANG-1060l failed");
        Assertions.assertEquals(Double.valueOf("+001.1E200"), NumberUtils.createNumber("+001.1E200"), "createNumber(String) LANG-1060m failed");
        Assertions.assertEquals(Double.valueOf("-001.1E200"), NumberUtils.createNumber("-001.1E200"), "createNumber(String) LANG-1060n failed");
    }

    @Test
    public void testLang1087() {
        // no sign cases
        Assertions.assertEquals(Float.class, NumberUtils.createNumber("0.0").getClass());
        Assertions.assertEquals(Float.valueOf("0.0"), NumberUtils.createNumber("0.0"));
        // explicit positive sign cases
        Assertions.assertEquals(Float.class, NumberUtils.createNumber("+0.0").getClass());
        Assertions.assertEquals(Float.valueOf("+0.0"), NumberUtils.createNumber("+0.0"));
        // negative sign cases
        Assertions.assertEquals(Float.class, NumberUtils.createNumber("-0.0").getClass());
        Assertions.assertEquals(Float.valueOf("-0.0"), NumberUtils.createNumber("-0.0"));
    }

    @Test
    public void TestLang747() {
        Assertions.assertEquals(Integer.valueOf(32768), NumberUtils.createNumber("0x8000"));
        Assertions.assertEquals(Integer.valueOf(524288), NumberUtils.createNumber("0x80000"));
        Assertions.assertEquals(Integer.valueOf(8388608), NumberUtils.createNumber("0x800000"));
        Assertions.assertEquals(Integer.valueOf(134217728), NumberUtils.createNumber("0x8000000"));
        Assertions.assertEquals(Integer.valueOf(2147483647), NumberUtils.createNumber("0x7FFFFFFF"));
        Assertions.assertEquals(Long.valueOf(2147483648L), NumberUtils.createNumber("0x80000000"));
        Assertions.assertEquals(Long.valueOf(4294967295L), NumberUtils.createNumber("0xFFFFFFFF"));
        // Leading zero tests
        Assertions.assertEquals(Integer.valueOf(134217728), NumberUtils.createNumber("0x08000000"));
        Assertions.assertEquals(Integer.valueOf(2147483647), NumberUtils.createNumber("0x007FFFFFFF"));
        Assertions.assertEquals(Long.valueOf(2147483648L), NumberUtils.createNumber("0x080000000"));
        Assertions.assertEquals(Long.valueOf(4294967295L), NumberUtils.createNumber("0x00FFFFFFFF"));
        Assertions.assertEquals(Long.valueOf(34359738368L), NumberUtils.createNumber("0x800000000"));
        Assertions.assertEquals(Long.valueOf(549755813888L), NumberUtils.createNumber("0x8000000000"));
        Assertions.assertEquals(Long.valueOf(8796093022208L), NumberUtils.createNumber("0x80000000000"));
        Assertions.assertEquals(Long.valueOf(140737488355328L), NumberUtils.createNumber("0x800000000000"));
        Assertions.assertEquals(Long.valueOf(2251799813685248L), NumberUtils.createNumber("0x8000000000000"));
        Assertions.assertEquals(Long.valueOf(36028797018963968L), NumberUtils.createNumber("0x80000000000000"));
        Assertions.assertEquals(Long.valueOf(576460752303423488L), NumberUtils.createNumber("0x800000000000000"));
        Assertions.assertEquals(Long.valueOf(9223372036854775807L), NumberUtils.createNumber("0x7FFFFFFFFFFFFFFF"));
        // N.B. Cannot use a hex constant such as 0x8000000000000000L here as that is interpreted as a negative long
        Assertions.assertEquals(new BigInteger("8000000000000000", 16), NumberUtils.createNumber("0x8000000000000000"));
        Assertions.assertEquals(new BigInteger("FFFFFFFFFFFFFFFF", 16), NumberUtils.createNumber("0xFFFFFFFFFFFFFFFF"));
        // Leading zero tests
        Assertions.assertEquals(Long.valueOf(36028797018963968L), NumberUtils.createNumber("0x00080000000000000"));
        Assertions.assertEquals(Long.valueOf(576460752303423488L), NumberUtils.createNumber("0x0800000000000000"));
        Assertions.assertEquals(Long.valueOf(9223372036854775807L), NumberUtils.createNumber("0x07FFFFFFFFFFFFFFF"));
        // N.B. Cannot use a hex constant such as 0x8000000000000000L here as that is interpreted as a negative long
        Assertions.assertEquals(new BigInteger("8000000000000000", 16), NumberUtils.createNumber("0x00008000000000000000"));
        Assertions.assertEquals(new BigInteger("FFFFFFFFFFFFFFFF", 16), NumberUtils.createNumber("0x0FFFFFFFFFFFFFFFF"));
    }

    // Check that the code fails to create a valid number when preceded by -- rather than -
    @Test
    public void testCreateNumberFailure_1() {
        Assertions.assertThrows(NumberFormatException.class, () -> NumberUtils.createNumber("--1.1E-700F"));
    }

    // Check that the code fails to create a valid number when both e and E are present (with decimal)
    @Test
    public void testCreateNumberFailure_2() {
        Assertions.assertThrows(NumberFormatException.class, () -> NumberUtils.createNumber("-1.1E+0-7e00"));
    }

    // Check that the code fails to create a valid number when both e and E are present (no decimal)
    @Test
    public void testCreateNumberFailure_3() {
        Assertions.assertThrows(NumberFormatException.class, () -> NumberUtils.createNumber("-11E+0-7e00"));
    }

    // Check that the code fails to create a valid number when both e and E are present (no decimal)
    @Test
    public void testCreateNumberFailure_4() {
        Assertions.assertThrows(NumberFormatException.class, () -> NumberUtils.createNumber("1eE+00001"));
    }

    // Check that the code fails to create a valid number when there are multiple trailing 'f' characters (LANG-1205)
    @Test
    public void testCreateNumberFailure_5() {
        Assertions.assertThrows(NumberFormatException.class, () -> NumberUtils.createNumber("1234.5ff"));
    }

    // Check that the code fails to create a valid number when there are multiple trailing 'F' characters (LANG-1205)
    @Test
    public void testCreateNumberFailure_6() {
        Assertions.assertThrows(NumberFormatException.class, () -> NumberUtils.createNumber("1234.5FF"));
    }

    // Check that the code fails to create a valid number when there are multiple trailing 'd' characters (LANG-1205)
    @Test
    public void testCreateNumberFailure_7() {
        Assertions.assertThrows(NumberFormatException.class, () -> NumberUtils.createNumber("1234.5dd"));
    }

    // Check that the code fails to create a valid number when there are multiple trailing 'D' characters (LANG-1205)
    @Test
    public void testCreateNumberFailure_8() {
        Assertions.assertThrows(NumberFormatException.class, () -> NumberUtils.createNumber("1234.5DD"));
    }

    // Tests to show when magnitude causes switch to next Number type
    // Will probably need to be adjusted if code is changed to check precision (LANG-693)
    @Test
    public void testCreateNumberMagnitude() {
        // Test Float.MAX_VALUE, and same with +1 in final digit to check conversion changes to next Number type
        Assertions.assertEquals(Float.valueOf(Float.MAX_VALUE), NumberUtils.createNumber("3.4028235e+38"));
        Assertions.assertEquals(Double.valueOf(3.4028236E38), NumberUtils.createNumber("3.4028236e+38"));
        // Test Double.MAX_VALUE
        Assertions.assertEquals(Double.valueOf(Double.MAX_VALUE), NumberUtils.createNumber("1.7976931348623157e+308"));
        // Test with +2 in final digit (+1 does not cause roll-over to BigDecimal)
        Assertions.assertEquals(new BigDecimal("1.7976931348623159e+308"), NumberUtils.createNumber("1.7976931348623159e+308"));
        Assertions.assertEquals(Integer.valueOf(305419896), NumberUtils.createNumber("0x12345678"));
        Assertions.assertEquals(Long.valueOf(4886718345L), NumberUtils.createNumber("0x123456789"));
        Assertions.assertEquals(Long.valueOf(9223372036854775807L), NumberUtils.createNumber("0x7fffffffffffffff"));
        // Does not appear to be a way to create a literal BigInteger of this magnitude
        Assertions.assertEquals(new BigInteger("7fffffffffffffff0", 16), NumberUtils.createNumber("0x7fffffffffffffff0"));
        Assertions.assertEquals(Long.valueOf(9223372036854775807L), NumberUtils.createNumber("#7fffffffffffffff"));
        Assertions.assertEquals(new BigInteger("7fffffffffffffff0", 16), NumberUtils.createNumber("#7fffffffffffffff0"));
        Assertions.assertEquals(Integer.valueOf(2147483647), NumberUtils.createNumber("017777777777"));// 31 bits

        Assertions.assertEquals(Long.valueOf(4294967295L), NumberUtils.createNumber("037777777777"));// 32 bits

        Assertions.assertEquals(Long.valueOf(9223372036854775807L), NumberUtils.createNumber("0777777777777777777777"));// 63 bits

        Assertions.assertEquals(new BigInteger("1777777777777777777777", 8), NumberUtils.createNumber("01777777777777777777777"));// 64 bits

    }

    @Test
    public void testCreateFloat() {
        Assertions.assertEquals(Float.valueOf("1234.5"), NumberUtils.createFloat("1234.5"), "createFloat(String) failed");
        Assertions.assertNull(NumberUtils.createFloat(null), "createFloat(null) failed");
        this.testCreateFloatFailure("");
        this.testCreateFloatFailure(" ");
        this.testCreateFloatFailure("\b\t\n\f\r");
        // Funky whitespaces
        this.testCreateFloatFailure("\u00a0\ufeff\u000b\f\u001c\u001d\u001e\u001f");
    }

    @Test
    public void testCreateDouble() {
        Assertions.assertEquals(Double.valueOf("1234.5"), NumberUtils.createDouble("1234.5"), "createDouble(String) failed");
        Assertions.assertNull(NumberUtils.createDouble(null), "createDouble(null) failed");
        this.testCreateDoubleFailure("");
        this.testCreateDoubleFailure(" ");
        this.testCreateDoubleFailure("\b\t\n\f\r");
        // Funky whitespaces
        this.testCreateDoubleFailure("\u00a0\ufeff\u000b\f\u001c\u001d\u001e\u001f");
    }

    @Test
    public void testCreateInteger() {
        Assertions.assertEquals(Integer.valueOf("12345"), NumberUtils.createInteger("12345"), "createInteger(String) failed");
        Assertions.assertNull(NumberUtils.createInteger(null), "createInteger(null) failed");
        this.testCreateIntegerFailure("");
        this.testCreateIntegerFailure(" ");
        this.testCreateIntegerFailure("\b\t\n\f\r");
        // Funky whitespaces
        this.testCreateIntegerFailure("\u00a0\ufeff\u000b\f\u001c\u001d\u001e\u001f");
    }

    @Test
    public void testCreateLong() {
        Assertions.assertEquals(Long.valueOf("12345"), NumberUtils.createLong("12345"), "createLong(String) failed");
        Assertions.assertNull(NumberUtils.createLong(null), "createLong(null) failed");
        this.testCreateLongFailure("");
        this.testCreateLongFailure(" ");
        this.testCreateLongFailure("\b\t\n\f\r");
        // Funky whitespaces
        this.testCreateLongFailure("\u00a0\ufeff\u000b\f\u001c\u001d\u001e\u001f");
    }

    @Test
    public void testCreateBigInteger() {
        Assertions.assertEquals(new BigInteger("12345"), NumberUtils.createBigInteger("12345"), "createBigInteger(String) failed");
        Assertions.assertNull(NumberUtils.createBigInteger(null), "createBigInteger(null) failed");
        this.testCreateBigIntegerFailure("");
        this.testCreateBigIntegerFailure(" ");
        this.testCreateBigIntegerFailure("\b\t\n\f\r");
        // Funky whitespaces
        this.testCreateBigIntegerFailure("\u00a0\ufeff\u000b\f\u001c\u001d\u001e\u001f");
        Assertions.assertEquals(new BigInteger("255"), NumberUtils.createBigInteger("0xff"), "createBigInteger(String) failed");
        Assertions.assertEquals(new BigInteger("255"), NumberUtils.createBigInteger("0Xff"), "createBigInteger(String) failed");
        Assertions.assertEquals(new BigInteger("255"), NumberUtils.createBigInteger("#ff"), "createBigInteger(String) failed");
        Assertions.assertEquals(new BigInteger("-255"), NumberUtils.createBigInteger("-0xff"), "createBigInteger(String) failed");
        Assertions.assertEquals(new BigInteger("255"), NumberUtils.createBigInteger("0377"), "createBigInteger(String) failed");
        Assertions.assertEquals(new BigInteger("-255"), NumberUtils.createBigInteger("-0377"), "createBigInteger(String) failed");
        Assertions.assertEquals(new BigInteger("-255"), NumberUtils.createBigInteger("-0377"), "createBigInteger(String) failed");
        Assertions.assertEquals(new BigInteger("-0"), NumberUtils.createBigInteger("-0"), "createBigInteger(String) failed");
        Assertions.assertEquals(new BigInteger("0"), NumberUtils.createBigInteger("0"), "createBigInteger(String) failed");
        testCreateBigIntegerFailure("#");
        testCreateBigIntegerFailure("-#");
        testCreateBigIntegerFailure("0x");
        testCreateBigIntegerFailure("-0x");
    }

    @Test
    public void testCreateBigDecimal() {
        Assertions.assertEquals(new BigDecimal("1234.5"), NumberUtils.createBigDecimal("1234.5"), "createBigDecimal(String) failed");
        Assertions.assertNull(NumberUtils.createBigDecimal(null), "createBigDecimal(null) failed");
        this.testCreateBigDecimalFailure("");
        this.testCreateBigDecimalFailure(" ");
        this.testCreateBigDecimalFailure("\b\t\n\f\r");
        // Funky whitespaces
        this.testCreateBigDecimalFailure("\u00a0\ufeff\u000b\f\u001c\u001d\u001e\u001f");
        this.testCreateBigDecimalFailure("-");// sign alone not valid

        this.testCreateBigDecimalFailure("--");// comment in NumberUtils suggests some implementations may incorrectly allow this

        this.testCreateBigDecimalFailure("--0");
        this.testCreateBigDecimalFailure("+");// sign alone not valid

        this.testCreateBigDecimalFailure("++");// in case this was also allowed by some JVMs

        this.testCreateBigDecimalFailure("++0");
    }

    // min/max tests
    // ----------------------------------------------------------------------
    @Test
    public void testMinLong_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.min(((long[]) (null))));
    }

    @Test
    public void testMinLong_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.min(new long[0]));
    }

    @Test
    public void testMinLong() {
        Assertions.assertEquals(5, NumberUtils.min(new long[]{ 5 }), "min(long[]) failed for array length 1");
        Assertions.assertEquals(6, NumberUtils.min(new long[]{ 6, 9 }), "min(long[]) failed for array length 2");
        Assertions.assertEquals((-10), NumberUtils.min(new long[]{ -10, -5, 0, 5, 10 }));
        Assertions.assertEquals((-10), NumberUtils.min(new long[]{ -5, 0, -10, 5, 10 }));
    }

    @Test
    public void testMinInt_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.min(((int[]) (null))));
    }

    @Test
    public void testMinInt_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.min(new int[0]));
    }

    @Test
    public void testMinInt() {
        Assertions.assertEquals(5, NumberUtils.min(5), "min(int[]) failed for array length 1");
        Assertions.assertEquals(6, NumberUtils.min(6, 9), "min(int[]) failed for array length 2");
        Assertions.assertEquals((-10), NumberUtils.min((-10), (-5), 0, 5, 10));
        Assertions.assertEquals((-10), NumberUtils.min((-5), 0, (-10), 5, 10));
    }

    @Test
    public void testMinShort_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.min(((short[]) (null))));
    }

    @Test
    public void testMinShort_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.min(new short[0]));
    }

    @Test
    public void testMinShort() {
        Assertions.assertEquals(5, NumberUtils.min(new short[]{ 5 }), "min(short[]) failed for array length 1");
        Assertions.assertEquals(6, NumberUtils.min(new short[]{ 6, 9 }), "min(short[]) failed for array length 2");
        Assertions.assertEquals((-10), NumberUtils.min(new short[]{ -10, -5, 0, 5, 10 }));
        Assertions.assertEquals((-10), NumberUtils.min(new short[]{ -5, 0, -10, 5, 10 }));
    }

    @Test
    public void testMinByte_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.min(((byte[]) (null))));
    }

    @Test
    public void testMinByte_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, NumberUtils::min);
    }

    @Test
    public void testMinByte() {
        Assertions.assertEquals(5, NumberUtils.min(new byte[]{ 5 }), "min(byte[]) failed for array length 1");
        Assertions.assertEquals(6, NumberUtils.min(new byte[]{ 6, 9 }), "min(byte[]) failed for array length 2");
        Assertions.assertEquals((-10), NumberUtils.min(new byte[]{ -10, -5, 0, 5, 10 }));
        Assertions.assertEquals((-10), NumberUtils.min(new byte[]{ -5, 0, -10, 5, 10 }));
    }

    @Test
    public void testMinDouble_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.min(((double[]) (null))));
    }

    @Test
    public void testMinDouble_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.min(new double[0]));
    }

    @Test
    public void testMinDouble() {
        Assertions.assertEquals(5.12, NumberUtils.min(5.12), "min(double[]) failed for array length 1");
        Assertions.assertEquals(6.23, NumberUtils.min(6.23, 9.34), "min(double[]) failed for array length 2");
        Assertions.assertEquals((-10.45), NumberUtils.min((-10.45), (-5.56), 0, 5.67, 10.78), "min(double[]) failed for array length 5");
        Assertions.assertEquals((-10), NumberUtils.min(new double[]{ -10, -5, 0, 5, 10 }), 1.0E-4);
        Assertions.assertEquals((-10), NumberUtils.min(new double[]{ -5, 0, -10, 5, 10 }), 1.0E-4);
    }

    @Test
    public void testMinFloat_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.min(((float[]) (null))));
    }

    @Test
    public void testMinFloat_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.min(new float[0]));
    }

    @Test
    public void testMinFloat() {
        Assertions.assertEquals(5.9F, NumberUtils.min(5.9F), "min(float[]) failed for array length 1");
        Assertions.assertEquals(6.8F, NumberUtils.min(6.8F, 9.7F), "min(float[]) failed for array length 2");
        Assertions.assertEquals((-10.6F), NumberUtils.min((-10.6F), (-5.5F), 0, 5.4F, 10.3F), "min(float[]) failed for array length 5");
        Assertions.assertEquals((-10), NumberUtils.min(new float[]{ -10, -5, 0, 5, 10 }), 1.0E-4F);
        Assertions.assertEquals((-10), NumberUtils.min(new float[]{ -5, 0, -10, 5, 10 }), 1.0E-4F);
    }

    @Test
    public void testMaxLong_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(((long[]) (null))));
    }

    @Test
    public void testMaxLong_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(new long[0]));
    }

    @Test
    public void testMaxLong() {
        Assertions.assertEquals(5, NumberUtils.max(new long[]{ 5 }), "max(long[]) failed for array length 1");
        Assertions.assertEquals(9, NumberUtils.max(new long[]{ 6, 9 }), "max(long[]) failed for array length 2");
        Assertions.assertEquals(10, NumberUtils.max(new long[]{ -10, -5, 0, 5, 10 }), "max(long[]) failed for array length 5");
        Assertions.assertEquals(10, NumberUtils.max(new long[]{ -10, -5, 0, 5, 10 }));
        Assertions.assertEquals(10, NumberUtils.max(new long[]{ -5, 0, 10, 5, -10 }));
    }

    @Test
    public void testMaxInt_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(((int[]) (null))));
    }

    @Test
    public void testMaxInt_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(new int[0]));
    }

    @Test
    public void testMaxInt() {
        Assertions.assertEquals(5, NumberUtils.max(5), "max(int[]) failed for array length 1");
        Assertions.assertEquals(9, NumberUtils.max(6, 9), "max(int[]) failed for array length 2");
        Assertions.assertEquals(10, NumberUtils.max((-10), (-5), 0, 5, 10), "max(int[]) failed for array length 5");
        Assertions.assertEquals(10, NumberUtils.max((-10), (-5), 0, 5, 10));
        Assertions.assertEquals(10, NumberUtils.max((-5), 0, 10, 5, (-10)));
    }

    @Test
    public void testMaxShort_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(((short[]) (null))));
    }

    @Test
    public void testMaxShort_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(new short[0]));
    }

    @Test
    public void testMaxShort() {
        Assertions.assertEquals(5, NumberUtils.max(new short[]{ 5 }), "max(short[]) failed for array length 1");
        Assertions.assertEquals(9, NumberUtils.max(new short[]{ 6, 9 }), "max(short[]) failed for array length 2");
        Assertions.assertEquals(10, NumberUtils.max(new short[]{ -10, -5, 0, 5, 10 }), "max(short[]) failed for array length 5");
        Assertions.assertEquals(10, NumberUtils.max(new short[]{ -10, -5, 0, 5, 10 }));
        Assertions.assertEquals(10, NumberUtils.max(new short[]{ -5, 0, 10, 5, -10 }));
    }

    @Test
    public void testMaxByte_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(((byte[]) (null))));
    }

    @Test
    public void testMaxByte_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, NumberUtils::max);
    }

    @Test
    public void testMaxByte() {
        Assertions.assertEquals(5, NumberUtils.max(new byte[]{ 5 }), "max(byte[]) failed for array length 1");
        Assertions.assertEquals(9, NumberUtils.max(new byte[]{ 6, 9 }), "max(byte[]) failed for array length 2");
        Assertions.assertEquals(10, NumberUtils.max(new byte[]{ -10, -5, 0, 5, 10 }), "max(byte[]) failed for array length 5");
        Assertions.assertEquals(10, NumberUtils.max(new byte[]{ -10, -5, 0, 5, 10 }));
        Assertions.assertEquals(10, NumberUtils.max(new byte[]{ -5, 0, 10, 5, -10 }));
    }

    @Test
    public void testMaxDouble_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(((double[]) (null))));
    }

    @Test
    public void testMaxDouble_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(new double[0]));
    }

    @Test
    public void testMaxDouble() {
        final double[] d = null;
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(d), "No exception was thrown for null input.");
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(new double[0]), "No exception was thrown for empty input.");
        Assertions.assertEquals(5.1F, NumberUtils.max(new double[]{ 5.1F }), "max(double[]) failed for array length 1");
        Assertions.assertEquals(9.2F, NumberUtils.max(new double[]{ 6.3F, 9.2F }), "max(double[]) failed for array length 2");
        Assertions.assertEquals(10.4F, NumberUtils.max(new double[]{ -10.5F, -5.6F, 0, 5.7F, 10.4F }), "max(double[]) failed for float length 5");
        Assertions.assertEquals(10, NumberUtils.max(new double[]{ -10, -5, 0, 5, 10 }), 1.0E-4);
        Assertions.assertEquals(10, NumberUtils.max(new double[]{ -5, 0, 10, 5, -10 }), 1.0E-4);
    }

    @Test
    public void testMaxFloat_nullArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(((float[]) (null))));
    }

    @Test
    public void testMaxFloat_emptyArray() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberUtils.max(new float[0]));
    }

    @Test
    public void testMaxFloat() {
        Assertions.assertEquals(5.1F, NumberUtils.max(5.1F), "max(float[]) failed for array length 1");
        Assertions.assertEquals(9.2F, NumberUtils.max(6.3F, 9.2F), "max(float[]) failed for array length 2");
        Assertions.assertEquals(10.4F, NumberUtils.max((-10.5F), (-5.6F), 0, 5.7F, 10.4F), "max(float[]) failed for float length 5");
        Assertions.assertEquals(10, NumberUtils.max(new float[]{ -10, -5, 0, 5, 10 }), 1.0E-4F);
        Assertions.assertEquals(10, NumberUtils.max(new float[]{ -5, 0, 10, 5, -10 }), 1.0E-4F);
    }

    @Test
    public void testMinimumLong() {
        Assertions.assertEquals(12345L, NumberUtils.min(12345L, (12345L + 1L), (12345L + 2L)), "minimum(long, long, long) 1 failed");
        Assertions.assertEquals(12345L, NumberUtils.min((12345L + 1L), 12345L, (12345 + 2L)), "minimum(long, long, long) 2 failed");
        Assertions.assertEquals(12345L, NumberUtils.min((12345L + 1L), (12345L + 2L), 12345L), "minimum(long, long, long) 3 failed");
        Assertions.assertEquals(12345L, NumberUtils.min((12345L + 1L), 12345L, 12345L), "minimum(long, long, long) 4 failed");
        Assertions.assertEquals(12345L, NumberUtils.min(12345L, 12345L, 12345L), "minimum(long, long, long) 5 failed");
    }

    @Test
    public void testMinimumInt() {
        Assertions.assertEquals(12345, NumberUtils.min(12345, (12345 + 1), (12345 + 2)), "minimum(int, int, int) 1 failed");
        Assertions.assertEquals(12345, NumberUtils.min((12345 + 1), 12345, (12345 + 2)), "minimum(int, int, int) 2 failed");
        Assertions.assertEquals(12345, NumberUtils.min((12345 + 1), (12345 + 2), 12345), "minimum(int, int, int) 3 failed");
        Assertions.assertEquals(12345, NumberUtils.min((12345 + 1), 12345, 12345), "minimum(int, int, int) 4 failed");
        Assertions.assertEquals(12345, NumberUtils.min(12345, 12345, 12345), "minimum(int, int, int) 5 failed");
    }

    @Test
    public void testMinimumShort() {
        final short low = 1234;
        final short mid = 1234 + 1;
        final short high = 1234 + 2;
        Assertions.assertEquals(low, NumberUtils.min(low, mid, high), "minimum(short, short, short) 1 failed");
        Assertions.assertEquals(low, NumberUtils.min(mid, low, high), "minimum(short, short, short) 2 failed");
        Assertions.assertEquals(low, NumberUtils.min(mid, high, low), "minimum(short, short, short) 3 failed");
        Assertions.assertEquals(low, NumberUtils.min(low, mid, low), "minimum(short, short, short) 4 failed");
    }

    @Test
    public void testMinimumByte() {
        final byte low = 123;
        final byte mid = 123 + 1;
        final byte high = 123 + 2;
        Assertions.assertEquals(low, NumberUtils.min(low, mid, high), "minimum(byte, byte, byte) 1 failed");
        Assertions.assertEquals(low, NumberUtils.min(mid, low, high), "minimum(byte, byte, byte) 2 failed");
        Assertions.assertEquals(low, NumberUtils.min(mid, high, low), "minimum(byte, byte, byte) 3 failed");
        Assertions.assertEquals(low, NumberUtils.min(low, mid, low), "minimum(byte, byte, byte) 4 failed");
    }

    @Test
    public void testMinimumDouble() {
        final double low = 12.3;
        final double mid = 12.3 + 1;
        final double high = 12.3 + 2;
        Assertions.assertEquals(low, NumberUtils.min(low, mid, high), 1.0E-4);
        Assertions.assertEquals(low, NumberUtils.min(mid, low, high), 1.0E-4);
        Assertions.assertEquals(low, NumberUtils.min(mid, high, low), 1.0E-4);
        Assertions.assertEquals(low, NumberUtils.min(low, mid, low), 1.0E-4);
        Assertions.assertEquals(mid, NumberUtils.min(high, mid, high), 1.0E-4);
    }

    @Test
    public void testMinimumFloat() {
        final float low = 12.3F;
        final float mid = 12.3F + 1;
        final float high = 12.3F + 2;
        Assertions.assertEquals(low, NumberUtils.min(low, mid, high), 1.0E-4F);
        Assertions.assertEquals(low, NumberUtils.min(mid, low, high), 1.0E-4F);
        Assertions.assertEquals(low, NumberUtils.min(mid, high, low), 1.0E-4F);
        Assertions.assertEquals(low, NumberUtils.min(low, mid, low), 1.0E-4F);
        Assertions.assertEquals(mid, NumberUtils.min(high, mid, high), 1.0E-4F);
    }

    @Test
    public void testMaximumLong() {
        Assertions.assertEquals(12345L, NumberUtils.max(12345L, (12345L - 1L), (12345L - 2L)), "maximum(long, long, long) 1 failed");
        Assertions.assertEquals(12345L, NumberUtils.max((12345L - 1L), 12345L, (12345L - 2L)), "maximum(long, long, long) 2 failed");
        Assertions.assertEquals(12345L, NumberUtils.max((12345L - 1L), (12345L - 2L), 12345L), "maximum(long, long, long) 3 failed");
        Assertions.assertEquals(12345L, NumberUtils.max((12345L - 1L), 12345L, 12345L), "maximum(long, long, long) 4 failed");
        Assertions.assertEquals(12345L, NumberUtils.max(12345L, 12345L, 12345L), "maximum(long, long, long) 5 failed");
    }

    @Test
    public void testMaximumInt() {
        Assertions.assertEquals(12345, NumberUtils.max(12345, (12345 - 1), (12345 - 2)), "maximum(int, int, int) 1 failed");
        Assertions.assertEquals(12345, NumberUtils.max((12345 - 1), 12345, (12345 - 2)), "maximum(int, int, int) 2 failed");
        Assertions.assertEquals(12345, NumberUtils.max((12345 - 1), (12345 - 2), 12345), "maximum(int, int, int) 3 failed");
        Assertions.assertEquals(12345, NumberUtils.max((12345 - 1), 12345, 12345), "maximum(int, int, int) 4 failed");
        Assertions.assertEquals(12345, NumberUtils.max(12345, 12345, 12345), "maximum(int, int, int) 5 failed");
    }

    @Test
    public void testMaximumShort() {
        final short low = 1234;
        final short mid = 1234 + 1;
        final short high = 1234 + 2;
        Assertions.assertEquals(high, NumberUtils.max(low, mid, high), "maximum(short, short, short) 1 failed");
        Assertions.assertEquals(high, NumberUtils.max(mid, low, high), "maximum(short, short, short) 2 failed");
        Assertions.assertEquals(high, NumberUtils.max(mid, high, low), "maximum(short, short, short) 3 failed");
        Assertions.assertEquals(high, NumberUtils.max(high, mid, high), "maximum(short, short, short) 4 failed");
    }

    @Test
    public void testMaximumByte() {
        final byte low = 123;
        final byte mid = 123 + 1;
        final byte high = 123 + 2;
        Assertions.assertEquals(high, NumberUtils.max(low, mid, high), "maximum(byte, byte, byte) 1 failed");
        Assertions.assertEquals(high, NumberUtils.max(mid, low, high), "maximum(byte, byte, byte) 2 failed");
        Assertions.assertEquals(high, NumberUtils.max(mid, high, low), "maximum(byte, byte, byte) 3 failed");
        Assertions.assertEquals(high, NumberUtils.max(high, mid, high), "maximum(byte, byte, byte) 4 failed");
    }

    @Test
    public void testMaximumDouble() {
        final double low = 12.3;
        final double mid = 12.3 + 1;
        final double high = 12.3 + 2;
        Assertions.assertEquals(high, NumberUtils.max(low, mid, high), 1.0E-4);
        Assertions.assertEquals(high, NumberUtils.max(mid, low, high), 1.0E-4);
        Assertions.assertEquals(high, NumberUtils.max(mid, high, low), 1.0E-4);
        Assertions.assertEquals(mid, NumberUtils.max(low, mid, low), 1.0E-4);
        Assertions.assertEquals(high, NumberUtils.max(high, mid, high), 1.0E-4);
    }

    @Test
    public void testMaximumFloat() {
        final float low = 12.3F;
        final float mid = 12.3F + 1;
        final float high = 12.3F + 2;
        Assertions.assertEquals(high, NumberUtils.max(low, mid, high), 1.0E-4F);
        Assertions.assertEquals(high, NumberUtils.max(mid, low, high), 1.0E-4F);
        Assertions.assertEquals(high, NumberUtils.max(mid, high, low), 1.0E-4F);
        Assertions.assertEquals(mid, NumberUtils.max(low, mid, low), 1.0E-4F);
        Assertions.assertEquals(high, NumberUtils.max(high, mid, high), 1.0E-4F);
    }

    // Testing JDK against old Lang functionality
    @Test
    public void testCompareDouble() {
        Assertions.assertEquals(0, Double.compare(Double.NaN, Double.NaN));
        Assertions.assertEquals(Double.compare(Double.NaN, Double.POSITIVE_INFINITY), (+1));
        Assertions.assertEquals(Double.compare(Double.NaN, Double.MAX_VALUE), (+1));
        Assertions.assertEquals(Double.compare(Double.NaN, 1.2), (+1));
        Assertions.assertEquals(Double.compare(Double.NaN, 0.0), (+1));
        Assertions.assertEquals(Double.compare(Double.NaN, (-0.0)), (+1));
        Assertions.assertEquals(Double.compare(Double.NaN, (-1.2)), (+1));
        Assertions.assertEquals(Double.compare(Double.NaN, (-(Double.MAX_VALUE))), (+1));
        Assertions.assertEquals(Double.compare(Double.NaN, Double.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Double.compare(Double.POSITIVE_INFINITY, Double.NaN), (-1));
        Assertions.assertEquals(0, Double.compare(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        Assertions.assertEquals(Double.compare(Double.POSITIVE_INFINITY, Double.MAX_VALUE), (+1));
        Assertions.assertEquals(Double.compare(Double.POSITIVE_INFINITY, 1.2), (+1));
        Assertions.assertEquals(Double.compare(Double.POSITIVE_INFINITY, 0.0), (+1));
        Assertions.assertEquals(Double.compare(Double.POSITIVE_INFINITY, (-0.0)), (+1));
        Assertions.assertEquals(Double.compare(Double.POSITIVE_INFINITY, (-1.2)), (+1));
        Assertions.assertEquals(Double.compare(Double.POSITIVE_INFINITY, (-(Double.MAX_VALUE))), (+1));
        Assertions.assertEquals(Double.compare(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Double.compare(Double.MAX_VALUE, Double.NaN), (-1));
        Assertions.assertEquals(Double.compare(Double.MAX_VALUE, Double.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(0, Double.compare(Double.MAX_VALUE, Double.MAX_VALUE));
        Assertions.assertEquals(Double.compare(Double.MAX_VALUE, 1.2), (+1));
        Assertions.assertEquals(Double.compare(Double.MAX_VALUE, 0.0), (+1));
        Assertions.assertEquals(Double.compare(Double.MAX_VALUE, (-0.0)), (+1));
        Assertions.assertEquals(Double.compare(Double.MAX_VALUE, (-1.2)), (+1));
        Assertions.assertEquals(Double.compare(Double.MAX_VALUE, (-(Double.MAX_VALUE))), (+1));
        Assertions.assertEquals(Double.compare(Double.MAX_VALUE, Double.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Double.compare(1.2, Double.NaN), (-1));
        Assertions.assertEquals(Double.compare(1.2, Double.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Double.compare(1.2, Double.MAX_VALUE), (-1));
        Assertions.assertEquals(0, Double.compare(1.2, 1.2));
        Assertions.assertEquals(Double.compare(1.2, 0.0), (+1));
        Assertions.assertEquals(Double.compare(1.2, (-0.0)), (+1));
        Assertions.assertEquals(Double.compare(1.2, (-1.2)), (+1));
        Assertions.assertEquals(Double.compare(1.2, (-(Double.MAX_VALUE))), (+1));
        Assertions.assertEquals(Double.compare(1.2, Double.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Double.compare(0.0, Double.NaN), (-1));
        Assertions.assertEquals(Double.compare(0.0, Double.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Double.compare(0.0, Double.MAX_VALUE), (-1));
        Assertions.assertEquals(Double.compare(0.0, 1.2), (-1));
        Assertions.assertEquals(0, Double.compare(0.0, 0.0));
        Assertions.assertEquals(Double.compare(0.0, (-0.0)), (+1));
        Assertions.assertEquals(Double.compare(0.0, (-1.2)), (+1));
        Assertions.assertEquals(Double.compare(0.0, (-(Double.MAX_VALUE))), (+1));
        Assertions.assertEquals(Double.compare(0.0, Double.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Double.compare((-0.0), Double.NaN), (-1));
        Assertions.assertEquals(Double.compare((-0.0), Double.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Double.compare((-0.0), Double.MAX_VALUE), (-1));
        Assertions.assertEquals(Double.compare((-0.0), 1.2), (-1));
        Assertions.assertEquals(Double.compare((-0.0), 0.0), (-1));
        Assertions.assertEquals(0, Double.compare((-0.0), (-0.0)));
        Assertions.assertEquals(Double.compare((-0.0), (-1.2)), (+1));
        Assertions.assertEquals(Double.compare((-0.0), (-(Double.MAX_VALUE))), (+1));
        Assertions.assertEquals(Double.compare((-0.0), Double.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Double.compare((-1.2), Double.NaN), (-1));
        Assertions.assertEquals(Double.compare((-1.2), Double.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Double.compare((-1.2), Double.MAX_VALUE), (-1));
        Assertions.assertEquals(Double.compare((-1.2), 1.2), (-1));
        Assertions.assertEquals(Double.compare((-1.2), 0.0), (-1));
        Assertions.assertEquals(Double.compare((-1.2), (-0.0)), (-1));
        Assertions.assertEquals(0, Double.compare((-1.2), (-1.2)));
        Assertions.assertEquals(Double.compare((-1.2), (-(Double.MAX_VALUE))), (+1));
        Assertions.assertEquals(Double.compare((-1.2), Double.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Double.compare((-(Double.MAX_VALUE)), Double.NaN), (-1));
        Assertions.assertEquals(Double.compare((-(Double.MAX_VALUE)), Double.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Double.compare((-(Double.MAX_VALUE)), Double.MAX_VALUE), (-1));
        Assertions.assertEquals(Double.compare((-(Double.MAX_VALUE)), 1.2), (-1));
        Assertions.assertEquals(Double.compare((-(Double.MAX_VALUE)), 0.0), (-1));
        Assertions.assertEquals(Double.compare((-(Double.MAX_VALUE)), (-0.0)), (-1));
        Assertions.assertEquals(Double.compare((-(Double.MAX_VALUE)), (-1.2)), (-1));
        Assertions.assertEquals(0, Double.compare((-(Double.MAX_VALUE)), (-(Double.MAX_VALUE))));
        Assertions.assertEquals(Double.compare((-(Double.MAX_VALUE)), Double.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Double.compare(Double.NEGATIVE_INFINITY, Double.NaN), (-1));
        Assertions.assertEquals(Double.compare(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Double.compare(Double.NEGATIVE_INFINITY, Double.MAX_VALUE), (-1));
        Assertions.assertEquals(Double.compare(Double.NEGATIVE_INFINITY, 1.2), (-1));
        Assertions.assertEquals(Double.compare(Double.NEGATIVE_INFINITY, 0.0), (-1));
        Assertions.assertEquals(Double.compare(Double.NEGATIVE_INFINITY, (-0.0)), (-1));
        Assertions.assertEquals(Double.compare(Double.NEGATIVE_INFINITY, (-1.2)), (-1));
        Assertions.assertEquals(Double.compare(Double.NEGATIVE_INFINITY, (-(Double.MAX_VALUE))), (-1));
        Assertions.assertEquals(0, Double.compare(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testCompareFloat() {
        Assertions.assertEquals(0, Float.compare(Float.NaN, Float.NaN));
        Assertions.assertEquals(Float.compare(Float.NaN, Float.POSITIVE_INFINITY), (+1));
        Assertions.assertEquals(Float.compare(Float.NaN, Float.MAX_VALUE), (+1));
        Assertions.assertEquals(Float.compare(Float.NaN, 1.2F), (+1));
        Assertions.assertEquals(Float.compare(Float.NaN, 0.0F), (+1));
        Assertions.assertEquals(Float.compare(Float.NaN, (-0.0F)), (+1));
        Assertions.assertEquals(Float.compare(Float.NaN, (-1.2F)), (+1));
        Assertions.assertEquals(Float.compare(Float.NaN, (-(Float.MAX_VALUE))), (+1));
        Assertions.assertEquals(Float.compare(Float.NaN, Float.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Float.compare(Float.POSITIVE_INFINITY, Float.NaN), (-1));
        Assertions.assertEquals(0, Float.compare(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
        Assertions.assertEquals(Float.compare(Float.POSITIVE_INFINITY, Float.MAX_VALUE), (+1));
        Assertions.assertEquals(Float.compare(Float.POSITIVE_INFINITY, 1.2F), (+1));
        Assertions.assertEquals(Float.compare(Float.POSITIVE_INFINITY, 0.0F), (+1));
        Assertions.assertEquals(Float.compare(Float.POSITIVE_INFINITY, (-0.0F)), (+1));
        Assertions.assertEquals(Float.compare(Float.POSITIVE_INFINITY, (-1.2F)), (+1));
        Assertions.assertEquals(Float.compare(Float.POSITIVE_INFINITY, (-(Float.MAX_VALUE))), (+1));
        Assertions.assertEquals(Float.compare(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Float.compare(Float.MAX_VALUE, Float.NaN), (-1));
        Assertions.assertEquals(Float.compare(Float.MAX_VALUE, Float.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(0, Float.compare(Float.MAX_VALUE, Float.MAX_VALUE));
        Assertions.assertEquals(Float.compare(Float.MAX_VALUE, 1.2F), (+1));
        Assertions.assertEquals(Float.compare(Float.MAX_VALUE, 0.0F), (+1));
        Assertions.assertEquals(Float.compare(Float.MAX_VALUE, (-0.0F)), (+1));
        Assertions.assertEquals(Float.compare(Float.MAX_VALUE, (-1.2F)), (+1));
        Assertions.assertEquals(Float.compare(Float.MAX_VALUE, (-(Float.MAX_VALUE))), (+1));
        Assertions.assertEquals(Float.compare(Float.MAX_VALUE, Float.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Float.compare(1.2F, Float.NaN), (-1));
        Assertions.assertEquals(Float.compare(1.2F, Float.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Float.compare(1.2F, Float.MAX_VALUE), (-1));
        Assertions.assertEquals(0, Float.compare(1.2F, 1.2F));
        Assertions.assertEquals(Float.compare(1.2F, 0.0F), (+1));
        Assertions.assertEquals(Float.compare(1.2F, (-0.0F)), (+1));
        Assertions.assertEquals(Float.compare(1.2F, (-1.2F)), (+1));
        Assertions.assertEquals(Float.compare(1.2F, (-(Float.MAX_VALUE))), (+1));
        Assertions.assertEquals(Float.compare(1.2F, Float.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Float.compare(0.0F, Float.NaN), (-1));
        Assertions.assertEquals(Float.compare(0.0F, Float.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Float.compare(0.0F, Float.MAX_VALUE), (-1));
        Assertions.assertEquals(Float.compare(0.0F, 1.2F), (-1));
        Assertions.assertEquals(0, Float.compare(0.0F, 0.0F));
        Assertions.assertEquals(Float.compare(0.0F, (-0.0F)), (+1));
        Assertions.assertEquals(Float.compare(0.0F, (-1.2F)), (+1));
        Assertions.assertEquals(Float.compare(0.0F, (-(Float.MAX_VALUE))), (+1));
        Assertions.assertEquals(Float.compare(0.0F, Float.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Float.compare((-0.0F), Float.NaN), (-1));
        Assertions.assertEquals(Float.compare((-0.0F), Float.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Float.compare((-0.0F), Float.MAX_VALUE), (-1));
        Assertions.assertEquals(Float.compare((-0.0F), 1.2F), (-1));
        Assertions.assertEquals(Float.compare((-0.0F), 0.0F), (-1));
        Assertions.assertEquals(0, Float.compare((-0.0F), (-0.0F)));
        Assertions.assertEquals(Float.compare((-0.0F), (-1.2F)), (+1));
        Assertions.assertEquals(Float.compare((-0.0F), (-(Float.MAX_VALUE))), (+1));
        Assertions.assertEquals(Float.compare((-0.0F), Float.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Float.compare((-1.2F), Float.NaN), (-1));
        Assertions.assertEquals(Float.compare((-1.2F), Float.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Float.compare((-1.2F), Float.MAX_VALUE), (-1));
        Assertions.assertEquals(Float.compare((-1.2F), 1.2F), (-1));
        Assertions.assertEquals(Float.compare((-1.2F), 0.0F), (-1));
        Assertions.assertEquals(Float.compare((-1.2F), (-0.0F)), (-1));
        Assertions.assertEquals(0, Float.compare((-1.2F), (-1.2F)));
        Assertions.assertEquals(Float.compare((-1.2F), (-(Float.MAX_VALUE))), (+1));
        Assertions.assertEquals(Float.compare((-1.2F), Float.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Float.compare((-(Float.MAX_VALUE)), Float.NaN), (-1));
        Assertions.assertEquals(Float.compare((-(Float.MAX_VALUE)), Float.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Float.compare((-(Float.MAX_VALUE)), Float.MAX_VALUE), (-1));
        Assertions.assertEquals(Float.compare((-(Float.MAX_VALUE)), 1.2F), (-1));
        Assertions.assertEquals(Float.compare((-(Float.MAX_VALUE)), 0.0F), (-1));
        Assertions.assertEquals(Float.compare((-(Float.MAX_VALUE)), (-0.0F)), (-1));
        Assertions.assertEquals(Float.compare((-(Float.MAX_VALUE)), (-1.2F)), (-1));
        Assertions.assertEquals(0, Float.compare((-(Float.MAX_VALUE)), (-(Float.MAX_VALUE))));
        Assertions.assertEquals(Float.compare((-(Float.MAX_VALUE)), Float.NEGATIVE_INFINITY), (+1));
        Assertions.assertEquals(Float.compare(Float.NEGATIVE_INFINITY, Float.NaN), (-1));
        Assertions.assertEquals(Float.compare(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY), (-1));
        Assertions.assertEquals(Float.compare(Float.NEGATIVE_INFINITY, Float.MAX_VALUE), (-1));
        Assertions.assertEquals(Float.compare(Float.NEGATIVE_INFINITY, 1.2F), (-1));
        Assertions.assertEquals(Float.compare(Float.NEGATIVE_INFINITY, 0.0F), (-1));
        Assertions.assertEquals(Float.compare(Float.NEGATIVE_INFINITY, (-0.0F)), (-1));
        Assertions.assertEquals(Float.compare(Float.NEGATIVE_INFINITY, (-1.2F)), (-1));
        Assertions.assertEquals(Float.compare(Float.NEGATIVE_INFINITY, (-(Float.MAX_VALUE))), (-1));
        Assertions.assertEquals(0, Float.compare(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY));
    }

    @Test
    public void testIsDigits() {
        Assertions.assertFalse(NumberUtils.isDigits(null), "isDigits(null) failed");
        Assertions.assertFalse(NumberUtils.isDigits(""), "isDigits('') failed");
        Assertions.assertTrue(NumberUtils.isDigits("12345"), "isDigits(String) failed");
        Assertions.assertFalse(NumberUtils.isDigits("1234.5"), "isDigits(String) neg 1 failed");
        Assertions.assertFalse(NumberUtils.isDigits("1ab"), "isDigits(String) neg 3 failed");
        Assertions.assertFalse(NumberUtils.isDigits("abc"), "isDigits(String) neg 4 failed");
    }

    /**
     * Tests isCreatable(String) and tests that createNumber(String) returns
     * a valid number iff isCreatable(String) returns false.
     */
    @Test
    public void testIsCreatable() {
        compareIsCreatableWithCreateNumber("12345", true);
        compareIsCreatableWithCreateNumber("1234.5", true);
        compareIsCreatableWithCreateNumber(".12345", true);
        compareIsCreatableWithCreateNumber("1234E5", true);
        compareIsCreatableWithCreateNumber("1234E+5", true);
        compareIsCreatableWithCreateNumber("1234E-5", true);
        compareIsCreatableWithCreateNumber("123.4E5", true);
        compareIsCreatableWithCreateNumber("-1234", true);
        compareIsCreatableWithCreateNumber("-1234.5", true);
        compareIsCreatableWithCreateNumber("-.12345", true);
        compareIsCreatableWithCreateNumber("-1234E5", true);
        compareIsCreatableWithCreateNumber("0", true);
        compareIsCreatableWithCreateNumber("0.1", true);// LANG-1216

        compareIsCreatableWithCreateNumber("-0", true);
        compareIsCreatableWithCreateNumber("01234", true);
        compareIsCreatableWithCreateNumber("-01234", true);
        compareIsCreatableWithCreateNumber("-0xABC123", true);
        compareIsCreatableWithCreateNumber("-0x0", true);
        compareIsCreatableWithCreateNumber("123.4E21D", true);
        compareIsCreatableWithCreateNumber("-221.23F", true);
        compareIsCreatableWithCreateNumber("22338L", true);
        compareIsCreatableWithCreateNumber(null, false);
        compareIsCreatableWithCreateNumber("", false);
        compareIsCreatableWithCreateNumber(" ", false);
        compareIsCreatableWithCreateNumber("\r\n\t", false);
        compareIsCreatableWithCreateNumber("--2.3", false);
        compareIsCreatableWithCreateNumber(".12.3", false);
        compareIsCreatableWithCreateNumber("-123E", false);
        compareIsCreatableWithCreateNumber("-123E+-212", false);
        compareIsCreatableWithCreateNumber("-123E2.12", false);
        compareIsCreatableWithCreateNumber("0xGF", false);
        compareIsCreatableWithCreateNumber("0xFAE-1", false);
        compareIsCreatableWithCreateNumber(".", false);
        compareIsCreatableWithCreateNumber("-0ABC123", false);
        compareIsCreatableWithCreateNumber("123.4E-D", false);
        compareIsCreatableWithCreateNumber("123.4ED", false);
        compareIsCreatableWithCreateNumber("1234E5l", false);
        compareIsCreatableWithCreateNumber("11a", false);
        compareIsCreatableWithCreateNumber("1a", false);
        compareIsCreatableWithCreateNumber("a", false);
        compareIsCreatableWithCreateNumber("11g", false);
        compareIsCreatableWithCreateNumber("11z", false);
        compareIsCreatableWithCreateNumber("11def", false);
        compareIsCreatableWithCreateNumber("11d11", false);
        compareIsCreatableWithCreateNumber("11 11", false);
        compareIsCreatableWithCreateNumber(" 1111", false);
        compareIsCreatableWithCreateNumber("1111 ", false);
        compareIsCreatableWithCreateNumber("2.", true);// LANG-521

        compareIsCreatableWithCreateNumber("1.1L", false);// LANG-664

    }

    @Test
    public void testLANG971() {
        compareIsCreatableWithCreateNumber("0085", false);
        compareIsCreatableWithCreateNumber("085", false);
        compareIsCreatableWithCreateNumber("08", false);
        compareIsCreatableWithCreateNumber("07", true);
        compareIsCreatableWithCreateNumber("00", true);
    }

    @Test
    public void testLANG992() {
        compareIsCreatableWithCreateNumber("0.0", true);
        compareIsCreatableWithCreateNumber("0.4790", true);
    }

    @Test
    public void testLANG972() {
        compareIsCreatableWithCreateNumber("0xABCD", true);
        compareIsCreatableWithCreateNumber("0XABCD", true);
    }

    @Test
    public void testLANG1252() {
        compareIsCreatableWithCreateNumber("+2", true);
        compareIsCreatableWithCreateNumber("+2.0", true);
    }

    /**
     * Tests isCreatable(String) and tests that createNumber(String) returns
     * a valid number iff isCreatable(String) returns false.
     */
    @Test
    public void testIsNumber() {
        compareIsNumberWithCreateNumber("12345", true);
        compareIsNumberWithCreateNumber("1234.5", true);
        compareIsNumberWithCreateNumber(".12345", true);
        compareIsNumberWithCreateNumber("1234E5", true);
        compareIsNumberWithCreateNumber("1234E+5", true);
        compareIsNumberWithCreateNumber("1234E-5", true);
        compareIsNumberWithCreateNumber("123.4E5", true);
        compareIsNumberWithCreateNumber("-1234", true);
        compareIsNumberWithCreateNumber("-1234.5", true);
        compareIsNumberWithCreateNumber("-.12345", true);
        compareIsNumberWithCreateNumber("-0001.12345", true);
        compareIsNumberWithCreateNumber("-000.12345", true);
        compareIsNumberWithCreateNumber("+00.12345", true);
        compareIsNumberWithCreateNumber("+0002.12345", true);
        compareIsNumberWithCreateNumber("-1234E5", true);
        compareIsNumberWithCreateNumber("0", true);
        compareIsNumberWithCreateNumber("-0", true);
        compareIsNumberWithCreateNumber("01234", true);
        compareIsNumberWithCreateNumber("-01234", true);
        compareIsNumberWithCreateNumber("-0xABC123", true);
        compareIsNumberWithCreateNumber("-0x0", true);
        compareIsNumberWithCreateNumber("123.4E21D", true);
        compareIsNumberWithCreateNumber("-221.23F", true);
        compareIsNumberWithCreateNumber("22338L", true);
        compareIsNumberWithCreateNumber(null, false);
        compareIsNumberWithCreateNumber("", false);
        compareIsNumberWithCreateNumber(" ", false);
        compareIsNumberWithCreateNumber("\r\n\t", false);
        compareIsNumberWithCreateNumber("--2.3", false);
        compareIsNumberWithCreateNumber(".12.3", false);
        compareIsNumberWithCreateNumber("-123E", false);
        compareIsNumberWithCreateNumber("-123E+-212", false);
        compareIsNumberWithCreateNumber("-123E2.12", false);
        compareIsNumberWithCreateNumber("0xGF", false);
        compareIsNumberWithCreateNumber("0xFAE-1", false);
        compareIsNumberWithCreateNumber(".", false);
        compareIsNumberWithCreateNumber("-0ABC123", false);
        compareIsNumberWithCreateNumber("123.4E-D", false);
        compareIsNumberWithCreateNumber("123.4ED", false);
        compareIsNumberWithCreateNumber("+000E.12345", false);
        compareIsNumberWithCreateNumber("-000E.12345", false);
        compareIsNumberWithCreateNumber("1234E5l", false);
        compareIsNumberWithCreateNumber("11a", false);
        compareIsNumberWithCreateNumber("1a", false);
        compareIsNumberWithCreateNumber("a", false);
        compareIsNumberWithCreateNumber("11g", false);
        compareIsNumberWithCreateNumber("11z", false);
        compareIsNumberWithCreateNumber("11def", false);
        compareIsNumberWithCreateNumber("11d11", false);
        compareIsNumberWithCreateNumber("11 11", false);
        compareIsNumberWithCreateNumber(" 1111", false);
        compareIsNumberWithCreateNumber("1111 ", false);
        compareIsNumberWithCreateNumber("2.", true);// LANG-521

        compareIsNumberWithCreateNumber("1.1L", false);// LANG-664

    }

    @Test
    public void testIsNumberLANG971() {
        compareIsNumberWithCreateNumber("0085", false);
        compareIsNumberWithCreateNumber("085", false);
        compareIsNumberWithCreateNumber("08", false);
        compareIsNumberWithCreateNumber("07", true);
        compareIsNumberWithCreateNumber("00", true);
    }

    @Test
    public void testIsNumberLANG992() {
        compareIsNumberWithCreateNumber("0.0", true);
        compareIsNumberWithCreateNumber("0.4790", true);
    }

    @Test
    public void testIsNumberLANG972() {
        compareIsNumberWithCreateNumber("0xABCD", true);
        compareIsNumberWithCreateNumber("0XABCD", true);
    }

    @Test
    public void testIsNumberLANG1252() {
        compareIsNumberWithCreateNumber("+2", true);
        compareIsNumberWithCreateNumber("+2.0", true);
    }

    @Test
    public void testIsNumberLANG1385() {
        compareIsNumberWithCreateNumber("L", false);
    }

    @Test
    public void testIsParsable() {
        Assertions.assertFalse(NumberUtils.isParsable(null));
        Assertions.assertFalse(NumberUtils.isParsable(""));
        Assertions.assertFalse(NumberUtils.isParsable("0xC1AB"));
        Assertions.assertFalse(NumberUtils.isParsable("65CBA2"));
        Assertions.assertFalse(NumberUtils.isParsable("pendro"));
        Assertions.assertFalse(NumberUtils.isParsable("64, 2"));
        Assertions.assertFalse(NumberUtils.isParsable("64.2.2"));
        Assertions.assertFalse(NumberUtils.isParsable("64."));
        Assertions.assertFalse(NumberUtils.isParsable("64L"));
        Assertions.assertFalse(NumberUtils.isParsable("-"));
        Assertions.assertFalse(NumberUtils.isParsable("--2"));
        Assertions.assertTrue(NumberUtils.isParsable("64.2"));
        Assertions.assertTrue(NumberUtils.isParsable("64"));
        Assertions.assertTrue(NumberUtils.isParsable("018"));
        Assertions.assertTrue(NumberUtils.isParsable(".18"));
        Assertions.assertTrue(NumberUtils.isParsable("-65"));
        Assertions.assertTrue(NumberUtils.isParsable("-018"));
        Assertions.assertTrue(NumberUtils.isParsable("-018.2"));
        Assertions.assertTrue(NumberUtils.isParsable("-.236"));
    }

    // suppress instanceof warning check
    @SuppressWarnings("cast")
    @Test
    public void testConstants() {
        Assertions.assertTrue(((NumberUtils.LONG_ZERO) instanceof Long));
        Assertions.assertTrue(((NumberUtils.LONG_ONE) instanceof Long));
        Assertions.assertTrue(((NumberUtils.LONG_MINUS_ONE) instanceof Long));
        Assertions.assertTrue(((NumberUtils.INTEGER_ZERO) instanceof Integer));
        Assertions.assertTrue(((NumberUtils.INTEGER_ONE) instanceof Integer));
        Assertions.assertTrue(((NumberUtils.INTEGER_MINUS_ONE) instanceof Integer));
        Assertions.assertTrue(((NumberUtils.SHORT_ZERO) instanceof Short));
        Assertions.assertTrue(((NumberUtils.SHORT_ONE) instanceof Short));
        Assertions.assertTrue(((NumberUtils.SHORT_MINUS_ONE) instanceof Short));
        Assertions.assertTrue(((NumberUtils.BYTE_ZERO) instanceof Byte));
        Assertions.assertTrue(((NumberUtils.BYTE_ONE) instanceof Byte));
        Assertions.assertTrue(((NumberUtils.BYTE_MINUS_ONE) instanceof Byte));
        Assertions.assertTrue(((NumberUtils.DOUBLE_ZERO) instanceof Double));
        Assertions.assertTrue(((NumberUtils.DOUBLE_ONE) instanceof Double));
        Assertions.assertTrue(((NumberUtils.DOUBLE_MINUS_ONE) instanceof Double));
        Assertions.assertTrue(((NumberUtils.FLOAT_ZERO) instanceof Float));
        Assertions.assertTrue(((NumberUtils.FLOAT_ONE) instanceof Float));
        Assertions.assertTrue(((NumberUtils.FLOAT_MINUS_ONE) instanceof Float));
        Assertions.assertEquals(0, NumberUtils.LONG_ZERO.longValue());
        Assertions.assertEquals(1, NumberUtils.LONG_ONE.longValue());
        Assertions.assertEquals(NumberUtils.LONG_MINUS_ONE.longValue(), (-1));
        Assertions.assertEquals(0, NumberUtils.INTEGER_ZERO.intValue());
        Assertions.assertEquals(1, NumberUtils.INTEGER_ONE.intValue());
        Assertions.assertEquals(NumberUtils.INTEGER_MINUS_ONE.intValue(), (-1));
        Assertions.assertEquals(0, NumberUtils.SHORT_ZERO.shortValue());
        Assertions.assertEquals(1, NumberUtils.SHORT_ONE.shortValue());
        Assertions.assertEquals(NumberUtils.SHORT_MINUS_ONE.shortValue(), (-1));
        Assertions.assertEquals(0, NumberUtils.BYTE_ZERO.byteValue());
        Assertions.assertEquals(1, NumberUtils.BYTE_ONE.byteValue());
        Assertions.assertEquals(NumberUtils.BYTE_MINUS_ONE.byteValue(), (-1));
        Assertions.assertEquals(0.0, NumberUtils.DOUBLE_ZERO.doubleValue());
        Assertions.assertEquals(1.0, NumberUtils.DOUBLE_ONE.doubleValue());
        Assertions.assertEquals(NumberUtils.DOUBLE_MINUS_ONE.doubleValue(), (-1.0));
        Assertions.assertEquals(0.0F, NumberUtils.FLOAT_ZERO.floatValue());
        Assertions.assertEquals(1.0F, NumberUtils.FLOAT_ONE.floatValue());
        Assertions.assertEquals(NumberUtils.FLOAT_MINUS_ONE.floatValue(), (-1.0F));
    }

    @Test
    public void testLang300() {
        NumberUtils.createNumber("-1l");
        NumberUtils.createNumber("01l");
        NumberUtils.createNumber("1l");
    }

    @Test
    public void testLang381() {
        Assertions.assertTrue(Double.isNaN(NumberUtils.min(1.2, 2.5, Double.NaN)));
        Assertions.assertTrue(Double.isNaN(NumberUtils.max(1.2, 2.5, Double.NaN)));
        Assertions.assertTrue(Float.isNaN(NumberUtils.min(1.2F, 2.5F, Float.NaN)));
        Assertions.assertTrue(Float.isNaN(NumberUtils.max(1.2F, 2.5F, Float.NaN)));
        final double[] a = new double[]{ 1.2, Double.NaN, 3.7, 27.0, 42.0, Double.NaN };
        Assertions.assertTrue(Double.isNaN(NumberUtils.max(a)));
        Assertions.assertTrue(Double.isNaN(NumberUtils.min(a)));
        final double[] b = new double[]{ Double.NaN, 1.2, Double.NaN, 3.7, 27.0, 42.0, Double.NaN };
        Assertions.assertTrue(Double.isNaN(NumberUtils.max(b)));
        Assertions.assertTrue(Double.isNaN(NumberUtils.min(b)));
        final float[] aF = new float[]{ 1.2F, Float.NaN, 3.7F, 27.0F, 42.0F, Float.NaN };
        Assertions.assertTrue(Float.isNaN(NumberUtils.max(aF)));
        final float[] bF = new float[]{ Float.NaN, 1.2F, Float.NaN, 3.7F, 27.0F, 42.0F, Float.NaN };
        Assertions.assertTrue(Float.isNaN(NumberUtils.max(bF)));
    }

    @Test
    public void compareInt() {
        Assertions.assertTrue(((NumberUtils.compare((-3), 0)) < 0));
        Assertions.assertEquals(0, NumberUtils.compare(113, 113));
        Assertions.assertTrue(((NumberUtils.compare(213, 32)) > 0));
    }

    @Test
    public void compareLong() {
        Assertions.assertTrue(((NumberUtils.compare((-3L), 0L)) < 0));
        Assertions.assertEquals(0, NumberUtils.compare(113L, 113L));
        Assertions.assertTrue(((NumberUtils.compare(213L, 32L)) > 0));
    }

    @Test
    public void compareShort() {
        Assertions.assertTrue(((NumberUtils.compare(((short) (-3)), ((short) (0)))) < 0));
        Assertions.assertEquals(0, NumberUtils.compare(((short) (113)), ((short) (113))));
        Assertions.assertTrue(((NumberUtils.compare(((short) (213)), ((short) (32)))) > 0));
    }

    @Test
    public void compareByte() {
        Assertions.assertTrue(((NumberUtils.compare(((byte) (-3)), ((byte) (0)))) < 0));
        Assertions.assertEquals(0, NumberUtils.compare(((byte) (113)), ((byte) (113))));
        Assertions.assertTrue(((NumberUtils.compare(((byte) (123)), ((byte) (32)))) > 0));
    }
}

