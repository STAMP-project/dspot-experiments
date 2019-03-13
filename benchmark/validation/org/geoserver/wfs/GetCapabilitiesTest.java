/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import CiteTestData.UPDATES;
import HttpHeaders.CACHE_CONTROL;
import HttpHeaders.ETAG;
import HttpStatus.OK;
import ResourceErrorHandling.SKIP_MISCONFIGURED_LAYERS;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.config.CapabilitiesCacheHeadersCallback;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class GetCapabilitiesTest extends WFSTestSupport {
    @Test
    public void testGet() throws Exception {
        Document doc = getAsDOM("wfs?service=WFS&version=1.0.0&request=getCapabilities");
        Assert.assertEquals("WFS_Capabilities", doc.getDocumentElement().getNodeName());
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertTrue(((xpath.getMatchingNodes("//wfs:FeatureType", doc).getLength()) > 0));
    }

    @Test
    public void testSkipMisconfiguredLayers() throws Exception {
        // configure geoserver to skip misconfigured layers
        GeoServerInfo global = getGeoServer().getGlobal();
        global.setResourceErrorHandling(SKIP_MISCONFIGURED_LAYERS);
        getGeoServer().save(global);
        // introduce misconfiguration
        FeatureTypeInfo ftype = getCatalog().getFeatureTypeByName(UPDATES.getLocalPart());
        ftype.setLatLonBoundingBox(null);
        getCatalog().save(ftype);
        // fetch capabilities document
        Document doc = getAsDOM("wfs?version=1.0.0&service=WFS&request=getCapabilities");
        // print(doc);
        int count = 0;
        for (FeatureTypeInfo ft : getCatalog().getFeatureTypes()) {
            if (ft.enabled())
                count++;

        }
        // print(doc);
        assertXpathEvaluatesTo(String.valueOf((count - 1)), "count(//wfs:FeatureType)", doc);
    }

    @Test
    public void testNamespaceFilter() throws Exception {
        // filter on an existing namespace
        Document doc = getAsDOM("wfs?service=WFS&version=1.0.0&request=getCapabilities&namespace=sf");
        Element e = doc.getDocumentElement();
        Assert.assertEquals("WFS_Capabilities", e.getLocalName());
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertTrue(((xpath.getMatchingNodes("//wfs:FeatureType/wfs:Name[starts-with(., sf)]", doc).getLength()) > 0));
        Assert.assertEquals(0, xpath.getMatchingNodes("//wfs:FeatureType/wfs:Name[not(starts-with(., sf))]", doc).getLength());
        // try again with a missing one
        doc = getAsDOM("wfs?service=WFS&version=1.0.0&request=getCapabilities&namespace=NotThere");
        e = doc.getDocumentElement();
        Assert.assertEquals("WFS_Capabilities", e.getLocalName());
        Assert.assertEquals(0, xpath.getMatchingNodes("//wfs:FeatureType", doc).getLength());
    }

    @Test
    public void testPost() throws Exception {
        String xml = "<GetCapabilities service=\"WFS\" version=\"1.0.0\"" + (((" xmlns=\"http://www.opengis.net/wfs\" " + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ") + " xsi:schemaLocation=\"http://www.opengis.net/wfs ") + " http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\"/>");
        Document doc = postAsDOM("wfs", xml);
        Assert.assertEquals("WFS_Capabilities", doc.getDocumentElement().getNodeName());
    }

    @Test
    public void testOutputFormats() throws Exception {
        Document doc = getAsDOM("wfs?service=WFS&request=getCapabilities&version=1.0.0");
        Element outputFormats = getFirstElementByTagName(doc, "ResultFormat");
        NodeList formats = outputFormats.getChildNodes();
        TreeSet s1 = new TreeSet();
        for (int i = 0; i < (formats.getLength()); i++) {
            String format = formats.item(i).getNodeName();
            s1.add(format);
        }
        List extensions = GeoServerExtensions.extensions(WFSGetFeatureOutputFormat.class);
        TreeSet s2 = new TreeSet();
        for (Iterator e = extensions.iterator(); e.hasNext();) {
            WFSGetFeatureOutputFormat extension = ((WFSGetFeatureOutputFormat) (e.next()));
            s2.add(extension.getCapabilitiesElementName());
        }
        Assert.assertEquals(s1, s2);
    }

    @Test
    public void testSupportedSpatialOperators() throws Exception {
        Document doc = getAsDOM("wfs?service=WFS&request=getCapabilities&version=1.0.0");
        Element spatialOperators = getFirstElementByTagName(doc, "ogc:Spatial_Operators");
        NodeList ops = spatialOperators.getChildNodes();
        TreeSet<String> o = new TreeSet<String>();
        for (int i = 0; i < (ops.getLength()); i++) {
            String operator = ops.item(i).getLocalName();
            o.add(operator);
        }
        List<String> expectedSpatialOperators = getSupportedSpatialOperatorsList(true);
        Assert.assertEquals(expectedSpatialOperators.size(), o.size());
        Assert.assertTrue(o.containsAll(expectedSpatialOperators));
    }

    @Test
    public void testTypeNameCount() throws Exception {
        // filter on an existing namespace
        Document doc = getAsDOM("wfs?service=WFS&version=1.0.0&request=getCapabilities");
        Element e = doc.getDocumentElement();
        Assert.assertEquals("WFS_Capabilities", e.getLocalName());
        XpathEngine xpath = XMLUnit.newXpathEngine();
        final List<FeatureTypeInfo> enabledTypes = getCatalog().getFeatureTypes();
        for (Iterator<FeatureTypeInfo> it = enabledTypes.iterator(); it.hasNext();) {
            FeatureTypeInfo ft = it.next();
            if (!(ft.enabled())) {
                it.remove();
            }
        }
        final int enabledCount = enabledTypes.size();
        Assert.assertEquals(enabledCount, xpath.getMatchingNodes("/wfs:WFS_Capabilities/wfs:FeatureTypeList/wfs:FeatureType", doc).getLength());
    }

    @Test
    public void testTypeNames() throws Exception {
        // filter on an existing namespace
        Document doc = getAsDOM("wfs?service=WFS&version=1.0.0&request=getCapabilities");
        print(doc);
        Element e = doc.getDocumentElement();
        Assert.assertEquals("WFS_Capabilities", e.getLocalName());
        final List<FeatureTypeInfo> enabledTypes = getCatalog().getFeatureTypes();
        for (Iterator<FeatureTypeInfo> it = enabledTypes.iterator(); it.hasNext();) {
            FeatureTypeInfo ft = it.next();
            if (ft.enabled()) {
                String prefixedName = ft.getPrefixedName();
                String xpathExpr = (("/wfs:WFS_Capabilities/wfs:FeatureTypeList/" + "wfs:FeatureType/wfs:Name[text()=\"") + prefixedName) + "\"]";
                XMLAssert.assertXpathExists(xpathExpr, doc);
            }
        }
    }

    @Test
    public void testWorkspaceQualified() throws Exception {
        // filter on an existing namespace
        Document doc = getAsDOM("sf/wfs?service=WFS&version=1.0.0&request=getCapabilities");
        Element e = doc.getDocumentElement();
        Assert.assertEquals("WFS_Capabilities", e.getLocalName());
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertTrue(((xpath.getMatchingNodes("//wfs:FeatureType/wfs:Name[starts-with(., sf)]", doc).getLength()) > 0));
        Assert.assertEquals(0, xpath.getMatchingNodes("//wfs:FeatureType/wfs:Name[not(starts-with(., sf))]", doc).getLength());
        Assert.assertEquals(6, xpath.getMatchingNodes("//wfs:Get[contains(@onlineResource,'sf/wfs')]", doc).getLength());
        Assert.assertEquals(6, xpath.getMatchingNodes("//wfs:Post[contains(@onlineResource,'sf/wfs')]", doc).getLength());
        // TODO: test with a non existing workspace
    }

    @Test
    public void testLayerQualified() throws Exception {
        // filter on an existing namespace
        Document doc = getAsDOM("sf/PrimitiveGeoFeature/wfs?service=WFS&version=1.0.0&request=getCapabilities");
        Element e = doc.getDocumentElement();
        Assert.assertEquals("WFS_Capabilities", e.getLocalName());
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertEquals(1, xpath.getMatchingNodes("//wfs:FeatureType/wfs:Name[starts-with(., sf)]", doc).getLength());
        Assert.assertEquals(0, xpath.getMatchingNodes("//wfs:FeatureType/wfs:Name[not(starts-with(., sf))]", doc).getLength());
        Assert.assertEquals(6, xpath.getMatchingNodes("//wfs:Get[contains(@onlineResource,'sf/PrimitiveGeoFeature/wfs')]", doc).getLength());
        Assert.assertEquals(6, xpath.getMatchingNodes("//wfs:Post[contains(@onlineResource,'sf/PrimitiveGeoFeature/wfs')]", doc).getLength());
        // TODO: test with a non existing workspace
    }

    @Test
    public void testNonAdvertisedLayer() throws Exception {
        String layerId = getLayerId(CiteTestData.MLINES);
        LayerInfo layer = getCatalog().getLayerByName(layerId);
        try {
            // now you see me
            Document dom = getAsDOM("wfs?request=getCapabilities&version=1.0.0");
            assertXpathExists((("//wfs:FeatureType[wfs:Name='" + layerId) + "']"), dom);
            // now you don't!
            layer.setAdvertised(false);
            getCatalog().save(layer);
            dom = getAsDOM("wfs?request=getCapabilities&version=1.0.0");
            assertXpathNotExists((("//wfs:FeatureType[wfs:Name = '" + layerId) + "']"), dom);
        } finally {
            layer.setAdvertised(true);
            getCatalog().save(layer);
        }
    }

    @Test
    public void testCachingHeaders() throws Exception {
        // Check the cache control headers are set
        MockHttpServletRequest request = createGetRequestWithHeaders("wfs?service=WFS&version=1.0.0&request=getCapabilities");
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals(OK.value(), response.getStatus());
        // check caching headers
        Assert.assertEquals("max-age=0, must-revalidate", response.getHeader(CACHE_CONTROL));
    }

    @Test
    public void testCachingHeadersDisabled() throws Exception {
        CapabilitiesCacheHeadersCallback callback = GeoServerExtensions.bean(CapabilitiesCacheHeadersCallback.class);
        boolean backup = callback.isCapabilitiesCacheHeadersEnabled();
        try {
            callback.setCapabilitiesCacheHeadersEnabled(false);
            // first request, get the etag
            MockHttpServletRequest request = createGetRequestWithHeaders("wfs?service=WFS&version=1.0.0&request=getCapabilities");
            MockHttpServletResponse response = dispatch(request);
            Assert.assertEquals(OK.value(), response.getStatus());
            // check caching headers are not there
            Assert.assertNull(response.getHeader(ETAG));
            Assert.assertNull(response.getHeader(CACHE_CONTROL));
        } finally {
            callback.setCapabilitiesCacheHeadersEnabled(backup);
        }
    }
}

