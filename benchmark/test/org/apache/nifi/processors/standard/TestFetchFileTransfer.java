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


import CoreAttributes.PATH;
import FetchFileTransfer.COMPLETION_DELETE;
import FetchFileTransfer.COMPLETION_MOVE;
import FetchFileTransfer.COMPLETION_STRATEGY;
import FetchFileTransfer.HOSTNAME;
import FetchFileTransfer.MOVE_CREATE_DIRECTORY;
import FetchFileTransfer.MOVE_DESTINATION_DIR;
import FetchFileTransfer.REL_NOT_FOUND;
import FetchFileTransfer.REL_PERMISSION_DENIED;
import FetchFileTransfer.REL_SUCCESS;
import FetchFileTransfer.REMOTE_FILENAME;
import FetchFileTransfer.UNDEFAULTED_PORT;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processors.standard.util.FileInfo;
import org.apache.nifi.processors.standard.util.FileTransfer;
import org.apache.nifi.processors.standard.util.PermissionDeniedException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestFetchFileTransfer {
    @Test
    public void testContentFetched() {
        final TestFetchFileTransfer.TestableFetchFileTransfer proc = new TestFetchFileTransfer.TestableFetchFileTransfer();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(UNDEFAULTED_PORT, "11");
        runner.setProperty(REMOTE_FILENAME, "${filename}");
        proc.addContent("hello.txt", "world".getBytes());
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "hello.txt");
        runner.enqueue(new byte[0], attrs);
        runner.run(1, false, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertFalse(proc.closed);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals("world");
    }

    @Test
    public void testFilenameContainsPath() {
        final String filenameWithPath = "./here/is/my/path/hello.txt";
        final TestFetchFileTransfer.TestableFetchFileTransfer proc = new TestFetchFileTransfer.TestableFetchFileTransfer();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(UNDEFAULTED_PORT, "11");
        runner.setProperty(REMOTE_FILENAME, "${filename}");
        proc.addContent(filenameWithPath, "world".getBytes());
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", filenameWithPath);
        runner.enqueue(new byte[0], attrs);
        runner.run(1, false, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertFalse(proc.closed);
        MockFlowFile transferredFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transferredFlowFile.assertContentEquals("world");
        transferredFlowFile.assertAttributeExists(PATH.key());
        transferredFlowFile.assertAttributeEquals(PATH.key(), "./here/is/my/path");
    }

    @Test
    public void testContentNotFound() {
        final TestFetchFileTransfer.TestableFetchFileTransfer proc = new TestFetchFileTransfer.TestableFetchFileTransfer();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(UNDEFAULTED_PORT, "11");
        runner.setProperty(REMOTE_FILENAME, "${filename}");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "hello.txt");
        runner.enqueue(new byte[0], attrs);
        runner.run(1, false, false);
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
    }

    @Test
    public void testInsufficientPermissions() {
        final TestFetchFileTransfer.TestableFetchFileTransfer proc = new TestFetchFileTransfer.TestableFetchFileTransfer();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(UNDEFAULTED_PORT, "11");
        runner.setProperty(REMOTE_FILENAME, "${filename}");
        proc.addContent("hello.txt", "world".getBytes());
        proc.allowAccess = false;
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "hello.txt");
        runner.enqueue(new byte[0], attrs);
        runner.run(1, false, false);
        runner.assertAllFlowFilesTransferred(REL_PERMISSION_DENIED, 1);
    }

    @Test
    public void testMoveFileWithNoTrailingSlashDirName() {
        final TestFetchFileTransfer.TestableFetchFileTransfer proc = new TestFetchFileTransfer.TestableFetchFileTransfer();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(UNDEFAULTED_PORT, "11");
        runner.setProperty(REMOTE_FILENAME, "${filename}");
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.setProperty(MOVE_DESTINATION_DIR, "/moved");
        runner.setProperty(MOVE_CREATE_DIRECTORY, "true");
        proc.addContent("hello.txt", "world".getBytes());
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "hello.txt");
        runner.enqueue(new byte[0], attrs);
        runner.run(1, false, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        proc.fileContents.containsKey("/moved/hello.txt");
        Assert.assertEquals(1, proc.fileContents.size());
    }

    @Test
    public void testMoveFileWithTrailingSlashDirName() {
        final TestFetchFileTransfer.TestableFetchFileTransfer proc = new TestFetchFileTransfer.TestableFetchFileTransfer();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(UNDEFAULTED_PORT, "11");
        runner.setProperty(REMOTE_FILENAME, "${filename}");
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.setProperty(MOVE_DESTINATION_DIR, "/moved/");
        proc.addContent("hello.txt", "world".getBytes());
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "hello.txt");
        runner.enqueue(new byte[0], attrs);
        runner.run(1, false, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        proc.fileContents.containsKey("/moved/hello.txt");
        Assert.assertEquals(1, proc.fileContents.size());
    }

    @Test
    public void testDeleteFile() {
        final TestFetchFileTransfer.TestableFetchFileTransfer proc = new TestFetchFileTransfer.TestableFetchFileTransfer();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(UNDEFAULTED_PORT, "11");
        runner.setProperty(REMOTE_FILENAME, "${filename}");
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_DELETE.getValue());
        proc.addContent("hello.txt", "world".getBytes());
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "hello.txt");
        runner.enqueue(new byte[0], attrs);
        runner.run(1, false, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertTrue(proc.fileContents.isEmpty());
    }

    @Test
    public void testDeleteFails() {
        final TestFetchFileTransfer.TestableFetchFileTransfer proc = new TestFetchFileTransfer.TestableFetchFileTransfer();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(UNDEFAULTED_PORT, "11");
        runner.setProperty(REMOTE_FILENAME, "${filename}");
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_DELETE.getValue());
        proc.addContent("hello.txt", "world".getBytes());
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "hello.txt");
        runner.enqueue(new byte[0], attrs);
        proc.allowDelete = false;
        runner.run(1, false, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertFalse(proc.fileContents.isEmpty());
    }

    @Test
    public void testRenameFails() {
        final TestFetchFileTransfer.TestableFetchFileTransfer proc = new TestFetchFileTransfer.TestableFetchFileTransfer();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(UNDEFAULTED_PORT, "11");
        runner.setProperty(REMOTE_FILENAME, "${filename}");
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.setProperty(MOVE_DESTINATION_DIR, "/moved/");
        proc.addContent("hello.txt", "world".getBytes());
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "hello.txt");
        runner.enqueue(new byte[0], attrs);
        proc.allowDelete = false;
        proc.allowRename = false;
        runner.run(1, false, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals(1, proc.fileContents.size());
        Assert.assertTrue(proc.fileContents.containsKey("hello.txt"));
    }

    @Test
    public void testCreateDirFails() {
        final TestFetchFileTransfer.TestableFetchFileTransfer proc = new TestFetchFileTransfer.TestableFetchFileTransfer();
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(HOSTNAME, "localhost");
        runner.setProperty(UNDEFAULTED_PORT, "11");
        runner.setProperty(REMOTE_FILENAME, "${filename}");
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.setProperty(MOVE_DESTINATION_DIR, "/moved/");
        runner.setProperty(MOVE_CREATE_DIRECTORY, "true");
        proc.addContent("hello.txt", "world".getBytes());
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "hello.txt");
        runner.enqueue(new byte[0], attrs);
        proc.allowCreateDir = false;
        runner.run(1, false, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals(1, proc.fileContents.size());
        Assert.assertTrue(proc.fileContents.containsKey("hello.txt"));
    }

    private static class TestableFetchFileTransfer extends FetchFileTransfer {
        private boolean allowAccess = true;

        private boolean allowDelete = true;

        private boolean allowCreateDir = true;

        private boolean allowRename = true;

        private boolean closed = false;

        private final Map<String, byte[]> fileContents = new HashMap<>();

        public void addContent(final String filename, final byte[] content) {
            this.fileContents.put(filename, content);
        }

        @Override
        protected FileTransfer createFileTransfer(final ProcessContext context) {
            return new FileTransfer() {
                @Override
                public void close() throws IOException {
                    closed = true;
                }

                @Override
                public String getHomeDirectory(FlowFile flowFile) throws IOException {
                    return null;
                }

                @Override
                public List<FileInfo> getListing() throws IOException {
                    return null;
                }

                @Override
                public InputStream getInputStream(final String remoteFileName) throws IOException {
                    return getInputStream(remoteFileName, null);
                }

                @Override
                public InputStream getInputStream(String remoteFileName, FlowFile flowFile) throws IOException {
                    if (!(allowAccess)) {
                        throw new PermissionDeniedException("test permission denied");
                    }
                    final byte[] content = fileContents.get(remoteFileName);
                    if (content == null) {
                        throw new FileNotFoundException();
                    }
                    return new ByteArrayInputStream(content);
                }

                @Override
                public void flush() throws IOException {
                }

                @Override
                public boolean flush(FlowFile flowFile) throws IOException {
                    return true;
                }

                @Override
                public FileInfo getRemoteFileInfo(FlowFile flowFile, String path, String remoteFileName) throws IOException {
                    return null;
                }

                @Override
                public String put(FlowFile flowFile, String path, String filename, InputStream content) throws IOException {
                    return null;
                }

                @Override
                public void deleteFile(FlowFile flowFile, String path, String remoteFileName) throws IOException {
                    if (!(allowDelete)) {
                        throw new PermissionDeniedException("test permission denied");
                    }
                    if (!(fileContents.containsKey(remoteFileName))) {
                        throw new FileNotFoundException();
                    }
                    fileContents.remove(remoteFileName);
                }

                @Override
                public void rename(FlowFile flowFile, String source, String target) throws IOException {
                    if (!(allowRename)) {
                        throw new PermissionDeniedException("test permission denied");
                    }
                    if (!(fileContents.containsKey(source))) {
                        throw new FileNotFoundException();
                    }
                    final byte[] content = fileContents.remove(source);
                    fileContents.put(target, content);
                }

                @Override
                public void deleteDirectory(FlowFile flowFile, String remoteDirectoryName) throws IOException {
                }

                @Override
                public boolean isClosed() {
                    return false;
                }

                @Override
                public String getProtocolName() {
                    return "test";
                }

                @Override
                public void ensureDirectoryExists(FlowFile flowFile, File remoteDirectory) throws IOException {
                    if (!(allowCreateDir)) {
                        throw new PermissionDeniedException("test permission denied");
                    }
                }
            };
        }
    }
}

