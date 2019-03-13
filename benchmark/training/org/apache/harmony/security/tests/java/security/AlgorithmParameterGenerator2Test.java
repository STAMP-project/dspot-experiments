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
package org.apache.harmony.security.tests.java.security;


import java.security.AlgorithmParameterGenerator;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for <code>AlgorithmParameterGenerator</code> class constructors and
 * methods.
 */
public class AlgorithmParameterGenerator2Test extends TestCase {
    private static final String AlgorithmParameterGeneratorProviderClass = "org.apache.harmony.security.tests.support.MyAlgorithmParameterGeneratorSpi";

    private static final String defaultAlg = "APG";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static final String[] validValues;

    static {
        validValues = new String[4];
        AlgorithmParameterGenerator2Test.validValues[0] = AlgorithmParameterGenerator2Test.defaultAlg;
        AlgorithmParameterGenerator2Test.validValues[1] = AlgorithmParameterGenerator2Test.defaultAlg.toLowerCase();
        AlgorithmParameterGenerator2Test.validValues[2] = "apG";
        AlgorithmParameterGenerator2Test.validValues[3] = "ApG";
    }

    Provider mProv;

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertions:
     * throws NullPointerException must be thrown is null
     * throws NoSuchAlgorithmException must be thrown if algorithm is not available
     * returns AlgorithmParameterGenerator object
     */
    public void testGetInstance01() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        try {
            AlgorithmParameterGenerator.getInstance(null);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (AlgorithmParameterGenerator2Test.invalidValues.length); i++) {
            try {
                AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator2Test.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(AlgorithmParameterGenerator2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        AlgorithmParameterGenerator apG;
        for (int i = 0; i < (AlgorithmParameterGenerator2Test.validValues.length); i++) {
            apG = AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator2Test.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", apG.getAlgorithm(), AlgorithmParameterGenerator2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", apG.getProvider(), mProv);
            checkResult(apG);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertions:
     * throws NullPointerException must be thrown is null
     * throws NoSuchAlgorithmException must be thrown if algorithm is not available
     * throws IllegalArgumentException when provider is null;
     * throws NoSuchProviderException when provider is available;
     * returns AlgorithmParameterGenerator object
     */
    public void testGetInstance02() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            AlgorithmParameterGenerator.getInstance(null, mProv.getName());
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (AlgorithmParameterGenerator2Test.invalidValues.length); i++) {
            try {
                AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator2Test.invalidValues[i], mProv.getName());
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(AlgorithmParameterGenerator2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        String prov = null;
        for (int i = 0; i < (AlgorithmParameterGenerator2Test.validValues.length); i++) {
            try {
                AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator2Test.validValues[i], prov);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null (algorithm: ".concat(AlgorithmParameterGenerator2Test.invalidValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        for (int i = 0; i < (AlgorithmParameterGenerator2Test.validValues.length); i++) {
            for (int j = 1; j < (AlgorithmParameterGenerator2Test.invalidValues.length); j++) {
                try {
                    AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator2Test.validValues[i], AlgorithmParameterGenerator2Test.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException must be thrown (algorithm: ".concat(AlgorithmParameterGenerator2Test.invalidValues[i]).concat(" provider: ").concat(AlgorithmParameterGenerator2Test.invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
        AlgorithmParameterGenerator apG;
        for (int i = 0; i < (AlgorithmParameterGenerator2Test.validValues.length); i++) {
            apG = AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator2Test.validValues[i], mProv.getName());
            TestCase.assertEquals("Incorrect algorithm", apG.getAlgorithm(), AlgorithmParameterGenerator2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", apG.getProvider().getName(), mProv.getName());
            checkResult(apG);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertions:
     * throws NullPointerException must be thrown is null
     * throws NoSuchAlgorithmException must be thrown if algorithm is not available
     * throws IllegalArgumentException when provider is null;
     * returns AlgorithmParameterGenerator object
     */
    public void testGetInstance03() throws IllegalArgumentException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        try {
            AlgorithmParameterGenerator.getInstance(null, mProv);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (AlgorithmParameterGenerator2Test.invalidValues.length); i++) {
            try {
                AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator2Test.invalidValues[i], mProv);
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(AlgorithmParameterGenerator2Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        Provider prov = null;
        for (int i = 0; i < (AlgorithmParameterGenerator2Test.validValues.length); i++) {
            try {
                AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator2Test.validValues[i], prov);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null (algorithm: ".concat(AlgorithmParameterGenerator2Test.invalidValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        AlgorithmParameterGenerator apG;
        for (int i = 0; i < (AlgorithmParameterGenerator2Test.validValues.length); i++) {
            apG = AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator2Test.validValues[i], mProv);
            TestCase.assertEquals("Incorrect algorithm", apG.getAlgorithm(), AlgorithmParameterGenerator2Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider", apG.getProvider(), mProv);
            checkResult(apG);
        }
    }

    /**
     * Additional class for init(...) methods verification
     */
    class tmpAlgorithmParameterSpec implements AlgorithmParameterSpec {
        private final String type;

        public tmpAlgorithmParameterSpec(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}

