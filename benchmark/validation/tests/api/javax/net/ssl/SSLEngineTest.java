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
package tests.api.javax.net.ssl;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.Pipe;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import junit.framework.TestCase;

import static javax.net.ssl.SSLEngineResult.HandshakeStatus.FINISHED;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_TASK;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_WRAP;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;


/**
 * Tests for SSLEngine class
 */
public class SSLEngineTest extends TestCase {
    private SSLEngineTest.HandshakeHandler clientEngine;

    private SSLEngineTest.HandshakeHandler serverEngine;

    /**
     * Test for <code>SSLEngine()</code> constructor Assertion: creates
     * SSLEngine object with null host and -1 port
     *
     * @throws NoSuchAlgorithmException
     * 		
     */
    public void test_Constructor() throws NoSuchAlgorithmException {
        SSLEngine e = getEngine();
        TestCase.assertNull(e.getPeerHost());
        TestCase.assertEquals((-1), e.getPeerPort());
        String[] suites = e.getSupportedCipherSuites();
        e.setEnabledCipherSuites(suites);
        TestCase.assertEquals(e.getEnabledCipherSuites().length, suites.length);
    }

    /**
     * Test for <code>SSLEngine(String host, int port)</code> constructor
     *
     * @throws NoSuchAlgorithmException
     * 		
     */
    public void test_ConstructorLjava_lang_StringI01() throws NoSuchAlgorithmException {
        int port = 1010;
        SSLEngine e = getEngine(null, port);
        TestCase.assertNull(e.getPeerHost());
        TestCase.assertEquals(e.getPeerPort(), port);
        try {
            e.beginHandshake();
        } catch (IllegalStateException ex) {
            // expected
        } catch (SSLException ex) {
            TestCase.fail("unexpected SSLException was thrown.");
        }
        e = getEngine(null, port);
        e.setUseClientMode(true);
        try {
            e.beginHandshake();
        } catch (SSLException ex) {
            // expected
        }
        e = getEngine(null, port);
        e.setUseClientMode(false);
        try {
            e.beginHandshake();
        } catch (SSLException ex) {
            // expected
        }
    }

    /**
     * Test for <code>SSLEngine(String host, int port)</code> constructor
     *
     * @throws NoSuchAlgorithmException
     * 		
     */
    public void test_ConstructorLjava_lang_StringI02() throws NoSuchAlgorithmException {
        String host = "new host";
        int port = 8080;
        SSLEngine e = getEngine(host, port);
        TestCase.assertEquals(e.getPeerHost(), host);
        TestCase.assertEquals(e.getPeerPort(), port);
        String[] suites = e.getSupportedCipherSuites();
        e.setEnabledCipherSuites(suites);
        TestCase.assertEquals(e.getEnabledCipherSuites().length, suites.length);
        e.setUseClientMode(true);
        TestCase.assertTrue(e.getUseClientMode());
    }

    /**
     * Test for <code>getPeerHost()</code> method
     *
     * @throws NoSuchAlgorithmException
     * 		
     */
    public void test_getPeerHost() throws NoSuchAlgorithmException {
        SSLEngine e = getEngine();
        TestCase.assertNull(e.getPeerHost());
        e = getEngine("www.fortify.net", 80);
        TestCase.assertEquals("Incorrect host name", "www.fortify.net", e.getPeerHost());
    }

