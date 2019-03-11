/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.config;


import junit.framework.Assert;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geowebcache.storage.blobstore.memory.guava.GuavaCacheProvider;
import org.junit.Test;


public class GWCConfigTest extends GeoServerSystemTestSupport {
    private GWCConfig oldDefaults;

    private GWCConfig config;

    @Test
    public void testSaneConfig() {
        Assert.assertTrue(config.isSane());
        Assert.assertSame(config, config.saneConfig());
        Assert.assertTrue(oldDefaults.isSane());
        Assert.assertSame(oldDefaults, oldDefaults.saneConfig());
        config.setMetaTilingX((-1));
        Assert.assertFalse(config.isSane());
        Assert.assertTrue((config = config.saneConfig()).isSane());
        config.setMetaTilingY((-1));
        Assert.assertFalse(config.isSane());
        Assert.assertTrue((config = config.saneConfig()).isSane());
        config.setGutter((-1));
        Assert.assertFalse(config.isSane());
        Assert.assertTrue((config = config.saneConfig()).isSane());
        config.getDefaultCachingGridSetIds().clear();
        Assert.assertFalse(config.isSane());
        Assert.assertTrue((config = config.saneConfig()).isSane());
        config.getDefaultCoverageCacheFormats().clear();
        Assert.assertFalse(config.isSane());
        Assert.assertTrue((config = config.saneConfig()).isSane());
        config.getDefaultOtherCacheFormats().clear();
        Assert.assertFalse(config.isSane());
        Assert.assertTrue((config = config.saneConfig()).isSane());
        config.getDefaultVectorCacheFormats().clear();
        Assert.assertFalse(config.isSane());
        Assert.assertTrue((config = config.saneConfig()).isSane());
    }

    @Test
    public void testClone() {
        GWCConfig clone = config.clone();
        Assert.assertEquals(config, clone);
        Assert.assertNotSame(config.getDefaultCachingGridSetIds(), clone.getDefaultCachingGridSetIds());
        Assert.assertNotSame(config.getDefaultCoverageCacheFormats(), clone.getDefaultCoverageCacheFormats());
        Assert.assertNotSame(config.getDefaultOtherCacheFormats(), clone.getDefaultOtherCacheFormats());
        Assert.assertNotSame(config.getDefaultVectorCacheFormats(), clone.getDefaultVectorCacheFormats());
        Assert.assertNotSame(config.getCacheConfigurations(), clone.getCacheConfigurations());
        Assert.assertTrue(clone.getCacheConfigurations().containsKey(GuavaCacheProvider.class.toString()));
    }

    @Test
    public void testIsServiceEnabled() {
        config.setWMSCEnabled((!(config.isWMSCEnabled())));
        config.setTMSEnabled((!(config.isTMSEnabled())));
        Assert.assertEquals(config.isEnabled("wms"), config.isWMSCEnabled());
        Assert.assertEquals(config.isEnabled("WMS"), config.isWMSCEnabled());
        Assert.assertEquals(config.isEnabled("tms"), config.isTMSEnabled());
        Assert.assertEquals(config.isEnabled("TMS"), config.isTMSEnabled());
        Assert.assertTrue(config.isEnabled("anything else"));
    }
}

