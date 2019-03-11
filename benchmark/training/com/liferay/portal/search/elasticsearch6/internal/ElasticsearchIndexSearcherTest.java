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
package com.liferay.portal.search.elasticsearch6.internal;


import ElasticsearchSearchContextAttributes.ATTRIBUTE_KEY_SEARCH_REQUEST_PREFERENCE;
import SearchContextAttributes.ATTRIBUTE_KEY_BASIC_FACET_SELECTION;
import SearchContextAttributes.ATTRIBUTE_KEY_LUCENE_SYNTAX;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Michael C. Han
 */
public class ElasticsearchIndexSearcherTest {
    @Test
    public void testSearchContextAttributes() throws SearchException {
        SearchContext searchContext = new SearchContext();
        searchContext.setAttribute(ATTRIBUTE_KEY_BASIC_FACET_SELECTION, Boolean.TRUE);
        searchContext.setAttribute(ATTRIBUTE_KEY_LUCENE_SYNTAX, Boolean.TRUE);
        searchContext.setAttribute(ATTRIBUTE_KEY_SEARCH_REQUEST_PREFERENCE, "testValue");
        SearchSearchRequest searchSearchRequest = _elasticsearchIndexSearcher.createSearchSearchRequest(searchContext, Mockito.mock(Query.class), 0, 0);
        Assert.assertTrue(searchSearchRequest.isBasicFacetSelection());
        Assert.assertTrue(searchSearchRequest.isLuceneSyntax());
        Assert.assertEquals("testValue", searchSearchRequest.getPreference());
    }

    private final DocumentFixture _documentFixture = new DocumentFixture();

    private ElasticsearchIndexSearcher _elasticsearchIndexSearcher;
}

