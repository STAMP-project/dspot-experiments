/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs;


import DimensionPresentation.LIST;
import MockData.CDF_PREFIX;
import MockData.TASMANIA_DEM;
import ResourceErrorHandling.SKIP_MISCONFIGURED_LAYERS;
import ResourceInfo.TIME;
import WcsExceptionCode.InvalidParameterValue;
import java.util.Map;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.MetadataLinkInfo;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.ows.URLMangler;
import org.geoserver.ows.util.KvpUtils;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerExtensionsHelper;
import org.geoserver.wcs.test.CoverageTestSupport;
import org.geoserver.wcs.test.WCSTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class GetCapabilitiesTest extends WCSTestSupport {
    @Test
    public void testGetBasic() throws Exception {
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0"));
        // print(dom);
        checkValidationErrors(dom, WCSTestSupport.WCS10_GETCAPABILITIES_SCHEMA);
        // checke backlinks URLs can simply string-appended to
        XpathEngine xpath = XMLUnit.newXpathEngine();
        NodeList nodes = xpath.getMatchingNodes("//wcs:OnlineResource/@xlink:href", dom);
        Assert.assertTrue(((nodes.getLength()) > 0));
        for (int i = 0; i < (nodes.getLength()); i++) {
            Node node = nodes.item(i);
            String text = node.getTextContent();
            Assert.assertThat(text, CoreMatchers.endsWith("?"));
        }
    }

    @Test
    public void testExtraOperationKVP() throws Exception {
        // adds a custom kvp param to the urls
        URLMangler testMangler = ( baseURL, path, kvp, type) -> {
            if (type == URLMangler.URLType.SERVICE) {
                kvp.put("test", "abc");
            }
        };
        // force a lookup to prime the extension cache, then add a custom mangler to it
        GeoServerExtensions.extensions(URLMangler.class);
        GeoServerExtensionsHelper.singleton("wcs10CapsTestMangler", testMangler, URLMangler.class);
        try {
            Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0"));
            // print(dom);
            XpathEngine xpath = XMLUnit.newXpathEngine();
            NodeList nodes = xpath.getMatchingNodes("//wcs:OnlineResource/@xlink:href", dom);
            Assert.assertTrue(((nodes.getLength()) > 0));
            for (int i = 0; i < (nodes.getLength()); i++) {
                Node node = nodes.item(i);
                String text = node.getTextContent();
                Map<String, Object> kvp = KvpUtils.parseQueryString(text);
                Assert.assertEquals("abc", kvp.get("test"));
                Assert.assertThat(text, CoreMatchers.endsWith("&"));
            }
        } finally {
            GeoServerExtensionsHelper.clear();
        }
    }

    @Test
    public void testSkipMisconfigured() throws Exception {
        // enable skipping of misconfigured layers
        GeoServerInfo global = getGeoServer().getGlobal();
        global.setResourceErrorHandling(SKIP_MISCONFIGURED_LAYERS);
        getGeoServer().save(global);
        // manually misconfigure one layer
        CoverageStoreInfo cvInfo = getCatalog().getCoverageStoreByName(TASMANIA_DEM.getLocalPart());
        cvInfo.setURL("file:///I/AM/NOT/THERE");
        getCatalog().save(cvInfo);
        // check we got everything but that specific layer, and that the output is still schema
        // compliant
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0"));
        checkValidationErrors(dom, WCSTestSupport.WCS10_DESCRIBECOVERAGE_SCHEMA);
        int count = getCatalog().getCoverages().size();
        Assert.assertEquals((count - 1), dom.getElementsByTagName("wcs:CoverageOfferingBrief").getLength());
    }

    @Test
    public void testNoServiceContactInfo() throws Exception {
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS"));
        // print(dom);
        checkValidationErrors(dom, WCSTestSupport.WCS10_GETCAPABILITIES_SCHEMA);
    }

    @Test
    public void testPostBasic() throws Exception {
        String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (("<wcs:GetCapabilities service=\"WCS\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" " + "xmlns:wcs=\"http://www.opengis.net/wcs\" ") + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>");
        Document dom = postAsDOM(CoverageTestSupport.BASEPATH, request);
        // print(dom);
        checkValidationErrors(dom, WCSTestSupport.WCS10_GETCAPABILITIES_SCHEMA);
    }

    @Test
    public void testUpdateSequenceInferiorGet() throws Exception {
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&updateSequence=-1"));
        checkValidationErrors(dom, WCSTestSupport.WCS10_GETCAPABILITIES_SCHEMA);
        final Node root = dom.getFirstChild();
        Assert.assertEquals("wcs:WCS_Capabilities", root.getNodeName());
        Assert.assertTrue(((root.getChildNodes().getLength()) > 0));
    }

    @Test
    public void testUpdateSequenceInferiorPost() throws Exception {
        String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ((("<wcs:GetCapabilities service=\"WCS\" xmlns:ows=\"http://www.opengis.net/ows/1.1\"" + " xmlns:wcs=\"http://www.opengis.net/wcs\"") + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"") + " updateSequence=\"-1\"/>");
        Document dom = postAsDOM(CoverageTestSupport.BASEPATH, request);
        checkValidationErrors(dom, WCSTestSupport.WCS10_GETCAPABILITIES_SCHEMA);
        final Node root = dom.getFirstChild();
        Assert.assertEquals("wcs:WCS_Capabilities", root.getNodeName());
        Assert.assertTrue(((root.getChildNodes().getLength()) > 0));
    }

    @Test
    public void testUpdateSequenceEqualsGet() throws Exception {
        long i = getGeoServer().getGlobal().getUpdateSequence();
        Document dom = getAsDOM((((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0&updateSequence=") + i));
        // print(dom);
        final Node root = dom.getFirstChild();
        Assert.assertEquals("ServiceExceptionReport", root.getNodeName());
        Assert.assertEquals("CurrentUpdateSequence", root.getFirstChild().getNextSibling().getAttributes().getNamedItem("code").getNodeValue());
    }

    @Test
    public void testUpdateSequenceEqualsPost() throws Exception {
        long i = getGeoServer().getGlobal().getUpdateSequence();
        String request = (("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ((("<wcs:GetCapabilities service=\"WCS\" xmlns:ows=\"http://www.opengis.net/ows/1.1\"" + " xmlns:wcs=\"http://www.opengis.net/wcs\"") + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"") + " updateSequence=\"")) + i) + "\"/>";
        Document dom = postAsDOM(CoverageTestSupport.BASEPATH, request);
        // print(dom);
        final Node root = dom.getFirstChild();
        Assert.assertEquals("ServiceExceptionReport", root.getNodeName());
        Assert.assertEquals("CurrentUpdateSequence", root.getFirstChild().getNextSibling().getAttributes().getNamedItem("code").getNodeValue());
    }

    @Test
    public void testUpdateSequenceSuperiorGet() throws Exception {
        long i = (getGeoServer().getGlobal().getUpdateSequence()) + 1;
        Document dom = getAsDOM((((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0&updateSequence=") + i));
        // print(dom);
        checkOws11Exception(dom);
    }

    @Test
    public void testUpdateSequenceSuperiorPost() throws Exception {
        long i = (getGeoServer().getGlobal().getUpdateSequence()) + 1;
        String request = (("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ((("<wcs:GetCapabilities service=\"WCS\" xmlns:ows=\"http://www.opengis.net/ows/1.1\"" + " xmlns:wcs=\"http://www.opengis.net/wcs\"") + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"") + " updateSequence=\"")) + i) + "\" version=\"1.0.0\"/>";
        Document dom = postAsDOM(CoverageTestSupport.BASEPATH, request);
        checkOws11Exception(dom);
    }

    @Test
    public void testSectionsBogus() throws Exception {
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0&section=Bogus"));
        checkOws11Exception(dom);
        assertXpathEvaluatesTo(InvalidParameterValue.toString(), "/ServiceExceptionReport/ServiceException/@code", dom);
    }

    @Test
    public void testSectionsAll() throws Exception {
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0&section=/"));
        checkValidationErrors(dom, WCSTestSupport.WCS10_GETCAPABILITIES_SCHEMA);
        assertXpathEvaluatesTo("1", "count(//wcs:Service)", dom);
        assertXpathEvaluatesTo("1", "count(//wcs:Capability)", dom);
        assertXpathEvaluatesTo("1", "count(//wcs:ContentMetadata)", dom);
    }

    @Test
    public void testAcceptVersions() throws Exception {
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0&acceptversions=1.0.0"));
        checkValidationErrors(dom, WCSTestSupport.WCS10_GETCAPABILITIES_SCHEMA);
    }

    @Test
    public void testOneSection() throws Exception {
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0&section=/WCS_Capabilities/Service"));
        assertXpathEvaluatesTo("1", "count(//wcs:Service)", dom);
        assertXpathEvaluatesTo("0", "count(//wcs:Capability)", dom);
        assertXpathEvaluatesTo("0", "count(//wcs:ContentMetadata)", dom);
    }

    @Test
    public void testMetadataLinks() throws Exception {
        Catalog catalog = getCatalog();
        CoverageInfo ci = catalog.getCoverageByName(getLayerId(TASMANIA_DEM));
        MetadataLinkInfo ml = catalog.getFactory().createMetadataLink();
        ml.setContent("http://www.geoserver.org/tasmania/dem.xml");
        ml.setMetadataType("FGDC");
        ml.setAbout("http://www.geoserver.org");
        ci.getMetadataLinks().add(ml);
        catalog.save(ci);
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0"));
        print(dom);
        checkValidationErrors(dom, WCSTestSupport.WCS10_GETCAPABILITIES_SCHEMA);
        String xpathBase = ("//wcs:CoverageOfferingBrief[wcs:name = '" + (getLayerId(TASMANIA_DEM))) + "']/wcs:metadataLink";
        assertXpathEvaluatesTo("http://www.geoserver.org", (xpathBase + "/@about"), dom);
        assertXpathEvaluatesTo("FGDC", (xpathBase + "/@metadataType"), dom);
        assertXpathEvaluatesTo("simple", (xpathBase + "/@xlink:type"), dom);
        assertXpathEvaluatesTo("http://www.geoserver.org/tasmania/dem.xml", (xpathBase + "/@xlink:href"), dom);
    }

    @Test
    public void testMetadataLinksTransormToProxyBaseURL() throws Exception {
        GeoServerInfo global = getGeoServer().getGlobal();
        global.getSettings().setProxyBaseUrl("src/test/resources/geoserver");
        getGeoServer().save(global);
        try {
            Catalog catalog = getCatalog();
            CoverageInfo ci = catalog.getCoverageByName(getLayerId(TASMANIA_DEM));
            MetadataLinkInfo ml = catalog.getFactory().createMetadataLink();
            ml.setContent("/metadata?key=value");
            ml.setMetadataType("FGDC");
            ml.setAbout("http://www.geoserver.org");
            ci.getMetadataLinks().add(ml);
            catalog.save(ci);
            String proxyBaseUrl = getGeoServer().getGlobal().getSettings().getProxyBaseUrl();
            Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0"));
            print(dom);
            checkValidationErrors(dom, WCSTestSupport.WCS10_GETCAPABILITIES_SCHEMA);
            String xpathBase = ("//wcs:CoverageOfferingBrief[wcs:name = '" + (getLayerId(TASMANIA_DEM))) + "']/wcs:metadataLink";
            assertXpathEvaluatesTo("http://www.geoserver.org", (xpathBase + "/@about"), dom);
            assertXpathEvaluatesTo("FGDC", (xpathBase + "/@metadataType"), dom);
            assertXpathEvaluatesTo("simple", (xpathBase + "/@xlink:type"), dom);
            assertXpathEvaluatesTo((proxyBaseUrl + "/metadata?key=value"), (xpathBase + "/@xlink:href"), dom);
        } finally {
            global.getSettings().setProxyBaseUrl(null);
            getGeoServer().save(global);
        }
    }

    @Test
    public void testWorkspaceQualified() throws Exception {
        int expected = getCatalog().getCoverageStores().size();
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0"));
        Assert.assertEquals(expected, WCSTestSupport.xpath.getMatchingNodes("//wcs:CoverageOfferingBrief", dom).getLength());
        expected = getCatalog().getCoverageStoresByWorkspace(CDF_PREFIX).size();
        dom = getAsDOM("cdf/wcs?request=GetCapabilities&service=WCS&version=1.0.0");
        Assert.assertEquals(expected, WCSTestSupport.xpath.getMatchingNodes("//wcs:CoverageOfferingBrief", dom).getLength());
    }

    @Test
    public void testLayerQualified() throws Exception {
        int expected = getCatalog().getCoverageStores().size();
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0"));
        Assert.assertEquals(expected, WCSTestSupport.xpath.getMatchingNodes("//wcs:CoverageOfferingBrief", dom).getLength());
        dom = getAsDOM("wcs/World/wcs?request=GetCapabilities&service=WCS&version=1.0.0");
        Assert.assertEquals(1, WCSTestSupport.xpath.getMatchingNodes("//wcs:CoverageOfferingBrief", dom).getLength());
    }

    @Test
    public void testTimeCoverage() throws Exception {
        setupRasterDimension(CoverageTestSupport.WATTEMP, TIME, LIST, null);
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0"));
        // print(dom);
        checkValidationErrors(dom, WCSTestSupport.WCS10_GETCAPABILITIES_SCHEMA);
        // check the envelopes
        String base = "//wcs:CoverageOfferingBrief[wcs:name='wcs:watertemp']//wcs:lonLatEnvelope";
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z", (base + "/gml:timePosition[1]"), dom);
        assertXpathEvaluatesTo("2008-11-01T00:00:00.000Z", (base + "/gml:timePosition[2]"), dom);
    }

    @Test
    public void testTimeRangeCoverage() throws Exception {
        setupRasterDimension(CoverageTestSupport.TIMERANGES, TIME, LIST, null);
        Document dom = getAsDOM(((CoverageTestSupport.BASEPATH) + "?request=GetCapabilities&service=WCS&version=1.0.0"));
        // print(dom);
        checkValidationErrors(dom, WCSTestSupport.WCS10_GETCAPABILITIES_SCHEMA);
        // check the envelopes
        String base = "//wcs:CoverageOfferingBrief[wcs:name='sf:timeranges']//wcs:lonLatEnvelope";
        assertXpathEvaluatesTo("2008-10-31T00:00:00.000Z", (base + "/gml:timePosition[1]"), dom);
        assertXpathEvaluatesTo("2008-11-07T00:00:00.000Z", (base + "/gml:timePosition[2]"), dom);
    }
}

