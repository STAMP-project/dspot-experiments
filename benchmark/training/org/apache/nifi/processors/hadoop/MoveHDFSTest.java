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


import MoveHDFS.FILE_FILTER_REGEX;
import MoveHDFS.IGNORE_DOTTED_FILES;
import MoveHDFS.INPUT_DIRECTORY_OR_FILE;
import MoveHDFS.OPERATION;
import MoveHDFS.OUTPUT_DIRECTORY;
import MoveHDFS.REL_SUCCESS;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class MoveHDFSTest {
    private static final String OUTPUT_DIRECTORY = "target/test-data-output";

    private static final String TEST_DATA_DIRECTORY = "src/test/resources/testdata";

    private static final String INPUT_DIRECTORY = "target/test-data-input";

    private NiFiProperties mockNiFiProperties;

    private KerberosProperties kerberosProperties;

    @Test
    public void testOutputDirectoryValidator() {
        MoveHDFS proc = new MoveHDFSTest.TestableMoveHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        Collection<ValidationResult> results;
        ProcessContext pc;
        results = new HashSet();
        runner.setProperty(INPUT_DIRECTORY_OR_FILE, "/source");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            Assert.assertTrue(vr.toString().contains("Output Directory is required"));
        }
    }

    @Test
    public void testBothInputAndOutputDirectoriesAreValid() {
        MoveHDFS proc = new MoveHDFSTest.TestableMoveHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        Collection<ValidationResult> results;
        ProcessContext pc;
        results = new HashSet();
        runner.setProperty(INPUT_DIRECTORY_OR_FILE, MoveHDFSTest.INPUT_DIRECTORY);
        runner.setProperty(MoveHDFS.OUTPUT_DIRECTORY, MoveHDFSTest.OUTPUT_DIRECTORY);
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testOnScheduledShouldRunCleanly() throws IOException {
        FileUtils.copyDirectory(new File(MoveHDFSTest.TEST_DATA_DIRECTORY), new File(MoveHDFSTest.INPUT_DIRECTORY));
        MoveHDFS proc = new MoveHDFSTest.TestableMoveHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(INPUT_DIRECTORY_OR_FILE, MoveHDFSTest.INPUT_DIRECTORY);
        runner.setProperty(MoveHDFS.OUTPUT_DIRECTORY, MoveHDFSTest.OUTPUT_DIRECTORY);
        runner.enqueue(new byte[0]);
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Assert.assertEquals(7, flowFiles.size());
    }

    @Test
    public void testDotFileFilterIgnore() throws IOException {
        FileUtils.copyDirectory(new File(MoveHDFSTest.TEST_DATA_DIRECTORY), new File(MoveHDFSTest.INPUT_DIRECTORY));
        MoveHDFS proc = new MoveHDFSTest.TestableMoveHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(INPUT_DIRECTORY_OR_FILE, MoveHDFSTest.INPUT_DIRECTORY);
        runner.setProperty(MoveHDFS.OUTPUT_DIRECTORY, MoveHDFSTest.OUTPUT_DIRECTORY);
        runner.setProperty(IGNORE_DOTTED_FILES, "true");
        runner.enqueue(new byte[0]);
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Assert.assertEquals(7, flowFiles.size());
        Assert.assertTrue(new File(MoveHDFSTest.INPUT_DIRECTORY, ".dotfile").exists());
    }

    @Test
    public void testDotFileFilterInclude() throws IOException {
        FileUtils.copyDirectory(new File(MoveHDFSTest.TEST_DATA_DIRECTORY), new File(MoveHDFSTest.INPUT_DIRECTORY));
        MoveHDFS proc = new MoveHDFSTest.TestableMoveHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(INPUT_DIRECTORY_OR_FILE, MoveHDFSTest.INPUT_DIRECTORY);
        runner.setProperty(MoveHDFS.OUTPUT_DIRECTORY, MoveHDFSTest.OUTPUT_DIRECTORY);
        runner.setProperty(IGNORE_DOTTED_FILES, "false");
        runner.enqueue(new byte[0]);
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Assert.assertEquals(8, flowFiles.size());
    }

    @Test
    public void testFileFilterRegex() throws IOException {
        FileUtils.copyDirectory(new File(MoveHDFSTest.TEST_DATA_DIRECTORY), new File(MoveHDFSTest.INPUT_DIRECTORY));
        MoveHDFS proc = new MoveHDFSTest.TestableMoveHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(INPUT_DIRECTORY_OR_FILE, MoveHDFSTest.INPUT_DIRECTORY);
        runner.setProperty(MoveHDFS.OUTPUT_DIRECTORY, MoveHDFSTest.OUTPUT_DIRECTORY);
        runner.setProperty(FILE_FILTER_REGEX, ".*\\.gz");
        runner.enqueue(new byte[0]);
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
    }

    @Test
    public void testSingleFileAsInputCopy() throws IOException {
        FileUtils.copyDirectory(new File(MoveHDFSTest.TEST_DATA_DIRECTORY), new File(MoveHDFSTest.INPUT_DIRECTORY));
        MoveHDFS proc = new MoveHDFSTest.TestableMoveHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(INPUT_DIRECTORY_OR_FILE, ((MoveHDFSTest.INPUT_DIRECTORY) + "/randombytes-1"));
        runner.setProperty(MoveHDFS.OUTPUT_DIRECTORY, MoveHDFSTest.OUTPUT_DIRECTORY);
        runner.setProperty(OPERATION, "copy");
        runner.enqueue(new byte[0]);
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        Assert.assertTrue(new File(MoveHDFSTest.INPUT_DIRECTORY, "randombytes-1").exists());
        Assert.assertTrue(new File(MoveHDFSTest.OUTPUT_DIRECTORY, "randombytes-1").exists());
    }

    @Test
    public void testSingleFileAsInputMove() throws IOException {
        FileUtils.copyDirectory(new File(MoveHDFSTest.TEST_DATA_DIRECTORY), new File(MoveHDFSTest.INPUT_DIRECTORY));
        MoveHDFS proc = new MoveHDFSTest.TestableMoveHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(INPUT_DIRECTORY_OR_FILE, ((MoveHDFSTest.INPUT_DIRECTORY) + "/randombytes-1"));
        runner.setProperty(MoveHDFS.OUTPUT_DIRECTORY, MoveHDFSTest.OUTPUT_DIRECTORY);
        runner.enqueue(new byte[0]);
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        Assert.assertFalse(new File(MoveHDFSTest.INPUT_DIRECTORY, "randombytes-1").exists());
        Assert.assertTrue(new File(MoveHDFSTest.OUTPUT_DIRECTORY, "randombytes-1").exists());
    }

    @Test
    public void testDirectoryWithSubDirectoryAsInputMove() throws IOException {
        FileUtils.copyDirectory(new File(MoveHDFSTest.TEST_DATA_DIRECTORY), new File(MoveHDFSTest.INPUT_DIRECTORY));
        File subdir = new File(MoveHDFSTest.INPUT_DIRECTORY, "subdir");
        FileUtils.copyDirectory(new File(MoveHDFSTest.TEST_DATA_DIRECTORY), subdir);
        MoveHDFS proc = new MoveHDFSTest.TestableMoveHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(INPUT_DIRECTORY_OR_FILE, MoveHDFSTest.INPUT_DIRECTORY);
        runner.setProperty(MoveHDFS.OUTPUT_DIRECTORY, MoveHDFSTest.OUTPUT_DIRECTORY);
        runner.enqueue(new byte[0]);
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Assert.assertEquals(7, flowFiles.size());
        Assert.assertTrue(new File(MoveHDFSTest.INPUT_DIRECTORY).exists());
        Assert.assertTrue(subdir.exists());
    }

    @Test
    public void testEmptyInputDirectory() throws IOException {
        MoveHDFS proc = new MoveHDFSTest.TestableMoveHDFS(kerberosProperties);
        TestRunner runner = TestRunners.newTestRunner(proc);
        Files.createDirectories(Paths.get(MoveHDFSTest.INPUT_DIRECTORY));
        runner.setProperty(INPUT_DIRECTORY_OR_FILE, MoveHDFSTest.INPUT_DIRECTORY);
        runner.setProperty(MoveHDFS.OUTPUT_DIRECTORY, MoveHDFSTest.OUTPUT_DIRECTORY);
        runner.enqueue(new byte[0]);
        Assert.assertEquals(0, Files.list(Paths.get(MoveHDFSTest.INPUT_DIRECTORY)).count());
        runner.run();
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Assert.assertEquals(0, flowFiles.size());
    }

    private static class TestableMoveHDFS extends MoveHDFS {
        private KerberosProperties testKerberosProperties;

        public TestableMoveHDFS(KerberosProperties testKerberosProperties) {
            this.testKerberosProperties = testKerberosProperties;
        }

        @Override
        protected KerberosProperties getKerberosProperties(File kerberosConfigFile) {
            return testKerberosProperties;
        }
    }
}

