/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.ppio;


import java.io.ByteArrayOutputStream;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.test.GeoServerTestSupport;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * Test Cases for KML 2.2 Encoder.
 */
public class KMLPPIOTest extends GeoServerTestSupport {
    private XpathEngine xpath;

    private KMLPPIO ppio;

    @Test
    public void testEncodeLinestring() throws Exception {
        FeatureTypeInfo fti = getCatalog().getFeatureTypeByName(getLayerId(MockData.LINES));
        SimpleFeatureCollection fc = ((SimpleFeatureCollection) (fti.getFeatureSource(null, null).getFeatures()));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ppio.encode(fc, bos);
        Document dom = dom(new java.io.ByteArrayInputStream(bos.toByteArray()));
        // print(dom);
        checkValidationErorrs(dom, "./src/test/resources/org/geoserver/wps/ppio/ogckml22.xsd");
        // check the data was reprojected to wgs84
        assertEquals("-92.99887316950249,4.523788751137377 -92.99842243632469,4.524241087719057", xpath.evaluate("//kml:LineString/kml:coordinates", dom));
        assertEquals("t0001 ", xpath.evaluate("//kml:ExtendedData/kml:SchemaData/kml:SimpleData[@name='id']", dom));
    }

    @Test
    public void testEncodePolygon() throws Exception {
        FeatureTypeInfo fti = getCatalog().getFeatureTypeByName(getLayerId(MockData.BASIC_POLYGONS));
        SimpleFeatureCollection fc = ((SimpleFeatureCollection) (fti.getFeatureSource(null, null).getFeatures()));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ppio.encode(fc, bos);
        Document dom = dom(new java.io.ByteArrayInputStream(bos.toByteArray()));
        // print(dom);
        checkValidationErorrs(dom, "./src/test/resources/org/geoserver/wps/ppio/ogckml22.xsd");
        assertEquals("-1.0,5.0 2.0,5.0 2.0,2.0 -1.0,2.0 -1.0,5.0", xpath.evaluate("//kml:Placemark[@id='BasicPolygons.1107531493644']//kml:LinearRing/kml:coordinates", dom));
    }

    @Test
    public void testEncodePoints() throws Exception {
        FeatureTypeInfo fti = getCatalog().getFeatureTypeByName(getLayerId(MockData.POINTS));
        SimpleFeatureCollection fc = ((SimpleFeatureCollection) (fti.getFeatureSource(null, null).getFeatures()));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ppio.encode(fc, bos);
        Document dom = dom(new java.io.ByteArrayInputStream(bos.toByteArray()));
        // print(dom);
        checkValidationErorrs(dom, "./src/test/resources/org/geoserver/wps/ppio/ogckml22.xsd");
        assertEquals(1, xpath.getMatchingNodes("//kml:Placemark", dom).getLength());
        assertEquals("t0000", xpath.evaluate("//kml:ExtendedData/kml:SchemaData/kml:SimpleData[@name='id']", dom));
        assertEquals("-92.99954926766114,4.52401492058674", xpath.evaluate("//kml:Point/kml:coordinates", dom));
        assertEquals("t0000", xpath.evaluate("//kml:ExtendedData/kml:SchemaData/kml:SimpleData[@name='id']", dom));
    }
}

