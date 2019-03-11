/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import KeyStoreProviderImpl.CONFIGPASSWORDKEY;
import org.geoserver.security.password.RandomPasswordProvider;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static KeyStoreProviderImpl.USERGROUP_POSTFIX;
import static KeyStoreProviderImpl.USERGROUP_PREFIX;


public class KeyStoreProviderTest extends GeoServerSystemTestSupport {
    @Test
    public void testKeyStoreProvider() throws Exception {
        // System.setProperty(MasterPasswordProvider.DEFAULT_PROPERTY_NAME, "mymasterpw");
        KeyStoreProvider ksp = getSecurityManager().getKeyStoreProvider();
        ksp.removeKey(CONFIGPASSWORDKEY);
        ksp.removeKey(ksp.aliasForGroupService("default"));
        ksp.storeKeyStore();
        ksp.reloadKeyStore();
        Assert.assertFalse(ksp.hasConfigPasswordKey());
        Assert.assertFalse(ksp.hasUserGroupKey("default"));
        ksp.setSecretKey(CONFIGPASSWORDKEY, "configKey".toCharArray());
        ksp.storeKeyStore();
        Assert.assertTrue(ksp.hasConfigPasswordKey());
        Assert.assertEquals("configKey", new String(ksp.getConfigPasswordKey()));
        Assert.assertFalse(ksp.hasUserGroupKey("default"));
        RandomPasswordProvider rpp = getSecurityManager().getRandomPassworddProvider();
        char[] urlKey = rpp.getRandomPasswordWithDefaultLength();
        // System.out.printf("Random password with length %d : %s\n",urlKey.length,new
        // String(urlKey));
        char[] urlKey2 = rpp.getRandomPasswordWithDefaultLength();
        // System.out.printf("Random password with length %d : %s\n",urlKey2.length,new
        // String(urlKey2));
        Assert.assertThat(urlKey, CoreMatchers.not(CoreMatchers.equalTo(urlKey2)));
        ksp.setSecretKey((((USERGROUP_PREFIX) + "default") + (USERGROUP_POSTFIX)), "defaultKey".toCharArray());
        ksp.storeKeyStore();
        Assert.assertTrue(ksp.hasConfigPasswordKey());
        Assert.assertEquals("configKey", new String(ksp.getConfigPasswordKey()));
        Assert.assertTrue(ksp.hasUserGroupKey("default"));
        Assert.assertEquals("defaultKey", new String(ksp.getUserGroupKey("default")));
        Assert.assertTrue(ksp.isKeyStorePassword(getSecurityManager().getMasterPassword()));
        Assert.assertFalse(ksp.isKeyStorePassword("blabla".toCharArray()));
    }
}

