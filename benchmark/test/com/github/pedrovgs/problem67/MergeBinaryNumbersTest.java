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
package com.github.pedrovgs.problem67;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class MergeBinaryNumbersTest {
    private MergeBinaryNumbers mergeBinaryNumbers;

    @Test
    public void shouldMergeBinaryNumbersUsingPointers() {
        int n = 1024;// 10000000000

        int m = 19;// 10011

        int result = mergeBinaryNumbers.merge(n, m, 2, 6);
        int expectedResult = 1100;// 10001001100

        Assert.assertEquals(expectedResult, result);
    }
}

