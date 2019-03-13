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


public class OverwritingInputMergerTest {
    private OverwritingInputMerger mOverwritingInputMerger;

    @Test
    public void testMerge_singleArgument() {
        String key = "key";
        String value = "value";
        Data input = new Data.Builder().putString(key, value).build();
        Data output = getOutputFor(input);
        MatcherAssert.assertThat(output.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(output.getString(key), CoreMatchers.is(value));
    }

    @Test
    public void testMerge_multipleArguments() {
        String key1 = "key1";
        String value1 = "value1";
        String value1a = "value1a";
        String key2 = "key2";
        String value2 = "value2";
        String key3 = "key3";
        String value3 = "value3";
        Data input1 = new Data.Builder().putString(key1, value1).putString(key2, value2).build();
        Data input2 = new Data.Builder().putString(key1, value1a).putString(key3, value3).build();
        Data output = getOutputFor(input1, input2);
        MatcherAssert.assertThat(output.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(output.getString(key1), CoreMatchers.is(value1a));
        MatcherAssert.assertThat(output.getString(key2), CoreMatchers.is(value2));
        MatcherAssert.assertThat(output.getString(key3), CoreMatchers.is(value3));
    }
}

