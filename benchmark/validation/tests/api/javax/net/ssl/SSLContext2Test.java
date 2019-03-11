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


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;
import org.apache.harmony.xnet.tests.support.MySSLContextSpi;


/**
 * Tests for SSLContext class constructors and methods
 */
public class SSLContext2Test extends TestCase {
    private static String srvSSLContext = "SSLContext";

    private static final String defaultProtocol = "S+S+L";

    public static final String SSLContextProviderClass = MySSLContextSpi.class.getName();

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static final String[] validValues;

    static {
        validValues = new String[4];
        SSLContext2Test.validValues[0] = SSLContext2Test.defaultProtocol;
        SSLContext2Test.validValues[1] = SSLContext2Test.defaultProtocol.toLowerCase();
        SSLContext2Test.validValues[2] = "s+S+L";
        SSLContext2Test.validValues[3] = "S+s+L";
    }

    Provider mProv;

    /**
     * Test for <code>getInstance(String protocol)</code> method
     * Assertions:
     * throws NullPointerException when protocol is null;
     * throws NoSuchAlgorithmException when protocol is not correct;
     * returns SSLContext object
     */
    public void test_getInstanceLjava_lang_String() throws KeyManagementException, NoSuchAlgorithmException {
        try {
            SSLContext.getInstance(null);
            TestCase.fail(("NoSuchAlgorithmException or NullPointerException should be thrown " + "(protocol is null)"));
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (SSLContext2Test.invalidValues.length); i++) {
            try {
                SSLContext.getInstance(SSLContext2Test.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException must be thrown (protocol: ".concat(SSLContext2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        SSLContext sslC;
        for (int i = 0; i < (SSLContext2Test.validValues.length); i++) {
            sslC = SSLContext.getInstance(SSLContext2Test.validValues[i]);
            TestCase.assertTrue("Not instanceof SSLContext object", (sslC instanceof SSLContext));
            TestCase.assertEquals("Incorrect protocol", sslC.getProtocol(), SSLContext2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", sslC.getProvider(), mProv);
            checkSSLContext(sslC);
        }
    }

    /**
     * Test for <code>getInstance(String protocol, String provider)</code>
     * method
     * Assertions:
     * throws NullPointerException when protocol is null;
     * throws NoSuchAlgorithmException when protocol is not correct;
     * throws IllegalArgumentException when provider is null or empty;
     * throws NoSuchProviderException when provider is available;
     * returns SSLContext object
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String() throws IllegalArgumentException, KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            SSLContext.getInstance(null, mProv.getName());
            TestCase.fail(("NoSuchAlgorithmException or NullPointerException should be thrown " + "(protocol is null)"));
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (SSLContext2Test.invalidValues.length); i++) {
            try {
                SSLContext.getInstance(SSLContext2Test.invalidValues[i], mProv.getName());
                TestCase.fail("NoSuchAlgorithmException must be thrown (protocol: ".concat(SSLContext2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        String prov = null;
        for (int i = 0; i < (SSLContext2Test.validValues.length); i++) {
            try {
                SSLContext.getInstance(SSLContext2Test.validValues[i], prov);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null (protocol: ".concat(SSLContext2Test.invalidValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
            try {
                SSLContext.getInstance(SSLContext2Test.validValues[i], "");
                TestCase.fail("IllegalArgumentException must be thrown when provider is empty (protocol: ".concat(SSLContext2Test.invalidValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        for (int i = 0; i < (SSLContext2Test.validValues.length); i++) {
            for (int j = 1; j < (SSLContext2Test.invalidValues.length); j++) {
                try {
                    SSLContext.getInstance(SSLContext2Test.validValues[i], SSLContext2Test.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException must be thrown (protocol: ".concat(SSLContext2Test.invalidValues[i]).concat(" provider: ").concat(SSLContext2Test.invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
        SSLContext sslC;
        for (int i = 0; i < (SSLContext2Test.validValues.length); i++) {
            sslC = SSLContext.getInstance(SSLContext2Test.validValues[i], mProv.getName());
            TestCase.assertTrue("Not instanceof SSLContext object", (sslC instanceof SSLContext));
            TestCase.assertEquals("Incorrect protocol", sslC.getProtocol(), SSLContext2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", sslC.getProvider().getName(), mProv.getName());
            checkSSLContext(sslC);
        }
    }

    /**
     * Test for <code>getInstance(String protocol, Provider provider)</code>
     * method
     * Assertions:
     * throws NullPointerException when protocol is null;
     * throws NoSuchAlgorithmException when protocol is not correct;
     * throws IllegalArgumentException when provider is null;
     * returns SSLContext object
     */
    public void test_getInstanceLjava_lang_StringLjava_security_Provider() throws IllegalArgumentException, KeyManagementException, NoSuchAlgorithmException {
        try {
            SSLContext.getInstance(null, mProv);
            TestCase.fail(("NoSuchAlgorithmException or NullPointerException should be thrown " + "(protocol is null)"));
        } catch (NoSuchAlgorithmException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (SSLContext2Test.invalidValues.length); i++) {
            try {
                SSLContext.getInstance(SSLContext2Test.invalidValues[i], mProv);
                TestCase.fail("NoSuchAlgorithmException must be thrown (protocol: ".concat(SSLContext2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        Provider prov = null;
        for (int i = 0; i < (SSLContext2Test.validValues.length); i++) {
            try {
                SSLContext.getInstance(SSLContext2Test.validValues[i], prov);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null (protocol: ".concat(SSLContext2Test.invalidValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        SSLContext sslC;
        for (int i = 0; i < (SSLContext2Test.validValues.length); i++) {
            sslC = SSLContext.getInstance(SSLContext2Test.validValues[i], mProv);
            TestCase.assertTrue("Not instanceof SSLContext object", (sslC instanceof SSLContext));
            TestCase.assertEquals("Incorrect protocol", sslC.getProtocol(), SSLContext2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", sslC.getProvider(), mProv);
            checkSSLContext(sslC);
        }
    }

    class TManager implements TrustManager {}

    class KManager implements KeyManager {}
}

