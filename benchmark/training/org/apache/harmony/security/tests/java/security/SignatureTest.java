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
 * @author Boris V. Kuznetsov
 * @version $Revision$
 */
package org.apache.harmony.security.tests.java.security;


import MySignature1.SIGN;
import MySignature1.UNINITIALIZED;
import MySignature1.VERIFY;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.MySignature1;


/**
 * Tests for <code>Signature</code> constructor and methods
 */
public class SignatureTest extends TestCase {
    /* Class under test for Signature(String) */
    public void testConstructor() {
        String[] algorithms = new String[]{ "SHA256WITHRSA", "NONEWITHDSA", "SHA384WITHRSA", "MD5ANDSHA1WITHRSA", "SHA512WITHRSA", "SHA1WITHRSA", "SHA1WITHDSA", "MD5WITHRSA" };
        for (int i = 0; i < (algorithms.length); i++) {
            MySignature1 s = new MySignature1(algorithms[i]);
            TestCase.assertEquals(algorithms[i], s.getAlgorithm());
            TestCase.assertNull(s.getProvider());
            TestCase.assertEquals(0, s.getState());
        }
        MySignature1 s1 = new MySignature1(null);
        TestCase.assertNull(s1.getAlgorithm());
        TestCase.assertNull(s1.getProvider());
        TestCase.assertEquals(0, s1.getState());
        MySignature1 s2 = new MySignature1("ABCD@#&^%$)(*&");
        TestCase.assertEquals("ABCD@#&^%$)(*&", s2.getAlgorithm());
        TestCase.assertNull(s2.getProvider());
        TestCase.assertEquals(0, s2.getState());
    }

