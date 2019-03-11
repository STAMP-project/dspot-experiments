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


import KeyPurposeId.anyExtendedKeyUsage;
import KeyPurposeId.id_kp_clientAuth;
import KeyPurposeId.id_kp_codeSigning;
import KeyPurposeId.id_kp_serverAuth;
import StandardNames.TRUST_MANAGER_FACTORY_DEFAULT;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Set;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import junit.framework.TestCase;
import libcore.java.security.TestKeyStore;


public class TrustManagerFactoryTest extends TestCase {
    private static final String[] KEY_TYPES = new String[]{ "RSA", "DSA", "EC", "EC_RSA" };

    private static TestKeyStore TEST_KEY_STORE;

    public void test_TrustManagerFactory_getDefaultAlgorithm() throws Exception {
        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        TestCase.assertEquals(TRUST_MANAGER_FACTORY_DEFAULT, algorithm);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
        test_TrustManagerFactory(tmf);
    }

    private static class UselessManagerFactoryParameters implements ManagerFactoryParameters {}

    public void test_TrustManagerFactory_getInstance() throws Exception {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                String type = service.getType();
                if (!(type.equals("TrustManagerFactory"))) {
                    continue;
                }
                String algorithm = service.getAlgorithm();
                {
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                    TestCase.assertEquals(algorithm, tmf.getAlgorithm());
                    test_TrustManagerFactory(tmf);
                }
                {
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm, provider);
                    TestCase.assertEquals(algorithm, tmf.getAlgorithm());
                    TestCase.assertEquals(provider, tmf.getProvider());
                    test_TrustManagerFactory(tmf);
                }
                {
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm, provider.getName());
                    TestCase.assertEquals(algorithm, tmf.getAlgorithm());
                    TestCase.assertEquals(provider, tmf.getProvider());
                    test_TrustManagerFactory(tmf);
                }
            }
        }
    }

    public void test_TrustManagerFactory_intermediate() throws Exception {
        // chain should be server/intermediate/root
        KeyStore.PrivateKeyEntry pke = TestKeyStore.getServer().getPrivateKey("RSA", "RSA");
        X509Certificate[] chain = ((X509Certificate[]) (pke.getCertificateChain()));
        TestCase.assertEquals(3, chain.length);
        // keyStore should contain only the intermediate CA so we can
        // test proper validation even if there are extra certs after
        // the trusted one (in this case the original root is "extra")
        KeyStore keyStore = TestKeyStore.createKeyStore();
        keyStore.setCertificateEntry("alias", chain[1]);
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                String type = service.getType();
                if (!(type.equals("TrustManagerFactory"))) {
                    continue;
                }
                String algorithm = service.getAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                tmf.init(keyStore);
                TrustManager[] trustManagers = tmf.getTrustManagers();
                for (TrustManager trustManager : trustManagers) {
                    if (!(trustManager instanceof X509TrustManager)) {
                        continue;
                    }
                    X509TrustManager tm = ((X509TrustManager) (trustManager));
                    tm.checkClientTrusted(chain, "RSA");
                    tm.checkServerTrusted(chain, "RSA");
                }
            }
        }
    }

    public void test_TrustManagerFactory_keyOnly() throws Exception {
        // create a KeyStore containing only a private key with chain.
        // unlike PKIXParameters(KeyStore), the cert chain of the key should be trusted.
        KeyStore ks = TestKeyStore.createKeyStore();
        KeyStore.PrivateKeyEntry pke = TrustManagerFactoryTest.getTestKeyStore().getPrivateKey("RSA", "RSA");
        ks.setKeyEntry("key", pke.getPrivateKey(), "pw".toCharArray(), pke.getCertificateChain());
        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
        tmf.init(ks);
        X509TrustManager trustManager = ((X509TrustManager) (tmf.getTrustManagers()[0]));
        trustManager.checkServerTrusted(((X509Certificate[]) (pke.getCertificateChain())), "RSA");
    }

    public void test_TrustManagerFactory_extendedKeyUsage() throws Exception {
        // anyExtendedKeyUsage should work for client or server
        test_TrustManagerFactory_extendedKeyUsage(anyExtendedKeyUsage, false, true, true);
        test_TrustManagerFactory_extendedKeyUsage(anyExtendedKeyUsage, true, true, true);
        // critical clientAuth should work for client
        test_TrustManagerFactory_extendedKeyUsage(id_kp_clientAuth, false, true, false);
        test_TrustManagerFactory_extendedKeyUsage(id_kp_clientAuth, true, true, false);
        // critical serverAuth should work for server
        test_TrustManagerFactory_extendedKeyUsage(id_kp_serverAuth, false, false, true);
        test_TrustManagerFactory_extendedKeyUsage(id_kp_serverAuth, true, false, true);
        // codeSigning should not work
        test_TrustManagerFactory_extendedKeyUsage(id_kp_codeSigning, false, false, false);
        test_TrustManagerFactory_extendedKeyUsage(id_kp_codeSigning, true, false, false);
    }
}

