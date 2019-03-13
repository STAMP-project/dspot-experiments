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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.apache.avro.mapreduce;


import AvroJob.CONF_OUTPUT_CODEC;
import DataFileConstants.BZIP2_CODEC;
import DataFileConstants.DEFAULT_SYNC_INTERVAL;
import DataFileConstants.SNAPPY_CODEC;
import DataFileConstants.ZSTANDARD_CODEC;
import java.io.IOException;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.mapred.AvroOutputFormat;
import org.apache.avro.mapred.AvroOutputFormat.DEFLATE_LEVEL_KEY;
import org.apache.hadoop.conf.Configuration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestAvroKeyOutputFormat {
    private static final String SYNC_INTERVAL_KEY = AvroOutputFormat.SYNC_INTERVAL_KEY;

    private static final int TEST_SYNC_INTERVAL = 12345;

    @Rule
    public TemporaryFolder mTempDir = new TemporaryFolder();

    @Test
    public void testWithNullCodec() throws IOException {
        Configuration conf = new Configuration();
        conf.setInt(TestAvroKeyOutputFormat.SYNC_INTERVAL_KEY, TestAvroKeyOutputFormat.TEST_SYNC_INTERVAL);
        testGetRecordWriter(conf, CodecFactory.nullCodec(), TestAvroKeyOutputFormat.TEST_SYNC_INTERVAL);
    }

    @Test
    public void testWithDeflateCodec() throws IOException {
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.output.compress", true);
        conf.setInt(DEFLATE_LEVEL_KEY, 3);
        testGetRecordWriter(conf, CodecFactory.deflateCodec(3), DEFAULT_SYNC_INTERVAL);
    }

    @Test
    public void testWithSnappyCode() throws IOException {
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.output.compress", true);
        conf.set(CONF_OUTPUT_CODEC, SNAPPY_CODEC);
        conf.setInt(TestAvroKeyOutputFormat.SYNC_INTERVAL_KEY, TestAvroKeyOutputFormat.TEST_SYNC_INTERVAL);
        testGetRecordWriter(conf, CodecFactory.snappyCodec(), TestAvroKeyOutputFormat.TEST_SYNC_INTERVAL);
    }

    @Test
    public void testWithBZip2Code() throws IOException {
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.output.compress", true);
        conf.set(CONF_OUTPUT_CODEC, BZIP2_CODEC);
        testGetRecordWriter(conf, CodecFactory.bzip2Codec(), DEFAULT_SYNC_INTERVAL);
    }

    @Test
    public void testWithZstandardCode() throws IOException {
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.output.compress", true);
        conf.set(CONF_OUTPUT_CODEC, ZSTANDARD_CODEC);
        testGetRecordWriter(conf, CodecFactory.zstandardCodec(), DEFAULT_SYNC_INTERVAL);
    }

    @Test
    public void testWithDeflateCodeWithHadoopConfig() throws IOException {
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.output.compress", true);
        conf.set("mapred.output.compression.codec", "org.apache.hadoop.io.compress.DeflateCodec");
        conf.setInt(DEFLATE_LEVEL_KEY, (-1));
        conf.setInt(TestAvroKeyOutputFormat.SYNC_INTERVAL_KEY, TestAvroKeyOutputFormat.TEST_SYNC_INTERVAL);
        testGetRecordWriter(conf, CodecFactory.deflateCodec((-1)), TestAvroKeyOutputFormat.TEST_SYNC_INTERVAL);
    }

    @Test
    public void testWithSnappyCodeWithHadoopConfig() throws IOException {
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.output.compress", true);
        conf.set("mapred.output.compression.codec", "org.apache.hadoop.io.compress.SnappyCodec");
        testGetRecordWriter(conf, CodecFactory.snappyCodec(), DEFAULT_SYNC_INTERVAL);
    }

    @Test
    public void testWithBZip2CodeWithHadoopConfig() throws IOException {
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.output.compress", true);
        conf.set("mapred.output.compression.codec", "org.apache.hadoop.io.compress.BZip2Codec");
        conf.setInt(TestAvroKeyOutputFormat.SYNC_INTERVAL_KEY, TestAvroKeyOutputFormat.TEST_SYNC_INTERVAL);
        testGetRecordWriter(conf, CodecFactory.bzip2Codec(), TestAvroKeyOutputFormat.TEST_SYNC_INTERVAL);
    }
}

