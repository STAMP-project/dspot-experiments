/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v1_1;


import org.geoserver.wfs.WFSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * Test for 3D Bounding Box with Simple Features
 *
 * @author Niels Charlier
 */
public class BoundingBox3DTest extends WFSTestSupport {
    @Test
    public void testBBoxFeature1() throws Exception {
        Document doc = getAsDOM("wfs?request=getfeature&service=wfs&version=1.1.0&typename=sf:With3D&bbox=-200,-200,0,200,200,50");
        // print(doc);
        NodeList features = doc.getElementsByTagName("sf:With3D");
        Assert.assertEquals(1, features.getLength());
        Assert.assertEquals(features.item(0).getAttributes().getNamedItem("gml:id").getNodeValue(), "fid1");
    }

    @Test
    public void testBBoxFeature2() throws Exception {
        Document doc = getAsDOM("wfs?request=getfeature&service=wfs&version=1.1.0&typename=sf:With3D&bbox=-200,-200,50,200,200,100");
        NodeList features = doc.getElementsByTagName("sf:With3D");
        Assert.assertEquals(1, features.getLength());
        Assert.assertEquals(features.item(0).getAttributes().getNamedItem("gml:id").getNodeValue(), "fid2");
    }

    @Test
    public void testBBoxOnly2DCoordinates() throws Exception {
        Document doc = getAsDOM("wfs?request=getfeature&service=wfs&version=1.1.0&typename=sf:With3D&bbox=-200,-200,200,200,EPSG:4979");
        NodeList features = doc.getElementsByTagName("sf:With3D");
        // should have picked all coordinates
        Assert.assertEquals(3, features.getLength());
    }

    @Test
    public void testBBox3DCoordinatesExplicitCRS() throws Exception {
        Document doc = getAsDOM("wfs?request=getfeature&service=wfs&version=1.1.0&typename=sf:With3D&bbox=-200,-200,0,200,200,200,EPSG:4979");
        NodeList features = doc.getElementsByTagName("sf:With3D");
        // should have picked all coordinates
        Assert.assertEquals(3, features.getLength());
    }
}

