/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web;


import org.apache.wicket.markup.html.WebMarkupContainer;
import org.geoserver.GeoServerNodeData;
import org.junit.Test;


public class CustomGeoServerNodeIdTest extends GeoServerWicketTestSupport {
    @Test
    public void testNodeInfoInvisible() throws Exception {
        CustomGeoServerNodeIdTest.CustomNodeInfo.ID = null;
        DefaultGeoServerNodeInfo.initializeFromEnviroment();
        GeoServerWicketTestSupport.tester.startPage(GeoServerHomePage.class);
        GeoServerWicketTestSupport.tester.assertInvisible("nodeIdContainer");
    }

    @Test
    public void testNodeInfoVisible() throws Exception {
        CustomGeoServerNodeIdTest.CustomNodeInfo.ID = "testId";
        GeoServerWicketTestSupport.tester.startPage(GeoServerHomePage.class);
        GeoServerWicketTestSupport.tester.assertVisible("nodeIdContainer");
        GeoServerWicketTestSupport.tester.assertModelValue("nodeIdContainer:nodeId", "testId");
    }

    public static class CustomNodeInfo implements GeoServerNodeInfo {
        static String ID = null;

        static String STYLE = null;

        @Override
        public String getId() {
            return CustomGeoServerNodeIdTest.CustomNodeInfo.ID;
        }

        @Override
        public GeoServerNodeData getData() {
            return new GeoServerNodeData(CustomGeoServerNodeIdTest.CustomNodeInfo.ID, CustomGeoServerNodeIdTest.CustomNodeInfo.STYLE);
        }

        @Override
        public void customize(WebMarkupContainer nodeInfoContainer) {
            if ((CustomGeoServerNodeIdTest.CustomNodeInfo.STYLE) != null) {
                nodeInfoContainer.add(new org.apache.wicket.behavior.AttributeAppender("style", new org.apache.wicket.model.Model<String>(CustomGeoServerNodeIdTest.CustomNodeInfo.STYLE), ";"));
            }
        }
    }
}

