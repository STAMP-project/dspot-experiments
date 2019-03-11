/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ee.concurrent;


import ContextServiceResourceDefinition.JNDI_NAME;
import EESubsystemModel.CONTEXT_SERVICE;
import EESubsystemModel.MANAGED_EXECUTOR_SERVICE;
import EESubsystemModel.MANAGED_SCHEDULED_EXECUTOR_SERVICE;
import EESubsystemModel.MANAGED_THREAD_FACTORY;
import EeExtension.SUBSYSTEM_NAME;
import ManagedExecutorServiceResourceDefinition.CORE_THREADS;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case for adding and removing EE concurrent resources.
 *
 * @author Eduardo Martins
 */
@RunWith(Arquillian.class)
public class EEConcurrentManagementTestCase {
    private static final PathAddress EE_SUBSYSTEM_PATH_ADDRESS = PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, SUBSYSTEM_NAME));

    private static final String RESOURCE_NAME = EEConcurrentManagementTestCase.class.getSimpleName();

    @ArquillianResource
    private ManagementClient managementClient;

    @Test
    public void testContextServiceManagement() throws Exception {
        final PathAddress pathAddress = EEConcurrentManagementTestCase.EE_SUBSYSTEM_PATH_ADDRESS.append(CONTEXT_SERVICE, EEConcurrentManagementTestCase.RESOURCE_NAME);
        // add
        final ModelNode addOperation = Util.createAddOperation(pathAddress);
        final String jndiName = "java:jboss/ee/concurrency/contextservice/" + (EEConcurrentManagementTestCase.RESOURCE_NAME);
        addOperation.get(JNDI_NAME).set(jndiName);
        final ModelNode addResult = managementClient.getControllerClient().execute(addOperation);
        Assert.assertFalse(addResult.get(FAILURE_DESCRIPTION).toString(), addResult.get(FAILURE_DESCRIPTION).isDefined());
        try {
            // lookup
            Assert.assertNotNull(new InitialContext().lookup(jndiName));
        } finally {
            // remove
            final ModelNode removeOperation = Util.createRemoveOperation(pathAddress);
            removeOperation.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            final ModelNode removeResult = managementClient.getControllerClient().execute(removeOperation);
            Assert.assertFalse(removeResult.get(FAILURE_DESCRIPTION).toString(), removeResult.get(FAILURE_DESCRIPTION).isDefined());
            try {
                new InitialContext().lookup(jndiName);
                Assert.fail();
            } catch (NameNotFoundException e) {
                // expected
            }
        }
    }

    @Test
    public void testManagedThreadFactoryManagement() throws Exception {
        final PathAddress pathAddress = EEConcurrentManagementTestCase.EE_SUBSYSTEM_PATH_ADDRESS.append(MANAGED_THREAD_FACTORY, EEConcurrentManagementTestCase.RESOURCE_NAME);
        // add
        final ModelNode addOperation = Util.createAddOperation(pathAddress);
        final String jndiName = "java:jboss/ee/concurrency/threadfactory/" + (EEConcurrentManagementTestCase.RESOURCE_NAME);
        addOperation.get(ManagedThreadFactoryResourceDefinition.JNDI_NAME).set(jndiName);
        final ModelNode addResult = managementClient.getControllerClient().execute(addOperation);
        Assert.assertFalse(addResult.get(FAILURE_DESCRIPTION).toString(), addResult.get(FAILURE_DESCRIPTION).isDefined());
        try {
            // lookup
            Assert.assertNotNull(new InitialContext().lookup(jndiName));
        } finally {
            // remove
            final ModelNode removeOperation = Util.createRemoveOperation(pathAddress);
            removeOperation.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            final ModelNode removeResult = managementClient.getControllerClient().execute(removeOperation);
            Assert.assertFalse(removeResult.get(FAILURE_DESCRIPTION).toString(), removeResult.get(FAILURE_DESCRIPTION).isDefined());
            try {
                new InitialContext().lookup(jndiName);
                Assert.fail();
            } catch (NameNotFoundException e) {
                // expected
            }
        }
    }

    @Test
    public void testManagedExecutorServiceManagement() throws Exception {
        final PathAddress pathAddress = EEConcurrentManagementTestCase.EE_SUBSYSTEM_PATH_ADDRESS.append(MANAGED_EXECUTOR_SERVICE, EEConcurrentManagementTestCase.RESOURCE_NAME);
        // add
        final ModelNode addOperation = Util.createAddOperation(pathAddress);
        final String jndiName = "java:jboss/ee/concurrency/executor/" + (EEConcurrentManagementTestCase.RESOURCE_NAME);
        addOperation.get(ManagedExecutorServiceResourceDefinition.JNDI_NAME).set(jndiName);
        addOperation.get(CORE_THREADS).set(2);
        final ModelNode addResult = managementClient.getControllerClient().execute(addOperation);
        Assert.assertFalse(addResult.get(FAILURE_DESCRIPTION).toString(), addResult.get(FAILURE_DESCRIPTION).isDefined());
        try {
            // lookup
            Assert.assertNotNull(new InitialContext().lookup(jndiName));
        } finally {
            // remove
            final ModelNode removeOperation = Util.createRemoveOperation(pathAddress);
            removeOperation.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            final ModelNode removeResult = managementClient.getControllerClient().execute(removeOperation);
            Assert.assertFalse(removeResult.get(FAILURE_DESCRIPTION).toString(), removeResult.get(FAILURE_DESCRIPTION).isDefined());
            try {
                new InitialContext().lookup(jndiName);
                Assert.fail();
            } catch (NameNotFoundException e) {
                // expected
            }
        }
    }

    @Test
    public void testManagedScheduledExecutorServiceManagement() throws Exception {
        final PathAddress pathAddress = EEConcurrentManagementTestCase.EE_SUBSYSTEM_PATH_ADDRESS.append(MANAGED_SCHEDULED_EXECUTOR_SERVICE, EEConcurrentManagementTestCase.RESOURCE_NAME);
        // add
        final ModelNode addOperation = Util.createAddOperation(pathAddress);
        final String jndiName = "java:jboss/ee/concurrency/scheduledexecutor/" + (EEConcurrentManagementTestCase.RESOURCE_NAME);
        addOperation.get(ManagedScheduledExecutorServiceResourceDefinition.JNDI_NAME).set(jndiName);
        addOperation.get(ManagedScheduledExecutorServiceResourceDefinition.CORE_THREADS).set(2);
        final ModelNode addResult = managementClient.getControllerClient().execute(addOperation);
        Assert.assertFalse(addResult.get(FAILURE_DESCRIPTION).toString(), addResult.get(FAILURE_DESCRIPTION).isDefined());
        try {
            // lookup
            Assert.assertNotNull(new InitialContext().lookup(jndiName));
        } finally {
            // remove
            final ModelNode removeOperation = Util.createRemoveOperation(pathAddress);
            removeOperation.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            final ModelNode removeResult = managementClient.getControllerClient().execute(removeOperation);
            Assert.assertFalse(removeResult.get(FAILURE_DESCRIPTION).toString(), removeResult.get(FAILURE_DESCRIPTION).isDefined());
            try {
                new InitialContext().lookup(jndiName);
                Assert.fail();
            } catch (NameNotFoundException e) {
                // expected
            }
        }
    }
}

