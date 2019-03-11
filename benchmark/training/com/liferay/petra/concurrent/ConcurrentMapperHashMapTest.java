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


import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 */
public class ConcurrentMapperHashMapTest {
    @ClassRule
    public static final CodeCoverageAssertor codeCoverageAssertor = CodeCoverageAssertor.INSTANCE;

    @Test
    public void testClear() {
        Assert.assertTrue(_concurrentMap.isEmpty());
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertFalse(_concurrentMap.isEmpty());
        _concurrentMap.clear();
        Assert.assertTrue(_concurrentMap.isEmpty());
    }

    @Test
    public void testCompute() {
        try {
            _concurrentMap.compute(null, null);
            Assert.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        try {
            _concurrentMap.compute(_testKey, null);
            Assert.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Remapping function is null", npe.getMessage());
        }
        Assert.assertNull(_concurrentMap.compute(_testKey, ( key, value) -> null));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_KEY);
        Assert.assertSame(_testValue1, _concurrentMap.compute(_testKey, ( key, value) -> _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertSame(_testValue2, _concurrentMap.compute(_testKey, ( key, value) -> _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_KEY);
        Assert.assertSame(_testValue2, _concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
    }

    @Test
    public void testComputeIfAbsent() {
        try {
            _concurrentMap.computeIfAbsent(null, null);
            Assert.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        try {
            _concurrentMap.computeIfAbsent(_testKey, null);
            Assert.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Mapping function is null", npe.getMessage());
        }
        Assert.assertNull(_concurrentMap.computeIfAbsent(_testKey, ( key) -> null));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_KEY);
        Assert.assertSame(_testValue1, _concurrentMap.computeIfAbsent(_testKey, ( key) -> _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertSame(_testValue1, _concurrentMap.computeIfAbsent(_testKey, ( key) -> _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertSame(_testValue1, _concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        ConcurrentMap<ConcurrentMapperHashMapTest.KeyReference, ConcurrentMapperHashMapTest.ValueReference> innerConcurrentMap = ReflectionTestUtil.getFieldValue(_concurrentMap, "innerConcurrentMap");
        Assert.assertEquals(innerConcurrentMap.toString(), 1, innerConcurrentMap.size());
        Collection<ConcurrentMapperHashMapTest.ValueReference> valueReferences = innerConcurrentMap.values();
        Iterator<ConcurrentMapperHashMapTest.ValueReference> iterator = valueReferences.iterator();
        ConcurrentMapperHashMapTest.ValueReference valueReference = iterator.next();
        ReflectionTestUtil.setFieldValue(valueReference, "_value", null);
        Assert.assertSame(_testValue2, _concurrentMap.computeIfAbsent(_testKey, ( key) -> _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
    }

    @Test
    public void testComputeIfPresent() {
        try {
            _concurrentMap.computeIfPresent(null, null);
            Assert.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        try {
            _concurrentMap.computeIfPresent(_testKey, null);
            Assert.fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Remapping function is null", npe.getMessage());
        }
        Assert.assertNull(_concurrentMap.computeIfPresent(_testKey, ( key, value) -> null));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        Assert.assertNull(_concurrentMap.computeIfPresent(_testKey, ( key, value) -> _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertSame(_testValue2, _concurrentMap.computeIfPresent(_testKey, ( key, value) -> _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertNull(_concurrentMap.computeIfPresent(_testKey, ( key, value) -> null));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertNull(_concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
    }

    @Test
    public void testContainsKey() {
        try {
            _concurrentMap.containsKey(null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        Assert.assertFalse(_concurrentMap.containsKey(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertTrue(_concurrentMap.containsKey(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
    }

    @Test
    public void testContainsValue() {
        try {
            _concurrentMap.containsValue(null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Value is null", npe.getMessage());
        }
        Assert.assertFalse(_concurrentMap.containsValue(_testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertTrue(_concurrentMap.containsValue(_testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
    }

    @Test
    public void testEntrySet() {
        Set<Map.Entry<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value>> entrySet = _concurrentMap.entrySet();
        Assert.assertSame(entrySet, _concurrentMap.entrySet());
        Assert.assertTrue(entrySet.toString(), entrySet.isEmpty());
        Assert.assertFalse(entrySet.toString(), entrySet.contains(new Object()));
        Assert.assertFalse(entrySet.remove(new Object()));
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertEquals(entrySet.toString(), 1, entrySet.size());
        Assert.assertTrue(entrySet.toString(), entrySet.contains(new AbstractMap.SimpleEntry<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value>(_testKey, _testValue1)));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertFalse(entrySet.toString(), entrySet.contains(new AbstractMap.SimpleEntry<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value>(_testKey, new ConcurrentMapperHashMapTest.Value(""))));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertFalse(entrySet.remove(new AbstractMap.SimpleEntry<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value>(_testKey, new ConcurrentMapperHashMapTest.Value(""))));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
        Assert.assertTrue(entrySet.remove(new AbstractMap.SimpleEntry<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value>(_testKey, _testValue1)));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Iterator<Map.Entry<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value>> iterator = entrySet.iterator();
        Assert.assertTrue(iterator.hasNext());
        Map.Entry<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value> entry = iterator.next();
        Assert.assertEquals(entry, entry);
        Assert.assertNotEquals(entry, new Object());
        Assert.assertNotEquals(entry, new AbstractMap.SimpleEntry<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value>(new ConcurrentMapperHashMapTest.Key("someKey"), _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY);
        Assert.assertNotEquals(entry, new AbstractMap.SimpleEntry<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value>(_testKey, new ConcurrentMapperHashMapTest.Value("")));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertEquals(entry, new AbstractMap.SimpleEntry<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value>(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertEquals(((_testKey.hashCode()) ^ (_testValue1.hashCode())), entry.hashCode());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertSame(_testKey, entry.getKey());
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY);
        Assert.assertSame(_testValue1, entry.getValue());
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertTrue(entrySet.toString(), entrySet.contains(entry));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        entry.setValue(_testValue2);
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertSame(_testValue2, _concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        iterator.remove();
        Assert.assertTrue(entrySet.toString(), entrySet.isEmpty());
        Assert.assertFalse(entrySet.toString(), entrySet.contains(entry));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertEquals(entrySet.toString(), 1, entrySet.size());
        entrySet.clear();
        Assert.assertTrue(entrySet.toString(), entrySet.isEmpty());
    }

    @Test
    public void testGet() {
        try {
            _concurrentMap.get(null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        Assert.assertNull(_concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertSame(_testValue1, _concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
    }

    @Test
    public void testKeySet() {
        Set<ConcurrentMapperHashMapTest.Key> keySet = _concurrentMap.keySet();
        Assert.assertSame(keySet, _concurrentMap.keySet());
        Assert.assertTrue(keySet.toString(), keySet.isEmpty());
        Assert.assertFalse(keySet.toString(), keySet.contains(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        Assert.assertFalse(keySet.remove(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        String keySetString = keySet.toString();
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY);
        Assert.assertEquals(keySetString, 1, keySet.size());
        Assert.assertTrue(keySetString, keySet.contains(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        Assert.assertTrue(keySet.remove(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        keySetString = keySet.toString();
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY);
        Assert.assertEquals(keySetString, 1, keySet.size());
        Assert.assertEquals(Collections.singleton(_testKey), keySet);
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY);
        Iterator<ConcurrentMapperHashMapTest.Key> iterator = keySet.iterator();
        Assert.assertTrue(iterator.hasNext());
        ConcurrentMapperHashMapTest.Key key = iterator.next();
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY);
        keySetString = keySet.toString();
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY);
        Assert.assertEquals(_testKey, key);
        Assert.assertFalse(iterator.hasNext());
        Assert.assertTrue(keySetString, keySet.contains(key));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        iterator.remove();
        Assert.assertTrue(keySet.toString(), keySet.isEmpty());
        Assert.assertFalse(keySet.toString(), keySet.contains(key));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertEquals(keySet.toString(), 1, keySet.size());
        keySet.clear();
        Assert.assertTrue(_concurrentMap.isEmpty());
    }

    @Test
    public void testPut() {
        try {
            _concurrentMap.put(null, null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        try {
            _concurrentMap.put(_testKey, null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Value is null", npe.getMessage());
        }
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertSame(_testValue1, _concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertSame(_testValue1, _concurrentMap.put(_testKey, _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertSame(_testValue2, _concurrentMap.put(_testKey, _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertSame(_testValue2, _concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
    }

    @Test
    public void testPutAll() {
        Map<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value> map = createDataMap();
        _concurrentMap.putAll(map);
        Assert.assertEquals(map, _concurrentMap);
    }

    @Test
    public void testPutIfAbsent() {
        try {
            _concurrentMap.putIfAbsent(null, null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        try {
            _concurrentMap.putIfAbsent(_testKey, null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Value is null", npe.getMessage());
        }
        Assert.assertNull(_concurrentMap.putIfAbsent(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertSame(_testValue1, _concurrentMap.putIfAbsent(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertSame(_testValue1, _concurrentMap.putIfAbsent(_testKey, _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertSame(_testValue1, _concurrentMap.putIfAbsent(_testKey, _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_KEY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertSame(_testValue1, _concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
    }

    @Test
    public void testRemove() {
        try {
            _concurrentMap.remove(null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        Assert.assertNull(_concurrentMap.remove(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertSame(_testValue1, _concurrentMap.remove(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertNull(_concurrentMap.remove(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
        Assert.assertNull(_concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
    }

    @Test
    public void testRemoveWithValue() {
        try {
            _concurrentMap.remove(null, null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        try {
            _concurrentMap.remove(_testKey, null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Value is null", npe.getMessage());
        }
        Assert.assertFalse(_concurrentMap.remove(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertFalse(_concurrentMap.remove(_testKey, new ConcurrentMapperHashMapTest.Value("")));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
        Assert.assertTrue(_concurrentMap.remove(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertFalse(_concurrentMap.remove(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
        Assert.assertNull(_concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
    }

    @Test
    public void testRemoveWithValueConcurrentModification() {
        ConcurrentMap<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value> concurrentMap = new ConcurrentMapperHashMapTest.ConcurrentTypeReferenceHashMap(new ConcurrentHashMap<ConcurrentMapperHashMapTest.KeyReference, ConcurrentMapperHashMapTest.ValueReference>() {
            @Override
            public ConcurrentMapperHashMapTest.ValueReference get(Object key) {
                ConcurrentMapperHashMapTest.KeyReference keyReference = ((ConcurrentMapperHashMapTest.KeyReference) (key));
                ConcurrentMapperHashMapTest.ValueReference valueReference = super.get(keyReference);
                if (_testKey.equals(keyReference._key)) {
                    put(new ConcurrentMapperHashMapTest.KeyReference(_testKey), new ConcurrentMapperHashMapTest.ValueReference(_testValue2));
                }
                return valueReference;
            }
        });
        Assert.assertNull(concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertFalse(concurrentMap.remove(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
    }

    @Test
    public void testReplace() {
        try {
            _concurrentMap.replace(null, null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        try {
            _concurrentMap.replace(_testKey, null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Value is null", npe.getMessage());
        }
        Assert.assertNull(_concurrentMap.replace(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertSame(_testValue1, _concurrentMap.replace(_testKey, _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertSame(_testValue2, _concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
    }

    @Test
    public void testReplaceWithValue() {
        try {
            _concurrentMap.replace(null, null, null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Key is null", npe.getMessage());
        }
        try {
            _concurrentMap.replace(_testKey, null, null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("Old value is null", npe.getMessage());
        }
        try {
            _concurrentMap.replace(_testKey, _testValue1, null);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertEquals("New value is null", npe.getMessage());
        }
        Assert.assertFalse(_concurrentMap.replace(_testKey, _testValue1, _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertTrue(_concurrentMap.replace(_testKey, _testValue1, _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
        Assert.assertSame(_testValue2, _concurrentMap.get(_testKey));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertFalse(_concurrentMap.replace(_testKey, _testValue1, _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
    }

    @Test
    public void testReplaceWithValueConcurrentModification() {
        ConcurrentMap<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value> concurrentMap = new ConcurrentMapperHashMapTest.ConcurrentTypeReferenceHashMap(new ConcurrentHashMap<ConcurrentMapperHashMapTest.KeyReference, ConcurrentMapperHashMapTest.ValueReference>() {
            @Override
            public ConcurrentMapperHashMapTest.ValueReference get(Object key) {
                ConcurrentMapperHashMapTest.KeyReference keyReference = ((ConcurrentMapperHashMapTest.KeyReference) (key));
                ConcurrentMapperHashMapTest.ValueReference valueReference = super.get(keyReference);
                if (_testKey.equals(keyReference._key)) {
                    put(new ConcurrentMapperHashMapTest.KeyReference(_testKey), new ConcurrentMapperHashMapTest.ValueReference(_testValue2));
                }
                return valueReference;
            }
        });
        Assert.assertNull(concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertFalse(concurrentMap.replace(_testKey, _testValue1, _testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY, ConcurrentMapperHashMapTest.Event.MAP_VALUE, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY, ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
    }

    @Test
    public void testSerialization() throws Exception {
        Map<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value> map = createDataMap();
        _concurrentMap.putAll(map);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(_concurrentMap);
        }
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        Assert.assertEquals(_concurrentMap, objectInputStream.readObject());
    }

    @Test
    public void testValues() {
        Collection<ConcurrentMapperHashMapTest.Value> values = _concurrentMap.values();
        Assert.assertSame(values, _concurrentMap.values());
        Assert.assertTrue(values.toString(), values.isEmpty());
        Assert.assertFalse(values.toString(), values.contains(new ConcurrentMapperHashMapTest.Value("")));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
        Assert.assertFalse(values.remove(new ConcurrentMapperHashMapTest.Value("")));
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        String valuesString = values.toString();
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertEquals(valuesString, 1, values.size());
        Assert.assertTrue(valuesString, values.contains(_testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
        Assert.assertFalse(valuesString, values.contains(_testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
        Assert.assertFalse(values.remove(_testValue2));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertTrue(values.remove(_testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Iterator<ConcurrentMapperHashMapTest.Value> iterator = values.iterator();
        Assert.assertTrue(iterator.hasNext());
        ConcurrentMapperHashMapTest.Value value = iterator.next();
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        valuesString = values.toString();
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
        Assert.assertSame(_testValue1, value);
        Assert.assertFalse(iterator.hasNext());
        Assert.assertTrue(valuesString, values.contains(value));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
        iterator.remove();
        Assert.assertTrue(values.toString(), values.isEmpty());
        Assert.assertFalse(values.toString(), values.contains(value));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
        Assert.assertNull(_concurrentMap.put(_testKey, _testValue1));
        _assertEventQueue(ConcurrentMapperHashMapTest.Event.MAP_KEY, ConcurrentMapperHashMapTest.Event.MAP_VALUE);
        Assert.assertEquals(values.toString(), 1, values.size());
        values.clear();
        Assert.assertTrue(_concurrentMap.isEmpty());
    }

    public static class Key implements Serializable {
        public Key(String id) {
            if (id == null) {
                throw new NullPointerException("Id is null");
            }
            _id = id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ConcurrentMapperHashMapTest.Key)) {
                return false;
            }
            ConcurrentMapperHashMapTest.Key key = ((ConcurrentMapperHashMapTest.Key) (obj));
            return _id.equals(key._id);
        }

        @Override
        public int hashCode() {
            return _id.hashCode();
        }

        private static final long serialVersionUID = 1L;

        private final String _id;
    }

    public static class KeyReference implements Serializable {
        public KeyReference(ConcurrentMapperHashMapTest.Key key) {
            if (key == null) {
                throw new NullPointerException("Type is null");
            }
            _key = key;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ConcurrentMapperHashMapTest.KeyReference)) {
                return false;
            }
            ConcurrentMapperHashMapTest.KeyReference keyReference = ((ConcurrentMapperHashMapTest.KeyReference) (obj));
            return _key.equals(keyReference._key);
        }

        @Override
        public int hashCode() {
            return _key.hashCode();
        }

        private static final long serialVersionUID = 1L;

        private final ConcurrentMapperHashMapTest.Key _key;
    }

    public static class Value implements Serializable {
        public Value(String valueId) {
            if (valueId == null) {
                throw new NullPointerException("Value id is null");
            }
            _valueId = valueId;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ConcurrentMapperHashMapTest.Value)) {
                return false;
            }
            ConcurrentMapperHashMapTest.Value value = ((ConcurrentMapperHashMapTest.Value) (obj));
            return _valueId.equals(value._valueId);
        }

        @Override
        public int hashCode() {
            return _valueId.hashCode();
        }

        private static final long serialVersionUID = 1L;

        private final String _valueId;
    }

    public static class ValueReference implements Serializable {
        public ValueReference(ConcurrentMapperHashMapTest.Value value) {
            if (value == null) {
                throw new NullPointerException("Value is null");
            }
            _value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ConcurrentMapperHashMapTest.ValueReference)) {
                return false;
            }
            ConcurrentMapperHashMapTest.ValueReference valueReference = ((ConcurrentMapperHashMapTest.ValueReference) (obj));
            return _value.equals(valueReference._value);
        }

        @Override
        public int hashCode() {
            return _value.hashCode();
        }

        private static final long serialVersionUID = 1L;

        private final ConcurrentMapperHashMapTest.Value _value;
    }

    private static final Queue<ConcurrentMapperHashMapTest.Event> _eventQueue = new LinkedList<>();

    private final ConcurrentMap<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.Value> _concurrentMap = new ConcurrentMapperHashMapTest.ConcurrentTypeReferenceHashMap();

    private final ConcurrentMapperHashMapTest.Key _testKey = new ConcurrentMapperHashMapTest.Key("testKey");

    private final ConcurrentMapperHashMapTest.Value _testValue1 = new ConcurrentMapperHashMapTest.Value("testValue1");

    private final ConcurrentMapperHashMapTest.Value _testValue2 = new ConcurrentMapperHashMapTest.Value("testValue2");

    private static class ConcurrentTypeReferenceHashMap extends ConcurrentMapperHashMap<ConcurrentMapperHashMapTest.Key, ConcurrentMapperHashMapTest.KeyReference, ConcurrentMapperHashMapTest.Value, ConcurrentMapperHashMapTest.ValueReference> {
        public ConcurrentTypeReferenceHashMap() {
            super(new ConcurrentHashMap<ConcurrentMapperHashMapTest.KeyReference, ConcurrentMapperHashMapTest.ValueReference>());
        }

        public ConcurrentTypeReferenceHashMap(ConcurrentMap<ConcurrentMapperHashMapTest.KeyReference, ConcurrentMapperHashMapTest.ValueReference> innerConcurrentMap) {
            super(innerConcurrentMap);
        }

        @Override
        public ConcurrentMapperHashMapTest.KeyReference mapKey(ConcurrentMapperHashMapTest.Key key) {
            ConcurrentMapperHashMapTest._eventQueue.offer(ConcurrentMapperHashMapTest.Event.MAP_KEY);
            return new ConcurrentMapperHashMapTest.KeyReference(key);
        }

        @Override
        public ConcurrentMapperHashMapTest.KeyReference mapKeyForQuery(ConcurrentMapperHashMapTest.Key key) {
            ConcurrentMapperHashMapTest._eventQueue.offer(ConcurrentMapperHashMapTest.Event.MAP_KEY_FOR_QUERY);
            return new ConcurrentMapperHashMapTest.KeyReference(key);
        }

        @Override
        public ConcurrentMapperHashMapTest.ValueReference mapValue(ConcurrentMapperHashMapTest.Key key, ConcurrentMapperHashMapTest.Value value) {
            ConcurrentMapperHashMapTest._eventQueue.offer(ConcurrentMapperHashMapTest.Event.MAP_VALUE);
            return new ConcurrentMapperHashMapTest.ValueReference(value);
        }

        @Override
        public ConcurrentMapperHashMapTest.ValueReference mapValueForQuery(ConcurrentMapperHashMapTest.Value value) {
            ConcurrentMapperHashMapTest._eventQueue.offer(ConcurrentMapperHashMapTest.Event.MAP_VALUE_FOR_QUERY);
            return new ConcurrentMapperHashMapTest.ValueReference(value);
        }

        @Override
        public ConcurrentMapperHashMapTest.Key unmapKey(ConcurrentMapperHashMapTest.KeyReference keyReference) {
            ConcurrentMapperHashMapTest._eventQueue.offer(ConcurrentMapperHashMapTest.Event.UNMAP_KEY);
            return keyReference._key;
        }

        @Override
        public ConcurrentMapperHashMapTest.Key unmapKeyForQuery(ConcurrentMapperHashMapTest.KeyReference keyReference) {
            ConcurrentMapperHashMapTest._eventQueue.offer(ConcurrentMapperHashMapTest.Event.UNMAP_KEY_FOR_QUERY);
            return keyReference._key;
        }

        @Override
        public ConcurrentMapperHashMapTest.Value unmapValue(ConcurrentMapperHashMapTest.ValueReference valueReference) {
            ConcurrentMapperHashMapTest._eventQueue.offer(ConcurrentMapperHashMapTest.Event.UNMAP_VALUE);
            return valueReference._value;
        }

        @Override
        public ConcurrentMapperHashMapTest.Value unmapValueForQuery(ConcurrentMapperHashMapTest.ValueReference valueReference) {
            ConcurrentMapperHashMapTest._eventQueue.offer(ConcurrentMapperHashMapTest.Event.UNMAP_VALUE_FOR_QUERY);
            return valueReference._value;
        }
    }

    private enum Event {

        MAP_KEY,
        MAP_KEY_FOR_QUERY,
        MAP_VALUE,
        MAP_VALUE_FOR_QUERY,
        UNMAP_KEY,
        UNMAP_KEY_FOR_QUERY,
        UNMAP_VALUE,
        UNMAP_VALUE_FOR_QUERY;}
}

