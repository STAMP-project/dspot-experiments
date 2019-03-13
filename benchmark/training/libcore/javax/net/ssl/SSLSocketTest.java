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


import StandardNames.CIPHER_SUITES;
import StandardNames.IS_RI;
import StandardNames.JSSE_PROVIDER_NAME;
import StandardNames.SSL_SOCKET_PROTOCOLS;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;
import junit.framework.TestCase;
import libcore.java.security.StandardNames;
import libcore.java.security.TestKeyStore;


public class SSLSocketTest extends TestCase {
    public void test_SSLSocket_getSupportedCipherSuites_names() throws Exception {
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        SSLSocket ssl = ((SSLSocket) (sf.createSocket()));
        String[] cipherSuites = ssl.getSupportedCipherSuites();
        StandardNames.assertSupportedCipherSuites(CIPHER_SUITES, cipherSuites);
        TestCase.assertNotSame(cipherSuites, ssl.getSupportedCipherSuites());
    }

    public void test_SSLSocket_getSupportedCipherSuites_connect() throws Exception {
        // note the rare usage of non-RSA keys
        TestKeyStore testKeyStore = new TestKeyStore.Builder().keyAlgorithms("RSA", "DSA", "EC", "EC_RSA").aliasPrefix("rsa-dsa-ec").ca(true).build();
        StringBuilder error = new StringBuilder();
        if (StandardNames.IS_RI) {
            test_SSLSocket_getSupportedCipherSuites_connect(testKeyStore, JSSE_PROVIDER_NAME, JSSE_PROVIDER_NAME, true, true, error);
        } else {
            test_SSLSocket_getSupportedCipherSuites_connect(testKeyStore, "HarmonyJSSE", "HarmonyJSSE", false, false, error);
            test_SSLSocket_getSupportedCipherSuites_connect(testKeyStore, "AndroidOpenSSL", "AndroidOpenSSL", true, true, error);
            test_SSLSocket_getSupportedCipherSuites_connect(testKeyStore, "HarmonyJSSE", "AndroidOpenSSL", false, true, error);
            test_SSLSocket_getSupportedCipherSuites_connect(testKeyStore, "AndroidOpenSSL", "HarmonyJSSE", true, false, error);
        }
        if ((error.length()) > 0) {
            throw new Exception((("One or more problems in " + "test_SSLSocket_getSupportedCipherSuites_connect:\n") + error));
        }
    }

    public void test_SSLSocket_getEnabledCipherSuites() throws Exception {
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        SSLSocket ssl = ((SSLSocket) (sf.createSocket()));
        String[] cipherSuites = ssl.getEnabledCipherSuites();
        StandardNames.assertValidCipherSuites(CIPHER_SUITES, cipherSuites);
        TestCase.assertNotSame(cipherSuites, ssl.getEnabledCipherSuites());
    }

