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
package org.apache.hadoop.hbase.util;


import Compression.Algorithm.LZO;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.util.NativeCodeLoader;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MiscTests.class, SmallTests.class })
public class TestCompressionTest {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompressionTest.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCompressionTest.class);

    @Test
    public void testExceptionCaching() {
        // This test will fail if you run the tests with LZO compression available.
        try {
            CompressionTest.testCompression(LZO);
            Assert.fail();// always throws

        } catch (IOException e) {
            // there should be a 'cause'.
            Assert.assertNotNull(e.getCause());
        }
        // this is testing the caching of the test results.
        try {
            CompressionTest.testCompression(LZO);
            Assert.fail();// always throws

        } catch (IOException e) {
            // there should be NO cause because it's a direct exception not wrapped
            Assert.assertNull(e.getCause());
        }
        Assert.assertFalse(CompressionTest.testCompression("LZO"));
    }

    @Test
    public void testTestCompression() {
        Assert.assertTrue(CompressionTest.testCompression("NONE"));
        Assert.assertTrue(CompressionTest.testCompression("GZ"));
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            nativeCodecTest("LZO", "lzo2", "com.hadoop.compression.lzo.LzoCodec");
            nativeCodecTest("LZ4", null, "org.apache.hadoop.io.compress.Lz4Codec");
            nativeCodecTest("SNAPPY", "snappy", "org.apache.hadoop.io.compress.SnappyCodec");
            nativeCodecTest("BZIP2", "bzip2", "org.apache.hadoop.io.compress.BZip2Codec");
            nativeCodecTest("ZSTD", "zstd", "org.apache.hadoop.io.compress.ZStandardCodec");
        } else {
            // Hadoop nativelib is not available
            TestCompressionTest.LOG.debug("Native code not loaded");
            Assert.assertFalse(CompressionTest.testCompression("LZO"));
            Assert.assertFalse(CompressionTest.testCompression("LZ4"));
            Assert.assertFalse(CompressionTest.testCompression("SNAPPY"));
            Assert.assertFalse(CompressionTest.testCompression("BZIP2"));
            Assert.assertFalse(CompressionTest.testCompression("ZSTD"));
        }
    }
}

