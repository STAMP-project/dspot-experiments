/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.security.spec;


import java.math.BigInteger;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import junit.framework.TestCase;


public class ECPrivateKeySpecTest extends TestCase {
    BigInteger s;

    ECParameterSpec ecparams;

    ECPrivateKeySpec ecpks;

    /**
     * test for constructor ECPrivateKeySpec(BigInteger, ECParameterSpec)
     * test covers following usecases:
     * case 1: creating object with valid parameters
     * case 2: catch NullPointerException - if s is null.
     * case 3: catch NullPointerException - if params is null.
     */
    public void test_constructorLjava_math_BigIntegerLjava_security_spec_ECParameterSpec() {
        // case 1: creating object with valid parameters
        TestCase.assertEquals("wrong private value", s, ecpks.getS());
        TestCase.assertEquals("wrong parameters", ecparams, ecpks.getParams());
        // case 2: catch NullPointerException - if s is null.
        try {
            new ECPrivateKeySpec(null, ecparams);
            TestCase.fail("NullPointerException has not been thrown");
        } catch (NullPointerException e) {
            // expected
        }
        // case 3: catch NullPointerException - if params is null.
        try {
            new ECPrivateKeySpec(s, null);
            TestCase.fail("NullPointerException has not been thrown");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * test for getS() method
     */
    public void test_GetS() {
        TestCase.assertEquals("wrong private value", s, ecpks.getS());
    }

    /**
     * test for getParams() method
     */
    public void test_GetParams() {
        TestCase.assertEquals("wrong parameters", ecparams, ecpks.getParams());
    }
}