    public void test_SSLSocket_setEnabledCipherSuites() throws Exception {
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        SSLSocket ssl = ((SSLSocket) (sf.createSocket()));
        try {
            ssl.setEnabledCipherSuites(null);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ssl.setEnabledCipherSuites(new String[1]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ssl.setEnabledCipherSuites(new String[]{ "Bogus" });
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        ssl.setEnabledCipherSuites(new String[0]);
        ssl.setEnabledCipherSuites(ssl.getEnabledCipherSuites());
        ssl.setEnabledCipherSuites(ssl.getSupportedCipherSuites());
    }

    public void test_SSLSocket_getSupportedProtocols() throws Exception {
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        SSLSocket ssl = ((SSLSocket) (sf.createSocket()));
        String[] protocols = ssl.getSupportedProtocols();
        StandardNames.assertSupportedProtocols(SSL_SOCKET_PROTOCOLS, protocols);
        TestCase.assertNotSame(protocols, ssl.getSupportedProtocols());
    }

    public void test_SSLSocket_getEnabledProtocols() throws Exception {
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        SSLSocket ssl = ((SSLSocket) (sf.createSocket()));
        String[] protocols = ssl.getEnabledProtocols();
        StandardNames.assertValidProtocols(SSL_SOCKET_PROTOCOLS, protocols);
        TestCase.assertNotSame(protocols, ssl.getEnabledProtocols());
    }

    public void test_SSLSocket_setEnabledProtocols() throws Exception {
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        SSLSocket ssl = ((SSLSocket) (sf.createSocket()));
        try {
            ssl.setEnabledProtocols(null);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ssl.setEnabledProtocols(new String[1]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ssl.setEnabledProtocols(new String[]{ "Bogus" });
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        ssl.setEnabledProtocols(new String[0]);
        ssl.setEnabledProtocols(ssl.getEnabledProtocols());
        ssl.setEnabledProtocols(ssl.getSupportedProtocols());
    }

    public void test_SSLSocket_getSession() throws Exception {
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        SSLSocket ssl = ((SSLSocket) (sf.createSocket()));
        SSLSession session = ssl.getSession();
        TestCase.assertNotNull(session);
        TestCase.assertFalse(session.isValid());
    }

    public void test_SSLSocket_startHandshake() throws Exception {
        final TestSSLContext c = TestSSLContext.create();
        SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                server.startHandshake();
                TestCase.assertNotNull(server.getSession());
                try {
                    server.getSession().getPeerCertificates();
                    TestCase.fail();
                } catch (SSLPeerUnverifiedException expected) {
                }
                Certificate[] localCertificates = server.getSession().getLocalCertificates();
                TestCase.assertNotNull(localCertificates);
                TestKeyStore.assertChainLength(localCertificates);
                TestCase.assertNotNull(localCertificates[0]);
                TestSSLContext.assertServerCertificateChain(c.serverTrustManager, localCertificates);
                TestSSLContext.assertCertificateInKeyStore(localCertificates[0], c.serverKeyStore);
                return null;
            }
        });
        executor.shutdown();
        client.startHandshake();
        TestCase.assertNotNull(client.getSession());
        TestCase.assertNull(client.getSession().getLocalCertificates());
        Certificate[] peerCertificates = client.getSession().getPeerCertificates();
        TestCase.assertNotNull(peerCertificates);
        TestKeyStore.assertChainLength(peerCertificates);
        TestCase.assertNotNull(peerCertificates[0]);
        TestSSLContext.assertServerCertificateChain(c.clientTrustManager, peerCertificates);
        TestSSLContext.assertCertificateInKeyStore(peerCertificates[0], c.serverKeyStore);
        future.get();
        client.close();
        server.close();
        c.close();
    }

    private static final class SSLServerSessionIdCallable implements Callable<byte[]> {
        private final SSLSocket server;

        private SSLServerSessionIdCallable(SSLSocket server) {
            this.server = server;
        }

        @Override
        public byte[] call() throws Exception {
            server.startHandshake();
            TestCase.assertNotNull(server.getSession());
            TestCase.assertNotNull(server.getSession().getId());
            return server.getSession().getId();
        }
    }

    public void test_SSLSocket_confirmSessionReuse() throws Exception {
        final TestSSLContext c = TestSSLContext.create();
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final SSLSocket client1 = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server1 = ((SSLSocket) (c.serverSocket.accept()));
        final Future<byte[]> future1 = executor.submit(new SSLSocketTest.SSLServerSessionIdCallable(server1));
        client1.startHandshake();
        TestCase.assertNotNull(client1.getSession());
        TestCase.assertNotNull(client1.getSession().getId());
        final byte[] clientSessionId1 = client1.getSession().getId();
        final byte[] serverSessionId1 = future1.get();
        TestCase.assertTrue(Arrays.equals(clientSessionId1, serverSessionId1));
        client1.close();
        server1.close();
        final SSLSocket client2 = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server2 = ((SSLSocket) (c.serverSocket.accept()));
        final Future<byte[]> future2 = executor.submit(new SSLSocketTest.SSLServerSessionIdCallable(server2));
        client2.startHandshake();
        TestCase.assertNotNull(client2.getSession());
        TestCase.assertNotNull(client2.getSession().getId());
        final byte[] clientSessionId2 = client2.getSession().getId();
        final byte[] serverSessionId2 = future2.get();
        TestCase.assertTrue(Arrays.equals(clientSessionId2, serverSessionId2));
        client2.close();
        server2.close();
        TestCase.assertTrue(Arrays.equals(clientSessionId1, clientSessionId2));
        executor.shutdown();
        c.close();
    }

    public void test_SSLSocket_startHandshake_noKeyStore() throws Exception {
        TestSSLContext c = TestSSLContext.create(null, null, null, null, null, null, null, null, SSLContext.getDefault(), SSLContext.getDefault());
        SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        // RI used to throw SSLException on accept, now throws on startHandshake
        if (StandardNames.IS_RI) {
            final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Void> future = executor.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    try {
                        server.startHandshake();
                        TestCase.fail();
                    } catch (SSLHandshakeException expected) {
                    }
                    return null;
                }
            });
            executor.shutdown();
            try {
                client.startHandshake();
                TestCase.fail();
            } catch (SSLHandshakeException expected) {
            }
            future.get();
            server.close();
        } else {
            try {
                c.serverSocket.accept();
                TestCase.fail();
            } catch (SSLException expected) {
            }
        }
        client.close();
        c.close();
    }

    public void test_SSLSocket_startHandshake_noClientCertificate() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLContext serverContext = c.serverContext;
        SSLContext clientContext = c.clientContext;
        SSLSocket client = ((SSLSocket) (clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                server.startHandshake();
                return null;
            }
        });
        executor.shutdown();
        client.startHandshake();
        future.get();
        client.close();
        server.close();
        c.close();
    }

    public void test_SSLSocket_HandshakeCompletedListener() throws Exception {
        final TestSSLContext c = TestSSLContext.create();
        final SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                server.startHandshake();
                return null;
            }
        });
        executor.shutdown();
        final boolean[] handshakeCompletedListenerCalled = new boolean[1];
        client.addHandshakeCompletedListener(new HandshakeCompletedListener() {
            public void handshakeCompleted(HandshakeCompletedEvent event) {
                try {
                    SSLSession session = event.getSession();
                    String cipherSuite = event.getCipherSuite();
                    Certificate[] localCertificates = event.getLocalCertificates();
                    Certificate[] peerCertificates = event.getPeerCertificates();
                    X509Certificate[] peerCertificateChain = event.getPeerCertificateChain();
                    Principal peerPrincipal = event.getPeerPrincipal();
                    Principal localPrincipal = event.getLocalPrincipal();
                    Socket socket = event.getSocket();
                    if (false) {
                        System.out.println(("Session=" + session));
                        System.out.println(("CipherSuite=" + cipherSuite));
                        System.out.println(("LocalCertificates=" + (Arrays.toString(localCertificates))));
                        System.out.println(("PeerCertificates=" + (Arrays.toString(peerCertificates))));
                        System.out.println(("PeerCertificateChain=" + (Arrays.toString(peerCertificateChain))));
                        System.out.println(("PeerPrincipal=" + peerPrincipal));
                        System.out.println(("LocalPrincipal=" + localPrincipal));
                        System.out.println(("Socket=" + socket));
                    }
                    TestCase.assertNotNull(session);
                    byte[] id = session.getId();
                    TestCase.assertNotNull(id);
                    TestCase.assertEquals(32, id.length);
                    TestCase.assertNotNull(c.clientContext.getClientSessionContext().getSession(id));
                    TestCase.assertNotNull(cipherSuite);
                    TestCase.assertTrue(Arrays.asList(client.getEnabledCipherSuites()).contains(cipherSuite));
                    TestCase.assertTrue(Arrays.asList(c.serverSocket.getEnabledCipherSuites()).contains(cipherSuite));
                    TestCase.assertNull(localCertificates);
                    TestCase.assertNotNull(peerCertificates);
                    TestKeyStore.assertChainLength(peerCertificates);
                    TestCase.assertNotNull(peerCertificates[0]);
                    TestSSLContext.assertServerCertificateChain(c.clientTrustManager, peerCertificates);
                    TestSSLContext.assertCertificateInKeyStore(peerCertificates[0], c.serverKeyStore);
                    TestCase.assertNotNull(peerCertificateChain);
                    TestKeyStore.assertChainLength(peerCertificateChain);
                    TestCase.assertNotNull(peerCertificateChain[0]);
                    TestSSLContext.assertCertificateInKeyStore(peerCertificateChain[0].getSubjectDN(), c.serverKeyStore);
                    TestCase.assertNotNull(peerPrincipal);
                    TestSSLContext.assertCertificateInKeyStore(peerPrincipal, c.serverKeyStore);
                    TestCase.assertNull(localPrincipal);
                    TestCase.assertNotNull(socket);
                    TestCase.assertSame(client, socket);
                    synchronized(handshakeCompletedListenerCalled) {
                        handshakeCompletedListenerCalled[0] = true;
                        handshakeCompletedListenerCalled.notify();
                    }
                    handshakeCompletedListenerCalled[0] = true;
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        client.startHandshake();
        future.get();
        if (!(TestSSLContext.sslServerSocketSupportsSessionTickets())) {
            TestCase.assertNotNull(c.serverContext.getServerSessionContext().getSession(client.getSession().getId()));
        }
        synchronized(handshakeCompletedListenerCalled) {
            while (!(handshakeCompletedListenerCalled[0])) {
                handshakeCompletedListenerCalled.wait();
            } 
        }
        client.close();
        server.close();
        c.close();
    }

    private static final class TestUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        Throwable actualException;

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            TestCase.assertNull(actualException);
            actualException = ex;
        }
    }

    public void test_SSLSocket_HandshakeCompletedListener_RuntimeException() throws Exception {
        final Thread self = Thread.currentThread();
        final Thread.UncaughtExceptionHandler original = self.getUncaughtExceptionHandler();
        final RuntimeException expectedException = new RuntimeException("expected");
        final SSLSocketTest.TestUncaughtExceptionHandler test = new SSLSocketTest.TestUncaughtExceptionHandler();
        self.setUncaughtExceptionHandler(test);
        final TestSSLContext c = TestSSLContext.create();
        final SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                server.startHandshake();
                return null;
            }
        });
        executor.shutdown();
        client.addHandshakeCompletedListener(new HandshakeCompletedListener() {
            public void handshakeCompleted(HandshakeCompletedEvent event) {
                throw expectedException;
            }
        });
        client.startHandshake();
        future.get();
        client.close();
        server.close();
        c.close();
        TestCase.assertSame(expectedException, test.actualException);
        self.setUncaughtExceptionHandler(original);
    }

    public void test_SSLSocket_getUseClientMode() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        TestCase.assertTrue(client.getUseClientMode());
        TestCase.assertFalse(server.getUseClientMode());
        client.close();
        server.close();
        c.close();
    }

    public void test_SSLSocket_setUseClientMode() throws Exception {
        // client is client, server is server
        test_SSLSocket_setUseClientMode(true, false);
        // client is server, server is client
        test_SSLSocket_setUseClientMode(true, false);
        // both are client
        try {
            test_SSLSocket_setUseClientMode(true, true);
            TestCase.fail();
        } catch (SSLProtocolException expected) {
            TestCase.assertTrue(IS_RI);
        } catch (SSLHandshakeException expected) {
            TestCase.assertFalse(IS_RI);
        }
        // both are server
        try {
            test_SSLSocket_setUseClientMode(false, false);
            TestCase.fail();
        } catch (SocketTimeoutException expected) {
        }
    }

    public void test_SSLSocket_setUseClientMode_afterHandshake() throws Exception {
        // can't set after handshake
        TestSSLEnginePair pair = TestSSLEnginePair.create(null);
        try {
            pair.server.setUseClientMode(false);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            pair.client.setUseClientMode(false);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void test_SSLSocket_untrustedServer() throws Exception {
        TestSSLContext c = TestSSLContext.create(TestKeyStore.getClientCA2(), TestKeyStore.getServer());
        SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    server.startHandshake();
                    TestCase.assertFalse(IS_RI);
                } catch (SSLHandshakeException expected) {
                    TestCase.assertTrue(IS_RI);
                }
                return null;
            }
        });
        executor.shutdown();
        try {
            client.startHandshake();
            TestCase.fail();
        } catch (SSLHandshakeException expected) {
            TestCase.assertTrue(((expected.getCause()) instanceof CertificateException));
        }
        client.close();
        server.close();
        future.get();
    }

    public void test_SSLSocket_clientAuth() throws Exception {
        TestSSLContext c = TestSSLContext.create(TestKeyStore.getClientCertificate(), TestKeyStore.getServer());
        SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                TestCase.assertFalse(server.getWantClientAuth());
                TestCase.assertFalse(server.getNeedClientAuth());
                // confirm turning one on by itself
                server.setWantClientAuth(true);
                TestCase.assertTrue(server.getWantClientAuth());
                TestCase.assertFalse(server.getNeedClientAuth());
                // confirm turning setting on toggles the other
                server.setNeedClientAuth(true);
                TestCase.assertFalse(server.getWantClientAuth());
                TestCase.assertTrue(server.getNeedClientAuth());
                // confirm toggling back
                server.setWantClientAuth(true);
                TestCase.assertTrue(server.getWantClientAuth());
                TestCase.assertFalse(server.getNeedClientAuth());
                server.startHandshake();
                return null;
            }
        });
        executor.shutdown();
        client.startHandshake();
        TestCase.assertNotNull(client.getSession().getLocalCertificates());
        TestKeyStore.assertChainLength(client.getSession().getLocalCertificates());
        TestSSLContext.assertClientCertificateChain(c.clientTrustManager, client.getSession().getLocalCertificates());
        future.get();
        client.close();
        server.close();
        c.close();
    }

    public void test_SSLSocket_clientAuth_bogusAlias() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLContext clientContext = SSLContext.getInstance("TLS");
        X509KeyManager keyManager = new X509KeyManager() {
            @Override
            public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
                return "bogus";
            }

            @Override
            public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
                throw new AssertionError();
            }

            @Override
            public java.security.cert.X509Certificate[] getCertificateChain(String alias) {
                // return null for "bogus" alias
                return null;
            }

            @Override
            public String[] getClientAliases(String keyType, Principal[] issuers) {
                throw new AssertionError();
            }

            @Override
            public String[] getServerAliases(String keyType, Principal[] issuers) {
                throw new AssertionError();
            }

            @Override
            public PrivateKey getPrivateKey(String alias) {
                // return null for "bogus" alias
                return null;
            }
        };
        clientContext.init(new KeyManager[]{ keyManager }, new TrustManager[]{ c.clientTrustManager }, null);
        SSLSocket client = ((SSLSocket) (clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    server.setNeedClientAuth(true);
                    server.startHandshake();
                    TestCase.fail();
                } catch (SSLHandshakeException expected) {
                }
                return null;
            }
        });
        executor.shutdown();
        try {
            client.startHandshake();
            TestCase.fail();
        } catch (SSLHandshakeException expected) {
            // before we would get a NullPointerException from passing
            // due to the null PrivateKey return by the X509KeyManager.
        }
        future.get();
        client.close();
        server.close();
        c.close();
    }

    public void test_SSLSocket_TrustManagerRuntimeException() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLContext clientContext = SSLContext.getInstance("TLS");
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                throw new AssertionError();
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                throw new RuntimeException();// throw a RuntimeException from custom TrustManager

            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                throw new AssertionError();
            }
        };
        clientContext.init(null, new TrustManager[]{ trustManager }, null);
        SSLSocket client = ((SSLSocket) (clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                server.startHandshake();
                return null;
            }
        });
        executor.shutdown();
        try {
            client.startHandshake();
            TestCase.fail();
        } catch (SSLHandshakeException expected) {
            // before we would get a RuntimeException from checkServerTrusted.
        }
        future.get();
        client.close();
        server.close();
        c.close();
    }

    public void test_SSLSocket_getEnableSessionCreation() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        TestCase.assertTrue(client.getEnableSessionCreation());
        TestCase.assertTrue(server.getEnableSessionCreation());
        client.close();
        server.close();
        c.close();
    }

    public void test_SSLSocket_setEnableSessionCreation_server() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                server.setEnableSessionCreation(false);
                try {
                    server.startHandshake();
                    TestCase.fail();
                } catch (SSLException expected) {
                }
                return null;
            }
        });
        executor.shutdown();
        try {
            client.startHandshake();
            TestCase.fail();
        } catch (SSLException expected) {
        }
        future.get();
        client.close();
        server.close();
        c.close();
    }

    public void test_SSLSocket_setEnableSessionCreation_client() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(c.host, c.port)));
        final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    server.startHandshake();
                    TestCase.fail();
                } catch (SSLException expected) {
                }
                return null;
            }
        });
        executor.shutdown();
        client.setEnableSessionCreation(false);
        try {
            client.startHandshake();
            TestCase.fail();
        } catch (SSLException expected) {
        }
        future.get();
        client.close();
        server.close();
        c.close();
    }

    public void test_SSLSocket_getSSLParameters() throws Exception {
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        SSLSocket ssl = ((SSLSocket) (sf.createSocket()));
        SSLParameters p = ssl.getSSLParameters();
        TestCase.assertNotNull(p);
        String[] cipherSuites = p.getCipherSuites();
        StandardNames.assertValidCipherSuites(CIPHER_SUITES, cipherSuites);
        TestCase.assertNotSame(cipherSuites, ssl.getEnabledCipherSuites());
        TestCase.assertEquals(Arrays.asList(cipherSuites), Arrays.asList(ssl.getEnabledCipherSuites()));
        String[] protocols = p.getProtocols();
        StandardNames.assertValidProtocols(SSL_SOCKET_PROTOCOLS, protocols);
        TestCase.assertNotSame(protocols, ssl.getEnabledProtocols());
        TestCase.assertEquals(Arrays.asList(protocols), Arrays.asList(ssl.getEnabledProtocols()));
        TestCase.assertEquals(p.getWantClientAuth(), ssl.getWantClientAuth());
        TestCase.assertEquals(p.getNeedClientAuth(), ssl.getNeedClientAuth());
    }

    public void test_SSLSocket_setSSLParameters() throws Exception {
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        SSLSocket ssl = ((SSLSocket) (sf.createSocket()));
        String[] defaultCipherSuites = ssl.getEnabledCipherSuites();
        String[] defaultProtocols = ssl.getEnabledProtocols();
        String[] supportedCipherSuites = ssl.getSupportedCipherSuites();
        String[] supportedProtocols = ssl.getSupportedProtocols();
        {
            SSLParameters p = new SSLParameters();
            ssl.setSSLParameters(p);
            TestCase.assertEquals(Arrays.asList(defaultCipherSuites), Arrays.asList(ssl.getEnabledCipherSuites()));
            TestCase.assertEquals(Arrays.asList(defaultProtocols), Arrays.asList(ssl.getEnabledProtocols()));
        }
        {
            SSLParameters p = new SSLParameters(supportedCipherSuites, supportedProtocols);
            ssl.setSSLParameters(p);
            TestCase.assertEquals(Arrays.asList(supportedCipherSuites), Arrays.asList(ssl.getEnabledCipherSuites()));
            TestCase.assertEquals(Arrays.asList(supportedProtocols), Arrays.asList(ssl.getEnabledProtocols()));
        }
        {
            SSLParameters p = new SSLParameters();
            p.setNeedClientAuth(true);
            TestCase.assertFalse(ssl.getNeedClientAuth());
            TestCase.assertFalse(ssl.getWantClientAuth());
            ssl.setSSLParameters(p);
            TestCase.assertTrue(ssl.getNeedClientAuth());
            TestCase.assertFalse(ssl.getWantClientAuth());
            p.setWantClientAuth(true);
            TestCase.assertTrue(ssl.getNeedClientAuth());
            TestCase.assertFalse(ssl.getWantClientAuth());
            ssl.setSSLParameters(p);
            TestCase.assertFalse(ssl.getNeedClientAuth());
            TestCase.assertTrue(ssl.getWantClientAuth());
            p.setWantClientAuth(false);
            TestCase.assertFalse(ssl.getNeedClientAuth());
            TestCase.assertTrue(ssl.getWantClientAuth());
            ssl.setSSLParameters(p);
            TestCase.assertFalse(ssl.getNeedClientAuth());
            TestCase.assertFalse(ssl.getWantClientAuth());
        }
    }

    public void test_SSLSocket_close() throws Exception {
        TestSSLSocketPair pair = TestSSLSocketPair.create();
        SSLSocket server = pair.server;
        SSLSocket client = pair.client;
        TestCase.assertFalse(server.isClosed());
        TestCase.assertFalse(client.isClosed());
        InputStream input = client.getInputStream();
        OutputStream output = client.getOutputStream();
        server.close();
        client.close();
        TestCase.assertTrue(server.isClosed());
        TestCase.assertTrue(client.isClosed());
        // close after close is okay...
        server.close();
        client.close();
        // ...so are a lot of other operations...
        HandshakeCompletedListener l = new HandshakeCompletedListener() {
            public void handshakeCompleted(HandshakeCompletedEvent e) {
            }
        };
        client.addHandshakeCompletedListener(l);
        TestCase.assertNotNull(client.getEnabledCipherSuites());
        TestCase.assertNotNull(client.getEnabledProtocols());
        client.getEnableSessionCreation();
        client.getNeedClientAuth();
        TestCase.assertNotNull(client.getSession());
        TestCase.assertNotNull(client.getSSLParameters());
        TestCase.assertNotNull(client.getSupportedProtocols());
        client.getUseClientMode();
        client.getWantClientAuth();
        client.removeHandshakeCompletedListener(l);
        client.setEnabledCipherSuites(new String[0]);
        client.setEnabledProtocols(new String[0]);
        client.setEnableSessionCreation(false);
        client.setNeedClientAuth(false);
        client.setSSLParameters(client.getSSLParameters());
        client.setWantClientAuth(false);
        // ...but some operations are expected to give SocketException...
        try {
            client.startHandshake();
            TestCase.fail();
        } catch (SocketException expected) {
        }
        try {
            client.getInputStream();
            TestCase.fail();
        } catch (SocketException expected) {
        }
        try {
            client.getOutputStream();
            TestCase.fail();
        } catch (SocketException expected) {
        }
        try {
            input.read();
            TestCase.fail();
        } catch (SocketException expected) {
        }
        try {
            input.read(null, (-1), (-1));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertTrue(IS_RI);
        } catch (SocketException expected) {
            TestCase.assertFalse(IS_RI);
        }
        try {
            output.write((-1));
            TestCase.fail();
        } catch (SocketException expected) {
        }
        try {
            output.write(null, (-1), (-1));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertTrue(IS_RI);
        } catch (SocketException expected) {
            TestCase.assertFalse(IS_RI);
        }
        // ... and one gives IllegalArgumentException
        try {
            client.setUseClientMode(false);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        pair.close();
    }

    /**
     * b/3350645 Test to confirm that an SSLSocket.close() performing
     * an SSL_shutdown does not throw an IOException if the peer
     * socket has been closed.
     */
    public void test_SSLSocket_shutdownCloseOnClosedPeer() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        final Socket underlying = new Socket(c.host, c.port);
        final SSLSocket wrapping = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(underlying, c.host.getHostName(), c.port, false)));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> clientFuture = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                wrapping.startHandshake();
                wrapping.getOutputStream().write(42);
                // close the underlying socket,
                // so that no SSL shutdown is sent
                underlying.close();
                wrapping.close();
                return null;
            }
        });
        executor.shutdown();
        SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        server.startHandshake();
        server.getInputStream().read();
        // wait for thread to finish so we know client is closed.
        clientFuture.get();
        // close should cause an SSL_shutdown which will fail
        // because the peer has closed, but it shouldn't throw.
        server.close();
    }

    public void test_SSLSocket_setSoTimeout_basic() throws Exception {
        ServerSocket listening = new ServerSocket(0);
        Socket underlying = new Socket(listening.getInetAddress(), listening.getLocalPort());
        TestCase.assertEquals(0, underlying.getSoTimeout());
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        Socket wrapping = sf.createSocket(underlying, null, (-1), false);
        TestCase.assertEquals(0, wrapping.getSoTimeout());
        // setting wrapper sets underlying and ...
        int expectedTimeoutMillis = 1000;// 10 was too small because it was affected by rounding

        wrapping.setSoTimeout(expectedTimeoutMillis);
        TestCase.assertEquals(expectedTimeoutMillis, wrapping.getSoTimeout());
        TestCase.assertEquals(expectedTimeoutMillis, underlying.getSoTimeout());
        // ... getting wrapper inspects underlying
        underlying.setSoTimeout(0);
        TestCase.assertEquals(0, wrapping.getSoTimeout());
        TestCase.assertEquals(0, underlying.getSoTimeout());
    }

    public void test_SSLSocket_setSoTimeout_wrapper() throws Exception {
        if (StandardNames.IS_RI) {
            // RI cannot handle this case
            return;
        }
        ServerSocket listening = new ServerSocket(0);
        // setSoTimeout applies to read, not connect, so connect first
        Socket underlying = new Socket(listening.getInetAddress(), listening.getLocalPort());
        Socket server = listening.accept();
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        Socket clientWrapping = sf.createSocket(underlying, null, (-1), false);
        underlying.setSoTimeout(1);
        try {
            clientWrapping.getInputStream().read();
            TestCase.fail();
        } catch (SocketTimeoutException expected) {
        }
        clientWrapping.close();
        server.close();
        underlying.close();
        listening.close();
    }

    public void test_SSLSocket_setSoWriteTimeout() throws Exception {
        if (StandardNames.IS_RI) {
            // RI does not support write timeout on sockets
            return;
        }
        final TestSSLContext c = TestSSLContext.create();
        SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket()));
        // Try to make the client SO_SNDBUF size as small as possible
        // (it can default to 512k or even megabytes).  Note that
        // socket(7) says that the kernel will double the request to
        // leave room for its own book keeping and that the minimal
        // value will be 2048. Also note that tcp(7) says the value
        // needs to be set before connect(2).
        int sendBufferSize = 1024;
        client.setSendBufferSize(sendBufferSize);
        sendBufferSize = client.getSendBufferSize();
        // In jb-mr2 it was found that we need to also set SO_RCVBUF
        // to a minimal size or the write would not block. While
        // tcp(2) says the value has to be set before listen(2), it
        // seems fine to set it before accept(2).
        final int recvBufferSize = 128;
        c.serverSocket.setReceiveBufferSize(recvBufferSize);
        client.connect(new InetSocketAddress(c.host, c.port));
        final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                server.startHandshake();
                return null;
            }
        });
        executor.shutdown();
        client.startHandshake();
        // Reflection is used so this can compile on the RI
        String expectedClassName = "com.android.org.conscrypt.OpenSSLSocketImpl";
        Class actualClass = client.getClass();
        TestCase.assertEquals(expectedClassName, actualClass.getName());
        Method setSoWriteTimeout = actualClass.getMethod("setSoWriteTimeout", new Class[]{ Integer.TYPE });
        setSoWriteTimeout.invoke(client, 1);
        try {
            // Add extra space to the write to exceed the send buffer
            // size and cause the write to block.
            final int extra = 1;
            client.getOutputStream().write(new byte[sendBufferSize + extra]);
            TestCase.fail();
        } catch (SocketTimeoutException expected) {
        }
        future.get();
        client.close();
        server.close();
        c.close();
    }

    public void test_SSLSocket_reusedNpnSocket() throws Exception {
        if (StandardNames.IS_RI) {
            // RI does not support NPN/ALPN
            return;
        }
        byte[] npnProtocols = new byte[]{ 8, 'h', 't', 't', 'p', '/', '1', '.', '1' };
        final TestSSLContext c = TestSSLContext.create();
        SSLSocket client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket()));
        // Reflection is used so this can compile on the RI
        String expectedClassName = "com.android.org.conscrypt.OpenSSLSocketImpl";
        Class<?> actualClass = client.getClass();
        TestCase.assertEquals(expectedClassName, actualClass.getName());
        Method setNpnProtocols = actualClass.getMethod("setNpnProtocols", byte[].class);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // First connection with NPN set on client and server
        {
            setNpnProtocols.invoke(client, npnProtocols);
            client.connect(new InetSocketAddress(c.host, c.port));
            final SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
            TestCase.assertEquals(expectedClassName, server.getClass().getName());
            setNpnProtocols.invoke(server, npnProtocols);
            Future<Void> future = executor.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    server.startHandshake();
                    return null;
                }
            });
            client.startHandshake();
            future.get();
            client.close();
            server.close();
        }
        // Second connection with client NPN already set on the SSL context, but
        // without server NPN set.
        {
            SSLServerSocket serverSocket = ((SSLServerSocket) (c.serverContext.getServerSocketFactory().createServerSocket(0)));
            InetAddress host = InetAddress.getLocalHost();
            int port = serverSocket.getLocalPort();
            client = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket()));
            client.connect(new InetSocketAddress(host, port));
            final SSLSocket server = ((SSLSocket) (serverSocket.accept()));
            Future<Void> future = executor.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    server.startHandshake();
                    return null;
                }
            });
            client.startHandshake();
            future.get();
            client.close();
            server.close();
            serverSocket.close();
        }
        c.close();
    }

    public void test_SSLSocket_interrupt() throws Exception {
        test_SSLSocket_interrupt_case(true, true);
        test_SSLSocket_interrupt_case(true, false);
        test_SSLSocket_interrupt_case(false, true);
        // Currently failing due to reader blocking closing thread http://b/10681815
        if (StandardNames.IS_RI) {
            test_SSLSocket_interrupt_case(false, false);
        }
    }

    /**
     * b/7014266 Test to confirm that an SSLSocket.close() on one
     * thread will interupt another thread blocked reading on the same
     * socket.
     */
    public void test_SSLSocket_interrupt_read() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        final Socket underlying = new Socket(c.host, c.port);
        final SSLSocket wrapping = ((SSLSocket) (c.clientContext.getSocketFactory().createSocket(underlying, c.host.getHostName(), c.port, false)));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> clientFuture = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    wrapping.startHandshake();
                    TestCase.assertFalse(IS_RI);
                    wrapping.setSoTimeout((5 * 1000));
                    TestCase.assertEquals((-1), wrapping.getInputStream().read());
                } catch (Exception e) {
                    TestCase.assertTrue(IS_RI);
                }
                return null;
            }
        });
        executor.shutdown();
        SSLSocket server = ((SSLSocket) (c.serverSocket.accept()));
        server.startHandshake();
        wrapping.close();
        clientFuture.get();
        server.close();
    }

    public void test_TestSSLSocketPair_create() {
        TestSSLSocketPair test = TestSSLSocketPair.create();
        TestCase.assertNotNull(test.c);
        TestCase.assertNotNull(test.server);
        TestCase.assertNotNull(test.client);
        TestCase.assertTrue(test.server.isConnected());
        TestCase.assertTrue(test.client.isConnected());
        TestCase.assertFalse(test.server.isClosed());
        TestCase.assertFalse(test.client.isClosed());
        TestCase.assertNotNull(test.server.getSession());
        TestCase.assertNotNull(test.client.getSession());
        TestCase.assertTrue(test.server.getSession().isValid());
        TestCase.assertTrue(test.client.getSession().isValid());
        test.close();
    }
}

