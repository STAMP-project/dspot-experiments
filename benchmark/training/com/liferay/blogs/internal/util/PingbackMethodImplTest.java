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
package com.liferay.blogs.internal.util;


import BlogsPortletKeys.BLOGS;
import PingbackMethodImpl.ACCESS_DENIED;
import PingbackMethodImpl.PINGBACK_ALREADY_REGISTERED;
import PingbackMethodImpl.SOURCE_URI_DOES_NOT_EXIST;
import PingbackMethodImpl.SOURCE_URI_INVALID;
import PingbackMethodImpl.TARGET_URI_INVALID;
import XmlRpcConstants.REQUESTED_METHOD_NOT_FOUND;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.blogs.web.internal.util.BlogsUtil;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DuplicateCommentException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFunction;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.xmlrpc.XmlRpc;
import com.liferay.portal.util.PropsValues;
import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerMap;
import java.io.IOException;
import java.net.InetAddress;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


/**
 *
 *
 * @author Andr? de Oliveira
 */
@PrepareForTest({ BlogsEntryLocalServiceUtil.class, BlogsUtil.class, InetAddressUtil.class, PingbackMethodImpl.class, PortletLocalServiceUtil.class, PortletProviderUtil.class, PropsValues.class, ServiceTrackerCollections.class, UserLocalServiceUtil.class })
@RunWith(PowerMockRunner.class)
public class PingbackMethodImplTest extends PowerMockito {
    @Test
    public void testAddPingbackWhenBlogEntryDisablesPingbacks() throws Exception {
        when(_blogsEntry.isAllowPingbacks()).thenReturn(false);
        execute();
        verifyFault(REQUESTED_METHOD_NOT_FOUND, "Pingbacks are disabled");
    }

    @Test
    public void testAddPingbackWhenPortalPropertyDisablesPingbacks() throws Exception {
        boolean previous = PropsValues.BLOGS_PINGBACK_ENABLED;
        Whitebox.setInternalState(PropsValues.class, "BLOGS_PINGBACK_ENABLED", false);
        try {
            execute();
            verifyFault(REQUESTED_METHOD_NOT_FOUND, "Pingbacks are disabled");
        } finally {
            Whitebox.setInternalState(PropsValues.class, "BLOGS_PINGBACK_ENABLED", previous);
        }
    }

    @Test
    public void testAddPingbackWithFriendlyURL() throws Exception {
        long plid = RandomTestUtil.randomLong();
        String friendlyURL = RandomTestUtil.randomString();
        when(_portal.getPlidFromFriendlyURL(PingbackMethodImplTest._COMPANY_ID, ("/" + friendlyURL))).thenReturn(plid);
        long groupId = RandomTestUtil.randomLong();
        when(_portal.getScopeGroupId(plid)).thenReturn(groupId);
        String friendlyURLPath = RandomTestUtil.randomString();
        whenFriendlyURLMapperPopulateParams(("/" + friendlyURLPath), "urlTitle", PingbackMethodImplTest._URL_TITLE);
        String targetURI = (((("http://" + (RandomTestUtil.randomString())) + "/") + friendlyURL) + "/-/") + friendlyURLPath;
        whenHttpURLToString((((("<body><a href='" + targetURI) + "'>") + (RandomTestUtil.randomString())) + "</a></body>"));
        execute(targetURI);
        verifySuccess();
        Mockito.verify(_blogsEntryLocalService).getEntry(groupId, PingbackMethodImplTest._URL_TITLE);
    }

    @Test
    public void testAddPingbackWithFriendlyURLParameterEntryId() throws Exception {
        testAddPingbackWithFriendlyURLParameterEntryId(null);
    }

    @Test
    public void testAddPingbackWithFriendlyURLParameterEntryIdInNamespace() throws Exception {
        String namespace = (RandomTestUtil.randomString()) + ".";
        when(_portal.getPortletNamespace(BLOGS)).thenReturn(namespace);
        testAddPingbackWithFriendlyURLParameterEntryId(namespace);
    }

    @Test
    public void testBuildServiceContext() throws Exception {
        PingbackMethodImpl pingbackMethodImpl = getPingbackMethodImpl();
        ServiceContext serviceContext = pingbackMethodImpl.buildServiceContext(PingbackMethodImplTest._COMPANY_ID, PingbackMethodImplTest._GROUP_ID, PingbackMethodImplTest._URL_TITLE);
        Assert.assertEquals(PingbackMethodImplTest._PINGBACK_USER_NAME, serviceContext.getAttribute("pingbackUserName"));
        Assert.assertEquals((((((PingbackMethodImplTest._LAYOUT_FULL_URL) + "/-/") + (PingbackMethodImplTest._FRIENDLY_URL_MAPPING)) + "/") + (PingbackMethodImplTest._URL_TITLE)), serviceContext.getAttribute("redirect"));
        Assert.assertEquals(PingbackMethodImplTest._LAYOUT_FULL_URL, serviceContext.getLayoutFullURL());
    }

