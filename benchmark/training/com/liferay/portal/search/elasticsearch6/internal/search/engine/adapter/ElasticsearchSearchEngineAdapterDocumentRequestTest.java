/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter;


import Field.UID;
import RestStatus.CREATED;
import RestStatus.OK;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentItemResponse;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentResponse;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dylan Rebelak
 */
public class ElasticsearchSearchEngineAdapterDocumentRequestTest {
    @Test
    public void testExecuteBulkDocumentRequest() {
        Document document1 = new DocumentImpl();
        document1.addKeyword(UID, "1");
        document1.addKeyword(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME, Boolean.TRUE.toString());
        IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(ElasticsearchSearchEngineAdapterDocumentRequestTest._INDEX_NAME, document1);
        indexDocumentRequest.setType(ElasticsearchSearchEngineAdapterDocumentRequestTest._MAPPING_NAME);
        BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();
        bulkDocumentRequest.addBulkableDocumentRequest(indexDocumentRequest);
        Document document2 = new DocumentImpl();
        document2.addKeyword(UID, "2");
        document2.addKeyword(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME, Boolean.FALSE.toString());
        IndexDocumentRequest indexDocumentRequest2 = new IndexDocumentRequest(ElasticsearchSearchEngineAdapterDocumentRequestTest._INDEX_NAME, document2);
        indexDocumentRequest2.setType(ElasticsearchSearchEngineAdapterDocumentRequestTest._MAPPING_NAME);
        bulkDocumentRequest.addBulkableDocumentRequest(indexDocumentRequest2);
        BulkDocumentResponse bulkDocumentResponse = _searchEngineAdapter.execute(bulkDocumentRequest);
        Assert.assertFalse(bulkDocumentResponse.hasErrors());
        List<BulkDocumentItemResponse> bulkDocumentItemResponses = bulkDocumentResponse.getBulkDocumentItemResponses();
        Assert.assertEquals(bulkDocumentItemResponses.toString(), 2, bulkDocumentItemResponses.size());
        BulkDocumentItemResponse bulkDocumentItemResponse1 = bulkDocumentItemResponses.get(0);
        Assert.assertEquals("1", bulkDocumentItemResponse1.getId());
        BulkDocumentItemResponse bulkDocumentItemResponse2 = bulkDocumentItemResponses.get(1);
        Assert.assertEquals("2", bulkDocumentItemResponse2.getId());
        DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(ElasticsearchSearchEngineAdapterDocumentRequestTest._INDEX_NAME, "1");
        deleteDocumentRequest.setType(ElasticsearchSearchEngineAdapterDocumentRequestTest._MAPPING_NAME);
        BulkDocumentRequest bulkDocumentRequest2 = new BulkDocumentRequest();
        bulkDocumentRequest2.addBulkableDocumentRequest(deleteDocumentRequest);
        Document document2Update = new DocumentImpl();
        document2Update.addKeyword(UID, "2");
        document2Update.addKeyword(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME, Boolean.TRUE.toString());
        UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(ElasticsearchSearchEngineAdapterDocumentRequestTest._INDEX_NAME, "2", document2Update);
        updateDocumentRequest.setType(ElasticsearchSearchEngineAdapterDocumentRequestTest._MAPPING_NAME);
        bulkDocumentRequest2.addBulkableDocumentRequest(updateDocumentRequest);
        BulkDocumentResponse bulkDocumentResponse2 = _searchEngineAdapter.execute(bulkDocumentRequest2);
        Assert.assertFalse(bulkDocumentResponse2.hasErrors());
        List<BulkDocumentItemResponse> bulkDocumentItemResponses2 = bulkDocumentResponse2.getBulkDocumentItemResponses();
        Assert.assertEquals(bulkDocumentItemResponses2.toString(), 2, bulkDocumentItemResponses2.size());
        BulkDocumentItemResponse bulkDocumentItemResponse3 = bulkDocumentItemResponses2.get(0);
        Assert.assertEquals("1", bulkDocumentItemResponse3.getId());
        BulkDocumentItemResponse bulkDocumentItemResponse4 = bulkDocumentItemResponses2.get(1);
        Assert.assertEquals("2", bulkDocumentItemResponse4.getId());
        GetResponse getResponse1 = _getDocument("1");
        Assert.assertFalse(getResponse1.isExists());
        GetResponse getResponse2 = _getDocument("2");
        Assert.assertTrue(getResponse2.isExists());
        Map<String, Object> map2 = getResponse2.getSource();
        Assert.assertEquals(Boolean.TRUE.toString(), map2.get(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME));
    }

