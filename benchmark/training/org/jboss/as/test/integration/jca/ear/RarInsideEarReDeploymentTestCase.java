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
package org.jboss.as.test.integration.jca.ear;


import javax.naming.Context;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.test.integration.jca.rar.MultipleAdminObject1;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="vrastsel@redhat.com">Vladimir Rastseluev</a> JBQA-5968 test
for undeployment and re-deployment
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RarInsideEarReDeploymentTestCase extends ContainerResourceMgmtTestBase {
    static final String deploymentName = "re-deployment.ear";

    static String subDeploymentName = "ear_packaged.rar";

    private static ModelNode address;

    @ContainerResource
    private Context context;

    @ArquillianResource
    private Deployer deployer;

    /**
     * Test configuration
     *
     * @throws Throwable
     * 		Thrown if case of an error
     */
    @Test
    public void testConfiguration() throws Throwable {
        try {
            deployer.deploy(RarInsideEarReDeploymentTestCase.deploymentName);
            setup();
            deployer.undeploy(RarInsideEarReDeploymentTestCase.deploymentName);
            deployer.deploy(RarInsideEarReDeploymentTestCase.deploymentName);
            MultipleAdminObject1 adminObject1 = ((MultipleAdminObject1) (context.lookup("redeployed/Name3")));
            Assert.assertNotNull("AO1 not found", adminObject1);
        } finally {
            deployer.undeploy(RarInsideEarReDeploymentTestCase.deploymentName);
            remove(RarInsideEarReDeploymentTestCase.address);
        }
    }
}

