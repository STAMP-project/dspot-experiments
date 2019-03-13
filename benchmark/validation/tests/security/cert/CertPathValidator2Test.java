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
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXParameters;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;
import org.apache.harmony.security.tests.support.cert.MyCertPath;
import org.apache.harmony.security.tests.support.cert.TestUtils;


/**
 * Tests for CertPathValidator class constructors and methods
 */
public class CertPathValidator2Test extends TestCase {
    private static final String defaultAlg = "CertPB";

    public static final String CertPathValidatorProviderClass = "org.apache.harmony.security.tests.support.cert.MyCertPathValidatorSpi";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static final String[] validValues;

    static {
        validValues = new String[4];
        CertPathValidator2Test.validValues[0] = CertPathValidator2Test.defaultAlg;
        CertPathValidator2Test.validValues[1] = CertPathValidator2Test.defaultAlg.toLowerCase();
        CertPathValidator2Test.validValues[2] = "CeRtPb";
        CertPathValidator2Test.validValues[3] = "cERTpb";
    }

    Provider mProv;

    /**
     * Test for <code>getInstance(String algorithm)</code> method Assertions:
     * throws NullPointerException when algorithm is null throws
     * NoSuchAlgorithmException when algorithm is not available returns
     * CertPathValidator object
     */
    public void testGetInstance01() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertPathValidatorException {
        try {
            CertPathValidator.getInstance(null);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (CertPathValidator2Test.invalidValues.length); i++) {
            try {
                CertPathValidator.getInstance(CertPathValidator2Test.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException must be thrown (type: ".concat(CertPathValidator2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        CertPathValidator cerPV;
        for (int i = 0; i < (CertPathValidator2Test.validValues.length); i++) {
            cerPV = CertPathValidator.getInstance(CertPathValidator2Test.validValues[i]);
            TestCase.assertEquals("Incorrect type", cerPV.getAlgorithm(), CertPathValidator2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", cerPV.getProvider(), mProv);
            checkResult(cerPV);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method Assertions: throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is not available throws
     * IllegalArgumentException when provider is null or empty; throws
     * NoSuchProviderException when provider is available; returns
     * CertPathValidator object
     */
    public void testGetInstance02() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CertPathValidatorException {
        try {
            CertPathValidator.getInstance(null, mProv.getName());
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (CertPathValidator2Test.invalidValues.length); i++) {
            try {
                CertPathValidator.getInstance(CertPathValidator2Test.invalidValues[i], mProv.getName());
                TestCase.fail("NoSuchAlgorithmException must be thrown (type: ".concat(CertPathValidator2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        String prov = null;
        for (int i = 0; i < (CertPathValidator2Test.validValues.length); i++) {
            try {
                CertPathValidator.getInstance(CertPathValidator2Test.validValues[i], prov);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null (type: ".concat(CertPathValidator2Test.validValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
            try {
                CertPathValidator.getInstance(CertPathValidator2Test.validValues[i], "");
                TestCase.fail("IllegalArgumentException must be thrown when provider is empty (type: ".concat(CertPathValidator2Test.validValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        for (int i = 0; i < (CertPathValidator2Test.validValues.length); i++) {
            for (int j = 1; j < (CertPathValidator2Test.invalidValues.length); j++) {
                try {
                    CertPathValidator.getInstance(CertPathValidator2Test.validValues[i], CertPathValidator2Test.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException must be thrown (type: ".concat(CertPathValidator2Test.validValues[i]).concat(" provider: ").concat(CertPathValidator2Test.invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
        CertPathValidator cerPV;
        for (int i = 0; i < (CertPathValidator2Test.validValues.length); i++) {
            cerPV = CertPathValidator.getInstance(CertPathValidator2Test.validValues[i], mProv.getName());
            TestCase.assertEquals("Incorrect type", cerPV.getAlgorithm(), CertPathValidator2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", cerPV.getProvider().getName(), mProv.getName());
            checkResult(cerPV);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method Assertions: throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is not available throws
     * IllegalArgumentException when provider is null; returns CertPathValidator
     * object
     */
    public void testGetInstance03() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertPathValidatorException {
        try {
            CertPathValidator.getInstance(null, mProv);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (CertPathValidator2Test.invalidValues.length); i++) {
            try {
                CertPathValidator.getInstance(CertPathValidator2Test.invalidValues[i], mProv);
                TestCase.fail("NoSuchAlgorithmException must be thrown (type: ".concat(CertPathValidator2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        Provider prov = null;
        for (int i = 0; i < (CertPathValidator2Test.validValues.length); i++) {
            try {
                CertPathValidator.getInstance(CertPathValidator2Test.validValues[i], prov);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null (type: ".concat(CertPathValidator2Test.validValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        CertPathValidator cerPV;
        for (int i = 0; i < (CertPathValidator2Test.validValues.length); i++) {
            cerPV = CertPathValidator.getInstance(CertPathValidator2Test.validValues[i], mProv);
            TestCase.assertEquals("Incorrect type", cerPV.getAlgorithm(), CertPathValidator2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", cerPV.getProvider(), mProv);
            checkResult(cerPV);
        }
    }

    public void testValidate() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        MyCertPath mCP = new MyCertPath(new byte[0]);
        CertPathParameters params = new PKIXParameters(TestUtils.getTrustAnchorSet());
        CertPathValidator certPV = CertPathValidator.getInstance(CertPathValidator2Test.defaultAlg);
        try {
            certPV.validate(mCP, params);
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertPathValidatorException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            certPV.validate(null, params);
            TestCase.fail("NullPointerException must be thrown");
        } catch (InvalidAlgorithmParameterException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertPathValidatorException e) {
            // ok
        }
        try {
            certPV.validate(mCP, null);
            TestCase.fail("InvalidAlgorithmParameterException must be thrown");
        } catch (InvalidAlgorithmParameterException e) {
            // ok
        } catch (CertPathValidatorException e) {
            TestCase.fail("unexpected exception");
        }
    }
}

