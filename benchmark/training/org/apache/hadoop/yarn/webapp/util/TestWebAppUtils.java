/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.webapp.util;


import WebAppUtils.WEB_APP_KEYSTORE_PASSWORD_KEY;
import WebAppUtils.WEB_APP_KEY_PASSWORD_KEY;
import WebAppUtils.WEB_APP_TRUSTSTORE_PASSWORD_KEY;
import YarnConfiguration.RM_HA_ENABLED;
import YarnConfiguration.RM_HA_IDS;
import YarnConfiguration.RM_WEBAPP_ADDRESS;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.HttpServer2;
import org.apache.hadoop.http.HttpServer2.Builder;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestWebAppUtils {
    private static final String RM1_NODE_ID = "rm1";

    private static final String RM2_NODE_ID = "rm2";

    // Because WebAppUtils#getResolvedAddress tries to resolve the hostname, we add a static mapping for dummy hostnames
    // to make this test run anywhere without having to give some resolvable hostnames
    private static String[] dummyHostNames = new String[]{ "host1", "host2", "host3" };

    private static final String anyIpAddress = "1.2.3.4";

    private static Map<String, String> savedStaticResolution = new HashMap<>();

    @Test
    public void TestRMWebAppURLRemoteAndLocal() throws UnknownHostException {
        Configuration configuration = new Configuration();
        final String rmAddress = "host1:8088";
        configuration.set(RM_WEBAPP_ADDRESS, rmAddress);
        final String rm1Address = "host2:8088";
        final String rm2Address = "host3:8088";
        configuration.set((((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".") + (TestWebAppUtils.RM1_NODE_ID)), rm1Address);
        configuration.set((((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".") + (TestWebAppUtils.RM2_NODE_ID)), rm2Address);
        configuration.setBoolean(RM_HA_ENABLED, true);
        configuration.set(RM_HA_IDS, (((TestWebAppUtils.RM1_NODE_ID) + ",") + (TestWebAppUtils.RM2_NODE_ID)));
        String rmRemoteUrl = WebAppUtils.getResolvedRemoteRMWebAppURLWithoutScheme(configuration);
        Assert.assertEquals("ResolvedRemoteRMWebAppUrl should resolve to the first HA RM address", rm1Address, rmRemoteUrl);
        String rmLocalUrl = WebAppUtils.getResolvedRMWebAppURLWithoutScheme(configuration);
        Assert.assertEquals("ResolvedRMWebAppUrl should resolve to the default RM webapp address", rmAddress, rmLocalUrl);
    }

    @Test
    public void testGetPassword() throws Exception {
        Configuration conf = provisionCredentialsForSSL();
        // use WebAppUtils as would be used by loadSslConfiguration
        Assert.assertEquals("keypass", WebAppUtils.getPassword(conf, WEB_APP_KEY_PASSWORD_KEY));
        Assert.assertEquals("storepass", WebAppUtils.getPassword(conf, WEB_APP_KEYSTORE_PASSWORD_KEY));
        Assert.assertEquals("trustpass", WebAppUtils.getPassword(conf, WEB_APP_TRUSTSTORE_PASSWORD_KEY));
        // let's make sure that a password that doesn't exist returns null
        Assert.assertEquals(null, WebAppUtils.getPassword(conf, "invalid-alias"));
    }

    @Test
    public void testLoadSslConfiguration() throws Exception {
        Configuration conf = provisionCredentialsForSSL();
        TestWebAppUtils.TestBuilder builder = ((TestWebAppUtils.TestBuilder) (new TestWebAppUtils.TestBuilder()));
        builder = ((TestWebAppUtils.TestBuilder) (WebAppUtils.loadSslConfiguration(builder, conf)));
        String keypass = "keypass";
        String storepass = "storepass";
        String trustpass = "trustpass";
        // make sure we get the right passwords in the builder
        Assert.assertEquals(keypass, ((TestWebAppUtils.TestBuilder) (builder)).keypass);
        Assert.assertEquals(storepass, ((TestWebAppUtils.TestBuilder) (builder)).keystorePassword);
        Assert.assertEquals(trustpass, ((TestWebAppUtils.TestBuilder) (builder)).truststorePassword);
    }

    @Test
    public void testAppendQueryParams() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String targetUri = "/test/path";
        Mockito.when(request.getCharacterEncoding()).thenReturn(null);
        Map<String, String> paramResultMap = new HashMap<>();
        paramResultMap.put("param1=x", ((targetUri + "?") + "param1=x"));
        paramResultMap.put("param1=x&param2=y", ((targetUri + "?") + "param1=x&param2=y"));
        paramResultMap.put("param1=x&param2=y&param3=x+y", ((targetUri + "?") + "param1=x&param2=y&param3=x+y"));
        for (Map.Entry<String, String> entry : paramResultMap.entrySet()) {
            Mockito.when(request.getQueryString()).thenReturn(entry.getKey());
            String uri = WebAppUtils.appendQueryParams(request, targetUri);
            Assert.assertEquals(entry.getValue(), uri);
        }
    }

    @Test
    public void testGetHtmlEscapedURIWithQueryString() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String targetUri = "/test/path";
        Mockito.when(request.getCharacterEncoding()).thenReturn(null);
        Mockito.when(request.getRequestURI()).thenReturn(targetUri);
        Map<String, String> paramResultMap = new HashMap<>();
        paramResultMap.put("param1=x", ((targetUri + "?") + "param1=x"));
        paramResultMap.put("param1=x&param2=y", ((targetUri + "?") + "param1=x&amp;param2=y"));
        paramResultMap.put("param1=x&param2=y&param3=x+y", ((targetUri + "?") + "param1=x&amp;param2=y&amp;param3=x+y"));
        for (Map.Entry<String, String> entry : paramResultMap.entrySet()) {
            Mockito.when(request.getQueryString()).thenReturn(entry.getKey());
            String uri = WebAppUtils.getHtmlEscapedURIWithQueryString(request);
            Assert.assertEquals(entry.getValue(), uri);
        }
    }

    public class TestBuilder extends HttpServer2.Builder {
        public String keypass;

        public String keystorePassword;

        public String truststorePassword;

        @Override
        public Builder trustStore(String location, String password, String type) {
            truststorePassword = password;
            return super.trustStore(location, password, type);
        }

        @Override
        public Builder keyStore(String location, String password, String type) {
            keystorePassword = password;
            return super.keyStore(location, password, type);
        }

        @Override
        public Builder keyPassword(String password) {
            keypass = password;
            return super.keyPassword(password);
        }
    }
}

