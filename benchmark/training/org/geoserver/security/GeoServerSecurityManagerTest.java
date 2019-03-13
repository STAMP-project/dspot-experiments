/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import GeoServerRole.ADMIN_ROLE;
import GeoServerSecurityFilterChain.WEB_LOGIN_CHAIN_NAME;
import GeoServerUser.ADMIN_USERNAME;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.geoserver.platform.GeoServerEnvironment;
import org.geoserver.platform.resource.Files;
import org.geoserver.security.config.SecurityManagerConfig;
import org.geoserver.security.impl.GeoServerRole;
import org.geoserver.test.SystemTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static GeoServerSecurityManager.MASTER_PASSWD_DEFAULT;


@Category(SystemTest.class)
public class GeoServerSecurityManagerTest extends GeoServerSecurityTestSupport {
    @Test
    public void testAdminRole() throws Exception {
        GeoServerSecurityManager secMgr = getSecurityManager();
        TestingAuthenticationToken auth = new TestingAuthenticationToken("admin", "geoserver", ((List) (Arrays.asList(ADMIN_ROLE))));
        auth.setAuthenticated(true);
        Assert.assertTrue(secMgr.checkAuthenticationForAdminRole(auth));
    }

    @Test
    public void testMasterPasswordForMigration() throws Exception {
        // simulate no user.properties file
        GeoServerSecurityManager secMgr = getSecurityManager();
        char[] generatedPW = secMgr.extractMasterPasswordForMigration(null);
        Assert.assertTrue(((generatedPW.length) == 8));
        Assert.assertTrue(masterPWInfoFileContains(new String(generatedPW)));
        // dumpPWInfoFile();
        Properties props = new Properties();
        String adminUser = "user1";
        String noAdminUser = "user2";
        // check all users with default password
        String defaultMasterePassword = new String(MASTER_PASSWD_DEFAULT);
        props.put(ADMIN_USERNAME, ((defaultMasterePassword + ",") + (GeoServerRole.ADMIN_ROLE)));
        props.put(adminUser, ((defaultMasterePassword + ",") + (GeoServerRole.ADMIN_ROLE)));
        props.put(noAdminUser, (defaultMasterePassword + ",ROLE_WFS"));
        generatedPW = secMgr.extractMasterPasswordForMigration(props);
        Assert.assertTrue(((generatedPW.length) == 8));
        Assert.assertTrue(masterPWInfoFileContains(new String(generatedPW)));
        Assert.assertFalse(masterPWInfoFileContains(ADMIN_USERNAME));
        Assert.assertFalse(masterPWInfoFileContains(adminUser));
        Assert.assertFalse(masterPWInfoFileContains(noAdminUser));
        // dumpPWInfoFile();
        // valid master password for noadminuser
        props.put(noAdminUser, ("validPassword" + ",ROLE_WFS"));
        generatedPW = secMgr.extractMasterPasswordForMigration(props);
        Assert.assertTrue(((generatedPW.length) == 8));
        Assert.assertTrue(masterPWInfoFileContains(new String(generatedPW)));
        // password to short  for adminuser
        props.put(adminUser, (("abc" + ",") + (GeoServerRole.ADMIN_ROLE)));
        generatedPW = secMgr.extractMasterPasswordForMigration(props);
        Assert.assertTrue(((generatedPW.length) == 8));
        Assert.assertTrue(masterPWInfoFileContains(new String(generatedPW)));
        // valid password for user having admin role
        String validPassword = "validPassword";
        props.put(adminUser, ((validPassword + ",") + (GeoServerRole.ADMIN_ROLE)));
        generatedPW = secMgr.extractMasterPasswordForMigration(props);
        Assert.assertEquals(validPassword, new String(generatedPW));
        Assert.assertFalse(masterPWInfoFileContains(validPassword));
        Assert.assertTrue(masterPWInfoFileContains(adminUser));
        // dumpPWInfoFile();
        // valid password for "admin" user
        props.put(ADMIN_USERNAME, ((validPassword + ",") + (GeoServerRole.ADMIN_ROLE)));
        generatedPW = secMgr.extractMasterPasswordForMigration(props);
        Assert.assertEquals(validPassword, new String(generatedPW));
        Assert.assertFalse(masterPWInfoFileContains(validPassword));
        Assert.assertTrue(masterPWInfoFileContains(ADMIN_USERNAME));
        // dumpPWInfoFile();
        // assert configuration reload works properly
        secMgr.reload();
    }

