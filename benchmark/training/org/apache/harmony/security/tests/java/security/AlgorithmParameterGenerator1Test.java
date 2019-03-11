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
import java.security.AlgorithmParameterGeneratorSpi;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.MyAlgorithmParameterGeneratorSpi;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for <code>AlgorithmParameterGenerator</code> class constructors and
 * methods.
 */
public class AlgorithmParameterGenerator1Test extends TestCase {
    private static String[] invalidValues = SpiEngUtils.invalidValues;

    private static String validAlgName = "DSA";

    private static String[] algs = new String[]{ "DSA", "dsa", "Dsa", "DsA", "dsA" };

    public static final String srvAlgorithmParameterGenerator = "AlgorithmParameterGenerator";

    private static String validProviderName = null;

    private static Provider validProvider = null;

    private static boolean DSASupported = false;

    static {
        AlgorithmParameterGenerator1Test.validProvider = SpiEngUtils.isSupport(AlgorithmParameterGenerator1Test.validAlgName, AlgorithmParameterGenerator1Test.srvAlgorithmParameterGenerator);
        AlgorithmParameterGenerator1Test.DSASupported = (AlgorithmParameterGenerator1Test.validProvider) != null;
        AlgorithmParameterGenerator1Test.validProviderName = (AlgorithmParameterGenerator1Test.DSASupported) ? AlgorithmParameterGenerator1Test.validProvider.getName() : null;
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion:
     * throws NullPointerException must be thrown is null
     * throws NoSuchAlgorithmException must be thrown if algorithm is not available
     */
    public void testAlgorithmParameterGenerator01() throws NoSuchAlgorithmException {
        try {
            AlgorithmParameterGenerator.getInstance(null);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (AlgorithmParameterGenerator1Test.invalidValues.length); i++) {
            try {
                AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator1Test.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException should be thrown");
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion: returns AlgorithmParameterGenerator instance
     * when algorithm is DSA
     */
    public void testAlgorithmParameterGenerator02() throws NoSuchAlgorithmException {
        if (!(AlgorithmParameterGenerator1Test.DSASupported)) {
            TestCase.fail(((AlgorithmParameterGenerator1Test.validAlgName) + " algorithm is not supported"));
            return;
        }
        AlgorithmParameterGenerator apg;
        for (int i = 0; i < (AlgorithmParameterGenerator1Test.algs.length); i++) {
            apg = AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator1Test.algs[i]);
            TestCase.assertEquals("Incorrect algorithm", apg.getAlgorithm(), AlgorithmParameterGenerator1Test.algs[i]);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion:
     * throws IllegalArgumentException if provider is null or empty
     */
    public void testAlgorithmParameterGenerator03() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(AlgorithmParameterGenerator1Test.DSASupported)) {
            TestCase.fail(((AlgorithmParameterGenerator1Test.validAlgName) + " algorithm is not supported"));
            return;
        }
        String provider = null;
        for (int i = 0; i < (AlgorithmParameterGenerator1Test.algs.length); i++) {
            try {
                AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator1Test.algs[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
            try {
                AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator1Test.algs[i], "");
                TestCase.fail("IllegalArgumentException must be thrown when provider is empty");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion: throws NoSuchProviderException if provider is not
     * available
     */
    public void testAlgorithmParameterGenerator04() throws NoSuchAlgorithmException {
        if (!(AlgorithmParameterGenerator1Test.DSASupported)) {
            TestCase.fail(((AlgorithmParameterGenerator1Test.validAlgName) + " algorithm is not supported"));
            return;
        }
        for (int i = 0; i < (AlgorithmParameterGenerator1Test.algs.length); i++) {
            for (int j = 1; j < (AlgorithmParameterGenerator1Test.invalidValues.length); j++) {
                try {
                    AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator1Test.algs[i], AlgorithmParameterGenerator1Test.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException must be thrown (provider: ".concat(AlgorithmParameterGenerator1Test.invalidValues[j]));
                } catch (NoSuchProviderException e) {
                }
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion:
     * throws NullPointerException must be thrown is null
     * throws NoSuchAlgorithmException must be thrown if algorithm is not available
     */
    public void testAlgorithmParameterGenerator05() throws NoSuchProviderException {
        if (!(AlgorithmParameterGenerator1Test.DSASupported)) {
            TestCase.fail(((AlgorithmParameterGenerator1Test.validAlgName) + " algorithm is not supported"));
            return;
        }
        try {
            AlgorithmParameterGenerator.getInstance(null, AlgorithmParameterGenerator1Test.validProviderName);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (AlgorithmParameterGenerator1Test.invalidValues.length); i++) {
            try {
                AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator1Test.invalidValues[i], AlgorithmParameterGenerator1Test.validProviderName);
                TestCase.fail("NoSuchAlgorithmException must be thrown when (algorithm: ".concat(AlgorithmParameterGenerator1Test.invalidValues[i].concat(")")));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code>
     * method
     * Assertion: return AlgorithmParameterGenerator
     */
    public void testAlgorithmParameterGenerator06() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(AlgorithmParameterGenerator1Test.DSASupported)) {
            TestCase.fail(((AlgorithmParameterGenerator1Test.validAlgName) + " algorithm is not supported"));
            return;
        }
        AlgorithmParameterGenerator apg;
        for (int i = 0; i < (AlgorithmParameterGenerator1Test.algs.length); i++) {
            apg = AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator1Test.algs[i], AlgorithmParameterGenerator1Test.validProviderName);
            TestCase.assertEquals("Incorrect algorithm", AlgorithmParameterGenerator1Test.algs[i], apg.getAlgorithm());
            TestCase.assertEquals("Incorrect provider", apg.getProvider().getName(), AlgorithmParameterGenerator1Test.validProviderName);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion: throws IllegalArgumentException when provider is null
     */
    public void testAlgorithmParameterGenerator07() throws NoSuchAlgorithmException {
        if (!(AlgorithmParameterGenerator1Test.DSASupported)) {
            TestCase.fail(((AlgorithmParameterGenerator1Test.validAlgName) + " algorithm is not supported"));
            return;
        }
        Provider provider = null;
        for (int i = 0; i < (AlgorithmParameterGenerator1Test.algs.length); i++) {
            try {
                AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator1Test.algs[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion:
     * throws NullPointerException must be thrown is null
     * throws NoSuchAlgorithmException must be thrown if algorithm is not available
     */
    public void testAlgorithmParameterGenerator08() {
        if (!(AlgorithmParameterGenerator1Test.DSASupported)) {
            TestCase.fail(((AlgorithmParameterGenerator1Test.validAlgName) + " algorithm is not supported"));
            return;
        }
        try {
            AlgorithmParameterGenerator.getInstance(null, AlgorithmParameterGenerator1Test.validProvider);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (AlgorithmParameterGenerator1Test.invalidValues.length); i++) {
            try {
                AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator1Test.invalidValues[i], AlgorithmParameterGenerator1Test.validProvider);
                TestCase.fail("NoSuchAlgorithmException must be thrown (algorithm: ".concat(AlgorithmParameterGenerator1Test.invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method
     * Assertion: returns AlgorithmParameterGenerator object
     */
    public void testAlgorithmParameterGenerator09() throws NoSuchAlgorithmException {
        if (!(AlgorithmParameterGenerator1Test.DSASupported)) {
            TestCase.fail(((AlgorithmParameterGenerator1Test.validAlgName) + " algorithm is not supported"));
            return;
        }
        AlgorithmParameterGenerator apg;
        for (int i = 0; i < (AlgorithmParameterGenerator1Test.algs.length); i++) {
            apg = AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator1Test.algs[i], AlgorithmParameterGenerator1Test.validProvider);
            TestCase.assertEquals("Incorrect algorithm", apg.getAlgorithm(), AlgorithmParameterGenerator1Test.algs[i]);
            TestCase.assertEquals("Incorrect provider", apg.getProvider(), AlgorithmParameterGenerator1Test.validProvider);
        }
    }

    /**
     * Test for <code>generateParameters()</code> method
     * Assertion: returns AlgorithmParameters object
     */
    public void testAlgorithmParameterGenerator10() throws NoSuchAlgorithmException {
        if (!(AlgorithmParameterGenerator1Test.DSASupported)) {
            TestCase.fail(((AlgorithmParameterGenerator1Test.validAlgName) + " algorithm is not supported"));
            return;
        }
        AlgorithmParameterGenerator apg = AlgorithmParameterGenerator.getInstance(AlgorithmParameterGenerator1Test.validAlgName);
        apg.init(512);
        AlgorithmParameters ap = apg.generateParameters();
        TestCase.assertEquals("Incorrect algorithm", ap.getAlgorithm().toUpperCase(), apg.getAlgorithm().toUpperCase());
    }

    /**
     * Test for <code>init(AlgorithmParameterSpec param)</code> and
     * <code>init(AlgorithmParameterSpec param, SecureRandom random<code>
     * methods
     * Assertion: throws InvalidAlgorithmParameterException when param is null
     */
    public void testAlgorithmParameterGenerator12() {
        if (!(AlgorithmParameterGenerator1Test.DSASupported)) {
            TestCase.fail(((AlgorithmParameterGenerator1Test.validAlgName) + " algorithm is not supported"));
            return;
        }
        SecureRandom random = new SecureRandom();
        AlgorithmParameterSpec aps = null;
        AlgorithmParameterGenerator[] apgs = createAPGen();
        TestCase.assertNotNull("AlgorithmParameterGenerator objects were not created", apgs);
        for (int i = 0; i < (apgs.length); i++) {
            try {
                apgs[i].init(aps);
                TestCase.fail("InvalidAlgorithmParameterException expected for null argument.");
            } catch (InvalidAlgorithmParameterException e) {
                // expected
            }
            try {
                apgs[i].init(aps, random);
                TestCase.fail("InvalidAlgorithmParameterException expected for null argument.");
            } catch (InvalidAlgorithmParameterException e) {
                // expected
            }
        }
    }

    /**
     * Test for <code>AlgorithmParameterGenerator</code> constructor
     * Assertion: returns AlgorithmParameterGenerator object
     */
    public void testConstructor() throws NoSuchAlgorithmException {
        if (!(AlgorithmParameterGenerator1Test.DSASupported)) {
            TestCase.fail(((AlgorithmParameterGenerator1Test.validAlgName) + " algorithm is not supported"));
            return;
        }
        AlgorithmParameterGeneratorSpi spi = new MyAlgorithmParameterGeneratorSpi();
        AlgorithmParameterGenerator apg = new myAlgPG(spi, AlgorithmParameterGenerator1Test.validProvider, AlgorithmParameterGenerator1Test.validAlgName);
        TestCase.assertEquals("Incorrect algorithm", apg.getAlgorithm(), AlgorithmParameterGenerator1Test.validAlgName);
        TestCase.assertEquals("Incorrect provider", apg.getProvider(), AlgorithmParameterGenerator1Test.validProvider);
        try {
            apg.init((-10), null);
            TestCase.fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
        apg = new myAlgPG(null, null, null);
        TestCase.assertNull("Incorrect algorithm", apg.getAlgorithm());
        TestCase.assertNull("Incorrect provider", apg.getProvider());
        try {
            apg.init((-10), null);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
    }
}

