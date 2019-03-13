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


import GetHDFSFileInfo.DESTINATION;
import GetHDFSFileInfo.DESTINATION_ATTRIBUTES;
import GetHDFSFileInfo.DESTINATION_CONTENT;
import GetHDFSFileInfo.DIR_FILTER;
import GetHDFSFileInfo.FILE_EXCLUDE_FILTER;
import GetHDFSFileInfo.FILE_FILTER;
import GetHDFSFileInfo.FULL_PATH;
import GetHDFSFileInfo.GROUPING;
import GetHDFSFileInfo.GROUP_ALL;
import GetHDFSFileInfo.GROUP_NONE;
import GetHDFSFileInfo.GROUP_PARENT_DIR;
import GetHDFSFileInfo.IGNORE_DOTTED_DIRS;
import GetHDFSFileInfo.IGNORE_DOTTED_FILES;
import GetHDFSFileInfo.RECURSE_SUBDIRS;
import GetHDFSFileInfo.REL_FAILURE;
import GetHDFSFileInfo.REL_NOT_FOUND;
import GetHDFSFileInfo.REL_ORIGINAL;
import GetHDFSFileInfo.REL_SUCCESS;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestGetHDFSFileInfo {
    private TestRunner runner;

    private TestGetHDFSFileInfo.GetHDFSFileInfoWithMockedFileSystem proc;

    private NiFiProperties mockNiFiProperties;

    private KerberosProperties kerberosProperties;

    @Test
    public void testNoRunOnIncomingConnectionExists() throws InterruptedException {
        setFileSystemBasicTree(proc.fileSystem);
        runner.setIncomingConnection(true);
        runner.setProperty(FULL_PATH, "${literal('/some/home/mydir'):substring(0,15)}");
        runner.setProperty(RECURSE_SUBDIRS, "true");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
    }

    @Test
    public void testRunOnScheduleNoConnections() throws InterruptedException {
        setFileSystemBasicTree(proc.fileSystem);
        runner.setIncomingConnection(false);
        runner.setProperty(FULL_PATH, "/some/home/mydir");
        runner.setProperty(RECURSE_SUBDIRS, "false");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
    }

    @Test
    public void testValidELFunction() throws InterruptedException {
        setFileSystemBasicTree(proc.fileSystem);
        runner.setIncomingConnection(true);
        runner.setProperty(FULL_PATH, "${literal('/some/home/mydir'):substring(0,16)}");
        runner.setProperty(DIR_FILTER, "${literal('_^(dir.*)$_'):substring(1,10)}");
        runner.setProperty(FILE_FILTER, "${literal('_^(.*)$_'):substring(1,7)}");
        runner.setProperty(FILE_EXCLUDE_FILTER, "${literal('_^(none.*)$_'):substring(1,11)}");
        runner.setProperty(RECURSE_SUBDIRS, "false");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.enqueue("foo", new HashMap<String, String>());
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        ProcessContext context = runner.getProcessContext();
        Assert.assertEquals(context.getProperty(FULL_PATH).evaluateAttributeExpressions(mff).getValue(), "/some/home/mydir");
        Assert.assertEquals(context.getProperty(DIR_FILTER).evaluateAttributeExpressions(mff).getValue(), "^(dir.*)$");
        Assert.assertEquals(context.getProperty(FILE_FILTER).evaluateAttributeExpressions(mff).getValue(), "^(.*)$");
        Assert.assertEquals(context.getProperty(FILE_EXCLUDE_FILTER).evaluateAttributeExpressions(mff).getValue(), "^(none.*)$");
    }

    @Test
    public void testRunWithConnections() throws InterruptedException {
        setFileSystemBasicTree(proc.fileSystem);
        Map<String, String> attributes = Maps.newHashMap();
        attributes.put("input.dir", "/some/home/mydir");
        runner.setIncomingConnection(true);
        runner.setProperty(FULL_PATH, "${input.dir}");
        runner.setProperty(RECURSE_SUBDIRS, "false");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.enqueue("foo", attributes);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        ProcessContext context = runner.getProcessContext();
        Assert.assertEquals(context.getProperty(FULL_PATH).evaluateAttributeExpressions(mff).getValue(), "/some/home/mydir");
    }

    @Test
    public void testRunWithIOException() throws InterruptedException {
        setFileSystemBasicTree(proc.fileSystem);
        proc.fileSystem.addFileStatus(proc.fileSystem.newDir("/some/home/mydir"), proc.fileSystem.newFile("/some/home/mydir/exception_java.io.InterruptedIOException"));
        runner.setIncomingConnection(false);
        runner.setProperty(FULL_PATH, "/some/home/mydir/exception_java.io.InterruptedIOException");
        runner.setProperty(RECURSE_SUBDIRS, "false");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        mff.assertAttributeEquals("hdfs.status", "Failed due to: java.io.InterruptedIOException");
    }

    @Test
    public void testRunWithPermissionsExceptionAttributes() throws InterruptedException {
        setFileSystemBasicTree(proc.fileSystem);
        proc.fileSystem.addFileStatus(proc.fileSystem.newDir("/some/home/mydir/dir1"), proc.fileSystem.newDir("/some/home/mydir/dir1/list_exception_java.io.InterruptedIOException"));
        runner.setIncomingConnection(false);
        runner.setProperty(FULL_PATH, "/some/home/mydir");
        runner.setProperty(RECURSE_SUBDIRS, "true");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_ATTRIBUTES);
        runner.setProperty(GROUPING, GROUP_PARENT_DIR);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 6);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
    }

    @Test
    public void testRunWithPermissionsExceptionContent() throws Exception {
        setFileSystemBasicTree(proc.fileSystem);
        proc.fileSystem.addFileStatus(proc.fileSystem.newDir("/some/home/mydir/dir1"), proc.fileSystem.newDir("/some/home/mydir/dir1/list_exception_java.io.InterruptedIOException"));
        runner.setIncomingConnection(false);
        runner.setProperty(FULL_PATH, "/some/home/mydir");
        runner.setProperty(RECURSE_SUBDIRS, "true");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.setProperty(GROUPING, GROUP_ALL);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertContentEquals(Paths.get("src/test/resources/TestGetHDFSFileInfo/testRunWithPermissionsExceptionContent.json"));
    }

    @Test
    public void testObjectNotFound() throws InterruptedException {
        setFileSystemBasicTree(proc.fileSystem);
        runner.setIncomingConnection(false);
        runner.setProperty(FULL_PATH, "/some/home/mydir/ObjectThatDoesNotExist");
        runner.setProperty(RECURSE_SUBDIRS, "true");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 1);
    }

    @Test
    public void testRecursive() throws InterruptedException {
        setFileSystemBasicTree(proc.fileSystem);
        runner.setIncomingConnection(false);
        runner.setProperty(FULL_PATH, "/some/home/mydir");
        runner.setProperty(RECURSE_SUBDIRS, "true");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
    }

    @Test
    public void testRecursiveGroupAllToAttributes() throws Exception {
        setFileSystemBasicTree(proc.fileSystem);
        runner.setIncomingConnection(false);
        runner.setProperty(FULL_PATH, "/some/home/mydir");
        runner.setProperty(RECURSE_SUBDIRS, "true");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_ATTRIBUTES);
        runner.setProperty(GROUPING, GROUP_ALL);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertAttributeEquals("hdfs.objectName", "mydir");
        mff.assertAttributeEquals("hdfs.path", "/some/home");
        mff.assertAttributeEquals("hdfs.type", "directory");
        mff.assertAttributeEquals("hdfs.owner", "owner");
        mff.assertAttributeEquals("hdfs.group", "group");
        mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
        mff.assertAttributeEquals("hdfs.length", ("" + 500));
        mff.assertAttributeEquals("hdfs.count.files", ("" + 5));
        mff.assertAttributeEquals("hdfs.count.dirs", ("" + 10));
        mff.assertAttributeEquals("hdfs.replication", ("" + 3));
        mff.assertAttributeEquals("hdfs.permissions", "rwxr-xr-x");
        mff.assertAttributeNotExists("hdfs.status");
        final String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/TestGetHDFSFileInfo/testRecursiveGroupAllToAttributes.json")));
        mff.assertAttributeEquals("hdfs.full.tree", expected);
    }

    @Test
    public void testRecursiveGroupNoneToAttributes() throws InterruptedException {
        setFileSystemBasicTree(proc.fileSystem);
        runner.setIncomingConnection(false);
        runner.setProperty(FULL_PATH, "/some/home/mydir");
        runner.setProperty(RECURSE_SUBDIRS, "true");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_ATTRIBUTES);
        runner.setProperty(GROUPING, GROUP_NONE);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 7);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        int matchCount = 0;
        for (final MockFlowFile mff : runner.getFlowFilesForRelationship(REL_SUCCESS)) {
            if (mff.getAttribute("hdfs.objectName").equals("mydir")) {
                matchCount++;
                mff.assertAttributeEquals("hdfs.path", "/some/home");
                mff.assertAttributeEquals("hdfs.type", "directory");
                mff.assertAttributeEquals("hdfs.owner", "owner");
                mff.assertAttributeEquals("hdfs.group", "group");
                mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                mff.assertAttributeEquals("hdfs.length", ("" + 500));
                mff.assertAttributeEquals("hdfs.count.files", ("" + 5));
                mff.assertAttributeEquals("hdfs.count.dirs", ("" + 10));
                mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                mff.assertAttributeEquals("hdfs.permissions", "rwxr-xr-x");
                mff.assertAttributeNotExists("hdfs.status");
            } else
                if (mff.getAttribute("hdfs.objectName").equals("dir1")) {
                    matchCount++;
                    mff.assertAttributeEquals("hdfs.path", "/some/home/mydir");
                    mff.assertAttributeEquals("hdfs.type", "directory");
                    mff.assertAttributeEquals("hdfs.owner", "owner");
                    mff.assertAttributeEquals("hdfs.group", "group");
                    mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                    mff.assertAttributeEquals("hdfs.length", ("" + 200));
                    mff.assertAttributeEquals("hdfs.count.files", ("" + 2));
                    mff.assertAttributeEquals("hdfs.count.dirs", ("" + 3));
                    mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                    mff.assertAttributeEquals("hdfs.permissions", "rwxr-xr-x");
                    mff.assertAttributeNotExists("hdfs.status");
                } else
                    if (mff.getAttribute("hdfs.objectName").equals("dir2")) {
                        matchCount++;
                        mff.assertAttributeEquals("hdfs.path", "/some/home/mydir");
                        mff.assertAttributeEquals("hdfs.type", "directory");
                        mff.assertAttributeEquals("hdfs.owner", "owner");
                        mff.assertAttributeEquals("hdfs.group", "group");
                        mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                        mff.assertAttributeEquals("hdfs.length", ("" + 200));
                        mff.assertAttributeEquals("hdfs.count.files", ("" + 2));
                        mff.assertAttributeEquals("hdfs.count.dirs", ("" + 3));
                        mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                        mff.assertAttributeEquals("hdfs.permissions", "rwxr-xr-x");
                        mff.assertAttributeNotExists("hdfs.status");
                    } else
                        if (mff.getAttribute("hdfs.objectName").equals("regDir")) {
                            matchCount++;
                            mff.assertAttributeEquals("hdfs.path", "/some/home/mydir/dir1");
                            mff.assertAttributeEquals("hdfs.type", "directory");
                            mff.assertAttributeEquals("hdfs.owner", "owner");
                            mff.assertAttributeEquals("hdfs.group", "group");
                            mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                            mff.assertAttributeEquals("hdfs.length", ("" + 0));
                            mff.assertAttributeEquals("hdfs.count.files", ("" + 0));
                            mff.assertAttributeEquals("hdfs.count.dirs", ("" + 1));
                            mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                            mff.assertAttributeEquals("hdfs.permissions", "rwxr-xr-x");
                            mff.assertAttributeNotExists("hdfs.status");
                        } else
                            if (mff.getAttribute("hdfs.objectName").equals("regDir2")) {
                                matchCount++;
                                mff.assertAttributeEquals("hdfs.path", "/some/home/mydir/dir2");
                                mff.assertAttributeEquals("hdfs.type", "directory");
                                mff.assertAttributeEquals("hdfs.owner", "owner");
                                mff.assertAttributeEquals("hdfs.group", "group");
                                mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                                mff.assertAttributeEquals("hdfs.length", ("" + 0));
                                mff.assertAttributeEquals("hdfs.count.files", ("" + 0));
                                mff.assertAttributeEquals("hdfs.count.dirs", ("" + 1));
                                mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                                mff.assertAttributeEquals("hdfs.permissions", "rwxr-xr-x");
                                mff.assertAttributeNotExists("hdfs.status");
                            } else
                                if (mff.getAttribute("hdfs.objectName").equals("regFile")) {
                                    matchCount++;
                                    mff.assertAttributeEquals("hdfs.path", "/some/home/mydir/dir1");
                                    mff.assertAttributeEquals("hdfs.type", "file");
                                    mff.assertAttributeEquals("hdfs.owner", "owner");
                                    mff.assertAttributeEquals("hdfs.group", "group");
                                    mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                                    mff.assertAttributeEquals("hdfs.length", ("" + 100));
                                    mff.assertAttributeNotExists("hdfs.count.files");
                                    mff.assertAttributeNotExists("hdfs.count.dirs");
                                    mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                                    mff.assertAttributeEquals("hdfs.permissions", "rw-r--r--");
                                    mff.assertAttributeNotExists("hdfs.status");
                                } else
                                    if (mff.getAttribute("hdfs.objectName").equals("regFile2")) {
                                        matchCount++;
                                        mff.assertAttributeEquals("hdfs.path", "/some/home/mydir/dir2");
                                        mff.assertAttributeEquals("hdfs.type", "file");
                                        mff.assertAttributeEquals("hdfs.owner", "owner");
                                        mff.assertAttributeEquals("hdfs.group", "group");
                                        mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                                        mff.assertAttributeEquals("hdfs.length", ("" + 100));
                                        mff.assertAttributeNotExists("hdfs.count.files");
                                        mff.assertAttributeNotExists("hdfs.count.dirs");
                                        mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                                        mff.assertAttributeEquals("hdfs.permissions", "rw-r--r--");
                                        mff.assertAttributeNotExists("hdfs.status");
                                    } else {
                                        runner.assertNotValid();
                                    }






        }
        Assert.assertEquals(matchCount, 7);
    }

    @Test
    public void testRecursiveGroupDirToAttributes() throws Exception {
        setFileSystemBasicTree(proc.fileSystem);
        runner.setIncomingConnection(false);
        runner.setProperty(FULL_PATH, "/some/home/mydir");
        runner.setProperty(RECURSE_SUBDIRS, "true");
        runner.setProperty(IGNORE_DOTTED_DIRS, "true");
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.setProperty(DESTINATION, DESTINATION_ATTRIBUTES);
        runner.setProperty(GROUPING, GROUP_PARENT_DIR);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 5);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        int matchCount = 0;
        for (final MockFlowFile mff : runner.getFlowFilesForRelationship(REL_SUCCESS)) {
            if (mff.getAttribute("hdfs.objectName").equals("mydir")) {
                matchCount++;
                mff.assertAttributeEquals("hdfs.path", "/some/home");
                mff.assertAttributeEquals("hdfs.type", "directory");
                mff.assertAttributeEquals("hdfs.owner", "owner");
                mff.assertAttributeEquals("hdfs.group", "group");
                mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                mff.assertAttributeEquals("hdfs.length", ("" + 500));
                mff.assertAttributeEquals("hdfs.count.files", ("" + 5));
                mff.assertAttributeEquals("hdfs.count.dirs", ("" + 10));
                mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                mff.assertAttributeEquals("hdfs.permissions", "rwxr-xr-x");
                mff.assertAttributeNotExists("hdfs.status");
                final String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/TestGetHDFSFileInfo/testRecursiveGroupDirToAttributes-mydir.json")));
                mff.assertAttributeEquals("hdfs.full.tree", expected);
            } else
                if (mff.getAttribute("hdfs.objectName").equals("dir1")) {
                    matchCount++;
                    mff.assertAttributeEquals("hdfs.path", "/some/home/mydir");
                    mff.assertAttributeEquals("hdfs.type", "directory");
                    mff.assertAttributeEquals("hdfs.owner", "owner");
                    mff.assertAttributeEquals("hdfs.group", "group");
                    mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                    mff.assertAttributeEquals("hdfs.length", ("" + 200));
                    mff.assertAttributeEquals("hdfs.count.files", ("" + 2));
                    mff.assertAttributeEquals("hdfs.count.dirs", ("" + 3));
                    mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                    mff.assertAttributeEquals("hdfs.permissions", "rwxr-xr-x");
                    mff.assertAttributeNotExists("hdfs.status");
                    final String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/TestGetHDFSFileInfo/testRecursiveGroupDirToAttributes-dir1.json")));
                    mff.assertAttributeEquals("hdfs.full.tree", expected);
                } else
                    if (mff.getAttribute("hdfs.objectName").equals("dir2")) {
                        matchCount++;
                        mff.assertAttributeEquals("hdfs.path", "/some/home/mydir");
                        mff.assertAttributeEquals("hdfs.type", "directory");
                        mff.assertAttributeEquals("hdfs.owner", "owner");
                        mff.assertAttributeEquals("hdfs.group", "group");
                        mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                        mff.assertAttributeEquals("hdfs.length", ("" + 200));
                        mff.assertAttributeEquals("hdfs.count.files", ("" + 2));
                        mff.assertAttributeEquals("hdfs.count.dirs", ("" + 3));
                        mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                        mff.assertAttributeEquals("hdfs.permissions", "rwxr-xr-x");
                        mff.assertAttributeNotExists("hdfs.status");
                        final String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/TestGetHDFSFileInfo/testRecursiveGroupDirToAttributes-dir2.json")));
                        mff.assertAttributeEquals("hdfs.full.tree", expected);
                    } else
                        if (mff.getAttribute("hdfs.objectName").equals("regDir")) {
                            matchCount++;
                            mff.assertAttributeEquals("hdfs.path", "/some/home/mydir/dir1");
                            mff.assertAttributeEquals("hdfs.type", "directory");
                            mff.assertAttributeEquals("hdfs.owner", "owner");
                            mff.assertAttributeEquals("hdfs.group", "group");
                            mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                            mff.assertAttributeEquals("hdfs.length", ("" + 0));
                            mff.assertAttributeEquals("hdfs.count.files", ("" + 0));
                            mff.assertAttributeEquals("hdfs.count.dirs", ("" + 1));
                            mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                            mff.assertAttributeEquals("hdfs.permissions", "rwxr-xr-x");
                            mff.assertAttributeNotExists("hdfs.status");
                            final String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/TestGetHDFSFileInfo/testRecursiveGroupDirToAttributes-regDir.json")));
                            mff.assertAttributeEquals("hdfs.full.tree", expected);
                        } else
                            if (mff.getAttribute("hdfs.objectName").equals("regDir2")) {
                                matchCount++;
                                mff.assertAttributeEquals("hdfs.path", "/some/home/mydir/dir2");
                                mff.assertAttributeEquals("hdfs.type", "directory");
                                mff.assertAttributeEquals("hdfs.owner", "owner");
                                mff.assertAttributeEquals("hdfs.group", "group");
                                mff.assertAttributeEquals("hdfs.lastModified", ("" + 1523456000000L));
                                mff.assertAttributeEquals("hdfs.length", ("" + 0));
                                mff.assertAttributeEquals("hdfs.count.files", ("" + 0));
                                mff.assertAttributeEquals("hdfs.count.dirs", ("" + 1));
                                mff.assertAttributeEquals("hdfs.replication", ("" + 3));
                                mff.assertAttributeEquals("hdfs.permissions", "rwxr-xr-x");
                                mff.assertAttributeNotExists("hdfs.status");
                                final String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/TestGetHDFSFileInfo/testRecursiveGroupDirToAttributes-regDir2.json")));
                                mff.assertAttributeEquals("hdfs.full.tree", expected);
                            } else {
                                runner.assertNotValid();
                            }




        }
        Assert.assertEquals(matchCount, 5);
    }

    private class GetHDFSFileInfoWithMockedFileSystem extends GetHDFSFileInfo {
        private final TestGetHDFSFileInfo.MockFileSystem fileSystem = new TestGetHDFSFileInfo.MockFileSystem();

        private final KerberosProperties testKerberosProps;

        public GetHDFSFileInfoWithMockedFileSystem(KerberosProperties kerberosProperties) {
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
        protected FileSystem getFileSystem(final Configuration config) throws IOException {
            return fileSystem;
        }
    }

    private class MockFileSystem extends FileSystem {
        private final Map<Path, Set<FileStatus>> fileStatuses = new HashMap<>();

        private final Map<Path, FileStatus> pathToStatus = new HashMap<>();

        public void addFileStatus(final FileStatus parent, final FileStatus child) {
            Set<FileStatus> children = fileStatuses.get(parent.getPath());
            if (children == null) {
                children = new HashSet();
                fileStatuses.put(parent.getPath(), children);
            }
            if (child != null) {
                children.add(child);
                if ((child.isDirectory()) && (!(fileStatuses.containsKey(child.getPath())))) {
                    fileStatuses.put(child.getPath(), new HashSet<FileStatus>());
                }
            }
            pathToStatus.put(parent.getPath(), parent);
            pathToStatus.put(child.getPath(), child);
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
            if (!(fileStatuses.containsKey(f))) {
                throw new FileNotFoundException();
            }
            if (f.getName().startsWith("list_exception_")) {
                String clzName = f.getName().substring("list_exception_".length(), f.getName().length());
                IOException exception = null;
                try {
                    exception = ((IOException) (Class.forName(clzName).newInstance()));
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
                throw exception;
            }
            final Set<FileStatus> statuses = fileStatuses.get(f);
            if (statuses == null) {
                return new FileStatus[0];
            }
            for (FileStatus s : statuses) {
                getFileStatus(s.getPath());// support exception handling only.

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
            if ((f != null) && (f.getName().startsWith("exception_"))) {
                String clzName = f.getName().substring("exception_".length(), f.getName().length());
                IOException exception = null;
                try {
                    exception = ((IOException) (Class.forName(clzName).newInstance()));
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
                throw exception;
            }
            final FileStatus fileStatus = pathToStatus.get(f);
            if (fileStatus == null)
                throw new FileNotFoundException();

            return fileStatus;
        }

        public FileStatus newFile(String p) {
            return new FileStatus(100L, false, 3, ((128 * 1024) * 1024), 1523456000000L, 1523457000000L, TestGetHDFSFileInfo.perms(((short) (420))), "owner", "group", new Path(p));
        }

        public FileStatus newDir(String p) {
            return new FileStatus(1L, true, 3, ((128 * 1024) * 1024), 1523456000000L, 1523457000000L, TestGetHDFSFileInfo.perms(((short) (493))), "owner", "group", new Path(p));
        }
    }
}

