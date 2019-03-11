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
package tests.api.javax.net.ssl;


import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;
import org.apache.harmony.xnet.tests.support.MyTrustManagerFactorySpi;


/**
 * Tests for <code>TrustManagerFactory</code> class constructors and methods.
 */
public class TrustManagerFactory1Test extends TestCase {
    private static final String srvTrustManagerFactory = "TrustManagerFactory";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static String DEFAULT_ALGORITHM;

    private static String DEFAULT_PROVIDER_NAME;

    private static Provider DEFAULT_PROVIDER;

    private static String[] VALID_VALUES;

    public void test_ConstructorLjavax_net_ssl_TrustManagerFactorySpiLjava_security_ProviderLjava_lang_String() throws NoSuchAlgorithmException {
        TrustManagerFactorySpi spi = new MyTrustManagerFactorySpi();
        TrustManagerFactory tmF = new myTrustManagerFactory(spi, TrustManagerFactory1Test.getDefaultProvider(), TrustManagerFactory1Test.getDefaultAlgorithm());
        TestCase.assertTrue("Not CertStore object", (tmF instanceof TrustManagerFactory));
        TestCase.assertEquals("Incorrect algorithm", tmF.getAlgorithm(), TrustManagerFactory1Test.getDefaultAlgorithm());
        TestCase.assertEquals("Incorrect provider", tmF.getProvider(), TrustManagerFactory1Test.getDefaultProvider());
        TestCase.assertNull("Incorrect result", tmF.getTrustManagers());
        tmF = new myTrustManagerFactory(null, null, null);
        TestCase.assertTrue("Not CertStore object", (tmF instanceof TrustManagerFactory));
        TestCase.assertNull("Provider must be null", tmF.getProvider());
        TestCase.assertNull("Algorithm must be null", tmF.getAlgorithm());
        try {
            tmF.getTrustManagers();
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test for <code>getAlgorithm()</code> method
     * Assertion: returns the algorithm name of this object
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws NoSuchProviderException
     * 		
     */
    public void test_getAlgorithm() throws NoSuchAlgorithmException, NoSuchProviderException {
        TestCase.assertEquals("Incorrect algorithm", TrustManagerFactory1Test.getDefaultAlgorithm(), TrustManagerFactory.getInstance(TrustManagerFactory1Test.getDefaultAlgorithm()).getAlgorithm());
        TestCase.assertEquals("Incorrect algorithm", TrustManagerFactory1Test.getDefaultAlgorithm(), TrustManagerFactory.getInstance(TrustManagerFactory1Test.getDefaultAlgorithm(), TrustManagerFactory1Test.getDefaultProviderName()).getAlgorithm());
        TestCase.assertEquals("Incorrect algorithm", TrustManagerFactory1Test.getDefaultAlgorithm(), TrustManagerFactory.getInstance(TrustManagerFactory1Test.getDefaultAlgorithm(), TrustManagerFactory1Test.getDefaultProvider()).getAlgorithm());
    }

    /**
     * Test for <code>getDefaultAlgorithm()</code> method
     * Assertion: returns value which is specifoed in security property
     */
    public void test_getDefaultAlgorithm() {
        String def = TrustManagerFactory.getDefaultAlgorithm();
        if ((TrustManagerFactory1Test.getDefaultAlgorithm()) == null) {
            TestCase.assertNull("DefaultAlgorithm must be null", def);
        } else {
            TestCase.assertEquals("Invalid default algorithm", def, TrustManagerFactory1Test.getDefaultAlgorithm());
        }
        String defA = "Proba.trustmanagerfactory.defaul.type";
        Security.setProperty("ssl.TrustManagerFactory.algorithm", defA);
        TestCase.assertEquals("Incorrect getDefaultAlgorithm()", TrustManagerFactory.getDefaultAlgorithm(), defA);
        if (def == null) {
            def = "";
        }
        Security.setProperty("ssl.TrustManagerFactory.algorithm", def);
        TestCase.assertEquals("Incorrect getDefaultAlgorithm()", TrustManagerFactory.getDefaultAlgorithm(), def);
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertions: returns security property "ssl.TrustManagerFactory.algorithm";
     * returns instance of TrustManagerFactory
     */
    public void test_getInstanceLjava_lang_String01() throws NoSuchAlgorithmException {
        for (String validValue : TrustManagerFactory1Test.getValidValues()) {
            TrustManagerFactory trustMF = TrustManagerFactory.getInstance(validValue);
            TestCase.assertTrue("Not TrustManagerFactory object", (trustMF instanceof TrustManagerFactory));
            TestCase.assertEquals("Invalid algorithm", trustMF.getAlgorithm(), validValue);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm is not correct;
     */
    public void test_getInstanceLjava_lang_String02() {
        try {
            TrustManagerFactory.getInstance(null);
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        } catch (NullPointerException expected) {
        }
        for (int i = 0; i < (TrustManagerFactory1Test.invalidValues.length); i++) {
            try {
                TrustManagerFactory.getInstance(TrustManagerFactory1Test.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException was not thrown as expected for algorithm: ".concat(TrustManagerFactory1Test.invalidValues[i]));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion: throws IllegalArgumentException when provider is null
     * or empty
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String01() throws Exception {
        for (String validValue : TrustManagerFactory1Test.getValidValues()) {
            try {
                TrustManagerFactory.getInstance(validValue, ((String) (null)));
                TestCase.fail();
            } catch (IllegalArgumentException expected) {
            }
            try {
                TrustManagerFactory.getInstance(validValue, "");
                TestCase.fail();
            } catch (IllegalArgumentException expected) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion:
     * throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm is not correct;
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String02() throws Exception {
        try {
            TrustManagerFactory.getInstance(null, TrustManagerFactory1Test.getDefaultProviderName());
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        } catch (NullPointerException expected) {
        }
        for (int i = 0; i < (TrustManagerFactory1Test.invalidValues.length); i++) {
            try {
                TrustManagerFactory.getInstance(TrustManagerFactory1Test.invalidValues[i], TrustManagerFactory1Test.getDefaultProviderName());
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(TrustManagerFactory1Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion: throws NoSuchProviderException when provider has
     * invalid value
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String03() throws Exception {
        for (String invalidValue : TrustManagerFactory1Test.invalidValues) {
            for (String validValue : TrustManagerFactory1Test.getValidValues()) {
                try {
                    TrustManagerFactory.getInstance(validValue, invalidValue);
                    TestCase.fail("NoSuchProviderException must be thrown (algorithm: ".concat(validValue).concat(" provider: ").concat(invalidValue).concat(")"));
                } catch (NoSuchProviderException expected) {
                    TestCase.assertFalse("".equals(invalidValue));
                } catch (IllegalArgumentException expected) {
                    TestCase.assertEquals("", invalidValue);
                }
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion: returns instance of TrustManagerFactory
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String04() throws Exception {
        for (String validValue : TrustManagerFactory1Test.getValidValues()) {
            TrustManagerFactory trustMF = TrustManagerFactory.getInstance(validValue, TrustManagerFactory1Test.getDefaultProviderName());
            TestCase.assertTrue("Not TrustManagerFactory object", (trustMF instanceof TrustManagerFactory));
            TestCase.assertEquals("Invalid algorithm", trustMF.getAlgorithm(), validValue);
            TestCase.assertEquals("Invalid provider", trustMF.getProvider(), TrustManagerFactory1Test.getDefaultProvider());
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion: throws IllegalArgumentException when provider is null
     */
    public void test_getInstanceLjava_lang_StringLjava_security_Provider01() throws Exception {
        for (String validValue : TrustManagerFactory1Test.getValidValues()) {
            try {
                TrustManagerFactory.getInstance(validValue, ((Provider) (null)));
            } catch (IllegalArgumentException expected) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion:
     * throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm is not correct;
     */
    public void test_getInstanceLjava_lang_StringLjava_security_Provider02() {
        try {
            TrustManagerFactory.getInstance(null, TrustManagerFactory1Test.getDefaultProvider());
            TestCase.fail("");
        } catch (NoSuchAlgorithmException expected) {
        } catch (NullPointerException expected) {
        }
        for (int i = 0; i < (TrustManagerFactory1Test.invalidValues.length); i++) {
            try {
                TrustManagerFactory.getInstance(TrustManagerFactory1Test.invalidValues[i], TrustManagerFactory1Test.getDefaultProvider());
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(TrustManagerFactory1Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion: returns instance of TrustManagerFactory
     */
    public void test_getInstanceLjava_lang_StringLjava_security_Provider03() throws Exception {
        for (String validValue : TrustManagerFactory1Test.getValidValues()) {
            TrustManagerFactory trustMF = TrustManagerFactory.getInstance(validValue, TrustManagerFactory1Test.getDefaultProvider());
            TestCase.assertTrue("Not TrustManagerFactory object", (trustMF instanceof TrustManagerFactory));
            TestCase.assertEquals("Invalid algorithm", trustMF.getAlgorithm(), validValue);
            TestCase.assertEquals("Invalid provider", trustMF.getProvider(), TrustManagerFactory1Test.getDefaultProvider());
        }
    }

    /**
     * Test for <code>getProvider()</code>
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws NoSuchProviderException
     * 		
     */
    public void test_getProvider() throws NoSuchAlgorithmException, NoSuchProviderException {
        TestCase.assertEquals("Incorrect provider", TrustManagerFactory1Test.getDefaultProvider(), TrustManagerFactory.getInstance(TrustManagerFactory1Test.getDefaultAlgorithm()).getProvider());
        TestCase.assertEquals("Incorrect provider", TrustManagerFactory1Test.getDefaultProvider(), TrustManagerFactory.getInstance(TrustManagerFactory1Test.getDefaultAlgorithm(), TrustManagerFactory1Test.getDefaultProviderName()).getProvider());
        TestCase.assertEquals("Incorrect provider", TrustManagerFactory1Test.getDefaultProvider(), TrustManagerFactory.getInstance(TrustManagerFactory1Test.getDefaultAlgorithm(), TrustManagerFactory1Test.getDefaultProvider()).getProvider());
    }

    /**
     * Test for <code>geTrustManagers()</code>
     *
     * @throws KeyStoreException
     * 		
     * @throws IOException
     * 		
     * @throws CertificateException
     * 		
     * @throws NoSuchAlgorithmException
     * 		
     */
    public void test_getTrustManagers() {
        try {
            TrustManagerFactory trustMF = TrustManagerFactory.getInstance(TrustManagerFactory1Test.getDefaultAlgorithm());
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            trustMF.init(ks);
            TrustManager[] tm = trustMF.getTrustManagers();
            TestCase.assertNotNull("Result has not be null", tm);
            TestCase.assertTrue("Length of result TrustManager array should not be 0", ((tm.length) > 0));
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception " + (ex.toString())));
        }
    }

    /**
     * Test for <code>init(KeyStore keyStore)</code>
     * Assertion: call method with null parameter
     */
    public void test_initLjava_security_KeyStore_01() throws Exception {
        KeyStore ksNull = null;
        TrustManagerFactory[] trustMF = TrustManagerFactory1Test.createTMFac();
        TestCase.assertNotNull("TrustManagerFactory objects were not created", trustMF);
        // null parameter
        try {
            trustMF[0].init(ksNull);
        } catch (Exception ex) {
            TestCase.fail((ex + " unexpected exception was thrown for null parameter"));
        }
    }

    /**
     * Test for <code>init(KeyStore keyStore)</code>
     * Assertion: call method with not null parameter
     */
    public void test_initLjava_security_KeyStore_02() throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        TrustManagerFactory[] trustMF = TrustManagerFactory1Test.createTMFac();
        TestCase.assertNotNull("TrustManagerFactory objects were not created", trustMF);
        // not null parameter
        trustMF[0].init(ks);
    }
}

