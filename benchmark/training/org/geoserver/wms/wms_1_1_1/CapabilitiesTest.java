/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import LayerGroupInfo.Mode.SINGLE;
import MockData.BUILDINGS;
import MockData.LAKES;
import MockData.LINES;
import MockData.NAMED_PLACES;
import MockData.POINTS;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.AttributionInfo;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.DataLinkInfo;
import org.geoserver.catalog.Keyword;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MetadataLinkInfo;
import org.geoserver.catalog.PublishedInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.config.ContactInfo;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.wfs.json.JSONType;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WMSTestSupport;
import org.geoserver.wms.capabilities.GetCapabilitiesTransformer;
import org.geotools.referencing.CRS;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class CapabilitiesTest extends WMSTestSupport {
    private static final String BASE_URL = "http://localhost/geoserver";

    public CapabilitiesTest() {
        super();
    }

    @Test
    public void testCapabilities() throws Exception {
        Document dom = dom(get("wms?request=getCapabilities&version=1.1.1"), false);
        Element e = dom.getDocumentElement();
        Assert.assertEquals("WMT_MS_Capabilities", e.getLocalName());
    }

    @Test
    public void testCapabilitiesNoWGS84DD() throws Exception {
        Document dom = dom(get("wms?request=getCapabilities&version=1.1.1"), false);
        // print(dom);
        XMLAssert.assertXpathNotExists("//SRS[text() = 'EPSG:WGS84(DD)']", dom);
    }

    /**
     * Tests the behavior of the format vendor parameter.
     */
    @Test
    public void testCapabilitiesFormat() throws Exception {
        // the default and only wms standard conform mime type is application/vnd.ogc.wms_xml
        MockHttpServletResponse response = getAsServletResponse("wms?request=getCapabilities&version=1.1.1");
        Assert.assertThat(response.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(response.getContentType(), CoreMatchers.is("application/vnd.ogc.wms_xml"));
        // request with the supported ime type text/xml
        response = getAsServletResponse("wms?request=getCapabilities&version=1.1.1&format=text/xml");
        Assert.assertThat(response.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(response.getContentType(), CoreMatchers.is("text/xml"));
        // using an invalid mime type should throw an exception
        response = getAsServletResponse("wms?request=getCapabilities&version=1.1.1&format=invalid");
        Assert.assertThat(response.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(response.getContentType(), CoreMatchers.is("application/vnd.ogc.se_xml"));
        // using an empty mime type should fall back to the default mime type
        response = getAsServletResponse("wms?request=getCapabilities&version=1.1.1&format=");
        Assert.assertThat(response.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(response.getContentType(), CoreMatchers.is("application/vnd.ogc.wms_xml"));
    }

    /**
     * Test that all the supported mime types for the get capabilities operation are listed.
     */
    @Test
    public void testGetCapabilities() throws Exception {
        // request a wms 1.1.1 capabilities document
        MockHttpServletResponse response = getAsServletResponse("wms?request=getCapabilities&version=1.1.1");
        Assert.assertThat(response.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(response.getContentType(), CoreMatchers.is("application/vnd.ogc.wms_xml"));
        // parse the result as a xml document
        InputStream content = new ByteArrayInputStream(response.getContentAsByteArray());
        Document document = dom(content, true);
        // check that all the available mime types are present
        for (String mimeType : GetCapabilitiesTransformer.WMS_CAPS_AVAIL_MIME) {
            assertXpathExists(String.format("//GetCapabilities[Format='%s']", mimeType), document);
        }
    }

    @Test
    public void testGetCapsContainsNoDisabledTypes() throws Exception {
        Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        // print(doc);
        Assert.assertEquals("WMT_MS_Capabilities", doc.getDocumentElement().getNodeName());
        // see that disabled elements are disabled for good
        assertXpathEvaluatesTo("0", "count(//Name[text()='sf:PrimitiveGeoFeature'])", doc);
    }

    @Test
    public void testFilteredCapabilitiesCite() throws Exception {
        Document dom = dom(get("wms?request=getCapabilities&version=1.1.1&namespace=cite"), true);
        Element e = dom.getDocumentElement();
        Assert.assertEquals("WMT_MS_Capabilities", e.getLocalName());
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertTrue(((xpath.getMatchingNodes("//Layer/Name[starts-with(., cite)]", dom).getLength()) > 0));
        Assert.assertEquals(0, xpath.getMatchingNodes("//Layer/Name[not(starts-with(., cite))]", dom).getLength());
    }

    @Test
    public void testLayerCount() throws Exception {
        Document dom = dom(get("wms?request=getCapabilities&version=1.1.1"), true);
        XpathEngine xpath = XMLUnit.newXpathEngine();
        NodeList nodeLayers = xpath.getMatchingNodes("/WMT_MS_Capabilities/Capability/Layer/Layer", dom);
        Assert.assertEquals(getRawTopLayerCount(), nodeLayers.getLength());
    }

    @Test
    public void testNonAdvertisedLayer() throws Exception {
        String layerId = getLayerId(BUILDINGS);
        LayerInfo layer = getCatalog().getLayerByName(layerId);
        try {
            // now you see me
            Document dom = dom(get("wms?request=getCapabilities&version=1.1.1"), true);
            assertXpathExists((("//Layer[Name='" + layerId) + "']"), dom);
            // now you don't!
            layer.setAdvertised(false);
            getCatalog().save(layer);
            dom = dom(get("wms?request=getCapabilities&version=1.1.1"), true);
            assertXpathNotExists((("//Layer[Name='" + layerId) + "']"), dom);
        } finally {
            layer.setAdvertised(true);
            getCatalog().save(layer);
        }
    }

    @Test
    public void testNonAdvertisedLayerInLayerSpecificService() throws Exception {
        String layerId = getLayerId(BUILDINGS);
        LayerInfo layer = getCatalog().getLayerByName(layerId);
        String context = layerId.replace(":", "/");
        String localName = BUILDINGS.getLocalPart();
        try {
            // now you see me
            Document dom = dom(get((context + "/wms?request=getCapabilities&version=1.1.1")), true);
            assertXpathExists((("//Layer[Name='" + localName) + "']"), dom);
            // now you... still do :-)
            layer.setAdvertised(false);
            getCatalog().save(layer);
            dom = dom(get((context + "/wms?request=getCapabilities&version=1.1.1")), true);
            assertXpathExists((("//Layer[Name='" + localName) + "']"), dom);
        } finally {
            layer.setAdvertised(true);
            getCatalog().save(layer);
        }
    }

    @Test
    public void testWorkspaceQualified() throws Exception {
        Document dom = dom(get("cite/wms?request=getCapabilities&version=1.1.1"), true);
        Element e = dom.getDocumentElement();
        Assert.assertEquals("WMT_MS_Capabilities", e.getLocalName());
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertEquals(0, xpath.getMatchingNodes("//Layer/Name[starts-with(., 'cite')]", dom).getLength());
        Assert.assertTrue(((xpath.getMatchingNodes("//Layer/Name[not(starts-with(., 'cite'))]", dom).getLength()) > 0));
        NodeList nodes = xpath.getMatchingNodes("//Layer//OnlineResource", dom);
        Assert.assertTrue(((nodes.getLength()) > 0));
        for (int i = 0; i < (nodes.getLength()); i++) {
            e = ((Element) (nodes.item(i)));
            String attribute = e.getAttribute("xlink:href");
            Assert.assertTrue(attribute.contains("geoserver/cite/wms"));
        }
    }

    @Test
    public void testLayerQualified() throws Exception {
        Document dom = dom(get("cite/Forests/wms?request=getCapabilities&version=1.1.1"), true);
        Element e = dom.getDocumentElement();
        Assert.assertEquals("WMT_MS_Capabilities", e.getLocalName());
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertEquals(0, xpath.getMatchingNodes("//Layer/Name[starts-with(., 'cite:Forests')]", dom).getLength());
        Assert.assertEquals(1, xpath.getMatchingNodes("//Layer[Name = 'Forests']", dom).getLength());
        NodeList nodes = xpath.getMatchingNodes("//Layer//OnlineResource", dom);
        Assert.assertTrue(((nodes.getLength()) > 0));
        for (int i = 0; i < (nodes.getLength()); i++) {
            e = ((Element) (nodes.item(i)));
            Assert.assertTrue(e.getAttribute("xlink:href").contains("geoserver/cite/Forests/wms"));
        }
    }

    @Test
    public void testAttribution() throws Exception {
        // Uncomment the following lines if you want to use DTD validation for these tests
        // (by passing false as the second param to getAsDOM())
        // BUG: Currently, this doesn't seem to actually validate the document, although
        // 'validation' fails if the DTD is missing
        // GeoServerInfo global = getGeoServer().getGlobal();
        // global.setProxyBaseUrl("src/test/resources/geoserver");
        // getGeoServer().save(global);
        Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        assertXpathEvaluatesTo("0", "count(//Attribution)", doc);
        // Add attribution to one of the layers
        LayerInfo points = getCatalog().getLayerByName(POINTS.getLocalPart());
        AttributionInfo attr = points.getAttribution();
        attr.setTitle("Point Provider");
        getCatalog().save(points);
        doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        assertXpathEvaluatesTo("1", "count(//Attribution)", doc);
        assertXpathEvaluatesTo("1", "count(//Attribution/Title)", doc);
        // Add href to same layer
        attr = points.getAttribution();
        attr.setHref("http://example.com/points/provider");
        getCatalog().save(points);
        doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        // print(doc);
        assertXpathEvaluatesTo("1", "count(//Attribution)", doc);
        assertXpathEvaluatesTo("1", "count(//Attribution/Title)", doc);
        assertXpathEvaluatesTo("1", "count(//Attribution/OnlineResource)", doc);
        // Add logo to same layer
        attr = points.getAttribution();
        attr.setLogoURL("http://example.com/points/logo");
        attr.setLogoType("image/logo");
        attr.setLogoHeight(50);
        attr.setLogoWidth(50);
        getCatalog().save(points);
        doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        // print(doc);
        assertXpathEvaluatesTo("1", "count(//Attribution)", doc);
        assertXpathEvaluatesTo("1", "count(//Attribution/Title)", doc);
        assertXpathEvaluatesTo("1", "count(//Attribution/LogoURL)", doc);
    }

    @Test
    public void testLayerGroup() throws Exception {
        LayerInfo points = getCatalog().getLayerByName(POINTS.getLocalPart());
        CatalogBuilder builder = new CatalogBuilder(getCatalog());
        // create layergr
        LayerGroupInfo lg = getCatalog().getFactory().createLayerGroup();
        // attribution
        lg.setName("MyLayerGroup");
        lg.getLayers().add(points);
        lg.getStyles().add(null);
        builder.calculateLayerGroupBounds(lg, CRS.decode("EPSG:4326"));
        lg.setAttribution(getCatalog().getFactory().createAttribution());
        lg.getAttribution().setTitle("My Attribution");
        MetadataLinkInfo info = getCatalog().getFactory().createMetadataLink();
        info.setType("text/html");
        info.setMetadataType("FGDC");
        info.setContent("http://my/metadata/link");
        lg.getMetadataLinks().add(info);
        getCatalog().add(lg);
        // add keywords to layer group
        addKeywordsToLayerGroup("MyLayerGroup");
        try {
            Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
            // print(doc);
            assertXpathEvaluatesTo("1", "count(//Layer[Name='MyLayerGroup']/Attribution)", doc);
            assertXpathEvaluatesTo("My Attribution", "//Layer[Name='MyLayerGroup']/Attribution/Title", doc);
            assertXpathEvaluatesTo("1", "count(//Layer[Name='MyLayerGroup']/MetadataURL)", doc);
            assertXpathEvaluatesTo("http://my/metadata/link", "//Layer[Name='MyLayerGroup']/MetadataURL/OnlineResource/@xlink:href", doc);
            // check keywords are present
            assertXpathEvaluatesTo("2", "count(//Layer[Name='MyLayerGroup']/KeywordList/Keyword)", doc);
            assertXpathEvaluatesTo("1", "count(//Layer[Name='MyLayerGroup']/KeywordList[Keyword='keyword1'])", doc);
            assertXpathEvaluatesTo("1", "count(//Layer[Name='MyLayerGroup']/KeywordList[Keyword='keyword2'])", doc);
        } finally {
            // clean up
            getCatalog().remove(lg);
        }
    }

    @Test
    public void testAlternateStyles() throws Exception {
        // add an alternate style to Fifteen
        StyleInfo pointStyle = getCatalog().getStyleByName("point");
        LayerInfo layer = getCatalog().getLayerByName("Fifteen");
        layer.getStyles().add(pointStyle);
        getCatalog().save(layer);
        Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        // print(doc);
        assertXpathEvaluatesTo("1", "count(//Layer[Name='cdf:Fifteen'])", doc);
        assertXpathEvaluatesTo("2", "count(//Layer[Name='cdf:Fifteen']/Style)", doc);
        XpathEngine xpath = newXpathEngine();
        String href = xpath.evaluate("//Layer[Name='cdf:Fifteen']/Style[Name='Default']/LegendURL/OnlineResource/@xlink:href", doc);
        Assert.assertTrue(href.contains("GetLegendGraphic"));
        Assert.assertTrue(href.contains("layer=cdf%3AFifteen"));
        Assert.assertFalse(href.contains("style"));
        href = xpath.evaluate("//Layer[Name='cdf:Fifteen']/Style[Name='point']/LegendURL/OnlineResource/@xlink:href", doc);
        Assert.assertTrue(href.contains("GetLegendGraphic"));
        Assert.assertTrue(href.contains("layer=cdf%3AFifteen"));
        Assert.assertTrue(href.contains("style=point"));
    }

    @Test
    public void testServiceMetadata() throws Exception {
        final WMSInfo service = getGeoServer().getService(WMSInfo.class);
        service.setTitle("test title");
        service.setAbstract("test abstract");
        service.setAccessConstraints("test accessConstraints");
        service.setFees("test fees");
        service.getKeywords().clear();
        service.getKeywords().add(new Keyword("test keyword 1"));
        service.getKeywords().add(new Keyword("test keyword 2"));
        service.setMaintainer("test maintainer");
        service.setOnlineResource("http://example.com/geoserver");
        GeoServerInfo global = getGeoServer().getGlobal();
        ContactInfo contact = global.getContact();
        contact.setAddress("__address");
        contact.setAddressCity("__city");
        contact.setAddressCountry("__country");
        contact.setAddressPostalCode("__ZIP");
        contact.setAddressState("__state");
        contact.setAddressType("__type");
        contact.setContactEmail("e@mail");
        contact.setContactOrganization("__org");
        contact.setContactFacsimile("__fax");
        contact.setContactPerson("__me");
        contact.setContactPosition("__position");
        contact.setContactVoice("__phone");
        getGeoServer().save(global);
        getGeoServer().save(service);
        Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        // print(doc);
        String base = "WMT_MS_Capabilities/Service/";
        assertXpathEvaluatesTo("OGC:WMS", (base + "Name"), doc);
        assertXpathEvaluatesTo("test title", (base + "Title"), doc);
        assertXpathEvaluatesTo("test abstract", (base + "Abstract"), doc);
        assertXpathEvaluatesTo("test keyword 1", (base + "KeywordList/Keyword[1]"), doc);
        assertXpathEvaluatesTo("test keyword 2", (base + "KeywordList/Keyword[2]"), doc);
        assertXpathEvaluatesTo("http://example.com/geoserver", (base + "OnlineResource/@xlink:href"), doc);
        String cinfo = base + "ContactInformation/";
        assertXpathEvaluatesTo("__me", (cinfo + "ContactPersonPrimary/ContactPerson"), doc);
        assertXpathEvaluatesTo("__org", (cinfo + "ContactPersonPrimary/ContactOrganization"), doc);
        assertXpathEvaluatesTo("__position", (cinfo + "ContactPosition"), doc);
        assertXpathEvaluatesTo("__type", (cinfo + "ContactAddress/AddressType"), doc);
        assertXpathEvaluatesTo("__address", (cinfo + "ContactAddress/Address"), doc);
        assertXpathEvaluatesTo("__city", (cinfo + "ContactAddress/City"), doc);
        assertXpathEvaluatesTo("__state", (cinfo + "ContactAddress/StateOrProvince"), doc);
        assertXpathEvaluatesTo("__ZIP", (cinfo + "ContactAddress/PostCode"), doc);
        assertXpathEvaluatesTo("__country", (cinfo + "ContactAddress/Country"), doc);
        assertXpathEvaluatesTo("__phone", (cinfo + "ContactVoiceTelephone"), doc);
        assertXpathEvaluatesTo("__fax", (cinfo + "ContactFacsimileTelephone"), doc);
        assertXpathEvaluatesTo("e@mail", (cinfo + "ContactElectronicMailAddress"), doc);
    }

    @Test
    public void testNoFeesOrContraints() throws Exception {
        final WMSInfo service = getGeoServer().getService(WMSInfo.class);
        service.setAccessConstraints(null);
        service.setFees(null);
        getGeoServer().save(service);
        Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        // print(doc);
        String base = "WMT_MS_Capabilities/Service/";
        assertXpathEvaluatesTo("OGC:WMS", (base + "Name"), doc);
        assertXpathEvaluatesTo("none", (base + "Fees"), doc);
        assertXpathEvaluatesTo("none", (base + "AccessConstraints"), doc);
    }

    @Test
    public void testQueryable() throws Exception {
        LayerInfo lines = getCatalog().getLayerByName(LINES.getLocalPart());
        lines.setQueryable(true);
        getCatalog().save(lines);
        LayerInfo points = getCatalog().getLayerByName(POINTS.getLocalPart());
        points.setQueryable(false);
        getCatalog().save(points);
        String linesName = ((LINES.getPrefix()) + ":") + (LINES.getLocalPart());
        String pointsName = ((POINTS.getPrefix()) + ":") + (POINTS.getLocalPart());
        Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        // print(doc);
        assertXpathEvaluatesTo("1", (("//Layer[Name='" + linesName) + "']/@queryable"), doc);
        assertXpathEvaluatesTo("0", (("//Layer[Name='" + pointsName) + "']/@queryable"), doc);
    }

    @Test
    public void testOpaque() throws Exception {
        LayerInfo lines = getCatalog().getLayerByName(LINES.getLocalPart());
        lines.setOpaque(true);
        getCatalog().save(lines);
        LayerInfo points = getCatalog().getLayerByName(POINTS.getLocalPart());
        points.setOpaque(false);
        getCatalog().save(points);
        String linesName = ((LINES.getPrefix()) + ":") + (LINES.getLocalPart());
        String pointsName = ((POINTS.getPrefix()) + ":") + (POINTS.getLocalPart());
        Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        assertXpathEvaluatesTo("1", (("//Layer[Name='" + linesName) + "']/@opaque"), doc);
        assertXpathEvaluatesTo("0", (("//Layer[Name='" + pointsName) + "']/@opaque"), doc);
    }

    @Test
    public void testExceptions() throws Exception {
        Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        XpathEngine xpath = XMLUnit.newXpathEngine();
        Assert.assertTrue(xpath.evaluate("//Exception/Format[1]", doc).equals("application/vnd.ogc.se_xml"));
        Assert.assertTrue(xpath.evaluate("//Exception/Format[2]", doc).equals("application/vnd.ogc.se_inimage"));
        Assert.assertTrue(xpath.evaluate("//Exception/Format[3]", doc).equals("application/vnd.ogc.se_blank"));
        Assert.assertTrue(xpath.evaluate("//Exception/Format[4]", doc).equals("application/json"));
        Assert.assertTrue(((xpath.getMatchingNodes("//Exception/Format", doc).getLength()) >= 4));
        boolean jsonpOriginal = JSONType.isJsonpEnabled();
        try {
            JSONType.setJsonpEnabled(true);
            doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
            Assert.assertTrue(xpath.evaluate("//Exception/Format[5]", doc).equals("text/javascript"));
            Assert.assertTrue(((xpath.getMatchingNodes("//Exception/Format", doc).getLength()) == 5));
            JSONType.setJsonpEnabled(false);
            doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
            Assert.assertTrue(((xpath.getMatchingNodes("//Exception/Format", doc).getLength()) == 4));
        } finally {
            JSONType.setJsonpEnabled(jsonpOriginal);
        }
    }

    @Test
    public void testDataLinks() throws Exception {
        String layerName = ((POINTS.getPrefix()) + ":") + (POINTS.getLocalPart());
        LayerInfo layer = getCatalog().getLayerByName(POINTS.getLocalPart());
        DataLinkInfo mdlink = getCatalog().getFactory().createDataLink();
        mdlink.setContent("http://geoserver.org");
        mdlink.setType("text/xml");
        ResourceInfo resource = layer.getResource();
        resource.getDataLinks().add(mdlink);
        getCatalog().save(resource);
        Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        String xpath = ("//Layer[Name='" + layerName) + "']/DataURL/Format";
        assertXpathEvaluatesTo("text/xml", xpath, doc);
        xpath = ("//Layer[Name='" + layerName) + "']/DataURL/OnlineResource/@xlink:type";
        assertXpathEvaluatesTo("simple", xpath, doc);
        xpath = ("//Layer[Name='" + layerName) + "']/DataURL/OnlineResource/@xlink:href";
        assertXpathEvaluatesTo("http://geoserver.org", xpath, doc);
        // Test transforming localhost to proxyBaseUrl
        GeoServerInfo global = getGeoServer().getGlobal();
        String proxyBaseUrl = global.getSettings().getProxyBaseUrl();
        mdlink.setContent("/metadata");
        getCatalog().save(resource);
        doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        assertXpathEvaluatesTo((proxyBaseUrl + "/metadata"), xpath, doc);
        // Test KVP in URL
        String query = "key=value";
        mdlink.setContent(("/metadata?" + query));
        getCatalog().save(resource);
        doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        assertXpathEvaluatesTo(((proxyBaseUrl + "/metadata?") + query), xpath, doc);
        mdlink.setContent(("http://localhost/metadata?" + query));
        getCatalog().save(resource);
        doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        assertXpathEvaluatesTo(("http://localhost/metadata?" + query), xpath, doc);
    }

    @Test
    public void testStyleWorkspaceQualified() throws Exception {
        Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        // check the style name got prefixed too
        assertXpathEvaluatesTo("cite:Lakes", "//Layer[Name='cite:Lakes']/Style[1]/Name", doc);
        assertXpathEvaluatesTo("cite:tiger_roads", "//Layer[Name='cite:Lakes']/Style[2]/Name", doc);
    }

    // GEOS-7217: Make sure Styles are valid to DTD
    @Test
    public void testStyleElementsValidity() throws Exception {
        Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        assertXpathExists("//Layer[Name='cite:Lakes']/Style[1]/Name", doc);
        assertXpathExists("//Layer[Name='cite:Lakes']/Style[1]/Title", doc);
        assertXpathExists("//Layer[Name='cite:Lakes']/Style[1]/LegendURL", doc);
        assertXpathExists("//Layer[Name='cite:Lakes']/Style[2]/Name", doc);
        assertXpathExists("//Layer[Name='cite:Lakes']/Style[2]/Title", doc);
        assertXpathExists("//Layer[Name='cite:Lakes']/Style[2]/LegendURL", doc);
    }

    @Test
    public void testDuplicateLayerGroup() throws Exception {
        // see https://osgeo-org.atlassian.net/browse/GEOS-6154
        Catalog catalog = getCatalog();
        LayerInfo lakes = catalog.getLayerByName(getLayerId(LAKES));
        lakes.setAdvertised(false);
        catalog.save(lakes);
        try {
            Document doc = getAsDOM("wms?service=WMS&request=getCapabilities&version=1.1.1", true);
            // print(doc);
            // nested
            assertXpathEvaluatesTo("1", "count(//Layer[Title='containerGroup']/Layer[Name='nature'])", doc);
            // no other instances
            assertXpathEvaluatesTo("1", "count(//Layer[Name='nature'])", doc);
        } finally {
            lakes.setAdvertised(true);
            catalog.save(lakes);
        }
    }

    @Test
    public void testOpaqueGroup() throws Exception {
        Document dom = dom(get("wms?request=GetCapabilities&version=1.1.0"), true);
        // the layer group is there, but not the contained layers
        assertXpathEvaluatesTo("1", "count(//Layer[Name='opaqueGroup'])", dom);
        for (LayerInfo l : getCatalog().getLayerGroupByName(WMSTestSupport.OPAQUE_GROUP).layers()) {
            assertXpathNotExists((("//Layer[Name='" + (l.prefixedName())) + "']"), dom);
        }
    }

    @Test
    public void testNestedGroupInOpaqueGroup() throws Exception {
        Catalog catalog = getCatalog();
        // nest container inside opaque, this should make it disappear from the caps
        LayerGroupInfo container = catalog.getLayerGroupByName(WMSTestSupport.CONTAINER_GROUP);
        LayerGroupInfo opaque = catalog.getLayerGroupByName(WMSTestSupport.OPAQUE_GROUP);
        opaque.getLayers().add(container);
        opaque.getStyles().add(null);
        catalog.save(opaque);
        try {
            Document dom = getAsDOM("wms?request=GetCapabilities&version=1.1.0");
            // print(dom);
            // the layer group is there, but not the contained layers, which are not visible anymore
            assertXpathEvaluatesTo("1", "count(//Layer[Name='opaqueGroup'])", dom);
            for (PublishedInfo p : getCatalog().getLayerGroupByName(WMSTestSupport.OPAQUE_GROUP).getLayers()) {
                assertXpathNotExists((("//Layer[Name='" + (p.prefixedName())) + "']"), dom);
            }
            // now check the layer count too, we just hide everything in the container layer
            List<LayerInfo> nestedLayers = allLayers();
            int expectedLayerCount = ((getRawTopLayerCount()) - // layers gone due to the nesting
            (nestedLayers.size())) - /* container has been nested and disappeared */
            1;
            XpathEngine xpath = XMLUnit.newXpathEngine();
            NodeList nodeLayers = xpath.getMatchingNodes("/WMT_MS_Capabilities/Capability/Layer/Layer", dom);
            /* the layers under the opaque group */
            Assert.assertEquals(expectedLayerCount, nodeLayers.getLength());
        } finally {
            // restore the configuration
            opaque.getLayers().remove(container);
            opaque.getStyles().remove(((opaque.getStyles().size()) - 1));
            catalog.save(opaque);
        }
    }

    @Test
    public void testRootLayer() throws Exception {
        Document dom = findCapabilities(false);
        WMS wms = getWMS();
        WMSInfo info = wms.getServiceInfo();
        DOMSource domSource = new DOMSource(dom);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        Assert.assertEquals(writer.toString().contains(info.getRootLayerTitle()), true);
    }

    @Test
    public void testNonAdvertisedQueriableWithinGroup() throws Exception {
        // make layers non advertised
        Catalog catalog = getCatalog();
        setAdvertised(catalog, LAKES, false);
        setAdvertised(catalog, NAMED_PLACES, false);
        LayerGroupInfo group = null;
        try {
            group = createLakesPlacesLayerGroup(catalog, SINGLE, null);
            Document dom = dom(get("wms?request=GetCapabilities&version=1.1.0"), true);
            // print(dom);
            assertXpathEvaluatesTo("1", "//Layer[Name='lakes_and_places']/@queryable", dom);
        } finally {
            if (group != null) {
                catalog.remove(group);
            }
            setAdvertised(catalog, LAKES, true);
            setAdvertised(catalog, NAMED_PLACES, true);
        }
    }

    @Test
    public void testNonAdvertisedNonQueriableWithinGroup() throws Exception {
        // make layers non advertised
        Catalog catalog = getCatalog();
        setAdvertised(catalog, LAKES, false);
        setQueryable(catalog, LAKES, false);
        setAdvertised(catalog, NAMED_PLACES, false);
        setQueryable(catalog, NAMED_PLACES, false);
        LayerGroupInfo group = null;
        try {
            group = createLakesPlacesLayerGroup(catalog, SINGLE, null);
            Document dom = dom(get("wms?request=GetCapabilities&version=1.1.0"), true);
            // print(dom);
            assertXpathEvaluatesTo("0", "//Layer[Name='lakes_and_places']/@queryable", dom);
        } finally {
            if (group != null) {
                catalog.remove(group);
            }
            setAdvertised(catalog, LAKES, true);
            setQueryable(catalog, LAKES, true);
            setAdvertised(catalog, NAMED_PLACES, true);
            setQueryable(catalog, NAMED_PLACES, true);
        }
    }
}

