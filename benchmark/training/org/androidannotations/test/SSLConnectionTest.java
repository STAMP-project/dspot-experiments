/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test;


import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class SSLConnectionTest {
    private SSLConnection_ activity;

    @Test
    public void truststoreProvided() {
        Assert.assertNotNull(activity.mHttpsClientTest1);
        ClientConnectionManager ccm = activity.mHttpsClientTest1.getConnectionManager();
        Assert.assertNotNull(ccm);
        Scheme httpsScheme = ccm.getSchemeRegistry().getScheme("https");
        Assert.assertNotNull(httpsScheme);
        Assert.assertEquals(443, httpsScheme.getDefaultPort());
        SocketFactory socketFactHttps = httpsScheme.getSocketFactory();
        if (!(socketFactHttps instanceof SSLSocketFactory)) {
            Assert.fail(("wrong instance should be org.apache.http.conn.ssl.SSLSocketFactory, getting " + socketFactHttps));
        }
        Assert.assertEquals(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER, ((SSLSocketFactory) (socketFactHttps)).getHostnameVerifier());
    }

    @Test
    public void strictHostnameVerifier() {
        Assert.assertNotNull(activity.mHttpsClientTest2);
        ClientConnectionManager ccm = activity.mHttpsClientTest2.getConnectionManager();
        Scheme httpsScheme = ccm.getSchemeRegistry().getScheme("https");
        SSLSocketFactory socketFactHttps = ((SSLSocketFactory) (httpsScheme.getSocketFactory()));
        Assert.assertEquals(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER, socketFactHttps.getHostnameVerifier());
    }

    @Test
    public void noOptions() {
        Assert.assertNotNull(activity.mHttpsClientTest3);
        ClientConnectionManager ccm = activity.mHttpsClientTest3.getConnectionManager();
        Assert.assertNotNull(ccm);
    }

    @Test
    public void methodInjectedHttpsClient() {
        Assert.assertNotNull(activity.methodInjectedHttpsClient);
        ClientConnectionManager ccm = activity.methodInjectedHttpsClient.getConnectionManager();
        Assert.assertNotNull(ccm);
    }

    @Test
    public void multiInjectedHttpsClient() {
        Assert.assertNotNull(activity.multiInjectedHttpsClient);
        ClientConnectionManager ccm = activity.multiInjectedHttpsClient.getConnectionManager();
        Assert.assertNotNull(ccm);
    }
}

