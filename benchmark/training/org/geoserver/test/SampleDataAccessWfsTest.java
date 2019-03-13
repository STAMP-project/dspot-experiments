/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * WFS GetFeature to test integration of {@link SampleDataAccess} with GeoServer.
 *
 * @author Ben Caradoc-Davies, CSIRO Exploration and Mining
 */
public class SampleDataAccessWfsTest extends SampleDataAccessTestSupport {
    /**
     * Test whether GetCapabilities returns wfs:WFS_Capabilities.
     */
    @Test
    public void testGetCapabilities() throws Exception {
        Document doc = getAsDOM("wfs?request=GetCapabilities&version=1.1.0");
        LOGGER.info(("WFS GetCapabilities response:\n" + (prettyString(doc))));
        Assert.assertEquals("wfs:WFS_Capabilities", doc.getDocumentElement().getNodeName());
    }

    /**
     * Test whether DescribeFeatureType returns xsd:schema.
     */
    @Test
    public void testDescribeFeatureType() throws Exception {
        Document doc = getAsDOM("wfs?request=DescribeFeatureType&version=1.1.0&typename=gsml:MappedFeature");
        LOGGER.info(("WFS DescribeFeatureType response:\n" + (prettyString(doc))));
        Assert.assertEquals("xsd:schema", doc.getDocumentElement().getNodeName());
    }

    /**
     * Test whether GetFeature returns wfs:FeatureCollection.
     */
    @Test
    public void testGetFeature() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=gsml:MappedFeature");
        LOGGER.info(("WFS GetFeature response:\n" + (prettyString(doc))));
        Assert.assertEquals("wfs:FeatureCollection", doc.getDocumentElement().getNodeName());
    }

    /**
     * Test content of GetFeature response.
     */
    @Test
    public void testGetFeatureContent() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=gsml:MappedFeature");
        assertXpathCount(2, "//gsml:MappedFeature", doc);
        // mf1
        XMLAssert.assertXpathEvaluatesTo("GUNTHORPE FORMATION", "//gsml:MappedFeature[@gml:id='mf1']/gml:description", doc);
        XMLAssert.assertXpathEvaluatesTo("mf1.spec", ("//gsml:MappedFeature[@gml:id='mf1']/gsml:specification" + "/gsml:GeologicUnit/@gml:id"), doc);
        XMLAssert.assertXpathEvaluatesTo("Gunthorpe specification description", ("//gsml:MappedFeature[@gml:id='mf1']/gsml:specification" + "/gsml:GeologicUnit/gml:description"), doc);
        XMLAssert.assertXpathEvaluatesTo("-1.2 52.5 -1.2 52.6 -1.1 52.6 -1.1 52.5 -1.2 52.5", "//gsml:MappedFeature[@gml:id='mf1']/gsml:shape//gml:posList", doc);
        // mf2
        XMLAssert.assertXpathEvaluatesTo("MERCIA MUDSTONE GROUP", "//gsml:MappedFeature[@gml:id='mf2']/gml:description", doc);
        XMLAssert.assertXpathEvaluatesTo("mf2.spec", ("//gsml:MappedFeature[@gml:id='mf2']/gsml:specification" + "/gsml:GeologicUnit/@gml:id"), doc);
        XMLAssert.assertXpathEvaluatesTo("Mercia specification description", ("//gsml:MappedFeature[@gml:id='mf2']/gsml:specification" + "/gsml:GeologicUnit/gml:description"), doc);
        XMLAssert.assertXpathEvaluatesTo("-1.3 52.5 -1.3 52.6 -1.2 52.6 -1.2 52.5 -1.3 52.5", "//gsml:MappedFeature[@gml:id='mf2']/gsml:shape//gml:posList", doc);
    }
}

