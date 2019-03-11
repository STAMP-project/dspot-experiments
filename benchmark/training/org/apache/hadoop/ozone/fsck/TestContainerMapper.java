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
package org.apache.hadoop.ozone.fsck;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.protocolPB.StorageContainerLocationProtocolClientSideTranslatorPB;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.om.OzoneManager;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for ContainerMapper.
 */
public class TestContainerMapper {
    private static MiniOzoneCluster cluster = null;

    private static OzoneClient ozClient = null;

    private static ObjectStore store = null;

    private static OzoneManager ozoneManager;

    private static StorageContainerLocationProtocolClientSideTranslatorPB storageContainerLocationClient;

    private static final String SCM_ID = UUID.randomUUID().toString();

    private static String volName = UUID.randomUUID().toString();

    private static String bucketName = UUID.randomUUID().toString();

    private static OzoneConfiguration conf;

    private static List<String> keyList = new ArrayList<>();

    private static String dbPath;

    @Test
    public void testContainerMapper() throws Exception {
        ContainerMapper containerMapper = new ContainerMapper();
        Map<Long, List<Map<Long, BlockIdDetails>>> dataMap = containerMapper.parseOmDB(TestContainerMapper.conf);
        // As we have created 20 keys with 10 MB size, and each
        // container max size is 100 MB, it should create 3 containers because
        // containers are closing before reaching the threshold
        Assert.assertEquals(3, dataMap.size());
    }
}

