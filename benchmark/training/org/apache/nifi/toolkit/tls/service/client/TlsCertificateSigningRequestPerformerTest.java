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
package org.apache.nifi.toolkit.tls.service.client;


import TlsCertificateSigningRequestPerformer.EXPECTED_ONE_CERTIFICATE;
import TlsCertificateSigningRequestPerformer.EXPECTED_RESPONSE_TO_CONTAIN_CERTIFICATE;
import TlsCertificateSigningRequestPerformer.EXPECTED_RESPONSE_TO_CONTAIN_HMAC;
import TlsCertificateSigningRequestPerformer.UNEXPECTED_HMAC_RECEIVED_POSSIBLE_MAN_IN_THE_MIDDLE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.function.Supplier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.nifi.toolkit.tls.configuration.TlsClientConfig;
import org.apache.nifi.toolkit.tls.service.dto.TlsCertificateAuthorityResponse;
import org.eclipse.jetty.server.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static TlsCertificateSigningRequestPerformer.RECEIVED_RESPONSE_CODE;


@RunWith(MockitoJUnitRunner.class)
public class TlsCertificateSigningRequestPerformerTest {
    @Mock
    Supplier<HttpClientBuilder> httpClientBuilderSupplier;

    @Mock
    HttpClientBuilder httpClientBuilder;

    @Mock
    CloseableHttpClient closeableHttpClient;

    @Mock
    TlsClientConfig tlsClientConfig;

    X509Certificate caCertificate;

    X509Certificate signedCsr;

    ObjectMapper objectMapper;

    KeyPair keyPair;

    TlsCertificateSigningRequestPerformer tlsCertificateSigningRequestPerformer;

    String testToken;

    String testCaHostname;

    int testPort;

    List<X509Certificate> certificates;

    TlsCertificateAuthorityResponse tlsCertificateAuthorityResponse;

    int statusCode;

    private byte[] testHmac;

    private String testSignedCsr;

    @Test
    public void testOk() throws Exception {
        certificates.add(caCertificate);
        statusCode = Response.SC_OK;
        tlsCertificateAuthorityResponse = new TlsCertificateAuthorityResponse(testHmac, testSignedCsr);
        tlsCertificateSigningRequestPerformer.perform(keyPair);
    }

    @Test
    public void testBadStatusCode() throws Exception {
        statusCode = Response.SC_FORBIDDEN;
        tlsCertificateAuthorityResponse = new TlsCertificateAuthorityResponse();
        try {
            tlsCertificateSigningRequestPerformer.perform(keyPair);
            Assert.fail("Expected IOE");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().startsWith(((RECEIVED_RESPONSE_CODE) + (statusCode))));
        }
    }

    @Test
    public void test0CertSize() throws Exception {
        statusCode = Response.SC_OK;
        tlsCertificateAuthorityResponse = new TlsCertificateAuthorityResponse();
        try {
            tlsCertificateSigningRequestPerformer.perform(keyPair);
            Assert.fail("Expected IOE");
        } catch (IOException e) {
            Assert.assertEquals(EXPECTED_ONE_CERTIFICATE, e.getMessage());
        }
    }

    @Test
    public void test2CertSize() throws Exception {
        certificates.add(caCertificate);
        certificates.add(caCertificate);
        statusCode = Response.SC_OK;
        tlsCertificateAuthorityResponse = new TlsCertificateAuthorityResponse();
        try {
            tlsCertificateSigningRequestPerformer.perform(keyPair);
            Assert.fail("Expected IOE");
        } catch (IOException e) {
            Assert.assertEquals(EXPECTED_ONE_CERTIFICATE, e.getMessage());
        }
    }

    @Test
    public void testNoHmac() throws Exception {
        certificates.add(caCertificate);
        statusCode = Response.SC_OK;
        tlsCertificateAuthorityResponse = new TlsCertificateAuthorityResponse(null, testSignedCsr);
        try {
            tlsCertificateSigningRequestPerformer.perform(keyPair);
            Assert.fail("Expected IOE");
        } catch (IOException e) {
            Assert.assertEquals(EXPECTED_RESPONSE_TO_CONTAIN_HMAC, e.getMessage());
        }
    }

    @Test
    public void testBadHmac() throws Exception {
        certificates.add(caCertificate);
        statusCode = Response.SC_OK;
        tlsCertificateAuthorityResponse = new TlsCertificateAuthorityResponse("badHmac".getBytes(StandardCharsets.UTF_8), testSignedCsr);
        try {
            tlsCertificateSigningRequestPerformer.perform(keyPair);
            Assert.fail("Expected IOE");
        } catch (IOException e) {
            Assert.assertEquals(UNEXPECTED_HMAC_RECEIVED_POSSIBLE_MAN_IN_THE_MIDDLE, e.getMessage());
        }
    }

    @Test
    public void testNoCertificate() throws Exception {
        certificates.add(caCertificate);
        statusCode = Response.SC_OK;
        tlsCertificateAuthorityResponse = new TlsCertificateAuthorityResponse(testHmac, null);
        try {
            tlsCertificateSigningRequestPerformer.perform(keyPair);
            Assert.fail("Expected IOE");
        } catch (IOException e) {
            Assert.assertEquals(EXPECTED_RESPONSE_TO_CONTAIN_CERTIFICATE, e.getMessage());
        }
    }
}

