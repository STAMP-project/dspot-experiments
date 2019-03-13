package org.baeldung.java;


import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CollectionJavaPartitionUnitTest {
    // java8 groupBy
    @Test
    public final void givenList_whenParitioningIntoNSublistsUsingGroupingBy_thenCorrect() {
        final List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        final Map<Integer, List<Integer>> groups = intList.stream().collect(Collectors.groupingBy(( s) -> (s - 1) / 3));
        final List<List<Integer>> subSets = new ArrayList<List<Integer>>(groups.values());
        // When
        final List<Integer> lastPartition = subSets.get(2);
        final List<Integer> expectedLastPartition = Lists.<Integer>newArrayList(7, 8);
        Assert.assertThat(subSets.size(), Matchers.equalTo(3));
        Assert.assertThat(lastPartition, Matchers.equalTo(expectedLastPartition));
        // intList.add(9);
        // System.out.println(groups.values());
    }

    // java8 partitionBy
    @Test
    public final void givenList_whenParitioningIntoSublistsUsingPartitionBy_thenCorrect() {
        final List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        final Map<Boolean, List<Integer>> groups = intList.stream().collect(Collectors.partitioningBy(( s) -> s > 6));
        final List<List<Integer>> subSets = new ArrayList<List<Integer>>(groups.values());
        // When
        final List<Integer> lastPartition = subSets.get(1);
        final List<Integer> expectedLastPartition = Lists.<Integer>newArrayList(7, 8);
        Assert.assertThat(subSets.size(), Matchers.equalTo(2));
        Assert.assertThat(lastPartition, Matchers.equalTo(expectedLastPartition));
        // intList.add(9);
        // System.out.println(groups.values());
    }

    // java8 split by separator
    @Test
    public final void givenList_whenSplittingBySeparator_thenCorrect() {
        final List<Integer> intList = Lists.newArrayList(1, 2, 3, 0, 4, 5, 6, 0, 7, 8);
        final int[] indexes = Stream.of(IntStream.of((-1)), IntStream.range(0, intList.size()).filter(( i) -> (intList.get(i)) == 0), IntStream.of(intList.size())).flatMapToInt(( s) -> s).toArray();
        final List<List<Integer>> subSets = IntStream.range(0, ((indexes.length) - 1)).mapToObj(( i) -> intList.subList(((indexes[i]) + 1), indexes[(i + 1)])).collect(Collectors.toList());
        // When
        final List<Integer> lastPartition = subSets.get(2);
        final List<Integer> expectedLastPartition = Lists.<Integer>newArrayList(7, 8);
        Assert.assertThat(subSets.size(), Matchers.equalTo(3));
        Assert.assertThat(lastPartition, Matchers.equalTo(expectedLastPartition));
    }
}

