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


import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import org.junit.Test;


/**
 *
 *
 * @author Dylan Rebelak
 */
public class DeleteByQueryDocumentRequestExecutorTest {
    @Test
    public void testDocumentRequestTranslationWithNoRefresh() {
        doTestDocumentRequestTranslation(false);
    }

    @Test
    public void testDocumentRequestTranslationWithRefresh() {
        doTestDocumentRequestTranslation(true);
    }

    private static final String _FIELD_NAME = "testField";

    private static final String _INDEX_NAME = "test_request_index";

    private ElasticsearchFixture _elasticsearchFixture;
}

