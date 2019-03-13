/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.util;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 */
public class CollectionUtilsTests {
    @Test
    public void testIsEmpty() {
        Assert.assertTrue(CollectionUtils.isEmpty(((Set<Object>) (null))));
        Assert.assertTrue(CollectionUtils.isEmpty(((Map<String, String>) (null))));
        Assert.assertTrue(CollectionUtils.isEmpty(new HashMap<String, String>()));
        Assert.assertTrue(CollectionUtils.isEmpty(new HashSet()));
        List<Object> list = new LinkedList<>();
        list.add(new Object());
        Assert.assertFalse(CollectionUtils.isEmpty(list));
        Map<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        Assert.assertFalse(CollectionUtils.isEmpty(map));
    }

    @Test
    public void testMergeArrayIntoCollection() {
        Object[] arr = new Object[]{ "value1", "value2" };
        List<Comparable<?>> list = new LinkedList<>();
        list.add("value3");
        CollectionUtils.mergeArrayIntoCollection(arr, list);
        Assert.assertEquals("value3", list.get(0));
        Assert.assertEquals("value1", list.get(1));
        Assert.assertEquals("value2", list.get(2));
    }

    @Test
    public void testMergePrimitiveArrayIntoCollection() {
        int[] arr = new int[]{ 1, 2 };
        List<Comparable<?>> list = new LinkedList<>();
        list.add(Integer.valueOf(3));
        CollectionUtils.mergeArrayIntoCollection(arr, list);
        Assert.assertEquals(Integer.valueOf(3), list.get(0));
        Assert.assertEquals(Integer.valueOf(1), list.get(1));
        Assert.assertEquals(Integer.valueOf(2), list.get(2));
    }

    @Test
    public void testMergePropertiesIntoMap() {
        Properties defaults = new Properties();
        defaults.setProperty("prop1", "value1");
        Properties props = new Properties(defaults);
        props.setProperty("prop2", "value2");
        props.put("prop3", Integer.valueOf(3));
        Map<String, String> map = new HashMap<>();
        map.put("prop4", "value4");
        CollectionUtils.mergePropertiesIntoMap(props, map);
        Assert.assertEquals("value1", map.get("prop1"));
        Assert.assertEquals("value2", map.get("prop2"));
        Assert.assertEquals(Integer.valueOf(3), map.get("prop3"));
        Assert.assertEquals("value4", map.get("prop4"));
    }

    @Test
    public void testContains() {
        Assert.assertFalse(CollectionUtils.contains(((Iterator<String>) (null)), "myElement"));
        Assert.assertFalse(CollectionUtils.contains(((Enumeration<String>) (null)), "myElement"));
        Assert.assertFalse(CollectionUtils.contains(new LinkedList<String>().iterator(), "myElement"));
        Assert.assertFalse(CollectionUtils.contains(new Hashtable<String, Object>().keys(), "myElement"));
        List<String> list = new LinkedList<>();
        list.add("myElement");
        Assert.assertTrue(CollectionUtils.contains(list.iterator(), "myElement"));
        Hashtable<String, String> ht = new Hashtable<>();
        ht.put("myElement", "myValue");
        Assert.assertTrue(CollectionUtils.contains(ht.keys(), "myElement"));
    }

    @Test
    public void testContainsAny() throws Exception {
        List<String> source = new ArrayList<>();
        source.add("abc");
        source.add("def");
        source.add("ghi");
        List<String> candidates = new ArrayList<>();
        candidates.add("xyz");
        candidates.add("def");
        candidates.add("abc");
        Assert.assertTrue(CollectionUtils.containsAny(source, candidates));
        candidates.remove("def");
        Assert.assertTrue(CollectionUtils.containsAny(source, candidates));
        candidates.remove("abc");
        Assert.assertFalse(CollectionUtils.containsAny(source, candidates));
    }

    @Test
    public void testContainsInstanceWithNullCollection() throws Exception {
        Assert.assertFalse("Must return false if supplied Collection argument is null", CollectionUtils.containsInstance(null, this));
    }

    @Test
    public void testContainsInstanceWithInstancesThatAreEqualButDistinct() throws Exception {
        List<CollectionUtilsTests.Instance> list = new ArrayList<>();
        list.add(new CollectionUtilsTests.Instance("fiona"));
        Assert.assertFalse("Must return false if instance is not in the supplied Collection argument", CollectionUtils.containsInstance(list, new CollectionUtilsTests.Instance("fiona")));
    }

    @Test
    public void testContainsInstanceWithSameInstance() throws Exception {
        List<CollectionUtilsTests.Instance> list = new ArrayList<>();
        list.add(new CollectionUtilsTests.Instance("apple"));
        CollectionUtilsTests.Instance instance = new CollectionUtilsTests.Instance("fiona");
        list.add(instance);
        Assert.assertTrue("Must return true if instance is in the supplied Collection argument", CollectionUtils.containsInstance(list, instance));
    }

    @Test
    public void testContainsInstanceWithNullInstance() throws Exception {
        List<CollectionUtilsTests.Instance> list = new ArrayList<>();
        list.add(new CollectionUtilsTests.Instance("apple"));
        list.add(new CollectionUtilsTests.Instance("fiona"));
        Assert.assertFalse("Must return false if null instance is supplied", CollectionUtils.containsInstance(list, null));
    }

    @Test
    public void testFindFirstMatch() throws Exception {
        List<String> source = new ArrayList<>();
        source.add("abc");
        source.add("def");
        source.add("ghi");
        List<String> candidates = new ArrayList<>();
        candidates.add("xyz");
        candidates.add("def");
        candidates.add("abc");
        Assert.assertEquals("def", CollectionUtils.findFirstMatch(source, candidates));
    }

    @Test
    public void testHasUniqueObject() {
        List<String> list = new LinkedList<>();
        list.add("myElement");
        list.add("myOtherElement");
        Assert.assertFalse(CollectionUtils.hasUniqueObject(list));
        list = new LinkedList<>();
        list.add("myElement");
        Assert.assertTrue(CollectionUtils.hasUniqueObject(list));
        list = new LinkedList<>();
        list.add("myElement");
        list.add(null);
        Assert.assertFalse(CollectionUtils.hasUniqueObject(list));
        list = new LinkedList<>();
        list.add(null);
        list.add("myElement");
        Assert.assertFalse(CollectionUtils.hasUniqueObject(list));
        list = new LinkedList<>();
        list.add(null);
        list.add(null);
        Assert.assertTrue(CollectionUtils.hasUniqueObject(list));
        list = new LinkedList<>();
        list.add(null);
        Assert.assertTrue(CollectionUtils.hasUniqueObject(list));
        list = new LinkedList<>();
        Assert.assertFalse(CollectionUtils.hasUniqueObject(list));
    }

    private static final class Instance {
        private final String name;

        public Instance(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object rhs) {
            if ((this) == rhs) {
                return true;
            }
            if ((rhs == null) || ((this.getClass()) != (rhs.getClass()))) {
                return false;
            }
            CollectionUtilsTests.Instance instance = ((CollectionUtilsTests.Instance) (rhs));
            return this.name.equals(instance.name);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }
}

