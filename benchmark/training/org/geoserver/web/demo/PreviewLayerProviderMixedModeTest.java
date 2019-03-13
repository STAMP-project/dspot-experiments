/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.demo;


import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class PreviewLayerProviderMixedModeTest extends GeoServerWicketTestSupport {
    @Test
    public void testMixedMode() throws Exception {
        PreviewLayerProvider provider = new PreviewLayerProvider();
        // full access
        login("cite", "cite");
        Assert.assertTrue(previewHasBuildings(provider));
        // no access, but no exception either, since this is not a direct access
        login("cite_mixed", "cite");
        Assert.assertFalse(previewHasBuildings(provider));
    }
}

