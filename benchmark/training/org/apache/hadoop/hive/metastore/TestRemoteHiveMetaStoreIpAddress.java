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
package org.apache.hadoop.hive.metastore;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TestRemoteHiveMetaStoreIpAddress.
 *
 * Test which checks that the remote Hive metastore stores the proper IP address using
 * IpAddressListener
 */
@Category(MetastoreCheckinTest.class)
public class TestRemoteHiveMetaStoreIpAddress {
    private static final Logger LOG = LoggerFactory.getLogger(TestRemoteHiveMetaStoreIpAddress.class);

    private static Configuration conf;

    private static HiveMetaStoreClient msc;

    @Test
    public void testIpAddress() throws Exception {
        Database db = new DatabaseBuilder().setName("testIpAddressIp").create(TestRemoteHiveMetaStoreIpAddress.msc, TestRemoteHiveMetaStoreIpAddress.conf);
        TestRemoteHiveMetaStoreIpAddress.msc.dropDatabase(db.getName());
    }
}

