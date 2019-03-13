package com.annimon.stream.streamtests;


import com.annimon.stream.Optional;
import com.annimon.stream.test.hamcrest.OptionalMatcher;
import org.junit.Assert;
import org.junit.Test;

import static com.annimon.stream.Functions.descendingAbsoluteOrder;
import static com.annimon.stream.Functions.naturalOrder;


public final class MaxTest {
    @Test
    public void testMax() {
        Optional<Integer> max = com.annimon.stream.Stream.of(6, 3, 9, 0, (-7), 19).max(naturalOrder());
        Assert.assertThat(max, isPresent());
        Assert.assertNotNull(max.get());
        Assert.assertEquals(19, ((int) (max.get())));
    }

    @Test
    public void testMaxDescendingOrder() {
        Optional<Integer> max = com.annimon.stream.Stream.of(6, 3, 9, 0, (-7), 19).max(descendingAbsoluteOrder());
        Assert.assertThat(max, isPresent());
        Assert.assertNotNull(max.get());
        Assert.assertEquals(0, ((int) (max.get())));
    }

    @Test
    public void testMaxEmpty() {
        Optional<Integer> max = com.annimon.stream.Stream.<Integer>empty().max(naturalOrder());
        Assert.assertThat(max, OptionalMatcher.isEmpty());
    }
}

