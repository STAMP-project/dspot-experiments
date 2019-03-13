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


import Data.Builder;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static Data.EMPTY;
import static Data.MAX_DATA_BYTES;


public class DataTest {
    private static final String KEY1 = "key1";

    private static final String KEY2 = "key2";

    @Test
    public void testSize_noArguments() {
        Data data = new Data.Builder().build();
        MatcherAssert.assertThat(data.size(), CoreMatchers.is(0));
    }

    @Test
    public void testSize_hasArguments() {
        Data data = new Data.Builder().putBoolean(DataTest.KEY1, true).build();
        MatcherAssert.assertThat(data.size(), CoreMatchers.is(1));
    }

    @Test
    public void testSerializeEmpty() {
        Data data = EMPTY;
        byte[] byteArray = Data.toByteArray(data);
        Data restoredData = Data.fromByteArray(byteArray);
        MatcherAssert.assertThat(restoredData, CoreMatchers.is(data));
    }

    @Test
    public void testSerializeString() {
        String expectedValue1 = "value1";
        String expectedValue2 = "value2";
        Data data = new Data.Builder().putString(DataTest.KEY1, expectedValue1).putString(DataTest.KEY2, expectedValue2).build();
        byte[] byteArray = Data.toByteArray(data);
        Data restoredData = Data.fromByteArray(byteArray);
        MatcherAssert.assertThat(restoredData, CoreMatchers.is(data));
    }

    @Test
    public void testSerializeIntArray() {
        int[] expectedValue1 = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        int[] expectedValue2 = new int[]{ 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
        Data data = new Data.Builder().putIntArray(DataTest.KEY1, expectedValue1).putIntArray(DataTest.KEY2, expectedValue2).build();
        byte[] byteArray = Data.toByteArray(data);
        Data restoredData = Data.fromByteArray(byteArray);
        MatcherAssert.assertThat(restoredData, CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(restoredData.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(restoredData.getIntArray(DataTest.KEY1), CoreMatchers.is(CoreMatchers.equalTo(expectedValue1)));
        MatcherAssert.assertThat(restoredData.getIntArray(DataTest.KEY2), CoreMatchers.is(CoreMatchers.equalTo(expectedValue2)));
    }

    @Test
    public void testSerializePastMaxSize() {
        int[] payload = new int[(MAX_DATA_BYTES) + 1];
        boolean caughtIllegalStateException = false;
        try {
            new Data.Builder().putIntArray("payload", payload).build();
        } catch (IllegalStateException e) {
            caughtIllegalStateException = true;
        } finally {
            MatcherAssert.assertThat(caughtIllegalStateException, CoreMatchers.is(true));
        }
    }

    @Test
    public void testDeserializePastMaxSize() {
        byte[] payload = new byte[(MAX_DATA_BYTES) + 1];
        boolean caughtIllegalStateException = false;
        try {
            Data.fromByteArray(payload);
        } catch (IllegalStateException e) {
            caughtIllegalStateException = true;
        } finally {
            MatcherAssert.assertThat(caughtIllegalStateException, CoreMatchers.is(true));
        }
    }

    @Test
    public void testPutAll() {
        Map<String, Object> map = new HashMap<>();
        map.put("int", 1);
        map.put("float", 99.0F);
        map.put("String", "two");
        map.put("long array", new long[]{ 1L, 2L, 3L });
        map.put("null", null);
        Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.putAll(map);
        Data data = dataBuilder.build();
        MatcherAssert.assertThat(data.getInt("int", 0), CoreMatchers.is(1));
        MatcherAssert.assertThat(data.getFloat("float", 0.0F), CoreMatchers.is(99.0F));
        MatcherAssert.assertThat(data.getString("String"), CoreMatchers.is("two"));
        long[] longArray = data.getLongArray("long array");
        MatcherAssert.assertThat(longArray, CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(longArray.length, CoreMatchers.is(3));
        MatcherAssert.assertThat(longArray[0], CoreMatchers.is(1L));
        MatcherAssert.assertThat(longArray[1], CoreMatchers.is(2L));
        MatcherAssert.assertThat(longArray[2], CoreMatchers.is(3L));
        MatcherAssert.assertThat(data.getString("null"), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testPutAllWithInvalidTypes() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", new Object());
        boolean caughtIllegalArgumentException = false;
        try {
            new Data.Builder().putAll(map);
        } catch (IllegalArgumentException e) {
            caughtIllegalArgumentException = true;
        }
        MatcherAssert.assertThat(caughtIllegalArgumentException, CoreMatchers.is(true));
    }
}

