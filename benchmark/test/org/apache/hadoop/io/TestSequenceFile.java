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


import CommonConfigurationKeys.IO_SERIALIZATIONS_KEY;
import CompressionType.BLOCK;
import CompressionType.NONE;
import CompressionType.RECORD;
import SequenceFile.Reader;
import SequenceFile.Writer;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import org.apache.hadoop.fs.Configuration;
import org.apache.hadoop.io.SequenceFile.Metadata;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.io.serializer.avro.AvroReflectSerialization;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Support for flat files of binary key/value pairs.
 */
public class TestSequenceFile {
    private static final Logger LOG = LoggerFactory.getLogger(TestSequenceFile.class);

    private Configuration conf = new Configuration();

    /**
     * Unit tests for SequenceFile.
     */
    @Test
    public void testZlibSequenceFile() throws Exception {
        TestSequenceFile.LOG.info("Testing SequenceFile with DefaultCodec");
        compressedSeqFileTest(new DefaultCodec());
        TestSequenceFile.LOG.info("Successfully tested SequenceFile with DefaultCodec");
    }

    /**
     * Unit tests for SequenceFile metadata.
     */
    @Test
    public void testSequenceFileMetadata() throws Exception {
        TestSequenceFile.LOG.info("Testing SequenceFile with metadata");
        int count = 1024 * 10;
        CompressionCodec codec = new DefaultCodec();
        Path file = new Path(GenericTestUtils.getTempPath("test.seq.metadata"));
        Path sortedFile = new Path(GenericTestUtils.getTempPath("test.sorted.seq.metadata"));
        Path recordCompressedFile = new Path(GenericTestUtils.getTempPath("test.rc.seq.metadata"));
        Path blockCompressedFile = new Path(GenericTestUtils.getTempPath("test.bc.seq.metadata"));
        FileSystem fs = FileSystem.getLocal(conf);
        SequenceFile.Metadata theMetadata = new SequenceFile.Metadata();
        theMetadata.set(new Text("name_1"), new Text("value_1"));
        theMetadata.set(new Text("name_2"), new Text("value_2"));
        theMetadata.set(new Text("name_3"), new Text("value_3"));
        theMetadata.set(new Text("name_4"), new Text("value_4"));
        int seed = new Random().nextInt();
        try {
            // SequenceFile.Writer
            writeMetadataTest(fs, count, seed, file, NONE, null, theMetadata);
            SequenceFile.Metadata aMetadata = readMetadata(fs, file);
            if (!(theMetadata.equals(aMetadata))) {
                TestSequenceFile.LOG.info(("The original metadata:\n" + (theMetadata.toString())));
                TestSequenceFile.LOG.info(("The retrieved metadata:\n" + (aMetadata.toString())));
                throw new RuntimeException(("metadata not match:  " + 1));
            }
            // SequenceFile.RecordCompressWriter
            writeMetadataTest(fs, count, seed, recordCompressedFile, RECORD, codec, theMetadata);
            aMetadata = readMetadata(fs, recordCompressedFile);
            if (!(theMetadata.equals(aMetadata))) {
                TestSequenceFile.LOG.info(("The original metadata:\n" + (theMetadata.toString())));
                TestSequenceFile.LOG.info(("The retrieved metadata:\n" + (aMetadata.toString())));
                throw new RuntimeException(("metadata not match:  " + 2));
            }
            // SequenceFile.BlockCompressWriter
            writeMetadataTest(fs, count, seed, blockCompressedFile, BLOCK, codec, theMetadata);
            aMetadata = readMetadata(fs, blockCompressedFile);
            if (!(theMetadata.equals(aMetadata))) {
                TestSequenceFile.LOG.info(("The original metadata:\n" + (theMetadata.toString())));
                TestSequenceFile.LOG.info(("The retrieved metadata:\n" + (aMetadata.toString())));
                throw new RuntimeException(("metadata not match:  " + 3));
            }
            // SequenceFile.Sorter
            sortMetadataTest(fs, file, sortedFile, theMetadata);
            aMetadata = readMetadata(fs, recordCompressedFile);
            if (!(theMetadata.equals(aMetadata))) {
                TestSequenceFile.LOG.info(("The original metadata:\n" + (theMetadata.toString())));
                TestSequenceFile.LOG.info(("The retrieved metadata:\n" + (aMetadata.toString())));
                throw new RuntimeException(("metadata not match:  " + 4));
            }
        } finally {
            close();
        }
        TestSequenceFile.LOG.info("Successfully tested SequenceFile with metadata");
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testClose() throws IOException {
        Configuration conf = new Configuration();
        LocalFileSystem fs = FileSystem.getLocal(conf);
        // create a sequence file 1
        Path path1 = new Path(GenericTestUtils.getTempPath("test1.seq"));
        SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, path1, Text.class, NullWritable.class, BLOCK);
        writer.append(new Text("file1-1"), NullWritable.get());
        writer.append(new Text("file1-2"), NullWritable.get());
        writer.close();
        Path path2 = new Path(GenericTestUtils.getTempPath("test2.seq"));
        writer = SequenceFile.createWriter(fs, conf, path2, Text.class, NullWritable.class, BLOCK);
        writer.append(new Text("file2-1"), NullWritable.get());
        writer.append(new Text("file2-2"), NullWritable.get());
        writer.close();
        // Create a reader which uses 4 BuiltInZLibInflater instances
        SequenceFile.Reader reader = new SequenceFile.Reader(fs, path1, conf);
        // Returns the 4 BuiltInZLibInflater instances to the CodecPool
        reader.close();
        // The second close _could_ erroneously returns the same
        // 4 BuiltInZLibInflater instances to the CodecPool again
        reader.close();
        // The first reader gets 4 BuiltInZLibInflater instances from the CodecPool
        SequenceFile.Reader reader1 = new SequenceFile.Reader(fs, path1, conf);
        // read first value from reader1
        Text text = new Text();
        reader1.next(text);
        Assert.assertEquals("file1-1", text.toString());
        // The second reader _could_ get the same 4 BuiltInZLibInflater
        // instances from the CodePool as reader1
        SequenceFile.Reader reader2 = new SequenceFile.Reader(fs, path2, conf);
        // read first value from reader2
        reader2.next(text);
        Assert.assertEquals("file2-1", text.toString());
        // read second value from reader1
        reader1.next(text);
        Assert.assertEquals("file1-2", text.toString());
        // read second value from reader2 (this throws an exception)
        reader2.next(text);
        Assert.assertEquals("file2-2", text.toString());
        Assert.assertFalse(reader1.next(text));
        Assert.assertFalse(reader2.next(text));
    }

