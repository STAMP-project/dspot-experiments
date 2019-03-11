package org.apache.ambari.server.upgrade;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for {@link SchemaUpgradeHelper}
 */
public class SchemaUpgradeHelperTest {
    private SchemaUpgradeHelper schemaUpgradeHelper;

    @Test
    public void testGetMinimalUpgradeCatalogVersion() throws Exception {
        Method getMinimalUpgradeCatalogVersion = schemaUpgradeHelper.getClass().getDeclaredMethod("getMinimalUpgradeCatalogVersion");
        getMinimalUpgradeCatalogVersion.setAccessible(true);
        String s = ((String) (getMinimalUpgradeCatalogVersion.invoke(schemaUpgradeHelper)));
        Assert.assertEquals("0.1.0", s);
    }

    @Test
    public void testVerifyUpgradePath() throws Exception {
        Method verifyUpgradePath = schemaUpgradeHelper.getClass().getDeclaredMethod("verifyUpgradePath", String.class, String.class);
        verifyUpgradePath.setAccessible(true);
        boolean failToVerify = ((boolean) (verifyUpgradePath.invoke(schemaUpgradeHelper, "0.3.0", "0.2.0")));
        boolean verifyPassed = ((boolean) (verifyUpgradePath.invoke(schemaUpgradeHelper, "0.1.0", "0.2.0")));
        Assert.assertTrue(verifyPassed);
        Assert.assertFalse(failToVerify);
    }
}

