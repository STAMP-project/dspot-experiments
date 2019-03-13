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
package org.apache.avro.ipc.jetty;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.test.BulkData;
import org.junit.Assert;
import org.junit.Test;


public class TestBulkData {
    private static final long COUNT = Integer.parseInt(System.getProperty("test.count", "10"));

    private static final int SIZE = Integer.parseInt(System.getProperty("test.size", "65536"));

    private static final ByteBuffer DATA = ByteBuffer.allocate(TestBulkData.SIZE);

    static {
        Random rand = new Random();
        TestBulkData.DATA.limit(TestBulkData.DATA.capacity());
        TestBulkData.DATA.position(0);
        rand.nextBytes(TestBulkData.DATA.array());
    }

    public static class BulkDataImpl implements BulkData {
        @Override
        public ByteBuffer read() {
            return TestBulkData.DATA.duplicate();
        }

        @Override
        public void write(ByteBuffer data) {
            Assert.assertEquals(TestBulkData.SIZE, data.remaining());
        }
    }

    private static Server server;

    private static Transceiver client;

    private static BulkData proxy;

    @Test
    public void testRead() throws IOException {
        for (int i = 0; i < (TestBulkData.COUNT); i++)
            Assert.assertEquals(TestBulkData.SIZE, TestBulkData.proxy.read().remaining());

    }

    @Test
    public void testWrite() throws IOException {
        for (int i = 0; i < (TestBulkData.COUNT); i++)
            TestBulkData.proxy.write(TestBulkData.DATA.duplicate());

    }
}

