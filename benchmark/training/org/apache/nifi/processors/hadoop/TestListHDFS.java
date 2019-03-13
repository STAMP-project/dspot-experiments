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
package org.apache.nifi.processors.hadoop;


import ListHDFS.DIRECTORY;
import ListHDFS.EMITTED_TIMESTAMP_KEY;
import ListHDFS.FILE_FILTER;
import ListHDFS.FILE_FILTER_MODE;
import ListHDFS.LISTING_TIMESTAMP_KEY;
import ListHDFS.MAX_AGE;
import ListHDFS.MIN_AGE;
import ListHDFS.RECURSE_SUBDIRS;
import ListHDFS.REL_SUCCESS;
import Scope.CLUSTER;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.distributed.cache.client.Deserializer;
import org.apache.nifi.distributed.cache.client.DistributedMapCacheClient;
import org.apache.nifi.distributed.cache.client.Serializer;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;

import static ListHDFS.LISTING_LAG_NANOS;


public class TestListHDFS {
    private TestRunner runner;

    private TestListHDFS.ListHDFSWithMockedFileSystem proc;

    private NiFiProperties mockNiFiProperties;

    private KerberosProperties kerberosProperties;

    @Test
    public void testListingWithValidELFunction() throws InterruptedException {
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.txt")));
        runner.setProperty(DIRECTORY, "${literal('/test'):substring(0,5)}");
        // first iteration will not pick up files because it has to instead check timestamps.
        // We must then wait long enough to ensure that the listing can be performed safely and
        // run the Processor again.
        runner.run();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertAttributeEquals("path", "/test");
        mff.assertAttributeEquals("filename", "testFile.txt");
    }

    @Test
    public void testListingWithFilter() throws InterruptedException {
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.txt")));
        runner.setProperty(DIRECTORY, "${literal('/test'):substring(0,5)}");
        runner.setProperty(FILE_FILTER, "[^test].*");
        // first iteration will not pick up files because it has to instead check timestamps.
        // We must then wait long enough to ensure that the listing can be performed safely and
        // run the Processor again.
        runner.run();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
    }

    @Test
    public void testListingWithInvalidELFunction() throws InterruptedException {
        runner.setProperty(DIRECTORY, "${literal('/test'):foo()}");
        runner.assertNotValid();
    }

    @Test
    public void testListingWithUnrecognizedELFunction() throws InterruptedException {
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.txt")));
        runner.setProperty(DIRECTORY, "data_${literal('testing'):substring(0,4)%7D");
        // first iteration will not pick up files because it has to instead check timestamps.
        // We must then wait long enough to ensure that the listing can be performed safely and
        // run the Processor again.
        runner.run();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
    }

    @Test
    public void testListingHasCorrectAttributes() throws InterruptedException {
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.txt")));
        // first iteration will not pick up files because it has to instead check timestamps.
        // We must then wait long enough to ensure that the listing can be performed safely and
        // run the Processor again.
        runner.run();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertAttributeEquals("path", "/test");
        mff.assertAttributeEquals("filename", "testFile.txt");
    }

    @Test
    public void testRecursiveWithDefaultFilterAndFilterMode() throws InterruptedException {
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/.testFile.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, true, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/1.txt")));
        // first iteration will not pick up files because it has to instead check timestamps.
        // We must then wait long enough to ensure that the listing can be performed safely and
        // run the Processor again.
        runner.run();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        for (int i = 0; i < 2; i++) {
            final MockFlowFile ff = flowFiles.get(i);
            final String filename = ff.getAttribute("filename");
            if (filename.equals("testFile.txt")) {
                ff.assertAttributeEquals("path", "/test");
            } else
                if (filename.equals("1.txt")) {
                    ff.assertAttributeEquals("path", "/test/testDir");
                } else {
                    Assert.fail(("filename was " + filename));
                }

        }
    }

