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


import DeleteHDFS.FILE_OR_DIRECTORY;
import DeleteHDFS.REL_FAILURE;
import DeleteHDFS.REL_SUCCESS;
import ProvenanceEventType.REMOTE_INVOCATION;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestDeleteHDFS {
    private NiFiProperties mockNiFiProperties;

    private FileSystem mockFileSystem;

    private KerberosProperties kerberosProperties;

    // Tests the case where a file is found and deleted but there was no incoming connection
    @Test
    public void testSuccessfulDelete() throws Exception {
        Path filePath = new Path("/some/path/to/file.txt");
        Mockito.when(mockFileSystem.exists(ArgumentMatchers.any(Path.class))).thenReturn(true);
        Mockito.when(mockFileSystem.getUri()).thenReturn(new URI("hdfs://0.example.com:8020"));
        DeleteHDFS deleteHDFS = new TestDeleteHDFS.TestableDeleteHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(deleteHDFS);
        runner.setIncomingConnection(false);
        runner.assertNotValid();
        runner.setProperty(FILE_OR_DIRECTORY, filePath.toString());
        runner.assertValid();
        runner.run();
        // Even if there's no incoming relationship, a FlowFile is created to indicate which path is deleted.
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        final List<ProvenanceEventRecord> provenanceEvents = runner.getProvenanceEvents();
        Assert.assertEquals(1, provenanceEvents.size());
        Assert.assertEquals(REMOTE_INVOCATION, provenanceEvents.get(0).getEventType());
        Assert.assertEquals("hdfs://0.example.com:8020/some/path/to/file.txt", provenanceEvents.get(0).getTransitUri());
    }

    @Test
    public void testDeleteFromIncomingFlowFile() throws Exception {
        Path filePath = new Path("/some/path/to/file.txt");
        Mockito.when(mockFileSystem.exists(ArgumentMatchers.any(Path.class))).thenReturn(true);
        Mockito.when(mockFileSystem.getUri()).thenReturn(new URI("hdfs://0.example.com:8020"));
        DeleteHDFS deleteHDFS = new TestDeleteHDFS.TestableDeleteHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(deleteHDFS);
        runner.setProperty(FILE_OR_DIRECTORY, "${hdfs.file}");
        Map<String, String> attributes = Maps.newHashMap();
        attributes.put("hdfs.file", filePath.toString());
        runner.enqueue("foo", attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
    }

    @Test
    public void testIOException() throws Exception {
        Path filePath = new Path("/some/path/to/file.txt");
        Mockito.when(mockFileSystem.exists(ArgumentMatchers.any(Path.class))).thenThrow(new IOException());
        DeleteHDFS deleteHDFS = new TestDeleteHDFS.TestableDeleteHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(deleteHDFS);
        runner.setProperty(FILE_OR_DIRECTORY, "${hdfs.file}");
        Map<String, String> attributes = Maps.newHashMap();
        attributes.put("hdfs.file", filePath.toString());
        runner.enqueue("foo", attributes);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void testPermissionIOException() throws Exception {
        Path filePath = new Path("/some/path/to/file.txt");
        Mockito.when(mockFileSystem.exists(ArgumentMatchers.any(Path.class))).thenReturn(true);
        Mockito.when(mockFileSystem.delete(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Boolean.class))).thenThrow(new IOException("Permissions Error"));
        DeleteHDFS deleteHDFS = new TestDeleteHDFS.TestableDeleteHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(deleteHDFS);
        runner.setProperty(FILE_OR_DIRECTORY, "${hdfs.file}");
        Map<String, String> attributes = Maps.newHashMap();
        attributes.put("hdfs.file", filePath.toString());
        runner.enqueue("foo", attributes);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertEquals("file.txt", flowFile.getAttribute("hdfs.filename"));
        Assert.assertEquals("/some/path/to", flowFile.getAttribute("hdfs.path"));
        Assert.assertEquals("Permissions Error", flowFile.getAttribute("hdfs.error.message"));
    }

    @Test
    public void testNoFlowFilesWithIncomingConnection() throws Exception {
        Path filePath = new Path("${hdfs.file}");
        DeleteHDFS deleteHDFS = new TestDeleteHDFS.TestableDeleteHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(deleteHDFS);
        runner.setProperty(FILE_OR_DIRECTORY, filePath.toString());
        runner.setIncomingConnection(true);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test
    public void testUnsuccessfulDelete() throws Exception {
        Path filePath = new Path("/some/path/to/file.txt");
        Mockito.when(mockFileSystem.exists(ArgumentMatchers.any(Path.class))).thenReturn(false);
        DeleteHDFS deleteHDFS = new TestDeleteHDFS.TestableDeleteHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(deleteHDFS);
        runner.setIncomingConnection(false);
        runner.assertNotValid();
        runner.setProperty(FILE_OR_DIRECTORY, filePath.toString());
        runner.assertValid();
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test
    public void testGlobDelete() throws Exception {
        Path glob = new Path("/data/for/2017/08/05/*");
        int fileCount = 300;
        FileStatus[] fileStatuses = new FileStatus[fileCount];
        for (int i = 0; i < fileCount; i++) {
            Path file = new Path(("/data/for/2017/08/05/file" + i));
            FileStatus fileStatus = Mockito.mock(FileStatus.class);
            Mockito.when(fileStatus.getPath()).thenReturn(file);
            fileStatuses[i] = fileStatus;
        }
        Mockito.when(mockFileSystem.exists(ArgumentMatchers.any(Path.class))).thenReturn(true);
        Mockito.when(mockFileSystem.globStatus(ArgumentMatchers.any(Path.class))).thenReturn(fileStatuses);
        Mockito.when(mockFileSystem.getUri()).thenReturn(new URI("hdfs://0.example.com:8020"));
        DeleteHDFS deleteHDFS = new TestDeleteHDFS.TestableDeleteHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(deleteHDFS);
        runner.setIncomingConnection(false);
        runner.assertNotValid();
        runner.setProperty(FILE_OR_DIRECTORY, glob.toString());
        runner.assertValid();
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
    }

    @Test
    public void testGlobDeleteFromIncomingFlowFile() throws Exception {
        Path glob = new Path("/data/for/2017/08/05/*");
        int fileCount = 300;
        FileStatus[] fileStatuses = new FileStatus[fileCount];
        for (int i = 0; i < fileCount; i++) {
            Path file = new Path(("/data/for/2017/08/05/file" + i));
            FileStatus fileStatus = Mockito.mock(FileStatus.class);
            Mockito.when(fileStatus.getPath()).thenReturn(file);
            fileStatuses[i] = fileStatus;
        }
        Mockito.when(mockFileSystem.exists(ArgumentMatchers.any(Path.class))).thenReturn(true);
        Mockito.when(mockFileSystem.globStatus(ArgumentMatchers.any(Path.class))).thenReturn(fileStatuses);
        Mockito.when(mockFileSystem.getUri()).thenReturn(new URI("hdfs://0.example.com:8020"));
        DeleteHDFS deleteHDFS = new TestDeleteHDFS.TestableDeleteHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(deleteHDFS);
        runner.setIncomingConnection(true);
        Map<String, String> attributes = Maps.newHashMap();
        runner.enqueue("foo", attributes);
        runner.setProperty(FILE_OR_DIRECTORY, glob.toString());
        runner.assertValid();
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        runner.assertTransferCount(REL_SUCCESS, 1);
    }

    private static class TestableDeleteHDFS extends DeleteHDFS {
        private KerberosProperties testKerberosProperties;

        private FileSystem mockFileSystem;

        public TestableDeleteHDFS(KerberosProperties kerberosProperties, FileSystem mockFileSystem) {
            this.testKerberosProperties = kerberosProperties;
            this.mockFileSystem = mockFileSystem;
        }

        @Override
        protected KerberosProperties getKerberosProperties(File kerberosConfigFile) {
            return testKerberosProperties;
        }

        @Override
        protected FileSystem getFileSystem() {
            return mockFileSystem;
        }
    }

    @Test
    public void testGlobMatcher() throws Exception {
        DeleteHDFS deleteHDFS = new DeleteHDFS();
        Assert.assertTrue(deleteHDFS.GLOB_MATCHER.reset("/data/for/08/09/*").find());
        Assert.assertTrue(deleteHDFS.GLOB_MATCHER.reset("/data/for/08/09/[01-04]").find());
        Assert.assertTrue(deleteHDFS.GLOB_MATCHER.reset("/data/for/0?/09/").find());
        Assert.assertFalse(deleteHDFS.GLOB_MATCHER.reset("/data/for/08/09").find());
    }
}

