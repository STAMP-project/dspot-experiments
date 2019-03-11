/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2014, Red Hat, Inc., and individual contributors
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


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Set;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.security.vault.VaultSession;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.integration.security.common.VaultHandler;
import org.jboss.as.test.integration.security.loginmodules.ExternalPasswordProvider;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.jboss.security.util.StringPropertyReplacer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for Security Vault external passwords, obtained from external command
 * or class. Each test case initialize a test vault with different password
 * type.
 *
 * @author Filip Bogyai
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup({ ExternalPasswordModuleSetupTask.class, ExternalPasswordCommandsTestCase.ExternalVaultPasswordSetup.class })
public class ExternalPasswordCommandsTestCase {
    private static Logger LOGGER = Logger.getLogger(ExternalPasswordCommandsTestCase.class);

    @ArquillianResource
    private ManagementClient managementClient;

    @ArquillianResource
    private URL url;

    public static final String ATTRIBUTE_NAME = "someAttributeName";

    public static final String VAULT_BLOCK = "someVaultBlockaj";

    public static final String VAULT_ATTRIBUTE = "secretValue";

    public static final String VAULTED_PROPERTY = ((("VAULT::" + (ExternalPasswordCommandsTestCase.VAULT_BLOCK)) + "::") + (ExternalPasswordCommandsTestCase.ATTRIBUTE_NAME)) + "::1";

    public static String KEYSTORE_URL = "vault.keystore";

    public static final String VAULT_PASSWORD = "VaultPassword";

    public static final String VAULT_ALIAS = "vault";

    public static String RESOURCE_LOCATION = "";

    public static final String VAULT_DAT_FILE = (ExternalPasswordCommandsTestCase.RESOURCE_LOCATION) + "VAULT.dat";

    public static final String SALT = "87654321";

    public static final int ITER_COUNT = 47;

    private static final ExternalPasswordProvider passwordProvider = new ExternalPasswordProvider((((System.getProperty("java.io.tmpdir")) + (File.separator)) + "tmp.counter"));

    public static final PathAddress VAULT_PATH = PathAddress.pathAddress().append(CORE_SERVICE, VAULT);

    /**
     * Testing {EXT} option password as a exact command to execute. The exact
     * command is a string delimited by ' '.
     */
    @Test
    public void testExtPassword() throws Exception {
        createVault(buildCommand("EXT", " "));
        Assert.assertEquals(ExternalPasswordCommandsTestCase.VAULT_ATTRIBUTE, getVaultedPassword());
        removeVault();
    }

    /**
     * Testing {CMD} option password as a general command to execute. The
     * general command is a string delimited by ',' where the first part is the
     * actual command and further parts represents its parameters.
     */
    @Test
    public void testCmdPassword() throws Exception {
        createVault(buildCommand("CMD", ","));
        Assert.assertEquals(ExternalPasswordCommandsTestCase.VAULT_ATTRIBUTE, getVaultedPassword());
        removeVault();
    }

    /**
     * Testing {CLASS[@module_name]}classname' option of password where the
     * the class @ExternalPassword is loaded from custom module and toCharArray()
     * method return password.
     */
    @Test
    public void testCustomModuleClassPassword() throws Exception {
        createVault(((("{CLASS@" + (ExternalPasswordModuleSetupTask.getModuleName())) + "}") + (ExternalPassword.class.getName())));
        Assert.assertEquals(ExternalPasswordCommandsTestCase.VAULT_ATTRIBUTE, getVaultedPassword());
        removeVault();
    }

    /**
     * Testing {CLASS[@module_name]}classname[:ctorargs]' option of password
     * where the the class @ExternalPassword is loaded from custom module and
     * toCharArray() method return password. The class constructor takes two
     * arguments, which will be used to construct the password.
     */
    @Test
    public void testCustomModuleClassWithArguments() throws Exception {
        createVault((((("{CLASS@" + (ExternalPasswordModuleSetupTask.getModuleName())) + "}") + (ExternalPassword.class.getName())) + ":Vault,Password"));
        Assert.assertEquals(ExternalPasswordCommandsTestCase.VAULT_ATTRIBUTE, getVaultedPassword());
        removeVault();
    }

    /**
     * TestingTesting {CLASS[@module_name]}classname[:ctorargs]' option password
     * where the '[:ctorargs]' is an optional string delimited by the ':' from
     * the classname that will be passed to the class constructor. The class
     *
     * @unknown constructor takes one argument with file, in which the password
    is stored password.
     */
    @Test
    public void testPicketboxClassPassword() throws Exception {
        File tmpPassword = new File(System.getProperty("java.io.tmpdir"), "tmp.password");
        Files.write(tmpPassword.toPath(), ExternalPasswordCommandsTestCase.VAULT_PASSWORD.getBytes(StandardCharsets.UTF_8));
        String passwordCmd = "{CLASS@org.picketbox}org.jboss.security.plugins.TmpFilePassword:${java.io.tmpdir}/tmp.password";
        passwordCmd = StringPropertyReplacer.replaceProperties(passwordCmd);
        createVault(passwordCmd);
        Assert.assertEquals(ExternalPasswordCommandsTestCase.VAULT_ATTRIBUTE, getVaultedPassword());
        removeVault();
        tmpPassword.delete();
    }

