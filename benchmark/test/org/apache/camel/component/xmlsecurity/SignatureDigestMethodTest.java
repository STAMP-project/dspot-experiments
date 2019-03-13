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
package org.apache.camel.component.xmlsecurity;


import java.lang.reflect.Constructor;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.util.List;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Test;


/**
 * Test signing using all available digest methods
 */
public class SignatureDigestMethodTest extends CamelTestSupport {
    private static String payload;

    private KeyPair keyPair;

    static {
        boolean includeNewLine = true;
        if ((TestSupport.getJavaMajorVersion()) >= 9) {
            includeNewLine = false;
        }
        SignatureDigestMethodTest.payload = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (includeNewLine ? "\n" : "")) + "<root xmlns=\"http://test/test\"><test>Test Message</test></root>";
    }

    public SignatureDigestMethodTest() throws Exception {
        // BouncyCastle is required for some algorithms
        if ((Security.getProvider("BC")) == null) {
            Constructor<?> cons;
            Class<?> c = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            cons = c.getConstructor(new Class[]{  });
            Provider provider = ((Provider) (cons.newInstance()));
            Security.insertProviderAt(provider, 2);
        }
    }

    @Test
    public void testSHA1() throws Exception {
        setupMock();
        sendBody("direct:sha1", SignatureDigestMethodTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSHA224() throws Exception {
        setupMock();
        sendBody("direct:sha224", SignatureDigestMethodTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSHA256() throws Exception {
        setupMock();
        sendBody("direct:sha256", SignatureDigestMethodTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSHA384() throws Exception {
        setupMock();
        sendBody("direct:sha384", SignatureDigestMethodTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSHA512() throws Exception {
        setupMock();
        sendBody("direct:sha512", SignatureDigestMethodTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRIPEMD160() throws Exception {
        setupMock();
        sendBody("direct:ripemd160", SignatureDigestMethodTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testWHIRLPOOL() throws Exception {
        setupMock();
        sendBody("direct:whirlpool", SignatureDigestMethodTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSHA3224() throws Exception {
        setupMock();
        sendBody("direct:sha3_224", SignatureDigestMethodTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSHA3256() throws Exception {
        setupMock();
        sendBody("direct:sha3_256", SignatureDigestMethodTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSHA3384() throws Exception {
        setupMock();
        sendBody("direct:sha3_384", SignatureDigestMethodTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSHA3512() throws Exception {
        setupMock();
        sendBody("direct:sha3_512", SignatureDigestMethodTest.payload);
        assertMockEndpointsSatisfied();
    }

    /**
     * KeySelector which retrieves the public key from the KeyValue element and
     * returns it. NOTE: If the key algorithm doesn't match signature algorithm,
     * then the public key will be ignored.
     */
    static class KeyValueKeySelector extends KeySelector {
        public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method, XMLCryptoContext context) throws KeySelectorException {
            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }
            SignatureMethod sm = ((SignatureMethod) (method));
            @SuppressWarnings("rawtypes")
            List list = keyInfo.getContent();
            for (int i = 0; i < (list.size()); i++) {
                XMLStructure xmlStructure = ((XMLStructure) (list.get(i)));
                if (xmlStructure instanceof KeyValue) {
                    PublicKey pk = null;
                    try {
                        pk = ((KeyValue) (xmlStructure)).getPublicKey();
                    } catch (KeyException ke) {
                        throw new KeySelectorException(ke);
                    }
                    // make sure algorithm is compatible with method
                    if (SignatureDigestMethodTest.KeyValueKeySelector.algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                        return new SignatureDigestMethodTest.SimpleKeySelectorResult(pk);
                    }
                }
            }
            throw new KeySelectorException("No KeyValue element found!");
        }

        static boolean algEquals(String algURI, String algName) {
            return ((algName.equalsIgnoreCase("DSA")) && (algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1))) || ((algName.equalsIgnoreCase("RSA")) && (algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)));
        }
    }

    private static class SimpleKeySelectorResult implements KeySelectorResult {
        private PublicKey pk;

        SimpleKeySelectorResult(PublicKey pk) {
            this.pk = pk;
        }

        public Key getKey() {
            return pk;
        }
    }
}

