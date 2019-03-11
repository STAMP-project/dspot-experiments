/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.smoke.deployment.rar.tests.afterresourcecreation;


import javax.naming.Context;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.management.base.AbstractMgmtServerSetupTask;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.as.test.smoke.deployment.rar.MultipleAdminObject1;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="vrastsel@redhat.com">Vladimir Rastseluev</a>
JBQA-5841 test for RA activation
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(AfterResourceCreationDeploymentTestCase.AfterResourceCreationDeploymentTestCaseSetup.class)
public class AfterResourceCreationDeploymentTestCase extends ContainerResourceMgmtTestBase {
    static String deploymentName = "basic-after.rar";

    @ContainerResource
    private Context context;

    static class AfterResourceCreationDeploymentTestCaseSetup extends AbstractMgmtServerSetupTask {
        @Override
        public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
            final ModelNode address = new ModelNode();
            address.add("subsystem", "resource-adapters");
            address.add("resource-adapter", AfterResourceCreationDeploymentTestCase.deploymentName);
            address.protect();
            remove(address);
        }

        @Override
        protected void doSetup(final ManagementClient managementClient) throws Exception {
        }
    }

    /**
     * Test configuration
     *
     * @throws Throwable
     * 		Thrown if case of an error
     */
    @Test
    public void testConfiguration() throws Throwable {
        setup();
        MultipleAdminObject1 adminObject1 = ((MultipleAdminObject1) (context.lookup("after/Name3")));
        Assert.assertNotNull("AO1 not found", adminObject1);
    }
}

