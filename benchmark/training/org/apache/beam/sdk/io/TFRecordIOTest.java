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
package org.apache.beam.sdk.io;


import Charsets.UTF_8;
import TFRecordIO.Read;
import TFRecordIO.Write;
import java.io.IOException;
import java.util.Collections;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.display.DisplayDataMatchers;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Charsets;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.BaseEncoding;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for TFRecordIO Read and Write transforms.
 */
@RunWith(JUnit4.class)
public class TFRecordIOTest {
    /* From https://github.com/apache/beam/blob/master/sdks/python/apache_beam/io/tfrecordio_test.py
    Created by running following code in python:
    >>> import tensorflow as tf
    >>> import base64
    >>> writer = tf.python_io.TFRecordWriter('/tmp/python_foo.tfrecord')
    >>> writer.write('foo')
    >>> writer.close()
    >>> with open('/tmp/python_foo.tfrecord', 'rb') as f:
    ...   data = base64.b64encode(f.read())
    ...   print data
     */
    private static final String FOO_RECORD_BASE64 = "AwAAAAAAAACwmUkOZm9vYYq+/g==";

    // Same as above but containing two records ['foo', 'bar']
    private static final String FOO_BAR_RECORD_BASE64 = "AwAAAAAAAACwmUkOZm9vYYq+/gMAAAAAAAAAsJlJDmJhckYA5cg=";

    private static final String BAR_FOO_RECORD_BASE64 = "AwAAAAAAAACwmUkOYmFyRgDlyAMAAAAAAAAAsJlJDmZvb2GKvv4=";

    private static final String[] FOO_RECORDS = new String[]{ "foo" };

    private static final String[] FOO_BAR_RECORDS = new String[]{ "foo", "bar" };

    private static final Iterable<String> EMPTY = Collections.emptyList();

    private static final Iterable<String> LARGE = TFRecordIOTest.makeLines(1000, 4);

    private static final Iterable<String> LARGE_RECORDS = TFRecordIOTest.makeLines(100, 100000);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public TestPipeline readPipeline = TestPipeline.create();

    @Rule
    public TestPipeline writePipeline = TestPipeline.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testReadNamed() {
        writePipeline.enableAbandonedNodeEnforcement(false);
        Assert.assertEquals("TFRecordIO.Read/Read.out", writePipeline.apply(TFRecordIO.read().from("foo.*").withoutValidation()).getName());
        Assert.assertEquals("MyRead/Read.out", writePipeline.apply("MyRead", TFRecordIO.read().from("foo.*").withoutValidation()).getName());
    }

