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
package org.apache.nifi.websocket.jetty;


import StandardSSLContextService.KEYSTORE;
import StandardSSLContextService.KEYSTORE_PASSWORD;
import StandardSSLContextService.KEYSTORE_TYPE;
import StandardSSLContextService.TRUSTSTORE;
import StandardSSLContextService.TRUSTSTORE_PASSWORD;
import StandardSSLContextService.TRUSTSTORE_TYPE;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.ssl.StandardSSLContextService;
import org.junit.Test;


public class ITJettyWebSocketSecureCommunication extends ITJettyWebSocketCommunication {
    private final StandardSSLContextService sslContextService = new StandardSSLContextService();

    private final ControllerServiceTestContext sslTestContext = new ControllerServiceTestContext(sslContextService, "SSLContextService");

    public ITJettyWebSocketSecureCommunication() {
        try {
            sslTestContext.setCustomValue(KEYSTORE, "src/test/resources/certs/keystore.jks");
            sslTestContext.setCustomValue(KEYSTORE_PASSWORD, "passwordpassword");
            sslTestContext.setCustomValue(KEYSTORE_TYPE, "JKS");
            sslTestContext.setCustomValue(TRUSTSTORE, "src/test/resources/certs/truststore.jks");
            sslTestContext.setCustomValue(TRUSTSTORE_PASSWORD, "passwordpassword");
            sslTestContext.setCustomValue(TRUSTSTORE_TYPE, "JKS");
            sslContextService.initialize(sslTestContext.getInitializationContext());
            sslContextService.onConfigured(sslTestContext.getConfigurationContext());
        } catch (InitializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testClientServerCommunication() throws Exception {
        super.testClientServerCommunication();
    }
}

