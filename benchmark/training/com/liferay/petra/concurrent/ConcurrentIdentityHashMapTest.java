/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.concurrent;


import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 */
public class ConcurrentIdentityHashMapTest {
    @ClassRule
    public static final CodeCoverageAssertor codeCoverageAssertor = CodeCoverageAssertor.INSTANCE;

    @Test
    public void testConcurrentIdentityHashMap() {
        ConcurrentIdentityHashMap<String, Object> concurrentIdentityHashMap = new ConcurrentIdentityHashMap();
        Assert.assertFalse(concurrentIdentityHashMap.containsKey(ConcurrentIdentityHashMapTest._TEST_KEY_1));
        Assert.assertFalse(concurrentIdentityHashMap.containsValue(_testValue1));
        Assert.assertFalse(concurrentIdentityHashMap.containsKey(ConcurrentIdentityHashMapTest._TEST_KEY_2));
        Assert.assertFalse(concurrentIdentityHashMap.containsValue(_testValue2));
        Assert.assertNull(concurrentIdentityHashMap.put(ConcurrentIdentityHashMapTest._TEST_KEY_1, _testValue1));
        Assert.assertTrue(concurrentIdentityHashMap.containsKey(ConcurrentIdentityHashMapTest._TEST_KEY_1));
        Assert.assertTrue(concurrentIdentityHashMap.containsValue(_testValue1));
        Assert.assertFalse(concurrentIdentityHashMap.containsKey(ConcurrentIdentityHashMapTest._TEST_KEY_2));
        Assert.assertFalse(concurrentIdentityHashMap.containsValue(_testValue2));
        Assert.assertSame(_testValue1, concurrentIdentityHashMap.get(ConcurrentIdentityHashMapTest._TEST_KEY_1));
        Assert.assertNull(concurrentIdentityHashMap.get(ConcurrentIdentityHashMapTest._TEST_KEY_2));
        Assert.assertSame(_testValue1, concurrentIdentityHashMap.put(ConcurrentIdentityHashMapTest._TEST_KEY_1, _testValue2));
        Assert.assertTrue(concurrentIdentityHashMap.containsKey(ConcurrentIdentityHashMapTest._TEST_KEY_1));
        Assert.assertFalse(concurrentIdentityHashMap.containsValue(_testValue1));
        Assert.assertFalse(concurrentIdentityHashMap.containsKey(ConcurrentIdentityHashMapTest._TEST_KEY_2));
        Assert.assertTrue(concurrentIdentityHashMap.containsValue(_testValue2));
        Assert.assertSame(_testValue2, concurrentIdentityHashMap.get(ConcurrentIdentityHashMapTest._TEST_KEY_1));
        Assert.assertNull(concurrentIdentityHashMap.get(ConcurrentIdentityHashMapTest._TEST_KEY_2));
        Set<String> keySet = concurrentIdentityHashMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        Assert.assertSame(ConcurrentIdentityHashMapTest._TEST_KEY_1, iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testConstructor() {
        ConcurrentMap<IdentityKey<String>, Object> innerConcurrentMap = new ConcurrentHashMap<>();
        ConcurrentIdentityHashMap<String, Object> concurrentIdentityHashMap = new ConcurrentIdentityHashMap(innerConcurrentMap);
        Assert.assertSame(innerConcurrentMap, concurrentIdentityHashMap.innerConcurrentMap);
        Map<String, Object> dataMap = createDataMap();
        concurrentIdentityHashMap = new ConcurrentIdentityHashMap(dataMap);
        Assert.assertEquals(dataMap, concurrentIdentityHashMap);
        new ConcurrentIdentityHashMap<String, Object>(10);
        new ConcurrentIdentityHashMap<String, Object>(10, 0.75F, 4);
    }

    private static final String _TEST_KEY_1 = "testKey";

    private static final String _TEST_KEY_2 = new String(ConcurrentIdentityHashMapTest._TEST_KEY_1);

    private final Object _testValue1 = new Object();

    private final Object _testValue2 = new Object();
}

