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
package com.liferay.portal.search.web.internal.result.display.builder;


import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultContentDisplayContext;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Wade Cao
 * @author Andr? de Oliveira
 */
public class SearchResultContentDisplayBuilderTest {
    @Test
    public void testEditPermission() throws Exception {
        String title = RandomTestUtil.randomString();
        Mockito.doReturn(title).when(_assetRenderer).getTitle(Mockito.anyObject());
        String editPortletURLString = RandomTestUtil.randomString();
        Mockito.doReturn(editPortletURLString).when(_editPortletURL).toString();
        SearchResultContentDisplayContext searchResultContentDisplayContext = buildDisplayContext();
        Assert.assertTrue(searchResultContentDisplayContext.hasEditPermission());
        assertIcon(title, editPortletURLString, searchResultContentDisplayContext);
    }

    @Test
    public void testEditPermissionFalse() throws Exception {
        Mockito.doReturn(false).when(_assetRenderer).hasEditPermission(Mockito.anyObject());
        SearchResultContentDisplayContext searchResultContentDisplayContext = buildDisplayContext();
        Assert.assertFalse(searchResultContentDisplayContext.hasEditPermission());
        assertIconMissing(searchResultContentDisplayContext);
        assertAssetDisplay(searchResultContentDisplayContext);
    }

    @Test
    public void testVisible() throws Exception {
        SearchResultContentDisplayContext searchResultContentDisplayContext = buildDisplayContext();
        Assert.assertTrue(searchResultContentDisplayContext.isVisible());
        assertAssetDisplay(searchResultContentDisplayContext);
    }

    @Test
    public void testVisibleFalseFromEntry() throws Exception {
        Mockito.doReturn(false).when(_assetEntry).isVisible();
        SearchResultContentDisplayContext searchResultContentDisplayContext = buildDisplayContext();
        Assert.assertFalse(searchResultContentDisplayContext.isVisible());
    }

    @Test
    public void testVisibleFalseFromViewPermission() throws Exception {
        Mockito.doReturn(false).when(_assetRenderer).hasViewPermission(Mockito.anyObject());
        SearchResultContentDisplayContext searchResultContentDisplayContext = buildDisplayContext();
        Assert.assertFalse(searchResultContentDisplayContext.isVisible());
    }

    @Mock
    private AssetEntry _assetEntry;

    @Mock
    private AssetRenderer<?> _assetRenderer;

    @Mock
    @SuppressWarnings("rawtypes")
    private AssetRendererFactory _assetRendererFactory;

    @Mock
    private AssetRendererFactoryLookup _assetRendererFactoryLookup;

    @Mock
    private PortletURL _editPortletURL;

    @Mock
    private PermissionChecker _permissionChecker;

    @Mock
    private Portal _portal;

    @Mock
    private PortletURL _renderPortletURL;

    @Mock
    private RenderRequest _renderRequest;

    @Mock
    private RenderResponse _renderResponse;
}

