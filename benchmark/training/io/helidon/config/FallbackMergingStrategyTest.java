/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config;


import io.helidon.common.CollectionsHelper;
import io.helidon.config.spi.ConfigNode.ListNode;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link FallbackMergingStrategy}.
 */
public class FallbackMergingStrategyTest {
    @Test
    public void testMergeEmptyList() {
        ObjectNode rootNode = mergeLoads();
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(0));
    }

    @Test
    public void testMergeSingleSource() {
        ObjectNode rootNode = mergeLoads(ConfigSources.create(ObjectNode.builder().addValue("top1.prop1", "1").build()));
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(1));
        MatcherAssert.assertThat(get("prop1"), ValueNodeMatcher.valueNode("1"));
    }

    @Test
    public void testMergeValueToValueNew() {
        ObjectNode rootNode = mergeLoads(ConfigSources.create(ObjectNode.builder().addValue("top1.prop1", "1").build()), ConfigSources.create(ObjectNode.builder().addValue("top1.prop1", "2").build()));
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(1));
        MatcherAssert.assertThat(get("prop1"), ValueNodeMatcher.valueNode("1"));
    }

    @Test
    public void testMergeValueToValue() {
        ObjectNode rootNode = mergeLoads(ConfigSources.create(ObjectNode.builder().addValue("top1.prop1", "1").build()), ConfigSources.create(ObjectNode.builder().addValue("top1.prop1", "2").build()));
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(1));
        MatcherAssert.assertThat(get("prop1"), ValueNodeMatcher.valueNode("1"));
    }

    @Test
    public void testMergeObjectWithNonNumberKeyToList() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            mergeLoads(ConfigSources.create(ObjectNode.builder().addValue("top1.prop1.sub1", "1").build()), ConfigSources.create(ObjectNode.builder().addList("top1.prop1", ListNode.builder().addValue("2").build()).build()));
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("top1", "prop1", "merge", "OBJECT", "[sub1]", "LIST")));
    }

    @Test
    public void testMergeObjectWithNumberKeyToList() {
        ObjectNode rootNode = mergeLoads(ConfigSources.create(ObjectNode.builder().addValue("top1.prop1.0", "1").build()), ConfigSources.create(ObjectNode.builder().addList("top1.prop1", ListNode.builder().addValue("2").build()).build()));
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(1));
        ListNode listNode = ((ListNode) (get("prop1")));
        MatcherAssert.assertThat(listNode, Matchers.hasSize(1));
        MatcherAssert.assertThat(listNode.get(0), ValueNodeMatcher.valueNode("1"));
    }

    @Test
    public void testMergeObjectWithNumberKeyOutOfBoundsToList() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            mergeLoads(ConfigSources.create(ObjectNode.builder().addValue("top1.prop1.1", "1").build()), ConfigSources.create(ObjectNode.builder().addList("top1.prop1", ListNode.builder().addValue("2").build()).build()));
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("top1", "prop1", "merge", "OBJECT", "[1]", "LIST")));
    }

    @Test
    public void testMergeObjectToObject() {
        ObjectNode rootNode = mergeLoads(ConfigSources.create(ObjectNode.builder().addValue("top1.prop1.sub1", "1").build()), ConfigSources.create(ObjectNode.builder().addValue("top1.prop1.sub2", "2").build()));
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(1));
        ObjectNode objectNode = ((ObjectNode) (get("prop1")));
        MatcherAssert.assertThat(objectNode.entrySet(), Matchers.hasSize(2));
        MatcherAssert.assertThat(objectNode.get("sub1"), ValueNodeMatcher.valueNode("1"));
        MatcherAssert.assertThat(objectNode.get("sub2"), ValueNodeMatcher.valueNode("2"));
    }

    @Test
    public void testMergeListToList() {
        ObjectNode rootNode = mergeLoads(ConfigSources.create(ObjectNode.builder().addList("top1.prop1", ListNode.builder().addValue("1").addValue("2").build()).build()), ConfigSources.create(ObjectNode.builder().addList("top1.prop1", ListNode.builder().addValue("3").build()).build()));
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(1));
        ListNode listNode = ((ListNode) (get("prop1")));
        MatcherAssert.assertThat(listNode, Matchers.hasSize(2));
        MatcherAssert.assertThat(listNode.get(0), ValueNodeMatcher.valueNode("1"));
        MatcherAssert.assertThat(listNode.get(1), ValueNodeMatcher.valueNode("2"));
    }

    @Test
    public void testMergeListToObjectWithNonNumberKey() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            mergeLoads(ConfigSources.create(ObjectNode.builder().addList("top1.prop1", ListNode.builder().addValue("1").build()).build()), ConfigSources.create(ObjectNode.builder().addValue("top1.prop1.sub1", "2").build()));
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("top1", "prop1", "merge", "LIST", "OBJECT")));
    }

    @Test
    public void testMergeListToObjectWithNumberKey() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            mergeLoads(ConfigSources.create(ObjectNode.builder().addList("top1.prop1", ListNode.builder().addValue("1").build()).build()), ConfigSources.create(ObjectNode.builder().addValue("top1.prop1.0", "2").build()));
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("top1", "prop1", "merge", "LIST", "OBJECT")));
    }

    @Test
    public void testMergeWithNewKeys() {
        ObjectNode rootNode = mergeLoads(ConfigSources.create(ObjectNode.builder().addValue("a-prop1", "1").addList("a-list", ListNode.builder().addValue("2").addValue("3").build()).addObject("a-object", ObjectNode.builder().addValue("prop1", "4").build()).build()), ConfigSources.create(ObjectNode.builder().addValue("b-prop1", "11").addList("b-list", ListNode.builder().addValue("12").addValue("13").build()).addObject("b-object", ObjectNode.builder().addValue("prop1", "14").build()).build()));
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(6));
        // values
        MatcherAssert.assertThat(rootNode.get("a-prop1"), ValueNodeMatcher.valueNode("1"));
        MatcherAssert.assertThat(rootNode.get("b-prop1"), ValueNodeMatcher.valueNode("11"));
        // lists
        ListNode aListNode = ((ListNode) (rootNode.get("a-list")));
        MatcherAssert.assertThat(aListNode, Matchers.hasSize(2));
        MatcherAssert.assertThat(aListNode.get(0), ValueNodeMatcher.valueNode("2"));
        MatcherAssert.assertThat(aListNode.get(1), ValueNodeMatcher.valueNode("3"));
        ListNode bListNode = ((ListNode) (rootNode.get("b-list")));
        MatcherAssert.assertThat(bListNode, Matchers.hasSize(2));
        MatcherAssert.assertThat(bListNode.get(0), ValueNodeMatcher.valueNode("12"));
        MatcherAssert.assertThat(bListNode.get(1), ValueNodeMatcher.valueNode("13"));
        // objects
        ObjectNode aObjectNode = ((ObjectNode) (rootNode.get("a-object")));
        MatcherAssert.assertThat(aObjectNode.entrySet(), Matchers.hasSize(1));
        MatcherAssert.assertThat(aObjectNode.get("prop1"), ValueNodeMatcher.valueNode("4"));
        ObjectNode bObjectNode = ((ObjectNode) (rootNode.get("b-object")));
        MatcherAssert.assertThat(bObjectNode.entrySet(), Matchers.hasSize(1));
        MatcherAssert.assertThat(bObjectNode.get("prop1"), ValueNodeMatcher.valueNode("14"));
    }
}

