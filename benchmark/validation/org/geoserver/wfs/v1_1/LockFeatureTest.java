/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v1_1;


import OGC.NAMESPACE;
import org.geoserver.wfs.WFSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class LockFeatureTest extends WFSTestSupport {
    @Test
    public void testLock() throws Exception {
        String xml = "<wfs:LockFeature xmlns:sf=\"http://cite.opengeospatial.org/gmlsf\" xmlns:wfs=\"http://www.opengis.net/wfs\" expiry=\"5\" handle=\"LockFeature-tc1\" " + ((((" lockAction=\"ALL\" " + " service=\"WFS\" ") + " version=\"1.1.0\">") + "<wfs:Lock handle=\"lock-1\" typeName=\"sf:PrimitiveGeoFeature\"/>") + "</wfs:LockFeature>");
        Document dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:LockFeatureResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(5, dom.getElementsByTagNameNS(NAMESPACE, "FeatureId").getLength());
        // release the lock
        releaseLock(dom);
    }

    @Test
    public void testLockGet() throws Exception {
        Document dom = getAsDOM("wfs?service=WFS&version=1.1.0&request=LockFeature&typename=sf:GenericEntity", 200);
        print(dom);
        Assert.assertEquals("wfs:LockFeatureResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(3, dom.getElementsByTagNameNS(NAMESPACE, "FeatureId").getLength());
        // release the lock
        releaseLock(dom);
    }

    @Test
    public void testLockWithNamespacesGet() throws Exception {
        Document dom = getAsDOM(("wfs?service=WFS&version=1.1.0&request=LockFeature&typename=ns53:GenericEntity" + "&namespace=xmlns(ns53=http://cite.opengeospatial.org/gmlsf)"), 200);
        // print(dom);
        Assert.assertEquals("wfs:LockFeatureResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(3, dom.getElementsByTagNameNS(NAMESPACE, "FeatureId").getLength());
        releaseLock(dom);
    }

    @Test
    public void testLockByBBOX() throws Exception {
        Document dom = getAsDOM(("wfs?service=WFS&version=1.1.0&request=LockFeature&typeName=sf:PrimitiveGeoFeature" + "&BBOX=57.0,-4.5,62.0,1.0,EPSG:4326"), 200);
        // print(dom);
        Assert.assertEquals("wfs:LockFeatureResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(1, dom.getElementsByTagNameNS(NAMESPACE, "FeatureId").getLength());
        // release the lock
        releaseLock(dom);
    }
}

