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
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import junit.framework.TestCase;


public class ECParameterSpecTest extends TestCase {
    EllipticCurve curve;

    ECPoint ecpoint;

    ECParameterSpec ecps;

    /**
     * test for ECParameterSpec(EllipticCurve, ECPoint, BigInteger, int) constructor
     * test covers following usecases:
     * case 1: creating object with valid parameters
     * case 2: NullPointerException - if curve is null
     * case 3: NullPointerException - if g is null
     * case 4: NullPointerException - if n is null
     * case 5: IllegalArgumentException - if n is not positive
     * case 6: IllegalArgumentException - if h is not positive
     */
    public void test_constructorLjava_security_spec_EllipticCurveLjava_security_spec_ECPointLjava_math_BigIntegerI() {
        // case 1: creating object with valid parameters
        TestCase.assertEquals("wrong cofactor was returned", 1, ecps.getCofactor());
        TestCase.assertEquals("wrong elliptic curve", curve, ecps.getCurve());
        TestCase.assertEquals("wrong generator was returned", ecpoint, ecps.getGenerator());
        TestCase.assertEquals("wrong order was reteurned", BigInteger.valueOf(1), ecps.getOrder());
        // case 2: NullPointerException - if curve is null.
        try {
            new ECParameterSpec(null, ecpoint, BigInteger.valueOf(1), 1);
            TestCase.fail("NullPointerException exception has not been thrown");
        } catch (NullPointerException e) {
            // expected
        }
        // case 3: NullPointerException - if g is null.
        try {
            new ECParameterSpec(curve, null, BigInteger.valueOf(1), 1);
            TestCase.fail("NullPointerException exception has not been thrown");
        } catch (NullPointerException e) {
            // expected
        }
        // case 4: NullPointerException - if n is null.
        try {
            new ECParameterSpec(curve, ecpoint, null, 1);
            TestCase.fail("NullPointerException exception has not been thrown");
        } catch (NullPointerException e) {
            // expected
        }
        // case 5: IllegalArgumentException - if n is not positive.
        try {
            new ECParameterSpec(curve, ecpoint, BigInteger.valueOf((-1)), 1);
            TestCase.fail("IllegalArgumentException exception has not been thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
        // case 6: IllegalArgumentException - if h is not positive.
        try {
            new ECParameterSpec(curve, ecpoint, BigInteger.valueOf(1), (-1));
            TestCase.fail("IllegalArgumentException exception has not been thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * test for getCurve() method
     */
    public void test_GetCurve() {
        TestCase.assertEquals("wrong elliptic curve", curve, ecps.getCurve());
    }

    /**
     * test for getGenerator() method
     */
    public void test_GetGenerator() {
        TestCase.assertEquals("wrong generator was returned", ecpoint, ecps.getGenerator());
    }

    /**
     * test for getOrder() method
     */
    public void test_GetOrder() {
        TestCase.assertEquals("wrong order was reteurned", BigInteger.valueOf(1), ecps.getOrder());
    }

    /**
     * test for getCofactor() method
     */
    public void test_GetCofactor() {
        TestCase.assertEquals("wrong cofactor was returned", 1, ecps.getCofactor());
    }
}

