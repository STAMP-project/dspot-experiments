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
import GetHDFS.COMPRESSION_CODEC;
import GetHDFS.FILE_FILTER_REGEX;
import GetHDFS.KEEP_SOURCE_FILE;
import GetHDFS.MAX_AGE;
import GetHDFS.MIN_AGE;
import GetHDFS.REL_SUCCESS;
import ProvenanceEventType.RECEIVE;
import PutHDFS.DIRECTORY;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class GetHDFSTest {
    private NiFiProperties mockNiFiProperties;

    private KerberosProperties kerberosProperties;

    @Test
    public void getPathDifferenceTest() {
        Assert.assertEquals("", GetHDFS.getPathDifference(new Path("/root"), new Path("/file")));
        Assert.assertEquals("", GetHDFS.getPathDifference(new Path("/root"), new Path("/root/file")));
        Assert.assertEquals("one", GetHDFS.getPathDifference(new Path("/root"), new Path("/root/one/file")));
        Assert.assertEquals("one/two", GetHDFS.getPathDifference(new Path("/root"), new Path("/root/one/two/file")));
        Assert.assertEquals("one/two/three", GetHDFS.getPathDifference(new Path("/root"), new Path("/root/one/two/three/file")));
        Assert.assertEquals("", GetHDFS.getPathDifference(new Path("root"), new Path("/file")));
        Assert.assertEquals("", GetHDFS.getPathDifference(new Path("root"), new Path("/root/file")));
        Assert.assertEquals("one", GetHDFS.getPathDifference(new Path("root"), new Path("/root/one/file")));
        Assert.assertEquals("one/two", GetHDFS.getPathDifference(new Path("root"), new Path("/root/one/two/file")));
        Assert.assertEquals("one/two/three", GetHDFS.getPathDifference(new Path("root"), new Path("/base/root/one/two/three/file")));
        Assert.assertEquals("", GetHDFS.getPathDifference(new Path("/foo/bar"), new Path("/file")));
        Assert.assertEquals("", GetHDFS.getPathDifference(new Path("/foo/bar"), new Path("/foo/bar/file")));
        Assert.assertEquals("one", GetHDFS.getPathDifference(new Path("/foo/bar"), new Path("/foo/bar/one/file")));
        Assert.assertEquals("one/two", GetHDFS.getPathDifference(new Path("/foo/bar"), new Path("/foo/bar/one/two/file")));
        Assert.assertEquals("one/two/three", GetHDFS.getPathDifference(new Path("/foo/bar"), new Path("/foo/bar/one/two/three/file")));
        Assert.assertEquals("", GetHDFS.getPathDifference(new Path("foo/bar"), new Path("/file")));
        Assert.assertEquals("", GetHDFS.getPathDifference(new Path("foo/bar"), new Path("/foo/bar/file")));
        Assert.assertEquals("one", GetHDFS.getPathDifference(new Path("foo/bar"), new Path("/foo/bar/one/file")));
        Assert.assertEquals("one/two", GetHDFS.getPathDifference(new Path("foo/bar"), new Path("/foo/bar/one/two/file")));
        Assert.assertEquals("one/two/three", GetHDFS.getPathDifference(new Path("foo/bar"), new Path("/base/foo/bar/one/two/three/file")));
        Assert.assertEquals("one/two/three", GetHDFS.getPathDifference(new Path("foo/bar"), new Path("/base/base2/base3/foo/bar/one/two/three/file")));
    }

    @Test
    public void testValidators() {
        GetHDFS proc = new GetHDFSTest.TestableGetHDFS(kerberosProperties);
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
        runner.setProperty(GetHDFS.DIRECTORY, "/target");
        runner.setProperty(MIN_AGE, "10 secs");
        runner.setProperty(MAX_AGE, "5 secs");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            Assert.assertTrue(vr.toString().contains("is invalid because Minimum File Age cannot be greater than Maximum File Age"));
        }
    }

    @Test
    public void testGetFilesWithFilter() {
        GetHDFS proc = new GetHDFSTest.TestableGetHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "src/test/resources/testdata");
        runner.setProperty(FILE_FILTER_REGEX, "random.*");
        runner.setProperty(KEEP_SOURCE_FILE, "true");
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(4, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertTrue(flowFile.getAttribute(FILENAME.key()).startsWith("random"));
        }
    }

    @Test
    public void testDirectoryDoesNotExist() {
        GetHDFS proc = new GetHDFSTest.TestableGetHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "does/not/exist/${now():format('yyyyMMdd')}");
        runner.setProperty(KEEP_SOURCE_FILE, "true");
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, flowFiles.size());
    }

    @Test
    public void testAutomaticDecompression() throws IOException {
        GetHDFS proc = new GetHDFSTest.TestableGetHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "src/test/resources/testdata");
        runner.setProperty(FILE_FILTER_REGEX, "random.*.gz");
        runner.setProperty(KEEP_SOURCE_FILE, "true");
        runner.setProperty(COMPRESSION_CODEC, "AUTOMATIC");
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        MockFlowFile flowFile = flowFiles.get(0);
        Assert.assertTrue(flowFile.getAttribute(FILENAME.key()).equals("randombytes-1"));
        InputStream expected = getClass().getResourceAsStream("/testdata/randombytes-1");
        flowFile.assertContentEquals(expected);
    }

    @Test
    public void testInferCompressionCodecDisabled() throws IOException {
        GetHDFS proc = new GetHDFSTest.TestableGetHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "src/test/resources/testdata");
        runner.setProperty(FILE_FILTER_REGEX, "random.*.gz");
        runner.setProperty(KEEP_SOURCE_FILE, "true");
        runner.setProperty(COMPRESSION_CODEC, "NONE");
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        MockFlowFile flowFile = flowFiles.get(0);
        Assert.assertTrue(flowFile.getAttribute(FILENAME.key()).equals("randombytes-1.gz"));
        InputStream expected = getClass().getResourceAsStream("/testdata/randombytes-1.gz");
        flowFile.assertContentEquals(expected);
    }

    @Test
    public void testFileExtensionNotACompressionCodec() throws IOException {
        GetHDFS proc = new GetHDFSTest.TestableGetHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "src/test/resources/testdata");
        runner.setProperty(FILE_FILTER_REGEX, ".*.zip");
        runner.setProperty(KEEP_SOURCE_FILE, "true");
        runner.setProperty(COMPRESSION_CODEC, "AUTOMATIC");
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        MockFlowFile flowFile = flowFiles.get(0);
        Assert.assertTrue(flowFile.getAttribute(FILENAME.key()).equals("13545423550275052.zip"));
        InputStream expected = getClass().getResourceAsStream("/testdata/13545423550275052.zip");
        flowFile.assertContentEquals(expected);
    }

    @Test
    public void testDirectoryUsesValidEL() throws IOException {
        GetHDFS proc = new GetHDFSTest.TestableGetHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "src/test/resources/${literal('testdata'):substring(0,8)}");
        runner.setProperty(FILE_FILTER_REGEX, ".*.zip");
        runner.setProperty(KEEP_SOURCE_FILE, "true");
        runner.setProperty(COMPRESSION_CODEC, "AUTOMATIC");
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        MockFlowFile flowFile = flowFiles.get(0);
        Assert.assertTrue(flowFile.getAttribute(FILENAME.key()).equals("13545423550275052.zip"));
        InputStream expected = getClass().getResourceAsStream("/testdata/13545423550275052.zip");
        flowFile.assertContentEquals(expected);
        final List<ProvenanceEventRecord> provenanceEvents = runner.getProvenanceEvents();
        Assert.assertEquals(1, provenanceEvents.size());
        final ProvenanceEventRecord receiveEvent = provenanceEvents.get(0);
        Assert.assertEquals(RECEIVE, receiveEvent.getEventType());
        // If it runs with a real HDFS, the protocol will be "hdfs://", but with a local filesystem, just assert the filename.
        Assert.assertTrue(receiveEvent.getTransitUri().endsWith("13545423550275052.zip"));
    }

    @Test
    public void testDirectoryUsesUnrecognizedEL() throws IOException {
        GetHDFS proc = new GetHDFSTest.TestableGetHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "data_${literal('testing'):substring(0,4)%7D");
        runner.setProperty(FILE_FILTER_REGEX, ".*.zip");
        runner.setProperty(KEEP_SOURCE_FILE, "true");
        runner.setProperty(COMPRESSION_CODEC, "AUTOMATIC");
        runner.assertNotValid();
    }

    @Test
    public void testDirectoryUsesInvalidEL() throws IOException {
        GetHDFS proc = new GetHDFSTest.TestableGetHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(DIRECTORY, "data_${literal('testing'):foo()}");
        runner.setProperty(FILE_FILTER_REGEX, ".*.zip");
        runner.setProperty(KEEP_SOURCE_FILE, "true");
        runner.setProperty(COMPRESSION_CODEC, "AUTOMATIC");
        runner.assertNotValid();
    }

    private static class TestableGetHDFS extends GetHDFS {
        private final KerberosProperties testKerberosProperties;

        public TestableGetHDFS(KerberosProperties testKerberosProperties) {
            this.testKerberosProperties = testKerberosProperties;
        }

        @Override
        protected KerberosProperties getKerberosProperties(File kerberosConfigFile) {
            return testKerberosProperties;
        }
    }
}

