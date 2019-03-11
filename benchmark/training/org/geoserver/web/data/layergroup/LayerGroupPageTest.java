/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.layergroup;


import org.apache.wicket.markup.repeater.data.DataView;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class LayerGroupPageTest extends LayerGroupBaseTest {
    @Test
    public void testLoad() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(LayerGroupPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        @SuppressWarnings("unchecked")
        DataView<LayerGroupInfo> dv = ((DataView<LayerGroupInfo>) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        Assert.assertEquals(getCatalog().getLayerGroups().size(), dv.size());
        LayerGroupInfo lg = ((LayerGroupInfo) (dv.getDataProvider().iterator(0, 1).next()));
        Assert.assertEquals(getCatalog().getLayerGroups().get(0), lg);
    }
}