    /**
     * Test that makes sure the FileSystem passed to createWriter
     *
     * @throws Exception
     * 		
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testCreateUsesFsArg() throws Exception {
        FileSystem fs = FileSystem.getLocal(conf);
        FileSystem spyFs = Mockito.spy(fs);
        Path p = new Path(GenericTestUtils.getTempPath("testCreateUsesFSArg.seq"));
        SequenceFile.Writer writer = SequenceFile.createWriter(spyFs, conf, p, NullWritable.class, NullWritable.class);
        writer.close();
        Mockito.verify(spyFs).getDefaultReplication(p);
    }

    private static class TestFSDataInputStream extends FSDataInputStream {
        private boolean closed = false;

        private TestFSDataInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            close();
        }

        public boolean isClosed() {
            return closed;
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testCloseForErroneousSequenceFile() throws IOException {
        Configuration conf = new Configuration();
        LocalFileSystem fs = FileSystem.getLocal(conf);
        // create an empty file (which is not a valid sequence file)
        Path path = new Path(GenericTestUtils.getTempPath("broken.seq"));
        close();
        // try to create SequenceFile.Reader
        final TestSequenceFile.TestFSDataInputStream[] openedFile = new TestSequenceFile.TestFSDataInputStream[1];
        try {
            new SequenceFile.Reader(fs, path, conf) {
                // this method is called by the SequenceFile.Reader constructor, overwritten, so we can access the opened file
                @Override
                protected org.apache.hadoop.fs.FSDataInputStream openFile(FileSystem fs, Path file, int bufferSize, long length) throws IOException {
                    final InputStream in = super.openFile(fs, file, bufferSize, length);
                    openedFile[0] = new TestSequenceFile.TestFSDataInputStream(in);
                    return openedFile[0];
                }
            };
            Assert.fail("IOException expected.");
        } catch (IOException expected) {
        }
        Assert.assertNotNull((path + " should have been opened."), openedFile[0]);
        Assert.assertTrue((("InputStream for " + path) + " should have been closed."), openedFile[0].isClosed());
    }

    /**
     * Test to makes sure zero length sequence file is handled properly while
     * initializing.
     */
    @Test
    public void testInitZeroLengthSequenceFile() throws IOException {
        Configuration conf = new Configuration();
        LocalFileSystem fs = FileSystem.getLocal(conf);
        // create an empty file (which is not a valid sequence file)
        Path path = new Path(GenericTestUtils.getTempPath("zerolength.seq"));
        close();
        try {
            new SequenceFile.Reader(conf, Reader.file(path));
            Assert.fail("IOException expected.");
        } catch (IOException expected) {
            Assert.assertTrue((expected instanceof EOFException));
        }
    }

