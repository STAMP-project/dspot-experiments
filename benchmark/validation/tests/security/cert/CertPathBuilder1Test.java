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
package tests.security.cert;


import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertificateException;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;
import org.apache.harmony.security.tests.support.cert.MyCertPathBuilderSpi;


/**
 * Tests for <code>CertPathBuilder</code> class constructors and
 * methods.
 */
public class CertPathBuilder1Test extends TestCase {
    public static final String srvCertPathBuilder = "CertPathBuilder";

    public static final String defaultType = "PKIX";

    public static final String[] validValues = new String[]{ "PKIX", "pkix", "PkiX", "pKiX" };

    private static String[] invalidValues = SpiEngUtils.invalidValues;

    private static boolean PKIXSupport = false;

    private static Provider defaultProvider;

    private static String defaultProviderName;

    private static String NotSupportMsg = "";

    public static final String DEFAULT_TYPE_PROPERTY = "certpathbuilder.type";

    static {
        CertPathBuilder1Test.defaultProvider = SpiEngUtils.isSupport(CertPathBuilder1Test.defaultType, CertPathBuilder1Test.srvCertPathBuilder);
        CertPathBuilder1Test.PKIXSupport = (CertPathBuilder1Test.defaultProvider) != null;
        CertPathBuilder1Test.defaultProviderName = (CertPathBuilder1Test.PKIXSupport) ? CertPathBuilder1Test.defaultProvider.getName() : null;
        CertPathBuilder1Test.NotSupportMsg = CertPathBuilder1Test.defaultType.concat(" is not supported");
    }

