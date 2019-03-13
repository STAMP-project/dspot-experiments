/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.manualmode.ejb.client.cluster;


import ModelDescriptionConstants.ADD;
import ModelDescriptionConstants.REMOVE;
import javax.naming.Context;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.integration.management.util.ModelUtil;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that clustered EJB remote invocations between the server and client, where the client itself
 * is another server instance, work correctly and the clustering failover capabilities are available. This
 * further tests that the cluster configurations in the jboss-ejb-client.xml are honoured.
 *
 * @author Jaikiran Pai
 * @see https://issues.jboss.org/browse/AS7-4099
 */
@RunWith(Arquillian.class)
@RunAsClient
@Ignore("WFLY-4421")
public class EJBClientClusterConfigurationTestCase {
    private static final Logger logger = Logger.getLogger(EJBClientClusterConfigurationTestCase.class);

    private static final String MODULE_NAME = "server-to-server-clustered-ejb-invocation";

    private static final String DEFAULT_JBOSSAS = "default-jbossas";

    private static final String JBOSSAS_WITH_REMOTE_OUTBOUND_CONNECTION = "jbossas-with-remote-outbound-connection";

    private static final String DEFAULT_AS_DEPLOYMENT = "default-jbossas-deployment";

    private static final String DEPLOYMENT_WITH_JBOSS_EJB_CLIENT_XML = "other-deployment";

    // These should match what's configured in arquillian.xml for -Djboss.node.name of each server instance
    private static final String DEFAULT_JBOSSAS_NODE_NAME = "default-jbossas";

    private static final String JBOSSAS_WITH_OUTBOUND_CONNECTION_NODE_NAME = "jbossas-with-remote-outbound-connection";

    @ArquillianResource
    private ContainerController container;

    @ArquillianResource
    private Deployer deployer;

    private Context context;

