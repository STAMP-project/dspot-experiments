package org.baeldung.java;


import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.collections4.ListUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CollectionApachePartitionUnitTest {
    // tests - apache common collections
    @Test
    public final void givenList_whenParitioningIntoNSublists_thenCorrect() {
        final List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        final List<List<Integer>> subSets = ListUtils.partition(intList, 3);
        // When
        final List<Integer> lastPartition = subSets.get(2);
        final List<Integer> expectedLastPartition = Lists.<Integer>newArrayList(7, 8);
        Assert.assertThat(subSets.size(), Matchers.equalTo(3));
        Assert.assertThat(lastPartition, Matchers.equalTo(expectedLastPartition));
    }

    @Test
    public final void givenListPartitioned_whenOriginalListIsModified_thenPartitionsChange() {
        // Given
        final List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        final List<List<Integer>> subSets = ListUtils.partition(intList, 3);
        // When
        intList.add(9);
        final List<Integer> lastPartition = subSets.get(2);
        final List<Integer> expectedLastPartition = Lists.<Integer>newArrayList(7, 8, 9);
        Assert.assertThat(lastPartition, Matchers.equalTo(expectedLastPartition));
    }
}

