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
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for CertPathBuilder class constructors and methods
 */
public class CertPathBuilder2Test extends TestCase {
    private static final String defaultAlg = "CertPB";

    private static final String CertPathBuilderProviderClass = "org.apache.harmony.security.tests.support.cert.MyCertPathBuilderSpi";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static final String[] validValues;

    static {
        validValues = new String[4];
        CertPathBuilder2Test.validValues[0] = CertPathBuilder2Test.defaultAlg;
        CertPathBuilder2Test.validValues[1] = CertPathBuilder2Test.defaultAlg.toLowerCase();
        CertPathBuilder2Test.validValues[2] = "CeRtPb";
        CertPathBuilder2Test.validValues[3] = "cERTpb";
    }

    Provider mProv;

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertions:
     * throws
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm  is not correct
     * returns CertPathBuilder object
     */
    public void testGetInstance01() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertPathBuilderException {
        try {
            CertPathBuilder.getInstance(null);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (CertPathBuilder2Test.invalidValues.length); i++) {
            try {
                CertPathBuilder.getInstance(CertPathBuilder2Test.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException must be thrown (type: ".concat(CertPathBuilder2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        CertPathBuilder cerPB;
        for (int i = 0; i < (CertPathBuilder2Test.validValues.length); i++) {
            cerPB = CertPathBuilder.getInstance(CertPathBuilder2Test.validValues[i]);
            TestCase.assertEquals("Incorrect type", cerPB.getAlgorithm(), CertPathBuilder2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", cerPB.getProvider(), mProv);
            checkResult(cerPB);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertions:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm  is not correct
     * throws IllegalArgumentException when provider is null or empty;
     * throws NoSuchProviderException when provider is available;
     * returns CertPathBuilder object
     */
    public void testGetInstance02() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CertPathBuilderException {
        try {
            CertPathBuilder.getInstance(null, mProv.getName());
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (CertPathBuilder2Test.invalidValues.length); i++) {
            try {
                CertPathBuilder.getInstance(CertPathBuilder2Test.invalidValues[i], mProv.getName());
                TestCase.fail("NoSuchAlgorithmException must be thrown (type: ".concat(CertPathBuilder2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        String prov = null;
        for (int i = 0; i < (CertPathBuilder2Test.validValues.length); i++) {
            try {
                CertPathBuilder.getInstance(CertPathBuilder2Test.validValues[i], prov);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null (type: ".concat(CertPathBuilder2Test.validValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
            try {
                CertPathBuilder.getInstance(CertPathBuilder2Test.validValues[i], "");
                TestCase.fail("IllegalArgumentException must be thrown when provider is empty (type: ".concat(CertPathBuilder2Test.validValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        for (int i = 0; i < (CertPathBuilder2Test.validValues.length); i++) {
            for (int j = 1; j < (CertPathBuilder2Test.invalidValues.length); j++) {
                try {
                    CertPathBuilder.getInstance(CertPathBuilder2Test.validValues[i], CertPathBuilder2Test.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException must be thrown (type: ".concat(CertPathBuilder2Test.validValues[i]).concat(" provider: ").concat(CertPathBuilder2Test.invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
        CertPathBuilder cerPB;
        for (int i = 0; i < (CertPathBuilder2Test.validValues.length); i++) {
            cerPB = CertPathBuilder.getInstance(CertPathBuilder2Test.validValues[i], mProv.getName());
            TestCase.assertEquals("Incorrect type", cerPB.getAlgorithm(), CertPathBuilder2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", cerPB.getProvider().getName(), mProv.getName());
            checkResult(cerPB);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertions:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm  is not correct
     * returns CertPathBuilder object
     */
    public void testGetInstance03() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertPathBuilderException {
        try {
            CertPathBuilder.getInstance(null, mProv);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (CertPathBuilder2Test.invalidValues.length); i++) {
            try {
                CertPathBuilder.getInstance(CertPathBuilder2Test.invalidValues[i], mProv);
                TestCase.fail("NoSuchAlgorithmException must be thrown (type: ".concat(CertPathBuilder2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        Provider prov = null;
        for (int i = 0; i < (CertPathBuilder2Test.validValues.length); i++) {
            try {
                CertPathBuilder.getInstance(CertPathBuilder2Test.validValues[i], prov);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null (type: ".concat(CertPathBuilder2Test.validValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        CertPathBuilder cerPB;
        for (int i = 0; i < (CertPathBuilder2Test.validValues.length); i++) {
            cerPB = CertPathBuilder.getInstance(CertPathBuilder2Test.validValues[i], mProv);
            TestCase.assertEquals("Incorrect type", cerPB.getAlgorithm(), CertPathBuilder2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", cerPB.getProvider(), mProv);
            checkResult(cerPB);
        }
    }
}

