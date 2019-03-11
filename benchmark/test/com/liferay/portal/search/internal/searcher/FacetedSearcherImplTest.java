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
package com.liferay.portal.search.internal.searcher;


import SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH;
import StringPool.BLANK;
import StringPool.FOUR_SPACES;
import com.liferay.portal.kernel.search.ExpandoQueryContributor;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelper;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.internal.indexer.PreFilterContributorHelper;
import com.liferay.portal.search.internal.test.util.DocumentFixture;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class FacetedSearcherImplTest {
    @Test
    public void testEmptySearchDisabledBlank() throws Exception {
        SearchContext searchContext = new SearchContext();
        searchContext.setKeywords(BLANK);
        assertSearchSkipped(searchContext);
    }

    @Test
    public void testEmptySearchDisabledByDefault() throws Exception {
        SearchContext searchContext = new SearchContext();
        assertSearchSkipped(searchContext);
    }

    @Test
    public void testEmptySearchDisabledSpaces() throws Exception {
        SearchContext searchContext = new SearchContext();
        searchContext.setKeywords(FOUR_SPACES);
        assertSearchSkipped(searchContext);
    }

    @Test
    public void testEmptySearchEnabled() throws Exception {
        SearchContext searchContext = new SearchContext();
        searchContext.setAttribute(ATTRIBUTE_KEY_EMPTY_SEARCH, Boolean.TRUE);
        searchContext.setEntryClassNames(new String[]{ RandomTestUtil.randomString() });
        Hits hits = facetedSearcher.search(searchContext);
        Assert.assertNull(hits);
        Mockito.verify(indexSearcherHelper).search(Mockito.same(searchContext), Mockito.any());
    }

    @Mock
    protected ExpandoQueryContributor expandoQueryContributor;

    protected FacetedSearcher facetedSearcher;

    @Mock
    protected IndexerRegistry indexerRegistry;

    @Mock
    protected IndexSearcherHelper indexSearcherHelper;

    @Mock
    protected PreFilterContributorHelper preFilterContributorHelper;

    @Mock
    protected SearchEngineHelper searchEngineHelper;

    private DocumentFixture _documentFixture;
}

