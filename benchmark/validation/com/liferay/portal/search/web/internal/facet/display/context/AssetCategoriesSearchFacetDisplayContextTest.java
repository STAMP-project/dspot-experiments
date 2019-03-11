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


import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.web.internal.facet.display.builder.AssetCategoryPermissionChecker;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class AssetCategoriesSearchFacetDisplayContextTest {
    @Test
    public void testEmptySearchResults() throws Exception {
        String facetParam = StringPool.BLANK;
        AssetCategoriesSearchFacetDisplayContext assetCategoriesSearchFacetDisplayContext = createDisplayContext(facetParam);
        List<AssetCategoriesSearchFacetTermDisplayContext> assetCategoriesSearchFacetTermDisplayContexts = assetCategoriesSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(assetCategoriesSearchFacetTermDisplayContexts.toString(), 0, assetCategoriesSearchFacetTermDisplayContexts.size());
        Assert.assertEquals(facetParam, assetCategoriesSearchFacetDisplayContext.getParameterValue());
        Assert.assertTrue(assetCategoriesSearchFacetDisplayContext.isNothingSelected());
        Assert.assertTrue(assetCategoriesSearchFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testEmptySearchResultsWithPreviousSelection() throws Exception {
        long assetCategoryId = RandomTestUtil.randomLong();
        setUpAssetCategory(assetCategoryId);
        String facetParam = String.valueOf(assetCategoryId);
        AssetCategoriesSearchFacetDisplayContext assetCategoriesSearchFacetDisplayContext = createDisplayContext(facetParam);
        List<AssetCategoriesSearchFacetTermDisplayContext> assetCategoriesSearchFacetTermDisplayContexts = assetCategoriesSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(assetCategoriesSearchFacetTermDisplayContexts.toString(), 1, assetCategoriesSearchFacetTermDisplayContexts.size());
        AssetCategoriesSearchFacetTermDisplayContext assetCategoriesSearchFacetTermDisplayContext = assetCategoriesSearchFacetTermDisplayContexts.get(0);
        Assert.assertEquals(assetCategoryId, assetCategoriesSearchFacetTermDisplayContext.getAssetCategoryId());
        Assert.assertEquals(String.valueOf(assetCategoryId), assetCategoriesSearchFacetTermDisplayContext.getDisplayName());
        Assert.assertEquals(0, assetCategoriesSearchFacetTermDisplayContext.getFrequency());
        Assert.assertTrue(assetCategoriesSearchFacetTermDisplayContext.isFrequencyVisible());
        Assert.assertTrue(assetCategoriesSearchFacetTermDisplayContext.isSelected());
        Assert.assertEquals(facetParam, assetCategoriesSearchFacetDisplayContext.getParameterValue());
        Assert.assertFalse(assetCategoriesSearchFacetDisplayContext.isNothingSelected());
        Assert.assertFalse(assetCategoriesSearchFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testOneTerm() throws Exception {
        long assetCategoryId = RandomTestUtil.randomLong();
        setUpAssetCategory(assetCategoryId);
        int frequency = RandomTestUtil.randomInt();
        setUpOneTermCollector(assetCategoryId, frequency);
        String facetParam = StringPool.BLANK;
        AssetCategoriesSearchFacetDisplayContext assetCategoriesSearchFacetDisplayContext = createDisplayContext(facetParam);
        List<AssetCategoriesSearchFacetTermDisplayContext> assetCategoriesSearchFacetTermDisplayContexts = assetCategoriesSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(assetCategoriesSearchFacetTermDisplayContexts.toString(), 1, assetCategoriesSearchFacetTermDisplayContexts.size());
        AssetCategoriesSearchFacetTermDisplayContext assetCategoriesSearchFacetTermDisplayContext = assetCategoriesSearchFacetTermDisplayContexts.get(0);
        Assert.assertEquals(assetCategoryId, assetCategoriesSearchFacetTermDisplayContext.getAssetCategoryId());
        Assert.assertEquals(String.valueOf(assetCategoryId), assetCategoriesSearchFacetTermDisplayContext.getDisplayName());
        Assert.assertEquals(frequency, assetCategoriesSearchFacetTermDisplayContext.getFrequency());
        Assert.assertTrue(assetCategoriesSearchFacetTermDisplayContext.isFrequencyVisible());
        Assert.assertFalse(assetCategoriesSearchFacetTermDisplayContext.isSelected());
        Assert.assertEquals(facetParam, assetCategoriesSearchFacetDisplayContext.getParameterValue());
        Assert.assertTrue(assetCategoriesSearchFacetDisplayContext.isNothingSelected());
        Assert.assertFalse(assetCategoriesSearchFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testOneTermWithPreviousSelection() throws Exception {
        long assetCategoryId = RandomTestUtil.randomLong();
        setUpAssetCategory(assetCategoryId);
        int frequency = RandomTestUtil.randomInt();
        setUpOneTermCollector(assetCategoryId, frequency);
        AssetCategoriesSearchFacetDisplayContext assetCategoriesSearchFacetDisplayContext = createDisplayContext(String.valueOf(assetCategoryId));
        List<AssetCategoriesSearchFacetTermDisplayContext> assetCategoriesSearchFacetTermDisplayContexts = assetCategoriesSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(assetCategoriesSearchFacetTermDisplayContexts.toString(), 1, assetCategoriesSearchFacetTermDisplayContexts.size());
        AssetCategoriesSearchFacetTermDisplayContext assetCategoriesSearchFacetTermDisplayContext = assetCategoriesSearchFacetTermDisplayContexts.get(0);
        Assert.assertEquals(assetCategoryId, assetCategoriesSearchFacetTermDisplayContext.getAssetCategoryId());
        Assert.assertEquals(String.valueOf(assetCategoryId), assetCategoriesSearchFacetTermDisplayContext.getDisplayName());
        Assert.assertEquals(frequency, assetCategoriesSearchFacetTermDisplayContext.getFrequency());
        Assert.assertTrue(assetCategoriesSearchFacetTermDisplayContext.isFrequencyVisible());
        Assert.assertTrue(assetCategoriesSearchFacetTermDisplayContext.isSelected());
        Assert.assertEquals(assetCategoryId, GetterUtil.getLong(assetCategoriesSearchFacetDisplayContext.getParameterValue()));
        Assert.assertFalse(assetCategoriesSearchFacetDisplayContext.isNothingSelected());
        Assert.assertFalse(assetCategoriesSearchFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testUnauthorized() throws Exception {
        long assetCategoryId = RandomTestUtil.randomLong();
        setUpAssetCategoryUnauthorized(assetCategoryId);
        int frequency = RandomTestUtil.randomInt();
        setUpOneTermCollector(assetCategoryId, frequency);
        String facetParam = StringPool.BLANK;
        AssetCategoriesSearchFacetDisplayContext assetCategoriesSearchFacetDisplayContext = createDisplayContext(facetParam);
        List<AssetCategoriesSearchFacetTermDisplayContext> assetCategoriesSearchFacetTermDisplayContexts = assetCategoriesSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(assetCategoriesSearchFacetTermDisplayContexts.toString(), 0, assetCategoriesSearchFacetTermDisplayContexts.size());
        Assert.assertEquals(facetParam, assetCategoriesSearchFacetDisplayContext.getParameterValue());
        Assert.assertTrue(assetCategoriesSearchFacetDisplayContext.isNothingSelected());
        Assert.assertTrue(assetCategoriesSearchFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testUnauthorizedWithPreviousSelection() throws Exception {
        long assetCategoryId = RandomTestUtil.randomLong();
        setUpAssetCategoryUnauthorized(assetCategoryId);
        String facetParam = String.valueOf(assetCategoryId);
        AssetCategoriesSearchFacetDisplayContext assetCategoriesSearchFacetDisplayContext = createDisplayContext(facetParam);
        List<AssetCategoriesSearchFacetTermDisplayContext> assetCategoriesSearchFacetTermDisplayContexts = assetCategoriesSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(assetCategoriesSearchFacetTermDisplayContexts.toString(), 0, assetCategoriesSearchFacetTermDisplayContexts.size());
        Assert.assertEquals(facetParam, assetCategoriesSearchFacetDisplayContext.getParameterValue());
        Assert.assertFalse(assetCategoriesSearchFacetDisplayContext.isNothingSelected());
        Assert.assertFalse(assetCategoriesSearchFacetDisplayContext.isRenderNothing());
    }

    @Mock
    private AssetCategoryLocalService _assetCategoryLocalService;

    @Mock
    private AssetCategoryPermissionChecker _assetCategoryPermissionChecker;

    @Mock
    private Facet _facet;

    @Mock
    private FacetCollector _facetCollector;
}

