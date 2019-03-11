package com.baeldung.collectors;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.junit.Test;

import static java.util.stream.Collector.Characteristics.UNORDERED;


public class Java8CollectorsUnitTest {
    private final List<String> givenList = Arrays.asList("a", "bb", "ccc", "dd");

    private final List<String> listWithDuplicates = Arrays.asList("a", "bb", "c", "d", "bb");

    @Test
    public void whenCollectingToList_shouldCollectToList() throws Exception {
        final List<String> result = givenList.stream().collect(Collectors.toList());
        assertThat(result).containsAll(givenList);
    }

    @Test
    public void whenCollectingToSet_shouldCollectToSet() throws Exception {
        final Set<String> result = givenList.stream().collect(Collectors.toSet());
        assertThat(result).containsAll(givenList);
    }

    @Test
    public void givenContainsDuplicateElements_whenCollectingToSet_shouldAddDuplicateElementsOnlyOnce() throws Exception {
        final Set<String> result = listWithDuplicates.stream().collect(Collectors.toSet());
        assertThat(result).hasSize(4);
    }

    @Test
    public void whenCollectingToCollection_shouldCollectToCollection() throws Exception {
        final List<String> result = givenList.stream().collect(Collectors.toCollection(LinkedList::new));
        assertThat(result).containsAll(givenList).isInstanceOf(LinkedList.class);
    }

    @Test
    public void whenCollectingToImmutableCollection_shouldThrowException() throws Exception {
        assertThatThrownBy(() -> {
            givenList.stream().collect(toCollection(ImmutableList::of));
        }).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void whenCollectingToMap_shouldCollectToMap() throws Exception {
        final Map<String, Integer> result = givenList.stream().collect(Collectors.toMap(Function.identity(), String::length));
        assertThat(result).containsEntry("a", 1).containsEntry("bb", 2).containsEntry("ccc", 3).containsEntry("dd", 2);
    }

    @Test
    public void whenCollectingToMapwWithDuplicates_shouldCollectToMapMergingTheIdenticalItems() throws Exception {
        final Map<String, Integer> result = listWithDuplicates.stream().collect(Collectors.toMap(Function.identity(), String::length, ( item, identicalItem) -> item));
        assertThat(result).containsEntry("a", 1).containsEntry("bb", 2).containsEntry("c", 1).containsEntry("d", 1);
    }

    @Test
    public void givenContainsDuplicateElements_whenCollectingToMap_shouldThrowException() throws Exception {
        assertThatThrownBy(() -> {
            listWithDuplicates.stream().collect(toMap(Function.identity(), String::length));
        }).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void whenCollectingAndThen_shouldCollect() throws Exception {
        final List<String> result = givenList.stream().collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
        assertThat(result).containsAll(givenList).isInstanceOf(ImmutableList.class);
    }

    @Test
    public void whenJoining_shouldJoin() throws Exception {
        final String result = givenList.stream().collect(Collectors.joining());
        assertThat(result).isEqualTo("abbcccdd");
    }

    @Test
    public void whenJoiningWithSeparator_shouldJoinWithSeparator() throws Exception {
        final String result = givenList.stream().collect(Collectors.joining(" "));
        assertThat(result).isEqualTo("a bb ccc dd");
    }

    @Test
    public void whenJoiningWithSeparatorAndPrefixAndPostfix_shouldJoinWithSeparatorPrePost() throws Exception {
        final String result = givenList.stream().collect(Collectors.joining(" ", "PRE-", "-POST"));
        assertThat(result).isEqualTo("PRE-a bb ccc dd-POST");
    }

    @Test
    public void whenPartitioningBy_shouldPartition() throws Exception {
        final Map<Boolean, List<String>> result = givenList.stream().collect(Collectors.partitioningBy(( s) -> (s.length()) > 2));
        assertThat(result).containsKeys(true, false).satisfies(( booleanListMap) -> {
            assertThat(booleanListMap.get(true)).contains("ccc");
            assertThat(booleanListMap.get(false)).contains("a", "bb", "dd");
        });
    }

    @Test
    public void whenCounting_shouldCount() throws Exception {
        final Long result = givenList.stream().collect(Collectors.counting());
        assertThat(result).isEqualTo(4);
    }

    @Test
    public void whenSummarizing_shouldSummarize() throws Exception {
        final DoubleSummaryStatistics result = givenList.stream().collect(Collectors.summarizingDouble(String::length));
        assertThat(result.getAverage()).isEqualTo(2);
        assertThat(result.getCount()).isEqualTo(4);
        assertThat(result.getMax()).isEqualTo(3);
        assertThat(result.getMin()).isEqualTo(1);
        assertThat(result.getSum()).isEqualTo(8);
    }

    @Test
    public void whenAveraging_shouldAverage() throws Exception {
        final Double result = givenList.stream().collect(Collectors.averagingDouble(String::length));
        assertThat(result).isEqualTo(2);
    }

    @Test
    public void whenSumming_shouldSum() throws Exception {
        final Double result = givenList.stream().filter(( i) -> true).collect(Collectors.summingDouble(String::length));
        assertThat(result).isEqualTo(8);
    }

    @Test
    public void whenMaxingBy_shouldMaxBy() throws Exception {
        final Optional<String> result = givenList.stream().collect(Collectors.maxBy(Comparator.naturalOrder()));
        assertThat(result).isPresent().hasValue("dd");
    }

    @Test
    public void whenGroupingBy_shouldGroupBy() throws Exception {
        final Map<Integer, Set<String>> result = givenList.stream().collect(Collectors.groupingBy(String::length, Collectors.toSet()));
        assertThat(result).containsEntry(1, Sets.newHashSet("a")).containsEntry(2, Sets.newHashSet("bb", "dd")).containsEntry(3, Sets.newHashSet("ccc"));
    }

    @Test
    public void whenCreatingCustomCollector_shouldCollect() throws Exception {
        final ImmutableSet<String> result = givenList.stream().collect(Java8CollectorsUnitTest.toImmutableSet());
        assertThat(result).isInstanceOf(ImmutableSet.class).contains("a", "bb", "ccc", "dd");
    }

    private static class ImmutableSetCollector<T> implements Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> {
        @Override
        public Supplier<ImmutableSet.Builder<T>> supplier() {
            return ImmutableSet::builder;
        }

        @Override
        public BiConsumer<ImmutableSet.Builder<T>, T> accumulator() {
            return ImmutableSet.Builder::add;
        }

        @Override
        public BinaryOperator<ImmutableSet.Builder<T>> combiner() {
            return ( left, right) -> left.addAll(right.build());
        }

        @Override
        public Function<ImmutableSet.Builder<T>, ImmutableSet<T>> finisher() {
            return ImmutableSet.Builder::build;
        }

        @Override
        public Set<Collector.Characteristics> characteristics() {
            return Sets.immutableEnumSet(UNORDERED);
        }
    }
}

