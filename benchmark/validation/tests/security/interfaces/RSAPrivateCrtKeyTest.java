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


import java.security.interfaces.RSAPrivateCrtKey;
import junit.framework.TestCase;


public class RSAPrivateCrtKeyTest extends TestCase {
    RSAPrivateCrtKey key = null;

    /**
     * java.security.interfaces.RSAPrivateCrtKey
     * #getCrtCoefficient()
     */
    public void test_getCrtCoefficient() {
        TestCase.assertEquals("invalid CRT coefficient", Util.rsaCrtParam.getCrtCoefficient(), key.getCrtCoefficient());
    }

    /**
     * java.security.interfaces.RSAPrivateCrtKey
     * #getPrimeExponentP()
     */
    public void test_getPrimeExponentP() {
        TestCase.assertEquals("invalid prime exponent P", Util.rsaCrtParam.getPrimeExponentP(), key.getPrimeExponentP());
    }

    /**
     * java.security.interfaces.RSAPrivateCrtKey
     * #getPrimeExponentQ()
     */
    public void test_getPrimeExponentQ() {
        TestCase.assertEquals("invalid prime exponent Q", Util.rsaCrtParam.getPrimeExponentQ(), key.getPrimeExponentQ());
    }

    /**
     * java.security.interfaces.RSAPrivateCrtKey
     * #getPrimeP()
     */
    public void test_getPrimeP() {
        TestCase.assertEquals("invalid prime P", Util.rsaCrtParam.getPrimeP(), key.getPrimeP());
    }

    /**
     * java.security.interfaces.RSAPrivateCrtKey
     * #getPrimeQ()
     */
    public void test_getPrimeQ() {
        TestCase.assertEquals("invalid prime Q", Util.rsaCrtParam.getPrimeQ(), key.getPrimeQ());
    }

    /**
     * java.security.interfaces.RSAPrivateCrtKey
     * #getPublicExponent()
     */
    public void test_getPublicExponent() {
        TestCase.assertEquals("invalid public exponent", Util.rsaCrtParam.getPublicExponent(), key.getPublicExponent());
    }
}

