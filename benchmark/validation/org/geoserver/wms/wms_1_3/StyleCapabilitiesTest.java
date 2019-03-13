/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_3;


import javax.xml.namespace.QName;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * Tests for GEOS-8063: WMS1.3.0 SLD definition can break getcapabilities
 */
public class StyleCapabilitiesTest extends WMSTestSupport {
    private static final String CAPABILITIES_REQUEST = "wms?request=getCapabilities&version=1.3.0";

    private static final String LAYER_NAME_WITH_STYLE_TITLE = "states_with_style_title";

    private static final String LAYER_NAME_WITHOUT_STYLE_TITLE = "states_without_style_title";

    private static final String LAYER_NAME_WITHOUT_STYLE_DESCRIPTION = "states_without_style_description";

    private static final String STYLE_NAME_WITH_TITLE = "style_with_style_title";

    private static final String STYLE_NAME_WITHOUT_TITLE = "style_without_style_title";

    private static final String STYLE_NAME_WITHOUT_DESCRIPTION = "style_without_style_description";

    private static final QName LAYER_WITH_SYTLE_TITLE = new QName(MockData.DEFAULT_URI, StyleCapabilitiesTest.LAYER_NAME_WITH_STYLE_TITLE, MockData.DEFAULT_PREFIX);

    private static final QName LAYER_WITHOUT_STYLE_TITLE = new QName(MockData.DEFAULT_URI, StyleCapabilitiesTest.LAYER_NAME_WITHOUT_STYLE_TITLE, MockData.DEFAULT_PREFIX);

    private static final QName LAYER_WITHOUT_STYLE_DESCRIPTION = new QName(MockData.DEFAULT_URI, StyleCapabilitiesTest.LAYER_NAME_WITHOUT_STYLE_DESCRIPTION, MockData.DEFAULT_PREFIX);

    private static final String BASE = "src/test/resources/geoserver";

    @Test
    public void testLayerStyleWithTitle() throws Exception {
        Document dom = dom(get(StyleCapabilitiesTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check we have the userStyle title
        assertXpathEvaluatesTo("Population in the United States", getLayerStyleTitleXPath(StyleCapabilitiesTest.LAYER_NAME_WITH_STYLE_TITLE), dom);
    }

    @Test
    public void testLayerStyleWithoutTitle() throws Exception {
        Document dom = dom(get(StyleCapabilitiesTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check we have the style name
        assertXpathEvaluatesTo(StyleCapabilitiesTest.STYLE_NAME_WITHOUT_TITLE, getLayerStyleTitleXPath(StyleCapabilitiesTest.LAYER_NAME_WITHOUT_STYLE_TITLE), dom);
    }

    @Test
    public void testLayerStyleWithoutDescription() throws Exception {
        Document dom = dom(get(StyleCapabilitiesTest.CAPABILITIES_REQUEST), false);
        // print(dom);
        // check we have the style name
        assertXpathEvaluatesTo(StyleCapabilitiesTest.STYLE_NAME_WITHOUT_DESCRIPTION, getLayerStyleTitleXPath(StyleCapabilitiesTest.LAYER_NAME_WITHOUT_STYLE_DESCRIPTION), dom);
    }
}

