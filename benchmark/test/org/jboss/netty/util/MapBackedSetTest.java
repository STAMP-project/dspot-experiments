/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.util;


import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class MapBackedSetTest {
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testSize() {
        Map map = createStrictMock(Map.class);
        expect(map.size()).andReturn(0);
        replay(map);
        Assert.assertEquals(0, new MapBackedSet(map).size());
        verify(map);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testContains() {
        Map map = createStrictMock(Map.class);
        expect(map.containsKey("key")).andReturn(true);
        replay(map);
        Assert.assertTrue(new MapBackedSet(map).contains("key"));
        verify(map);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testRemove() {
        Map map = createStrictMock(Map.class);
        expect(map.remove("key")).andReturn(true);
        expect(map.remove("key")).andReturn(null);
        replay(map);
        Assert.assertTrue(new MapBackedSet(map).remove("key"));
        Assert.assertFalse(new MapBackedSet(map).remove("key"));
        verify(map);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testAdd() {
        Map map = createStrictMock(Map.class);
        expect(map.put("key", true)).andReturn(null);
        expect(map.put("key", true)).andReturn(true);
        replay(map);
        Assert.assertTrue(new MapBackedSet(map).add("key"));
        Assert.assertFalse(new MapBackedSet(map).add("key"));
        verify(map);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testClear() {
        Map map = createStrictMock(Map.class);
        map.clear();
        replay(map);
        new MapBackedSet(map).clear();
        verify(map);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testIterator() {
        Map map = createStrictMock(Map.class);
        Set keySet = createStrictMock(Set.class);
        Iterator keySetIterator = createStrictMock(Iterator.class);
        expect(map.keySet()).andReturn(keySet);
        expect(keySet.iterator()).andReturn(keySetIterator);
        replay(map);
        replay(keySet);
        replay(keySetIterator);
        Assert.assertSame(keySetIterator, new MapBackedSet(map).iterator());
        verify(map);
        verify(keySet);
        verify(keySetIterator);
    }
}

