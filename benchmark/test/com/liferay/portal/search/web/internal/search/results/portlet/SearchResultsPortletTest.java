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
package com.liferay.portal.search.web.internal.search.results.portlet;


import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactory;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.request.SearchSettings;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class SearchResultsPortletTest {
    @Test
    public void testDocumentWithoutSummaryIsRemoved() throws Exception {
        Document document = createDocumentWithSummary();
        setUpSearchResponseDocuments(document, new DocumentImpl());
        render();
        assertDisplayContextDocuments(document);
    }

    @Mock
    private IndexerRegistry _indexerRegistry;

    @Mock
    private PortletSharedSearchResponse _portletSharedSearchResponse;

    private PortletURLFactory _portletURLFactory;

    private RenderRequest _renderRequest;

    @Mock
    private RenderResponse _renderResponse;

    @Mock
    private SearchContext _searchContext;

    @Mock
    private SearchResponse _searchResponse;

    private SearchResultsPortlet _searchResultsPortlet;

    @Mock
    private SearchSettings _searchSettings;
}

