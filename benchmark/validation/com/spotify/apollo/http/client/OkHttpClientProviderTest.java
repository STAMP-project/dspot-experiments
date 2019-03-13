/**
 * -\-\-
 * Spotify Apollo okhttp Client Module
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo.http.client;


import com.squareup.okhttp.OkHttpClient;
import org.junit.Assert;
import org.junit.Test;


public class OkHttpClientProviderTest {
    @Test
    public void testConnectTimeout() {
        Assert.assertEquals(1234, OkHttpClientProviderTest.buildClient("http.client.connectTimeout: 1234").getConnectTimeout());
    }

    @Test
    public void testReadTimeout() {
        Assert.assertEquals(444, OkHttpClientProviderTest.buildClient("http.client.readTimeout: 444").getReadTimeout());
    }

    @Test
    public void testWriteTimeout() {
        Assert.assertEquals(5555, OkHttpClientProviderTest.buildClient("http.client.writeTimeout: 5555").getWriteTimeout());
    }

    @Test
    public void testMaxRequests() {
        final OkHttpClient client = OkHttpClientProviderTest.buildClient("http.client.async.maxRequests: 72");
        Assert.assertEquals(72, client.getDispatcher().getMaxRequests());
    }

    @Test
    public void testMaxRequestsPerHost() {
        final OkHttpClient client = OkHttpClientProviderTest.buildClient("http.client.async.maxRequestsPerHost: 79");
        Assert.assertEquals(79, client.getDispatcher().getMaxRequestsPerHost());
    }

    @Test
    public void testFollowRedirects() {
        final OkHttpClient client = OkHttpClientProviderTest.buildClient("http.client.followRedirects: false");
        Assert.assertEquals(false, client.getFollowRedirects());
    }
}

