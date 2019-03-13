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
package org.apache.hadoop.ozone.container.common.statemachine.commandhandler;


import ReplicationFactor.ONE;
import ReplicationType.STAND_ALONE;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.OzoneTestUtils;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneVolume;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.apache.hadoop.ozone.om.OzoneManager;
import org.apache.hadoop.ozone.om.helpers.OmKeyArgs;
import org.apache.hadoop.ozone.om.helpers.OmKeyLocationInfoGroup;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for Block deletion.
 */
public class TestBlockDeletion {
    private static OzoneConfiguration conf = null;

    private static ObjectStore store;

    private static MiniOzoneCluster cluster = null;

    private static StorageContainerManager scm = null;

    private static OzoneManager om = null;

    private static Set<Long> containerIdsWithDeletedBlocks;

    private static long maxTransactionId = 0;

    @Test
    public void testBlockDeletion() throws Exception {
        String volumeName = UUID.randomUUID().toString();
        String bucketName = UUID.randomUUID().toString();
        String value = RandomStringUtils.random(10000000);
        TestBlockDeletion.store.createVolume(volumeName);
        OzoneVolume volume = TestBlockDeletion.store.getVolume(volumeName);
        volume.createBucket(bucketName);
        OzoneBucket bucket = volume.getBucket(bucketName);
        String keyName = UUID.randomUUID().toString();
        OzoneOutputStream out = bucket.createKey(keyName, value.getBytes().length, STAND_ALONE, ONE, new HashMap());
        for (int i = 0; i < 100; i++) {
            out.write(value.getBytes());
        }
        out.close();
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName(volumeName).setBucketName(bucketName).setKeyName(keyName).setDataSize(0).setType(HddsProtos.ReplicationType.STAND_ALONE).setFactor(HddsProtos.ReplicationFactor.ONE).build();
        List<OmKeyLocationInfoGroup> omKeyLocationInfoGroupList = TestBlockDeletion.om.lookupKey(keyArgs).getKeyLocationVersions();
        // verify key blocks were created in DN.
        verifyBlocksCreated(omKeyLocationInfoGroupList);
        // No containers with deleted blocks
        Assert.assertTrue(TestBlockDeletion.containerIdsWithDeletedBlocks.isEmpty());
        // Delete transactionIds for the containers should be 0.
        // NOTE: this test assumes that all the container is KetValueContainer. If
        // other container types is going to be added, this test should be checked.
        matchContainerTransactionIds();
        TestBlockDeletion.om.deleteKey(keyArgs);
        Thread.sleep(5000);
        // The blocks should not be deleted in the DN as the container is open
        try {
            verifyBlocksDeleted(omKeyLocationInfoGroupList);
            Assert.fail("Blocks should not have been deleted");
        } catch (Throwable e) {
            Assert.assertTrue(e.getMessage().contains("expected null, but was"));
            Assert.assertEquals(e.getClass(), AssertionError.class);
        }
        // close the containers which hold the blocks for the key
        OzoneTestUtils.closeContainers(omKeyLocationInfoGroupList, TestBlockDeletion.scm);
        waitForDatanodeCommandRetry();
        waitForDatanodeBlockDeletionStart();
        // The blocks should be deleted in the DN.
        verifyBlocksDeleted(omKeyLocationInfoGroupList);
        // Few containers with deleted blocks
        Assert.assertTrue((!(TestBlockDeletion.containerIdsWithDeletedBlocks.isEmpty())));
        // Containers in the DN and SCM should have same delete transactionIds
        matchContainerTransactionIds();
        // Containers in the DN and SCM should have same delete transactionIds
        // after DN restart. The assertion is just to verify that the state of
        // containerInfos in dn and scm is consistent after dn restart.
        TestBlockDeletion.cluster.restartHddsDatanode(0, true);
        matchContainerTransactionIds();
        // verify PENDING_DELETE_STATUS event is fired
        verifyPendingDeleteEvent();
        // Verify transactions committed
        verifyTransactionsCommitted();
    }
}

