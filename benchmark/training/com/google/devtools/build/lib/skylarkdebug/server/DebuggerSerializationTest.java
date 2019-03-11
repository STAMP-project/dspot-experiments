/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.skylarkdebug.server;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.Value;
import com.google.devtools.build.lib.skylarkinterface.SkylarkCallable;
import com.google.devtools.build.lib.skylarkinterface.SkylarkPrinter;
import com.google.devtools.build.lib.skylarkinterface.SkylarkValue;
import com.google.devtools.build.lib.syntax.SkylarkNestedSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link DebuggerSerialization}.
 */
@RunWith(JUnit4.class)
public final class DebuggerSerializationTest {
    private final ThreadObjectMap dummyObjectMap = new ThreadObjectMap();

    @Test
    public void testSimpleNestedSet() {
        Set<String> children = ImmutableSet.of("a", "b");
        SkylarkNestedSet set = SkylarkNestedSet.of(Object.class, NestedSetBuilder.stableOrder().addAll(children).build());
        Value value = getValueProto("name", set);
        DebuggerSerializationTest.assertTypeAndDescription(set, value);
        assertThat(value.getHasChildren()).isTrue();
        assertThat(value.getLabel()).isEqualTo("name");
        List<Value> childValues = getChildren(value);
        assertThat(childValues.get(0)).isEqualTo(Value.newBuilder().setLabel("order").setType("Traversal order").setDescription("default").build());
        assertEqualIgnoringTypeDescriptionAndId(childValues.get(1), getValueProto("directs", children));
        assertEqualIgnoringTypeDescriptionAndId(childValues.get(2), getValueProto("transitives", ImmutableList.of()));
    }

    @Test
    public void testNestedSetWithNestedChildren() {
        NestedSet<String> innerNestedSet = NestedSetBuilder.<String>stableOrder().add("inner1").add("inner2").build();
        ImmutableSet<String> directChildren = ImmutableSet.of("a", "b");
        SkylarkNestedSet outerSet = SkylarkNestedSet.of(String.class, NestedSetBuilder.<String>linkOrder().addAll(directChildren).addTransitive(innerNestedSet).build());
        Value value = getValueProto("name", outerSet);
        List<Value> childValues = getChildren(value);
        DebuggerSerializationTest.assertTypeAndDescription(outerSet, value);
        assertThat(childValues).hasSize(3);
        assertThat(childValues.get(0)).isEqualTo(Value.newBuilder().setLabel("order").setType("Traversal order").setDescription("topological").build());
        assertEqualIgnoringTypeDescriptionAndId(childValues.get(1), getValueProto("directs", directChildren));
        assertEqualIgnoringTypeDescriptionAndId(childValues.get(2), getValueProto("transitives", ImmutableList.of(new com.google.devtools.build.lib.collect.nestedset.NestedSetView(innerNestedSet))));
    }

    @Test
    public void testSimpleMap() {
        Map<String, Integer> map = ImmutableMap.of("a", 1, "b", 2);
        Value value = getValueProto("name", map);
        List<Value> childValues = getChildren(value);
        DebuggerSerializationTest.assertTypeAndDescription(map, value);
        assertThat(childValues).hasSize(2);
        assertThat(childValues.get(0).getLabel()).isEqualTo("[0]");
        assertThat(getChildren(childValues.get(0))).isEqualTo(ImmutableList.of(getValueProto("key", "a"), getValueProto("value", 1)));
        assertThat(childValues.get(1).getLabel()).isEqualTo("[1]");
        assertThat(getChildren(childValues.get(1))).isEqualTo(ImmutableList.of(getValueProto("key", "b"), getValueProto("value", 2)));
    }

    @Test
    public void testNestedMap() {
        Set<String> set = ImmutableSet.of("a", "b");
        Map<String, Object> map = ImmutableMap.of("a", set);
        Value value = getValueProto("name", map);
        List<Value> childValues = getChildren(value);
        DebuggerSerializationTest.assertTypeAndDescription(map, value);
        assertThat(childValues).hasSize(1);
        assertThat(childValues.get(0).getLabel()).isEqualTo("[0]");
        assertThat(clearIds(getChildren(childValues.get(0)))).isEqualTo(ImmutableList.of(getValueProto("key", "a"), clearId(getValueProto("value", set))));
    }

