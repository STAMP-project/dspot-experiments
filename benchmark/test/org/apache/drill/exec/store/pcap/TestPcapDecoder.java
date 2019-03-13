/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.pcap;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.drill.exec.store.pcap.decoder.Packet;
import org.apache.drill.exec.store.pcap.decoder.PacketDecoder;
import org.apache.drill.shaded.guava.com.google.common.io.Resources;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static PcapRecordReader.BUFFER_SIZE;


public class TestPcapDecoder extends BaseTestQuery {
    private static final Logger logger = LoggerFactory.getLogger(TestPcapDecoder.class);

    private static File bigFile;

    @Test
    public void testByteOrdering() throws IOException {
        File f = File.createTempFile("foo", "pcap");
        f.deleteOnExit();
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(f))) {
            TestPcapDecoder.writeHeader(out);
        }
        try (InputStream in = new FileInputStream(f)) {
            PacketDecoder pd = new PacketDecoder(in);
            Assert.assertTrue(pd.isBigEndian());
        }
    }

    @Test
    public void testBasics() throws IOException {
        InputStream in = Resources.getResource("store/pcap/tcp-2.pcap").openStream();
        PacketDecoder pd = new PacketDecoder(in);
        Packet p = pd.packet();
        int offset = 0;
        byte[] buffer = new byte[(BUFFER_SIZE) + (pd.getMaxLength())];
        int validBytes = in.read(buffer);
        Assert.assertTrue((validBytes > 50));
        offset = pd.decodePacket(buffer, offset, p, pd.getMaxLength(), validBytes);
        offset = pd.decodePacket(buffer, offset, p, pd.getMaxLength(), validBytes);
        Assert.assertEquals(228, offset);
        Assert.assertEquals("FE:00:00:00:00:02", p.getEthernetDestination());
        Assert.assertEquals("FE:00:00:00:00:01", p.getEthernetSource());
        Assert.assertEquals("/192.168.0.1", p.getSrc_ip().toString());
        Assert.assertEquals("/192.168.0.2", p.getDst_ip().toString());
        Assert.assertEquals(161, p.getSrc_port());
        Assert.assertEquals(0, p.getDst_port());
        Assert.assertEquals(0, p.getTimestampMicro());
    }
}

