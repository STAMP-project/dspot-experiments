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
package org.apache.hadoop.io;


import BloomMapFile.Reader;
import BloomMapFile.Writer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.io.compress.Decompressor;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestBloomMapFile {
    private static Configuration conf = new Configuration();

    private static final Path TEST_ROOT = new Path(GenericTestUtils.getTempPath(TestMapFile.class.getSimpleName()));

    private static final Path TEST_DIR = new Path(TestBloomMapFile.TEST_ROOT, "testfile");

    private static final Path TEST_FILE = new Path(TestBloomMapFile.TEST_ROOT, "testfile");

    @SuppressWarnings("deprecation")
    @Test
    public void testMembershipTest() throws Exception {
        // write the file
        FileSystem fs = FileSystem.getLocal(TestBloomMapFile.conf);
        Path qualifiedDirName = fs.makeQualified(TestBloomMapFile.TEST_DIR);
        TestBloomMapFile.conf.setInt("io.mapfile.bloom.size", 2048);
        BloomMapFile.Writer writer = null;
        BloomMapFile.Reader reader = null;
        try {
            writer = new BloomMapFile.Writer(TestBloomMapFile.conf, fs, qualifiedDirName.toString(), IntWritable.class, Text.class);
            IntWritable key = new IntWritable();
            Text value = new Text();
            for (int i = 0; i < 2000; i += 2) {
                key.set(i);
                value.set(("00" + i));
                writer.append(key, value);
            }
            writer.close();
            reader = new BloomMapFile.Reader(fs, qualifiedDirName.toString(), TestBloomMapFile.conf);
            // check false positives rate
            int falsePos = 0;
            int falseNeg = 0;
            for (int i = 0; i < 2000; i++) {
                key.set(i);
                boolean exists = reader.probablyHasKey(key);
                if ((i % 2) == 0) {
                    if (!exists)
                        falseNeg++;

                } else {
                    if (exists)
                        falsePos++;

                }
            }
            reader.close();
            fs.delete(qualifiedDirName, true);
            System.out.println(("False negatives: " + falseNeg));
            Assert.assertEquals(0, falseNeg);
            System.out.println(("False positives: " + falsePos));
            Assert.assertTrue((falsePos < 2));
        } finally {
            IOUtils.cleanup(null, writer, reader);
        }
    }

    @Test
    public void testMembershipVaryingSizedKeysTest1() throws Exception {
        ArrayList<Text> list = new ArrayList<Text>();
        list.add(new Text("A"));
        list.add(new Text("BB"));
        checkMembershipVaryingSizedKeys(list);
    }

    @Test
    public void testMembershipVaryingSizedKeysTest2() throws Exception {
        ArrayList<Text> list = new ArrayList<Text>();
        list.add(new Text("AA"));
        list.add(new Text("B"));
        checkMembershipVaryingSizedKeys(list);
    }

    /**
     * test {@code BloomMapFile.delete()} method
     */
    @Test
    public void testDeleteFile() {
        BloomMapFile.Writer writer = null;
        try {
            FileSystem fs = FileSystem.getLocal(TestBloomMapFile.conf);
            writer = new BloomMapFile.Writer(TestBloomMapFile.conf, TestBloomMapFile.TEST_FILE, MapFile.Writer.keyClass(IntWritable.class), MapFile.Writer.valueClass(Text.class));
            Assert.assertNotNull("testDeleteFile error !!!", writer);
            writer.close();
            BloomMapFile.delete(fs, TestBloomMapFile.TEST_FILE.toString());
        } catch (Exception ex) {
            Assert.fail("unexpect ex in testDeleteFile !!!");
        } finally {
            IOUtils.cleanup(null, writer);
        }
    }

    /**
     * test {@link BloomMapFile.Reader} constructor with
     * IOException
     */
    @Test
    public void testIOExceptionInWriterConstructor() {
        Path dirNameSpy = Mockito.spy(TestBloomMapFile.TEST_FILE);
        BloomMapFile.Reader reader = null;
        BloomMapFile.Writer writer = null;
        try {
            writer = new BloomMapFile.Writer(TestBloomMapFile.conf, TestBloomMapFile.TEST_FILE, MapFile.Writer.keyClass(IntWritable.class), MapFile.Writer.valueClass(Text.class));
            writer.append(new IntWritable(1), new Text("123124142"));
            writer.close();
            Mockito.when(dirNameSpy.getFileSystem(TestBloomMapFile.conf)).thenThrow(new IOException());
            reader = new BloomMapFile.Reader(dirNameSpy, TestBloomMapFile.conf, MapFile.Reader.comparator(new WritableComparator(IntWritable.class)));
            Assert.assertNull("testIOExceptionInWriterConstructor error !!!", reader.getBloomFilter());
        } catch (Exception ex) {
            Assert.fail("unexpect ex in testIOExceptionInWriterConstructor !!!");
        } finally {
            IOUtils.cleanup(null, writer, reader);
        }
    }

    /**
     * test {@link BloomMapFile.Reader#get(WritableComparable, Writable)} method
     */
    @Test
    public void testGetBloomMapFile() {
        int SIZE = 10;
        BloomMapFile.Reader reader = null;
        BloomMapFile.Writer writer = null;
        try {
            writer = new BloomMapFile.Writer(TestBloomMapFile.conf, TestBloomMapFile.TEST_FILE, MapFile.Writer.keyClass(IntWritable.class), MapFile.Writer.valueClass(Text.class));
            for (int i = 0; i < SIZE; i++) {
                writer.append(new IntWritable(i), new Text());
            }
            writer.close();
            reader = new BloomMapFile.Reader(TestBloomMapFile.TEST_FILE, TestBloomMapFile.conf, MapFile.Reader.comparator(new WritableComparator(IntWritable.class)));
            for (int i = 0; i < SIZE; i++) {
                Assert.assertNotNull("testGetBloomMapFile error !!!", reader.get(new IntWritable(i), new Text()));
            }
            Assert.assertNull("testGetBloomMapFile error !!!", reader.get(new IntWritable((SIZE + 5)), new Text()));
        } catch (Exception ex) {
            Assert.fail("unexpect ex in testGetBloomMapFile !!!");
        } finally {
            IOUtils.cleanup(null, writer, reader);
        }
    }

    /**
     * test {@code BloomMapFile.Writer} constructors
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testBloomMapFileConstructors() {
        BloomMapFile.Writer writer = null;
        try {
            FileSystem ts = FileSystem.get(TestBloomMapFile.conf);
            String testFileName = TestBloomMapFile.TEST_FILE.toString();
            writer = new BloomMapFile.Writer(TestBloomMapFile.conf, ts, testFileName, IntWritable.class, Text.class, CompressionType.BLOCK, TestBloomMapFile.defaultCodec, TestBloomMapFile.defaultProgress);
            Assert.assertNotNull("testBloomMapFileConstructors error !!!", writer);
            writer.close();
            writer = new BloomMapFile.Writer(TestBloomMapFile.conf, ts, testFileName, IntWritable.class, Text.class, CompressionType.BLOCK, TestBloomMapFile.defaultProgress);
            Assert.assertNotNull("testBloomMapFileConstructors error !!!", writer);
            writer.close();
            writer = new BloomMapFile.Writer(TestBloomMapFile.conf, ts, testFileName, IntWritable.class, Text.class, CompressionType.BLOCK);
            Assert.assertNotNull("testBloomMapFileConstructors error !!!", writer);
            writer.close();
            writer = new BloomMapFile.Writer(TestBloomMapFile.conf, ts, testFileName, IntWritable.class, Text.class, CompressionType.RECORD, TestBloomMapFile.defaultCodec, TestBloomMapFile.defaultProgress);
            Assert.assertNotNull("testBloomMapFileConstructors error !!!", writer);
            writer.close();
            writer = new BloomMapFile.Writer(TestBloomMapFile.conf, ts, testFileName, IntWritable.class, Text.class, CompressionType.RECORD, TestBloomMapFile.defaultProgress);
            Assert.assertNotNull("testBloomMapFileConstructors error !!!", writer);
            writer.close();
            writer = new BloomMapFile.Writer(TestBloomMapFile.conf, ts, testFileName, IntWritable.class, Text.class, CompressionType.RECORD);
            Assert.assertNotNull("testBloomMapFileConstructors error !!!", writer);
            writer.close();
            writer = new BloomMapFile.Writer(TestBloomMapFile.conf, ts, testFileName, WritableComparator.get(Text.class), Text.class);
            Assert.assertNotNull("testBloomMapFileConstructors error !!!", writer);
            writer.close();
        } catch (Exception ex) {
            Assert.fail("testBloomMapFileConstructors error !!!");
        } finally {
            IOUtils.cleanup(null, writer);
        }
    }

    static final Progressable defaultProgress = new Progressable() {
        @Override
        public void progress() {
        }
    };

    static final CompressionCodec defaultCodec = new CompressionCodec() {
        @Override
        public String getDefaultExtension() {
            return null;
        }

        @Override
        public Class<? extends Decompressor> getDecompressorType() {
            return null;
        }

        @Override
        public Class<? extends Compressor> getCompressorType() {
            return null;
        }

        @Override
        public CompressionOutputStream createOutputStream(OutputStream out, Compressor compressor) throws IOException {
            return Mockito.mock(CompressionOutputStream.class);
        }

        @Override
        public CompressionOutputStream createOutputStream(OutputStream out) throws IOException {
            return Mockito.mock(CompressionOutputStream.class);
        }

        @Override
        public CompressionInputStream createInputStream(InputStream in, Decompressor decompressor) throws IOException {
            return null;
        }

        @Override
        public CompressionInputStream createInputStream(InputStream in) throws IOException {
            return null;
        }

        @Override
        public Decompressor createDecompressor() {
            return null;
        }

        @Override
        public Compressor createCompressor() {
            return null;
        }
    };
}

