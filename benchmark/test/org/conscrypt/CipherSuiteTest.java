/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.conscrypt;


import StandardNames.CIPHER_SUITES_SSLENGINE;
import StandardNames.CIPHER_SUITE_SECURE_RENEGOTIATION;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import libcore.java.security.StandardNames;


public class CipherSuiteTest extends TestCase {
    public void test_getByName() throws Exception {
        for (String name : StandardNames.CIPHER_SUITES) {
            if (name.equals(CIPHER_SUITE_SECURE_RENEGOTIATION)) {
                TestCase.assertNull(CipherSuite.getByName(name));
            } else {
                test_CipherSuite(name);
            }
        }
        TestCase.assertNull(CipherSuite.getByName("bogus"));
        try {
            CipherSuite.getByName(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void test_getByCode() {
        // CipherSuite.getByCode is also covered by test_CipherSuite
        assertUnknown(CipherSuite.getByCode(((byte) (18)), ((byte) (52))));
        assertUnknown(CipherSuite.getByCode(((byte) (18)), ((byte) (52)), ((byte) (86))));
        assertUnknown(CipherSuite.getByCode(((byte) (-1)), ((byte) (-1))));
        assertUnknown(CipherSuite.getByCode(((byte) (-1)), ((byte) (-1)), ((byte) (-1))));
    }

    public void test_getSupported() throws Exception {
        CipherSuite[] suites = CipherSuite.getSupported();
        List<String> names = new ArrayList<String>(suites.length);
        for (CipherSuite cs : suites) {
            test_CipherSuite(cs);
            names.add(cs.getName());
        }
        TestCase.assertEquals(Arrays.asList(CipherSuite.getSupportedCipherSuiteNames()), names);
    }

    public void test_getSupportedCipherSuiteNames() throws Exception {
        String[] names = CipherSuite.getSupportedCipherSuiteNames();
        StandardNames.assertSupportedCipherSuites(CIPHER_SUITES_SSLENGINE, names);
        for (String name : names) {
            test_CipherSuite(name);
        }
    }

    public void test_getClientKeyType() throws Exception {
        byte b = Byte.MIN_VALUE;
        do {
            String byteString = Byte.toString(b);
            String keyType = CipherSuite.getClientKeyType(b);
            switch (b) {
                case 1 :
                    TestCase.assertEquals(byteString, "RSA", keyType);
                    break;
                case 2 :
                    TestCase.assertEquals(byteString, "DSA", keyType);
                    break;
                case 3 :
                    TestCase.assertEquals(byteString, "DH_RSA", keyType);
                    break;
                case 4 :
                    TestCase.assertEquals(byteString, "DH_DSA", keyType);
                    break;
                case 64 :
                    TestCase.assertEquals(byteString, "EC", keyType);
                    break;
                case 65 :
                    TestCase.assertEquals(byteString, "EC_RSA", keyType);
                    break;
                case 66 :
                    TestCase.assertEquals(byteString, "EC_EC", keyType);
                    break;
                default :
                    TestCase.assertNull(byteString, keyType);
            }
            b++;
        } while (b != (Byte.MIN_VALUE) );
    }
}

