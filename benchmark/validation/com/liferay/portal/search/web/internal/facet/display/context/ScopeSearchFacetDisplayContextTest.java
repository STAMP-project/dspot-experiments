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


import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.service.GroupLocalService;
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
public class ScopeSearchFacetDisplayContextTest {
    @Test
    public void testEmptySearchResults() throws Exception {
        String parameterValue = "0";
        ScopeSearchFacetDisplayContext scopeSearchFacetDisplayContext = createDisplayContext(parameterValue);
        List<ScopeSearchFacetTermDisplayContext> scopeSearchFacetTermDisplayContexts = scopeSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(scopeSearchFacetTermDisplayContexts.toString(), 0, scopeSearchFacetTermDisplayContexts.size());
        Assert.assertEquals(parameterValue, scopeSearchFacetDisplayContext.getParameterValue());
        Assert.assertTrue(scopeSearchFacetDisplayContext.isNothingSelected());
        Assert.assertTrue(scopeSearchFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testEmptySearchResultsWithPreviousSelection() throws Exception {
        long groupId = RandomTestUtil.randomLong();
        String name = RandomTestUtil.randomString();
        addGroup(groupId, name);
        String parameterValue = String.valueOf(groupId);
        ScopeSearchFacetDisplayContext scopeSearchFacetDisplayContext = createDisplayContext(parameterValue);
        List<ScopeSearchFacetTermDisplayContext> scopeSearchFacetTermDisplayContexts = scopeSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(scopeSearchFacetTermDisplayContexts.toString(), 1, scopeSearchFacetTermDisplayContexts.size());
        ScopeSearchFacetTermDisplayContext scopeSearchFacetTermDisplayContext = scopeSearchFacetTermDisplayContexts.get(0);
        Assert.assertEquals(0, scopeSearchFacetTermDisplayContext.getCount());
        Assert.assertEquals(name, scopeSearchFacetTermDisplayContext.getDescriptiveName());
        Assert.assertEquals(groupId, scopeSearchFacetTermDisplayContext.getGroupId());
        Assert.assertTrue(scopeSearchFacetTermDisplayContext.isSelected());
        Assert.assertTrue(scopeSearchFacetTermDisplayContext.isShowCount());
        Assert.assertEquals(parameterValue, scopeSearchFacetDisplayContext.getParameterValue());
        Assert.assertFalse(scopeSearchFacetDisplayContext.isNothingSelected());
        Assert.assertFalse(scopeSearchFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testOneTerm() throws Exception {
        long groupId = RandomTestUtil.randomLong();
        String name = RandomTestUtil.randomString();
        addGroup(groupId, name);
        int count = RandomTestUtil.randomInt();
        setUpOneTermCollector(groupId, count);
        String parameterValue = "0";
        ScopeSearchFacetDisplayContext scopeSearchFacetDisplayContext = createDisplayContext(parameterValue);
        List<ScopeSearchFacetTermDisplayContext> scopeSearchFacetTermDisplayContexts = scopeSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(scopeSearchFacetTermDisplayContexts.toString(), 1, scopeSearchFacetTermDisplayContexts.size());
        ScopeSearchFacetTermDisplayContext scopeSearchFacetTermDisplayContext = scopeSearchFacetTermDisplayContexts.get(0);
        Assert.assertEquals(count, scopeSearchFacetTermDisplayContext.getCount());
        Assert.assertEquals(name, scopeSearchFacetTermDisplayContext.getDescriptiveName());
        Assert.assertEquals(groupId, scopeSearchFacetTermDisplayContext.getGroupId());
        Assert.assertFalse(scopeSearchFacetTermDisplayContext.isSelected());
        Assert.assertTrue(scopeSearchFacetTermDisplayContext.isShowCount());
        Assert.assertEquals(parameterValue, scopeSearchFacetDisplayContext.getParameterValue());
        Assert.assertTrue(scopeSearchFacetDisplayContext.isNothingSelected());
        Assert.assertFalse(scopeSearchFacetDisplayContext.isRenderNothing());
    }

    @Test
    public void testOneTermWithPreviousSelection() throws Exception {
        long groupId = RandomTestUtil.randomLong();
        String name = RandomTestUtil.randomString();
        addGroup(groupId, name);
        int count = RandomTestUtil.randomInt();
        setUpOneTermCollector(groupId, count);
        String parameterValue = String.valueOf(groupId);
        ScopeSearchFacetDisplayContext scopeSearchFacetDisplayContext = createDisplayContext(parameterValue);
        List<ScopeSearchFacetTermDisplayContext> scopeSearchFacetTermDisplayContexts = scopeSearchFacetDisplayContext.getTermDisplayContexts();
        Assert.assertEquals(scopeSearchFacetTermDisplayContexts.toString(), 1, scopeSearchFacetTermDisplayContexts.size());
        ScopeSearchFacetTermDisplayContext scopeSearchFacetTermDisplayContext = scopeSearchFacetTermDisplayContexts.get(0);
        Assert.assertEquals(count, scopeSearchFacetTermDisplayContext.getCount());
        Assert.assertEquals(name, scopeSearchFacetTermDisplayContext.getDescriptiveName());
        Assert.assertEquals(groupId, scopeSearchFacetTermDisplayContext.getGroupId());
        Assert.assertTrue(scopeSearchFacetTermDisplayContext.isSelected());
        Assert.assertTrue(scopeSearchFacetTermDisplayContext.isShowCount());
        Assert.assertEquals(parameterValue, scopeSearchFacetDisplayContext.getParameterValue());
        Assert.assertFalse(scopeSearchFacetDisplayContext.isNothingSelected());
        Assert.assertFalse(scopeSearchFacetDisplayContext.isRenderNothing());
    }

    @Mock
    private Facet _facet;

    @Mock
    private FacetCollector _facetCollector;

    @Mock
    private GroupLocalService _groupLocalService;
}

