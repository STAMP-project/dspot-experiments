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
package org.apache.harmony.crypto.tests.javax.crypto;


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import junit.framework.TestCase;
import org.apache.harmony.crypto.tests.support.MyKeyAgreementSpi;


/**
 * Tests for <code>KeyAgreementSpi</code> class constructors and methods.
 */
public class KeyAgreementSpiTest extends TestCase {
    class Mock_KeyAgreementSpi extends MyKeyAgreementSpi {
        @Override
        protected Key engineDoPhase(Key key, boolean lastPhase) throws IllegalStateException, InvalidKeyException {
            return super.engineDoPhase(key, lastPhase);
        }

        @Override
        protected byte[] engineGenerateSecret() throws IllegalStateException {
            return super.engineGenerateSecret();
        }

        @Override
        protected SecretKey engineGenerateSecret(String algorithm) throws IllegalStateException, InvalidKeyException, NoSuchAlgorithmException {
            return super.engineGenerateSecret(algorithm);
        }

        @Override
        protected int engineGenerateSecret(byte[] sharedSecret, int offset) throws IllegalStateException, ShortBufferException {
            return super.engineGenerateSecret(sharedSecret, offset);
        }

        @Override
        protected void engineInit(Key key, SecureRandom random) throws InvalidKeyException {
            super.engineInit(key, random);
        }

        @Override
        protected void engineInit(Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException, InvalidKeyException {
            super.engineInit(key, params, random);
        }
    }

    /**
     * Test for <code>KeyAgreementSpi</code> constructor Assertion: constructs
     * KeyAgreementSpi
     */
    public void testKeyAgreementSpi01() throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, ShortBufferException {
        KeyAgreementSpiTest.Mock_KeyAgreementSpi kaSpi = new KeyAgreementSpiTest.Mock_KeyAgreementSpi();
        TestCase.assertNull("Not null result", kaSpi.engineDoPhase(null, true));
        try {
            kaSpi.engineDoPhase(null, false);
            TestCase.fail("IllegalStateException must be thrown");
        } catch (IllegalStateException e) {
        }
        byte[] bb = kaSpi.engineGenerateSecret();
        TestCase.assertEquals("Length is not 0", bb.length, 0);
        TestCase.assertEquals("Returned integer is not 0", kaSpi.engineGenerateSecret(new byte[1], 10), (-1));
        TestCase.assertNull("Not null result", kaSpi.engineGenerateSecret("aaa"));
        try {
            kaSpi.engineGenerateSecret("");
            TestCase.fail("NoSuchAlgorithmException must be thrown");
        } catch (NoSuchAlgorithmException e) {
        }
        Key key = null;
        try {
            kaSpi.engineInit(key, new SecureRandom());
            TestCase.fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
        AlgorithmParameterSpec params = null;
        try {
            kaSpi.engineInit(key, params, new SecureRandom());
            TestCase.fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
    }
}

