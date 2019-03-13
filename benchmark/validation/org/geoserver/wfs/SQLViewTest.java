/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import Query.ALL;
import junit.framework.Assert;
import net.sf.json.JSON;
import org.geoserver.catalog.FeatureTypeInfo;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class SQLViewTest extends WFSTestSupport {
    static final String tableTypeName = "gs:pgeo";

    static final String viewTypeName = "gs:pgeo_view";

    /**
     * Checks the setup did the expected job
     */
    @Test
    public void testStoreSetup() throws Exception {
        FeatureTypeInfo tableTypeInfo = getCatalog().getFeatureTypeByName(SQLViewTest.tableTypeName);
        Assert.assertNotNull(tableTypeInfo);
        Assert.assertEquals(5, tableTypeInfo.getFeatureSource(null, null).getCount(ALL));
        FeatureTypeInfo viewTypeInfo = getCatalog().getFeatureTypeByName(SQLViewTest.viewTypeName);
        Assert.assertNotNull(viewTypeInfo);
        Assert.assertEquals(1, viewTypeInfo.getFeatureSource(null, null).getCount(ALL));
    }

    @Test
    public void testViewParamsGet() throws Exception {
        Document dom = getAsDOM((("wfs?service=WFS&request=GetFeature&typename=" + (SQLViewTest.viewTypeName)) + "&version=1.1&viewparams=bool:true;name:name-f003"));
        // print(dom);
        assertXpathEvaluatesTo("name-f003", "//gs:pgeo_view/gml:name", dom);
        assertXpathEvaluatesTo("1", "count(//gs:pgeo_view)", dom);
    }

    @Test
    public void testViewParamsJsonGet() throws Exception {
        JSON json = getAsJSON((("wfs?service=WFS&request=GetFeature&typename=" + (SQLViewTest.viewTypeName)) + "&version=1.1&viewparams=bool:true;name:name-f003&outputFormat=application/json"));
        // print(json);
        Assert.assertEquals(1, getInt("totalFeatures"));
    }

    @Test
    public void testPostWithViewParams_v100() throws Exception {
        String xml = ((("<wfs:GetFeature service=\"WFS\" version=\"1.0.0\" " + (((("viewParams=\"bool:true;name:name-f003\" " + "xmlns:cdf=\"http://www.opengis.net/cite/data\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" > ") + "<wfs:Query typeName=\"")) + (SQLViewTest.viewTypeName)) + "\"> ") + "</wfs:Query></wfs:GetFeature>";
        Document doc = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:FeatureCollection", doc.getDocumentElement().getNodeName());
        NodeList featureMembers = doc.getElementsByTagName("gml:featureMember");
        Assert.assertFalse(((featureMembers.getLength()) == 0));
        assertXpathEvaluatesTo("name-f003", "//gs:pgeo_view/gs:name", doc);
        assertXpathEvaluatesTo("1", "count(//gs:pgeo_view)", doc);
    }

    @Test
    public void testPostWithViewParams_110() throws Exception {
        String xml = ((("<wfs:GetFeature service=\"WFS\" version=\"1.1.0\" " + (((("viewParams=\"bool:true;name:name-f003\" " + "xmlns:cdf=\"http://www.opengis.net/cite/data\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" > ") + "<wfs:Query typeName=\"")) + (SQLViewTest.viewTypeName)) + "\"> ") + "</wfs:Query></wfs:GetFeature>";
        Document doc = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:FeatureCollection", doc.getDocumentElement().getNodeName());
        NodeList featureCollection = doc.getElementsByTagName("wfs:FeatureCollection");
        Assert.assertFalse(((featureCollection.getLength()) == 0));
        assertXpathEvaluatesTo("name-f003", "//gs:pgeo_view/gml:name", doc);
        assertXpathEvaluatesTo("1", "count(//gs:pgeo_view)", doc);
    }

    @Test
    public void testPostWithViewParams_200() throws Exception {
        String xml = ((("<wfs:GetFeature service=\"WFS\" version=\"2.0.0\" " + (("xmlns:wfs=\"http://www.opengis.net/wfs/2.0\" " + "viewParams=\"bool:true;name:name-f003\"> ") + "<wfs:Query typeNames=\"")) + (SQLViewTest.viewTypeName)) + "\">") + "</wfs:Query></wfs:GetFeature>";
        Document doc = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:FeatureCollection", doc.getDocumentElement().getNodeName());
        NodeList features = doc.getElementsByTagName("gs:pgeo_view");
        Assert.assertEquals(1, features.getLength());
        Assert.assertEquals(features.item(0).getFirstChild().getNodeName(), "gml:name");
        Assert.assertEquals(features.item(0).getFirstChild().getTextContent(), "name-f003");
    }
}

