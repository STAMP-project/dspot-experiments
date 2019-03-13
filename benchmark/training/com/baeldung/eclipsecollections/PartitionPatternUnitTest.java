package com.baeldung.eclipsecollections;


import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.junit.Test;


public class PartitionPatternUnitTest {
    MutableList<Integer> list;

    @Test
    public void whenAnySatisfiesCondition_thenCorrect() {
        MutableList<Integer> numbers = list;
        PartitionMutableList<Integer> partitionedFolks = numbers.partition(new org.eclipse.collections.api.block.predicate.Predicate<Integer>() {
            /**
             *
             */
            private static final long serialVersionUID = -1551138743683678406L;

            public boolean accept(Integer each) {
                return each > 30;
            }
        });
        MutableList<Integer> greaterThanThirty = partitionedFolks.getSelected().sortThis();
        MutableList<Integer> smallerThanThirty = partitionedFolks.getRejected().sortThis();
        Assertions.assertThat(smallerThanThirty).containsExactly(1, 5, 8, 17, 23);
        Assertions.assertThat(greaterThanThirty).containsExactly(31, 38, 41);
    }
}

