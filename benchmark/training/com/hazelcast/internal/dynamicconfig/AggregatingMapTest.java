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
package com.hazelcast.internal.dynamicconfig;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AggregatingMapTest extends HazelcastTestSupport {
    @Test
    public void givenKeyExistInTheFirstMap_whenGet_theHit() {
        String key = "key";
        String value = "value";
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put(key, value);
        Map<String, String> map2 = new HashMap<String, String>();
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        Assert.assertEquals(value, aggregatingMap.get(key));
    }

    @Test
    public void givenKeyExistInTheSecondMap_whenGet_theHit() {
        String key = "key";
        String value = "value";
        Map<String, String> map1 = new HashMap<String, String>();
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put(key, value);
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        Assert.assertEquals(value, aggregatingMap.get(key));
    }

    @Test
    public void whenBothMapsAreNull_thenSizeIsEmpty() {
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(null, null);
        Assert.assertTrue(aggregatingMap.isEmpty());
    }

    @Test
    public void testAggregatingSize() {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("key1", "value1");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("key2", "value2");
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        Assert.assertEquals(2, aggregatingMap.size());
    }

    @Test
    public void testContainsKey() {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("key1", "value1");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("key2", "value2");
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        Assert.assertTrue(aggregatingMap.containsKey("key1"));
        Assert.assertTrue(aggregatingMap.containsKey("key2"));
        Assert.assertFalse(aggregatingMap.containsKey("key3"));
    }

    @Test
    public void testContainsValue() {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("key1", "value1");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("key2", "value2");
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        Assert.assertTrue(aggregatingMap.containsValue("value1"));
        Assert.assertTrue(aggregatingMap.containsValue("value2"));
        Assert.assertFalse(aggregatingMap.containsValue("value3"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutThrowUOE() {
        Map<String, String> map1 = new HashMap<String, String>();
        Map<String, String> map2 = new HashMap<String, String>();
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        aggregatingMap.put("key", "value");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClearThrowUOE() {
        Map<String, String> map1 = new HashMap<String, String>();
        Map<String, String> map2 = new HashMap<String, String>();
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        aggregatingMap.clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveThrowUOE() {
        Map<String, String> map1 = new HashMap<String, String>();
        Map<String, String> map2 = new HashMap<String, String>();
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        aggregatingMap.remove("key");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutAllThrowUOE() {
        Map<String, String> map1 = new HashMap<String, String>();
        Map<String, String> map2 = new HashMap<String, String>();
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        Map<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("key", "value");
        aggregatingMap.putAll(tempMap);
    }

    @Test
    public void testKeys_hasKeyFromBothMaps() {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("key1", "value1");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("key2", "value2");
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        Set<String> keySet = aggregatingMap.keySet();
        Assert.assertEquals(2, keySet.size());
        Assert.assertThat(keySet, Matchers.containsInAnyOrder("key1", "key2"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testKeys_isUnmodifiable() {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("key1", "value1");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("key2", "value2");
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        Set<String> keySet = aggregatingMap.keySet();
        keySet.remove("key1");
    }

    @Test
    public void testValues_hasValuesFromBothMaps() {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("key1", "value");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("key2", "value");
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        Collection<String> values = aggregatingMap.values();
        Assert.assertEquals(2, values.size());
        Assert.assertThat(values, Matchers.contains("value", "value"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testValues_isUnmodifiable() {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("key1", "value");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("key2", "value");
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        Collection<String> values = aggregatingMap.values();
        values.remove("value");
    }

    @Test
    public void testEntrySet_hasKeyFromBothMaps() {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("key1", "value1");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("key2", "value2");
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        for (Map.Entry<String, String> entry : aggregatingMap.entrySet()) {
            String key = entry.getKey();
            if (key.equals("key1")) {
                Assert.assertEquals("value1", entry.getValue());
            } else
                if (key.equals("key2")) {
                    Assert.assertEquals("value2", entry.getValue());
                } else {
                    Assert.fail();
                }

        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testEntrySet_isUnmodifiable() {
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("key1", "value1");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("key2", "value2");
        Map<String, String> aggregatingMap = AggregatingMap.aggregate(map1, map2);
        aggregatingMap.remove("entry");
    }
}

