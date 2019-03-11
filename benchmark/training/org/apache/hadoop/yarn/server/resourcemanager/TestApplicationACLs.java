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
package org.apache.hadoop.yarn.server.resourcemanager;


import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestApplicationACLs extends ParameterizedSchedulerTestBase {
    private static final String APP_OWNER = "owner";

    private static final String FRIEND = "friend";

    private static final String ENEMY = "enemy";

    private static final String QUEUE_ADMIN_USER = "queue-admin-user";

    private static final String SUPER_USER = "superUser";

    private static final String FRIENDLY_GROUP = "friendly-group";

    private static final String SUPER_GROUP = "superGroup";

    private static final String UNAVAILABLE = "N/A";

    private static final Logger LOG = LoggerFactory.getLogger(TestApplicationACLs.class);

    private MockRM resourceManager;

    private Configuration conf;

    private YarnRPC rpc;

    private InetSocketAddress rmAddress;

    private ApplicationClientProtocol rmClient;

    private RecordFactory recordFactory;

    private boolean isQueueUser;

    public TestApplicationACLs(ParameterizedSchedulerTestBase.SchedulerType type) throws IOException {
        super(type);
    }

    @Test
    public void testApplicationACLs() throws Exception {
        verifyOwnerAccess();
        verifySuperUserAccess();
        verifyFriendAccess();
        verifyEnemyAccess();
        verifyAdministerQueueUserAccess();
        verifyInvalidQueueWithAcl();
    }
}

