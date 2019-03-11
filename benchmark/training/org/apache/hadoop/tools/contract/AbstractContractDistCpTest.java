/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.tools.contract;


import CopyMapper.Counter.BYTESCOPIED;
import CopyMapper.Counter.SKIP;
import SequenceFile.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.AbstractFSContractTestBase;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.tools.CopyListingFileStatus;
import org.apache.hadoop.tools.DistCpConstants;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Contract test suite covering a file system's integration with DistCp.  The
 * tests coordinate two file system instances: one "local", which is the local
 * file system, and the other "remote", which is the file system implementation
 * under test.  The tests in the suite cover both copying from local to remote
 * (e.g. a backup use case) and copying from remote to local (e.g. a restore use
 * case).
 */
public abstract class AbstractContractDistCpTest extends AbstractFSContractTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractContractDistCpTest.class);

    public static final String SCALE_TEST_DISTCP_FILE_SIZE_KB = "scale.test.distcp.file.size.kb";

    public static final int DEFAULT_DISTCP_SIZE_KB = 1024;

    protected static final int MB = 1024 * 1024;

    @Rule
    public TestName testName = new TestName();

    private Configuration conf;

    private FileSystem localFS;

    private FileSystem remoteFS;

    private Path localDir;

    private Path remoteDir;

    private Path inputDir;

    private Path inputSubDir1;

    private Path inputSubDir2;

    private Path inputSubDir4;

    private Path inputFile1;

    private Path inputFile2;

    private Path inputFile3;

    private Path inputFile4;

    private Path inputFile5;

    private Path outputDir;

    private Path outputSubDir1;

    private Path outputSubDir2;

    private Path outputSubDir4;

    private Path outputFile1;

    private Path outputFile2;

    private Path outputFile3;

    private Path outputFile4;

    private Path outputFile5;

    private Path inputDirUnderOutputDir;

    @Test
    public void testUpdateDeepDirectoryStructureToRemote() throws Exception {
        describe("update a deep directory structure from local to remote");
        distCpDeepDirectoryStructure(localFS, localDir, remoteFS, remoteDir);
        distCpUpdateDeepDirectoryStructure(inputDirUnderOutputDir);
    }

    @Test
    public void testUpdateDeepDirectoryStructureNoChange() throws Exception {
        describe(("update an unchanged directory structure" + " from local to remote; expect no copy"));
        Path target = distCpDeepDirectoryStructure(localFS, localDir, remoteFS, remoteDir);
        describe("\nExecuting Update\n");
        Job job = distCpUpdate(localDir, target);
        assertCounterInRange(job, SKIP, 1, (-1));
        assertCounterInRange(job, BYTESCOPIED, 0, 0);
    }

    @Test
    public void testTrackDeepDirectoryStructureToRemote() throws Exception {
        describe("copy a deep directory structure from local to remote");
        Path destDir = distCpDeepDirectoryStructure(localFS, localDir, remoteFS, remoteDir);
        ContractTestUtils.assertIsDirectory(remoteFS, destDir);
        describe("Now do an incremental update and save of missing files");
        Path srcDir = inputDir;
        // same path setup as in deepDirectoryStructure()
        Path trackDir = new Path(localDir, "trackDir");
        describe("\nDirectories\n");
        lsR("Local to update", localFS, srcDir);
        lsR("Remote before update", remoteFS, destDir);
        ContractTestUtils.assertPathsExist(localFS, "Paths for test are wrong", inputFile2, inputFile3, inputFile4, inputFile5);
        Path inputFileNew1 = modifySourceDirectories();
        // Distcp set to track but not delete
        runDistCp(buildWithStandardOptions(new org.apache.hadoop.tools.DistCpOptions.Builder(Collections.singletonList(srcDir), inputDirUnderOutputDir).withTrackMissing(trackDir).withSyncFolder(true).withOverwrite(false)));
        lsR("tracked udpate", remoteFS, destDir);
        // new file went over
        Path outputFileNew1 = new Path(outputSubDir2, "newfile1");
        ContractTestUtils.assertIsFile(remoteFS, outputFileNew1);
        ContractTestUtils.assertPathExists(localFS, "tracking directory", trackDir);
        // now read in the listings
        Path sortedSourceListing = new Path(trackDir, DistCpConstants.SOURCE_SORTED_FILE);
        ContractTestUtils.assertIsFile(localFS, sortedSourceListing);
        Path sortedTargetListing = new Path(trackDir, DistCpConstants.TARGET_SORTED_FILE);
        ContractTestUtils.assertIsFile(localFS, sortedTargetListing);
        // deletion didn't happen
        ContractTestUtils.assertPathsExist(remoteFS, "DistCP should have retained", outputFile2, outputFile3, outputFile4, outputSubDir4);
        // now scan the table and see that things are there.
        Map<String, Path> sourceFiles = new HashMap<>(10);
        Map<String, Path> targetFiles = new HashMap<>(10);
        try (SequenceFile.Reader sourceReader = new SequenceFile.Reader(conf, Reader.file(sortedSourceListing));SequenceFile.Reader targetReader = new SequenceFile.Reader(conf, Reader.file(sortedTargetListing))) {
            CopyListingFileStatus copyStatus = new CopyListingFileStatus();
            Text name = new Text();
            while (sourceReader.next(name, copyStatus)) {
                String key = name.toString();
                Path path = copyStatus.getPath();
                AbstractContractDistCpTest.LOG.info("{}: {}", key, path);
                sourceFiles.put(key, path);
            } 
            while (targetReader.next(name, copyStatus)) {
                String key = name.toString();
                Path path = copyStatus.getPath();
                AbstractContractDistCpTest.LOG.info("{}: {}", key, path);
                targetFiles.put(name.toString(), copyStatus.getPath());
            } 
        }
        // look for the new file in both lists
        assertTrue((("No " + outputFileNew1) + " in source listing"), sourceFiles.containsValue(inputFileNew1));
        assertTrue((("No " + outputFileNew1) + " in target listing"), targetFiles.containsValue(outputFileNew1));
        assertTrue((("No " + (outputSubDir4)) + " in target listing"), targetFiles.containsValue(outputSubDir4));
        assertFalse((("Found " + (inputSubDir4)) + " in source listing"), sourceFiles.containsValue(inputSubDir4));
    }

    @Test
    public void largeFilesToRemote() throws Exception {
        describe("copy multiple large files from local to remote");
        largeFiles(localFS, localDir, remoteFS, remoteDir);
    }

    @Test
    public void testDeepDirectoryStructureFromRemote() throws Exception {
        describe("copy a deep directory structure from remote to local");
        distCpDeepDirectoryStructure(remoteFS, remoteDir, localFS, localDir);
    }

    @Test
    public void testLargeFilesFromRemote() throws Exception {
        describe("copy multiple large files from remote to local");
        largeFiles(remoteFS, remoteDir, localFS, localDir);
    }

    @Test
    public void testDirectWrite() throws Exception {
        describe("copy file from local to remote using direct write option");
        directWrite(localFS, localDir, remoteFS, remoteDir, true);
    }

    @Test
    public void testNonDirectWrite() throws Exception {
        describe(("copy file from local to remote without using direct write " + "option"));
        directWrite(localFS, localDir, remoteFS, remoteDir, false);
    }
}

