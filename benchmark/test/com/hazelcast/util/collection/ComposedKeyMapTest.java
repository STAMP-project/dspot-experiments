/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.util.collection;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ComposedKeyMapTest {
    private ComposedKeyMap<String, String, String> map = new ComposedKeyMap<String, String, String>();

    @Test
    public void givenEmpty_whenPut_thenReturnNull() {
        String prevValue = map.put("1", "2", "value");
        Assert.assertNull(prevValue);
    }

    @Test
    public void givenValueAssociated_whenPut_thenReturnPreviousValue() {
        map.put("1", "2", "prevValue");
        String prevValue = map.put("1", "2", "newValue");
        Assert.assertEquals("prevValue", prevValue);
    }

    @Test
    public void givenEmpty_whenGet_thenReturnNull() {
        String value = map.get("key1", "key2");
        Assert.assertNull(value);
    }

    @Test
    public void givenHasEntry_whenGetWithDifferent2ndKey_thenReturnNull() {
        map.put("key1", "key2", "value");
        String value = map.get("key1", "wrongKey2");
        Assert.assertNull(value);
    }

    @Test
    public void givenHasEntry_whenGetWithTheSameKeys_thenReturnValue() {
        map.put("key1", "key2", "value");
        String value = map.get("key1", "key2");
        Assert.assertEquals("value", value);
    }
}

