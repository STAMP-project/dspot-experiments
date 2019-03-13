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
import org.apache.avro.ipc.SocketServer;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.test.errors.TestError;
import org.apache.avro.test.namespace.TestNamespace;
import org.apache.avro.test.namespace.TestRecord;
import org.apache.avro.test.util.MD5;
import org.junit.Assert;
import org.junit.Test;


public class TestNamespaceSpecific {
    public static class TestImpl implements TestNamespace {
        public TestRecord echo(TestRecord record) {
            return record;
        }

        public void error() throws AvroRemoteException {
            throw TestError.newBuilder().setMessage$("an error").build();
        }
    }

    protected static SocketServer server;

    protected static Transceiver client;

    protected static TestNamespace proxy;

    @Test
    public void testEcho() throws IOException {
        TestRecord record = new TestRecord();
        record.setHash(new MD5(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5 }));
        TestRecord echoed = TestNamespaceSpecific.proxy.echo(record);
        Assert.assertEquals(record, echoed);
        Assert.assertEquals(record.hashCode(), echoed.hashCode());
    }

    @Test
    public void testError() throws IOException {
        TestError error = null;
        try {
            TestNamespaceSpecific.proxy.error();
        } catch (TestError e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals("an error", error.getMessage$());
    }
}

