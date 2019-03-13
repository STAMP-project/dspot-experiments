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
package com.hazelcast.internal.ascii;


import CPGroupInfo.DEFAULT_GROUP_NAME;
import CPGroupStatus.ACTIVE;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.session.CPSession;
import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class RestCPSubsystemTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Config config = new Config();

    private String groupName = config.getGroupConfig().getName();

    private String groupPassword = config.getGroupConfig().getPassword();

    @Test
    public void test_getCPGroupIds() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        instance1.getCPSubsystem().getAtomicLong("long1").set(5);
        instance1.getCPSubsystem().getAtomicLong("long1@custom").set(5);
        HTTPCommunicator communicator = new HTTPCommunicator(instance1);
        HTTPCommunicator.ConnectionResponse response = communicator.getCPGroupIds();
        Assert.assertEquals(200, response.responseCode);
        JsonArray responseArr = ((JsonArray) (Json.parse(response.response)));
        boolean metadataCPGroupExists = false;
        boolean defaultCPGroupExists = false;
        boolean customCPGroupExists = false;
        for (JsonValue val : responseArr) {
            JsonObject obj = ((JsonObject) (val));
            String name = obj.getString("name", "");
            if (DEFAULT_GROUP_NAME.equals(name)) {
                defaultCPGroupExists = true;
            } else
                if (CPGroup.METADATA_CP_GROUP_NAME.equals(name)) {
                    metadataCPGroupExists = true;
                } else
                    if ("custom".equals(name)) {
                        customCPGroupExists = true;
                    }


        }
        Assert.assertTrue(metadataCPGroupExists);
        Assert.assertTrue(defaultCPGroupExists);
        Assert.assertTrue(customCPGroupExists);
    }

    @Test
    public void test_getMetadataCPGroupByName() throws IOException {
        final HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(instance1.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance2.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance3.getCPSubsystem().getLocalCPMember());
            }
        });
        HTTPCommunicator communicator = new HTTPCommunicator(instance1);
        HTTPCommunicator.ConnectionResponse response = communicator.getCPGroupByName(CPGroup.METADATA_CP_GROUP_NAME);
        Assert.assertEquals(200, response.responseCode);
        CPMember cpMember1 = instance1.getCPSubsystem().getLocalCPMember();
        CPMember cpMember2 = instance2.getCPSubsystem().getLocalCPMember();
        CPMember cpMember3 = instance3.getCPSubsystem().getLocalCPMember();
        boolean cpMember1Found = false;
        boolean cpMember2Found = false;
        boolean cpMember3Found = false;
        JsonObject json = ((JsonObject) (Json.parse(response.response)));
        Assert.assertEquals(CPGroup.METADATA_CP_GROUP_NAME, getString("name", ""));
        Assert.assertEquals(ACTIVE.name(), json.getString("status", ""));
        for (JsonValue val : ((JsonArray) (json.get("members")))) {
            JsonObject mem = ((JsonObject) (val));
            cpMember1Found |= cpMember1.getUuid().equals(mem.getString("uuid", ""));
            cpMember2Found |= cpMember2.getUuid().equals(mem.getString("uuid", ""));
            cpMember3Found |= cpMember3.getUuid().equals(mem.getString("uuid", ""));
        }
        Assert.assertTrue(cpMember1Found);
        Assert.assertTrue(cpMember2Found);
        Assert.assertTrue(cpMember3Found);
    }

    @Test
    public void test_getDefaultCPGroupByName() throws IOException {
        final HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(instance1.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance2.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance3.getCPSubsystem().getLocalCPMember());
            }
        });
        instance1.getCPSubsystem().getAtomicLong("long1").set(5);
        HTTPCommunicator communicator = new HTTPCommunicator(instance1);
        HTTPCommunicator.ConnectionResponse response = communicator.getCPGroupByName(CPGroup.DEFAULT_GROUP_NAME);
        Assert.assertEquals(200, response.responseCode);
        CPMember cpMember1 = instance1.getCPSubsystem().getLocalCPMember();
        CPMember cpMember2 = instance2.getCPSubsystem().getLocalCPMember();
        CPMember cpMember3 = instance3.getCPSubsystem().getLocalCPMember();
        boolean cpMember1Found = false;
        boolean cpMember2Found = false;
        boolean cpMember3Found = false;
        JsonObject json = ((JsonObject) (Json.parse(response.response)));
        Assert.assertEquals(CPGroup.DEFAULT_GROUP_NAME, getString("name", ""));
        Assert.assertEquals(ACTIVE.name(), json.getString("status", ""));
        for (JsonValue val : ((JsonArray) (json.get("members")))) {
            JsonObject mem = ((JsonObject) (val));
            cpMember1Found |= cpMember1.getUuid().equals(mem.getString("uuid", ""));
            cpMember2Found |= cpMember2.getUuid().equals(mem.getString("uuid", ""));
            cpMember3Found |= cpMember3.getUuid().equals(mem.getString("uuid", ""));
        }
        Assert.assertTrue(cpMember1Found);
        Assert.assertTrue(cpMember2Found);
        Assert.assertTrue(cpMember3Found);
    }

    @Test
    public void test_getCustomCPGroupByName() throws IOException {
        final HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(instance1.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance2.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance3.getCPSubsystem().getLocalCPMember());
            }
        });
        instance1.getCPSubsystem().getAtomicLong("long1@custom").set(5);
        HTTPCommunicator communicator = new HTTPCommunicator(instance1);
        HTTPCommunicator.ConnectionResponse response = communicator.getCPGroupByName("custom");
        Assert.assertEquals(200, response.responseCode);
        CPMember cpMember1 = instance1.getCPSubsystem().getLocalCPMember();
        CPMember cpMember2 = instance2.getCPSubsystem().getLocalCPMember();
        CPMember cpMember3 = instance3.getCPSubsystem().getLocalCPMember();
        boolean cpMember1Found = false;
        boolean cpMember2Found = false;
        boolean cpMember3Found = false;
        JsonObject json = ((JsonObject) (Json.parse(response.response)));
        Assert.assertEquals("custom", getString("name", ""));
        Assert.assertEquals(ACTIVE.name(), json.getString("status", ""));
        for (JsonValue val : ((JsonArray) (json.get("members")))) {
            JsonObject mem = ((JsonObject) (val));
            cpMember1Found |= cpMember1.getUuid().equals(mem.getString("uuid", ""));
            cpMember2Found |= cpMember2.getUuid().equals(mem.getString("uuid", ""));
            cpMember3Found |= cpMember3.getUuid().equals(mem.getString("uuid", ""));
        }
        Assert.assertTrue(cpMember1Found);
        Assert.assertTrue(cpMember2Found);
        Assert.assertTrue(cpMember3Found);
    }

    @Test
    public void test_getCPGroupByInvalidName() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        HTTPCommunicator communicator = new HTTPCommunicator(instance1);
        HTTPCommunicator.ConnectionResponse response = communicator.getCPGroupByName("custom");
        Assert.assertEquals(404, response.responseCode);
    }

    @Test
    public void test_getLocalCPMember() throws IOException {
        final HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance4 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(instance1.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance2.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance3.getCPSubsystem().getLocalCPMember());
            }
        });
        HTTPCommunicator.ConnectionResponse response1 = new HTTPCommunicator(instance1).getLocalCPMember();
        HTTPCommunicator.ConnectionResponse response2 = new HTTPCommunicator(instance2).getLocalCPMember();
        HTTPCommunicator.ConnectionResponse response3 = new HTTPCommunicator(instance3).getLocalCPMember();
        HTTPCommunicator.ConnectionResponse response4 = new HTTPCommunicator(instance4).getLocalCPMember();
        Assert.assertEquals(200, response1.responseCode);
        Assert.assertEquals(200, response2.responseCode);
        Assert.assertEquals(200, response3.responseCode);
        Assert.assertEquals(404, response4.responseCode);
        CPMember cpMember1 = instance1.getCPSubsystem().getLocalCPMember();
        CPMember cpMember2 = instance2.getCPSubsystem().getLocalCPMember();
        CPMember cpMember3 = instance3.getCPSubsystem().getLocalCPMember();
        Assert.assertEquals(cpMember1.getUuid(), getString("uuid", ""));
        Assert.assertEquals(cpMember2.getUuid(), getString("uuid", ""));
        Assert.assertEquals(cpMember3.getUuid(), getString("uuid", ""));
    }

    @Test
    public void test_getCPMembers() throws IOException {
        final HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(instance1.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance2.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance3.getCPSubsystem().getLocalCPMember());
            }
        });
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).getCPMembers();
        CPMember cpMember1 = instance1.getCPSubsystem().getLocalCPMember();
        CPMember cpMember2 = instance2.getCPSubsystem().getLocalCPMember();
        CPMember cpMember3 = instance3.getCPSubsystem().getLocalCPMember();
        boolean cpMember1Found = false;
        boolean cpMember2Found = false;
        boolean cpMember3Found = false;
        JsonArray arr = ((JsonArray) (Json.parse(response.response)));
        for (JsonValue val : arr) {
            JsonObject mem = ((JsonObject) (val));
            cpMember1Found |= cpMember1.getUuid().equals(mem.getString("uuid", ""));
            cpMember2Found |= cpMember2.getUuid().equals(mem.getString("uuid", ""));
            cpMember3Found |= cpMember3.getUuid().equals(mem.getString("uuid", ""));
        }
        Assert.assertTrue(cpMember1Found);
        Assert.assertTrue(cpMember2Found);
        Assert.assertTrue(cpMember3Found);
    }

    @Test
    public void test_forceDestroyDefaultCPGroup() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        IAtomicLong long1 = instance1.getCPSubsystem().getAtomicLong("long1");
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).forceDestroyCPGroup(CPGroup.DEFAULT_GROUP_NAME, groupName, groupPassword);
        Assert.assertEquals(200, response.responseCode);
        exception.expect(CPGroupDestroyedException.class);
        long1.set(5);
    }

    @Test
    public void test_forceDestroyCPGroup_withInvalidCredentials() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        instance1.getCPSubsystem().getAtomicLong("long1");
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).forceDestroyCPGroup(CPGroup.DEFAULT_GROUP_NAME, "x", "x");
        Assert.assertEquals(403, response.responseCode);
    }

    @Test
    public void test_forceDestroyMETADATACPGroup() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).forceDestroyCPGroup(CPGroup.METADATA_CP_GROUP_NAME, groupName, groupPassword);
        Assert.assertEquals(400, response.responseCode);
    }

    @Test
    public void test_forceDestroyInvalidCPGroup() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).forceDestroyCPGroup("custom", groupName, groupPassword);
        Assert.assertEquals(400, response.responseCode);
    }

    @Test
    public void test_removeCPMember() throws IOException {
        final HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(instance3.getCPSubsystem().getLocalCPMember());
            }
        });
        CPMember crashedCPMember = instance3.getCPSubsystem().getLocalCPMember();
        instance3.getLifecycleService().terminate();
        HazelcastTestSupport.assertClusterSizeEventually(2, instance1, instance2);
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).removeCPMember(crashedCPMember.getUuid(), groupName, groupPassword);
        Assert.assertEquals(200, response.responseCode);
    }

    @Test
    public void test_removeCPMember_withInvalidCredentials() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(instance3.getCPSubsystem().getLocalCPMember());
            }
        });
        CPMember crashedCPMember = instance3.getCPSubsystem().getLocalCPMember();
        instance3.getLifecycleService().terminate();
        HazelcastTestSupport.assertClusterSizeEventually(2, instance1, instance2);
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).removeCPMember(crashedCPMember.getUuid(), "x", "x");
        Assert.assertEquals(403, response.responseCode);
    }

    @Test
    public void test_removeInvalidCPMember() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).removeCPMember("invalid_uid", groupName, groupPassword);
        Assert.assertEquals(400, response.responseCode);
    }

    @Test
    public void test_removeCPMemberFromNonMaster() throws IOException {
        Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(instance2.getCPSubsystem().getLocalCPMember());
            }
        });
        CPMember crashedCPMember = instance2.getCPSubsystem().getLocalCPMember();
        instance2.getLifecycleService().terminate();
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance3).removeCPMember(crashedCPMember.getUuid(), groupName, groupPassword);
        Assert.assertEquals(200, response.responseCode);
    }

    @Test
    public void test_promoteAPMemberToCPMember() throws InterruptedException, ExecutionException {
        final HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        final HazelcastInstance instance4 = Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(instance1.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance2.getCPSubsystem().getLocalCPMember());
                Assert.assertNotNull(instance3.getCPSubsystem().getLocalCPMember());
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance4).promoteCPMember(groupName, groupPassword);
                Assert.assertEquals(200, response.responseCode);
            }
        });
        Collection<CPMember> cpMembers = instance1.getCPSubsystem().getCPSubsystemManagementService().getCPMembers().get();
        Assert.assertEquals(4, cpMembers.size());
        Assert.assertNotNull(instance4.getCPSubsystem().getLocalCPMember());
    }

    @Test
    public void test_promoteAPMemberToCPMember_withInvalidCredentials() throws IOException, InterruptedException, ExecutionException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance4 = Hazelcast.newHazelcastInstance(config);
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance4).promoteCPMember("x", "x");
        Assert.assertEquals(403, response.responseCode);
        Collection<CPMember> cpMembers = instance1.getCPSubsystem().getCPSubsystemManagementService().getCPMembers().get();
        Assert.assertEquals(3, cpMembers.size());
    }

    @Test
    public void test_promoteExistingCPMember() throws IOException, InterruptedException, ExecutionException {
        final HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(instance1.getCPSubsystem().getLocalCPMember());
            }
        });
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).promoteCPMember(groupName, groupPassword);
        Assert.assertEquals(200, response.responseCode);
        Collection<CPMember> cpMembers = instance1.getCPSubsystem().getCPSubsystemManagementService().getCPMembers().get();
        Assert.assertEquals(3, cpMembers.size());
    }

    @Test
    public void test_resetAndInit() throws IOException, InterruptedException, ExecutionException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        instance1.getCPSubsystem().getAtomicLong("long1").set(5);
        CPGroup cpGroup1 = instance1.getCPSubsystem().getCPSubsystemManagementService().getCPGroup(CPGroup.DEFAULT_GROUP_NAME).get();
        HazelcastTestSupport.sleepAtLeastMillis(10);
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).restart(groupName, groupPassword);
        Assert.assertEquals(200, response.responseCode);
        instance1.getCPSubsystem().getAtomicLong("long1").set(5);
        CPGroup cpGroup2 = instance1.getCPSubsystem().getCPSubsystemManagementService().getCPGroup(CPGroup.DEFAULT_GROUP_NAME).get();
        RaftGroupId id1 = ((RaftGroupId) (cpGroup1.id()));
        RaftGroupId id2 = ((RaftGroupId) (cpGroup2.id()));
        Assert.assertTrue(((id2.seed()) > (id1.seed())));
    }

    @Test
    public void test_resetAndInit_withInvalidCredentials() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        for (HazelcastInstance instance : Arrays.asList(instance1, instance2, instance3)) {
            HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance).restart("x", "x");
            Assert.assertEquals(403, response.responseCode);
        }
    }

    @Test
    public void test_getCPSessions() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        instance1.getCPSubsystem().getLock("lock1").lock();
        instance1.getCPSubsystem().getLock("lock1").unlock();
        instance2.getCPSubsystem().getLock("lock1").lock();
        instance2.getCPSubsystem().getLock("lock1").unlock();
        instance3.getCPSubsystem().getLock("lock1").lock();
        instance3.getCPSubsystem().getLock("lock1").unlock();
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).getCPSessions(CPGroup.DEFAULT_GROUP_NAME);
        Assert.assertEquals(200, response.responseCode);
        JsonArray sessions = ((JsonArray) (Json.parse(response.response)));
        Assert.assertEquals(3, sessions.size());
    }

    @Test
    public void test_forceCloseValidCPSession() throws IOException, InterruptedException, ExecutionException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        instance1.getCPSubsystem().getLock("lock1").lock();
        instance1.getCPSubsystem().getLock("lock1").unlock();
        Collection<CPSession> sessions1 = instance1.getCPSubsystem().getCPSessionManagementService().getAllSessions(CPGroup.DEFAULT_GROUP_NAME).get();
        Assert.assertEquals(1, sessions1.size());
        long sessionId = sessions1.iterator().next().id();
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).forceCloseCPSession(CPGroup.DEFAULT_GROUP_NAME, sessionId, groupName, groupPassword);
        Assert.assertEquals(200, response.responseCode);
        Collection<CPSession> sessions2 = instance1.getCPSubsystem().getCPSessionManagementService().getAllSessions(CPGroup.DEFAULT_GROUP_NAME).get();
        Assert.assertEquals(0, sessions2.size());
    }

    @Test
    public void test_forceCloseValidCPSession_withInvalidCredentials() throws IOException, InterruptedException, ExecutionException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        instance1.getCPSubsystem().getLock("lock1").lock();
        instance1.getCPSubsystem().getLock("lock1").unlock();
        Collection<CPSession> sessions1 = instance1.getCPSubsystem().getCPSessionManagementService().getAllSessions(CPGroup.DEFAULT_GROUP_NAME).get();
        Assert.assertEquals(1, sessions1.size());
        long sessionId = sessions1.iterator().next().id();
        HTTPCommunicator.ConnectionResponse response = new HTTPCommunicator(instance1).forceCloseCPSession(CPGroup.DEFAULT_GROUP_NAME, sessionId, "x", "x");
        Assert.assertEquals(403, response.responseCode);
        Collection<CPSession> sessions2 = instance1.getCPSubsystem().getCPSessionManagementService().getAllSessions(CPGroup.DEFAULT_GROUP_NAME).get();
        Assert.assertEquals(1, sessions2.size());
    }

    @Test
    public void test_forceCloseInvalidCPSession() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        instance1.getCPSubsystem().getAtomicLong("long1").set(5);
        HTTPCommunicator.ConnectionResponse response1 = new HTTPCommunicator(instance1).forceCloseCPSession(CPGroup.DEFAULT_GROUP_NAME, 1, groupName, groupPassword);
        Assert.assertEquals(400, response1.responseCode);
    }

    @Test
    public void test_forceCloseCPSessionOfInvalidCPGroup() throws IOException {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        Hazelcast.newHazelcastInstance(config);
        HTTPCommunicator.ConnectionResponse response1 = new HTTPCommunicator(instance1).forceCloseCPSession(CPGroup.DEFAULT_GROUP_NAME, 1, groupName, groupPassword);
        Assert.assertEquals(400, response1.responseCode);
    }
}