    @Test
    public void testRecursiveWithCustomFilterDirectoriesAndFiles() throws IOException, InterruptedException {
        // set custom regex filter and filter mode
        runner.setProperty(FILE_FILTER, ".*txt.*");
        runner.setProperty(FILE_FILTER_MODE, ListHDFS.FILTER_DIRECTORIES_AND_FILES_VALUE.getValue());
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.out")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, true, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/1.txt")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, true, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir/anotherDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir/2.out")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir/anotherDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir/2.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, true, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/txtDir")));
        proc.fileSystem.addFileStatus(new Path("/test/txtDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/txtDir/3.out")));
        proc.fileSystem.addFileStatus(new Path("/test/txtDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/txtDir/3.txt")));
        // first iteration will not pick up files because it has to instead check timestamps.
        // We must then wait long enough to ensure that the listing can be performed safely and
        // run the Processor again.
        runner.run();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        for (int i = 0; i < 2; i++) {
            final MockFlowFile ff = flowFiles.get(i);
            final String filename = ff.getAttribute("filename");
            if (filename.equals("testFile.txt")) {
                ff.assertAttributeEquals("path", "/test");
            } else
                if (filename.equals("3.txt")) {
                    ff.assertAttributeEquals("path", "/test/txtDir");
                } else {
                    Assert.fail(("filename was " + filename));
                }

        }
    }

    @Test
    public void testRecursiveWithCustomFilterFilesOnly() throws IOException, InterruptedException {
        // set custom regex filter and filter mode
        runner.setProperty(FILE_FILTER, "[^\\.].*\\.txt");
        runner.setProperty(FILE_FILTER_MODE, ListHDFS.FILTER_FILES_ONLY_VALUE.getValue());
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.out")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/.partfile.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, true, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/1.txt")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, true, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir/anotherDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir/.txt")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir/anotherDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir/2.out")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir/anotherDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir/2.txt")));
        // first iteration will not pick up files because it has to instead check timestamps.
        // We must then wait long enough to ensure that the listing can be performed safely and
        // run the Processor again.
        runner.run();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        for (int i = 0; i < 2; i++) {
            final MockFlowFile ff = flowFiles.get(i);
            final String filename = ff.getAttribute("filename");
            if (filename.equals("testFile.txt")) {
                ff.assertAttributeEquals("path", "/test");
            } else
                if (filename.equals("1.txt")) {
                    ff.assertAttributeEquals("path", "/test/testDir");
                } else
                    if (filename.equals("2.txt")) {
                        ff.assertAttributeEquals("path", "/test/testDir/anotherDir");
                    } else {
                        Assert.fail(("filename was " + filename));
                    }


        }
    }

    @Test
    public void testRecursiveWithCustomFilterFullPath() throws IOException, InterruptedException {
        // set custom regex filter and filter mode
        runner.setProperty(FILE_FILTER, "(/.*/)*anotherDir/1\\..*");
        runner.setProperty(FILE_FILTER_MODE, ListHDFS.FILTER_FULL_PATH_VALUE.getValue());
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.out")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, true, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/1.txt")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, true, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir/anotherDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir/1.out")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir/anotherDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir/1.txt")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir/anotherDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir/2.out")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir/anotherDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/anotherDir/2.txt")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, true, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/someDir")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir/someDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/someDir/1.out")));
        // first iteration will not pick up files because it has to instead check timestamps.
        // We must then wait long enough to ensure that the listing can be performed safely and
        // run the Processor again.
        runner.run();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        for (int i = 0; i < 2; i++) {
            final MockFlowFile ff = flowFiles.get(i);
            final String filename = ff.getAttribute("filename");
            if (filename.equals("1.out")) {
                ff.assertAttributeEquals("path", "/test/testDir/anotherDir");
            } else
                if (filename.equals("1.txt")) {
                    ff.assertAttributeEquals("path", "/test/testDir/anotherDir");
                } else {
                    Assert.fail(("filename was " + filename));
                }

        }
    }

    @Test
    public void testNotRecursive() throws InterruptedException {
        runner.setProperty(RECURSE_SUBDIRS, "false");
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, true, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testDir/1.txt")));
        // first iteration will not pick up files because it has to instead check timestamps.
        // We must then wait long enough to ensure that the listing can be performed safely and
        // run the Processor again.
        runner.run();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile mff1 = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff1.assertAttributeEquals("path", "/test");
        mff1.assertAttributeEquals("filename", "testFile.txt");
    }

    @Test
    public void testNoListUntilUpdateFromRemoteOnPrimaryNodeChange() throws IOException, InterruptedException {
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 1999L, 0L, create777(), "owner", "group", new Path("/test/testFile.txt")));
        // first iteration will not pick up files because it has to instead check timestamps.
        // We must then wait long enough to ensure that the listing can be performed safely and
        // run the Processor again.
        runner.run();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile mff1 = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff1.assertAttributeEquals("path", "/test");
        mff1.assertAttributeEquals("filename", "testFile.txt");
        runner.clearTransferState();
        // add new file to pull
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 2000L, 0L, create777(), "owner", "group", new Path("/test/testFile2.txt")));
        runner.getStateManager().setFailOnStateGet(CLUSTER, true);
        // Should fail to perform @OnScheduled methods.
        try {
            runner.run();
            Assert.fail("Processor ran successfully");
        } catch (final AssertionError e) {
        }
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        // Should fail to perform @OnScheduled methods.
        try {
            runner.run();
            Assert.fail("Processor ran successfully");
        } catch (final AssertionError e) {
        }
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.getStateManager().setFailOnStateGet(CLUSTER, false);
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        Map<String, String> newState = runner.getStateManager().getState(CLUSTER).toMap();
        Assert.assertEquals("2000", newState.get(LISTING_TIMESTAMP_KEY));
        Assert.assertEquals("1999", newState.get(EMITTED_TIMESTAMP_KEY));
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        newState = runner.getStateManager().getState(CLUSTER).toMap();
        Assert.assertEquals("2000", newState.get(LISTING_TIMESTAMP_KEY));
        Assert.assertEquals("2000", newState.get(EMITTED_TIMESTAMP_KEY));
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testOnlyNewestEntriesHeldBack() throws InterruptedException {
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 0L, 0L, create777(), "owner", "group", new Path("/test/testFile.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, 8L, 0L, create777(), "owner", "group", new Path("/test/testFile2.txt")));
        // this is a directory, so it won't be counted toward the entries
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, true, 1, 1L, 8L, 0L, create777(), "owner", "group", new Path("/test/testDir")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, false, 1, 1L, 100L, 0L, create777(), "owner", "group", new Path("/test/testDir/1.txt")));
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, false, 1, 1L, 100L, 0L, create777(), "owner", "group", new Path("/test/testDir/2.txt")));
        // The first iteration should pick up 2 files with the smaller timestamps.
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        // Next iteration should pick up the other 2 files, since nothing else was added.
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 4);
        proc.fileSystem.addFileStatus(new Path("/test/testDir"), new FileStatus(1L, false, 1, 1L, 110L, 0L, create777(), "owner", "group", new Path("/test/testDir/3.txt")));
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 4);
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 5);
    }

    @Test
    public void testMinAgeMaxAge() throws IOException, InterruptedException {
        long now = new Date().getTime();
        long oneHourAgo = now - 3600000;
        long twoHoursAgo = now - (2 * 3600000);
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, now, now, create777(), "owner", "group", new Path("/test/willBeIgnored.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, (now - 5), (now - 5), create777(), "owner", "group", new Path("/test/testFile.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, oneHourAgo, oneHourAgo, create777(), "owner", "group", new Path("/test/testFile1.txt")));
        proc.fileSystem.addFileStatus(new Path("/test"), new FileStatus(1L, false, 1, 1L, twoHoursAgo, twoHoursAgo, create777(), "owner", "group", new Path("/test/testFile2.txt")));
        // all files
        runner.run();
        runner.assertValid();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        runner.clearTransferState();
        runner.getStateManager().clear(CLUSTER);
        // invalid min_age > max_age
        runner.setProperty(MIN_AGE, "30 sec");
        runner.setProperty(MAX_AGE, "1 sec");
        runner.assertNotValid();
        // only one file (one hour ago)
        runner.setProperty(MIN_AGE, "30 sec");
        runner.setProperty(MAX_AGE, "90 min");
        runner.assertValid();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();// will ignore the file for this cycle

        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        // Next iteration should pick up the file, since nothing else was added.
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeEquals("filename", "testFile1.txt");
        runner.clearTransferState();
        runner.getStateManager().clear(CLUSTER);
        // two files (one hour ago and two hours ago)
        runner.setProperty(MIN_AGE, "30 sec");
        runner.removeProperty(MAX_AGE);
        runner.assertValid();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        runner.clearTransferState();
        runner.getStateManager().clear(CLUSTER);
        // two files (now and one hour ago)
        runner.setProperty(MIN_AGE, "0 sec");
        runner.setProperty(MAX_AGE, "90 min");
        runner.assertValid();
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
    }

    @Test
    public void testListAfterDirectoryChange() throws InterruptedException {
        proc.fileSystem.addFileStatus(new Path("/test1"), new FileStatus(1L, false, 1, 1L, 100L, 0L, create777(), "owner", "group", new Path("/test1/testFile-1_1.txt")));
        proc.fileSystem.addFileStatus(new Path("/test2"), new FileStatus(1L, false, 1, 1L, 150L, 0L, create777(), "owner", "group", new Path("/test2/testFile-2_1.txt")));
        proc.fileSystem.addFileStatus(new Path("/test1"), new FileStatus(1L, false, 1, 1L, 200L, 0L, create777(), "owner", "group", new Path("/test1/testFile-1_2.txt")));
        runner.setProperty(DIRECTORY, "/test1");
        runner.run();// Initial run, latest file from /test1 will be ignored

        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();// Latest file i.e. testFile-1_2.txt from /test1 should also be picked up now

        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        runner.setProperty(DIRECTORY, "/test2");// Changing directory should reset the state

        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();// Will ignore the files for this cycle

        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        Thread.sleep(TimeUnit.NANOSECONDS.toMillis((2 * (LISTING_LAG_NANOS))));
        runner.run();// Since state has been reset, testFile-2_1.txt from /test2 should be picked up

        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
    }

    private class ListHDFSWithMockedFileSystem extends ListHDFS {
        private final TestListHDFS.MockFileSystem fileSystem = new TestListHDFS.MockFileSystem();

        private final KerberosProperties testKerberosProps;

        public ListHDFSWithMockedFileSystem(KerberosProperties kerberosProperties) {
            this.testKerberosProps = kerberosProperties;
        }

        @Override
        protected KerberosProperties getKerberosProperties(File kerberosConfigFile) {
            return testKerberosProps;
        }

        @Override
        protected FileSystem getFileSystem() {
            return fileSystem;
        }

        @Override
        protected File getPersistenceFile() {
            return new File("target/conf/state-file");
        }

        @Override
        protected FileSystem getFileSystem(final Configuration config) throws IOException {
            return fileSystem;
        }
    }

    private class MockFileSystem extends FileSystem {
        private final Map<Path, Set<FileStatus>> fileStatuses = new HashMap<>();

        public void addFileStatus(final Path parent, final FileStatus child) {
            Set<FileStatus> children = fileStatuses.get(parent);
            if (children == null) {
                children = new HashSet();
                fileStatuses.put(parent, children);
            }
            children.add(child);
        }

        @Override
        @SuppressWarnings("deprecation")
        public long getDefaultBlockSize() {
            return 1024L;
        }

        @Override
        @SuppressWarnings("deprecation")
        public short getDefaultReplication() {
            return 1;
        }

        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public FSDataInputStream open(final Path f, final int bufferSize) throws IOException {
            return null;
        }

        @Override
        public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
            return null;
        }

        @Override
        public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
            return null;
        }

        @Override
        public boolean rename(final Path src, final Path dst) throws IOException {
            return false;
        }

        @Override
        public boolean delete(final Path f, final boolean recursive) throws IOException {
            return false;
        }

        @Override
        public FileStatus[] listStatus(final Path f) throws FileNotFoundException, IOException {
            final Set<FileStatus> statuses = fileStatuses.get(f);
            if (statuses == null) {
                return new FileStatus[0];
            }
            return statuses.toArray(new FileStatus[statuses.size()]);
        }

        @Override
        public void setWorkingDirectory(final Path new_dir) {
        }

        @Override
        public Path getWorkingDirectory() {
            return new Path(new File(".").getAbsolutePath());
        }

        @Override
        public boolean mkdirs(final Path f, final FsPermission permission) throws IOException {
            return false;
        }

        @Override
        public FileStatus getFileStatus(final Path f) throws IOException {
            return null;
        }
    }

    private class MockCacheClient extends AbstractControllerService implements DistributedMapCacheClient {
        private final ConcurrentMap<Object, Object> values = new ConcurrentHashMap<>();

        private boolean failOnCalls = false;

        private void verifyNotFail() throws IOException {
            if (failOnCalls) {
                throw new IOException("Could not call to remote service because Unit Test marked service unavailable");
            }
        }

        @Override
        public <K, V> boolean putIfAbsent(final K key, final V value, final Serializer<K> keySerializer, final Serializer<V> valueSerializer) throws IOException {
            verifyNotFail();
            final Object retValue = values.putIfAbsent(key, value);
            return retValue == null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <K, V> V getAndPutIfAbsent(final K key, final V value, final Serializer<K> keySerializer, final Serializer<V> valueSerializer, final Deserializer<V> valueDeserializer) throws IOException {
            verifyNotFail();
            return ((V) (values.putIfAbsent(key, value)));
        }

        @Override
        public <K> boolean containsKey(final K key, final Serializer<K> keySerializer) throws IOException {
            verifyNotFail();
            return values.containsKey(key);
        }

        @Override
        public <K, V> void put(final K key, final V value, final Serializer<K> keySerializer, final Serializer<V> valueSerializer) throws IOException {
            verifyNotFail();
            values.put(key, value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <K, V> V get(final K key, final Serializer<K> keySerializer, final Deserializer<V> valueDeserializer) throws IOException {
            verifyNotFail();
            return ((V) (values.get(key)));
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public <K> boolean remove(final K key, final Serializer<K> serializer) throws IOException {
            verifyNotFail();
            values.remove(key);
            return true;
        }

        @Override
        public long removeByPattern(String regex) throws IOException {
            verifyNotFail();
            final List<Object> removedRecords = new ArrayList<>();
            Pattern p = Pattern.compile(regex);
            for (Object key : values.keySet()) {
                // Key must be backed by something that array() returns a byte[] that can be converted into a String via the default charset
                Matcher m = p.matcher(key.toString());
                if (m.matches()) {
                    removedRecords.add(values.get(key));
                }
            }
            final long numRemoved = removedRecords.size();
            removedRecords.forEach(values::remove);
            return numRemoved;
        }
    }
}

