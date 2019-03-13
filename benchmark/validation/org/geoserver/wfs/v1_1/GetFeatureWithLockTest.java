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


public class GetFeatureWithLockTest extends WFSTestSupport {
    @Test
    public void test() throws Exception {
        String xml = "<wfs:GetFeatureWithLock service=\"WFS\" version=\"1.1.0\" " + (((((("\t  handle=\"GetFeatureWithLock-tc1\"" + "\t  expiry=\"5\"") + "\t  resultType=\"results\"") + "\t  xmlns:wfs=\"http://www.opengis.net/wfs\"") + "\t  xmlns:sf=\"http://cite.opengeospatial.org/gmlsf\">") + "\t  <wfs:Query handle=\"qry-1\" typeName=\"sf:PrimitiveGeoFeature\" />") + "	</wfs:GetFeatureWithLock>");
        Document dom = postAsDOM("wfs", xml);
        // print( dom );
        Assert.assertEquals("wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
        Assert.assertNotNull(dom.getDocumentElement().getAttribute("lockId"));
    }
}

