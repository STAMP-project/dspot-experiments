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
package org.jboss.as.test.integration.jca.beanvalidation;


import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.jca.JcaMgmtBase;
import org.jboss.as.test.integration.jca.JcaMgmtServerSetupTask;
import org.jboss.dmr.ModelNode;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="vrastsel@redhat.com">Vladimir Rastseluev</a>
JBQA-6006 - disabled bean validation
 */
@RunWith(Arquillian.class)
@ServerSetup(DisabledValidationTestCase.DisabledValidationTestCaseSetup.class)
@RunAsClient
public class DisabledValidationTestCase extends JcaMgmtBase {
    static class DisabledValidationTestCaseSetup extends JcaMgmtServerSetupTask {
        ModelNode bvAddress = JcaMgmtBase.subsystemAddress.clone().add("bean-validation", "bean-validation");

        @Override
        protected void doSetup(ManagementClient managementClient) throws Exception {
            writeAttribute(bvAddress, "enabled", "false");
        }
    }

    @ArquillianResource
    Deployer deployer;

    @Test
    public void testWrongAO() {
        test("wrong-ao");
    }

    @Test
    public void testWrongCf() {
        test("wrong-cf");
    }

    @Test
    public void testWrongRA() {
        test("wrong-ra");
    }
}

