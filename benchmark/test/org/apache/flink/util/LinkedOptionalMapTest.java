/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.util;


import java.util.LinkedHashMap;
import org.apache.flink.util.LinkedOptionalMap.MergeResult;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link LinkedOptionalMap}.
 */
public class LinkedOptionalMapTest {
    @Test
    public void usageExample() {
        LinkedOptionalMap<Class<?>, String> map = new LinkedOptionalMap();
        map.put("java.lang.String", String.class, "a string class");
        map.put("scala.Option", null, "a scala Option");
        map.put("java.lang.Boolean", Boolean.class, null);
        Assert.assertThat(map.keyNames(), Matchers.hasItems("java.lang.String", "scala.Option"));
        Assert.assertThat(map.absentKeysOrValues(), Matchers.hasItems("scala.Option", "java.lang.Boolean"));
    }

    @Test
    public void overridingKeyWithTheSameKeyName() {
        LinkedOptionalMap<Class<?>, String> map = new LinkedOptionalMap();
        map.put("java.lang.String", null, "a string class");
        map.put("java.lang.String", String.class, "a string class");
        Assert.assertThat(map.absentKeysOrValues(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void overridingKeysAndValuesWithTheSameKeyName() {
        LinkedOptionalMap<Class<?>, String> map = new LinkedOptionalMap();
        map.put("java.lang.String", null, null);
        map.put("java.lang.String", String.class, "a string class");
        Assert.assertThat(map.absentKeysOrValues(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void overridingAValueWithMissingKeyShouldBeConsideredAsAbsent() {
        LinkedOptionalMap<Class<?>, String> map = new LinkedOptionalMap();
        map.put("java.lang.String", null, null);
        map.put("java.lang.String", null, "a string class");
        Assert.assertThat(map.absentKeysOrValues(), Matchers.hasItem("java.lang.String"));
    }

    @Test
    public void mergingMapsWithPresentEntriesLeavesNoAbsentKeyNames() {
        LinkedOptionalMap<Class<?>, String> first = new LinkedOptionalMap();
        first.put("b", null, null);
        first.put("c", String.class, null);
        LinkedOptionalMap<Class<?>, String> second = new LinkedOptionalMap();
        second.put("a", String.class, "aaa");
        second.put("b", String.class, "bbb");
        second.put("c", Void.class, "ccc");
        second.put("d", String.class, "ddd");
        first.putAll(second);
        Assert.assertThat(first.absentKeysOrValues(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void mergingMapsPreserversTheOrderOfTheOriginalMap() {
        LinkedOptionalMap<Class<?>, String> first = new LinkedOptionalMap();
        first.put("b", null, null);
        first.put("c", String.class, null);
        LinkedOptionalMap<Class<?>, String> second = new LinkedOptionalMap();
        second.put("a", String.class, "aaa");
        second.put("b", String.class, "bbb");
        second.put("c", Void.class, "ccc");
        second.put("d", String.class, "ddd");
        first.putAll(second);
        Assert.assertThat(first.keyNames(), Matchers.contains("b", "c", "a", "d"));
    }

    @Test
    public void mergingToEmpty() {
        LinkedOptionalMap<Class<?>, String> first = new LinkedOptionalMap();
        LinkedOptionalMap<Class<?>, String> second = new LinkedOptionalMap();
        second.put("a", String.class, "aaa");
        second.put("b", String.class, "bbb");
        second.put("c", Void.class, "ccc");
        second.put("d", String.class, "ddd");
        first.putAll(second);
        Assert.assertThat(first.keyNames(), Matchers.contains("a", "b", "c", "d"));
    }

    @Test(expected = IllegalStateException.class)
    public void unwrapOptionalsWithMissingValueThrows() {
        LinkedOptionalMap<Class<?>, String> map = new LinkedOptionalMap();
        map.put("a", String.class, null);
        map.unwrapOptionals();
    }

    @Test(expected = IllegalStateException.class)
    public void unwrapOptionalsWithMissingKeyThrows() {
        LinkedOptionalMap<Class<?>, String> map = new LinkedOptionalMap();
        map.put("a", null, "blabla");
        map.unwrapOptionals();
    }

    @Test
    public void unwrapOptionalsPreservesOrder() {
        LinkedOptionalMap<Class<?>, String> map = new LinkedOptionalMap();
        map.put("a", String.class, "aaa");
        map.put("b", Boolean.class, "bbb");
        LinkedHashMap<Class<?>, String> m = map.unwrapOptionals();
        Assert.assertThat(m.keySet(), Matchers.contains(String.class, Boolean.class));
        Assert.assertThat(m.values(), Matchers.contains("aaa", "bbb"));
    }

    @Test
    public void testPrefix() {
        LinkedOptionalMap<Class<?>, String> left = new LinkedOptionalMap();
        left.put("a", String.class, "aaa");
        left.put("b", String.class, "aaa");
        LinkedOptionalMap<Class<?>, String> right = new LinkedOptionalMap(left);
        right.put("c", Boolean.class, "bbb");
        Assert.assertTrue(LinkedOptionalMap.isLeftPrefixOfRight(left, right));
    }

    @Test
    public void testNonPrefix() {
        LinkedOptionalMap<Class<?>, String> left = new LinkedOptionalMap();
        left.put("a", String.class, "aaa");
        left.put("c", String.class, "aaa");
        LinkedOptionalMap<Class<?>, String> right = new LinkedOptionalMap();
        right.put("b", Boolean.class, "bbb");
        right.put("c", Boolean.class, "bbb");
        Assert.assertFalse(LinkedOptionalMap.isLeftPrefixOfRight(left, right));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void demoMergeResult() {
        LinkedOptionalMap<Class<?>, String> left = new LinkedOptionalMap();
        left.put("b", null, null);
        left.put("c", String.class, null);
        LinkedOptionalMap<Class<?>, String> right = new LinkedOptionalMap();
        right.put("b", String.class, "bbb");
        right.put("c", Void.class, "ccc");
        right.put("a", Boolean.class, "aaa");
        right.put("d", Long.class, "ddd");
        MergeResult<Class<?>, String> result = LinkedOptionalMap.mergeRightIntoLeft(left, right);
        Assert.assertThat(result.hasMissingKeys(), Matchers.is(false));
        Assert.assertThat(result.isOrderedSubset(), Matchers.is(true));
        Assert.assertThat(result.missingKeys(), Matchers.is(Matchers.empty()));
        LinkedHashMap<Class<?>, String> merged = result.getMerged();
        Assert.assertThat(merged.keySet(), Matchers.contains(String.class, Void.class, Boolean.class, Long.class));
    }
}

