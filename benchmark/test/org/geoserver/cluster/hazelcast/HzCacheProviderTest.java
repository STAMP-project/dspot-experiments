/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster.hazelcast;


import com.google.common.cache.Cache;
import java.io.Serializable;
import org.geoserver.catalog.Info;
import org.junit.Assert;
import org.junit.Test;


public class HzCacheProviderTest {
    private HzCacheProvider cacheProvider;

    @Test
    public void testGetCache() {
        Cache<Serializable, Serializable> cache = this.cacheProvider.getCache("test");
        Assert.assertNotNull(cache);
    }

    @Test
    public void testGetCacheCatalog() {
        Cache<String, Info> cache = this.cacheProvider.getCache("catalog");
        Assert.assertNotNull(cache);
    }
}

