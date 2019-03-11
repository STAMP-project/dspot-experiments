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
package org.jboss.as.test.integration.jca.ijdeployment;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.dmr.ModelNode;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test of two ear ij deployments conflict.
 *
 * @author baranowb
 */
@RunWith(Arquillian.class)
@RunAsClient
public class IronJacamarDoubleDeploymentTestCase extends ContainerResourceMgmtTestBase {
    private static final String deploymentName = "test-ij.ear";

    private static final String deployment2Name = "test-ij2.ear";

    private static final String deploymentConfigName = "ironjacamar.xml";

    private static final String deployment2ConfigName = "ironjacamar-2.xml";

    private static final String subDeploymentName = "ij.rar";

    /**
     *
     */
    @Test
    public void testEarConfiguration() throws Throwable {
        ModelNode address = getAddress(IronJacamarDoubleDeploymentTestCase.deploymentName);
        final ModelNode operation = new ModelNode();
        operation.get(OP).set("read-resource");
        operation.get(OP_ADDR).set(address);
        operation.get(RECURSIVE).set(true);
        executeOperation(operation);
        address = getAddress(IronJacamarDoubleDeploymentTestCase.deployment2Name);
        operation.get(OP_ADDR).set(address);
        executeOperation(operation);
    }
}

