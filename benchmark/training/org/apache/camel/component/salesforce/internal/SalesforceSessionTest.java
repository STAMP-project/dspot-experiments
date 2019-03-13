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
package org.apache.camel.component.salesforce.internal;


import org.apache.camel.component.salesforce.SalesforceHttpClient;
import org.apache.camel.component.salesforce.SalesforceLoginConfig;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SalesforceSessionTest {
    private static final int TIMEOUT = 1;

    private final KeyStoreParameters parameters;

    public SalesforceSessionTest() {
        parameters = new KeyStoreParameters();
        parameters.setResource("test.p12");
        parameters.setType("PKCS12");
        parameters.setPassword("password");
    }

    @Test
    public void shouldGenerateJwtTokens() {
        final SalesforceLoginConfig config = new SalesforceLoginConfig("https://login.salesforce.com", "ABCD", "username", parameters, true);
        final SalesforceSession session = new SalesforceSession(new DefaultCamelContext(), Mockito.mock(SalesforceHttpClient.class), SalesforceSessionTest.TIMEOUT, config);
        final String jwtAssertion = session.generateJwtAssertion();
        Assert.assertNotNull(jwtAssertion);
    }

    @Test
    public void shouldUseTheOverridenInstanceUrl() throws Exception {
        final SalesforceLoginConfig config = new SalesforceLoginConfig("https://login.salesforce.com", "clientId", "clientSecret", "username", "password", true);
        config.setInstanceUrl("https://custom.salesforce.com:8443");
        final SalesforceSession session = SalesforceSessionTest.login(config);
        Assert.assertEquals("https://custom.salesforce.com:8443", session.getInstanceUrl());
    }

    @Test
    public void shouldUseTheSalesforceSuppliedInstanceUrl() throws Exception {
        final SalesforceLoginConfig config = new SalesforceLoginConfig("https://login.salesforce.com", "clientId", "clientSecret", "username", "password", true);
        final SalesforceSession session = SalesforceSessionTest.login(config);
        Assert.assertEquals("https://eu11.salesforce.com", session.getInstanceUrl());
    }
}

