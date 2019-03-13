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
package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.document;


import Field.TYPE;
import Field.UID;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michael C. Han
 */
public class BulkDocumentRequestExecutorTest {
    @Test
    public void testBulkDocumentRequestTranslation() {
        String uid = "1";
        Document document = new DocumentImpl();
        document.addKeyword(TYPE, BulkDocumentRequestExecutorTest._MAPPING_NAME);
        document.addKeyword(UID, uid);
        document.addKeyword("staging", "true");
        IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(BulkDocumentRequestExecutorTest._INDEX_NAME, document);
        BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();
        bulkDocumentRequest.addBulkableDocumentRequest(indexDocumentRequest);
        DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(BulkDocumentRequestExecutorTest._INDEX_NAME, uid);
        bulkDocumentRequest.addBulkableDocumentRequest(deleteDocumentRequest);
        Document updatedDocument = new DocumentImpl();
        updatedDocument.addKeyword(UID, uid);
        updatedDocument.addKeyword("staging", "false");
        UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(BulkDocumentRequestExecutorTest._INDEX_NAME, uid, updatedDocument);
        bulkDocumentRequest.addBulkableDocumentRequest(updateDocumentRequest);
        BulkRequestBuilder bulkRequestBuilder = _bulkDocumentRequestExecutor.createBulkRequestBuilder(bulkDocumentRequest);
        Assert.assertEquals(3, bulkRequestBuilder.numberOfActions());
    }

    private static final String _INDEX_NAME = "test_request_index";

    private static final String _MAPPING_NAME = "testMapping";

    private BulkDocumentRequestExecutorImpl _bulkDocumentRequestExecutor;

    private final DocumentFixture _documentFixture = new DocumentFixture();

    private ElasticsearchFixture _elasticsearchFixture;
}

