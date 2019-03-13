package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntPredicate;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OnCloseTest {
    @Test
    public void testOnClose() {
        final boolean[] state = new boolean[]{ false };
        IntStream stream = IntStream.of(0, 1, 2).onClose(new Runnable() {
            @Override
            public void run() {
                state[0] = true;
            }
        });
        stream.findFirst();
        stream.close();
        Assert.assertTrue(state[0]);
    }

    @Test
    public void testOnCloseWithOtherOperators() {
        final boolean[] state = new boolean[]{ false };
        IntStream stream = IntStream.of(0, 1, 2, 2, 3, 4, 4, 5).filter(new IntPredicate() {
            @Override
            public boolean test(int value) {
                return value < 4;
            }
        }).onClose(new Runnable() {
            @Override
            public void run() {
                state[0] = true;
            }
        }).distinct().limit(2);
        stream.findFirst();
        stream.close();
        Assert.assertTrue(state[0]);
    }

    @Test
    public void testOnCloseFlatMap() {
        final int[] counter = new int[]{ 0 };
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                (counter[0])++;
            }
        };
        IntStream stream = IntStream.rangeClosed(2, 4).onClose(runnable).onClose(runnable).flatMap(new com.annimon.stream.function.IntFunction<IntStream>() {
            @Override
            public IntStream apply(final int i) {
                return IntStream.rangeClosed(2, 4).onClose(runnable);
            }
        });
        stream.count();
        Assert.assertThat(counter[0], CoreMatchers.is(3));
        stream.close();
        Assert.assertThat(counter[0], CoreMatchers.is(5));
    }

    @Test
    public void testOnCloseConcat() {
        final int[] counter = new int[]{ 0 };
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                (counter[0])++;
            }
        };
        IntStream stream1 = IntStream.range(0, 2).onClose(runnable);
        IntStream stream2 = IntStream.range(0, 2).onClose(runnable);
        IntStream stream = IntStream.concat(stream1, stream2).onClose(runnable);
        stream.count();
        Assert.assertThat(counter[0], CoreMatchers.is(0));
        stream.close();
        Assert.assertThat(counter[0], CoreMatchers.is(3));
    }
}

