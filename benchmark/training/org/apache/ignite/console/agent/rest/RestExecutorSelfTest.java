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
package org.apache.ignite.console.agent.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.ignite.internal.processors.rest.protocols.http.jetty.GridJettyObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Test for RestExecutor.
 */
public class RestExecutorSelfTest {
    /**
     * Name of the cache created by default in the cluster.
     */
    private static final String DEFAULT_CACHE_NAME = "default";

    /**
     * Path to certificates and configs.
     */
    private static final String PATH_TO_RESOURCES = "modules/web-console/web-agent/src/test/resources/";

    /**
     * JSON object mapper.
     */
    private static final ObjectMapper MAPPER = new GridJettyObjectMapper();

    /**
     *
     */
    private static final String HTTP_URI = "http://localhost:8080";

    /**
     *
     */
    private static final String HTTPS_URI = "https://localhost:8080";

    /**
     *
     */
    private static final String JETTY_WITH_SSL = "jetty-with-ssl.xml";

    /**
     *
     */
    private static final String JETTY_WITH_CIPHERS_0 = "jetty-with-ciphers-0.xml";

    /**
     *
     */
    private static final String JETTY_WITH_CIPHERS_1 = "jetty-with-ciphers-1.xml";

    /**
     *
     */
    private static final String JETTY_WITH_CIPHERS_2 = "jetty-with-ciphers-2.xml";

    /**
     * This cipher is disabled by default in JDK 8.
     */
    private static final List<String> CIPHER_0 = Collections.singletonList("TLS_DH_anon_WITH_AES_256_GCM_SHA384");

    /**
     *
     */
    private static final List<String> CIPHER_1 = Collections.singletonList("TLS_RSA_WITH_NULL_SHA256");

    /**
     *
     */
    private static final List<String> CIPHER_2 = Collections.singletonList("TLS_ECDHE_ECDSA_WITH_NULL_SHA");

    /**
     *
     */
    private static final List<String> COMMON_CIPHERS = Arrays.asList("TLS_RSA_WITH_NULL_SHA256", "TLS_ECDHE_ECDSA_WITH_NULL_SHA");

    /**
     *
     */
    @Rule
    public final ExpectedException ruleForExpectedException = ExpectedException.none();

    /**
     *
     */
    @Test
    public void nodeNoSslAgentNoSsl() throws Exception {
        checkRest(nodeConfiguration(""), RestExecutorSelfTest.HTTP_URI, null, null, null, null, null);
    }

    /**
     *
     */
    @Test
    public void nodeNoSslAgentWithSsl() throws Exception {
        // Check Web Agent with SSL.
        ruleForExpectedException.expect(SSLException.class);
        checkRest(nodeConfiguration(""), RestExecutorSelfTest.HTTPS_URI, resolvePath("client.jks"), "123456", resolvePath("ca.jks"), "123456", null);
    }

    /**
     *
     */
    @Test
    public void nodeWithSslAgentNoSsl() throws Exception {
        ruleForExpectedException.expect(IOException.class);
        checkRest(nodeConfiguration(RestExecutorSelfTest.JETTY_WITH_SSL), RestExecutorSelfTest.HTTP_URI, null, null, null, null, null);
    }

    /**
     *
     */
    @Test
    public void nodeWithSslAgentWithSsl() throws Exception {
        checkRest(nodeConfiguration(RestExecutorSelfTest.JETTY_WITH_SSL), RestExecutorSelfTest.HTTPS_URI, resolvePath("client.jks"), "123456", resolvePath("ca.jks"), "123456", null);
    }

    /**
     *
     */
    @Test
    public void nodeNoCiphersAgentWithCiphers() throws Exception {
        ruleForExpectedException.expect(SSLHandshakeException.class);
        checkRest(nodeConfiguration(RestExecutorSelfTest.JETTY_WITH_SSL), RestExecutorSelfTest.HTTPS_URI, resolvePath("client.jks"), "123456", resolvePath("ca.jks"), "123456", RestExecutorSelfTest.CIPHER_0);
    }

    /**
     *
     */
    @Test
    public void nodeWithCiphersAgentNoCiphers() throws Exception {
        ruleForExpectedException.expect(SSLHandshakeException.class);
        checkRest(nodeConfiguration(RestExecutorSelfTest.JETTY_WITH_CIPHERS_0), RestExecutorSelfTest.HTTPS_URI, resolvePath("client.jks"), "123456", resolvePath("ca.jks"), "123456", null);
    }

    /**
     *
     */
    @Test
    public void nodeWithCiphersAgentWithCiphers() throws Exception {
        checkRest(nodeConfiguration(RestExecutorSelfTest.JETTY_WITH_CIPHERS_1), RestExecutorSelfTest.HTTPS_URI, resolvePath("client.jks"), "123456", resolvePath("ca.jks"), "123456", RestExecutorSelfTest.CIPHER_1);
    }

    /**
     *
     */
    @Test
    public void differentCiphers1() throws Exception {
        ruleForExpectedException.expect(SSLHandshakeException.class);
        checkRest(nodeConfiguration(RestExecutorSelfTest.JETTY_WITH_CIPHERS_1), RestExecutorSelfTest.HTTPS_URI, resolvePath("client.jks"), "123456", resolvePath("ca.jks"), "123456", RestExecutorSelfTest.CIPHER_2);
    }

    /**
     *
     */
    @Test
    public void differentCiphers2() throws Exception {
        ruleForExpectedException.expect(SSLException.class);
        checkRest(nodeConfiguration(RestExecutorSelfTest.JETTY_WITH_CIPHERS_2), RestExecutorSelfTest.HTTPS_URI, resolvePath("client.jks"), "123456", resolvePath("ca.jks"), "123456", RestExecutorSelfTest.CIPHER_1);
    }

    /**
     *
     */
    @Test
    public void commonCiphers() throws Exception {
        checkRest(nodeConfiguration(RestExecutorSelfTest.JETTY_WITH_CIPHERS_1), RestExecutorSelfTest.HTTPS_URI, resolvePath("client.jks"), "123456", resolvePath("ca.jks"), "123456", RestExecutorSelfTest.COMMON_CIPHERS);
    }
}

