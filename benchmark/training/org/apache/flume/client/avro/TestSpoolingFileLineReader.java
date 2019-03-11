/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.client.avro;


import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.flume.Event;
import org.apache.flume.source.SpoolDirectorySourceConfigurationConstants;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSpoolingFileLineReader {
    Logger logger = LoggerFactory.getLogger(TestSpoolingFileLineReader.class);

    private static String completedSuffix = SpoolDirectorySourceConfigurationConstants.DEFAULT_SPOOLED_FILE_SUFFIX;

    private static int bufferMaxLineLength = 500;

    private File tmpDir;

    // Create three multi-line files then read them back out. Ensures that
    // files are accessed in correct order and that lines are read correctly
    // from files.
    @Test
    public void testBasicSpooling() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        File f2 = new File(((tmpDir.getAbsolutePath()) + "/file2"));
        File f3 = new File(((tmpDir.getAbsolutePath()) + "/file3"));
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        Files.write("file2line1\nfile2line2\n", f2, Charsets.UTF_8);
        Files.write("file3line1\nfile3line2\n", f3, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser = getParser();
        List<String> out = Lists.newArrayList();
        for (int i = 0; i < 6; i++) {
            logger.info("At line {}", i);
            String body = TestSpoolingFileLineReader.bodyAsString(parser.readEvent());
            logger.debug("Seen event with body: {}", body);
            out.add(body);
            parser.commit();
        }
        // Make sure we got every line
        Assert.assertTrue(out.contains("file1line1"));
        Assert.assertTrue(out.contains("file1line2"));
        Assert.assertTrue(out.contains("file2line1"));
        Assert.assertTrue(out.contains("file2line2"));
        Assert.assertTrue(out.contains("file3line1"));
        Assert.assertTrue(out.contains("file3line2"));
        List<File> outFiles = Lists.newArrayList(tmpDir.listFiles(directoryFilter()));
        Assert.assertEquals(3, outFiles.size());
        // Make sure files 1 and 2 have been processed and file 3 is still open
        Assert.assertTrue(outFiles.contains(new File((((tmpDir) + "/file1") + (TestSpoolingFileLineReader.completedSuffix)))));
        Assert.assertTrue(outFiles.contains(new File((((tmpDir) + "/file2") + (TestSpoolingFileLineReader.completedSuffix)))));
        Assert.assertTrue(outFiles.contains(new File(((tmpDir) + "/file3"))));
    }

    // Make sure this works when there are initially no files
    @Test
    public void testInitiallyEmptyDirectory() throws IOException {
        ReliableSpoolingFileEventReader parser = getParser();
        Assert.assertNull(parser.readEvent());
        Assert.assertEquals(0, parser.readEvents(10).size());
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        List<String> out = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(2));
        parser.commit();
        // Make sure we got all of the first file
        Assert.assertTrue(out.contains("file1line1"));
        Assert.assertTrue(out.contains("file1line2"));
        File f2 = new File(((tmpDir.getAbsolutePath()) + "/file2"));
        Files.write("file2line1\nfile2line2\n", f2, Charsets.UTF_8);
        parser.readEvent();// force roll

        parser.commit();
        List<File> outFiles = Lists.newArrayList(tmpDir.listFiles(directoryFilter()));
        Assert.assertEquals(2, outFiles.size());
        Assert.assertTrue(outFiles.contains(new File((((tmpDir) + "/file1") + (TestSpoolingFileLineReader.completedSuffix)))));
        Assert.assertTrue(outFiles.contains(new File(((tmpDir) + "/file2"))));
    }

    // Ensures that file immutability is enforced.
    @Test(expected = IllegalStateException.class)
    public void testFileChangesDuringRead() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser1 = getParser();
        List<String> out = Lists.newArrayList();
        out.addAll(TestSpoolingFileLineReader.bodiesAsStrings(parser1.readEvents(2)));
        parser1.commit();
        Assert.assertEquals(2, out.size());
        Assert.assertTrue(out.contains("file1line1"));
        Assert.assertTrue(out.contains("file1line2"));
        Files.append("file1line3\n", f1, Charsets.UTF_8);
        out.add(TestSpoolingFileLineReader.bodyAsString(parser1.readEvent()));
        parser1.commit();
        out.add(TestSpoolingFileLineReader.bodyAsString(parser1.readEvent()));
        parser1.commit();
    }

    // Test when a competing destination file is found, but it matches,
    // and we are on a Windows machine.
    @Test
    public void testDestinationExistsAndSameFileWindows() throws IOException {
        System.setProperty("os.name", "Some version of Windows");
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        File f1Completed = new File((((tmpDir.getAbsolutePath()) + "/file1") + (TestSpoolingFileLineReader.completedSuffix)));
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        Files.write("file1line1\nfile1line2\n", f1Completed, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser = getParser();
        List<String> out = Lists.newArrayList();
        for (int i = 0; i < 2; i++) {
            out.add(TestSpoolingFileLineReader.bodyAsString(parser.readEvent()));
            parser.commit();
        }
        File f2 = new File(((tmpDir.getAbsolutePath()) + "/file2"));
        Files.write("file2line1\nfile2line2\n", f2, Charsets.UTF_8);
        for (int i = 0; i < 2; i++) {
            out.add(TestSpoolingFileLineReader.bodyAsString(parser.readEvent()));
            parser.commit();
        }
        // Make sure we got every line
        Assert.assertEquals(4, out.size());
        Assert.assertTrue(out.contains("file1line1"));
        Assert.assertTrue(out.contains("file1line2"));
        Assert.assertTrue(out.contains("file2line1"));
        Assert.assertTrue(out.contains("file2line2"));
        // Make sure original is deleted
        List<File> outFiles = Lists.newArrayList(tmpDir.listFiles(directoryFilter()));
        Assert.assertEquals(2, outFiles.size());
        Assert.assertTrue(outFiles.contains(new File(((tmpDir) + "/file2"))));
        Assert.assertTrue(outFiles.contains(new File((((tmpDir) + "/file1") + (TestSpoolingFileLineReader.completedSuffix)))));
    }

    // Test when a competing destination file is found, but it matches,
    // and we are not on a Windows machine.
    @Test(expected = IllegalStateException.class)
    public void testDestinationExistsAndSameFileNotOnWindows() throws IOException {
        System.setProperty("os.name", "Some version of Linux");
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        File f1Completed = new File((((tmpDir.getAbsolutePath()) + "/file1") + (TestSpoolingFileLineReader.completedSuffix)));
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        Files.write("file1line1\nfile1line2\n", f1Completed, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser = getParser();
        List<String> out = Lists.newArrayList();
        for (int i = 0; i < 2; i++) {
            out.add(TestSpoolingFileLineReader.bodyAsString(parser.readEvent()));
            parser.commit();
        }
        File f2 = new File(((tmpDir.getAbsolutePath()) + "/file2"));
        Files.write("file2line1\nfile2line2\n", f2, Charsets.UTF_8);
        for (int i = 0; i < 2; i++) {
            out.add(TestSpoolingFileLineReader.bodyAsString(parser.readEvent()));
            parser.commit();
        }
        // Not reached
    }

    // Test a basic case where a commit is missed.
    @Test
    public void testBasicCommitFailure() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + ("file1line5\nfile1line6\nfile1line7\nfile1line8\n" + "file1line9\nfile1line10\nfile1line11\nfile1line12\n")), f1, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser = getParser();
        List<String> out1 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(4));
        Assert.assertTrue(out1.contains("file1line1"));
        Assert.assertTrue(out1.contains("file1line2"));
        Assert.assertTrue(out1.contains("file1line3"));
        Assert.assertTrue(out1.contains("file1line4"));
        List<String> out2 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(4));
        Assert.assertTrue(out2.contains("file1line1"));
        Assert.assertTrue(out2.contains("file1line2"));
        Assert.assertTrue(out2.contains("file1line3"));
        Assert.assertTrue(out2.contains("file1line4"));
        parser.commit();
        List<String> out3 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(4));
        Assert.assertTrue(out3.contains("file1line5"));
        Assert.assertTrue(out3.contains("file1line6"));
        Assert.assertTrue(out3.contains("file1line7"));
        Assert.assertTrue(out3.contains("file1line8"));
        parser.commit();
        List<String> out4 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(4));
        Assert.assertEquals(4, out4.size());
        Assert.assertTrue(out4.contains("file1line9"));
        Assert.assertTrue(out4.contains("file1line10"));
        Assert.assertTrue(out4.contains("file1line11"));
        Assert.assertTrue(out4.contains("file1line12"));
    }

    // Test a case where a commit is missed and the buffer size shrinks.
    @Test
    public void testBasicCommitFailureAndBufferSizeChanges() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + ("file1line5\nfile1line6\nfile1line7\nfile1line8\n" + "file1line9\nfile1line10\nfile1line11\nfile1line12\n")), f1, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser = getParser();
        List<String> out1 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(5));
        Assert.assertTrue(out1.contains("file1line1"));
        Assert.assertTrue(out1.contains("file1line2"));
        Assert.assertTrue(out1.contains("file1line3"));
        Assert.assertTrue(out1.contains("file1line4"));
        Assert.assertTrue(out1.contains("file1line5"));
        List<String> out2 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(2));
        Assert.assertTrue(out2.contains("file1line1"));
        Assert.assertTrue(out2.contains("file1line2"));
        parser.commit();
        List<String> out3 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(2));
        Assert.assertTrue(out3.contains("file1line3"));
        Assert.assertTrue(out3.contains("file1line4"));
        parser.commit();
        List<String> out4 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(2));
        Assert.assertTrue(out4.contains("file1line5"));
        Assert.assertTrue(out4.contains("file1line6"));
        parser.commit();
        List<String> out5 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(2));
        Assert.assertTrue(out5.contains("file1line7"));
        Assert.assertTrue(out5.contains("file1line8"));
        parser.commit();
        List<String> out6 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(15));
        Assert.assertTrue(out6.contains("file1line9"));
        Assert.assertTrue(out6.contains("file1line10"));
        Assert.assertTrue(out6.contains("file1line11"));
        Assert.assertTrue(out6.contains("file1line12"));
    }

    // Test when a competing destination file is found and it does not match.
    @Test(expected = IllegalStateException.class)
    public void testDestinationExistsAndDifferentFile() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        File f1Completed = new File((((tmpDir.getAbsolutePath()) + "/file1") + (TestSpoolingFileLineReader.completedSuffix)));
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        Files.write("file1line1\nfile1XXXe2\n", f1Completed, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser = getParser();
        List<String> out = Lists.newArrayList();
        for (int i = 0; i < 2; i++) {
            out.add(TestSpoolingFileLineReader.bodyAsString(parser.readEvent()));
            parser.commit();
        }
        File f2 = new File(((tmpDir.getAbsolutePath()) + "/file2"));
        Files.write("file2line1\nfile2line2\n", f2, Charsets.UTF_8);
        for (int i = 0; i < 2; i++) {
            out.add(TestSpoolingFileLineReader.bodyAsString(parser.readEvent()));
            parser.commit();
        }
        // Not reached
    }

    // Empty files should be treated the same as other files and rolled.
    @Test
    public void testBehaviorWithEmptyFile() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file0"));
        Files.touch(f1);
        ReliableSpoolingFileEventReader parser = getParser();
        File f2 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f2, Charsets.UTF_8);
        // Skip over first file, which is empty, and will return an empty event.
        Event event = parser.readEvent();
        Assert.assertEquals(0, event.getBody().length);
        List<String> out = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(8));
        parser.commit();
        Assert.assertEquals(8, out.size());
        Assert.assertTrue(out.contains("file1line1"));
        Assert.assertTrue(out.contains("file1line2"));
        Assert.assertTrue(out.contains("file1line3"));
        Assert.assertTrue(out.contains("file1line4"));
        Assert.assertTrue(out.contains("file1line5"));
        Assert.assertTrue(out.contains("file1line6"));
        Assert.assertTrue(out.contains("file1line7"));
        Assert.assertTrue(out.contains("file1line8"));
        Assert.assertNull(parser.readEvent());
        // Make sure original is deleted
        List<File> outFiles = Lists.newArrayList(tmpDir.listFiles(directoryFilter()));
        Assert.assertEquals(2, outFiles.size());
        Assert.assertTrue(("Outfiles should have file0 & file1: " + outFiles), outFiles.contains(new File((((tmpDir) + "/file0") + (TestSpoolingFileLineReader.completedSuffix)))));
        Assert.assertTrue(("Outfiles should have file0 & file1: " + outFiles), outFiles.contains(new File((((tmpDir) + "/file1") + (TestSpoolingFileLineReader.completedSuffix)))));
    }

    @Test
    public void testBatchedReadsWithinAFile() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser = getParser();
        List<String> out = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(5));
        parser.commit();
        // Make sure we got every line
        Assert.assertEquals(5, out.size());
        Assert.assertTrue(out.contains("file1line1"));
        Assert.assertTrue(out.contains("file1line2"));
        Assert.assertTrue(out.contains("file1line3"));
        Assert.assertTrue(out.contains("file1line4"));
        Assert.assertTrue(out.contains("file1line5"));
    }

    @Test
    public void testBatchedReadsAcrossFileBoundary() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser = getParser();
        List<String> out1 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(5));
        parser.commit();
        File f2 = new File(((tmpDir.getAbsolutePath()) + "/file2"));
        Files.write(("file2line1\nfile2line2\nfile2line3\nfile2line4\n" + "file2line5\nfile2line6\nfile2line7\nfile2line8\n"), f2, Charsets.UTF_8);
        List<String> out2 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(5));
        parser.commit();
        List<String> out3 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(5));
        parser.commit();
        // Should have first 5 lines of file1
        Assert.assertEquals(5, out1.size());
        Assert.assertTrue(out1.contains("file1line1"));
        Assert.assertTrue(out1.contains("file1line2"));
        Assert.assertTrue(out1.contains("file1line3"));
        Assert.assertTrue(out1.contains("file1line4"));
        Assert.assertTrue(out1.contains("file1line5"));
        // Should have 3 remaining lines of file1
        Assert.assertEquals(3, out2.size());
        Assert.assertTrue(out2.contains("file1line6"));
        Assert.assertTrue(out2.contains("file1line7"));
        Assert.assertTrue(out2.contains("file1line8"));
        // Should have first 5 lines of file2
        Assert.assertEquals(5, out3.size());
        Assert.assertTrue(out3.contains("file2line1"));
        Assert.assertTrue(out3.contains("file2line2"));
        Assert.assertTrue(out3.contains("file2line3"));
        Assert.assertTrue(out3.contains("file2line4"));
        Assert.assertTrue(out3.contains("file2line5"));
        // file1 should be moved now
        List<File> outFiles = Lists.newArrayList(tmpDir.listFiles(directoryFilter()));
        Assert.assertEquals(2, outFiles.size());
        Assert.assertTrue(outFiles.contains(new File((((tmpDir) + "/file1") + (TestSpoolingFileLineReader.completedSuffix)))));
        Assert.assertTrue(outFiles.contains(new File(((tmpDir) + "/file2"))));
    }

    // Test the case where we read finish reading and fully commit a file, then
    // the directory is empty.
    @Test
    public void testEmptyDirectoryAfterCommittingFile() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser = getParser();
        List<String> allLines = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(2));
        Assert.assertEquals(2, allLines.size());
        parser.commit();
        List<String> empty = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(10));
        Assert.assertEquals(0, empty.size());
    }

    // When a line violates the character limit we should truncate it.
    @Test
    public void testLineExceedsMaxLineLength() throws IOException {
        final int maxLineLength = 12;
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + (("file1line5\nfile1line6\nfile1line7\nfile1line8\n" + "reallyreallyreallyreallyLongLineHerefile1line9\n") + "file1line10\nfile1line11\nfile1line12\nfile1line13\n")), f1, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser = getParser(maxLineLength);
        List<String> out1 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(5));
        Assert.assertTrue(out1.contains("file1line1"));
        Assert.assertTrue(out1.contains("file1line2"));
        Assert.assertTrue(out1.contains("file1line3"));
        Assert.assertTrue(out1.contains("file1line4"));
        Assert.assertTrue(out1.contains("file1line5"));
        parser.commit();
        List<String> out2 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(4));
        Assert.assertTrue(out2.contains("file1line6"));
        Assert.assertTrue(out2.contains("file1line7"));
        Assert.assertTrue(out2.contains("file1line8"));
        Assert.assertTrue(out2.contains("reallyreally"));
        parser.commit();
        List<String> out3 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(5));
        Assert.assertTrue(out3.contains("reallyreally"));
        Assert.assertTrue(out3.contains("LongLineHere"));
        Assert.assertTrue(out3.contains("file1line9"));
        Assert.assertTrue(out3.contains("file1line10"));
        Assert.assertTrue(out3.contains("file1line11"));
        parser.commit();
        List<String> out4 = TestSpoolingFileLineReader.bodiesAsStrings(parser.readEvents(5));
        Assert.assertTrue(out4.contains("file1line12"));
        Assert.assertTrue(out4.contains("file1line13"));
        Assert.assertEquals(2, out4.size());
        parser.commit();
        Assert.assertEquals(0, parser.readEvents(5).size());
    }

    @Test
    public void testNameCorrespondsToLatestRead() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        ReliableSpoolingFileEventReader parser = getParser();
        parser.readEvents(5);
        parser.commit();
        Assert.assertNotNull(parser.getLastFileRead());
        Assert.assertTrue(parser.getLastFileRead().endsWith("file1"));
        File f2 = new File(((tmpDir.getAbsolutePath()) + "/file2"));
        Files.write(("file2line1\nfile2line2\nfile2line3\nfile2line4\n" + "file2line5\nfile2line6\nfile2line7\nfile2line8\n"), f2, Charsets.UTF_8);
        parser.readEvents(5);
        parser.commit();
        Assert.assertNotNull(parser.getLastFileRead());
        Assert.assertTrue(parser.getLastFileRead().endsWith("file1"));
        parser.readEvents(5);
        parser.commit();
        Assert.assertNotNull(parser.getLastFileRead());
        Assert.assertTrue(parser.getLastFileRead().endsWith("file2"));
        parser.readEvents(5);
        Assert.assertTrue(parser.getLastFileRead().endsWith("file2"));
        parser.readEvents(5);
        Assert.assertTrue(parser.getLastFileRead().endsWith("file2"));
    }
}

