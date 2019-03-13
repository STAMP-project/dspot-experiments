/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.password;


import URLMasterPasswordProviderConfig.MASTER_PASSWD_FILENAME;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.geoserver.security.GeoServerSecurityTestSupport;
import org.geoserver.security.auth.GeoServerRootAuthenticationProvider;
import org.geoserver.security.validation.MasterPasswordChangeException;
import org.geoserver.test.SystemTest;
import org.geotools.util.URLs;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


// @TestSetup(run=TestSetupFrequency.REPEAT)
@Category(SystemTest.class)
public class MasterPasswordChangeTest extends GeoServerSecurityTestSupport {
    @Test
    public void testMasterPasswordChange() throws Exception {
        // keytool -storepasswd -new geoserver1 -storepass geoserver -storetype jceks -keystore
        // geoserver.jks
        // We need to enable Master Root login first
        MasterPasswordProviderConfig masterPasswordConfig = getSecurityManager().loadMasterPassswordProviderConfig(getSecurityManager().getMasterPasswordConfig().getProviderName());
        masterPasswordConfig.setLoginEnabled(true);
        getSecurityManager().saveMasterPasswordProviderConfig(masterPasswordConfig);
        String masterPWAsString = getMasterPassword();
        MasterPasswordConfig config = getSecurityManager().getMasterPasswordConfig();
        URLMasterPasswordProviderConfig mpConfig = ((URLMasterPasswordProviderConfig) (getSecurityManager().loadMasterPassswordProviderConfig(config.getProviderName())));
        Assert.assertTrue(mpConfig.getURL().toString().endsWith(MASTER_PASSWD_FILENAME));
        getSecurityManager().getKeyStoreProvider().reloadKeyStore();
        try {
            getSecurityManager().saveMasterPasswordConfig(config, null, null, null);
            Assert.fail();
        } catch (MasterPasswordChangeException ex) {
        }
        // /// First change using rw_url
        mpConfig = new URLMasterPasswordProviderConfig();
        mpConfig.setName("rw");
        mpConfig.setClassName(URLMasterPasswordProvider.class.getCanonicalName());
        mpConfig.setReadOnly(false);
        File tmp = new File(getSecurityManager().get("security").dir(), "mpw1.properties");
        mpConfig.setURL(URLs.fileToUrl(tmp));
        getSecurityManager().saveMasterPasswordProviderConfig(mpConfig);
        config = getSecurityManager().getMasterPasswordConfig();
        config.setProviderName(mpConfig.getName());
        getSecurityManager().saveMasterPasswordConfig(config, masterPWAsString.toCharArray(), "geoserver1".toCharArray(), "geoserver1".toCharArray());
        Assert.assertEquals("geoserver1", getMasterPassword());
        getSecurityManager().getKeyStoreProvider().getConfigPasswordKey();
        // ///////////// change with ro url
        mpConfig = new URLMasterPasswordProviderConfig();
        mpConfig.setName("ro");
        mpConfig.setClassName(URLMasterPasswordProvider.class.getCanonicalName());
        mpConfig.setReadOnly(true);
        tmp = new File(getSecurityManager().get("security").dir(), "mpw2.properties");
        mpConfig.setURL(URLs.fileToUrl(tmp));
        FileUtils.writeStringToFile(tmp, "geoserver2");
        getSecurityManager().saveMasterPasswordProviderConfig(mpConfig);
        config = getSecurityManager().getMasterPasswordConfig();
        config.setProviderName("ro");
        getSecurityManager().saveMasterPasswordConfig(config, "geoserver1".toCharArray(), null, "geoserver2".toCharArray());
        Assert.assertEquals("geoserver2", getMasterPassword());
        getSecurityManager().getKeyStoreProvider().getConfigPasswordKey();
        // ///////////////////// change simulating spring injection
        MasterPasswordProviderConfig mpConfig2 = new MasterPasswordProviderConfig();
        mpConfig2.setLoginEnabled(true);
        mpConfig2.setName("test");
        mpConfig2.setClassName(TestMasterPasswordProvider.class.getCanonicalName());
        getSecurityManager().saveMasterPasswordProviderConfig(mpConfig2);
        config = getSecurityManager().getMasterPasswordConfig();
        config.setProviderName("test");
        getSecurityManager().saveMasterPasswordConfig(config, "geoserver2".toCharArray(), "geoserver3".toCharArray(), "geoserver3".toCharArray());
        // now, a geoserver restart should appear, simulate with
        getSecurityManager().getKeyStoreProvider().commitMasterPasswordChange();
        // ////////
        Assert.assertEquals("geoserver3", getMasterPassword());
        getSecurityManager().getKeyStoreProvider().getConfigPasswordKey();
        // / Test root login after master password change
        Authentication auth = new UsernamePasswordAuthenticationToken("root", "geoserver3");
        GeoServerRootAuthenticationProvider authProvider = new GeoServerRootAuthenticationProvider();
        authProvider.setSecurityManager(getSecurityManager());
        auth = authProvider.authenticate(auth);
        Assert.assertTrue(auth.isAuthenticated());
        auth = new UsernamePasswordAuthenticationToken("root", "abcdefghijk");
        Assert.assertNull(authProvider.authenticate(auth));
        Assert.assertFalse(auth.isAuthenticated());
    }
}