    @Test
    public void testSimpleIterable() {
        Iterable<Integer> iter = ImmutableList.of(1, 2);
        Value value = getValueProto("name", iter);
        List<Value> childValues = getChildren(value);
        DebuggerSerializationTest.assertTypeAndDescription(iter, value);
        assertThat(childValues).hasSize(2);
        assertThat(childValues.get(0)).isEqualTo(getValueProto("[0]", 1));
        assertThat(childValues.get(1)).isEqualTo(getValueProto("[1]", 2));
    }

    @Test
    public void testNestedIterable() {
        Iterable<Object> iter = ImmutableList.of(ImmutableList.of(1, 2));
        Value value = getValueProto("name", iter);
        List<Value> childValues = getChildren(value);
        DebuggerSerializationTest.assertTypeAndDescription(iter, value);
        assertThat(childValues).hasSize(1);
        assertValuesEqualIgnoringId(childValues.get(0), getValueProto("[0]", ImmutableList.of(1, 2)));
    }

    @Test
    public void testSimpleArray() {
        int[] array = new int[]{ 1, 2 };
        Value value = getValueProto("name", array);
        List<Value> childValues = getChildren(value);
        DebuggerSerializationTest.assertTypeAndDescription(array, value);
        assertThat(childValues).hasSize(2);
        assertThat(childValues.get(0)).isEqualTo(getValueProto("[0]", 1));
        assertThat(childValues.get(1)).isEqualTo(getValueProto("[1]", 2));
    }

    @Test
    public void testNestedArray() {
        Object[] array = new Object[]{ 1, ImmutableList.of(2, 3) };
        Value value = getValueProto("name", array);
        List<Value> childValues = getChildren(value);
        DebuggerSerializationTest.assertTypeAndDescription(array, value);
        assertThat(childValues).hasSize(2);
        assertThat(childValues.get(0)).isEqualTo(getValueProto("[0]", 1));
        assertValuesEqualIgnoringId(childValues.get(1), getValueProto("[1]", ImmutableList.of(2, 3)));
    }

    @Test
    public void testUnrecognizedObjectOrSkylarkPrimitiveHasNoChildren() {
        assertThat(getValueProto("name", 1).getHasChildren()).isFalse();
        assertThat(getValueProto("name", "string").getHasChildren()).isFalse();
        assertThat(getValueProto("name", new Object()).getHasChildren()).isFalse();
    }

    @Test
    public void testSkylarkValue() {
        DebuggerSerializationTest.DummyType dummy = new DebuggerSerializationTest.DummyType();
        Value value = getValueProto("name", dummy);
        DebuggerSerializationTest.assertTypeAndDescription(dummy, value);
        assertThat(getChildren(value)).containsExactly(getValueProto("bool", true));
    }

    private static class DummyType implements SkylarkValue {
        @Override
        public void repr(SkylarkPrinter printer) {
            printer.append("DummyType");
        }

        @SkylarkCallable(name = "bool", doc = "Returns True", structField = true)
        public boolean bool() {
            return true;
        }

        public boolean anotherMethod() {
            return false;
        }
    }

    @Test
    public void testSkipSkylarkCallableThrowingException() {
        DebuggerSerializationTest.DummyTypeWithException dummy = new DebuggerSerializationTest.DummyTypeWithException();
        Value value = getValueProto("name", dummy);
        DebuggerSerializationTest.assertTypeAndDescription(dummy, value);
        assertThat(getChildren(value)).containsExactly(getValueProto("bool", true));
    }

    private static class DummyTypeWithException implements SkylarkValue {
        @Override
        public void repr(SkylarkPrinter printer) {
            printer.append("DummyTypeWithException");
        }

        @SkylarkCallable(name = "bool", doc = "Returns True", structField = true)
        public boolean bool() {
            return true;
        }

        @SkylarkCallable(name = "invalid", doc = "Throws exception!", structField = true)
        public boolean invalid() {
            throw new IllegalArgumentException();
        }

        public boolean anotherMethod() {
            return false;
        }
    }
}

