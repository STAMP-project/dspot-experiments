/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.nsg.pagination.random;


import IndexConfigurationManager.STORE_SCHEMA_NAME;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.nsg.TestsUtils;
import org.geoserver.wfs.v2_0.WFS20TestSupport;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.filter.text.cql2.CQL;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class IndexResultTypeTest extends WFS20TestSupport {
    @Test
    public void testIndexGet() throws Exception {
        Document doc = getAsDOM("ows?service=WFS&version=2.0.0&request=GetFeature&typeNames=cdf:Fifteen&resultType=index");
        assertGML32(doc);
        Assert.assertEquals("15", doc.getDocumentElement().getAttribute("numberMatched"));
        Assert.assertEquals("0", doc.getDocumentElement().getAttribute("numberReturned"));
        XMLAssert.assertXpathEvaluatesTo("0", "count(//cdf:Fifteen)", doc);
        IndexConfigurationManager ic = applicationContext.getBean(IndexConfigurationManager.class);
        DataStore dataStore = ic.getCurrentDataStore();
        SimpleFeatureStore featureStore = ((SimpleFeatureStore) (dataStore.getFeatureSource(STORE_SCHEMA_NAME)));
        String resultSetId = doc.getDocumentElement().getAttribute("resultSetID");
        SimpleFeature feature = featureStore.getFeatures(CQL.toFilter((("ID='" + resultSetId) + "'"))).features().next();
        Assert.assertNotNull(feature);
    }

    @Test
    public void testIndexResultTypePost() throws Exception {
        String request = TestsUtils.readResource("/org/geoserver/nsg/pagination/random/get_request_1.xml");
        MockHttpServletResponse response = postAsServletResponse("wfs", request);
        Assert.assertThat(response.getStatus(), Matchers.is(200));
        Document document = dom(response, true);
        assertGML32(document);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection[@resultSetID])", document);
        IndexConfigurationManager ic = applicationContext.getBean(IndexConfigurationManager.class);
        DataStore dataStore = ic.getCurrentDataStore();
        SimpleFeatureStore featureStore = ((SimpleFeatureStore) (dataStore.getFeatureSource(STORE_SCHEMA_NAME)));
        String resultSetId = document.getDocumentElement().getAttribute("resultSetID");
        SimpleFeature feature = featureStore.getFeatures(CQL.toFilter((("ID='" + resultSetId) + "'"))).features().next();
        Assert.assertNotNull(feature);
        // check that is possible to perform a PageResults operation on the obtained resultSetID
        response = getAsServletResponse(("ows?service=WFS&version=2.0.0&request=PageResults&resultSetID=" + resultSetId));
        Assert.assertThat(response.getStatus(), Matchers.is(200));
        document = dom(response, true);
        assertGML32(document);
        XMLAssert.assertXpathEvaluatesTo("5", "count(//wfs:FeatureCollection/wfs:member)", document);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection[@numberMatched='15'])", document);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection[@numberReturned='5'])", document);
    }

    @Test
    public void testIndexPaginationGet() throws Exception {
        Document doc = getAsDOM("ows?service=WFS&version=2.0.0&request=GetFeature&typeNames=cdf:Fifteen&resultType=index&count=2&startIndex=6");
        assertGML32(doc);
        Assert.assertEquals("15", doc.getDocumentElement().getAttribute("numberMatched"));
        Assert.assertEquals("0", doc.getDocumentElement().getAttribute("numberReturned"));
        Assert.assertEquals("http://localhost:8080/geoserver/wfs?REQUEST=GetFeature&RESULTTYPE=index&VERSION=2.0.0&TYPENAMES=cdf%3AFifteen&SERVICE=WFS&COUNT=2&STARTINDEX=4", doc.getDocumentElement().getAttribute("previous"));
        Assert.assertEquals("http://localhost:8080/geoserver/wfs?REQUEST=GetFeature&RESULTTYPE=index&VERSION=2.0.0&TYPENAMES=cdf%3AFifteen&SERVICE=WFS&COUNT=2&STARTINDEX=8", doc.getDocumentElement().getAttribute("next"));
        XMLAssert.assertXpathEvaluatesTo("0", "count(//cdf:Fifteen)", doc);
        IndexConfigurationManager ic = applicationContext.getBean(IndexConfigurationManager.class);
        DataStore dataStore = ic.getCurrentDataStore();
        SimpleFeatureStore featureStore = ((SimpleFeatureStore) (dataStore.getFeatureSource(STORE_SCHEMA_NAME)));
        String resultSetId = doc.getDocumentElement().getAttribute("resultSetID");
        SimpleFeature feature = featureStore.getFeatures(CQL.toFilter((("ID='" + resultSetId) + "'"))).features().next();
        Assert.assertNotNull(feature);
    }

    @Test
    public void testPageResultIndexPaginationGet() throws Exception {
        Document docIndex = getAsDOM("ows?service=WFS&version=2.0.0&request=GetFeature&typeNames=cdf:Fifteen&resultType=index&count=2&startIndex=6");
        String resultSetId = docIndex.getDocumentElement().getAttribute("resultSetID");
        Document docResutl = getAsDOM(("ows?service=WFS&version=2.0.2&request=PageResults&resultSetID=" + resultSetId));
        XMLAssert.assertXpathEvaluatesTo("2", "count(//cdf:Fifteen)", docResutl);
        assertStartIndexCount(docResutl, "previous", 4, 2);
        assertStartIndexCount(docResutl, "next", 8, 2);
    }

    @Test
    public void testPageResultDefaultPaginationGet() throws Exception {
        Document docIndex = getAsDOM("ows?service=WFS&version=2.0.0&request=GetFeature&typeNames=cdf:Fifteen&resultType=index");
        String resultSetId = docIndex.getDocumentElement().getAttribute("resultSetID");
        Document docResutl = getAsDOM(("ows?service=WFS&version=2.0.2&request=PageResults&resultSetID=" + resultSetId));
        XMLAssert.assertXpathEvaluatesTo("10", "count(//cdf:Fifteen)", docResutl);
    }

    @Test
    public void testPageResultOverridePaginationGet() throws Exception {
        Document docIndex = getAsDOM("ows?service=WFS&version=2.0.0&request=GetFeature&typeNames=cdf:Fifteen&resultType=index&count=2&startIndex=6");
        String resultSetId = docIndex.getDocumentElement().getAttribute("resultSetID");
        Document docResutl = getAsDOM(("ows?service=WFS&version=2.0.2&request=PageResults&count=3&startIndex=5&resultSetID=" + resultSetId));
        XMLAssert.assertXpathEvaluatesTo("3", "count(//cdf:Fifteen)", docResutl);
        assertStartIndexCount(docResutl, "previous", 2, 3);
        assertStartIndexCount(docResutl, "next", 8, 3);
    }
}

