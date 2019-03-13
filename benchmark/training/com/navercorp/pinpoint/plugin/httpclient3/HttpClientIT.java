/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.httpclient3;


import HttpMethodParams.RETRY_HANDLER;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.plugin.AgentPath;
import com.navercorp.pinpoint.plugin.WebServer;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestSuite;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author jaehong.kim
 */
@RunWith(PinpointPluginTestSuite.class)
@PinpointAgent(AgentPath.PATH)
@Dependency({ "commons-httpclient:commons-httpclient:[3.0],[3.0.1],[3.1]", "org.nanohttpd:nanohttpd:2.3.1" })
public class HttpClientIT {
    private static WebServer webServer;

    private static final long CONNECTION_TIMEOUT = 10000;

    private static final int SO_TIMEOUT = 10000;

    @Test
    public void test() throws Exception {
        HttpClient client = new HttpClient();
        client.getParams().setConnectionManagerTimeout(HttpClientIT.CONNECTION_TIMEOUT);
        client.getParams().setSoTimeout(HttpClientIT.SO_TIMEOUT);
        GetMethod method = new GetMethod(HttpClientIT.webServer.getCallHttpUrl());
        // Provide custom retry handler is necessary
        method.getParams().setParameter(RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        method.setQueryString(new NameValuePair[]{ new NameValuePair("key2", "value2") });
        try {
            // Execute the method.
            client.executeMethod(method);
        } catch (Exception ignored) {
        } finally {
            method.releaseConnection();
        }
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
    }

    @Test
    public void hostConfig() throws Exception {
        HttpClient client = new HttpClient();
        client.getParams().setConnectionManagerTimeout(HttpClientIT.CONNECTION_TIMEOUT);
        client.getParams().setSoTimeout(HttpClientIT.SO_TIMEOUT);
        HostConfiguration config = new HostConfiguration();
        config.setHost("weather.naver.com", 80, "http");
        GetMethod method = new GetMethod("/rgn/cityWetrMain.nhn");
        // Provide custom retry handler is necessary
        method.getParams().setParameter(RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        method.setQueryString(new NameValuePair[]{ new NameValuePair("key2", "value2") });
        try {
            // Execute the method.
            client.executeMethod(config, method);
        } catch (Exception ignored) {
        } finally {
            method.releaseConnection();
        }
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
    }
}

