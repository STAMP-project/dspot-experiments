/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.security.interfaces;


import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.security.interfaces.DSAParams;
import java.security.spec.DSAParameterSpec;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.interfaces.DSAKeyPairGeneratorImpl;


public class DSAKeyPairGeneratorTest extends TestCase {
    private final BigInteger p = new BigInteger("4");

    private final BigInteger q = BigInteger.TEN;

    private final BigInteger g = BigInteger.ZERO;

    class MyDSA extends DSAKeyPairGeneratorImpl {
        public MyDSA(DSAParams dsaParams) {
            super(dsaParams);
        }
    }

    /**
     * java.security.interfaces.DSAKeyPairGenerator
     * #initialize(DSAParams params, SecureRandom random)
     */
    public void test_DSAKeyPairGenerator01() {
        DSAParams dsaParams = new DSAParameterSpec(p, q, g);
        SecureRandom random = null;
        DSAKeyPairGeneratorTest.MyDSA dsa = new DSAKeyPairGeneratorTest.MyDSA(dsaParams);
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception for SecureRandom: " + e));
        }
        try {
            dsa.initialize(dsaParams, random);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            dsa.initialize(dsaParams, null);
            TestCase.fail("InvalidParameterException was not thrown");
        } catch (InvalidParameterException ipe) {
            // expected
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of InvalidParameterException"));
        }
        try {
            dsa.initialize(null, random);
            TestCase.fail("InvalidParameterException was not thrown");
        } catch (InvalidParameterException ipe) {
            // expected
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of InvalidParameterException"));
        }
    }

    /**
     * java.security.interfaces.DSAKeyPairGenerator
     * #initialize(int modlen, boolean genParams, SecureRandom randomm)
     */
    public void test_DSAKeyPairGenerator02() {
        int[] invalidLen = new int[]{ -1, 0, 511, 513, 650, 1023, 1025 };
        DSAParams dsaParams = new DSAParameterSpec(p, q, g);
        SecureRandom random = null;
        DSAKeyPairGeneratorTest.MyDSA dsa = new DSAKeyPairGeneratorTest.MyDSA(null);
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception for SecureRandom: " + e));
        }
        // exception case
        try {
            initialize(520, false, random);
            TestCase.fail("InvalidParameterException was not thrown");
        } catch (InvalidParameterException ipe) {
            String str = ipe.getMessage();
            if (!(str.equals("there are not precomputed parameters"))) {
                TestCase.fail(("Incorrect exception's message: " + str));
            }
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of InvalidParameterException"));
        }
        // exception case
        for (int i = 0; i < (invalidLen.length); i++) {
            try {
                initialize(invalidLen[i], true, random);
                TestCase.fail("InvalidParameterException was not thrown");
            } catch (InvalidParameterException ipe) {
                String str = ipe.getMessage();
                if (!(str.equals("Incorrect modlen"))) {
                    TestCase.fail(("Incorrect exception's message: " + str));
                }
            } catch (Exception e) {
                TestCase.fail((e + " was thrown instead of InvalidParameterException"));
            }
        }
        // positive case
        dsa = new DSAKeyPairGeneratorTest.MyDSA(dsaParams);
        try {
            initialize(520, true, random);
        } catch (Exception e) {
            TestCase.fail((e + " was thrown for subcase 1"));
        }
        // positive case
        try {
            initialize(520, false, random);
        } catch (Exception e) {
            TestCase.fail((e + " was thrown for subcase 1"));
        }
    }
}

