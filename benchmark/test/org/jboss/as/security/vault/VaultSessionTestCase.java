package org.jboss.as.security.vault;


import org.junit.Assert;
import org.junit.Test;


public class VaultSessionTestCase extends VaultTest {
    @Test
    public void testVaultConfiguration() {
        final String expectedCommand = (((((((((((((("/core-service=vault:add(vault-options=[(\"KEYSTORE_URL\" => \"" + (VaultTest.KEYSTORE_URL_VALUE)) + "\"),") + "(\"KEYSTORE_PASSWORD\" => \"") + (VaultTest.MASKED_MYPASSWORD_VALUE)) + "\"),(\"KEYSTORE_ALIAS\" => \"") + (VaultTest.KEYSTORE_ALIAS_VALUE)) + "\"),") + "(\"SALT\" => \"") + (VaultTest.SALT_VALUE)) + "\"),(\"ITERATION_COUNT\" => \"") + (VaultTest.ITERATION_COUNT_VALUE)) + "\"),") + "(\"ENC_FILE_DIR\" => \"") + (VaultTest.ENC_FILE_DIR_VALUE)) + "/\")])";
        VaultSession vaultSession = null;
        try {
            vaultSession = new VaultSession(VaultTest.KEYSTORE_URL_VALUE, VaultTest.KEYSTORE_PASSWORD, VaultTest.ENC_FILE_DIR_VALUE, VaultTest.SALT_VALUE, Integer.valueOf(VaultTest.ITERATION_COUNT_VALUE), true);
            vaultSession.startVaultSession(VaultTest.KEYSTORE_ALIAS_VALUE);
        } catch (Exception e) {
            Assert.fail(("Failed while initializing vault session with exception " + (e.getMessage())));
        }
        Assert.assertEquals(expectedCommand, vaultSession.vaultConfiguration());
    }
}

