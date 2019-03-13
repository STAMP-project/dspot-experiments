/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 *
 *
 * @author Vera Y. Petrashkova
 * @version $Revision$
 */
package org.apache.harmony.security.tests.java.security;


import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import junit.framework.TestCase;


/**
 * Tests for <code>KeyStore.PrivateKeyEntry</code>  class constructor and methods
 */
public class KSPrivateKeyEntryTest extends TestCase {
    private PrivateKey testPrivateKey;

    private Certificate[] testChain;

    /**
     * Test for <code>PrivateKeyEntry(PrivateKey privateKey, Certificate[] chain)</code>
     * constructor
     * Assertion: throws NullPointerException when privateKey is null
     */
    public void testPrivateKeyEntry01() {
        Certificate[] certs = new MyCertificate[1];// new Certificate[1];

        PrivateKey pk = null;
        try {
            new KeyStore.PrivateKeyEntry(pk, certs);
            TestCase.fail("NullPointerException must be thrown when privateKey is null");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test for <code>PrivateKeyEntry(PrivateKey privateKey, Certificate[] chain)</code>
     * constructor
     * Assertion: throws NullPointerException when chain is null
     * and throws IllegalArgumentException when chain length is 0
     */
    public void testPrivateKeyEntry02() {
        Certificate[] chain = null;
        PrivateKey pk = new KSPrivateKeyEntryTest.tmpPrivateKey();
        try {
            new KeyStore.PrivateKeyEntry(pk, chain);
            TestCase.fail("NullPointerException must be thrown when chain is null");
        } catch (NullPointerException e) {
        }
        try {
            chain = new Certificate[0];
            new KeyStore.PrivateKeyEntry(pk, chain);
            TestCase.fail("IllegalArgumentException must be thrown when chain length is 0");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test for <code>PrivateKeyEntry(PrivateKey privateKey, Certificate[] chain)</code>
     * constructor
     * Assertion: throws IllegalArgumentException when chain contains certificates
     * of different types
     */
    public void testPrivateKeyEntry03() {
        createParams(true, false);
        try {
            new KeyStore.PrivateKeyEntry(testPrivateKey, testChain);
            TestCase.fail("IllegalArgumentException must be thrown when chain contains certificates of different types");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test for <code>PrivateKeyEntry(PrivateKey privateKey, Certificate[] chain)</code>
     * constructor
     * Assertion: throws IllegalArgumentException when algorithm of privateKey
     * does not match the algorithm of PublicKey in the end certificate (with 0 index)
     */
    public void testPrivateKeyEntry04() {
        createParams(false, true);
        try {
            new KeyStore.PrivateKeyEntry(testPrivateKey, testChain);
            TestCase.fail("IllegalArgumentException must be thrown when key algorithms do not match");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test for <code>getPrivateKey()</code> method
     * Assertion: returns PrivateKey object
     */
    public void testGetPrivateKey() {
        createParams(false, false);
        KeyStore.PrivateKeyEntry ksPKE = new KeyStore.PrivateKeyEntry(testPrivateKey, testChain);
        TestCase.assertEquals("Incorrect PrivateKey", testPrivateKey, ksPKE.getPrivateKey());
    }

    /**
     * Test for <code>getCertificateChain()</code> method Assertion: returns
     * array of the Certificates corresponding to chain
     */
    public void testGetCertificateChain() {
        createParams(false, false);
        KeyStore.PrivateKeyEntry ksPKE = new KeyStore.PrivateKeyEntry(testPrivateKey, testChain);
        Certificate[] res = ksPKE.getCertificateChain();
        TestCase.assertEquals("Incorrect chain length", testChain.length, res.length);
        for (int i = 0; i < (res.length); i++) {
            TestCase.assertEquals("Incorrect chain element: ".concat(Integer.toString(i)), testChain[i], res[i]);
        }
    }

    /**
     * Test for <code>getCertificate()</code> method
     * Assertion: returns end Certificate (with 0 index in chain)
     */
    public void testGetCertificate() {
        createParams(false, false);
        KeyStore.PrivateKeyEntry ksPKE = new KeyStore.PrivateKeyEntry(testPrivateKey, testChain);
        Certificate res = ksPKE.getCertificate();
        TestCase.assertEquals("Incorrect end certificate (number 0)", testChain[0], res);
    }

    /**
     * Test for <code>toString()</code> method
     * Assertion: returns non null String
     */
    public void testToString() {
        createParams(false, false);
        KeyStore.PrivateKeyEntry ksPKE = new KeyStore.PrivateKeyEntry(testPrivateKey, testChain);
        String res = ksPKE.toString();
        TestCase.assertNotNull("toString() returns null", res);
    }

    private static class tmpPrivateKey implements PrivateKey {
        private String alg = "My algorithm";

        public String getAlgorithm() {
            return alg;
        }

        public String getFormat() {
            return "My Format";
        }

        public byte[] getEncoded() {
            return new byte[1];
        }

        public tmpPrivateKey() {
        }

        public tmpPrivateKey(String algorithm) {
            super();
            alg = algorithm;
        }
    }
}

