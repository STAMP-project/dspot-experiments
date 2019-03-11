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
package org.apache.flume.sink.hdfs;


import SequenceFile.CompressionType.BLOCK;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.flume.Context;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHDFSCompressedDataStream {
    private static final Logger logger = LoggerFactory.getLogger(TestHDFSCompressedDataStream.class);

    private File file;

    private String fileURI;

    private CompressionCodecFactory factory;

    // make sure the data makes it to disk if we sync() the data stream
    @Test
    public void testGzipDurability() throws Exception {
        Context context = new Context();
        HDFSCompressedDataStream writer = new HDFSCompressedDataStream();
        writer.configure(context);
        writer.open(fileURI, factory.getCodec(new Path(fileURI)), BLOCK);
        String[] bodies = new String[]{ "yarf!" };
        writeBodies(writer, bodies);
        byte[] buf = new byte[256];
        GZIPInputStream cmpIn = new GZIPInputStream(new FileInputStream(file));
        int len = cmpIn.read(buf);
        String result = new String(buf, 0, len, Charsets.UTF_8);
        result = result.trim();// BodyTextEventSerializer adds a newline

        Assert.assertEquals("input and output must match", bodies[0], result);
    }

    @Test
    public void testGzipDurabilityWithSerializer() throws Exception {
        Context context = new Context();
        context.put("serializer", "AVRO_EVENT");
        HDFSCompressedDataStream writer = new HDFSCompressedDataStream();
        writer.configure(context);
        writer.open(fileURI, factory.getCodec(new Path(fileURI)), BLOCK);
        String[] bodies = new String[]{ "yarf!", "yarfing!" };
        writeBodies(writer, bodies);
        int found = 0;
        int expected = bodies.length;
        List<String> expectedBodies = Lists.newArrayList(bodies);
        GZIPInputStream cmpIn = new GZIPInputStream(new FileInputStream(file));
        DatumReader<GenericRecord> reader = new org.apache.avro.generic.GenericDatumReader<GenericRecord>();
        DataFileStream<GenericRecord> avroStream = new DataFileStream<GenericRecord>(cmpIn, reader);
        GenericRecord record = new org.apache.avro.generic.GenericData.Record(avroStream.getSchema());
        while (avroStream.hasNext()) {
            avroStream.next(record);
            CharsetDecoder decoder = Charsets.UTF_8.newDecoder();
            String bodyStr = decoder.decode(((ByteBuffer) (record.get("body")))).toString();
            expectedBodies.remove(bodyStr);
            found++;
        } 
        avroStream.close();
        cmpIn.close();
        Assert.assertTrue(((((((("Found = " + found) + ", Expected = ") + expected) + ", Left = ") + (expectedBodies.size())) + " ") + expectedBodies), ((expectedBodies.size()) == 0));
    }
}

