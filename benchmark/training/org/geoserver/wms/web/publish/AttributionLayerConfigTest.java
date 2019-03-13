/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.web.publish;


import MockData.PONDS;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Test;


public class AttributionLayerConfigTest extends GeoServerWicketTestSupport {
    @Test
    public void testLayer() {
        final LayerInfo layer = getCatalog().getLayerByName(PONDS.getLocalPart());
        testPublished(new org.apache.wicket.model.Model<LayerInfo>(layer));
    }

    @Test
    public void testLayerGroup() {
        final LayerGroupInfo layerGroup = getCatalog().getFactory().createLayerGroup();
        testPublished(new org.apache.wicket.model.Model<LayerGroupInfo>(layerGroup));
    }
}

