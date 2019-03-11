/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import CoreAttributes.ABSOLUTE_PATH;
import CoreAttributes.FILENAME;
import CoreAttributes.PATH;
import ListFile.DIRECTORY;
import ListFile.FILE_CREATION_TIME_ATTRIBUTE;
import ListFile.FILE_FILTER;
import ListFile.FILE_GROUP_ATTRIBUTE;
import ListFile.FILE_LAST_ACCESS_TIME_ATTRIBUTE;
import ListFile.FILE_LAST_MODIFY_TIME_ATTRIBUTE;
import ListFile.FILE_OWNER_ATTRIBUTE;
import ListFile.FILE_PERMISSIONS_ATTRIBUTE;
import ListFile.FILE_SIZE_ATTRIBUTE;
import ListFile.IGNORE_HIDDEN_FILES;
import ListFile.MAX_AGE;
import ListFile.MAX_SIZE;
import ListFile.MIN_AGE;
import ListFile.MIN_SIZE;
import ListFile.PATH_FILTER;
import ListFile.RECURSE;
import ListFile.REL_SUCCESS;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.list.ListProcessorTestWatcher;
import org.apache.nifi.processors.standard.util.FileInfo;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;

import static ListFile.FILE_MODIFY_DATE_ATTR_FORMAT;


public class TestListFile {
    private final String TESTDIR = "target/test/data/in";

    private final File testDir = new File(TESTDIR);

    private ListFile processor;

    private TestRunner runner;

    private ProcessContext context;

    // Testing factors in milliseconds for file ages that are configured on each run by resetAges()
    // age#millis are relative time references
    // time#millis are absolute time references
    // age#filter are filter label strings for the filter properties
    private Long syncTime = TestListFile.getTestModifiedTime();

    private Long time0millis;

    private Long time1millis;

    private Long time2millis;

    private Long time3millis;

    private Long time4millis;

    private Long time5millis;

    private Long age0millis;

    private Long age1millis;

    private Long age2millis;

    private Long age3millis;

    private Long age4millis;

    private Long age5millis;

    private String age0;

    private String age1;

    private String age2;

    private String age3;

    private String age4;

    private String age5;

    @Rule
    public ListProcessorTestWatcher dumpState = new ListProcessorTestWatcher(() -> {
        try {
            return runner.getStateManager().getState(Scope.LOCAL).toMap();
        } catch ( e) {
            throw new <e>RuntimeException("Failed to retrieve state");
        }
    }, () -> listFiles(testDir).stream().map(( f) -> new FileInfo.Builder().filename(f.getName()).lastModifiedTime(f.lastModified()).build()).collect(Collectors.toList()), () -> runner.getFlowFilesForRelationship(AbstractListProcessor.REL_SUCCESS).stream().map(( m) -> ((FlowFile) (m))).collect(Collectors.toList())) {
        @Override
        protected void finished(Description description) {
            try {
                // In order to refer files in testDir, we want to execute this rule before tearDown, because tearDown removes files.
                // And @After is always executed before @Rule.
                tearDown();
            } catch (Exception e) {
                throw new RuntimeException("Failed to tearDown.", e);
            }
        }
    };

    @Test
    public void testGetPath() {
        runner.setProperty(DIRECTORY, "/dir/test1");
        Assert.assertEquals("/dir/test1", processor.getPath(context));
        runner.setProperty(DIRECTORY, "${literal(\"/DIR/TEST2\"):toLower()}");
        Assert.assertEquals("/dir/test2", processor.getPath(context));
    }

