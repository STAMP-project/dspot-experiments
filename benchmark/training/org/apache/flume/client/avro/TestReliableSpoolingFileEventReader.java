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
package org.apache.flume.client.avro;


import ConsumeOrder.OLDEST;
import ConsumeOrder.RANDOM;
import ConsumeOrder.YOUNGEST;
import DeletePolicy.IMMEDIATE;
import TrackingPolicy.RENAME;
import TrackingPolicy.TRACKER_DIR;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.flume.Event;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.SpoolDirectorySourceConfigurationConstants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ReliableSpoolingFileEventReader.metaFileName;


public class TestReliableSpoolingFileEventReader {
    private static final Logger logger = LoggerFactory.getLogger(TestReliableSpoolingFileEventReader.class);

    private static final File WORK_DIR = new File(("target/test/work/" + (TestReliableSpoolingFileEventReader.class.getSimpleName())));

    private static final File TRACKER_DIR = new File(TestReliableSpoolingFileEventReader.WORK_DIR, SpoolDirectorySourceConfigurationConstants.DEFAULT_TRACKER_DIR);

    @Test
    public void testIncludePattern() throws IOException {
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).includePattern("^file2$").deletePolicy(IMMEDIATE.toString()).sourceCounter(new SourceCounter("test")).build();
        String[] beforeFiles = new String[]{ "file0", "file1", "file2", "file3", "emptylineFile" };
        Assert.assertTrue((("Expected " + (beforeFiles.length)) + " files in working dir"), checkLeftFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, beforeFiles));
        processEventsWithReader(reader, 10);
        String[] afterFiles = new String[]{ "file0", "file1", "file3", "emptylineFile" };
        Assert.assertTrue((("Expected " + (afterFiles.length)) + " files left in working dir"), checkLeftFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, afterFiles));
        Assert.assertTrue("Expected no files left in tracker dir", checkLeftFilesInDir(TestReliableSpoolingFileEventReader.TRACKER_DIR, new String[0]));
    }

    @Test
    public void testIgnorePattern() throws IOException {
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).ignorePattern("^file2$").deletePolicy(IMMEDIATE.toString()).sourceCounter(new SourceCounter("test")).build();
        String[] beforeFiles = new String[]{ "file0", "file1", "file2", "file3", "emptylineFile" };
        Assert.assertTrue((("Expected " + (beforeFiles.length)) + " files in working dir"), checkLeftFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, beforeFiles));
        processEventsWithReader(reader, 10);
        String[] files = new String[]{ "file2" };
        Assert.assertTrue((("Expected " + (files.length)) + " files left in working dir"), checkLeftFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, files));
        Assert.assertTrue("Expected no files left in tracker dir", checkLeftFilesInDir(TestReliableSpoolingFileEventReader.TRACKER_DIR, new String[0]));
    }

    @Test
    public void testIncludeExcludePatternNoConflict() throws IOException {
        // Expected behavior mixing include/exclude conditions:
        // - file0, file1, file3: not deleted as matching ignore pattern and not
        // matching include pattern
        // - file2: deleted as not matching ignore pattern and matching include
        // pattern
        // - emptylineFile: not deleted as not matching ignore pattern but not
        // matching include pattern as well
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).ignorePattern("^file[013]$").includePattern("^file2$").deletePolicy(IMMEDIATE.toString()).sourceCounter(new SourceCounter("test")).build();
        String[] beforeFiles = new String[]{ "file0", "file1", "file2", "file3", "emptylineFile" };
        Assert.assertTrue((("Expected " + (beforeFiles.length)) + " files in working dir"), checkLeftFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, beforeFiles));
        processEventsWithReader(reader, 10);
        String[] files = new String[]{ "file0", "file1", "file3", "emptylineFile" };
        Assert.assertTrue((("Expected " + (files.length)) + " files left in working dir"), checkLeftFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, files));
        Assert.assertTrue("Expected no files left in tracker dir", checkLeftFilesInDir(TestReliableSpoolingFileEventReader.TRACKER_DIR, new String[0]));
    }

    @Test
    public void testIncludeExcludePatternConflict() throws IOException {
        // This test will stress what happens when both ignore and include options
        // are specified and the two patterns match at the same time.
        // Expected behavior:
        // - file2: not deleted as both include and ignore patterns match (safety
        // measure: ignore always wins on conflict)
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).ignorePattern("^file2$").includePattern("^file2$").deletePolicy(IMMEDIATE.toString()).sourceCounter(new SourceCounter("test")).build();
        String[] beforeFiles = new String[]{ "file0", "file1", "file2", "file3", "emptylineFile" };
        Assert.assertTrue((("Expected " + (beforeFiles.length)) + " files in working dir"), checkLeftFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, beforeFiles));
        processEventsWithReader(reader, 10);
        String[] files = new String[]{ "file0", "file1", "file2", "file3", "emptylineFile" };
        Assert.assertTrue((("Expected " + (files.length)) + " files left in working dir"), checkLeftFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, files));
        Assert.assertTrue("Expected no files left in tracker dir", checkLeftFilesInDir(TestReliableSpoolingFileEventReader.TRACKER_DIR, new String[0]));
    }

    @Test
    public void testRepeatedCallsWithCommitAlways() throws IOException {
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).sourceCounter(new SourceCounter("test")).build();
        final int expectedLines = (((1 + 1) + 2) + 3) + 1;
        int seenLines = 0;
        for (int i = 0; i < 10; i++) {
            List<Event> events = reader.readEvents(10);
            seenLines += events.size();
            reader.commit();
        }
        Assert.assertEquals(expectedLines, seenLines);
    }

    @Test
    public void testRepeatedCallsWithCommitOnSuccess() throws IOException {
        String trackerDirPath = SpoolDirectorySourceConfigurationConstants.DEFAULT_TRACKER_DIR;
        File trackerDir = new File(TestReliableSpoolingFileEventReader.WORK_DIR, trackerDirPath);
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).trackerDirPath(trackerDirPath).sourceCounter(new SourceCounter("test")).build();
        final int expectedLines = (((1 + 1) + 2) + 3) + 1;
        int seenLines = 0;
        for (int i = 0; i < 10; i++) {
            List<Event> events = reader.readEvents(10);
            int numEvents = events.size();
            if (numEvents > 0) {
                seenLines += numEvents;
                reader.commit();
                // ensure that there are files in the trackerDir
                File[] files = trackerDir.listFiles();
                Assert.assertNotNull(files);
                Assert.assertTrue(("Expected tracker files in tracker dir " + (trackerDir.getAbsolutePath())), ((files.length) > 0));
            }
        }
        Assert.assertEquals(expectedLines, seenLines);
    }

    @Test
    public void testFileDeletion() throws IOException {
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).deletePolicy(IMMEDIATE.name()).sourceCounter(new SourceCounter("test")).build();
        List<File> before = TestReliableSpoolingFileEventReader.listFiles(TestReliableSpoolingFileEventReader.WORK_DIR);
        Assert.assertEquals(("Expected 5, not: " + before), 5, before.size());
        List<Event> events;
        do {
            events = reader.readEvents(10);
            reader.commit();
        } while (!(events.isEmpty()) );
        List<File> after = TestReliableSpoolingFileEventReader.listFiles(TestReliableSpoolingFileEventReader.WORK_DIR);
        Assert.assertEquals(("Expected 0, not: " + after), 0, after.size());
        List<File> trackerFiles = TestReliableSpoolingFileEventReader.listFiles(new File(TestReliableSpoolingFileEventReader.WORK_DIR, SpoolDirectorySourceConfigurationConstants.DEFAULT_TRACKER_DIR));
        Assert.assertEquals(("Expected 0, not: " + trackerFiles), 0, trackerFiles.size());
    }

    @Test(expected = NullPointerException.class)
    public void testNullConsumeOrder() throws IOException {
        new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).consumeOrder(null).sourceCounter(new SourceCounter("test")).build();
    }

    @Test
    public void testConsumeFileRandomly() throws IOException {
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).consumeOrder(RANDOM).sourceCounter(new SourceCounter("test")).build();
        File fileName = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file");
        FileUtils.write(fileName, "New file created in the end. Shoud be read randomly.\n");
        Set<String> actual = Sets.newHashSet();
        readEventsForFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, reader, actual);
        Set<String> expected = Sets.newHashSet();
        createExpectedFromFilesInSetup(expected);
        expected.add("");
        expected.add("New file created in the end. Shoud be read randomly.");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConsumeFileRandomlyNewFile() throws Exception {
        // Atomic moves are not supported in Windows.
        if (SystemUtils.IS_OS_WINDOWS) {
            return;
        }
        final ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).consumeOrder(RANDOM).sourceCounter(new SourceCounter("test")).build();
        File fileName = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file");
        FileUtils.write(fileName, "New file created in the end. Shoud be read randomly.\n");
        Set<String> expected = Sets.newHashSet();
        int totalFiles = TestReliableSpoolingFileEventReader.WORK_DIR.listFiles().length;
        final Set<String> actual = Sets.newHashSet();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final Semaphore semaphore1 = new Semaphore(0);
        final Semaphore semaphore2 = new Semaphore(0);
        Future<Void> wait = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                readEventsForFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, reader, actual, semaphore1, semaphore2);
                return null;
            }
        });
        semaphore1.acquire();
        File finalFile = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "t-file");
        FileUtils.write(finalFile, "Last file");
        semaphore2.release();
        wait.get();
        int listFilesCount = getListFilesCount();
        finalFile.delete();
        createExpectedFromFilesInSetup(expected);
        expected.add("");
        expected.add("New file created in the end. Shoud be read randomly.");
        expected.add("Last file");
        Assert.assertTrue((listFilesCount < (totalFiles + 2)));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConsumeFileOldest() throws IOException, InterruptedException {
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).consumeOrder(OLDEST).sourceCounter(new SourceCounter("test")).build();
        File file1 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file1");
        File file2 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file2");
        File file3 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file3");
        Thread.sleep(1000L);
        FileUtils.write(file2, "New file2 created.\n");
        Thread.sleep(1000L);
        FileUtils.write(file1, "New file1 created.\n");
        Thread.sleep(1000L);
        FileUtils.write(file3, "New file3 created.\n");
        // order of age oldest to youngest (file2, file1, file3)
        List<String> actual = Lists.newLinkedList();
        readEventsForFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, reader, actual);
        List<String> expected = Lists.newLinkedList();
        createExpectedFromFilesInSetup(expected);
        expected.add("");// Empty file was added in the last in setup.

        expected.add("New file2 created.");
        expected.add("New file1 created.");
        expected.add("New file3 created.");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConsumeFileYoungest() throws IOException, InterruptedException {
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).consumeOrder(YOUNGEST).sourceCounter(new SourceCounter("test")).build();
        File file1 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file1");
        File file2 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file2");
        File file3 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file3");
        Thread.sleep(1000L);
        FileUtils.write(file2, "New file2 created.\n");
        Thread.sleep(1000L);
        FileUtils.write(file3, "New file3 created.\n");
        Thread.sleep(1000L);
        FileUtils.write(file1, "New file1 created.\n");
        // order of age youngest to oldest (file2, file3, file1)
        List<String> actual = Lists.newLinkedList();
        readEventsForFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, reader, actual);
        List<String> expected = Lists.newLinkedList();
        createExpectedFromFilesInSetup(expected);
        Collections.sort(expected);
        // Empty Line file was added in the last in Setup.
        expected.add(0, "");
        expected.add(0, "New file2 created.");
        expected.add(0, "New file3 created.");
        expected.add(0, "New file1 created.");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConsumeFileOldestWithLexicographicalComparision() throws IOException, InterruptedException {
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).consumeOrder(OLDEST).sourceCounter(new SourceCounter("test")).build();
        File file1 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file1");
        File file2 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file2");
        File file3 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file3");
        Thread.sleep(1000L);
        FileUtils.write(file3, "New file3 created.\n");
        FileUtils.write(file2, "New file2 created.\n");
        FileUtils.write(file1, "New file1 created.\n");
        file1.setLastModified(file3.lastModified());
        file1.setLastModified(file2.lastModified());
        // file ages are same now they need to be ordered
        // lexicographically (file1, file2, file3).
        List<String> actual = Lists.newLinkedList();
        readEventsForFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, reader, actual);
        List<String> expected = Lists.newLinkedList();
        createExpectedFromFilesInSetup(expected);
        expected.add("");// Empty file was added in the last in setup.

        expected.add("New file1 created.");
        expected.add("New file2 created.");
        expected.add("New file3 created.");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConsumeFileYoungestWithLexicographicalComparision() throws IOException, InterruptedException {
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).consumeOrder(YOUNGEST).sourceCounter(new SourceCounter("test")).build();
        File file1 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file1");
        File file2 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file2");
        File file3 = new File(TestReliableSpoolingFileEventReader.WORK_DIR, "new-file3");
        Thread.sleep(1000L);
        FileUtils.write(file1, "New file1 created.\n");
        FileUtils.write(file2, "New file2 created.\n");
        FileUtils.write(file3, "New file3 created.\n");
        file1.setLastModified(file3.lastModified());
        file1.setLastModified(file2.lastModified());
        // file ages are same now they need to be ordered
        // lexicographically (file1, file2, file3).
        List<String> actual = Lists.newLinkedList();
        readEventsForFilesInDir(TestReliableSpoolingFileEventReader.WORK_DIR, reader, actual);
        List<String> expected = Lists.newLinkedList();
        createExpectedFromFilesInSetup(expected);
        expected.add(0, "");// Empty file was added in the last in setup.

        expected.add(0, "New file3 created.");
        expected.add(0, "New file2 created.");
        expected.add(0, "New file1 created.");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testLargeNumberOfFilesOLDEST() throws IOException {
        templateTestForRecursiveDirs(OLDEST, null, 3, 3, 37, RENAME);
    }

    @Test
    public void testLargeNumberOfFilesYOUNGEST() throws IOException {
        templateTestForRecursiveDirs(YOUNGEST, Comparator.reverseOrder(), 3, 3, 37, RENAME);
    }

    @Test
    public void testLargeNumberOfFilesRANDOM() throws IOException {
        templateTestForRecursiveDirs(RANDOM, null, 3, 3, 37, RENAME);
    }

    @Test
    public void testLargeNumberOfFilesOLDESTTrackerDir() throws IOException {
        templateTestForRecursiveDirs(OLDEST, null, 3, 3, 10, TrackingPolicy.TRACKER_DIR);
    }

    @Test
    public void testLargeNumberOfFilesYOUNGESTTrackerDir() throws IOException {
        templateTestForRecursiveDirs(YOUNGEST, Comparator.reverseOrder(), 3, 3, 10, TrackingPolicy.TRACKER_DIR);
    }

    @Test
    public void testLargeNumberOfFilesRANDOMTrackerDir() throws IOException {
        templateTestForRecursiveDirs(RANDOM, null, 3, 3, 10, TrackingPolicy.TRACKER_DIR);
    }

    @Test
    public void testZeroByteTrackerFile() throws IOException {
        String trackerDirPath = SpoolDirectorySourceConfigurationConstants.DEFAULT_TRACKER_DIR;
        File trackerDir = new File(TestReliableSpoolingFileEventReader.WORK_DIR, trackerDirPath);
        if (!(trackerDir.exists())) {
            trackerDir.mkdir();
        }
        File trackerFile = new File(trackerDir, metaFileName);
        if (trackerFile.exists()) {
            trackerFile.delete();
        }
        trackerFile.createNewFile();
        ReliableEventReader reader = new ReliableSpoolingFileEventReader.Builder().spoolDirectory(TestReliableSpoolingFileEventReader.WORK_DIR).trackerDirPath(trackerDirPath).sourceCounter(new SourceCounter("test")).build();
        final int expectedLines = 1;
        int seenLines = 0;
        List<Event> events = reader.readEvents(10);
        int numEvents = events.size();
        if (numEvents > 0) {
            seenLines += numEvents;
            reader.commit();
        }
        // This line will fail, if the zero-byte tracker file has not been handled
        Assert.assertEquals(expectedLines, seenLines);
    }
}

