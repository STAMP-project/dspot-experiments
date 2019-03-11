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
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorSpi;
import java.security.cert.CertificateException;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;
import org.apache.harmony.security.tests.support.cert.MyCertPath;
import org.apache.harmony.security.tests.support.cert.MyCertPathValidatorSpi;


/**
 * Tests for <code>CertPathValidator</code> class constructors and
 * methods.
 */
public class CertPathValidator1Test extends TestCase {
    public static final String srvCertPathValidator = "CertPathValidator";

    private static final String defaultType = "PKIX";

    public static final String[] validValues = new String[]{ "PKIX", "pkix", "PkiX", "pKiX" };

    private static String[] invalidValues = SpiEngUtils.invalidValues;

    private static boolean PKIXSupport = false;

    private static Provider defaultProvider;

    private static String defaultProviderName;

    private static String NotSupportMsg = "";

    static {
        CertPathValidator1Test.defaultProvider = SpiEngUtils.isSupport(CertPathValidator1Test.defaultType, CertPathValidator1Test.srvCertPathValidator);
        CertPathValidator1Test.PKIXSupport = (CertPathValidator1Test.defaultProvider) != null;
        CertPathValidator1Test.defaultProviderName = (CertPathValidator1Test.PKIXSupport) ? CertPathValidator1Test.defaultProvider.getName() : null;
        CertPathValidator1Test.NotSupportMsg = CertPathValidator1Test.defaultType.concat(" is not supported");
    }

