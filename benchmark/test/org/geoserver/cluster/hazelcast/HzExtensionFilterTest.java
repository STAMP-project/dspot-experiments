/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster.hazelcast;


import GeoServerExtensionsHelper.ExtensionsHelperRule;
import org.easymock.EasyMock;
import org.geoserver.platform.ExtensionFilter;
import org.geoserver.platform.GeoServerExtensionsHelper;
import org.geoserver.util.CacheProvider;
import org.geoserver.util.DefaultCacheProvider;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class HzExtensionFilterTest {
    @Rule
    public ExtensionsHelperRule extensions = new GeoServerExtensionsHelper.ExtensionsHelperRule();

    @Test
    public void testActive() {
        CacheProvider rivalProvider = EasyMock.createMock("rivalProvider", CacheProvider.class);
        CacheProvider hzProvider = EasyMock.createMock("hzProvider", HzCacheProvider.class);
        EasyMock.replay(rivalProvider, hzProvider);
        ExtensionFilter filter = new HzExtensionFilter();
        extensions.singleton("filter", filter, ExtensionFilter.class);
        extensions.singleton("rivalProvider", rivalProvider, CacheProvider.class);
        extensions.singleton("hzProvider", hzProvider, CacheProvider.class, HzCacheProvider.class);
        CacheProvider result = DefaultCacheProvider.findProvider();
        Assert.assertThat(result, Matchers.sameInstance(hzProvider));// Clustered provider used

        EasyMock.verify(rivalProvider, hzProvider);
    }
}

