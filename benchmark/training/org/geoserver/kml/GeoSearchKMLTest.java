/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.kml;


import MockData.BASIC_POLYGONS;
import MockData.DIVIDED_ROUTES;
import java.util.logging.Level;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static KMLMapOutputFormat.MIME_TYPE;


public class GeoSearchKMLTest extends RegionatingTestSupport {
    @Test
    public void testSelfLinks() throws Exception {
        final String path = ((((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())) + "&styles=") + (BASIC_POLYGONS.getLocalPart())) + "&height=1024&width=1024&bbox=-180,-90,0,90&srs=EPSG:4326") + "&featureid=BasicPolygons.1107531493643&format_options=selfLinks:true";
        Document document = getAsDOM(path);
        // print(document);
        assertXpathEvaluatesTo("1", "count(//kml:Folder/kml:Placemark)", document);
        assertXpathEvaluatesTo("http://localhost:8080/geoserver/rest/cite/BasicPolygons/1107531493643.kml", "//kml:Placemark/atom:link/@href", document);
        assertXpathEvaluatesTo("self", "//kml:Placemark/atom:link/@rel", document);
    }

    /**
     * Test that requests regionated by data actually return stuff.
     */
    @Test
    public void testDataRegionator() throws Exception {
        final String path = ((((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (DIVIDED_ROUTES.getPrefix())) + ":") + (DIVIDED_ROUTES.getLocalPart())) + "&styles=") + (DIVIDED_ROUTES.getLocalPart())) + "&height=1024&width=1024&srs=EPSG:4326") + "&format_options=regionateBy:external-sorting;regionateAttr:NUM_LANES";
        Document document = getAsDOM((path + "&bbox=-180,-90,0,90"));
        Assert.assertEquals("kml", document.getDocumentElement().getTagName());
        int westCount = document.getDocumentElement().getElementsByTagName("Placemark").getLength();
        assertStatusCodeForGet(204, (path + "&bbox=0,-90,180,90"));
        Assert.assertEquals(1, westCount);
    }

    /**
     * Test that requests regionated by geometry actually return stuff.
     */
    @Test
    public void testGeometryRegionator() throws Exception {
        final String path = ((((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (DIVIDED_ROUTES.getPrefix())) + ":") + (DIVIDED_ROUTES.getLocalPart())) + "&styles=") + (DIVIDED_ROUTES.getLocalPart())) + "&height=1024&width=1024&srs=EPSG:4326") + "&format_options=regionateBy:geometry;regionateAttr:the_geom";
        Document document = getAsDOM((path + "&bbox=-180,-90,0,90"));
        Assert.assertEquals("kml", document.getDocumentElement().getTagName());
        Assert.assertEquals(1, document.getDocumentElement().getElementsByTagName("Placemark").getLength());
        assertStatusCodeForGet(204, (path + "&bbox=0,-90,180,90"));
    }

    /**
     * Test that requests regionated by random criteria actually return stuff.
     */
    @Test
    public void testRandomRegionator() throws Exception {
        final String path = ((((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (DIVIDED_ROUTES.getPrefix())) + ":") + (DIVIDED_ROUTES.getLocalPart())) + "&styles=") + (DIVIDED_ROUTES.getLocalPart())) + "&height=1024&width=1024&srs=EPSG:4326") + "&format_options=regionateBy:random";
        Document document = getAsDOM((path + "&bbox=-180,-90,0,90"));
        Assert.assertEquals("kml", document.getDocumentElement().getTagName());
        Assert.assertEquals(1, document.getDocumentElement().getElementsByTagName("Placemark").getLength());
        assertStatusCodeForGet(204, (path + "&bbox=0,-90,180,90"));
    }

    /**
     * Test that when a bogus regionating strategy is requested things still work. TODO: Evaluate
     * whether an error message should be returned instead.
     */
    @Test
    public void testBogusRegionator() throws Exception {
        Logging.getLogger("org.geoserver.ows").setLevel(Level.OFF);
        final String path = ((((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (DIVIDED_ROUTES.getPrefix())) + ":") + (DIVIDED_ROUTES.getLocalPart())) + "&styles=") + (DIVIDED_ROUTES.getLocalPart())) + "&height=1024&width=1024&srs=EPSG:4326") + "&format_options=regionateBy:bogus";
        Document document = getAsDOM((path + "&bbox=0,-90,180,90"), true);
        Assert.assertEquals("ServiceExceptionReport", document.getDocumentElement().getTagName());
    }

    /**
     * Test whether geometries that cross tiles get put into both of them.
     */
    @Test
    public void testBigGeometries() throws Exception {
        final String path = (((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (RegionatingTestSupport.CENTERED_POLY.getPrefix())) + ":") + (RegionatingTestSupport.CENTERED_POLY.getLocalPart())) + "&styles=") + "&height=1024&width=1024&srs=EPSG:4326") + "&format_options=regionateBy:external-sorting;regionateattr:foo";
        assertStatusCodeForGet(204, (path + "&bbox=-180,-90,0,90"));
        Document document = getAsDOM((path + "&bbox=0,-90,180,90"));
        Assert.assertEquals("kml", document.getDocumentElement().getTagName());
        Assert.assertEquals(1, document.getDocumentElement().getElementsByTagName("Placemark").getLength());
    }

    /**
     * Test whether specifying different regionating strategies changes the results.
     */
    @Test
    public void testStrategyChangesStuff() throws Exception {
        final String path = ((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (RegionatingTestSupport.TILE_TESTS.getPrefix())) + ":") + (RegionatingTestSupport.TILE_TESTS.getLocalPart())) + "&bbox=-180,-90,0,90&styles=") + "&height=1024&width=1024&srs=EPSG:4326";
        FeatureTypeInfo fti = getFeatureTypeInfo(RegionatingTestSupport.TILE_TESTS);
        fti.getMetadata().put("kml.regionateFeatureLimit", 2);
        getCatalog().save(fti);
        Document geo = getAsDOM((path + "&format_options=regionateBy:geometry;regionateattr:location"));
        Assert.assertEquals("kml", geo.getDocumentElement().getTagName());
        NodeList geoPlacemarks = geo.getDocumentElement().getElementsByTagName("Placemark");
        Assert.assertEquals(2, geoPlacemarks.getLength());
        Document data = getAsDOM((path + "&format_options=regionateBy:external-sorting;regionateAttr:z"));
        Assert.assertEquals("kml", data.getDocumentElement().getTagName());
        NodeList dataPlacemarks = data.getDocumentElement().getElementsByTagName("Placemark");
        Assert.assertEquals(2, dataPlacemarks.getLength());
        for (int i = 0; i < (geoPlacemarks.getLength()); i++) {
            String geoName = ((Element) (geoPlacemarks.item(i))).getAttribute("id");
            String dataName = ((Element) (dataPlacemarks.item(i))).getAttribute("id");
            Assert.assertTrue((((geoName + " and ") + dataName) + " should not be the same!"), (!(geoName.equals(dataName))));
        }
    }

    /**
     * Test whether specifying different regionating strategies changes the results.
     */
    @Test
    public void testDuplicateAttribute() throws Exception {
        final String path = ((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (RegionatingTestSupport.TILE_TESTS.getPrefix())) + ":") + (RegionatingTestSupport.TILE_TESTS.getLocalPart())) + "&bbox=-180,-90,0,90&styles=") + "&height=1024&width=1024&srs=EPSG:4326";
        FeatureTypeInfo fti = getFeatureTypeInfo(RegionatingTestSupport.TILE_TESTS);
        fti.getMetadata().put("kml.regionateFeatureLimit", 2);
        Document geo = getAsDOM((path + "&format_options=regionateBy:best_guess;regionateattr:the_geom"));
        Assert.assertEquals("kml", geo.getDocumentElement().getTagName());
    }
}

