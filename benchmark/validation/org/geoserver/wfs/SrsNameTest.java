/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import SrsNameStyle.NORMAL;
import SrsNameStyle.URL;
import SrsNameStyle.URN;
import SrsNameStyle.URN2;
import SrsNameStyle.XML;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class SrsNameTest extends WFSTestSupport {
    @Test
    public void testWfs10() throws Exception {
        String q = "wfs?request=getfeature&service=wfs&version=1.0.0" + "&typename=cgf:Points";
        Document d = getAsDOM(q);
        Assert.assertEquals("wfs:FeatureCollection", d.getDocumentElement().getNodeName());
        print(d);
        NodeList boxes = d.getElementsByTagName("gml:Box");
        Assert.assertFalse(((boxes.getLength()) == 0));
        for (int i = 0; i < (boxes.getLength()); i++) {
            Element box = ((Element) (boxes.item(i)));
            Assert.assertEquals("http://www.opengis.net/gml/srs/epsg.xml#32615", box.getAttribute("srsName"));
        }
        NodeList points = d.getElementsByTagName("gml:Point");
        Assert.assertFalse(((points.getLength()) == 0));
        for (int i = 0; i < (points.getLength()); i++) {
            Element point = ((Element) (points.item(i)));
            Assert.assertEquals("http://www.opengis.net/gml/srs/epsg.xml#32615", point.getAttribute("srsName"));
        }
    }

    @Test
    public void testWfs11() throws Exception {
        WFSInfo wfs = getWFS();
        boolean oldFeatureBounding = wfs.isFeatureBounding();
        wfs.setFeatureBounding(true);
        getGeoServer().save(wfs);
        try {
            String q = "wfs?request=getfeature&service=wfs&version=1.1.0" + "&typename=cgf:Points";
            Document d = getAsDOM(q);
            Assert.assertEquals("wfs:FeatureCollection", d.getDocumentElement().getNodeName());
            NodeList boxes = d.getElementsByTagName("gml:Envelope");
            Assert.assertFalse(((boxes.getLength()) == 0));
            for (int i = 0; i < (boxes.getLength()); i++) {
                Element box = ((Element) (boxes.item(i)));
                Assert.assertEquals("urn:x-ogc:def:crs:EPSG:32615", box.getAttribute("srsName"));
            }
            NodeList points = d.getElementsByTagName("gml:Point");
            Assert.assertFalse(((points.getLength()) == 0));
            for (int i = 0; i < (points.getLength()); i++) {
                Element point = ((Element) (points.item(i)));
                Assert.assertEquals("urn:x-ogc:def:crs:EPSG:32615", point.getAttribute("srsName"));
            }
        } finally {
            wfs.setFeatureBounding(oldFeatureBounding);
            getGeoServer().save(wfs);
        }
    }

    @Test
    public void testSrsNameSyntax11() throws Exception {
        doTestSrsNameSyntax11(URN, false);
        doTestSrsNameSyntax11(URN2, true);
        doTestSrsNameSyntax11(URL, true);
        doTestSrsNameSyntax11(NORMAL, true);
        doTestSrsNameSyntax11(XML, true);
    }
}

