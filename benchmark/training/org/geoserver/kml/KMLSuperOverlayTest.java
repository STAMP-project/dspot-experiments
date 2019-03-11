/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.kml;


import NetworkLinkMapOutputFormat.KML_MIME_TYPE;
import java.util.Map;
import javax.xml.namespace.QName;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.data.test.MockData;
import org.geoserver.ows.util.KvpUtils;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class KMLSuperOverlayTest extends WMSTestSupport {
    public static QName DISPERSED_FEATURES = new QName(MockData.SF_URI, "Dispersed", MockData.SF_PREFIX);

    public static QName BOULDER = new QName(MockData.SF_URI, "boulder", MockData.SF_PREFIX);

    private XpathEngine xpath;

    /**
     * Verify that the tiles are produced for a request that encompasses the world.
     */
    @Test
    public void testWorldBoundsSuperOverlay() throws Exception {
        Document document = getAsDOM((((("wms/kml?layers=" + (getLayerId(MockData.BASIC_POLYGONS))) + ",") + (getLayerId(KMLSuperOverlayTest.DISPERSED_FEATURES))) + "&mode=superoverlay"));
        // print(document);
        Assert.assertEquals("kml", document.getDocumentElement().getNodeName());
        // two folders, one per layer
        Assert.assertEquals("2", xpath.evaluate("count(//kml:Folder)", document));
        // regions
        Assert.assertEquals(9, document.getElementsByTagName("Region").getLength());
        // links
        Assert.assertEquals("8", xpath.evaluate("count(//kml:NetworkLink)", document));
        // no ground overlays, direct links to contents instead
        Assert.assertEquals("0", xpath.evaluate("count(//kml:GroundOverlay)", document));
        // overall bbox
        assertXpathEvaluatesTo("90.0", "//kml:Region/kml:LatLonAltBox/kml:north", document);
        assertXpathEvaluatesTo("-90.0", "//kml:Region/kml:LatLonAltBox/kml:south", document);
        assertXpathEvaluatesTo("180.0", "//kml:Region/kml:LatLonAltBox/kml:east", document);
        assertXpathEvaluatesTo("-180.0", "//kml:Region/kml:LatLonAltBox/kml:west", document);
        // check we have contents starting from the top
        assertXpathExists("//kml:NetworkLink[kml:name='contents-0']", document);
        assertXpathExists("//kml:NetworkLink[kml:name='contents-1']", document);
    }

    /**
     * Checks what happens when the data bbox is at the crossing of a parent tile that is two levels
     * above the bbox itself
     */
    @Test
    public void testCrossingSuperoverlay() throws Exception {
        Document document = getAsDOM((("wms/kml?layers=" + (getLayerId(KMLSuperOverlayTest.BOULDER))) + "&mode=superoverlay"));
        // print(document);
        // check the overall bbox (the top-most tile that contains all data)
        assertXpathEvaluatesTo("40.78125", "//kml:Region/kml:LatLonAltBox/kml:north", document);
        assertXpathEvaluatesTo("39.375", "//kml:Region/kml:LatLonAltBox/kml:south", document);
        assertXpathEvaluatesTo("-104.0625", "//kml:Region/kml:LatLonAltBox/kml:east", document);
        assertXpathEvaluatesTo("-105.46875", "//kml:Region/kml:LatLonAltBox/kml:west", document);
        // however the lookats are pointing to the center of the data set
        assertXpathEvaluatesTo("-105.22419118401743", "//kml:Document/kml:LookAt/kml:longitude", document);
        assertXpathEvaluatesTo("40.008056082289826", "//kml:Document/kml:LookAt/kml:latitude", document);
        Assert.assertEquals((-105.2243), Double.parseDouble(xpath.evaluate("//kml:Document/kml:Folder/kml:LookAt/kml:longitude", document)), 1.0E-4);
        Assert.assertEquals(40.0081, Double.parseDouble(xpath.evaluate("//kml:Document/kml:Folder/kml:LookAt/kml:latitude", document)), 1.0E-4);
    }

    /**
     * Check the link contents a bit
     */
    @Test
    public void testSuperOverlayLinkContents() throws Exception {
        Document document = getAsDOM((("wms/kml?layers=" + (getLayerId(MockData.BASIC_POLYGONS))) + "&mode=superoverlay"));
        // print(document);
        Assert.assertEquals("kml", document.getDocumentElement().getNodeName());
        // two folders, one per layer
        Assert.assertEquals("1", xpath.evaluate("count(//kml:Folder)", document));
        // regions
        Assert.assertEquals(5, document.getElementsByTagName("Region").getLength());
        // links
        Assert.assertEquals("4", xpath.evaluate("count(//kml:NetworkLink)", document));
        // no ground overlays, direct links to contents instead
        Assert.assertEquals("0", xpath.evaluate("count(//kml:GroundOverlay)", document));
        // check sub-link 0, it should still got to the network link builder
        String link0 = xpath.evaluate("//kml:NetworkLink[kml:name='0']/kml:Link/kml:href", document);
        Map<String, Object> kvp0 = KvpUtils.parseQueryString(link0);
        Assert.assertEquals(KML_MIME_TYPE, kvp0.get("format"));
        Assert.assertEquals("-180.0,-90.0,0.0,90.0", kvp0.get("bbox"));
        // check sub-link 1, the other side of the world
        String link1 = xpath.evaluate("//kml:NetworkLink[kml:name='1']/kml:Link/kml:href", document);
        Map<String, Object> kvp1 = KvpUtils.parseQueryString(link1);
        Assert.assertEquals(KML_MIME_TYPE, kvp1.get("format"));
        Assert.assertEquals("0.0,-90.0,180.0,90.0", kvp1.get("bbox"));
    }

    /**
     * Verify that when a tile smaller than one hemisphere is requested, then subtiles are included
     * in the result (but only the ones necessary for the data at hand)
     */
    @Test
    public void testSubtileSuperOverlay() throws Exception {
        Document document = getAsDOM((((("wms/kml?layers=" + (getLayerId(MockData.BASIC_POLYGONS))) + ",") + (getLayerId(KMLSuperOverlayTest.DISPERSED_FEATURES))) + "&mode=superoverlay&bbox=0,-90,180,90"));
        // print(document);
        Assert.assertEquals("kml", document.getDocumentElement().getNodeName());
        // only three regions, the root one and one per network link
        Assert.assertEquals(3, document.getElementsByTagName("Region").getLength());
        // only network links to the data, we don't have enough feature to have sublinks generate
        Assert.assertEquals(2, document.getElementsByTagName("NetworkLink").getLength());
        // no ground overlays
        Assert.assertEquals(0, document.getElementsByTagName("GroundOverlay").getLength());
    }

    @Test
    public void testGWCIntegration() throws Exception {
        Document document = getAsDOM((("wms/kml?layers=" + (getLayerId(MockData.USA_WORLDIMG))) + "&mode=superoverlay&superoverlay_mode=cached"));
        // print(document);
        Assert.assertEquals("kml", document.getDocumentElement().getNodeName());
        // only three regions, the root one and one per network link
        Assert.assertEquals(1, document.getElementsByTagName("Region").getLength());
        // only network links to the data, we don't have enough feature to have sublinks generate
        Assert.assertEquals(1, document.getElementsByTagName("NetworkLink").getLength());
        // no ground overlays
        Assert.assertEquals(0, document.getElementsByTagName("GroundOverlay").getLength());
        // check we have a direct link to GWC
        Assert.assertEquals("http://localhost:8080/geoserver/gwc/service/kml/cdf:usa.png.kml", xpath.evaluate("//kml:NetworkLink/kml:Link/kml:href", document));
        Assert.assertEquals("never", xpath.evaluate("//kml:NetworkLink/kml:Link/kml:viewRefreshMode", document));
    }

    @Test
    public void testGWCIntegrationFailing() throws Exception {
        // force placemarks, this prevents usage of gwc
        Document document = getAsDOM((("wms/kml?layers=" + (getLayerId(MockData.USA_WORLDIMG))) + "&mode=superoverlay&superoverlay_mode=cached&kmplacemark=true"));
        // print(document);
        Assert.assertEquals("kml", document.getDocumentElement().getNodeName());
        Assert.assertEquals(6, document.getElementsByTagName("Region").getLength());
        Assert.assertEquals(4, document.getElementsByTagName("NetworkLink").getLength());
        // no ground overlays
        Assert.assertEquals(1, document.getElementsByTagName("GroundOverlay").getLength());
        // check we do not have a direct link to GWC, but back to the wms
        Assert.assertTrue("http://localhost:8080/geoserver/gwc/service/kml/cdf:usa.png.kml", xpath.evaluate("//kml:NetworkLink/kml:Link/kml:href", document).contains("geoserver/wms"));
    }

    @Test
    public void testKmlTitleFormatOption() throws Exception {
        Document document = getAsDOM((((("wms/kml?layers=" + (getLayerId(MockData.BASIC_POLYGONS))) + ",") + (getLayerId(KMLSuperOverlayTest.DISPERSED_FEATURES))) + "&mode=superoverlay&bbox=0,-90,180,90&format_options=kmltitle:myCustomLayerTitle"));
        // print(document);
        Assert.assertEquals("kml", document.getDocumentElement().getNodeName());
        Assert.assertEquals("myCustomLayerTitle", xpath.evaluate("//kml:Document/kml:name", document));
    }
}

