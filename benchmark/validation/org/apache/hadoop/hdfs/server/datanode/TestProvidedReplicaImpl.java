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
package org.apache.hadoop.hdfs.server.datanode;


import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the implementation of {@link ProvidedReplica}.
 */
public class TestProvidedReplicaImpl {
    private static final Logger LOG = LoggerFactory.getLogger(TestProvidedReplicaImpl.class);

    private static final String BASE_DIR = new FileSystemTestHelper().getTestRootDir();

    private static final String FILE_NAME = "provided-test";

    // length of the file that is associated with the provided blocks.
    private static final long FILE_LEN = ((128 * 1024) * 10L) + (64 * 1024);

    // length of each provided block.
    private static final long BLK_LEN = 128 * 1024L;

    private static List<ProvidedReplica> replicas;

    @Test
    public void testProvidedReplicaRead() throws IOException {
        File providedFile = new File(TestProvidedReplicaImpl.BASE_DIR, TestProvidedReplicaImpl.FILE_NAME);
        for (int i = 0; i < (TestProvidedReplicaImpl.replicas.size()); i++) {
            ProvidedReplica replica = TestProvidedReplicaImpl.replicas.get(i);
            // block data should exist!
            Assert.assertTrue(replica.blockDataExists());
            Assert.assertEquals(providedFile.toURI(), replica.getBlockURI());
            TestProvidedReplicaImpl.verifyReplicaContents(providedFile, replica.getDataInputStream(0), ((TestProvidedReplicaImpl.BLK_LEN) * i), replica.getBlockDataLength());
        }
        TestProvidedReplicaImpl.LOG.info("All replica contents verified");
        providedFile.delete();
        // the block data should no longer be found!
        for (int i = 0; i < (TestProvidedReplicaImpl.replicas.size()); i++) {
            ProvidedReplica replica = TestProvidedReplicaImpl.replicas.get(i);
            Assert.assertTrue((!(replica.blockDataExists())));
        }
    }
}