    /**
     * Test for <code>getDefaultType()</code> method
     * Assertion: returns security property "certpathvalidator.type" or "PKIX"
     */
    public void testCertPathValidator01() {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        String propName = "certpathvalidator.type";
        String defCPV = Security.getProperty(propName);
        String dt = CertPathValidator.getDefaultType();
        String resType = defCPV;
        if (resType == null) {
            resType = CertPathValidator1Test.defaultType;
        }
        TestCase.assertNotNull("Default type have not be null", dt);
        TestCase.assertEquals("Incorrect default type", dt, resType);
        if (defCPV == null) {
            Security.setProperty(propName, CertPathValidator1Test.defaultType);
            dt = CertPathValidator.getDefaultType();
            resType = Security.getProperty(propName);
            TestCase.assertNotNull("Incorrect default type", resType);
            TestCase.assertNotNull("Default type have not be null", dt);
            TestCase.assertEquals("Incorrect default type", dt, resType);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm  is not available
     */
    public void testCertPathValidator02() {
        try {
            CertPathValidator.getInstance(null);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (CertPathValidator1Test.invalidValues.length); i++) {
            try {
                CertPathValidator.getInstance(CertPathValidator1Test.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException must be thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion: returns CertPathValidator object
     */
    public void testCertPathValidator03() throws NoSuchAlgorithmException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        CertPathValidator certPV;
        for (int i = 0; i < (CertPathValidator1Test.validValues.length); i++) {
            certPV = CertPathValidator.getInstance(CertPathValidator1Test.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", certPV.getAlgorithm(), CertPathValidator1Test.validValues[i]);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion: throws IllegalArgumentException when provider is null or empty
     *
     * FIXME: verify what exception will be thrown if provider is empty
     */
    public void testCertPathValidator04() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        String provider = null;
        for (int i = 0; i < (CertPathValidator1Test.validValues.length); i++) {
            try {
                CertPathValidator.getInstance(CertPathValidator1Test.validValues[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown thrown");
            } catch (IllegalArgumentException e) {
            }
            try {
                CertPathValidator.getInstance(CertPathValidator1Test.validValues[i], "");
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
    public void testCertPathValidator05() throws NoSuchAlgorithmException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        for (int t = 0; t < (CertPathValidator1Test.validValues.length); t++) {
            for (int i = 1; i < (CertPathValidator1Test.invalidValues.length); i++) {
                try {
                    CertPathValidator.getInstance(CertPathValidator1Test.validValues[t], CertPathValidator1Test.invalidValues[i]);
                    TestCase.fail("NoSuchProviderException must be thrown");
                } catch (NoSuchProviderException e1) {
                }
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm  is not available
     */
    public void testCertPathValidator06() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        try {
            CertPathValidator.getInstance(null, CertPathValidator1Test.defaultProviderName);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (CertPathValidator1Test.invalidValues.length); i++) {
            try {
                CertPathValidator.getInstance(CertPathValidator1Test.invalidValues[i], CertPathValidator1Test.defaultProviderName);
                TestCase.fail("NoSuchAlgorithmException must be thrown");
            } catch (NoSuchAlgorithmException e1) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion: returns CertPathValidator object
     */
    public void testCertPathValidator07() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        CertPathValidator certPV;
        for (int i = 0; i < (CertPathValidator1Test.validValues.length); i++) {
            certPV = CertPathValidator.getInstance(CertPathValidator1Test.validValues[i], CertPathValidator1Test.defaultProviderName);
            TestCase.assertEquals("Incorrect algorithm", certPV.getAlgorithm(), CertPathValidator1Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider name", certPV.getProvider().getName(), CertPathValidator1Test.defaultProviderName);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code> method
     * Assertion: throws IllegalArgumentException when provider is null
     */
    public void testCertPathValidator08() throws NoSuchAlgorithmException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        Provider prov = null;
        for (int t = 0; t < (CertPathValidator1Test.validValues.length); t++) {
            try {
                CertPathValidator.getInstance(CertPathValidator1Test.validValues[t], prov);
                TestCase.fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e1) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm  is not available
     */
    public void testCertPathValidator09() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        try {
            CertPathValidator.getInstance(null, CertPathValidator1Test.defaultProvider);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (CertPathValidator1Test.invalidValues.length); i++) {
            try {
                CertPathValidator.getInstance(CertPathValidator1Test.invalidValues[i], CertPathValidator1Test.defaultProvider);
                TestCase.fail("NoSuchAlgorithm must be thrown");
            } catch (NoSuchAlgorithmException e1) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion: returns CertPathValidator object
     */
    public void testCertPathValidator10() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        CertPathValidator certPV;
        for (int i = 0; i < (CertPathValidator1Test.invalidValues.length); i++) {
            certPV = CertPathValidator.getInstance(CertPathValidator1Test.validValues[i], CertPathValidator1Test.defaultProvider);
            TestCase.assertEquals("Incorrect algorithm", certPV.getAlgorithm(), CertPathValidator1Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider name", certPV.getProvider(), CertPathValidator1Test.defaultProvider);
        }
    }

    /**
     * Test for <code>validate(CertPath certpath, CertPathParameters params)</code> method
     * Assertion: throws InvalidAlgorithmParameterException params is not
     * instance of PKIXParameters or null
     */
    public void testCertPathValidator11() throws NoSuchAlgorithmException, NoSuchProviderException, CertPathValidatorException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        CertPathValidator[] certPV = CertPathValidator1Test.createCPVs();
        TestCase.assertNotNull("CertPathValidator objects were not created", certPV);
        MyCertPath mCP = new MyCertPath(new byte[0]);
        invalidParams mPar = new invalidParams();
        for (int i = 0; i < (certPV.length); i++) {
            try {
                certPV[i].validate(mCP, mPar);
                TestCase.fail("InvalidAlgorithmParameterException must be thrown");
            } catch (InvalidAlgorithmParameterException e) {
            }
            try {
                certPV[i].validate(mCP, null);
                TestCase.fail("InvalidAlgorithmParameterException must be thrown");
            } catch (InvalidAlgorithmParameterException e) {
            }
        }
    }

    /**
     * Test for
     * <code>CertPathValidator</code> constructor
     * Assertion: returns CertPathValidator object
     */
    public void testCertPathValidator12() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CertPathValidatorException, CertificateException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        CertPathValidatorSpi spi = new MyCertPathValidatorSpi();
        CertPathValidator certPV = new myCertPathValidator(spi, CertPathValidator1Test.defaultProvider, CertPathValidator1Test.defaultType);
        TestCase.assertEquals("Incorrect algorithm", certPV.getAlgorithm(), CertPathValidator1Test.defaultType);
        TestCase.assertEquals("Incorrect provider", certPV.getProvider(), CertPathValidator1Test.defaultProvider);
        certPV.validate(null, null);
        try {
            certPV.validate(null, null);
            TestCase.fail("CertPathValidatorException must be thrown");
        } catch (CertPathValidatorException e) {
        }
        certPV = new myCertPathValidator(null, null, null);
        TestCase.assertNull("Incorrect algorithm", certPV.getAlgorithm());
        TestCase.assertNull("Incorrect provider", certPV.getProvider());
        try {
            certPV.validate(null, null);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test for <code>getAlgorithm()</code> method
     */
    public void testCertPathValidator13() throws NoSuchAlgorithmException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        CertPathValidator certPV;
        for (int i = 0; i < (CertPathValidator1Test.validValues.length); i++) {
            certPV = CertPathValidator.getInstance(CertPathValidator1Test.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", certPV.getAlgorithm(), CertPathValidator1Test.validValues[i]);
            try {
                certPV = CertPathValidator.getInstance(CertPathValidator1Test.validValues[i], CertPathValidator1Test.defaultProviderName);
                TestCase.assertEquals("Incorrect algorithm", certPV.getAlgorithm(), CertPathValidator1Test.validValues[i]);
            } catch (NoSuchProviderException e) {
                TestCase.fail(("Unexpected NoSuchAlgorithmException " + (e.getMessage())));
            }
            certPV = CertPathValidator.getInstance(CertPathValidator1Test.validValues[i], CertPathValidator1Test.defaultProvider);
            TestCase.assertEquals("Incorrect algorithm", certPV.getAlgorithm(), CertPathValidator1Test.validValues[i]);
        }
    }

    /**
     * Test for <code>getProvider()</code> method
     */
    public void testCertPathValidator14() throws NoSuchAlgorithmException {
        if (!(CertPathValidator1Test.PKIXSupport)) {
            TestCase.fail(CertPathValidator1Test.NotSupportMsg);
            return;
        }
        CertPathValidator certPV;
        for (int i = 0; i < (CertPathValidator1Test.validValues.length); i++) {
            try {
                certPV = CertPathValidator.getInstance(CertPathValidator1Test.validValues[i], CertPathValidator1Test.defaultProviderName);
                TestCase.assertEquals("Incorrect provider", certPV.getProvider(), CertPathValidator1Test.defaultProvider);
            } catch (NoSuchProviderException e) {
                TestCase.fail(("Unexpected NoSuchProviderException " + (e.getMessage())));
            }
            certPV = CertPathValidator.getInstance(CertPathValidator1Test.validValues[i], CertPathValidator1Test.defaultProvider);
            TestCase.assertEquals("Incorrect provider", certPV.getProvider(), CertPathValidator1Test.defaultProvider);
        }
    }
}

