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
package com.google.devtools.build.lib.collect.nestedset;


import Order.STABLE_ORDER;
import com.google.common.testing.EqualsTester;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link com.google.devtools.build.lib.collect.nestedset.NestedSet}.
 */
@RunWith(JUnit4.class)
public class NestedSetImplTest {
    @Test
    public void simple() {
        NestedSet<String> set = NestedSetImplTest.nestedSetBuilder("a").build();
        assertThat(set.toList()).containsExactly("a");
        assertThat(set.isEmpty()).isFalse();
    }

    @Test
    public void flatToString() {
        assertThat(NestedSetImplTest.nestedSetBuilder().build().toString()).isEqualTo("{}");
        assertThat(NestedSetImplTest.nestedSetBuilder("a").build().toString()).isEqualTo("{a}");
        assertThat(NestedSetImplTest.nestedSetBuilder("a", "b").build().toString()).isEqualTo("{a, b}");
    }

    @Test
    public void nestedToString() {
        NestedSet<String> b = NestedSetImplTest.nestedSetBuilder("b1", "b2").build();
        NestedSet<String> c = NestedSetImplTest.nestedSetBuilder("c1", "c2").build();
        assertThat(NestedSetImplTest.nestedSetBuilder("a").addTransitive(b).build().toString()).isEqualTo("{{b1, b2}, a}");
        assertThat(NestedSetImplTest.nestedSetBuilder("a").addTransitive(b).addTransitive(c).build().toString()).isEqualTo("{{b1, b2}, {c1, c2}, a}");
        assertThat(NestedSetImplTest.nestedSetBuilder().addTransitive(b).build().toString()).isEqualTo("{b1, b2}");
    }

    @Test
    public void isEmpty() {
        NestedSet<String> triviallyEmpty = NestedSetImplTest.nestedSetBuilder().build();
        assertThat(triviallyEmpty.isEmpty()).isTrue();
        NestedSet<String> emptyLevel1 = NestedSetImplTest.nestedSetBuilder().addTransitive(triviallyEmpty).build();
        assertThat(emptyLevel1.isEmpty()).isTrue();
        NestedSet<String> emptyLevel2 = NestedSetImplTest.nestedSetBuilder().addTransitive(emptyLevel1).build();
        assertThat(emptyLevel2.isEmpty()).isTrue();
        NestedSet<String> triviallyNonEmpty = NestedSetImplTest.nestedSetBuilder("mango").build();
        assertThat(triviallyNonEmpty.isEmpty()).isFalse();
        NestedSet<String> nonEmptyLevel1 = NestedSetImplTest.nestedSetBuilder().addTransitive(triviallyNonEmpty).build();
        assertThat(nonEmptyLevel1.isEmpty()).isFalse();
        NestedSet<String> nonEmptyLevel2 = NestedSetImplTest.nestedSetBuilder().addTransitive(nonEmptyLevel1).build();
        assertThat(nonEmptyLevel2.isEmpty()).isFalse();
    }

