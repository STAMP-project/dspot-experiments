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


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TestTaildirMatcher {
    private File tmpDir;

    private Map<String, File> files;

    private boolean isCachingNeeded = true;

    final String msgAlreadyExistingFile = "a file was not found but it was created before matcher";

    final String msgAfterNewFileCreated = "files which were created after last check are not found";

    final String msgAfterAppend = "a file was not found although it was just appended within the dir";

    final String msgEmptyDir = "empty dir should return an empty list";

    final String msgNoMatch = "no match should return an empty list";

    final String msgSubDirs = "only files on the same level as the pattern should be returned";

    final String msgNoChange = "file wasn't touched after last check cannot be found";

    final String msgAfterDelete = "file was returned even after it was deleted";

    @Test
    public void getMatchingFiles() throws Exception {
        append("file0");
        append("file1");
        TaildirMatcher tm = new TaildirMatcher("f1", (((tmpDir.getAbsolutePath()) + (File.separator)) + "file.*"), isCachingNeeded);
        List<String> files = TestTaildirMatcher.filesToNames(tm.getMatchingFiles());
        Assert.assertEquals(msgAlreadyExistingFile, 2, files.size());
        Assert.assertTrue(msgAlreadyExistingFile, files.contains("file1"));
        append("file1");
        files = TestTaildirMatcher.filesToNames(tm.getMatchingFiles());
        Assert.assertEquals(msgAfterNewFileCreated, 2, files.size());
        Assert.assertTrue(msgAfterNewFileCreated, files.contains("file0"));
        Assert.assertTrue(msgAfterNewFileCreated, files.contains("file1"));
        append("file2");
        append("file3");
        files = TestTaildirMatcher.filesToNames(tm.getMatchingFiles());
        Assert.assertEquals(msgAfterAppend, 4, files.size());
        Assert.assertTrue(msgAfterAppend, files.contains("file0"));
        Assert.assertTrue(msgAfterAppend, files.contains("file1"));
        Assert.assertTrue(msgAfterAppend, files.contains("file2"));
        Assert.assertTrue(msgAfterAppend, files.contains("file3"));
        this.files.get("file0").delete();
        files = TestTaildirMatcher.filesToNames(tm.getMatchingFiles());
        Assert.assertEquals(msgAfterDelete, 3, files.size());
        Assert.assertFalse(msgAfterDelete, files.contains("file0"));
        Assert.assertTrue(msgNoChange, files.contains("file1"));
        Assert.assertTrue(msgNoChange, files.contains("file2"));
        Assert.assertTrue(msgNoChange, files.contains("file3"));
    }

    @Test
    public void getMatchingFilesNoCache() throws Exception {
        append("file0");
        append("file1");
        TaildirMatcher tm = new TaildirMatcher("f1", (((tmpDir.getAbsolutePath()) + (File.separator)) + "file.*"), false);
        List<String> files = TestTaildirMatcher.filesToNames(tm.getMatchingFiles());
        Assert.assertEquals(msgAlreadyExistingFile, 2, files.size());
        Assert.assertTrue(msgAlreadyExistingFile, files.contains("file1"));
        append("file1");
        files = TestTaildirMatcher.filesToNames(tm.getMatchingFiles());
        Assert.assertEquals(msgAfterAppend, 2, files.size());
        Assert.assertTrue(msgAfterAppend, files.contains("file0"));
        Assert.assertTrue(msgAfterAppend, files.contains("file1"));
        append("file2");
        append("file3");
        files = TestTaildirMatcher.filesToNames(tm.getMatchingFiles());
        Assert.assertEquals(msgAfterNewFileCreated, 4, files.size());
        Assert.assertTrue(msgAfterNewFileCreated, files.contains("file0"));
        Assert.assertTrue(msgAfterNewFileCreated, files.contains("file1"));
        Assert.assertTrue(msgAfterNewFileCreated, files.contains("file2"));
        Assert.assertTrue(msgAfterNewFileCreated, files.contains("file3"));
        this.files.get("file0").delete();
        files = TestTaildirMatcher.filesToNames(tm.getMatchingFiles());
        Assert.assertEquals(msgAfterDelete, 3, files.size());
        Assert.assertFalse(msgAfterDelete, files.contains("file0"));
        Assert.assertTrue(msgNoChange, files.contains("file1"));
        Assert.assertTrue(msgNoChange, files.contains("file2"));
        Assert.assertTrue(msgNoChange, files.contains("file3"));
    }

    @Test
    public void testEmtpyDirMatching() throws Exception {
        TaildirMatcher tm = new TaildirMatcher("empty", (((tmpDir.getAbsolutePath()) + (File.separator)) + ".*"), isCachingNeeded);
        List<File> files = tm.getMatchingFiles();
        Assert.assertNotNull(msgEmptyDir, files);
        Assert.assertTrue(msgEmptyDir, files.isEmpty());
    }

    @Test
    public void testNoMatching() throws Exception {
        TaildirMatcher tm = new TaildirMatcher("nomatch", (((tmpDir.getAbsolutePath()) + (File.separator)) + "abracadabra_nonexisting"), isCachingNeeded);
        List<File> files = tm.getMatchingFiles();
        Assert.assertNotNull(msgNoMatch, files);
        Assert.assertTrue(msgNoMatch, files.isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void testNonExistingDir() {
        TaildirMatcher tm = new TaildirMatcher("exception", "/abracadabra/doesntexist/.*", isCachingNeeded);
    }

    @Test
    public void testDirectoriesAreNotListed() throws Exception {
        new File(tmpDir, "outerFile").createNewFile();
        new File(tmpDir, "recursiveDir").mkdir();
        new File((((tmpDir) + (File.separator)) + "recursiveDir"), "innerFile").createNewFile();
        TaildirMatcher tm = new TaildirMatcher("f1", (((tmpDir.getAbsolutePath()) + (File.separator)) + ".*"), isCachingNeeded);
        List<String> files = TestTaildirMatcher.filesToNames(tm.getMatchingFiles());
        Assert.assertEquals(msgSubDirs, 1, files.size());
        Assert.assertTrue(msgSubDirs, files.contains("outerFile"));
    }

    @Test
    public void testRegexFileNameFiltering() throws IOException {
        append("a.log");
        append("a.log.1");
        append("b.log");
        append("c.log.yyyy.MM-01");
        append("c.log.yyyy.MM-02");
        // Tail a.log and b.log
        TaildirMatcher tm1 = new TaildirMatcher("ab", (((tmpDir.getAbsolutePath()) + (File.separator)) + "[ab].log"), isCachingNeeded);
        // Tail files that starts with c.log
        TaildirMatcher tm2 = new TaildirMatcher("c", (((tmpDir.getAbsolutePath()) + (File.separator)) + "c.log.*"), isCachingNeeded);
        List<String> files1 = TestTaildirMatcher.filesToNames(tm1.getMatchingFiles());
        List<String> files2 = TestTaildirMatcher.filesToNames(tm2.getMatchingFiles());
        Assert.assertEquals(2, files1.size());
        Assert.assertEquals(2, files2.size());
        // Make sure we got every file
        Assert.assertTrue("Regex pattern for ab should have matched a.log file", files1.contains("a.log"));
        Assert.assertFalse("Regex pattern for ab should NOT have matched a.log.1 file", files1.contains("a.log.1"));
        Assert.assertTrue("Regex pattern for ab should have matched b.log file", files1.contains("b.log"));
        Assert.assertTrue("Regex pattern for c should have matched c.log.yyyy-MM-01 file", files2.contains("c.log.yyyy.MM-01"));
        Assert.assertTrue("Regex pattern for c should have matched c.log.yyyy-MM-02 file", files2.contains("c.log.yyyy.MM-02"));
    }
}

