/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.rest.protocols.tcp;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.apache.ignite.internal.client.marshaller.GridClientMarshaller;
import org.apache.ignite.internal.client.marshaller.optimized.GridClientOptimizedMarshaller;
import org.apache.ignite.internal.processors.rest.client.message.GridClientCacheRequest;
import org.apache.ignite.internal.processors.rest.client.message.GridClientHandshakeRequest;
import org.apache.ignite.internal.processors.rest.client.message.GridClientMessage;
import org.apache.ignite.internal.util.nio.GridNioSession;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 * This class tests that parser confirms memcache extended specification.
 */
@SuppressWarnings("TypeMayBeWeakened")
public class TcpRestParserSelfTest extends GridCommonAbstractTest {
    /**
     * Marshaller.
     */
    private GridClientMarshaller marshaller = new GridClientOptimizedMarshaller();

    /**
     * Extras value.
     */
    public static final byte[] EXTRAS = new byte[]{ ((byte) (222)), 0, ((byte) (190)), 0// Flags, string encoding.
    , 0, 0, 0, 0// Expiration value.
     };

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimplePacketParsing() throws Exception {
        GridNioSession ses = new MockNioSession();
        GridTcpRestParser parser = new GridTcpRestParser(false);
        byte hdr = GridMemcachedMessage.MEMCACHE_REQ_FLAG;
        byte[] opCodes = new byte[]{ 1, 2, 3 };
        byte[] opaque = new byte[]{ 1, 2, 3, ((byte) (255)) };
        String key = "key";
        String val = "value";
        for (byte opCode : opCodes) {
            ByteBuffer raw = rawPacket(hdr, opCode, opaque, key.getBytes(), val.getBytes(), TcpRestParserSelfTest.EXTRAS);
            GridClientMessage msg = parser.decode(ses, raw);
            assertTrue((msg instanceof GridMemcachedMessage));
            GridMemcachedMessage packet = ((GridMemcachedMessage) (msg));
            assertEquals("Parser leaved unparsed bytes", 0, raw.remaining());
            assertEquals("Invalid opcode", opCode, packet.operationCode());
            assertEquals("Invalid key", key, packet.key());
            assertEquals("Invalid value", val, packet.value());
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testIncorrectPackets() throws Exception {
        final GridNioSession ses = new MockNioSession();
        final GridTcpRestParser parser = new GridTcpRestParser(false);
        final byte[] opaque = new byte[]{ 1, 2, 3, ((byte) (255)) };
        final String key = "key";
        final String val = "value";
        GridTestUtils.assertThrows(log(), new Callable<Object>() {
            @Nullable
            @Override
            public Object call() throws Exception {
                parser.decode(ses, rawPacket(((byte) (1)), ((byte) (1)), opaque, key.getBytes(), val.getBytes(), TcpRestParserSelfTest.EXTRAS));
                return null;
            }
        }, IOException.class, null);
        GridTestUtils.assertThrows(log(), new Callable<Object>() {
            @Nullable
            @Override
            public Object call() throws Exception {
                parser.decode(ses, rawPacket(GridMemcachedMessage.MEMCACHE_REQ_FLAG, ((byte) (1)), opaque, key.getBytes(), val.getBytes(), null));
                return null;
            }
        }, IOException.class, null);
        GridTestUtils.assertThrows(log(), new Callable<Object>() {
            @Nullable
            @Override
            public Object call() throws Exception {
                ByteBuffer fake = ByteBuffer.allocate(21);
                fake.put(GridMemcachedMessage.IGNITE_REQ_FLAG);
                fake.put(U.intToBytes((-5)));
                fake.put(U.longToBytes(0));
                fake.put(U.longToBytes(0));
                fake.flip();
                parser.decode(ses, fake);
                return null;
            }
        }, IOException.class, null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCustomMessages() throws Exception {
        GridClientCacheRequest req = new GridClientCacheRequest(CAS);
        req.key("key");
        req.value(1);
        req.value2(2);
        req.clientId(UUID.randomUUID());
        ByteBuffer raw = clientRequestPacket(req);
        GridNioSession ses = new MockNioSession();
        ses.addMeta(MARSHALLER.ordinal(), new GridClientOptimizedMarshaller());
        GridTcpRestParser parser = new GridTcpRestParser(false);
        GridClientMessage msg = parser.decode(ses, raw);
        assertNotNull(msg);
        assertEquals("Parser leaved unparsed bytes", 0, raw.remaining());
        assertTrue((msg instanceof GridClientCacheRequest));
        GridClientCacheRequest res = ((GridClientCacheRequest) (msg));
        assertEquals("Invalid operation", req.operation(), res.operation());
        assertEquals("Invalid clientId", req.clientId(), res.clientId());
        assertEquals("Invalid key", req.key(), res.key());
        assertEquals("Invalid value 1", req.value(), res.value());
        assertEquals("Invalid value 2", req.value2(), res.value2());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMixedParsing() throws Exception {
        GridNioSession ses1 = new MockNioSession();
        GridNioSession ses2 = new MockNioSession();
        ses1.addMeta(MARSHALLER.ordinal(), new GridClientOptimizedMarshaller());
        ses2.addMeta(MARSHALLER.ordinal(), new GridClientOptimizedMarshaller());
        GridTcpRestParser parser = new GridTcpRestParser(false);
        GridClientCacheRequest req = new GridClientCacheRequest(CAS);
        req.key("key");
        String val = "value";
        req.value(val);
        req.value2(val);
        req.clientId(UUID.randomUUID());
        byte[] opaque = new byte[]{ 1, 2, 3, ((byte) (255)) };
        String key = "key";
        ByteBuffer raw1 = rawPacket(GridMemcachedMessage.MEMCACHE_REQ_FLAG, ((byte) (1)), opaque, key.getBytes(), val.getBytes(), TcpRestParserSelfTest.EXTRAS);
        ByteBuffer raw2 = clientRequestPacket(req);
        raw1.mark();
        raw2.mark();
        int splits = Math.min(raw1.remaining(), raw2.remaining());
        for (int i = 1; i < splits; i++) {
            ByteBuffer[] packet1 = split(raw1, i);
            ByteBuffer[] packet2 = split(raw2, i);
            GridClientMessage msg = parser.decode(ses1, packet1[0]);
            assertNull(msg);
            msg = parser.decode(ses2, packet2[0]);
            assertNull(msg);
            msg = parser.decode(ses1, packet1[1]);
            assertTrue((msg instanceof GridMemcachedMessage));
            assertEquals(key, key());
            assertEquals(val, value());
            msg = parser.decode(ses2, packet2[1]);
            assertTrue((msg instanceof GridClientCacheRequest));
            assertEquals(val, value());
            assertEquals(val, value2());
            raw1.reset();
            raw2.reset();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testParseContinuousSplit() throws Exception {
        ByteBuffer tmp = ByteBuffer.allocate((10 * 1024));
        GridClientCacheRequest req = new GridClientCacheRequest(CAS);
        req.key("key");
        req.value(1);
        req.value2(2);
        req.clientId(UUID.randomUUID());
        for (int i = 0; i < 5; i++)
            tmp.put(clientRequestPacket(req));

        tmp.flip();
        for (int splitPos = 0; splitPos < (tmp.remaining()); splitPos++) {
            ByteBuffer[] split = split(tmp, splitPos);
            tmp.flip();
            GridNioSession ses = new MockNioSession();
            ses.addMeta(MARSHALLER.ordinal(), new GridClientOptimizedMarshaller());
            GridTcpRestParser parser = new GridTcpRestParser(false);
            Collection<GridClientCacheRequest> lst = new ArrayList<>(5);
            for (ByteBuffer buf : split) {
                GridClientCacheRequest r;
                while ((buf.hasRemaining()) && ((r = ((GridClientCacheRequest) (parser.decode(ses, buf)))) != null))
                    lst.add(r);

                assertTrue("Parser has left unparsed bytes.", ((buf.remaining()) == 0));
            }
            assertEquals(5, lst.size());
            for (GridClientCacheRequest res : lst) {
                assertEquals("Invalid operation", req.operation(), res.operation());
                assertEquals("Invalid clientId", req.clientId(), res.clientId());
                assertEquals("Invalid key", req.key(), res.key());
                assertEquals("Invalid value 1", req.value(), res.value());
                assertEquals("Invalid value 2", req.value2(), res.value2());
            }
        }
    }

    /**
     * Tests correct parsing of client handshake packets.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testParseClientHandshake() throws Exception {
        for (int splitPos = 1; splitPos < 5; splitPos++) {
            log.info(("Checking split position: " + splitPos));
            ByteBuffer tmp = clientHandshakePacket();
            ByteBuffer[] split = split(tmp, splitPos);
            GridNioSession ses = new MockNioSession();
            ses.addMeta(MARSHALLER.ordinal(), new GridClientOptimizedMarshaller());
            GridTcpRestParser parser = new GridTcpRestParser(false);
            Collection<GridClientMessage> lst = new ArrayList<>(1);
            for (ByteBuffer buf : split) {
                GridClientMessage r;
                while ((buf.hasRemaining()) && ((r = parser.decode(ses, buf)) != null))
                    lst.add(r);

                assertTrue("Parser has left unparsed bytes.", ((buf.remaining()) == 0));
            }
            assertEquals(1, lst.size());
            GridClientHandshakeRequest req = ((GridClientHandshakeRequest) (F.first(lst)));
            assertNotNull(req);
            assertEquals(U.bytesToShort(new byte[]{ 5, 0 }, 0), req.version());
        }
    }
}