    @Test
    public void canIncludeAnyOrderInStableOrderAndViceVersa() {
        NestedSetBuilder.stableOrder().addTransitive(NestedSetBuilder.compileOrder().addTransitive(NestedSetBuilder.stableOrder().build()).build()).addTransitive(NestedSetBuilder.linkOrder().addTransitive(NestedSetBuilder.stableOrder().build()).build()).addTransitive(NestedSetBuilder.naiveLinkOrder().addTransitive(NestedSetBuilder.stableOrder().build()).build()).build();
        try {
            NestedSetBuilder.compileOrder().addTransitive(NestedSetBuilder.linkOrder().build()).build();
            Assert.fail("Shouldn't be able to include a non-stable order inside a different non-stable order!");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
    }

    /**
     * A handy wrapper that allows us to use EqualsTester to test shallowEquals and shallowHashCode.
     */
    private static class SetWrapper<E> {
        NestedSet<E> set;

        SetWrapper(NestedSet<E> wrapped) {
            set = wrapped;
        }

        @Override
        public int hashCode() {
            return set.shallowHashCode();
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof NestedSetImplTest.SetWrapper)) {
                return false;
            }
            try {
                @SuppressWarnings("unchecked")
                NestedSetImplTest.SetWrapper<E> other = ((NestedSetImplTest.SetWrapper<E>) (o));
                return set.shallowEquals(other.set);
            } catch (ClassCastException e) {
                return false;
            }
        }
    }

    @Test
    public void shallowEquality() {
        // Used below to check that inner nested sets can be compared by reference equality.
        NestedSetImplTest.SetWrapper<Integer> myRef = NestedSetImplTest.nest(NestedSetImplTest.nest(NestedSetImplTest.flat(7, 8)), NestedSetImplTest.flat(9));
        // Used to check equality for deserializing nested sets
        ListenableFuture<Object[]> contents = Futures.immediateFuture(new Object[]{ "a", "b" });
        NestedSet<String> referenceNestedSet = NestedSet.withFuture(STABLE_ORDER, contents);
        NestedSet<String> otherReferenceNestedSet = NestedSet.withFuture(STABLE_ORDER, contents);
        // Each "equality group" contains elements that are equal to one another
        // (according to equals() and hashCode()), yet distinct from all elements
        // of all other equality groups.
        // Set de-duplication.
        // Make a couple sets deep enough that shallowEquals() fails.
        // If this test case fails because you improve the representation, just delete it.
        // Like flat("4").
        // Automatic elision of one-element nested sets.
        // Element de-duplication.
        // Empty set elision.
        new EqualsTester().addEqualityGroup(NestedSetImplTest.flat(), NestedSetImplTest.flat(), NestedSetImplTest.nest(NestedSetImplTest.flat())).addEqualityGroup(NestedSetBuilder.<Integer>linkOrder().build()).addEqualityGroup(NestedSetImplTest.flat(3), NestedSetImplTest.flat(3), NestedSetImplTest.flat(3, 3)).addEqualityGroup(NestedSetImplTest.flat(4), NestedSetImplTest.nest(NestedSetImplTest.flat(4))).addEqualityGroup(NestedSetBuilder.<Integer>linkOrder().add(4).build()).addEqualityGroup(NestedSetImplTest.nestedSetBuilder("4").build()).addEqualityGroup(NestedSetImplTest.flat(3, 4), NestedSetImplTest.flat(3, 4)).addEqualityGroup(NestedSetImplTest.nest(NestedSetImplTest.nest(NestedSetImplTest.flat(3, 4), NestedSetImplTest.flat(5)), NestedSetImplTest.nest(NestedSetImplTest.flat(6, 7), NestedSetImplTest.flat(8)))).addEqualityGroup(NestedSetImplTest.nest(NestedSetImplTest.nest(NestedSetImplTest.flat(3, 4), NestedSetImplTest.flat(5)), NestedSetImplTest.nest(NestedSetImplTest.flat(6, 7), NestedSetImplTest.flat(8)))).addEqualityGroup(NestedSetImplTest.nest(myRef), NestedSetImplTest.nest(myRef), NestedSetImplTest.nest(myRef, myRef)).addEqualityGroup(NestedSetImplTest.nest(3, myRef)).addEqualityGroup(NestedSetImplTest.nest(4, myRef)).addEqualityGroup(new NestedSetImplTest.SetWrapper(referenceNestedSet), new NestedSetImplTest.SetWrapper(otherReferenceNestedSet)).testEquals();
        // Some things that are not tested by the above:
        // - ordering among direct members
        // - ordering among transitive sets
    }

    @Test
    public void shallowInequality() {
        assertThat(NestedSetImplTest.nestedSetBuilder("a").build().shallowEquals(null)).isFalse();
        Object[] contents = new Object[]{ "a", "b" };
        assertThat(NestedSet.withFuture(STABLE_ORDER, Futures.immediateFuture(contents)).shallowEquals(null)).isFalse();
        // shallowEquals() should require reference equality for underlying futures
        assertThat(NestedSet.withFuture(STABLE_ORDER, Futures.immediateFuture(contents)).shallowEquals(NestedSet.withFuture(STABLE_ORDER, Futures.immediateFuture(contents)))).isFalse();
    }

    /**
     * Checks that the builder always return a nested set with the correct order.
     */
    @Test
    public void correctOrder() {
        for (Order order : Order.values()) {
            for (int numDirects = 0; numDirects < 3; numDirects++) {
                for (int numTransitives = 0; numTransitives < 3; numTransitives++) {
                    assertThat(createNestedSet(order, numDirects, numTransitives, order).getOrder()).isEqualTo(order);
                    // We allow mixing orders if one of them is stable. This tests that the top level order is
                    // the correct one.
                    assertThat(createNestedSet(order, numDirects, numTransitives, STABLE_ORDER).getOrder()).isEqualTo(order);
                }
            }
        }
    }
}

