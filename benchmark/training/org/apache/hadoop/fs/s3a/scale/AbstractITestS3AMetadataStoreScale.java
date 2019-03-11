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
package org.apache.hadoop.fs.s3a.scale;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.fs.s3a.S3ATestConstants;
import org.apache.hadoop.fs.s3a.s3guard.MetadataStore;
import org.apache.hadoop.fs.s3a.s3guard.PathMetadata;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the performance of a MetadataStore.  Useful for load testing.
 * Could be separated from S3A code, but we're using the S3A scale test
 * framework for convenience.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractITestS3AMetadataStoreScale extends S3AScaleTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractITestS3AMetadataStoreScale.class);

    /**
     * Some dummy values for FileStatus contents.
     */
    static final long BLOCK_SIZE = (32 * 1024) * 1024;

    static final long SIZE = (AbstractITestS3AMetadataStoreScale.BLOCK_SIZE) * 2;

    static final String OWNER = "bob";

    static final long ACCESS_TIME = System.currentTimeMillis();

    static final Path BUCKET_ROOT = new Path("s3a://fake-bucket/");

    @Test
    public void test_010_Put() throws Throwable {
        describe("Test workload of put() operations");
        // As described in hadoop-aws site docs, count parameter is used for
        // width and depth of directory tree
        int width = getConf().getInt(S3ATestConstants.KEY_DIRECTORY_COUNT, S3ATestConstants.DEFAULT_DIRECTORY_COUNT);
        int depth = width;
        List<PathMetadata> paths = new ArrayList<>();
        AbstractITestS3AMetadataStoreScale.createDirTree(AbstractITestS3AMetadataStoreScale.BUCKET_ROOT, depth, width, paths);
        long count = 1;// Some value in case we throw an exception below

        try (MetadataStore ms = createMetadataStore()) {
            try {
                count = populateMetadataStore(paths, ms);
            } finally {
                clearMetadataStore(ms, count);
            }
        }
    }

    @Test
    public void test_020_Moves() throws Throwable {
        describe("Test workload of batched move() operations");
        // As described in hadoop-aws site docs, count parameter is used for
        // width and depth of directory tree
        int width = getConf().getInt(S3ATestConstants.KEY_DIRECTORY_COUNT, S3ATestConstants.DEFAULT_DIRECTORY_COUNT);
        int depth = width;
        long operations = getConf().getLong(S3ATestConstants.KEY_OPERATION_COUNT, S3ATestConstants.DEFAULT_OPERATION_COUNT);
        List<PathMetadata> origMetas = new ArrayList<>();
        AbstractITestS3AMetadataStoreScale.createDirTree(AbstractITestS3AMetadataStoreScale.BUCKET_ROOT, depth, width, origMetas);
        // Pre-compute source and destination paths for move() loop below
        List<Path> origPaths = metasToPaths(origMetas);
        List<PathMetadata> movedMetas = moveMetas(origMetas, AbstractITestS3AMetadataStoreScale.BUCKET_ROOT, new Path(AbstractITestS3AMetadataStoreScale.BUCKET_ROOT, "moved-here"));
        List<Path> movedPaths = metasToPaths(movedMetas);
        long count = 1;// Some value in case we throw an exception below

        try (MetadataStore ms = createMetadataStore()) {
            try {
                // Setup
                count = populateMetadataStore(origMetas, ms);
                // Main loop: move things back and forth
                describe("Running move workload");
                ContractTestUtils.NanoTimer moveTimer = new ContractTestUtils.NanoTimer();
                AbstractITestS3AMetadataStoreScale.LOG.info("Running {} moves of {} paths each", operations, origMetas.size());
                for (int i = 0; i < operations; i++) {
                    Collection<Path> toDelete;
                    Collection<PathMetadata> toCreate;
                    if ((i % 2) == 0) {
                        toDelete = origPaths;
                        toCreate = movedMetas;
                    } else {
                        toDelete = movedPaths;
                        toCreate = origMetas;
                    }
                    ms.move(toDelete, toCreate);
                }
                moveTimer.end();
                AbstractITestS3AMetadataStoreScale.printTiming(AbstractITestS3AMetadataStoreScale.LOG, "move", moveTimer, operations);
            } finally {
                // Cleanup
                clearMetadataStore(ms, count);
            }
        }
    }
}

