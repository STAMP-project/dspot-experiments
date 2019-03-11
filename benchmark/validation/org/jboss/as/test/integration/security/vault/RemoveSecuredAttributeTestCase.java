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


import org.jboss.as.security.vault.VaultSession;
import org.jboss.as.test.integration.security.common.VaultHandler;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of removing vault entries
 *
 * @author Jan Tymel
 */
public class RemoveSecuredAttributeTestCase {
    private static final Logger LOGGER = Logger.getLogger(RemoveSecuredAttributeTestCase.class);

    private static final String KEY_STORE = "myVault.keystore";

    private static final String VAULT_PASSWORD = "VaultPassword";

    private static final String RESOURCE_LOCATION = "";

    private static final int KEY_SIZE = 128;

    private static final String VAULT_ALIAS = "VaultAlias";

    private static final String SALT = "87654321";

    private static final int ITERATION_COUNT = 20;

    private static final String VAULT_BLOCK = "VaultBlock";

    private static final String WRONG_VAULT_BLOCK = "WrongVaultBlock";

    private static final String ATTRIBUTE_NAME = "AttributeName";

    private static final String WRONG_ATTRIBUTE_NAME = "WrongAttributeName";

    private static final char[] ATTRIBUTE_VALUE = "SecretPassword".toCharArray();

    private static final char[] ANOTHER_ATTRIBUTE_VALUE = "AnotherSecretPassword".toCharArray();

    private VaultHandler vaultHandler;

    /**
     * Test of remove secured attribute
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRemoveSecuredAttributeBySameVaultSession() throws Exception {
        VaultSession vaultSession = vaultHandler.getVaultSession();
        String securedAttribute = addSecuredAttributeToDefaultVault(vaultSession, RemoveSecuredAttributeTestCase.ATTRIBUTE_VALUE);
        Assert.assertArrayEquals("Retrieved secured attribute differs from the saved", vaultSession.retrieveSecuredAttribute(RemoveSecuredAttributeTestCase.VAULT_BLOCK, RemoveSecuredAttributeTestCase.ATTRIBUTE_NAME), RemoveSecuredAttributeTestCase.ATTRIBUTE_VALUE);
        assertCorrectAttributeRemoval(vaultSession, securedAttribute);
    }

    /**
     * Test of remove secured attribute with another vault session because of possible caching
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRemoveSecuredAttributeByDifferentVaultSession() throws Exception {
        VaultSession vaultSession = vaultHandler.getVaultSession();
        String securedAttribute = addSecuredAttributeToDefaultVault(vaultSession, RemoveSecuredAttributeTestCase.ATTRIBUTE_VALUE);
        // Starting new vault session
        vaultSession.startVaultSession(RemoveSecuredAttributeTestCase.VAULT_ALIAS);
        Assert.assertArrayEquals("Retrieved secured attribute differs from the saved", vaultSession.retrieveSecuredAttribute(RemoveSecuredAttributeTestCase.VAULT_BLOCK, RemoveSecuredAttributeTestCase.ATTRIBUTE_NAME), RemoveSecuredAttributeTestCase.ATTRIBUTE_VALUE);
        assertCorrectAttributeRemoval(vaultSession, securedAttribute);
    }

    /**
     * Test of remove secured attribute with another vault instance
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRemoveSecuredAttributeByDifferentVaultHandler() throws Exception {
        VaultSession vaultSession = vaultHandler.getVaultSession();
        String securedAttribute = addSecuredAttributeToDefaultVault(vaultSession, RemoveSecuredAttributeTestCase.ATTRIBUTE_VALUE);
        VaultHandler vaultHandler2 = RemoveSecuredAttributeTestCase.createDefaultVaultHandler();
        VaultSession vaultSession2 = vaultHandler2.getVaultSession();
        Assert.assertArrayEquals("Retrieved secured attribute differs from the saved", vaultSession2.retrieveSecuredAttribute(RemoveSecuredAttributeTestCase.VAULT_BLOCK, RemoveSecuredAttributeTestCase.ATTRIBUTE_NAME), RemoveSecuredAttributeTestCase.ATTRIBUTE_VALUE);
        assertCorrectAttributeRemoval(vaultSession2, securedAttribute);
    }

    /**
     * Test of remove non existent secured attribute
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRemoveNonExistentSecuredAttribute() throws Exception {
        VaultSession vaultSession = vaultHandler.getVaultSession();
        String securedAttribute = addSecuredAttributeToDefaultVault(vaultSession, RemoveSecuredAttributeTestCase.ATTRIBUTE_VALUE);
        Assert.assertArrayEquals("Retrieved secured attribute differs from the saved", vaultSession.retrieveSecuredAttribute(RemoveSecuredAttributeTestCase.VAULT_BLOCK, RemoveSecuredAttributeTestCase.ATTRIBUTE_NAME), RemoveSecuredAttributeTestCase.ATTRIBUTE_VALUE);
        Assert.assertFalse("Method returned true and removed secured attribute with wrong vault block identifier. It should return false and shouldn't remove this secured attribute", vaultSession.removeSecuredAttribute(RemoveSecuredAttributeTestCase.WRONG_VAULT_BLOCK, RemoveSecuredAttributeTestCase.ATTRIBUTE_NAME));
        Assert.assertFalse("Method returned true and removed secured attribute with wrong attribute name. It should return false and shouldn't remove this secured attribute", vaultSession.removeSecuredAttribute(RemoveSecuredAttributeTestCase.VAULT_BLOCK, RemoveSecuredAttributeTestCase.WRONG_ATTRIBUTE_NAME));
        assertCorrectAttributeRemoval(vaultSession, securedAttribute);
    }

    /**
     * Test of remove secured attribute and then add different secured attribute with the same name to the same vault block
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRemoveSecuredAttributeAndAddDifferent() throws Exception {
        VaultSession vaultSession = vaultHandler.getVaultSession();
        String securedAttribute = addSecuredAttributeToDefaultVault(vaultSession, RemoveSecuredAttributeTestCase.ATTRIBUTE_VALUE);
        Assert.assertArrayEquals("Retrieved secured attribute differs from the saved", vaultSession.retrieveSecuredAttribute(RemoveSecuredAttributeTestCase.VAULT_BLOCK, RemoveSecuredAttributeTestCase.ATTRIBUTE_NAME), RemoveSecuredAttributeTestCase.ATTRIBUTE_VALUE);
        assertCorrectAttributeRemoval(vaultSession, securedAttribute);
        VaultHandler vaultHandler2 = RemoveSecuredAttributeTestCase.createDefaultVaultHandler();
        VaultSession vaultSession2 = vaultHandler2.getVaultSession();
        securedAttribute = addSecuredAttributeToDefaultVault(vaultSession2, RemoveSecuredAttributeTestCase.ANOTHER_ATTRIBUTE_VALUE);
        Assert.assertArrayEquals("Retrieved secured attribute differs from the saved", vaultSession2.retrieveSecuredAttribute(RemoveSecuredAttributeTestCase.VAULT_BLOCK, RemoveSecuredAttributeTestCase.ATTRIBUTE_NAME), RemoveSecuredAttributeTestCase.ANOTHER_ATTRIBUTE_VALUE);
        assertCorrectAttributeRemoval(vaultSession2, securedAttribute);
    }
}

