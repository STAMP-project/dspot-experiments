/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.daemon.logviewer.utils;


import DaemonConfig.LOGVIEWER_FILTER;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.storm.Config;
import org.apache.storm.DaemonConfig;
import org.apache.storm.utils.Utils;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ResourceAuthorizerTest {
    /**
     * allow cluster admin.
     */
    @Test
    public void testAuthorizedLogUserAllowClusterAdmin() {
        Map<String, Object> stormConf = Utils.readStormConfig();
        Map<String, Object> conf = new HashMap<>(stormConf);
        conf.put(Config.NIMBUS_ADMINS, Collections.singletonList("alice"));
        ResourceAuthorizer authorizer = Mockito.spy(new ResourceAuthorizer(conf));
        Mockito.doReturn(new ResourceAuthorizer.LogUserGroupWhitelist(Collections.emptySet(), Collections.emptySet())).when(authorizer).getLogUserGroupWhitelist(ArgumentMatchers.anyString());
        Mockito.doReturn(Collections.emptySet()).when(authorizer).getUserGroups(ArgumentMatchers.anyString());
        Assert.assertTrue(authorizer.isAuthorizedLogUser("alice", "non-blank-fname"));
        verifyStubMethodsAreCalledProperly(authorizer);
    }

    /**
     * ignore any cluster-set topology.users topology.groups.
     */
    @Test
    public void testAuthorizedLogUserIgnoreAnyClusterSetTopologyUsersAndTopologyGroups() {
        Map<String, Object> stormConf = Utils.readStormConfig();
        Map<String, Object> conf = new HashMap<>(stormConf);
        conf.put(Config.TOPOLOGY_USERS, Collections.singletonList("alice"));
        conf.put(Config.TOPOLOGY_GROUPS, Collections.singletonList("alice-group"));
        ResourceAuthorizer authorizer = Mockito.spy(new ResourceAuthorizer(conf));
        Mockito.doReturn(new ResourceAuthorizer.LogUserGroupWhitelist(Collections.emptySet(), Collections.emptySet())).when(authorizer).getLogUserGroupWhitelist(ArgumentMatchers.anyString());
        Mockito.doReturn(Collections.singleton("alice-group")).when(authorizer).getUserGroups(ArgumentMatchers.anyString());
        Assert.assertFalse(authorizer.isAuthorizedLogUser("alice", "non-blank-fname"));
        verifyStubMethodsAreCalledProperly(authorizer);
    }

    /**
     * allow cluster logs user.
     */
    @Test
    public void testAuthorizedLogUserAllowClusterLogsUser() {
        Map<String, Object> stormConf = Utils.readStormConfig();
        Map<String, Object> conf = new HashMap<>(stormConf);
        conf.put(DaemonConfig.LOGS_USERS, Collections.singletonList("alice"));
        ResourceAuthorizer authorizer = Mockito.spy(new ResourceAuthorizer(conf));
        Mockito.doReturn(new ResourceAuthorizer.LogUserGroupWhitelist(Collections.emptySet(), Collections.emptySet())).when(authorizer).getLogUserGroupWhitelist(ArgumentMatchers.anyString());
        Mockito.doReturn(Collections.emptySet()).when(authorizer).getUserGroups(ArgumentMatchers.anyString());
        Assert.assertTrue(authorizer.isAuthorizedLogUser("alice", "non-blank-fname"));
        verifyStubMethodsAreCalledProperly(authorizer);
    }

    /**
     * allow whitelisted topology user.
     */
    @Test
    public void testAuthorizedLogUserAllowWhitelistedTopologyUser() {
        Map<String, Object> stormConf = Utils.readStormConfig();
        Map<String, Object> conf = new HashMap<>(stormConf);
        ResourceAuthorizer authorizer = Mockito.spy(new ResourceAuthorizer(conf));
        Mockito.doReturn(new ResourceAuthorizer.LogUserGroupWhitelist(Collections.singleton("alice"), Collections.emptySet())).when(authorizer).getLogUserGroupWhitelist(ArgumentMatchers.anyString());
        Mockito.doReturn(Collections.emptySet()).when(authorizer).getUserGroups(ArgumentMatchers.anyString());
        Assert.assertTrue(authorizer.isAuthorizedLogUser("alice", "non-blank-fname"));
        verifyStubMethodsAreCalledProperly(authorizer);
    }

    /**
     * allow whitelisted topology group.
     */
    @Test
    public void testAuthorizedLogUserAllowWhitelistedTopologyGroup() {
        Map<String, Object> stormConf = Utils.readStormConfig();
        Map<String, Object> conf = new HashMap<>(stormConf);
        ResourceAuthorizer authorizer = Mockito.spy(new ResourceAuthorizer(conf));
        Mockito.doReturn(new ResourceAuthorizer.LogUserGroupWhitelist(Collections.emptySet(), Collections.singleton("alice-group"))).when(authorizer).getLogUserGroupWhitelist(ArgumentMatchers.anyString());
        Mockito.doReturn(Collections.singleton("alice-group")).when(authorizer).getUserGroups(ArgumentMatchers.anyString());
        Assert.assertTrue(authorizer.isAuthorizedLogUser("alice", "non-blank-fname"));
        verifyStubMethodsAreCalledProperly(authorizer);
    }

    /**
     * disallow user not in nimbus admin, topo user, logs user, or whitelist.
     */
    @Test
    public void testAuthorizedLogUserDisallowUserNotInNimbusAdminNorTopoUserNorLogsUserNotWhitelist() {
        Map<String, Object> stormConf = Utils.readStormConfig();
        Map<String, Object> conf = new HashMap<>(stormConf);
        ResourceAuthorizer authorizer = Mockito.spy(new ResourceAuthorizer(conf));
        Mockito.doReturn(new ResourceAuthorizer.LogUserGroupWhitelist(Collections.emptySet(), Collections.emptySet())).when(authorizer).getLogUserGroupWhitelist(ArgumentMatchers.anyString());
        Mockito.doReturn(Collections.emptySet()).when(authorizer).getUserGroups(ArgumentMatchers.anyString());
        Assert.assertFalse(authorizer.isAuthorizedLogUser("alice", "non-blank-fname"));
        verifyStubMethodsAreCalledProperly(authorizer);
    }

    /**
     * disallow upward path traversal in filenames.
     */
    @Test
    public void testFailOnUpwardPathTraversal() {
        Map<String, Object> stormConf = Utils.readStormConfig();
        Map<String, Object> conf = new HashMap<>(stormConf);
        ResourceAuthorizer authorizer = new ResourceAuthorizer(conf);
        Assertions.assertThrows(IllegalArgumentException.class, () -> authorizer.isAuthorizedLogUser("user", Paths.get("some/../path").toString()));
    }

    @Test
    public void authorizationFailsWhenFilterConfigured() {
        Map<String, Object> stormConf = Utils.readStormConfig();
        Map<String, Object> conf = new HashMap<>(stormConf);
        ResourceAuthorizer authorizer = Mockito.spy(new ResourceAuthorizer(conf));
        Mockito.when(authorizer.isAuthorizedLogUser(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(false);
        boolean authorized = authorizer.isUserAllowedToAccessFile("bob", "anyfile");
        Assert.assertTrue(authorized);// no filter configured, allow anyone

        conf.put(LOGVIEWER_FILTER, "someFilter");
        authorized = authorizer.isUserAllowedToAccessFile("bob", "anyfile");
        Assert.assertFalse(authorized);// filter configured, should fail all users

    }
}