    @Test
    public void testExecuteBulkDocumentRequestNoUid() {
        Document document1 = new DocumentImpl();
        document1.addKeyword(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME, Boolean.TRUE.toString());
        IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(ElasticsearchSearchEngineAdapterDocumentRequestTest._INDEX_NAME, document1);
        indexDocumentRequest.setType(ElasticsearchSearchEngineAdapterDocumentRequestTest._MAPPING_NAME);
        BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();
        bulkDocumentRequest.addBulkableDocumentRequest(indexDocumentRequest);
        Document document2 = new DocumentImpl();
        document2.addKeyword(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME, Boolean.FALSE.toString());
        IndexDocumentRequest indexDocumentRequest2 = new IndexDocumentRequest(ElasticsearchSearchEngineAdapterDocumentRequestTest._INDEX_NAME, document2);
        indexDocumentRequest2.setType(ElasticsearchSearchEngineAdapterDocumentRequestTest._MAPPING_NAME);
        bulkDocumentRequest.addBulkableDocumentRequest(indexDocumentRequest2);
        BulkDocumentResponse bulkDocumentResponse = _searchEngineAdapter.execute(bulkDocumentRequest);
        Assert.assertFalse(bulkDocumentResponse.hasErrors());
        List<BulkDocumentItemResponse> bulkDocumentItemResponses = bulkDocumentResponse.getBulkDocumentItemResponses();
        Assert.assertEquals(bulkDocumentItemResponses.toString(), 2, bulkDocumentItemResponses.size());
        BulkDocumentItemResponse bulkDocumentItemResponse1 = bulkDocumentItemResponses.get(0);
        Assert.assertFalse(Validator.isBlank(bulkDocumentItemResponse1.getId()));
        BulkDocumentItemResponse bulkDocumentItemResponse2 = bulkDocumentItemResponses.get(1);
        Assert.assertFalse(Validator.isBlank(bulkDocumentItemResponse2.getId()));
        DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(ElasticsearchSearchEngineAdapterDocumentRequestTest._INDEX_NAME, bulkDocumentItemResponse1.getId());
        deleteDocumentRequest.setType(ElasticsearchSearchEngineAdapterDocumentRequestTest._MAPPING_NAME);
        BulkDocumentRequest bulkDocumentRequest2 = new BulkDocumentRequest();
        bulkDocumentRequest2.addBulkableDocumentRequest(deleteDocumentRequest);
        Document document2Update = new DocumentImpl();
        document2Update.addKeyword(UID, bulkDocumentItemResponse2.getId());
        document2Update.addKeyword(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME, Boolean.TRUE.toString());
        UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(ElasticsearchSearchEngineAdapterDocumentRequestTest._INDEX_NAME, bulkDocumentItemResponse2.getId(), document2Update);
        updateDocumentRequest.setType(ElasticsearchSearchEngineAdapterDocumentRequestTest._MAPPING_NAME);
        bulkDocumentRequest2.addBulkableDocumentRequest(updateDocumentRequest);
        BulkDocumentResponse bulkDocumentResponse2 = _searchEngineAdapter.execute(bulkDocumentRequest2);
        Assert.assertFalse(bulkDocumentResponse2.hasErrors());
        List<BulkDocumentItemResponse> bulkDocumentItemResponses2 = bulkDocumentResponse2.getBulkDocumentItemResponses();
        Assert.assertEquals(bulkDocumentItemResponses2.toString(), 2, bulkDocumentItemResponses2.size());
        BulkDocumentItemResponse bulkDocumentItemResponse3 = bulkDocumentItemResponses2.get(0);
        Assert.assertEquals(bulkDocumentItemResponse1.getId(), bulkDocumentItemResponse3.getId());
        BulkDocumentItemResponse bulkDocumentItemResponse4 = bulkDocumentItemResponses2.get(1);
        Assert.assertEquals(bulkDocumentItemResponse2.getId(), bulkDocumentItemResponse4.getId());
        GetResponse getResponse1 = _getDocument(bulkDocumentItemResponse1.getId());
        Assert.assertFalse(getResponse1.isExists());
        GetResponse getResponse2 = _getDocument(bulkDocumentItemResponse2.getId());
        Assert.assertTrue(getResponse2.isExists());
        Map<String, Object> map2 = getResponse2.getSource();
        Assert.assertEquals(Boolean.TRUE.toString(), map2.get(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME));
    }

    @Test
    public void testExecuteDeleteDocumentRequest() {
        String documentSource = ("{\"" + (ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME)) + "\":\"true\"}";
        String id = "1";
        _indexDocument(documentSource, id);
        GetResponse getResponse1 = _getDocument(id);
        Assert.assertTrue(getResponse1.isExists());
        DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(ElasticsearchSearchEngineAdapterDocumentRequestTest._INDEX_NAME, id);
        deleteDocumentRequest.setType(ElasticsearchSearchEngineAdapterDocumentRequestTest._MAPPING_NAME);
        DeleteDocumentResponse deleteDocumentResponse = _searchEngineAdapter.execute(deleteDocumentRequest);
        Assert.assertEquals(OK.getStatus(), deleteDocumentResponse.getStatus());
        GetResponse getResponse2 = _getDocument(id);
        Assert.assertFalse(getResponse2.isExists());
    }

