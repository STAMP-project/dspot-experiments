/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.web.security.otp;


import OtpAuthenticationFilter.ACCESS_TOKEN;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class OtpAuthenticationFilterTest {
    private static final String UI_EXTENSION_AUTHENTICATED_USER = "ui-extension-token-authenticated-user";

    private static final String UI_EXTENSION_TOKEN = "ui-extension-token";

    private static final String DOWNLOAD_AUTHENTICATED_USER = "download-token-authenticated-user";

    private static final String DOWNLOAD_TOKEN = "download-token";

    private OtpAuthenticationFilter otpAuthenticationFilter;

    @Test
    public void testInsecureHttp() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(false);
        Assert.assertNull(otpAuthenticationFilter.attemptAuthentication(request));
    }

    @Test
    public void testNoAccessToken() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getParameter(ACCESS_TOKEN)).thenReturn(null);
        Assert.assertNull(otpAuthenticationFilter.attemptAuthentication(request));
    }

    @Test
    public void testUnsupportedDownloadPath() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getParameter(ACCESS_TOKEN)).thenReturn("my-access-token");
        Mockito.when(request.getContextPath()).thenReturn("/nifi-api");
        Mockito.when(request.getPathInfo()).thenReturn("/flow/cluster/summary");
        Assert.assertNull(otpAuthenticationFilter.attemptAuthentication(request));
    }

    @Test
    public void testUiExtensionPath() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getParameter(ACCESS_TOKEN)).thenReturn(OtpAuthenticationFilterTest.UI_EXTENSION_TOKEN);
        Mockito.when(request.getContextPath()).thenReturn("/nifi-update-attribute-ui");
        final OtpAuthenticationRequestToken result = ((OtpAuthenticationRequestToken) (otpAuthenticationFilter.attemptAuthentication(request)));
        Assert.assertEquals(OtpAuthenticationFilterTest.UI_EXTENSION_TOKEN, result.getToken());
        Assert.assertFalse(result.isDownloadToken());
    }

    @Test
    public void testProvenanceInputContentDownload() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getParameter(ACCESS_TOKEN)).thenReturn(OtpAuthenticationFilterTest.DOWNLOAD_TOKEN);
        Mockito.when(request.getContextPath()).thenReturn("/nifi-api");
        Mockito.when(request.getPathInfo()).thenReturn("/provenance-events/0/content/input");
        final OtpAuthenticationRequestToken result = ((OtpAuthenticationRequestToken) (otpAuthenticationFilter.attemptAuthentication(request)));
        Assert.assertEquals(OtpAuthenticationFilterTest.DOWNLOAD_TOKEN, result.getToken());
        Assert.assertTrue(result.isDownloadToken());
    }

    @Test
    public void testProvenanceOutputContentDownload() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getParameter(ACCESS_TOKEN)).thenReturn(OtpAuthenticationFilterTest.DOWNLOAD_TOKEN);
        Mockito.when(request.getContextPath()).thenReturn("/nifi-api");
        Mockito.when(request.getPathInfo()).thenReturn("/provenance-events/0/content/output");
        final OtpAuthenticationRequestToken result = ((OtpAuthenticationRequestToken) (otpAuthenticationFilter.attemptAuthentication(request)));
        Assert.assertEquals(OtpAuthenticationFilterTest.DOWNLOAD_TOKEN, result.getToken());
        Assert.assertTrue(result.isDownloadToken());
    }

    @Test
    public void testFlowFileContentDownload() throws Exception {
        final String uuid = UUID.randomUUID().toString();
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getParameter(ACCESS_TOKEN)).thenReturn(OtpAuthenticationFilterTest.DOWNLOAD_TOKEN);
        Mockito.when(request.getContextPath()).thenReturn("/nifi-api");
        Mockito.when(request.getPathInfo()).thenReturn(String.format("/flowfile-queues/%s/flowfiles/%s/content", uuid, uuid));
        final OtpAuthenticationRequestToken result = ((OtpAuthenticationRequestToken) (otpAuthenticationFilter.attemptAuthentication(request)));
        Assert.assertEquals(OtpAuthenticationFilterTest.DOWNLOAD_TOKEN, result.getToken());
        Assert.assertTrue(result.isDownloadToken());
    }

    @Test
    public void testTemplateDownload() throws Exception {
        final String uuid = UUID.randomUUID().toString();
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getParameter(ACCESS_TOKEN)).thenReturn(OtpAuthenticationFilterTest.DOWNLOAD_TOKEN);
        Mockito.when(request.getContextPath()).thenReturn("/nifi-api");
        Mockito.when(request.getPathInfo()).thenReturn(String.format("/templates/%s/download", uuid));
        final OtpAuthenticationRequestToken result = ((OtpAuthenticationRequestToken) (otpAuthenticationFilter.attemptAuthentication(request)));
        Assert.assertEquals(OtpAuthenticationFilterTest.DOWNLOAD_TOKEN, result.getToken());
        Assert.assertTrue(result.isDownloadToken());
    }
}

