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
package org.apache.harmony.security.tests.java.security;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class MessageDigest2Test extends TestCase {
    private static final String MESSAGEDIGEST_ID = "MessageDigest.";

    private Map<Provider, List<String>> digestAlgs = new HashMap<Provider, List<String>>();

    private static final byte[] AR1 = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

    private static final byte[] AR2 = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

    private static final String MESSAGE = "abc";

    private static final byte[] MESSAGE_DIGEST = new byte[]{ -87, -103, 62, 54, 71, 6, -127, 106, -70, 62, 37, 113, 120, 80, -62, 108, -100, -48, -40, -99 };

    private static final byte[] MESSAGE_DIGEST_63_As = new byte[]{ 3, -16, -97, 91, 21, -118, 122, -116, -38, -39, 32, -67, -36, 41, -72, 28, 24, -91, 81, -11 };

    private static final byte[] MESSAGE_DIGEST_64_As = new byte[]{ 0, -104, -70, -126, 75, 92, 22, 66, 123, -41, -95, 18, 42, 90, 68, 42, 37, -20, 100, 77 };

    private static final byte[] MESSAGE_DIGEST_65_As = new byte[]{ 17, 101, 83, 38, -57, 8, -41, 3, 25, -66, 38, 16, -24, -91, 125, -102, 91, -107, -99, 59 };

    /**
     * java.security.MessageDigest#MessageDigest(java.lang.String)
     */
    public void test_constructor() {
        for (List<String> algorithms : digestAlgs.values()) {
            for (String algorithm : algorithms) {
                MessageDigest2Test.MessageDigestStub md = new MessageDigest2Test.MessageDigestStub(algorithm);
                TestCase.assertEquals(algorithm, md.getAlgorithm());
                TestCase.assertEquals(0, md.getDigestLength());
                TestCase.assertNull(md.getProvider());
            }
        }
    }

    /**
     * java.security.MessageDigest#clone()
     */
    public void test_clone() throws Exception {
        for (Map.Entry<Provider, List<String>> e : digestAlgs.entrySet()) {
            for (String algorithm : e.getValue()) {
                MessageDigest d1 = MessageDigest.getInstance(algorithm, e.getKey().getName());
                for (byte b = 0; b < 84; b++) {
                    d1.update(b);
                }
                MessageDigest d2 = ((MessageDigest) (d1.clone()));
                d1.update(((byte) (1)));
                d2.update(((byte) (1)));
                TestCase.assertTrue(("cloned hash differs from original for algorithm " + algorithm), MessageDigest.isEqual(d1.digest(), d2.digest()));
            }
        }
    }

    private static final byte[] SHA_DATA_2 = new byte[]{ 70, -54, 124, 120, -29, 57, 56, 119, -108, -54, -97, -76, -97, -50, -63, -73, 2, 85, -53, -79 };

    private static final byte[] SHA_DATA_1 = new byte[]{ 90, 36, 111, 106, -32, 38, 4, 126, 21, -51, 107, 45, -64, -68, -109, 112, -31, -46, 34, 115 };

    /**
     * java.security.MessageDigest#digest()
     */
    public void test_digest() throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA");
        TestCase.assertNotNull(sha);
        sha.update(MessageDigest2Test.MESSAGE.getBytes());
        byte[] digest = sha.digest();
        TestCase.assertTrue("bug in SHA", MessageDigest.isEqual(digest, MessageDigest2Test.MESSAGE_DIGEST));
        sha.reset();
        for (int i = 0; i < 63; i++) {
            // just under buffer capacity
            sha.update(((byte) ('a')));
        }
        digest = sha.digest();
        TestCase.assertTrue("bug in SHA", MessageDigest.isEqual(digest, MessageDigest2Test.MESSAGE_DIGEST_63_As));
        sha.reset();
        for (int i = 0; i < 64; i++) {
            // exact SHA buffer capacity
            sha.update(((byte) ('a')));
        }
        digest = sha.digest();
        TestCase.assertTrue("bug in SHA", MessageDigest.isEqual(digest, MessageDigest2Test.MESSAGE_DIGEST_64_As));
        sha.reset();
        for (int i = 0; i < 65; i++) {
            // just above SHA buffer capacity
            sha.update(((byte) ('a')));
        }
        digest = sha.digest();
        TestCase.assertTrue("bug in SHA", MessageDigest.isEqual(digest, MessageDigest2Test.MESSAGE_DIGEST_65_As));
        testSerializationSHA_DATA_1(sha);
        testSerializationSHA_DATA_2(sha);
    }

    /**
     * java.security.MessageDigest#digest(byte[])
     */
    public void test_digest$B() throws Exception {
        for (Map.Entry<Provider, List<String>> e : digestAlgs.entrySet()) {
            for (String algorithm : e.getValue()) {
                MessageDigest digest = MessageDigest.getInstance(algorithm, e.getKey().getName());
                TestCase.assertNotNull(digest);
                digest.digest(MessageDigest2Test.AR1);
            }
        }
    }

    /**
     * java.security.MessageDigest#digest(byte[], int, int)
     */
    public void test_digest$BII() throws Exception {
        for (Map.Entry<Provider, List<String>> e : digestAlgs.entrySet()) {
            for (String algorithm : e.getValue()) {
                MessageDigest digest = MessageDigest.getInstance(algorithm, e.getKey().getName());
                TestCase.assertNotNull(digest);
                int len = digest.getDigestLength();
                byte[] digestBytes = new byte[len];
                digest.digest(digestBytes, 0, digestBytes.length);
            }
            try {
                MessageDigest.getInstance("SHA").digest(new byte[]{  }, Integer.MAX_VALUE, 755);
                TestCase.fail();
            } catch (IllegalArgumentException expected) {
            }
        }
    }

    /**
     * java.security.MessageDigest#update(byte[], int, int)
     */
    public void test_update$BII() throws Exception {
        try {
            MessageDigest.getInstance("SHA").update(new byte[]{  }, Integer.MAX_VALUE, Integer.MAX_VALUE);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * java.security.MessageDigest#getAlgorithm()
     */
    public void test_getAlgorithm() throws Exception {
        for (Map.Entry<Provider, List<String>> e : digestAlgs.entrySet()) {
            for (String algorithm : e.getValue()) {
                MessageDigest md = MessageDigest.getInstance(algorithm, e.getKey().getName());
                TestCase.assertEquals(algorithm, md.getAlgorithm());
            }
        }
    }

    /**
     * java.security.MessageDigest#getDigestLength()
     */
    public void test_getDigestLength() throws Exception {
        for (Map.Entry<Provider, List<String>> e : digestAlgs.entrySet()) {
            for (String algorithm : e.getValue()) {
                MessageDigest md = MessageDigest.getInstance(algorithm, e.getKey().getName());
                TestCase.assertTrue("length not ok", ((md.getDigestLength()) > 0));
            }
        }
    }

    /**
     * java.security.MessageDigest#getInstance(java.lang.String)
     */
    public void test_getInstanceLjava_lang_String() throws Exception {
        for (Map.Entry<Provider, List<String>> e : digestAlgs.entrySet()) {
            for (String algorithm : e.getValue()) {
                MessageDigest md = MessageDigest.getInstance(algorithm);
                TestCase.assertNotNull(md);
            }
        }
        try {
            MessageDigest.getInstance("UnknownDigest");
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException expected) {
        }
    }

    /**
     * java.security.MessageDigest#getInstance(java.lang.String,
     *        java.lang.String)
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String() throws Exception {
        for (Map.Entry<Provider, List<String>> e : digestAlgs.entrySet()) {
            for (String algorithm : e.getValue()) {
                MessageDigest md = MessageDigest.getInstance(algorithm, e.getKey().getName());
                TestCase.assertNotNull(md);
            }
        }
        for (List<String> algorithms : digestAlgs.values()) {
            for (String algorithm : algorithms) {
                try {
                    MessageDigest.getInstance(algorithm, "UnknownProvider");
                    TestCase.fail("expected NoSuchProviderException");
                } catch (NoSuchProviderException expected) {
                }
            }
        }
        for (Provider provider : digestAlgs.keySet()) {
            try {
                MessageDigest.getInstance("UnknownDigest", provider.getName());
                TestCase.fail("expected NoSuchAlgorithmException");
            } catch (NoSuchAlgorithmException expected) {
            }
        }
        for (Provider provider : digestAlgs.keySet()) {
            try {
                MessageDigest.getInstance(null, provider.getName());
                TestCase.fail("expected NullPointerException");
            } catch (NullPointerException expected) {
            }
        }
        try {
            MessageDigest.getInstance("AnyDigest", ((String) (null)));
            TestCase.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * java.security.MessageDigest#getInstance(java.lang.String,
     *        java.security.Provider)
     */
    public void test_getInstanceLjava_lang_StringLjava_security_Provider() throws Exception {
        for (Map.Entry<Provider, List<String>> e : digestAlgs.entrySet()) {
            for (String algorithm : e.getValue()) {
                MessageDigest md = MessageDigest.getInstance(algorithm, e.getKey().getName());
                TestCase.assertNotNull(md);
            }
        }
        try {
            MessageDigest.getInstance(null, new MessageDigest2Test.TestProvider());
            TestCase.fail("expected NullPointerException");
        } catch (NullPointerException expected) {
        }
        try {
            MessageDigest.getInstance("UnknownDigest", new MessageDigest2Test.TestProvider());
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException expected) {
        }
        try {
            MessageDigest.getInstance("AnyDigest", ((Provider) (null)));
            TestCase.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * java.security.MessageDigest#getProvider()
     */
    public void test_getProvider() throws Exception {
        for (Map.Entry<Provider, List<String>> e : digestAlgs.entrySet()) {
            for (String algorithm : e.getValue()) {
                MessageDigest md = MessageDigest.getInstance(algorithm, e.getKey().getName());
                TestCase.assertNotNull("provider is null", md.getProvider());
            }
        }
    }

    /**
     * java.security.MessageDigest#isEqual(byte[], byte[])
     */
    public void test_isEqual$B$B() {
        TestCase.assertTrue("isEqual is not correct", MessageDigest.isEqual(MessageDigest2Test.AR1, MessageDigest2Test.AR2));
    }

    /**
     * java.security.MessageDigest#toString()
     */
    public void test_toString() throws Exception {
        String str = MessageDigest.getInstance("SHA").toString();
        TestCase.assertNotNull("toString is null", str);
    }

    private class MessageDigestStub extends MessageDigest {
        public MessageDigestStub(String algorithm) {
            super(algorithm);
        }

        public byte[] engineDigest() {
            return null;
        }

        public void engineReset() {
        }

        public void engineUpdate(byte input) {
        }

        public void engineUpdate(byte[] input, int offset, int len) {
        }
    }

    private static class TestProvider extends Provider {
        protected TestProvider() {
            super("TestProvider", 1.0, "INFO");
        }
    }
}

