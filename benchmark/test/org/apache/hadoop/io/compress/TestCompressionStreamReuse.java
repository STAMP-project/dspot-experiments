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
package org.apache.hadoop.io.compress;


import CompressionLevel.BEST_COMPRESSION;
import CompressionStrategy.HUFFMAN_ONLY;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.zlib.ZlibFactory;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCompressionStreamReuse {
    private static final Logger LOG = LoggerFactory.getLogger(TestCompressionStreamReuse.class);

    private Configuration conf = new Configuration();

    private int count = 10000;

    private int seed = new Random().nextInt();

    @Test
    public void testBZip2Codec() throws IOException {
        resetStateTest(conf, seed, count, "org.apache.hadoop.io.compress.BZip2Codec");
    }

    @Test
    public void testGzipCompressStreamReuse() throws IOException {
        resetStateTest(conf, seed, count, "org.apache.hadoop.io.compress.GzipCodec");
    }

    @Test
    public void testZStandardCompressStreamReuse() throws IOException {
        Assume.assumeTrue(ZStandardCodec.isNativeCodeLoaded());
        resetStateTest(conf, seed, count, "org.apache.hadoop.io.compress.ZStandardCodec");
    }

    @Test
    public void testGzipCompressStreamReuseWithParam() throws IOException {
        Configuration conf = new Configuration(this.conf);
        ZlibFactory.setCompressionLevel(conf, BEST_COMPRESSION);
        ZlibFactory.setCompressionStrategy(conf, HUFFMAN_ONLY);
        resetStateTest(conf, seed, count, "org.apache.hadoop.io.compress.GzipCodec");
    }
}

