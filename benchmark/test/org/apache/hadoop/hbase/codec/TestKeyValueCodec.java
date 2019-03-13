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
package org.apache.hadoop.hbase.codec;


import Codec.Decoder;
import Codec.Encoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.io.CountingInputStream;
import org.apache.hbase.thirdparty.com.google.common.io.CountingOutputStream;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestKeyValueCodec {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestKeyValueCodec.class);

    @Test
    public void testEmptyWorks() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CountingOutputStream cos = new CountingOutputStream(baos);
        DataOutputStream dos = new DataOutputStream(cos);
        KeyValueCodec kvc = new KeyValueCodec();
        Codec.Encoder encoder = kvc.getEncoder(dos);
        encoder.flush();
        dos.close();
        long offset = cos.getCount();
        Assert.assertEquals(0, offset);
        CountingInputStream cis = new CountingInputStream(new ByteArrayInputStream(baos.toByteArray()));
        DataInputStream dis = new DataInputStream(cis);
        Codec.Decoder decoder = kvc.getDecoder(dis);
        Assert.assertFalse(decoder.advance());
        dis.close();
        Assert.assertEquals(0, cis.getCount());
    }

    @Test
    public void testOne() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CountingOutputStream cos = new CountingOutputStream(baos);
        DataOutputStream dos = new DataOutputStream(cos);
        KeyValueCodec kvc = new KeyValueCodec();
        Codec.Encoder encoder = kvc.getEncoder(dos);
        final KeyValue kv = new KeyValue(Bytes.toBytes("r"), Bytes.toBytes("f"), Bytes.toBytes("q"), Bytes.toBytes("v"));
        final int length = (kv.getLength()) + (Bytes.SIZEOF_INT);
        encoder.write(kv);
        encoder.flush();
        dos.close();
        long offset = cos.getCount();
        Assert.assertEquals(length, offset);
        CountingInputStream cis = new CountingInputStream(new ByteArrayInputStream(baos.toByteArray()));
        DataInputStream dis = new DataInputStream(cis);
        Codec.Decoder decoder = kvc.getDecoder(dis);
        Assert.assertTrue(decoder.advance());// First read should pull in the KV

        // Second read should trip over the end-of-stream  marker and return false
        Assert.assertFalse(decoder.advance());
        dis.close();
        Assert.assertEquals(length, cis.getCount());
    }

    @Test
    public void testThree() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CountingOutputStream cos = new CountingOutputStream(baos);
        DataOutputStream dos = new DataOutputStream(cos);
        KeyValueCodec kvc = new KeyValueCodec();
        Codec.Encoder encoder = kvc.getEncoder(dos);
        final KeyValue kv1 = new KeyValue(Bytes.toBytes("r"), Bytes.toBytes("f"), Bytes.toBytes("1"), Bytes.toBytes("1"));
        final KeyValue kv2 = new KeyValue(Bytes.toBytes("r"), Bytes.toBytes("f"), Bytes.toBytes("2"), Bytes.toBytes("2"));
        final KeyValue kv3 = new KeyValue(Bytes.toBytes("r"), Bytes.toBytes("f"), Bytes.toBytes("3"), Bytes.toBytes("3"));
        final int length = (kv1.getLength()) + (Bytes.SIZEOF_INT);
        encoder.write(kv1);
        encoder.write(kv2);
        encoder.write(kv3);
        encoder.flush();
        dos.close();
        long offset = cos.getCount();
        Assert.assertEquals((length * 3), offset);
        CountingInputStream cis = new CountingInputStream(new ByteArrayInputStream(baos.toByteArray()));
        DataInputStream dis = new DataInputStream(cis);
        Codec.Decoder decoder = kvc.getDecoder(dis);
        Assert.assertTrue(decoder.advance());
        KeyValue kv = ((KeyValue) (decoder.current()));
        Assert.assertTrue(kv1.equals(kv));
        Assert.assertTrue(decoder.advance());
        kv = ((KeyValue) (decoder.current()));
        Assert.assertTrue(kv2.equals(kv));
        Assert.assertTrue(decoder.advance());
        kv = ((KeyValue) (decoder.current()));
        Assert.assertTrue(kv3.equals(kv));
        Assert.assertFalse(decoder.advance());
        dis.close();
        Assert.assertEquals((length * 3), cis.getCount());
    }
}

