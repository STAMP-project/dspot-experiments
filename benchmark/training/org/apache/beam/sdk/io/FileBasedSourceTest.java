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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.FileBasedSource.FileBasedReader;
import org.apache.beam.sdk.io.Source.Reader;
import org.apache.beam.sdk.io.fs.EmptyMatchTreatment;
import org.apache.beam.sdk.io.fs.MatchResult.Metadata;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.SourceTestUtils;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.values.PCollection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests code common to all file-based sources.
 */
@RunWith(JUnit4.class)
public class FileBasedSourceTest {
    private Random random = new Random(0L);

    @Rule
    public final TestPipeline p = TestPipeline.create();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * If {@code splitHeader} is null, this is just a simple line-based reader. Otherwise, the file is
     * considered to consist of blocks beginning with {@code splitHeader}. The header itself is not
     * returned as a record. The first record after the header is considered to be a split point.
     *
     * <p>E.g., if {@code splitHeader} is "h" and the lines of the file are: h, a, b, h, h, c, then
     * the records in this source are a,b,c, and records a and c are split points.
     */
    static class TestFileBasedSource extends FileBasedSource<String> {
        final String splitHeader;

        public TestFileBasedSource(String fileOrPattern, long minBundleSize, String splitHeader) {
            super(StaticValueProvider.of(fileOrPattern), minBundleSize);
            this.splitHeader = splitHeader;
        }

        public TestFileBasedSource(String fileOrPattern, EmptyMatchTreatment emptyMatchTreatment, long minBundleSize, String splitHeader) {
            super(StaticValueProvider.of(fileOrPattern), emptyMatchTreatment, minBundleSize);
            this.splitHeader = splitHeader;
        }

        public TestFileBasedSource(Metadata fileOrPattern, long minBundleSize, long startOffset, long endOffset, @Nullable
        String splitHeader) {
            super(fileOrPattern, minBundleSize, startOffset, endOffset);
            this.splitHeader = splitHeader;
        }

        @Override
        public Coder<String> getOutputCoder() {
            return StringUtf8Coder.of();
        }

        @Override
        protected FileBasedSource<String> createForSubrangeOfFile(Metadata fileName, long start, long end) {
            return new FileBasedSourceTest.TestFileBasedSource(fileName, getMinBundleSize(), start, end, splitHeader);
        }

        @Override
        protected FileBasedReader<String> createSingleFileReader(PipelineOptions options) {
            if ((splitHeader) == null) {
                return new FileBasedSourceTest.TestReader(this);
            } else {
                return new FileBasedSourceTest.TestReaderWithSplits(this);
            }
        }
    }

    /**
     * A utility class that starts reading lines from a given offset in a file until EOF.
     */
    private static class LineReader {
        private ReadableByteChannel channel = null;

        private long nextLineStart = 0;

        private long currentLineStart = 0;

        private final ByteBuffer buf;

        private static final int BUF_SIZE = 1024;

        private String currentValue = null;

        public LineReader(ReadableByteChannel channel) throws IOException {
            buf = ByteBuffer.allocate(FileBasedSourceTest.LineReader.BUF_SIZE);
            buf.flip();
            boolean removeLine = false;
            // If we are not at the beginning of a line, we should ignore the current line.
            if (channel instanceof SeekableByteChannel) {
                SeekableByteChannel seekChannel = ((SeekableByteChannel) (channel));
                if ((seekChannel.position()) > 0) {
                    // Start from one character back and read till we find a new line.
                    seekChannel.position(((seekChannel.position()) - 1));
                    removeLine = true;
                }
                nextLineStart = seekChannel.position();
            }
            this.channel = channel;
            if (removeLine) {
                nextLineStart += readNextLine(new ByteArrayOutputStream());
            }
        }

        private int readNextLine(ByteArrayOutputStream out) throws IOException {
            int byteCount = 0;
            while (true) {
                if (!(buf.hasRemaining())) {
                    buf.clear();
                    int read = channel.read(buf);
                    if (read < 0) {
                        break;
                    }
                    buf.flip();
                }
                byte b = buf.get();
                byteCount++;
                if (b == '\n') {
                    break;
                }
                out.write(b);
            } 
            return byteCount;
        }

