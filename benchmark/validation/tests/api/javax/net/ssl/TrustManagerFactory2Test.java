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


import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import javax.net.ssl.TrustManagerFactory;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for TrustManagerFactory class constructors and methods
 */
public class TrustManagerFactory2Test extends TestCase {
    private static final String srvTrustManagerFactory = "TrustManagerFactory";

    private static final String defaultAlg = "TMF";

    private static final String TrustManagerFactoryProviderClass = "org.apache.harmony.xnet.tests.support.MyTrustManagerFactorySpi";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static final String[] validValues;

    static {
        validValues = new String[4];
        TrustManagerFactory2Test.validValues[0] = TrustManagerFactory2Test.defaultAlg;
        TrustManagerFactory2Test.validValues[1] = TrustManagerFactory2Test.defaultAlg.toLowerCase();
        TrustManagerFactory2Test.validValues[2] = "Tmf";
        TrustManagerFactory2Test.validValues[3] = "tMF";
    }

    Provider mProv;

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertions:
     * throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm is not correct;
     * returns TrustManagerFactory object
     */
    public void test_getInstanceLjava_lang_String() throws Exception {
        try {
            TrustManagerFactory.getInstance(null);
            TestCase.fail("NoSuchAlgorithmException or NullPointerException should be thrown (algorithm is null");
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (TrustManagerFactory2Test.invalidValues.length); i++) {
            try {
                TrustManagerFactory.getInstance(TrustManagerFactory2Test.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(TrustManagerFactory2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        TrustManagerFactory tmf;
        for (int i = 0; i < (TrustManagerFactory2Test.validValues.length); i++) {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory2Test.validValues[i]);
            TestCase.assertTrue("Not instanceof TrustManagerFactory object", (tmf instanceof TrustManagerFactory));
            TestCase.assertEquals("Incorrect algorithm", tmf.getAlgorithm(), TrustManagerFactory2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", tmf.getProvider(), mProv);
            checkResult(tmf);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertions:
     * throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm is not correct;
     * throws IllegalArgumentException when provider is null or empty;
     * throws NoSuchProviderException when provider is available;
     * returns TrustManagerFactory object
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String() throws Exception {
        try {
            TrustManagerFactory.getInstance(null, mProv.getName());
            TestCase.fail("NoSuchAlgorithmException or NullPointerException should be thrown (algorithm is null");
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (TrustManagerFactory2Test.invalidValues.length); i++) {
            try {
                TrustManagerFactory.getInstance(TrustManagerFactory2Test.invalidValues[i], mProv.getName());
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(TrustManagerFactory2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        String prov = null;
        for (int i = 0; i < (TrustManagerFactory2Test.validValues.length); i++) {
            try {
                TrustManagerFactory.getInstance(TrustManagerFactory2Test.validValues[i], prov);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null (algorithm: ".concat(TrustManagerFactory2Test.invalidValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
            try {
                TrustManagerFactory.getInstance(TrustManagerFactory2Test.validValues[i], "");
                TestCase.fail("IllegalArgumentException must be thrown when provider is empty (algorithm: ".concat(TrustManagerFactory2Test.invalidValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        for (int i = 0; i < (TrustManagerFactory2Test.validValues.length); i++) {
            for (int j = 1; j < (TrustManagerFactory2Test.invalidValues.length); j++) {
                try {
                    TrustManagerFactory.getInstance(TrustManagerFactory2Test.validValues[i], TrustManagerFactory2Test.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException must be thrown (algorithm: ".concat(TrustManagerFactory2Test.invalidValues[i]).concat(" provider: ").concat(TrustManagerFactory2Test.invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
        TrustManagerFactory tmf;
        for (int i = 0; i < (TrustManagerFactory2Test.validValues.length); i++) {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory2Test.validValues[i], mProv.getName());
            TestCase.assertTrue("Not instanceof TrustManagerFactory object", (tmf instanceof TrustManagerFactory));
            TestCase.assertEquals("Incorrect algorithm", tmf.getAlgorithm(), TrustManagerFactory2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", tmf.getProvider().getName(), mProv.getName());
            checkResult(tmf);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertions:
     * throws NullPointerException when algorithm is null;
     * throws NoSuchAlgorithmException when algorithm is not correct;
     * throws IllegalArgumentException when provider is null;
     * returns TrustManagerFactory object
     */
    public void testLjava_lang_StringLjava_security_Provider() throws Exception {
        try {
            TrustManagerFactory.getInstance(null, mProv);
            TestCase.fail("NoSuchAlgorithmException or NullPointerException should be thrown (algorithm is null");
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (TrustManagerFactory2Test.invalidValues.length); i++) {
            try {
                TrustManagerFactory.getInstance(TrustManagerFactory2Test.invalidValues[i], mProv);
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(TrustManagerFactory2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        Provider prov = null;
        for (int i = 0; i < (TrustManagerFactory2Test.validValues.length); i++) {
            try {
                TrustManagerFactory.getInstance(TrustManagerFactory2Test.validValues[i], prov);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null (algorithm: ".concat(TrustManagerFactory2Test.invalidValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        TrustManagerFactory tmf;
        for (int i = 0; i < (TrustManagerFactory2Test.validValues.length); i++) {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory2Test.validValues[i], mProv);
            TestCase.assertTrue("Not instanceof TrustManagerFactory object", (tmf instanceof TrustManagerFactory));
            TestCase.assertEquals("Incorrect algorithm", tmf.getAlgorithm(), TrustManagerFactory2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", tmf.getProvider(), mProv);
            checkResult(tmf);
        }
    }
}

