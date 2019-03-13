/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.manualmode.jms.resourceadapter;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test configures legacy security domain and resource adapter.
 * Test tries to start sever several times and checks it does not fail during the startup.
 * Test for [ WFLY-9978 ].
 *
 * @author Daniel Cihak
 */
@RunWith(Arquillian.class)
@ServerSetup(LegacySecurityDomainRAServerSetupTask.class)
@RunAsClient
public class LegacySecurityDomainRATestCase {
    private static final Logger LOG = Logger.getLogger(LegacySecurityDomainRATestCase.class);

    private static final String DEPLOYMENT = "deployment";

    private static final String CONTAINER = "jbossas-with-remote-outbound-connection-non-clustered";

    @ArquillianResource
    private ContainerController controller;

    @ArquillianResource
    private Deployer deployer;

    @Test
    public void testRepeatedlyStartServerWithLegacySecurityDomain() {
        for (int i = 0; i <= 20; i++) {
            PrintStream oldOut = System.out;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                System.setOut(new PrintStream(baos));
                controller.start(LegacySecurityDomainRATestCase.CONTAINER);
                LegacySecurityDomainRATestCase.LOG.trace("===appserver started===");
                deployer.deploy(LegacySecurityDomainRATestCase.DEPLOYMENT);
                LegacySecurityDomainRATestCase.LOG.trace("===deployment deployed===");
                try {
                    if (controller.isStarted(LegacySecurityDomainRATestCase.CONTAINER)) {
                        System.setOut(oldOut);
                        String output = new String(baos.toByteArray());
                        Assert.assertFalse(output, output.contains("ERROR"));
                        Assert.assertFalse(output, output.contains("MSC000001"));
                    } else {
                        Assert.fail((("Container " + (LegacySecurityDomainRATestCase.CONTAINER)) + " could not be started."));
                    }
                } finally {
                    deployer.undeploy(LegacySecurityDomainRATestCase.DEPLOYMENT);
                    controller.stop(LegacySecurityDomainRATestCase.CONTAINER);
                }
            } finally {
                System.setOut(oldOut);
            }
        }
    }
}