        public boolean readNextLine() throws IOException {
            currentLineStart = nextLineStart;
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int offsetAdjustment = readNextLine(buf);
            if (offsetAdjustment == 0) {
                // EOF
                return false;
            }
            nextLineStart += offsetAdjustment;
            // When running on Windows, each line obtained from 'readNextLine()' will end with a '\r'
            // since we use '\n' as the line boundary of the reader. So we trim it off here.
            currentValue = CoderUtils.decodeFromByteArray(StringUtf8Coder.of(), buf.toByteArray()).trim();
            return true;
        }

        public String getCurrent() {
            return currentValue;
        }

        public long getCurrentLineStart() {
            return currentLineStart;
        }
    }

    /**
     * A reader that can read lines of text from a {@link TestFileBasedSource}. This reader does not
     * consider {@code splitHeader} defined by {@code TestFileBasedSource} hence every line can be the
     * first line of a split.
     */
    private static class TestReader extends FileBasedReader<String> {
        private FileBasedSourceTest.LineReader lineReader = null;

        public TestReader(FileBasedSourceTest.TestFileBasedSource source) {
            super(source);
        }

        @Override
        protected void startReading(ReadableByteChannel channel) throws IOException {
            this.lineReader = new FileBasedSourceTest.LineReader(channel);
        }

        @Override
        protected boolean readNextRecord() throws IOException {
            return lineReader.readNextLine();
        }

        @Override
        protected boolean isAtSplitPoint() {
            return true;
        }

        @Override
        protected long getCurrentOffset() {
            return lineReader.getCurrentLineStart();
        }

        @Override
        public String getCurrent() throws NoSuchElementException {
            return lineReader.getCurrent();
        }
    }

    /**
     * A reader that can read lines of text from a {@link TestFileBasedSource}. This reader considers
     * {@code splitHeader} defined by {@code TestFileBasedSource} hence only lines that immediately
     * follow a {@code splitHeader} are split points.
     */
    private static class TestReaderWithSplits extends FileBasedReader<String> {
        private FileBasedSourceTest.LineReader lineReader;

        private final String splitHeader;

        private boolean foundFirstSplitPoint = false;

        private boolean isAtSplitPoint = false;

        private long currentOffset;

        public TestReaderWithSplits(FileBasedSourceTest.TestFileBasedSource source) {
            super(source);
            this.splitHeader = source.splitHeader;
        }

        @Override
        protected void startReading(ReadableByteChannel channel) throws IOException {
            this.lineReader = new FileBasedSourceTest.LineReader(channel);
        }

        @Override
        protected boolean readNextRecord() throws IOException {
            if (!(foundFirstSplitPoint)) {
                while (!(isAtSplitPoint)) {
                    if (!(readNextRecordInternal())) {
                        return false;
                    }
                } 
                foundFirstSplitPoint = true;
                return true;
            }
            return readNextRecordInternal();
        }

        private boolean readNextRecordInternal() throws IOException {
            isAtSplitPoint = false;
            if (!(lineReader.readNextLine())) {
                return false;
            }
            currentOffset = lineReader.getCurrentLineStart();
            while (getCurrent().equals(splitHeader)) {
                currentOffset = lineReader.getCurrentLineStart();
                if (!(lineReader.readNextLine())) {
                    return false;
                }
                isAtSplitPoint = true;
            } 
            return true;
        }

        @Override
        protected boolean isAtSplitPoint() {
            return isAtSplitPoint;
        }

        @Override
        protected long getCurrentOffset() {
            return currentOffset;
        }

        @Override
        public String getCurrent() throws NoSuchElementException {
            return lineReader.getCurrent();
        }
    }

