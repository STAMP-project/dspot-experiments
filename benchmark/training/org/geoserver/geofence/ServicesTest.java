/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class ServicesTest extends GeofenceBaseTest {
    @Test
    public void testAdmin() throws Exception {
        if (!(IS_GEOFENCE_AVAILABLE)) {
            return;
        }
        // check from the caps he can access everything
        Document dom = getAsDOM("wms?request=GetCapabilities&version=1.1.1&service=WMS");
        // print(dom);
        assertXpathEvaluatesTo("11", "count(//Layer[starts-with(Name, 'cite:')])", dom);
        assertXpathEvaluatesTo("3", "count(//Layer[starts-with(Name, 'sf:')])", dom);
        assertXpathEvaluatesTo("8", "count(//Layer[starts-with(Name, 'cdf:')])", dom);
    }

    @Test
    public void testCiteCapabilities() throws Exception {
        if (!(IS_GEOFENCE_AVAILABLE)) {
            return;
        }
        loginAsCite();
        // check from the caps he can access cite and sf, but not others
        Document dom = getAsDOM("wms?request=GetCapabilities&version=1.1.1&service=wms");
        // print(dom);
        assertXpathEvaluatesTo("11", "count(//Layer[starts-with(Name, 'cite:')])", dom);
        assertXpathEvaluatesTo("3", "count(//Layer[starts-with(Name, 'sf:')])", dom);
        assertXpathEvaluatesTo("0", "count(//Layer[starts-with(Name, 'cdf:')])", dom);
    }

    @Test
    public void testCiteLayers() throws Exception {
        if (!(IS_GEOFENCE_AVAILABLE)) {
            return;
        }
        loginAsCite();
        // try a getmap/reflector on a sf layer, should work
        MockHttpServletResponse response = getAsServletResponse(("wms/reflect?layers=" + (getLayerId(MockData.BASIC_POLYGONS))));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        // try a getmap/reflector on a sf layer, should work
        response = getAsServletResponse(("wms/reflect?layers=" + (getLayerId(MockData.GENERICENTITY))));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("image/png", response.getContentType());
        // try a getfeature on a sf layer
        response = getAsServletResponse(("wfs?service=wfs&version=1.0.0&request=getfeature&typeName=" + (getLayerId(MockData.GENERICENTITY))));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/xml", response.getContentType());
        String content = response.getContentAsString();
        LOGGER.info(("Content: " + content));
        // assertTrue(content.contains("Unknown namespace [sf]"));
        Assert.assertTrue(content.contains("Feature type sf:GenericEntity unknown"));
    }
}

