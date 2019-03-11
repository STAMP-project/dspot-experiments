/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.web;


import MultiDimensionalExtension.EXPAND_LIMIT_KEY;
import MultiDimensionalExtension.EXPAND_LIMIT_MAX_KEY;
import javax.xml.namespace.QName;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.data.test.MockData;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Test;


public class MultiDimLayerPanelTest extends GeoServerWicketTestSupport {
    protected static final QName WATERTEMP = new QName(MockData.SF_URI, "watertemp", MockData.SF_PREFIX);

    @Test
    public void testExtensionPanel() {
        LayerInfo waterTemp = getCatalog().getLayerByName(getLayerId(MultiDimLayerPanelTest.WATERTEMP));
        MetadataMap metadata = waterTemp.getResource().getMetadata();
        metadata.put(EXPAND_LIMIT_KEY, "50");
        metadata.put(EXPAND_LIMIT_MAX_KEY, "100");
        MultiDimLayerPanel panel = tester.startComponentInPage(new MultiDimLayerPanel("foo", new org.apache.wicket.model.Model(waterTemp)));
        // print(tester.getLastRenderedPage(), true, true, true);
        tester.assertNoErrorMessage();
        tester.assertModelValue("foo:defaultExpandLimit", "50");
        tester.assertModelValue("foo:maxExpandLimit", "100");
    }
}

