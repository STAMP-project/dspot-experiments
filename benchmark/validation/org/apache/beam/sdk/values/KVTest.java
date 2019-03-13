/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.values;


import java.util.Comparator;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for KV.
 */
@RunWith(JUnit4.class)
public class KVTest {
    private static final Integer[] TEST_VALUES = new Integer[]{ null, Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE };

    @Test
    public void testEquals() {
        // Neither position are arrays
        Assert.assertThat(KV.of(1, 2), Matchers.equalTo(KV.of(1, 2)));
        // Key is array
        Assert.assertThat(KV.of(new int[]{ 1, 2 }, 3), Matchers.equalTo(KV.of(new int[]{ 1, 2 }, 3)));
        // Value is array
        Assert.assertThat(KV.of(1, new int[]{ 2, 3 }), Matchers.equalTo(KV.of(1, new int[]{ 2, 3 })));
        // Both are arrays
        Assert.assertThat(KV.of(new int[]{ 1, 2 }, new int[]{ 3, 4 }), Matchers.equalTo(KV.of(new int[]{ 1, 2 }, new int[]{ 3, 4 })));
        // Unfortunately, deep equals only goes so far
        Assert.assertThat(KV.of(ImmutableList.of(new int[]{ 1, 2 }), 3), Matchers.not(Matchers.equalTo(KV.of(ImmutableList.of(new int[]{ 1, 2 }), 3))));
        Assert.assertThat(KV.of(1, ImmutableList.of(new int[]{ 2, 3 })), Matchers.not(Matchers.equalTo(KV.of(1, ImmutableList.of(new int[]{ 2, 3 })))));
        // Key is array and differs
        Assert.assertThat(KV.of(new int[]{ 1, 2 }, 3), Matchers.not(Matchers.equalTo(KV.of(new int[]{ 1, 37 }, 3))));
        // Key is non-array and differs
        Assert.assertThat(KV.of(1, new int[]{ 2, 3 }), Matchers.not(Matchers.equalTo(KV.of(37, new int[]{ 1, 2 }))));
        // Value is array and differs
        Assert.assertThat(KV.of(1, new int[]{ 2, 3 }), Matchers.not(Matchers.equalTo(KV.of(1, new int[]{ 37, 3 }))));
        // Value is non-array and differs
        Assert.assertThat(KV.of(new byte[]{ 1, 2 }, 3), Matchers.not(Matchers.equalTo(KV.of(new byte[]{ 1, 2 }, 37))));
    }

    @Test
    public void testOrderByKey() {
        Comparator<KV<Integer, Integer>> orderByKey = new KV.OrderByKey<>();
        for (Integer key1 : KVTest.TEST_VALUES) {
            for (Integer val1 : KVTest.TEST_VALUES) {
                for (Integer key2 : KVTest.TEST_VALUES) {
                    for (Integer val2 : KVTest.TEST_VALUES) {
                        Assert.assertEquals(compareInt(key1, key2), orderByKey.compare(KV.of(key1, val1), KV.of(key2, val2)));
                    }
                }
            }
        }
    }

    @Test
    public void testOrderByValue() {
        Comparator<KV<Integer, Integer>> orderByValue = new KV.OrderByValue<>();
        for (Integer key1 : KVTest.TEST_VALUES) {
            for (Integer val1 : KVTest.TEST_VALUES) {
                for (Integer key2 : KVTest.TEST_VALUES) {
                    for (Integer val2 : KVTest.TEST_VALUES) {
                        Assert.assertEquals(compareInt(val1, val2), orderByValue.compare(KV.of(key1, val1), KV.of(key2, val2)));
                    }
                }
            }
        }
    }
}