    /**
     * 1) Start a server (A) which has a remote outbound connection to another server (B).
     * 2) Both server A and B have clustering capability and have a deployment containing a clustered SFSB.
     * 3) Server A furthermore has a non-clustered SFSB too.
     * 4) This test invokes on the non-clustered SFSB on server A which inturn invokes on the clustered
     * SFSB on server B (this is done by disabling local ejb receiver in the jboss-ejb-client.xml).
     * 5) Invocation works fine and at the same time (asynchronously) server B sends back a cluster topology
     * to server A containing, both server A and B and the nodes.
     * 6) The test then stops server B and invokes on the same non-clustered SFSB instance of server A, which inturn
     * invokes on the stateful clustered SFSB which it had injected earlier. This time since server B (which owns the session)
     * is down the invocation is expected to end up on server A itself (since server A is part of the cluster too)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testServerToServerClusterFormation() throws Exception {
        // First start the default server
        this.container.start(EJBClientClusterConfigurationTestCase.DEFAULT_JBOSSAS);
        // remove local authentication for applications at the server B
        final ModelControllerClient serverBClient = EJBClientClusterConfigurationTestCase.createModelControllerClient(EJBClientClusterConfigurationTestCase.DEFAULT_JBOSSAS);
        ModelControllerClient serverAClient = null;
        try {
            // deploy a deployment which contains a clustered EJB
            this.deployer.deploy(EJBClientClusterConfigurationTestCase.DEFAULT_AS_DEPLOYMENT);
            // start the other server
            this.container.start(EJBClientClusterConfigurationTestCase.JBOSSAS_WITH_REMOTE_OUTBOUND_CONNECTION);
            // setup system properties for jboss-ejb-client.xml before the deployment
            serverAClient = EJBClientClusterConfigurationTestCase.createModelControllerClient(EJBClientClusterConfigurationTestCase.JBOSSAS_WITH_REMOTE_OUTBOUND_CONNECTION);
            EJBClientClusterConfigurationTestCase.SystemPropertySetup.INSTANCE.setup(EJBClientClusterConfigurationTestCase.createManagementClient(serverAClient, EJBClientClusterConfigurationTestCase.JBOSSAS_WITH_REMOTE_OUTBOUND_CONNECTION), EJBClientClusterConfigurationTestCase.JBOSSAS_WITH_REMOTE_OUTBOUND_CONNECTION);
            // deploy the deployment containing a non-clustered EJB
            this.deployer.deploy(EJBClientClusterConfigurationTestCase.DEPLOYMENT_WITH_JBOSS_EJB_CLIENT_XML);
            // invoke on the non-clustered bean which internally calls the clustered bean on a remote server
            final NodeNameEcho nonClusteredBean = ((NodeNameEcho) (this.context.lookup((((((("ejb:/" + (EJBClientClusterConfigurationTestCase.MODULE_NAME)) + "//") + (NonClusteredStatefulNodeNameEcho.class.getSimpleName())) + "!") + (NodeNameEcho.class.getName())) + "?stateful"))));
            final String nodeNameBeforeShutdown = nonClusteredBean.getNodeName(true);
            Assert.assertEquals("EJB invocation ended up on unexpected node", EJBClientClusterConfigurationTestCase.DEFAULT_JBOSSAS_NODE_NAME, nodeNameBeforeShutdown);
            // now shutdown the default server
            this.container.stop(EJBClientClusterConfigurationTestCase.DEFAULT_JBOSSAS);
            // now invoke again. this time the internal invocation on the clustered bean should end up on
            // one of the cluster nodes instead of the default server, since it was shutdown
            final String nodeNameAfterShutdown = nonClusteredBean.getNodeName(false);
            Assert.assertEquals("EJB invocation ended up on unexpected node after shutdown", EJBClientClusterConfigurationTestCase.JBOSSAS_WITH_OUTBOUND_CONNECTION_NODE_NAME, nodeNameAfterShutdown);
        } finally {
            try {
                this.deployer.undeploy(EJBClientClusterConfigurationTestCase.DEPLOYMENT_WITH_JBOSS_EJB_CLIENT_XML);
                EJBClientClusterConfigurationTestCase.SystemPropertySetup.INSTANCE.tearDown(EJBClientClusterConfigurationTestCase.createManagementClient(serverAClient, EJBClientClusterConfigurationTestCase.JBOSSAS_WITH_REMOTE_OUTBOUND_CONNECTION), EJBClientClusterConfigurationTestCase.JBOSSAS_WITH_REMOTE_OUTBOUND_CONNECTION);
            } catch (Exception e) {
                EJBClientClusterConfigurationTestCase.logger.trace("Exception during container shutdown", e);
            } finally {
                this.container.stop(EJBClientClusterConfigurationTestCase.JBOSSAS_WITH_REMOTE_OUTBOUND_CONNECTION);
            }
            try {
                if (!(this.container.isStarted(EJBClientClusterConfigurationTestCase.DEFAULT_JBOSSAS))) {
                    // start the default server again so that we can clean up the deployment first
                    this.container.start(EJBClientClusterConfigurationTestCase.DEFAULT_JBOSSAS);
                }
                this.deployer.undeploy(EJBClientClusterConfigurationTestCase.DEFAULT_AS_DEPLOYMENT);
            } catch (Exception e) {
                EJBClientClusterConfigurationTestCase.logger.trace("Exception during container shutdown", e);
            } finally {
                this.container.stop(EJBClientClusterConfigurationTestCase.DEFAULT_JBOSSAS);
            }
            if (serverAClient != null) {
                serverAClient.close();
            }
            serverBClient.close();
        }
    }

    static class SystemPropertySetup implements ServerSetupTask {
        public static final EJBClientClusterConfigurationTestCase.SystemPropertySetup INSTANCE = new EJBClientClusterConfigurationTestCase.SystemPropertySetup();

        @Override
        public void setup(final ManagementClient managementClient, final String containerId) throws Exception {
            final ModelControllerClient client = managementClient.getControllerClient();
            ModelNode operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.outbound-connection-ref", ADD);
            operation.get("value").set("remote-ejb-connection");
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.exclude-local-receiver", ADD);
            operation.get("value").set("true");
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.cluster-name", ADD);
            operation.get("value").set("ejb");
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.max-allowed-connected-nodes", ADD);
            operation.get("value").set("20");
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.cluster-node-selector", ADD);
            operation.get("value").set("org.jboss.as.test.manualmode.ejb.client.cluster.ApplicationSpecificClusterNodeSelector");
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.connect-timeout", ADD);
            operation.get("value").set("15000");
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.username", ADD);
            operation.get("value").set("user1");
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.security-realm", ADD);
            operation.get("value").set("PasswordRealm");
            Utils.applyUpdate(operation, client);
        }

        @Override
        public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
            final ModelControllerClient client = managementClient.getControllerClient();
            ModelNode operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.outbound-connection-ref", REMOVE);
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.exclude-local-receiver", REMOVE);
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.cluster-name", REMOVE);
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.max-allowed-connected-nodes", REMOVE);
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.cluster-node-selector", REMOVE);
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.connect-timeout", REMOVE);
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.username", REMOVE);
            Utils.applyUpdate(operation, client);
            operation = ModelUtil.createOpNode("system-property=EJBClientClusterConfigurationTestCase.security-realm", REMOVE);
            Utils.applyUpdate(operation, client);
        }
    }
}

