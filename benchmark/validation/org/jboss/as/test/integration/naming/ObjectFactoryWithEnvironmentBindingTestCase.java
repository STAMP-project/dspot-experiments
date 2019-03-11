/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.naming;


import NamingExtension.SUBSYSTEM_NAME;
import java.util.Map;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case for binding of {@link ObjectFactory} with environment properties (see AS7-4575). The test case deploys a module,
 * containing the object factory class, and then uses the {@link ManagementClient} to bind it. The factory, when invoked to
 * retrieve an instance, verifies that the env properties are the ones used in the binding management operation.
 *
 * @author Eduardo Martins
 */
@RunWith(Arquillian.class)
@ServerSetup(ObjectFactoryWithEnvironmentBindingTestCase.ObjectFactoryWithEnvironmentBindingTestCaseServerSetup.class)
public class ObjectFactoryWithEnvironmentBindingTestCase {
    private static Logger LOGGER = Logger.getLogger(ObjectFactoryWithEnvironmentBindingTestCase.class);

    // caution, must match module.xml
    private static final String MODULE_NAME = "objectFactoryWithEnvironmentBindingModule";

    private static final String MODULE_JAR_NAME = "objectFactoryWithEnvironmentBinding.jar";

    // the environment properties used in the binding operation
    private static final Map<String, String> ENVIRONMENT_PROPERTIES = ObjectFactoryWithEnvironmentBindingTestCase.getEnvironmentProperties();

    static class ObjectFactoryWithEnvironmentBindingTestCaseServerSetup implements ServerSetupTask {
        @Override
        public void setup(final ManagementClient managementClient, final String containerId) throws Exception {
            ObjectFactoryWithEnvironmentBindingTestCase.deployModule();
            // bind the object factory
            final ModelNode address = createAddress();
            final ModelNode bindingAdd = new ModelNode();
            bindingAdd.get(OP).set(ADD);
            bindingAdd.get(OP_ADDR).set(address);
            bindingAdd.get(BINDING_TYPE).set(OBJECT_FACTORY);
            bindingAdd.get(MODULE).set(ObjectFactoryWithEnvironmentBindingTestCase.MODULE_NAME);
            bindingAdd.get(CLASS).set(ObjectFactoryWithEnvironmentBinding.class.getName());
            final ModelNode environment = new ModelNode();
            for (Map.Entry<String, String> property : ObjectFactoryWithEnvironmentBindingTestCase.ENVIRONMENT_PROPERTIES.entrySet()) {
                environment.add(property.getKey(), property.getValue());
            }
            bindingAdd.get(ENVIRONMENT).set(environment);
            final ModelNode addResult = managementClient.getControllerClient().execute(bindingAdd);
            Assert.assertFalse(addResult.get(FAILURE_DESCRIPTION).toString(), addResult.get(FAILURE_DESCRIPTION).isDefined());
            ObjectFactoryWithEnvironmentBindingTestCase.LOGGER.trace("Object factory bound.");
        }

        private ModelNode createAddress() {
            final ModelNode address = new ModelNode();
            address.add(SUBSYSTEM, SUBSYSTEM_NAME);
            address.add(BINDING, "java:global/b");
            return address;
        }

        @Override
        public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
            try {
                // unbind the object factory
                final ModelNode bindingRemove = new ModelNode();
                bindingRemove.get(OP).set(REMOVE);
                bindingRemove.get(OP_ADDR).set(createAddress());
                bindingRemove.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
                final ModelNode removeResult = managementClient.getControllerClient().execute(bindingRemove);
                Assert.assertFalse(removeResult.get(FAILURE_DESCRIPTION).toString(), removeResult.get(FAILURE_DESCRIPTION).isDefined());
                ObjectFactoryWithEnvironmentBindingTestCase.LOGGER.trace("Object factory unbound.");
            } finally {
                ObjectFactoryWithEnvironmentBindingTestCase.undeployModule();
                ObjectFactoryWithEnvironmentBindingTestCase.LOGGER.trace("Module undeployed.");
            }
        }
    }

    @Test
    public void testBindingWithEnvironment() throws Exception {
        InitialContext context = new InitialContext();
        Assert.assertEquals("v1", context.lookup("java:global/b"));
    }
}

