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
package com.alipay.sofa.rpc.server.rest;


import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.test.ActivelyDestroyTest;
import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author zhangchengxi
Date 2018/12/2
 */
public class CustomizeFilterTest extends ActivelyDestroyTest {
    private static RestService filterRestService;

    private static CustomizeTestFilter providerFilter;

    private static CustomizeTestFilter clientFilter;

    private static ProviderConfig<RestService> providerConfig;

    private static CustomizeContainerRequestTestFilter customizeContainerRequestTestFilter;

    private static CustomizeContainerResponseTestFilter customizeContainerResponseTestFilter;

    private static CustomizeClientRequestTestFilter customizeClientRequestTestFilter;

    private static CustomizeClientResponseTestFilter customizeClientResponseTestFilter;

    @Test
    public void testFilterInvoked() {
        Assert.assertFalse(CustomizeContainerRequestTestFilter.isInvoked());
        Assert.assertFalse(CustomizeContainerResponseTestFilter.isInvoked());
        Assert.assertFalse(CustomizeClientRequestTestFilter.isInvoked());
        Assert.assertFalse(CustomizeClientResponseTestFilter.isInvoked());
        Assert.assertFalse(CustomizeFilterTest.providerFilter.isInvoked());
        Assert.assertFalse(CustomizeFilterTest.clientFilter.isInvoked());
        CustomizeFilterTest.filterRestService.get("test");
        Assert.assertTrue(CustomizeContainerRequestTestFilter.isInvoked());
        Assert.assertTrue(CustomizeContainerResponseTestFilter.isInvoked());
        Assert.assertTrue(CustomizeClientRequestTestFilter.isInvoked());
        Assert.assertTrue(CustomizeClientResponseTestFilter.isInvoked());
        Assert.assertTrue(CustomizeFilterTest.providerFilter.isInvoked());
        Assert.assertTrue(CustomizeFilterTest.clientFilter.isInvoked());
    }

    @Test
    public void testNormalHttpRequest() throws IOException {
        Assert.assertFalse(CustomizeFilterTest.providerFilter.isInvoked());
        Assert.assertFalse(CustomizeContainerRequestTestFilter.isInvoked());
        Assert.assertFalse(CustomizeContainerResponseTestFilter.isInvoked());
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://127.0.0.1:8583/rest/get/abc");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        Assert.assertTrue(CustomizeFilterTest.providerFilter.isInvoked());
        Assert.assertTrue(CustomizeContainerRequestTestFilter.isInvoked());
        Assert.assertTrue(CustomizeContainerResponseTestFilter.isInvoked());
    }
}

