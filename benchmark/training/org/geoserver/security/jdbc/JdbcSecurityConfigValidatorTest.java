/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.jdbc;


import AbstractRoleService.DEFAULT_LOCAL_ADMIN_ROLE;
import JDBCRoleService.DEFAULT_DDL_FILE;
import JDBCRoleService.DEFAULT_DML_FILE;
import PasswordValidator.DEFAULT_NAME;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.jdbc.config.JDBCRoleServiceConfig;
import org.geoserver.security.jdbc.config.JDBCUserGroupServiceConfig;
import org.geoserver.security.password.GeoServerPlainTextPasswordEncoder;
import org.geoserver.security.validation.SecurityConfigException;
import org.geoserver.security.validation.SecurityConfigValidatorTest;
import org.geotools.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;


public class JdbcSecurityConfigValidatorTest extends SecurityConfigValidatorTest {
    protected static Logger LOGGER = Logging.getLogger("org.geoserver.security");

    @Override
    @Test
    public void testRoleConfig() throws IOException {
        super.testRoleConfig();
        JDBCRoleServiceConfig config = ((JDBCRoleServiceConfig) (createRoleConfig("jdbc", JDBCRoleService.class, DEFAULT_LOCAL_ADMIN_ROLE)));
        config.setDriverClassName("a.b.c");
        config.setUserName("user");
        config.setConnectURL("jdbc:connect");
        config.setPropertyFileNameDDL(DEFAULT_DDL_FILE);
        config.setPropertyFileNameDML(DEFAULT_DML_FILE);
        JDBCRoleServiceConfig configJNDI = ((JDBCRoleServiceConfig) (createRoleConfig("jndi", JDBCRoleService.class, DEFAULT_LOCAL_ADMIN_ROLE)));
        configJNDI.setJndi(true);
        configJNDI.setJndiName("jndi:connect");
        configJNDI.setPropertyFileNameDDL(DEFAULT_DDL_FILE);
        configJNDI.setPropertyFileNameDML(DEFAULT_DML_FILE);
        JdbcSecurityConfigValidator validator = new JdbcSecurityConfigValidator(getSecurityManager());
        try {
            configJNDI.setJndiName("");
            validator.validateAddRoleService(configJNDI);
            // getSecurityManager().saveRoleService(configJNDI);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.JNDINAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        try {
            config.setDriverClassName("");
            validator.validateAddRoleService(config);
            // getSecurityManager().saveRoleService(config);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.DRIVER_CLASSNAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setDriverClassName("a.b.c");
        try {
            config.setUserName("");
            validator.validateAddRoleService(config);
            // getSecurityManager().saveRoleService(config);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.USERNAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setUserName("user");
        try {
            config.setConnectURL(null);
            validator.validateAddRoleService(config);
            // getSecurityManager().saveRoleService(config);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.JDBCURL_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setConnectURL("jdbc:connect");
        try {
            validator.validateAddRoleService(config);
            // getSecurityManager().saveRoleService(config);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.DRIVER_CLASS_NOT_FOUND_$1, ex.getId());
            Assert.assertEquals("a.b.c", ex.getArgs()[0]);
        }
        config.setDriverClassName("java.lang.String");
        config.setPropertyFileNameDDL(null);
        try {
            validator.validateAddRoleService(config);
            // getSecurityManager().saveRoleService(config);
        } catch (SecurityConfigException ex) {
            throw new IOException(ex);
        }
        GeoServerSecurityManager secMgr = createNiceMock(GeoServerSecurityManager.class);
        expect(secMgr.listRoleServices()).andReturn(new TreeSet<String>(Arrays.asList("default", "jdbc"))).anyTimes();
        replay(secMgr);
        validator = new JdbcSecurityConfigValidator(secMgr);
        JDBCRoleServiceConfig oldConfig = new JDBCRoleServiceConfig(config);
        config.setPropertyFileNameDML(null);
        try {
            // getSecurityManager().saveRoleService(config);
            validator.validateModifiedRoleService(config, oldConfig);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.DML_FILE_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setPropertyFileNameDDL(DEFAULT_DDL_FILE);
        config.setPropertyFileNameDML(DEFAULT_DML_FILE);
        // run only if a temp dir is availbale
        if ((getTempDir()) != null) {
            oldConfig = new JDBCRoleServiceConfig(config);
            String invalidPath = ("abc" + (File.separator)) + "def.properties";
            config.setPropertyFileNameDDL(invalidPath);
            try {
                // getSecurityManager().saveRoleService(config);
                validator.validateModifiedRoleService(config, oldConfig);
                Assert.fail();
            } catch (SecurityConfigException ex) {
                Assert.assertEquals(JDBCSecurityConfigException.DDL_FILE_INVALID, ex.getId());
                Assert.assertEquals(invalidPath, ex.getArgs()[0]);
            }
        }
        config.setPropertyFileNameDDL(DEFAULT_DDL_FILE);
        config.setPropertyFileNameDML(DEFAULT_DML_FILE);
        // run only if a temp dir is availbale
        if ((getTempDir()) != null) {
            oldConfig = new JDBCRoleServiceConfig(config);
            String invalidPath = ("abc" + (File.separator)) + "def.properties";
            config.setPropertyFileNameDML(invalidPath);
            try {
                // getSecurityManager().saveRoleService(config);
                validator.validateModifiedRoleService(config, oldConfig);
                Assert.fail();
            } catch (SecurityConfigException ex) {
                Assert.assertEquals(JDBCSecurityConfigException.DML_FILE_INVALID, ex.getId());
                Assert.assertEquals(invalidPath, ex.getArgs()[0]);
            }
        }
        oldConfig = new JDBCRoleServiceConfig(config);
        config.setPropertyFileNameDDL(null);
        config.setCreatingTables(true);
        config.setPropertyFileNameDML(DEFAULT_DML_FILE);
        try {
            // getSecurityManager().saveRoleService(config);
            validator.validateModifiedRoleService(config, oldConfig);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.DDL_FILE_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
    }

    @Override
    @Test
    public void testUserGroupConfig() throws IOException {
        super.testUserGroupConfig();
        JDBCUserGroupServiceConfig config = ((JDBCUserGroupServiceConfig) (createUGConfig("jdbc", JDBCUserGroupService.class, getPlainTextPasswordEncoder().getName(), DEFAULT_NAME)));
        config.setDriverClassName("a.b.c");
        config.setUserName("user");
        config.setConnectURL("jdbc:connect");
        config.setPropertyFileNameDDL(JDBCUserGroupService.DEFAULT_DDL_FILE);
        config.setPropertyFileNameDML(JDBCUserGroupService.DEFAULT_DML_FILE);
        JDBCUserGroupServiceConfig configJNDI = ((JDBCUserGroupServiceConfig) (createUGConfig("jdbc", JDBCUserGroupService.class, getPlainTextPasswordEncoder().getName(), DEFAULT_NAME)));
        configJNDI.setJndi(true);
        configJNDI.setJndiName("jndi:connect");
        configJNDI.setPropertyFileNameDDL(JDBCUserGroupService.DEFAULT_DDL_FILE);
        configJNDI.setPropertyFileNameDML(JDBCUserGroupService.DEFAULT_DML_FILE);
        JdbcSecurityConfigValidator validator = new JdbcSecurityConfigValidator(getSecurityManager());
        try {
            configJNDI.setJndiName("");
            // getSecurityManager().saveUserGroupService(configJNDI);
            validator.validateAddUserGroupService(configJNDI);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.JNDINAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        try {
            config.setDriverClassName("");
            // getSecurityManager().saveUserGroupService(config);
            validator.validateAddUserGroupService(config);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.DRIVER_CLASSNAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setDriverClassName("a.b.c");
        try {
            config.setUserName("");
            // getSecurityManager().saveUserGroupService(config);
            validator.validateAddUserGroupService(config);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.USERNAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setUserName("user");
        try {
            config.setConnectURL(null);
            // getSecurityManager().saveUserGroupService(config);
            validator.validateAddUserGroupService(config);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.JDBCURL_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setConnectURL("jdbc:connect");
        try {
            // getSecurityManager().saveUserGroupService(config);
            validator.validateAddUserGroupService(config);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.DRIVER_CLASS_NOT_FOUND_$1, ex.getId());
            Assert.assertEquals("a.b.c", ex.getArgs()[0]);
        }
        config.setDriverClassName("java.lang.String");
        config.setPropertyFileNameDDL(null);
        try {
            // getSecurityManager().saveUserGroupService(config);
            validator.validateAddUserGroupService(config);
        } catch (SecurityConfigException ex) {
            throw new IOException(ex);
        }
        GeoServerSecurityManager secMgr = createNiceMock(GeoServerSecurityManager.class);
        expect(secMgr.listUserGroupServices()).andReturn(new TreeSet<String>(Arrays.asList("default", "jdbc"))).anyTimes();
        GeoServerPlainTextPasswordEncoder pwEncoder = getPlainTextPasswordEncoder();
        expect(secMgr.loadPasswordEncoder(pwEncoder.getName())).andReturn(pwEncoder).anyTimes();
        expect(secMgr.listPasswordValidators()).andReturn(new TreeSet<String>(Arrays.asList(DEFAULT_NAME))).anyTimes();
        replay(secMgr);
        validator = new JdbcSecurityConfigValidator(secMgr);
        JDBCUserGroupServiceConfig oldConfig = new JDBCUserGroupServiceConfig(config);
        config.setPropertyFileNameDML(null);
        try {
            // getSecurityManager().saveUserGroupService(config);
            validator.validateModifiedUserGroupService(config, oldConfig);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.DML_FILE_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setPropertyFileNameDDL(JDBCUserGroupService.DEFAULT_DDL_FILE);
        config.setPropertyFileNameDML(JDBCUserGroupService.DEFAULT_DML_FILE);
        // run only if a temp dir is availbale
        if ((getTempDir()) != null) {
            oldConfig = new JDBCUserGroupServiceConfig(config);
            String invalidPath = ("abc" + (File.separator)) + "def.properties";
            config.setPropertyFileNameDDL(invalidPath);
            try {
                // getSecurityManager().saveUserGroupService(config);
                validator.validateModifiedUserGroupService(config, oldConfig);
                Assert.fail();
            } catch (SecurityConfigException ex) {
                Assert.assertEquals(JDBCSecurityConfigException.DDL_FILE_INVALID, ex.getId());
                Assert.assertEquals(invalidPath, ex.getArgs()[0]);
            }
        }
        config.setPropertyFileNameDDL(JDBCUserGroupService.DEFAULT_DDL_FILE);
        config.setPropertyFileNameDML(JDBCUserGroupService.DEFAULT_DML_FILE);
        // run only if a temp dir is availbale
        if ((getTempDir()) != null) {
            oldConfig = new JDBCUserGroupServiceConfig(config);
            String invalidPath = ("abc" + (File.separator)) + "def.properties";
            config.setPropertyFileNameDML(invalidPath);
            try {
                // getSecurityManager().saveUserGroupService(config);
                validator.validateModifiedUserGroupService(config, oldConfig);
                Assert.fail();
            } catch (SecurityConfigException ex) {
                Assert.assertEquals(JDBCSecurityConfigException.DML_FILE_INVALID, ex.getId());
                Assert.assertEquals(invalidPath, ex.getArgs()[0]);
            }
        }
        config.setPropertyFileNameDDL(null);
        config.setCreatingTables(true);
        config.setPropertyFileNameDML(JDBCUserGroupService.DEFAULT_DML_FILE);
        try {
            // getSecurityManager().saveUserGroupService(config);
            validator.validateModifiedUserGroupService(config, oldConfig);
            Assert.fail();
        } catch (SecurityConfigException ex) {
            Assert.assertEquals(JDBCSecurityConfigException.DDL_FILE_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
    }
}

