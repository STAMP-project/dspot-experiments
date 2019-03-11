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


import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.spec.DSAParameterSpec;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import libcore.java.security.StandardNames;


public class Signature2Test extends TestCase {
    private static final String MESSAGE = "abc";

    private static KeyPair DSA_KEYS;

    private static KeyPair RSA_KEYS;

    /**
     * java.security.Signature#clone()
     */
    public void test_clone() throws Exception {
        Signature s = Signature.getInstance("DSA");
        try {
            s.clone();
            TestCase.fail();
        } catch (CloneNotSupportedException expected) {
        }
    }

    /**
     * java.security.Signature#getAlgorithm()
     */
    public void test_getAlgorithm() throws Exception {
        String alg = Signature.getInstance("DSA").getAlgorithm();
        TestCase.assertTrue((("getAlgorithm did not get DSA (" + alg) + ")"), ((alg.indexOf("DSA")) != (-1)));
    }

    /**
     * java.security.Signature#getInstance(java.lang.String)
     */
    public void test_getInstanceLjava_lang_String() throws Exception {
        Signature.getInstance("DSA");
        try {
            Signature.getInstance("bogus");
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        }
    }

    /**
     * java.security.Signature#getInstance(java.lang.String,
     *        java.security.Provider)
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String_java_security_Provider() throws Exception {
        Provider[] providers = Security.getProviders("Signature.DSA");
        for (int i = 0; i < (providers.length); i++) {
            Signature signature = Signature.getInstance("DSA", providers[i]);
            TestCase.assertEquals("DSA", signature.getAlgorithm());
            TestCase.assertEquals(providers[i], signature.getProvider());
        }
        try {
            Signature.getInstance(((String) (null)), ((Provider) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            Signature.getInstance("DSA", ((Provider) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Signature.getInstance(((String) (null)), providers[0]);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            Signature.getInstance("bogus", providers[0]);
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        }
    }

    /**
     * java.security.Signature#getInstance(java.lang.String,
     *        java.lang.String)
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String() throws Exception {
        Provider[] providers = Security.getProviders("Signature.DSA");
        for (int i = 0; i < (providers.length); i++) {
            Signature.getInstance("DSA", providers[i].getName());
        }
        try {
            Signature.getInstance("bogus", providers[0].getName());
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        }
        Provider[] pp = Security.getProviders();
        for (int i = 0; i < (pp.length); i++) {
            try {
                Signature.getInstance("DSA", pp[i].toString());
                TestCase.fail(pp[i].toString());
            } catch (NoSuchProviderException expected) {
            }
        }
        String[] sp = new String[]{ null, "" };
        for (int i = 0; i < (sp.length); i++) {
            try {
                Signature.getInstance("DSA", sp[i]);
                TestCase.fail(sp[i]);
            } catch (IllegalArgumentException expected) {
            }
        }
    }

    /**
     * java.security.Signature#getParameters()
     */
    public void test_getParameters() throws Exception {
        Signature sig = Signature.getInstance("DSA");
        try {
            sig.getParameters();
        } catch (UnsupportedOperationException e) {
            // Could be that the operation is not supported
        }
        try {
            Signature2Test.MySignature sig2 = new Signature2Test.MySignature("test");
            sig2.getParameters();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        Signature2Test.MySignature sig2 = new Signature2Test.MySignature("ABC");
        sig2.getParameters();
    }

    /**
     * java.security.Signature#getProvider()
     */
    public void test_getProvider() throws Exception {
        Provider p = Signature.getInstance("DSA").getProvider();
        TestCase.assertNotNull("provider is null", p);
    }

    /**
     * java.security.Signature#initSign(java.security.PrivateKey)
     */
    public void test_initSignLjava_security_PrivateKey() throws Exception {
        Signature.getInstance("DSA").initSign(Signature2Test.getDsaKeys().getPrivate());
        try {
            Signature.getInstance("DSA").initSign(Signature2Test.getRsaKeys().getPrivate());
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
    }

    public void test_initSignLjava_security_PrivateKeyLjava_security_SecureRandom() throws Exception {
        Signature sig = Signature.getInstance("DSA");
        sig.initSign(Signature2Test.getDsaKeys().getPrivate(), new SecureRandom());
    }

    public void test_initSignLjava_security_PrivateKeyLjava_security_SecureRandom_mismatch() throws Exception {
        try {
            Signature sig = Signature.getInstance("DSA");
            sig.initSign(Signature2Test.getRsaKeys().getPrivate(), new SecureRandom());
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
    }

    /**
     * java.security.Signature#initVerify(java.security.PublicKey)
     */
    public void test_initVerifyLjava_security_PublicKey() throws Exception {
        Signature.getInstance("DSA").initVerify(Signature2Test.getDsaKeys().getPublic());
        try {
            Signature.getInstance("DSA").initVerify(Signature2Test.getRsaKeys().getPublic());
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
    }

    /**
     * java.security.Signature#initVerify(java.security.cert.Certificate)
     */
    public void test_initVerifyLjava_security_Certificate() throws Exception {
        Provider p = new Signature2Test.MyProvider();
        p.put("DSA", "tests.java.security.support.cert.MyCertificate$1");
        Provider myProvider = new Signature2Test.MyProvider();
        Security.addProvider(myProvider);
        try {
            Provider[] pp = Security.getProviders();
            if (pp == null) {
                return;
            }
            try {
                Signature.getInstance("DSA").initVerify(((Certificate) (null)));
                TestCase.fail();
            } catch (NullPointerException expected) {
            }
        } finally {
            Security.removeProvider(myProvider.getName());
        }
    }

    /**
     * java.security.Signature#setParameter(java.security.spec.AlgorithmParameterSpec)
     */
    public void test_setParameterLjava_security_spec_AlgorithmParameterSpec() throws Exception {
        Signature sig = Signature.getInstance("DSA");
        try {
            DSAParameterSpec spec = new DSAParameterSpec(BigInteger.ONE, BigInteger.ONE, BigInteger.ONE);
            sig.setParameter(spec);
        } catch (InvalidParameterException e) {
            // Could be that it's an invalid param for the found algorithm
        } catch (UnsupportedOperationException e) {
            // Could be that the operation is not supported
        }
    }

    /**
     * java.security.Signature#sign()
     */
    public void test_sign() throws Exception {
        Signature sig = Signature.getInstance("DSA");
        sig.initSign(Signature2Test.getDsaKeys().getPrivate());
        sig.update(Signature2Test.MESSAGE.getBytes());
        sig.sign();
    }

    /**
     * java.security.Signature#toString()
     */
    public void test_toString() throws Exception {
        String str = Signature.getInstance("DSA").toString();
        TestCase.assertNotNull("toString is null", str);
    }

    /**
     * java.security.Signature#update(byte[])
     */
    public void test_update$B() throws Exception {
        Signature sig = Signature.getInstance("DSA");
        sig.initSign(Signature2Test.getDsaKeys().getPrivate());
        byte[] bytes = Signature2Test.MESSAGE.getBytes();
        sig.update(bytes);
        try {
            Signature sig2 = Signature.getInstance("DSA");
            sig2.update(Signature2Test.MESSAGE.getBytes());
            TestCase.fail();
        } catch (SignatureException expected) {
        }
    }

    /**
     * java.security.Signature#update(byte[], int, int)
     */
    public void test_update$BII() throws Exception {
        Signature sig = Signature.getInstance("DSA");
        byte[] bytes = Signature2Test.MESSAGE.getBytes();
        try {
            sig.update(bytes, 0, bytes.length);
            TestCase.fail();
        } catch (SignatureException expected) {
        }
        sig.initSign(Signature2Test.getDsaKeys().getPrivate());
        sig.update(bytes, 0, bytes.length);
        sig.update(bytes, ((bytes.length) - 2), 2);
        try {
            sig.update(bytes, ((bytes.length) - 3), 4);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            sig.update(null, 0, 5);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * java.security.Signature#update(byte)
     */
    public void test_updateB() throws Exception {
        Signature sig = Signature.getInstance("DSA");
        sig.initSign(Signature2Test.getDsaKeys().getPrivate());
        sig.update(Signature2Test.MESSAGE.getBytes()[0]);
    }

    /**
     * java.security.Signature#update(ByteBuffer data)
     */
    public void test_updateLjava_nio_ByteBuffer() throws Exception {
        Signature sig = Signature.getInstance("DSA");
        ByteBuffer buffer = ByteBuffer.allocate(10);
        try {
            sig.update(buffer);
            TestCase.fail();
        } catch (SignatureException expected) {
        }
        sig.initSign(Signature2Test.getDsaKeys().getPrivate());
        sig.update(buffer);
    }

    /**
     * java.security.Signature#verify(byte[])
     */
    public void test_verify$B() throws Exception {
        Signature sig = Signature.getInstance("DSA");
        try {
            sig.verify(new byte[]{ 0, 1, 2, 3 });
            TestCase.fail();
        } catch (SignatureException expected) {
        }
        sig.initSign(Signature2Test.getDsaKeys().getPrivate());
        sig.update(Signature2Test.MESSAGE.getBytes());
        byte[] signature = sig.sign();
        sig.initVerify(Signature2Test.getDsaKeys().getPublic());
        sig.update(Signature2Test.MESSAGE.getBytes());
        TestCase.assertTrue("Sign/Verify does not pass", sig.verify(signature));
    }

    /**
     * java.security.Signature#verify(byte[], int, int)
     */
    public void test_verify$BII() throws Exception {
        Signature sig = Signature.getInstance("DSA");
        sig.initSign(Signature2Test.getDsaKeys().getPrivate());
        sig.update(Signature2Test.MESSAGE.getBytes());
        byte[] signature = sig.sign();
        sig.initVerify(Signature2Test.getDsaKeys().getPublic());
        sig.update(Signature2Test.MESSAGE.getBytes());
        TestCase.assertTrue("Sign/Verify does not pass", sig.verify(signature, 0, signature.length));
        try {
            sig.verify(null, 0, signature.length);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            sig.verify(signature, (-5), signature.length);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        if (StandardNames.IS_RI) {
            try {
                sig.verify(signature, signature.length, 0);
                TestCase.fail();
            } catch (SignatureException expected) {
            }
        } else {
            // Calling Signature.verify a second time should not throw
            // http://code.google.com/p/android/issues/detail?id=34933
            boolean verified = sig.verify(signature, signature.length, 0);
            TestCase.assertFalse(verified);
        }
        try {
            sig.verify(signature, 0, ((signature.length) * 2));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    class MyProvider extends Provider {
        private Set<Provider.Service> services = null;

        MyProvider() {
            super("MyProvider", 1.0, "Provider for testing");
            put("MessageDigest.SHA-1", "SomeClassName");
            put("MessageDigest.abc", "SomeClassName");
            put("Alg.Alias.MessageDigest.SHA1", "SHA-1");
            if ((services) != null) {
                services.clear();
            } else {
                services = new HashSet<Provider.Service>();
            }
        }

        MyProvider(String name, double version, String info) {
            super(name, version, info);
            if ((services) != null) {
                services.clear();
            } else {
                services = new HashSet<Provider.Service>();
            }
        }

        public void putService(Provider.Service s) {
            super.putService(s);
            services.add(s);
        }

        public void removeService(Provider.Service s) {
            super.removeService(s);
            services.remove(s);
        }

        public int getNumServices() {
            return services.size();
        }
    }

    @SuppressWarnings("unused")
    private class MySignature extends Signature {
        protected MySignature(String algorithm) {
            super(algorithm);
        }

        @Override
        protected Object engineGetParameter(String param) throws InvalidParameterException {
            return null;
        }

        @Override
        protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        }

        @Override
        protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        }

        @Override
        protected void engineSetParameter(String param, Object value) throws InvalidParameterException {
        }

        @Override
        protected byte[] engineSign() throws SignatureException {
            return null;
        }

        @Override
        protected void engineUpdate(byte b) throws SignatureException {
        }

        @Override
        protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {
        }

        @Override
        protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
            return false;
        }

        @Override
        protected AlgorithmParameters engineGetParameters() {
            if (this.getAlgorithm().equals("test")) {
                return super.engineGetParameters();
            } else {
                return null;
            }
        }
    }
}

