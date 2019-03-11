/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.manualmode.security;


import BasicVaultServerSetupTask.VAULT_ATTRIBUTE;
import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.as.test.integration.security.common.BasicVaultServerSetupTask;
import org.jboss.as.test.integration.security.common.servlets.PrintSystemPropertyServlet;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test whether server starts when system property contain vault value.
 *
 * @author olukas
 */
@RunWith(Arquillian.class)
public class VaultSystemPropertyOnServerStartTestCase {
    private static Logger LOGGER = Logger.getLogger(VaultSystemPropertyOnServerStartTestCase.class);

    @ArquillianResource
    private ContainerController container;

    @ArquillianResource
    private Deployer deployer;

    private static final String CONTAINER = "default-jbossas";

    private static final String DEPLOYMENT = "test-deployment";

    private ManagementClient managementClient;

    private BasicVaultServerSetupTask serverSetup = new BasicVaultServerSetupTask();

    public static final String TESTING_SYSTEM_PROPERTY = "vault.testing.property";

    public static final PathAddress SYSTEM_PROPERTIES_PATH = PathAddress.pathAddress().append(SYSTEM_PROPERTY, VaultSystemPropertyOnServerStartTestCase.TESTING_SYSTEM_PROPERTY);

    private static final String printPropertyServlet = ((((((("http://" + (TestSuiteEnvironment.getServerAddress())) + ":8080/") + (VaultSystemPropertyOnServerStartTestCase.DEPLOYMENT)) + (PrintSystemPropertyServlet.SERVLET_PATH)) + "?") + (PrintSystemPropertyServlet.PARAM_PROPERTY_NAME)) + "=") + (VaultSystemPropertyOnServerStartTestCase.TESTING_SYSTEM_PROPERTY);

    @Test
    public void testVaultedSystemPropertyOnStart() throws Exception {
        VaultSystemPropertyOnServerStartTestCase.LOGGER.trace("*** starting server");
        container.start(VaultSystemPropertyOnServerStartTestCase.CONTAINER);
        deployer.deploy(VaultSystemPropertyOnServerStartTestCase.DEPLOYMENT);
        VaultSystemPropertyOnServerStartTestCase.LOGGER.trace(("Try to access " + (VaultSystemPropertyOnServerStartTestCase.printPropertyServlet)));
        String response = HttpRequest.get(VaultSystemPropertyOnServerStartTestCase.printPropertyServlet, 10, TimeUnit.SECONDS);
        Assert.assertTrue("Vaulted system property wasn't read successfully", response.contains(VAULT_ATTRIBUTE));
        deployer.undeploy(VaultSystemPropertyOnServerStartTestCase.DEPLOYMENT);
    }
}

