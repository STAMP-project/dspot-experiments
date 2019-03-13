/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api.iterable;


import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class IterableAssert_should_honor_SortedSet_comparator_Test {
    private Iterable<Set<String>> sets;

    @Test
    public void should_honor_sorted_set_comparator() {
        Assertions.assertThat(sets).allSatisfy(( set) -> {
            assertThat(set).contains("foo");
            assertThat(set).containsAll(newLinkedHashSet("foo"));
            assertThat(set).containsAnyElementsOf(newLinkedHashSet("foo", "bar"));
            assertThat(set).containsAnyOf("foo", "bar");
            assertThat(set).containsExactly("foo");
            assertThat(set).containsExactlyElementsOf(newLinkedHashSet("foo"));
            assertThat(set).containsExactlyInAnyOrder("foo");
            assertThat(set).containsExactlyInAnyOrderElementsOf(newLinkedHashSet("foo"));
            assertThat(set).containsOnly("foo");
            assertThat(set).containsOnlyElementsOf(newLinkedHashSet("foo"));
            assertThat(set).containsOnlyOnce("foo");
            assertThat(set).containsSequence("foo");
            assertThat(set).containsSequence(newLinkedHashSet("foo"));
            assertThat(set).containsSubsequence("foo");
            assertThat(set).containsSubsequence(newLinkedHashSet("foo"));
        });
        Assertions.assertThat(sets).noneSatisfy(( set) -> assertThat(set).doesNotContain("foo", "FOO"));
        Assertions.assertThat(sets).noneSatisfy(( set) -> assertThat(set).doesNotContainAnyElementsOf(newLinkedHashSet("foo", "FOO")));
        Assertions.assertThat(sets).noneSatisfy(( set) -> assertThat(set).doesNotContainSequence("foo"));
        Assertions.assertThat(sets).noneSatisfy(( set) -> assertThat(set).doesNotContainSequence(newLinkedHashSet("foo")));
        Assertions.assertThat(sets).noneSatisfy(( set) -> assertThat(set).doesNotContainSubsequence("foo"));
        Assertions.assertThat(sets).noneSatisfy(( set) -> assertThat(set).doesNotContainSubsequence(newLinkedHashSet("foo")));
    }
}

