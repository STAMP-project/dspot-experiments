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


import io.helidon.config.ValueNodeMatcher;
import io.helidon.config.spi.ConfigNode.ListNode;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigNode.ValueNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link ListNodeBuilderImpl}.
 */
public class ListNodeBuilderImplTest {
    @Test
    public void testEmpty() {
        ListNodeBuilderImpl builder = new ListNodeBuilderImpl();
        MatcherAssert.assertThat(builder.build(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void testComplex() {
        ListNodeBuilderImpl builder = new ListNodeBuilderImpl();
        builder.addValue("text value");
        builder.addList(ListNode.builder().addValue("another value").build());
        builder.addObject(ObjectNode.builder().addValue("obj1.key1", "value1").build());
        ListNode listNode = builder.build();
        MatcherAssert.assertThat(listNode, Matchers.hasSize(3));
        // 0
        MatcherAssert.assertThat(listNode.get(0), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(listNode.get(0), ValueNodeMatcher.valueNode("text value"));
        // 1
        MatcherAssert.assertThat(listNode.get(1), Matchers.instanceOf(ListNode.class));
        MatcherAssert.assertThat(((ListNode) (listNode.get(1))).get(0), ValueNodeMatcher.valueNode("another value"));
        // 2
        MatcherAssert.assertThat(listNode.get(2), Matchers.instanceOf(ObjectNode.class));
        MatcherAssert.assertThat(get("key1"), ValueNodeMatcher.valueNode("value1"));
    }

    @Test
    public void testMerge() {
        ObjectNode objectNode = new ObjectNodeBuilderImpl().addList("top1.prop1", new ListNodeBuilderImpl().addValue("text value").addList(ListNode.builder().addValue("another value").build()).addObject(ObjectNode.builder().addValue("obj1.key1", "value1").build()).build()).addValue("top1.prop1.0", "new text value").addValue("top1.prop1.1.0", "another another value").addValue("top1.prop1.2.obj1.key1", "value2").build();
        ListNode listNode = ((ListNode) (get("prop1")));
        MatcherAssert.assertThat(listNode, Matchers.hasSize(3));
        // 0
        MatcherAssert.assertThat(listNode.get(0), Matchers.instanceOf(ValueNode.class));
        MatcherAssert.assertThat(listNode.get(0), ValueNodeMatcher.valueNode("new text value"));
        // 1
        MatcherAssert.assertThat(listNode.get(1), Matchers.instanceOf(ListNode.class));
        MatcherAssert.assertThat(((ListNode) (listNode.get(1))).get(0), ValueNodeMatcher.valueNode("another another value"));
        // 2
        MatcherAssert.assertThat(listNode.get(2), Matchers.instanceOf(ObjectNode.class));
        MatcherAssert.assertThat(get("key1"), ValueNodeMatcher.valueNode("value2"));
    }
}

