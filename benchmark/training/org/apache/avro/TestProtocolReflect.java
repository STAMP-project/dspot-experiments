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


import java.io.IOException;
import java.util.Random;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.reflect.ReflectRequestor;
import org.apache.avro.ipc.reflect.ReflectResponder;
import org.apache.avro.reflect.ReflectData;
import org.junit.Assert;
import org.junit.Test;


public class TestProtocolReflect {
    public static class TestRecord {
        private String name;

        public int hashCode() {
            return this.name.hashCode();
        }

        public boolean equals(Object that) {
            return this.name.equals(((TestProtocolReflect.TestRecord) (that)).name);
        }
    }

    public interface Simple {
        String hello(String greeting);

        TestProtocolReflect.TestRecord echo(TestProtocolReflect.TestRecord record);

        int add(int arg1, int arg2);

        byte[] echoBytes(byte[] data);

        void error() throws SimpleException;
    }

    private static boolean throwUndeclaredError;

    public static class TestImpl implements TestProtocolReflect.Simple {
        public String hello(String greeting) {
            return "goodbye";
        }

        public int add(int arg1, int arg2) {
            return arg1 + arg2;
        }

        public TestProtocolReflect.TestRecord echo(TestProtocolReflect.TestRecord record) {
            return record;
        }

        public byte[] echoBytes(byte[] data) {
            return data;
        }

        public void error() throws SimpleException {
            if (TestProtocolReflect.throwUndeclaredError)
                throw new RuntimeException("foo");

            throw new SimpleException("foo");
        }
    }

    protected static Server server;

    protected static Transceiver client;

    protected static TestProtocolReflect.Simple proxy;

    @Test
    public void testClassLoader() throws Exception {
        ClassLoader loader = new ClassLoader() {};
        ReflectResponder responder = new ReflectResponder(TestProtocolReflect.Simple.class, new TestProtocolReflect.TestImpl(), new ReflectData(loader));
        Assert.assertEquals(responder.getReflectData().getClassLoader(), loader);
        ReflectRequestor requestor = new ReflectRequestor(TestProtocolReflect.Simple.class, TestProtocolReflect.client, new ReflectData(loader));
        Assert.assertEquals(requestor.getReflectData().getClassLoader(), loader);
    }

    @Test
    public void testHello() throws IOException {
        String response = TestProtocolReflect.proxy.hello("bob");
        Assert.assertEquals("goodbye", response);
    }

    @Test
    public void testEcho() throws IOException {
        TestProtocolReflect.TestRecord record = new TestProtocolReflect.TestRecord();
        record.name = "foo";
        TestProtocolReflect.TestRecord echoed = TestProtocolReflect.proxy.echo(record);
        Assert.assertEquals(record, echoed);
    }

    @Test
    public void testAdd() throws IOException {
        int result = TestProtocolReflect.proxy.add(1, 2);
        Assert.assertEquals(3, result);
    }

    @Test
    public void testEchoBytes() throws IOException {
        Random random = new Random();
        int length = random.nextInt((1024 * 16));
        byte[] data = new byte[length];
        random.nextBytes(data);
        byte[] echoed = TestProtocolReflect.proxy.echoBytes(data);
        Assert.assertArrayEquals(data, echoed);
    }

    @Test
    public void testError() throws IOException {
        SimpleException error = null;
        try {
            TestProtocolReflect.proxy.error();
        } catch (SimpleException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals("foo", error.getMessage());
    }

    @Test
    public void testUndeclaredError() throws Exception {
        this.throwUndeclaredError = true;
        RuntimeException error = null;
        try {
            TestProtocolReflect.proxy.error();
        } catch (RuntimeException e) {
            error = e;
        } finally {
            this.throwUndeclaredError = false;
        }
        Assert.assertNotNull(error);
        Assert.assertTrue(error.toString().contains("foo"));
    }
}

