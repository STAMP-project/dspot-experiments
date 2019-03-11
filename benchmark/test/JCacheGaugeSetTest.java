

import com.codahale.metrics.MetricRegistry;
import javax.cache.Cache;
import javax.cache.CacheManager;
import org.junit.Test;


public class JCacheGaugeSetTest {
    private MetricRegistry registry;

    private Cache<Object, Object> myCache;

    private Cache<Object, Object> myOtherCache;

    private CacheManager cacheManager;

    @Test
    public void measuresGauges() throws Exception {
        myOtherCache.get("woo");
        assertThat(registry.getGauges().get("jcache.statistics.myOtherCache.cache-misses").getValue()).isEqualTo(1L);
        myCache.get("woo");
        assertThat(registry.getGauges().get("jcache.statistics.myCache.cache-misses").getValue()).isEqualTo(1L);
        assertThat(registry.getGauges().get("jcache.statistics.myCache.cache-hits").getValue()).isEqualTo(0L);
        assertThat(registry.getGauges().get("jcache.statistics.myCache.cache-gets").getValue()).isEqualTo(1L);
        myCache.put("woo", "whee");
        myCache.get("woo");
        assertThat(registry.getGauges().get("jcache.statistics.myCache.cache-puts").getValue()).isEqualTo(1L);
        assertThat(registry.getGauges().get("jcache.statistics.myCache.cache-hits").getValue()).isEqualTo(1L);
        assertThat(registry.getGauges().get("jcache.statistics.myCache.cache-hit-percentage").getValue()).isEqualTo(50.0F);
        assertThat(registry.getGauges().get("jcache.statistics.myCache.cache-miss-percentage").getValue()).isEqualTo(50.0F);
        assertThat(registry.getGauges().get("jcache.statistics.myCache.cache-gets").getValue()).isEqualTo(2L);
        // cache size being 1, eviction occurs after this line
        myCache.put("woo2", "whoza");
        assertThat(registry.getGauges().get("jcache.statistics.myCache.cache-evictions").getValue()).isEqualTo(1L);
        myCache.remove("woo2");
        assertThat(((Float) (registry.getGauges().get("jcache.statistics.myCache.average-get-time").getValue()))).isGreaterThan(0.0F);
        assertThat(((Float) (registry.getGauges().get("jcache.statistics.myCache.average-put-time").getValue()))).isGreaterThan(0.0F);
        assertThat(((Float) (registry.getGauges().get("jcache.statistics.myCache.average-remove-time").getValue()))).isGreaterThan(0.0F);
    }
}

