/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.om;


import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test Key Deleting Service.
 * <p>
 * This test does the following things.
 * <p>
 * 1. Creates a bunch of keys. 2. Then executes delete key directly using
 * Metadata Manager. 3. Waits for a while for the KeyDeleting Service to pick up
 * and call into SCM. 4. Confirms that calls have been successful.
 */
public class TestKeyDeletingService {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * In this test, we create a bunch of keys and delete them. Then we start the
     * KeyDeletingService and pass a SCMClient which does not fail. We make sure
     * that all the keys that we deleted is picked up and deleted by
     * OzoneManager.
     *
     * @throws IOException
     * 		- on Failure.
     */
    @Test(timeout = 30000)
    public void checkIfDeleteServiceisDeletingKeys() throws IOException, InterruptedException, TimeoutException {
        OzoneConfiguration conf = createConfAndInitValues();
        OmMetadataManagerImpl metaMgr = new OmMetadataManagerImpl(conf);
        KeyManager keyManager = new KeyManagerImpl(new ScmBlockLocationTestIngClient(null, null, 0), metaMgr, conf, UUID.randomUUID().toString(), null);
        final int keyCount = 100;
        createAndDeleteKeys(keyManager, keyCount, 1);
        KeyDeletingService keyDeletingService = ((KeyDeletingService) (keyManager.getDeletingService()));
        GenericTestUtils.waitFor(() -> (keyDeletingService.getDeletedKeyCount().get()) >= keyCount, 1000, 10000);
        Assert.assertTrue(((keyDeletingService.getRunCount().get()) > 1));
        Assert.assertEquals(keyManager.getPendingDeletionKeys(Integer.MAX_VALUE).size(), 0);
    }

    @Test(timeout = 30000)
    public void checkIfDeleteServiceWithFailingSCM() throws IOException, InterruptedException, TimeoutException {
        OzoneConfiguration conf = createConfAndInitValues();
        OmMetadataManagerImpl metaMgr = new OmMetadataManagerImpl(conf);
        // failCallsFrequency = 1 , means all calls fail.
        KeyManager keyManager = new KeyManagerImpl(new ScmBlockLocationTestIngClient(null, null, 1), metaMgr, conf, UUID.randomUUID().toString(), null);
        final int keyCount = 100;
        createAndDeleteKeys(keyManager, keyCount, 1);
        KeyDeletingService keyDeletingService = ((KeyDeletingService) (keyManager.getDeletingService()));
        keyManager.start(conf);
        Assert.assertEquals(keyManager.getPendingDeletionKeys(Integer.MAX_VALUE).size(), keyCount);
        // Make sure that we have run the background thread 5 times more
        GenericTestUtils.waitFor(() -> (keyDeletingService.getRunCount().get()) >= 5, 100, 1000);
        // Since SCM calls are failing, deletedKeyCount should be zero.
        Assert.assertEquals(keyDeletingService.getDeletedKeyCount().get(), 0);
        Assert.assertEquals(keyManager.getPendingDeletionKeys(Integer.MAX_VALUE).size(), keyCount);
    }

    @Test(timeout = 30000)
    public void checkDeletionForEmptyKey() throws IOException, InterruptedException, TimeoutException {
        OzoneConfiguration conf = createConfAndInitValues();
        OmMetadataManagerImpl metaMgr = new OmMetadataManagerImpl(conf);
        // failCallsFrequency = 1 , means all calls fail.
        KeyManager keyManager = new KeyManagerImpl(new ScmBlockLocationTestIngClient(null, null, 1), metaMgr, conf, UUID.randomUUID().toString(), null);
        final int keyCount = 100;
        createAndDeleteKeys(keyManager, keyCount, 0);
        KeyDeletingService keyDeletingService = ((KeyDeletingService) (keyManager.getDeletingService()));
        keyManager.start(conf);
        // Since empty keys are directly deleted from db there should be no
        // pending deletion keys. Also deletedKeyCount should be zero.
        Assert.assertEquals(keyManager.getPendingDeletionKeys(Integer.MAX_VALUE).size(), 0);
        // Make sure that we have run the background thread 2 times or more
        GenericTestUtils.waitFor(() -> (keyDeletingService.getRunCount().get()) >= 2, 100, 1000);
        Assert.assertEquals(keyDeletingService.getDeletedKeyCount().get(), 0);
    }
}

