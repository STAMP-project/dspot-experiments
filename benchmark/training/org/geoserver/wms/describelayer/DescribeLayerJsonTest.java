/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.describelayer;


import MockData.FORESTS;
import org.geoserver.wfs.json.JSONType;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Unit test suite for {@link JSONDescribeLayerResponse}
 *
 * @author Carlo Cancellieri - GeoSolutions
 * @version $Id$
 */
public class DescribeLayerJsonTest extends WMSTestSupport {
    @Test
    public void testBuild() throws Exception {
        try {
            new JSONDescribeLayerResponse(getWMS(), "fail");
            Assert.fail("Should fails");
        } catch (Exception e) {
        }
    }

    /**
     * Tests jsonp with custom callback function
     */
    @Test
    public void testCustomJSONP() throws Exception {
        String layer = ((FORESTS.getPrefix()) + ":") + (FORESTS.getLocalPart());
        String request = ((((((((("wms?version=1.1.1" + ("&request=DescribeLayer" + "&layers=")) + layer) + "&query_layers=") + layer) + "&width=20&height=20") + "&outputFormat=") + (JSONType.jsonp)) + "&format_options=") + (JSONType.CALLBACK_FUNCTION_KEY)) + ":DescribeLayer";
        JSONType.setJsonpEnabled(true);
        String result = getAsString(request);
        JSONType.setJsonpEnabled(false);
        checkJSONPDescribeLayer(result, layer);
    }

    /**
     * Tests JSON
     */
    @Test
    public void testSimpleJSON() throws Exception {
        String layer = ((FORESTS.getPrefix()) + ":") + (FORESTS.getLocalPart());
        String request = (((((("wms?version=1.1.1" + ("&request=DescribeLayer" + "&layers=")) + layer) + "&query_layers=") + layer) + "&width=20&height=20") + "&outputFormat=") + (JSONType.json);
        String result = getAsString(request);
        checkJSONDescribeLayer(result, layer);
    }

    /**
     * Tests jsonp with custom callback function
     */
    @Test
    public void testJSONLayerGroup() throws Exception {
        String layer = WMSTestSupport.NATURE_GROUP;
        String request = (((((("wms?version=1.1.1" + ("&request=DescribeLayer" + "&layers=")) + layer) + "&query_layers=") + layer) + "&width=20&height=20") + "&outputFormat=") + (JSONType.json);
        String result = getAsString(request);
        checkJSONDescribeLayerGroup(result, layer);
    }

    @Test
    public void testJSONDescribeLayerCharset() throws Exception {
        String layer = ((FORESTS.getPrefix()) + ":") + (FORESTS.getLocalPart());
        String request = (((((("wms?version=1.1.1" + ("&request=DescribeLayer" + "&layers=")) + layer) + "&query_layers=") + layer) + "&width=20&height=20") + "&outputFormat=") + (JSONType.json);
        MockHttpServletResponse result = getAsServletResponse(request, "");
        Assert.assertTrue("UTF-8".equals(result.getCharacterEncoding()));
    }
}

