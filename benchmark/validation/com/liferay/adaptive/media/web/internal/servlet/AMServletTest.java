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
package com.liferay.adaptive.media.web.internal.servlet;


import AMException.AMNotFound;
import HttpServletResponse.SC_BAD_REQUEST;
import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_NOT_FOUND;
import com.liferay.adaptive.media.handler.AMRequestHandler;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Adolfo P?rez
 */
public class AMServletTest {
    @Test
    public void testMiscellaneousError() throws Exception {
        Mockito.when(_request.getPathInfo()).thenReturn(RandomTestUtil.randomString());
        Mockito.when(_amRequestHandlerLocator.locateForPattern(Mockito.anyString())).thenReturn(_amRequestHandler);
        Mockito.when(_amRequestHandler.handleRequest(_request)).thenThrow(new IllegalArgumentException());
        _amServlet.doGet(_request, _response);
        Mockito.verify(_response).sendError(Mockito.eq(SC_BAD_REQUEST), Mockito.anyString());
    }

    @Test
    public void testNoMediaFound() throws Exception {
        Mockito.when(_request.getPathInfo()).thenReturn(RandomTestUtil.randomString());
        Mockito.when(_amRequestHandlerLocator.locateForPattern(Mockito.anyString())).thenReturn(_amRequestHandler);
        Mockito.when(_amRequestHandler.handleRequest(_request)).thenReturn(Optional.empty());
        _amServlet.doGet(_request, _response);
        Mockito.verify(_response).sendError(Mockito.eq(SC_NOT_FOUND), Mockito.anyString());
    }

    @Test
    public void testNoMediaFoundWithException() throws Exception {
        Mockito.when(_request.getPathInfo()).thenReturn(RandomTestUtil.randomString());
        Mockito.when(_amRequestHandlerLocator.locateForPattern(Mockito.anyString())).thenReturn(_amRequestHandler);
        Mockito.when(_amRequestHandler.handleRequest(_request)).thenThrow(AMNotFound.class);
        _amServlet.doGet(_request, _response);
        Mockito.verify(_response).sendError(Mockito.eq(SC_NOT_FOUND), Mockito.anyString());
    }

    @Test
    public void testNoPermissionError() throws Exception {
        Mockito.when(_request.getPathInfo()).thenReturn(RandomTestUtil.randomString());
        Mockito.when(_amRequestHandlerLocator.locateForPattern(Mockito.anyString())).thenReturn(_amRequestHandler);
        Mockito.when(_amRequestHandler.handleRequest(_request)).thenThrow(new javax.servlet.ServletException(new PrincipalException()));
        _amServlet.doGet(_request, _response);
        Mockito.verify(_response).sendError(Mockito.eq(SC_FORBIDDEN), Mockito.anyString());
    }

    @Test
    public void testNoRequestHandlerFound() throws Exception {
        Mockito.when(_request.getPathInfo()).thenReturn(RandomTestUtil.randomString());
        Mockito.when(_amRequestHandlerLocator.locateForPattern(Mockito.anyString())).thenReturn(null);
        _amServlet.doGet(_request, _response);
        Mockito.verify(_response).sendError(Mockito.eq(SC_NOT_FOUND), Mockito.anyString());
    }

    private final AMRequestHandler<?> _amRequestHandler = Mockito.mock(AMRequestHandler.class);

    private final AMRequestHandlerLocator _amRequestHandlerLocator = Mockito.mock(AMRequestHandlerLocator.class);

    private final AMServlet _amServlet = new AMServlet();

    private final HttpServletRequest _request = Mockito.mock(HttpServletRequest.class);

    private final HttpServletResponse _response = Mockito.mock(HttpServletResponse.class);
}

