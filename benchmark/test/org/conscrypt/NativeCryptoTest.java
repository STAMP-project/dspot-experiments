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
/**
 * RoboVM note: Not available in RoboVM.
 */
/**
 * import dalvik.system.BaseDexClassLoader;
 */
package org.conscrypt;


import NativeCrypto.EC_CURVE_GF2M;
import NativeCrypto.EC_CURVE_GFP;
import NativeCrypto.OPENSSL_TO_STANDARD_CIPHER_SUITES;
import NativeCrypto.SSL_OP_NO_SESSION_RESUMPTION_ON_RENEGOTIATION;
import NativeCrypto.SSL_OP_NO_SSLv3;
import NativeCrypto.SSL_VERIFY_FAIL_IF_NO_PEER_CERT;
import NativeCrypto.SSL_VERIFY_NONE;
import NativeCrypto.SSL_VERIFY_PEER;
import StandardNames.SSL_SOCKET_PROTOCOLS;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.CertificateException;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLProtocolException;
import javax.security.auth.x500.X500Principal;
import junit.framework.TestCase;
import libcore.io.IoUtils;
import org.conscrypt.NativeCrypto.SSLHandshakeCallbacks;

import static NativeCrypto.SSL_OP_NO_SSLv3;
import static NativeCrypto.SSL_OP_NO_TLSv1;
import static NativeCrypto.SSL_OP_NO_TLSv1_1;
import static NativeCrypto.SSL_OP_NO_TLSv1_2;
import static NativeCrypto.SSL_VERIFY_FAIL_IF_NO_PEER_CERT;
import static NativeCrypto.SSL_VERIFY_PEER;


public class NativeCryptoTest extends TestCase {
    /**
     * Corresponds to the native test library "libjavacoretests.so"
     */
    public static final String TEST_ENGINE_ID = "javacoretests";

    private static final long NULL = 0;

    private static final FileDescriptor INVALID_FD = new FileDescriptor();

    private static final SSLHandshakeCallbacks DUMMY_CB = new NativeCryptoTest.TestSSLHandshakeCallbacks(null, 0, null);

    private static final long TIMEOUT_SECONDS = 5;

    private static OpenSSLKey SERVER_PRIVATE_KEY;

    private static byte[][] SERVER_CERTIFICATES;

    private static OpenSSLKey CLIENT_PRIVATE_KEY;

    private static byte[][] CLIENT_CERTIFICATES;

    private static byte[][] CA_PRINCIPALS;

    private static OpenSSLKey CHANNEL_ID_PRIVATE_KEY;

    private static byte[] CHANNEL_ID;

    public void test_EVP_PKEY_cmp() throws Exception {
        try {
            NativeCrypto.EVP_PKEY_cmp(NativeCryptoTest.NULL, NativeCryptoTest.NULL);
            TestCase.fail("Should throw NullPointerException when arguments are NULL");
        } catch (NullPointerException expected) {
        }
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(512);
        KeyPair kp1 = kpg.generateKeyPair();
        RSAPrivateCrtKey privKey1 = ((RSAPrivateCrtKey) (kp1.getPrivate()));
        KeyPair kp2 = kpg.generateKeyPair();
        RSAPrivateCrtKey privKey2 = ((RSAPrivateCrtKey) (kp2.getPrivate()));
        long pkey1 = 0;
        long pkey1_copy = 0;
        long pkey2 = 0;
        try {
            pkey1 = NativeCrypto.EVP_PKEY_new_RSA(privKey1.getModulus().toByteArray(), privKey1.getPublicExponent().toByteArray(), privKey1.getPrivateExponent().toByteArray(), privKey1.getPrimeP().toByteArray(), privKey1.getPrimeQ().toByteArray(), privKey1.getPrimeExponentP().toByteArray(), privKey1.getPrimeExponentQ().toByteArray(), privKey1.getCrtCoefficient().toByteArray());
            TestCase.assertNotSame(NativeCryptoTest.NULL, pkey1);
            pkey1_copy = NativeCrypto.EVP_PKEY_new_RSA(privKey1.getModulus().toByteArray(), privKey1.getPublicExponent().toByteArray(), privKey1.getPrivateExponent().toByteArray(), privKey1.getPrimeP().toByteArray(), privKey1.getPrimeQ().toByteArray(), privKey1.getPrimeExponentP().toByteArray(), privKey1.getPrimeExponentQ().toByteArray(), privKey1.getCrtCoefficient().toByteArray());
            TestCase.assertNotSame(NativeCryptoTest.NULL, pkey1_copy);
            pkey2 = NativeCrypto.EVP_PKEY_new_RSA(privKey2.getModulus().toByteArray(), privKey2.getPublicExponent().toByteArray(), privKey2.getPrivateExponent().toByteArray(), privKey2.getPrimeP().toByteArray(), privKey2.getPrimeQ().toByteArray(), privKey2.getPrimeExponentP().toByteArray(), privKey2.getPrimeExponentQ().toByteArray(), privKey2.getCrtCoefficient().toByteArray());
            TestCase.assertNotSame(NativeCryptoTest.NULL, pkey2);
            try {
                NativeCrypto.EVP_PKEY_cmp(pkey1, NativeCryptoTest.NULL);
                TestCase.fail("Should throw NullPointerException when arguments are NULL");
            } catch (NullPointerException expected) {
            }
            try {
                NativeCrypto.EVP_PKEY_cmp(NativeCryptoTest.NULL, pkey1);
                TestCase.fail("Should throw NullPointerException when arguments are NULL");
            } catch (NullPointerException expected) {
            }
            TestCase.assertEquals("Same keys should be the equal", 1, NativeCrypto.EVP_PKEY_cmp(pkey1, pkey1));
            TestCase.assertEquals("Same keys should be the equal", 1, NativeCrypto.EVP_PKEY_cmp(pkey1, pkey1_copy));
            TestCase.assertEquals("Different keys should not be equal", 0, NativeCrypto.EVP_PKEY_cmp(pkey1, pkey2));
        } finally {
            if (pkey1 != 0) {
                NativeCrypto.EVP_PKEY_free(pkey1);
            }
            if (pkey1_copy != 0) {
                NativeCrypto.EVP_PKEY_free(pkey1_copy);
            }
            if (pkey2 != 0) {
                NativeCrypto.EVP_PKEY_free(pkey2);
            }
        }
    }

    public void test_SSL_CTX_new() throws Exception {
        long c = NativeCrypto.SSL_CTX_new();
        TestCase.assertTrue((c != (NativeCryptoTest.NULL)));
        long c2 = NativeCrypto.SSL_CTX_new();
        TestCase.assertTrue((c != c2));
        NativeCrypto.SSL_CTX_free(c);
        NativeCrypto.SSL_CTX_free(c2);
    }

