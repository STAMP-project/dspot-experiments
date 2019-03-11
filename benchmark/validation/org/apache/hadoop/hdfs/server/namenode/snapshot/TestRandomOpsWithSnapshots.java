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
package org.apache.hadoop.hdfs.server.namenode.snapshot;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.SnapshottableDirectoryStatus;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing random FileSystem operations with random Snapshot operations.
 */
public class TestRandomOpsWithSnapshots {
    private static final Logger LOG = LoggerFactory.getLogger(TestRandomOpsWithSnapshots.class);

    private static final short REPL = 3;

    private static final long BLOCKSIZE = 1024;

    private static final int TOTAL_FILECOUNT = 250;

    private static final int MAX_NUM_ITERATIONS = 10;

    private static final int MAX_NUM_FILESYSTEM_OPERATIONS = 50;

    private static final int MAX_NUM_SNAPSHOT_OPERATIONS = 50;

    private static final int MAX_NUM_SUB_DIRECTORIES_LEVEL = 10;

    private static final int MAX_NUM_FILE_LENGTH = 100;

    private static final int MIN_NUM_OPERATIONS = 25;

    private static final String TESTDIRSTRING = "/testDir";

    private static final String WITNESSDIRSTRING = "/WITNESSDIR";

    private static final Path TESTDIR = new Path(TestRandomOpsWithSnapshots.TESTDIRSTRING);

    private static final Path WITNESSDIR = new Path(TestRandomOpsWithSnapshots.WITNESSDIRSTRING);

    private static List<Path> snapshottableDirectories = new ArrayList<Path>();

    private static Map<Path, ArrayList<String>> pathToSnapshotsMap = new HashMap<Path, ArrayList<String>>();

    private static final Configuration CONFIG = new Configuration();

    private MiniDFSCluster cluster;

    private DistributedFileSystem hdfs;

    private static Random generator = null;

    private int numberFileCreated = 0;

    private int numberFileDeleted = 0;

    private int numberFileRenamed = 0;

    private int numberDirectoryCreated = 0;

    private int numberDirectoryDeleted = 0;

    private int numberDirectoryRenamed = 0;

    private int numberSnapshotCreated = 0;

    private int numberSnapshotDeleted = 0;

    private int numberSnapshotRenamed = 0;

    // Operation directories
    private enum OperationDirectories {

        TestDir,
        WitnessDir;}

    // Operation type
    private enum OperationType {

        FileSystem,
        Snapshot;}

    // FileSystem & Snapshot operation
    private enum Operations {

        FileSystem_CreateFile(2, TestRandomOpsWithSnapshots.OperationType.FileSystem),
        /* operation weight */
        FileSystem_DeleteFile(2, TestRandomOpsWithSnapshots.OperationType.FileSystem),
        FileSystem_RenameFile(2, TestRandomOpsWithSnapshots.OperationType.FileSystem),
        FileSystem_CreateDir(1, TestRandomOpsWithSnapshots.OperationType.FileSystem),
        FileSystem_DeleteDir(1, TestRandomOpsWithSnapshots.OperationType.FileSystem),
        FileSystem_RenameDir(2, TestRandomOpsWithSnapshots.OperationType.FileSystem),
        Snapshot_CreateSnapshot(5, TestRandomOpsWithSnapshots.OperationType.Snapshot),
        Snapshot_DeleteSnapshot(3, TestRandomOpsWithSnapshots.OperationType.Snapshot),
        Snapshot_RenameSnapshot(2, TestRandomOpsWithSnapshots.OperationType.Snapshot);
        private int weight;

        private TestRandomOpsWithSnapshots.OperationType operationType;

        Operations(int weight, TestRandomOpsWithSnapshots.OperationType type) {
            this.weight = weight;
            this.operationType = type;
        }

        private int getWeight() {
            return weight;
        }

        private static final TestRandomOpsWithSnapshots.Operations[] VALUES = TestRandomOpsWithSnapshots.Operations.values();

        private static int sumWeights(TestRandomOpsWithSnapshots.OperationType type) {
            int sum = 0;
            for (TestRandomOpsWithSnapshots.Operations value : TestRandomOpsWithSnapshots.Operations.VALUES) {
                if ((value.operationType) == type) {
                    sum += value.getWeight();
                }
            }
            return sum;
        }

