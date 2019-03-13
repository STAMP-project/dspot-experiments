/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.manualmode.ws;


import java.net.URL;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Some tests on changes to the model requiring reload
 *
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ReloadRequiringChangesTestCase {
    private static final String DEFAULT_JBOSSAS = "default-jbossas";

    private static final String DEPLOYMENT = "jaxws-manual-pojo";

    @ArquillianResource
    ContainerController containerController;

    @ArquillianResource
    Deployer deployer;

    @Test
    @OperateOnDeployment(ReloadRequiringChangesTestCase.DEPLOYMENT)
    public void testWSDLHostChangeRequiresReloadAndDoesNotAffectRuntime() throws Exception {
        Assert.assertTrue(containerController.isStarted(ReloadRequiringChangesTestCase.DEFAULT_JBOSSAS));
        ManagementClient managementClient = new ManagementClient(TestSuiteEnvironment.getModelControllerClient(), TestSuiteEnvironment.getServerAddress(), TestSuiteEnvironment.getServerPort(), "remote+http");
        ModelControllerClient client = managementClient.getControllerClient();
        String initialWsdlHost = null;
        try {
            initialWsdlHost = getWsdlHost(client);
            // change wsdl-host to "foo-host" and reload
            final String hostname = "foo-host";
            setWsdlHost(client, hostname);
            ServerReload.executeReloadAndWaitForCompletion(client);
            // change wsdl-host to "bar-host" and verify deployment still uses "foo-host"
            setWsdlHost(client, "bar-host");
            URL wsdlURL = new URL(managementClient.getWebUri().toURL(), (('/' + (ReloadRequiringChangesTestCase.DEPLOYMENT)) + "/POJOService?wsdl"));
            checkWsdl(wsdlURL, hostname);
        } finally {
            try {
                if (initialWsdlHost != null) {
                    setWsdlHost(client, initialWsdlHost);
                }
            } finally {
                managementClient.close();
            }
        }
    }

    @Test
    @OperateOnDeployment(ReloadRequiringChangesTestCase.DEPLOYMENT)
    public void testWSDLHostUndefineRequiresReloadAndDoesNotAffectRuntime() throws Exception {
        Assert.assertTrue(containerController.isStarted(ReloadRequiringChangesTestCase.DEFAULT_JBOSSAS));
        ManagementClient managementClient = new ManagementClient(TestSuiteEnvironment.getModelControllerClient(), TestSuiteEnvironment.getServerAddress(), TestSuiteEnvironment.getServerPort(), "remote+http");
        ModelControllerClient client = managementClient.getControllerClient();
        String initialWsdlHost = null;
        try {
            initialWsdlHost = getWsdlHost(client);
            // change wsdl-host to "my-host" and reload
            final String hostname = "my-host";
            setWsdlHost(client, hostname);
            ServerReload.executeReloadAndWaitForCompletion(client);
            // undefine wsdl-host and verify deployment still uses "foo-host"
            setWsdlHost(client, null);
            URL wsdlURL = new URL(managementClient.getWebUri().toURL(), (('/' + (ReloadRequiringChangesTestCase.DEPLOYMENT)) + "/POJOService?wsdl"));
            checkWsdl(wsdlURL, hostname);
        } finally {
            try {
                if (initialWsdlHost != null) {
                    setWsdlHost(client, initialWsdlHost);
                }
            } finally {
                managementClient.close();
            }
        }
    }
}

