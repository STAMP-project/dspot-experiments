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
package org.apache.camel.converter.crypto;


import CryptoDataFormat.KEY;
import java.security.Key;
import javax.crypto.KeyGenerator;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class CryptoDataFormatTest extends CamelTestSupport {
    @Test
    public void testBasicSymmetric() throws Exception {
        doRoundTripEncryptionTests("direct:basic-encryption");
    }

    @Test
    public void testSymmetricWithInitVector() throws Exception {
        doRoundTripEncryptionTests("direct:init-vector");
    }

    @Test
    public void testSymmetricWithInlineInitVector() throws Exception {
        doRoundTripEncryptionTests("direct:inline");
    }

    @Test
    public void testSymmetricWithHMAC() throws Exception {
        doRoundTripEncryptionTests("direct:hmac");
    }

    @Test
    public void testSymmetricWithMD5HMAC() throws Exception {
        doRoundTripEncryptionTests("direct:hmac-algorithm");
    }

    @Test
    public void testSymmetricWithSHA256HMAC() throws Exception {
        doRoundTripEncryptionTests("direct:hmac-sha-256-algorithm");
    }

    @Test
    public void testKeySuppliedAsHeader() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("DES");
        Key key = generator.generateKey();
        Exchange unecrypted = getMandatoryEndpoint("direct:key-in-header-encrypt").createExchange();
        unecrypted.getIn().setBody("Hi Alice, Be careful Eve is listening, signed Bob");
        unecrypted.getIn().setHeader(KEY, key);
        unecrypted = template.send("direct:key-in-header-encrypt", unecrypted);
        validateHeaderIsCleared(unecrypted);
        MockEndpoint mock = setupExpectations(context, 1, "mock:unencrypted");
        Exchange encrypted = getMandatoryEndpoint("direct:key-in-header-decrypt").createExchange();
        encrypted.getIn().copyFrom(unecrypted.getIn());
        encrypted.getIn().setHeader(KEY, key);
        template.send("direct:key-in-header-decrypt", encrypted);
        assertMockEndpointsSatisfied();
        Exchange received = mock.getReceivedExchanges().get(0);
        validateHeaderIsCleared(received);
    }

    @Test
    public void test3DESECBSymmetric() throws Exception {
        doRoundTripEncryptionTests("direct:3des-ecb-encryption");
    }

    @Test
    public void test3DESCBCSymmetric() throws Exception {
        doRoundTripEncryptionTests("direct:3des-cbc-encryption");
    }

    @Test
    public void testAES128ECBSymmetric() throws Exception {
        if (CryptoDataFormatTest.checkUnrestrictedPoliciesInstalled()) {
            doRoundTripEncryptionTests("direct:aes-128-ecb-encryption");
        }
    }
}

