/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web;


import MultiDimensionalExtension.EXPAND_LIMIT_KEY;
import MultiDimensionalExtension.EXPAND_LIMIT_MAX_KEY;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.gwc.wmts.WMTSInfoImpl;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Test;


public class MultiDimAdminPanelTest extends GeoServerWicketTestSupport {
    @Test
    public void testExtensionPanel() {
        WMTSInfoImpl info = new WMTSInfoImpl();
        MetadataMap metadata = info.getMetadata();
        metadata.put(EXPAND_LIMIT_KEY, "50");
        metadata.put(EXPAND_LIMIT_MAX_KEY, "100");
        MultiDimAdminPanel panel = tester.startComponentInPage(new MultiDimAdminPanel("foo", new org.apache.wicket.model.Model(info)));
        print(tester.getLastRenderedPage(), true, true, true);
        tester.assertNoErrorMessage();
        tester.assertModelValue("foo:defaultExpandLimit", "50");
        tester.assertModelValue("foo:maxExpandLimit", "100");
    }
}

