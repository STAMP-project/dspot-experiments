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


import Compression.BZIP2;
import Compression.DEFLATE;
import Compression.GZIP;
import CompressionType.UNCOMPRESSED;
import GlobalWindow.INSTANCE;
import StandardResolveOptions.RESOLVE_FILE;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.beam.sdk.io.FileBasedSink.FileResult;
import org.apache.beam.sdk.io.FileBasedSink.FilenamePolicy;
import org.apache.beam.sdk.io.FileBasedSink.WriteOperation;
import org.apache.beam.sdk.io.FileBasedSink.Writer;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.transforms.windowing.GlobalWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.apache.beam.sdk.io.SimpleSink.SimpleWriteOperation.getSink;
import static org.apache.beam.sdk.io.SimpleSink.SimpleWriter.FOOTER;
import static org.apache.beam.sdk.io.SimpleSink.SimpleWriter.HEADER;
import static org.apache.beam.sdk.io.SimpleSink.SimpleWriter.close;
import static org.apache.beam.sdk.io.SimpleSink.SimpleWriter.getOutputFile;
import static org.apache.beam.sdk.io.SimpleSink.SimpleWriter.open;


/**
 * Tests for {@link FileBasedSink}.
 */
@RunWith(JUnit4.class)
public class FileBasedSinkTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private final String tempDirectoryName = "temp";

    /**
     * Writer opens the correct file, writes the header, footer, and elements in the correct order,
     * and returns the correct filename.
     */
    @Test
    public void testWriter() throws Exception {
        String testUid = "testId";
        ResourceId expectedTempFile = getBaseTempDirectory().resolve(testUid, RESOLVE_FILE);
        List<String> values = Arrays.asList("sympathetic vulture", "boresome hummingbird");
        List<String> expected = new ArrayList<>();
        expected.add(HEADER);
        expected.addAll(values);
        expected.add(FOOTER);
        SimpleSink.SimpleWriter<Void> writer = buildWriteOperationWithTempDir(getBaseTempDirectory()).createWriter();
        open(testUid);
        for (String value : values) {
            writer.write(value);
        }
        close();
        Assert.assertEquals(expectedTempFile, getOutputFile());
        assertFileContains(expected, expectedTempFile);
    }

    /**
     * Removes temporary files when temporary and output directories differ.
     */
    @Test
    public void testRemoveWithTempFilename() throws Exception {
        testRemoveTemporaryFiles(3, getBaseTempDirectory());
    }

    /**
     * Finalize copies temporary files to output files and removes any temporary files.
     */
    @Test
    public void testFinalize() throws Exception {
        List<File> files = generateTemporaryFilesForFinalize(3);
        runFinalize(buildWriteOperation(), files);
    }

    /**
     * Finalize can be called repeatedly.
     */
    @Test
    public void testFinalizeMultipleCalls() throws Exception {
        List<File> files = generateTemporaryFilesForFinalize(3);
        SimpleSink.SimpleWriteOperation writeOp = buildWriteOperation();
        runFinalize(writeOp, files);
        runFinalize(writeOp, files);
    }

    /**
     * Finalize can be called when some temporary files do not exist and output files exist.
     */
    @Test
    public void testFinalizeWithIntermediateState() throws Exception {
        SimpleSink.SimpleWriteOperation writeOp = buildWriteOperation();
        List<File> files = generateTemporaryFilesForFinalize(3);
        runFinalize(writeOp, files);
        // create a temporary file and then rerun finalize
        tmpFolder.newFolder(tempDirectoryName);
        tmpFolder.newFile(((tempDirectoryName) + "/1"));
        runFinalize(writeOp, files);
    }

    /**
     * Output files are copied to the destination location with the correct names and contents.
     */
    @Test
    public void testCopyToOutputFiles() throws Exception {
        SimpleSink.SimpleWriteOperation<Void> writeOp = buildWriteOperation();
        List<String> inputFilenames = Arrays.asList("input-1", "input-2", "input-3");
        List<String> inputContents = Arrays.asList("1", "2", "3");
        List<String> expectedOutputFilenames = Arrays.asList("file-00-of-03.test", "file-01-of-03.test", "file-02-of-03.test");
        List<KV<FileResult<Void>, ResourceId>> resultsToFinalFilenames = Lists.newArrayList();
        List<ResourceId> expectedOutputPaths = Lists.newArrayList();
        for (int i = 0; i < (inputFilenames.size()); i++) {
            // Generate output paths.
            expectedOutputPaths.add(getBaseOutputDirectory().resolve(expectedOutputFilenames.get(i), RESOLVE_FILE));
            // Generate and write to input paths.
            File inputTmpFile = tmpFolder.newFile(inputFilenames.get(i));
            List<String> lines = Collections.singletonList(inputContents.get(i));
            writeFile(lines, inputTmpFile);
            ResourceId finalFilename = getSink().getDynamicDestinations().getFilenamePolicy(null).unwindowedFilename(i, inputFilenames.size(), UNCOMPRESSED);
            resultsToFinalFilenames.add(KV.of(new FileResult(LocalResources.fromFile(inputTmpFile, false), WriteFiles.UNKNOWN_SHARDNUM, GlobalWindow.INSTANCE, PaneInfo.ON_TIME_AND_ONLY_FIRING, null), finalFilename));
        }
        // Copy input files to output files.
        writeOp.moveToOutputFiles(resultsToFinalFilenames);
        // Assert that the contents were copied.
        for (int i = 0; i < (expectedOutputPaths.size()); i++) {
            assertFileContains(Collections.singletonList(inputContents.get(i)), expectedOutputPaths.get(i));
        }
    }

    /**
     * Output filenames are generated correctly when an extension is supplied.
     */
    @Test
    public void testGenerateOutputFilenames() {
        List<ResourceId> expected;
        List<ResourceId> actual;
        ResourceId root = getBaseOutputDirectory();
        SimpleSink<Void> sink = SimpleSink.makeSimpleSink(root, "file", ".SSSSS.of.NNNNN", ".test", Compression.UNCOMPRESSED);
        FilenamePolicy policy = sink.getDynamicDestinations().getFilenamePolicy(null);
        expected = Arrays.asList(root.resolve("file.00000.of.00003.test", RESOLVE_FILE), root.resolve("file.00001.of.00003.test", RESOLVE_FILE), root.resolve("file.00002.of.00003.test", RESOLVE_FILE));
        actual = generateDestinationFilenames(policy, 3);
        Assert.assertEquals(expected, actual);
        expected = Collections.singletonList(root.resolve("file.00000.of.00001.test", RESOLVE_FILE));
        actual = generateDestinationFilenames(policy, 1);
        Assert.assertEquals(expected, actual);
        expected = new ArrayList();
        actual = generateDestinationFilenames(policy, 0);
        Assert.assertEquals(expected, actual);
    }

    /**
     * Reject non-distinct output filenames.
     */
    @Test
    public void testCollidingOutputFilenames() throws Exception {
        ResourceId root = getBaseOutputDirectory();
        SimpleSink<Void> sink = SimpleSink.makeSimpleSink(root, "file", "-NN", "test", Compression.UNCOMPRESSED);
        SimpleSink.SimpleWriteOperation<Void> writeOp = new SimpleSink.SimpleWriteOperation<>(sink);
        try {
            List<FileResult<Void>> results = Lists.newArrayList();
            for (int i = 0; i < 3; ++i) {
                results.add(/* shard - should be different, but is the same */
                new FileResult(root.resolve(("temp" + i), RESOLVE_FILE), 1, GlobalWindow.INSTANCE, PaneInfo.ON_TIME_AND_ONLY_FIRING, null));
            }
            /* numShards */
            writeOp.finalizeDestination(null, INSTANCE, 5, results);
            Assert.fail("Should have failed.");
        } catch (IllegalArgumentException exn) {
            Assert.assertThat(exn.getMessage(), Matchers.containsString("generated the same name"));
            Assert.assertThat(exn.getMessage(), Matchers.containsString("temp0"));
            Assert.assertThat(exn.getMessage(), Matchers.containsString("temp1"));
        }
    }

    /**
     * Output filenames are generated correctly when an extension is not supplied.
     */
    @Test
    public void testGenerateOutputFilenamesWithoutExtension() {
        List<ResourceId> expected;
        List<ResourceId> actual;
        ResourceId root = getBaseOutputDirectory();
        SimpleSink<Void> sink = SimpleSink.makeSimpleSink(root, "file", "-SSSSS-of-NNNNN", "", Compression.UNCOMPRESSED);
        FilenamePolicy policy = sink.getDynamicDestinations().getFilenamePolicy(null);
        expected = Arrays.asList(root.resolve("file-00000-of-00003", RESOLVE_FILE), root.resolve("file-00001-of-00003", RESOLVE_FILE), root.resolve("file-00002-of-00003", RESOLVE_FILE));
        actual = generateDestinationFilenames(policy, 3);
        Assert.assertEquals(expected, actual);
        expected = Collections.singletonList(root.resolve("file-00000-of-00001", RESOLVE_FILE));
        actual = generateDestinationFilenames(policy, 1);
        Assert.assertEquals(expected, actual);
        expected = new ArrayList();
        actual = generateDestinationFilenames(policy, 0);
        Assert.assertEquals(expected, actual);
    }

    /**
     * {@link Compression#BZIP2} correctly writes BZip2 data.
     */
    @Test
    public void testCompressionBZIP2() throws FileNotFoundException, IOException {
        final File file = writeValuesWithCompression(BZIP2, "abc", "123");
        // Read Bzip2ed data back in using Apache commons API (de facto standard).
        assertReadValues(new BufferedReader(new InputStreamReader(new BZip2CompressorInputStream(new FileInputStream(file)), StandardCharsets.UTF_8)), "abc", "123");
    }

    /**
     * {@link Compression#GZIP} correctly writes Gzipped data.
     */
    @Test
    public void testCompressionGZIP() throws FileNotFoundException, IOException {
        final File file = writeValuesWithCompression(GZIP, "abc", "123");
        // Read Gzipped data back in using standard API.
        assertReadValues(new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), StandardCharsets.UTF_8)), "abc", "123");
    }

    /**
     * {@link Compression#DEFLATE} correctly writes deflate data.
     */
    @Test
    public void testCompressionDEFLATE() throws FileNotFoundException, IOException {
        final File file = writeValuesWithCompression(DEFLATE, "abc", "123");
        // Read Gzipped data back in using standard API.
        assertReadValues(new BufferedReader(new InputStreamReader(new DeflateCompressorInputStream(new FileInputStream(file)), StandardCharsets.UTF_8)), "abc", "123");
    }

    /**
     * {@link Compression#UNCOMPRESSED} correctly writes uncompressed data.
     */
    @Test
    public void testCompressionUNCOMPRESSED() throws FileNotFoundException, IOException {
        final File file = writeValuesWithCompression(Compression.UNCOMPRESSED, "abc", "123");
        // Read uncompressed data back in using standard API.
        assertReadValues(new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)), "abc", "123");
    }

    /**
     * {@link Writer} writes to the {@link WritableByteChannel} provided by {@link DrunkWritableByteChannelFactory}.
     */
    @Test
    public void testFileBasedWriterWithWritableByteChannelFactory() throws Exception {
        final String testUid = "testId";
        ResourceId root = getBaseOutputDirectory();
        WriteOperation<Void, String> writeOp = SimpleSink.makeSimpleSink(root, "file", "-SS-of-NN", "txt", new DrunkWritableByteChannelFactory()).createWriteOperation();
        final Writer<Void, String> writer = writeOp.createWriter();
        final ResourceId expectedFile = writeOp.tempDirectory.get().resolve(testUid, RESOLVE_FILE);
        final List<String> expected = new ArrayList<>();
        expected.add("header");
        expected.add("header");
        expected.add("a");
        expected.add("a");
        expected.add("b");
        expected.add("b");
        expected.add("footer");
        expected.add("footer");
        writer.open(testUid);
        writer.write("a");
        writer.write("b");
        writer.close();
        Assert.assertEquals(expectedFile, writer.getOutputFile());
        assertFileContains(expected, expectedFile);
    }
}

