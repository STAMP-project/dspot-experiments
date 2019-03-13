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
package org.apache.hadoop.hbase.ipc;


import Charsets.UTF_8;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.RPCTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.io.Files;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ RPCTests.class, SmallTests.class })
public class TestBufferChain {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBufferChain.class);

    private File tmpFile;

    private static final byte[][] HELLO_WORLD_CHUNKS = new byte[][]{ "hello".getBytes(UTF_8), " ".getBytes(UTF_8), "world".getBytes(UTF_8) };

    @Test
    public void testGetBackBytesWePutIn() {
        ByteBuffer[] bufs = wrapArrays(TestBufferChain.HELLO_WORLD_CHUNKS);
        BufferChain chain = new BufferChain(bufs);
        Assert.assertTrue(Bytes.equals(Bytes.toBytes("hello world"), chain.getBytes()));
    }

    @Test
    public void testChainChunkBiggerThanWholeArray() throws IOException {
        ByteBuffer[] bufs = wrapArrays(TestBufferChain.HELLO_WORLD_CHUNKS);
        BufferChain chain = new BufferChain(bufs);
        writeAndVerify(chain, "hello world", 8192);
        assertNoRemaining(bufs);
    }

    @Test
    public void testChainChunkBiggerThanSomeArrays() throws IOException {
        ByteBuffer[] bufs = wrapArrays(TestBufferChain.HELLO_WORLD_CHUNKS);
        BufferChain chain = new BufferChain(bufs);
        writeAndVerify(chain, "hello world", 3);
        assertNoRemaining(bufs);
    }

    @Test
    public void testLimitOffset() throws IOException {
        ByteBuffer[] bufs = new ByteBuffer[]{ stringBuf("XXXhelloYYY", 3, 5), stringBuf(" ", 0, 1), stringBuf("XXXXworldY", 4, 5) };
        BufferChain chain = new BufferChain(bufs);
        writeAndVerify(chain, "hello world", 3);
        assertNoRemaining(bufs);
    }

    @Test
    public void testWithSpy() throws IOException {
        ByteBuffer[] bufs = new ByteBuffer[]{ stringBuf("XXXhelloYYY", 3, 5), stringBuf(" ", 0, 1), stringBuf("XXXXworldY", 4, 5) };
        BufferChain chain = new BufferChain(bufs);
        FileOutputStream fos = new FileOutputStream(tmpFile);
        FileChannel ch = Mockito.spy(fos.getChannel());
        try {
            chain.write(ch, 2);
            Assert.assertEquals("he", Files.toString(tmpFile, UTF_8));
            chain.write(ch, 2);
            Assert.assertEquals("hell", Files.toString(tmpFile, UTF_8));
            chain.write(ch, 3);
            Assert.assertEquals("hello w", Files.toString(tmpFile, UTF_8));
            chain.write(ch, 8);
            Assert.assertEquals("hello world", Files.toString(tmpFile, UTF_8));
        } finally {
            ch.close();
            fos.close();
        }
    }
}

