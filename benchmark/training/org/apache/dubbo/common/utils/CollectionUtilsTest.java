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
package org.apache.dubbo.common.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class CollectionUtilsTest {
    @Test
    public void testSort() throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        list.add(100);
        list.add(10);
        list.add(20);
        List<Integer> expected = new ArrayList<Integer>();
        expected.add(10);
        expected.add(20);
        expected.add(100);
        Assertions.assertEquals(expected, CollectionUtils.sort(list));
    }

    @Test
    public void testSortNull() throws Exception {
        Assertions.assertNull(CollectionUtils.sort(null));
        Assertions.assertTrue(CollectionUtils.sort(new ArrayList<Integer>()).isEmpty());
    }

    @Test
    public void testSortSimpleName() throws Exception {
        List<String> list = new ArrayList<String>();
        list.add("aaa.z");
        list.add("b");
        list.add(null);
        list.add("zzz.a");
        list.add("c");
        list.add(null);
        List<String> sorted = CollectionUtils.sortSimpleName(list);
        Assertions.assertNull(sorted.get(0));
        Assertions.assertNull(sorted.get(1));
    }

    @Test
    public void testSortSimpleNameNull() throws Exception {
        Assertions.assertNull(CollectionUtils.sortSimpleName(null));
        Assertions.assertTrue(CollectionUtils.sortSimpleName(new ArrayList<String>()).isEmpty());
    }

    @Test
    public void testSplitAll() throws Exception {
        Assertions.assertNull(CollectionUtils.splitAll(null, null));
        Assertions.assertNull(CollectionUtils.splitAll(null, "-"));
        Assertions.assertTrue(CollectionUtils.splitAll(new HashMap<String, List<String>>(), "-").isEmpty());
        Map<String, List<String>> input = new HashMap<String, List<String>>();
        input.put("key1", Arrays.asList("1:a", "2:b", "3:c"));
        input.put("key2", Arrays.asList("1:a", "2:b"));
        input.put("key3", null);
        input.put("key4", new ArrayList<String>());
        Map<String, Map<String, String>> expected = new HashMap<String, Map<String, String>>();
        expected.put("key1", CollectionUtils.toStringMap("1", "a", "2", "b", "3", "c"));
        expected.put("key2", CollectionUtils.toStringMap("1", "a", "2", "b"));
        expected.put("key3", null);
        expected.put("key4", new HashMap<String, String>());
        Assertions.assertEquals(expected, CollectionUtils.splitAll(input, ":"));
    }

    @Test
    public void testJoinAll() throws Exception {
        Assertions.assertNull(CollectionUtils.joinAll(null, null));
        Assertions.assertNull(CollectionUtils.joinAll(null, "-"));
        Map<String, List<String>> expected = new HashMap<String, List<String>>();
        expected.put("key1", Arrays.asList("1:a", "2:b", "3:c"));
        expected.put("key2", Arrays.asList("1:a", "2:b"));
        expected.put("key3", null);
        expected.put("key4", new ArrayList<String>());
        Map<String, Map<String, String>> input = new HashMap<String, Map<String, String>>();
        input.put("key1", CollectionUtils.toStringMap("1", "a", "2", "b", "3", "c"));
        input.put("key2", CollectionUtils.toStringMap("1", "a", "2", "b"));
        input.put("key3", null);
        input.put("key4", new HashMap<String, String>());
        Map<String, List<String>> output = CollectionUtils.joinAll(input, ":");
        for (Map.Entry<String, List<String>> entry : output.entrySet()) {
            if ((entry.getValue()) == null)
                continue;

            Collections.sort(entry.getValue());
        }
        Assertions.assertEquals(expected, output);
    }

    @Test
    public void testJoinList() throws Exception {
        List<String> list = Arrays.asList();
        Assertions.assertEquals("", CollectionUtils.join(list, "/"));
        list = Arrays.asList("x");
        Assertions.assertEquals("x", CollectionUtils.join(list, "-"));
        list = Arrays.asList("a", "b");
        Assertions.assertEquals("a/b", CollectionUtils.join(list, "/"));
    }

    @Test
    public void testMapEquals() throws Exception {
        Assertions.assertTrue(CollectionUtils.mapEquals(null, null));
        Assertions.assertFalse(CollectionUtils.mapEquals(null, new HashMap<String, String>()));
        Assertions.assertFalse(CollectionUtils.mapEquals(new HashMap<String, String>(), null));
        Assertions.assertTrue(CollectionUtils.mapEquals(CollectionUtils.toStringMap("1", "a", "2", "b"), CollectionUtils.toStringMap("1", "a", "2", "b")));
        Assertions.assertFalse(CollectionUtils.mapEquals(CollectionUtils.toStringMap("1", "a"), CollectionUtils.toStringMap("1", "a", "2", "b")));
    }

    @Test
    public void testStringMap1() throws Exception {
        MatcherAssert.assertThat(CollectionUtils.toStringMap("key", "value"), Matchers.equalTo(Collections.singletonMap("key", "value")));
    }

    @Test
    public void testStringMap2() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> CollectionUtils.toStringMap("key", "value", "odd"));
    }

    @Test
    public void testToMap1() throws Exception {
        Assertions.assertTrue(CollectionUtils.toMap().isEmpty());
        Map<String, Integer> expected = new HashMap<String, Integer>();
        expected.put("a", 1);
        expected.put("b", 2);
        expected.put("c", 3);
        Assertions.assertEquals(expected, CollectionUtils.toMap("a", 1, "b", 2, "c", 3));
    }

    @Test
    public void testToMap2() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> CollectionUtils.toMap("a", "b", "c"));
    }

    @Test
    public void testIsEmpty() throws Exception {
        MatcherAssert.assertThat(CollectionUtils.isEmpty(null), Matchers.is(true));
        MatcherAssert.assertThat(CollectionUtils.isEmpty(new HashSet()), Matchers.is(true));
        MatcherAssert.assertThat(CollectionUtils.isEmpty(Collections.emptyList()), Matchers.is(true));
    }

    @Test
    public void testIsNotEmpty() throws Exception {
        MatcherAssert.assertThat(CollectionUtils.isNotEmpty(Collections.singleton("a")), Matchers.is(true));
    }
}

