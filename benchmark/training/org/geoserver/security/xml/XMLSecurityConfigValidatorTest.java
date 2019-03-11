/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.xml;


import XMLConstants.FILE_RR;
import XMLConstants.FILE_UR;
import XMLRoleService.DEFAULT_LOCAL_ADMIN_ROLE;
import XMLRoleService.DEFAULT_NAME;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.geoserver.platform.resource.Files;
import org.geoserver.security.GeoServerRoleService;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.GeoServerUserGroupService;
import org.geoserver.security.auth.UsernamePasswordAuthenticationProvider;
import org.geoserver.security.config.SecurityAuthProviderConfig;
import org.geoserver.security.impl.GeoServerUserGroup;
import org.geoserver.security.validation.SecurityConfigException;
import org.geoserver.security.validation.SecurityConfigValidatorTest;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class XMLSecurityConfigValidatorTest extends SecurityConfigValidatorTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    protected static Logger LOGGER = Logging.getLogger("org.geoserver.security");

    @Test
    public void testRoleConfig() throws IOException {
        super.testRoleConfig();
        XMLRoleServiceConfig config = ((XMLRoleServiceConfig) (createRoleConfig(DEFAULT_NAME, XMLRoleService.class, DEFAULT_LOCAL_ADMIN_ROLE, FILE_RR)));
        XMLSecurityConfigValidator validator = new XMLSecurityConfigValidator(getSecurityManager());
        try {
            config.setName("default2");
            config.setCheckInterval((-1L));
            validator.validateAddRoleService(config);
            Assert.fail("invalid interval should fail");
            // getSecurityManager().saveRoleService(config);
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(CHECK_INTERVAL_INVALID, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        try {
            config.setCheckInterval(999L);
            validator.validateAddRoleService(config);
            Assert.fail("invalid interval should fail");
            // getSecurityManager().saveRoleService(config);
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(CHECK_INTERVAL_INVALID, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setCheckInterval(0);
        XMLRoleServiceConfig xmlConfig = ((XMLRoleServiceConfig) (createRoleConfig("test1", XMLRoleService.class, DEFAULT_LOCAL_ADMIN_ROLE, "test1.xml")));
        try {
            validator.validateAddRoleService(xmlConfig);
            // getSecurityManager().saveRoleService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.fail(("Should work but got: " + (ex.getMessage())));
        }
        // run only if a temp dir is availbale
        if ((getTempDir()) != null) {
            String invalidPath = ("abc" + (File.separator)) + "def.xml";
            XMLRoleServiceConfig xmlConfig4 = ((XMLRoleServiceConfig) (createRoleConfig("test4", XMLRoleService.class, DEFAULT_LOCAL_ADMIN_ROLE, invalidPath)));
            try {
                validator.validateAddRoleService(xmlConfig4);
                Assert.fail("file creation failure should occur");
                // getSecurityManager().saveRoleService(xmlConfig);
            } catch (SecurityConfigException ex) {
                Assert.assertEquals(FILE_CREATE_FAILED_$1, ex.getId());
                Assert.assertEquals(invalidPath, ex.getArgs()[0]);
            }
        }
        GeoServerSecurityManager secMgr = createNiceMock(GeoServerSecurityManager.class);
        GeoServerRoleService roleService1 = createNiceMock(GeoServerRoleService.class);
        expect(roleService1.getRoleCount()).andReturn(0).anyTimes();
        expect(secMgr.loadRoleService("test1")).andReturn(roleService1).anyTimes();
        GeoServerRoleService roleService2 = createNiceMock(GeoServerRoleService.class);
        expect(roleService2.getRoleCount()).andReturn(1).anyTimes();
        expect(secMgr.loadRoleService("test2")).andReturn(roleService2).anyTimes();
        GeoServerRoleService roleService3 = createNiceMock(GeoServerRoleService.class);
        expect(roleService3.getRoleCount()).andReturn(1).anyTimes();
        expect(secMgr.loadRoleService("test3")).andReturn(roleService3).anyTimes();
        GeoServerRoleService roleService4 = createNiceMock(GeoServerRoleService.class);
        expect(roleService4.getRoleCount()).andReturn(1).anyTimes();
        expect(secMgr.loadRoleService("test4")).andReturn(roleService4).anyTimes();
        GeoServerRoleService activeRoleService = createNiceMock(GeoServerRoleService.class);
        expect(activeRoleService.getName()).andReturn("foo").anyTimes();
        expect(secMgr.getActiveRoleService()).andReturn(activeRoleService).anyTimes();
        expect(secMgr.role()).andReturn(Files.asResource(tempFolder.getRoot())).anyTimes();
        expect(secMgr.listRoleServices()).andReturn(new TreeSet<String>(Arrays.asList("test1", "test2", "test3", "test4"))).anyTimes();
        replay(roleService1, roleService2, roleService3, roleService4, activeRoleService, secMgr);
        validator = new XMLSecurityConfigValidator(secMgr);
        try {
            validator.validateRemoveRoleService(xmlConfig);
            // getSecurityManager().removeRoleService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.fail(("Should work but got: " + (ex.getMessage())));
        }
        xmlConfig = ((XMLRoleServiceConfig) (createRoleConfig("test2", XMLRoleService.class, DEFAULT_LOCAL_ADMIN_ROLE, "test2.xml")));
        try {
            validator.validateRemoveRoleService(xmlConfig);
            Assert.fail("non empty role service should fail");
            // getSecurityManager().saveRoleService(xmlConfig);
            // GeoServerRoleStore store =
            // getSecurityManager().loadRoleService("test2").createStore();
            // store.addRole(GeoServerRole.ADMIN_ROLE);
            // store.store();
            // getSecurityManager().removeRoleService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(ROLE_SERVICE_NOT_EMPTY_$1, ex.getId());
            Assert.assertEquals("test2", ex.getArgs()[0]);
        }
        xmlConfig = ((XMLRoleServiceConfig) (createRoleConfig("test3", XMLRoleService.class, DEFAULT_LOCAL_ADMIN_ROLE, new File(getSecurityManager().role().dir(), "test3.xml").getAbsolutePath())));
        try {
            validator.validateRemoveRoleService(xmlConfig);
            // getSecurityManager().saveRoleService(xmlConfig);
            // GeoServerRoleStore store =
            // getSecurityManager().loadRoleService("test3").createStore();
            // store.addRole(GeoServerRole.ADMIN_ROLE);
            // store.store();
            // getSecurityManager().removeRoleService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.fail("Should work");
        }
        // ///////////// test modify
        xmlConfig = ((XMLRoleServiceConfig) (createRoleConfig("test4", XMLRoleService.class, DEFAULT_LOCAL_ADMIN_ROLE, "testModify.xml")));
        XMLRoleServiceConfig oldXmlConfig = new XMLRoleServiceConfig(xmlConfig);
        try {
            xmlConfig.setValidating(true);
            validator.validateModifiedRoleService(xmlConfig, xmlConfig);
            // getSecurityManager().saveRoleService(xmlConfig);
            // xmlConfig.setValidating(true);
            // getSecurityManager().saveRoleService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.fail("Should work");
        }
        try {
            xmlConfig.setFileName("xyz.xml");
            validator.validateModifiedRoleService(xmlConfig, oldXmlConfig);
            Assert.fail("invalid filename change should fail");
            // getSecurityManager().saveRoleService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(FILENAME_CHANGE_INVALID_$2, ex.getId());
            Assert.assertEquals("testModify.xml", ex.getArgs()[0]);
            Assert.assertEquals("xyz.xml", ex.getArgs()[1]);
        }
    }

    @Test
    public void testUserGroupConfig() throws IOException {
        super.testUserGroupConfig();
        XMLUserGroupServiceConfig config = ((XMLUserGroupServiceConfig) (createUGConfig(XMLUserGroupService.DEFAULT_NAME, XMLUserGroupService.class, getPlainTextPasswordEncoder().getName(), PasswordValidator.DEFAULT_NAME, FILE_UR)));
        XMLSecurityConfigValidator validator = new XMLSecurityConfigValidator(getSecurityManager());
        try {
            config.setName("default2");
            config.setCheckInterval((-1L));
            validator.validateAddUserGroupService(config);
            Assert.fail("invalid check interval should fail");
            // getSecurityManager().saveUserGroupService(config);
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(CHECK_INTERVAL_INVALID, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        try {
            config.setCheckInterval(999L);
            validator.validateAddUserGroupService(config);
            Assert.fail("invalid check interval should fail");
            // getSecurityManager().saveUserGroupService(config);
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(CHECK_INTERVAL_INVALID, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setCheckInterval(0);
        XMLUserGroupServiceConfig xmlConfig = ((XMLUserGroupServiceConfig) (createUGConfig("test1", XMLUserGroupService.class, getPlainTextPasswordEncoder().getName(), PasswordValidator.DEFAULT_NAME, "test1.xml")));
        GeoServerUserGroup group = new GeoServerUserGroup("testgroup");
        try {
            validator.validateAddUserGroupService(xmlConfig);
            // getSecurityManager().saveUserGroupService(xmlConfig);
            // getSecurityManager().removeUserGroupService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.fail(("Should work but got: " + (ex.getMessage())));
        }
        XMLUserGroupServiceConfig xmlConfig5 = ((XMLUserGroupServiceConfig) (createUGConfig("test5", XMLUserGroupService.class, getPlainTextPasswordEncoder().getName(), PasswordValidator.DEFAULT_NAME, "abc.xml")));
        try {
            // getSecurityManager().saveUserGroupService(xmlConfig);
            validator.validateAddUserGroupService(xmlConfig5);
        } catch (SecurityConfigException ex) {
            Assert.fail(("Should work but got: " + (ex.getMessage())));
        }
        try {
            xmlConfig5.setFileName("");
            validator.validateAddUserGroupService(xmlConfig5);
            Assert.fail("empty file name should fail");
            // getSecurityManager().saveUserGroupService(xmlConfig5);
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(FILENAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        // run only if a temp dir is availbale
        if ((getTempDir()) != null) {
            String invalidPath = ("abc" + (File.separator)) + "def.xml";
            XMLUserGroupServiceConfig xmlConfig4 = ((XMLUserGroupServiceConfig) (createUGConfig("test4", XMLUserGroupService.class, getPlainTextPasswordEncoder().getName(), PasswordValidator.DEFAULT_NAME, invalidPath)));
            try {
                validator.validateAddUserGroupService(xmlConfig4);
                Assert.fail("file creation should fail");
                // getSecurityManager().saveUserGroupService(xmlConfig);
            } catch (SecurityConfigException ex) {
                Assert.assertEquals(FILE_CREATE_FAILED_$1, ex.getId());
                Assert.assertEquals(invalidPath, ex.getArgs()[0]);
            }
        }
        GeoServerSecurityManager secMgr = createNiceMock(GeoServerSecurityManager.class);
        expect(secMgr.listAuthenticationProviders()).andReturn(new TreeSet<String>()).anyTimes();
        GeoServerUserGroupService ugService1 = createNiceMock(GeoServerUserGroupService.class);
        expect(ugService1.getName()).andReturn("test1").anyTimes();
        expect(secMgr.loadUserGroupService("test1")).andReturn(ugService1).anyTimes();
        GeoServerUserGroupService ugService2 = createNiceMock(GeoServerUserGroupService.class);
        expect(ugService2.getName()).andReturn("test2").anyTimes();
        expect(ugService2.getGroupCount()).andReturn(1).anyTimes();
        expect(secMgr.loadUserGroupService("test2")).andReturn(ugService2).anyTimes();
        GeoServerUserGroupService ugServiceModify = createNiceMock(GeoServerUserGroupService.class);
        expect(ugServiceModify.getName()).andReturn("testModify").anyTimes();
        expect(secMgr.loadUserGroupService("testModify")).andReturn(ugService2).anyTimes();
        expect(secMgr.listUserGroupServices()).andReturn(new TreeSet<String>(Arrays.asList("test1", "test2", "testModify"))).anyTimes();
        expect(secMgr.userGroup()).andReturn(Files.asResource(tempFolder.getRoot())).anyTimes();
        expect(secMgr.loadPasswordEncoder(getPlainTextPasswordEncoder().getName())).andReturn(getPlainTextPasswordEncoder()).anyTimes();
        expect(secMgr.listPasswordValidators()).andReturn(new TreeSet<String>(Arrays.asList(PasswordValidator.DEFAULT_NAME))).anyTimes();
        replay(ugService1, ugService2, ugServiceModify, secMgr);
        // expect(secMgr.listUserGroupServices()).andReturn()
        validator = new XMLSecurityConfigValidator(secMgr);
        try {
            validator.validateRemoveUserGroupService(xmlConfig);
            // getSecurityManager().removeRoleService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.fail(("Should work but got: " + (ex.getMessage())));
        }
        xmlConfig = ((XMLUserGroupServiceConfig) (createUGConfig("test2", XMLUserGroupService.class, getPlainTextPasswordEncoder().getName(), PasswordValidator.DEFAULT_NAME, "test2.xml")));
        try {
            validator.validateRemoveUserGroupService(xmlConfig);
            Assert.fail("non empty ug service should fail");
            // getSecurityManager().saveUserGroupService(xmlConfig);
            // GeoServerUserGroupStore store =
            // getSecurityManager().loadUserGroupService("test2").createStore();
            // store.addGroup(group);
            // store.store();
            // getSecurityManager().removeUserGroupService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(USERGROUP_SERVICE_NOT_EMPTY_$1, ex.getId());
            Assert.assertEquals("test2", ex.getArgs()[0]);
        }
        xmlConfig = ((XMLUserGroupServiceConfig) (createUGConfig("test3", XMLUserGroupService.class, getPlainTextPasswordEncoder().getName(), PasswordValidator.DEFAULT_NAME, new File(getSecurityManager().userGroup().dir(), "test3.xml").getAbsolutePath())));
        try {
            validator.validateRemoveUserGroupService(xmlConfig);
            // getSecurityManager().saveUserGroupService(xmlConfig);
            // GeoServerUserGroupStore store =
            // getSecurityManager().loadUserGroupService("test3").createStore();
            // store.addGroup(group);
            // store.store();
            // getSecurityManager().removeUserGroupService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.fail(("Should work but got: " + (ex.getMessage())));
        }
        // ///////////// test modify
        xmlConfig = ((XMLUserGroupServiceConfig) (createUGConfig("testModify", XMLUserGroupService.class, getPlainTextPasswordEncoder().getName(), PasswordValidator.DEFAULT_NAME, "testModify.xml")));
        XMLUserGroupServiceConfig oldXmlConfig = new XMLUserGroupServiceConfig(xmlConfig);
        try {
            xmlConfig.setValidating(true);
            validator.validateModifiedUserGroupService(xmlConfig, oldXmlConfig);
            // getSecurityManager().saveUserGroupService(xmlConfig);
            // xmlConfig.setValidating(true);
            // getSecurityManager().saveUserGroupService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.fail(("Should work but got: " + (ex.getMessage())));
        }
        try {
            xmlConfig.setFileName("xyz.xml");
            validator.validateModifiedUserGroupService(xmlConfig, oldXmlConfig);
            Assert.fail("invalid file name change should fail");
            // getSecurityManager().saveUserGroupService(xmlConfig);
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(FILENAME_CHANGE_INVALID_$2, ex.getId());
            Assert.assertEquals("testModify.xml", ex.getArgs()[0]);
            Assert.assertEquals("xyz.xml", ex.getArgs()[1]);
        }
    }

    @Override
    @Test
    public void testAuthenticationProvider() throws IOException {
        super.testAuthenticationProvider();
        SecurityAuthProviderConfig config = createAuthConfig("default2", UsernamePasswordAuthenticationProvider.class, null);
        XMLSecurityConfigValidator validator = new XMLSecurityConfigValidator(getSecurityManager());
        try {
            // getSecurityManager().saveAuthenticationProvider(config/*, false*/);
            validator.validateAddAuthProvider(config);
            Assert.fail("no user group service should fail");
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(USERGROUP_SERVICE_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
    }
}

