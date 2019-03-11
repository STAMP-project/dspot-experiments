/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import SystemTestData.POLYGONS;
import javax.xml.namespace.QName;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.data.test.SystemTestData;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.w3c.dom.Document;


public class ReprojectionTest extends WFSTestSupport {
    private static final String TARGET_CRS_CODE = "EPSG:900913";

    public static QName NULL_GEOMETRIES = new QName(SystemTestData.CITE_URI, "NullGeometries", SystemTestData.CITE_PREFIX);

    public static QName GOOGLE = new QName(SystemTestData.CITE_URI, "GoogleFeatures", SystemTestData.CITE_PREFIX);

    static MathTransform tx;

    @Test
    public void testGetFeatureGet() throws Exception {
        Document dom1 = getAsDOM(("wfs?request=getfeature&service=wfs&version=1.0.0&typename=" + (POLYGONS.getLocalPart())));
        Document dom2 = getAsDOM(((("wfs?request=getfeature&service=wfs&version=1.0.0&typename=" + (POLYGONS.getLocalPart())) + "&srsName=") + (ReprojectionTest.TARGET_CRS_CODE)));
        // print(dom1);
        // print(dom2);
        runTest(dom1, dom2, ReprojectionTest.tx);
    }

    @Test
    public void testGetFeatureGetAutoCRS() throws Exception {
        Document dom1 = getAsDOM(("wfs?request=getfeature&service=wfs&version=1.0.0&typename=" + (POLYGONS.getLocalPart())));
        Document dom2 = getAsDOM((("wfs?request=getfeature&service=wfs&version=1.0.0&typename=" + (POLYGONS.getLocalPart())) + "&srsName=AUTO:42001,9001,-93,0"));
        // print(dom1);
        // print(dom2);
        MathTransform tx = CRS.findMathTransform(CRS.decode("EPSG:32615"), CRS.decode("AUTO:42001,9001,-93,0"));
        runTest(dom1, dom2, tx);
    }

    @Test
    public void testGetFeatureAutoCRSBBox() throws Exception {
        CoordinateReferenceSystem auto = CRS.decode("AUTO:42001,9001,-93,0");
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName(getLayerId(POLYGONS));
        ReferencedEnvelope nativeEnv = ftInfo.getFeatureSource(null, null).getBounds();
        ReferencedEnvelope reprojectedEnv = nativeEnv.transform(auto, true);
        Document dom1 = getAsDOM(("wfs?request=getfeature&service=wfs&version=1.0.0&typename=" + (POLYGONS.getLocalPart())));
        Document dom2 = getAsDOM((((((((((("wfs?request=getfeature&service=wfs&version=1.0.0&typename=" + (POLYGONS.getLocalPart())) + "&srsName=AUTO:42001,9001,-93,00&bbox=") + (reprojectedEnv.getMinX())) + ",") + (reprojectedEnv.getMinY())) + ",") + (reprojectedEnv.getMaxX())) + ",") + (reprojectedEnv.getMaxY())) + ",AUTO:42001,9001,-93,0"));
        // print(dom1);
        // print(dom2);
        MathTransform tx = CRS.findMathTransform(CRS.decode("EPSG:32615"), auto);
        runTest(dom1, dom2, tx);
    }

    @Test
    public void testGetFeatureReprojectedFeatureType() throws Exception {
        // bbox is 4,4,6,6 in wgs84, coordinates have been reprojected to 900913
        Document dom = getAsDOM((("wfs?request=getfeature&service=wfs&version=1.0.0&typename=" + (ReprojectionTest.GOOGLE.getLocalPart())) + "&bbox=445000,445000,668000,668000"));
        print(dom);
        assertXpathEvaluatesTo("1", "count(//cite:GoogleFeatures)", dom);
    }

