/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.mdb.cdi;


import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that the CDI request scope is active in MDB invocations.
 *
 * @author baranowb
 */
@RunWith(Arquillian.class)
@ServerSetup({ MDBRAScopeCdiIntegrationTestCase.JmsQueueSetup.class })
public class MDBRAScopeCdiIntegrationTestCase extends ContainerResourceMgmtTestBase {
    public static final String testDeploymentName = "test.jar";

    public static final String deploymentName = "test-ear.ear";

    public static final String subDeploymentName = "ear_packaged.rar";

    private ModelNode raAddress_subdeployment;

    private ModelNode raAddress_regular;

    static class JmsQueueSetup implements ServerSetupTask {
        private JMSOperations jmsAdminOperations;

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            jmsAdminOperations = JMSOperationsProvider.getInstance(managementClient);
            jmsAdminOperations.createJmsQueue("mdb-cdi-test/queue", MDBProxy.QUEUE_JNDI_NAME);
            jmsAdminOperations.createJmsQueue("mdb-cdi-test/reply-queue", MDBProxy.REPLY_QUEUE_JNDI_NAME);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            if ((jmsAdminOperations) != null) {
                jmsAdminOperations.removeJmsQueue("mdb-cdi-test/queue");
                jmsAdminOperations.removeJmsQueue("mdb-cdi-test/reply-queue");
                jmsAdminOperations.close();
            }
        }
    }

    @Test
    public void testMe() throws Exception {
        Assert.assertNotNull(getModelControllerClient());
        try {
            // deployer.deploy(deploymentName);
            MDBProxy mdbProxy = ((MDBProxy) (MDBRAScopeCdiIntegrationTestCase.getInitialContext().lookup(MDBRAScopeCdiIntegrationTestCase.getEJBJNDIBinding())));
            mdbProxy.trigger();
        } finally {
            try {
            } finally {
                // deployer.undeploy(deploymentName);
            }
        }
    }
}