    @Test
    public void testExecuteIndexDocumentRequestNoUid() {
        Document document = new DocumentImpl();
        IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter(null, document);
        Assert.assertEquals(CREATED.getStatus(), indexDocumentResponse.getStatus());
        Assert.assertNotNull(indexDocumentResponse.getUid());
    }

    @Test
    public void testExecuteIndexDocumentRequestNoUidWithUpdate() {
        Document document = new DocumentImpl();
        IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter(null, document);
        document.addKeyword(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME, true);
        _updateDocumentWithAdapter(indexDocumentResponse.getUid(), document);
        GetResponse getResponse = _getDocument(indexDocumentResponse.getUid());
        Map<String, Object> map = getResponse.getSource();
        Assert.assertEquals(Boolean.TRUE.toString(), map.get(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME));
    }

    @Test
    public void testExecuteIndexDocumentRequestUidInDocument() {
        Document document = new DocumentImpl();
        document.addKeyword(UID, "1");
        IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter(null, document);
        Assert.assertEquals(CREATED.getStatus(), indexDocumentResponse.getStatus());
        Assert.assertEquals("1", indexDocumentResponse.getUid());
    }

    @Test
    public void testExecuteIndexDocumentRequestUidInRequest() {
        Document document = new DocumentImpl();
        IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter("1", document);
        Assert.assertEquals(CREATED.getStatus(), indexDocumentResponse.getStatus());
        Assert.assertEquals("1", indexDocumentResponse.getUid());
    }

    @Test
    public void testExecuteUpdateDocumentRequest() {
        String documentSource = ("{\"" + (ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME)) + "\":\"true\"}";
        String id = "1";
        _indexDocument(documentSource, id);
        GetResponse getResponse1 = _getDocument(id);
        Map<String, Object> map1 = getResponse1.getSource();
        Assert.assertEquals(Boolean.TRUE.toString(), map1.get(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME));
        Document document = new DocumentImpl();
        document.addKeyword(UID, id);
        document.addKeyword(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME, false);
        UpdateDocumentResponse updateDocumentResponse = _updateDocumentWithAdapter(id, document);
        Assert.assertEquals(OK.getStatus(), updateDocumentResponse.getStatus());
        GetResponse getResponse2 = _getDocument(id);
        Map<String, Object> map2 = getResponse2.getSource();
        Assert.assertEquals(Boolean.FALSE.toString(), map2.get(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME));
    }

    @Test
    public void testExecuteUpdateDocumentRequestNoDocumentUid() {
        String documentSource = ("{\"" + (ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME)) + "\":\"true\"}";
        String id = "1";
        _indexDocument(documentSource, id);
        GetResponse getResponse1 = _getDocument(id);
        Map<String, Object> map1 = getResponse1.getSource();
        Assert.assertEquals(Boolean.TRUE.toString(), map1.get(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME));
        Document document = new DocumentImpl();
        document.addKeyword(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME, false);
        UpdateDocumentResponse updateDocumentResponse = _updateDocumentWithAdapter(id, document);
        Assert.assertEquals(OK.getStatus(), updateDocumentResponse.getStatus());
        GetResponse getResponse2 = _getDocument(id);
        Map<String, Object> map2 = getResponse2.getSource();
        Assert.assertEquals(Boolean.FALSE.toString(), map2.get(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME));
    }

    @Test
    public void testExecuteUpdateDocumentRequestNoRequestId() {
        String documentSource = ("{\"" + (ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME)) + "\":\"true\"}";
        String id = "1";
        _indexDocument(documentSource, id);
        GetResponse getResponse1 = _getDocument(id);
        Map<String, Object> map1 = getResponse1.getSource();
        Assert.assertEquals(Boolean.TRUE.toString(), map1.get(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME));
        Document document = new DocumentImpl();
        document.addKeyword(UID, id);
        document.addKeyword(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME, false);
        UpdateDocumentResponse updateDocumentResponse = _updateDocumentWithAdapter(null, document);
        Assert.assertEquals(OK.getStatus(), updateDocumentResponse.getStatus());
        GetResponse getResponse2 = _getDocument(id);
        Map<String, Object> map2 = getResponse2.getSource();
        Assert.assertEquals(Boolean.FALSE.toString(), map2.get(ElasticsearchSearchEngineAdapterDocumentRequestTest._FIELD_NAME));
    }

    private static final String _FIELD_NAME = "matchDocument";

    private static final String _INDEX_NAME = "test_request_index";

    private static final String _MAPPING_NAME = "testDocumentMapping";

    private static final String _MAPPING_SOURCE = "{\"properties\":{\"matchDocument\":{\"type\":\"boolean\"}}}";

    private Client _client;

    private final DocumentFixture _documentFixture = new DocumentFixture();

    private ElasticsearchFixture _elasticsearchFixture;

    private SearchEngineAdapter _searchEngineAdapter;
}

