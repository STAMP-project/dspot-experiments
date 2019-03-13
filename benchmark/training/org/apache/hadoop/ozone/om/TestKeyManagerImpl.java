/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.??See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.??The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.??You may obtain a copy of the License at
 *
 * ???? http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.om;


import ReplicationFactor.ONE;
import ReplicationType.STAND_ALONE;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.container.common.helpers.ExcludeList;
import org.apache.hadoop.hdds.scm.protocol.ScmBlockLocationProtocol;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.ozone.om.exceptions.OMException;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for @{@link KeyManagerImpl}.
 */
public class TestKeyManagerImpl {
    private static KeyManagerImpl keyManager;

    private static VolumeManagerImpl volumeManager;

    private static BucketManagerImpl bucketManager;

    private static StorageContainerManager scm;

    private static ScmBlockLocationProtocol mockScmBlockLocationProtocol;

    private static OzoneConfiguration conf;

    private static OMMetadataManager metadataManager;

    private static File dir;

    private static long scmBlockSize;

    private static final String KEY_NAME = "key1";

    private static final String BUCKET_NAME = "bucket1";

    private static final String VOLUME_NAME = "vol1";

    @Test
    public void allocateBlockFailureInChillMode() throws Exception {
        KeyManager keyManager1 = new KeyManagerImpl(TestKeyManagerImpl.mockScmBlockLocationProtocol, TestKeyManagerImpl.metadataManager, TestKeyManagerImpl.conf, "om1", null);
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setKeyName(TestKeyManagerImpl.KEY_NAME).setBucketName(TestKeyManagerImpl.BUCKET_NAME).setFactor(ONE).setType(STAND_ALONE).setVolumeName(TestKeyManagerImpl.VOLUME_NAME).build();
        OpenKeySession keySession = keyManager1.openKey(keyArgs);
        LambdaTestUtils.intercept(OMException.class, "ChillModePrecheck failed for allocateBlock", () -> {
            keyManager1.allocateBlock(keyArgs, keySession.getId(), new ExcludeList());
        });
    }

    @Test
    public void openKeyFailureInChillMode() throws Exception {
        KeyManager keyManager1 = new KeyManagerImpl(TestKeyManagerImpl.mockScmBlockLocationProtocol, TestKeyManagerImpl.metadataManager, TestKeyManagerImpl.conf, "om1", null);
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setKeyName(TestKeyManagerImpl.KEY_NAME).setBucketName(TestKeyManagerImpl.BUCKET_NAME).setFactor(ONE).setDataSize(1000).setType(STAND_ALONE).setVolumeName(TestKeyManagerImpl.VOLUME_NAME).build();
        LambdaTestUtils.intercept(OMException.class, "ChillModePrecheck failed for allocateBlock", () -> {
            keyManager1.openKey(keyArgs);
        });
    }

    @Test
    public void openKeyWithMultipleBlocks() throws IOException {
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setKeyName(UUID.randomUUID().toString()).setBucketName(TestKeyManagerImpl.BUCKET_NAME).setFactor(ONE).setDataSize(((TestKeyManagerImpl.scmBlockSize) * 10)).setType(STAND_ALONE).setVolumeName(TestKeyManagerImpl.VOLUME_NAME).build();
        OpenKeySession keySession = TestKeyManagerImpl.keyManager.openKey(keyArgs);
        OmKeyInfo keyInfo = keySession.getKeyInfo();
        Assert.assertEquals(10, keyInfo.getLatestVersionLocations().getLocationList().size());
    }
}