    public void test_SSL_CTX_free() throws Exception {
        try {
            NativeCrypto.SSL_CTX_free(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        NativeCrypto.SSL_CTX_free(NativeCrypto.SSL_CTX_new());
    }

    public void test_SSL_CTX_set_session_id_context() throws Exception {
        byte[] empty = new byte[0];
        try {
            NativeCrypto.SSL_CTX_set_session_id_context(NativeCryptoTest.NULL, empty);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        try {
            NativeCrypto.SSL_CTX_set_session_id_context(c, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        NativeCrypto.SSL_CTX_set_session_id_context(c, empty);
        NativeCrypto.SSL_CTX_set_session_id_context(c, new byte[32]);
        try {
            NativeCrypto.SSL_CTX_set_session_id_context(c, new byte[33]);
        } catch (IllegalArgumentException expected) {
        }
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_new() throws Exception {
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        TestCase.assertTrue((s != (NativeCryptoTest.NULL)));
        TestCase.assertTrue((((NativeCrypto.SSL_get_options(s)) & 16777216L) != 0));// SSL_OP_NO_SSLv2

        TestCase.assertTrue((((NativeCrypto.SSL_get_options(s)) & (SSL_OP_NO_SSLv3)) == 0));
        TestCase.assertTrue((((NativeCrypto.SSL_get_options(s)) & (SSL_OP_NO_TLSv1)) == 0));
        TestCase.assertTrue((((NativeCrypto.SSL_get_options(s)) & (SSL_OP_NO_TLSv1_1)) == 0));
        TestCase.assertTrue((((NativeCrypto.SSL_get_options(s)) & (SSL_OP_NO_TLSv1_2)) == 0));
        long s2 = NativeCrypto.SSL_new(c);
        TestCase.assertTrue((s != s2));
        NativeCrypto.SSL_free(s2);
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_use_certificate() throws Exception {
        try {
            NativeCrypto.SSL_use_certificate(NativeCryptoTest.NULL, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        try {
            NativeCrypto.SSL_use_certificate(s, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        NativeCrypto.SSL_use_certificate(s, NativeCryptoTest.getServerCertificates());
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_use_PrivateKey_for_tls_channel_id() throws Exception {
        NativeCryptoTest.initChannelIdKey();
        try {
            NativeCrypto.SSL_set1_tls_channel_id(NativeCryptoTest.NULL, NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        try {
            NativeCrypto.SSL_set1_tls_channel_id(s, NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // Use the key natively. This works because the initChannelIdKey method ensures that the
        // key is backed by OpenSSL.
        NativeCrypto.SSL_set1_tls_channel_id(s, NativeCryptoTest.CHANNEL_ID_PRIVATE_KEY.getPkeyContext());
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_use_PrivateKey() throws Exception {
        try {
            NativeCrypto.SSL_use_PrivateKey(NativeCryptoTest.NULL, NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        try {
            NativeCrypto.SSL_use_PrivateKey(s, NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        NativeCrypto.SSL_use_PrivateKey(s, NativeCryptoTest.getServerPrivateKey().getPkeyContext());
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_check_private_key_null() throws Exception {
        try {
            NativeCrypto.SSL_check_private_key(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void test_SSL_check_private_key_no_key_no_cert() throws Exception {
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        // neither private or certificate set
        try {
            NativeCrypto.SSL_check_private_key(s);
            TestCase.fail();
        } catch (SSLException expected) {
        }
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_check_private_key_cert_then_key() throws Exception {
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        // first certificate, then private
        NativeCrypto.SSL_use_certificate(s, NativeCryptoTest.getServerCertificates());
        try {
            NativeCrypto.SSL_check_private_key(s);
            TestCase.fail();
        } catch (SSLException expected) {
        }
        NativeCrypto.SSL_use_PrivateKey(s, NativeCryptoTest.getServerPrivateKey().getPkeyContext());
        NativeCrypto.SSL_check_private_key(s);
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_check_private_key_key_then_cert() throws Exception {
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        // first private, then certificate
        NativeCrypto.SSL_use_PrivateKey(s, NativeCryptoTest.getServerPrivateKey().getPkeyContext());
        try {
            NativeCrypto.SSL_check_private_key(s);
            TestCase.fail();
        } catch (SSLException expected) {
        }
        NativeCrypto.SSL_use_certificate(s, NativeCryptoTest.getServerCertificates());
        NativeCrypto.SSL_check_private_key(s);
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_get_mode() throws Exception {
        try {
            NativeCrypto.SSL_get_mode(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        TestCase.assertTrue(((NativeCrypto.SSL_get_mode(s)) != 0));
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_set_mode_and_clear_mode() throws Exception {
        try {
            NativeCrypto.SSL_set_mode(NativeCryptoTest.NULL, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        // check SSL_MODE_HANDSHAKE_CUTTHROUGH off by default
        TestCase.assertEquals(0, ((NativeCrypto.SSL_get_mode(s)) & (NativeCrypto.SSL_MODE_HANDSHAKE_CUTTHROUGH)));
        // set SSL_MODE_HANDSHAKE_CUTTHROUGH on
        NativeCrypto.SSL_set_mode(s, NativeCrypto.SSL_MODE_HANDSHAKE_CUTTHROUGH);
        TestCase.assertTrue((((NativeCrypto.SSL_get_mode(s)) & (NativeCrypto.SSL_MODE_HANDSHAKE_CUTTHROUGH)) != 0));
        // clear SSL_MODE_HANDSHAKE_CUTTHROUGH off
        NativeCrypto.SSL_clear_mode(s, NativeCrypto.SSL_MODE_HANDSHAKE_CUTTHROUGH);
        TestCase.assertTrue((((NativeCrypto.SSL_get_mode(s)) & (NativeCrypto.SSL_MODE_HANDSHAKE_CUTTHROUGH)) == 0));
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_get_options() throws Exception {
        try {
            NativeCrypto.SSL_get_options(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        TestCase.assertTrue(((NativeCrypto.SSL_get_options(s)) != 0));
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_set_options() throws Exception {
        try {
            NativeCrypto.SSL_set_options(NativeCryptoTest.NULL, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        TestCase.assertTrue((((NativeCrypto.SSL_get_options(s)) & (SSL_OP_NO_SSLv3)) == 0));
        NativeCrypto.SSL_set_options(s, SSL_OP_NO_SSLv3);
        TestCase.assertTrue((((NativeCrypto.SSL_get_options(s)) & (SSL_OP_NO_SSLv3)) != 0));
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_clear_options() throws Exception {
        try {
            NativeCrypto.SSL_clear_options(NativeCryptoTest.NULL, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        TestCase.assertTrue((((NativeCrypto.SSL_get_options(s)) & (SSL_OP_NO_SSLv3)) == 0));
        NativeCrypto.SSL_set_options(s, SSL_OP_NO_SSLv3);
        TestCase.assertTrue((((NativeCrypto.SSL_get_options(s)) & (SSL_OP_NO_SSLv3)) != 0));
        NativeCrypto.SSL_clear_options(s, SSL_OP_NO_SSLv3);
        TestCase.assertTrue((((NativeCrypto.SSL_get_options(s)) & (SSL_OP_NO_SSLv3)) == 0));
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_set_cipher_lists() throws Exception {
        try {
            NativeCrypto.SSL_set_cipher_lists(NativeCryptoTest.NULL, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        try {
            NativeCrypto.SSL_set_cipher_lists(s, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        NativeCrypto.SSL_set_cipher_lists(s, new String[]{  });
        try {
            NativeCrypto.SSL_set_cipher_lists(s, new String[]{ null });
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // see OpenSSL ciphers man page
        String[] illegals = new String[]{ // empty
        "", // never standardized
        "EXP1024-DES-CBC-SHA", "EXP1024-RC4-SHA", "DHE-DSS-RC4-SHA", // IDEA
        "IDEA-CBC-SHA", "IDEA-CBC-MD5" };
        for (String illegal : illegals) {
            try {
                NativeCrypto.SSL_set_cipher_lists(s, new String[]{ illegal });
                TestCase.fail(illegal);
            } catch (IllegalArgumentException expected) {
            }
        }
        List<String> ciphers = new ArrayList<String>(OPENSSL_TO_STANDARD_CIPHER_SUITES.keySet());
        NativeCrypto.SSL_set_cipher_lists(s, ciphers.toArray(new String[ciphers.size()]));
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_set_verify() throws Exception {
        try {
            NativeCrypto.SSL_set_verify(NativeCryptoTest.NULL, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        NativeCrypto.SSL_set_verify(s, SSL_VERIFY_NONE);
        NativeCrypto.SSL_set_verify(s, SSL_VERIFY_PEER);
        NativeCrypto.SSL_set_verify(s, SSL_VERIFY_FAIL_IF_NO_PEER_CERT);
        NativeCrypto.SSL_set_verify(s, ((SSL_VERIFY_PEER) | (SSL_VERIFY_FAIL_IF_NO_PEER_CERT)));
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    private static final boolean DEBUG = false;

    public static class Hooks {
        private OpenSSLKey channelIdPrivateKey;

        public long getContext() throws SSLException {
            return NativeCrypto.SSL_CTX_new();
        }

        public long beforeHandshake(long context) throws SSLException {
            long s = NativeCrypto.SSL_new(context);
            // without this SSL_set_cipher_lists call the tests were
            // negotiating DHE-RSA-AES256-SHA by default which had
            // very slow ephemeral RSA key generation
            NativeCrypto.SSL_set_cipher_lists(s, new String[]{ "RC4-MD5" });
            if ((channelIdPrivateKey) != null) {
                NativeCrypto.SSL_set1_tls_channel_id(s, channelIdPrivateKey.getPkeyContext());
            }
            return s;
        }

        public void clientCertificateRequested(long s) {
        }

        public void afterHandshake(long session, long ssl, long context, Socket socket, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
            if (session != (NativeCryptoTest.NULL)) {
                NativeCrypto.SSL_SESSION_free(session);
            }
            if (ssl != (NativeCryptoTest.NULL)) {
                try {
                    NativeCrypto.SSL_shutdown(ssl, fd, callback);
                } catch (IOException e) {
                }
                NativeCrypto.SSL_free(ssl);
            }
            if (context != (NativeCryptoTest.NULL)) {
                NativeCrypto.SSL_CTX_free(context);
            }
            if (socket != null) {
                socket.close();
            }
        }
    }

    public static class TestSSLHandshakeCallbacks implements SSLHandshakeCallbacks {
        private final Socket socket;

        private final long sslNativePointer;

        private final NativeCryptoTest.Hooks hooks;

        public TestSSLHandshakeCallbacks(Socket socket, long sslNativePointer, NativeCryptoTest.Hooks hooks) {
            this.socket = socket;
            this.sslNativePointer = sslNativePointer;
            this.hooks = hooks;
        }

        public byte[][] asn1DerEncodedCertificateChain;

        public String authMethod;

        public boolean verifyCertificateChainCalled;

        public void verifyCertificateChain(byte[][] asn1DerEncodedCertificateChain, String authMethod) throws CertificateException {
            if (NativeCryptoTest.DEBUG) {
                System.out.println((((((("ssl=0x" + (Long.toString(sslNativePointer, 16))) + " verifyCertificateChain") + " asn1DerEncodedCertificateChain=") + asn1DerEncodedCertificateChain) + " authMethod=") + authMethod));
            }
            this.asn1DerEncodedCertificateChain = asn1DerEncodedCertificateChain;
            this.authMethod = authMethod;
            this.verifyCertificateChainCalled = true;
        }

        public byte[] keyTypes;

        public byte[][] asn1DerEncodedX500Principals;

        public boolean clientCertificateRequestedCalled;

        public void clientCertificateRequested(byte[] keyTypes, byte[][] asn1DerEncodedX500Principals) {
            if (NativeCryptoTest.DEBUG) {
                System.out.println((((((("ssl=0x" + (Long.toString(sslNativePointer, 16))) + " clientCertificateRequested") + " keyTypes=") + keyTypes) + " asn1DerEncodedX500Principals=") + asn1DerEncodedX500Principals));
            }
            this.keyTypes = keyTypes;
            this.asn1DerEncodedX500Principals = asn1DerEncodedX500Principals;
            this.clientCertificateRequestedCalled = true;
            if ((hooks) != null) {
                hooks.clientCertificateRequested(sslNativePointer);
            }
        }

        public boolean handshakeCompletedCalled;

        public void handshakeCompleted() {
            if (NativeCryptoTest.DEBUG) {
                System.out.println((("ssl=0x" + (Long.toString(sslNativePointer, 16))) + " handshakeCompleted"));
            }
            this.handshakeCompletedCalled = true;
        }

        public Socket getSocket() {
            return socket;
        }
    }

    public static class ServerHooks extends NativeCryptoTest.Hooks {
        private final OpenSSLKey privateKey;

        private final byte[][] certificates;

        private boolean channelIdEnabled;

        private byte[] channelIdAfterHandshake;

        private Throwable channelIdAfterHandshakeException;

        public ServerHooks(OpenSSLKey privateKey, byte[][] certificates) {
            this.privateKey = privateKey;
            this.certificates = certificates;
        }

        @Override
        public long beforeHandshake(long c) throws SSLException {
            long s = super.beforeHandshake(c);
            if ((privateKey) != null) {
                NativeCrypto.SSL_use_PrivateKey(s, privateKey.getPkeyContext());
            }
            if ((certificates) != null) {
                NativeCrypto.SSL_use_certificate(s, certificates);
            }
            if (channelIdEnabled) {
                NativeCrypto.SSL_enable_tls_channel_id(s);
            }
            return s;
        }

        @Override
        public void afterHandshake(long session, long ssl, long context, Socket socket, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
            if (channelIdEnabled) {
                try {
                    channelIdAfterHandshake = NativeCrypto.SSL_get_tls_channel_id(ssl);
                } catch (Exception e) {
                    channelIdAfterHandshakeException = e;
                }
            }
            super.afterHandshake(session, ssl, context, socket, fd, callback);
        }

        public void clientCertificateRequested(long s) {
            TestCase.fail("Server asked for client certificates");
        }
    }

    public void test_SSL_do_handshake_NULL_SSL() throws Exception {
        try {
            NativeCrypto.SSL_do_handshake(NativeCryptoTest.NULL, null, null, 0, false, null, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void test_SSL_do_handshake_null_args() throws Exception {
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        try {
            NativeCrypto.SSL_do_handshake(s, null, null, 0, true, null, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            NativeCrypto.SSL_do_handshake(s, NativeCryptoTest.INVALID_FD, null, 0, true, null, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
    }

    public void test_SSL_do_handshake_normal() throws Exception {
        // normal client and server case
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks();
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        NativeCryptoTest.TestSSLHandshakeCallbacks clientCallback = client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        NativeCryptoTest.TestSSLHandshakeCallbacks serverCallback = server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        TestCase.assertTrue(clientCallback.verifyCertificateChainCalled);
        NativeCryptoTest.assertEqualCertificateChains(NativeCryptoTest.getServerCertificates(), clientCallback.asn1DerEncodedCertificateChain);
        TestCase.assertEquals("RSA", clientCallback.authMethod);
        TestCase.assertFalse(serverCallback.verifyCertificateChainCalled);
        TestCase.assertFalse(clientCallback.clientCertificateRequestedCalled);
        TestCase.assertFalse(serverCallback.clientCertificateRequestedCalled);
        TestCase.assertTrue(clientCallback.handshakeCompletedCalled);
        TestCase.assertTrue(serverCallback.handshakeCompletedCalled);
    }

    public void test_SSL_do_handshake_optional_client_certificate() throws Exception {
        // optional client certificate case
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public void clientCertificateRequested(long s) {
                super.clientCertificateRequested(s);
                NativeCrypto.SSL_use_PrivateKey(s, NativeCryptoTest.getClientPrivateKey().getPkeyContext());
                NativeCrypto.SSL_use_certificate(s, NativeCryptoTest.getClientCertificates());
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
            @Override
            public long beforeHandshake(long c) throws SSLException {
                long s = super.beforeHandshake(c);
                NativeCrypto.SSL_set_client_CA_list(s, NativeCryptoTest.getCaPrincipals());
                NativeCrypto.SSL_set_verify(s, SSL_VERIFY_PEER);
                return s;
            }
        };
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        NativeCryptoTest.TestSSLHandshakeCallbacks clientCallback = client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        NativeCryptoTest.TestSSLHandshakeCallbacks serverCallback = server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        TestCase.assertTrue(clientCallback.verifyCertificateChainCalled);
        NativeCryptoTest.assertEqualCertificateChains(NativeCryptoTest.getServerCertificates(), clientCallback.asn1DerEncodedCertificateChain);
        TestCase.assertEquals("RSA", clientCallback.authMethod);
        TestCase.assertTrue(serverCallback.verifyCertificateChainCalled);
        NativeCryptoTest.assertEqualCertificateChains(NativeCryptoTest.getClientCertificates(), serverCallback.asn1DerEncodedCertificateChain);
        TestCase.assertEquals("RSA", serverCallback.authMethod);
        TestCase.assertTrue(clientCallback.clientCertificateRequestedCalled);
        TestCase.assertNotNull(clientCallback.keyTypes);
        // this depends on the SSL_set_cipher_lists call in beforeHandshake
        // the three returned are the non-ephemeral cases.
        TestCase.assertEquals(3, clientCallback.keyTypes.length);
        TestCase.assertEquals("RSA", CipherSuite.getClientKeyType(clientCallback.keyTypes[0]));
        TestCase.assertEquals("DSA", CipherSuite.getClientKeyType(clientCallback.keyTypes[1]));
        TestCase.assertEquals("EC", CipherSuite.getClientKeyType(clientCallback.keyTypes[2]));
        NativeCryptoTest.assertEqualPrincipals(NativeCryptoTest.getCaPrincipals(), clientCallback.asn1DerEncodedX500Principals);
        TestCase.assertFalse(serverCallback.clientCertificateRequestedCalled);
        TestCase.assertTrue(clientCallback.handshakeCompletedCalled);
        TestCase.assertTrue(serverCallback.handshakeCompletedCalled);
    }

    public void test_SSL_do_handshake_missing_required_certificate() throws Exception {
        // required client certificate negative case
        final ServerSocket listener = new ServerSocket(0);
        try {
            NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks();
            NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
                @Override
                public long beforeHandshake(long c) throws SSLException {
                    long s = super.beforeHandshake(c);
                    NativeCrypto.SSL_set_client_CA_list(s, NativeCryptoTest.getCaPrincipals());
                    NativeCrypto.SSL_set_verify(s, ((NativeCrypto.SSL_VERIFY_PEER) | (NativeCrypto.SSL_VERIFY_FAIL_IF_NO_PEER_CERT)));
                    return s;
                }
            };
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
            server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertEquals(SSLProtocolException.class, expected.getCause().getClass());
        }
    }

    /**
     * Usually if a RuntimeException is thrown by the
     * clientCertificateRequestedCalled callback, the caller sees it
     * during the call to NativeCrypto_SSL_do_handshake.  However, IIS
     * does not request client certs until after the initial
     * handshake. It does an SSL renegotiation, which means we need to
     * be able to deliver the callback's exception in cases like
     * SSL_read, SSL_write, and SSL_shutdown.
     */
    public void test_SSL_do_handshake_clientCertificateRequested_throws_after_renegotiate() throws Exception {
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public long beforeHandshake(long context) throws SSLException {
                long s = super.beforeHandshake(context);
                NativeCrypto.SSL_clear_mode(s, NativeCrypto.SSL_MODE_HANDSHAKE_CUTTHROUGH);
                return s;
            }

            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                NativeCrypto.SSL_read(s, fd, callback, new byte[1], 0, 1, 0);
                TestCase.fail();
                super.afterHandshake(session, s, c, sock, fd, callback);
            }

            @Override
            public void clientCertificateRequested(long s) {
                super.clientCertificateRequested(s);
                throw new RuntimeException("expected");
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                try {
                    NativeCrypto.SSL_set_verify(s, SSL_VERIFY_PEER);
                    NativeCrypto.SSL_set_options(s, SSL_OP_NO_SESSION_RESUMPTION_ON_RENEGOTIATION);
                    NativeCrypto.SSL_renegotiate(s);
                    NativeCrypto.SSL_write(s, fd, callback, new byte[]{ 42 }, 0, 1, ((int) (((NativeCryptoTest.TIMEOUT_SECONDS) * 1000) / 2)));
                } catch (IOException expected) {
                } finally {
                    super.afterHandshake(session, s, c, sock, fd, callback);
                }
            }
        };
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        try {
            client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (!("expected".equals(e.getCause().getMessage()))) {
                throw e;
            }
        }
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void test_SSL_do_handshake_client_timeout() throws Exception {
        // client timeout
        final ServerSocket listener = new ServerSocket(0);
        Socket serverSocket = null;
        try {
            NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks();
            NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 1, true, cHooks, null, null);
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, (-1), false, sHooks, null, null);
            serverSocket = server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS).getSocket();
            client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
            TestCase.fail();
        } catch (ExecutionException expected) {
            if ((SocketTimeoutException.class) != (expected.getCause().getClass())) {
                expected.printStackTrace();
            }
            TestCase.assertEquals(SocketTimeoutException.class, expected.getCause().getClass());
        } finally {
            // Manually close peer socket when testing timeout
            IoUtils.closeQuietly(serverSocket);
        }
    }

    public void test_SSL_do_handshake_server_timeout() throws Exception {
        // server timeout
        final ServerSocket listener = new ServerSocket(0);
        Socket clientSocket = null;
        try {
            NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks();
            NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, (-1), true, cHooks, null, null);
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 1, false, sHooks, null, null);
            clientSocket = client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS).getSocket();
            server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertEquals(SocketTimeoutException.class, expected.getCause().getClass());
        } finally {
            // Manually close peer socket when testing timeout
            IoUtils.closeQuietly(clientSocket);
        }
    }

    public void test_SSL_do_handshake_with_channel_id_normal() throws Exception {
        NativeCryptoTest.initChannelIdKey();
        // Normal handshake with TLS Channel ID.
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks();
        cHooks.channelIdPrivateKey = NativeCryptoTest.CHANNEL_ID_PRIVATE_KEY;
        NativeCryptoTest.ServerHooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
        sHooks.channelIdEnabled = true;
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        NativeCryptoTest.TestSSLHandshakeCallbacks clientCallback = client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        NativeCryptoTest.TestSSLHandshakeCallbacks serverCallback = server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        TestCase.assertTrue(clientCallback.verifyCertificateChainCalled);
        NativeCryptoTest.assertEqualCertificateChains(NativeCryptoTest.getServerCertificates(), clientCallback.asn1DerEncodedCertificateChain);
        TestCase.assertEquals("RSA", clientCallback.authMethod);
        TestCase.assertFalse(serverCallback.verifyCertificateChainCalled);
        TestCase.assertFalse(clientCallback.clientCertificateRequestedCalled);
        TestCase.assertFalse(serverCallback.clientCertificateRequestedCalled);
        TestCase.assertTrue(clientCallback.handshakeCompletedCalled);
        TestCase.assertTrue(serverCallback.handshakeCompletedCalled);
        TestCase.assertNull(sHooks.channelIdAfterHandshakeException);
        NativeCryptoTest.assertEqualByteArrays(NativeCryptoTest.CHANNEL_ID, sHooks.channelIdAfterHandshake);
    }

    public void test_SSL_do_handshake_with_channel_id_not_supported_by_server() throws Exception {
        NativeCryptoTest.initChannelIdKey();
        // Client tries to use TLS Channel ID but the server does not enable/offer the extension.
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks();
        cHooks.channelIdPrivateKey = NativeCryptoTest.CHANNEL_ID_PRIVATE_KEY;
        NativeCryptoTest.ServerHooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
        sHooks.channelIdEnabled = false;
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        NativeCryptoTest.TestSSLHandshakeCallbacks clientCallback = client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        NativeCryptoTest.TestSSLHandshakeCallbacks serverCallback = server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        TestCase.assertTrue(clientCallback.verifyCertificateChainCalled);
        NativeCryptoTest.assertEqualCertificateChains(NativeCryptoTest.getServerCertificates(), clientCallback.asn1DerEncodedCertificateChain);
        TestCase.assertEquals("RSA", clientCallback.authMethod);
        TestCase.assertFalse(serverCallback.verifyCertificateChainCalled);
        TestCase.assertFalse(clientCallback.clientCertificateRequestedCalled);
        TestCase.assertFalse(serverCallback.clientCertificateRequestedCalled);
        TestCase.assertTrue(clientCallback.handshakeCompletedCalled);
        TestCase.assertTrue(serverCallback.handshakeCompletedCalled);
        TestCase.assertNull(sHooks.channelIdAfterHandshakeException);
        TestCase.assertNull(sHooks.channelIdAfterHandshake);
    }

    public void test_SSL_do_handshake_with_channel_id_not_enabled_by_client() throws Exception {
        NativeCryptoTest.initChannelIdKey();
        // Client does not use TLS Channel ID when the server has the extension enabled/offered.
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks();
        cHooks.channelIdPrivateKey = null;
        NativeCryptoTest.ServerHooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
        sHooks.channelIdEnabled = true;
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        NativeCryptoTest.TestSSLHandshakeCallbacks clientCallback = client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        NativeCryptoTest.TestSSLHandshakeCallbacks serverCallback = server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        TestCase.assertTrue(clientCallback.verifyCertificateChainCalled);
        NativeCryptoTest.assertEqualCertificateChains(NativeCryptoTest.getServerCertificates(), clientCallback.asn1DerEncodedCertificateChain);
        TestCase.assertEquals("RSA", clientCallback.authMethod);
        TestCase.assertFalse(serverCallback.verifyCertificateChainCalled);
        TestCase.assertFalse(clientCallback.clientCertificateRequestedCalled);
        TestCase.assertFalse(serverCallback.clientCertificateRequestedCalled);
        TestCase.assertTrue(clientCallback.handshakeCompletedCalled);
        TestCase.assertTrue(serverCallback.handshakeCompletedCalled);
        TestCase.assertNull(sHooks.channelIdAfterHandshakeException);
        TestCase.assertNull(sHooks.channelIdAfterHandshake);
    }

    public void test_SSL_set_session() throws Exception {
        try {
            NativeCrypto.SSL_set_session(NativeCryptoTest.NULL, NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            NativeCrypto.SSL_set_session(s, NativeCryptoTest.NULL);
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        {
            final long clientContext = NativeCrypto.SSL_CTX_new();
            final long serverContext = NativeCrypto.SSL_CTX_new();
            final ServerSocket listener = new ServerSocket(0);
            final long[] clientSession = new long[]{ NativeCryptoTest.NULL };
            final long[] serverSession = new long[]{ NativeCryptoTest.NULL };
            {
                NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
                    @Override
                    public long getContext() throws SSLException {
                        return clientContext;
                    }

                    @Override
                    public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                        super.afterHandshake(NativeCryptoTest.NULL, s, NativeCryptoTest.NULL, sock, fd, callback);
                        clientSession[0] = session;
                    }
                };
                NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
                    @Override
                    public long getContext() throws SSLException {
                        return serverContext;
                    }

                    @Override
                    public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                        super.afterHandshake(NativeCryptoTest.NULL, s, NativeCryptoTest.NULL, sock, fd, callback);
                        serverSession[0] = session;
                    }
                };
                Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
                Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
                client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
                server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }
            NativeCryptoTest.assertEqualSessions(clientSession[0], serverSession[0]);
            {
                NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
                    @Override
                    public long getContext() throws SSLException {
                        return clientContext;
                    }

                    @Override
                    public long beforeHandshake(long c) throws SSLException {
                        long s = NativeCrypto.SSL_new(clientContext);
                        NativeCrypto.SSL_set_session(s, clientSession[0]);
                        return s;
                    }

                    @Override
                    public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                        NativeCryptoTest.assertEqualSessions(clientSession[0], session);
                        super.afterHandshake(NativeCryptoTest.NULL, s, NativeCryptoTest.NULL, sock, fd, callback);
                    }
                };
                NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
                    @Override
                    public long getContext() throws SSLException {
                        return serverContext;
                    }

                    @Override
                    public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                        NativeCryptoTest.assertEqualSessions(serverSession[0], session);
                        super.afterHandshake(NativeCryptoTest.NULL, s, NativeCryptoTest.NULL, sock, fd, callback);
                    }
                };
                Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
                Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
                client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
                server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }
            NativeCrypto.SSL_SESSION_free(clientSession[0]);
            NativeCrypto.SSL_SESSION_free(serverSession[0]);
            NativeCrypto.SSL_CTX_free(serverContext);
            NativeCrypto.SSL_CTX_free(clientContext);
        }
    }

    public void test_SSL_set_session_creation_enabled() throws Exception {
        try {
            NativeCrypto.SSL_set_session_creation_enabled(NativeCryptoTest.NULL, false);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            NativeCrypto.SSL_set_session_creation_enabled(s, false);
            NativeCrypto.SSL_set_session_creation_enabled(s, true);
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        final ServerSocket listener = new ServerSocket(0);
        // negative test case for SSL_set_session_creation_enabled(false) on client
        try {
            NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
                @Override
                public long beforeHandshake(long c) throws SSLException {
                    long s = super.beforeHandshake(c);
                    NativeCrypto.SSL_set_session_creation_enabled(s, false);
                    return s;
                }
            };
            NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
            client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertEquals(SSLProtocolException.class, expected.getCause().getClass());
        }
        // negative test case for SSL_set_session_creation_enabled(false) on server
        try {
            NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks();
            NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
                @Override
                public long beforeHandshake(long c) throws SSLException {
                    long s = super.beforeHandshake(c);
                    NativeCrypto.SSL_set_session_creation_enabled(s, false);
                    return s;
                }
            };
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
            client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertEquals(SSLProtocolException.class, expected.getCause().getClass());
        }
    }

    public void test_SSL_set_tlsext_host_name() throws Exception {
        // NULL SSL
        try {
            NativeCrypto.SSL_set_tlsext_host_name(NativeCryptoTest.NULL, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        final String hostname = "www.android.com";
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            // null hostname
            try {
                NativeCrypto.SSL_set_tlsext_host_name(s, null);
                TestCase.fail();
            } catch (NullPointerException expected) {
            }
            // too long hostname
            try {
                char[] longHostname = new char[256];
                Arrays.fill(longHostname, 'w');
                NativeCrypto.SSL_set_tlsext_host_name(s, new String(longHostname));
                TestCase.fail();
            } catch (SSLException expected) {
            }
            TestCase.assertNull(NativeCrypto.SSL_get_servername(s));
            NativeCrypto.SSL_set_tlsext_host_name(s, new String(hostname));
            TestCase.assertEquals(hostname, NativeCrypto.SSL_get_servername(s));
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        final ServerSocket listener = new ServerSocket(0);
        // normal
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public long beforeHandshake(long c) throws SSLException {
                long s = super.beforeHandshake(c);
                NativeCrypto.SSL_set_tlsext_host_name(s, hostname);
                return s;
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                TestCase.assertEquals(hostname, NativeCrypto.SSL_get_servername(s));
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void test_SSL_NpnNegotiateSuccess() throws Exception {
        final byte[] clientNpnProtocols = new byte[]{ 8, 'h', 't', 't', 'p', '/', '1', '.', '1', 3, 'f', 'o', 'o', 6, 's', 'p', 'd', 'y', '/', '2' };
        final byte[] serverNpnProtocols = new byte[]{ 6, 's', 'p', 'd', 'y', '/', '2', 3, 'f', 'o', 'o', 3, 'b', 'a', 'r' };
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public long beforeHandshake(long context) throws SSLException {
                NativeCrypto.SSL_CTX_enable_npn(context);
                return super.beforeHandshake(context);
            }

            @Override
            public void afterHandshake(long session, long ssl, long context, Socket socket, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                byte[] negotiated = NativeCrypto.SSL_get_npn_negotiated_protocol(ssl);
                TestCase.assertEquals("spdy/2", new String(negotiated));
                TestCase.assertTrue("NPN should enable cutthrough on the client", (0 != ((NativeCrypto.SSL_get_mode(ssl)) & (NativeCrypto.SSL_MODE_HANDSHAKE_CUTTHROUGH))));
                super.afterHandshake(session, ssl, context, socket, fd, callback);
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
            @Override
            public long beforeHandshake(long context) throws SSLException {
                NativeCrypto.SSL_CTX_enable_npn(context);
                return super.beforeHandshake(context);
            }

            @Override
            public void afterHandshake(long session, long ssl, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                byte[] negotiated = NativeCrypto.SSL_get_npn_negotiated_protocol(ssl);
                TestCase.assertEquals("spdy/2", new String(negotiated));
                TestCase.assertEquals("NPN should not enable cutthrough on the server", 0, ((NativeCrypto.SSL_get_mode(ssl)) & (NativeCrypto.SSL_MODE_HANDSHAKE_CUTTHROUGH)));
                super.afterHandshake(session, ssl, c, sock, fd, callback);
            }
        };
        ServerSocket listener = new ServerSocket(0);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, clientNpnProtocols, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, serverNpnProtocols, null);
        client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void test_SSL_AlpnNegotiateSuccess() throws Exception {
        final byte[] clientAlpnProtocols = new byte[]{ 8, 'h', 't', 't', 'p', '/', '1', '.', '1', 3, 'f', 'o', 'o', 6, 's', 'p', 'd', 'y', '/', '2' };
        final byte[] serverAlpnProtocols = new byte[]{ 6, 's', 'p', 'd', 'y', '/', '2', 3, 'f', 'o', 'o', 3, 'b', 'a', 'r' };
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public long beforeHandshake(long context) throws SSLException {
                NativeCrypto.SSL_CTX_set_alpn_protos(context, clientAlpnProtocols);
                return super.beforeHandshake(context);
            }

            @Override
            public void afterHandshake(long session, long ssl, long context, Socket socket, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                byte[] negotiated = NativeCrypto.SSL_get0_alpn_selected(ssl);
                TestCase.assertEquals("spdy/2", new String(negotiated));
                /* There is no callback on the client, so we can't enable
                cut-through
                 */
                TestCase.assertEquals("ALPN should not enable cutthrough on the client", 0, ((NativeCrypto.SSL_get_mode(ssl)) & (NativeCrypto.SSL_MODE_HANDSHAKE_CUTTHROUGH)));
                super.afterHandshake(session, ssl, context, socket, fd, callback);
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
            @Override
            public void afterHandshake(long session, long ssl, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                byte[] negotiated = NativeCrypto.SSL_get0_alpn_selected(ssl);
                TestCase.assertEquals("spdy/2", new String(negotiated));
                TestCase.assertEquals("ALPN should not enable cutthrough on the server", 0, ((NativeCrypto.SSL_get_mode(ssl)) & (NativeCrypto.SSL_MODE_HANDSHAKE_CUTTHROUGH)));
                super.afterHandshake(session, ssl, c, sock, fd, callback);
            }
        };
        ServerSocket listener = new ServerSocket(0);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, serverAlpnProtocols);
        client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void test_SSL_get_servername_null() throws Exception {
        // NULL SSL
        try {
            NativeCrypto.SSL_get_servername(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        TestCase.assertNull(NativeCrypto.SSL_get_servername(s));
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
        // additional positive testing by test_SSL_set_tlsext_host_name
    }

    public void test_SSL_renegotiate() throws Exception {
        try {
            NativeCrypto.SSL_renegotiate(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                byte[] buffer = new byte[1];
                NativeCrypto.SSL_read(s, fd, callback, buffer, 0, 1, 0);
                TestCase.assertEquals(42, buffer[0]);
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                NativeCrypto.SSL_renegotiate(s);
                NativeCrypto.SSL_write(s, fd, callback, new byte[]{ 42 }, 0, 1, 0);
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void test_SSL_get_certificate() throws Exception {
        try {
            NativeCrypto.SSL_get_certificate(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                TestCase.assertNull(NativeCrypto.SSL_get_certificate(s));
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                NativeCryptoTest.assertEqualCertificateChains(NativeCryptoTest.getServerCertificates(), NativeCrypto.SSL_get_certificate(s));
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void test_SSL_get_peer_cert_chain() throws Exception {
        try {
            NativeCrypto.SSL_get_peer_cert_chain(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                byte[][] cc = NativeCrypto.SSL_get_peer_cert_chain(s);
                NativeCryptoTest.assertEqualCertificateChains(NativeCryptoTest.getServerCertificates(), cc);
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    final byte[] BYTES = new byte[]{ 2, -3, 5, 127, 0, -128 };

    public void test_SSL_read() throws Exception {
        // NULL ssl
        try {
            NativeCrypto.SSL_read(NativeCryptoTest.NULL, null, null, null, 0, 0, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // null FileDescriptor
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            try {
                NativeCrypto.SSL_read(s, null, NativeCryptoTest.DUMMY_CB, null, 0, 0, 0);
                TestCase.fail();
            } catch (NullPointerException expected) {
            }
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        // null SSLHandshakeCallbacks
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            try {
                NativeCrypto.SSL_read(s, NativeCryptoTest.INVALID_FD, null, null, 0, 0, 0);
                TestCase.fail();
            } catch (NullPointerException expected) {
            }
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        // null byte array
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            try {
                NativeCrypto.SSL_read(s, NativeCryptoTest.INVALID_FD, NativeCryptoTest.DUMMY_CB, null, 0, 0, 0);
                TestCase.fail();
            } catch (NullPointerException expected) {
            }
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        // handshaking not yet performed
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            try {
                NativeCrypto.SSL_read(s, NativeCryptoTest.INVALID_FD, NativeCryptoTest.DUMMY_CB, new byte[1], 0, 1, 0);
                TestCase.fail();
            } catch (SSLException expected) {
            }
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        final ServerSocket listener = new ServerSocket(0);
        // normal case
        {
            NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
                @Override
                public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                    byte[] in = new byte[256];
                    TestCase.assertEquals(BYTES.length, NativeCrypto.SSL_read(s, fd, callback, in, 0, BYTES.length, 0));
                    for (int i = 0; i < (BYTES.length); i++) {
                        TestCase.assertEquals(BYTES[i], in[i]);
                    }
                    super.afterHandshake(session, s, c, sock, fd, callback);
                }
            };
            NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
                @Override
                public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                    NativeCrypto.SSL_write(s, fd, callback, BYTES, 0, BYTES.length, 0);
                    super.afterHandshake(session, s, c, sock, fd, callback);
                }
            };
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
            client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
            server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
        // timeout case
        try {
            NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
                @Override
                public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                    NativeCrypto.SSL_read(s, fd, callback, new byte[1], 0, 1, 1);
                    TestCase.fail();
                }
            };
            NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
                @Override
                public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                    NativeCrypto.SSL_read(s, fd, callback, new byte[1], 0, 1, 0);
                    super.afterHandshake(session, s, c, sock, fd, callback);
                }
            };
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
            client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertEquals(SocketTimeoutException.class, expected.getCause().getClass());
        }
    }

    public void test_SSL_write() throws Exception {
        try {
            NativeCrypto.SSL_write(NativeCryptoTest.NULL, null, null, null, 0, 0, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // null FileDescriptor
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            try {
                NativeCrypto.SSL_write(s, null, NativeCryptoTest.DUMMY_CB, null, 0, 1, 0);
                TestCase.fail();
            } catch (NullPointerException expected) {
            }
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        // null SSLHandshakeCallbacks
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            try {
                NativeCrypto.SSL_write(s, NativeCryptoTest.INVALID_FD, null, null, 0, 1, 0);
                TestCase.fail();
            } catch (NullPointerException expected) {
            }
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        // null byte array
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            try {
                NativeCrypto.SSL_write(s, NativeCryptoTest.INVALID_FD, NativeCryptoTest.DUMMY_CB, null, 0, 1, 0);
                TestCase.fail();
            } catch (NullPointerException expected) {
            }
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        // handshaking not yet performed
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            try {
                NativeCrypto.SSL_write(s, NativeCryptoTest.INVALID_FD, NativeCryptoTest.DUMMY_CB, new byte[1], 0, 1, 0);
                TestCase.fail();
            } catch (SSLException expected) {
            }
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        // positively tested by test_SSL_read
    }

    public void test_SSL_interrupt() throws Exception {
        // SSL_interrupt is a rare case that tolerates a null SSL argument
        NativeCrypto.SSL_interrupt(NativeCryptoTest.NULL);
        // also works without handshaking
        {
            long c = NativeCrypto.SSL_CTX_new();
            long s = NativeCrypto.SSL_new(c);
            NativeCrypto.SSL_interrupt(s);
            NativeCrypto.SSL_free(s);
            NativeCrypto.SSL_CTX_free(c);
        }
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                NativeCrypto.SSL_read(s, fd, callback, new byte[1], 0, 1, 0);
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates()) {
            @Override
            public void afterHandshake(long session, final long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep((1 * 1000));
                            NativeCrypto.SSL_interrupt(s);
                        } catch (Exception e) {
                        }
                    }
                }.start();
                TestCase.assertEquals((-1), NativeCrypto.SSL_read(s, fd, callback, new byte[1], 0, 1, 0));
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void test_SSL_shutdown() throws Exception {
        // null FileDescriptor
        try {
            NativeCrypto.SSL_shutdown(NativeCryptoTest.NULL, null, NativeCryptoTest.DUMMY_CB);
        } catch (NullPointerException expected) {
        }
        // null SSLHandshakeCallbacks
        try {
            NativeCrypto.SSL_shutdown(NativeCryptoTest.NULL, NativeCryptoTest.INVALID_FD, null);
        } catch (NullPointerException expected) {
        }
        // SSL_shutdown is a rare case that tolerates a null SSL argument
        NativeCrypto.SSL_shutdown(NativeCryptoTest.NULL, NativeCryptoTest.INVALID_FD, NativeCryptoTest.DUMMY_CB);
        // handshaking not yet performed
        long c = NativeCrypto.SSL_CTX_new();
        long s = NativeCrypto.SSL_new(c);
        try {
            NativeCrypto.SSL_shutdown(s, NativeCryptoTest.INVALID_FD, NativeCryptoTest.DUMMY_CB);
        } catch (SSLProtocolException expected) {
        }
        NativeCrypto.SSL_free(s);
        NativeCrypto.SSL_CTX_free(c);
        // positively tested elsewhere because handshake uses use
        // SSL_shutdown to ensure SSL_SESSIONs are reused.
    }

    public void test_SSL_free() throws Exception {
        try {
            NativeCrypto.SSL_free(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        long c = NativeCrypto.SSL_CTX_new();
        NativeCrypto.SSL_free(NativeCrypto.SSL_new(c));
        NativeCrypto.SSL_CTX_free(c);
        // additional positive testing elsewhere because handshake
        // uses use SSL_free to cleanup in afterHandshake.
    }

    public void test_SSL_SESSION_session_id() throws Exception {
        try {
            NativeCrypto.SSL_SESSION_session_id(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                byte[] id = NativeCrypto.SSL_SESSION_session_id(session);
                TestCase.assertNotNull(id);
                TestCase.assertEquals(32, id.length);
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void test_SSL_SESSION_get_time() throws Exception {
        try {
            NativeCrypto.SSL_SESSION_get_time(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        final ServerSocket listener = new ServerSocket(0);
        {
            NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
                @Override
                public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                    long time = NativeCrypto.SSL_SESSION_get_time(session);
                    TestCase.assertTrue((time != 0));
                    TestCase.assertTrue((time < (System.currentTimeMillis())));
                    super.afterHandshake(session, s, c, sock, fd, callback);
                }
            };
            NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
            Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
            client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
            server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
    }

    public void test_SSL_SESSION_get_version() throws Exception {
        try {
            NativeCrypto.SSL_SESSION_get_version(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                String v = NativeCrypto.SSL_SESSION_get_version(session);
                TestCase.assertTrue(SSL_SOCKET_PROTOCOLS.contains(v));
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void test_SSL_SESSION_cipher() throws Exception {
        try {
            NativeCrypto.SSL_SESSION_cipher(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                String a = NativeCrypto.SSL_SESSION_cipher(session);
                TestCase.assertTrue(OPENSSL_TO_STANDARD_CIPHER_SUITES.containsKey(a));
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void test_SSL_SESSION_free() throws Exception {
        try {
            NativeCrypto.SSL_SESSION_free(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        // additional positive testing elsewhere because handshake
        // uses use SSL_SESSION_free to cleanup in afterHandshake.
    }

    public void test_i2d_SSL_SESSION() throws Exception {
        try {
            NativeCrypto.i2d_SSL_SESSION(NativeCryptoTest.NULL);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        final ServerSocket listener = new ServerSocket(0);
        NativeCryptoTest.Hooks cHooks = new NativeCryptoTest.Hooks() {
            @Override
            public void afterHandshake(long session, long s, long c, Socket sock, FileDescriptor fd, SSLHandshakeCallbacks callback) throws Exception {
                byte[] b = NativeCrypto.i2d_SSL_SESSION(session);
                TestCase.assertNotNull(b);
                long session2 = NativeCrypto.d2i_SSL_SESSION(b);
                TestCase.assertTrue((session2 != (NativeCryptoTest.NULL)));
                // Make sure d2i_SSL_SESSION retores SSL_SESSION_cipher value http://b/7091840
                TestCase.assertTrue(((NativeCrypto.SSL_SESSION_cipher(session2)) != null));
                TestCase.assertEquals(NativeCrypto.SSL_SESSION_cipher(session), NativeCrypto.SSL_SESSION_cipher(session2));
                NativeCrypto.SSL_SESSION_free(session2);
                super.afterHandshake(session, s, c, sock, fd, callback);
            }
        };
        NativeCryptoTest.Hooks sHooks = new NativeCryptoTest.ServerHooks(NativeCryptoTest.getServerPrivateKey(), NativeCryptoTest.getServerCertificates());
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> client = NativeCryptoTest.handshake(listener, 0, true, cHooks, null, null);
        Future<NativeCryptoTest.TestSSLHandshakeCallbacks> server = NativeCryptoTest.handshake(listener, 0, false, sHooks, null, null);
        client.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.get(NativeCryptoTest.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void test_d2i_SSL_SESSION() throws Exception {
        try {
            NativeCrypto.d2i_SSL_SESSION(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        TestCase.assertEquals(NativeCryptoTest.NULL, NativeCrypto.d2i_SSL_SESSION(new byte[0]));
        TestCase.assertEquals(NativeCryptoTest.NULL, NativeCrypto.d2i_SSL_SESSION(new byte[1]));
        // positive testing by test_i2d_SSL_SESSION
    }

    public void test_X509_NAME_hashes() {
        // ensure these hash functions are stable over time since the
        // /system/etc/security/cacerts CA filenames have to be
        // consistent with the output.
        X500Principal name = new X500Principal("CN=localhost");
        TestCase.assertEquals((-1372642656), NativeCrypto.X509_NAME_hash(name));// SHA1

        TestCase.assertEquals((-1626170662), NativeCrypto.X509_NAME_hash_old(name));// MD5

    }

    public void test_ENGINE_by_id_Failure() throws Exception {
        NativeCrypto.ENGINE_load_dynamic();
        long engine = NativeCrypto.ENGINE_by_id("non-existent");
        if (engine != 0) {
            NativeCrypto.ENGINE_finish(engine);
            TestCase.fail("should not acquire reference to non-existent engine");
        }
    }

    public void test_ENGINE_by_id_TestEngine() throws Exception {
        NativeCryptoTest.loadTestEngine();
        long engine = NativeCrypto.ENGINE_by_id(NativeCryptoTest.TEST_ENGINE_ID);
        TestCase.assertTrue((engine != 0));
        NativeCrypto.ENGINE_add(engine);
        long pkey = NativeCryptoTest.NULL;
        try {
            final String rsaPem = "-----BEGIN RSA PRIVATE KEY-----\n" + ((((((((((((("MIICXAIBAAKBgQCvvsYz1VKhU9PT0NHlotX22tcCjeaiVFNg0JrkjoK2XuMb+7a6\n" + "R5bzgIr24+OnBB0LqgaKnHwxZTA73lo/Wy/Ms5Kvg4yX9UMkNE+PvH5vzcQBbFdI\n") + "lwETFPvFokHO5OyOcEY+iVWG2fDloteH2JsrKYLh9Sx3Br5pHFCCm5qT5wIDAQAB\n") + "AoGAWDxoNs371pPH3qkROUIwOuhU2ytziDzeP9V8bxQ9/GJXlE0kyRH4b/kxzBNO\n") + "0SP3kUukTSOUFxi+xtA0b2rQ7Be2txtjzW1TGOHSCWbFrJAdTqeBcmQJSaZay8n1\n") + "LOpk4/zvBl7VScBth1IgXP44v6lOzthsrDhMlUYs07ymwYECQQDonaLOhkmVThPa\n") + "CIThdE5CN/wF5UDzGOz+ZBz3dt8D8QQMu0aZaPzibq9BC462j/fWeWS5OFzbq2+T\n") + "+cor3nwPAkEAwWmTQdra6GMPEc40zNsM5ehF2FjOpX8aU8267eG56y0Y+GbHx2BN\n") + "zAHfPxGBBH8cZ0cLhk4RSo/po7Vv+cRyqQJAAQz1N0mT+4Cmxk1TjFEiKVpnYP9w\n") + "E6kBKQT6vINk7negNQ6Dex3mRn+Jexm6Q0jTLbzOn6eJg9R6ZIi0SQ5wMQJAKX2n\n") + "fGohqdaORgiRZRzcsHlaemXatsAEetPYdO2Gf7/l6mvKEahEKC6CoLn1jmxiQHmK\n") + "LF6U8QTcXyUuB0uwOQJBAIwWWjQGGc2sAQ1HW0C2wwCQbWneeBkiRBedonDBHtiB\n") + "Wz0zS2CMCtBPNeHQmmsXH2Ca+ADdh53sKTuperLiuiw=\n") + "-----END RSA PRIVATE KEY-----");
            pkey = NativeCrypto.ENGINE_load_private_key(engine, rsaPem);
            TestCase.assertTrue((pkey != 0));
        } finally {
            if (pkey != (NativeCryptoTest.NULL)) {
                NativeCrypto.EVP_PKEY_free(pkey);
            }
            NativeCrypto.ENGINE_free(engine);
            NativeCrypto.ENGINE_finish(engine);
        }
    }

    public void test_RAND_bytes_Success() throws Exception {
        byte[] output = new byte[128];
        NativeCrypto.RAND_bytes(output);
        boolean isZero = true;
        for (int i = 0; i < (output.length); i++) {
            isZero &= (output[i]) == 0;
        }
        TestCase.assertFalse(("Random output was zero. This is a very low probability event (1 in 2^128) " + "and probably indicates an error."), isZero);
    }

    public void test_RAND_bytes_Null_Failure() throws Exception {
        byte[] output = null;
        try {
            NativeCrypto.RAND_bytes(output);
            TestCase.fail("Should be an error on null buffer input");
        } catch (RuntimeException expected) {
        }
    }

    public void test_EVP_get_digestbyname() throws Exception {
        TestCase.assertTrue(((NativeCrypto.EVP_get_digestbyname("sha256")) != (NativeCryptoTest.NULL)));
        try {
            NativeCrypto.EVP_get_digestbyname(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            NativeCrypto.EVP_get_digestbyname("");
            NativeCrypto.EVP_get_digestbyname("foobar");
            TestCase.fail();
        } catch (RuntimeException expected) {
        }
    }

    public void test_EVP_SignInit() throws Exception {
        final long ctx = NativeCrypto.EVP_SignInit("RSA-SHA256");
        TestCase.assertTrue((ctx != (NativeCryptoTest.NULL)));
        NativeCrypto.EVP_MD_CTX_destroy(ctx);
        try {
            NativeCrypto.EVP_SignInit("foobar");
            TestCase.fail();
        } catch (RuntimeException expected) {
        }
    }

    public void test_get_RSA_private_params() throws Exception {
        try {
            NativeCrypto.get_RSA_private_params(NativeCryptoTest.NULL);
        } catch (NullPointerException expected) {
        }
        try {
            NativeCrypto.get_RSA_private_params(NativeCryptoTest.NULL);
        } catch (NullPointerException expected) {
        }
        // Test getting params for the wrong kind of key.
        final byte[] seed = new byte[20];
        long ctx = 0;
        try {
            ctx = NativeCrypto.DSA_generate_key(2048, seed, dsa2048_g, dsa2048_p, dsa2048_q);
            TestCase.assertTrue((ctx != (NativeCryptoTest.NULL)));
            try {
                NativeCrypto.get_RSA_private_params(ctx);
                TestCase.fail();
            } catch (RuntimeException expected) {
            }
        } finally {
            if (ctx != 0) {
                NativeCrypto.EVP_PKEY_free(ctx);
            }
        }
    }

    public void test_get_RSA_public_params() throws Exception {
        try {
            NativeCrypto.get_RSA_public_params(NativeCryptoTest.NULL);
        } catch (NullPointerException expected) {
        }
        try {
            NativeCrypto.get_RSA_public_params(NativeCryptoTest.NULL);
        } catch (NullPointerException expected) {
        }
        // Test getting params for the wrong kind of key.
        final byte[] seed = new byte[20];
        long ctx = 0;
        try {
            ctx = NativeCrypto.DSA_generate_key(2048, seed, dsa2048_g, dsa2048_p, dsa2048_q);
            TestCase.assertTrue((ctx != (NativeCryptoTest.NULL)));
            try {
                NativeCrypto.get_RSA_public_params(ctx);
                TestCase.fail();
            } catch (RuntimeException expected) {
            }
        } finally {
            if (ctx != 0) {
                NativeCrypto.EVP_PKEY_free(ctx);
            }
        }
    }

    final byte[] dsa2048_p = new byte[]{ ((byte) (195)), ((byte) (22)), ((byte) (212)), ((byte) (186)), ((byte) (220)), ((byte) (14)), ((byte) (184)), ((byte) (252)), ((byte) (64)), ((byte) (219)), ((byte) (176)), ((byte) (118)), ((byte) (71)), ((byte) (184)), ((byte) (141)), ((byte) (193)), ((byte) (241)), ((byte) (171)), ((byte) (155)), ((byte) (128)), ((byte) (157)), ((byte) (220)), ((byte) (85)), ((byte) (51)), ((byte) (236)), ((byte) (182)), ((byte) (9)), ((byte) (143)), ((byte) (183)), ((byte) (217)), ((byte) (165)), ((byte) (127)), ((byte) (193)), ((byte) (227)), ((byte) (173)), ((byte) (225)), ((byte) (122)), ((byte) (88)), ((byte) (244)), ((byte) (45)), ((byte) (185)), ((byte) (97)), ((byte) (207)), ((byte) (91)), ((byte) (202)), ((byte) (65)), ((byte) (159)), ((byte) (115)), ((byte) (141)), ((byte) (129)), ((byte) (98)), ((byte) (210)), ((byte) (25)), ((byte) (125)), ((byte) (24)), ((byte) (219)), ((byte) (179)), ((byte) (4)), ((byte) (231)), ((byte) (178)), ((byte) (40)), ((byte) (89)), ((byte) (20)), ((byte) (115)), ((byte) (67)), ((byte) (241)), ((byte) (69)), ((byte) (199)), ((byte) (71)), ((byte) (204)), ((byte) (209)), ((byte) (18)), ((byte) (142)), ((byte) (25)), ((byte) (0)), ((byte) (44)), ((byte) (208)), ((byte) (134)), ((byte) (84)), ((byte) (100)), ((byte) (45)), ((byte) (66)), ((byte) (108)), ((byte) (107)), ((byte) (92)), ((byte) (45)), ((byte) (77)), ((byte) (151)), ((byte) (106)), ((byte) (29)), ((byte) (137)), ((byte) (177)), ((byte) (44)), ((byte) (160)), ((byte) (5)), ((byte) (43)), ((byte) (60)), ((byte) (219)), ((byte) (31)), ((byte) (137)), ((byte) (3)), ((byte) (3)), ((byte) (146)), ((byte) (99)), ((byte) (182)), ((byte) (8)), ((byte) (50)), ((byte) (80)), ((byte) (178)), ((byte) (84)), ((byte) (163)), ((byte) (254)), ((byte) (108)), ((byte) (53)), ((byte) (23)), ((byte) (47)), ((byte) (127)), ((byte) (84)), ((byte) (164)), ((byte) (174)), ((byte) (150)), ((byte) (30)), ((byte) (49)), ((byte) (131)), ((byte) (241)), ((byte) (63)), ((byte) (158)), ((byte) (185)), ((byte) (93)), ((byte) (211)), ((byte) (169)), ((byte) (203)), ((byte) (229)), ((byte) (47)), ((byte) (188)), ((byte) (164)), ((byte) (26)), ((byte) (49)), ((byte) (65)), ((byte) (145)), ((byte) (44)), ((byte) (160)), ((byte) (244)), ((byte) (131)), ((byte) (172)), ((byte) (213)), ((byte) (186)), ((byte) (61)), ((byte) (25)), ((byte) (237)), ((byte) (241)), ((byte) (108)), ((byte) (217)), ((byte) (63)), ((byte) (48)), ((byte) (218)), ((byte) (128)), ((byte) (6)), ((byte) (86)), ((byte) (58)), ((byte) (140)), ((byte) (116)), ((byte) (99)), ((byte) (242)), ((byte) (237)), ((byte) (30)), ((byte) (227)), ((byte) (134)), ((byte) (149)), ((byte) (100)), ((byte) (42)), ((byte) (196)), ((byte) (95)), ((byte) (178)), ((byte) (100)), ((byte) (64)), ((byte) (157)), ((byte) (166)), ((byte) (184)), ((byte) (245)), ((byte) (132)), ((byte) (3)), ((byte) (46)), ((byte) (74)), ((byte) (122)), ((byte) (26)), ((byte) (176)), ((byte) (14)), ((byte) (186)), ((byte) (177)), ((byte) (245)), ((byte) (210)), ((byte) (231)), ((byte) (101)), ((byte) (206)), ((byte) (238)), ((byte) (44)), ((byte) (124)), ((byte) (104)), ((byte) (32)), ((byte) (80)), ((byte) (83)), ((byte) (15)), ((byte) (96)), ((byte) (146)), ((byte) (129)), ((byte) (192)), ((byte) (44)), ((byte) (42)), ((byte) (234)), ((byte) (233)), ((byte) (179)), ((byte) (42)), ((byte) (129)), ((byte) (218)), ((byte) (15)), ((byte) (187)), ((byte) (250)), ((byte) (91)), ((byte) (71)), ((byte) (218)), ((byte) (87)), ((byte) (78)), ((byte) (252)), ((byte) (5)), ((byte) (44)), ((byte) (106)), ((byte) (144)), ((byte) (160)), ((byte) (153)), ((byte) (136)), ((byte) (113)), ((byte) (138)), ((byte) (204)), ((byte) (210)), ((byte) (151)), ((byte) (17)), ((byte) (177)), ((byte) (206)), ((byte) (247)), ((byte) (71)), ((byte) (83)), ((byte) (83)), ((byte) (104)), ((byte) (225)), ((byte) (42)), ((byte) (86)), ((byte) (213)), ((byte) (61)), ((byte) (223)), ((byte) (8)), ((byte) (22)), ((byte) (31)), ((byte) (170)), ((byte) (84)), ((byte) (21)) };

    final byte[] dsa2048_q = new byte[]{ ((byte) (170)), ((byte) (221)), ((byte) (226)), ((byte) (206)), ((byte) (8)), ((byte) (192)), ((byte) (14)), ((byte) (145)), ((byte) (140)), ((byte) (217)), ((byte) (188)), ((byte) (30)), ((byte) (5)), ((byte) (112)), ((byte) (7)), ((byte) (59)), ((byte) (181)), ((byte) (169)), ((byte) (181)), ((byte) (139)), ((byte) (33)), ((byte) (104)), ((byte) (162)), ((byte) (118)), ((byte) (83)), ((byte) (30)), ((byte) (104)), ((byte) (27)), ((byte) (79)), ((byte) (136)), ((byte) (109)), ((byte) (207)) };

    final byte[] dsa2048_g = new byte[]{ ((byte) (107)), ((byte) (77)), ((byte) (33)), ((byte) (146)), ((byte) (36)), ((byte) (118)), ((byte) (229)), ((byte) (162)), ((byte) (206)), ((byte) (2)), ((byte) (133)), ((byte) (50)), ((byte) (115)), ((byte) (112)), ((byte) (255)), ((byte) (185)), ((byte) (212)), ((byte) (81)), ((byte) (186)), ((byte) (34)), ((byte) (139)), ((byte) (117)), ((byte) (41)), ((byte) (227)), ((byte) (242)), ((byte) (46)), ((byte) (32)), ((byte) (245)), ((byte) (106)), ((byte) (217)), ((byte) (117)), ((byte) (160)), ((byte) (192)), ((byte) (59)), ((byte) (18)), ((byte) (47)), ((byte) (79)), ((byte) (154)), ((byte) (248)), ((byte) (93)), ((byte) (69)), ((byte) (197)), ((byte) (128)), ((byte) (108)), ((byte) (155)), ((byte) (86)), ((byte) (190)), ((byte) (142)), ((byte) (64)), ((byte) (249)), ((byte) (10)), ((byte) (240)), ((byte) (61)), ((byte) (215)), ((byte) (124)), ((byte) (222)), ((byte) (34)), ((byte) (16)), ((byte) (36)), ((byte) (204)), ((byte) (174)), ((byte) (138)), ((byte) (192)), ((byte) (5)), ((byte) (205)), ((byte) (220)), ((byte) (16)), ((byte) (41)), ((byte) (77)), ((byte) (252)), ((byte) (236)), ((byte) (239)), ((byte) (81)), ((byte) (75)), ((byte) (249)), ((byte) (204)), ((byte) (153)), ((byte) (132)), ((byte) (27)), ((byte) (20)), ((byte) (104)), ((byte) (236)), ((byte) (240)), ((byte) (94)), ((byte) (7)), ((byte) (16)), ((byte) (9)), ((byte) (169)), ((byte) (44)), ((byte) (4)), ((byte) (208)), ((byte) (20)), ((byte) (191)), ((byte) (136)), ((byte) (158)), ((byte) (187)), ((byte) (227)), ((byte) (63)), ((byte) (222)), ((byte) (146)), ((byte) (225)), ((byte) (100)), ((byte) (7)), ((byte) (40)), ((byte) (193)), ((byte) (202)), ((byte) (72)), ((byte) (193)), ((byte) (29)), ((byte) (51)), ((byte) (228)), ((byte) (53)), ((byte) (190)), ((byte) (223)), ((byte) (94)), ((byte) (80)), ((byte) (249)), ((byte) (194)), ((byte) (14)), ((byte) (37)), ((byte) (13)), ((byte) (32)), ((byte) (140)), ((byte) (1)), ((byte) (10)), ((byte) (35)), ((byte) (212)), ((byte) (110)), ((byte) (66)), ((byte) (71)), ((byte) (225)), ((byte) (158)), ((byte) (54)), ((byte) (145)), ((byte) (200)), ((byte) (101)), ((byte) (68)), ((byte) (224)), ((byte) (4)), ((byte) (134)), ((byte) (47)), ((byte) (212)), ((byte) (144)), ((byte) (22)), ((byte) (9)), ((byte) (20)), ((byte) (177)), ((byte) (197)), ((byte) (125)), ((byte) (178)), ((byte) (124)), ((byte) (54)), ((byte) (13)), ((byte) (156)), ((byte) (31)), ((byte) (131)), ((byte) (87)), ((byte) (148)), ((byte) (38)), ((byte) (50)), ((byte) (156)), ((byte) (134)), ((byte) (142)), ((byte) (229)), ((byte) (128)), ((byte) (58)), ((byte) (169)), ((byte) (175)), ((byte) (74)), ((byte) (149)), ((byte) (120)), ((byte) (141)), ((byte) (230)), ((byte) (195)), ((byte) (12)), ((byte) (120)), ((byte) (131)), ((byte) (75)), ((byte) (245)), ((byte) (64)), ((byte) (4)), ((byte) (32)), ((byte) (144)), ((byte) (92)), ((byte) (161)), ((byte) (25)), ((byte) (235)), ((byte) (149)), ((byte) (112)), ((byte) (43)), ((byte) (148)), ((byte) (163)), ((byte) (67)), ((byte) (221)), ((byte) (235)), ((byte) (212)), ((byte) (12)), ((byte) (188)), ((byte) (189)), ((byte) (88)), ((byte) (45)), ((byte) (117)), ((byte) (176)), ((byte) (141)), ((byte) (139)), ((byte) (112)), ((byte) (185)), ((byte) (231)), ((byte) (163)), ((byte) (204)), ((byte) (140)), ((byte) (180)), ((byte) (205)), ((byte) (187)), ((byte) (75)), ((byte) (177)), ((byte) (21)), ((byte) (24)), ((byte) (121)), ((byte) (223)), ((byte) (34)), ((byte) (166)), ((byte) (92)), ((byte) (144)), ((byte) (124)), ((byte) (31)), ((byte) (234)), ((byte) (27)), ((byte) (242)), ((byte) (137)), ((byte) (135)), ((byte) (178)), ((byte) (236)), ((byte) (87)), ((byte) (255)), ((byte) (178)), ((byte) (218)), ((byte) (245)), ((byte) (173)), ((byte) (115)), ((byte) (192)), ((byte) (160)), ((byte) (32)), ((byte) (139)), ((byte) (120)), ((byte) (161)), ((byte) (93)), ((byte) (4)), ((byte) (10)), ((byte) (41)), ((byte) (227)), ((byte) (215)), ((byte) (55)), ((byte) (246)), ((byte) (162)), ((byte) (202)) };

    public void test_DSA_generate_key() throws Exception {
        final byte[] seed = new byte[20];
        // Real key
        {
            long ctx = 0;
            try {
                ctx = NativeCrypto.DSA_generate_key(2048, seed, dsa2048_g, dsa2048_p, dsa2048_q);
                TestCase.assertTrue((ctx != (NativeCryptoTest.NULL)));
            } finally {
                if (ctx != 0) {
                    NativeCrypto.EVP_PKEY_free(ctx);
                }
            }
        }
        // Real key with minimum bit size (should be 512 bits)
        {
            long ctx = 0;
            try {
                ctx = NativeCrypto.DSA_generate_key(0, null, null, null, null);
                TestCase.assertTrue((ctx != (NativeCryptoTest.NULL)));
            } finally {
                if (ctx != 0) {
                    NativeCrypto.EVP_PKEY_free(ctx);
                }
            }
        }
        // Bad DSA params.
        {
            long ctx = 0;
            try {
                ctx = NativeCrypto.DSA_generate_key(0, null, new byte[]{  }, new byte[]{  }, new byte[]{  });
                TestCase.fail();
            } catch (RuntimeException expected) {
            } finally {
                if (ctx != 0) {
                    NativeCrypto.EVP_PKEY_free(ctx);
                }
            }
        }
    }

    /* Test vector generation:
    openssl rand -hex 16
     */
    private static final byte[] AES_128_KEY = new byte[]{ ((byte) (61)), ((byte) (79)), ((byte) (137)), ((byte) (112)), ((byte) (177)), ((byte) (242)), ((byte) (117)), ((byte) (55)), ((byte) (244)), ((byte) (10)), ((byte) (57)), ((byte) (41)), ((byte) (138)), ((byte) (65)), ((byte) (85)), ((byte) (95)) };

    private static final byte[] AES_IV_ZEROES = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) };

    public void testEC_GROUP() throws Exception {
        /* Test using NIST's P-256 curve */
        check_EC_GROUP(EC_CURVE_GFP, "prime256v1", "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", "5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b", "6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", "4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", "FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", 1L);
        check_EC_GROUP(EC_CURVE_GF2M, "sect283r1", "0800000000000000000000000000000000000000000000000000000000000000000010A1", "000000000000000000000000000000000000000000000000000000000000000000000001", "027B680AC8B8596DA5A4AF8A19A0303FCA97FD7645309FA2A581485AF6263E313B79A2F5", "05F939258DB7DD90E1934F8C70B0DFEC2EED25B8557EAC9C80E2E198F8CDBECD86B12053", "03676854FE24141CB98FE6D4B20D02B4516FF702350EDDB0826779C813F0DF45BE8112F4", "03FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEF90399660FC938A90165B042A7CEFADB307", 2L);
    }

    public void test_EVP_CipherInit_ex_Null_Failure() throws Exception {
        final long ctx = NativeCrypto.EVP_CIPHER_CTX_new();
        try {
            final long evpCipher = NativeCrypto.EVP_get_cipherbyname("aes-128-ecb");
            try {
                NativeCrypto.EVP_CipherInit_ex(NativeCryptoTest.NULL, evpCipher, null, null, true);
                TestCase.fail("Null context should throw NullPointerException");
            } catch (NullPointerException expected) {
            }
            /* Initialize encrypting. */
            NativeCrypto.EVP_CipherInit_ex(ctx, evpCipher, null, null, true);
            NativeCrypto.EVP_CipherInit_ex(ctx, NativeCryptoTest.NULL, null, null, true);
            /* Initialize decrypting. */
            NativeCrypto.EVP_CipherInit_ex(ctx, evpCipher, null, null, false);
            NativeCrypto.EVP_CipherInit_ex(ctx, NativeCryptoTest.NULL, null, null, false);
        } finally {
            NativeCrypto.EVP_CIPHER_CTX_cleanup(ctx);
        }
    }

    public void test_EVP_CipherInit_ex_Success() throws Exception {
        final long ctx = NativeCrypto.EVP_CIPHER_CTX_new();
        try {
            final long evpCipher = NativeCrypto.EVP_get_cipherbyname("aes-128-ecb");
            NativeCrypto.EVP_CipherInit_ex(ctx, evpCipher, NativeCryptoTest.AES_128_KEY, null, true);
        } finally {
            NativeCrypto.EVP_CIPHER_CTX_cleanup(ctx);
        }
    }

    public void test_EVP_CIPHER_iv_length() throws Exception {
        long aes128ecb = NativeCrypto.EVP_get_cipherbyname("aes-128-ecb");
        TestCase.assertEquals(0, NativeCrypto.EVP_CIPHER_iv_length(aes128ecb));
        long aes128cbc = NativeCrypto.EVP_get_cipherbyname("aes-128-cbc");
        TestCase.assertEquals(16, NativeCrypto.EVP_CIPHER_iv_length(aes128cbc));
    }

    public void test_OpenSSLKey_toJava() throws Exception {
        OpenSSLKey key1;
        BigInteger e = BigInteger.valueOf(65537);
        key1 = new OpenSSLKey(NativeCrypto.RSA_generate_key_ex(1024, e.toByteArray()));
        TestCase.assertTrue(((key1.getPublicKey()) instanceof RSAPublicKey));
        key1 = new OpenSSLKey(NativeCrypto.DSA_generate_key(1024, null, null, null, null));
        TestCase.assertTrue(((key1.getPublicKey()) instanceof DSAPublicKey));
        long group1 = NativeCryptoTest.NULL;
        try {
            group1 = NativeCrypto.EC_GROUP_new_by_curve_name("prime256v1");
            TestCase.assertTrue((group1 != (NativeCryptoTest.NULL)));
            key1 = new OpenSSLKey(NativeCrypto.EC_KEY_generate_key(group1));
        } finally {
            if (group1 != (NativeCryptoTest.NULL)) {
                NativeCrypto.EC_GROUP_clear_free(group1);
            }
        }
        TestCase.assertTrue(((key1.getPublicKey()) instanceof ECPublicKey));
    }

    public void test_create_BIO_InputStream() throws Exception {
        byte[] actual = "Test".getBytes();
        ByteArrayInputStream is = new ByteArrayInputStream(actual);
        long ctx = NativeCrypto.create_BIO_InputStream(new OpenSSLBIOInputStream(is));
        try {
            byte[] buffer = new byte[1024];
            int numRead = NativeCrypto.BIO_read(ctx, buffer);
            TestCase.assertEquals(actual.length, numRead);
            TestCase.assertEquals(Arrays.toString(actual), Arrays.toString(Arrays.copyOfRange(buffer, 0, numRead)));
        } finally {
            NativeCrypto.BIO_free(ctx);
        }
    }

    public void test_create_BIO_OutputStream() throws Exception {
        byte[] actual = "Test".getBytes();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        long ctx = NativeCrypto.create_BIO_OutputStream(os);
        try {
            NativeCrypto.BIO_write(ctx, actual, 0, actual.length);
            TestCase.assertEquals(actual.length, os.size());
            TestCase.assertEquals(Arrays.toString(actual), Arrays.toString(os.toByteArray()));
        } finally {
            NativeCrypto.BIO_free(ctx);
        }
    }
}

