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


import CoreAttributes.FILENAME;
import ProvenanceEventType.SEND;
import PutHDFS.ABSOLUTE_HDFS_PATH_ATTRIBUTE;
import PutHDFS.COMPRESSION_CODEC;
import PutHDFS.CONFLICT_RESOLUTION;
import PutHDFS.DIRECTORY;
import PutHDFS.HADOOP_CONFIGURATION_RESOURCES;
import PutHDFS.REL_FAILURE;
import PutHDFS.REL_SUCCESS;
import PutHDFS.REPLICATION_FACTOR;
import PutHDFS.UMASK;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.security.sasl.SaslException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.util.Progressable;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.ietf.jgss.GSSException;
import org.junit.Assert;
import org.junit.Test;


public class PutHDFSTest {
    private KerberosProperties kerberosProperties;

    private FileSystem mockFileSystem;

    @Test
    public void testValidators() {
        PutHDFS proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(proc);
        Collection<ValidationResult> results;
        ProcessContext pc;
        results = new HashSet();
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            Assert.assertTrue(vr.toString().contains("is invalid because Directory is required"));
        }
        results = new HashSet();
        runner.setProperty(DIRECTORY, "target");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(0, results.size());
        results = new HashSet();
        runner.setProperty(DIRECTORY, "/target");
        runner.setProperty(REPLICATION_FACTOR, "-1");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            Assert.assertTrue(vr.toString().contains("is invalid because short integer must be greater than zero"));
        }
        proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, mockFileSystem);
        runner = TestRunners.newTestRunner(proc);
        results = new HashSet();
        runner.setProperty(DIRECTORY, "/target");
        runner.setProperty(REPLICATION_FACTOR, "0");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            Assert.assertTrue(vr.toString().contains("is invalid because short integer must be greater than zero"));
        }
        proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, mockFileSystem);
        runner = TestRunners.newTestRunner(proc);
        results = new HashSet();
        runner.setProperty(DIRECTORY, "/target");
        runner.setProperty(UMASK, "-1");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            Assert.assertTrue(vr.toString().contains("is invalid because octal umask [-1] cannot be negative"));
        }
        proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, mockFileSystem);
        runner = TestRunners.newTestRunner(proc);
        results = new HashSet();
        runner.setProperty(DIRECTORY, "/target");
        runner.setProperty(UMASK, "18");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            Assert.assertTrue(vr.toString().contains("is invalid because [18] is not a valid short octal number"));
        }
        results = new HashSet();
        runner.setProperty(DIRECTORY, "/target");
        runner.setProperty(UMASK, "2000");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            Assert.assertTrue(vr.toString().contains("is invalid because octal umask [2000] is not a valid umask"));
        }
        results = new HashSet();
        proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, mockFileSystem);
        runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "/target");
        runner.setProperty(COMPRESSION_CODEC, CompressionCodec.class.getName());
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            Assert.assertTrue(vr.toString().contains("is invalid because Given value not found in allowed set"));
        }
    }

    @Test
    public void testPutFile() throws IOException {
        PutHDFS proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "target/test-classes");
        runner.setProperty(CONFLICT_RESOLUTION, "replace");
        try (FileInputStream fis = new FileInputStream("src/test/resources/testdata/randombytes-1")) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put(FILENAME.key(), "randombytes-1");
            runner.enqueue(fis, attributes);
            runner.run();
        }
        List<MockFlowFile> failedFlowFiles = runner.getFlowFilesForRelationship(new Relationship.Builder().name("failure").build());
        Assert.assertTrue(failedFlowFiles.isEmpty());
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        MockFlowFile flowFile = flowFiles.get(0);
        Assert.assertTrue(mockFileSystem.exists(new Path("target/test-classes/randombytes-1")));
        Assert.assertEquals("randombytes-1", flowFile.getAttribute(FILENAME.key()));
        Assert.assertEquals("target/test-classes", flowFile.getAttribute(ABSOLUTE_HDFS_PATH_ATTRIBUTE));
        final List<ProvenanceEventRecord> provenanceEvents = runner.getProvenanceEvents();
        Assert.assertEquals(1, provenanceEvents.size());
        final ProvenanceEventRecord sendEvent = provenanceEvents.get(0);
        Assert.assertEquals(SEND, sendEvent.getEventType());
        // If it runs with a real HDFS, the protocol will be "hdfs://", but with a local filesystem, just assert the filename.
        Assert.assertTrue(sendEvent.getTransitUri().endsWith("target/test-classes/randombytes-1"));
    }

    @Test
    public void testPutFileWithCompression() throws IOException {
        PutHDFS proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "target/test-classes");
        runner.setProperty(CONFLICT_RESOLUTION, "replace");
        runner.setProperty(COMPRESSION_CODEC, "GZIP");
        try (FileInputStream fis = new FileInputStream("src/test/resources/testdata/randombytes-1")) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put(FILENAME.key(), "randombytes-1");
            runner.enqueue(fis, attributes);
            runner.run();
        }
        List<MockFlowFile> failedFlowFiles = runner.getFlowFilesForRelationship(new Relationship.Builder().name("failure").build());
        Assert.assertTrue(failedFlowFiles.isEmpty());
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        MockFlowFile flowFile = flowFiles.get(0);
        Assert.assertTrue(mockFileSystem.exists(new Path("target/test-classes/randombytes-1.gz")));
        Assert.assertEquals("randombytes-1.gz", flowFile.getAttribute(FILENAME.key()));
        Assert.assertEquals("target/test-classes", flowFile.getAttribute(ABSOLUTE_HDFS_PATH_ATTRIBUTE));
    }

    @Test
    public void testPutFileWithGSSException() throws IOException {
        FileSystem noCredentialsFileSystem = new PutHDFSTest.MockFileSystem() {
            @Override
            public FileStatus getFileStatus(Path path) throws IOException {
                throw new IOException("ioe", new SaslException("sasle", new GSSException(13)));
            }
        };
        TestRunner runner = TestRunners.newTestRunner(new PutHDFSTest.TestablePutHDFS(kerberosProperties, noCredentialsFileSystem));
        runner.setProperty(DIRECTORY, "target/test-classes");
        runner.setProperty(CONFLICT_RESOLUTION, "replace");
        try (FileInputStream fis = new FileInputStream("src/test/resources/testdata/randombytes-1")) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put(FILENAME.key(), "randombytes-1");
            runner.enqueue(fis, attributes);
            runner.run();
        }
        // assert no flowfiles transferred to outgoing relationships
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        // assert the input flowfile was penalized
        List<MockFlowFile> penalizedFlowFiles = runner.getPenalizedFlowFiles();
        Assert.assertEquals(1, penalizedFlowFiles.size());
        Assert.assertEquals("randombytes-1", penalizedFlowFiles.iterator().next().getAttribute(FILENAME.key()));
        // assert the processor's queue is not empty
        Assert.assertFalse(runner.isQueueEmpty());
        Assert.assertEquals(1, runner.getQueueSize().getObjectCount());
        // assert the input file is back on the queue
        ProcessSession session = runner.getProcessSessionFactory().createSession();
        FlowFile queuedFlowFile = session.get();
        Assert.assertNotNull(queuedFlowFile);
        Assert.assertEquals("randombytes-1", queuedFlowFile.getAttribute(FILENAME.key()));
        session.rollback();
    }

    @Test
    public void testPutFileWithProcessException() throws IOException {
        String dirName = "target/testPutFileWrongPermissions";
        File file = new File(dirName);
        file.mkdirs();
        Path p = new Path(dirName).makeQualified(mockFileSystem.getUri(), mockFileSystem.getWorkingDirectory());
        TestRunner runner = TestRunners.newTestRunner(new PutHDFSTest.TestablePutHDFS(kerberosProperties, mockFileSystem) {
            @Override
            protected void changeOwner(ProcessContext context, FileSystem hdfs, Path name, FlowFile flowFile) {
                throw new ProcessException("Forcing Exception to get thrown in order to verify proper handling");
            }
        });
        runner.setProperty(DIRECTORY, dirName);
        runner.setProperty(CONFLICT_RESOLUTION, "replace");
        try (FileInputStream fis = new FileInputStream("src/test/resources/testdata/randombytes-1")) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put(FILENAME.key(), "randombytes-1");
            runner.enqueue(fis, attributes);
            runner.run();
        }
        List<MockFlowFile> failedFlowFiles = runner.getFlowFilesForRelationship(new Relationship.Builder().name("failure").build());
        Assert.assertFalse(failedFlowFiles.isEmpty());
        Assert.assertTrue(failedFlowFiles.get(0).isPenalized());
        mockFileSystem.delete(p, true);
    }

    @Test
    public void testPutFileWhenDirectoryUsesValidELFunction() throws IOException {
        PutHDFS proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "target/data_${literal('testing'):substring(0,4)}");
        runner.setProperty(CONFLICT_RESOLUTION, "replace");
        try (FileInputStream fis = new FileInputStream("src/test/resources/testdata/randombytes-1")) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put(FILENAME.key(), "randombytes-1");
            runner.enqueue(fis, attributes);
            runner.run();
        }
        List<MockFlowFile> failedFlowFiles = runner.getFlowFilesForRelationship(new Relationship.Builder().name("failure").build());
        Assert.assertTrue(failedFlowFiles.isEmpty());
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        MockFlowFile flowFile = flowFiles.get(0);
        Assert.assertTrue(mockFileSystem.exists(new Path("target/data_test/randombytes-1")));
        Assert.assertEquals("randombytes-1", flowFile.getAttribute(FILENAME.key()));
        Assert.assertEquals("target/data_test", flowFile.getAttribute(ABSOLUTE_HDFS_PATH_ATTRIBUTE));
    }

    @Test
    public void testPutFileWhenDirectoryUsesUnrecognizedEL() throws IOException {
        PutHDFS proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(proc);
        // this value somehow causes NiFi to not even recognize the EL, and thus it returns successfully from calling
        // evaluateAttributeExpressions and then tries to create a Path with the exact value below and blows up
        runner.setProperty(DIRECTORY, "data_${literal('testing'):substring(0,4)%7D");
        runner.setProperty(CONFLICT_RESOLUTION, "replace");
        try (FileInputStream fis = new FileInputStream("src/test/resources/testdata/randombytes-1")) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put(FILENAME.key(), "randombytes-1");
            runner.enqueue(fis, attributes);
            runner.run();
        }
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
    }

    @Test
    public void testPutFileWhenDirectoryUsesInvalidEL() {
        PutHDFS proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, mockFileSystem);
        TestRunner runner = TestRunners.newTestRunner(proc);
        // the validator should pick up the invalid EL
        runner.setProperty(DIRECTORY, "target/data_${literal('testing'):foo()}");
        runner.setProperty(CONFLICT_RESOLUTION, "replace");
        runner.assertNotValid();
    }

    @Test
    public void testPutFilePermissionsWithProcessorConfiguredUmask() throws IOException {
        // assert the file permission is the same value as processor's property
        PutHDFSTest.MockFileSystem fileSystem = new PutHDFSTest.MockFileSystem();
        PutHDFS proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, fileSystem);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "target/test-classes");
        runner.setProperty(CONFLICT_RESOLUTION, "replace");
        String umaskPropertyValue = "027";
        runner.setProperty(UMASK, umaskPropertyValue);
        // invoke the abstractOnScheduled method so the Hadoop configuration is available to apply the MockFileSystem instance
        proc.abstractOnScheduled(runner.getProcessContext());
        fileSystem.setConf(proc.getConfiguration());
        try (FileInputStream fis = new FileInputStream("src/test/resources/testdata/randombytes-1")) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put(FILENAME.key(), "randombytes-1");
            runner.enqueue(fis, attributes);
            runner.run();
        }
        Assert.assertEquals(FsPermission.getFileDefault().applyUMask(new FsPermission(umaskPropertyValue)), fileSystem.getFileStatus(new Path("target/test-classes/randombytes-1")).getPermission());
    }

    @Test
    public void testPutFilePermissionsWithXmlConfiguredUmask() throws IOException {
        // assert the file permission is the same value as xml
        PutHDFSTest.MockFileSystem fileSystem = new PutHDFSTest.MockFileSystem();
        PutHDFS proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, fileSystem);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "target/test-classes");
        runner.setProperty(CONFLICT_RESOLUTION, "replace");
        runner.setProperty(HADOOP_CONFIGURATION_RESOURCES, "src/test/resources/core-site-perms.xml");
        // invoke the abstractOnScheduled method so the Hadoop configuration is available to apply the MockFileSystem instance
        proc.abstractOnScheduled(runner.getProcessContext());
        fileSystem.setConf(proc.getConfiguration());
        try (FileInputStream fis = new FileInputStream("src/test/resources/testdata/randombytes-1")) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put(FILENAME.key(), "randombytes-1");
            runner.enqueue(fis, attributes);
            runner.run();
        }
        Assert.assertEquals(FsPermission.getFileDefault().applyUMask(new FsPermission("777")), fileSystem.getFileStatus(new Path("target/test-classes/randombytes-1")).getPermission());
    }

    @Test
    public void testPutFilePermissionsWithNoConfiguredUmask() throws IOException {
        // assert the file permission fallback works. It should read FsPermission.DEFAULT_UMASK
        PutHDFSTest.MockFileSystem fileSystem = new PutHDFSTest.MockFileSystem();
        PutHDFS proc = new PutHDFSTest.TestablePutHDFS(kerberosProperties, fileSystem);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "target/test-classes");
        runner.setProperty(CONFLICT_RESOLUTION, "replace");
        // invoke the abstractOnScheduled method so the Hadoop configuration is available to apply the MockFileSystem instance
        proc.abstractOnScheduled(runner.getProcessContext());
        fileSystem.setConf(proc.getConfiguration());
        try (FileInputStream fis = new FileInputStream("src/test/resources/testdata/randombytes-1")) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put(FILENAME.key(), "randombytes-1");
            runner.enqueue(fis, attributes);
            runner.run();
        }
        Assert.assertEquals(FsPermission.getFileDefault().applyUMask(new FsPermission(((short) (FsPermission.DEFAULT_UMASK)))), fileSystem.getFileStatus(new Path("target/test-classes/randombytes-1")).getPermission());
    }

    private class TestablePutHDFS extends PutHDFS {
        private KerberosProperties testKerberosProperties;

        private FileSystem fileSystem;

        TestablePutHDFS(KerberosProperties testKerberosProperties, FileSystem fileSystem) {
            this.testKerberosProperties = testKerberosProperties;
            this.fileSystem = fileSystem;
        }

        @Override
        protected KerberosProperties getKerberosProperties(File kerberosConfigFile) {
            return testKerberosProperties;
        }

        @Override
        protected FileSystem getFileSystem(Configuration config) {
            fileSystem.setConf(config);
            return fileSystem;
        }

        @Override
        protected FileSystem getFileSystem() {
            return fileSystem;
        }
    }

    private class MockFileSystem extends FileSystem {
        private final Map<Path, FileStatus> pathToStatus = new HashMap<>();

        @Override
        public URI getUri() {
            return URI.create("file:///");
        }

        @Override
        public FSDataInputStream open(final Path f, final int bufferSize) {
            return null;
        }

        @Override
        public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) {
            pathToStatus.put(f, newFile(f, permission));
            return new FSDataOutputStream(new ByteArrayOutputStream(), new Statistics(""));
        }

        @Override
        public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) {
            return null;
        }

        @Override
        public boolean rename(final Path src, final Path dst) {
            if (pathToStatus.containsKey(src)) {
                pathToStatus.put(dst, pathToStatus.remove(src));
            } else {
                return false;
            }
            return true;
        }

        @Override
        public boolean delete(final Path f, final boolean recursive) {
            if (pathToStatus.containsKey(f)) {
                pathToStatus.remove(f);
            } else {
                return false;
            }
            return true;
        }

        @Override
        public FileStatus[] listStatus(final Path f) {
            return null;
        }

        @Override
        public void setWorkingDirectory(final Path new_dir) {
        }

        @Override
        public Path getWorkingDirectory() {
            return new Path(new File(".").getAbsolutePath());
        }

        @Override
        public boolean mkdirs(final Path f, final FsPermission permission) {
            return false;
        }

        @Override
        public boolean mkdirs(Path f) {
            pathToStatus.put(f, newDir(f));
            return true;
        }

        @Override
        public FileStatus getFileStatus(final Path f) throws IOException {
            final FileStatus fileStatus = pathToStatus.get(f);
            if (fileStatus == null)
                throw new FileNotFoundException();

            return fileStatus;
        }

        @Override
        public boolean exists(Path f) {
            return pathToStatus.containsKey(f);
        }

        private FileStatus newFile(Path p, FsPermission permission) {
            return new FileStatus(100L, false, 3, ((128 * 1024) * 1024), 1523456000000L, 1523457000000L, permission, "owner", "group", p);
        }

        private FileStatus newDir(Path p) {
            return new FileStatus(1L, true, 3, ((128 * 1024) * 1024), 1523456000000L, 1523457000000L, PutHDFSTest.perms(((short) (493))), "owner", "group", p);
        }

        @Override
        public long getDefaultBlockSize(Path f) {
            return 33554432L;
        }
    }
}

