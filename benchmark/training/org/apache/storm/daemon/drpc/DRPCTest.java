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
package org.apache.storm.daemon.drpc;


import Config.DRPC_AUTHORIZER_ACL_STRICT;
import Config.STORM_PRINCIPAL_TO_LOCAL_PLUGIN;
import DRPCExceptionType.FAILED_REQUEST;
import DRPCExceptionType.SERVER_TIMEOUT;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.security.auth.Subject;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.DRPCExecutionException;
import org.apache.storm.generated.DRPCRequest;
import org.apache.storm.metric.StormMetricsRegistry;
import org.apache.storm.security.auth.DefaultPrincipalToLocal;
import org.apache.storm.security.auth.ReqContext;
import org.apache.storm.security.auth.SingleUserPrincipal;
import org.apache.storm.security.auth.authorizer.DRPCSimpleACLAuthorizer;
import org.apache.storm.security.auth.authorizer.DRPCSimpleACLAuthorizer.AclFunctionEntry;
import org.apache.storm.security.auth.authorizer.DenyAuthorizer;
import org.apache.storm.utils.Time;
import org.junit.Assert;
import org.junit.Test;


public class DRPCTest {
    private static final ExecutorService exec = Executors.newCachedThreadPool();

    @Test
    public void testGoodBlocking() throws Exception {
        try (DRPC server = new DRPC(new StormMetricsRegistry(), null, 100)) {
            Future<String> found = DRPCTest.exec.submit(() -> server.executeBlocking("testing", "test"));
            DRPCRequest request = DRPCTest.getNextAvailableRequest(server, "testing");
            Assert.assertNotNull(request);
            Assert.assertEquals("test", request.get_func_args());
            Assert.assertNotNull(request.get_request_id());
            server.returnResult(request.get_request_id(), "tested");
            String result = found.get(10, TimeUnit.MILLISECONDS);
            Assert.assertEquals("tested", result);
        }
    }

    @Test
    public void testFailedBlocking() throws Exception {
        try (DRPC server = new DRPC(new StormMetricsRegistry(), null, 100)) {
            Future<String> found = DRPCTest.exec.submit(() -> server.executeBlocking("testing", "test"));
            DRPCRequest request = DRPCTest.getNextAvailableRequest(server, "testing");
            Assert.assertNotNull(request);
            Assert.assertEquals("test", request.get_func_args());
            Assert.assertNotNull(request.get_request_id());
            server.failRequest(request.get_request_id(), null);
            try {
                found.get(100, TimeUnit.MILLISECONDS);
                Assert.fail("exec did not throw an exception");
            } catch (ExecutionException e) {
                Throwable t = e.getCause();
                Assert.assertTrue((t instanceof DRPCExecutionException));
                // Don't know a better way to validate that it failed.
                Assert.assertEquals(FAILED_REQUEST, get_type());
            }
        }
    }

    @Test
    public void testDequeueAfterTimeout() throws Exception {
        long timeout = 1000;
        try (DRPC server = new DRPC(new StormMetricsRegistry(), null, timeout)) {
            long start = Time.currentTimeMillis();
            try {
                server.executeBlocking("testing", "test");
                Assert.fail("Should have timed out....");
            } catch (DRPCExecutionException e) {
                long spent = (Time.currentTimeMillis()) - start;
                Assert.assertTrue((spent < (timeout * 2)));
                Assert.assertTrue((spent >= timeout));
                Assert.assertEquals(SERVER_TIMEOUT, e.get_type());
            }
            DRPCRequest request = server.fetchRequest("testing");
            Assert.assertNotNull(request);
            Assert.assertEquals("", request.get_request_id());
            Assert.assertEquals("", request.get_func_args());
        }
    }

    @Test
    public void testDeny() throws Exception {
        try (DRPC server = new DRPC(new StormMetricsRegistry(), new DenyAuthorizer(), 100)) {
            DRPCTest.assertThrows(() -> server.executeBlocking("testing", "test"), AuthorizationException.class);
            DRPCTest.assertThrows(() -> server.fetchRequest("testing"), AuthorizationException.class);
        }
    }

