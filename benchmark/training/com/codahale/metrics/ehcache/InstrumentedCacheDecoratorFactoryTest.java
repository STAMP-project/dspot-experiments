package com.codahale.metrics.ehcache;


import com.codahale.metrics.MetricRegistry;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.junit.Test;


public class InstrumentedCacheDecoratorFactoryTest {
    private static final CacheManager MANAGER = CacheManager.create();

    private MetricRegistry registry;

    private Ehcache cache;

    @Test
    public void measuresGets() {
        cache.get("woo");
        assertThat(registry.timer(MetricRegistry.name(Cache.class, "test-config", "gets")).getCount()).isEqualTo(1);
    }

    @Test
    public void measuresPuts() {
        cache.put(new Element("woo", "whee"));
        assertThat(registry.timer(MetricRegistry.name(Cache.class, "test-config", "puts")).getCount()).isEqualTo(1);
    }
}

