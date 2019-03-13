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
package com.liferay.portal.search.web.internal.facet.display.context;


import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class AssetTagsSearchFacetDisplayContextTest {
    @Test
    public void testEmptySearchResults() throws Exception {
        String facetParam = "";
        AssetTagsSearchFacetDisplayContext assetTagsSearchFacetDisplayContext = createDisplayContext(facetParam);
        List<AssetTagsSearchFacetTermDisplayContext> assetTagsSearchFacetTermDisplayContexts = assetTagsSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(assetTagsSearchFacetTermDisplayContexts.toString(), 0, assetTagsSearchFacetTermDisplayContexts.size());
        Assert.assertEquals(facetParam, assetTagsSearchFacetDisplayContext.getParameterValue());
        Assert.assertTrue(assetTagsSearchFacetDisplayContext.isNothingSelected());
        Assert.assertTrue(assetTagsSearchFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testEmptySearchResultsWithPreviousSelection() throws Exception {
        String term = RandomTestUtil.randomString();
        String facetParam = term;
        AssetTagsSearchFacetDisplayContext assetTagsSearchFacetDisplayContext = createDisplayContext(facetParam);
        List<AssetTagsSearchFacetTermDisplayContext> assetTagsSearchFacetTermDisplayContexts = assetTagsSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(assetTagsSearchFacetTermDisplayContexts.toString(), 1, assetTagsSearchFacetTermDisplayContexts.size());
        AssetTagsSearchFacetTermDisplayContext assetTagsSearchFacetTermDisplayContext = assetTagsSearchFacetTermDisplayContexts.get(0);
        Assert.assertEquals(term, assetTagsSearchFacetTermDisplayContext.getDisplayName());
        Assert.assertEquals(0, assetTagsSearchFacetTermDisplayContext.getFrequency());
        Assert.assertEquals(term, assetTagsSearchFacetTermDisplayContext.getValue());
        Assert.assertTrue(assetTagsSearchFacetTermDisplayContext.isSelected());
        Assert.assertTrue(assetTagsSearchFacetTermDisplayContext.isFrequencyVisible());
        Assert.assertEquals(facetParam, assetTagsSearchFacetDisplayContext.getParameterValue());
        Assert.assertFalse(assetTagsSearchFacetDisplayContext.isNothingSelected());
        Assert.assertFalse(assetTagsSearchFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testOneTerm() throws Exception {
        String term = RandomTestUtil.randomString();
        int frequency = RandomTestUtil.randomInt();
        setUpOneTermCollector(term, frequency);
        String facetParam = StringPool.BLANK;
        AssetTagsSearchFacetDisplayContext assetTagsSearchFacetDisplayContext = createDisplayContext(facetParam);
        List<AssetTagsSearchFacetTermDisplayContext> assetTagsSearchFacetTermDisplayContexts = assetTagsSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(assetTagsSearchFacetTermDisplayContexts.toString(), 1, assetTagsSearchFacetTermDisplayContexts.size());
        AssetTagsSearchFacetTermDisplayContext assetTagsSearchFacetTermDisplayContext = assetTagsSearchFacetTermDisplayContexts.get(0);
        Assert.assertEquals(term, assetTagsSearchFacetTermDisplayContext.getDisplayName());
        Assert.assertEquals(frequency, assetTagsSearchFacetTermDisplayContext.getFrequency());
        Assert.assertEquals(term, assetTagsSearchFacetTermDisplayContext.getValue());
        Assert.assertFalse(assetTagsSearchFacetTermDisplayContext.isSelected());
        Assert.assertTrue(assetTagsSearchFacetTermDisplayContext.isFrequencyVisible());
        Assert.assertEquals(facetParam, assetTagsSearchFacetDisplayContext.getParameterValue());
        Assert.assertTrue(assetTagsSearchFacetDisplayContext.isNothingSelected());
        Assert.assertFalse(assetTagsSearchFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testOneTermWithPreviousSelection() throws Exception {
        String term = RandomTestUtil.randomString();
        int frequency = RandomTestUtil.randomInt();
        setUpOneTermCollector(term, frequency);
        String facetParam = term;
        AssetTagsSearchFacetDisplayContext assetTagsSearchFacetDisplayContext = createDisplayContext(facetParam);
        List<AssetTagsSearchFacetTermDisplayContext> assetTagsSearchFacetTermDisplayContexts = assetTagsSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(assetTagsSearchFacetTermDisplayContexts.toString(), 1, assetTagsSearchFacetTermDisplayContexts.size());
        AssetTagsSearchFacetTermDisplayContext assetTagsSearchFacetTermDisplayContext = assetTagsSearchFacetTermDisplayContexts.get(0);
        Assert.assertEquals(term, assetTagsSearchFacetTermDisplayContext.getDisplayName());
        Assert.assertEquals(frequency, assetTagsSearchFacetTermDisplayContext.getFrequency());
        Assert.assertEquals(term, assetTagsSearchFacetTermDisplayContext.getValue());
        Assert.assertTrue(assetTagsSearchFacetTermDisplayContext.isSelected());
        Assert.assertTrue(assetTagsSearchFacetTermDisplayContext.isFrequencyVisible());
        Assert.assertEquals(facetParam, assetTagsSearchFacetDisplayContext.getParameterValue());
        Assert.assertFalse(assetTagsSearchFacetDisplayContext.isNothingSelected());
        Assert.assertFalse(assetTagsSearchFacetDisplayContext.isRenderNothing());
    }

    @Mock
    private Facet _facet;

    @Mock
    private FacetCollector _facetCollector;
}

