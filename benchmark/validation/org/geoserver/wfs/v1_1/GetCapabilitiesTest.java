/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v1_1;


import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.MetadataLinkInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.wfs.WFSGetFeatureOutputFormat;
import org.geoserver.wfs.WFSInfo;
import org.geoserver.wfs.WFSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class GetCapabilitiesTest extends WFSTestSupport {
    @Test
    public void testGet() throws Exception {
        Document doc = getAsDOM("wfs?service=WFS&request=getCapabilities&version=1.1.0");
        String docText = getAsString("wfs?service=WFS&request=GetCapabilities&version=1.1.0");
        Assert.assertEquals("wfs:WFS_Capabilities", doc.getDocumentElement().getNodeName());
        Assert.assertEquals("1.1.0", doc.getDocumentElement().getAttribute("version"));
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertTrue(((xpath.getMatchingNodes("//wfs:FeatureType", doc).getLength()) > 0));
        Assert.assertFalse(docText, docText.contains("xmlns:xml="));
    }

    @Test
    public void testNamespaceFilter() throws Exception {
        // filter on an existing namespace
        Document doc = getAsDOM("wfs?service=WFS&version=1.1.0&request=getCapabilities&namespace=sf");
        Element e = doc.getDocumentElement();
        Assert.assertEquals("WFS_Capabilities", e.getLocalName());
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertTrue(((xpath.getMatchingNodes("//wfs:FeatureType/wfs:Name[starts-with(., sf)]", doc).getLength()) > 0));
        Assert.assertEquals(0, xpath.getMatchingNodes("//wfs:FeatureType/wfs:Name[not(starts-with(., sf))]", doc).getLength());
        // try again with a missing one
        doc = getAsDOM("wfs?service=WFS&version=1.1.0&request=getCapabilities&namespace=NotThere");
        e = doc.getDocumentElement();
        Assert.assertEquals("WFS_Capabilities", e.getLocalName());
        Assert.assertEquals(0, xpath.getMatchingNodes("//wfs:FeatureType", doc).getLength());
    }

    @Test
    public void testPost() throws Exception {
        String xml = "<GetCapabilities service=\"WFS\" version=\'1.1.0\'" + ((((((" xmlns=\"http://www.opengis.net/wfs\" " + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ") + " xmlns:ows=\"http://www.opengis.net/ows\" ") + " xsi:schemaLocation=\"http://www.opengis.net/wfs ") + " http://schemas.opengis.net/wfs/1.1.0/wfs.xsd\">") + "<ows:AcceptVersions><ows:Version>1.1.0</ows:Version></ows:AcceptVersions>") + "</GetCapabilities>");
        Document doc = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:WFS_Capabilities", doc.getDocumentElement().getNodeName());
        Assert.assertEquals("1.1.0", doc.getDocumentElement().getAttribute("version"));
    }

    @Test
    public void testPostNoSchemaLocation() throws Exception {
        String xml = "<GetCapabilities service=\"WFS\" version=\'1.1.0\'" + ((((" xmlns=\"http://www.opengis.net/wfs\" " + " xmlns:ows=\"http://www.opengis.net/ows\" ") + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >") + "<ows:AcceptVersions><ows:Version>1.1.0</ows:Version></ows:AcceptVersions>") + "</GetCapabilities>");
        Document doc = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:WFS_Capabilities", doc.getDocumentElement().getNodeName());
        Assert.assertEquals("1.1.0", doc.getDocumentElement().getAttribute("version"));
    }

    @Test
    public void testOutputFormats() throws Exception {
        Document doc = getAsDOM("wfs?service=WFS&request=getCapabilities&version=1.1.0");
        // print(doc);
        // let's look for the outputFormat parameter values inside of the GetFeature operation
        // metadata
        XpathEngine engine = XMLUnit.newXpathEngine();
        NodeList formats = engine.getMatchingNodes("//ows:Operation[@name=\"GetFeature\"]/ows:Parameter[@name=\"outputFormat\"]/ows:Value", doc);
        Set<String> s1 = new TreeSet<String>();
        for (int i = 0; i < (formats.getLength()); i++) {
            String format = formats.item(i).getFirstChild().getNodeValue();
            s1.add(format);
        }
        List<WFSGetFeatureOutputFormat> extensions = GeoServerExtensions.extensions(WFSGetFeatureOutputFormat.class);
        Set<String> s2 = new TreeSet<String>();
        for (Iterator e = extensions.iterator(); e.hasNext();) {
            WFSGetFeatureOutputFormat extension = ((WFSGetFeatureOutputFormat) (e.next()));
            s2.addAll(extension.getOutputFormats());
        }
        Assert.assertEquals(s1, s2);
    }

    @Test
    public void testSupportedSpatialOperators() throws Exception {
        Document doc = getAsDOM("wfs?service=WFS&request=getCapabilities&version=1.1.0");
        // let's look for the spatial capabilities, extract all the spatial operators
        XpathEngine engine = XMLUnit.newXpathEngine();
        NodeList spatialOperators = engine.getMatchingNodes("//ogc:Spatial_Capabilities/ogc:SpatialOperators/ogc:SpatialOperator/@name", doc);
        Set<String> ops = new TreeSet<String>();
        for (int i = 0; i < (spatialOperators.getLength()); i++) {
            String format = spatialOperators.item(i).getFirstChild().getNodeValue();
            ops.add(format);
        }
        List<String> expectedSpatialOperators = getSupportedSpatialOperatorsList(false);
        Assert.assertEquals(expectedSpatialOperators.size(), ops.size());
        Assert.assertTrue(ops.containsAll(expectedSpatialOperators));
    }

    @Test
    public void testFunctionArgCount() throws Exception {
        Document doc = getAsDOM("wfs?service=WFS&request=getCapabilities&version=1.1.0");
        // print(doc);
        // let's check the argument count of "abs" function
        assertXpathEvaluatesTo("1", "//ogc:FunctionName[text()=\"abs\"]/@nArgs", doc);
    }

    @Test
    public void testTypeNameCount() throws Exception {
        // filter on an existing namespace
        Document doc = getAsDOM("wfs?service=WFS&version=1.1.0&request=getCapabilities");
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
        Document doc = getAsDOM("wfs?service=WFS&version=1.1.0&request=getCapabilities");
        Element e = doc.getDocumentElement();
        Assert.assertEquals("WFS_Capabilities", e.getLocalName());
        final List<FeatureTypeInfo> enabledTypes = getCatalog().getFeatureTypes();
        for (Iterator<FeatureTypeInfo> it = enabledTypes.iterator(); it.hasNext();) {
            FeatureTypeInfo ft = it.next();
            if (ft.enabled()) {
                String prefixedName = ft.prefixedName();
                String xpathExpr = (("/wfs:WFS_Capabilities/wfs:FeatureTypeList/" + "wfs:FeatureType/wfs:Name[text()=\"") + prefixedName) + "\"]";
                XMLAssert.assertXpathExists(xpathExpr, doc);
            }
        }
    }

    @Test
    public void testLayerQualified() throws Exception {
        // filter on an existing namespace
        Document doc = getAsDOM("sf/PrimitiveGeoFeature/wfs?service=WFS&version=1.1.0&request=getCapabilities");
        Element e = doc.getDocumentElement();
        Assert.assertEquals("WFS_Capabilities", e.getLocalName());
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertEquals(1, xpath.getMatchingNodes("//wfs:FeatureType/wfs:Name[starts-with(., sf)]", doc).getLength());
        Assert.assertEquals(0, xpath.getMatchingNodes("//wfs:FeatureType/wfs:Name[not(starts-with(., sf))]", doc).getLength());
        Assert.assertEquals(7, xpath.getMatchingNodes("//ows:Get[contains(@xlink:href,'sf/PrimitiveGeoFeature/wfs')]", doc).getLength());
        Assert.assertEquals(7, xpath.getMatchingNodes("//ows:Post[contains(@xlink:href,'sf/PrimitiveGeoFeature/wfs')]", doc).getLength());
        // TODO: test with a non existing workspace
    }

    @Test
    public void testMetadataLinks() throws Exception {
        FeatureTypeInfo mpolys = getCatalog().getFeatureTypeByName(getLayerId(MockTestData.MPOLYGONS));
        // a valid link whose metadata type needs tweaking
        MetadataLinkInfo ml1 = getCatalog().getFactory().createMetadataLink();
        ml1.setMetadataType("ISO19115:2003");
        ml1.setType("text/html");
        ml1.setContent("http://www.geoserver.org");
        mpolys.getMetadataLinks().add(ml1);
        // a valid one
        MetadataLinkInfo ml2 = getCatalog().getFactory().createMetadataLink();
        ml2.setMetadataType("FGDC");
        ml2.setType("text/html");
        ml2.setContent("http://www.geoserver.org");
        mpolys.getMetadataLinks().add(ml2);
        // an invalid one, not a valid type
        MetadataLinkInfo ml3 = getCatalog().getFactory().createMetadataLink();
        ml3.setMetadataType("other");
        ml3.setType("text/html");
        ml3.setContent("http://www.geoserver.org");
        mpolys.getMetadataLinks().add(ml3);
        // an invalid one, not a valid format
        MetadataLinkInfo ml4 = getCatalog().getFactory().createMetadataLink();
        ml4.setMetadataType("FGDC");
        ml4.setType("application/zip");
        ml4.setContent("http://www.geoserver.org");
        mpolys.getMetadataLinks().add(ml4);
        getCatalog().save(mpolys);
        Document doc = getAsDOM("wfs?service=WFS&version=1.1.0&request=getCapabilities");
        // print(doc);
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertEquals(2, xpath.getMatchingNodes("//wfs:FeatureType[wfs:Name='cgf:MPolygons']/wfs:MetadataURL", doc).getLength());
        Assert.assertEquals(2, xpath.getMatchingNodes("//wfs:FeatureType[wfs:Name='cgf:MPolygons']/wfs:MetadataURL[@format='text/html']", doc).getLength());
        Assert.assertEquals(1, xpath.getMatchingNodes("//wfs:FeatureType[wfs:Name='cgf:MPolygons']/wfs:MetadataURL[@type='19115']", doc).getLength());
        Assert.assertEquals(1, xpath.getMatchingNodes("//wfs:FeatureType[wfs:Name='cgf:MPolygons']/wfs:MetadataURL[@type='FGDC']", doc).getLength());
        Assert.assertEquals(2, xpath.getMatchingNodes("//wfs:FeatureType[wfs:Name='cgf:MPolygons']/wfs:MetadataURL[text() = 'http://www.geoserver.org']", doc).getLength());
    }

    @Test
    public void testMetadataLinksTransormToProxyBaseURL() throws Exception {
        FeatureTypeInfo mpolys = getCatalog().getFeatureTypeByName(getLayerId(MockTestData.MPOLYGONS));
        // a valid link whose metadata type needs tweaking
        MetadataLinkInfo ml1 = getCatalog().getFactory().createMetadataLink();
        ml1.setMetadataType("ISO19115:2003");
        ml1.setType("text/html");
        ml1.setContent("/metadata?key=value");
        mpolys.getMetadataLinks().add(ml1);
        // a valid one
        MetadataLinkInfo ml2 = getCatalog().getFactory().createMetadataLink();
        ml2.setMetadataType("FGDC");
        ml2.setType("text/html");
        ml2.setContent("/metadata?key=value");
        mpolys.getMetadataLinks().add(ml2);
        // an invalid one, not a valid type
        MetadataLinkInfo ml3 = getCatalog().getFactory().createMetadataLink();
        ml3.setMetadataType("other");
        ml3.setType("text/html");
        ml3.setContent("/metadata?key=value");
        mpolys.getMetadataLinks().add(ml3);
        // an invalid one, not a valid format
        MetadataLinkInfo ml4 = getCatalog().getFactory().createMetadataLink();
        ml4.setMetadataType("FGDC");
        ml4.setType("application/zip");
        ml4.setContent("/metadata?key=value");
        mpolys.getMetadataLinks().add(ml4);
        getCatalog().save(mpolys);
        String proxyBaseUrl = getGeoServer().getGlobal().getSettings().getProxyBaseUrl();
        Document doc = getAsDOM("wfs?service=WFS&version=1.1.0&request=getCapabilities");
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertEquals(2, xpath.getMatchingNodes("//wfs:FeatureType[wfs:Name='cgf:MPolygons']/wfs:MetadataURL", doc).getLength());
        Assert.assertEquals(2, xpath.getMatchingNodes("//wfs:FeatureType[wfs:Name='cgf:MPolygons']/wfs:MetadataURL[@format='text/html']", doc).getLength());
        Assert.assertEquals(1, xpath.getMatchingNodes("//wfs:FeatureType[wfs:Name='cgf:MPolygons']/wfs:MetadataURL[@type='19115']", doc).getLength());
        Assert.assertEquals(1, xpath.getMatchingNodes("//wfs:FeatureType[wfs:Name='cgf:MPolygons']/wfs:MetadataURL[@type='FGDC']", doc).getLength());
        Assert.assertEquals(2, xpath.getMatchingNodes((("//wfs:FeatureType[wfs:Name='cgf:MPolygons']/wfs:MetadataURL[text() = '" + proxyBaseUrl) + "/metadata?key=value']"), doc).getLength());
    }

    @Test
    public void testOtherSRS() throws Exception {
        WFSInfo wfs = getGeoServer().getService(WFSInfo.class);
        wfs.getSRS().add("EPSG:4326");// this one corresponds to the native one, should not be

        // generated
        wfs.getSRS().add("EPSG:3857");
        wfs.getSRS().add("EPSG:3003");
        try {
            getGeoServer().save(wfs);
            // perform get caps
            Document doc = getAsDOM("wfs?service=WFS&version=1.1.0&request=getCapabilities");
            // for each enabled type, check we added the otherSRS
            final List<FeatureTypeInfo> enabledTypes = getCatalog().getFeatureTypes();
            for (Iterator<FeatureTypeInfo> it = enabledTypes.iterator(); it.hasNext();) {
                FeatureTypeInfo ft = it.next();
                if (ft.enabled()) {
                    String prefixedName = ft.prefixedName();
                    String base = (("/wfs:WFS_Capabilities/wfs:FeatureTypeList/" + "wfs:FeatureType[wfs:Name =\"") + prefixedName) + "\"]";
                    XMLAssert.assertXpathExists(base, doc);
                    // we generate the other SRS only if it's not equal to native
                    boolean wgs84Native = "EPSG:4326".equals(ft.getSRS());
                    if (wgs84Native) {
                        assertXpathEvaluatesTo("2", (("count(" + base) + "/wfs:OtherSRS)"), doc);
                    } else {
                        assertXpathEvaluatesTo("3", (("count(" + base) + "/wfs:OtherSRS)"), doc);
                        XMLAssert.assertXpathExists((base + "[wfs:OtherSRS = 'urn:x-ogc:def:crs:EPSG:4326']"), doc);
                    }
                    XMLAssert.assertXpathExists((base + "[wfs:OtherSRS = 'urn:x-ogc:def:crs:EPSG:3003']"), doc);
                    XMLAssert.assertXpathExists((base + "[wfs:OtherSRS = 'urn:x-ogc:def:crs:EPSG:3857']"), doc);
                }
            }
        } finally {
            wfs.getSRS().clear();
            getGeoServer().save(wfs);
        }
    }

    @Test
    public void testOtherSRSSingleTypeOverride() throws Exception {
        WFSInfo wfs = getGeoServer().getService(WFSInfo.class);
        wfs.getSRS().add("EPSG:4326");// this one corresponds to the native one, should not be

        // generated
        wfs.getSRS().add("EPSG:3857");
        wfs.getSRS().add("EPSG:3003");
        String polygonsName = getLayerId(MockData.POLYGONS);
        FeatureTypeInfo polygons = getCatalog().getFeatureTypeByName(polygonsName);
        polygons.getResponseSRS().add("EPSG:32632");
        polygons.setOverridingServiceSRS(true);
        try {
            getGeoServer().save(wfs);
            getCatalog().save(polygons);
            // check for this layer we have a different list
            Document doc = getAsDOM("wfs?service=WFS&version=1.1.0&request=getCapabilities");
            String base = (("/wfs:WFS_Capabilities/wfs:FeatureTypeList/" + "wfs:FeatureType[wfs:Name =\"") + polygonsName) + "\"]";
            XMLAssert.assertXpathExists(base, doc);
            assertXpathEvaluatesTo("1", (("count(" + base) + "/wfs:OtherSRS)"), doc);
            XMLAssert.assertXpathExists((base + "[wfs:OtherSRS = 'urn:x-ogc:def:crs:EPSG:32632']"), doc);
        } finally {
            wfs.getSRS().clear();
            getGeoServer().save(wfs);
            polygons.setOverridingServiceSRS(false);
            polygons.getResponseSRS().clear();
            getCatalog().save(polygons);
        }
    }

    @Test
    public void testGetSections() throws Exception {
        // do not specify sections
        testSections("", 1, 1, 1, 1, 1);
        // ask explicitly for all
        testSections("All", 1, 1, 1, 1, 1);
        // test sections one by one
        testSections("ServiceIdentification", 1, 0, 0, 0, 0);
        testSections("ServiceProvider", 0, 1, 0, 0, 0);
        testSections("OperationsMetadata", 0, 0, 1, 0, 0);
        testSections("FeatureTypeList", 0, 0, 0, 1, 0);
        testSections("Filter_Capabilities", 0, 0, 0, 0, 1);
        // try a group
        testSections("ServiceIdentification,Filter_Capabilities", 1, 0, 0, 0, 1);
        // mix All in the middle
        testSections("ServiceIdentification,Filter_Capabilities,All", 1, 1, 1, 1, 1);
        // try an invalid section
        Document dom = getAsDOM("wfs?service=WFS&version=1.1.0&request=GetCapabilities&sections=FooBar");
        checkOws10Exception(dom, "InvalidParameterValue", "sections");
    }
}

