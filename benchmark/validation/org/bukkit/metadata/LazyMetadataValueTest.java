package org.bukkit.metadata;


import LazyMetadataValue.CacheStrategy;
import java.util.concurrent.Callable;
import org.bukkit.plugin.TestPlugin;
import org.junit.Assert;
import org.junit.Test;


public class LazyMetadataValueTest {
    private LazyMetadataValue subject;

    private TestPlugin plugin = new TestPlugin("x");

    @Test
    public void testLazyInt() {
        int value = 10;
        subject = makeSimpleCallable(value);
        Assert.assertEquals(value, subject.value());
    }

    @Test
    public void testLazyDouble() {
        double value = 10.5;
        subject = makeSimpleCallable(value);
        Assert.assertEquals(value, ((Double) (subject.value())), 0.01);
    }

    @Test
    public void testLazyString() {
        String value = "TEN";
        subject = makeSimpleCallable(value);
        Assert.assertEquals(value, subject.value());
    }

    @Test
    public void testLazyBoolean() {
        boolean value = false;
        subject = makeSimpleCallable(value);
        Assert.assertEquals(value, subject.value());
    }

    @Test(expected = MetadataEvaluationException.class)
    public void testEvalException() {
        subject = new LazyMetadataValue(plugin, CacheStrategy.CACHE_AFTER_FIRST_EVAL, new Callable<Object>() {
            public Object call() throws Exception {
                throw new RuntimeException("Gotcha!");
            }
        });
        subject.value();
    }

    @Test
    public void testCacheStrategyCacheAfterFirstEval() {
        final LazyMetadataValueTest.Counter counter = new LazyMetadataValueTest.Counter();
        final int value = 10;
        subject = new LazyMetadataValue(plugin, CacheStrategy.CACHE_AFTER_FIRST_EVAL, new Callable<Object>() {
            public Object call() throws Exception {
                counter.increment();
                return value;
            }
        });
        subject.value();
        subject.value();
        Assert.assertEquals(value, subject.value());
        Assert.assertEquals(1, counter.value());
        subject.invalidate();
        subject.value();
        Assert.assertEquals(2, counter.value());
    }

    @Test
    public void testCacheStrategyNeverCache() {
        final LazyMetadataValueTest.Counter counter = new LazyMetadataValueTest.Counter();
        final int value = 10;
        subject = new LazyMetadataValue(plugin, CacheStrategy.NEVER_CACHE, new Callable<Object>() {
            public Object call() throws Exception {
                counter.increment();
                return value;
            }
        });
        subject.value();
        subject.value();
        Assert.assertEquals(value, subject.value());
        Assert.assertEquals(3, counter.value());
    }

    @Test
    public void testCacheStrategyEternally() {
        final LazyMetadataValueTest.Counter counter = new LazyMetadataValueTest.Counter();
        final int value = 10;
        subject = new LazyMetadataValue(plugin, CacheStrategy.CACHE_ETERNALLY, new Callable<Object>() {
            public Object call() throws Exception {
                counter.increment();
                return value;
            }
        });
        subject.value();
        subject.value();
        Assert.assertEquals(value, subject.value());
        Assert.assertEquals(1, counter.value());
        subject.invalidate();
        subject.value();
        Assert.assertEquals(value, subject.value());
        Assert.assertEquals(1, counter.value());
    }

    private class Counter {
        private int c = 0;

        public void increment() {
            (c)++;
        }

        public int value() {
            return c;
        }
    }
}

