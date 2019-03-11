package com.baeldung.java8;


import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class Java8CollectionCleanupUnitTest {
    // tests -
    @Test
    public void givenListContainsNulls_whenFilteringParallel_thenCorrect() {
        final List<Integer> list = Lists.newArrayList(null, 1, 2, null, 3, null);
        final List<Integer> listWithoutNulls = list.parallelStream().filter(Objects::nonNull).collect(Collectors.toList());
        Assert.assertThat(listWithoutNulls, Matchers.hasSize(3));
    }

    @Test
    public void givenListContainsNulls_whenFilteringSerial_thenCorrect() {
        final List<Integer> list = Lists.newArrayList(null, 1, 2, null, 3, null);
        final List<Integer> listWithoutNulls = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Assert.assertThat(listWithoutNulls, Matchers.hasSize(3));
    }

    @Test
    public void givenListContainsNulls_whenRemovingNullsWithRemoveIf_thenCorrect() {
        final List<Integer> listWithoutNulls = Lists.newArrayList(null, 1, 2, null, 3, null);
        listWithoutNulls.removeIf(Objects::isNull);
        Assert.assertThat(listWithoutNulls, Matchers.hasSize(3));
    }

    @Test
    public void givenListContainsDuplicates_whenRemovingDuplicatesWithJava8_thenCorrect() {
        final List<Integer> listWithDuplicates = Lists.newArrayList(1, 1, 2, 2, 3, 3);
        final List<Integer> listWithoutDuplicates = listWithDuplicates.parallelStream().distinct().collect(Collectors.toList());
        Assert.assertThat(listWithoutDuplicates, Matchers.hasSize(3));
    }
}

