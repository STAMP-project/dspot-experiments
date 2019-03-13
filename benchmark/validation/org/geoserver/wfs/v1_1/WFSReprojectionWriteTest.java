/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v1_1;


import SystemTestData.POLYGONS;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.wfs.WFSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.operation.MathTransform;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class WFSReprojectionWriteTest extends WFSTestSupport {
    private static final String TARGET_CRS_CODE = "EPSG:900913";

    MathTransform tx;

    @Test
    public void testInsertSrsName() throws Exception {
        String q = "wfs?request=getfeature&service=wfs&version=1.1&typeName=" + (POLYGONS.getLocalPart());
        Document dom = getAsDOM(q);
        Assert.assertEquals(1, dom.getElementsByTagName((((POLYGONS.getPrefix()) + ":") + (POLYGONS.getLocalPart()))).getLength());
        Element polygonProperty = getFirstElementByTagName(dom, "cgf:polygonProperty");
        Element posList = getFirstElementByTagName(polygonProperty, "gml:posList");
        double[] c = posList(posList.getFirstChild().getNodeValue());
        double[] cr = new double[c.length];
        tx.transform(c, 0, cr, 0, ((cr.length) / 2));
        String xml = ((((((((((("<wfs:Transaction service=\"WFS\" version=\"1.1.0\" " + ((" xmlns:wfs=\"http://www.opengis.net/wfs\" " + " xmlns:gml=\"http://www.opengis.net/gml\" ") + " xmlns:cgf=\"")) + (SystemTestData.CGF_URI)) + "\">") + "<wfs:Insert handle=\"insert-1\" srsName=\"") + (WFSReprojectionWriteTest.TARGET_CRS_CODE)) + "\">") + " <cgf:Polygons>") + "<cgf:polygonProperty>") + "<gml:Polygon >") + "<gml:exterior>") + "<gml:LinearRing>") + "<gml:posList>";
        for (int i = 0; i < (cr.length); i++) {
            xml += cr[i];
            if (i < ((cr.length) - 1)) {
                xml += " ";
            }
        }
        xml += "</gml:posList>" + (((((("</gml:LinearRing>" + "</gml:exterior>") + "</gml:Polygon>") + "</cgf:polygonProperty>") + " </cgf:Polygons>") + "</wfs:Insert>") + "</wfs:Transaction>");
        postAsDOM("wfs", xml);
        dom = getAsDOM(q);
        // print(dom);
        Assert.assertEquals(2, dom.getElementsByTagName((((POLYGONS.getPrefix()) + ":") + (POLYGONS.getLocalPart()))).getLength());
    }

    @Test
    public void testInsertGeomSrsName() throws Exception {
        String q = "wfs?request=getfeature&service=wfs&version=1.1&typeName=" + (POLYGONS.getLocalPart());
        Document dom = getAsDOM(q);
        Element polygonProperty = getFirstElementByTagName(dom, "cgf:polygonProperty");
        Element posList = getFirstElementByTagName(polygonProperty, "gml:posList");
        double[] c = posList(posList.getFirstChild().getNodeValue());
        double[] cr = new double[c.length];
        tx.transform(c, 0, cr, 0, ((cr.length) / 2));
        String xml = ((((((((((("<wfs:Transaction service=\"WFS\" version=\"1.1.0\" " + ((" xmlns:wfs=\"http://www.opengis.net/wfs\" " + " xmlns:gml=\"http://www.opengis.net/gml\" ") + " xmlns:cgf=\"")) + (SystemTestData.CGF_URI)) + "\">") + "<wfs:Insert handle=\"insert-1\">") + " <cgf:Polygons>") + "<cgf:polygonProperty>") + "<gml:Polygon srsName=\"") + (WFSReprojectionWriteTest.TARGET_CRS_CODE)) + "\">") + "<gml:exterior>") + "<gml:LinearRing>") + "<gml:posList>";
        for (int i = 0; i < (cr.length); i++) {
            xml += cr[i];
            if (i < ((cr.length) - 1)) {
                xml += " ";
            }
        }
        xml += "</gml:posList>" + (((((("</gml:LinearRing>" + "</gml:exterior>") + "</gml:Polygon>") + "</cgf:polygonProperty>") + " </cgf:Polygons>") + "</wfs:Insert>") + "</wfs:Transaction>");
        postAsDOM("wfs", xml);
        dom = getAsDOM(q);
        Assert.assertEquals(2, dom.getElementsByTagName((((POLYGONS.getPrefix()) + ":") + (POLYGONS.getLocalPart()))).getLength());
    }

    @Test
    public void testUpdate() throws Exception {
        String q = "wfs?request=getfeature&service=wfs&version=1.1&typeName=" + (POLYGONS.getLocalPart());
        Document dom = getAsDOM(q);
        // print(dom);
        Element polygonProperty = getFirstElementByTagName(dom, "cgf:polygonProperty");
        Element posList = getFirstElementByTagName(polygonProperty, "gml:posList");
        double[] c = posList(posList.getFirstChild().getNodeValue());
        double[] cr = new double[c.length];
        tx.transform(c, 0, cr, 0, ((cr.length) / 2));
        // perform an update
        String xml = ((((("<wfs:Transaction service=\"WFS\" version=\"1.1.0\" " + (((((((("xmlns:cgf=\"http://www.opengis.net/cite/geometry\" " + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:gml=\"http://www.opengis.net/gml\"> ") + "<wfs:Update typeName=\"cgf:Polygons\" > ") + "<wfs:Property>") + "<wfs:Name>polygonProperty</wfs:Name>") + "<wfs:Value>") + "<gml:Polygon srsName=\"")) + (WFSReprojectionWriteTest.TARGET_CRS_CODE)) + "\">") + "<gml:exterior>") + "<gml:LinearRing>") + "<gml:posList>";
        for (int i = 0; i < (cr.length); i++) {
            xml += cr[i];
            if (i < ((cr.length) - 1)) {
                xml += " ";
            }
        }
        xml += "</gml:posList>" + (((((((((((("</gml:LinearRing>" + "</gml:exterior>") + "</gml:Polygon>") + "</wfs:Value>") + "</wfs:Property>") + "<ogc:Filter>") + "<ogc:PropertyIsEqualTo>") + "<ogc:PropertyName>id</ogc:PropertyName>") + "<ogc:Literal>t0002</ogc:Literal>") + "</ogc:PropertyIsEqualTo>") + "</ogc:Filter>") + "</wfs:Update>") + "</wfs:Transaction>");
        dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:TransactionResponse", dom.getDocumentElement().getNodeName());
        Element totalUpdated = getFirstElementByTagName(dom, "wfs:totalUpdated");
        Assert.assertEquals("1", totalUpdated.getFirstChild().getNodeValue());
        dom = getAsDOM(q);
        polygonProperty = getFirstElementByTagName(dom, "cgf:polygonProperty");
        posList = getFirstElementByTagName(polygonProperty, "gml:posList");
        double[] c1 = posList(posList.getFirstChild().getNodeValue());
        Assert.assertEquals(c.length, c1.length);
        for (int i = 0; i < (c.length); i++) {
            int x = ((int) ((c[i]) + 0.5));
            int y = ((int) ((c1[i]) + 0.5));
            Assert.assertEquals(x, y);
        }
    }

    @Test
    public void testUpdateReprojectFilter() throws Exception {
        testUpdateReprojectFilter("srsName=\"urn:x-ogc:def:crs:EPSG:6.11.2:4326\"");
    }

    @Test
    public void testUpdateReprojectFilterDefaultCRS() throws Exception {
        testUpdateReprojectFilter("");
    }

    @Test
    public void testDeleteReprojectFilter() throws Exception {
        testDeleteReprojectFilter("srsName=\"urn:x-ogc:def:crs:EPSG:6.11.2:4326\"");
    }

    @Test
    public void testDeleteReprojectFilterDefaultCRS() throws Exception {
        testDeleteReprojectFilter("");
    }

    @Test
    public void testLockReprojectFilter() throws Exception {
        testLockReprojectFilter("srsName=\"urn:x-ogc:def:crs:EPSG:6.11.2:4326\"");
    }

    @Test
    public void testLockReprojectFilterDefaultCRS() throws Exception {
        testLockReprojectFilter("");
    }
}

