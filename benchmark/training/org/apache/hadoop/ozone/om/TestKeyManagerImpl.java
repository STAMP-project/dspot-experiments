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
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.container.common.helpers.ExcludeList;
import org.apache.hadoop.hdds.scm.protocol.ScmBlockLocationProtocol;
import org.apache.hadoop.ozone.om.exceptions.OMException;
import org.apache.hadoop.ozone.om.helpers.OmBucketInfo;
import org.apache.hadoop.ozone.om.helpers.OmKeyArgs;
import org.apache.hadoop.ozone.om.helpers.OmKeyInfo;
import org.apache.hadoop.ozone.om.helpers.OmVolumeArgs;
import org.apache.hadoop.ozone.protocol.proto.OzoneManagerProtocolProtos.KeyInfo;
import org.apache.hadoop.test.LambdaTestUtils;
import org.apache.hadoop.utils.db.RDBStore;
import org.apache.hadoop.utils.db.Table;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.rocksdb.DBOptions;


/**
 * Test class for @{@link KeyManagerImpl}.
 */
public class TestKeyManagerImpl {
    private static KeyManagerImpl keyManager;

    private static ScmBlockLocationProtocol scmBlockLocationProtocol;

    private static OzoneConfiguration conf;

    private static OMMetadataManager metadataManager;

    private static long blockSize = 1000;

    private static final String KEY_NAME = "key1";

    private static final String BUCKET_NAME = "bucket1";

    private static final String VOLUME_NAME = "vol1";

    private static RDBStore rdbStore = null;

    private static Table<String, OmKeyInfo> keyTable = null;

    private static Table<String, OmBucketInfo> bucketTable = null;

    private static Table<String, OmVolumeArgs> volumeTable = null;

    private static DBOptions options = null;

    private KeyInfo keyData;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void allocateBlockFailureInChillMode() throws Exception {
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setKeyName(TestKeyManagerImpl.KEY_NAME).setBucketName(TestKeyManagerImpl.BUCKET_NAME).setFactor(ONE).setType(STAND_ALONE).setVolumeName(TestKeyManagerImpl.VOLUME_NAME).build();
        LambdaTestUtils.intercept(OMException.class, "ChillModePrecheck failed for allocateBlock", () -> {
            TestKeyManagerImpl.keyManager.allocateBlock(keyArgs, 1, new ExcludeList());
        });
    }

    @Test
    public void openKeyFailureInChillMode() throws Exception {
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setKeyName(TestKeyManagerImpl.KEY_NAME).setBucketName(TestKeyManagerImpl.BUCKET_NAME).setFactor(ONE).setDataSize(1000).setType(STAND_ALONE).setVolumeName(TestKeyManagerImpl.VOLUME_NAME).build();
        LambdaTestUtils.intercept(OMException.class, "ChillModePrecheck failed for allocateBlock", () -> {
            TestKeyManagerImpl.keyManager.openKey(keyArgs);
        });
    }
}

