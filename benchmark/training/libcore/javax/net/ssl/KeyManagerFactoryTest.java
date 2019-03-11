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
package libcore.javax.net.ssl;


import StandardNames.KEY_MANAGER_FACTORY_DEFAULT;
import StandardNames.KEY_TYPES;
import java.security.Provider;
import java.security.Security;
import java.util.Set;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.ManagerFactoryParameters;
import junit.framework.TestCase;
import libcore.java.security.TestKeyStore;


public class KeyManagerFactoryTest extends TestCase {
    private static TestKeyStore TEST_KEY_STORE;

    public void test_KeyManagerFactory_getDefaultAlgorithm() throws Exception {
        String algorithm = KeyManagerFactory.getDefaultAlgorithm();
        TestCase.assertEquals(KEY_MANAGER_FACTORY_DEFAULT, algorithm);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
        test_KeyManagerFactory(kmf);
    }

    private static class UselessManagerFactoryParameters implements ManagerFactoryParameters {}

    private static final String[] KEY_TYPES_ONLY = KEY_TYPES.toArray(new String[KEY_TYPES.size()]);

    private static final String[] KEY_TYPES_WITH_EMPTY = new String[(KeyManagerFactoryTest.KEY_TYPES_ONLY.length) + 1];

    private static final String[] KEY_TYPES_WITH_EMPTY_AND_NULL = new String[(KeyManagerFactoryTest.KEY_TYPES_ONLY.length) + 2];

    static {
        System.arraycopy(KeyManagerFactoryTest.KEY_TYPES_ONLY, 0, KeyManagerFactoryTest.KEY_TYPES_WITH_EMPTY, 0, KeyManagerFactoryTest.KEY_TYPES_ONLY.length);
        KeyManagerFactoryTest.KEY_TYPES_WITH_EMPTY[((KeyManagerFactoryTest.KEY_TYPES_WITH_EMPTY.length) - 1)] = "";
        System.arraycopy(KeyManagerFactoryTest.KEY_TYPES_WITH_EMPTY, 0, KeyManagerFactoryTest.KEY_TYPES_WITH_EMPTY_AND_NULL, 0, KeyManagerFactoryTest.KEY_TYPES_WITH_EMPTY.length);
        // extra null at end requires no initialization
    }

    public void test_KeyManagerFactory_getInstance() throws Exception {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                String type = service.getType();
                if (!(type.equals("KeyManagerFactory"))) {
                    continue;
                }
                String algorithm = service.getAlgorithm();
                try {
                    {
                        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
                        TestCase.assertEquals(algorithm, kmf.getAlgorithm());
                        test_KeyManagerFactory(kmf);
                    }
                    {
                        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm, provider);
                        TestCase.assertEquals(algorithm, kmf.getAlgorithm());
                        TestCase.assertEquals(provider, kmf.getProvider());
                        test_KeyManagerFactory(kmf);
                    }
                    {
                        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm, provider.getName());
                        TestCase.assertEquals(algorithm, kmf.getAlgorithm());
                        TestCase.assertEquals(provider, kmf.getProvider());
                        test_KeyManagerFactory(kmf);
                    }
                } catch (Exception e) {
                    throw new Exception(("Problem with algorithm " + algorithm), e);
                }
            }
        }
    }
}

