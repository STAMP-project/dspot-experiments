/**
 * Copyright (C) 2011 The Android Open Source Project
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


import java.io.File;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.X509TrustManager;
import junit.framework.TestCase;
import libcore.java.security.TestKeyStore;


public class TrustManagerImplTest extends TestCase {
    private List<File> tmpFiles = new ArrayList<File>();

    /**
     * Ensure that our non-standard behavior of learning to trust new
     * intermediate CAs does not regress. http://b/3404902
     */
    public void testLearnIntermediate() throws Exception {
        // chain3 should be server/intermediate/root
        KeyStore.PrivateKeyEntry pke = TestKeyStore.getServer().getPrivateKey("RSA", "RSA");
        X509Certificate[] chain3 = ((X509Certificate[]) (pke.getCertificateChain()));
        X509Certificate root = chain3[2];
        X509Certificate intermediate = chain3[1];
        X509Certificate server = chain3[0];
        X509Certificate[] chain2 = new X509Certificate[]{ server, intermediate };
        X509Certificate[] chain1 = new X509Certificate[]{ server };
        // Normal behavior
        assertValid(chain3, trustManager(root));
        assertValid(chain2, trustManager(root));
        assertInvalid(chain1, trustManager(root));
        assertValid(chain3, trustManager(intermediate));
        assertValid(chain2, trustManager(intermediate));
        assertValid(chain1, trustManager(intermediate));
        assertValid(chain3, trustManager(server));
        assertValid(chain2, trustManager(server));
        assertValid(chain1, trustManager(server));
        // non-standard behavior
        X509TrustManager tm = trustManager(root);
        // fail on short chain with only root trusted
        assertInvalid(chain1, tm);
        // succeed on longer chain, learn intermediate
        assertValid(chain2, tm);
        // now we can validate the short chain
        assertValid(chain1, tm);
    }

    // We should ignore duplicate cruft in the certificate chain
    // See https://code.google.com/p/android/issues/detail?id=52295 http://b/8313312
    public void testDuplicateInChain() throws Exception {
        // chain3 should be server/intermediate/root
        KeyStore.PrivateKeyEntry pke = TestKeyStore.getServer().getPrivateKey("RSA", "RSA");
        X509Certificate[] chain3 = ((X509Certificate[]) (pke.getCertificateChain()));
        X509Certificate root = chain3[2];
        X509Certificate intermediate = chain3[1];
        X509Certificate server = chain3[0];
        X509Certificate[] chain4 = new X509Certificate[]{ server, intermediate, server, intermediate };
        assertValid(chain4, trustManager(root));
    }

    public void testGetFullChain() throws Exception {
        // build the trust manager
        KeyStore.PrivateKeyEntry pke = TestKeyStore.getServer().getPrivateKey("RSA", "RSA");
        X509Certificate[] chain3 = ((X509Certificate[]) (pke.getCertificateChain()));
        X509Certificate root = chain3[2];
        X509TrustManager tm = trustManager(root);
        // build the chains we'll use for testing
        X509Certificate intermediate = chain3[1];
        X509Certificate server = chain3[0];
        X509Certificate[] chain2 = new X509Certificate[]{ server, intermediate };
        X509Certificate[] chain1 = new X509Certificate[]{ server };
        TestCase.assertTrue((tm instanceof TrustManagerImpl));
        TrustManagerImpl tmi = ((TrustManagerImpl) (tm));
        List<X509Certificate> certs = tmi.checkServerTrusted(chain2, "RSA", "purple.com");
        TestCase.assertEquals(Arrays.asList(chain3), certs);
        certs = tmi.checkServerTrusted(chain1, "RSA", "purple.com");
        TestCase.assertEquals(Arrays.asList(chain3), certs);
    }

    public void testCertPinning() throws Exception {
        // chain3 should be server/intermediate/root
        KeyStore.PrivateKeyEntry pke = TestKeyStore.getServer().getPrivateKey("RSA", "RSA");
        X509Certificate[] chain3 = ((X509Certificate[]) (pke.getCertificateChain()));
        X509Certificate root = chain3[2];
        X509Certificate intermediate = chain3[1];
        X509Certificate server = chain3[0];
        X509Certificate[] chain2 = new X509Certificate[]{ server, intermediate };
        X509Certificate[] chain1 = new X509Certificate[]{ server };
        // test without a hostname, expecting failure
        assertInvalidPinned(chain1, trustManager(root, "gugle.com", root), null);
        // test without a hostname, expecting success
        assertValidPinned(chain3, trustManager(root, "gugle.com", root), null, chain3);
        // test an unpinned hostname that should fail
        assertInvalidPinned(chain1, trustManager(root, "gugle.com", root), "purple.com");
        // test an unpinned hostname that should succeed
        assertValidPinned(chain3, trustManager(root, "gugle.com", root), "purple.com", chain3);
        // test a pinned hostname that should fail
        assertInvalidPinned(chain1, trustManager(intermediate, "gugle.com", root), "gugle.com");
        // test a pinned hostname that should succeed
        assertValidPinned(chain2, trustManager(intermediate, "gugle.com", server), "gugle.com", chain2);
    }
}

