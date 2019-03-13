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
package org.apache.camel.component.crypto;


import DigitalSignatureConstants.KEYSTORE_ALIAS;
import DigitalSignatureConstants.KEYSTORE_PASSWORD;
import DigitalSignatureConstants.SIGNATURE;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class SignatureTests extends CamelTestSupport {
    private KeyPair keyPair;

    private String payload = "Dear Alice, Rest assured it's me, signed Bob";

    @Test
    public void testBasicSignatureRoute() throws Exception {
        setupMock();
        sendBody("direct:keypair", payload);
        assertMockEndpointsSatisfied();
        MockEndpoint mock = getMockEndpoint("mock:result");
        Exchange e = mock.getExchanges().get(0);
        Message result = (e == null) ? null : e.hasOut() ? e.getOut() : e.getIn();
        assertNull(result.getHeader(SIGNATURE));
    }

    @Test
    public void testSetAlgorithmInRouteDefinition() throws Exception {
        setupMock();
        sendBody("direct:algorithm", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSASHA1() throws Exception {
        setupMock();
        sendBody("direct:rsa-sha1", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSASHA256() throws Exception {
        setupMock();
        sendBody("direct:rsa-sha256", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSetBufferInRouteDefinition() throws Exception {
        setupMock();
        sendBody("direct:buffersize", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSetRandomInRouteDefinition() throws Exception {
        setupMock();
        sendBody("direct:random", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSetProviderInRouteDefinition() throws Exception {
        if (isJavaVendor("ibm")) {
            return;
        }
        // can only be run on SUN JDK
        setupMock();
        sendBody("direct:provider", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSetCertificateInRouteDefinition() throws Exception {
        setupMock();
        sendBody("direct:certificate", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSetKeystoreInRouteDefinition() throws Exception {
        setupMock();
        sendBody("direct:keystore", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSetKeystoreParametersInRouteDefinition() throws Exception {
        setupMock();
        sendBody("direct:keystoreParameters", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSignatureHeaderInRouteDefinition() throws Exception {
        setupMock();
        Exchange signed = getMandatoryEndpoint("direct:signature-header").createExchange();
        signed.getIn().setBody(payload);
        template.send("direct:signature-header", signed);
        assertNotNull(signed.getIn().getHeader("AnotherDigitalSignature"));
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testProvideAliasInHeader() throws Exception {
        setupMock();
        // START SNIPPET: alias-send
        Exchange unsigned = getMandatoryEndpoint("direct:alias-sign").createExchange();
        unsigned.getIn().setBody(payload);
        unsigned.getIn().setHeader(KEYSTORE_ALIAS, "bob");
        unsigned.getIn().setHeader(KEYSTORE_PASSWORD, "letmein".toCharArray());
        template.send("direct:alias-sign", unsigned);
        Exchange signed = getMandatoryEndpoint("direct:alias-sign").createExchange();
        signed.getIn().copyFrom(unsigned.getOut());
        signed.getIn().setHeader(DigitalSignatureConstants.KEYSTORE_ALIAS, "bob");
        template.send("direct:alias-verify", signed);
        // START SNIPPET: alias-send
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testProvideKeysInHeader() throws Exception {
        setupMock();
        Exchange unsigned = getMandatoryEndpoint("direct:headerkey-sign").createExchange();
        unsigned.getIn().setBody(payload);
        // create a keypair
        KeyPair pair = getKeyPair("DSA");
        // sign with the private key
        unsigned.getIn().setHeader(DigitalSignatureConstants.SIGNATURE_PRIVATE_KEY, pair.getPrivate());
        template.send("direct:headerkey-sign", unsigned);
        // verify with the public key
        Exchange signed = getMandatoryEndpoint("direct:alias-sign").createExchange();
        signed.getIn().copyFrom(unsigned.getOut());
        signed.getIn().setHeader(DigitalSignatureConstants.SIGNATURE_PUBLIC_KEY_OR_CERT, pair.getPublic());
        template.send("direct:headerkey-verify", signed);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testProvideCertificateInHeader() throws Exception {
        setupMock();
        Exchange unsigned = getMandatoryEndpoint("direct:signature-property").createExchange();
        unsigned.getIn().setBody(payload);
        // create a keypair
        KeyStore keystore = SignatureTests.loadKeystore();
        Certificate certificate = keystore.getCertificate("bob");
        PrivateKey pk = ((PrivateKey) (keystore.getKey("bob", "letmein".toCharArray())));
        // sign with the private key
        unsigned.getIn().setHeader(DigitalSignatureConstants.SIGNATURE_PRIVATE_KEY, pk);
        template.send("direct:headerkey-sign", unsigned);
        // verify with the public key
        Exchange signed = getMandatoryEndpoint("direct:alias-sign").createExchange();
        signed.getIn().copyFrom(unsigned.getOut());
        signed.getIn().setHeader(DigitalSignatureConstants.SIGNATURE_PUBLIC_KEY_OR_CERT, certificate);
        template.send("direct:headerkey-verify", signed);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testVerifyHeadersNotCleared() throws Exception {
        setupMock();
        template.requestBody("direct:headers", payload);
        assertMockEndpointsSatisfied();
        assertMockEndpointsSatisfied();
        MockEndpoint mock = getMockEndpoint("mock:result");
        Exchange e = mock.getExchanges().get(0);
        Message result = (e == null) ? null : e.hasOut() ? e.getOut() : e.getIn();
        assertNotNull(result.getHeader(SIGNATURE));
    }
}