    @Test
    public void testPerformListing() throws Exception {
        runner.setProperty(DIRECTORY, testDir.getAbsolutePath());
        runNext();
        runner.assertTransferCount(REL_SUCCESS, 0);
        // create first file
        final File file1 = new File(((TESTDIR) + "/listing1.txt"));
        Assert.assertTrue(file1.createNewFile());
        Assert.assertTrue(file1.setLastModified(time4millis));
        // process first file and set new timestamp
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles1 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles1.size());
        // create second file
        final File file2 = new File(((TESTDIR) + "/listing2.txt"));
        Assert.assertTrue(file2.createNewFile());
        Assert.assertTrue(file2.setLastModified(time2millis));
        // process second file after timestamp
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles2 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles2.size());
        // create third file
        final File file3 = new File(((TESTDIR) + "/listing3.txt"));
        Assert.assertTrue(file3.createNewFile());
        Assert.assertTrue(file3.setLastModified(time4millis));
        // process third file before timestamp
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles3 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, successFiles3.size());
        // force state to reset and process all files
        runner.removeProperty(DIRECTORY);
        runner.setProperty(DIRECTORY, testDir.getAbsolutePath());
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles4 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(3, successFiles4.size());
        runNext();
        runner.assertTransferCount(REL_SUCCESS, 0);
    }

    @Test
    public void testFilterAge() throws Exception {
        final File file1 = new File(((TESTDIR) + "/age1.txt"));
        Assert.assertTrue(file1.createNewFile());
        final File file2 = new File(((TESTDIR) + "/age2.txt"));
        Assert.assertTrue(file2.createNewFile());
        final File file3 = new File(((TESTDIR) + "/age3.txt"));
        Assert.assertTrue(file3.createNewFile());
        final Function<Boolean, Object> runNext = ( resetAges) -> {
            if (resetAges) {
                resetAges();
                Assert.assertTrue(file1.setLastModified(time0millis));
                Assert.assertTrue(file2.setLastModified(time2millis));
                Assert.assertTrue(file3.setLastModified(time4millis));
            }
            try {
                runNext();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return null;
        };
        // check all files
        runner.setProperty(DIRECTORY, testDir.getAbsolutePath());
        runNext.apply(true);
        runner.assertTransferCount(REL_SUCCESS, 3);
        // processor updates internal state, it shouldn't pick the same ones.
        runNext.apply(false);
        runner.assertTransferCount(REL_SUCCESS, 0);
        // exclude oldest
        runner.setProperty(MIN_AGE, age0);
        runner.setProperty(MAX_AGE, age3);
        runNext.apply(true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles2 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, successFiles2.size());
        Assert.assertEquals(file2.getName(), successFiles2.get(0).getAttribute("filename"));
        Assert.assertEquals(file1.getName(), successFiles2.get(1).getAttribute("filename"));
        // exclude newest
        runner.setProperty(MIN_AGE, age1);
        runner.setProperty(MAX_AGE, age5);
        runNext.apply(true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles3 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, successFiles3.size());
        Assert.assertEquals(file3.getName(), successFiles3.get(0).getAttribute("filename"));
        Assert.assertEquals(file2.getName(), successFiles3.get(1).getAttribute("filename"));
        // exclude oldest and newest
        runner.setProperty(MIN_AGE, age1);
        runner.setProperty(MAX_AGE, age3);
        runNext.apply(true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles4 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles4.size());
        Assert.assertEquals(file2.getName(), successFiles4.get(0).getAttribute("filename"));
    }

    @Test
    public void testFilterSize() throws Exception {
        final byte[] bytes1000 = new byte[1000];
        final byte[] bytes5000 = new byte[5000];
        final byte[] bytes10000 = new byte[10000];
        FileOutputStream fos;
        final File file1 = new File(((TESTDIR) + "/size1.txt"));
        Assert.assertTrue(file1.createNewFile());
        fos = new FileOutputStream(file1);
        fos.write(bytes10000);
        fos.close();
        final File file2 = new File(((TESTDIR) + "/size2.txt"));
        Assert.assertTrue(file2.createNewFile());
        fos = new FileOutputStream(file2);
        fos.write(bytes5000);
        fos.close();
        final File file3 = new File(((TESTDIR) + "/size3.txt"));
        Assert.assertTrue(file3.createNewFile());
        fos = new FileOutputStream(file3);
        fos.write(bytes1000);
        fos.close();
        final long now = TestListFile.getTestModifiedTime();
        Assert.assertTrue(file1.setLastModified(now));
        Assert.assertTrue(file2.setLastModified(now));
        Assert.assertTrue(file3.setLastModified(now));
        // check all files
        runner.setProperty(DIRECTORY, testDir.getAbsolutePath());
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles1 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(3, successFiles1.size());
        // exclude largest
        runner.removeProperty(MIN_AGE);
        runner.removeProperty(MAX_AGE);
        runner.setProperty(MIN_SIZE, "0 b");
        runner.setProperty(MAX_SIZE, "7500 b");
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles2 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, successFiles2.size());
        // exclude smallest
        runner.removeProperty(MIN_AGE);
        runner.removeProperty(MAX_AGE);
        runner.setProperty(MIN_SIZE, "2500 b");
        runner.removeProperty(MAX_SIZE);
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles3 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, successFiles3.size());
        // exclude oldest and newest
        runner.removeProperty(MIN_AGE);
        runner.removeProperty(MAX_AGE);
        runner.setProperty(MIN_SIZE, "2500 b");
        runner.setProperty(MAX_SIZE, "7500 b");
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles4 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles4.size());
    }

    @Test
    public void testFilterHidden() throws Exception {
        final long now = TestListFile.getTestModifiedTime();
        FileOutputStream fos;
        final File file1 = new File(((TESTDIR) + "/hidden1.txt"));
        Assert.assertTrue(file1.createNewFile());
        fos = new FileOutputStream(file1);
        fos.close();
        final File file2 = new File(((TESTDIR) + "/.hidden2.txt"));
        Assert.assertTrue(file2.createNewFile());
        fos = new FileOutputStream(file2);
        fos.close();
        FileStore store = Files.getFileStore(file2.toPath());
        if (store.supportsFileAttributeView("dos")) {
            Files.setAttribute(file2.toPath(), "dos:hidden", true);
        }
        Assert.assertTrue(file1.setLastModified(now));
        Assert.assertTrue(file2.setLastModified(now));
        // check all files
        runner.setProperty(DIRECTORY, testDir.getAbsolutePath());
        runner.setProperty(FILE_FILTER, ".*");
        runner.removeProperty(MIN_AGE);
        runner.removeProperty(MAX_AGE);
        runner.removeProperty(MIN_SIZE);
        runner.removeProperty(MAX_SIZE);
        runner.setProperty(IGNORE_HIDDEN_FILES, "false");
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles1 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, successFiles1.size());
        // exclude hidden
        runner.setProperty(IGNORE_HIDDEN_FILES, "true");
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles2 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles2.size());
    }

    @Test
    public void testFilterFilePattern() throws Exception {
        final long now = TestListFile.getTestModifiedTime();
        final File file1 = new File(((TESTDIR) + "/file1-abc-apple.txt"));
        Assert.assertTrue(file1.createNewFile());
        Assert.assertTrue(file1.setLastModified(now));
        final File file2 = new File(((TESTDIR) + "/file2-xyz-apple.txt"));
        Assert.assertTrue(file2.createNewFile());
        Assert.assertTrue(file2.setLastModified(now));
        final File file3 = new File(((TESTDIR) + "/file3-xyz-banana.txt"));
        Assert.assertTrue(file3.createNewFile());
        Assert.assertTrue(file3.setLastModified(now));
        final File file4 = new File(((TESTDIR) + "/file4-pdq-banana.txt"));
        Assert.assertTrue(file4.createNewFile());
        Assert.assertTrue(file4.setLastModified(now));
        // check all files
        runner.setProperty(DIRECTORY, testDir.getAbsolutePath());
        runner.setProperty(FILE_FILTER, FILE_FILTER.getDefaultValue());
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles1 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(4, successFiles1.size());
        // filter file on pattern
        // Modifying FILE_FILTER property reset listing status, so these files will be listed again.
        runner.setProperty(FILE_FILTER, ".*-xyz-.*");
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        runNext();
        runner.assertTransferCount(REL_SUCCESS, 0);
    }

    @Test
    public void testFilterPathPattern() throws Exception {
        final long now = TestListFile.getTestModifiedTime();
        final File subdir1 = new File(((TESTDIR) + "/subdir1"));
        Assert.assertTrue(subdir1.mkdirs());
        final File subdir2 = new File(((TESTDIR) + "/subdir1/subdir2"));
        Assert.assertTrue(subdir2.mkdirs());
        final File file1 = new File(((TESTDIR) + "/file1.txt"));
        Assert.assertTrue(file1.createNewFile());
        Assert.assertTrue(file1.setLastModified(now));
        final File file2 = new File(((TESTDIR) + "/subdir1/file2.txt"));
        Assert.assertTrue(file2.createNewFile());
        Assert.assertTrue(file2.setLastModified(now));
        final File file3 = new File(((TESTDIR) + "/subdir1/subdir2/file3.txt"));
        Assert.assertTrue(file3.createNewFile());
        Assert.assertTrue(file3.setLastModified(now));
        final File file4 = new File(((TESTDIR) + "/subdir1/file4.txt"));
        Assert.assertTrue(file4.createNewFile());
        Assert.assertTrue(file4.setLastModified(now));
        // check all files
        runner.setProperty(DIRECTORY, testDir.getAbsolutePath());
        runner.setProperty(FILE_FILTER, FILE_FILTER.getDefaultValue());
        runner.setProperty(RECURSE, "true");
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles1 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(4, successFiles1.size());
        // filter path on pattern subdir1
        runner.setProperty(PATH_FILTER, "subdir1");
        runner.setProperty(RECURSE, "true");
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles2 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(3, successFiles2.size());
        // filter path on pattern subdir2
        runner.setProperty(PATH_FILTER, "subdir2");
        runner.setProperty(RECURSE, "true");
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles3 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles3.size());
    }

    @Test
    public void testRecurse() throws Exception {
        final long now = TestListFile.getTestModifiedTime();
        final File subdir1 = new File(((TESTDIR) + "/subdir1"));
        Assert.assertTrue(subdir1.mkdirs());
        final File subdir2 = new File(((TESTDIR) + "/subdir1/subdir2"));
        Assert.assertTrue(subdir2.mkdirs());
        final File file1 = new File(((TESTDIR) + "/file1.txt"));
        Assert.assertTrue(file1.createNewFile());
        Assert.assertTrue(file1.setLastModified(now));
        final File file2 = new File(((TESTDIR) + "/subdir1/file2.txt"));
        Assert.assertTrue(file2.createNewFile());
        Assert.assertTrue(file2.setLastModified(now));
        final File file3 = new File(((TESTDIR) + "/subdir1/subdir2/file3.txt"));
        Assert.assertTrue(file3.createNewFile());
        Assert.assertTrue(file3.setLastModified(now));
        // check all files
        runner.setProperty(DIRECTORY, testDir.getAbsolutePath());
        runner.setProperty(RECURSE, "true");
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        final List<MockFlowFile> successFiles1 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        for (final MockFlowFile mff : successFiles1) {
            final String filename = mff.getAttribute(FILENAME.key());
            final String path = mff.getAttribute(PATH.key());
            switch (filename) {
                case "file1.txt" :
                    Assert.assertEquals(("." + (File.separator)), path);
                    mff.assertAttributeEquals(ABSOLUTE_PATH.key(), ((file1.getParentFile().getAbsolutePath()) + (File.separator)));
                    break;
                case "file2.txt" :
                    Assert.assertEquals(("subdir1" + (File.separator)), path);
                    mff.assertAttributeEquals(ABSOLUTE_PATH.key(), ((file2.getParentFile().getAbsolutePath()) + (File.separator)));
                    break;
                case "file3.txt" :
                    Assert.assertEquals(((("subdir1" + (File.separator)) + "subdir2") + (File.separator)), path);
                    mff.assertAttributeEquals(ABSOLUTE_PATH.key(), ((file3.getParentFile().getAbsolutePath()) + (File.separator)));
                    break;
            }
        }
        Assert.assertEquals(3, successFiles1.size());
        // exclude hidden
        runner.setProperty(RECURSE, "false");
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles2 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles2.size());
    }

    @Test
    public void testReadable() throws Exception {
        final long now = TestListFile.getTestModifiedTime();
        final File file1 = new File(((TESTDIR) + "/file1.txt"));
        Assert.assertTrue(file1.createNewFile());
        Assert.assertTrue(file1.setLastModified(now));
        final File file2 = new File(((TESTDIR) + "/file2.txt"));
        Assert.assertTrue(file2.createNewFile());
        Assert.assertTrue(file2.setLastModified(now));
        final File file3 = new File(((TESTDIR) + "/file3.txt"));
        Assert.assertTrue(file3.createNewFile());
        Assert.assertTrue(file3.setLastModified(now));
        // check all files
        runner.setProperty(DIRECTORY, testDir.getAbsolutePath());
        runner.setProperty(RECURSE, "true");
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 3);
    }

    @Test
    public void testAttributesSet() throws Exception {
        // create temp file and time constant
        final File file1 = new File(((TESTDIR) + "/file1.txt"));
        Assert.assertTrue(file1.createNewFile());
        FileOutputStream fos = new FileOutputStream(file1);
        fos.write(new byte[1234]);
        fos.close();
        Assert.assertTrue(file1.setLastModified(time3millis));
        Long time3rounded = (time3millis) - ((time3millis) % 1000);
        String userName = System.getProperty("user.name");
        // validate the file transferred
        runner.setProperty(DIRECTORY, testDir.getAbsolutePath());
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final List<MockFlowFile> successFiles1 = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successFiles1.size());
        // get attribute check values
        final Path file1Path = file1.toPath();
        final Path directoryPath = new File(TESTDIR).toPath();
        final Path relativePath = directoryPath.relativize(file1.toPath().getParent());
        String relativePathString = relativePath.toString();
        relativePathString = (relativePathString.isEmpty()) ? "." + (File.separator) : relativePathString + (File.separator);
        final Path absolutePath = file1.toPath().toAbsolutePath();
        final String absolutePathString = (absolutePath.getParent().toString()) + (File.separator);
        final FileStore store = Files.getFileStore(file1Path);
        final DateFormat formatter = new SimpleDateFormat(FILE_MODIFY_DATE_ATTR_FORMAT, Locale.US);
        final String time3Formatted = formatter.format(time3rounded);
        // check standard attributes
        MockFlowFile mock1 = successFiles1.get(0);
        Assert.assertEquals(relativePathString, mock1.getAttribute(PATH.key()));
        Assert.assertEquals("file1.txt", mock1.getAttribute(FILENAME.key()));
        Assert.assertEquals(absolutePathString, mock1.getAttribute(ABSOLUTE_PATH.key()));
        Assert.assertEquals("1234", mock1.getAttribute(FILE_SIZE_ATTRIBUTE));
        // check attributes dependent on views supported
        if (store.supportsFileAttributeView("basic")) {
            Assert.assertEquals(time3Formatted, mock1.getAttribute(FILE_LAST_MODIFY_TIME_ATTRIBUTE));
            Assert.assertNotNull(mock1.getAttribute(FILE_CREATION_TIME_ATTRIBUTE));
            Assert.assertNotNull(mock1.getAttribute(FILE_LAST_ACCESS_TIME_ATTRIBUTE));
        }
        if (store.supportsFileAttributeView("owner")) {
            // look for username containment to handle Windows domains as well as Unix user names
            // org.junit.ComparisonFailure: expected:<[]username> but was:<[DOMAIN\]username>
            Assert.assertTrue(mock1.getAttribute(FILE_OWNER_ATTRIBUTE).contains(userName));
        }
        if (store.supportsFileAttributeView("posix")) {
            Assert.assertNotNull("Group name should be set", mock1.getAttribute(FILE_GROUP_ATTRIBUTE));
            Assert.assertNotNull("File permissions should be set", mock1.getAttribute(FILE_PERMISSIONS_ATTRIBUTE));
        }
    }

    @Test
    public void testIsListingResetNecessary() throws Exception {
        Assert.assertTrue(processor.isListingResetNecessary(DIRECTORY));
        Assert.assertTrue(processor.isListingResetNecessary(RECURSE));
        Assert.assertTrue(processor.isListingResetNecessary(FILE_FILTER));
        Assert.assertTrue(processor.isListingResetNecessary(PATH_FILTER));
        Assert.assertTrue(processor.isListingResetNecessary(MIN_AGE));
        Assert.assertTrue(processor.isListingResetNecessary(MAX_AGE));
        Assert.assertTrue(processor.isListingResetNecessary(MIN_SIZE));
        Assert.assertTrue(processor.isListingResetNecessary(MAX_SIZE));
        Assert.assertEquals(true, processor.isListingResetNecessary(IGNORE_HIDDEN_FILES));
        Assert.assertEquals(false, processor.isListingResetNecessary(new PropertyDescriptor.Builder().name("x").build()));
    }

    @Test
    public void testFilterRunMidFileWrites() throws Exception {
        final Map<String, Long> fileTimes = new HashMap<>();
        runner.setProperty(DIRECTORY, testDir.getAbsolutePath());
        makeTestFile("/batch1-age3.txt", time3millis, fileTimes);
        makeTestFile("/batch1-age4.txt", time4millis, fileTimes);
        makeTestFile("/batch1-age5.txt", time5millis, fileTimes);
        // check files
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 3);
        Assert.assertEquals(3, runner.getFlowFilesForRelationship(REL_SUCCESS).size());
        // should be picked since it's newer than age3
        makeTestFile("/batch2-age2.txt", time2millis, fileTimes);
        // should be picked even if it has the same age3 timestamp, because it wasn't there at the previous cycle.
        makeTestFile("/batch2-age3.txt", time3millis, fileTimes);
        // should be ignored since it's older than age3
        makeTestFile("/batch2-age4.txt", time4millis, fileTimes);
        runNext();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 2);
        Assert.assertEquals(2, runner.getFlowFilesForRelationship(REL_SUCCESS).size());
    }
}