    @Test
    public void testConvertDuplicateCommentExceptionToXmlRpcFault() throws Exception {
        Mockito.doThrow(DuplicateCommentException.class).when(_commentManager).addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyString(), Mockito.<ServiceContextFunction>any());
        execute();
        verifyFault(PINGBACK_ALREADY_REGISTERED, "Pingback is already registered: null");
    }

    @Test
    public void testExecuteWithSuccess() throws Exception {
        execute();
        verifySuccess();
        Mockito.verify(_commentManager).addComment(Matchers.eq(PingbackMethodImplTest._USER_ID), Matchers.eq(PingbackMethodImplTest._GROUP_ID), Matchers.eq(BlogsEntry.class.getName()), Matchers.eq(PingbackMethodImplTest._ENTRY_ID), Matchers.eq((((((("[...] " + (PingbackMethodImplTest._EXCERPT_BODY)) + " [...] <a href=") + (PingbackMethodImplTest._SOURCE_URI)) + ">") + (PingbackMethodImplTest._READ_MORE)) + "</a>")), Mockito.<ServiceContextFunction>any());
    }

    @Test
    public void testGetExcerpt() throws Exception {
        int previous = PropsValues.BLOGS_LINKBACK_EXCERPT_LENGTH;
        Whitebox.setInternalState(PropsValues.class, "BLOGS_LINKBACK_EXCERPT_LENGTH", 4);
        try {
            whenHttpURLToString((("<body><a href='http://" + (PingbackMethodImplTest._TARGET_URI)) + "'>12345</a></body>"));
            execute();
            verifyExcerpt("1...");
        } finally {
            Whitebox.setInternalState(PropsValues.class, "BLOGS_LINKBACK_EXCERPT_LENGTH", previous);
        }
    }

    @Test
    public void testGetExcerptWhenAnchorHasParent() throws Exception {
        whenHttpURLToString(((("<body><p>Visit <a href='http://" + (PingbackMethodImplTest._TARGET_URI)) + "'>Liferay</a> ") + "to learn more</p></body>"));
        execute();
        verifyExcerpt("Visit Liferay to learn more");
    }

    @Test
    public void testGetExcerptWhenAnchorHasTwoParents() throws Exception {
        int previous = PropsValues.BLOGS_LINKBACK_EXCERPT_LENGTH;
        Whitebox.setInternalState(PropsValues.class, "BLOGS_LINKBACK_EXCERPT_LENGTH", 18);
        try {
            whenHttpURLToString((("<body>_____<p>12345<span>67890<a href='http://" + (PingbackMethodImplTest._TARGET_URI)) + "'>Liferay</a>12345</span>67890</p>_____</body>"));
            execute();
            verifyExcerpt("1234567890Lifer...");
        } finally {
            Whitebox.setInternalState(PropsValues.class, "BLOGS_LINKBACK_EXCERPT_LENGTH", previous);
        }
    }

    @Test
    public void testGetExcerptWhenAnchorIsMalformed() throws Exception {
        whenHttpURLToString("<a href='MALFORMED' />");
        execute("MALFORMED");
        verifyFault(TARGET_URI_INVALID, "Unable to parse target URI");
    }

    @Test
    public void testGetExcerptWhenAnchorIsMissing() throws Exception {
        whenHttpURLToString("");
        execute();
        verifyFault(SOURCE_URI_INVALID, "Unable to find target URI in source");
    }

    @Test
    public void testGetExcerptWhenReferrerIsUnavailable() throws Exception {
        when(_http.URLtoString(PingbackMethodImplTest._SOURCE_URI)).thenThrow(IOException.class);
        execute();
        verifyFault(SOURCE_URI_DOES_NOT_EXIST, "Error accessing source URI");
    }

    @Test
    public void testLocalNetworkSSRF() throws Exception {
        for (int i = 0; i < (_localAddresses.length); i++) {
            InetAddress inetAddress = _localAddresses[i];
            String sourceURL = "http://" + (inetAddress.getHostAddress());
            when(_http.URLtoString(sourceURL)).thenReturn((((("<body><a href='http://" + (PingbackMethodImplTest._TARGET_URI)) + "'>") + (PingbackMethodImplTest._EXCERPT_BODY)) + "</a></body>"));
            PingbackMethodImpl pingbackMethodImpl = getPingbackMethodImpl();
            pingbackMethodImpl.setArguments(new Object[]{ sourceURL, "http://" + (PingbackMethodImplTest._TARGET_URI) });
            pingbackMethodImpl.execute(PingbackMethodImplTest._COMPANY_ID);
            Mockito.verify(_xmlRpc, Mockito.times((i + 1))).createFault(ACCESS_DENIED, "Access Denied");
        }
    }

    private static final long _COMPANY_ID = RandomTestUtil.randomLong();

    private static final long _ENTRY_ID = RandomTestUtil.randomLong();

    private static final String _EXCERPT_BODY = RandomTestUtil.randomString();

    private static final String _FRIENDLY_URL_MAPPING = RandomTestUtil.randomString();

    private static final long _GROUP_ID = RandomTestUtil.randomLong();

    private static final String _LAYOUT_FULL_URL = RandomTestUtil.randomString();

    private static final String _PINGBACK_USER_NAME = RandomTestUtil.randomString();

    private static final String _READ_MORE = RandomTestUtil.randomString();

    private static final String _SOURCE_URI = "http://" + (RandomTestUtil.randomString());

    private static final String _TARGET_URI = RandomTestUtil.randomString();

    private static final String _URL_TITLE = RandomTestUtil.randomString();

    private static final long _USER_ID = RandomTestUtil.randomLong();

    @Mock
    private BlogsEntry _blogsEntry;

    @Mock
    private BlogsEntryLocalService _blogsEntryLocalService;

    @Mock
    private CommentManager _commentManager;

    @Mock
    private FriendlyURLMapper _friendlyURLMapper;

    @Mock
    private Http _http;

    @Mock
    private Language _language;

    private InetAddress[] _localAddresses;

    @Mock
    private Portal _portal;

    @Mock
    private PortletLocalService _portletLocalService;

    @Mock
    private ServiceTrackerMap<String, PortletProvider> _serviceTrackerMap;

    @Mock
    private UserLocalService _userLocalService;

    @Mock
    private XmlRpc _xmlRpc;
}

