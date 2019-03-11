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
package org.jboss.as.test.integration.jca.bootstrap;


import javax.annotation.Resource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.jca.JcaMgmtBase;
import org.jboss.as.test.integration.jca.JcaMgmtServerSetupTask;
import org.jboss.as.test.integration.jca.rar.MultipleAdminObject1;
import org.jboss.as.test.integration.jca.rar.MultipleAdminObject1Impl;
import org.jboss.as.test.integration.jca.rar.MultipleConnectionFactory1;
import org.jboss.as.test.integration.jca.rar.MultipleResourceAdapter2;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="vrastsel@redhat.com">Vladimir Rastseluev</a> JBQA-5936 custom bootstrap context deployment
 */
@RunWith(Arquillian.class)
@ServerSetup(CustomBootstrapContextTestCase.CustomBootstrapDeploymentTestCaseSetup.class)
public class CustomBootstrapContextTestCase extends JcaMgmtBase {
    public static String ctx = "customContext";

    public static String wm = "customWM";

    static class CustomBootstrapDeploymentTestCaseSetup extends JcaMgmtServerSetupTask {
        ModelNode wmAddress = JcaMgmtBase.subsystemAddress.clone().add("workmanager", CustomBootstrapContextTestCase.wm);

        ModelNode bsAddress = JcaMgmtBase.subsystemAddress.clone().add("bootstrap-context", CustomBootstrapContextTestCase.ctx);

        @Override
        public void doSetup(final ManagementClient managementClient) throws Exception {
            ModelNode operation = new ModelNode();
            try {
                operation.get(OP).set(ADD);
                operation.get(OP_ADDR).set(wmAddress);
                operation.get(NAME).set(CustomBootstrapContextTestCase.wm);
                executeOperation(operation);
                operation = new ModelNode();
                operation.get(OP).set(ADD);
                operation.get(OP_ADDR).set(wmAddress.clone().add("short-running-threads", CustomBootstrapContextTestCase.wm));
                operation.get("core-threads").set("20");
                operation.get("queue-length").set("20");
                operation.get("max-threads").set("20");
                executeOperation(operation);
                operation = new ModelNode();
                operation.get(OP).set(ADD);
                operation.get(OP_ADDR).set(bsAddress);
                operation.get(NAME).set(CustomBootstrapContextTestCase.ctx);
                operation.get("workmanager").set(CustomBootstrapContextTestCase.wm);
                executeOperation(operation);
            } catch (Exception e) {
                throw new Exception(((e.getMessage()) + operation), e);
            }
        }
    }

    @Resource(mappedName = "java:jboss/name1")
    private MultipleConnectionFactory1 connectionFactory1;

    @Resource(mappedName = "java:jboss/Name3")
    private MultipleAdminObject1 adminObject1;

    /**
     * Test configuration
     *
     * @throws Throwable
     * 		Thrown if case of an error
     */
    @Test
    public void testConfiguration() throws Throwable {
        Assert.assertNotNull("CF1 not found", connectionFactory1);
        Assert.assertNotNull("AO1 not found", adminObject1);
        MultipleAdminObject1Impl impl = ((MultipleAdminObject1Impl) (adminObject1));
        MultipleResourceAdapter2 adapter = ((MultipleResourceAdapter2) (impl.getResourceAdapter()));
        Assert.assertNotNull(adapter);
        Assert.assertEquals(CustomBootstrapContextTestCase.wm, adapter.getWorkManagerName());
        Assert.assertEquals(CustomBootstrapContextTestCase.ctx, adapter.getBootstrapContextName());
    }
}

