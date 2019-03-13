/**
 * Copyright 2016 Red Hat, Inc.
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
package org.jboss.as.test.integration.domain.mixed;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jboss.as.controller.ExpressionResolverImpl;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.dmr.ModelNode;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Base class for tests of the ability of a DC to exclude resources from visibility to a slave.
 *
 * @author Brian Stansberry
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class DomainHostExcludesTest {
    private static final String[] EXCLUDED_EXTENSIONS_6X = new String[]{ "org.wildfly.extension.batch.jberet", "org.wildfly.extension.bean-validation", "org.wildfly.extension.clustering.singleton", "org.wildfly.extension.core-management", "org.wildfly.extension.io", "org.wildfly.extension.messaging-activemq", "org.wildfly.extension.request-controller", "org.wildfly.extension.security.manager", "org.wildfly.extension.undertow", "org.wildfly.iiop-openjdk" };

    private static final String[] EXCLUDED_EXTENSIONS_7X = new String[]{ "org.jboss.as.web", "org.jboss.as.messaging", "org.jboss.as.threads" };

    public static final Set<String> EXTENSIONS_SET_6X = new HashSet<>(Arrays.asList(DomainHostExcludesTest.EXCLUDED_EXTENSIONS_6X));

    public static final Set<String> EXTENSIONS_SET_7X = new HashSet<>(Arrays.asList(DomainHostExcludesTest.EXCLUDED_EXTENSIONS_7X));

    private static final PathElement HOST = PathElement.pathElement("host", "slave");

    private static final PathAddress HOST_EXCLUDE = PathAddress.pathAddress("host-exclude", "test");

    private static final PathElement SOCKET = PathElement.pathElement(SOCKET_BINDING, "http");

    private static final PathAddress CLONE_PROFILE = PathAddress.pathAddress(PROFILE, CLONE);

    private static DomainTestSupport testSupport;

    private static Version.AsVersion version;

    @Test
    public void test001SlaveBoot() throws Exception {
        ModelControllerClient slaveClient = DomainHostExcludesTest.testSupport.getDomainSlaveLifecycleUtil().getDomainClient();
        checkExtensions(slaveClient);
        checkProfiles(slaveClient);
        checkSocketBindingGroups(slaveClient);
        checkSockets(slaveClient, PathAddress.pathAddress(SOCKET_BINDING_GROUP, "full-sockets"));
        checkSockets(slaveClient, PathAddress.pathAddress(SOCKET_BINDING_GROUP, "full-ha-sockets"));
    }

    @Test
    public void test002ServerBoot() throws IOException, InterruptedException, OperationFailedException, MgmtOperationException {
        ModelControllerClient masterClient = DomainHostExcludesTest.testSupport.getDomainMasterLifecycleUtil().getDomainClient();
        PathAddress serverCfgAddr = PathAddress.pathAddress(DomainHostExcludesTest.HOST, PathElement.pathElement(SERVER_CONFIG, "server-one"));
        ModelNode op = Util.createEmptyOperation("start", serverCfgAddr);
        executeForResult(op, masterClient);
        PathAddress serverAddr = PathAddress.pathAddress(DomainHostExcludesTest.HOST, PathElement.pathElement(RUNNING_SERVER, "server-one"));
        awaitServerLaunch(masterClient, serverAddr);
        checkSockets(masterClient, serverAddr.append(PathElement.pathElement(SOCKET_BINDING_GROUP, "full-ha-sockets")));
    }

    @Test
    public void test003PostBootUpdates() throws IOException, MgmtOperationException {
        ModelControllerClient masterClient = DomainHostExcludesTest.testSupport.getDomainMasterLifecycleUtil().getDomainClient();
        ModelControllerClient slaveClient = DomainHostExcludesTest.testSupport.getDomainSlaveLifecycleUtil().getDomainClient();
        // Tweak an ignored profile and socket-binding-group to prove slave doesn't see it
        updateExcludedProfile(masterClient);
        updateExcludedSocketBindingGroup(masterClient);
        // Verify profile cloning is ignored when the cloned profile is excluded
        testProfileCloning(masterClient, slaveClient);
        // Add more ignored extensions to verify slave doesn't see the ops
        DomainHostExcludesTest.addExtensions(false, masterClient);
        checkExtensions(slaveClient);
    }

    private static class TestExpressionResolver extends ExpressionResolverImpl {
        public TestExpressionResolver() {
        }
    }
}