    @Test
    public void testMasterPasswordDump() throws Exception {
        GeoServerSecurityManager secMgr = getSecurityManager();
        File f = File.createTempFile("masterpw", "info");
        f.delete();
        try {
            Assert.assertFalse(secMgr.dumpMasterPassword(Files.asResource(f)));
            TestingAuthenticationToken auth = new TestingAuthenticationToken("admin", "geoserver", ((List) (Arrays.asList(ADMIN_ROLE))));
            auth.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(auth);
            Assert.assertTrue(secMgr.dumpMasterPassword(Files.asResource(f)));
            dumpPWInfoFile(f);
            Assert.assertTrue(masterPWInfoFileContains(f, new String(secMgr.getMasterPassword())));
        } finally {
            f.delete();
        }
    }

    @Test
    public void testMasterPasswordDumpNotAuthorized() throws Exception {
        GeoServerSecurityManager secMgr = getSecurityManager();
        File f = File.createTempFile("masterpw", "info");
        try {
            Assert.assertFalse(secMgr.dumpMasterPassword(Files.asResource(f)));
            TestingAuthenticationToken auth = new TestingAuthenticationToken("admin", "geoserver", ((List) (Arrays.asList(ADMIN_ROLE))));
            auth.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(auth);
            Assert.assertFalse(secMgr.dumpMasterPassword(Files.asResource(f)));
        } finally {
            f.delete();
        }
    }

    @Test
    public void testMasterPasswordDumpNotOverwrite() throws Exception {
        GeoServerSecurityManager secMgr = getSecurityManager();
        File f = File.createTempFile("masterpw", "info");
        try (FileOutputStream os = new FileOutputStream(f)) {
            os.write("This should not be overwritten!".getBytes(StandardCharsets.UTF_8));
        }
        try {
            Assert.assertFalse(secMgr.dumpMasterPassword(Files.asResource(f)));
            TestingAuthenticationToken auth = new TestingAuthenticationToken("admin", "geoserver", ((List) (Arrays.asList(ADMIN_ROLE))));
            auth.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(auth);
            Assert.assertFalse(secMgr.dumpMasterPassword(Files.asResource(f)));
            dumpPWInfoFile(f);
            Assert.assertTrue(masterPWInfoFileContains(f, "This should not be overwritten!"));
            Assert.assertFalse(masterPWInfoFileContains(f, new String(secMgr.getMasterPassword())));
        } finally {
            f.delete();
        }
    }

    @Test
    public void testWebLoginChainSessionCreation() throws Exception {
        // GEOS-6077
        GeoServerSecurityManager secMgr = getSecurityManager();
        SecurityManagerConfig config = secMgr.loadSecurityConfig();
        RequestFilterChain chain = config.getFilterChain().getRequestChainByName(WEB_LOGIN_CHAIN_NAME);
        Assert.assertTrue(chain.isAllowSessionCreation());
    }

    @Test
    public void testGeoServerEnvParametrization() throws Exception {
        GeoServerSecurityManager secMgr = getSecurityManager();
        SecurityManagerConfig config = secMgr.loadSecurityConfig();
        String oldRoleServiceName = config.getRoleServiceName();
        try {
            if (GeoServerEnvironment.ALLOW_ENV_PARAMETRIZATION) {
                System.setProperty("TEST_SYS_PROPERTY", oldRoleServiceName);
                config.setRoleServiceName("${TEST_SYS_PROPERTY}");
                secMgr.saveSecurityConfig(config);
                SecurityManagerConfig config1 = secMgr.loadSecurityConfig();
                Assert.assertEquals(config1.getRoleServiceName(), oldRoleServiceName);
            }
        } finally {
            config.setRoleServiceName(oldRoleServiceName);
            secMgr.saveSecurityConfig(config);
            System.clearProperty("TEST_SYS_PROPERTY");
        }
    }
}