    @Test
    public void testGetFeaturePost() throws Exception {
        String xml = ((((((("<wfs:GetFeature " + (((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cdf=\"http://www.opengis.net/cite/data\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"")) + (POLYGONS.getPrefix())) + ":") + (POLYGONS.getLocalPart())) + "\"> ") + "<wfs:PropertyName>cgf:polygonProperty</wfs:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>";
        Document dom1 = postAsDOM("wfs", xml);
        xml = ((((((((("<wfs:GetFeature " + (((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cdf=\"http://www.opengis.net/cite/data\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query srsName=\"")) + (ReprojectionTest.TARGET_CRS_CODE)) + "\" typeName=\"") + (POLYGONS.getPrefix())) + ":") + (POLYGONS.getLocalPart())) + "\"> ") + "<wfs:PropertyName>cgf:polygonProperty</wfs:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>";
        Document dom2 = postAsDOM("wfs", xml);
        runTest(dom1, dom2, ReprojectionTest.tx);
    }

    @Test
    public void testReprojectNullGeometries() throws Exception {
        // see https://osgeo-org.atlassian.net/browse/GEOS-1612
        String xml = (((((((("<wfs:GetFeature " + (((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cdf=\"http://www.opengis.net/cite/data\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query srsName=\"")) + (ReprojectionTest.TARGET_CRS_CODE)) + "\" typeName=\"") + (ReprojectionTest.NULL_GEOMETRIES.getPrefix())) + ":") + (ReprojectionTest.NULL_GEOMETRIES.getLocalPart())) + "\"> ") + "</wfs:Query> ") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        assertEquals(1, dom.getElementsByTagName("wfs:FeatureCollection").getLength());
    }

    @Test
    public void testGetFeatureWithProjectedBoxGet() throws Exception {
        Document dom;
        double[] cr = getTransformedPolygonsLayerBBox();
        String q = (((((((((("wfs?request=getfeature&service=wfs&version=1.0&typeName=" + (POLYGONS.getLocalPart())) + "&bbox=") + (cr[0])) + ",") + (cr[1])) + ",") + (cr[2])) + ",") + (cr[3])) + ",") + (ReprojectionTest.TARGET_CRS_CODE);
        dom = getAsDOM(q);
        assertEquals(1, dom.getElementsByTagName((((POLYGONS.getPrefix()) + ":") + (POLYGONS.getLocalPart()))).getLength());
    }

    @Test
    public void testGetFeatureWithProjectedBoxPost() throws Exception {
        Document dom;
        double[] cr = getTransformedPolygonsLayerBBox();
        String xml = ((((((((((((((((((((((((((((((((((((((((("<wfs:GetFeature service=\"WFS\" version=\"1.0.0\"" + " xmlns:") + (POLYGONS.getPrefix())) + "=\"") + (POLYGONS.getNamespaceURI())) + "\"") + " xmlns:ogc=\"http://www.opengis.net/ogc\" ") + " xmlns:gml=\"http://www.opengis.net/gml\" ") + " xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"") + (POLYGONS.getPrefix())) + ":") + (POLYGONS.getLocalPart())) + "\">") + "<wfs:PropertyName>cgf:polygonProperty</wfs:PropertyName> ") + "<ogc:Filter>") + "<ogc:BBOX>") + "<ogc:PropertyName>polygonProperty</ogc:PropertyName>") + "<gml:Box srsName=\"") + (ReprojectionTest.TARGET_CRS_CODE)) + "\">") + "<gml:coord>") + "<gml:X>") + (cr[0])) + "</gml:X>") + "<gml:Y>") + (cr[1])) + "</gml:Y>") + "</gml:coord>") + "<gml:coord>") + "<gml:X>") + (cr[2])) + "</gml:X>") + "<gml:Y>") + (cr[3])) + "</gml:Y>") + "</gml:coord>") + "</gml:Box>") + "</ogc:BBOX>") + "</ogc:Filter>") + "</wfs:Query> ") + "</wfs:GetFeature>";
        dom = postAsDOM("wfs", xml);
        assertEquals(1, dom.getElementsByTagName((((POLYGONS.getPrefix()) + ":") + (POLYGONS.getLocalPart()))).getLength());
    }

    /**
     * See GEOT-3760
     */
    @Test
    public void testGetFeatureWithProjectedBoxIntersectsPost() throws Exception {
        Document dom;
        double[] cr = getTransformedPolygonsLayerBBox();
        String xml = ((((((((((((((((((((((((((((((((((((((((("<wfs:GetFeature service=\"WFS\" version=\"1.0.0\"" + " xmlns:") + (POLYGONS.getPrefix())) + "=\"") + (POLYGONS.getNamespaceURI())) + "\"") + " xmlns:ogc=\"http://www.opengis.net/ogc\" ") + " xmlns:gml=\"http://www.opengis.net/gml\" ") + " xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"") + (POLYGONS.getPrefix())) + ":") + (POLYGONS.getLocalPart())) + "\" srsName=\"") + (ReprojectionTest.TARGET_CRS_CODE)) + "\">") + "<wfs:PropertyName>cgf:polygonProperty</wfs:PropertyName> ") + "<ogc:Filter>") + "<ogc:Intersects>") + "<ogc:PropertyName>polygonProperty</ogc:PropertyName>") + "<gml:Box>") + "<gml:coord>") + "<gml:X>") + (cr[0])) + "</gml:X>") + "<gml:Y>") + (cr[1])) + "</gml:Y>") + "</gml:coord>") + "<gml:coord>") + "<gml:X>") + (cr[2])) + "</gml:X>") + "<gml:Y>") + (cr[3])) + "</gml:Y>") + "</gml:coord>") + "</gml:Box>") + "</ogc:Intersects>") + "</ogc:Filter>") + "</wfs:Query> ") + "</wfs:GetFeature>";
        dom = postAsDOM("wfs", xml);
        assertEquals(1, dom.getElementsByTagName((((POLYGONS.getPrefix()) + ":") + (POLYGONS.getLocalPart()))).getLength());
    }
}

