/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro;


import Kind.BAR;
import Protocol.Message;
import Schema.Field;
import Schema.Type.BOOLEAN;
import Schema.Type.STRING;
import Simple.PROTOCOL;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.ipc.HttpTransceiver;
import org.apache.avro.ipc.RPCContext;
import org.apache.avro.ipc.RPCPlugin;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.SocketServer;
import org.apache.avro.ipc.SocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.generic.GenericRequestor;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.test.MD5;
import org.apache.avro.test.Simple;
import org.apache.avro.test.TestError;
import org.apache.avro.test.TestRecord;
import org.junit.Assert;
import org.junit.Test;


public class TestProtocolSpecific {
    protected static final int REPEATING = -1;

    public static int ackCount;

    private static boolean throwUndeclaredError;

    public static class TestImpl implements Simple {
        public String hello(String greeting) {
            return "goodbye";
        }

        public int add(int arg1, int arg2) {
            return arg1 + arg2;
        }

        public TestRecord echo(TestRecord record) {
            return record;
        }

        public ByteBuffer echoBytes(ByteBuffer data) {
            return data;
        }

        public void error() throws AvroRemoteException {
            if (TestProtocolSpecific.throwUndeclaredError)
                throw new RuntimeException("foo");

            throw TestError.newBuilder().setMessage$("an error").build();
        }

        public void ack() {
            (TestProtocolSpecific.ackCount)++;
        }
    }

    protected static Server server;

    protected static Transceiver client;

    protected static Simple proxy;

    protected static SpecificResponder responder;

    protected static TestProtocolSpecific.HandshakeMonitor monitor;

    @Test
    public void testClassLoader() throws Exception {
        ClassLoader loader = new ClassLoader() {};
        SpecificResponder responder = new SpecificResponder(Simple.class, new TestProtocolSpecific.TestImpl(), new SpecificData(loader));
        Assert.assertEquals(responder.getSpecificData().getClassLoader(), loader);
        SpecificRequestor requestor = new SpecificRequestor(Simple.class, TestProtocolSpecific.client, new SpecificData(loader));
        Assert.assertEquals(requestor.getSpecificData().getClassLoader(), loader);
    }

    @Test
    public void testGetRemote() throws IOException {
        Assert.assertEquals(PROTOCOL, SpecificRequestor.getRemote(TestProtocolSpecific.proxy));
    }

    @Test
    public void testHello() throws IOException {
        String response = TestProtocolSpecific.proxy.hello("bob");
        Assert.assertEquals("goodbye", response);
    }

    @Test
    public void testHashCode() throws IOException {
        TestError error = new TestError();
        error.hashCode();
    }

