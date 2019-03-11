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


import org.apache.nifi.authorization.user.NiFiUserDetails;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.web.security.token.NiFiAuthenticationToken;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class OtpAuthenticationProviderTest {
    private static final String UI_EXTENSION_AUTHENTICATED_USER = "ui-extension-token-authenticated-user";

    private static final String UI_EXTENSION_TOKEN = "ui-extension-token";

    private static final String DOWNLOAD_AUTHENTICATED_USER = "download-token-authenticated-user";

    private static final String DOWNLOAD_TOKEN = "download-token";

    private OtpService otpService;

    private OtpAuthenticationProvider otpAuthenticationProvider;

    private NiFiProperties nifiProperties;

    @Test
    public void testUiExtensionPath() throws Exception {
        final OtpAuthenticationRequestToken request = new OtpAuthenticationRequestToken(OtpAuthenticationProviderTest.UI_EXTENSION_TOKEN, false, null);
        final NiFiAuthenticationToken result = ((NiFiAuthenticationToken) (otpAuthenticationProvider.authenticate(request)));
        final NiFiUserDetails details = ((NiFiUserDetails) (result.getPrincipal()));
        Assert.assertEquals(OtpAuthenticationProviderTest.UI_EXTENSION_AUTHENTICATED_USER, details.getUsername());
        Mockito.verify(otpService, Mockito.times(1)).getAuthenticationFromUiExtensionToken(OtpAuthenticationProviderTest.UI_EXTENSION_TOKEN);
        Mockito.verify(otpService, Mockito.never()).getAuthenticationFromDownloadToken(ArgumentMatchers.anyString());
    }

    @Test
    public void testDownload() throws Exception {
        final OtpAuthenticationRequestToken request = new OtpAuthenticationRequestToken(OtpAuthenticationProviderTest.DOWNLOAD_TOKEN, true, null);
        final NiFiAuthenticationToken result = ((NiFiAuthenticationToken) (otpAuthenticationProvider.authenticate(request)));
        final NiFiUserDetails details = ((NiFiUserDetails) (result.getPrincipal()));
        Assert.assertEquals(OtpAuthenticationProviderTest.DOWNLOAD_AUTHENTICATED_USER, details.getUsername());
        Mockito.verify(otpService, Mockito.never()).getAuthenticationFromUiExtensionToken(ArgumentMatchers.anyString());
        Mockito.verify(otpService, Mockito.times(1)).getAuthenticationFromDownloadToken(OtpAuthenticationProviderTest.DOWNLOAD_TOKEN);
    }
}