    /**
     * java.security.cert.CertPathBuilder#getDefaultType()
     */
    public void test_getDefaultType() throws Exception {
        // Regression for HARMONY-2785
        // test: default value
        TestCase.assertNull(Security.getProperty(CertPathBuilder1Test.DEFAULT_TYPE_PROPERTY));
        TestCase.assertEquals("PKIX", CertPathBuilder.getDefaultType());
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm  is not correct
     * or it is not available
     */
    public void testCertPathBuilder02() throws NoSuchAlgorithmException {
        try {
            CertPathBuilder.getInstance(null);
            TestCase.fail("No expected NullPointerException");
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (CertPathBuilder1Test.invalidValues.length); i++) {
            try {
                CertPathBuilder.getInstance(CertPathBuilder1Test.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException must be thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion: returns CertPathBuilder object
     */
    public void testCertPathBuilder03() throws NoSuchAlgorithmException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        for (int i = 0; i < (CertPathBuilder1Test.validValues.length); i++) {
            CertPathBuilder cpb = CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", cpb.getAlgorithm(), CertPathBuilder1Test.validValues[i]);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion: throws IllegalArgumentException when provider is null or empty
     *
     * FIXME: verify what exception will be thrown if provider is empty
     */
    public void testCertPathBuilder04() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        String provider = null;
        for (int i = 0; i < (CertPathBuilder1Test.validValues.length); i++) {
            try {
                CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown thrown");
            } catch (IllegalArgumentException e) {
            }
            try {
                CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[i], "");
                TestCase.fail("IllegalArgumentException must be thrown thrown");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion:
     * throws NoSuchProviderException when provider has invalid value
     */
    public void testCertPathBuilder05() throws NoSuchAlgorithmException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        for (int i = 0; i < (CertPathBuilder1Test.validValues.length); i++) {
            for (int j = 1; j < (CertPathBuilder1Test.invalidValues.length); j++) {
                try {
                    CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[i], CertPathBuilder1Test.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException must be hrown");
                } catch (NoSuchProviderException e1) {
                }
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm  is not correct
     */
    public void testCertPathBuilder06() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        try {
            CertPathBuilder.getInstance(null, CertPathBuilder1Test.defaultProviderName);
            TestCase.fail("No expected NullPointerException");
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (CertPathBuilder1Test.invalidValues.length); i++) {
            try {
                CertPathBuilder.getInstance(CertPathBuilder1Test.invalidValues[i], CertPathBuilder1Test.defaultProviderName);
                TestCase.fail("NoSuchAlgorithmException must be thrown");
            } catch (NoSuchAlgorithmException e1) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion: returns CertPathBuilder object
     */
    public void testCertPathBuilder07() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        CertPathBuilder certPB;
        for (int i = 0; i < (CertPathBuilder1Test.validValues.length); i++) {
            certPB = CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[i], CertPathBuilder1Test.defaultProviderName);
            TestCase.assertEquals("Incorrect algorithm", certPB.getAlgorithm(), CertPathBuilder1Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider name", certPB.getProvider().getName(), CertPathBuilder1Test.defaultProviderName);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code> method
     * Assertion: throws IllegalArgumentException when provider is null
     */
    public void testCertPathBuilder08() throws NoSuchAlgorithmException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        Provider prov = null;
        for (int t = 0; t < (CertPathBuilder1Test.validValues.length); t++) {
            try {
                CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[t], prov);
                TestCase.fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e1) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm  is not correct
     */
    public void testCertPathBuilder09() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        try {
            CertPathBuilder.getInstance(null, CertPathBuilder1Test.defaultProvider);
            TestCase.fail("No expected NullPointerException");
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (CertPathBuilder1Test.invalidValues.length); i++) {
            try {
                CertPathBuilder.getInstance(CertPathBuilder1Test.invalidValues[i], CertPathBuilder1Test.defaultProvider);
                TestCase.fail("NoSuchAlgorithm must be thrown");
            } catch (NoSuchAlgorithmException e1) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion: returns CertPathBuilder object
     */
    public void testCertPathBuilder10() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        CertPathBuilder certPB;
        for (int i = 0; i < (CertPathBuilder1Test.invalidValues.length); i++) {
            certPB = CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[i], CertPathBuilder1Test.defaultProvider);
            TestCase.assertEquals("Incorrect algorithm", certPB.getAlgorithm(), CertPathBuilder1Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider name", certPB.getProvider(), CertPathBuilder1Test.defaultProvider);
        }
    }

    /**
     * Test for <code>build(CertPathParameters params)</code> method
     * Assertion: throws InvalidAlgorithmParameterException params is null
     */
    public void testCertPathBuilder11() throws NoSuchAlgorithmException, NoSuchProviderException, CertPathBuilderException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        CertPathBuilder[] certPB = CertPathBuilder1Test.createCPBs();
        TestCase.assertNotNull("CertPathBuilder objects were not created", certPB);
        for (int i = 0; i < (certPB.length); i++) {
            try {
                certPB[i].build(null);
                TestCase.fail("InvalidAlgorithmParameterException must be thrown");
            } catch (InvalidAlgorithmParameterException e) {
            }
        }
    }

    /**
     * Test for
     * <code>CertPathBuilder</code> constructor
     * Assertion: returns CertPathBuilder object
     */
    public void testCertPathBuilder12() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CertPathBuilderException, CertificateException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        CertPathBuilderSpi spi = new MyCertPathBuilderSpi();
        CertPathBuilder certPB = new myCertPathBuilder(spi, CertPathBuilder1Test.defaultProvider, CertPathBuilder1Test.defaultType);
        TestCase.assertEquals("Incorrect algorithm", certPB.getAlgorithm(), CertPathBuilder1Test.defaultType);
        TestCase.assertEquals("Incorrect provider", certPB.getProvider(), CertPathBuilder1Test.defaultProvider);
        try {
            certPB.build(null);
            TestCase.fail("CertPathBuilderException must be thrown ");
        } catch (CertPathBuilderException e) {
        }
        certPB = new myCertPathBuilder(null, null, null);
        TestCase.assertNull("Incorrect algorithm", certPB.getAlgorithm());
        TestCase.assertNull("Incorrect provider", certPB.getProvider());
        try {
            certPB.build(null);
            TestCase.fail("NullPointerException must be thrown ");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test for <code>getAlgorithm()</code> method Assertion: returns
     * CertPathBuilder object
     */
    public void testCertPathBuilder13() throws NoSuchAlgorithmException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        for (int i = 0; i < (CertPathBuilder1Test.validValues.length); i++) {
            CertPathBuilder cpb = CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", cpb.getAlgorithm(), CertPathBuilder1Test.validValues[i]);
            try {
                cpb = CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[i], CertPathBuilder1Test.defaultProviderName);
                TestCase.assertEquals("Incorrect algorithm", cpb.getAlgorithm(), CertPathBuilder1Test.validValues[i]);
            } catch (NoSuchProviderException e) {
                TestCase.fail(("Unexpected NoSuchProviderException exeption " + (e.getMessage())));
            }
            try {
                cpb = CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[i], CertPathBuilder1Test.defaultProviderName);
                TestCase.assertEquals("Incorrect algorithm", cpb.getAlgorithm(), CertPathBuilder1Test.validValues[i]);
            } catch (NoSuchProviderException e) {
                TestCase.fail(("Unexpected NoSuchProviderException " + (e.getMessage())));
            }
        }
    }

    /**
     * Test for <code>getProvider()</code> method Assertion: returns
     * CertPathBuilder object
     */
    public void testCertPathBuilder14() throws NoSuchAlgorithmException {
        if (!(CertPathBuilder1Test.PKIXSupport)) {
            TestCase.fail(CertPathBuilder1Test.NotSupportMsg);
            return;
        }
        for (int i = 0; i < (CertPathBuilder1Test.validValues.length); i++) {
            CertPathBuilder cpb2 = CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[i], CertPathBuilder1Test.defaultProvider);
            TestCase.assertEquals("Incorrect provider", cpb2.getProvider(), CertPathBuilder1Test.defaultProvider);
            try {
                CertPathBuilder cpb3 = CertPathBuilder.getInstance(CertPathBuilder1Test.validValues[i], CertPathBuilder1Test.defaultProviderName);
                TestCase.assertEquals("Incorrect provider", cpb3.getProvider(), CertPathBuilder1Test.defaultProvider);
            } catch (NoSuchProviderException e) {
                TestCase.fail(("Unexpected NoSuchProviderException " + (e.getMessage())));
            }
        }
    }
}

