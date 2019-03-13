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


import BoundedReader.SPLIT_POINTS_UNKNOWN;
import CompressionMode.BZIP2;
import CompressionMode.DEFLATE;
import CompressionMode.GZIP;
import CompressionMode.UNCOMPRESSED;
import CompressionMode.ZIP;
import CompressionMode.ZSTD;
import DisplayData.Builder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.BoundedSource.BoundedReader;
import org.apache.beam.sdk.io.CompressedSource.CompressedReader;
import org.apache.beam.sdk.io.CompressedSource.CompressionMode;
import org.apache.beam.sdk.io.FileBasedSource.FileBasedReader;
import org.apache.beam.sdk.io.fs.MatchResult.Metadata;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.testing.SourceTestUtils;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.display.DisplayDataMatchers;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.HashMultiset;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.Files;
import org.apache.beam.vendor.guava.v20_0.com.google.common.primitives.Bytes;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for CompressedSource.
 */
@RunWith(JUnit4.class)
public class CompressedSourceTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Test reading nonempty input with gzip.
     */
    @Test
    public void testReadGzip() throws Exception {
        byte[] input = generateInput(5000);
        runReadTest(input, GZIP);
    }

    /**
     * Test splittability of files in AUTO mode.
     */
    @Test
    public void testAutoSplittable() throws Exception {
        CompressedSource<Byte> source;
        // GZip files are not splittable
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.gz", 1));
        Assert.assertFalse(source.isSplittable());
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.GZ", 1));
        Assert.assertFalse(source.isSplittable());
        // BZ2 files are not splittable
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.bz2", 1));
        Assert.assertFalse(source.isSplittable());
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.BZ2", 1));
        Assert.assertFalse(source.isSplittable());
        // ZIP files are not splittable
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.zip", 1));
        Assert.assertFalse(source.isSplittable());
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.ZIP", 1));
        Assert.assertFalse(source.isSplittable());
        // ZSTD files are not splittable
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.zst", 1));
        Assert.assertFalse(source.isSplittable());
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.ZST", 1));
        Assert.assertFalse(source.isSplittable());
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.zstd", 1));
        Assert.assertFalse(source.isSplittable());
        // DEFLATE files are not splittable
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.deflate", 1));
        Assert.assertFalse(source.isSplittable());
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.DEFLATE", 1));
        Assert.assertFalse(source.isSplittable());
        // Other extensions are assumed to be splittable.
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.txt", 1));
        Assert.assertTrue(source.isSplittable());
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.csv", 1));
        Assert.assertTrue(source.isSplittable());
    }

    /**
     * Test splittability of files in GZIP mode -- none should be splittable.
     */
    @Test
    public void testGzipSplittable() throws Exception {
        CompressedSource<Byte> source;
        // GZip files are not splittable
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.gz", 1)).withDecompression(GZIP);
        Assert.assertFalse(source.isSplittable());
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.GZ", 1)).withDecompression(GZIP);
        Assert.assertFalse(source.isSplittable());
        // Other extensions are also not splittable.
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.txt", 1)).withDecompression(GZIP);
        Assert.assertFalse(source.isSplittable());
        source = CompressedSource.from(new CompressedSourceTest.ByteSource("input.csv", 1)).withDecompression(GZIP);
        Assert.assertFalse(source.isSplittable());
    }

    /**
     * Test reading nonempty input with bzip2.
     */
    @Test
    public void testReadBzip2() throws Exception {
        byte[] input = generateInput(5000);
        runReadTest(input, BZIP2);
    }

    /**
     * Test reading nonempty input with zip.
     */
    @Test
    public void testReadZip() throws Exception {
        byte[] input = generateInput(5000);
        runReadTest(input, ZIP);
    }

    /**
     * Test reading nonempty input with deflate.
     */
    @Test
    public void testReadDeflate() throws Exception {
        byte[] input = generateInput(5000);
        runReadTest(input, DEFLATE);
    }

    /**
     * Test reading empty input with gzip.
     */
    @Test
    public void testEmptyReadGzip() throws Exception {
        byte[] input = generateInput(0);
        runReadTest(input, GZIP);
    }

    /**
     * Test reading empty input with zstd.
     */
    @Test
    public void testEmptyReadZstd() throws Exception {
        byte[] input = generateInput(0);
        runReadTest(input, ZSTD);
    }

    /**
     * Test a concatenation of gzip files is correctly decompressed.
     *
     * <p>A concatenation of gzip files as one file is a valid gzip file and should decompress to be
     * the concatenation of those individual files.
     */
    @Test
    public void testReadConcatenatedGzip() throws IOException {
        byte[] header = "a,b,c\n".getBytes(StandardCharsets.UTF_8);
        byte[] body = "1,2,3\n4,5,6\n7,8,9\n".getBytes(StandardCharsets.UTF_8);
        byte[] expected = CompressedSourceTest.concat(header, body);
        byte[] totalGz = CompressedSourceTest.concat(CompressedSourceTest.compressGzip(header), CompressedSourceTest.compressGzip(body));
        File tmpFile = tmpFolder.newFile();
        try (FileOutputStream os = new FileOutputStream(tmpFile)) {
            os.write(totalGz);
        }
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(tmpFile.getAbsolutePath(), 1)).withDecompression(GZIP);
        List<Byte> actual = SourceTestUtils.readFromSource(source, PipelineOptionsFactory.create());
        Assert.assertEquals(Bytes.asList(expected), actual);
    }

    /**
     * Test a bzip2 file containing multiple streams is correctly decompressed.
     *
     * <p>A bzip2 file may contain multiple streams and should decompress as the concatenation of
     * those streams.
     */
    @Test
    public void testReadMultiStreamBzip2() throws IOException {
        CompressionMode mode = CompressionMode.BZIP2;
        byte[] input1 = generateInput(5, 587973);
        byte[] input2 = generateInput(5, 387374);
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        try (OutputStream os = getOutputStreamForMode(mode, stream1)) {
            os.write(input1);
        }
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        try (OutputStream os = getOutputStreamForMode(mode, stream2)) {
            os.write(input2);
        }
        File tmpFile = tmpFolder.newFile();
        try (OutputStream os = new FileOutputStream(tmpFile)) {
            os.write(stream1.toByteArray());
            os.write(stream2.toByteArray());
        }
        byte[] output = Bytes.concat(input1, input2);
        verifyReadContents(output, tmpFile, mode);
    }

    /**
     * Test reading empty input with bzip2.
     */
    @Test
    public void testCompressedReadBzip2() throws Exception {
        byte[] input = generateInput(0);
        runReadTest(input, BZIP2);
    }

    /**
     * Test reading empty input with zstd.
     */
    @Test
    public void testCompressedReadZstd() throws Exception {
        byte[] input = generateInput(0);
        runReadTest(input, ZSTD);
    }

    /**
     * Test reading according to filepattern when the file is gzipped.
     */
    @Test
    public void testCompressedAccordingToFilepatternGzip() throws Exception {
        byte[] input = generateInput(100);
        File tmpFile = tmpFolder.newFile("test.gz");
        writeFile(tmpFile, input, GZIP);
        /* default auto decompression factory */
        verifyReadContents(input, tmpFile, null);
    }

    /**
     * Test reading according to filepattern when the file is bzipped.
     */
    @Test
    public void testCompressedAccordingToFilepatternBzip2() throws Exception {
        byte[] input = generateInput(100);
        File tmpFile = tmpFolder.newFile("test.bz2");
        writeFile(tmpFile, input, BZIP2);
        /* default auto decompression factory */
        verifyReadContents(input, tmpFile, null);
    }

    /**
     * Test reading according to filepattern when the file is zstd compressed.
     */
    @Test
    public void testCompressedAccordingToFilepatternZstd() throws Exception {
        byte[] input = generateInput(100);
        File tmpFile = tmpFolder.newFile("test.zst");
        writeFile(tmpFile, input, ZSTD);
        /* default auto decompression factory */
        verifyReadContents(input, tmpFile, null);
    }

    /**
     * Test reading multiple files with different compression.
     */
    @Test
    public void testHeterogeneousCompression() throws Exception {
        String baseName = "test-input";
        // Expected data
        byte[] generated = generateInput(1000);
        List<Byte> expected = new ArrayList<>();
        // Every sort of compression
        File uncompressedFile = tmpFolder.newFile((baseName + ".bin"));
        generated = generateInput(1000, 1);
        Files.write(generated, uncompressedFile);
        expected.addAll(Bytes.asList(generated));
        File gzipFile = tmpFolder.newFile((baseName + ".gz"));
        generated = generateInput(1000, 2);
        writeFile(gzipFile, generated, GZIP);
        expected.addAll(Bytes.asList(generated));
        File bzip2File = tmpFolder.newFile((baseName + ".bz2"));
        generated = generateInput(1000, 3);
        writeFile(bzip2File, generated, BZIP2);
        expected.addAll(Bytes.asList(generated));
        File zstdFile = tmpFolder.newFile((baseName + ".zst"));
        generated = generateInput(1000, 4);
        writeFile(zstdFile, generated, ZSTD);
        expected.addAll(Bytes.asList(generated));
        String filePattern = new File(tmpFolder.getRoot().toString(), (baseName + ".*")).toString();
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(filePattern, 1));
        List<Byte> actual = SourceTestUtils.readFromSource(source, PipelineOptionsFactory.create());
        Assert.assertEquals(HashMultiset.create(actual), HashMultiset.create(expected));
    }

    @Test
    public void testUncompressedFileWithAutoIsSplittable() throws Exception {
        String baseName = "test-input";
        File uncompressedFile = tmpFolder.newFile((baseName + ".bin"));
        Files.write(generateInput(10), uncompressedFile);
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(uncompressedFile.getPath(), 1));
        Assert.assertTrue(source.isSplittable());
        SourceTestUtils.assertSplitAtFractionExhaustive(source, PipelineOptionsFactory.create());
    }

    @Test
    public void testUncompressedFileWithUncompressedIsSplittable() throws Exception {
        String baseName = "test-input";
        File uncompressedFile = tmpFolder.newFile((baseName + ".bin"));
        Files.write(generateInput(10), uncompressedFile);
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(uncompressedFile.getPath(), 1)).withDecompression(UNCOMPRESSED);
        Assert.assertTrue(source.isSplittable());
        SourceTestUtils.assertSplitAtFractionExhaustive(source, PipelineOptionsFactory.create());
    }

    @Test
    public void testGzipFileIsNotSplittable() throws Exception {
        String baseName = "test-input";
        File compressedFile = tmpFolder.newFile((baseName + ".gz"));
        writeFile(compressedFile, generateInput(10), GZIP);
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(compressedFile.getPath(), 1));
        Assert.assertFalse(source.isSplittable());
    }

    @Test
    public void testBzip2FileIsNotSplittable() throws Exception {
        String baseName = "test-input";
        File compressedFile = tmpFolder.newFile((baseName + ".bz2"));
        writeFile(compressedFile, generateInput(10), BZIP2);
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(compressedFile.getPath(), 1));
        Assert.assertFalse(source.isSplittable());
    }

    @Test
    public void testZstdFileIsNotSplittable() throws Exception {
        String baseName = "test-input";
        File compressedFile = tmpFolder.newFile((baseName + ".zst"));
        writeFile(compressedFile, generateInput(10), ZSTD);
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(compressedFile.getPath(), 1));
        Assert.assertFalse(source.isSplittable());
    }

    /**
     * Test reading an uncompressed file with {@link CompressionMode#GZIP}, since we must support this
     * due to properties of services that we read from.
     */
    @Test
    public void testFalseGzipStream() throws Exception {
        byte[] input = generateInput(1000);
        File tmpFile = tmpFolder.newFile("test.gz");
        Files.write(input, tmpFile);
        verifyReadContents(input, tmpFile, GZIP);
    }

    /**
     * Test reading an uncompressed file with {@link CompressionMode#BZIP2}, and show that we fail.
     */
    @Test
    public void testFalseBzip2Stream() throws Exception {
        byte[] input = generateInput(1000);
        File tmpFile = tmpFolder.newFile("test.bz2");
        Files.write(input, tmpFile);
        thrown.expectMessage("Stream is not in the BZip2 format");
        verifyReadContents(input, tmpFile, BZIP2);
    }

    /**
     * Test reading an uncompressed file with {@link Compression#ZSTD}, and show that we fail.
     */
    @Test
    public void testFalseZstdStream() throws Exception {
        byte[] input = generateInput(1000);
        File tmpFile = tmpFolder.newFile("test.zst");
        Files.write(input, tmpFile);
        thrown.expectMessage("Decompression error: Unknown frame descriptor");
        verifyReadContents(input, tmpFile, ZSTD);
    }

    /**
     * Test reading an empty input file with gzip; it must be interpreted as uncompressed because the
     * gzip header is two bytes.
     */
    @Test
    public void testEmptyReadGzipUncompressed() throws Exception {
        byte[] input = generateInput(0);
        File tmpFile = tmpFolder.newFile("test.gz");
        Files.write(input, tmpFile);
        verifyReadContents(input, tmpFile, GZIP);
    }

    /**
     * Test reading single byte input with gzip; it must be interpreted as uncompressed because the
     * gzip header is two bytes.
     */
    @Test
    public void testOneByteReadGzipUncompressed() throws Exception {
        byte[] input = generateInput(1);
        File tmpFile = tmpFolder.newFile("test.gz");
        Files.write(input, tmpFile);
        verifyReadContents(input, tmpFile, GZIP);
    }

    /**
     * Test reading multiple files.
     */
    @Test
    public void testCompressedReadMultipleFiles() throws Exception {
        int numFiles = 3;
        String baseName = "test_input-";
        String filePattern = new File(tmpFolder.getRoot().toString(), (baseName + "*")).toString();
        List<Byte> expected = new ArrayList<>();
        for (int i = 0; i < numFiles; i++) {
            byte[] generated = generateInput(100);
            File tmpFile = tmpFolder.newFile((baseName + i));
            writeFile(tmpFile, generated, GZIP);
            expected.addAll(Bytes.asList(generated));
        }
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(filePattern, 1)).withDecompression(GZIP);
        List<Byte> actual = SourceTestUtils.readFromSource(source, PipelineOptionsFactory.create());
        Assert.assertEquals(HashMultiset.create(expected), HashMultiset.create(actual));
    }

    @Test
    public void testDisplayData() {
        CompressedSourceTest.ByteSource inputSource = new CompressedSourceTest.ByteSource("foobar.txt", 1) {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        CompressedSource<?> compressedSource = CompressedSource.from(inputSource);
        CompressedSource<?> gzipSource = compressedSource.withDecompression(GZIP);
        DisplayData compressedSourceDisplayData = DisplayData.from(compressedSource);
        DisplayData gzipDisplayData = DisplayData.from(gzipSource);
        Assert.assertThat(compressedSourceDisplayData, DisplayDataMatchers.hasDisplayItem("compressionMode"));
        Assert.assertThat(gzipDisplayData, DisplayDataMatchers.hasDisplayItem("compressionMode", GZIP.toString()));
        Assert.assertThat(compressedSourceDisplayData, DisplayDataMatchers.hasDisplayItem("source", inputSource.getClass()));
        Assert.assertThat(compressedSourceDisplayData, DisplayDataMatchers.includesDisplayDataFor("source", inputSource));
    }

    /**
     * Extend of {@link ZipOutputStream} that splits up bytes into multiple entries.
     */
    private static class TestZipOutputStream extends OutputStream {
        private ZipOutputStream zipOutputStream;

        private long offset = 0;

        private int entry = 0;

        public TestZipOutputStream(OutputStream stream) throws IOException {
            super();
            zipOutputStream = new ZipOutputStream(stream);
            zipOutputStream.putNextEntry(new ZipEntry(String.format("entry-%05d", entry)));
        }

        @Override
        public void write(int b) throws IOException {
            zipOutputStream.write(b);
            (offset)++;
            if (((offset) % 100) == 0) {
                (entry)++;
                zipOutputStream.putNextEntry(new ZipEntry(String.format("entry-%05d", entry)));
            }
        }

        @Override
        public void close() throws IOException {
            zipOutputStream.closeEntry();
            super.close();
        }
    }

    /**
     * Dummy source for use in tests.
     */
    private static class ByteSource extends FileBasedSource<Byte> {
        public ByteSource(String fileOrPatternSpec, long minBundleSize) {
            super(StaticValueProvider.of(fileOrPatternSpec), minBundleSize);
        }

        public ByteSource(Metadata metadata, long minBundleSize, long startOffset, long endOffset) {
            super(metadata, minBundleSize, startOffset, endOffset);
        }

        @Override
        protected CompressedSourceTest.ByteSource createForSubrangeOfFile(Metadata metadata, long start, long end) {
            return new CompressedSourceTest.ByteSource(metadata, getMinBundleSize(), start, end);
        }

        @Override
        protected FileBasedReader<Byte> createSingleFileReader(PipelineOptions options) {
            return new CompressedSourceTest.ByteSource.ByteReader(this);
        }

        @Override
        public Coder<Byte> getOutputCoder() {
            return SerializableCoder.of(Byte.class);
        }

        private static class ByteReader extends FileBasedReader<Byte> {
            ByteBuffer buff = ByteBuffer.allocate(1);

            Byte current;

            long offset;

            ReadableByteChannel channel;

            public ByteReader(CompressedSourceTest.ByteSource source) {
                super(source);
                offset = (getStartOffset()) - 1;
            }

            @Override
            public Byte getCurrent() throws NoSuchElementException {
                return current;
            }

            @Override
            protected boolean isAtSplitPoint() {
                return true;
            }

            @Override
            protected void startReading(ReadableByteChannel channel) throws IOException {
                this.channel = channel;
            }

            @Override
            protected boolean readNextRecord() throws IOException {
                buff.clear();
                if ((channel.read(buff)) != 1) {
                    return false;
                }
                current = buff.get(0);
                offset += 1;
                return true;
            }

            @Override
            protected long getCurrentOffset() {
                return offset;
            }

            @Override
            public Instant getCurrentTimestamp() throws NoSuchElementException {
                return new Instant(getCurrentOffset());
            }
        }
    }

    private static class ExtractIndexFromTimestamp extends DoFn<Byte, KV<Long, Byte>> {
        @ProcessElement
        public void processElement(ProcessContext context) {
            context.output(KV.of(context.timestamp().getMillis(), context.element()));
        }
    }

    @Test
    public void testEmptyGzipProgress() throws IOException {
        File tmpFile = tmpFolder.newFile("empty.gz");
        String filename = tmpFile.toPath().toString();
        writeFile(tmpFile, new byte[0], GZIP);
        PipelineOptions options = PipelineOptionsFactory.create();
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(filename, 1));
        try (BoundedReader<Byte> readerOrig = source.createReader(options)) {
            Assert.assertThat(readerOrig, Matchers.instanceOf(CompressedReader.class));
            CompressedReader<Byte> reader = ((CompressedReader<Byte>) (readerOrig));
            // before starting
            Assert.assertEquals(0.0, reader.getFractionConsumed(), 1.0E-6);
            Assert.assertEquals(0, reader.getSplitPointsConsumed());
            Assert.assertEquals(1, reader.getSplitPointsRemaining());
            // confirm empty
            Assert.assertFalse(reader.start());
            // after reading empty source
            Assert.assertEquals(1.0, reader.getFractionConsumed(), 1.0E-6);
            Assert.assertEquals(0, reader.getSplitPointsConsumed());
            Assert.assertEquals(0, reader.getSplitPointsRemaining());
        }
    }

    @Test
    public void testGzipProgress() throws IOException {
        int numRecords = 3;
        File tmpFile = tmpFolder.newFile("nonempty.gz");
        String filename = tmpFile.toPath().toString();
        writeFile(tmpFile, new byte[numRecords], GZIP);
        PipelineOptions options = PipelineOptionsFactory.create();
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(filename, 1));
        try (BoundedReader<Byte> readerOrig = source.createReader(options)) {
            Assert.assertThat(readerOrig, Matchers.instanceOf(CompressedReader.class));
            CompressedReader<Byte> reader = ((CompressedReader<Byte>) (readerOrig));
            // before starting
            Assert.assertEquals(0.0, reader.getFractionConsumed(), 1.0E-6);
            Assert.assertEquals(0, reader.getSplitPointsConsumed());
            Assert.assertEquals(1, reader.getSplitPointsRemaining());
            // confirm has three records
            for (int i = 0; i < numRecords; ++i) {
                if (i == 0) {
                    Assert.assertTrue(reader.start());
                } else {
                    Assert.assertTrue(reader.advance());
                }
                Assert.assertEquals(0, reader.getSplitPointsConsumed());
                Assert.assertEquals(1, reader.getSplitPointsRemaining());
            }
            Assert.assertFalse(reader.advance());
            // after reading empty source
            Assert.assertEquals(1.0, reader.getFractionConsumed(), 1.0E-6);
            Assert.assertEquals(1, reader.getSplitPointsConsumed());
            Assert.assertEquals(0, reader.getSplitPointsRemaining());
        }
    }

    @Test
    public void testUnsplittable() throws IOException {
        String baseName = "test-input";
        File compressedFile = tmpFolder.newFile((baseName + ".gz"));
        byte[] input = generateInput(10000);
        writeFile(compressedFile, input, GZIP);
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(compressedFile.getPath(), 1));
        List<Byte> expected = Lists.newArrayList();
        for (byte i : input) {
            expected.add(i);
        }
        PipelineOptions options = PipelineOptionsFactory.create();
        BoundedReader<Byte> reader = source.createReader(options);
        List<Byte> actual = Lists.newArrayList();
        for (boolean hasNext = reader.start(); hasNext; hasNext = reader.advance()) {
            actual.add(reader.getCurrent());
            // checkpoint every 9 elements
            if (((actual.size()) % 9) == 0) {
                Double fractionConsumed = reader.getFractionConsumed();
                Assert.assertNotNull(fractionConsumed);
                Assert.assertNull(reader.splitAtFraction(fractionConsumed));
            }
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(Sets.newHashSet(expected), Sets.newHashSet(actual));
    }

    @Test
    public void testSplittableProgress() throws IOException {
        File tmpFile = tmpFolder.newFile("nonempty.txt");
        String filename = tmpFile.toPath().toString();
        Files.write(new byte[2], tmpFile);
        PipelineOptions options = PipelineOptionsFactory.create();
        CompressedSource<Byte> source = CompressedSource.from(new CompressedSourceTest.ByteSource(filename, 1));
        try (BoundedReader<Byte> readerOrig = source.createReader(options)) {
            Assert.assertThat(readerOrig, Matchers.not(Matchers.instanceOf(CompressedReader.class)));
            Assert.assertThat(readerOrig, Matchers.instanceOf(FileBasedReader.class));
            FileBasedReader<Byte> reader = ((FileBasedReader<Byte>) (readerOrig));
            // Check preconditions before starting
            Assert.assertEquals(0.0, reader.getFractionConsumed(), 1.0E-6);
            Assert.assertEquals(0, reader.getSplitPointsConsumed());
            Assert.assertEquals(SPLIT_POINTS_UNKNOWN, reader.getSplitPointsRemaining());
            // First record: none consumed, unknown remaining.
            Assert.assertTrue(reader.start());
            Assert.assertEquals(0, reader.getSplitPointsConsumed());
            Assert.assertEquals(SPLIT_POINTS_UNKNOWN, reader.getSplitPointsRemaining());
            // Second record: 1 consumed, know that we're on the last record.
            Assert.assertTrue(reader.advance());
            Assert.assertEquals(1, reader.getSplitPointsConsumed());
            Assert.assertEquals(1, reader.getSplitPointsRemaining());
            // Confirm empty and check post-conditions
            Assert.assertFalse(reader.advance());
            Assert.assertEquals(1.0, reader.getFractionConsumed(), 1.0E-6);
            Assert.assertEquals(2, reader.getSplitPointsConsumed());
            Assert.assertEquals(0, reader.getSplitPointsRemaining());
        }
    }
}

