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


import WriteRequest.RefreshPolicy.IMMEDIATE;
import WriteRequest.RefreshPolicy.NONE;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch6.internal.document.ElasticsearchDocumentFactory;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import org.junit.Test;


/**
 *
 *
 * @author Michael C. Han
 */
public class ElasticsearchBulkableDocumentRequestTranslatorTest {
    @Test
    public void testDeleteDocumentRequestTranslationWithNoRefresh() {
        doTestDeleteDocumentRequestTranslation(false, NONE);
    }

    @Test
    public void testDeleteDocumentRequestTranslationWithRefresh() {
        doTestDeleteDocumentRequestTranslation(true, IMMEDIATE);
    }

    @Test
    public void testIndexDocumentRequestTranslationWithNoRefresh() throws Exception {
        doTestIndexDocumentRequestTranslation("1", false, NONE);
    }

    @Test
    public void testIndexDocumentRequestTranslationWithNoRefreshNoId() throws Exception {
        doTestIndexDocumentRequestTranslation(null, false, NONE);
    }

    @Test
    public void testIndexDocumentRequestTranslationWithRefresh() throws Exception {
        doTestIndexDocumentRequestTranslation("1", true, IMMEDIATE);
    }

    @Test
    public void testIndexDocumentRequestTranslationWithRefreshNoId() throws Exception {
        doTestIndexDocumentRequestTranslation(null, true, IMMEDIATE);
    }

    @Test
    public void testUpdateDocumentRequestTranslationWithNoRefresh() throws Exception {
        doTestUpdateDocumentRequestTranslation("1", false, NONE);
    }

    @Test
    public void testUpdateDocumentRequestTranslationWithNoRefreshNoId() throws Exception {
        doTestUpdateDocumentRequestTranslation(null, false, NONE);
    }

    @Test
    public void testUpdateDocumentRequestTranslationWithRefresh() throws Exception {
        doTestUpdateDocumentRequestTranslation("1", true, IMMEDIATE);
    }

    @Test
    public void testUpdateDocumentRequestTranslationWithRefreshNoId() throws Exception {
        doTestUpdateDocumentRequestTranslation(null, true, IMMEDIATE);
    }

    private static final String _INDEX_NAME = "test_request_index";

    private static final String _MAPPING_NAME = "testMapping";

    private final DocumentFixture _documentFixture = new DocumentFixture();

    private ElasticsearchBulkableDocumentRequestTranslator _elasticsearchBulkableDocumentRequestTranslator;

    private ElasticsearchDocumentFactory _elasticsearchDocumentFactory;

    private ElasticsearchFixture _elasticsearchFixture;
}

