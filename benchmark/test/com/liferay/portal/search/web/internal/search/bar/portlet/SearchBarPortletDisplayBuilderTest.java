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
package com.liferay.portal.search.web.internal.search.bar.portlet;


import SearchScope.EVERYTHING;
import StringPool.BLANK;
import StringPool.SLASH;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Adam Brandizzi
 */
public class SearchBarPortletDisplayBuilderTest {
    @Test
    public void testDestinationBlank() {
        SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder = createSearchBarPortletDisplayBuilder();
        searchBarPortletDisplayBuilder.setDestination(BLANK);
        SearchBarPortletDisplayContext searchBarPortletDisplayContext = searchBarPortletDisplayBuilder.build();
        Assert.assertFalse(searchBarPortletDisplayContext.isDestinationUnreachable());
    }

    @Test
    public void testDestinationNull() {
        SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder = createSearchBarPortletDisplayBuilder();
        searchBarPortletDisplayBuilder.setDestination(null);
        SearchBarPortletDisplayContext searchBarPortletDisplayContext = searchBarPortletDisplayBuilder.build();
        Assert.assertFalse(searchBarPortletDisplayContext.isDestinationUnreachable());
    }

    @Test
    public void testDestinationUnreachable() {
        String destination = RandomTestUtil.randomString();
        whenLayoutLocalServiceFetchLayoutByFriendlyURL(destination, null);
        SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder = createSearchBarPortletDisplayBuilder();
        searchBarPortletDisplayBuilder.setDestination(destination);
        SearchBarPortletDisplayContext searchBarPortletDisplayContext = searchBarPortletDisplayBuilder.build();
        Assert.assertTrue(searchBarPortletDisplayContext.isDestinationUnreachable());
    }

    @Test
    public void testDestinationWithLeadingSlash() throws Exception {
        String destination = RandomTestUtil.randomString();
        Layout layout = Mockito.mock(Layout.class);
        whenLayoutLocalServiceFetchLayoutByFriendlyURL(destination, layout);
        String layoutFriendlyURL = RandomTestUtil.randomString();
        whenPortalGetLayoutFriendlyURL(layout, layoutFriendlyURL);
        SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder = createSearchBarPortletDisplayBuilder();
        searchBarPortletDisplayBuilder.setDestination(SLASH.concat(destination));
        SearchBarPortletDisplayContext searchBarPortletDisplayContext = searchBarPortletDisplayBuilder.build();
        Assert.assertEquals(layoutFriendlyURL, searchBarPortletDisplayContext.getSearchURL());
        Assert.assertFalse(searchBarPortletDisplayContext.isDestinationUnreachable());
    }

    @Test
    public void testDestinationWithoutLeadingSlash() throws Exception {
        String destination = RandomTestUtil.randomString();
        Layout layout = Mockito.mock(Layout.class);
        whenLayoutLocalServiceFetchLayoutByFriendlyURL(destination, layout);
        String layoutFriendlyURL = RandomTestUtil.randomString();
        whenPortalGetLayoutFriendlyURL(layout, layoutFriendlyURL);
        SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder = createSearchBarPortletDisplayBuilder();
        searchBarPortletDisplayBuilder.setDestination(destination);
        SearchBarPortletDisplayContext searchBarPortletDisplayContext = searchBarPortletDisplayBuilder.build();
        Assert.assertEquals(layoutFriendlyURL, searchBarPortletDisplayContext.getSearchURL());
        Assert.assertFalse(searchBarPortletDisplayContext.isDestinationUnreachable());
    }

    @Test
    public void testSamePageNoDestination() {
        Mockito.doReturn("http://example.com/web/guest/home?param=arg").when(_themeDisplay).getURLCurrent();
        SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder = createSearchBarPortletDisplayBuilder();
        SearchBarPortletDisplayContext searchBarPortletDisplayContext = searchBarPortletDisplayBuilder.build();
        Assert.assertFalse(searchBarPortletDisplayContext.isDestinationUnreachable());
        Assert.assertEquals("/web/guest/home", searchBarPortletDisplayContext.getSearchURL());
    }

    @Test
    public void testSearchScope() {
        SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder = createSearchBarPortletDisplayBuilder();
        searchBarPortletDisplayBuilder.setScopeParameterValue(EVERYTHING.getParameterString());
        Assert.assertEquals(EVERYTHING, searchBarPortletDisplayBuilder.getSearchScope());
    }

    @Mock
    private Group _group;

    @Mock
    private Http _http;

    @Mock
    private LayoutLocalService _layoutLocalService;

    @Mock
    private Portal _portal;

    @Mock
    private ThemeDisplay _themeDisplay;
}

