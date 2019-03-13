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
package org.apache.commons.lang3;


import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link Conversion}.
 */
public class ConversionTest {
    /**
     * Tests {@link Conversion#hexDigitToInt(char)}.
     */
    @Test
    public void testHexDigitToInt() {
        Assertions.assertEquals(0, Conversion.hexDigitToInt('0'));
        Assertions.assertEquals(1, Conversion.hexDigitToInt('1'));
        Assertions.assertEquals(2, Conversion.hexDigitToInt('2'));
        Assertions.assertEquals(3, Conversion.hexDigitToInt('3'));
        Assertions.assertEquals(4, Conversion.hexDigitToInt('4'));
        Assertions.assertEquals(5, Conversion.hexDigitToInt('5'));
        Assertions.assertEquals(6, Conversion.hexDigitToInt('6'));
        Assertions.assertEquals(7, Conversion.hexDigitToInt('7'));
        Assertions.assertEquals(8, Conversion.hexDigitToInt('8'));
        Assertions.assertEquals(9, Conversion.hexDigitToInt('9'));
        Assertions.assertEquals(10, Conversion.hexDigitToInt('A'));
        Assertions.assertEquals(10, Conversion.hexDigitToInt('a'));
        Assertions.assertEquals(11, Conversion.hexDigitToInt('B'));
        Assertions.assertEquals(11, Conversion.hexDigitToInt('b'));
        Assertions.assertEquals(12, Conversion.hexDigitToInt('C'));
        Assertions.assertEquals(12, Conversion.hexDigitToInt('c'));
        Assertions.assertEquals(13, Conversion.hexDigitToInt('D'));
        Assertions.assertEquals(13, Conversion.hexDigitToInt('d'));
        Assertions.assertEquals(14, Conversion.hexDigitToInt('E'));
        Assertions.assertEquals(14, Conversion.hexDigitToInt('e'));
        Assertions.assertEquals(15, Conversion.hexDigitToInt('F'));
        Assertions.assertEquals(15, Conversion.hexDigitToInt('f'));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Conversion.hexDigitToInt('G'));
    }

    /**
     * Tests {@link Conversion#hexDigitMsb0ToInt(char)}.
     */
    @Test
    public void testHexDigitMsb0ToInt() {
        Assertions.assertEquals(0, Conversion.hexDigitMsb0ToInt('0'));
        Assertions.assertEquals(8, Conversion.hexDigitMsb0ToInt('1'));
        Assertions.assertEquals(4, Conversion.hexDigitMsb0ToInt('2'));
        Assertions.assertEquals(12, Conversion.hexDigitMsb0ToInt('3'));
        Assertions.assertEquals(2, Conversion.hexDigitMsb0ToInt('4'));
        Assertions.assertEquals(10, Conversion.hexDigitMsb0ToInt('5'));
        Assertions.assertEquals(6, Conversion.hexDigitMsb0ToInt('6'));
        Assertions.assertEquals(14, Conversion.hexDigitMsb0ToInt('7'));
        Assertions.assertEquals(1, Conversion.hexDigitMsb0ToInt('8'));
        Assertions.assertEquals(9, Conversion.hexDigitMsb0ToInt('9'));
        Assertions.assertEquals(5, Conversion.hexDigitMsb0ToInt('A'));
        Assertions.assertEquals(5, Conversion.hexDigitMsb0ToInt('a'));
        Assertions.assertEquals(13, Conversion.hexDigitMsb0ToInt('B'));
        Assertions.assertEquals(13, Conversion.hexDigitMsb0ToInt('b'));
        Assertions.assertEquals(3, Conversion.hexDigitMsb0ToInt('C'));
        Assertions.assertEquals(3, Conversion.hexDigitMsb0ToInt('c'));
        Assertions.assertEquals(11, Conversion.hexDigitMsb0ToInt('D'));
        Assertions.assertEquals(11, Conversion.hexDigitMsb0ToInt('d'));
        Assertions.assertEquals(7, Conversion.hexDigitMsb0ToInt('E'));
        Assertions.assertEquals(7, Conversion.hexDigitMsb0ToInt('e'));
        Assertions.assertEquals(15, Conversion.hexDigitMsb0ToInt('F'));
        Assertions.assertEquals(15, Conversion.hexDigitMsb0ToInt('f'));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Conversion.hexDigitMsb0ToInt('G'));
    }

    /**
     * Tests {@link Conversion#hexDigitToBinary(char)}.
     */
    @Test
    public void testHexDigitToBinary() {
        Assertions.assertArrayEquals(new boolean[]{ false, false, false, false }, Conversion.hexDigitToBinary('0'));
        Assertions.assertArrayEquals(new boolean[]{ true, false, false, false }, Conversion.hexDigitToBinary('1'));
        Assertions.assertArrayEquals(new boolean[]{ false, true, false, false }, Conversion.hexDigitToBinary('2'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, false }, Conversion.hexDigitToBinary('3'));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, false }, Conversion.hexDigitToBinary('4'));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, false }, Conversion.hexDigitToBinary('5'));
        Assertions.assertArrayEquals(new boolean[]{ false, true, true, false }, Conversion.hexDigitToBinary('6'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, false }, Conversion.hexDigitToBinary('7'));
        Assertions.assertArrayEquals(new boolean[]{ false, false, false, true }, Conversion.hexDigitToBinary('8'));
        Assertions.assertArrayEquals(new boolean[]{ true, false, false, true }, Conversion.hexDigitToBinary('9'));
        Assertions.assertArrayEquals(new boolean[]{ false, true, false, true }, Conversion.hexDigitToBinary('A'));
        Assertions.assertArrayEquals(new boolean[]{ false, true, false, true }, Conversion.hexDigitToBinary('a'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, true }, Conversion.hexDigitToBinary('B'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, true }, Conversion.hexDigitToBinary('b'));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, true }, Conversion.hexDigitToBinary('C'));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, true }, Conversion.hexDigitToBinary('c'));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, true }, Conversion.hexDigitToBinary('D'));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, true }, Conversion.hexDigitToBinary('d'));
        Assertions.assertArrayEquals(new boolean[]{ false, true, true, true }, Conversion.hexDigitToBinary('E'));
        Assertions.assertArrayEquals(new boolean[]{ false, true, true, true }, Conversion.hexDigitToBinary('e'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, true }, Conversion.hexDigitToBinary('F'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, true }, Conversion.hexDigitToBinary('f'));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Conversion.hexDigitToBinary('G'));
    }

    /**
     * Tests {@link Conversion#hexDigitMsb0ToBinary(char)}.
     */
    @Test
    public void testHexDigitMsb0ToBinary() {
        Assertions.assertArrayEquals(new boolean[]{ false, false, false, false }, Conversion.hexDigitMsb0ToBinary('0'));
        Assertions.assertArrayEquals(new boolean[]{ false, false, false, true }, Conversion.hexDigitMsb0ToBinary('1'));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, false }, Conversion.hexDigitMsb0ToBinary('2'));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, true }, Conversion.hexDigitMsb0ToBinary('3'));
        Assertions.assertArrayEquals(new boolean[]{ false, true, false, false }, Conversion.hexDigitMsb0ToBinary('4'));
        Assertions.assertArrayEquals(new boolean[]{ false, true, false, true }, Conversion.hexDigitMsb0ToBinary('5'));
        Assertions.assertArrayEquals(new boolean[]{ false, true, true, false }, Conversion.hexDigitMsb0ToBinary('6'));
        Assertions.assertArrayEquals(new boolean[]{ false, true, true, true }, Conversion.hexDigitMsb0ToBinary('7'));
        Assertions.assertArrayEquals(new boolean[]{ true, false, false, false }, Conversion.hexDigitMsb0ToBinary('8'));
        Assertions.assertArrayEquals(new boolean[]{ true, false, false, true }, Conversion.hexDigitMsb0ToBinary('9'));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, false }, Conversion.hexDigitMsb0ToBinary('A'));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, false }, Conversion.hexDigitMsb0ToBinary('a'));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, true }, Conversion.hexDigitMsb0ToBinary('B'));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, true }, Conversion.hexDigitMsb0ToBinary('b'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, false }, Conversion.hexDigitMsb0ToBinary('C'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, false }, Conversion.hexDigitMsb0ToBinary('c'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, true }, Conversion.hexDigitMsb0ToBinary('D'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, true }, Conversion.hexDigitMsb0ToBinary('d'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, false }, Conversion.hexDigitMsb0ToBinary('E'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, false }, Conversion.hexDigitMsb0ToBinary('e'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, true }, Conversion.hexDigitMsb0ToBinary('F'));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, true }, Conversion.hexDigitMsb0ToBinary('f'));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Conversion.hexDigitMsb0ToBinary('G'));
    }

    /**
     * Tests {@link Conversion#binaryToHexDigit(boolean[])}.
     */
    @Test
    public void testBinaryToHexDigit() {
        Assertions.assertEquals('0', Conversion.binaryToHexDigit(new boolean[]{ false, false, false, false }));
        Assertions.assertEquals('1', Conversion.binaryToHexDigit(new boolean[]{ true, false, false, false }));
        Assertions.assertEquals('2', Conversion.binaryToHexDigit(new boolean[]{ false, true, false, false }));
        Assertions.assertEquals('3', Conversion.binaryToHexDigit(new boolean[]{ true, true, false, false }));
        Assertions.assertEquals('4', Conversion.binaryToHexDigit(new boolean[]{ false, false, true, false }));
        Assertions.assertEquals('5', Conversion.binaryToHexDigit(new boolean[]{ true, false, true, false }));
        Assertions.assertEquals('6', Conversion.binaryToHexDigit(new boolean[]{ false, true, true, false }));
        Assertions.assertEquals('7', Conversion.binaryToHexDigit(new boolean[]{ true, true, true, false }));
        Assertions.assertEquals('8', Conversion.binaryToHexDigit(new boolean[]{ false, false, false, true }));
        Assertions.assertEquals('9', Conversion.binaryToHexDigit(new boolean[]{ true, false, false, true }));
        Assertions.assertEquals('a', Conversion.binaryToHexDigit(new boolean[]{ false, true, false, true }));
        Assertions.assertEquals('b', Conversion.binaryToHexDigit(new boolean[]{ true, true, false, true }));
        Assertions.assertEquals('c', Conversion.binaryToHexDigit(new boolean[]{ false, false, true, true }));
        Assertions.assertEquals('d', Conversion.binaryToHexDigit(new boolean[]{ true, false, true, true }));
        Assertions.assertEquals('e', Conversion.binaryToHexDigit(new boolean[]{ false, true, true, true }));
        Assertions.assertEquals('f', Conversion.binaryToHexDigit(new boolean[]{ true, true, true, true }));
        Assertions.assertEquals('1', Conversion.binaryToHexDigit(new boolean[]{ true }));
        Assertions.assertEquals('f', Conversion.binaryToHexDigit(new boolean[]{ true, true, true, true, true }));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Conversion.binaryToHexDigit(new boolean[]{  }));
    }

    /**
     * Tests {@link Conversion#binaryBeMsb0ToHexDigit(boolean[], int)}.
     */
    @Test
    public void testBinaryToHexDigit_2args() {
        final boolean[] shortArray = new boolean[]{ false, true, true };
        Assertions.assertEquals('6', Conversion.binaryToHexDigit(shortArray, 0));
        Assertions.assertEquals('3', Conversion.binaryToHexDigit(shortArray, 1));
        Assertions.assertEquals('1', Conversion.binaryToHexDigit(shortArray, 2));
        final boolean[] longArray = new boolean[]{ true, false, true, false, false, true, true };
        Assertions.assertEquals('5', Conversion.binaryToHexDigit(longArray, 0));
        Assertions.assertEquals('2', Conversion.binaryToHexDigit(longArray, 1));
        Assertions.assertEquals('9', Conversion.binaryToHexDigit(longArray, 2));
        Assertions.assertEquals('c', Conversion.binaryToHexDigit(longArray, 3));
        Assertions.assertEquals('6', Conversion.binaryToHexDigit(longArray, 4));
        Assertions.assertEquals('3', Conversion.binaryToHexDigit(longArray, 5));
        Assertions.assertEquals('1', Conversion.binaryToHexDigit(longArray, 6));
    }

    /**
     * Tests {@link Conversion#binaryToHexDigitMsb0_4bits(boolean[])}.
     */
    @Test
    public void testBinaryToHexDigitMsb0_bits() {
        Assertions.assertEquals('0', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ false, false, false, false }));
        Assertions.assertEquals('1', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ false, false, false, true }));
        Assertions.assertEquals('2', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ false, false, true, false }));
        Assertions.assertEquals('3', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ false, false, true, true }));
        Assertions.assertEquals('4', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ false, true, false, false }));
        Assertions.assertEquals('5', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ false, true, false, true }));
        Assertions.assertEquals('6', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ false, true, true, false }));
        Assertions.assertEquals('7', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ false, true, true, true }));
        Assertions.assertEquals('8', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ true, false, false, false }));
        Assertions.assertEquals('9', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ true, false, false, true }));
        Assertions.assertEquals('a', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ true, false, true, false }));
        Assertions.assertEquals('b', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ true, false, true, true }));
        Assertions.assertEquals('c', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ true, true, false, false }));
        Assertions.assertEquals('d', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ true, true, false, true }));
        Assertions.assertEquals('e', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ true, true, true, false }));
        Assertions.assertEquals('f', Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{ true, true, true, true }));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Conversion.binaryToHexDigitMsb0_4bits(new boolean[]{  }));
    }

    /**
     * Tests {@link Conversion#binaryToHexDigitMsb0_4bits(boolean[], int)}.
     */
    @Test
    public void testBinaryToHexDigitMsb0_4bits_2args() {
        // boolean[] shortArray = new boolean[]{true, true, false};
        // assertEquals('6', Conversion.BinaryToHexDigitMsb0(shortArray, 0));
        // assertEquals('3', Conversion.BinaryToHexDigitMsb0(shortArray, 1));
        // assertEquals('1', Conversion.BinaryToHexDigitMsb0(shortArray, 2));
        final boolean[] shortArray = new boolean[]{ true, true, false, true };
        Assertions.assertEquals('d', Conversion.binaryToHexDigitMsb0_4bits(shortArray, 0));
        final boolean[] longArray = new boolean[]{ true, false, true, false, false, true, true };
        Assertions.assertEquals('a', Conversion.binaryToHexDigitMsb0_4bits(longArray, 0));
        Assertions.assertEquals('4', Conversion.binaryToHexDigitMsb0_4bits(longArray, 1));
        Assertions.assertEquals('9', Conversion.binaryToHexDigitMsb0_4bits(longArray, 2));
        Assertions.assertEquals('3', Conversion.binaryToHexDigitMsb0_4bits(longArray, 3));
        // assertEquals('6', Conversion.BinaryToHexDigitMsb0(longArray, 4));
        // assertEquals('3', Conversion.BinaryToHexDigitMsb0(longArray, 5));
        // assertEquals('1', Conversion.BinaryToHexDigitMsb0(longArray, 6));
        final boolean[] maxLengthArray = new boolean[]{ true, false, true, false, false, true, true, true };
        Assertions.assertEquals('a', Conversion.binaryToHexDigitMsb0_4bits(maxLengthArray, 0));
        Assertions.assertEquals('4', Conversion.binaryToHexDigitMsb0_4bits(maxLengthArray, 1));
        Assertions.assertEquals('9', Conversion.binaryToHexDigitMsb0_4bits(maxLengthArray, 2));
        Assertions.assertEquals('3', Conversion.binaryToHexDigitMsb0_4bits(maxLengthArray, 3));
        Assertions.assertEquals('7', Conversion.binaryToHexDigitMsb0_4bits(maxLengthArray, 4));
        // assertEquals('7', Conversion.BinaryToHexDigitMsb0(longArray, 5));
        // assertEquals('3', Conversion.BinaryToHexDigitMsb0(longArray, 6));
        // assertEquals('1', Conversion.BinaryToHexDigitMsb0(longArray, 7));
        final boolean[] javaDocCheck = new boolean[]{ true, false, false, true, true, false, true, false };
        Assertions.assertEquals('d', Conversion.binaryToHexDigitMsb0_4bits(javaDocCheck, 3));
    }

    /**
     * Tests {@link Conversion#binaryToHexDigit(boolean[])}.
     */
    @Test
    public void testBinaryBeMsb0ToHexDigit() {
        Assertions.assertEquals('0', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ false, false, false, false }));
        Assertions.assertEquals('1', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ false, false, false, true }));
        Assertions.assertEquals('2', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ false, false, true, false }));
        Assertions.assertEquals('3', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ false, false, true, true }));
        Assertions.assertEquals('4', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ false, true, false, false }));
        Assertions.assertEquals('5', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ false, true, false, true }));
        Assertions.assertEquals('6', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ false, true, true, false }));
        Assertions.assertEquals('7', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ false, true, true, true }));
        Assertions.assertEquals('8', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ true, false, false, false }));
        Assertions.assertEquals('9', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ true, false, false, true }));
        Assertions.assertEquals('a', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ true, false, true, false }));
        Assertions.assertEquals('b', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ true, false, true, true }));
        Assertions.assertEquals('c', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ true, true, false, false }));
        Assertions.assertEquals('d', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ true, true, false, true }));
        Assertions.assertEquals('e', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ true, true, true, false }));
        Assertions.assertEquals('f', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ true, true, true, true }));
        Assertions.assertEquals('4', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ true, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false }));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Conversion.binaryBeMsb0ToHexDigit(new boolean[]{  }));
    }

    /**
     * Tests {@link Conversion#binaryToHexDigit(boolean[], int)}.
     */
    @Test
    public void testBinaryBeMsb0ToHexDigit_2args() {
        Assertions.assertEquals('5', Conversion.binaryBeMsb0ToHexDigit(new boolean[]{ true, false, false, false, false, false, false, false, false, false, false, true, false, true, false, false }, 2));
        final boolean[] shortArray = new boolean[]{ true, true, false };
        Assertions.assertEquals('6', Conversion.binaryBeMsb0ToHexDigit(shortArray, 0));
        Assertions.assertEquals('3', Conversion.binaryBeMsb0ToHexDigit(shortArray, 1));
        Assertions.assertEquals('1', Conversion.binaryBeMsb0ToHexDigit(shortArray, 2));
        final boolean[] shortArray2 = new boolean[]{ true, true, true, false, false, true, false, true };
        Assertions.assertEquals('5', Conversion.binaryBeMsb0ToHexDigit(shortArray2, 0));
        Assertions.assertEquals('2', Conversion.binaryBeMsb0ToHexDigit(shortArray2, 1));
        Assertions.assertEquals('9', Conversion.binaryBeMsb0ToHexDigit(shortArray2, 2));
        Assertions.assertEquals('c', Conversion.binaryBeMsb0ToHexDigit(shortArray2, 3));
        Assertions.assertEquals('e', Conversion.binaryBeMsb0ToHexDigit(shortArray2, 4));
        Assertions.assertEquals('7', Conversion.binaryBeMsb0ToHexDigit(shortArray2, 5));
        Assertions.assertEquals('3', Conversion.binaryBeMsb0ToHexDigit(shortArray2, 6));
        Assertions.assertEquals('1', Conversion.binaryBeMsb0ToHexDigit(shortArray2, 7));
        final boolean[] multiBytesArray = new boolean[]{ true, true, false, false, true, false, true, false, true, true, true, false, false, true, false, true };
        Assertions.assertEquals('5', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 0));
        Assertions.assertEquals('2', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 1));
        Assertions.assertEquals('9', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 2));
        Assertions.assertEquals('c', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 3));
        Assertions.assertEquals('e', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 4));
        Assertions.assertEquals('7', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 5));
        Assertions.assertEquals('b', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 6));
        Assertions.assertEquals('5', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 7));
        Assertions.assertEquals('a', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 8));
        Assertions.assertEquals('5', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 9));
        Assertions.assertEquals('2', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 10));
        Assertions.assertEquals('9', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 11));
        Assertions.assertEquals('c', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 12));
        Assertions.assertEquals('6', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 13));
        Assertions.assertEquals('3', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 14));
        Assertions.assertEquals('1', Conversion.binaryBeMsb0ToHexDigit(multiBytesArray, 15));
    }

    /**
     * Tests {@link Conversion#intToHexDigit(int)}.
     */
    @Test
    public void testIntToHexDigit() {
        Assertions.assertEquals('0', Conversion.intToHexDigit(0));
        Assertions.assertEquals('1', Conversion.intToHexDigit(1));
        Assertions.assertEquals('2', Conversion.intToHexDigit(2));
        Assertions.assertEquals('3', Conversion.intToHexDigit(3));
        Assertions.assertEquals('4', Conversion.intToHexDigit(4));
        Assertions.assertEquals('5', Conversion.intToHexDigit(5));
        Assertions.assertEquals('6', Conversion.intToHexDigit(6));
        Assertions.assertEquals('7', Conversion.intToHexDigit(7));
        Assertions.assertEquals('8', Conversion.intToHexDigit(8));
        Assertions.assertEquals('9', Conversion.intToHexDigit(9));
        Assertions.assertEquals('a', Conversion.intToHexDigit(10));
        Assertions.assertEquals('b', Conversion.intToHexDigit(11));
        Assertions.assertEquals('c', Conversion.intToHexDigit(12));
        Assertions.assertEquals('d', Conversion.intToHexDigit(13));
        Assertions.assertEquals('e', Conversion.intToHexDigit(14));
        Assertions.assertEquals('f', Conversion.intToHexDigit(15));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Conversion.intToHexDigit(16));
    }

    /**
     * Tests {@link Conversion#intToHexDigitMsb0(int)}.
     */
    @Test
    public void testIntToHexDigitMsb0() {
        Assertions.assertEquals('0', Conversion.intToHexDigitMsb0(0));
        Assertions.assertEquals('8', Conversion.intToHexDigitMsb0(1));
        Assertions.assertEquals('4', Conversion.intToHexDigitMsb0(2));
        Assertions.assertEquals('c', Conversion.intToHexDigitMsb0(3));
        Assertions.assertEquals('2', Conversion.intToHexDigitMsb0(4));
        Assertions.assertEquals('a', Conversion.intToHexDigitMsb0(5));
        Assertions.assertEquals('6', Conversion.intToHexDigitMsb0(6));
        Assertions.assertEquals('e', Conversion.intToHexDigitMsb0(7));
        Assertions.assertEquals('1', Conversion.intToHexDigitMsb0(8));
        Assertions.assertEquals('9', Conversion.intToHexDigitMsb0(9));
        Assertions.assertEquals('5', Conversion.intToHexDigitMsb0(10));
        Assertions.assertEquals('d', Conversion.intToHexDigitMsb0(11));
        Assertions.assertEquals('3', Conversion.intToHexDigitMsb0(12));
        Assertions.assertEquals('b', Conversion.intToHexDigitMsb0(13));
        Assertions.assertEquals('7', Conversion.intToHexDigitMsb0(14));
        Assertions.assertEquals('f', Conversion.intToHexDigitMsb0(15));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Conversion.intToHexDigitMsb0(16));
    }

    /**
     * Tests {@link Conversion#intArrayToLong(int[], int, long, int, int)}.
     */
    @Test
    public void testIntArrayToLong() {
        final int[] src = new int[]{ -839782207, 252851286, 2013265920 };
        Assertions.assertEquals(0L, Conversion.intArrayToLong(src, 0, 0L, 0, 0));
        Assertions.assertEquals(0L, Conversion.intArrayToLong(src, 1, 0L, 0, 0));
        Assertions.assertEquals(3455185089L, Conversion.intArrayToLong(src, 0, 0L, 0, 1));
        Assertions.assertEquals(1085988007576727745L, Conversion.intArrayToLong(src, 0, 0L, 0, 2));
        Assertions.assertEquals(252851286L, Conversion.intArrayToLong(src, 1, 0L, 0, 1));
        Assertions.assertEquals(1311768467463790320L, Conversion.intArrayToLong(src, 0, 1311768467463790320L, 0, 0));
        Assertions.assertEquals(1311768466880987136L, Conversion.intArrayToLong(src, 2, 1311768467463790320L, 0, 1));
        // assertEquals(0x0F12345678000000L, Conversion.intsToLong(src, 1, 0x123456789ABCDEF0L, 32, 2));
    }

    /**
     * Tests {@link Conversion#shortArrayToLong(short[], int, long, int, int)}.
     */
    @Test
    public void testShortArrayToLong() {
        final short[] src = new short[]{ ((short) (52721)), ((short) (61633)), ((short) (3858)), ((short) (13398)), ((short) (30720)) };
        Assertions.assertEquals(0L, Conversion.shortArrayToLong(src, 0, 0L, 0, 0));
        Assertions.assertEquals(52721L, Conversion.shortArrayToLong(src, 0, 0L, 0, 1));
        Assertions.assertEquals(4039233009L, Conversion.shortArrayToLong(src, 0, 0L, 0, 2));
        Assertions.assertEquals(8646968828776083649L, Conversion.shortArrayToLong(src, 1, 0L, 0, 4));
        Assertions.assertEquals(1311768467463790320L, Conversion.shortArrayToLong(src, 0, 1311768467463790320L, 0, 0));
        Assertions.assertEquals(1311768833995628272L, Conversion.shortArrayToLong(src, 0, 1311768467463790320L, 24, 1));
        Assertions.assertEquals(1311805333745098480L, Conversion.shortArrayToLong(src, 3, 1311768467463790320L, 16, 2));
    }

    /**
     * Tests {@link Conversion#byteArrayToLong(byte[], int, long, int, int)}.
     */
    @Test
    public void testByteArrayToLong() {
        final byte[] src = new byte[]{ ((byte) (205)), ((byte) (241)), ((byte) (240)), ((byte) (193)), ((byte) (15)), ((byte) (18)), ((byte) (52)), ((byte) (86)), ((byte) (120)) };
        Assertions.assertEquals(0L, Conversion.byteArrayToLong(src, 0, 0L, 0, 0));
        Assertions.assertEquals(205L, Conversion.byteArrayToLong(src, 0, 0L, 0, 1));
        Assertions.assertEquals(3253793229L, Conversion.byteArrayToLong(src, 0, 0L, 0, 4));
        Assertions.assertEquals(264368369L, Conversion.byteArrayToLong(src, 1, 0L, 0, 4));
        Assertions.assertEquals(1311768467463790320L, Conversion.byteArrayToLong(src, 0, 1311768467463790320L, 0, 0));
        Assertions.assertEquals(1311768468319428336L, Conversion.byteArrayToLong(src, 0, 1311768467463790320L, 24, 1));
        Assertions.assertEquals(1311768467459299056L, Conversion.byteArrayToLong(src, 7, 1311768467463790320L, 8, 2));
    }

    /**
     * Tests {@link Conversion#shortArrayToInt(short[], int, int, int, int)}.
     */
    @Test
    public void testShortArrayToInt() {
        final short[] src = new short[]{ ((short) (52721)), ((short) (61633)), ((short) (3858)), ((short) (13398)), ((short) (30720)) };
        Assertions.assertEquals(0, Conversion.shortArrayToInt(src, 0, 0, 0, 0));
        Assertions.assertEquals(52721, Conversion.shortArrayToInt(src, 0, 0, 0, 1));
        Assertions.assertEquals(-255734287, Conversion.shortArrayToInt(src, 0, 0, 0, 2));
        Assertions.assertEquals(252899521, Conversion.shortArrayToInt(src, 1, 0, 0, 2));
        Assertions.assertEquals(305419896, Conversion.shortArrayToInt(src, 0, 305419896, 0, 0));
        Assertions.assertEquals(-839821704, Conversion.shortArrayToInt(src, 0, 305419896, 16, 1));
        // assertEquals(0x34567800, Conversion.ShortArrayToInt(src, 3, 0x12345678, 16, 2));
    }

    /**
     * Tests {@link Conversion#byteArrayToInt(byte[], int, int, int, int)}.
     */
    @Test
    public void testByteArrayToInt() {
        final byte[] src = new byte[]{ ((byte) (205)), ((byte) (241)), ((byte) (240)), ((byte) (193)), ((byte) (15)), ((byte) (18)), ((byte) (52)), ((byte) (86)), ((byte) (120)) };
        Assertions.assertEquals(0, Conversion.byteArrayToInt(src, 0, 0, 0, 0));
        Assertions.assertEquals(205, Conversion.byteArrayToInt(src, 0, 0, 0, 1));
        Assertions.assertEquals(-1041174067, Conversion.byteArrayToInt(src, 0, 0, 0, 4));
        Assertions.assertEquals(264368369, Conversion.byteArrayToInt(src, 1, 0, 0, 4));
        Assertions.assertEquals(305419896, Conversion.byteArrayToInt(src, 0, 305419896, 0, 0));
        Assertions.assertEquals(-852208008, Conversion.byteArrayToInt(src, 0, 305419896, 24, 1));
        // assertEquals(0x56341278, Conversion.ByteArrayToInt(src, 5, 0x01234567, 8, 4));
    }

    /**
     * Tests {@link Conversion#byteArrayToShort(byte[], int, short, int, int)}.
     */
    @Test
    public void testByteArrayToShort() {
        final byte[] src = new byte[]{ ((byte) (205)), ((byte) (241)), ((byte) (240)), ((byte) (193)), ((byte) (15)), ((byte) (18)), ((byte) (52)), ((byte) (86)), ((byte) (120)) };
        Assertions.assertEquals(((short) (0)), Conversion.byteArrayToShort(src, 0, ((short) (0)), 0, 0));
        Assertions.assertEquals(((short) (205)), Conversion.byteArrayToShort(src, 0, ((short) (0)), 0, 1));
        Assertions.assertEquals(((short) (61901)), Conversion.byteArrayToShort(src, 0, ((short) (0)), 0, 2));
        Assertions.assertEquals(((short) (61681)), Conversion.byteArrayToShort(src, 1, ((short) (0)), 0, 2));
        Assertions.assertEquals(((short) (4660)), Conversion.byteArrayToShort(src, 0, ((short) (4660)), 0, 0));
        Assertions.assertEquals(((short) (52532)), Conversion.byteArrayToShort(src, 0, ((short) (4660)), 8, 1));
        // assertEquals((short) 0x5678, Conversion.ByteArrayToShort(src, 7, (short) 0x0123, 8,
        // 2));
    }

    /**
     * Tests {@link Conversion#hexToLong(String, int, long, int, int)}.
     */
    @Test
    public void testHexToLong() {
        final String src = "CDF1F0C10F12345678";
        Assertions.assertEquals(0L, Conversion.hexToLong(src, 0, 0L, 0, 0));
        Assertions.assertEquals(12L, Conversion.hexToLong(src, 0, 0L, 0, 1));
        Assertions.assertEquals(470753244L, Conversion.hexToLong(src, 0, 0L, 0, 8));
        Assertions.assertEquals(29422077L, Conversion.hexToLong(src, 1, 0L, 0, 8));
        Assertions.assertEquals(1311768471490322160L, Conversion.hexToLong(src, 0, 1311768471490322160L, 0, 0));
        Assertions.assertEquals(1311768466859810544L, Conversion.hexToLong(src, 15, 1311768471490322160L, 24, 3));
    }

    /**
     * Tests {@link Conversion#hexToInt(String, int, int, int, int)}.
     */
    @Test
    public void testHexToInt() {
        final String src = "CDF1F0C10F12345678";
        Assertions.assertEquals(0, Conversion.hexToInt(src, 0, 0, 0, 0));
        Assertions.assertEquals(12, Conversion.hexToInt(src, 0, 0, 0, 1));
        Assertions.assertEquals(470753244, Conversion.hexToInt(src, 0, 0, 0, 8));
        Assertions.assertEquals(29422077, Conversion.hexToInt(src, 1, 0, 0, 8));
        Assertions.assertEquals(305419897, Conversion.hexToInt(src, 0, 305419897, 0, 0));
        Assertions.assertEquals(-2023467399, Conversion.hexToInt(src, 15, 305419897, 20, 3));
    }

    /**
     * Tests {@link Conversion#hexToShort(String, int, short, int, int)}.
     */
    @Test
    public void testHexToShort() {
        final String src = "CDF1F0C10F12345678";
        Assertions.assertEquals(((short) (0)), Conversion.hexToShort(src, 0, ((short) (0)), 0, 0));
        Assertions.assertEquals(((short) (12)), Conversion.hexToShort(src, 0, ((short) (0)), 0, 1));
        Assertions.assertEquals(((short) (8156)), Conversion.hexToShort(src, 0, ((short) (0)), 0, 4));
        Assertions.assertEquals(((short) (61949)), Conversion.hexToShort(src, 1, ((short) (0)), 0, 4));
        Assertions.assertEquals(((short) (4660)), Conversion.hexToShort(src, 0, ((short) (4660)), 0, 0));
        Assertions.assertEquals(((short) (34660)), Conversion.hexToShort(src, 15, ((short) (4660)), 4, 3));
    }

    /**
     * Tests {@link Conversion#hexToByte(String, int, byte, int, int)}.
     */
    @Test
    public void testHexToByte() {
        final String src = "CDF1F0C10F12345678";
        Assertions.assertEquals(((byte) (0)), Conversion.hexToByte(src, 0, ((byte) (0)), 0, 0));
        Assertions.assertEquals(((byte) (12)), Conversion.hexToByte(src, 0, ((byte) (0)), 0, 1));
        Assertions.assertEquals(((byte) (220)), Conversion.hexToByte(src, 0, ((byte) (0)), 0, 2));
        Assertions.assertEquals(((byte) (253)), Conversion.hexToByte(src, 1, ((byte) (0)), 0, 2));
        Assertions.assertEquals(((byte) (52)), Conversion.hexToByte(src, 0, ((byte) (52)), 0, 0));
        Assertions.assertEquals(((byte) (132)), Conversion.hexToByte(src, 17, ((byte) (52)), 4, 1));
    }

    /**
     * Tests {@link Conversion#binaryToLong(boolean[], int, long, int, int)}.
     */
    @Test
    public void testBinaryToLong() {
        final boolean[] src = new boolean[]{ false, false, true, true, true, false, true, true, true, true, true, true, true, false, false, false, true, true, true, true, false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, true, true, true, true, true, false, false, false, false, true, false, false, true, true, false, false, false, false, true, false, true, false, true, false, false, true, true, false, true, true, true, false, false, false, false, true };
        // conversion of "CDF1F0C10F12345678" by HexToBinary
        Assertions.assertEquals(0L, Conversion.binaryToLong(src, 0, 0L, 0, 0));
        Assertions.assertEquals(12L, Conversion.binaryToLong(src, 0, 0L, 0, (1 * 4)));
        Assertions.assertEquals(470753244L, Conversion.binaryToLong(src, 0, 0L, 0, (8 * 4)));
        Assertions.assertEquals(29422077L, Conversion.binaryToLong(src, (1 * 4), 0L, 0, (8 * 4)));
        Assertions.assertEquals(1311768471490322160L, Conversion.binaryToLong(src, 0, 1311768471490322160L, 0, 0));
        Assertions.assertEquals(1311768466859810544L, Conversion.binaryToLong(src, (15 * 4), 1311768471490322160L, 24, (3 * 4)));
    }

    /**
     * Tests {@link Conversion#binaryToInt(boolean[], int, int, int, int)}.
     */
    @Test
    public void testBinaryToInt() {
        final boolean[] src = new boolean[]{ false, false, true, true, true, false, true, true, true, true, true, true, true, false, false, false, true, true, true, true, false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, true, true, true, true, true, false, false, false, false, true, false, false, true, true, false, false, false, false, true, false, true, false, true, false, false, true, true, false, true, true, true, false, false, false, false, true };
        // conversion of "CDF1F0C10F12345678" by HexToBinary
        Assertions.assertEquals(0, Conversion.binaryToInt(src, (0 * 4), 0, 0, (0 * 4)));
        Assertions.assertEquals(12, Conversion.binaryToInt(src, (0 * 4), 0, 0, (1 * 4)));
        Assertions.assertEquals(470753244, Conversion.binaryToInt(src, (0 * 4), 0, 0, (8 * 4)));
        Assertions.assertEquals(29422077, Conversion.binaryToInt(src, (1 * 4), 0, 0, (8 * 4)));
        Assertions.assertEquals(305419897, Conversion.binaryToInt(src, (0 * 4), 305419897, 0, (0 * 4)));
        Assertions.assertEquals(-2023467399, Conversion.binaryToInt(src, (15 * 4), 305419897, 20, (3 * 4)));
    }

    /**
     * Tests {@link Conversion#binaryToShort(boolean[], int, short, int, int)}.
     */
    @Test
    public void testBinaryToShort() {
        final boolean[] src = new boolean[]{ false, false, true, true, true, false, true, true, true, true, true, true, true, false, false, false, true, true, true, true, false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, true, true, true, true, true, false, false, false, false, true, false, false, true, true, false, false, false, false, true, false, true, false, true, false, false, true, true, false, true, true, true, false, false, false, false, true };
        // conversion of "CDF1F0C10F12345678" by HexToBinary
        Assertions.assertEquals(((short) (0)), Conversion.binaryToShort(src, (0 * 4), ((short) (0)), 0, (0 * 4)));
        Assertions.assertEquals(((short) (12)), Conversion.binaryToShort(src, (0 * 4), ((short) (0)), 0, (1 * 4)));
        Assertions.assertEquals(((short) (8156)), Conversion.binaryToShort(src, (0 * 4), ((short) (0)), 0, (4 * 4)));
        Assertions.assertEquals(((short) (61949)), Conversion.binaryToShort(src, (1 * 4), ((short) (0)), 0, (4 * 4)));
        Assertions.assertEquals(((short) (4660)), Conversion.binaryToShort(src, (0 * 4), ((short) (4660)), 0, (0 * 4)));
        Assertions.assertEquals(((short) (34660)), Conversion.binaryToShort(src, (15 * 4), ((short) (4660)), 4, (3 * 4)));
    }

    /**
     * Tests {@link Conversion#binaryToByte(boolean[], int, byte, int, int)}.
     */
    @Test
    public void testBinaryToByte() {
        final boolean[] src = new boolean[]{ false, false, true, true, true, false, true, true, true, true, true, true, true, false, false, false, true, true, true, true, false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, true, true, true, true, true, false, false, false, false, true, false, false, true, true, false, false, false, false, true, false, true, false, true, false, false, true, true, false, true, true, true, false, false, false, false, true };
        // conversion of "CDF1F0C10F12345678" by HexToBinary
        Assertions.assertEquals(((byte) (0)), Conversion.binaryToByte(src, (0 * 4), ((byte) (0)), 0, (0 * 4)));
        Assertions.assertEquals(((byte) (12)), Conversion.binaryToByte(src, (0 * 4), ((byte) (0)), 0, (1 * 4)));
        Assertions.assertEquals(((byte) (220)), Conversion.binaryToByte(src, (0 * 4), ((byte) (0)), 0, (2 * 4)));
        Assertions.assertEquals(((byte) (253)), Conversion.binaryToByte(src, (1 * 4), ((byte) (0)), 0, (2 * 4)));
        Assertions.assertEquals(((byte) (52)), Conversion.binaryToByte(src, (0 * 4), ((byte) (52)), 0, (0 * 4)));
        Assertions.assertEquals(((byte) (132)), Conversion.binaryToByte(src, (17 * 4), ((byte) (52)), 4, (1 * 4)));
    }

    /**
     * Tests {@link Conversion#longToIntArray(long, int, int[], int, int)}.
     */
    @Test
    public void testLongToIntArray() {
        Assertions.assertArrayEquals(new int[]{  }, Conversion.longToIntArray(0L, 0, new int[]{  }, 0, 0));
        Assertions.assertArrayEquals(new int[]{  }, Conversion.longToIntArray(0L, 100, new int[]{  }, 0, 0));
        Assertions.assertArrayEquals(new int[]{  }, Conversion.longToIntArray(0L, 0, new int[]{  }, 100, 0));
        Assertions.assertArrayEquals(new int[]{ -1, -1, -1, -1 }, Conversion.longToIntArray(1311768467294899695L, 0, new int[]{ -1, -1, -1, -1 }, 0, 0));
        Assertions.assertArrayEquals(new int[]{ -1867788817, -1, -1, -1 }, Conversion.longToIntArray(1311768467294899695L, 0, new int[]{ -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new int[]{ -1867788817, 305419896, -1, -1 }, Conversion.longToIntArray(1311768467294899695L, 0, new int[]{ -1, -1, -1, -1 }, 0, 2));
        // assertArrayEquals(new
        // int[]{0x90ABCDEF, 0x12345678, 0x90ABCDEF, 0x12345678}, Conversion.longToIntArray(0x1234567890ABCDEFL,
        // 0, new int[]{-1, -1, -1, -1}, 0, 4));//rejected by assertion
        // assertArrayEquals(new
        // int[]{0xFFFFFFFF, 0x90ABCDEF, 0x12345678, 0x90ABCDEF}, Conversion.longToIntArray(0x1234567890ABCDEFL,
        // 0, new int[]{-1, -1, -1, -1}, 1, 3));
        Assertions.assertArrayEquals(new int[]{ -1, -1, -1867788817, 305419896 }, Conversion.longToIntArray(1311768467294899695L, 0, new int[]{ -1, -1, -1, -1 }, 2, 2));
        Assertions.assertArrayEquals(new int[]{ -1, -1, -1867788817, -1 }, Conversion.longToIntArray(1311768467294899695L, 0, new int[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new int[]{ -1, -1, -1, -1867788817 }, Conversion.longToIntArray(1311768467294899695L, 0, new int[]{ -1, -1, -1, -1 }, 3, 1));
        Assertions.assertArrayEquals(new int[]{ -1, -1, 1213589239, -1 }, Conversion.longToIntArray(1311768467294899695L, 1, new int[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new int[]{ -1, -1, 606794619, -1 }, Conversion.longToIntArray(1311768467294899695L, 2, new int[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new int[]{ -1, -1, 303397309, -1 }, Conversion.longToIntArray(1311768467294899695L, 3, new int[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new int[]{ -1, -1, -1995784994, -1 }, Conversion.longToIntArray(1311768467294899695L, 4, new int[]{ -1, -1, -1, -1 }, 2, 1));
        // assertArrayEquals(new
        // int[]{0x4855E6F7, 0x091A2B3C, 0x4855E6F7, 0x091A2B3C}, Conversion.longToIntArray(0x1234567890ABCDEFL,
        // 1, new int[]{-1, -1, -1, -1}, 0, 4));//rejected by assertion
        Assertions.assertArrayEquals(new int[]{ 152709948 }, Conversion.longToIntArray(1311768467294899695L, 33, new int[]{ 0 }, 0, 1));
    }

    /**
     * Tests {@link Conversion#longToShortArray(long, int, short[], int, int)}.
     */
    @Test
    public void testLongToShortArray() {
        Assertions.assertArrayEquals(new short[]{  }, Conversion.longToShortArray(0L, 0, new short[]{  }, 0, 0));
        Assertions.assertArrayEquals(new short[]{  }, Conversion.longToShortArray(0L, 100, new short[]{  }, 0, 0));
        Assertions.assertArrayEquals(new short[]{  }, Conversion.longToShortArray(0L, 0, new short[]{  }, 100, 0));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (65535)), ((short) (65535)) }, Conversion.longToShortArray(1311768467294899695L, 0, new short[]{ -1, -1, -1, -1 }, 0, 0));
        Assertions.assertArrayEquals(new short[]{ ((short) (52719)), ((short) (65535)), ((short) (65535)), ((short) (65535)) }, Conversion.longToShortArray(1311768467294899695L, 0, new short[]{ -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (52719)), ((short) (37035)), ((short) (65535)), ((short) (65535)) }, Conversion.longToShortArray(1311768467294899695L, 0, new short[]{ -1, -1, -1, -1 }, 0, 2));
        Assertions.assertArrayEquals(new short[]{ ((short) (52719)), ((short) (37035)), ((short) (22136)), ((short) (65535)) }, Conversion.longToShortArray(1311768467294899695L, 0, new short[]{ -1, -1, -1, -1 }, 0, 3));
        Assertions.assertArrayEquals(new short[]{ ((short) (52719)), ((short) (37035)), ((short) (22136)), ((short) (4660)) }, Conversion.longToShortArray(1311768467294899695L, 0, new short[]{ -1, -1, -1, -1 }, 0, 4));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (52719)), ((short) (37035)), ((short) (22136)) }, Conversion.longToShortArray(1311768467294899695L, 0, new short[]{ -1, -1, -1, -1 }, 1, 3));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (52719)), ((short) (37035)) }, Conversion.longToShortArray(1311768467294899695L, 0, new short[]{ -1, -1, -1, -1 }, 2, 2));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (52719)), ((short) (65535)) }, Conversion.longToShortArray(1311768467294899695L, 0, new short[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (65535)), ((short) (52719)) }, Conversion.longToShortArray(1311768467294899695L, 0, new short[]{ -1, -1, -1, -1 }, 3, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (59127)), ((short) (65535)) }, Conversion.longToShortArray(1311768467294899695L, 1, new short[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (62331)), ((short) (65535)) }, Conversion.longToShortArray(1311768467294899695L, 2, new short[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (31165)), ((short) (65535)) }, Conversion.longToShortArray(1311768467294899695L, 3, new short[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (48350)), ((short) (65535)) }, Conversion.longToShortArray(1311768467294899695L, 4, new short[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (59127)), ((short) (18517)), ((short) (11068)), ((short) (2330)) }, Conversion.longToShortArray(1311768467294899695L, 1, new short[]{ -1, -1, -1, -1 }, 0, 4));
        Assertions.assertArrayEquals(new short[]{ ((short) (11068)) }, Conversion.longToShortArray(1311768467294899695L, 33, new short[]{ 0 }, 0, 1));
    }

    /**
     * Tests {@link Conversion#intToShortArray(int, int, short[], int, int)}.
     */
    @Test
    public void testIntToShortArray() {
        Assertions.assertArrayEquals(new short[]{  }, Conversion.intToShortArray(0, 0, new short[]{  }, 0, 0));
        Assertions.assertArrayEquals(new short[]{  }, Conversion.intToShortArray(0, 100, new short[]{  }, 0, 0));
        Assertions.assertArrayEquals(new short[]{  }, Conversion.intToShortArray(0, 0, new short[]{  }, 100, 0));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (65535)), ((short) (65535)) }, Conversion.intToShortArray(305419896, 0, new short[]{ -1, -1, -1, -1 }, 0, 0));
        Assertions.assertArrayEquals(new short[]{ ((short) (22136)), ((short) (65535)), ((short) (65535)), ((short) (65535)) }, Conversion.intToShortArray(305419896, 0, new short[]{ -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (22136)), ((short) (4660)), ((short) (65535)), ((short) (65535)) }, Conversion.intToShortArray(305419896, 0, new short[]{ -1, -1, -1, -1 }, 0, 2));
        // assertArrayEquals(new
        // short[]{(short) 0x5678, (short) 0x1234, (short) 0x5678, (short) 0xFFFF}, Conversion.intToShortArray(0x12345678,
        // 0, new short[]{-1, -1, -1, -1}, 0, 3));//rejected by assertion
        // assertArrayEquals(new
        // short[]{(short) 0x5678, (short) 0x1234, (short) 0x5678, (short) 0x1234}, Conversion.intToShortArray(0x12345678,
        // 0, new short[]{-1, -1, -1, -1}, 0, 4));
        // assertArrayEquals(new
        // short[]{(short) 0xFFFF, (short) 0x5678, (short) 0x1234, (short) 0x5678}, Conversion.intToShortArray(0x12345678,
        // 0, new short[]{-1, -1, -1, -1}, 1, 3));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (22136)), ((short) (4660)) }, Conversion.intToShortArray(305419896, 0, new short[]{ -1, -1, -1, -1 }, 2, 2));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (22136)), ((short) (65535)) }, Conversion.intToShortArray(305419896, 0, new short[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (65535)), ((short) (22136)) }, Conversion.intToShortArray(305419896, 0, new short[]{ -1, -1, -1, -1 }, 3, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (11068)), ((short) (65535)) }, Conversion.intToShortArray(305419896, 1, new short[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (5534)), ((short) (65535)) }, Conversion.intToShortArray(305419896, 2, new short[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (35535)), ((short) (65535)) }, Conversion.intToShortArray(305419896, 3, new short[]{ -1, -1, -1, -1 }, 2, 1));
        Assertions.assertArrayEquals(new short[]{ ((short) (65535)), ((short) (65535)), ((short) (17767)), ((short) (65535)) }, Conversion.intToShortArray(305419896, 4, new short[]{ -1, -1, -1, -1 }, 2, 1));
        // assertArrayEquals(new
        // short[]{(short) 0xE6F7, (short) 0x4855, (short) 0x2B3C, (short) 0x091A}, Conversion.intToShortArray(0x12345678,
        // 1, new short[]{-1, -1, -1, -1}, 0, 4));//rejected by assertion
        // assertArrayEquals(new
        // short[]{(short) 0x2B3C}, Conversion.intToShortArray(0x12345678, 33, new
        // short[]{0}, 0, 1));//rejected by assertion
        Assertions.assertArrayEquals(new short[]{ ((short) (2330)) }, Conversion.intToShortArray(305419896, 17, new short[]{ 0 }, 0, 1));
    }

    /**
     * Tests {@link Conversion#longToByteArray(long, int, byte[], int, int)}.
     */
    @Test
    public void testLongToByteArray() {
        Assertions.assertArrayEquals(new byte[]{  }, Conversion.longToByteArray(0L, 0, new byte[]{  }, 0, 0));
        Assertions.assertArrayEquals(new byte[]{  }, Conversion.longToByteArray(0L, 100, new byte[]{  }, 0, 0));
        Assertions.assertArrayEquals(new byte[]{  }, Conversion.longToByteArray(0L, 0, new byte[]{  }, 100, 0));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 0));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (239)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (239)), ((byte) (205)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 2));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (239)), ((byte) (205)), ((byte) (171)), ((byte) (144)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 4));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (239)), ((byte) (205)), ((byte) (171)), ((byte) (144)), ((byte) (120)), ((byte) (86)), ((byte) (52)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 7));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (239)), ((byte) (205)), ((byte) (171)), ((byte) (144)), ((byte) (120)), ((byte) (86)), ((byte) (52)), ((byte) (18)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 8));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (239)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (239)), ((byte) (205)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 2));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (239)), ((byte) (205)), ((byte) (171)), ((byte) (144)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 4));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (239)), ((byte) (205)), ((byte) (171)), ((byte) (144)), ((byte) (120)), ((byte) (86)), ((byte) (52)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 7));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (239)), ((byte) (205)), ((byte) (171)), ((byte) (144)), ((byte) (120)), ((byte) (86)), ((byte) (52)), ((byte) (18)) }, Conversion.longToByteArray(1311768467294899695L, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 8));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (247)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 1, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (123)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 2, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (0)), ((byte) (255)), ((byte) (111)), ((byte) (94)), ((byte) (133)), ((byte) (196)), ((byte) (179)), ((byte) (162)), ((byte) (145)), ((byte) (0)) }, Conversion.longToByteArray(1311768467294899695L, 5, new byte[]{ -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 8));
        // assertArrayEquals(new
        // byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0xFF, (byte) 0x5E, (byte) 0x85, (byte) 0xC4, (byte) 0xB3, (byte) 0xA2, (byte) 0x91, (byte) 0x00, (byte) 0x00}, Conversion.longToByteArray(0x1234567890ABCDEFL, 13, new
        // byte[]{-1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1}, 3, 8));//rejected by assertion
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (0)), ((byte) (255)), ((byte) (94)), ((byte) (133)), ((byte) (196)), ((byte) (179)), ((byte) (162)), ((byte) (145)), ((byte) (0)), ((byte) (255)) }, Conversion.longToByteArray(1311768467294899695L, 13, new byte[]{ -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 7));
    }

    /**
     * Tests {@link Conversion#intToByteArray(int, int, byte[], int, int)}.
     */
    @Test
    public void testIntToByteArray() {
        Assertions.assertArrayEquals(new byte[]{  }, Conversion.intToByteArray(0, 0, new byte[]{  }, 0, 0));
        Assertions.assertArrayEquals(new byte[]{  }, Conversion.intToByteArray(0, 100, new byte[]{  }, 0, 0));
        Assertions.assertArrayEquals(new byte[]{  }, Conversion.intToByteArray(0, 0, new byte[]{  }, 100, 0));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.intToByteArray(-1867788817, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 0));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (239)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.intToByteArray(-1867788817, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (239)), ((byte) (205)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.intToByteArray(-1867788817, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 2));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (239)), ((byte) (205)), ((byte) (171)), ((byte) (144)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.intToByteArray(-1867788817, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 4));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (239)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.intToByteArray(-1867788817, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (239)), ((byte) (205)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.intToByteArray(-1867788817, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 2));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (239)), ((byte) (205)), ((byte) (171)), ((byte) (144)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.intToByteArray(-1867788817, 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 4));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (247)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.intToByteArray(-1867788817, 1, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (123)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.intToByteArray(-1867788817, 2, new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (0)), ((byte) (255)), ((byte) (111)), ((byte) (94)), ((byte) (133)), ((byte) (252)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.intToByteArray(-1867788817, 5, new byte[]{ -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 4));
        // assertArrayEquals(new
        // byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0xFF, (byte) 0x5E, (byte) 0x85, (byte) 0xFC, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, Conversion.intToByteArray(0x90ABCDEF, 13, new
        // byte[]{-1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1}, 3, 4));//rejected by assertion
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (0)), ((byte) (255)), ((byte) (94)), ((byte) (133)), ((byte) (252)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.intToByteArray(-1867788817, 13, new byte[]{ -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 3, 3));
    }

    /**
     * Tests {@link Conversion#shortToByteArray(short, int, byte[], int, int)}.
     */
    @Test
    public void testShortToByteArray() {
        Assertions.assertArrayEquals(new byte[]{  }, Conversion.shortToByteArray(((short) (0)), 0, new byte[]{  }, 0, 0));
        Assertions.assertArrayEquals(new byte[]{  }, Conversion.shortToByteArray(((short) (0)), 100, new byte[]{  }, 0, 0));
        Assertions.assertArrayEquals(new byte[]{  }, Conversion.shortToByteArray(((short) (0)), 0, new byte[]{  }, 100, 0));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.shortToByteArray(((short) (52719)), 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1 }, 0, 0));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (239)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.shortToByteArray(((short) (52719)), 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (239)), ((byte) (205)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.shortToByteArray(((short) (52719)), 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1 }, 0, 2));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (239)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.shortToByteArray(((short) (52719)), 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1 }, 3, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (239)), ((byte) (205)), ((byte) (255)), ((byte) (255)) }, Conversion.shortToByteArray(((short) (52719)), 0, new byte[]{ -1, -1, -1, -1, -1, -1, -1 }, 3, 2));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (247)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.shortToByteArray(((short) (52719)), 1, new byte[]{ -1, -1, -1, -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (123)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.shortToByteArray(((short) (52719)), 2, new byte[]{ -1, -1, -1, -1, -1, -1, -1 }, 0, 1));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (0)), ((byte) (255)), ((byte) (111)), ((byte) (254)), ((byte) (255)), ((byte) (255)) }, Conversion.shortToByteArray(((short) (52719)), 5, new byte[]{ -1, 0, -1, -1, -1, -1, -1 }, 3, 2));
        // assertArrayEquals(new
        // byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0xFF, (byte) 0x5E, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, Conversion.shortToByteArray((short) 0xCDEF, 13, new
        // byte[]{-1, 0, -1, -1, -1, -1, -1}, 3, 2));//rejected by assertion
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (0)), ((byte) (255)), ((byte) (254)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.shortToByteArray(((short) (52719)), 13, new byte[]{ -1, 0, -1, -1, -1, -1, -1 }, 3, 1));
    }

    /**
     * Tests {@link Conversion#longToHex(long, int, String, int, int)}.
     */
    @Test
    public void testLongToHex() {
        Assertions.assertEquals("", Conversion.longToHex(0L, 0, "", 0, 0));
        Assertions.assertEquals("", Conversion.longToHex(0L, 100, "", 0, 0));
        Assertions.assertEquals("", Conversion.longToHex(0L, 0, "", 100, 0));
        Assertions.assertEquals("ffffffffffffffffffffffff", Conversion.longToHex(1311768467294899695L, 0, "ffffffffffffffffffffffff", 0, 0));
        Assertions.assertEquals("3fffffffffffffffffffffff", Conversion.longToHex(1311768467294899683L, 0, "ffffffffffffffffffffffff", 0, 1));
        Assertions.assertEquals("feffffffffffffffffffffff", Conversion.longToHex(1311768467294899695L, 0, "ffffffffffffffffffffffff", 0, 2));
        Assertions.assertEquals("fedcffffffffffffffffffff", Conversion.longToHex(1311768467294899695L, 0, "ffffffffffffffffffffffff", 0, 4));
        Assertions.assertEquals("fedcba098765432fffffffff", Conversion.longToHex(1311768467294899695L, 0, "ffffffffffffffffffffffff", 0, 15));
        Assertions.assertEquals("fedcba0987654321ffffffff", Conversion.longToHex(1311768467294899695L, 0, "ffffffffffffffffffffffff", 0, 16));
        Assertions.assertEquals("fff3ffffffffffffffffffff", Conversion.longToHex(1311768467294899683L, 0, "ffffffffffffffffffffffff", 3, 1));
        Assertions.assertEquals("ffffefffffffffffffffffff", Conversion.longToHex(1311768467294899695L, 0, "ffffffffffffffffffffffff", 3, 2));
        Assertions.assertEquals("ffffedcfffffffffffffffff", Conversion.longToHex(1311768467294899695L, 0, "ffffffffffffffffffffffff", 3, 4));
        Assertions.assertEquals("ffffedcba098765432ffffff", Conversion.longToHex(1311768467294899695L, 0, "ffffffffffffffffffffffff", 3, 15));
        Assertions.assertEquals("ffffedcba0987654321fffff", Conversion.longToHex(1311768467294899695L, 0, "ffffffffffffffffffffffff", 3, 16));
        Assertions.assertEquals("7fffffffffffffffffffffff", Conversion.longToHex(1311768467294899695L, 1, "ffffffffffffffffffffffff", 0, 1));
        Assertions.assertEquals("bfffffffffffffffffffffff", Conversion.longToHex(1311768467294899695L, 2, "ffffffffffffffffffffffff", 0, 1));
        Assertions.assertEquals("fffdb975121fca86420fffff", Conversion.longToHex(1311768467294899695L, 3, "ffffffffffffffffffffffff", 3, 16));
        // assertEquals("ffffffffffffffffffffffff", Conversion.longToHex(0x1234567890ABCDEFL, 4, "ffffffffffffffffffffffff", 3, 16));//rejected
        // by assertion
        Assertions.assertEquals("fffedcba0987654321ffffff", Conversion.longToHex(1311768467294899695L, 4, "ffffffffffffffffffffffff", 3, 15));
        Assertions.assertEquals("fedcba0987654321", Conversion.longToHex(1311768467294899695L, 0, "", 0, 16));
        Assertions.assertThrows(StringIndexOutOfBoundsException.class, () -> Conversion.longToHex(1311768467294899695L, 0, "", 1, 8));
    }

    /**
     * Tests {@link Conversion#intToHex(int, int, String, int, int)}.
     */
    @Test
    public void testIntToHex() {
        Assertions.assertEquals("", Conversion.intToHex(0, 0, "", 0, 0));
        Assertions.assertEquals("", Conversion.intToHex(0, 100, "", 0, 0));
        Assertions.assertEquals("", Conversion.intToHex(0, 0, "", 100, 0));
        Assertions.assertEquals("ffffffffffffffffffffffff", Conversion.intToHex(-1867788817, 0, "ffffffffffffffffffffffff", 0, 0));
        Assertions.assertEquals("3fffffffffffffffffffffff", Conversion.intToHex(-1867788829, 0, "ffffffffffffffffffffffff", 0, 1));
        Assertions.assertEquals("feffffffffffffffffffffff", Conversion.intToHex(-1867788817, 0, "ffffffffffffffffffffffff", 0, 2));
        Assertions.assertEquals("fedcffffffffffffffffffff", Conversion.intToHex(-1867788817, 0, "ffffffffffffffffffffffff", 0, 4));
        Assertions.assertEquals("fedcba0fffffffffffffffff", Conversion.intToHex(-1867788817, 0, "ffffffffffffffffffffffff", 0, 7));
        Assertions.assertEquals("fedcba09ffffffffffffffff", Conversion.intToHex(-1867788817, 0, "ffffffffffffffffffffffff", 0, 8));
        Assertions.assertEquals("fff3ffffffffffffffffffff", Conversion.intToHex(-1867788829, 0, "ffffffffffffffffffffffff", 3, 1));
        Assertions.assertEquals("ffffefffffffffffffffffff", Conversion.intToHex(-1867788817, 0, "ffffffffffffffffffffffff", 3, 2));
        Assertions.assertEquals("ffffedcfffffffffffffffff", Conversion.intToHex(-1867788817, 0, "ffffffffffffffffffffffff", 3, 4));
        Assertions.assertEquals("ffffedcba0ffffffffffffff", Conversion.intToHex(-1867788817, 0, "ffffffffffffffffffffffff", 3, 7));
        Assertions.assertEquals("ffffedcba09fffffffffffff", Conversion.intToHex(-1867788817, 0, "ffffffffffffffffffffffff", 3, 8));
        Assertions.assertEquals("7fffffffffffffffffffffff", Conversion.intToHex(-1867788817, 1, "ffffffffffffffffffffffff", 0, 1));
        Assertions.assertEquals("bfffffffffffffffffffffff", Conversion.intToHex(-1867788817, 2, "ffffffffffffffffffffffff", 0, 1));
        Assertions.assertEquals("fffdb97512ffffffffffffff", Conversion.intToHex(-1867788817, 3, "ffffffffffffffffffffffff", 3, 8));
        // assertEquals("ffffffffffffffffffffffff", Conversion.intToHex(0x90ABCDEF,
        // 4, "ffffffffffffffffffffffff", 3, 8));//rejected by assertion
        Assertions.assertEquals("fffedcba09ffffffffffffff", Conversion.intToHex(-1867788817, 4, "ffffffffffffffffffffffff", 3, 7));
        Assertions.assertEquals("fedcba09", Conversion.intToHex(-1867788817, 0, "", 0, 8));
        Assertions.assertThrows(StringIndexOutOfBoundsException.class, () -> Conversion.intToHex(-1867788817, 0, "", 1, 8));
    }

    /**
     * Tests {@link Conversion#shortToHex(short, int, String, int, int)}.
     */
    @Test
    public void testShortToHex() {
        Assertions.assertEquals("", Conversion.shortToHex(((short) (0)), 0, "", 0, 0));
        Assertions.assertEquals("", Conversion.shortToHex(((short) (0)), 100, "", 0, 0));
        Assertions.assertEquals("", Conversion.shortToHex(((short) (0)), 0, "", 100, 0));
        Assertions.assertEquals("ffffffffffffffffffffffff", Conversion.shortToHex(((short) (52719)), 0, "ffffffffffffffffffffffff", 0, 0));
        Assertions.assertEquals("3fffffffffffffffffffffff", Conversion.shortToHex(((short) (52707)), 0, "ffffffffffffffffffffffff", 0, 1));
        Assertions.assertEquals("feffffffffffffffffffffff", Conversion.shortToHex(((short) (52719)), 0, "ffffffffffffffffffffffff", 0, 2));
        Assertions.assertEquals("fedfffffffffffffffffffff", Conversion.shortToHex(((short) (52719)), 0, "ffffffffffffffffffffffff", 0, 3));
        Assertions.assertEquals("fedcffffffffffffffffffff", Conversion.shortToHex(((short) (52719)), 0, "ffffffffffffffffffffffff", 0, 4));
        Assertions.assertEquals("fff3ffffffffffffffffffff", Conversion.shortToHex(((short) (52707)), 0, "ffffffffffffffffffffffff", 3, 1));
        Assertions.assertEquals("ffffefffffffffffffffffff", Conversion.shortToHex(((short) (52719)), 0, "ffffffffffffffffffffffff", 3, 2));
        Assertions.assertEquals("7fffffffffffffffffffffff", Conversion.shortToHex(((short) (52719)), 1, "ffffffffffffffffffffffff", 0, 1));
        Assertions.assertEquals("bfffffffffffffffffffffff", Conversion.shortToHex(((short) (52719)), 2, "ffffffffffffffffffffffff", 0, 1));
        Assertions.assertEquals("fffdb9ffffffffffffffffff", Conversion.shortToHex(((short) (52719)), 3, "ffffffffffffffffffffffff", 3, 4));
        // assertEquals("ffffffffffffffffffffffff", Conversion.shortToHex((short) 0xCDEF,
        // 4, "ffffffffffffffffffffffff", 3, 4));//rejected by assertion
        Assertions.assertEquals("fffedcffffffffffffffffff", Conversion.shortToHex(((short) (52719)), 4, "ffffffffffffffffffffffff", 3, 3));
        Assertions.assertEquals("fedc", Conversion.shortToHex(((short) (52719)), 0, "", 0, 4));
        Assertions.assertThrows(StringIndexOutOfBoundsException.class, () -> Conversion.shortToHex(((short) (52719)), 0, "", 1, 4));
    }

    /**
     * Tests {@link Conversion#byteToHex(byte, int, String, int, int)}.
     */
    @Test
    public void testByteToHex() {
        Assertions.assertEquals("", Conversion.byteToHex(((byte) (0)), 0, "", 0, 0));
        Assertions.assertEquals("", Conversion.byteToHex(((byte) (0)), 100, "", 0, 0));
        Assertions.assertEquals("", Conversion.byteToHex(((byte) (0)), 0, "", 100, 0));
        Assertions.assertEquals("00000", Conversion.byteToHex(((byte) (239)), 0, "00000", 0, 0));
        Assertions.assertEquals("f0000", Conversion.byteToHex(((byte) (239)), 0, "00000", 0, 1));
        Assertions.assertEquals("fe000", Conversion.byteToHex(((byte) (239)), 0, "00000", 0, 2));
        Assertions.assertEquals("000f0", Conversion.byteToHex(((byte) (239)), 0, "00000", 3, 1));
        Assertions.assertEquals("000fe", Conversion.byteToHex(((byte) (239)), 0, "00000", 3, 2));
        Assertions.assertEquals("70000", Conversion.byteToHex(((byte) (239)), 1, "00000", 0, 1));
        Assertions.assertEquals("b0000", Conversion.byteToHex(((byte) (239)), 2, "00000", 0, 1));
        Assertions.assertEquals("000df", Conversion.byteToHex(((byte) (239)), 3, "00000", 3, 2));
        // assertEquals("00000", Conversion.byteToHex((byte) 0xEF, 4, "00000", 3, 2));//rejected by
        // assertion
        Assertions.assertEquals("000e0", Conversion.byteToHex(((byte) (239)), 4, "00000", 3, 1));
        Assertions.assertEquals("fe", Conversion.byteToHex(((byte) (239)), 0, "", 0, 2));
        Assertions.assertThrows(StringIndexOutOfBoundsException.class, () -> Conversion.byteToHex(((byte) (239)), 0, "", 1, 2));
    }

    /**
     * Tests {@link Conversion#longToBinary(long, int, boolean[], int, int)}.
     */
    @Test
    public void testLongToBinary() {
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.longToBinary(0L, 0, new boolean[]{  }, 0, 0));
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.longToBinary(0L, 100, new boolean[]{  }, 0, 0));
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.longToBinary(0L, 0, new boolean[]{  }, 100, 0));
        Assertions.assertArrayEquals(new boolean[69], Conversion.longToBinary(1311768467294899695L, 0, new boolean[69], 0, 0));
        Assertions.assertArrayEquals(new boolean[]{ true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.longToBinary(1311768467294899695L, 0, new boolean[69], 0, 1));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.longToBinary(1311768467294899695L, 0, new boolean[69], 0, 2));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.longToBinary(1311768467294899695L, 0, new boolean[69], 0, 3));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, true, false, false, false, true, true, true, true, false, false, true, true, false, true, false, true, false, false, false, true, false, true, true, false, false, false, true, false, false, true, false, false, false, false, false, false, false, false }, Conversion.longToBinary(1311768467294899695L, 0, new boolean[69], 0, 63));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, true, false, false, false, true, true, true, true, false, false, true, true, false, true, false, true, false, false, false, true, false, true, true, false, false, false, true, false, false, true, false, false, false, false, false, false, false, false }, Conversion.longToBinary(1311768467294899695L, 0, new boolean[69], 0, 64));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.longToBinary(1311768467294899695L, 0, new boolean[69], 2, 1));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, true, true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, true, false, false, false, true, true, true, true, false, false, true, true, false, true, false, true, false, false, false, true, false, true, true, false, false, false, true, false, false, true, false, false, false, false, false, false }, Conversion.longToBinary(1311768467294899695L, 0, new boolean[69], 2, 64));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, true, false, false, false, true, true, true, true, false, false, true, true, false, true, false, true, false, false, false, true, false, true, true, false, false, false, true, false, false, true, false, false, false, false, false, false, false, false, false }, Conversion.longToBinary(1311768467294899695L, 1, new boolean[69], 0, 63));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, true, false, false, false, true, true, true, true, false, false, true, true, false, true, false, true, false, false, false, true, false, true, true, false, false, false, true, false, false, true, false, false, false, false, false, false, false, false, false, false }, Conversion.longToBinary(1311768467294899695L, 2, new boolean[69], 0, 62));
        // assertArrayEquals(new boolean[]{false, false, false, true, true, false, true, true,
        // true, true, false, true, true, false, false, true, true, true, true, false, true,
        // false, true, false, true, false, false, false, false, true, false, false, true,
        // false, false, false, true, true, true, true, false, false, true, true, false, true,
        // false, true, false, false, false, true, false, true, true, false, false, false, true,
        // false, false, true, false, false, false
        // , false, false, false, false}, Conversion.longToBinary(0x1234567890ABCDEFL, 2, new
        // boolean[69], 3, 63));//rejected by assertion
        Assertions.assertArrayEquals(new boolean[]{ false, false, false, true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, true, false, false, false, true, true, true, true, false, false, true, true, false, true, false, true, false, false, false, true, false, true, true, false, false, false, true, false, false, true, false, false, false, false, false, false, false }, Conversion.longToBinary(1311768467294899695L, 2, new boolean[69], 3, 62));
    }

    /**
     * Tests {@link Conversion#intToBinary(int, int, boolean[], int, int)}.
     */
    @Test
    public void testIntToBinary() {
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.intToBinary(0, 0, new boolean[]{  }, 0, 0));
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.intToBinary(0, 100, new boolean[]{  }, 0, 0));
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.intToBinary(0, 0, new boolean[]{  }, 100, 0));
        Assertions.assertArrayEquals(new boolean[69], Conversion.intToBinary(-1867788817, 0, new boolean[69], 0, 0));
        Assertions.assertArrayEquals(new boolean[]{ true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.intToBinary(-1867788817, 0, new boolean[37], 0, 1));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.intToBinary(-1867788817, 0, new boolean[37], 0, 2));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.intToBinary(-1867788817, 0, new boolean[37], 0, 3));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, false, false, false, false, false, false }, Conversion.intToBinary(-1867788817, 0, new boolean[37], 0, 31));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, true, false, false, false, false, false }, Conversion.intToBinary(-1867788817, 0, new boolean[37], 0, 32));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.intToBinary(-1867788817, 0, new boolean[37], 2, 1));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, true, true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, true, false, false, false }, Conversion.intToBinary(-1867788817, 0, new boolean[37], 2, 32));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, true, false, false, false, false, false, false }, Conversion.intToBinary(-1867788817, 1, new boolean[37], 0, 31));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, true, false, false, false, false, false, false, false }, Conversion.intToBinary(-1867788817, 2, new boolean[37], 0, 30));
        // assertArrayEquals(new boolean[]{false, false, false, true, true, false, true,
        // true,
        // true, true, false, true, true, false, false, true, true, true, true, false, true,
        // false, true, false, true, false, false, false, false, true, false, false, false,
        // false, false, false, false}, Conversion.intToBinary(0x90ABCDEF, 2, new boolean[37],
        // 3, 31));//rejected by assertion
        Assertions.assertArrayEquals(new boolean[]{ false, false, false, true, true, false, true, true, true, true, false, true, true, false, false, true, true, true, true, false, true, false, true, false, true, false, false, false, false, true, false, false, true, false, false, false, false }, Conversion.intToBinary(-1867788817, 2, new boolean[37], 3, 30));
    }

    /**
     * Tests {@link Conversion#shortToBinary(short, int, boolean[], int, int)}.
     */
    @Test
    public void testShortToBinary() {
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.shortToBinary(((short) (0)), 0, new boolean[]{  }, 0, 0));
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.shortToBinary(((short) (0)), 100, new boolean[]{  }, 0, 0));
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.shortToBinary(((short) (0)), 0, new boolean[]{  }, 100, 0));
        Assertions.assertArrayEquals(new boolean[69], Conversion.shortToBinary(((short) (52719)), 0, new boolean[69], 0, 0));
        Assertions.assertArrayEquals(new boolean[]{ true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.shortToBinary(((short) (52719)), 0, new boolean[21], 0, 1));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.shortToBinary(((short) (52719)), 0, new boolean[21], 0, 2));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.shortToBinary(((short) (52719)), 0, new boolean[21], 0, 3));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, true, false, true, true, true, true, false, true, true, false, false, true, false, false, false, false, false, false }, Conversion.shortToBinary(((short) (52719)), 0, new boolean[21], 0, 15));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, true, false, true, true, true, true, false, true, true, false, false, true, true, false, false, false, false, false }, Conversion.shortToBinary(((short) (52719)), 0, new boolean[21], 0, 16));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.shortToBinary(((short) (52719)), 0, new boolean[21], 2, 1));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, true, true, true, false, true, true, true, true, false, true, true, false, false, true, true, false, false, false }, Conversion.shortToBinary(((short) (52719)), 0, new boolean[21], 2, 16));
        Assertions.assertArrayEquals(new boolean[]{ true, true, true, false, true, true, true, true, false, true, true, false, false, true, true, false, false, false, false, false, false }, Conversion.shortToBinary(((short) (52719)), 1, new boolean[21], 0, 15));
        Assertions.assertArrayEquals(new boolean[]{ true, true, false, true, true, true, true, false, true, true, false, false, true, true, false, false, false, false, false, false, false }, Conversion.shortToBinary(((short) (52719)), 2, new boolean[21], 0, 14));
        // assertArrayEquals(new boolean[]{false, false, false, true, true, false, true, true,
        // true, true, false, true, true, false, false, true, false, false, false, false,
        // false}, Conversion.shortToBinary((short) 0xCDEF, 2, new boolean[21],
        // 3, 15));//rejected by
        // assertion
        Assertions.assertArrayEquals(new boolean[]{ false, false, false, true, true, false, true, true, true, true, false, true, true, false, false, true, true, false, false, false, false }, Conversion.shortToBinary(((short) (52719)), 2, new boolean[21], 3, 14));
    }

    /**
     * Tests {@link Conversion#byteToBinary(byte, int, boolean[], int, int)}.
     */
    @Test
    public void testByteToBinary() {
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.byteToBinary(((byte) (0)), 0, new boolean[]{  }, 0, 0));
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.byteToBinary(((byte) (0)), 100, new boolean[]{  }, 0, 0));
        Assertions.assertArrayEquals(new boolean[]{  }, Conversion.byteToBinary(((byte) (0)), 0, new boolean[]{  }, 100, 0));
        Assertions.assertArrayEquals(new boolean[69], Conversion.byteToBinary(((byte) (239)), 0, new boolean[69], 0, 0));
        Assertions.assertArrayEquals(new boolean[]{ true, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.byteToBinary(((byte) (149)), 0, new boolean[13], 0, 1));
        Assertions.assertArrayEquals(new boolean[]{ true, false, false, false, false, false, false, false, false, false, false, false, false }, Conversion.byteToBinary(((byte) (149)), 0, new boolean[13], 0, 2));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, false, false, false, false, false, false, false, false, false, false }, Conversion.byteToBinary(((byte) (149)), 0, new boolean[13], 0, 3));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, false, true, false, false, false, false, false, false, false, false }, Conversion.byteToBinary(((byte) (149)), 0, new boolean[13], 0, 7));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, false, true, false, false, true, false, false, false, false, false }, Conversion.byteToBinary(((byte) (149)), 0, new boolean[13], 0, 8));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, false, false, false, false, false, false, false, false, false, false }, Conversion.byteToBinary(((byte) (149)), 0, new boolean[13], 2, 1));
        Assertions.assertArrayEquals(new boolean[]{ false, false, true, false, true, false, true, false, false, true, false, false, false }, Conversion.byteToBinary(((byte) (149)), 0, new boolean[13], 2, 8));
        Assertions.assertArrayEquals(new boolean[]{ false, true, false, true, false, false, true, false, false, false, false, false, false }, Conversion.byteToBinary(((byte) (149)), 1, new boolean[13], 0, 7));
        Assertions.assertArrayEquals(new boolean[]{ true, false, true, false, false, true, false, false, false, false, false, false, false }, Conversion.byteToBinary(((byte) (149)), 2, new boolean[13], 0, 6));
        // assertArrayEquals(new boolean[]{false, false, false, true, true, false, true, true,
        // false, false, false, false, false}, Conversion.byteToBinary((byte) 0x95, 2, new
        // boolean[13], 3, 7));//rejected by assertion
        Assertions.assertArrayEquals(new boolean[]{ false, false, false, true, false, true, false, false, true, false, false, false, false }, Conversion.byteToBinary(((byte) (149)), 2, new boolean[13], 3, 6));
    }

    /**
     * Tests {@link Conversion#uuidToByteArray(UUID, byte[], int, int)}.
     */
    @Test
    public void testUuidToByteArray() {
        Assertions.assertArrayEquals(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, Conversion.uuidToByteArray(new UUID(-1L, -1L), new byte[16], 0, 16));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (136)), ((byte) (153)), ((byte) (170)), ((byte) (187)), ((byte) (204)), ((byte) (221)), ((byte) (238)), ((byte) (255)), ((byte) (0)), ((byte) (17)), ((byte) (34)), ((byte) (51)), ((byte) (68)), ((byte) (85)), ((byte) (102)), ((byte) (119)) }, Conversion.uuidToByteArray(new UUID(-4822678189205112L, 8603657889541918976L), new byte[16], 0, 16));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (136)), ((byte) (153)), ((byte) (170)), ((byte) (187)), ((byte) (204)), ((byte) (221)), ((byte) (238)), ((byte) (255)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) }, Conversion.uuidToByteArray(new UUID(-4822678189205112L, 8603657889541918976L), new byte[16], 4, 8));
        Assertions.assertArrayEquals(new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (136)), ((byte) (153)), ((byte) (170)), ((byte) (187)), ((byte) (204)), ((byte) (221)), ((byte) (238)), ((byte) (255)), ((byte) (0)), ((byte) (17)), ((byte) (34)), ((byte) (51)), ((byte) (0)), ((byte) (0)) }, Conversion.uuidToByteArray(new UUID(-4822678189205112L, 8603657889541918976L), new byte[16], 2, 12));
    }

    /**
     * Tests {@link Conversion#byteArrayToUuid(byte[], int)}.
     */
    @Test
    public void testByteArrayToUuid() {
        Assertions.assertEquals(new UUID(-1L, -1L), Conversion.byteArrayToUuid(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, 0));
        Assertions.assertEquals(new UUID(-4822678189205112L, 8603657889541918976L), Conversion.byteArrayToUuid(new byte[]{ ((byte) (136)), ((byte) (153)), ((byte) (170)), ((byte) (187)), ((byte) (204)), ((byte) (221)), ((byte) (238)), ((byte) (255)), ((byte) (0)), ((byte) (17)), ((byte) (34)), ((byte) (51)), ((byte) (68)), ((byte) (85)), ((byte) (102)), ((byte) (119)) }, 0));
        Assertions.assertEquals(new UUID(-4822678189205112L, 8603657889541918976L), Conversion.byteArrayToUuid(new byte[]{ 0, 0, ((byte) (136)), ((byte) (153)), ((byte) (170)), ((byte) (187)), ((byte) (204)), ((byte) (221)), ((byte) (238)), ((byte) (255)), ((byte) (0)), ((byte) (17)), ((byte) (34)), ((byte) (51)), ((byte) (68)), ((byte) (85)), ((byte) (102)), ((byte) (119)) }, 2));
    }
}

