/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.bonita.api;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import javax.ws.rs.client.ClientRequestContext;
import org.apache.camel.component.bonita.api.filter.BonitaAuthFilter;
import org.apache.camel.component.bonita.api.util.BonitaAPIConfig;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;


public class BonitaAuthFilterConnectionTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    @Mock
    private ClientRequestContext requestContext;

    @Test
    public void testConnection() throws Exception {
        String port = (wireMockRule.port()) + "";
        stubFor(post(urlEqualTo("/bonita/loginservice")).willReturn(aResponse().withHeader("Set-Cookie", "JSESSIONID=something")));
        BonitaAPIConfig bonitaApiConfig = new BonitaAPIConfig("localhost", port, "username", "password");
        BonitaAuthFilter bonitaAuthFilter = new BonitaAuthFilter(bonitaApiConfig);
        bonitaAuthFilter.filter(requestContext);
        Assert.assertEquals(1, requestContext.getHeaders().size());
    }

    @Test
    public void testConnectionSupportCSRF() throws Exception {
        String port = (wireMockRule.port()) + "";
        stubFor(post(urlEqualTo("/bonita/loginservice")).willReturn(aResponse().withHeader("Set-Cookie", "JSESSIONID=something", "X-Bonita-API-Token=something")));
        BonitaAPIConfig bonitaApiConfig = new BonitaAPIConfig("localhost", port, "username", "password");
        BonitaAuthFilter bonitaAuthFilter = new BonitaAuthFilter(bonitaApiConfig);
        bonitaAuthFilter.filter(requestContext);
        Assert.assertEquals(2, requestContext.getHeaders().size());
    }
}