    @Test
    public void testReadDisplayData() {
        TFRecordIO.Read read = TFRecordIO.read().from("foo.*").withCompression(Compression.GZIP).withoutValidation();
        DisplayData displayData = DisplayData.from(read);
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("filePattern", "foo.*"));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("compressionType", Compression.GZIP.toString()));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("validation", false));
    }

    @Test
    public void testWriteDisplayData() {
        TFRecordIO.Write write = TFRecordIO.write().to("/foo").withSuffix("bar").withShardNameTemplate("-SS-of-NN-").withNumShards(100).withCompression(Compression.GZIP);
        DisplayData displayData = DisplayData.from(write);
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("filePrefix", "/foo"));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("fileSuffix", "bar"));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("shardNameTemplate", "-SS-of-NN-"));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("numShards", 100));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("compressionType", Compression.GZIP.toString()));
    }

    @Test
    @Category(NeedsRunner.class)
    public void testReadOne() throws Exception {
        runTestRead(TFRecordIOTest.FOO_RECORD_BASE64, TFRecordIOTest.FOO_RECORDS);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testReadTwo() throws Exception {
        runTestRead(TFRecordIOTest.FOO_BAR_RECORD_BASE64, TFRecordIOTest.FOO_BAR_RECORDS);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testWriteOne() throws Exception {
        runTestWrite(TFRecordIOTest.FOO_RECORDS, TFRecordIOTest.FOO_RECORD_BASE64);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testWriteTwo() throws Exception {
        runTestWrite(TFRecordIOTest.FOO_BAR_RECORDS, TFRecordIOTest.FOO_BAR_RECORD_BASE64, TFRecordIOTest.BAR_FOO_RECORD_BASE64);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testReadInvalidRecord() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Not a valid TFRecord. Fewer than 12 bytes.");
        System.out.println("abr".getBytes(UTF_8).length);
        runTestRead("bar".getBytes(UTF_8), new String[0]);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testReadInvalidLengthMask() throws Exception {
        expectedException.expectCause(CoreMatchers.instanceOf(IOException.class));
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(CoreMatchers.containsString("Mismatch of length mask")));
        byte[] data = BaseEncoding.base64().decode(TFRecordIOTest.FOO_RECORD_BASE64);
        data[9] += ((byte) (1));
        runTestRead(data, TFRecordIOTest.FOO_RECORDS);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testReadInvalidDataMask() throws Exception {
        expectedException.expectCause(CoreMatchers.instanceOf(IOException.class));
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(CoreMatchers.containsString("Mismatch of data mask")));
        byte[] data = BaseEncoding.base64().decode(TFRecordIOTest.FOO_RECORD_BASE64);
        data[16] += ((byte) (1));
        runTestRead(data, TFRecordIOTest.FOO_RECORDS);
    }

    @Test
    @Category(NeedsRunner.class)
    public void runTestRoundTrip() throws IOException {
        runTestRoundTrip(TFRecordIOTest.LARGE, 10, ".tfrecords", Compression.UNCOMPRESSED, Compression.UNCOMPRESSED);
    }

    @Test
    @Category(NeedsRunner.class)
    public void runTestRoundTripWithEmptyData() throws IOException {
        runTestRoundTrip(TFRecordIOTest.EMPTY, 10, ".tfrecords", Compression.UNCOMPRESSED, Compression.UNCOMPRESSED);
    }

    @Test
    @Category(NeedsRunner.class)
    public void runTestRoundTripWithOneShards() throws IOException {
        runTestRoundTrip(TFRecordIOTest.LARGE, 1, ".tfrecords", Compression.UNCOMPRESSED, Compression.UNCOMPRESSED);
    }

    @Test
    @Category(NeedsRunner.class)
    public void runTestRoundTripWithSuffix() throws IOException {
        runTestRoundTrip(TFRecordIOTest.LARGE, 10, ".suffix", Compression.UNCOMPRESSED, Compression.UNCOMPRESSED);
    }

    @Test
    @Category(NeedsRunner.class)
    public void runTestRoundTripGzip() throws IOException {
        runTestRoundTrip(TFRecordIOTest.LARGE, 10, ".tfrecords", Compression.GZIP, Compression.GZIP);
    }

    @Test
    @Category(NeedsRunner.class)
    public void runTestRoundTripZlib() throws IOException {
        runTestRoundTrip(TFRecordIOTest.LARGE, 10, ".tfrecords", Compression.DEFLATE, Compression.DEFLATE);
    }

    @Test
    @Category(NeedsRunner.class)
    public void runTestRoundTripUncompressedFilesWithAuto() throws IOException {
        runTestRoundTrip(TFRecordIOTest.LARGE, 10, ".tfrecords", Compression.UNCOMPRESSED, Compression.AUTO);
    }

    @Test
    @Category(NeedsRunner.class)
    public void runTestRoundTripGzipFilesWithAuto() throws IOException {
        runTestRoundTrip(TFRecordIOTest.LARGE, 10, ".tfrecords", Compression.GZIP, Compression.AUTO);
    }

    @Test
    @Category(NeedsRunner.class)
    public void runTestRoundTripZlibFilesWithAuto() throws IOException {
        runTestRoundTrip(TFRecordIOTest.LARGE, 10, ".tfrecords", Compression.DEFLATE, Compression.AUTO);
    }

    @Test
    @Category(NeedsRunner.class)
    public void runTestRoundTripLargeRecords() throws IOException {
        runTestRoundTrip(TFRecordIOTest.LARGE_RECORDS, 10, ".tfrecords", Compression.UNCOMPRESSED, Compression.UNCOMPRESSED);
    }

    @Test
    @Category(NeedsRunner.class)
    public void runTestRoundTripLargeRecordsGzip() throws IOException {
        runTestRoundTrip(TFRecordIOTest.LARGE_RECORDS, 10, ".tfrecords", Compression.GZIP, Compression.GZIP);
    }

    static class ByteArrayToString extends DoFn<byte[], String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output(new String(c.element(), Charsets.UTF_8));
        }
    }

    static class StringToByteArray extends DoFn<String, byte[]> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output(c.element().getBytes(UTF_8));
        }
    }
}

