package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoublePredicate;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OnCloseTest {
    @Test
    public void testOnClose() {
        final boolean[] state = new boolean[]{ false };
        DoubleStream stream = DoubleStream.of(0, 1, 2).onClose(new Runnable() {
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
        DoubleStream stream = DoubleStream.of(0, 1, 2, 2, 3, 4, 4, 5).filter(new DoublePredicate() {
            @Override
            public boolean test(double value) {
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
        DoubleStream stream = DoubleStream.of(2, 3, 4).onClose(runnable).onClose(runnable).flatMap(new com.annimon.stream.function.DoubleFunction<DoubleStream>() {
            @Override
            public DoubleStream apply(final double i) {
                return DoubleStream.of(2, 3, 4).onClose(runnable);
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
        DoubleStream stream1 = DoubleStream.of(0, 1).onClose(runnable);
        DoubleStream stream2 = DoubleStream.of(2, 3).onClose(runnable);
        DoubleStream stream = DoubleStream.concat(stream1, stream2).onClose(runnable);
        stream.count();
        Assert.assertThat(counter[0], CoreMatchers.is(0));
        stream.close();
        Assert.assertThat(counter[0], CoreMatchers.is(3));
    }
}

