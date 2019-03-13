/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class AliasTest extends WFSTestSupport {
    @Test
    public void testAliasFifteen() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&typename=cdf:ft15&version=1.0.0&service=wfs");
        Assert.assertEquals("wfs:FeatureCollection", doc.getDocumentElement().getNodeName());
        Assert.assertTrue(((doc.getElementsByTagName("gml:featureMember").getLength()) > 0));
        Assert.assertTrue(((doc.getElementsByTagName("cdf:ft15").getLength()) > 0));
    }

    @Test
    public void testGetByFeatureId() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&typename=cdf:ft15&version=1.0.0&featureId=ft15.1");
        Assert.assertEquals("wfs:FeatureCollection", doc.getDocumentElement().getNodeName());
        Assert.assertEquals(1, doc.getElementsByTagName("gml:featureMember").getLength());
        final NodeList features = doc.getElementsByTagName("cdf:ft15");
        Assert.assertEquals(1, features.getLength());
        Node feature = features.item(0);
        final Node fidNode = feature.getAttributes().getNamedItem("fid");
        Assert.assertEquals("ft15.1", fidNode.getTextContent());
    }

    @Test
    public void testDescribeFeatureType() throws Exception {
        Document doc = getAsDOM("wfs?request=DescribeFeatureType&typename=cdf:ft15&version=1.0.0");
        print(doc);
        Assert.assertEquals("xsd:schema", doc.getDocumentElement().getNodeName());
        XMLAssert.assertXpathEvaluatesTo("ft15", "/xs:schema/xs:element/@name", doc);
    }
}

