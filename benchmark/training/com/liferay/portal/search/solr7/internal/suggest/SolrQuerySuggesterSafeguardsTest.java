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
package com.liferay.portal.search.solr7.internal.suggest;


import com.liferay.portal.search.solr7.internal.SolrQuerySuggester;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class SolrQuerySuggesterSafeguardsTest {
    @Test
    public void testErrorReturnsEmptyResults() throws Exception {
        SolrQuerySuggester solrQuerySuggester = createSolrQuerySuggester();
        String[] querySuggestions = solrQuerySuggester.suggestKeywordQueries(createSearchContext(), 0);
        Assert.assertEquals(Arrays.toString(querySuggestions), 0, querySuggestions.length);
    }
}

