/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.api.java.util;


import java.util.EnumMap;
import java.util.Map;
import junit.framework.TestCase;


public class EnumMapTest extends TestCase {
    enum Size {

        Small,
        Middle,
        Big() {};}

    enum Color {

        Red,
        Green,
        Blue() {};}

    // Empty
    enum Empty {
        ;
    }

    private static class MockEntry<K, V> implements Map.Entry<K, V> {
        private K key;

        private V value;

        public MockEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int hashCode() {
            return ((key) == null ? 0 : key.hashCode()) ^ ((value) == null ? 0 : value.hashCode());
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V object) {
            V oldValue = value;
            value = object;
            return oldValue;
        }
    }

    /**
     * java.util.EnumMap#put(Object,Object)
     */
    public void test_putLjava_lang_ObjectLjava_lang_Object() {
        EnumMap enumSizeMap = new EnumMap(EnumMapTest.Size.class);
        try {
            enumSizeMap.put(EnumMapTest.Color.Red, 2);
            TestCase.fail("Expected ClassCastException");
        } catch (ClassCastException e) {
            // Expected
        }
        TestCase.assertNull("Return non-null for non mapped key", enumSizeMap.put(EnumMapTest.Size.Small, 1));
        EnumMap enumColorMap = new EnumMap<EnumMapTest.Color, Double>(EnumMapTest.Color.class);
        try {
            enumColorMap.put(EnumMapTest.Size.Big, 2);
            TestCase.fail("Expected ClassCastException");
        } catch (ClassCastException e) {
            // Expected
        }
        try {
            enumColorMap.put(null, 2);
            TestCase.fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
        TestCase.assertNull("Return non-null for non mapped key", enumColorMap.put(EnumMapTest.Color.Green, 2));
        TestCase.assertEquals("Return wrong value", 2, enumColorMap.put(EnumMapTest.Color.Green, new Double(4)));
        TestCase.assertEquals("Return wrong value", new Double(4), enumColorMap.put(EnumMapTest.Color.Green, new Integer("3")));
        TestCase.assertEquals("Return wrong value", new Integer("3"), enumColorMap.put(EnumMapTest.Color.Green, null));
        Float f = new Float("3.4");
        TestCase.assertNull("Return non-null for non mapped key", enumColorMap.put(EnumMapTest.Color.Green, f));
        TestCase.assertNull("Return non-null for non mapped key", enumColorMap.put(EnumMapTest.Color.Blue, 2));
        TestCase.assertEquals("Return wrong value", 2, enumColorMap.put(EnumMapTest.Color.Blue, new Double(4)));
    }
}

