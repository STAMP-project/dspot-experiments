/**
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.work;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ArrayCreatingInputMergerTest {
    private static final String KEY = "key";

    private static final int[] VALUE_INT_ARRAY = new int[]{ 0, 1, 2 };

    private static final int VALUE_INT = 3;

    private static final Long VALUE_LONG = Long.MAX_VALUE;

    ArrayCreatingInputMerger mArrayCreatingInputMerger;

    Data mDataWithIntArray;

    Data mDataWithInt;

    Data mDataWithLong;

    @Test
    public void testMerge_singleArgument() {
        Data output = getOutputFor(mDataWithInt);
        MatcherAssert.assertThat(output.size(), CoreMatchers.is(1));
        int[] outputArray = output.getIntArray(ArrayCreatingInputMergerTest.KEY);
        MatcherAssert.assertThat(outputArray.length, CoreMatchers.is(1));
        MatcherAssert.assertThat(outputArray[0], CoreMatchers.is(ArrayCreatingInputMergerTest.VALUE_INT));
    }

    @Test
    public void testMerge_concatenatesNonArrays() {
        Data output = getOutputFor(mDataWithInt, mDataWithInt);
        MatcherAssert.assertThat(output.size(), CoreMatchers.is(1));
        int[] outputArray = output.getIntArray(ArrayCreatingInputMergerTest.KEY);
        MatcherAssert.assertThat(outputArray.length, CoreMatchers.is(2));
        MatcherAssert.assertThat(outputArray[0], CoreMatchers.is(ArrayCreatingInputMergerTest.VALUE_INT));
        MatcherAssert.assertThat(outputArray[1], CoreMatchers.is(ArrayCreatingInputMergerTest.VALUE_INT));
    }

    @Test
    public void testMerge_concatenatesArrays() {
        Data output = getOutputFor(mDataWithIntArray, mDataWithIntArray);
        MatcherAssert.assertThat(output.size(), CoreMatchers.is(1));
        int[] outputArray = output.getIntArray(ArrayCreatingInputMergerTest.KEY);
        MatcherAssert.assertThat(outputArray.length, CoreMatchers.is(((ArrayCreatingInputMergerTest.VALUE_INT_ARRAY.length) * 2)));
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < (ArrayCreatingInputMergerTest.VALUE_INT_ARRAY.length); ++j) {
                MatcherAssert.assertThat(outputArray[((i * (ArrayCreatingInputMergerTest.VALUE_INT_ARRAY.length)) + j)], CoreMatchers.is(ArrayCreatingInputMergerTest.VALUE_INT_ARRAY[j]));
            }
        }
    }

    @Test
    public void testMerge_concatenatesArrayAndPrimitive() {
        Data output = getOutputFor(mDataWithIntArray, mDataWithInt);
        MatcherAssert.assertThat(output.size(), CoreMatchers.is(1));
        int[] outputArray = output.getIntArray(ArrayCreatingInputMergerTest.KEY);
        MatcherAssert.assertThat(outputArray.length, CoreMatchers.is(((ArrayCreatingInputMergerTest.VALUE_INT_ARRAY.length) + 1)));
        for (int i = 0; i < (ArrayCreatingInputMergerTest.VALUE_INT_ARRAY.length); ++i) {
            MatcherAssert.assertThat(outputArray[i], CoreMatchers.is(ArrayCreatingInputMergerTest.VALUE_INT_ARRAY[i]));
        }
        MatcherAssert.assertThat(outputArray[ArrayCreatingInputMergerTest.VALUE_INT_ARRAY.length], CoreMatchers.is(ArrayCreatingInputMergerTest.VALUE_INT));
    }

    @Test
    public void testMerge_throwsIllegalArgumentExceptionOnDifferentTypes() {
        Throwable throwable = null;
        try {
            Data output = getOutputFor(mDataWithInt, mDataWithLong);
        } catch (Throwable t) {
            throwable = t;
        }
        MatcherAssert.assertThat(throwable, CoreMatchers.instanceOf(IllegalArgumentException.class));
    }
}

