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
package org.jboss.as.test.integration.security.vault;


import BasicVaultServerSetupTask.VAULTED_PROPERTY;
import BasicVaultServerSetupTask.VAULT_ATTRIBUTE;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.security.common.BasicVaultServerSetupTask;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test whether vault can be used for system property.
 *
 * @author olukas
 */
@RunWith(Arquillian.class)
@ServerSetup({ BasicVaultServerSetupTask.class, VaultSystemPropertiesTestCase.SystemPropertySetup.class })
public class VaultSystemPropertiesTestCase {
    public static final String TESTING_SYSTEM_PROPERTY = "vault.testing.property";

    private static Logger LOGGER = Logger.getLogger(VaultSystemPropertiesTestCase.class);

    @Test
    public void testVaultedSystemProperty() {
        Assert.assertEquals("Vaulted system property wasn't read successfully", VAULT_ATTRIBUTE, System.getProperty(VaultSystemPropertiesTestCase.TESTING_SYSTEM_PROPERTY));
    }

    static class SystemPropertySetup implements ServerSetupTask {
        static final PathAddress SYSTEM_PROPERTIES_PATH = PathAddress.pathAddress().append(SYSTEM_PROPERTY, VaultSystemPropertiesTestCase.TESTING_SYSTEM_PROPERTY);

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            VaultSystemPropertiesTestCase.LOGGER.trace(("Add system property: " + (VaultSystemPropertiesTestCase.TESTING_SYSTEM_PROPERTY)));
            ModelNode op = Util.createAddOperation(VaultSystemPropertiesTestCase.SystemPropertySetup.SYSTEM_PROPERTIES_PATH);
            op.get(VALUE).set(VAULTED_PROPERTY);
            Utils.applyUpdate(op, managementClient.getControllerClient());
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            VaultSystemPropertiesTestCase.LOGGER.trace(("Remove system property: " + (VaultSystemPropertiesTestCase.TESTING_SYSTEM_PROPERTY)));
            ModelNode op = Util.createRemoveOperation(VaultSystemPropertiesTestCase.SystemPropertySetup.SYSTEM_PROPERTIES_PATH);
            Utils.applyUpdate(op, managementClient.getControllerClient());
        }
    }
}

