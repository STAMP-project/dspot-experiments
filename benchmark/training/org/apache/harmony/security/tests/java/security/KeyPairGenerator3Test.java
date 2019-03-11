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


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for KeyPairGenerator class
 */
public class KeyPairGenerator3Test extends TestCase {
    private static String validProviderName = null;

    public static Provider validProvider = null;

    private static boolean DSASupported = false;

    private static String NotSupportMsg = KeyPairGenerator1Test.NotSupportMsg;

    static {
        KeyPairGenerator3Test.validProvider = SpiEngUtils.isSupport(KeyPairGenerator1Test.validAlgName, KeyPairGenerator1Test.srvKeyPairGenerator);
        KeyPairGenerator3Test.DSASupported = (KeyPairGenerator3Test.validProvider) != null;
        KeyPairGenerator3Test.validProviderName = (KeyPairGenerator3Test.DSASupported) ? KeyPairGenerator3Test.validProvider.getName() : null;
    }

    /**
     * Test for <code>generateKeyPair()</code> and <code>genKeyPair()</code>
     * methods
     * Assertion: KeyPairGenerator was initialized before the invocation
     * of these methods
     */
    public void testGenKeyPair01() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyPairGenerator3Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator3Test.NotSupportMsg);
            return;
        }
        KeyPairGenerator[] kpg = createKPGen();
        TestCase.assertNotNull("KeyPairGenerator objects were not created", kpg);
        KeyPair kp;
        KeyPair kp1;
        SecureRandom rr = new SecureRandom();
        for (int i = 0; i < (kpg.length); i++) {
            kpg[i].initialize(512, rr);
            kp = kpg[i].generateKeyPair();
            kp1 = kpg[i].genKeyPair();
            TestCase.assertFalse("Incorrect private key", kp.getPrivate().equals(kp1.getPrivate()));
            TestCase.assertFalse("Incorrect public key", kp.getPublic().equals(kp1.getPublic()));
        }
    }

    /**
     * Test for <code>generateKeyPair()</code> and <code>genKeyPair()</code>
     * methods
     * Assertion: these methods are used without previously initialization
     */
    public void testGenKeyPair02() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(KeyPairGenerator3Test.DSASupported)) {
            TestCase.fail(KeyPairGenerator3Test.NotSupportMsg);
            return;
        }
        KeyPairGenerator[] kpg = createKPGen();
        TestCase.assertNotNull("KeyPairGenerator objects were not created", kpg);
        KeyPair kp;
        KeyPair kp1;
        for (int i = 0; i < (kpg.length); i++) {
            kp = kpg[i].generateKeyPair();
            kp1 = kpg[i].genKeyPair();
            TestCase.assertFalse("Incorrect private key", kp.getPrivate().equals(kp1.getPrivate()));
            TestCase.assertFalse("Incorrect public key", kp.getPublic().equals(kp1.getPublic()));
        }
    }

    /**
     * Test for <code>KeyPairGenerator</code> constructor
     * Assertion: returns KeyPairGenerator object
     */
    public void testKeyPairGeneratorConst() {
        String[] alg = new String[]{ null, "", "AsDfGh!#$*", "DSA", "RSA" };
        KeyPairGenerator3Test.MykeyPGen kpg;
        for (int i = 0; i < (alg.length); i++) {
            try {
                kpg = new KeyPairGenerator3Test.MykeyPGen(alg[i]);
                TestCase.assertNotNull(kpg);
                TestCase.assertTrue((kpg instanceof KeyPairGenerator));
            } catch (Exception e) {
                TestCase.fail("Exception should not be thrown");
            }
        }
    }

    /**
     * Additional class to verify KeyPairGenerator constructor
     */
    class MykeyPGen extends KeyPairGenerator {
        public MykeyPGen(String alg) {
            super(alg);
        }
    }
}

