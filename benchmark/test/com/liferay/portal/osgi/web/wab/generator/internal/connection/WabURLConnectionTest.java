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
package com.liferay.portal.osgi.web.wab.generator.internal.connection;


import java.io.IOException;
import java.net.URL;
import java.net.UnknownServiceException;
import org.junit.Test;


/**
 *
 *
 * @author Gregory Amerson
 */
public class WabURLConnectionTest {
    @Test(expected = UnknownServiceException.class)
    public void testWabURLConnectionRequiredParams() throws IOException {
        WabURLConnection wabURLConnection = new WabURLConnection(null, null, new URL("webbundle:/path/to/foo?Web-ContextPath=foo&protocol=file"));
        wabURLConnection.getInputStream();
    }

    @Test(expected = UnknownServiceException.class)
    public void testWabURLConnectionRequiredParamsCompatibilityMode() throws Exception {
        String uriString = _getURIString("/classic-theme.autodeployed.war");
        WabURLConnection wabURLConnection = new WabURLConnection(null, null, new URL((("webbundle:" + uriString) + "?Web-ContextPath=foo")));
        wabURLConnection.getInputStream();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWabURLConnectionRequiredParamsMissing() throws Exception {
        WabURLConnection wabURLConnection = new WabURLConnection(null, null, new URL("webbundle:/path/to/foo?Web-ContextPath=foo"));
        wabURLConnection.getInputStream();
    }
}

