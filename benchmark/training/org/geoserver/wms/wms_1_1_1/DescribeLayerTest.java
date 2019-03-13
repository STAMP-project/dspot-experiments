/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import MockData.FORESTS;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DescribeLayerTest extends WMSTestSupport {
    @Test
    public void testDescribeLayerVersion111() throws Exception {
        String layer = ((FORESTS.getPrefix()) + ":") + (FORESTS.getLocalPart());
        String request = "wms?service=wms&version=1.1.1&request=DescribeLayer&layers=" + layer;
        Assert.assertEquals("src/test/resources/geoserver", getGeoServer().getGlobal().getProxyBaseUrl());
        Document dom = getAsDOM(request, true);
        Assert.assertEquals("1.1.1", dom.getDocumentElement().getAttributes().getNamedItem("version").getNodeValue());
    }

    // @Test
    // public void testDescribeLayerVersion110() throws Exception {
    // String layer = MockData.FORESTS.getPrefix() + ":" + MockData.FORESTS.getLocalPart();
    // String request = "wms?service=wms&version=1.1.0&request=DescribeLayer&layers=" +
    // layer;
    // Document dom = getAsDOM(request);
    // assertEquals("1.1.0",
    // dom.getDocumentElement().getAttributes().getNamedItem("version").getNodeValue());
    // }
    @Test
    public void testWorkspaceQualified() throws Exception {
        Document dom = getAsDOM(("cite/wms?service=wms&version=1.1.1&request=DescribeLayer" + "&layers=PrimitiveGeoFeature"), true);
        Assert.assertEquals("ServiceExceptionReport", dom.getDocumentElement().getNodeName());
        dom = getAsDOM(("sf/wms?service=wms&version=1.1.1&request=DescribeLayer" + "&layers=PrimitiveGeoFeature"), true);
        // print(dom);
        Assert.assertEquals("WMS_DescribeLayerResponse", dom.getDocumentElement().getNodeName());
        Element e = ((Element) (dom.getElementsByTagName("LayerDescription").item(0)));
        String attribute = e.getAttribute("owsURL");
        Assert.assertTrue(attribute.contains("sf/wfs"));
    }
}

