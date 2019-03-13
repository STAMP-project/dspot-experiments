/**
 * Copyright (C) 2014 Pedro Vicente G?mez S?nchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pedrovgs.problem71;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class ReverseBinaryNumberTest {
    private ReverseBinaryNumber reverseBinaryNumber;

    @Test
    public void shouldReturnOneIfInputIsZero() {
        int input = 0;// 0

        int result = reverseBinaryNumber.reverse(input);
        Assert.assertEquals(1, result);// 1

    }

    @Test
    public void shouldReturnZeroIfInputIsOne() {
        int input = 1;// 1

        int result = reverseBinaryNumber.reverse(input);
        Assert.assertEquals(0, result);// 0

    }

    @Test
    public void shouldReturnZeroIfInputIsFullOfOnes() {
        int input = 63;// 111111

        int result = reverseBinaryNumber.reverse(input);
        Assert.assertEquals(0, result);// 0

    }

    @Test
    public void shouldReturnOneIfInputIsTwo() {
        int input = 2;// 10

        int result = reverseBinaryNumber.reverse(input);
        Assert.assertEquals(1, result);// 01

    }

    @Test
    public void shouldReturnTheReverseBinaryNumber() {
        int input = 11;// 1011

        int result = reverseBinaryNumber.reverse(input);
        Assert.assertEquals(4, result);// 0100

    }
}

