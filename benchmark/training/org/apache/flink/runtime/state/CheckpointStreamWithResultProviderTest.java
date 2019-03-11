/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.state;


import CheckpointedStateScope.EXCLUSIVE;
import java.io.Closeable;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.util.MethodForwardingTestUtil;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static CheckpointStreamWithResultProvider.PrimaryAndSecondaryStream;
import static CheckpointStreamWithResultProvider.PrimaryStreamOnly;


public class CheckpointStreamWithResultProviderTest extends TestLogger {
    private static TemporaryFolder temporaryFolder;

    @Test
    public void testFactory() throws Exception {
        CheckpointStreamFactory primaryFactory = createCheckpointStreamFactory();
        try (CheckpointStreamWithResultProvider primaryOnly = CheckpointStreamWithResultProvider.createSimpleStream(EXCLUSIVE, primaryFactory)) {
            Assert.assertTrue((primaryOnly instanceof CheckpointStreamWithResultProvider.PrimaryStreamOnly));
        }
        LocalRecoveryDirectoryProvider directoryProvider = createLocalRecoveryDirectoryProvider();
        try (CheckpointStreamWithResultProvider primaryAndSecondary = CheckpointStreamWithResultProvider.createDuplicatingStream(42L, EXCLUSIVE, primaryFactory, directoryProvider)) {
            Assert.assertTrue((primaryAndSecondary instanceof CheckpointStreamWithResultProvider.PrimaryAndSecondaryStream));
        }
    }

    @Test
    public void testCloseAndFinalizeCheckpointStreamResultPrimaryOnly() throws Exception {
        CheckpointStreamFactory primaryFactory = createCheckpointStreamFactory();
        CheckpointStreamWithResultProvider resultProvider = CheckpointStreamWithResultProvider.createSimpleStream(EXCLUSIVE, primaryFactory);
        SnapshotResult<StreamStateHandle> result = writeCheckpointTestData(resultProvider);
        Assert.assertNotNull(result.getJobManagerOwnedSnapshot());
        Assert.assertNull(result.getTaskLocalSnapshot());
        try (FSDataInputStream inputStream = result.getJobManagerOwnedSnapshot().openInputStream()) {
            Assert.assertEquals(66, inputStream.read());
            Assert.assertEquals((-1), inputStream.read());
        }
    }

    @Test
    public void testCloseAndFinalizeCheckpointStreamResultPrimaryAndSecondary() throws Exception {
        CheckpointStreamFactory primaryFactory = createCheckpointStreamFactory();
        LocalRecoveryDirectoryProvider directoryProvider = createLocalRecoveryDirectoryProvider();
        CheckpointStreamWithResultProvider resultProvider = CheckpointStreamWithResultProvider.createDuplicatingStream(42L, EXCLUSIVE, primaryFactory, directoryProvider);
        SnapshotResult<StreamStateHandle> result = writeCheckpointTestData(resultProvider);
        Assert.assertNotNull(result.getJobManagerOwnedSnapshot());
        Assert.assertNotNull(result.getTaskLocalSnapshot());
        try (FSDataInputStream inputStream = result.getJobManagerOwnedSnapshot().openInputStream()) {
            Assert.assertEquals(66, inputStream.read());
            Assert.assertEquals((-1), inputStream.read());
        }
        try (FSDataInputStream inputStream = result.getTaskLocalSnapshot().openInputStream()) {
            Assert.assertEquals(66, inputStream.read());
            Assert.assertEquals((-1), inputStream.read());
        }
    }

    @Test
    public void testCompletedAndCloseStateHandling() throws Exception {
        CheckpointStreamFactory primaryFactory = createCheckpointStreamFactory();
        testCloseBeforeComplete(new CheckpointStreamWithResultProvider.PrimaryStreamOnly(primaryFactory.createCheckpointStateOutputStream(EXCLUSIVE)));
        testCompleteBeforeClose(new CheckpointStreamWithResultProvider.PrimaryStreamOnly(primaryFactory.createCheckpointStateOutputStream(EXCLUSIVE)));
        testCloseBeforeComplete(new CheckpointStreamWithResultProvider.PrimaryAndSecondaryStream(primaryFactory.createCheckpointStateOutputStream(EXCLUSIVE), primaryFactory.createCheckpointStateOutputStream(EXCLUSIVE)));
        testCompleteBeforeClose(new CheckpointStreamWithResultProvider.PrimaryAndSecondaryStream(primaryFactory.createCheckpointStateOutputStream(EXCLUSIVE), primaryFactory.createCheckpointStateOutputStream(EXCLUSIVE)));
    }

    @Test
    public void testCloseMethodForwarding() throws Exception {
        CheckpointStreamFactory streamFactory = createCheckpointStreamFactory();
        MethodForwardingTestUtil.testMethodForwarding(Closeable.class, PrimaryStreamOnly::new, () -> {
            try {
                return streamFactory.createCheckpointStateOutputStream(CheckpointedStateScope.EXCLUSIVE);
            } catch ( e) {
                throw new <e>RuntimeException();
            }
        });
        MethodForwardingTestUtil.testMethodForwarding(Closeable.class, PrimaryAndSecondaryStream::new, () -> {
            try {
                return new DuplicatingCheckpointOutputStream(streamFactory.createCheckpointStateOutputStream(CheckpointedStateScope.EXCLUSIVE), streamFactory.createCheckpointStateOutputStream(CheckpointedStateScope.EXCLUSIVE));
            } catch ( e) {
                throw new <e>RuntimeException();
            }
        });
    }
}

