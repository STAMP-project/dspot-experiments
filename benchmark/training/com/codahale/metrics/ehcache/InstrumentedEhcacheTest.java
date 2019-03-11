package com.codahale.metrics.ehcache;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.junit.Test;


public class InstrumentedEhcacheTest {
    private static final CacheManager MANAGER = CacheManager.create();

    private final MetricRegistry registry = new MetricRegistry();

    private Ehcache cache;

    @Test
    public void measuresGetsAndPuts() {
        cache.get("woo");
        cache.put(new Element("woo", "whee"));
        final Timer gets = registry.timer(MetricRegistry.name(Cache.class, "test", "gets"));
        assertThat(gets.getCount()).isEqualTo(1);
        final Timer puts = registry.timer(MetricRegistry.name(Cache.class, "test", "puts"));
        assertThat(puts.getCount()).isEqualTo(1);
    }
}