    @Test
    public void testEcho() throws IOException {
        TestRecord record = new TestRecord();
        record.setName("foo");
        record.setKind(BAR);
        record.setHash(new MD5(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5 }));
        TestRecord echoed = TestProtocolSpecific.proxy.echo(record);
        Assert.assertEquals(record, echoed);
        Assert.assertEquals(record.hashCode(), echoed.hashCode());
    }

    @Test
    public void testAdd() throws IOException {
        int result = TestProtocolSpecific.proxy.add(1, 2);
        Assert.assertEquals(3, result);
    }

    @Test
    public void testEchoBytes() throws IOException {
        Random random = new Random();
        int length = random.nextInt((1024 * 16));
        ByteBuffer data = ByteBuffer.allocate(length);
        random.nextBytes(data.array());
        data.flip();
        ByteBuffer echoed = TestProtocolSpecific.proxy.echoBytes(data);
        Assert.assertEquals(data, echoed);
    }

    @Test
    public void testEmptyEchoBytes() throws IOException {
        ByteBuffer data = ByteBuffer.allocate(0);
        ByteBuffer echoed = TestProtocolSpecific.proxy.echoBytes(data);
        data.flip();
        Assert.assertEquals(data, echoed);
    }

    @Test
    public void testError() throws IOException {
        TestError error = null;
        try {
            TestProtocolSpecific.proxy.error();
        } catch (TestError e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals("an error", error.getMessage$());
    }

    @Test
    public void testUndeclaredError() throws Exception {
        this.throwUndeclaredError = true;
        RuntimeException error = null;
        try {
            TestProtocolSpecific.proxy.error();
        } catch (RuntimeException e) {
            error = e;
        } finally {
            this.throwUndeclaredError = false;
        }
        Assert.assertNotNull(error);
        Assert.assertTrue(error.toString().contains("foo"));
    }

    @Test
    public void testOneWay() throws IOException {
        TestProtocolSpecific.ackCount = 0;
        TestProtocolSpecific.proxy.ack();
        TestProtocolSpecific.proxy.hello("foo");
        // intermix normal req
        TestProtocolSpecific.proxy.ack();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        Assert.assertEquals(2, TestProtocolSpecific.ackCount);
    }

    @Test
    public void testRepeatedAccess() throws Exception {
        for (int x = 0; x < 1000; x++) {
            TestProtocolSpecific.proxy.hello("hi!");
        }
    }

    @Test(expected = Exception.class)
    public void testConnectionRefusedOneWay() throws IOException {
        Transceiver client = new HttpTransceiver(new URL("http://localhost:4444"));
        SpecificRequestor req = new SpecificRequestor(Simple.class, client);
        addRpcPlugins(req);
        Simple proxy = SpecificRequestor.getClient(Simple.class, ((SpecificRequestor) (req)));
        proxy.ack();
    }

    /**
     * Construct and use a protocol whose "hello" method has an extra
     * argument to check that schema is sent to parse request.
     */
    @Test
    public void testParamVariation() throws Exception {
        Protocol protocol = new Protocol("Simple", "org.apache.avro.test");
        List<Schema.Field> fields = new ArrayList<>();
        fields.add(new Schema.Field("extra", Schema.create(BOOLEAN), null, null));
        fields.add(new Schema.Field("greeting", Schema.create(STRING), null, null));
        Protocol.Message message = /* doc */
        protocol.createMessage("hello", null, Schema.createRecord(fields), Schema.create(STRING), Schema.createUnion(new ArrayList()));
        protocol.getMessages().put("hello", message);
        Transceiver t = createTransceiver();
        try {
            GenericRequestor r = new GenericRequestor(protocol, t);
            addRpcPlugins(r);
            GenericRecord params = new org.apache.avro.generic.GenericData.Record(message.getRequest());
            params.put("extra", Boolean.TRUE);
            params.put("greeting", "bob");
            String response = r.request("hello", params).toString();
            Assert.assertEquals("goodbye", response);
        } finally {
            t.close();
        }
    }

    public class HandshakeMonitor extends RPCPlugin {
        private int handshakes;

        private HashSet<String> seenProtocols = new HashSet<>();

        @Override
        public void serverConnecting(RPCContext context) {
            (handshakes)++;
            int expected = getExpectedHandshakeCount();
            if ((expected > 0) && ((handshakes) > expected)) {
                throw new IllegalStateException(((("Expected number of Protocol negotiation handshakes exceeded expected " + expected) + " was ") + (handshakes)));
            }
            // check that a given client protocol is only sent once
            String clientProtocol = context.getHandshakeRequest().clientProtocol;
            if (clientProtocol != null) {
                Assert.assertFalse(seenProtocols.contains(clientProtocol));
                seenProtocols.add(clientProtocol);
            }
        }

        public void assertHandshake() {
            int expected = getExpectedHandshakeCount();
            if (expected != (TestProtocolSpecific.REPEATING)) {
                Assert.assertEquals("Expected number of handshakes did not take place.", expected, handshakes);
            }
        }
    }

    public static class InteropTest {
        private static File SERVER_PORTS_DIR;

        static {
            try {
                TestProtocolSpecific.InteropTest.SERVER_PORTS_DIR = Files.createTempDirectory(TestProtocolSpecific.class.getSimpleName()).toFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Test
        public void testClient() throws Exception {
            for (File f : Objects.requireNonNull(TestProtocolSpecific.InteropTest.SERVER_PORTS_DIR.listFiles())) {
                LineNumberReader reader = new LineNumberReader(new FileReader(f));
                int port = Integer.parseInt(reader.readLine());
                System.out.println(((("Validating java client to " + (f.getName())) + " - ") + port));
                Transceiver client = new SocketTransceiver(new InetSocketAddress("localhost", port));
                TestProtocolSpecific.proxy = SpecificRequestor.getClient(Simple.class, client);
                TestProtocolSpecific proto = new TestProtocolSpecific();
                proto.testHello();
                proto.testEcho();
                proto.testEchoBytes();
                proto.testError();
                System.out.println(((("Done! Validation java client to " + (f.getName())) + " - ") + port));
            }
        }

        /**
         * Starts the RPC server.
         */
        public static void main(String[] args) throws Exception {
            SocketServer server = new SocketServer(new SpecificResponder(Simple.class, new TestProtocolSpecific.TestImpl()), new InetSocketAddress(0));
            server.start();
            File portFile = new File(TestProtocolSpecific.InteropTest.SERVER_PORTS_DIR, "java-port");
            FileWriter w = new FileWriter(portFile);
            w.write(Integer.toString(server.getPort()));
            w.close();
        }
    }
}

