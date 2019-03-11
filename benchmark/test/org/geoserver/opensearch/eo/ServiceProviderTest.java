/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.opensearch.eo;


import java.util.List;
import org.geoserver.catalog.ServiceResourceProvider;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ServiceProviderTest extends GeoServerSystemTestSupport {
    @Test
    public void testHideServiceConfigVector() {
        final ServiceResourceProvider provider = GeoServerExtensions.bean(ServiceResourceProvider.class);
        final List<String> services = provider.getServicesForLayerName(getLayerId(SystemTestData.POLYGONS));
        Assert.assertThat(services, Matchers.not(Matchers.contains("OSEO")));
        Assert.assertThat(services, Matchers.containsInAnyOrder("WMS", "WFS"));
    }

    @Test
    public void testHideServiceConfigRaster() {
        final ServiceResourceProvider provider = GeoServerExtensions.bean(ServiceResourceProvider.class);
        final List<String> services = provider.getServicesForLayerName(getLayerId(SystemTestData.TASMANIA_DEM));
        Assert.assertThat(services, Matchers.not(Matchers.contains("OSEO")));
        Assert.assertThat(services, Matchers.contains("WMS"));
    }
}

