package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class GroupByTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testGroupBy() {
        final Integer partitionItem = 1;
        Stream.of(1, 2, 3, 1, 2, 3, 1, 2, 3).groupBy(Functions.equalityPartitionItem(partitionItem)).custom(assertElements(Matchers.containsInAnyOrder(entry(false, Arrays.asList(2, 3, 2, 3, 2, 3)), entry(true, Arrays.asList(1, 1, 1)))));
    }
}