    /* Class under test for Object clone() */
    public void testClone() {
        MySignature1 s = new MySignature1("ABC");
        try {
            s.clone();
            TestCase.fail("No expected CloneNotSupportedException");
        } catch (CloneNotSupportedException e) {
        }
        SignatureTest.MySignature sc = new SignatureTest.MySignature();
        try {
            sc.clone();
        } catch (CloneNotSupportedException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    public void testGetProvider() {
        MySignature1 s = new MySignature1("ABC");
        TestCase.assertEquals("state", UNINITIALIZED, s.getState());
        TestCase.assertNull("provider", s.getProvider());
    }

    public void testGetAlgorithm() {
        MySignature1 s = new MySignature1("ABC");
        TestCase.assertEquals("state", UNINITIALIZED, s.getState());
        TestCase.assertEquals("algorithm", "ABC", s.getAlgorithm());
    }

    /* Class under test for void initVerify(PublicKey) */
    public void testInitVerifyPublicKey() throws InvalidKeyException {
        MySignature1 s = new MySignature1("ABC");
        s.initVerify(new SignatureTest.MyPublicKey());
        TestCase.assertEquals("state", VERIFY, s.getState());
        TestCase.assertTrue("initVerify() failed", s.runEngineInitVerify);
        try {
            Signature sig = getTestSignature();
            sig.initVerify(((PublicKey) (null)));
        } catch (InvalidKeyException e) {
            // ok
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected : " + e));
        }
    }

    /* Class under test for void initVerify(Certificate) */
    public void testInitVerifyCertificate() throws InvalidKeyException {
        MySignature1 s = new MySignature1("ABC");
        s.initVerify(new SignatureTest.MyCertificate());
        TestCase.assertEquals("state", VERIFY, s.getState());
        TestCase.assertTrue("initVerify() failed", s.runEngineInitVerify);
        try {
            Signature sig = getTestSignature();
            sig.initVerify(new SignatureTest.MyCertificate());
            TestCase.fail("expected InvalidKeyException");
        } catch (InvalidKeyException e) {
            // ok
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected : " + e));
        }
    }

    /* Class under test for void initSign(PrivateKey) */
    public void testInitSignPrivateKey() throws InvalidKeyException {
        MySignature1 s = new MySignature1("ABC");
        s.initSign(new SignatureTest.MyPrivateKey());
        TestCase.assertEquals("state", SIGN, s.getState());
        TestCase.assertTrue("initSign() failed", s.runEngineInitSign);
        try {
            Signature signature = getTestSignature();
            signature.initSign(null);
            TestCase.fail("expected InvalidKeyException");
        } catch (InvalidKeyException e) {
            // ok
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected: " + e));
        }
    }

    /* Class under test for void initSign(PrivateKey, SecureRandom) */
    public void testInitSignPrivateKeySecureRandom() throws InvalidKeyException {
        MySignature1 s = new MySignature1("ABC");
        s.initSign(new SignatureTest.MyPrivateKey(), new SecureRandom());
        TestCase.assertEquals("state", SIGN, s.getState());
        TestCase.assertTrue("initSign() failed", s.runEngineInitSign);
        try {
            Signature sig = getTestSignature();
            sig.initSign(null, null);
            TestCase.fail("expected InvalidKeyException");
        } catch (InvalidKeyException e) {
            // ok
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected : " + e));
        }
    }

    /* Class under test for byte[] sign() */
    public void testSign() throws Exception {
        MySignature1 s = new MySignature1("ABC");
        try {
            s.sign();
            TestCase.fail("No expected SignatureException");
        } catch (SignatureException e) {
        }
        s.initVerify(new SignatureTest.MyPublicKey());
        try {
            s.sign();
            TestCase.fail("No expected SignatureException");
        } catch (SignatureException e) {
        }
        s.initSign(new SignatureTest.MyPrivateKey());
        s.sign();
        TestCase.assertEquals("state", SIGN, s.getState());
        TestCase.assertTrue("sign() failed", s.runEngineSign);
    }

    /* Class under test for sign(byte[], offset, len) */
    public void testSignbyteintint() throws Exception {
        MySignature1 s = new MySignature1("ABC");
        byte[] outbuf = new byte[10];
        try {
            s.sign(outbuf, 0, outbuf.length);
            TestCase.fail("No expected SignatureException");
        } catch (SignatureException e) {
        }
        s.initVerify(new SignatureTest.MyPublicKey());
        try {
            s.sign(outbuf, 0, outbuf.length);
            TestCase.fail("No expected SignatureException");
        } catch (SignatureException e) {
        }
        s.initSign(new SignatureTest.MyPrivateKey());
        TestCase.assertEquals(s.getBufferLength(), s.sign(outbuf, 0, outbuf.length));
        TestCase.assertEquals("state", SIGN, s.getState());
        TestCase.assertTrue("sign() failed", s.runEngineSign);
        try {
            s.initSign(new SignatureTest.MyPrivateKey());
            s.sign(outbuf, outbuf.length, 0);
            TestCase.fail("expected SignatureException");
        } catch (SignatureException e) {
            // ok
        }
        try {
            s.initSign(new SignatureTest.MyPrivateKey());
            s.sign(outbuf, outbuf.length, 3);
            TestCase.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    /* Class under test for boolean verify(byte[]) */
    public void testVerifybyteArray() throws Exception {
        MySignature1 s = new MySignature1("ABC");
        byte[] b = new byte[]{ 1, 2, 3, 4 };
        try {
            s.verify(b);
            TestCase.fail("No expected SignatureException");
        } catch (SignatureException e) {
        }
        s.initSign(new SignatureTest.MyPrivateKey());
        try {
            s.verify(b);
            TestCase.fail("No expected SignatureException");
        } catch (SignatureException e) {
        }
        s.initVerify(new SignatureTest.MyPublicKey());
        s.verify(b);
        TestCase.assertEquals("state", VERIFY, s.getState());
        TestCase.assertTrue("verify() failed", s.runEngineVerify);
    }

    /* Class under test for boolean verify(byte[], int, int) */
    public void testVerifybyteArrayintint() throws Exception {
        MySignature1 s = new MySignature1("ABC");
        byte[] b = new byte[]{ 1, 2, 3, 4 };
        try {
            s.verify(b, 0, 3);
            TestCase.fail("No expected SignatureException");
        } catch (SignatureException e) {
        }
        s.initSign(new SignatureTest.MyPrivateKey());
        try {
            s.verify(b, 0, 3);
            TestCase.fail("No expected SignatureException");
        } catch (SignatureException e) {
        }
        s.initVerify(new SignatureTest.MyPublicKey());
        try {
            s.verify(b, 0, 5);
            TestCase.fail("No expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        s.verify(b, 0, 3);
        TestCase.assertEquals("state", VERIFY, s.getState());
        TestCase.assertTrue("verify() failed", s.runEngineVerify);
    }

    /* Class under test for void update(byte) */
    public void testUpdatebyte() throws Exception {
        MySignature1 s = new MySignature1("ABC");
        try {
            s.update(((byte) (1)));
            TestCase.fail("No expected SignatureException");
        } catch (SignatureException e) {
        }
        s.initVerify(new SignatureTest.MyPublicKey());
        s.update(((byte) (1)));
        s.initSign(new SignatureTest.MyPrivateKey());
        s.update(((byte) (1)));
        TestCase.assertEquals("state", SIGN, s.getState());
        TestCase.assertTrue("update() failed", s.runEngineUpdate1);
        try {
            Signature sig = getTestSignature();
            sig.update(((byte) (42)));
            TestCase.fail("expected SignatureException");
        } catch (SignatureException e) {
            // ok
        }
    }

    /* Class under test for void update(byte[]) */
    public void testUpdatebyteArray() throws Exception {
        MySignature1 s = new MySignature1("ABC");
        byte[] b = new byte[]{ 1, 2, 3, 4 };
        try {
            s.update(b);
            TestCase.fail("No expected SignatureException");
        } catch (SignatureException e) {
        }
        s.initVerify(new SignatureTest.MyPublicKey());
        s.update(b);
        s.initSign(new SignatureTest.MyPrivateKey());
        s.update(b);
        TestCase.assertEquals("state", SIGN, s.getState());
        TestCase.assertTrue("update() failed", s.runEngineUpdate2);
        try {
            Signature sig = getTestSignature();
            sig.update(b);
            TestCase.fail("expected SignatureException");
        } catch (SignatureException e) {
            // ok
        }
        try {
            Signature sig = getTestSignature();
            sig.update(((byte[]) (null)));
            TestCase.fail("expected NullPointerException");
        } catch (SignatureException e) {
            // ok
        } catch (NullPointerException e) {
            // ok
        }
    }

    /* Class under test for void update(byte[], int, int) */
    public void testUpdatebyteArrayintint() throws Exception {
        MySignature1 s = new MySignature1("ABC");
        byte[] b = new byte[]{ 1, 2, 3, 4 };
        try {
            s.update(b, 0, 3);
            TestCase.fail("No expected SignatureException");
        } catch (SignatureException e) {
        }
        s.initVerify(new SignatureTest.MyPublicKey());
        s.update(b, 0, 3);
        s.initSign(new SignatureTest.MyPrivateKey());
        s.update(b, 0, 3);
        TestCase.assertEquals("state", SIGN, s.getState());
        TestCase.assertTrue("update() failed", s.runEngineUpdate2);
        try {
            s.update(b, 3, 0);
            TestCase.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            s.update(b, 0, ((b.length) + 1));
            TestCase.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            s.update(b, (-1), b.length);
            TestCase.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    /* Class under test for void setParameter(AlgorithmParameterSpec) */
    public void testSetParameterAlgorithmParameterSpec() throws InvalidAlgorithmParameterException {
        MySignature1 s = new MySignature1("ABC");
        try {
            s.setParameter(((AlgorithmParameterSpec) (null)));
            TestCase.fail("No expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
        try {
            Signature sig = getTestSignature();
            sig.setParameter(new AlgorithmParameterSpec() {});
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected: " + e));
        }
    }

    private class MyKey implements Key {
        public String getFormat() {
            return "123";
        }

        public byte[] getEncoded() {
            return null;
        }

        public String getAlgorithm() {
            return "aaa";
        }
    }

    private class MyPublicKey extends SignatureTest.MyKey implements PublicKey {}

    private class MyPrivateKey extends SignatureTest.MyKey implements PrivateKey {}

    private class MyCertificate extends Certificate {
        public MyCertificate() {
            super("MyCertificateType");
        }

        public PublicKey getPublicKey() {
            return new SignatureTest.MyPublicKey();
        }

        public byte[] getEncoded() {
            return null;
        }

        public void verify(PublicKey key) {
        }

        public void verify(PublicKey key, String sigProvider) {
        }

        public String toString() {
            return "MyCertificate";
        }
    }

    @SuppressWarnings("unused")
    protected static class MySignature extends Signature implements Cloneable {
        public MySignature() {
            super("TestSignature");
        }

        @Override
        protected Object engineGetParameter(String param) throws InvalidParameterException {
            throw new InvalidParameterException();
        }

        @Override
        protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
            throw new InvalidKeyException();
        }

        @Override
        protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
            throw new InvalidKeyException();
        }

        @Override
        protected void engineSetParameter(String param, Object value) throws InvalidParameterException {
            throw new InvalidParameterException();
        }

        @Override
        protected byte[] engineSign() throws SignatureException {
            return null;
        }

        @Override
        protected void engineUpdate(byte b) throws SignatureException {
            throw new SignatureException();
        }

        @Override
        protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {
        }

        @Override
        protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
            return false;
        }

        @Override
        protected void engineSetParameter(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            if (params == null) {
                throw new InvalidAlgorithmParameterException();
            }
        }
    }

    private class MyProvider extends Provider {
        protected MyProvider(String name, double version, String info, String signame, String className) {
            super(name, version, info);
            put(signame, className);
        }
    }
}

