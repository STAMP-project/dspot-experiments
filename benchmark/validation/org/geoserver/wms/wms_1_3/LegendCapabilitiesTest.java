/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_3;


import javax.xml.namespace.QName;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Test;
import org.w3c.dom.Document;


public class LegendCapabilitiesTest extends WMSTestSupport {
    private static final String CAPABILITIES_REQUEST = "wms?request=getCapabilities&version=1.3.0";

    // Reusing layer and SLD files from another test; their content doesn't really matter.
    // What is important for this test is the legend info we are adding.
    private static final String LAYER_NAME = "watertemp";

    private static final QName LAYER_QNAME = new QName(MockData.DEFAULT_URI, LegendCapabilitiesTest.LAYER_NAME, MockData.DEFAULT_PREFIX);

    private static final String LAYER_FILE = "custwatertemp.zip";

    private static final String STYLE_NAME = "temperature";

    private static final String STYLE_FILE = "../temperature.sld";

    private static final int LEGEND_WIDTH = 22;

    private static final int LEGEND_HEIGHT = 22;

    private static final String LEGEND_FORMAT = "image/jpeg";

    private static final String IMAGE_URL = "legend.png";

    private static final String BASE = "src/test/resources/geoserver";

    private static final String LAYER_NAME_WS = "watertemp_ws";

    private static final QName LAYER_QNAME_WS = new QName(MockData.DEFAULT_URI, LegendCapabilitiesTest.LAYER_NAME_WS, MockData.DEFAULT_PREFIX);

    private static final String STYLE_NAME_WS = "temperature_ws";

    @Test
    public void testCapabilities() throws Exception {
        Document dom = dom(get(LegendCapabilitiesTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        final String legendUrlPath = ("//wms:Layer[wms:Name='gs:" + (LegendCapabilitiesTest.LAYER_NAME)) + "']/wms:Style/wms:LegendURL";
        // Ensure capabilities document reflects the specified legend info
        assertXpathEvaluatesTo(String.valueOf(LegendCapabilitiesTest.LEGEND_WIDTH), (legendUrlPath + "/@width"), dom);
        assertXpathEvaluatesTo(String.valueOf(LegendCapabilitiesTest.LEGEND_HEIGHT), (legendUrlPath + "/@height"), dom);
        assertXpathEvaluatesTo(LegendCapabilitiesTest.LEGEND_FORMAT, (legendUrlPath + "/wms:Format"), dom);
        assertXpathEvaluatesTo((((LegendCapabilitiesTest.BASE) + "/styles/") + (LegendCapabilitiesTest.IMAGE_URL)), (legendUrlPath + "/wms:OnlineResource/@xlink:href"), dom);
        final String legendUrlPathWs = ("//wms:Layer[wms:Name='gs:" + (LegendCapabilitiesTest.LAYER_NAME_WS)) + "']/wms:Style/wms:LegendURL";
        // Ensure capabilities document reflects the specified legend info
        assertXpathEvaluatesTo(String.valueOf(LegendCapabilitiesTest.LEGEND_WIDTH), (legendUrlPathWs + "/@width"), dom);
        assertXpathEvaluatesTo(String.valueOf(LegendCapabilitiesTest.LEGEND_HEIGHT), (legendUrlPathWs + "/@height"), dom);
        assertXpathEvaluatesTo(LegendCapabilitiesTest.LEGEND_FORMAT, (legendUrlPathWs + "/wms:Format"), dom);
        assertXpathEvaluatesTo((((LegendCapabilitiesTest.BASE) + "/styles/gs/") + (LegendCapabilitiesTest.IMAGE_URL)), (legendUrlPathWs + "/wms:OnlineResource/@xlink:href"), dom);
    }
}

