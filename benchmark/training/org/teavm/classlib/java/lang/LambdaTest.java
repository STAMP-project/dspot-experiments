/**
 * Copyright 2017 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class LambdaTest {
    @Test
    public void lambdaWorks() {
        Integer[] src = new Integer[]{ 1, 2, 3 };
        Integer[] array = LambdaTest.map(src, (Integer n) -> (n * 2) + (src[0]));
        Assert.assertArrayEquals(new Integer[]{ 3, 5, 7 }, array);
    }

    @Test
    public void lambdaWorksWithLiteral() {
        LambdaTest.A[] src = new LambdaTest.A[]{ new LambdaTest.A(1), new LambdaTest.A(2), new LambdaTest.A(3) };
        LambdaTest.A[] array = LambdaTest.map(src, LambdaTest.A::inc);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(2, array[0].value);
        Assert.assertEquals(3, array[1].value);
    }

    @Test
    public void lambdaWorksWithFieldLiteral() {
        LambdaTest.A[] src = new LambdaTest.A[]{ new LambdaTest.A(1), new LambdaTest.A(2), new LambdaTest.A(3) };
        int[] array = LambdaTest.mapToInt(src, ( a) -> a.value);
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, array);
    }

    interface Function<T> {
        T apply(T value);
    }

    interface IntFunction<T> {
        int apply(T value);
    }

    static class A {
        int value;

        A(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        LambdaTest.A inc() {
            return new LambdaTest.A(((value) + 1));
        }
    }
}