    @Test
    public void testFullyReadSingleFile() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        List<String> data = createStringDataset(3, 50);
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(file.getPath(), 64, null);
        Assert.assertEquals(data, SourceTestUtils.readFromSource(source, options));
    }

    @Test
    public void testFullyReadFilePattern() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        List<String> data1 = createStringDataset(3, 50);
        File file1 = createFileWithData("file1", data1);
        List<String> data2 = createStringDataset(3, 50);
        createFileWithData("file2", data2);
        List<String> data3 = createStringDataset(3, 50);
        createFileWithData("file3", data3);
        List<String> data4 = createStringDataset(3, 50);
        createFileWithData("otherfile", data4);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(new File(file1.getParent(), "file*").getPath(), 64, null);
        List<String> expectedResults = new ArrayList<>();
        expectedResults.addAll(data1);
        expectedResults.addAll(data2);
        expectedResults.addAll(data3);
        Assert.assertThat(expectedResults, Matchers.containsInAnyOrder(SourceTestUtils.readFromSource(source, options).toArray()));
    }

    @Test
    public void testEmptyFilepatternTreatmentDefaultDisallow() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(new File(tempFolder.getRoot(), "doesNotExist").getPath(), 64, null);
        thrown.expect(FileNotFoundException.class);
        SourceTestUtils.readFromSource(source, options);
    }

    @Test
    public void testEmptyFilepatternTreatmentAllow() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(new File(tempFolder.getRoot(), "doesNotExist").getPath(), EmptyMatchTreatment.ALLOW, 64, null);
        FileBasedSourceTest.TestFileBasedSource sourceWithWildcard = new FileBasedSourceTest.TestFileBasedSource(new File(tempFolder.getRoot(), "doesNotExist*").getPath(), EmptyMatchTreatment.ALLOW_IF_WILDCARD, 64, null);
        Assert.assertEquals(0, SourceTestUtils.readFromSource(source, options).size());
        Assert.assertEquals(0, SourceTestUtils.readFromSource(sourceWithWildcard, options).size());
    }

    @Test
    public void testEmptyFilepatternTreatmentAllowIfWildcard() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(new File(tempFolder.getRoot(), "doesNotExist").getPath(), EmptyMatchTreatment.ALLOW_IF_WILDCARD, 64, null);
        thrown.expect(FileNotFoundException.class);
        SourceTestUtils.readFromSource(source, options);
    }

    @Test
    public void testCloseUnstartedFilePatternReader() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        List<String> data1 = createStringDataset(3, 50);
        File file1 = createFileWithData("file1", data1);
        List<String> data2 = createStringDataset(3, 50);
        createFileWithData("file2", data2);
        List<String> data3 = createStringDataset(3, 50);
        createFileWithData("file3", data3);
        List<String> data4 = createStringDataset(3, 50);
        createFileWithData("otherfile", data4);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(new File(file1.getParent(), "file*").getPath(), 64, null);
        Reader<String> reader = source.createReader(options);
        // Closing an unstarted FilePatternReader should not throw an exception.
        try {
            reader.close();
        } catch (Exception e) {
            throw new AssertionError("Closing an unstarted FilePatternReader should not throw an exception", e);
        }
    }

    @Test
    public void testSplittingFailsOnEmptyFileExpansion() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        String missingFilePath = (tempFolder.newFolder().getAbsolutePath()) + "/missing.txt";
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(missingFilePath, Long.MAX_VALUE, null);
        thrown.expect(FileNotFoundException.class);
        thrown.expectMessage(missingFilePath);
        source.split(1234, options);
    }

    @Test
    public void testFractionConsumedWhenReadingFilepattern() throws IOException {
        List<String> data1 = createStringDataset(3, 1000);
        File file1 = createFileWithData("file1", data1);
        List<String> data2 = createStringDataset(3, 1000);
        createFileWithData("file2", data2);
        List<String> data3 = createStringDataset(3, 1000);
        createFileWithData("file3", data3);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource((((file1.getParent()) + "/") + "file*"), 1024, null);
        try (BoundedSource.BoundedReader<String> reader = createReader(null)) {
            double lastFractionConsumed = 0.0;
            Assert.assertEquals(0.0, reader.getFractionConsumed(), 1.0E-6);
            Assert.assertTrue(reader.start());
            Assert.assertTrue(reader.advance());
            Assert.assertTrue(reader.advance());
            // We're inside the first file. Should be in [0, 1/3).
            Assert.assertTrue(((reader.getFractionConsumed()) > 0.0));
            Assert.assertTrue(((reader.getFractionConsumed()) < (1.0 / 3.0)));
            while (reader.advance()) {
                double fractionConsumed = reader.getFractionConsumed();
                Assert.assertTrue((fractionConsumed > lastFractionConsumed));
                lastFractionConsumed = fractionConsumed;
            } 
            Assert.assertEquals(1.0, reader.getFractionConsumed(), 1.0E-6);
        }
    }

    @Test
    public void testFullyReadFilePatternFirstRecordEmpty() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        File file1 = createFileWithData("file1", new ArrayList<>());
        String pattern = (file1.getParent()) + "/file*";
        List<String> data2 = createStringDataset(3, 50);
        createFileWithData("file2", data2);
        List<String> data3 = createStringDataset(3, 50);
        createFileWithData("file3", data3);
        List<String> data4 = createStringDataset(3, 50);
        createFileWithData("otherfile", data4);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(pattern, 64, null);
        List<String> expectedResults = new ArrayList<>();
        expectedResults.addAll(data2);
        expectedResults.addAll(data3);
        Assert.assertThat(expectedResults, Matchers.containsInAnyOrder(SourceTestUtils.readFromSource(source, options).toArray()));
    }

    @Test
    public void testReadRangeAtStart() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        List<String> data = createStringDataset(3, 50);
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        Metadata metadata = FileSystems.matchSingleFileSpec(file.getPath());
        FileBasedSourceTest.TestFileBasedSource source1 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 0, 25, null);
        FileBasedSourceTest.TestFileBasedSource source2 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 25, Long.MAX_VALUE, null);
        List<String> results = new ArrayList<>();
        results.addAll(SourceTestUtils.readFromSource(source1, options));
        results.addAll(SourceTestUtils.readFromSource(source2, options));
        Assert.assertThat(data, Matchers.containsInAnyOrder(results.toArray()));
    }

    @Test
    public void testReadEverythingFromFileWithSplits() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        String header = "<h>";
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(header);
            data.addAll(createStringDataset(3, 9));
        }
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(file.getPath(), 64, header);
        List<String> expectedResults = new ArrayList<>();
        expectedResults.addAll(data);
        // Remove all occurrences of header from expected results.
        expectedResults.removeAll(Collections.singletonList(header));
        Assert.assertEquals(expectedResults, SourceTestUtils.readFromSource(source, options));
    }

    @Test
    public void testReadRangeFromFileWithSplitsFromStart() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        String header = "<h>";
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(header);
            data.addAll(createStringDataset(3, 9));
        }
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        Metadata metadata = FileSystems.matchSingleFileSpec(file.getPath());
        FileBasedSourceTest.TestFileBasedSource source1 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 0, 60, header);
        FileBasedSourceTest.TestFileBasedSource source2 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 60, Long.MAX_VALUE, header);
        List<String> expectedResults = new ArrayList<>();
        expectedResults.addAll(data);
        // Remove all occurrences of header from expected results.
        expectedResults.removeAll(Arrays.asList(header));
        List<String> results = new ArrayList<>();
        results.addAll(SourceTestUtils.readFromSource(source1, options));
        results.addAll(SourceTestUtils.readFromSource(source2, options));
        Assert.assertThat(expectedResults, Matchers.containsInAnyOrder(results.toArray()));
    }

    @Test
    public void testReadRangeFromFileWithSplitsFromMiddle() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        String header = "<h>";
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(header);
            data.addAll(createStringDataset(3, 9));
        }
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        Metadata metadata = FileSystems.matchSingleFileSpec(file.getPath());
        FileBasedSourceTest.TestFileBasedSource source1 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 0, 42, header);
        FileBasedSourceTest.TestFileBasedSource source2 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 42, 112, header);
        FileBasedSourceTest.TestFileBasedSource source3 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 112, Long.MAX_VALUE, header);
        List<String> expectedResults = new ArrayList<>();
        expectedResults.addAll(data);
        // Remove all occurrences of header from expected results.
        expectedResults.removeAll(Collections.singletonList(header));
        List<String> results = new ArrayList<>();
        results.addAll(SourceTestUtils.readFromSource(source1, options));
        results.addAll(SourceTestUtils.readFromSource(source2, options));
        results.addAll(SourceTestUtils.readFromSource(source3, options));
        Assert.assertThat(expectedResults, Matchers.containsInAnyOrder(results.toArray()));
    }

    @Test
    public void testReadFileWithSplitsWithEmptyRange() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        String header = "<h>";
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            data.add(header);
            data.addAll(createStringDataset(3, 9));
        }
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        Metadata metadata = FileSystems.matchSingleFileSpec(file.getPath());
        FileBasedSourceTest.TestFileBasedSource source1 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 0, 42, header);
        FileBasedSourceTest.TestFileBasedSource source2 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 42, 62, header);
        FileBasedSourceTest.TestFileBasedSource source3 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 62, Long.MAX_VALUE, header);
        List<String> expectedResults = new ArrayList<>();
        expectedResults.addAll(data);
        // Remove all occurrences of header from expected results.
        expectedResults.removeAll(Collections.singletonList(header));
        List<String> results = new ArrayList<>();
        results.addAll(SourceTestUtils.readFromSource(source1, options));
        results.addAll(SourceTestUtils.readFromSource(source2, options));
        results.addAll(SourceTestUtils.readFromSource(source3, options));
        Assert.assertThat(expectedResults, Matchers.containsInAnyOrder(results.toArray()));
    }

    @Test
    public void testReadRangeFromFileWithSplitsFromMiddleOfHeader() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        String header = "<h>";
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(header);
            data.addAll(createStringDataset(3, 9));
        }
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        List<String> expectedResults = new ArrayList<>();
        expectedResults.addAll(data.subList(10, data.size()));
        // Remove all occurrences of header from expected results.
        expectedResults.removeAll(Collections.singletonList(header));
        Metadata metadata = FileSystems.matchSingleFileSpec(file.getPath());
        // Split starts after "<" of the header
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 1, Long.MAX_VALUE, header);
        Assert.assertThat(expectedResults, Matchers.containsInAnyOrder(SourceTestUtils.readFromSource(source, options).toArray()));
        // Split starts after "<h" of the header
        source = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 2, Long.MAX_VALUE, header);
        Assert.assertThat(expectedResults, Matchers.containsInAnyOrder(SourceTestUtils.readFromSource(source, options).toArray()));
        // Split starts after "<h>" of the header
        source = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 3, Long.MAX_VALUE, header);
        Assert.assertThat(expectedResults, Matchers.containsInAnyOrder(SourceTestUtils.readFromSource(source, options).toArray()));
    }

    @Test
    public void testReadRangeAtMiddle() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        List<String> data = createStringDataset(3, 50);
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        Metadata metadata = FileSystems.matchSingleFileSpec(file.getPath());
        FileBasedSourceTest.TestFileBasedSource source1 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 0, 52, null);
        FileBasedSourceTest.TestFileBasedSource source2 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 52, 72, null);
        FileBasedSourceTest.TestFileBasedSource source3 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 72, Long.MAX_VALUE, null);
        List<String> results = new ArrayList<>();
        results.addAll(SourceTestUtils.readFromSource(source1, options));
        results.addAll(SourceTestUtils.readFromSource(source2, options));
        results.addAll(SourceTestUtils.readFromSource(source3, options));
        Assert.assertThat(data, Matchers.containsInAnyOrder(results.toArray()));
    }

    @Test
    public void testReadRangeAtEnd() throws IOException {
        PipelineOptions options = PipelineOptionsFactory.create();
        List<String> data = createStringDataset(3, 50);
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        Metadata metadata = FileSystems.matchSingleFileSpec(file.getPath());
        FileBasedSourceTest.TestFileBasedSource source1 = new FileBasedSourceTest.TestFileBasedSource(metadata, 64, 0, 162, null);
        FileBasedSourceTest.TestFileBasedSource source2 = new FileBasedSourceTest.TestFileBasedSource(metadata, 1024, 162, Long.MAX_VALUE, null);
        List<String> results = new ArrayList<>();
        results.addAll(SourceTestUtils.readFromSource(source1, options));
        results.addAll(SourceTestUtils.readFromSource(source2, options));
        Assert.assertThat(data, Matchers.containsInAnyOrder(results.toArray()));
    }

    @Test
    public void testReadAllSplitsOfSingleFile() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        List<String> data = createStringDataset(3, 50);
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(file.getPath(), 16, null);
        List<? extends BoundedSource<String>> sources = split(32, null);
        // Not a trivial split.
        Assert.assertTrue(((sources.size()) > 1));
        List<String> results = new ArrayList<>();
        for (BoundedSource<String> split : sources) {
            results.addAll(SourceTestUtils.readFromSource(split, options));
        }
        Assert.assertThat(data, Matchers.containsInAnyOrder(results.toArray()));
    }

    @Test
    @Category(NeedsRunner.class)
    public void testDataflowFile() throws IOException {
        List<String> data = createStringDataset(3, 50);
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(file.getPath(), 64, null);
        PCollection<String> output = p.apply("ReadFileData", Read.from(source));
        PAssert.that(output).containsInAnyOrder(data);
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testDataflowFilePattern() throws IOException {
        List<String> data1 = createStringDataset(3, 50);
        File file1 = createFileWithData("file1", data1);
        List<String> data2 = createStringDataset(3, 50);
        createFileWithData("file2", data2);
        List<String> data3 = createStringDataset(3, 50);
        createFileWithData("file3", data3);
        List<String> data4 = createStringDataset(3, 50);
        createFileWithData("otherfile", data4);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(new File(file1.getParent(), "file*").getPath(), 64, null);
        PCollection<String> output = p.apply("ReadFileData", Read.from(source));
        List<String> expectedResults = new ArrayList<>();
        expectedResults.addAll(data1);
        expectedResults.addAll(data2);
        expectedResults.addAll(data3);
        PAssert.that(output).containsInAnyOrder(expectedResults);
        p.run();
    }

    @Test
    public void testEstimatedSizeOfFile() throws Exception {
        List<String> data = createStringDataset(3, 50);
        String fileName = "file";
        File file = createFileWithData(fileName, data);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(file.getPath(), 64, null);
        Assert.assertEquals(file.length(), getEstimatedSizeBytes(null));
    }

    @Test
    public void testEstimatedSizeOfFilePattern() throws Exception {
        List<String> data1 = createStringDataset(3, 20);
        File file1 = createFileWithData("file1", data1);
        List<String> data2 = createStringDataset(3, 40);
        File file2 = createFileWithData("file2", data2);
        List<String> data3 = createStringDataset(3, 30);
        File file3 = createFileWithData("file3", data3);
        List<String> data4 = createStringDataset(3, 45);
        createFileWithData("otherfile", data4);
        List<String> data5 = createStringDataset(3, 53);
        createFileWithData("anotherfile", data5);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(new File(file1.getParent(), "file*").getPath(), 64, null);
        // Estimated size of the file pattern based source should be the total size of files that the
        // corresponding pattern is expanded into.
        Assert.assertEquals((((file1.length()) + (file2.length())) + (file3.length())), getEstimatedSizeBytes(null));
    }

    @Test
    public void testReadAllSplitsOfFilePattern() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        List<String> data1 = createStringDataset(3, 50);
        File file1 = createFileWithData("file1", data1);
        List<String> data2 = createStringDataset(3, 50);
        createFileWithData("file2", data2);
        List<String> data3 = createStringDataset(3, 50);
        createFileWithData("file3", data3);
        List<String> data4 = createStringDataset(3, 50);
        createFileWithData("otherfile", data4);
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(new File(file1.getParent(), "file*").getPath(), 64, null);
        List<? extends BoundedSource<String>> sources = split(512, null);
        // Not a trivial split.
        Assert.assertTrue(((sources.size()) > 1));
        List<String> results = new ArrayList<>();
        for (BoundedSource<String> split : sources) {
            results.addAll(SourceTestUtils.readFromSource(split, options));
        }
        List<String> expectedResults = new ArrayList<>();
        expectedResults.addAll(data1);
        expectedResults.addAll(data2);
        expectedResults.addAll(data3);
        Assert.assertThat(expectedResults, Matchers.containsInAnyOrder(results.toArray()));
    }

    @Test
    public void testSplitAtFraction() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        File file = createFileWithData("file", createStringDataset(3, 100));
        Metadata metadata = FileSystems.matchSingleFileSpec(file.getPath());
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(metadata, 1, 0, file.length(), null);
        // Shouldn't be able to split while unstarted.
        SourceTestUtils.assertSplitAtFractionFails(source, 0, 0.7, options);
        SourceTestUtils.assertSplitAtFractionSucceedsAndConsistent(source, 1, 0.7, options);
        SourceTestUtils.assertSplitAtFractionSucceedsAndConsistent(source, 30, 0.7, options);
        SourceTestUtils.assertSplitAtFractionFails(source, 0, 0.0, options);
        SourceTestUtils.assertSplitAtFractionFails(source, 70, 0.3, options);
        SourceTestUtils.assertSplitAtFractionFails(source, 100, 1.0, options);
        SourceTestUtils.assertSplitAtFractionFails(source, 100, 0.99, options);
        SourceTestUtils.assertSplitAtFractionSucceedsAndConsistent(source, 100, 0.995, options);
    }

    @Test
    public void testSplitAtFractionExhaustive() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        // Smaller file for exhaustive testing.
        File file = createFileWithData("file", createStringDataset(3, 20));
        Metadata metadata = FileSystems.matchSingleFileSpec(file.getPath());
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(metadata, 1, 0, file.length(), null);
        SourceTestUtils.assertSplitAtFractionExhaustive(source, options);
    }

    @Test
    public void testToStringFile() throws Exception {
        File f = createFileWithData("foo", Collections.emptyList());
        Metadata metadata = FileSystems.matchSingleFileSpec(f.getPath());
        FileBasedSourceTest.TestFileBasedSource source = new FileBasedSourceTest.TestFileBasedSource(metadata, 1, 0, 10, null);
        Assert.assertEquals(String.format("%s range [0, 10)", f.getAbsolutePath()), source.toString());
    }
}

