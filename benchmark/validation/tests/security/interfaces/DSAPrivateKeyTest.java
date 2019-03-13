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


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAParameterSpec;
import junit.framework.TestCase;


public class DSAPrivateKeyTest extends TestCase {
    /**
     * java.security.interfaces.DSAPrivateKey
     * #getX()
     */
    public void test_getX() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(new DSAParameterSpec(Util.P, Util.Q, Util.G), new SecureRandom(new MySecureRandomSpi(), null) {});
        KeyPair keyPair = keyGen.generateKeyPair();
        DSAPrivateKey key = ((DSAPrivateKey) (keyPair.getPrivate()));
        TestCase.assertNotNull("Invalid X value", key.getX());
    }
}

