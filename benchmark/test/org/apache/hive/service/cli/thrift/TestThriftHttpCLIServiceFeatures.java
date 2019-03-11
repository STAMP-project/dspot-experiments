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
package org.apache.hive.service.cli.thrift;


import TCLIService.Client;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.security.HiveAuthenticationProvider;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthorizer;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthorizerFactory;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthzSessionContext;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveMetastoreClientFactory;
import org.apache.hive.jdbc.HttpBasicAuthInterceptor;
import org.apache.hive.service.rpc.thrift.TCLIService;
import org.apache.hive.service.rpc.thrift.TOpenSessionReq;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static ThriftCLIServiceTest.PASSWORD;
import static ThriftCLIServiceTest.USERNAME;


/**
 * Tests that are specific to HTTP transport mode, that need use of underlying
 * classes instead of jdbc.
 */
public class TestThriftHttpCLIServiceFeatures {
    private static String transportMode = "http";

    private static String thriftHttpPath = "cliservice";

    static HiveAuthorizer mockedAuthorizer;

    /**
     * HttpBasicAuthInterceptorWithLogging
     *  This adds httpRequestHeaders to the BasicAuthInterceptor
     */
    public class HttpBasicAuthInterceptorWithLogging extends HttpBasicAuthInterceptor {
        ArrayList<String> requestHeaders;

        String cookieHeader;

        public HttpBasicAuthInterceptorWithLogging(String username, String password, CookieStore cookieStore, String cn, boolean isSSL, Map<String, String> additionalHeaders, Map<String, String> customCookies) {
            super(username, password, cookieStore, cn, isSSL, additionalHeaders, customCookies);
            requestHeaders = new ArrayList<String>();
        }

        @Override
        public void process(HttpRequest httpRequest, HttpContext httpContext) throws IOException, HttpException {
            super.process(httpRequest, httpContext);
            String currHeaders = "";
            for (Header h : httpRequest.getAllHeaders()) {
                currHeaders += (((h.getName()) + ":") + (h.getValue())) + " ";
            }
            requestHeaders.add(currHeaders);
            Header[] headers = httpRequest.getHeaders("Cookie");
            cookieHeader = "";
            for (Header h : headers) {
                cookieHeader = (((cookieHeader) + (h.getName())) + ":") + (h.getValue());
            }
        }

        public ArrayList<String> getRequestHeaders() {
            return requestHeaders;
        }

        public String getCookieHeader() {
            return cookieHeader;
        }
    }

    /**
     * Tests calls from a raw (NOSASL) binary client,
     * to a HiveServer2 running in http mode.
     * This should throw an expected exception due to incompatibility.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBinaryClientHttpServer() throws Exception {
        TTransport transport = getRawBinaryTransport();
        TCLIService.Client rawBinaryClient = getClient(transport);
        // This will throw an expected exception since client-server modes are incompatible
        testOpenSessionExpectedException(rawBinaryClient);
    }

    /**
     * Configure a wrong service endpoint for the client transport,
     * and test for error.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testIncorrectHttpPath() throws Exception {
        TestThriftHttpCLIServiceFeatures.thriftHttpPath = "wrongPath";
        TTransport transport = TestThriftHttpCLIServiceFeatures.getHttpTransport();
        TCLIService.Client httpClient = getClient(transport);
        // This will throw an expected exception since
        // client is communicating with the wrong http service endpoint
        testOpenSessionExpectedException(httpClient);
        // Reset to correct http path
        TestThriftHttpCLIServiceFeatures.thriftHttpPath = "cliservice";
    }

    /**
     * Test additional http headers passed to request interceptor.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAdditionalHttpHeaders() throws Exception {
        TTransport transport;
        DefaultHttpClient hClient = new DefaultHttpClient();
        String httpUrl = TestThriftHttpCLIServiceFeatures.getHttpUrl();
        Map<String, String> additionalHeaders = new HashMap<String, String>();
        additionalHeaders.put("key1", "value1");
        additionalHeaders.put("key2", "value2");
        TestThriftHttpCLIServiceFeatures.HttpBasicAuthInterceptorWithLogging authInt = new TestThriftHttpCLIServiceFeatures.HttpBasicAuthInterceptorWithLogging(USERNAME, PASSWORD, null, null, false, additionalHeaders, null);
        hClient.addRequestInterceptor(authInt);
        transport = new THttpClient(httpUrl, hClient);
        TCLIService.Client httpClient = getClient(transport);
        // Create a new open session request object
        TOpenSessionReq openReq = new TOpenSessionReq();
        httpClient.OpenSession(openReq).getSessionHandle();
        ArrayList<String> headers = authInt.getRequestHeaders();
        for (String h : headers) {
            Assert.assertTrue(h.contains("key1:value1"));
            Assert.assertTrue(h.contains("key2:value2"));
        }
    }

    /**
     * Test additional http headers passed to request interceptor.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCustomCookies() throws Exception {
        TTransport transport;
        DefaultHttpClient hClient = new DefaultHttpClient();
        String httpUrl = TestThriftHttpCLIServiceFeatures.getHttpUrl();
        Map<String, String> additionalHeaders = new HashMap<String, String>();
        Map<String, String> cookieHeaders = new HashMap<String, String>();
        cookieHeaders.put("key1", "value1");
        cookieHeaders.put("key2", "value2");
        TestThriftHttpCLIServiceFeatures.HttpBasicAuthInterceptorWithLogging authInt = new TestThriftHttpCLIServiceFeatures.HttpBasicAuthInterceptorWithLogging(USERNAME, PASSWORD, null, null, false, additionalHeaders, cookieHeaders);
        hClient.addRequestInterceptor(authInt);
        transport = new THttpClient(httpUrl, hClient);
        TCLIService.Client httpClient = getClient(transport);
        // Create a new open session request object
        TOpenSessionReq openReq = new TOpenSessionReq();
        httpClient.OpenSession(openReq).getSessionHandle();
        String cookieHeader = authInt.getCookieHeader();
        Assert.assertTrue(cookieHeader.contains("key1=value1"));
        Assert.assertTrue(cookieHeader.contains("key2=value2"));
    }

    /**
     * This factory creates a mocked HiveAuthorizer class.
     * Use the mocked class to capture the argument passed to it in the test case.
     */
    static class MockedHiveAuthorizerFactory implements HiveAuthorizerFactory {
        @Override
        public HiveAuthorizer createHiveAuthorizer(HiveMetastoreClientFactory metastoreClientFactory, HiveConf conf, HiveAuthenticationProvider authenticator, HiveAuthzSessionContext ctx) {
            TestThriftHttpCLIServiceFeatures.mockedAuthorizer = Mockito.mock(HiveAuthorizer.class);
            return TestThriftHttpCLIServiceFeatures.mockedAuthorizer;
        }
    }

    /**
     * Test if addresses in X-Forwarded-For are passed to HiveAuthorizer calls
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testForwardedHeaders() throws Exception {
        verifyForwardedHeaders(new ArrayList<String>(Arrays.asList("127.0.0.1", "202.101.101.101")), "show tables");
        verifyForwardedHeaders(new ArrayList<String>(Arrays.asList("202.101.101.101")), "fs -ls /");
        verifyForwardedHeaders(new ArrayList<String>(), "show databases");
    }
}

