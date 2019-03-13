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
package org.jboss.netty.bootstrap;


import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * A test to make sure that a bootstrap can recognize ordered maps
 */
public class BootstrapOrderedMapTest {
    @Test
    public void shouldReturnTrueIfLinkedHashMap() {
        Assert.assertTrue(Bootstrap.isOrderedMap(new LinkedHashMap<String, String>()));
    }

    @Test
    public void shouldReturnTrueIfMapImplementsOrderedMap() {
        Assert.assertTrue(Bootstrap.isOrderedMap(new BootstrapOrderedMapTest.DummyOrderedMap<String, String>()));
    }

    @Test
    public void shouldReturnFalseIfMapHasNoDefaultConstructor() {
        Assert.assertFalse(Bootstrap.isOrderedMap(new BootstrapOrderedMapTest.MapWithoutDefaultConstructor<String, String>(new HashMap<String, String>())));
    }

    @Test
    public void shouldReturnFalseIfMapIsNotOrdered() {
        Assert.assertFalse(Bootstrap.isOrderedMap(new HashMap<String, String>()));
    }

    @Test
    public void shouldReturnTrueIfMapIsOrdered() {
        Assert.assertTrue(Bootstrap.isOrderedMap(new BootstrapOrderedMapTest.UnknownOrderedMap<String, String>()));
    }

    // A tag interface
    interface OrderedMap {}

    static class DummyOrderedMap<K, V> extends AbstractMap<K, V> implements BootstrapOrderedMapTest.OrderedMap {
        private final Map<K, V> map = new HashMap<K, V>();

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return map.entrySet();
        }
    }

    static class MapWithoutDefaultConstructor<K, V> extends AbstractMap<K, V> {
        private final Map<K, V> map;

        MapWithoutDefaultConstructor(Map<K, V> map) {
            this.map = map;
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return map.entrySet();
        }
    }

    static class UnknownOrderedMap<K, V> extends AbstractMap<K, V> {
        private final Map<K, V> map = new LinkedHashMap<K, V>();

        @Override
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public V put(K key, V value) {
            return map.put(key, value);
        }

        @Override
        public Set<K> keySet() {
            return map.keySet();
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return map.entrySet();
        }
    }
}

