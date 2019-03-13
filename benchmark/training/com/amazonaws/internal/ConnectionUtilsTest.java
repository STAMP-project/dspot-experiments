/**
 * Copyright (c) 2017. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.internal;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


public class ConnectionUtilsTest {
    @ClassRule
    public static WireMockRule mockProxyServer = new WireMockRule(0);

    @Rule
    public WireMockRule mockServer = new WireMockRule(0);

    private final ConnectionUtils sut = ConnectionUtils.getInstance();

    @Test
    public void proxiesAreNotUsedEvenIfPropertyIsSet() throws IOException {
        System.getProperties().put("http.proxyHost", "localhost");
        System.getProperties().put("http.proxyPort", String.valueOf(ConnectionUtilsTest.mockProxyServer.port()));
        HttpURLConnection connection = sut.connectToEndpoint(URI.create(((("http://" + (Inet4Address.getLocalHost().getHostAddress())) + ":") + (mockServer.port()))), new HashMap<String, String>());
        MatcherAssert.assertThat(connection.usingProxy(), CoreMatchers.is(false));
    }

    @Test
    public void headersArePassedAsPartOfRequest() throws IOException {
        HttpURLConnection connection = sut.connectToEndpoint(URI.create(("http://localhost:" + (mockServer.port()))), Collections.singletonMap("HeaderA", "ValueA"));
        connection.getResponseCode();
        mockServer.verify(getRequestedFor(urlMatching("/")).withHeader("HeaderA", equalTo("ValueA")));
    }
}

