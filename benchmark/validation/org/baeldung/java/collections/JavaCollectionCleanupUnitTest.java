package org.baeldung.java.collections;


import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.PredicateUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JavaCollectionCleanupUnitTest {
    // tests - removing nulls
    @Test
    public final void givenListContainsNulls_whenRemovingNullsWithPlainJava_thenCorrect() {
        final List<Integer> list = Lists.newArrayList(null, 1, null);
        while (list.remove(null));
        Assert.assertThat(list, Matchers.hasSize(1));
    }

    @Test
    public final void givenListContainsNulls_whenRemovingNullsWithPlainJavaAlternative_thenCorrect() {
        final List<Integer> list = Lists.newArrayList(null, 1, null);
        list.removeAll(Collections.singleton(null));
        Assert.assertThat(list, Matchers.hasSize(1));
    }

    @Test
    public final void givenListContainsNulls_whenRemovingNullsWithGuavaV1_thenCorrect() {
        final List<Integer> list = Lists.newArrayList(null, 1, null);
        Iterables.removeIf(list, Predicates.isNull());
        Assert.assertThat(list, Matchers.hasSize(1));
    }

    @Test
    public final void givenListContainsNulls_whenRemovingNullsWithGuavaV2_thenCorrect() {
        final List<Integer> list = Lists.newArrayList(null, 1, null, 2, 3);
        final List<Integer> listWithoutNulls = Lists.newArrayList(Iterables.filter(list, Predicates.notNull()));
        Assert.assertThat(listWithoutNulls, Matchers.hasSize(3));
    }

    @Test
    public final void givenListContainsNulls_whenRemovingNullsWithCommonsCollections_thenCorrect() {
        final List<Integer> list = Lists.newArrayList(null, 1, 2, null, 3, null);
        CollectionUtils.filter(list, PredicateUtils.notNullPredicate());
        Assert.assertThat(list, Matchers.hasSize(3));
    }

    // tests - remove duplicates
    @Test
    public final void givenListContainsDuplicates_whenRemovingDuplicatesWithPlainJava_thenCorrect() {
        final List<Integer> listWithDuplicates = Lists.newArrayList(0, 1, 2, 3, 0, 0);
        final List<Integer> listWithoutDuplicates = new ArrayList<>(new HashSet<>(listWithDuplicates));
        Assert.assertThat(listWithoutDuplicates, Matchers.hasSize(4));
    }

    @Test
    public final void givenListContainsDuplicates_whenRemovingDuplicatesWithGuava_thenCorrect() {
        final List<Integer> listWithDuplicates = Lists.newArrayList(0, 1, 2, 3, 0, 0);
        final List<Integer> listWithoutDuplicates = Lists.newArrayList(Sets.newHashSet(listWithDuplicates));
        Assert.assertThat(listWithoutDuplicates, Matchers.hasSize(4));
    }
}