    /**
     * Testing command which return wrong password. The vault configuration
     * should fail to create, because of wrong password.
     */
    @Test
    public void testWrongPassword() throws Exception {
        try {
            createVault((((("{CLASS@" + (ExternalPasswordModuleSetupTask.getModuleName())) + "}") + (ExternalPassword.class.getName())) + ":Wrong,Password"));
            Assert.fail();
        } catch (Exception ex) {
            // OK
        }
    }

    static class ExternalVaultPasswordSetup implements ServerSetupTask {
        private ModelNode originalVault;

        private VaultSession nonInteractiveSession;

        private VaultHandler vaultHandler;

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            ExternalPasswordCommandsTestCase.passwordProvider.resetFileCounter();
            ModelNode op = new ModelNode();
            // save original vault setting
            ExternalPasswordCommandsTestCase.LOGGER.trace("Saving original vault setting");
            op = Util.getReadAttributeOperation(ExternalPasswordCommandsTestCase.VAULT_PATH, VAULT_OPTIONS);
            originalVault = managementClient.getControllerClient().execute(build()).get(RESULT);
            // remove original vault
            if (((originalVault.get("KEYSTORE_URL")) != null) && (originalVault.hasDefined("KEYSTORE_URL"))) {
                op = Util.createRemoveOperation(ExternalPasswordCommandsTestCase.VAULT_PATH);
                Utils.applyUpdate(op, managementClient.getControllerClient());
            }
            // create new vault
            ExternalPasswordCommandsTestCase.LOGGER.trace("Creating new vault");
            clean();
            vaultHandler = new VaultHandler(ExternalPasswordCommandsTestCase.KEYSTORE_URL, ExternalPasswordCommandsTestCase.VAULT_PASSWORD, null, ExternalPasswordCommandsTestCase.RESOURCE_LOCATION, 128, ExternalPasswordCommandsTestCase.VAULT_ALIAS, ExternalPasswordCommandsTestCase.SALT, ExternalPasswordCommandsTestCase.ITER_COUNT);
            ExternalPasswordCommandsTestCase.KEYSTORE_URL = vaultHandler.getKeyStore();
            ExternalPasswordCommandsTestCase.RESOURCE_LOCATION = vaultHandler.getEncodedVaultFileDirectory();
            nonInteractiveSession = new VaultSession(ExternalPasswordCommandsTestCase.KEYSTORE_URL, ExternalPasswordCommandsTestCase.VAULT_PASSWORD, ExternalPasswordCommandsTestCase.RESOURCE_LOCATION, ExternalPasswordCommandsTestCase.SALT, ExternalPasswordCommandsTestCase.ITER_COUNT);
            nonInteractiveSession.startVaultSession(ExternalPasswordCommandsTestCase.VAULT_ALIAS);
            // create security attributes
            ExternalPasswordCommandsTestCase.LOGGER.trace((("Inserting attribute " + (ExternalPasswordCommandsTestCase.VAULT_ATTRIBUTE)) + " to vault"));
            nonInteractiveSession.addSecuredAttribute(ExternalPasswordCommandsTestCase.VAULT_BLOCK, ExternalPasswordCommandsTestCase.ATTRIBUTE_NAME, ExternalPasswordCommandsTestCase.VAULT_ATTRIBUTE.toCharArray());
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            ModelNode op;
            // set original vault
            if (((originalVault.get("KEYSTORE_URL")) != null) && (originalVault.hasDefined("KEYSTORE_URL"))) {
                Set<String> originalVaultParam = originalVault.keys();
                Iterator<String> it = originalVaultParam.iterator();
                op = Util.createAddOperation(ExternalPasswordCommandsTestCase.VAULT_PATH);
                ModelNode vaultOption = op.get(VAULT_OPTIONS);
                while (it.hasNext()) {
                    String param = it.next();
                    vaultOption.get(param).set(originalVault.get(param));
                } 
                Utils.applyUpdate(op, managementClient.getControllerClient());
            }
            // remove vault files
            vaultHandler.cleanUp();
            // remove password helper file
            ExternalPasswordCommandsTestCase.passwordProvider.cleanup();
        }

        private void clean() throws IOException {
            File datFile = new File(ExternalPasswordCommandsTestCase.VAULT_DAT_FILE);
            if (datFile.exists()) {
                Files.delete(datFile.toPath());
            }
        }
    }
}