    /**
     * Test for <code>getPeerPort()</code> method
     *
     * @throws NoSuchAlgorithmException
     * 		
     */
    public void test_getPeerPort() throws NoSuchAlgorithmException {
        SSLEngine e = getEngine();
        TestCase.assertEquals("Incorrect default value of peer port", (-1), e.getPeerPort());
        e = getEngine("www.fortify.net", 80);
        TestCase.assertEquals("Incorrect peer port", 80, e.getPeerPort());
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
    javax.net.ssl.SSLEngine#getSupportedProtocols()
     * 		
     */
    public void test_getSupportedProtocols() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        try {
            String[] res = sse.getSupportedProtocols();
            TestCase.assertNotNull(res);
            TestCase.assertTrue(((res.length) > 0));
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception " + ex));
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
     * 		javax.net.ssl.SSLEngine#setEnabledProtocols(String[] protocols)
     * 		javax.net.ssl.SSLEngine#getEnabledProtocols()
     */
    public void test_EnabledProtocols() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        String[] pr = sse.getSupportedProtocols();
        try {
            sse.setEnabledProtocols(pr);
            String[] res = sse.getEnabledProtocols();
            TestCase.assertNotNull("Null array was returned", res);
            TestCase.assertEquals("Incorrect array length", res.length, pr.length);
            TestCase.assertTrue("Incorrect array was returned", Arrays.equals(res, pr));
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception " + ex));
        }
        try {
            sse.setEnabledProtocols(null);
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
    javax.net.ssl.SSLEngine#getSupportedCipherSuites()
     * 		
     */
    public void test_getSupportedCipherSuites() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        try {
            String[] res = sse.getSupportedCipherSuites();
            TestCase.assertNotNull(res);
            TestCase.assertTrue(((res.length) > 0));
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception " + ex));
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
     * 		javax.net.ssl.SSLEngine#setEnabledCipherSuites(String[] suites)
     * 		javax.net.ssl.SSLEngine#getEnabledCipherSuites()
     */
    public void test_EnabledCipherSuites() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        String[] st = sse.getSupportedCipherSuites();
        try {
            sse.setEnabledCipherSuites(st);
            String[] res = sse.getEnabledCipherSuites();
            TestCase.assertNotNull("Null array was returned", res);
            TestCase.assertEquals("Incorrect array length", res.length, st.length);
            TestCase.assertTrue("Incorrect array was returned", Arrays.equals(res, st));
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception " + ex));
        }
        try {
            sse.setEnabledCipherSuites(null);
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
     * 		javax.net.ssl.SSLEngine#setEnableSessionCreation(boolean flag)
     * 		javax.net.ssl.SSLEngine#getEnableSessionCreation()
     */
    public void test_EnableSessionCreation() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        try {
            TestCase.assertTrue(sse.getEnableSessionCreation());
            sse.setEnableSessionCreation(false);
            TestCase.assertFalse(sse.getEnableSessionCreation());
            sse.setEnableSessionCreation(true);
            TestCase.assertTrue(sse.getEnableSessionCreation());
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception " + ex));
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
     * 		javax.net.ssl.SSLEngine#setNeedClientAuth(boolean need)
     * 		javax.net.ssl.SSLEngine#getNeedClientAuth()
     */
    public void test_NeedClientAuth() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        try {
            sse.setNeedClientAuth(false);
            TestCase.assertFalse(sse.getNeedClientAuth());
            sse.setNeedClientAuth(true);
            TestCase.assertTrue(sse.getNeedClientAuth());
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception " + ex));
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
     * 		javax.net.ssl.SSLEngine#setWantClientAuth(boolean want)
     * 		javax.net.ssl.SSLEngine#getWantClientAuth()
     */
    public void test_WantClientAuth() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        try {
            sse.setWantClientAuth(false);
            TestCase.assertFalse(sse.getWantClientAuth());
            sse.setWantClientAuth(true);
            TestCase.assertTrue(sse.getWantClientAuth());
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception " + ex));
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
    javax.net.ssl.SSLEngine#beginHandshake()
     * 		
     */
    public void test_beginHandshake() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        try {
            sse.beginHandshake();
            TestCase.fail("IllegalStateException wasn't thrown");
        } catch (IllegalStateException se) {
            // expected
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of IllegalStateException"));
        }
        sse = getEngine("new host", 1080);
        try {
            sse.beginHandshake();
            TestCase.fail("IllegalStateException wasn't thrown");
        } catch (IllegalStateException ise) {
            // expected
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of IllegalStateException"));
        }
        sse = getEngine();
        try {
            sse.setUseClientMode(true);
            sse.beginHandshake();
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception " + ex));
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
    javax.net.ssl.SSLEngine#getSession()
     * 		
     */
    public void test_getSession() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        try {
            TestCase.assertNotNull(sse.getSession());
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception " + ex));
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
    javax.net.ssl.SSLEngine#getHandshakeStatus()
     * 		
     */
    public void test_getHandshakeStatus() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        try {
            TestCase.assertEquals(sse.getHandshakeStatus().toString(), "NOT_HANDSHAKING");
            sse.setUseClientMode(true);
            sse.beginHandshake();
            TestCase.assertEquals(sse.getHandshakeStatus().toString(), "NEED_WRAP");
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception " + ex));
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		javax.net.ssl.SSLEngine#unwrap(ByteBuffer src, ByteBuffer[] dsts,
     * 		int offset, int length)
     * 		Exception case: SSLException should be thrown.
     */
    public void test_unwrap_01() throws IOException, InterruptedException {
        prepareEngines();
        doHandshake();
        ByteBuffer bbs = ByteBuffer.wrap(new byte[]{ 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 31, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3 });
        ByteBuffer bbd = ByteBuffer.allocate(100);
        try {
            clientEngine.engine.unwrap(bbs, new ByteBuffer[]{ bbd }, 0, 1);
            TestCase.fail("SSLException wasn't thrown");
        } catch (SSLException ex) {
            // expected
        }
    }

    /**
     * javax.net.ssl.SSLEngine#unwrap(ByteBuffer src, ByteBuffer[] dsts,
     *                                       int offset, int length)
     */
    public void test_unwrap_06() {
        String host = "new host";
        int port = 8080;
        ByteBuffer[] bbA = new ByteBuffer[]{ ByteBuffer.allocate(100), ByteBuffer.allocate(10), ByteBuffer.allocate(100) };
        ByteBuffer bb = ByteBuffer.allocate(10);
        SSLEngine sse = getEngine(host, port);
        sse.setUseClientMode(true);
        try {
            SSLEngineResult res = sse.unwrap(bb, bbA, 0, bbA.length);
            TestCase.assertEquals(0, res.bytesConsumed());
            TestCase.assertEquals(0, res.bytesProduced());
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception: " + ex));
        }
    }

    public void test_wrap_01() throws IOException, InterruptedException {
        prepareEngines();
        doHandshake();
        ByteBuffer bbs = ByteBuffer.allocate(100);
        ByteBuffer bbd = ByteBuffer.allocate(20000);
        clientEngine.engine.wrap(new ByteBuffer[]{ bbs }, 0, 1, bbd);
    }

    /**
     * javax.net.ssl.SSLEngine#wrap(ByteBuffer[] srcs, int offset,
     *                                     int length, ByteBuffer dst)
     * Exception case: ReadOnlyBufferException should be thrown.
     */
    public void test_wrap_03() throws SSLException {
        String host = "new host";
        int port = 8080;
        ByteBuffer bb = ByteBuffer.allocate(10).asReadOnlyBuffer();
        ByteBuffer[] bbA = new ByteBuffer[]{ ByteBuffer.allocate(5), ByteBuffer.allocate(10), ByteBuffer.allocate(5) };
        SSLEngine sse = getEngine(host, port);
        sse.setUseClientMode(true);
        try {
            sse.wrap(bbA, 0, bbA.length, bb);
            TestCase.fail("ReadOnlyBufferException wasn't thrown");
        } catch (ReadOnlyBufferException iobe) {
            // expected
        }
    }

    /**
     * javax.net.ssl.SSLEngine#wrap(ByteBuffer[] srcs, int offset,
     *                                     int length, ByteBuffer dst)
     */
    public void test_wrap_06() {
        String host = "new host";
        int port = 8080;
        ByteBuffer bb = ByteBuffer.allocate(10);
        ByteBuffer[] bbA = new ByteBuffer[]{ ByteBuffer.allocate(5), ByteBuffer.allocate(10), ByteBuffer.allocate(5) };
        SSLEngine sse = getEngine(host, port);
        sse.setUseClientMode(true);
        try {
            sse.wrap(bbA, 0, bbA.length, bb);
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception: " + ex));
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
    javax.net.ssl.SSLEngine#closeOutbound()
    javax.net.ssl.SSLEngine#isOutboundDone()
     * 		
     */
    public void test_closeOutbound() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        try {
            TestCase.assertFalse(sse.isOutboundDone());
            sse.closeOutbound();
            TestCase.assertTrue(sse.isOutboundDone());
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception: " + ex));
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
    javax.net.ssl.SSLEngine#closeInbound()
    javax.net.ssl.SSLEngine#isInboundDone()
     * 		
     */
    public void test_closeInbound() throws NoSuchAlgorithmException {
        SSLEngine sse = getEngine();
        try {
            TestCase.assertFalse(sse.isInboundDone());
            sse.closeInbound();
            TestCase.assertTrue(sse.isInboundDone());
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception: " + ex));
        }
    }

    /**
     * javax.net.ssl.SSLEngine#unwrap(ByteBuffer src, ByteBuffer dst)
     * SSLException should be thrown.
     */
    public void test_unwrap_ByteBuffer_ByteBuffer_01() throws IOException, InterruptedException {
        prepareEngines();
        doHandshake();
        ByteBuffer bbs = ByteBuffer.allocate(100);
        ByteBuffer bbd = ByteBuffer.allocate(100);
        try {
            SSLEngineResult unwrap = clientEngine.engine.unwrap(bbs, bbd);
            TestCase.fail("SSLException wasn't thrown");
        } catch (SSLException ex) {
            // expected
        }
    }

    /**
     * javax.net.ssl.SSLEngine#unwrap(ByteBuffer src, ByteBuffer dst)
     */
    public void test_unwrap_ByteBuffer_ByteBuffer_05() {
        String host = "new host";
        int port = 8080;
        ByteBuffer bbs = ByteBuffer.allocate(10);
        ByteBuffer bbd = ByteBuffer.allocate(100);
        SSLEngine sse = getEngine(host, port);
        sse.setUseClientMode(true);
        try {
            SSLEngineResult res = sse.unwrap(bbs, bbd);
            TestCase.assertEquals(0, res.bytesConsumed());
            TestCase.assertEquals(0, res.bytesProduced());
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
    }

    /**
     * javax.net.ssl.SSLEngine#unwrap(ByteBuffer src, ByteBuffer[] dsts)
     * SSLException should be thrown.
     */
    public void test_unwrap_ByteBuffer$ByteBuffer_01() throws IOException, InterruptedException {
        prepareEngines();
        doHandshake();
        ByteBuffer bbs = ByteBuffer.allocate(100);
        ByteBuffer bbd = ByteBuffer.allocate(100);
        try {
            clientEngine.engine.unwrap(bbs, new ByteBuffer[]{ bbd });
            TestCase.fail("SSLException wasn't thrown");
        } catch (SSLException ex) {
            // expected
        }
    }

    /**
     * javax.net.ssl.SSLEngine#unwrap(ByteBuffer src, ByteBuffer[] dsts)
     */
    public void test_unwrap_ByteBuffer$ByteBuffer_05() {
        String host = "new host";
        int port = 8080;
        ByteBuffer bbs = ByteBuffer.allocate(10);
        ByteBuffer[] bbd = new ByteBuffer[]{ ByteBuffer.allocate(100), ByteBuffer.allocate(10), ByteBuffer.allocate(100) };
        SSLEngine sse = getEngine(host, port);
        sse.setUseClientMode(true);
        try {
            SSLEngineResult res = sse.unwrap(bbs, bbd);
            TestCase.assertEquals(0, res.bytesConsumed());
            TestCase.assertEquals(0, res.bytesProduced());
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception: " + ex));
        }
    }

    public void test_wrap_ByteBuffer_ByteBuffer_01() throws IOException, InterruptedException {
        prepareEngines();
        doHandshake();
        ByteBuffer bbs = ByteBuffer.allocate(20);
        ByteBuffer bbd = ByteBuffer.allocate(20000);
        clientEngine.engine.wrap(bbs, bbd);
    }

    /**
     * javax.net.ssl.SSLEngine#wrap(ByteBuffer src, ByteBuffer dst)
     * ReadOnlyBufferException should be thrown.
     */
    public void test_wrap_ByteBuffer_ByteBuffer_02() {
        String host = "new host";
        int port = 8080;
        ByteBuffer bbs = ByteBuffer.allocate(10);
        ByteBuffer bbd = ByteBuffer.allocate(100).asReadOnlyBuffer();
        SSLEngine sse = getEngine(host, port);
        sse.setUseClientMode(true);
        try {
            sse.wrap(bbs, bbd);
            TestCase.fail("ReadOnlyBufferException wasn't thrown");
        } catch (ReadOnlyBufferException iobe) {
            // expected
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of ReadOnlyBufferException"));
        }
    }

    /**
     * javax.net.ssl.SSLEngine#wrap(ByteBuffer src, ByteBuffer dst)
     */
    public void test_wrap_ByteBuffer_ByteBuffer_05() {
        String host = "new host";
        int port = 8080;
        ByteBuffer bb = ByteBuffer.allocate(10);
        SSLEngine sse = getEngine(host, port);
        sse.setUseClientMode(true);
        try {
            SSLEngineResult res = sse.wrap(bb, ByteBuffer.allocate(10));
            TestCase.assertEquals(0, res.bytesConsumed());
            TestCase.assertEquals(0, res.bytesProduced());
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
    }

    /**
     *
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		javax.net.ssl.SSLEngine#wrap(ByteBuffer[] srcs, ByteBuffer dst)
     * 		SSLException should be thrown.
     */
    public void test_wrap_ByteBuffer$ByteBuffer_01() throws IOException, InterruptedException {
        prepareEngines();
        doHandshake();
        ByteBuffer bbs = ByteBuffer.allocate(100);
        ByteBuffer bbd = ByteBuffer.allocate(20000);
        try {
            clientEngine.engine.wrap(new ByteBuffer[]{ bbs }, bbd);
            serverEngine.engine.wrap(new ByteBuffer[]{ bbs }, bbd);
            // fail("SSLException wasn't thrown");
        } catch (SSLException ex) {
            // expected
        }
    }

    /**
     * javax.net.ssl.SSLEngine#wrap(ByteBuffer[] srcs, ByteBuffer dst)
     * ReadOnlyBufferException should be thrown.
     */
    public void test_wrap_ByteBuffer$ByteBuffer_02() {
        String host = "new host";
        int port = 8080;
        ByteBuffer bb = ByteBuffer.allocate(10).asReadOnlyBuffer();
        ByteBuffer[] bbA = new ByteBuffer[]{ ByteBuffer.allocate(5), ByteBuffer.allocate(10), ByteBuffer.allocate(5) };
        SSLEngine sse = getEngine(host, port);
        sse.setUseClientMode(true);
        try {
            sse.wrap(bbA, bb);
            TestCase.fail("ReadOnlyBufferException wasn't thrown");
        } catch (ReadOnlyBufferException iobe) {
            // expected
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of ReadOnlyBufferException"));
        }
    }

    /**
     * javax.net.ssl.SSLEngine#wrap(ByteBuffer[] srcs, ByteBuffer dst)
     */
    public void test_wrap_ByteBuffer$ByteBuffer_05() {
        String host = "new host";
        int port = 8080;
        ByteBuffer bb = ByteBuffer.allocate(10);
        ByteBuffer[] bbA = new ByteBuffer[]{ ByteBuffer.allocate(5), ByteBuffer.allocate(10), ByteBuffer.allocate(5) };
        SSLEngine sse = getEngine(host, port);
        sse.setUseClientMode(true);
        try {
            SSLEngineResult res = sse.wrap(bbA, bb);
            TestCase.assertEquals(0, res.bytesConsumed());
            TestCase.assertEquals(0, res.bytesProduced());
        } catch (Exception ex) {
            TestCase.fail(("Unexpected exception: " + ex));
        }
    }

    class HandshakeHandler implements Runnable {
        private final SSLEngine engine;

        private final Pipe.SourceChannel in;

        private final Pipe.SinkChannel out;

        private final ByteBuffer EMPTY = ByteBuffer.allocate(0);

        @SuppressWarnings("unused")
        private final String LOGTAG;

        private SSLEngineResult.HandshakeStatus status;

        private ByteBuffer readBuffer;

        private ByteBuffer writeBuffer;

        HandshakeHandler(boolean clientMode, Pipe.SourceChannel in, Pipe.SinkChannel out) throws SSLException {
            this.in = in;
            this.out = out;
            engine = getEngine();
            engine.setUseClientMode(clientMode);
            String[] cipherSuites = engine.getSupportedCipherSuites();
            Set<String> enabledSuites = new HashSet<String>();
            for (String cipherSuite : cipherSuites) {
                if (cipherSuite.contains("anon")) {
                    enabledSuites.add(cipherSuite);
                }
            }
            engine.setEnabledCipherSuites(((String[]) (enabledSuites.toArray(new String[enabledSuites.size()]))));
            engine.beginHandshake();
            status = engine.getHandshakeStatus();
            if (clientMode) {
                LOGTAG = "CLIENT: ";
            } else {
                LOGTAG = "SERVER: ";
            }
            log(("CipherSuites: " + (Arrays.toString(engine.getEnabledCipherSuites()))));
            log(status);
            readBuffer = ByteBuffer.allocate(200000);
            writeBuffer = ByteBuffer.allocate(20000);
        }

        public SSLEngineResult.HandshakeStatus getStatus() {
            return status;
        }

        private void log(Object o) {
            // System.out.print(LOGTAG);
            // System.out.println(o);
        }

        private ByteBuffer read() throws IOException {
            if ((((readBuffer) == null) || ((readBuffer.remaining()) == 0)) || ((readBuffer.position()) == 0)) {
                readBuffer.clear();
                int read = in.read(readBuffer);
                log(("read: " + read));
                readBuffer.rewind();
                readBuffer.limit(read);
            }
            return readBuffer;
        }

        public void run() {
            try {
                while (true) {
                    switch (status) {
                        case FINISHED :
                            {
                                log(status);
                                return;
                            }
                        case NEED_TASK :
                            {
                                log(status);
                                Runnable task;
                                while ((task = engine.getDelegatedTask()) != null) {
                                    task.run();
                                } 
                                status = engine.getHandshakeStatus();
                                break;
                            }
                        case NEED_UNWRAP :
                            {
                                log(status);
                                ByteBuffer source = read();
                                writeBuffer.clear();
                                while ((status) == (NEED_UNWRAP)) {
                                    SSLEngineResult result = engine.unwrap(source, writeBuffer);
                                    status = result.getHandshakeStatus();
                                    log(result);
                                } 
                                break;
                            }
                        case NEED_WRAP :
                            {
                                log(status);
                                writeBuffer.clear();
                                int produced = 0;
                                SSLEngineResult result = null;
                                while ((status) == (NEED_WRAP)) {
                                    result = engine.wrap(EMPTY, writeBuffer);
                                    status = result.getHandshakeStatus();
                                    produced += result.bytesProduced();
                                    log(result);
                                } 
                                writeBuffer.rewind();
                                writeBuffer.limit(produced);
                                log(("write: " + produced));
                                out.write(writeBuffer);
                                break;
                            }
                        case NOT_HANDSHAKING :
                            {
                                log("Not Handshaking");
                                return;
                            }
                    }
                } 
            } catch (IOException e) {
                log(e);
            } catch (RuntimeException e) {
                // ignore;
            }
        }
    }
}

