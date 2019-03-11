/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.logging;


import java.util.Deque;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that deployments that have a {@code logging.properties} file are configured correctly.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class LoggingDeploymentResourceTestCase {
    private static final String WAR_DEPLOYMENT_NAME = "logging-test-war.war";

    private static final String EAR_DEPLOYMENT_NAME = "logging-test-ear.ear";

    private static final String EAR_WAR_DEPLOYMENT_NAME = "logging-test-ear.war";

    private static final String EAR_PARENT_DEPLOYMENT_NAME = "logging-test-parent-ear.ear";

    private static final String EAR_CHILD_WAR_DEPLOYMENT_NAME = "logging-test-child-ear.war";

    @ArquillianResource
    private static ManagementClient client;

    @OperateOnDeployment(LoggingDeploymentResourceTestCase.WAR_DEPLOYMENT_NAME)
    @Test
    public void testWarDeploymentConfigurationResource() throws Exception {
        final ModelNode loggingConfiguration = LoggingDeploymentResourceTestCase.readDeploymentResource(LoggingDeploymentResourceTestCase.WAR_DEPLOYMENT_NAME, ((LoggingDeploymentResourceTestCase.WAR_DEPLOYMENT_NAME) + "/META-INF/logging.properties"));
        // The address should have logging.properties
        final Deque<Property> resultAddress = new java.util.ArrayDeque(Operations.getOperationAddress(loggingConfiguration).asPropertyList());
        Assert.assertTrue("The configuration path did not include logging.properties", resultAddress.getLast().getValue().asString().contains("logging.properties"));
        final ModelNode handler = loggingConfiguration.get("handler", "FILE");
        Assert.assertTrue("The FILE handler was not found effective configuration", handler.isDefined());
        Assert.assertTrue(handler.hasDefined("properties"));
        String fileName = null;
        // Find the fileName property
        for (Property property : handler.get("properties").asPropertyList()) {
            if ("fileName".equals(property.getName())) {
                fileName = property.getValue().asString();
                break;
            }
        }
        Assert.assertNotNull("fileName property not found", fileName);
        Assert.assertTrue(fileName.endsWith("test-logging-war.log"));
    }

    @OperateOnDeployment(LoggingDeploymentResourceTestCase.EAR_DEPLOYMENT_NAME)
    @Test
    public void testEarDeploymentConfigurationResource() throws Exception {
        ModelNode loggingConfiguration = LoggingDeploymentResourceTestCase.readDeploymentResource(LoggingDeploymentResourceTestCase.EAR_DEPLOYMENT_NAME, ((LoggingDeploymentResourceTestCase.EAR_DEPLOYMENT_NAME) + "/META-INF/logging.properties"));
        // The address should have logging.properties
        Deque<Property> resultAddress = new java.util.ArrayDeque(Operations.getOperationAddress(loggingConfiguration).asPropertyList());
        Assert.assertTrue("The configuration path did not include logging.properties", resultAddress.getLast().getValue().asString().contains("logging.properties"));
        ModelNode handler = loggingConfiguration.get("handler", "FILE");
        Assert.assertTrue("The FILE handler was not found effective configuration", handler.isDefined());
        Assert.assertTrue(handler.hasDefined("properties"));
        String fileName = null;
        // Find the fileName property
        for (Property property : handler.get("properties").asPropertyList()) {
            if ("fileName".equals(property.getName())) {
                fileName = property.getValue().asString();
                break;
            }
        }
        Assert.assertNotNull("fileName property not found", fileName);
        Assert.assertTrue(fileName.endsWith("test-logging-ear.log"));
        // Check the WAR which should inherit the EAR's logging.properties
        loggingConfiguration = LoggingDeploymentResourceTestCase.readSubDeploymentResource(LoggingDeploymentResourceTestCase.EAR_DEPLOYMENT_NAME, LoggingDeploymentResourceTestCase.EAR_WAR_DEPLOYMENT_NAME, ((LoggingDeploymentResourceTestCase.EAR_DEPLOYMENT_NAME) + "/META-INF/logging.properties"));
        // The address should have logging.properties
        resultAddress = new java.util.ArrayDeque(Operations.getOperationAddress(loggingConfiguration).asPropertyList());
        Assert.assertTrue("The configuration path did not include logging.properties", resultAddress.getLast().getValue().asString().contains("logging.properties"));
        handler = loggingConfiguration.get("handler", "FILE");
        Assert.assertTrue("The FILE handler was not found effective configuration", handler.isDefined());
        Assert.assertTrue(handler.hasDefined("properties"));
        fileName = null;
        // Find the fileName property
        for (Property property : handler.get("properties").asPropertyList()) {
            if ("fileName".equals(property.getName())) {
                fileName = property.getValue().asString();
                break;
            }
        }
        Assert.assertNotNull("fileName property not found", fileName);
        Assert.assertTrue(fileName.endsWith("test-logging-ear.log"));
    }

    @OperateOnDeployment(LoggingDeploymentResourceTestCase.EAR_PARENT_DEPLOYMENT_NAME)
    @Test
    public void testDeploymentConfigurationResource() throws Exception {
        ModelNode loggingConfiguration = LoggingDeploymentResourceTestCase.readDeploymentResource(LoggingDeploymentResourceTestCase.EAR_PARENT_DEPLOYMENT_NAME, ((LoggingDeploymentResourceTestCase.EAR_PARENT_DEPLOYMENT_NAME) + "/META-INF/logging.properties"));
        // The address should have logging.properties
        Deque<Property> resultAddress = new java.util.ArrayDeque(Operations.getOperationAddress(loggingConfiguration).asPropertyList());
        Assert.assertTrue("The configuration path did not include logging.properties", resultAddress.getLast().getValue().asString().contains("logging.properties"));
        ModelNode handler = loggingConfiguration.get("handler", "FILE");
        Assert.assertTrue("The FILE handler was not found effective configuration", handler.isDefined());
        Assert.assertTrue(handler.hasDefined("properties"));
        String fileName = null;
        // Find the fileName property
        for (Property property : handler.get("properties").asPropertyList()) {
            if ("fileName".equals(property.getName())) {
                fileName = property.getValue().asString();
                break;
            }
        }
        Assert.assertNotNull("fileName property not found", fileName);
        Assert.assertTrue(fileName.endsWith("test-logging-parent-ear.log"));
        // Check the WAR which should have it's own configuration
        loggingConfiguration = LoggingDeploymentResourceTestCase.readSubDeploymentResource(LoggingDeploymentResourceTestCase.EAR_PARENT_DEPLOYMENT_NAME, LoggingDeploymentResourceTestCase.EAR_CHILD_WAR_DEPLOYMENT_NAME, ((LoggingDeploymentResourceTestCase.EAR_CHILD_WAR_DEPLOYMENT_NAME) + "/META-INF/logging.properties"));
        // The address should have logging.properties
        resultAddress = new java.util.ArrayDeque(Operations.getOperationAddress(loggingConfiguration).asPropertyList());
        Assert.assertTrue("The configuration path did not include logging.properties", resultAddress.getLast().getValue().asString().contains("logging.properties"));
        handler = loggingConfiguration.get("handler", "FILE");
        Assert.assertTrue("The FILE handler was not found effective configuration", handler.isDefined());
        Assert.assertTrue(handler.hasDefined("properties"));
        fileName = null;
        // Find the fileName property
        for (Property property : handler.get("properties").asPropertyList()) {
            if ("fileName".equals(property.getName())) {
                fileName = property.getValue().asString();
                break;
            }
        }
        Assert.assertNotNull("fileName property not found", fileName);
        Assert.assertTrue(fileName.endsWith("test-logging-child-war.log"));
    }
}

