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
package org.jboss.as.test.smoke.deployment.rar.tests.eardeployment;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.as.test.smoke.deployment.RaServlet;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="vrastsel@redhat.com">Vladimir Rastseluev</a>
JBQA-5828 RAR inside EAR
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EarDeploymentTestCase extends ContainerResourceMgmtTestBase {
    @ArquillianResource
    private ManagementClient managementClient;

    @ArquillianResource
    private URL url;

    static String subdeploymentName = "complex_ij.rar";

    static String deploymentName = "new.ear";

    /**
     * Test configuration
     *
     * @throws Throwable
     * 		Thrown if case of an error
     */
    @Test
    public void testWebConfiguration() throws Throwable {
        URL servletURL = new URL(((("http://" + (url.getHost())) + ":8080/servlet") + (RaServlet.URL_PATTERN)));
        BufferedReader br = new BufferedReader(new InputStreamReader(servletURL.openStream(), StandardCharsets.UTF_8));
        String message = br.readLine();
        Assert.assertEquals(RaServlet.SUCCESS, message);
    }

    @Test
    public void testConfiguration() throws Throwable {
        Assert.assertNotNull("Deployment metadata for ear not found", managementClient.getProtocolMetaData(EarDeploymentTestCase.deploymentName));
        final ModelNode address = new ModelNode();
        address.add("deployment", EarDeploymentTestCase.deploymentName).add("subdeployment", EarDeploymentTestCase.subdeploymentName).add("subsystem", "resource-adapters");
        address.protect();
        final ModelNode snapshot = new ModelNode();
        snapshot.get(OP).set("read-resource");
        snapshot.get("recursive").set(true);
        snapshot.get(OP_ADDR).set(address);
        executeOperation(snapshot);
    }
}

