package com.annimon.stream.streamtests;


import com.annimon.stream.function.Consumer;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class PeekTest {
    @Test
    public void testPeek() {
        final List<Integer> result = new ArrayList<Integer>();
        long count = com.annimon.stream.Stream.range(0, 5).peek(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                result.add(t);
            }
        }).count();
        Assert.assertEquals(5, count);
        Assert.assertThat(result, Matchers.contains(0, 1, 2, 3, 4));
    }
}

