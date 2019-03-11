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
package org.apache.hive.service.cli.thrift;


import Service.STATE.STARTED;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.apache.hive.service.cli.CLIServiceClient;
import org.apache.hive.service.cli.SessionHandle;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TestMiniHS2StateWithNoZookeeper.
 * This tests HS2 shutdown is not triggered by CloseSession operation
 * while HS2 has never been registered with ZooKeeper.
 */
public class TestMiniHS2StateWithNoZookeeper {
    private static final Logger LOG = LoggerFactory.getLogger(TestMiniHS2StateWithNoZookeeper.class);

    private static MiniHS2 miniHS2 = null;

    private static HiveConf hiveConf = null;

    @Test
    public void openSessionAndClose() throws Exception {
        CLIServiceClient client = TestMiniHS2StateWithNoZookeeper.miniHS2.getServiceClient();
        SessionHandle sessionHandle = client.openSession(null, null, null);
        client.closeSession(sessionHandle);
        Thread.sleep(100);
        Assert.assertEquals(STARTED, TestMiniHS2StateWithNoZookeeper.miniHS2.getState());
    }
}

