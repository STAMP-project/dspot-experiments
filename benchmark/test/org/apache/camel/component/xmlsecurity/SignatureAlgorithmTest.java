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
 * Test signing using all available signature methods, apart from EC-algorithms which are
 * tested in ECDSASignatureTest.
 */
public class SignatureAlgorithmTest extends CamelTestSupport {
    private static String payload;

    private KeyPair keyPair;

    static {
        boolean includeNewLine = true;
        if ((TestSupport.getJavaMajorVersion()) >= 9) {
            includeNewLine = false;
        }
        SignatureAlgorithmTest.payload = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (includeNewLine ? "\n" : "")) + "<root xmlns=\"http://test/test\"><test>Test Message</test></root>";
    }

    public SignatureAlgorithmTest() throws Exception {
        // BouncyCastle is required for some algorithms
        if ((Security.getProvider("BC")) == null) {
            Constructor<?> cons;
            Class<?> c = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            cons = c.getConstructor(new Class[]{  });
            Provider provider = ((Provider) (cons.newInstance()));
            Security.insertProviderAt(provider, 2);
        }
    }

    // 
    // Secret Key algorithms
    // 
    @Test
    public void testHMACSHA1() throws Exception {
        setupMock();
        sendBody("direct:hmacsha1", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testHMACSHA224() throws Exception {
        setupMock();
        sendBody("direct:hmacsha224", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testHMACSHA256() throws Exception {
        setupMock();
        sendBody("direct:hmacsha256", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testHMACSHA384() throws Exception {
        setupMock();
        sendBody("direct:hmacsha384", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testHMACSHA512() throws Exception {
        setupMock();
        sendBody("direct:hmacsha512", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testHMACRIPEMD160() throws Exception {
        setupMock();
        sendBody("direct:hmacripemd160", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    // 
    // Public Key algorithms
    // 
    @Test
    public void testRSASHA1() throws Exception {
        setupMock();
        sendBody("direct:rsasha1", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSASHA224() throws Exception {
        setupMock();
        sendBody("direct:rsasha224", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSASHA256() throws Exception {
        setupMock();
        sendBody("direct:rsasha256", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSASHA384() throws Exception {
        setupMock();
        sendBody("direct:rsasha384", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSASHA512() throws Exception {
        setupMock();
        sendBody("direct:rsasha512", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSARIPEMD160() throws Exception {
        setupMock();
        sendBody("direct:rsaripemd160", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSASHA1MGF1() throws Exception {
        setupMock();
        sendBody("direct:rsasha1_mgf1", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSASHA224MGF1() throws Exception {
        setupMock();
        sendBody("direct:rsasha224_mgf1", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSASHA256MGF1() throws Exception {
        setupMock();
        sendBody("direct:rsasha256_mgf1", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSASHA384MGF1() throws Exception {
        setupMock();
        sendBody("direct:rsasha384_mgf1", SignatureAlgorithmTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRSASHA512MGF1() throws Exception {
        setupMock();
        sendBody("direct:rsasha512_mgf1", SignatureAlgorithmTest.payload);
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
                    if (SignatureAlgorithmTest.KeyValueKeySelector.algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                        return new SignatureAlgorithmTest.SimpleKeySelectorResult(pk);
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

