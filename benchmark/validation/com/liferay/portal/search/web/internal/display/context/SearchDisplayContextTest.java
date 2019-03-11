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
package com.liferay.portal.search.web.internal.display.context;


import StringPool.BLANK;
import StringPool.DOUBLE_SPACE;
import StringPool.NULL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.legacy.searcher.SearchResponseBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.SearchResponseBuilder;
import com.liferay.portal.search.searcher.Searcher;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class SearchDisplayContextTest {
    @Test
    public void testConfigurationKeywordsEmptySkipsSearch() throws Exception {
        assertSearchSkippedAndNullResults(null, new com.liferay.portlet.portletconfiguration.util.ConfigurationRenderRequest(renderRequest, portletPreferences));
    }

    @Test
    public void testSearchKeywordsBlank() throws Exception {
        assertSearchKeywords(BLANK, BLANK);
    }

    @Test
    public void testSearchKeywordsNullWord() throws Exception {
        assertSearchKeywords(NULL, NULL);
    }

    @Test
    public void testSearchKeywordsSpaces() throws Exception {
        assertSearchKeywords(DOUBLE_SPACE, BLANK);
    }

    @Mock
    protected HttpServletRequest httpServletRequest;

    @Mock
    protected PortletPreferences portletPreferences;

    @Mock
    protected PortletURLFactory portletURLFactory;

    @Mock
    protected RenderRequest renderRequest;

    @Mock
    protected Searcher searcher;

    protected SearchRequestBuilderFactory searchRequestBuilderFactory = new SearchRequestBuilderFactoryImpl();

    @Mock
    protected SearchResponse searchResponse;

    @Mock
    protected SearchResponseBuilder searchResponseBuilder;

    @Mock
    protected SearchResponseBuilderFactory searchResponseBuilderFactory;

    protected ThemeDisplay themeDisplay;
}

