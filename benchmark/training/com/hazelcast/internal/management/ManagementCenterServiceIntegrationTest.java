/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.management;


import com.hazelcast.client.impl.ClientImpl;
import com.hazelcast.core.Client;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.ParseException;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ManagementCenterServiceIntegrationTest {
    private static final String clusterName = "Session Integration (AWS discovery)";

    private static MancenterMock mancenterMock;

    private static HazelcastInstanceImpl instance;

    @Test
    public void testTimedMemberStateNotNull() {
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                String responseString = doHttpGet("/mancen/memberStateCheck");
                Assert.assertNotNull(responseString);
                Assert.assertNotEquals("", responseString);
                JsonObject object;
                try {
                    object = Json.parse(responseString).asObject();
                } catch (ParseException e) {
                    throw new AssertionError(("Failed to parse JSON: " + responseString));
                }
                TimedMemberState memberState = new TimedMemberState();
                memberState.fromJson(object);
                Assert.assertEquals(ManagementCenterServiceIntegrationTest.clusterName, memberState.getClusterName());
            }
        });
    }

    @Test
    public void testGetTaskUrlEncodes() {
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                String responseString = doHttpGet("/mancen/getClusterName");
                Assert.assertEquals(ManagementCenterServiceIntegrationTest.clusterName, responseString);
            }
        });
    }

    @Test
    public void testClientBwListApplies() {
        ManagementCenterServiceIntegrationTest.mancenterMock.enableClientWhitelist("127.0.0.1");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                String responseString = doHttpGet("/mancen/memberStateCheck");
                Assert.assertNotNull(responseString);
                Assert.assertNotEquals("", responseString);
                String name = HazelcastTestSupport.randomString();
                Set<String> labels = new HashSet<String>();
                Client client1 = new ClientImpl(null, createInetSocketAddress("127.0.0.1"), name, labels);
                Assert.assertTrue(ManagementCenterServiceIntegrationTest.instance.node.clientEngine.isClientAllowed(client1));
                Client client2 = new ClientImpl(null, createInetSocketAddress("127.0.0.2"), name, labels);
                Assert.assertFalse(ManagementCenterServiceIntegrationTest.instance.node.clientEngine.isClientAllowed(client2));
            }
        });
    }
}

