package org.axonframework.disruptor.commandhandling;


import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import org.axonframework.eventsourcing.EventSourcedAggregate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class FirstLevelCacheTest {
    private EventSourcedAggregate<FirstLevelCacheTest.MyAggregate> cacheable;

    private FirstLevelCache<FirstLevelCacheTest.MyAggregate> cache;

    @Test
    public void shouldPut() {
        cache.put("key", cacheable);
        Assert.assertEquals(cache.size(), 1);
    }

    @Test
    public void shouldGet() {
        cache.put("key", cacheable);
        EventSourcedAggregate<FirstLevelCacheTest.MyAggregate> cached = cache.get("key");
        Assert.assertSame(cached, cacheable);
    }

    @Test
    public void shouldRemove() {
        cache.put("key", cacheable);
        EventSourcedAggregate<FirstLevelCacheTest.MyAggregate> cached = cache.remove("key");
        Assert.assertSame(cached, cacheable);
        Assert.assertEquals(0, cache.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldClearWeakValues() throws Exception {
        FirstLevelCache<FirstLevelCacheTest.MyAggregate> myCache = new FirstLevelCache();
        int numberOfEntries = 200;
        IntStream.range(0, numberOfEntries).mapToObj(( i) -> "key-" + i).forEach(( key) -> myCache.put(key, Mockito.mock(EventSourcedAggregate.class)));
        int i = 0;
        while ((i < 10) && ((myCache.size()) > 0)) {
            System.gc();
            Thread.sleep(50);
            i++;
        } 
        Assert.assertEquals(0, myCache.size());
    }

    static class MyAggregate {}
}