        private static final int TOTAL_WEIGHT_FILESYSTEM = TestRandomOpsWithSnapshots.Operations.sumWeights(TestRandomOpsWithSnapshots.OperationType.FileSystem);

        private static final int TOTAL_WEIGHT_SNAPSHOT = TestRandomOpsWithSnapshots.Operations.sumWeights(TestRandomOpsWithSnapshots.OperationType.Snapshot);

        public static TestRandomOpsWithSnapshots.Operations getRandomOperation(TestRandomOpsWithSnapshots.OperationType type) {
            int randomNum = 0;
            TestRandomOpsWithSnapshots.Operations randomOperation = null;
            switch (type) {
                case FileSystem :
                    randomNum = TestRandomOpsWithSnapshots.generator.nextInt(TestRandomOpsWithSnapshots.Operations.TOTAL_WEIGHT_FILESYSTEM);
                    break;
                case Snapshot :
                    randomNum = TestRandomOpsWithSnapshots.generator.nextInt(TestRandomOpsWithSnapshots.Operations.TOTAL_WEIGHT_SNAPSHOT);
                    break;
                default :
                    break;
            }
            int currentWeightSum = 0;
            for (TestRandomOpsWithSnapshots.Operations currentValue : TestRandomOpsWithSnapshots.Operations.VALUES) {
                if ((currentValue.operationType) == type) {
                    if (randomNum <= (currentWeightSum + (currentValue.getWeight()))) {
                        randomOperation = currentValue;
                        break;
                    }
                    currentWeightSum += currentValue.getWeight();
                }
            }
            return randomOperation;
        }
    }

    /* Random file system operations with snapshot operations in between. */
    @Test(timeout = 900000)
    public void testRandomOperationsWithSnapshots() throws IOException, InterruptedException, TimeoutException {
        // Set
        long seed = System.currentTimeMillis();
        TestRandomOpsWithSnapshots.LOG.info(("testRandomOperationsWithSnapshots, seed to be used: " + seed));
        TestRandomOpsWithSnapshots.generator = new Random(seed);
        int fileLen = TestRandomOpsWithSnapshots.generator.nextInt(TestRandomOpsWithSnapshots.MAX_NUM_FILE_LENGTH);
        createFiles(TestRandomOpsWithSnapshots.TESTDIRSTRING, fileLen);
        // Get list of snapshottable directories
        SnapshottableDirectoryStatus[] snapshottableDirectoryStatus = hdfs.getSnapshottableDirListing();
        for (SnapshottableDirectoryStatus ssds : snapshottableDirectoryStatus) {
            TestRandomOpsWithSnapshots.snapshottableDirectories.add(ssds.getFullPath());
        }
        if ((TestRandomOpsWithSnapshots.snapshottableDirectories.size()) == 0) {
            hdfs.allowSnapshot(hdfs.getHomeDirectory());
            TestRandomOpsWithSnapshots.snapshottableDirectories.add(hdfs.getHomeDirectory());
        }
        int numberOfIterations = TestRandomOpsWithSnapshots.generator.nextInt(TestRandomOpsWithSnapshots.MAX_NUM_ITERATIONS);
        TestRandomOpsWithSnapshots.LOG.info(("Number of iterations: " + numberOfIterations));
        int numberFileSystemOperations = (TestRandomOpsWithSnapshots.generator.nextInt((((TestRandomOpsWithSnapshots.MAX_NUM_FILESYSTEM_OPERATIONS) - (TestRandomOpsWithSnapshots.MIN_NUM_OPERATIONS)) + 1))) + (TestRandomOpsWithSnapshots.MIN_NUM_OPERATIONS);
        TestRandomOpsWithSnapshots.LOG.info(("Number of FileSystem operations: " + numberFileSystemOperations));
        int numberSnapshotOperations = (TestRandomOpsWithSnapshots.generator.nextInt(((TestRandomOpsWithSnapshots.MAX_NUM_SNAPSHOT_OPERATIONS) - (TestRandomOpsWithSnapshots.MIN_NUM_OPERATIONS)))) + (TestRandomOpsWithSnapshots.MIN_NUM_OPERATIONS);
        TestRandomOpsWithSnapshots.LOG.info(("Number of Snapshot operations: " + numberSnapshotOperations));
        // Act && Verify
        randomOperationsWithSnapshots(numberOfIterations, numberFileSystemOperations, numberSnapshotOperations);
    }
}

