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
package libcore.javax.crypto;


import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.crypto.KeyGenerator;
import junit.framework.TestCase;


public class KeyGeneratorTest extends TestCase {
    public void test_getInstance() throws Exception {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                String type = service.getType();
                if (!(type.equals("KeyGenerator"))) {
                    continue;
                }
                String algorithm = service.getAlgorithm();
                try {
                    // KeyGenerator.getInstance(String)
                    KeyGenerator kg1 = KeyGenerator.getInstance(algorithm);
                    TestCase.assertEquals(algorithm, kg1.getAlgorithm());
                    test_KeyGenerator(kg1);
                    // KeyGenerator.getInstance(String, Provider)
                    KeyGenerator kg2 = KeyGenerator.getInstance(algorithm, provider);
                    TestCase.assertEquals(algorithm, kg2.getAlgorithm());
                    TestCase.assertEquals(provider, kg2.getProvider());
                    test_KeyGenerator(kg2);
                    // KeyGenerator.getInstance(String, String)
                    KeyGenerator kg3 = KeyGenerator.getInstance(algorithm, provider.getName());
                    TestCase.assertEquals(algorithm, kg3.getAlgorithm());
                    TestCase.assertEquals(provider, kg3.getProvider());
                    test_KeyGenerator(kg3);
                } catch (Exception e) {
                    throw new Exception(("Problem testing KeyPairGenerator." + algorithm), e);
                }
            }
        }
    }

    private static final Map<String, List<Integer>> KEY_SIZES = new HashMap<String, List<Integer>>();

    static {
        KeyGeneratorTest.putKeySize("AES", 128);
        KeyGeneratorTest.putKeySize("AES", 192);
        KeyGeneratorTest.putKeySize("AES", 256);
        KeyGeneratorTest.putKeySize("ARC4", 1024);
        KeyGeneratorTest.putKeySize("ARC4", 40);
        KeyGeneratorTest.putKeySize("ARC4", 41);
        KeyGeneratorTest.putKeySize("ARCFOUR", 1024);
        KeyGeneratorTest.putKeySize("ARCFOUR", 40);
        KeyGeneratorTest.putKeySize("ARCFOUR", 41);
        KeyGeneratorTest.putKeySize("Blowfish", 32);
        KeyGeneratorTest.putKeySize("Blowfish", (32 + 8));
        KeyGeneratorTest.putKeySize("Blowfish", 448);
        KeyGeneratorTest.putKeySize("DES", 56);
        KeyGeneratorTest.putKeySize("DESede", 112);
        KeyGeneratorTest.putKeySize("DESede", 168);
        KeyGeneratorTest.putKeySize("RC2", 40);
        KeyGeneratorTest.putKeySize("RC2", 41);
        KeyGeneratorTest.putKeySize("RC2", 1024);
        KeyGeneratorTest.putKeySize("RC4", 40);
        KeyGeneratorTest.putKeySize("RC4", 41);
        KeyGeneratorTest.putKeySize("RC4", 1024);
        KeyGeneratorTest.putKeySize("HmacMD5", 1);
        KeyGeneratorTest.putKeySize("HmacMD5", 1025);
        KeyGeneratorTest.putKeySize("HmacSHA1", 1);
        KeyGeneratorTest.putKeySize("HmacSHA1", 1025);
        KeyGeneratorTest.putKeySize("HmacSHA256", 40);
        KeyGeneratorTest.putKeySize("HmacSHA256", 1025);
        KeyGeneratorTest.putKeySize("HmacSHA384", 40);
        KeyGeneratorTest.putKeySize("HmacSHA384", 1025);
        KeyGeneratorTest.putKeySize("HmacSHA512", 40);
        KeyGeneratorTest.putKeySize("HmacSHA512", 1025);
    }
}

