/**
 * Copyright (c) 2018 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liulishuo.okdownload;


import DownloadConnection.Connected;
import RedirectUtil.HTTP_PERMANENT_REDIRECT;
import RedirectUtil.HTTP_TEMPORARY_REDIRECT;
import com.liulishuo.okdownload.core.connection.DownloadConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import javax.net.ssl.HttpsURLConnection;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class RedirectUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void isRedirect() {
        Assert.assertTrue(RedirectUtil.isRedirect(HttpURLConnection.HTTP_MOVED_PERM));
        Assert.assertTrue(RedirectUtil.isRedirect(HttpURLConnection.HTTP_MOVED_TEMP));
        Assert.assertTrue(RedirectUtil.isRedirect(HttpURLConnection.HTTP_SEE_OTHER));
        Assert.assertTrue(RedirectUtil.isRedirect(HttpURLConnection.HTTP_MULT_CHOICE));
        Assert.assertTrue(RedirectUtil.isRedirect(HTTP_TEMPORARY_REDIRECT));
        Assert.assertTrue(RedirectUtil.isRedirect(HTTP_PERMANENT_REDIRECT));
        Assert.assertFalse(RedirectUtil.isRedirect(HttpsURLConnection.HTTP_ACCEPTED));
    }

    @Test
    public void getRedirectedUrl_thrownProtocolException() throws IOException {
        thrown.expect(ProtocolException.class);
        thrown.expectMessage("Response code is 302 but can't find Location field");
        final DownloadConnection.Connected connected = Mockito.mock(Connected.class);
        Mockito.when(connected.getResponseHeaderField("Location")).thenReturn(null);
        RedirectUtil.getRedirectedUrl(connected, 302);
    }

    @Test
    public void getRedirectUrl() throws IOException {
        final String redirectUrl = "http://redirect";
        final DownloadConnection.Connected connected = Mockito.mock(Connected.class);
        Mockito.when(connected.getResponseHeaderField("Location")).thenReturn(redirectUrl);
        Assert.assertEquals(RedirectUtil.getRedirectedUrl(connected, 302), redirectUrl);
    }
}