    /**
     * Test that makes sure createWriter succeeds on a file that was
     * already created
     *
     * @throws IOException
     * 		
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testCreateWriterOnExistingFile() throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.getLocal(conf);
        Path name = new Path(new Path(GenericTestUtils.getTempPath("createWriterOnExistingFile")), "file");
        fs.create(name);
        SequenceFile.createWriter(fs, conf, name, RandomDatum.class, RandomDatum.class, 512, ((short) (1)), 4096, false, NONE, null, new Metadata());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testRecursiveSeqFileCreate() throws IOException {
        FileSystem fs = FileSystem.getLocal(conf);
        Path name = new Path(new Path(GenericTestUtils.getTempPath("recursiveCreateDir")), "file");
        boolean createParent = false;
        try {
            SequenceFile.createWriter(fs, conf, name, RandomDatum.class, RandomDatum.class, 512, ((short) (1)), 4096, createParent, NONE, null, new Metadata());
            Assert.fail("Expected an IOException due to missing parent");
        } catch (IOException ioe) {
            // Expected
        }
        createParent = true;
        SequenceFile.createWriter(fs, conf, name, RandomDatum.class, RandomDatum.class, 512, ((short) (1)), 4096, createParent, NONE, null, new Metadata());
        // should succeed, fails if exception thrown
    }

    @Test
    public void testSerializationAvailability() throws IOException {
        Configuration conf = new Configuration();
        Path path = new Path(GenericTestUtils.getTempPath("serializationAvailability"));
        // Check if any serializers aren't found.
        try {
            SequenceFile.createWriter(conf, Writer.file(path), Writer.keyClass(String.class), Writer.valueClass(NullWritable.class));
            // Note: This may also fail someday if JavaSerialization
            // is activated by default.
            Assert.fail("Must throw IOException for missing serializer for the Key class");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().startsWith((("Could not find a serializer for the Key class: '" + (String.class.getName())) + "'.")));
        }
        try {
            SequenceFile.createWriter(conf, Writer.file(path), Writer.keyClass(NullWritable.class), Writer.valueClass(String.class));
            // Note: This may also fail someday if JavaSerialization
            // is activated by default.
            Assert.fail("Must throw IOException for missing serializer for the Value class");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().startsWith((("Could not find a serializer for the Value class: '" + (String.class.getName())) + "'.")));
        }
        // Write a simple file to test deserialization failures with
        writeTest(FileSystem.get(conf), 1, 1, path, NONE, null);
        // Remove Writable serializations, to enforce error.
        conf.setStrings(IO_SERIALIZATIONS_KEY, AvroReflectSerialization.class.getName());
        // Now check if any deserializers aren't found.
        try {
            new SequenceFile.Reader(conf, Reader.file(path));
            Assert.fail("Must throw IOException for missing deserializer for the Key class");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().startsWith((("Could not find a deserializer for the Key class: '" + (RandomDatum.class.getName())) + "'.")));
        }
    }
}

