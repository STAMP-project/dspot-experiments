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
package org.apache.harmony.crypto.tests.javax.crypto;


import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import javax.crypto.ExemptionMechanism;
import javax.crypto.ExemptionMechanismException;
import javax.crypto.ExemptionMechanismSpi;
import javax.crypto.KeyGenerator;
import javax.crypto.ShortBufferException;
import junit.framework.TestCase;
import org.apache.harmony.crypto.tests.support.MyExemptionMechanismSpi;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for <code>ExemptionMechanism</code> class constructors and methods
 */
public class ExemptionMechanismTest extends TestCase {
    private static final String srvExemptionMechanism = "ExemptionMechanism";

    private static final String defaultAlg = "EMech";

    private static final String ExemptionMechanismProviderClass = "org.apache.harmony.crypto.tests.support.MyExemptionMechanismSpi";

    /**
     * Test for <code>ExemptionMechanism</code> constructor
     * Assertion: creates new object using provider and mechanism name
     */
    public void testExemptionMechanism() throws Exception {
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanismSpi spi = new MyExemptionMechanismSpi();
        ExemptionMechanism em = new ExemptionMechanism(spi, mProv, ExemptionMechanismTest.defaultAlg) {};
        TestCase.assertEquals("Incorrect provider", em.getProvider(), mProv);
        TestCase.assertEquals("Incorrect algorithm", em.getName(), ExemptionMechanismTest.defaultAlg);
        try {
            em.init(null);
            TestCase.fail("InvalidKeyException must be thrown");
        } catch (InvalidKeyException e) {
        }
        try {
            em.getOutputSize(100);
            TestCase.fail("IllegalStateException must be thrown");
        } catch (IllegalStateException e) {
        }
        em = new ExemptionMechanism(null, null, null) {};
        TestCase.assertNull("Incorrect mechanism", em.getName());
        TestCase.assertNull("Incorrect provider", em.getProvider());
        try {
            em.init(null);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
        try {
            em.getOutputSize(100);
            TestCase.fail("IllegalStateException must be thrown");
        } catch (IllegalStateException e) {
        }
    }

    /**
     * javax/crypto/ExemptionMechanism#getInstance(String algorithm, String provider)
     * Checks exception order
     */
    public void testGetInstance() throws Exception {
        // Regression for HARMONY-762
        try {
            ExemptionMechanism.getInstance(((String) (null)), "aaa");
            TestCase.fail("NoSuchProviderException must be thrown");
        } catch (NoSuchProviderException pe) {
            // expected
        }
        try {
            ExemptionMechanism.getInstance("AlgName", ((String) (null)));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Test for <code>isCryptoAllowed(Key key)</code> method
     */
    public void testIsCryptoAllowed() throws Exception {
        // Regression for HARMONY-1029
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new MyExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        Key key = new MyExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        TestCase.assertFalse(em.isCryptoAllowed(key));
        em.init(key);
        TestCase.assertFalse(em.isCryptoAllowed(key));
        em.genExemptionBlob();
        TestCase.assertTrue(em.isCryptoAllowed(key));
        Key key1 = new MyExemptionMechanismSpi().new tmpKey("Proba", new byte[]{ 1 });
        TestCase.assertFalse(em.isCryptoAllowed(key1));
        em.init(key1);
        TestCase.assertFalse(em.isCryptoAllowed(key));
    }

    /**
     * Test for <code>genExemptionBlob((byte[] output, int outputOffset)</code> method
     */
    public void testGenExemptionBlob() throws Exception {
        // Regression for HARMONY-1029
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new MyExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        Key key = new MyExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        em.init(key);
        // ExemptionMechanism doesn't check parameters
        // it is a responsibility of ExemptionMechanismSpi
        em.genExemptionBlob(null, 0);
        em.genExemptionBlob(new byte[0], 0);
        em.genExemptionBlob(new byte[10], (-5));
    }

    static boolean flag = false;

    class Mock_ExemptionMechanism extends ExemptionMechanism {
        protected Mock_ExemptionMechanism(ExemptionMechanismSpi exmechSpi, Provider provider, String mechanism) {
            super(exmechSpi, provider, mechanism);
        }

        @Override
        protected void finalize() {
            ExemptionMechanismTest.flag = true;
            super.finalize();
        }
    }

    class Mock_ExemptionMechanismSpi extends MyExemptionMechanismSpi {
        @Override
        protected byte[] engineGenExemptionBlob() throws ExemptionMechanismException {
            throw new ExemptionMechanismException();
        }

        @Override
        protected int engineGenExemptionBlob(byte[] output, int outputOffset) throws ExemptionMechanismException, ShortBufferException {
            if (((output.length) - outputOffset) < (super.engineGenExemptionBlob(output, outputOffset))) {
                throw new ShortBufferException();
            }
            if ((output[(outputOffset + 3)]) == 33) {
                throw new ExemptionMechanismException();
            }
            return super.engineGenExemptionBlob(output, outputOffset);
        }
    }

    public void test_genExemptionBlob() throws InvalidKeyException, ExemptionMechanismException {
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new MyExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        Key key = new MyExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        try {
            em.genExemptionBlob();
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // failed
        }
        em.init(key);
        TestCase.assertNotNull(em.genExemptionBlob());
        em = new ExemptionMechanism(new ExemptionMechanismTest.Mock_ExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        key = new ExemptionMechanismTest.Mock_ExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        em.init(key);
        try {
            em.genExemptionBlob();
            TestCase.fail("ExemptionMechanismException expected");
        } catch (ExemptionMechanismException e) {
            // failed
        }
    }

    public void test_genExemptionBlob$B() throws InvalidKeyException, ExemptionMechanismException, ShortBufferException {
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new ExemptionMechanismTest.Mock_ExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        Key key = new ExemptionMechanismTest.Mock_ExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        try {
            em.genExemptionBlob(new byte[10]);
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // failed
        }
        em.init(key);
        TestCase.assertEquals(5, em.genExemptionBlob(new byte[10]));
        try {
            em.genExemptionBlob(new byte[2]);
            TestCase.fail("ShortBufferException expected");
        } catch (ShortBufferException e) {
            // failed
        }
        byte[] b = new byte[]{ 0, 0, 0, 33, 0 };
        try {
            em.genExemptionBlob(b);
            TestCase.fail("ExemptionMechanismException expected");
        } catch (ExemptionMechanismException e) {
            // failed
        }
    }

    public void test_genExemptionBlob$BI() throws InvalidKeyException, ExemptionMechanismException, ShortBufferException {
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new ExemptionMechanismTest.Mock_ExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        Key key = new ExemptionMechanismTest.Mock_ExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        try {
            em.genExemptionBlob(new byte[10], 2);
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // failed
        }
        em.init(key);
        TestCase.assertEquals(5, em.genExemptionBlob(new byte[10], 5));
        try {
            em.genExemptionBlob(new byte[7], 3);
            TestCase.fail("ShortBufferException expected");
        } catch (ShortBufferException e) {
            // failed
        }
        byte[] b = new byte[]{ 0, 0, 0, 1, 2, 3, 33, 0 };
        try {
            em.genExemptionBlob(b, 3);
            TestCase.fail("ExemptionMechanismException expected");
        } catch (ExemptionMechanismException e) {
            // failed
        }
    }

    public void test_getInstanceLjava_lang_String() throws Exception {
        try {
            ExemptionMechanism.getInstance(((String) (null)));
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new ExemptionMechanismTest.Mock_ExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        try {
            em.getInstance("WrongAlgName");
            TestCase.fail("NoSuchAlgorithmException expected");
        } catch (NoSuchAlgorithmException e) {
            // expected
        }
    }

    public void test_getInstanceLjava_lang_StringLjava_security_Provider() throws Exception {
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        try {
            ExemptionMechanism.getInstance(((String) (null)), mProv);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        ExemptionMechanism em = new ExemptionMechanism(new ExemptionMechanismTest.Mock_ExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        try {
            em.getInstance("WrongAlgName", mProv);
            TestCase.fail("NoSuchAlgorithmException expected");
        } catch (NoSuchAlgorithmException e) {
            // expected
        }
        try {
            em.getInstance("WrongAlgName", ((Provider) (null)));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void test_getName() throws Exception {
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new MyExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        Key key = new MyExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        TestCase.assertEquals(ExemptionMechanismTest.defaultAlg, em.getName());
    }

    public void test_getOutputSizeI() throws Exception {
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new MyExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        Key key = new MyExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        try {
            em.getOutputSize(10);
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // failed
        }
        em.init(key);
        TestCase.assertEquals(10, em.getOutputSize(10));
    }

    public void test_getProvider() throws Exception {
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new MyExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        Key key = new MyExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        TestCase.assertEquals(mProv, em.getProvider());
    }

    public void test_initLjava_security_Key() throws Exception {
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new MyExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        Key key = new MyExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        em.init(key);
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(56, new SecureRandom());
        key = kg.generateKey();
        try {
            em.init(null);
            TestCase.fail("InvalidKeyException expected");
        } catch (InvalidKeyException e) {
            // expected
        }
        try {
            em.init(key);
            TestCase.fail("ExemptionMechanismException expected");
        } catch (ExemptionMechanismException e) {
            // expected
        }
    }

    public void test_initLjava_security_KeyLjava_security_AlgorithmParameters() throws Exception {
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new MyExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        Key key = new MyExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        em.init(key, AlgorithmParameters.getInstance("DES"));
        try {
            em.init(key, ((AlgorithmParameters) (null)));
            TestCase.fail("InvalidAlgorithmParameterException expected");
        } catch (InvalidAlgorithmParameterException e) {
            // expected
        }
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(56, new SecureRandom());
        key = kg.generateKey();
        try {
            em.init(null, AlgorithmParameters.getInstance("DES"));
            TestCase.fail("InvalidKeyException expected");
        } catch (InvalidKeyException e) {
            // expected
        }
        try {
            em.init(key, AlgorithmParameters.getInstance("DES"));
            TestCase.fail("ExemptionMechanismException expected");
        } catch (ExemptionMechanismException e) {
            // expected
        }
    }

    public void test_initLjava_security_KeyLjava_security_spec_AlgorithmParameterSpec() throws Exception {
        Provider mProv = new SpiEngUtils().new MyProvider("MyExMechProvider", "Provider for ExemptionMechanism testing", ExemptionMechanismTest.srvExemptionMechanism.concat(".").concat(ExemptionMechanismTest.defaultAlg), ExemptionMechanismTest.ExemptionMechanismProviderClass);
        ExemptionMechanism em = new ExemptionMechanism(new MyExemptionMechanismSpi(), mProv, ExemptionMechanismTest.defaultAlg) {};
        Key key = new MyExemptionMechanismSpi().new tmpKey("Proba", new byte[0]);
        em.init(key, new RSAKeyGenParameterSpec(10, new BigInteger("10")));
        try {
            em.init(key, ((AlgorithmParameterSpec) (null)));
            TestCase.fail("InvalidAlgorithmParameterException expected");
        } catch (InvalidAlgorithmParameterException e) {
            // expected
        }
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(56, new SecureRandom());
        key = kg.generateKey();
        try {
            em.init(null, new RSAKeyGenParameterSpec(10, new BigInteger("10")));
            TestCase.fail("InvalidKeyException expected");
        } catch (InvalidKeyException e) {
            // expected
        }
        try {
            em.init(key, new RSAKeyGenParameterSpec(10, new BigInteger("10")));
            TestCase.fail("ExemptionMechanismException expected");
        } catch (ExemptionMechanismException e) {
            // expected
        }
    }
}

