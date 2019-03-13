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
package com.twitter.distributedlog.metadata;


import com.twitter.distributedlog.ZooKeeperClusterTestCase;
import java.io.IOException;
import java.net.URI;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;

import static DLMetadata.BK_DL_TYPE;


public class TestDLMetadata extends ZooKeeperClusterTestCase {
    private static final BKDLConfig bkdlConfig = new BKDLConfig("127.0.0.1:7000", "127.0.0.1:7000", "127.0.0.1:7000", "127.0.0.1:7000", "ledgers");

    private static final BKDLConfig bkdlConfig2 = new BKDLConfig("127.0.0.1:7001", "127.0.0.1:7002", "127.0.0.1:7003", "127.0.0.1:7004", "ledgers2");

    private ZooKeeper zkc;

    @Test(timeout = 60000)
    public void testBadMetadata() throws Exception {
        URI uri = createURI("/");
        try {
            DLMetadata.deserialize(uri, new byte[0]);
            Assert.fail("Should fail to deserialize invalid metadata");
        } catch (IOException ie) {
            // expected
        }
        try {
            DLMetadata.deserialize(uri, serialize());
            Assert.fail("Should fail to deserialize due to unknown dl type.");
        } catch (IOException ie) {
            // expected
        }
        try {
            DLMetadata.deserialize(uri, serialize());
            Assert.fail("Should fail to deserialize due to invalid version.");
        } catch (IOException ie) {
            // expected
        }
        byte[] data = new DLMetadata(BK_DL_TYPE, TestDLMetadata.bkdlConfig).serialize();
        // truncate data
        byte[] badData = new byte[(data.length) - 3];
        System.arraycopy(data, 0, badData, 0, badData.length);
        try {
            DLMetadata.deserialize(uri, badData);
            Assert.fail("Should fail to deserialize truncated data.");
        } catch (IOException ie) {
            // expected
        }
    }

    @Test(timeout = 60000)
    public void testGoodMetadata() throws Exception {
        URI uri = createURI("/");
        byte[] data = new DLMetadata(BK_DL_TYPE, TestDLMetadata.bkdlConfig).serialize();
        DLMetadata deserailized = DLMetadata.deserialize(uri, data);
        Assert.assertEquals(TestDLMetadata.bkdlConfig, deserailized.getDLConfig());
    }

    @Test(timeout = 60000)
    public void testWriteMetadata() throws Exception {
        DLMetadata metadata = new DLMetadata(BK_DL_TYPE, TestDLMetadata.bkdlConfig);
        try {
            metadata.create(createURI("//metadata"));
            Assert.fail("Should fail due to invalid uri.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        URI uri = createURI("/metadata");
        metadata.create(uri);
        // create on existed path
        try {
            metadata.create(uri);
            Assert.fail("Should fail when create on existed path");
        } catch (IOException e) {
            // expected
        }
        // update on unexisted path
        try {
            metadata.update(createURI("/unexisted"));
            Assert.fail("Should fail when update on unexisted path");
        } catch (IOException e) {
            // expected
        }
        byte[] data = zkc.getData("/metadata", false, new Stat());
        Assert.assertEquals(TestDLMetadata.bkdlConfig, DLMetadata.deserialize(uri, data).getDLConfig());
        // update on existed path
        DLMetadata newMetadata = new DLMetadata(BK_DL_TYPE, TestDLMetadata.bkdlConfig2);
        newMetadata.update(createURI("/metadata"));
        byte[] newData = zkc.getData("/metadata", false, new Stat());
        Assert.assertEquals(TestDLMetadata.bkdlConfig2, DLMetadata.deserialize(uri, newData).getDLConfig());
    }

    // Missing dlZkServersForWriter, dlZkServersForReader default to configured server.
    @Test(timeout = 60000)
    public void testMetadataWithoutDLZKServers() throws Exception {
        testMetadataWithOrWithoutZkServers("/metadata-without-dlzk-servers", null, null, "127.0.0.1:7003", "127.0.0.1:7004", ("127.0.0.1:" + (ZooKeeperClusterTestCase.zkPort)), ("127.0.0.1:" + (ZooKeeperClusterTestCase.zkPort)), "127.0.0.1:7003", "127.0.0.1:7004");
    }

    @Test(timeout = 60000)
    public void testMetadataWithoutDLZKServersForRead() throws Exception {
        testMetadataWithOrWithoutZkServers("/metadata-without-dlzk-servers-for-read", "127.0.0.1:7001", null, "127.0.0.1:7003", "127.0.0.1:7004", "127.0.0.1:7001", "127.0.0.1:7001", "127.0.0.1:7003", "127.0.0.1:7004");
    }

    @Test(timeout = 60000)
    public void testMetadataWithoutBKZKServersForRead() throws Exception {
        testMetadataWithOrWithoutZkServers("/metadata-without-bkzk-servers-for-read", "127.0.0.1:7001", null, "127.0.0.1:7003", null, "127.0.0.1:7001", "127.0.0.1:7001", "127.0.0.1:7003", "127.0.0.1:7003");
    }

    @Test(timeout = 60000)
    public void testMetadataMissingRequiredFields() throws Exception {
        BKDLConfig bkdlConfig = new BKDLConfig(null, null, null, null, "ledgers");
        URI uri = createURI("/metadata-missing-fields");
        DLMetadata metadata = new DLMetadata(BK_DL_TYPE, bkdlConfig);
        metadata.create(uri);
        // read serialized metadata
        byte[] data = zkc.getData("/metadata-missing-fields", false, new Stat());
        try {
            DLMetadata.deserialize(uri, data);
            Assert.fail("Should fail on deserializing metadata missing fields");
        } catch (IOException ioe) {
            // expected
        }
    }
}

