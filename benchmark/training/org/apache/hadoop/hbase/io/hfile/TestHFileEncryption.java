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
package org.apache.hadoop.hbase.io.hfile;


import Compression.Algorithm;
import Encryption.Context;
import HFile.Reader;
import HFile.Writer;
import HFileBlock.FSReaderImpl;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueUtil;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.io.crypto.Encryption;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.RedundantKVGenerator;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ IOTests.class, SmallTests.class })
public class TestHFileEncryption {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHFileEncryption.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHFileEncryption.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final SecureRandom RNG = new SecureRandom();

    private static FileSystem fs;

    private static Context cryptoContext;

    @Test
    public void testDataBlockEncryption() throws IOException {
        final int blocks = 10;
        final int[] blockSizes = new int[blocks];
        for (int i = 0; i < blocks; i++) {
            blockSizes[i] = (1024 + (TestHFileEncryption.RNG.nextInt((1024 * 63)))) / (Bytes.SIZEOF_INT);
        }
        for (Compression.Algorithm compression : TestHFileBlock.COMPRESSION_ALGORITHMS) {
            Path path = new Path(getDataTestDir(), (("block_v3_" + compression) + "_AES"));
            TestHFileEncryption.LOG.info(("testDataBlockEncryption: encryption=AES compression=" + compression));
            long totalSize = 0;
            HFileContext fileContext = new HFileContextBuilder().withCompression(compression).withEncryptionContext(TestHFileEncryption.cryptoContext).build();
            FSDataOutputStream os = TestHFileEncryption.fs.create(path);
            try {
                for (int i = 0; i < blocks; i++) {
                    totalSize += writeBlock(os, fileContext, blockSizes[i]);
                }
            } finally {
                os.close();
            }
            FSDataInputStream is = TestHFileEncryption.fs.open(path);
            try {
                HFileBlock.FSReaderImpl hbr = new HFileBlock.FSReaderImpl(is, totalSize, fileContext);
                long pos = 0;
                for (int i = 0; i < blocks; i++) {
                    pos += readAndVerifyBlock(pos, fileContext, hbr, blockSizes[i]);
                }
            } finally {
                is.close();
            }
        }
    }

    @Test
    public void testHFileEncryptionMetadata() throws Exception {
        Configuration conf = TestHFileEncryption.TEST_UTIL.getConfiguration();
        CacheConfig cacheConf = new CacheConfig(conf);
        HFileContext fileContext = new HFileContextBuilder().withEncryptionContext(TestHFileEncryption.cryptoContext).build();
        // write a simple encrypted hfile
        Path path = new Path(getDataTestDir(), "cryptometa.hfile");
        FSDataOutputStream out = TestHFileEncryption.fs.create(path);
        HFile.Writer writer = HFile.getWriterFactory(conf, cacheConf).withOutputStream(out).withFileContext(fileContext).create();
        try {
            KeyValue kv = new KeyValue(Bytes.toBytes("foo"), Bytes.toBytes("f1"), null, Bytes.toBytes("value"));
            writer.append(kv);
        } finally {
            writer.close();
            out.close();
        }
        // read it back in and validate correct crypto metadata
        HFile.Reader reader = HFile.createReader(TestHFileEncryption.fs, path, cacheConf, true, conf);
        try {
            reader.loadFileInfo();
            FixedFileTrailer trailer = reader.getTrailer();
            Assert.assertNotNull(trailer.getEncryptionKey());
            Encryption.Context readerContext = reader.getFileContext().getEncryptionContext();
            Assert.assertEquals(readerContext.getCipher().getName(), TestHFileEncryption.cryptoContext.getCipher().getName());
            Assert.assertTrue(Bytes.equals(readerContext.getKeyBytes(), TestHFileEncryption.cryptoContext.getKeyBytes()));
        } finally {
            reader.close();
        }
    }

    @Test
    public void testHFileEncryption() throws Exception {
        // Create 1000 random test KVs
        RedundantKVGenerator generator = new RedundantKVGenerator();
        List<KeyValue> testKvs = generator.generateTestKeyValues(1000);
        // Iterate through data block encoding and compression combinations
        Configuration conf = TestHFileEncryption.TEST_UTIL.getConfiguration();
        CacheConfig cacheConf = new CacheConfig(conf);
        for (DataBlockEncoding encoding : DataBlockEncoding.values()) {
            for (Compression.Algorithm compression : TestHFileBlock.COMPRESSION_ALGORITHMS) {
                HFileContext fileContext = // small blocks
                new HFileContextBuilder().withBlockSize(4096).withEncryptionContext(TestHFileEncryption.cryptoContext).withCompression(compression).withDataBlockEncoding(encoding).build();
                // write a new test HFile
                TestHFileEncryption.LOG.info(("Writing with " + fileContext));
                Path path = new Path(getDataTestDir(), ((getRandomUUID().toString()) + ".hfile"));
                FSDataOutputStream out = TestHFileEncryption.fs.create(path);
                HFile.Writer writer = HFile.getWriterFactory(conf, cacheConf).withOutputStream(out).withFileContext(fileContext).create();
                try {
                    for (KeyValue kv : testKvs) {
                        writer.append(kv);
                    }
                } finally {
                    writer.close();
                    out.close();
                }
                // read it back in
                TestHFileEncryption.LOG.info(("Reading with " + fileContext));
                int i = 0;
                HFileScanner scanner = null;
                HFile.Reader reader = HFile.createReader(TestHFileEncryption.fs, path, cacheConf, true, conf);
                try {
                    reader.loadFileInfo();
                    FixedFileTrailer trailer = reader.getTrailer();
                    Assert.assertNotNull(trailer.getEncryptionKey());
                    scanner = reader.getScanner(false, false);
                    Assert.assertTrue("Initial seekTo failed", scanner.seekTo());
                    do {
                        Cell kv = scanner.getCell();
                        Assert.assertTrue("Read back an unexpected or invalid KV", testKvs.contains(KeyValueUtil.ensureKeyValue(kv)));
                        i++;
                    } while (scanner.next() );
                } finally {
                    reader.close();
                    scanner.close();
                }
                Assert.assertEquals("Did not read back as many KVs as written", i, testKvs.size());
                // Test random seeks with pread
                TestHFileEncryption.LOG.info(("Random seeking with " + fileContext));
                reader = HFile.createReader(TestHFileEncryption.fs, path, cacheConf, true, conf);
                try {
                    scanner = reader.getScanner(false, true);
                    Assert.assertTrue("Initial seekTo failed", scanner.seekTo());
                    for (i = 0; i < 100; i++) {
                        KeyValue kv = testKvs.get(TestHFileEncryption.RNG.nextInt(testKvs.size()));
                        Assert.assertEquals(("Unable to find KV as expected: " + kv), 0, scanner.seekTo(kv));
                    }
                } finally {
                    scanner.close();
                    reader.close();
                }
            }
        }
    }
}

