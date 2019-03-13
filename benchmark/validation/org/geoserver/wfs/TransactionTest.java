/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import java.io.IOException;
import java.util.Map;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.security.impl.GeoServerUser;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * This test must be run with the server configured with the wfs 1.0 cite configuration, with data
 * initialized.
 *
 * @author Justin Deoliveira, The Open Planning Project
 */
public class TransactionTest extends WFSTestSupport {
    @Test
    public void testDelete() throws Exception {
        // 1. do a getFeature
        String getFeature = "<wfs:GetFeature " + ((((((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cgf=\"http://www.opengis.net/cite/geometry\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"cgf:Points\"> ") + "<ogc:PropertyName>cite:id</ogc:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("wfs", getFeature);
        Assert.assertEquals(1, dom.getElementsByTagName("gml:featureMember").getLength());
        // perform a delete
        String delete = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + ((((((((((("xmlns:cgf=\"http://www.opengis.net/cite/geometry\" " + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\"> ") + "<wfs:Delete typeName=\"cgf:Points\"> ") + "<ogc:Filter> ") + "<ogc:PropertyIsEqualTo> ") + "<ogc:PropertyName>cgf:id</ogc:PropertyName> ") + "<ogc:Literal>t0000</ogc:Literal> ") + "</ogc:PropertyIsEqualTo> ") + "</ogc:Filter> ") + "</wfs:Delete> ") + "</wfs:Transaction>");
        dom = postAsDOM("wfs", delete);
        Assert.assertEquals("WFS_TransactionResponse", dom.getDocumentElement().getLocalName());
        Assert.assertEquals(1, dom.getElementsByTagName("wfs:SUCCESS").getLength());
        // do another get feature
        dom = postAsDOM("wfs", getFeature);
        Assert.assertEquals(0, dom.getElementsByTagName("gml:featureMember").getLength());
    }

    @Test
    public void testDoubleDelete() throws Exception {
        // see
        // 1. do a getFeature
        String getFeature = "<wfs:GetFeature " + ((((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cgf=\"http://www.opengis.net/cite/geometry\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"cdf:Fifteen\"/> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("wfs", getFeature);
        print(dom);
        XMLAssert.assertXpathEvaluatesTo("15", "count(//gml:featureMember)", dom);
        // perform a delete
        String delete = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + ((((((((((((("xmlns:cgf=\"http://www.opengis.net/cite/geometry\" " + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\"> ") + "<wfs:Delete typeName=\"cdf:Fifteen\"> ") + "<ogc:Filter> ") + "<ogc:FeatureId fid=\"Fifteen.1\"/> ") + "</ogc:Filter> ") + "</wfs:Delete> ") + "<wfs:Delete typeName=\"cdf:Fifteen\"> ") + "<ogc:Filter> ") + "<ogc:FeatureId fid=\"Fifteen.2\"/> ") + "</ogc:Filter> ") + "</wfs:Delete> ") + "</wfs:Transaction>");
        dom = postAsDOM("wfs", delete);
        Assert.assertEquals("WFS_TransactionResponse", dom.getDocumentElement().getLocalName());
        Assert.assertEquals(1, dom.getElementsByTagName("wfs:SUCCESS").getLength());
        // do another get feature
        dom = postAsDOM("wfs", getFeature);
        XMLAssert.assertXpathEvaluatesTo("13", "count(//gml:featureMember)", dom);
    }

    @Test
    public void testInsert() throws Exception {
        // 1. do a getFeature
        String getFeature = "<wfs:GetFeature " + ((((((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cgf=\"http://www.opengis.net/cite/geometry\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"cgf:Lines\"> ") + "<ogc:PropertyName>cite:id</ogc:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("wfs", getFeature);
        Assert.assertEquals(1, dom.getElementsByTagName("gml:featureMember").getLength());
        // perform an insert
        String insert = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + (((((((((((((((("xmlns:cgf=\"http://www.opengis.net/cite/geometry\" " + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:gml=\"http://www.opengis.net/gml\"> ") + "<wfs:Insert > ") + "<cgf:Lines>") + "<cgf:lineStringProperty>") + "<gml:LineString>") + "<gml:coordinates decimal=\".\" cs=\",\" ts=\" \">") + "494475.71056415,5433016.8189323 494982.70115662,5435041.95096618") + "</gml:coordinates>") + "</gml:LineString>") + "</cgf:lineStringProperty>") + "<cgf:id>t0002</cgf:id>") + "</cgf:Lines>") + "</wfs:Insert>") + "</wfs:Transaction>");
        dom = postAsDOM("wfs", insert);
        Assert.assertTrue(((dom.getElementsByTagName("wfs:SUCCESS").getLength()) != 0));
        Assert.assertTrue(((dom.getElementsByTagName("wfs:InsertResult").getLength()) != 0));
        // do another get feature
        dom = postAsDOM("wfs", getFeature);
        Assert.assertEquals(2, dom.getElementsByTagName("gml:featureMember").getLength());
    }

    @Test
    public void testInsertWithGetFeatureInThePath() throws Exception {
        // perform an insert
        String insert = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + (((((((((((((((("xmlns:cgf=\"http://www.opengis.net/cite/geometry\" " + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:gml=\"http://www.opengis.net/gml\"> ") + "<wfs:Insert > ") + "<cgf:Lines>") + "<cgf:lineStringProperty>") + "<gml:LineString>") + "<gml:coordinates decimal=\".\" cs=\",\" ts=\" \">") + "494475.71056415,5433016.8189323 494982.70115662,5435041.95096618") + "</gml:coordinates>") + "</gml:LineString>") + "</cgf:lineStringProperty>") + "<cgf:id>t0002</cgf:id>") + "</cgf:Lines>") + "</wfs:Insert>") + "</wfs:Transaction>");
        Document dom = postAsDOM("wfs?service=WFS&version=1.0.0&request=GetFeature&typeName=cgf:Lines", insert);
        Assert.assertTrue(((dom.getElementsByTagName("wfs:SUCCESS").getLength()) != 0));
        Assert.assertTrue(((dom.getElementsByTagName("wfs:InsertResult").getLength()) != 0));
    }

    @Test
    public void testUpdate() throws Exception {
        // 1. do a getFeature
        String getFeature = "<wfs:GetFeature " + ((((((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cgf=\"http://www.opengis.net/cite/geometry\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"cgf:Polygons\"> ") + "<ogc:PropertyName>cite:id</ogc:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("wfs", getFeature);
        Assert.assertEquals(1, dom.getElementsByTagName("gml:featureMember").getLength());
        Assert.assertEquals("t0002", dom.getElementsByTagName("cgf:id").item(0).getFirstChild().getNodeValue());
        // perform an update
        String insert = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + (((((((((((((((("xmlns:cgf=\"http://www.opengis.net/cite/geometry\" " + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:gml=\"http://www.opengis.net/gml\"> ") + "<wfs:Update typeName=\"cgf:Polygons\" > ") + "<wfs:Property>") + "<wfs:Name>id</wfs:Name>") + "<wfs:Value>t0003</wfs:Value>") + "</wfs:Property>") + "<ogc:Filter>") + "<ogc:PropertyIsEqualTo>") + "<ogc:PropertyName>id</ogc:PropertyName>") + "<ogc:Literal>t0002</ogc:Literal>") + "</ogc:PropertyIsEqualTo>") + "</ogc:Filter>") + "</wfs:Update>") + "</wfs:Transaction>");
        dom = postAsDOM("wfs", insert);
        // do another get feature
        dom = postAsDOM("wfs", getFeature);
        Assert.assertEquals("t0003", dom.getElementsByTagName("cgf:id").item(0).getFirstChild().getNodeValue());
    }

    @Test
    public void testUpdateLayerQualified() throws Exception {
        // 1. do a getFeature
        String getFeature = "<wfs:GetFeature " + ((((((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cgf=\"http://www.opengis.net/cite/geometry\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"Polygons\"> ") + "<ogc:PropertyName>cite:id</ogc:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("cgf/Polygons/wfs", getFeature);
        Assert.assertEquals(1, dom.getElementsByTagName("gml:featureMember").getLength());
        Assert.assertEquals("t0002", dom.getElementsByTagName("cgf:id").item(0).getFirstChild().getNodeValue());
        // perform an update
        String insert = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + (((((((((((((((("xmlns:cgf=\"http://www.opengis.net/cite/geometry\" " + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:gml=\"http://www.opengis.net/gml\"> ") + "<wfs:Update typeName=\"Polygons\" > ") + "<wfs:Property>") + "<wfs:Name>id</wfs:Name>") + "<wfs:Value>t0003</wfs:Value>") + "</wfs:Property>") + "<ogc:Filter>") + "<ogc:PropertyIsEqualTo>") + "<ogc:PropertyName>id</ogc:PropertyName>") + "<ogc:Literal>t0002</ogc:Literal>") + "</ogc:PropertyIsEqualTo>") + "</ogc:Filter>") + "</wfs:Update>") + "</wfs:Transaction>");
        dom = postAsDOM("cgf/Lines/wfs", insert);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//ogc:ServiceException)", dom);
        dom = postAsDOM("cgf/Polygons/wfs", insert);
        // do another get feature
        dom = postAsDOM("cgf/Polygons/wfs", getFeature);
        Assert.assertEquals("t0003", dom.getElementsByTagName("cgf:id").item(0).getFirstChild().getNodeValue());
    }

    @Test
    public void testInsertWithBoundedBy() throws Exception {
        String xml = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + ((((((((((((((((((((((((((" xmlns:wfs=\"http://www.opengis.net/wfs\" " + " xmlns:gml=\"http://www.opengis.net/gml\" ") + " xmlns:cite=\"http://www.opengis.net/cite\">") + "<wfs:Insert>") + " <cite:BasicPolygons>") + "<gml:boundedBy>") + "<gml:Box>") + "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">-2,-1 2,6</gml:coordinates>") + "</gml:Box>") + "</gml:boundedBy>") + "  <cite:the_geom>") + "<gml:MultiPolygon>") + "<gml:polygonMember>") + "<gml:Polygon>") + "<gml:outerBoundaryIs>") + "<gml:LinearRing>") + "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">-1,0 0,1 1,0 0,-1 -1,0</gml:coordinates>") + "</gml:LinearRing>") + "</gml:outerBoundaryIs>") + "</gml:Polygon>") + "</gml:polygonMember>") + "</gml:MultiPolygon>") + "  </cite:the_geom>") + "  <cite:ID>foo</cite:ID>") + " </cite:BasicPolygons>") + "</wfs:Insert>") + "</wfs:Transaction>");
        Document dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:WFS_TransactionResponse", dom.getDocumentElement().getNodeName());
        Assert.assertTrue(((dom.getElementsByTagName("ogc:FeatureId").getLength()) > 0));
        Assert.assertTrue(((dom.getElementsByTagName("wfs:SUCCESS").getLength()) > 0));
    }

    @Test
    public void testInsertWorkspaceQualified() throws Exception {
        // 1. do a getFeature
        String getFeature = "<wfs:GetFeature " + ((((((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cgf=\"http://www.opengis.net/cite/geometry\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"Lines\"> ") + "<ogc:PropertyName>id</ogc:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("cgf/wfs", getFeature);
        Assert.assertEquals(1, dom.getElementsByTagName("gml:featureMember").getLength());
        // perform an insert
        String insert = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + (((((((((((((((("xmlns:cgf=\"http://www.opengis.net/cite/geometry\" " + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:gml=\"http://www.opengis.net/gml\"> ") + "<wfs:Insert > ") + "<cgf:Lines>") + "<cgf:lineStringProperty>") + "<gml:LineString>") + "<gml:coordinates decimal=\".\" cs=\",\" ts=\" \">") + "494475.71056415,5433016.8189323 494982.70115662,5435041.95096618") + "</gml:coordinates>") + "</gml:LineString>") + "</cgf:lineStringProperty>") + "<cgf:id>t0002</cgf:id>") + "</cgf:Lines>") + "</wfs:Insert>") + "</wfs:Transaction>");
        dom = postAsDOM("cgf/wfs", insert);
        Assert.assertTrue(((dom.getElementsByTagName("wfs:SUCCESS").getLength()) != 0));
        Assert.assertTrue(((dom.getElementsByTagName("wfs:InsertResult").getLength()) != 0));
        dom = postAsDOM("sf/wfs", insert);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//ogc:ServiceException)", dom);
        // do another get feature
        dom = postAsDOM("cgf/wfs", getFeature);
        Assert.assertEquals(2, dom.getElementsByTagName("gml:featureMember").getLength());
    }

    @Test
    public void testInsertLayerQualified() throws Exception {
        // 1. do a getFeature
        String getFeature = "<wfs:GetFeature " + ((((((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cgf=\"http://www.opengis.net/cite/geometry\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"Lines\"> ") + "<ogc:PropertyName>id</ogc:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("cgf/Lines/wfs", getFeature);
        Assert.assertEquals(1, dom.getElementsByTagName("gml:featureMember").getLength());
        // perform an insert
        String insert = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + (((((((((((((((("xmlns:cgf=\"http://www.opengis.net/cite/geometry\" " + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:gml=\"http://www.opengis.net/gml\"> ") + "<wfs:Insert > ") + "<cgf:Lines>") + "<cgf:lineStringProperty>") + "<gml:LineString>") + "<gml:coordinates decimal=\".\" cs=\",\" ts=\" \">") + "494475.71056415,5433016.8189323 494982.70115662,5435041.95096618") + "</gml:coordinates>") + "</gml:LineString>") + "</cgf:lineStringProperty>") + "<cgf:id>t0002</cgf:id>") + "</cgf:Lines>") + "</wfs:Insert>") + "</wfs:Transaction>");
        dom = postAsDOM("cgf/Lines/wfs", insert);
        Assert.assertTrue(((dom.getElementsByTagName("wfs:SUCCESS").getLength()) != 0));
        Assert.assertTrue(((dom.getElementsByTagName("wfs:InsertResult").getLength()) != 0));
        dom = postAsDOM("cgf/Polygons/wfs", insert);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//ogc:ServiceException)", dom);
        // do another get feature
        dom = postAsDOM("cgf/Lines/wfs", getFeature);
        Assert.assertEquals(2, dom.getElementsByTagName("gml:featureMember").getLength());
    }

    @Test
    public void testUpdateWithDifferentPrefix() throws Exception {
        // 1. do a getFeature
        String getFeature = "<wfs:GetFeature " + ((((((((("service=\"WFS\" " + "version=\"1.0.0\" ") + "xmlns:cgf=\"http://www.opengis.net/cite/geometry\" ") + "xmlns:ogc=\"http://www.opengis.net/ogc\" ") + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "> ") + "<wfs:Query typeName=\"cgf:Polygons\"> ") + "<ogc:PropertyName>cite:id</ogc:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("wfs", getFeature);
        Assert.assertEquals(1, dom.getElementsByTagName("gml:featureMember").getLength());
        Assert.assertEquals("t0002", dom.getElementsByTagName("cgf:id").item(0).getFirstChild().getNodeValue());
        // perform an update
        String update = "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + ((((((((((((((("xmlns:ogc=\"http://www.opengis.net/ogc\" " + "xmlns:wfs=\"http://www.opengis.net/wfs\" ") + "xmlns:gml=\"http://www.opengis.net/gml\"> ") + "<wfs:Update xmlns:foo=\"http://www.opengis.net/cite/geometry\" typeName=\"foo:Polygons\" > ") + "<wfs:Property>") + "<wfs:Name>id</wfs:Name>") + "<wfs:Value>t0003</wfs:Value>") + "</wfs:Property>") + "<ogc:Filter>") + "<ogc:PropertyIsEqualTo>") + "<ogc:PropertyName>id</ogc:PropertyName>") + "<ogc:Literal>t0002</ogc:Literal>") + "</ogc:PropertyIsEqualTo>") + "</ogc:Filter>") + "</wfs:Update>") + "</wfs:Transaction>");
        dom = postAsDOM("wfs", update);
        // do another get feature
        dom = postAsDOM("wfs", getFeature);
        Assert.assertEquals("t0003", dom.getElementsByTagName("cgf:id").item(0).getFirstChild().getNodeValue());
    }

    @Test
    public void elementHandlerOrder() throws Exception {
        Catalog cat = getCatalog();
        DataStoreInfo ds = cat.getFactory().createDataStore();
        ds.setName("foo");
        ds.setWorkspace(cat.getDefaultWorkspace());
        ds.setEnabled(true);
        Map params = ds.getConnectionParameters();
        params.put("dbtype", "h2");
        params.put("database", getTestData().getDataDirectoryRoot().getAbsolutePath());
        cat.add(ds);
        DataStore store = ((DataStore) (ds.getDataStore(null)));
        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        tb.setName("bar");
        tb.add("name", String.class);
        tb.add("geom", Point.class);
        store.createSchema(tb.buildFeatureType());
        CatalogBuilder cb = new CatalogBuilder(cat);
        cb.setStore(ds);
        SimpleFeatureStore fs = ((SimpleFeatureStore) (store.getFeatureSource("bar")));
        FeatureTypeInfo ft = cb.buildFeatureType(fs);
        cat.add(ft);
        String xml = (((((((((((((((((((((((((((((((((((((((((((("<wfs:Transaction service=\"WFS\" version=\"1.0.0\" " + ((" xmlns:wfs=\"http://www.opengis.net/wfs\" " + " xmlns:gml=\"http://www.opengis.net/gml\" ") + " xmlns:gs='")) + (SystemTestData.DEFAULT_URI)) + "'>") + "<wfs:Insert idgen='UseExisting'>") + " <gs:bar gml:id='1'>") + "    <gs:name>acme</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='2'>") + "    <gs:name>wiley</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='3'>") + "    <gs:name>bugs</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='4'>") + "    <gs:name>roadrunner</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='5'>") + "    <gs:name>daffy</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='6'>") + "    <gs:name>elmer</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='7'>") + "    <gs:name>tweety</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='8'>") + "    <gs:name>sylvester</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='9'>") + "    <gs:name>marvin</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='10'>") + "    <gs:name>yosemite</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='11'>") + "    <gs:name>porky</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='12'>") + "    <gs:name>speedy</gs:name>") + " </gs:bar>") + " <gs:bar gml:id='13'>") + "    <gs:name>taz</gs:name>") + " </gs:bar>") + "</wfs:Insert>") + "</wfs:Transaction>";
        Document dom = postAsDOM("wfs", xml);
        dom = getAsDOM("wfs?request=GetFeature&version=1.0.0&service=wfs&typeNames=gs:bar");
        NodeList elementsByTagName = dom.getElementsByTagName("gs:bar");
        for (int i = 1; i <= (elementsByTagName.getLength()); i++) {
            String id = elementsByTagName.item((i - 1)).getAttributes().item(0).getNodeValue();
            Assert.assertEquals(("bar." + i), id);
        }
        dom = getAsDOM("wfs?request=GetFeature&version=1.0.0&service=wfs&featureId=bar.5");
        XMLAssert.assertXpathEvaluatesTo("daffy", "//gs:name/text()", dom);
    }

    @Test
    public void testBuildGeotoolsTransaction() throws IOException {
        Authentication authentication = null;
        // no authentication, defaults to "anonymous"
        testBuildGeotoolsTransaction(authentication, "anonymous");
        // principal being a String
        Object principal = "John Smith";
        authentication = new TestingAuthenticationToken(principal, null);
        testBuildGeotoolsTransaction(authentication, "John Smith");
        // principal being an org.springframework.security.core.userdetails.UserDetails
        principal = new GeoServerUser("Akira Kurosawa");
        authentication = new TestingAuthenticationToken(principal, null);
        testBuildGeotoolsTransaction(authentication, "Akira Kurosawa");
    }
}

