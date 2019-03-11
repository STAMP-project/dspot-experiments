/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.guava;


import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestGuavaCollectors {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugins();

    private Collection<Integer> expected;

    @Test
    public void immutableList() {
        ImmutableList<Integer> list = dbRule.getSharedHandle().createQuery("select intValue from something").collectInto(new org.jdbi.v3.core.generic.GenericType<ImmutableList<Integer>>() {});
        assertThat(list).containsOnlyElementsOf(expected);
    }

    @Test
    public void immutableSet() {
        ImmutableSet<Integer> set = dbRule.getSharedHandle().createQuery("select intValue from something").collectInto(new org.jdbi.v3.core.generic.GenericType<ImmutableSet<Integer>>() {});
        assertThat(set).containsOnlyElementsOf(expected);
    }

    @Test
    public void immutableSortedSet() {
        ImmutableSortedSet<Integer> set = dbRule.getSharedHandle().createQuery("select intValue from something").collectInto(new org.jdbi.v3.core.generic.GenericType<ImmutableSortedSet<Integer>>() {});
        assertThat(set).containsExactlyElementsOf(expected);
    }

    @Test
    public void immutableSortedSetWithComparator() {
        Comparator<Integer> comparator = Comparator.<Integer>naturalOrder().reversed();
        ImmutableSortedSet<Integer> set = dbRule.getSharedHandle().createQuery("select intValue from something").mapTo(int.class).collect(ImmutableSortedSet.toImmutableSortedSet(ImmutableSortedSet, comparator));
        assertThat(set).containsExactlyElementsOf(expected.stream().sorted(comparator).collect(Collectors.toList()));
    }

    @Test
    public void optionalPresent() {
        Optional<Integer> shouldBePresent = dbRule.getSharedHandle().createQuery("select intValue from something where intValue = 1").collectInto(new org.jdbi.v3.core.generic.GenericType<Optional<Integer>>() {});
        assertThat(shouldBePresent).contains(1);
    }

    @Test
    public void optionalAbsent() {
        Optional<Integer> shouldBeAbsent = dbRule.getSharedHandle().createQuery("select intValue from something where intValue = 100").collectInto(new org.jdbi.v3.core.generic.GenericType<Optional<Integer>>() {});
        assertThat(shouldBeAbsent).isAbsent();
    }

    @Test
    public void optionalMultiple() {
        assertThatThrownBy(() -> dbRule.getSharedHandle().createQuery("select intValue from something").collectInto(new GenericType<Optional<Integer>>() {})).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void mapCollectors() {
        testMapCollector(ImmutableMap.class, new org.jdbi.v3.core.generic.GenericType<ImmutableMap<Long, String>>() {});
        testMapCollector(BiMap.class, new org.jdbi.v3.core.generic.GenericType<BiMap<Long, String>>() {});
    }

    @Test
    public void multimapCollectors() {
        testMultimapCollector(ImmutableMultimap.class, new org.jdbi.v3.core.generic.GenericType<ImmutableMultimap<Long, String>>() {});
        testMultimapCollector(ImmutableListMultimap.class, new org.jdbi.v3.core.generic.GenericType<ImmutableListMultimap<Long, String>>() {});
        testMultimapCollector(ImmutableSetMultimap.class, new org.jdbi.v3.core.generic.GenericType<ImmutableSetMultimap<Long, String>>() {});
        testMultimapCollector(Multimap.class, new org.jdbi.v3.core.generic.GenericType<Multimap<Long, String>>() {});
        testMultimapCollector(ListMultimap.class, new org.jdbi.v3.core.generic.GenericType<ListMultimap<Long, String>>() {});
        testMultimapCollector(ArrayListMultimap.class, new org.jdbi.v3.core.generic.GenericType<ArrayListMultimap<Long, String>>() {});
        testMultimapCollector(LinkedListMultimap.class, new org.jdbi.v3.core.generic.GenericType<LinkedListMultimap<Long, String>>() {});
        testMultimapCollector(SetMultimap.class, new org.jdbi.v3.core.generic.GenericType<SetMultimap<Long, String>>() {});
        testMultimapCollector(HashMultimap.class, new org.jdbi.v3.core.generic.GenericType<HashMultimap<Long, String>>() {});
        testMultimapCollector(TreeMultimap.class, new org.jdbi.v3.core.generic.GenericType<TreeMultimap<Long, String>>() {});
    }
}

