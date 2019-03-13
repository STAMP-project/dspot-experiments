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
package org.apache.hadoop.hbase.regionserver.wal;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.io.util.Dictionary;
import org.apache.hadoop.hbase.io.util.LRUDictionary;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test our compressor class.
 */
@Category({ RegionServerTests.class, SmallTests.class })
public class TestCompressor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompressor.class);

    @Test
    public void testToShort() {
        short s = 1;
        Assert.assertEquals(s, Compressor.toShort(((byte) (0)), ((byte) (1))));
        s <<= 8;
        Assert.assertEquals(s, Compressor.toShort(((byte) (1)), ((byte) (0))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeToShort() {
        Compressor.toShort(((byte) (255)), ((byte) (255)));
    }

    @Test
    public void testCompressingWithNullDictionaries() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] blahBytes = Bytes.toBytes("blah");
        Compressor.writeCompressed(blahBytes, 0, blahBytes.length, dos, null);
        dos.close();
        byte[] dosbytes = baos.toByteArray();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(dosbytes));
        byte[] product = Compressor.readCompressed(dis, null);
        Assert.assertTrue(Bytes.equals(blahBytes, product));
    }

    @Test
    public void testCompressingWithClearDictionaries() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        Dictionary dictionary = new LRUDictionary();
        dictionary.init(Short.MAX_VALUE);
        byte[] blahBytes = Bytes.toBytes("blah");
        Compressor.writeCompressed(blahBytes, 0, blahBytes.length, dos, dictionary);
        dos.close();
        byte[] dosbytes = baos.toByteArray();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(dosbytes));
        dictionary = new LRUDictionary();
        dictionary.init(Short.MAX_VALUE);
        byte[] product = Compressor.readCompressed(dis, dictionary);
        Assert.assertTrue(Bytes.equals(blahBytes, product));
    }
}

