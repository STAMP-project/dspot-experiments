package com.annimon.stream.streamtests;


import com.annimon.stream.Optional;
import com.annimon.stream.test.hamcrest.OptionalMatcher;
import org.junit.Assert;
import org.junit.Test;

import static com.annimon.stream.Functions.descendingAbsoluteOrder;
import static com.annimon.stream.Functions.naturalOrder;


public final class MinTest {
    @Test
    public void testMin() {
        Optional<Integer> min = com.annimon.stream.Stream.of(6, 3, 9, 0, (-7), 19).min(naturalOrder());
        Assert.assertThat(min, isPresent());
        Assert.assertNotNull(min.get());
        Assert.assertEquals((-7), ((int) (min.get())));
    }

    @Test
    public void testMinDescendingOrder() {
        Optional<Integer> min = com.annimon.stream.Stream.of(6, 3, 9, 0, (-7), 19).min(descendingAbsoluteOrder());
        Assert.assertThat(min, isPresent());
        Assert.assertNotNull(min.get());
        Assert.assertEquals(19, ((int) (min.get())));
    }

    @Test
    public void testMinEmpty() {
        Optional<Integer> min = com.annimon.stream.Stream.<Integer>empty().min(naturalOrder());
        Assert.assertThat(min, OptionalMatcher.isEmpty());
    }
}

