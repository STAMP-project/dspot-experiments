/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.security.auth;


import Config.NIMBUS_ADMINS;
import Config.NIMBUS_ADMINS_GROUPS;
import Config.NIMBUS_IMPERSONATION_ACL;
import Config.NIMBUS_SUPERVISOR_USERS;
import Config.NIMBUS_USERS;
import Config.STORM_GROUP_MAPPING_SERVICE_PARAMS;
import Config.STORM_GROUP_MAPPING_SERVICE_PROVIDER_PLUGIN;
import Config.STORM_THRIFT_TRANSPORT_PLUGIN;
import Config.TOPOLOGY_USERS;
import FixedGroupsMapping.STORM_FIXED_GROUP_MAPPING;
import Nimbus.Iface;
import SimpleWhitelistAuthorizer.WHITELIST_USERS_CONF;
import ThriftConnectionType.NIMBUS;
import com.google.common.net.InetAddresses;
import java.io.File;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.security.auth.Subject;
import org.apache.storm.cluster.ClusterStateContext;
import org.apache.storm.cluster.ClusterUtils;
import org.apache.storm.cluster.IStormClusterState;
import org.apache.storm.generated.Nimbus;
import org.apache.storm.security.auth.authorizer.ImpersonationAuthorizer;
import org.apache.storm.security.auth.authorizer.SimpleACLAuthorizer;
import org.apache.storm.security.auth.authorizer.SimpleWhitelistAuthorizer;
import org.apache.storm.security.auth.digest.DigestSaslTransportPlugin;
import org.apache.storm.security.auth.workertoken.WorkerTokenManager;
import org.apache.storm.testing.InProcessZookeeper;
import org.apache.storm.utils.ConfigUtils;
import org.apache.storm.utils.NimbusClient;
import org.apache.storm.utils.Time;
import org.apache.storm.utils.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AuthTest {
    // 3 seconds in milliseconds
    public static final int NIMBUS_TIMEOUT = 3000;

    private static final Logger LOG = LoggerFactory.getLogger(AuthTest.class);

    private static final File BASE = new File("./src/test/resources/");

    private static final String DIGEST_JAAS_CONF = new File(AuthTest.BASE, "jaas_digest.conf").getAbsolutePath();

    private static final String BAD_PASSWORD_CONF = new File(AuthTest.BASE, "jaas_digest_bad_password.conf").getAbsolutePath();

    private static final String WRONG_USER_CONF = new File(AuthTest.BASE, "jaas_digest_unknown_user.conf").getAbsolutePath();

    private static final String MISSING_CLIENT = new File(AuthTest.BASE, "jaas_digest_missing_client.conf").getAbsolutePath();

    @Test
    public void kerbToLocalTest() {
        KerberosPrincipalToLocal kptol = new KerberosPrincipalToLocal();
        kptol.prepare(Collections.emptyMap());
        Assert.assertEquals("me", kptol.toLocal(AuthTest.mkPrincipal("me@realm")));
        Assert.assertEquals("simple", kptol.toLocal(AuthTest.mkPrincipal("simple")));
        Assert.assertEquals("someone", kptol.toLocal(AuthTest.mkPrincipal("someone/host@realm")));
    }

    @Test
    public void simpleAuthTest() throws Exception {
        Nimbus.Iface impl = Mockito.mock(Iface.class);
        AuthTest.withServer(SimpleTransportPlugin.class, impl, (ThriftServer server,Map<String, Object> conf) -> {
            try (NimbusClient client = new NimbusClient(conf, "localhost", server.getPort(), NIMBUS_TIMEOUT)) {
                client.getClient().activate("security_auth_test_topology");
            }
            // Verify digest is rejected...
            Map<String, Object> badConf = new HashMap<>(conf);
            badConf.put(Config.STORM_THRIFT_TRANSPORT_PLUGIN, .class.getName());
            badConf.put("java.security.auth.login.config", DIGEST_JAAS_CONF);
            badConf.put(Config.STORM_NIMBUS_RETRY_TIMES, 0);
            try (NimbusClient client = new NimbusClient(badConf, "localhost", server.getPort(), NIMBUS_TIMEOUT)) {
                client.getClient().activate("bad_security_auth_test_topology");
                fail("An exception should have been thrown trying to connect.");
            } catch ( te) {
                LOG.info("Got Exception...", te);
                assert Utils.exceptionCauseIsInstanceOf(.class, te);
            }
        });
        Mockito.verify(impl).activate("security_auth_test_topology");
        Mockito.verify(impl, Mockito.never()).activate("bad_security_auth_test_topology");
    }

    @Test
    public void digestAuthTest() throws Exception {
        Nimbus.Iface impl = Mockito.mock(Iface.class);
        final AtomicReference<ReqContext> user = new AtomicReference<>();
        Mockito.doAnswer(( invocation) -> {
            user.set(new ReqContext(ReqContext.context()));
            return null;
        }).when(impl).activate(ArgumentMatchers.anyString());
        AuthTest.withServer(AuthTest.DIGEST_JAAS_CONF, DigestSaslTransportPlugin.class, impl, (ThriftServer server,Map<String, Object> conf) -> {
            try (NimbusClient client = new NimbusClient(conf, "localhost", server.getPort(), NIMBUS_TIMEOUT)) {
                client.getClient().activate("security_auth_test_topology");
            }
            conf.put(Config.STORM_NIMBUS_RETRY_TIMES, 0);
            // Verify simple is rejected...
            Map<String, Object> badTransport = new HashMap<>(conf);
            badTransport.put(Config.STORM_THRIFT_TRANSPORT_PLUGIN, .class.getName());
            try (NimbusClient client = new NimbusClient(badTransport, "localhost", server.getPort(), NIMBUS_TIMEOUT)) {
                client.getClient().activate("bad_security_auth_test_topology");
                fail("An exception should have been thrown trying to connect.");
            } catch ( te) {
                LOG.info("Got Exception...", te);
                assert Utils.exceptionCauseIsInstanceOf(.class, te);
            }
            // The user here from the jaas conf is bob.  No impersonation is done, so verify that
            ReqContext found = user.get();
            assertNotNull(found);
            assertEquals("bob", found.principal().getName());
            assertFalse(found.isImpersonating());
            user.set(null);
            verifyIncorrectJaasConf(server, conf, BAD_PASSWORD_CONF, .class);
            verifyIncorrectJaasConf(server, conf, WRONG_USER_CONF, .class);
            verifyIncorrectJaasConf(server, conf, "./nonexistent.conf", .class);
            verifyIncorrectJaasConf(server, conf, MISSING_CLIENT, .class);
        });
        Mockito.verify(impl).activate("security_auth_test_topology");
        Mockito.verify(impl, Mockito.never()).activate("bad_auth_test_topology");
    }

    @Test
    public void workerTokenDigestAuthTest() throws Exception {
        AuthTest.LOG.info("\n\n\t\tworkerTokenDigestAuthTest - START\n\n");
        Nimbus.Iface impl = Mockito.mock(Iface.class);
        final AtomicReference<ReqContext> user = new AtomicReference<>();
        Mockito.doAnswer(( invocation) -> {
            user.set(new ReqContext(ReqContext.context()));
            return null;
        }).when(impl).activate(ArgumentMatchers.anyString());
        Map<String, Object> extraConfs = new HashMap<>();
        // Let worker tokens work on insecure ZK...
        extraConfs.put("TESTING.ONLY.ENABLE.INSECURE.WORKER.TOKENS", true);
        try (InProcessZookeeper zk = new InProcessZookeeper()) {
            AuthTest.withServer(AuthTest.MISSING_CLIENT, DigestSaslTransportPlugin.class, impl, zk, extraConfs, (ThriftServer server,Map<String, Object> conf) -> {
                try (Time.SimulatedTime sim = new Time.SimulatedTime()) {
                    conf.put(Config.STORM_NIMBUS_RETRY_TIMES, 0);
                    // We cannot connect if there is no client section in the jaas conf...
                    try (NimbusClient client = new NimbusClient(conf, "localhost", server.getPort(), NIMBUS_TIMEOUT)) {
                        client.getClient().activate("bad_auth_test_topology");
                        fail("We should not be able to connect without a token...");
                    } catch ( e) {
                        assert Utils.exceptionCauseIsInstanceOf(.class, e);
                    }
                    // Now lets create a token and verify that we can connect...
                    IStormClusterState state = ClusterUtils.mkStormClusterState(conf, new ClusterStateContext(DaemonType.NIMBUS, conf));
                    WorkerTokenManager wtMan = new WorkerTokenManager(conf, state);
                    Subject bob = testConnectWithTokenFor(wtMan, conf, server, "bob", "topo-bob");
                    verifyUserIs(user, "bob");
                    Time.advanceTimeSecs(TimeUnit.HOURS.toSeconds(12));
                    // Alice has no digest jaas section at all...
                    Subject alice = testConnectWithTokenFor(wtMan, conf, server, "alice", "topo-alice");
                    verifyUserIs(user, "alice");
                    Time.advanceTimeSecs(TimeUnit.HOURS.toSeconds(13));
                    // Verify that bob's token has expired
                    try {
                        tryConnectAs(conf, server, bob, "bad_auth_test_topology");
                        fail("We should not be able to connect with bad auth");
                    } catch ( e) {
                        assert Utils.exceptionCauseIsInstanceOf(.class, e);
                    }
                    tryConnectAs(conf, server, alice, "topo-alice");
                    verifyUserIs(user, "alice");
                    // Now see if we can create a new token for bob and try again.
                    bob = testConnectWithTokenFor(wtMan, conf, server, "bob", "topo-bob");
                    verifyUserIs(user, "bob");
                    tryConnectAs(conf, server, alice, "topo-alice");
                    verifyUserIs(user, "alice");
                }
            });
        }
        Mockito.verify(impl, Mockito.times(2)).activate("topo-bob");
        Mockito.verify(impl, Mockito.times(3)).activate("topo-alice");
        Mockito.verify(impl, Mockito.never()).activate("bad_auth_test_topology");
        AuthTest.LOG.info("\n\n\t\tworkerTokenDigestAuthTest - END\n\n");
    }

    @Test
    public void negativeWhitelistAuthroizationTest() {
        SimpleWhitelistAuthorizer auth = new SimpleWhitelistAuthorizer();
        Map<String, Object> conf = ConfigUtils.readStormConfig();
        auth.prepare(conf);
        ReqContext context = new ReqContext(AuthTest.mkSubject("user"));
        Assert.assertFalse(auth.permit(context, "activate", conf));
    }

    @Test
    public void positiveWhitelistAuthroizationTest() {
        SimpleWhitelistAuthorizer auth = new SimpleWhitelistAuthorizer();
        Map<String, Object> conf = ConfigUtils.readStormConfig();
        conf.put(WHITELIST_USERS_CONF, Arrays.asList("user"));
        auth.prepare(conf);
        ReqContext context = new ReqContext(AuthTest.mkSubject("user"));
        Assert.assertTrue(auth.permit(context, "activate", conf));
    }

    @Test
    public void simpleAclUserAuthTest() {
        Map<String, Object> clusterConf = ConfigUtils.readStormConfig();
        clusterConf.put(NIMBUS_ADMINS, Arrays.asList("admin"));
        clusterConf.put(NIMBUS_SUPERVISOR_USERS, Arrays.asList("supervisor"));
        ReqContext admin = new ReqContext(AuthTest.mkSubject("admin"));
        ReqContext supervisor = new ReqContext(AuthTest.mkSubject("supervisor"));
        ReqContext userA = new ReqContext(AuthTest.mkSubject("user-a"));
        ReqContext userB = new ReqContext(AuthTest.mkSubject("user-b"));
        final Map<String, Object> empty = Collections.emptyMap();
        final Map<String, Object> aAllowed = new HashMap<>();
        aAllowed.put(TOPOLOGY_USERS, Arrays.asList("user-a"));
        SimpleACLAuthorizer authorizer = new SimpleACLAuthorizer();
        authorizer.prepare(clusterConf);
        Assert.assertTrue(authorizer.permit(userA, "submitTopology", empty));
        Assert.assertTrue(authorizer.permit(userB, "submitTopology", empty));
        Assert.assertTrue(authorizer.permit(admin, "submitTopology", empty));
        Assert.assertFalse(authorizer.permit(supervisor, "submitTopology", empty));
        Assert.assertTrue(authorizer.permit(userA, "fileUpload", null));
        Assert.assertTrue(authorizer.permit(userB, "fileUpload", null));
        Assert.assertTrue(authorizer.permit(admin, "fileUpload", null));
        Assert.assertFalse(authorizer.permit(supervisor, "fileUpload", null));
        Assert.assertTrue(authorizer.permit(userA, "getNimbusConf", null));
        Assert.assertTrue(authorizer.permit(userB, "getNimbusConf", null));
        Assert.assertTrue(authorizer.permit(admin, "getNimbusConf", null));
        Assert.assertFalse(authorizer.permit(supervisor, "getNimbusConf", null));
        Assert.assertTrue(authorizer.permit(userA, "getClusterInfo", null));
        Assert.assertTrue(authorizer.permit(userB, "getClusterInfo", null));
        Assert.assertTrue(authorizer.permit(admin, "getClusterInfo", null));
        Assert.assertFalse(authorizer.permit(supervisor, "getClusterInfo", null));
        Assert.assertFalse(authorizer.permit(userA, "fileDownload", null));
        Assert.assertFalse(authorizer.permit(userB, "fileDownload", null));
        Assert.assertTrue(authorizer.permit(admin, "fileDownload", null));
        Assert.assertTrue(authorizer.permit(supervisor, "fileDownload", null));
        Assert.assertTrue(authorizer.permit(userA, "killTopology", aAllowed));
        Assert.assertFalse(authorizer.permit(userB, "killTopology", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "killTopology", aAllowed));
        Assert.assertFalse(authorizer.permit(supervisor, "killTopology", aAllowed));
        Assert.assertTrue(authorizer.permit(userA, "uploadNewCredentials", aAllowed));
        Assert.assertFalse(authorizer.permit(userB, "uploadNewCredentials", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "uploadNewCredentials", aAllowed));
        Assert.assertFalse(authorizer.permit(supervisor, "uploadNewCredentials", aAllowed));
        Assert.assertTrue(authorizer.permit(userA, "rebalance", aAllowed));
        Assert.assertFalse(authorizer.permit(userB, "rebalance", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "rebalance", aAllowed));
        Assert.assertFalse(authorizer.permit(supervisor, "rebalance", aAllowed));
        Assert.assertTrue(authorizer.permit(userA, "activate", aAllowed));
        Assert.assertFalse(authorizer.permit(userB, "activate", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "activate", aAllowed));
        Assert.assertFalse(authorizer.permit(supervisor, "activate", aAllowed));
        Assert.assertTrue(authorizer.permit(userA, "deactivate", aAllowed));
        Assert.assertFalse(authorizer.permit(userB, "deactivate", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "deactivate", aAllowed));
        Assert.assertFalse(authorizer.permit(supervisor, "deactivate", aAllowed));
        Assert.assertTrue(authorizer.permit(userA, "getTopologyConf", aAllowed));
        Assert.assertFalse(authorizer.permit(userB, "getTopologyConf", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "getTopologyConf", aAllowed));
        Assert.assertFalse(authorizer.permit(supervisor, "getTopologyConf", aAllowed));
        Assert.assertTrue(authorizer.permit(userA, "getTopology", aAllowed));
        Assert.assertFalse(authorizer.permit(userB, "getTopology", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "getTopology", aAllowed));
        Assert.assertFalse(authorizer.permit(supervisor, "getTopology", aAllowed));
        Assert.assertTrue(authorizer.permit(userA, "getUserTopology", aAllowed));
        Assert.assertFalse(authorizer.permit(userB, "getUserTopology", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "getUserTopology", aAllowed));
        Assert.assertFalse(authorizer.permit(supervisor, "getUserTopology", aAllowed));
        Assert.assertTrue(authorizer.permit(userA, "getTopologyInfo", aAllowed));
        Assert.assertFalse(authorizer.permit(userB, "getTopologyInfo", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "getTopologyInfo", aAllowed));
        Assert.assertFalse(authorizer.permit(supervisor, "getTopologyInfo", aAllowed));
    }

    @Test
    public void simpleAclNimbusUsersAuthTest() {
        Map<String, Object> clusterConf = ConfigUtils.readStormConfig();
        clusterConf.put(NIMBUS_ADMINS, Arrays.asList("admin"));
        clusterConf.put(NIMBUS_SUPERVISOR_USERS, Arrays.asList("supervisor"));
        clusterConf.put(NIMBUS_USERS, Arrays.asList("user-a"));
        ReqContext admin = new ReqContext(AuthTest.mkSubject("admin"));
        ReqContext supervisor = new ReqContext(AuthTest.mkSubject("supervisor"));
        ReqContext userA = new ReqContext(AuthTest.mkSubject("user-a"));
        ReqContext userB = new ReqContext(AuthTest.mkSubject("user-b"));
        final Map<String, Object> empty = Collections.emptyMap();
        SimpleACLAuthorizer authorizer = new SimpleACLAuthorizer();
        authorizer.prepare(clusterConf);
        Assert.assertTrue(authorizer.permit(userA, "submitTopology", empty));
        Assert.assertFalse(authorizer.permit(userB, "submitTopology", empty));
        Assert.assertTrue(authorizer.permit(admin, "fileUpload", null));
        Assert.assertTrue(authorizer.permit(supervisor, "fileDownload", null));
    }

    @Test
    public void simpleAclNimbusGroupsAuthTest() {
        Map<String, Object> clusterConf = ConfigUtils.readStormConfig();
        clusterConf.put(NIMBUS_ADMINS_GROUPS, Arrays.asList("admin-group"));
        clusterConf.put(NIMBUS_SUPERVISOR_USERS, Arrays.asList("supervisor"));
        clusterConf.put(NIMBUS_USERS, Arrays.asList("user-a"));
        clusterConf.put(STORM_GROUP_MAPPING_SERVICE_PROVIDER_PLUGIN, FixedGroupsMapping.class.getName());
        Map<String, Object> groups = new HashMap<>();
        groups.put("admin", Collections.singleton("admin-group"));
        groups.put("not-admin", Collections.singleton("not-admin-group"));
        Map<String, Object> groupsParams = new HashMap<>();
        groupsParams.put(STORM_FIXED_GROUP_MAPPING, groups);
        clusterConf.put(STORM_GROUP_MAPPING_SERVICE_PARAMS, groupsParams);
        ReqContext admin = new ReqContext(AuthTest.mkSubject("admin"));
        ReqContext notAdmin = new ReqContext(AuthTest.mkSubject("not-admin"));
        ReqContext supervisor = new ReqContext(AuthTest.mkSubject("supervisor"));
        ReqContext userA = new ReqContext(AuthTest.mkSubject("user-a"));
        ReqContext userB = new ReqContext(AuthTest.mkSubject("user-b"));
        final Map<String, Object> empty = Collections.emptyMap();
        SimpleACLAuthorizer authorizer = new SimpleACLAuthorizer();
        authorizer.prepare(clusterConf);
        Assert.assertTrue(authorizer.permit(userA, "submitTopology", empty));
        Assert.assertFalse(authorizer.permit(userB, "submitTopology", empty));
        Assert.assertTrue(authorizer.permit(admin, "fileUpload", null));
        Assert.assertFalse(authorizer.permit(notAdmin, "fileUpload", null));
        Assert.assertFalse(authorizer.permit(userB, "fileUpload", null));
        Assert.assertTrue(authorizer.permit(supervisor, "fileDownload", null));
    }

    @Test
    public void simpleAclSameUserAuthTest() {
        Map<String, Object> clusterConf = ConfigUtils.readStormConfig();
        clusterConf.put(NIMBUS_ADMINS, Arrays.asList("admin"));
        clusterConf.put(NIMBUS_SUPERVISOR_USERS, Arrays.asList("admin"));
        ReqContext admin = new ReqContext(AuthTest.mkSubject("admin"));
        final Map<String, Object> empty = Collections.emptyMap();
        final Map<String, Object> aAllowed = new HashMap<>();
        aAllowed.put(TOPOLOGY_USERS, Arrays.asList("user-a"));
        SimpleACLAuthorizer authorizer = new SimpleACLAuthorizer();
        authorizer.prepare(clusterConf);
        Assert.assertTrue(authorizer.permit(admin, "submitTopology", empty));
        Assert.assertTrue(authorizer.permit(admin, "fileUpload", null));
        Assert.assertTrue(authorizer.permit(admin, "getNimbusConf", null));
        Assert.assertTrue(authorizer.permit(admin, "getClusterInfo", null));
        Assert.assertTrue(authorizer.permit(admin, "fileDownload", null));
        Assert.assertTrue(authorizer.permit(admin, "killTopology", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "uploadNewCredentials", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "rebalance", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "activate", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "getTopologyConf", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "getTopology", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "getUserTopology", aAllowed));
        Assert.assertTrue(authorizer.permit(admin, "getTopologyInfo", aAllowed));
    }

    @Test
    public void shellBaseGroupsMappingTest() throws Exception {
        Map<String, Object> clusterConf = ConfigUtils.readStormConfig();
        ShellBasedGroupsMapping groups = new ShellBasedGroupsMapping();
        groups.prepare(clusterConf);
        String userName = System.getProperty("user.name");
        Assert.assertTrue(((groups.getGroups(userName).size()) >= 0));
        Assert.assertEquals(0, groups.getGroups("userDoesNotExist").size());
        Assert.assertEquals(0, groups.getGroups(null).size());
    }

    @Test(expected = RuntimeException.class)
    public void getTransportPluginThrowsRunimeTest() {
        Map<String, Object> conf = ConfigUtils.readStormConfig();
        conf.put(STORM_THRIFT_TRANSPORT_PLUGIN, "null.invalid");
        ClientAuthUtils.getTransportPlugin(NIMBUS, conf, null);
    }

    @Test
    public void impersonationAuthorizerTest() throws Exception {
        final String impersonatingUser = "admin";
        final String userBeingImpersonated = System.getProperty("user.name");
        Map<String, Object> clusterConf = ConfigUtils.readStormConfig();
        ShellBasedGroupsMapping groupMapper = new ShellBasedGroupsMapping();
        groupMapper.prepare(clusterConf);
        Set<String> groups = groupMapper.getGroups(userBeingImpersonated);
        InetAddress localHost = InetAddress.getLocalHost();
        Map<String, Object> acl = new HashMap<>();
        Map<String, Object> aclConf = new HashMap<>();
        aclConf.put("hosts", Arrays.asList(localHost.getHostName()));
        aclConf.put("groups", groups);
        acl.put(impersonatingUser, aclConf);
        clusterConf.put(NIMBUS_IMPERSONATION_ACL, acl);
        InetAddress unauthorizedHost = InetAddresses.forString("10.10.10.10");
        ImpersonationAuthorizer authorizer = new ImpersonationAuthorizer();
        authorizer.prepare(clusterConf);
        // non impersonating request, should be permitted.
        Assert.assertTrue(authorizer.permit(new ReqContext(AuthTest.mkSubject("anyuser")), "fileUplaod", null));
        // user with no impersonation acl should be reject
        Assert.assertFalse(authorizer.permit(AuthTest.mkImpersonatingReqContext("user-with-no-acl", userBeingImpersonated, localHost), "someOperation", null));
        // request from hosts that are not authorized should be rejected
        Assert.assertFalse(authorizer.permit(AuthTest.mkImpersonatingReqContext(impersonatingUser, userBeingImpersonated, unauthorizedHost), "someOperation", null));
        // request to impersonate users from unauthroized groups should be rejected.
        Assert.assertFalse(authorizer.permit(AuthTest.mkImpersonatingReqContext(impersonatingUser, "unauthorized-user", localHost), "someOperation", null));
        // request from authorized hosts and group should be allowed.
        Assert.assertTrue(authorizer.permit(AuthTest.mkImpersonatingReqContext(impersonatingUser, userBeingImpersonated, localHost), "someOperation", null));
    }

    public interface MyBiConsumer<T, U> {
        void accept(T t, U u) throws Exception;
    }
}

