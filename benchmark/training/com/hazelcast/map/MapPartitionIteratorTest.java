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
package com.hazelcast.map;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapPartitionIteratorTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public boolean prefetchValues;

    @Test(expected = NoSuchElementException.class)
    public void test_next_Throws_Exception_On_EmptyPartition() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        MapProxyImpl<Object, Object> proxy = ((MapProxyImpl<Object, Object>) (instance.getMap(HazelcastTestSupport.randomMapName())));
        Iterator<Map.Entry<Object, Object>> iterator = proxy.iterator(10, 1, prefetchValues);
        iterator.next();
    }

    @Test(expected = IllegalStateException.class)
    public void test_remove_Throws_Exception_When_Called_Without_Next() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        MapProxyImpl<Object, Object> proxy = ((MapProxyImpl<Object, Object>) (instance.getMap(HazelcastTestSupport.randomMapName())));
        Iterator<Map.Entry<Object, Object>> iterator = proxy.iterator(10, 1, prefetchValues);
        iterator.remove();
    }

    @Test
    public void test_Remove() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        MapProxyImpl<Object, Object> proxy = ((MapProxyImpl<Object, Object>) (instance.getMap(HazelcastTestSupport.randomMapName())));
        String key = HazelcastTestSupport.generateKeyForPartition(instance, 1);
        String value = HazelcastTestSupport.randomString();
        proxy.put(key, value);
        Iterator<Map.Entry<Object, Object>> iterator = proxy.iterator(10, 1, prefetchValues);
        iterator.next();
        iterator.remove();
        Assert.assertEquals(0, proxy.size());
    }

    @Test
    public void test_HasNext_Returns_False_On_EmptyPartition() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        MapProxyImpl<Object, Object> proxy = ((MapProxyImpl<Object, Object>) (instance.getMap(HazelcastTestSupport.randomMapName())));
        Iterator<Map.Entry<Object, Object>> iterator = proxy.iterator(10, 1, prefetchValues);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test_HasNext_Returns_True_On_NonEmptyPartition() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        MapProxyImpl<Object, Object> proxy = ((MapProxyImpl<Object, Object>) (instance.getMap(HazelcastTestSupport.randomMapName())));
        String key = HazelcastTestSupport.generateKeyForPartition(instance, 1);
        String value = HazelcastTestSupport.randomString();
        proxy.put(key, value);
        Iterator<Map.Entry<Object, Object>> iterator = proxy.iterator(10, 1, prefetchValues);
        assertTrue(iterator.hasNext());
    }

    @Test
    public void test_Next_Returns_Value_On_NonEmptyPartition() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        MapProxyImpl<Object, Object> proxy = ((MapProxyImpl<Object, Object>) (instance.getMap(HazelcastTestSupport.randomMapName())));
        String key = HazelcastTestSupport.generateKeyForPartition(instance, 1);
        String value = HazelcastTestSupport.randomString();
        proxy.put(key, value);
        Iterator<Map.Entry<Object, Object>> iterator = proxy.iterator(10, 1, prefetchValues);
        Map.Entry entry = iterator.next();
        Assert.assertEquals(value, entry.getValue());
    }

    @Test
    public void test_Next_Returns_Value_On_NonEmptyPartition_and_HasNext_Returns_False_when_Item_Consumed() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        MapProxyImpl<Object, Object> proxy = ((MapProxyImpl<Object, Object>) (instance.getMap(HazelcastTestSupport.randomMapName())));
        String key = HazelcastTestSupport.generateKeyForPartition(instance, 1);
        String value = HazelcastTestSupport.randomString();
        proxy.put(key, value);
        Iterator<Map.Entry<Object, Object>> iterator = proxy.iterator(10, 1, prefetchValues);
        Map.Entry entry = iterator.next();
        Assert.assertEquals(value, entry.getValue());
        boolean hasNext = iterator.hasNext();
        assertFalse(hasNext);
    }

    @Test
    public void test_Next_Returns_Values_When_FetchSizeExceeds_On_NonEmptyPartition() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        MapProxyImpl<Object, Object> proxy = ((MapProxyImpl<Object, Object>) (instance.getMap(HazelcastTestSupport.randomMapName())));
        String value = HazelcastTestSupport.randomString();
        for (int i = 0; i < 100; i++) {
            String key = HazelcastTestSupport.generateKeyForPartition(instance, 1);
            proxy.put(key, value);
        }
        Iterator<Map.Entry<Object, Object>> iterator = proxy.iterator(10, 1, prefetchValues);
        for (int i = 0; i < 100; i++) {
            Map.Entry entry = iterator.next();
            Assert.assertEquals(value, entry.getValue());
        }
    }
}

