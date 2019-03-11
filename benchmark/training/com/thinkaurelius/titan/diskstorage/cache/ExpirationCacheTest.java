package com.thinkaurelius.titan.diskstorage.cache;


import java.time.Duration;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class ExpirationCacheTest extends KCVSCacheTest {
    public static final String METRICS_STRING = "metrics";

    public static final long CACHE_SIZE = (1024 * 1024) * 48;// 48 MB


    @Test
    public void testExpiration() throws Exception {
        testExpiration(Duration.ofMillis(200));
        testExpiration(Duration.ofSeconds(4));
        testExpiration(Duration.ofSeconds(1));
    }

    @Test
    public void testGracePeriod() throws Exception {
        testGracePeriod(Duration.ofMillis(200));
        testGracePeriod(Duration.ZERO);
        testGracePeriod(Duration.ofSeconds(1));
    }
}

