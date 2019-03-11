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
package org.apache.flume.source.taildir;


import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.flume.Event;
import org.junit.Assert;
import org.junit.Test;


public class TestTaildirEventReader {
    private File tmpDir;

    private String posFilePath;

    // Create three multi-line files then read them back out. Ensures that
    // lines and appended ones are read correctly from files.
    @Test
    public void testBasicReadFiles() throws IOException {
        File f1 = new File(tmpDir, "file1");
        File f2 = new File(tmpDir, "file2");
        File f3 = new File(tmpDir, "file3");
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        Files.write("file2line1\nfile2line2\n", f2, Charsets.UTF_8);
        Files.write("file3line1\nfile3line2\n", f3, Charsets.UTF_8);
        ReliableTaildirEventReader reader = getReader();
        List<String> out = Lists.newArrayList();
        for (TailFile tf : reader.getTailFiles().values()) {
            List<String> bodies = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 2));
            out.addAll(bodies);
            reader.commit();
        }
        Assert.assertEquals(6, out.size());
        // Make sure we got every line
        Assert.assertTrue(out.contains("file1line1"));
        Assert.assertTrue(out.contains("file1line2"));
        Assert.assertTrue(out.contains("file2line1"));
        Assert.assertTrue(out.contains("file2line2"));
        Assert.assertTrue(out.contains("file3line1"));
        Assert.assertTrue(out.contains("file3line2"));
        Files.append("file3line3\nfile3line4\n", f3, Charsets.UTF_8);
        reader.updateTailFiles();
        for (TailFile tf : reader.getTailFiles().values()) {
            List<String> bodies = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 2));
            out.addAll(bodies);
            reader.commit();
        }
        Assert.assertEquals(8, out.size());
        Assert.assertTrue(out.contains("file3line3"));
        Assert.assertTrue(out.contains("file3line4"));
    }

    // Tests deleting a file
    @Test
    public void testDeleteFiles() throws IOException {
        File f1 = new File(tmpDir, "file1");
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        // Caching is used to be able to reproduce the problem when a file is deleted
        // right before the inode is fetched
        ReliableTaildirEventReader reader = getReader(false, true);
        File dir = f1.getParentFile();
        long lastModified = dir.lastModified();
        f1.delete();
        dir.setLastModified((lastModified - 1000));// substract a second to be sure the cache is used

        reader.updateTailFiles();
    }

    // Make sure this works when there are initially no files
    // and we finish reading all files and fully commit.
    @Test
    public void testInitiallyEmptyDirAndBehaviorAfterReadingAll() throws IOException {
        ReliableTaildirEventReader reader = getReader();
        List<Long> fileInodes = reader.updateTailFiles();
        Assert.assertEquals(0, fileInodes.size());
        File f1 = new File(tmpDir, "file1");
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        reader.updateTailFiles();
        List<String> out = null;
        for (TailFile tf : reader.getTailFiles().values()) {
            out = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 2));
            reader.commit();
        }
        Assert.assertEquals(2, out.size());
        // Make sure we got every line
        Assert.assertTrue(out.contains("file1line1"));
        Assert.assertTrue(out.contains("file1line2"));
        reader.updateTailFiles();
        List<String> empty = null;
        for (TailFile tf : reader.getTailFiles().values()) {
            empty = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 15));
            reader.commit();
        }
        Assert.assertEquals(0, empty.size());
    }

    // Test a basic case where a commit is missed.
    @Test
    public void testBasicCommitFailure() throws IOException {
        File f1 = new File(tmpDir, "file1");
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + ("file1line5\nfile1line6\nfile1line7\nfile1line8\n" + "file1line9\nfile1line10\nfile1line11\nfile1line12\n")), f1, Charsets.UTF_8);
        ReliableTaildirEventReader reader = getReader();
        List<String> out1 = null;
        for (TailFile tf : reader.getTailFiles().values()) {
            out1 = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 4));
        }
        Assert.assertTrue(out1.contains("file1line1"));
        Assert.assertTrue(out1.contains("file1line2"));
        Assert.assertTrue(out1.contains("file1line3"));
        Assert.assertTrue(out1.contains("file1line4"));
        List<String> out2 = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(4));
        Assert.assertTrue(out2.contains("file1line1"));
        Assert.assertTrue(out2.contains("file1line2"));
        Assert.assertTrue(out2.contains("file1line3"));
        Assert.assertTrue(out2.contains("file1line4"));
        reader.commit();
        List<String> out3 = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(4));
        Assert.assertTrue(out3.contains("file1line5"));
        Assert.assertTrue(out3.contains("file1line6"));
        Assert.assertTrue(out3.contains("file1line7"));
        Assert.assertTrue(out3.contains("file1line8"));
        reader.commit();
        List<String> out4 = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(4));
        Assert.assertEquals(4, out4.size());
        Assert.assertTrue(out4.contains("file1line9"));
        Assert.assertTrue(out4.contains("file1line10"));
        Assert.assertTrue(out4.contains("file1line11"));
        Assert.assertTrue(out4.contains("file1line12"));
    }

    // Test a case where a commit is missed and the batch size changes.
    @Test
    public void testBasicCommitFailureAndBatchSizeChanges() throws IOException {
        File f1 = new File(tmpDir, "file1");
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        ReliableTaildirEventReader reader = getReader();
        List<String> out1 = null;
        for (TailFile tf : reader.getTailFiles().values()) {
            out1 = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 5));
        }
        Assert.assertTrue(out1.contains("file1line1"));
        Assert.assertTrue(out1.contains("file1line2"));
        Assert.assertTrue(out1.contains("file1line3"));
        Assert.assertTrue(out1.contains("file1line4"));
        Assert.assertTrue(out1.contains("file1line5"));
        List<String> out2 = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(2));
        Assert.assertTrue(out2.contains("file1line1"));
        Assert.assertTrue(out2.contains("file1line2"));
        reader.commit();
        List<String> out3 = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(2));
        Assert.assertTrue(out3.contains("file1line3"));
        Assert.assertTrue(out3.contains("file1line4"));
        reader.commit();
        List<String> out4 = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(15));
        Assert.assertTrue(out4.contains("file1line5"));
        Assert.assertTrue(out4.contains("file1line6"));
        Assert.assertTrue(out4.contains("file1line7"));
        Assert.assertTrue(out4.contains("file1line8"));
    }

    @Test
    public void testIncludeEmptyFile() throws IOException {
        File f1 = new File(tmpDir, "file1");
        File f2 = new File(tmpDir, "file2");
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        Files.touch(f2);
        ReliableTaildirEventReader reader = getReader();
        // Expect to read nothing from empty file
        List<String> out = Lists.newArrayList();
        for (TailFile tf : reader.getTailFiles().values()) {
            out.addAll(TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 5)));
            reader.commit();
        }
        Assert.assertEquals(2, out.size());
        Assert.assertTrue(out.contains("file1line1"));
        Assert.assertTrue(out.contains("file1line2"));
        Assert.assertNull(reader.readEvent());
    }

    @Test
    public void testBackoffWithoutNewLine() throws IOException {
        File f1 = new File(tmpDir, "file1");
        Files.write("file1line1\nfile1", f1, Charsets.UTF_8);
        ReliableTaildirEventReader reader = getReader();
        List<String> out = Lists.newArrayList();
        // Expect to read only the line with newline
        for (TailFile tf : reader.getTailFiles().values()) {
            out.addAll(TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 5)));
            reader.commit();
        }
        Assert.assertEquals(1, out.size());
        Assert.assertTrue(out.contains("file1line1"));
        Files.append("line2\nfile1line3\nfile1line4", f1, Charsets.UTF_8);
        for (TailFile tf : reader.getTailFiles().values()) {
            out.addAll(TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 5)));
            reader.commit();
        }
        Assert.assertEquals(3, out.size());
        Assert.assertTrue(out.contains("file1line2"));
        Assert.assertTrue(out.contains("file1line3"));
        // Should read the last line if it finally has no newline
        out.addAll(TestTaildirEventReader.bodiesAsStrings(reader.readEvents(5, false)));
        reader.commit();
        Assert.assertEquals(4, out.size());
        Assert.assertTrue(out.contains("file1line4"));
    }

    @Test
    public void testBatchedReadsAcrossFileBoundary() throws IOException {
        File f1 = new File(tmpDir, "file1");
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        ReliableTaildirEventReader reader = getReader();
        List<String> out1 = Lists.newArrayList();
        for (TailFile tf : reader.getTailFiles().values()) {
            out1.addAll(TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 5)));
            reader.commit();
        }
        File f2 = new File(tmpDir, "file2");
        Files.write(("file2line1\nfile2line2\nfile2line3\nfile2line4\n" + "file2line5\nfile2line6\nfile2line7\nfile2line8\n"), f2, Charsets.UTF_8);
        List<String> out2 = TestTaildirEventReader.bodiesAsStrings(reader.readEvents(5));
        reader.commit();
        reader.updateTailFiles();
        List<String> out3 = Lists.newArrayList();
        for (TailFile tf : reader.getTailFiles().values()) {
            out3.addAll(TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 5)));
            reader.commit();
        }
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
    }

    @Test
    public void testLargeNumberOfFiles() throws IOException {
        int fileNum = 1000;
        Set<String> expected = Sets.newHashSet();
        for (int i = 0; i < fileNum; i++) {
            String data = "data" + i;
            File f = new File(tmpDir, ("file" + i));
            Files.write((data + "\n"), f, Charsets.UTF_8);
            expected.add(data);
        }
        ReliableTaildirEventReader reader = getReader();
        for (TailFile tf : reader.getTailFiles().values()) {
            List<Event> events = reader.readEvents(tf, 10);
            for (Event e : events) {
                expected.remove(new String(e.getBody()));
            }
            reader.commit();
        }
        Assert.assertEquals(0, expected.size());
    }

    @Test
    public void testLoadPositionFile() throws IOException {
        File f1 = new File(tmpDir, "file1");
        File f2 = new File(tmpDir, "file2");
        File f3 = new File(tmpDir, "file3");
        Files.write("file1line1\nfile1line2\nfile1line3\n", f1, Charsets.UTF_8);
        Files.write("file2line1\nfile2line2\n", f2, Charsets.UTF_8);
        Files.write("file3line1\n", f3, Charsets.UTF_8);
        ReliableTaildirEventReader reader = getReader();
        Map<Long, TailFile> tailFiles = reader.getTailFiles();
        long pos = f2.length();
        int i = 1;
        File posFile = new File(posFilePath);
        for (TailFile tf : tailFiles.values()) {
            Files.append((i == 1 ? "[" : ""), posFile, Charsets.UTF_8);
            Files.append(String.format("{\"inode\":%s,\"pos\":%s,\"file\":\"%s\"}", tf.getInode(), pos, tf.getPath()), posFile, Charsets.UTF_8);
            Files.append((i == 3 ? "]" : ","), posFile, Charsets.UTF_8);
            i++;
        }
        reader.loadPositionFile(posFilePath);
        for (TailFile tf : tailFiles.values()) {
            if (tf.getPath().equals(((tmpDir) + "file3"))) {
                // when given position is larger than file size
                Assert.assertEquals(0, tf.getPos());
            } else {
                Assert.assertEquals(pos, tf.getPos());
            }
        }
    }

    @Test
    public void testSkipToEndPosition() throws IOException {
        ReliableTaildirEventReader reader = getReader();
        File f1 = new File(tmpDir, "file1");
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        reader.updateTailFiles();
        for (TailFile tf : reader.getTailFiles().values()) {
            if (tf.getPath().equals(((tmpDir) + "file1"))) {
                Assert.assertEquals(0, tf.getPos());
            }
        }
        File f2 = new File(tmpDir, "file2");
        Files.write("file2line1\nfile2line2\n", f2, Charsets.UTF_8);
        // Expect to skip to EOF the read position when skipToEnd option is true
        reader.updateTailFiles(true);
        for (TailFile tf : reader.getTailFiles().values()) {
            if (tf.getPath().equals(((tmpDir) + "file2"))) {
                Assert.assertEquals(f2.length(), tf.getPos());
            }
        }
    }

    @Test
    public void testByteOffsetHeader() throws IOException {
        File f1 = new File(tmpDir, "file1");
        String line1 = "file1line1\n";
        String line2 = "file1line2\n";
        String line3 = "file1line3\n";
        Files.write(((line1 + line2) + line3), f1, Charsets.UTF_8);
        ReliableTaildirEventReader reader = getReader(true, false);
        List<String> headers = null;
        for (TailFile tf : reader.getTailFiles().values()) {
            headers = TestTaildirEventReader.headersAsStrings(reader.readEvents(tf, 5), TaildirSourceConfigurationConstants.BYTE_OFFSET_HEADER_KEY);
            reader.commit();
        }
        Assert.assertEquals(3, headers.size());
        // Make sure we got byte offset position
        Assert.assertTrue(headers.contains(String.valueOf(0)));
        Assert.assertTrue(headers.contains(String.valueOf(line1.length())));
        Assert.assertTrue(headers.contains(String.valueOf((line1 + line2).length())));
    }

    @Test
    public void testNewLineBoundaries() throws IOException {
        File f1 = new File(tmpDir, "file1");
        Files.write("file1line1\nfile1line2\rfile1line2\nfile1line3\r\nfile1line4\n", f1, Charsets.UTF_8);
        ReliableTaildirEventReader reader = getReader();
        List<String> out = Lists.newArrayList();
        for (TailFile tf : reader.getTailFiles().values()) {
            out.addAll(TestTaildirEventReader.bodiesAsStrings(reader.readEvents(tf, 5)));
            reader.commit();
        }
        Assert.assertEquals(4, out.size());
        // Should treat \n as line boundary
        Assert.assertTrue(out.contains("file1line1"));
        // Should not treat \r as line boundary
        Assert.assertTrue(out.contains("file1line2\rfile1line2"));
        // Should treat \r\n as line boundary
        Assert.assertTrue(out.contains("file1line3"));
        Assert.assertTrue(out.contains("file1line4"));
    }

    // Ensure tail file is set to be read when its last updated time
    // equals the underlying file's modification time and there are
    // pending bytes to be read.
    @Test
    public void testUpdateWhenLastUpdatedSameAsModificationTime() throws IOException {
        File file = new File(tmpDir, "file");
        Files.write("line1\n", file, Charsets.UTF_8);
        ReliableTaildirEventReader reader = getReader();
        for (TailFile tf : reader.getTailFiles().values()) {
            reader.readEvents(tf, 1);
            reader.commit();
        }
        Files.append("line2\n", file, Charsets.UTF_8);
        for (TailFile tf : reader.getTailFiles().values()) {
            tf.setLastUpdated(file.lastModified());
        }
        reader.updateTailFiles();
        for (TailFile tf : reader.getTailFiles().values()) {
            Assert.assertEquals(true, tf.needTail());
        }
    }
}

