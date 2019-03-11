/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v1_1;


import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.wfs.WFSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class AliasTest extends WFSTestSupport {
    @Test
    public void testAliasFifteen() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&typename=cdf:ft15&version=1.1.0&service=wfs");
        // print(doc);
        Assert.assertEquals("wfs:FeatureCollection", doc.getDocumentElement().getNodeName());
        Assert.assertEquals(1, doc.getElementsByTagName("gml:featureMembers").getLength());
        Assert.assertEquals(15, doc.getElementsByTagName("cdf:ft15").getLength());
    }

    @Test
    public void testGetByFeatureId() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&typename=cdf:ft15&version=1.1.0&featureId=ft15.1");
        // print(doc);
        Assert.assertEquals("wfs:FeatureCollection", doc.getDocumentElement().getNodeName());
        Assert.assertEquals(1, doc.getElementsByTagName("gml:featureMembers").getLength());
        final NodeList features = doc.getElementsByTagName("cdf:ft15");
        Assert.assertEquals(1, features.getLength());
        Node feature = features.item(0);
        final Node fidNode = feature.getAttributes().getNamedItem("gml:id");
        Assert.assertEquals("ft15.1", fidNode.getTextContent());
    }

    @Test
    public void testDescribeFeatureType() throws Exception {
        Document doc = getAsDOM("wfs?request=DescribeFeatureType&typename=cdf:ft15&version=1.1.0");
        Assert.assertEquals("xsd:schema", doc.getDocumentElement().getNodeName());
        XMLAssert.assertXpathEvaluatesTo("ft15", "/xsd:schema/xsd:element/@name", doc);
    }
}

