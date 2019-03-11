package com.baeldung.solrjava;


import java.io.IOException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Assert;
import org.junit.Test;


public class SolrJavaLiveTest {
    private SolrJavaIntegration solrJavaIntegration;

    @Test
    public void whenAdd_thenVerifyAddedByQueryOnId() throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
        query.set("q", "id:123456");
        QueryResponse response = null;
        response = solrJavaIntegration.getSolrClient().query(query);
        SolrDocumentList docList = response.getResults();
        Assert.assertEquals(1, docList.getNumFound());
        for (SolrDocument doc : docList) {
            Assert.assertEquals("Kenmore Dishwasher", ((String) (doc.getFieldValue("name"))));
            Assert.assertEquals(((Double) (599.99)), ((Double) (doc.getFieldValue("price"))));
        }
    }

    @Test
    public void whenAdd_thenVerifyAddedByQueryOnPrice() throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
        query.set("q", "price:599.99");
        QueryResponse response = null;
        response = solrJavaIntegration.getSolrClient().query(query);
        SolrDocumentList docList = response.getResults();
        Assert.assertEquals(1, docList.getNumFound());
        for (SolrDocument doc : docList) {
            Assert.assertEquals("123456", ((String) (doc.getFieldValue("id"))));
            Assert.assertEquals(((Double) (599.99)), ((Double) (doc.getFieldValue("price"))));
        }
    }

    @Test
    public void whenAdd_thenVerifyAddedByQuery() throws IOException, SolrServerException {
        SolrDocument doc = solrJavaIntegration.getSolrClient().getById("123456");
        Assert.assertEquals("Kenmore Dishwasher", ((String) (doc.getFieldValue("name"))));
        Assert.assertEquals(((Double) (599.99)), ((Double) (doc.getFieldValue("price"))));
    }

    @Test
    public void whenAddBean_thenVerifyAddedByQuery() throws IOException, SolrServerException {
        ProductBean pBean = new ProductBean("888", "Apple iPhone 6s", "299.99");
        solrJavaIntegration.addProductBean(pBean);
        SolrDocument doc = solrJavaIntegration.getSolrClient().getById("888");
        Assert.assertEquals("Apple iPhone 6s", ((String) (doc.getFieldValue("name"))));
        Assert.assertEquals(((Double) (299.99)), ((Double) (doc.getFieldValue("price"))));
    }

    @Test
    public void whenDeleteById_thenVerifyDeleted() throws IOException, SolrServerException {
        solrJavaIntegration.deleteSolrDocumentById("123456");
        SolrQuery query = new SolrQuery();
        query.set("q", "id:123456");
        QueryResponse response = solrJavaIntegration.getSolrClient().query(query);
        SolrDocumentList docList = response.getResults();
        Assert.assertEquals(0, docList.getNumFound());
    }

    @Test
    public void whenDeleteByQuery_thenVerifyDeleted() throws IOException, SolrServerException {
        solrJavaIntegration.deleteSolrDocumentByQuery("name:Kenmore Dishwasher");
        SolrQuery query = new SolrQuery();
        query.set("q", "id:123456");
        QueryResponse response = null;
        response = solrJavaIntegration.getSolrClient().query(query);
        SolrDocumentList docList = response.getResults();
        Assert.assertEquals(0, docList.getNumFound());
    }
}

