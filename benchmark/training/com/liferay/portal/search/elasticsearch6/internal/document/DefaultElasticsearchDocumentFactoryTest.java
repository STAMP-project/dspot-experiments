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
package com.liferay.portal.search.elasticsearch6.internal.document;


import StringPool.BLANK;
import StringPool.NULL;
import StringPool.SPACE;
import StringPool.THREE_SPACES;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import org.junit.Test;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class DefaultElasticsearchDocumentFactoryTest {
    @Test
    public void testNull() throws Exception {
        assertElasticsearchDocument(null, "{}");
    }

    @Test
    public void testSpaces() throws Exception {
        assertElasticsearchDocument(SPACE, "{\"field\":\"\"}");
        assertElasticsearchDocument(THREE_SPACES, "{\"field\":\"\"}");
    }

    @Test
    public void testStringBlank() throws Exception {
        assertElasticsearchDocument(BLANK, "{\"field\":\"\"}");
    }

    @Test
    public void testStringNull() throws Exception {
        assertElasticsearchDocument(NULL, "{\"field\":\"null\"}");
    }

    private final DocumentFixture _documentFixture = new DocumentFixture();

    private final ElasticsearchDocumentFactory _elasticsearchDocumentFactory = new DefaultElasticsearchDocumentFactory();
}

