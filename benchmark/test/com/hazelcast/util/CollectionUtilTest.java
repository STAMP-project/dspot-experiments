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
package com.hazelcast.util;


import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CollectionUtilTest extends HazelcastTestSupport {
    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(CollectionUtil.class);
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(CollectionUtil.isEmpty(Collections.EMPTY_LIST));
        Assert.assertFalse(CollectionUtil.isEmpty(Collections.singletonList(23)));
    }

    @Test
    public void testIsNotEmpty() {
        Assert.assertFalse(CollectionUtil.isNotEmpty(Collections.EMPTY_LIST));
        Assert.assertTrue(CollectionUtil.isNotEmpty(Collections.singletonList(23)));
    }

    @Test
    public void testAddToValueList() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(23);
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        map.put("existingKey", list);
        CollectionUtil.addToValueList(map, "existingKey", 42);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(2, list.size());
        HazelcastTestSupport.assertContains(list, 42);
    }

    @Test
    public void testAddToValueList_whenKeyDoesNotExist_thenNewListIsCreated() {
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        CollectionUtil.addToValueList(map, "nonExistingKey", 42);
        Assert.assertEquals(1, map.size());
        List<Integer> list = map.get("nonExistingKey");
        Assert.assertEquals(1, list.size());
        HazelcastTestSupport.assertContains(list, 42);
    }

    @Test
    public void testGetItemAtPositionOrNull_whenEmptyArray_thenReturnNull() {
        Collection<Object> src = new LinkedHashSet<Object>();
        Object result = CollectionUtil.getItemAtPositionOrNull(src, 0);
        Assert.assertNull(result);
    }

    @Test
    public void testGetItemAtPositionOrNull_whenPositionExist_thenReturnTheItem() {
        Object obj = new Object();
        Collection<Object> src = new LinkedHashSet<Object>();
        src.add(obj);
        Object result = CollectionUtil.getItemAtPositionOrNull(src, 0);
        Assert.assertSame(obj, result);
    }

    @Test
    public void testGetItemAtPositionOrNull_whenSmallerArray_thenReturnNull() {
        Object obj = new Object();
        Collection<Object> src = new LinkedHashSet<Object>();
        src.add(obj);
        Object result = CollectionUtil.getItemAtPositionOrNull(src, 1);
        Assert.assertNull(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetItemAsPositionOrNull_whenInputImplementsList_thenDoNotUserIterator() {
        Object obj = new Object();
        List<Object> src = Mockito.mock(List.class);
        Mockito.when(src.size()).thenReturn(1);
        Mockito.when(src.get(0)).thenReturn(obj);
        Object result = CollectionUtil.getItemAtPositionOrNull(src, 0);
        Assert.assertSame(obj, result);
        Mockito.verify(src, Mockito.never()).iterator();
    }

    @Test
    public void testObjectToDataCollection_size() {
        SerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        Collection<Object> list = new ArrayList<Object>();
        list.add(1);
        list.add("foo");
        Collection<Data> dataCollection = CollectionUtil.objectToDataCollection(list, serializationService);
        Assert.assertEquals(list.size(), dataCollection.size());
    }

    @Test(expected = NullPointerException.class)
    public void testObjectToDataCollection_withNullItem() {
        SerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        Collection<Object> list = new ArrayList<Object>();
        list.add(null);
        CollectionUtil.objectToDataCollection(list, serializationService);
    }

    @Test(expected = NullPointerException.class)
    public void testObjectToDataCollection_withNullCollection() {
        SerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        CollectionUtil.objectToDataCollection(null, serializationService);
    }

    @Test
    public void testObjectToDataCollection_deserializeBack() {
        SerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        Collection<Object> list = new ArrayList<Object>();
        list.add(1);
        list.add("foo");
        Collection<Data> dataCollection = CollectionUtil.objectToDataCollection(list, serializationService);
        Iterator<Data> it1 = dataCollection.iterator();
        Iterator it2 = list.iterator();
        while ((it1.hasNext()) && (it2.hasNext())) {
            Assert.assertEquals(serializationService.toObject(it1.next()), it2.next());
        } 
    }

    @Test
    public void testToIntArray() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(42);
        list.add(23);
        list.add(Integer.MAX_VALUE);
        int[] intArray = CollectionUtil.toIntArray(list);
        Assert.assertNotNull(intArray);
        Assert.assertEquals(list.size(), intArray.length);
        Assert.assertEquals(list.get(0).intValue(), intArray[0]);
        Assert.assertEquals(list.get(1).intValue(), intArray[1]);
        Assert.assertEquals(list.get(2).intValue(), intArray[2]);
    }

    @Test(expected = NullPointerException.class)
    public void testToIntArray_whenNull_thenThrowNPE() {
        CollectionUtil.toIntArray(null);
    }

    @Test
    public void testToLongArray() {
        List<Long> list = new ArrayList<Long>();
        list.add(42L);
        list.add(23L);
        list.add(Long.MAX_VALUE);
        long[] longArray = CollectionUtil.toLongArray(list);
        Assert.assertNotNull(longArray);
        Assert.assertEquals(list.size(), longArray.length);
        Assert.assertEquals(list.get(0).longValue(), longArray[0]);
        Assert.assertEquals(list.get(1).longValue(), longArray[1]);
        Assert.assertEquals(list.get(2).longValue(), longArray[2]);
    }

    @Test(expected = NullPointerException.class)
    public void testToLongArray_whenNull_thenThrowNPE() {
        CollectionUtil.toLongArray(null);
    }

    @Test(expected = NullPointerException.class)
    public void testToIntegerList_whenNull() {
        CollectionUtil.toIntegerList(null);
    }

    @Test
    public void testToIntegerList_whenEmpty() {
        List<Integer> result = CollectionUtil.toIntegerList(new int[0]);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testToIntegerList_whenNotEmpty() {
        List<Integer> result = CollectionUtil.toIntegerList(new int[]{ 1, 2, 3, 4 });
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), result);
    }

    @Test
    public void testNullToEmpty_whenNull() {
        Assert.assertEquals(Collections.emptyList(), CollectionUtil.nullToEmpty(null));
    }

    @Test
    public void testNullToEmpty_whenNotNull() {
        List<Integer> result = Arrays.asList(1, 2, 3, 4, 5);
        Assert.assertEquals(result, CollectionUtil.nullToEmpty(result));
    }
}

