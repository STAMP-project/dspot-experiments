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
package io.helidon.config.internal;


import io.helidon.common.CollectionsHelper;
import io.helidon.config.ConfigException;
import io.helidon.config.ValueNodeMatcher;
import io.helidon.config.spi.ConfigNode.ListNode;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigNode.ValueNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link ObjectNodeBuilderImpl}.
 */
public class ObjectNodeBuilderImplTest {
    @Test
    public void testEmpty() {
        ObjectNodeBuilderImpl builder = new ObjectNodeBuilderImpl();
        MatcherAssert.assertThat(builder.build().entrySet(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void testMergeValueToValue() {
        ObjectNode rootNode = new ObjectNodeBuilderImpl().addValue("top1.prop1", "1").addValue("top1.prop1", "2").build();
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(1));
        MatcherAssert.assertThat(((ObjectNode) (rootNode.get("top1"))).get("prop1"), ValueNodeMatcher.valueNode("2"));
    }

    @Test
    public void testMergeObjectWithNonNumberKeyToList() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            new ObjectNodeBuilderImpl().addList("top1.prop1", ListNode.builder().addValue("2").build()).addValue("top1.prop1.sub1", "1").build();
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("top1", "prop1", "merge", "OBJECT", "'sub1'", "LIST", "not a number")));
    }

    @Test
    public void testMergeObjectWithNumberKeyToList() {
        ObjectNode rootNode = new ObjectNodeBuilderImpl().addList("top1.prop1", ListNode.builder().addValue("2").build()).addValue("top1.prop1.0", "1").build();
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(1));
        ListNode listNode = ((ListNode) (((ObjectNode) (rootNode.get("top1"))).get("prop1")));
        MatcherAssert.assertThat(listNode, Matchers.hasSize(1));
        MatcherAssert.assertThat(listNode.get(0), ValueNodeMatcher.valueNode("1"));
    }

    @Test
    public void testMergeObjectWithNumberKeyOutOfBoundsToList() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            new ObjectNodeBuilderImpl().addList("top1.prop1", ListNode.builder().addValue("1").addValue("2").build()).addValue("top1.prop1.2", "1").build();
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("top1", "prop1", "merge", "OBJECT", "'2'", "LIST", "out of bounds")));
    }

    @Test
    public void testMergeObjectWithNegativeNumberKeyOutOfBoundsToList() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            new ObjectNodeBuilderImpl().addList("top1.prop1", ListNode.builder().addValue("2").build()).addValue("top1.prop1.-1", "1").build();
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("top1", "prop1", "merge", "OBJECT", "'-1'", "LIST", "negative index")));
    }

    @Test
    public void testMergeObjectToObject() {
        ObjectNode rootNode = new ObjectNodeBuilderImpl().addValue("top1.prop1.sub1", "1").addValue("top1.prop1.sub2", "2").build();
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(1));
        ObjectNode objectNode = ((ObjectNode) (((ObjectNode) (rootNode.get("top1"))).get("prop1")));
        MatcherAssert.assertThat(objectNode.entrySet(), Matchers.hasSize(2));
        MatcherAssert.assertThat(objectNode.get("sub1"), ValueNodeMatcher.valueNode("1"));
        MatcherAssert.assertThat(objectNode.get("sub2"), ValueNodeMatcher.valueNode("2"));
    }

    @Test
    public void testMergeListToList() {
        ObjectNode rootNode = new ObjectNodeBuilderImpl().addList("top1.prop1", ListNode.builder().addValue("3").build()).addList("top1.prop1", ListNode.builder().addValue("1").addValue("2").build()).build();
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(1));
        ListNode listNode = ((ListNode) (((ObjectNode) (rootNode.get("top1"))).get("prop1")));
        MatcherAssert.assertThat(listNode, Matchers.hasSize(2));
        MatcherAssert.assertThat(listNode.get(0), ValueNodeMatcher.valueNode("1"));
        MatcherAssert.assertThat(listNode.get(1), ValueNodeMatcher.valueNode("2"));
    }

    @Test
    public void testMergeListToObjectWithNonNumberKey() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            new ObjectNodeBuilderImpl().addValue("top1.prop1.sub1", "2").addList("top1.prop1", ListNode.builder().addValue("1").build()).build();
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("top1", "prop1", "merge", "LIST", "OBJECT")));
    }

    @Test
    public void testMergeListToObjectWithNumberKey() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            new ObjectNodeBuilderImpl().addValue("top1.prop1.0", "2").addList("top1.prop1", ListNode.builder().addValue("1").build()).build();
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("top1", "prop1", "merge", "LIST", "OBJECT")));
    }

    @Test
    public void testComplex() {
        ObjectNodeBuilderImpl builder = new ObjectNodeBuilderImpl();
        builder.addValue("key1", "value1");
        builder.addObject("obj1", ObjectNode.builder().addValue("key3", "value3").build());
        builder.addValue("obj1.key2", "value2");
        builder.addObject("obj2", ObjectNode.builder().addValue("key4", "value4").addValue("obj3.key5", "value5").build());
        builder.addList("array1", ListNode.builder().addValue("another prop1").build());
        ObjectNode objectNode = builder.build();
        MatcherAssert.assertThat(objectNode.entrySet(), Matchers.hasSize(4));
        // key1
        MatcherAssert.assertThat(objectNode.get("key1"), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(objectNode.get("key1"), ValueNodeMatcher.valueNode("value1"));
        // obj1.key2
        MatcherAssert.assertThat(((ObjectNode) (objectNode.get("obj1"))).get("key2"), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(((ObjectNode) (objectNode.get("obj1"))).get("key2"), ValueNodeMatcher.valueNode("value2"));
        // obj1.key3
        MatcherAssert.assertThat(((ObjectNode) (objectNode.get("obj1"))).get("key3"), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(((ObjectNode) (objectNode.get("obj1"))).get("key3"), ValueNodeMatcher.valueNode("value3"));
        // obj2.key4
        MatcherAssert.assertThat(((ObjectNode) (objectNode.get("obj2"))).get("key4"), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(((ObjectNode) (objectNode.get("obj2"))).get("key4"), ValueNodeMatcher.valueNode("value4"));
        // obj2.obj3.key5
        MatcherAssert.assertThat(((ObjectNode) (((ObjectNode) (objectNode.get("obj2"))).get("obj3"))).get("key5"), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(((ObjectNode) (((ObjectNode) (objectNode.get("obj2"))).get("obj3"))).get("key5"), ValueNodeMatcher.valueNode("value5"));
        // array1
        MatcherAssert.assertThat(objectNode.get("array1"), Matchers.instanceOf(ListNode.class));
        MatcherAssert.assertThat(get(0), ValueNodeMatcher.valueNode("another prop1"));
    }

    @Test
    public void testComplexThroughSubNodes() {
        ObjectNodeBuilderImpl builder = new ObjectNodeBuilderImpl();
        builder.addValue("key1", "value1");
        builder.addObject("obj1", ObjectNode.builder().addValue("key3", "value3").addValue("key2", "value2").build());
        builder.addObject("obj2", ObjectNode.builder().addValue("key4", "value4").addObject("obj3", ObjectNode.builder().addValue("key5", "value5").build()).build());
        builder.addList("array1", ListNode.builder().addValue("another prop1").build());
        ObjectNode objectNode = builder.build();
        MatcherAssert.assertThat(objectNode.entrySet(), Matchers.hasSize(4));
        // key1
        MatcherAssert.assertThat(objectNode.get("key1"), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(objectNode.get("key1"), ValueNodeMatcher.valueNode("value1"));
        // obj1
        ObjectNode obj1 = ((ObjectNode) (objectNode.get("obj1")));
        // obj1.key2
        MatcherAssert.assertThat(obj1.get("key2"), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(obj1.get("key2"), ValueNodeMatcher.valueNode("value2"));
        // obj1.key3
        MatcherAssert.assertThat(obj1.get("key3"), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(obj1.get("key3"), ValueNodeMatcher.valueNode("value3"));
        // obj2
        ObjectNode obj2 = ((ObjectNode) (objectNode.get("obj2")));
        // obj2.key4
        MatcherAssert.assertThat(obj2.get("key4"), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(obj2.get("key4"), ValueNodeMatcher.valueNode("value4"));
        // obj2
        ObjectNode obj3 = ((ObjectNode) (obj2.get("obj3")));
        // obj2.obj3.key5
        MatcherAssert.assertThat(obj3.get("key5"), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(obj3.get("key5"), ValueNodeMatcher.valueNode("value5"));
        // array1
        MatcherAssert.assertThat(objectNode.get("array1"), Matchers.instanceOf(ListNode.class));
        MatcherAssert.assertThat(get(0), ValueNodeMatcher.valueNode("another prop1"));
    }

    @Test
    public void testResolveTokenFunction() {
        ObjectNode rootNode = new ObjectNodeBuilderImpl(( s) -> {
            if (s.equals("$host")) {
                return "localhost";
            }
            return s;
        }).addValue("host", "localhost").addValue("$host", "2").build();
        MatcherAssert.assertThat(rootNode.entrySet(), Matchers.hasSize(2));
        MatcherAssert.assertThat(rootNode.get("host"), ValueNodeMatcher.valueNode("localhost"));
        MatcherAssert.assertThat(rootNode.get("localhost"), ValueNodeMatcher.valueNode("2"));
    }
}