    @Test
    public void testStrict() throws Exception {
        ReqContext jt = new ReqContext(new Subject());
        SingleUserPrincipal jumpTopo = new SingleUserPrincipal("jump_topo");
        jt.subject().getPrincipals().add(jumpTopo);
        ReqContext jc = new ReqContext(new Subject());
        SingleUserPrincipal jumpClient = new SingleUserPrincipal("jump_client");
        jc.subject().getPrincipals().add(jumpClient);
        ReqContext other = new ReqContext(new Subject());
        SingleUserPrincipal otherUser = new SingleUserPrincipal("other");
        other.subject().getPrincipals().add(otherUser);
        Map<String, AclFunctionEntry> acl = new HashMap<>();
        acl.put("jump", new AclFunctionEntry(Arrays.asList(jumpClient.getName()), jumpTopo.getName()));
        Map<String, Object> conf = new HashMap<>();
        conf.put(DRPC_AUTHORIZER_ACL_STRICT, true);
        conf.put(STORM_PRINCIPAL_TO_LOCAL_PLUGIN, DefaultPrincipalToLocal.class.getName());
        DRPCSimpleACLAuthorizer auth = new DRPCSimpleACLAuthorizer() {
            @Override
            protected Map<String, AclFunctionEntry> readAclFromConfig() {
                return acl;
            }
        };
        auth.prepare(conf);
        // JUMP
        DRPC.checkAuthorization(jt, auth, "fetchRequest", "jump");
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jc, auth, "fetchRequest", "jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(other, auth, "fetchRequest", "jump"), AuthorizationException.class);
        DRPC.checkAuthorization(jt, auth, "result", "jump");
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jc, auth, "result", "jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(other, auth, "result", "jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jt, auth, "execute", "jump"), AuthorizationException.class);
        DRPC.checkAuthorization(jc, auth, "execute", "jump");
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(other, auth, "execute", "jump"), AuthorizationException.class);
        // not_jump (closed in strict mode)
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jt, auth, "fetchRequest", "not_jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jc, auth, "fetchRequest", "not_jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(other, auth, "fetchRequest", "not_jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jt, auth, "result", "not_jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jc, auth, "result", "not_jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(other, auth, "result", "not_jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jt, auth, "execute", "not_jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jc, auth, "execute", "not_jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(other, auth, "execute", "not_jump"), AuthorizationException.class);
    }

    @Test
    public void testNotStrict() throws Exception {
        ReqContext jt = new ReqContext(new Subject());
        SingleUserPrincipal jumpTopo = new SingleUserPrincipal("jump_topo");
        jt.subject().getPrincipals().add(jumpTopo);
        ReqContext jc = new ReqContext(new Subject());
        SingleUserPrincipal jumpClient = new SingleUserPrincipal("jump_client");
        jc.subject().getPrincipals().add(jumpClient);
        ReqContext other = new ReqContext(new Subject());
        SingleUserPrincipal otherUser = new SingleUserPrincipal("other");
        other.subject().getPrincipals().add(otherUser);
        Map<String, AclFunctionEntry> acl = new HashMap<>();
        acl.put("jump", new AclFunctionEntry(Arrays.asList(jumpClient.getName()), jumpTopo.getName()));
        Map<String, Object> conf = new HashMap<>();
        conf.put(DRPC_AUTHORIZER_ACL_STRICT, false);
        conf.put(STORM_PRINCIPAL_TO_LOCAL_PLUGIN, DefaultPrincipalToLocal.class.getName());
        DRPCSimpleACLAuthorizer auth = new DRPCSimpleACLAuthorizer() {
            @Override
            protected Map<String, AclFunctionEntry> readAclFromConfig() {
                return acl;
            }
        };
        auth.prepare(conf);
        // JUMP
        DRPC.checkAuthorization(jt, auth, "fetchRequest", "jump");
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jc, auth, "fetchRequest", "jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(other, auth, "fetchRequest", "jump"), AuthorizationException.class);
        DRPC.checkAuthorization(jt, auth, "result", "jump");
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jc, auth, "result", "jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(other, auth, "result", "jump"), AuthorizationException.class);
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(jt, auth, "execute", "jump"), AuthorizationException.class);
        DRPC.checkAuthorization(jc, auth, "execute", "jump");
        DRPCTest.assertThrows(() -> DRPC.checkAuthorization(other, auth, "execute", "jump"), AuthorizationException.class);
        // not_jump (open in not strict mode)
        DRPC.checkAuthorization(jt, auth, "fetchRequest", "not_jump");
        DRPC.checkAuthorization(jc, auth, "fetchRequest", "not_jump");
        DRPC.checkAuthorization(other, auth, "fetchRequest", "not_jump");
        DRPC.checkAuthorization(jt, auth, "result", "not_jump");
        DRPC.checkAuthorization(jc, auth, "result", "not_jump");
        DRPC.checkAuthorization(other, auth, "result", "not_jump");
        DRPC.checkAuthorization(jt, auth, "execute", "not_jump");
        DRPC.checkAuthorization(jc, auth, "execute", "not_jump");
        DRPC.checkAuthorization(other, auth, "execute", "not_jump");
    }

    public static interface ThrowStuff {
        public void run() throws Exception;
    }
}

