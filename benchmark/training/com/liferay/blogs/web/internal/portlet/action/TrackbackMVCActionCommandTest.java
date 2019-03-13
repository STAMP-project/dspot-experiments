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
package com.liferay.blogs.web.internal.portlet.action;


import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.web.internal.trackback.Trackback;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import java.util.function.Function;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author Andr? de Oliveira
 */
@PrepareForTest(ActionUtil.class)
@RunWith(PowerMockRunner.class)
public class TrackbackMVCActionCommandTest extends PowerMockito {
    @Test
    public void testDisabledComments() throws Exception {
        whenGetEntryThenReturn(_blogsEntry);
        when(_portletPreferences.getValue("enableComments", null)).thenReturn("false");
        addTrackback();
        assertError("Comments are disabled");
    }

    @Test
    public void testMismatchedIPAddress() throws Exception {
        whenGetEntryThenReturn(_blogsEntry);
        initURL("123");
        addTrackback();
        assertError("Remote IP does not match the trackback URL's IP");
    }

    @Test
    public void testMissingURL() throws Exception {
        whenGetEntryThenReturn(_blogsEntry);
        addTrackback();
        assertError("Trackback requires a valid permanent URL");
    }

    @Test(expected = NoSuchEntryException.class)
    public void testNoSuchEntryException() throws Exception {
        whenGetEntryThenThrow(new NoSuchEntryException());
        initValidURL();
        addTrackback();
    }

    @Test
    public void testPrincipalException() throws Exception {
        whenGetEntryThenThrow(new PrincipalException());
        initValidURL();
        addTrackback();
        assertError("Blog entry must have guest view permissions to enable trackbacks");
    }

    @Test
    public void testSuccess() throws Exception {
        whenGetEntryThenReturn(_blogsEntry);
        _mockOriginalServletRequest.setParameter("blog_name", "__blogName__");
        _mockOriginalServletRequest.setParameter("excerpt", "__excerpt__");
        _mockOriginalServletRequest.setParameter("title", "__title__");
        initValidURL();
        addTrackback();
        assertSuccess();
        Mockito.verify(_trackback).addTrackback(Matchers.same(_blogsEntry), Matchers.same(_themeDisplay), Matchers.eq("__excerpt__"), Matchers.eq("__url__"), Matchers.eq("__blogName__"), Matchers.eq("__title__"), ((Function<String, ServiceContext>) (Matchers.any())));
    }

    @Test
    public void testTrackbacksNotEnabled() throws Exception {
        whenGetEntryThenReturn(_blogsEntry);
        when(_blogsEntry.isAllowTrackbacks()).thenReturn(false);
        initValidURL();
        addTrackback();
        assertError("Trackbacks are not enabled");
    }

    @Mock
    private ActionRequest _actionRequest;

    @Mock
    private ActionResponse _actionResponse;

    @Mock
    private BlogsEntry _blogsEntry;

    @Mock
    private Http _http;

    private final MockHttpServletRequest _mockHttpServletRequest = new MockHttpServletRequest();

    private final MockHttpServletResponse _mockHttpServletResponse = new MockHttpServletResponse();

    private final MockHttpServletRequest _mockOriginalServletRequest = new MockHttpServletRequest();

    @Mock
    private PortletPreferences _portletPreferences;

    private final ThemeDisplay _themeDisplay = new ThemeDisplay();

    @Mock
    private Trackback _trackback;
}

