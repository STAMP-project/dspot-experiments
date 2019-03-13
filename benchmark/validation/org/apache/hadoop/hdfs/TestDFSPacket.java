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
package org.apache.hadoop.hdfs;


import java.util.Random;
import org.apache.hadoop.hdfs.protocol.datatransfer.PacketHeader;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.htrace.core.SpanId;
import org.junit.Assert;
import org.junit.Test;


public class TestDFSPacket {
    private static final int chunkSize = 512;

    private static final int checksumSize = 4;

    private static final int maxChunksPerPacket = 4;

    @Test
    public void testPacket() throws Exception {
        Random r = new Random(12345L);
        byte[] data = new byte[TestDFSPacket.chunkSize];
        r.nextBytes(data);
        byte[] checksum = new byte[TestDFSPacket.checksumSize];
        r.nextBytes(checksum);
        DataOutputBuffer os = new DataOutputBuffer(((data.length) * 2));
        byte[] packetBuf = new byte[(data.length) * 2];
        DFSPacket p = new DFSPacket(packetBuf, TestDFSPacket.maxChunksPerPacket, 0, 0, TestDFSPacket.checksumSize, false);
        p.setSyncBlock(true);
        p.writeData(data, 0, data.length);
        p.writeChecksum(checksum, 0, checksum.length);
        p.writeTo(os);
        // we have set syncBlock to true, so the header has the maximum length
        int headerLen = PacketHeader.PKT_MAX_HEADER_LEN;
        byte[] readBuf = os.getData();
        TestDFSPacket.assertArrayRegionsEqual(readBuf, headerLen, checksum, 0, checksum.length);
        TestDFSPacket.assertArrayRegionsEqual(readBuf, (headerLen + (checksum.length)), data, 0, data.length);
    }

    @Test
    public void testAddParentsGetParents() throws Exception {
        DFSPacket p = new DFSPacket(null, TestDFSPacket.maxChunksPerPacket, 0, 0, TestDFSPacket.checksumSize, false);
        SpanId[] parents = p.getTraceParents();
        Assert.assertEquals(0, parents.length);
        p.addTraceParent(new SpanId(0, 123));
        p.addTraceParent(new SpanId(0, 123));
        parents = p.getTraceParents();
        Assert.assertEquals(1, parents.length);
        Assert.assertEquals(new SpanId(0, 123), parents[0]);
        parents = p.getTraceParents();// test calling 'get' again.

        Assert.assertEquals(1, parents.length);
        Assert.assertEquals(new SpanId(0, 123), parents[0]);
        p.addTraceParent(new SpanId(0, 1));
        p.addTraceParent(new SpanId(0, 456));
        p.addTraceParent(new SpanId(0, 789));
        parents = p.getTraceParents();
        Assert.assertEquals(4, parents.length);
        Assert.assertEquals(new SpanId(0, 1), parents[0]);
        Assert.assertEquals(new SpanId(0, 123), parents[1]);
        Assert.assertEquals(new SpanId(0, 456), parents[2]);
        Assert.assertEquals(new SpanId(0, 789), parents[3]);
    }
}

