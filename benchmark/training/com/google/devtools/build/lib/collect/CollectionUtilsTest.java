/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.collect;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import java.util.EnumSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link CollectionUtils}.
 */
@RunWith(JUnit4.class)
public class CollectionUtilsTest {
    @Test
    public void testDuplicatedElementsOf() {
        CollectionUtilsTest.assertDups(ImmutableList.<Integer>of(), ImmutableSet.<Integer>of());
        CollectionUtilsTest.assertDups(ImmutableList.of(0), ImmutableSet.<Integer>of());
        CollectionUtilsTest.assertDups(ImmutableList.of(0, 0, 0), ImmutableSet.of(0));
        CollectionUtilsTest.assertDups(ImmutableList.of(1, 2, 3, 1, 2, 3), ImmutableSet.of(1, 2, 3));
        CollectionUtilsTest.assertDups(ImmutableList.of(1, 2, 3, 1, 2, 3, 4), ImmutableSet.of(1, 2, 3));
        CollectionUtilsTest.assertDups(ImmutableList.of(1, 2, 3, 4), ImmutableSet.<Integer>of());
    }

    @Test
    public void testIsImmutable() throws Exception {
        assertThat(CollectionUtils.isImmutable(ImmutableList.of(1, 2, 3))).isTrue();
        assertThat(CollectionUtils.isImmutable(ImmutableSet.of(1, 2, 3))).isTrue();
        NestedSet<Integer> ns = NestedSetBuilder.<Integer>compileOrder().add(1).add(2).add(3).build();
        assertThat(CollectionUtils.isImmutable(ns)).isTrue();
        NestedSet<Integer> ns2 = NestedSetBuilder.<Integer>linkOrder().add(1).add(2).add(3).build();
        assertThat(CollectionUtils.isImmutable(ns2)).isTrue();
        Iterable<Integer> chain = IterablesChain.<Integer>builder().addElement(1).build();
        assertThat(CollectionUtils.isImmutable(chain)).isTrue();
        assertThat(CollectionUtils.isImmutable(Lists.newArrayList())).isFalse();
        assertThat(CollectionUtils.isImmutable(Lists.newLinkedList())).isFalse();
        assertThat(CollectionUtils.isImmutable(Sets.newHashSet())).isFalse();
        assertThat(CollectionUtils.isImmutable(Sets.newLinkedHashSet())).isFalse();
        // The result of Iterables.concat() actually is immutable, but we have no way of checking if
        // a given Iterable comes from concat().
        assertThat(CollectionUtils.isImmutable(Iterables.concat(ns, ns2))).isFalse();
        // We can override the check by using the ImmutableIterable wrapper.
        assertThat(CollectionUtils.isImmutable(ImmutableIterable.from(Iterables.concat(ns, ns2)))).isTrue();
    }

    @Test
    public void testCheckImmutable() throws Exception {
        CollectionUtils.checkImmutable(ImmutableList.of(1, 2, 3));
        CollectionUtils.checkImmutable(ImmutableSet.of(1, 2, 3));
        try {
            CollectionUtils.checkImmutable(Lists.newArrayList(1, 2, 3));
        } catch (IllegalStateException e) {
            return;
        }
        Assert.fail();
    }

    @Test
    public void testMakeImmutable() throws Exception {
        Iterable<Integer> immutableList = ImmutableList.of(1, 2, 3);
        assertThat(CollectionUtils.makeImmutable(immutableList)).isSameAs(immutableList);
        Iterable<Integer> mutableList = Lists.newArrayList(1, 2, 3);
        Iterable<Integer> converted = CollectionUtils.makeImmutable(mutableList);
        assertThat(converted).isNotSameAs(mutableList);
        assertThat(ImmutableList.copyOf(converted)).isEqualTo(mutableList);
    }

    private static enum Small {

        ALPHA,
        BRAVO;}

    private static enum Large {

        L0,
        L1,
        L2,
        L3,
        L4,
        L5,
        L6,
        L7,
        L8,
        L9,
        L10,
        L11,
        L12,
        L13,
        L14,
        L15,
        L16,
        L17,
        L18,
        L19,
        L20,
        L21,
        L22,
        L23,
        L24,
        L25,
        L26,
        L27,
        L28,
        L29,
        L30,
        L31;}

    private static enum TooLarge {

        T0,
        T1,
        T2,
        T3,
        T4,
        T5,
        T6,
        T7,
        T8,
        T9,
        T10,
        T11,
        T12,
        T13,
        T14,
        T15,
        T16,
        T17,
        T18,
        T19,
        T20,
        T21,
        T22,
        T23,
        T24,
        T25,
        T26,
        T27,
        T28,
        T29,
        T30,
        T31,
        T32;}

    private static enum Medium {

        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT;}

    @Test
    public void testEnumBitfields() throws Exception {
        assertThat(CollectionUtils.<CollectionUtilsTest.Small>toBits()).isEqualTo(0);
        assertThat(CollectionUtils.fromBits(0, CollectionUtilsTest.Small.class)).isEqualTo(EnumSet.noneOf(CollectionUtilsTest.Small.class));
        assertThat(CollectionUtils.toBits(CollectionUtilsTest.Small.ALPHA, CollectionUtilsTest.Small.BRAVO)).isEqualTo(3);
        assertThat(CollectionUtils.toBits(CollectionUtilsTest.Medium.TWO, CollectionUtilsTest.Medium.FOUR)).isEqualTo(10);
        assertThat(CollectionUtils.fromBits(192, CollectionUtilsTest.Medium.class)).isEqualTo(EnumSet.of(CollectionUtilsTest.Medium.SEVEN, CollectionUtilsTest.Medium.EIGHT));
        assertAllDifferent(CollectionUtilsTest.Small.class);
        assertAllDifferent(CollectionUtilsTest.Medium.class);
        assertAllDifferent(CollectionUtilsTest.Large.class);
        try {
            CollectionUtils.toBits(CollectionUtilsTest.TooLarge.T32);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // good
        }
        try {
            CollectionUtils.fromBits(0, CollectionUtilsTest.TooLarge.class);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // good
        }
    }
}

