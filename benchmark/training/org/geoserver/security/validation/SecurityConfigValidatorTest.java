/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.validation;


import GeoServerRole.ADMIN_ROLE;
import GeoServerSecurityFilterChain.ANONYMOUS_FILTER;
import GeoServerSecurityFilterChain.BASIC_AUTH_FILTER;
import GeoServerSecurityFilterChain.FORM_LOGIN_FILTER;
import GeoServerSecurityFilterChain.ROLE_FILTER;
import PasswordValidator.MASTERPASSWORD_NAME;
import XMLRoleService.DEFAULT_NAME;
import java.io.IOException;
import org.geoserver.security.GeoServerAuthenticationProvider;
import org.geoserver.security.GeoServerRoleService;
import org.geoserver.security.GeoServerUserGroupService;
import org.geoserver.security.auth.UsernamePasswordAuthenticationProvider;
import org.geoserver.security.filter.GeoServerSecurityFilter;
import org.geoserver.security.impl.GeoServerRole;
import org.geoserver.security.impl.MemoryRoleService;
import org.geoserver.security.impl.MemoryUserGroupService;
import org.geoserver.security.password.PasswordValidator;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class SecurityConfigValidatorTest extends GeoServerSystemTestSupport {
    @Test
    public void testMasterConfigValidation() throws Exception {
        SecurityManagerConfig config = new SecurityManagerConfig();
        config.setRoleServiceName(DEFAULT_NAME);
        config.setConfigPasswordEncrypterName(getPBEPasswordEncoder().getName());
        config.getAuthProviderNames().add(GeoServerAuthenticationProvider.DEFAULT_NAME);
        SecurityConfigValidator validator = new SecurityConfigValidator(getSecurityManager());
        validator.validateManagerConfig(config, new SecurityManagerConfig());
        try {
            config.setConfigPasswordEncrypterName("abc");
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("invalid password encoder should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(INVALID_PASSWORD_ENCODER_$1, ex.getId());
        }
        try {
            config.setConfigPasswordEncrypterName(null);
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("no password encoder should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PASSWORD_ENCODER_REQUIRED, ex.getId());
        }
        if ((getSecurityManager().isStrongEncryptionAvailable()) == false) {
            config.setConfigPasswordEncrypterName(getStrongPBEPasswordEncoder().getName());
            try {
                validator.validateManagerConfig(config, new SecurityManagerConfig());
                Assert.fail("invalid strong password encoder should fail");
            } catch (SecurityConfigException.SecurityConfigException ex) {
                Assert.assertEquals(INVALID_STRONG_CONFIG_PASSWORD_ENCODER, ex.getId());
            }
        }
        config.setConfigPasswordEncrypterName(getPBEPasswordEncoder().getName());
        config.setRoleServiceName("XX");
        try {
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("unknown role service should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(ROLE_SERVICE_NOT_FOUND_$1, ex.getId());
        }
        config.setRoleServiceName(null);
        try {
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("null role service should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(ROLE_SERVICE_NOT_FOUND_$1, ex.getId());
        }
        config.setRoleServiceName(DEFAULT_NAME);
        config.getAuthProviderNames().add("XX");
        try {
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("unknown auth provider should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(AUTH_PROVIDER_NOT_FOUND_$1, ex.getId());
        }
        config.getAuthProviderNames().remove("XX");
        // try {
        // validator.validateManagerConfig(config);
        // fail("empty filter chain  should fail");
        // } catch (SecurityConfigException ex){
        // assertEquals(FILTER_CHAIN_NULL_ERROR,ex.getId());
        // assertEquals(0,ex.getArgs().length);
        // }
        GeoServerSecurityFilterChain filterChain = new GeoServerSecurityFilterChain();
        config.setFilterChain(filterChain);
        ServiceLoginFilterChain chain = new ServiceLoginFilterChain();
        filterChain.getRequestChains().add(chain);
        try {
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("chain with no name should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(FILTER_CHAIN_NAME_MANDATORY, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        String chainName = "testChain";
        chain.setName(chainName);
        try {
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("chain with no patterns should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PATTERN_LIST_EMPTY_$1, ex.getId());
            Assert.assertEquals(1, ex.getArgs().length);
            Assert.assertEquals(chainName, ex.getArgs()[0]);
        }
        chain.getPatterns().add("/**");
        chain.setDisabled(true);
        validator.validateManagerConfig(config, new SecurityManagerConfig());
        chain.setDisabled(false);
        try {
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("enabled authentication chain with no filter should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(FILTER_CHAIN_EMPTY_$1, ex.getId());
            Assert.assertEquals(1, ex.getArgs().length);
            Assert.assertEquals(chainName, ex.getArgs()[0]);
        }
        String unknownFilter = "unknown";
        chain.getFilterNames().add(unknownFilter);
        chain.setRoleFilterName("XX");
        try {
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("unknown role filter should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(UNKNOWN_ROLE_FILTER_$2, ex.getId());
            Assert.assertEquals(2, ex.getArgs().length);
            Assert.assertEquals(chainName, ex.getArgs()[0]);
            Assert.assertEquals("XX", ex.getArgs()[1]);
        }
        chain.setRoleFilterName(ROLE_FILTER);
        chain.getFilterNames().add(0, ANONYMOUS_FILTER);
        try {
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("anonymous not last should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(ANONYMOUS_NOT_LAST_$1, ex.getId());
            Assert.assertEquals(1, ex.getArgs().length);
            Assert.assertEquals(chainName, ex.getArgs()[0]);
        }
        chain.getFilterNames().remove(ANONYMOUS_FILTER);
        chain.getFilterNames().add(ANONYMOUS_FILTER);
        try {
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("unknown  filter should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(UNKNOWN_FILTER_$2, ex.getId());
            Assert.assertEquals(2, ex.getArgs().length);
            Assert.assertEquals(chainName, ex.getArgs()[0]);
            Assert.assertEquals(unknownFilter, ex.getArgs()[1]);
        }
        chain.getFilterNames().remove(unknownFilter);
        chain.getFilterNames().add(0, ROLE_FILTER);
        try {
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("no authentication filter should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(NOT_AN_AUTHENTICATION_FILTER_$2, ex.getId());
            Assert.assertEquals(2, ex.getArgs().length);
            Assert.assertEquals(chainName, ex.getArgs()[0]);
            Assert.assertEquals(ROLE_FILTER, ex.getArgs()[1]);
        }
        chain.getFilterNames().remove(ROLE_FILTER);
        chain.getFilterNames().add(0, FORM_LOGIN_FILTER);
        try {
            validator.validateManagerConfig(config, new SecurityManagerConfig());
            Assert.fail("form login filter should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(NOT_A_SERVICE_AUTHENTICATION_FILTER_$2, ex.getId());
            Assert.assertEquals(2, ex.getArgs().length);
            Assert.assertEquals(chainName, ex.getArgs()[0]);
            Assert.assertEquals(FORM_LOGIN_FILTER, ex.getArgs()[1]);
        }
        chain.getFilterNames().remove(FORM_LOGIN_FILTER);
        chain.getFilterNames().add(0, BASIC_AUTH_FILTER);
        validator.validateManagerConfig(config, new SecurityManagerConfig());
    }

    @Test
    public void testNamedServices() {
        SecurityConfigValidator validator = new SecurityConfigValidator(getSecurityManager());
        Class<?>[] extensionPoints = new Class<?>[]{ GeoServerUserGroupService.class, GeoServerRoleService.class, PasswordValidator.class, GeoServerAuthenticationProvider.class, GeoServerSecurityFilter.class };
        for (Class<?> ep : extensionPoints) {
            try {
                validator.checkExtensionPont(ep, "a.b.c");
                Assert.fail("unknown class should fail");
            } catch (SecurityConfigException.SecurityConfigException ex) {
                Assert.assertEquals(ex.getId(), CLASS_NOT_FOUND_$1);
                Assert.assertEquals(ex.getArgs()[0], "a.b.c");
            }
            try {
                validator.checkExtensionPont(ep, "java.lang.String");
                Assert.fail("wrong class should fail");
            } catch (SecurityConfigException.SecurityConfigException ex) {
                Assert.assertEquals(ex.getId(), CLASS_WRONG_TYPE_$2);
                Assert.assertEquals(ex.getArgs()[0], ep);
                Assert.assertEquals(ex.getArgs()[1], "java.lang.String");
            }
            String className = (ep == (GeoServerUserGroupService.class)) ? null : "";
            try {
                validator.checkExtensionPont(ep, className);
                Assert.fail("no class should fail");
            } catch (SecurityConfigException.SecurityConfigException ex) {
                Assert.assertEquals(ex.getId(), CLASSNAME_REQUIRED);
                Assert.assertEquals(0, ex.getArgs().length);
            }
            String name = (ep == (GeoServerUserGroupService.class)) ? null : "";
            try {
                validator.checkServiceName(ep, name);
                Assert.fail("no name should fail");
            } catch (SecurityConfigException.SecurityConfigException ex) {
                Assert.assertEquals(ex.getId(), NAME_REQUIRED);
                Assert.assertEquals(0, ex.getArgs().length);
            }
        }
        // test names
        try {
            validator.validateAddPasswordPolicy(createPolicyConfig(PasswordValidator.DEFAULT_NAME, PasswordValidatorImpl.class, 1, 10));
            Assert.fail("passwd policy already exists should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PASSWD_POLICY_ALREADY_EXISTS_$1, ex.getId());
            Assert.assertEquals(ex.getArgs()[0], PasswordValidator.DEFAULT_NAME);
        }
        PasswordPolicyConfig pwConfig = createPolicyConfig("default2", PasswordValidatorImpl.class, 1, 10);
        try {
            validator.validateModifiedPasswordPolicy(pwConfig, pwConfig);
            Assert.fail("unknown passwd policy should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PASSWD_POLICY_NOT_FOUND_$1, ex.getId());
            Assert.assertEquals(ex.getArgs()[0], "default2");
        }
        try {
            validator.validateAddUserGroupService(createUGConfig(XMLUserGroupService.DEFAULT_NAME, GeoServerUserGroupService.class, getPlainTextPasswordEncoder().getName(), PasswordValidator.DEFAULT_NAME));
            Assert.fail("user group service already exists should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(ex.getId(), USERGROUP_SERVICE_ALREADY_EXISTS_$1);
            Assert.assertEquals(ex.getArgs()[0], XMLUserGroupService.DEFAULT_NAME);
        }
        SecurityUserGroupServiceConfig ugConfig = createUGConfig("default2", GeoServerUserGroupService.class, getPlainTextPasswordEncoder().getName(), PasswordValidator.DEFAULT_NAME);
        try {
            validator.validateModifiedUserGroupService(ugConfig, ugConfig);
            Assert.fail("unknown user group service should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(ex.getId(), USERGROUP_SERVICE_NOT_FOUND_$1);
            Assert.assertEquals(ex.getArgs()[0], "default2");
        }
        try {
            validator.validateAddRoleService(createRoleConfig(DEFAULT_NAME, GeoServerRoleService.class, ADMIN_ROLE.getAuthority()));
            Assert.fail("role service already exists should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(ex.getId(), ROLE_SERVICE_ALREADY_EXISTS_$1);
            Assert.assertEquals(ex.getArgs()[0], DEFAULT_NAME);
        }
        SecurityRoleServiceConfig config = createRoleConfig("default2", GeoServerRoleService.class, ADMIN_ROLE.getAuthority());
        try {
            validator.validateModifiedRoleService(config, config);
            Assert.fail("unknown role service should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(ex.getId(), ROLE_SERVICE_NOT_FOUND_$1);
            Assert.assertEquals(ex.getArgs()[0], "default2");
        }
        try {
            validator.validateAddAuthProvider(createAuthConfig(GeoServerAuthenticationProvider.DEFAULT_NAME, UsernamePasswordAuthenticationProvider.class, XMLUserGroupService.DEFAULT_NAME));
            Assert.fail("auth provider already exists should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(ex.getId(), AUTH_PROVIDER_ALREADY_EXISTS_$1);
            Assert.assertEquals(ex.getArgs()[0], GeoServerAuthenticationProvider.DEFAULT_NAME);
        }
        SecurityAuthProviderConfig aConfig = createAuthConfig("default2", UsernamePasswordAuthenticationProvider.class, XMLUserGroupService.DEFAULT_NAME);
        try {
            validator.validateModifiedAuthProvider(aConfig, aConfig);
            Assert.fail("unknown auth provider should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(ex.getId(), AUTH_PROVIDER_NOT_FOUND_$1);
            Assert.assertEquals(ex.getArgs()[0], "default2");
        }
    }

    @Test
    public void testPasswordPolicy() throws IOException {
        SecurityConfigValidator validator = new SecurityConfigValidator(getSecurityManager());
        PasswordPolicyConfig config = createPolicyConfig(PasswordValidator.DEFAULT_NAME, PasswordValidatorImpl.class, (-1), 10);
        try {
            config.setName("default2");
            validator.validateAddPasswordPolicy(config);
            Assert.fail("invalid min length should fail");
            // getSecurityManager().savePasswordPolicy(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(INVALID_MIN_LENGTH, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        try {
            validator.validateAddPasswordPolicy(config);
            Assert.fail("invalid min length should fail");
            // getSecurityManager().savePasswordPolicy(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(INVALID_MIN_LENGTH, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setMinLength(1);
        config.setMaxLength(0);
        try {
            validator.validateAddPasswordPolicy(config);
            Assert.fail("invalid max length should fail");
            getSecurityManager().savePasswordPolicy(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(INVALID_MAX_LENGTH, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        try {
            validator.validateAddPasswordPolicy(config);
            Assert.fail("invalid max length should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(INVALID_MAX_LENGTH, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setMaxLength((-1));
        try {
            config.setName("");
            validator.validateRemovePasswordPolicy(config);
            Assert.fail("no name should fail");
            // getSecurityManager().removePasswordValidator(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(NAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        try {
            config.setName(PasswordValidator.DEFAULT_NAME);
            validator.validateRemovePasswordPolicy(config);
            Assert.fail("remove active should fail");
            // getSecurityManager().removePasswordValidator(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PASSWD_POLICY_ACTIVE_$2, ex.getId());
            Assert.assertEquals(PasswordValidator.DEFAULT_NAME, ex.getArgs()[0]);
            Assert.assertEquals(XMLUserGroupService.DEFAULT_NAME, ex.getArgs()[1]);
        }
        try {
            config.setName(MASTERPASSWORD_NAME);
            validator.validateRemovePasswordPolicy(config);
            Assert.fail("remove master should fail");
            // getSecurityManager().removePasswordValidator(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PASSWD_POLICY_MASTER_DELETE, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
    }

    @Test
    public void testRoleConfig() throws IOException {
        SecurityRoleServiceConfig config = createRoleConfig(DEFAULT_NAME, MemoryRoleService.class, ADMIN_ROLE.getAuthority());
        SecurityConfigValidator validator = new SecurityConfigValidator(getSecurityManager());
        try {
            config.setName(null);
            validator.validateRemoveRoleService(config);
            Assert.fail("no name should fail");
            // getSecurityManager().removeRoleService(config) ;
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(NAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setName("abcd");
        for (GeoServerRole role : GeoServerRole.SystemRoles) {
            config.setAdminRoleName(role.getAuthority());
            try {
                validator.validateAddRoleService(config);
                Assert.fail("reserved role name should fail");
                // getSecurityManager().saveRoleService(config);
            } catch (SecurityConfigException.SecurityConfigException ex) {
                Assert.assertEquals(RESERVED_ROLE_NAME, ex.getId());
                Assert.assertEquals(role.getAuthority(), ex.getArgs()[0]);
            }
        }
        for (GeoServerRole role : GeoServerRole.SystemRoles) {
            config.setGroupAdminRoleName(role.getAuthority());
            try {
                validator.validateAddRoleService(config);
                Assert.fail("resoerved role name should fail");
                // getSecurityManager().saveRoleService(config);
            } catch (SecurityConfigException.SecurityConfigException ex) {
                Assert.assertEquals(RESERVED_ROLE_NAME, ex.getId());
                Assert.assertEquals(role.getAuthority(), ex.getArgs()[0]);
            }
        }
        try {
            config.setName(DEFAULT_NAME);
            validator.validateRemoveRoleService(config);
            Assert.fail("role service active should fail");
            // getSecurityManager().removeRoleService(config) ;
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(ROLE_SERVICE_ACTIVE_$1, ex.getId());
            Assert.assertEquals(DEFAULT_NAME, ex.getArgs()[0]);
        }
    }

    @Test
    public void testAuthenticationProvider() throws IOException {
        SecurityAuthProviderConfig config = createAuthConfig(GeoServerAuthenticationProvider.DEFAULT_NAME, UsernamePasswordAuthenticationProvider.class, "default2");
        SecurityConfigValidator validator = new SecurityConfigValidator(getSecurityManager());
        try {
            config.setName("default2");
            validator.validateAddAuthProvider(config);
            Assert.fail("user group service not found should fail");
            // getSecurityManager().saveAuthenticationProvider(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(USERGROUP_SERVICE_NOT_FOUND_$1, ex.getId());
            Assert.assertEquals("default2", ex.getArgs()[0]);
        }
        try {
            config.setName("other");
            validator.validateAddAuthProvider(config);
            Assert.fail("user group service not found should fail");
            // getSecurityManager().saveAuthenticationProvider(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(USERGROUP_SERVICE_NOT_FOUND_$1, ex.getId());
            Assert.assertEquals("default2", ex.getArgs()[0]);
        }
        try {
            config.setName("");
            validator.validateRemoveAuthProvider(config);
            Assert.fail("no name should fail");
            // getSecurityManager().removeAuthenticationProvider(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(NAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        try {
            config.setName(GeoServerAuthenticationProvider.DEFAULT_NAME);
            validator.validateRemoveAuthProvider(config);
            Assert.fail("active auth provieder should fail");
            // getSecurityManager().removeAuthenticationProvider(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(AUTH_PROVIDER_ACTIVE_$1, ex.getId());
            Assert.assertEquals(GeoServerAuthenticationProvider.DEFAULT_NAME, ex.getArgs()[0]);
        }
    }

    @Test
    public void testUserGroupConfig() throws IOException {
        SecurityUserGroupServiceConfig config = createUGConfig(XMLUserGroupService.DEFAULT_NAME, MemoryUserGroupService.class, getPlainTextPasswordEncoder().getName(), PasswordValidator.DEFAULT_NAME);
        SecurityConfigValidator validator = new SecurityConfigValidator(getSecurityManager());
        try {
            config.setName("default2");
            config.setPasswordEncoderName("xxx");
            validator.validateAddUserGroupService(config);
            Assert.fail("invalid config password encoder should fail");
            // getSecurityManager().saveUserGroupService(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(INVALID_CONFIG_PASSWORD_ENCODER_$1, ex.getId());
            Assert.assertEquals("xxx", ex.getArgs()[0]);
        }
        if (!(getSecurityManager().isStrongEncryptionAvailable())) {
            config.setPasswordEncoderName(getStrongPBEPasswordEncoder().getName());
            try {
                validator.validateAddUserGroupService(config);
                Assert.fail("invalid strong password encoder should fail");
                // getSecurityManager().saveUserGroupService(config);
            } catch (SecurityConfigException.SecurityConfigException ex) {
                Assert.assertEquals(INVALID_STRONG_PASSWORD_ENCODER, ex.getId());
            }
        }
        try {
            config.setName("other");
            config.setPasswordEncoderName("xxx");
            validator.validateAddUserGroupService(config);
            Assert.fail("invalid config password encoder should fail");
            // getSecurityManager().saveUserGroupService(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(INVALID_CONFIG_PASSWORD_ENCODER_$1, ex.getId());
            Assert.assertEquals("xxx", ex.getArgs()[0]);
        }
        try {
            config.setName("default2");
            config.setPasswordEncoderName("");
            validator.validateAddUserGroupService(config);
            Assert.fail("no password encoder should fail");
            // getSecurityManager().saveUserGroupService(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PASSWD_ENCODER_REQUIRED_$1, ex.getId());
            Assert.assertEquals("default2", ex.getArgs()[0]);
        }
        try {
            config.setName("default3");
            config.setPasswordEncoderName(null);
            // getSecurityManager().saveUserGroupService(config);
            validator.validateAddUserGroupService(config);
            Assert.fail("no password encoder should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PASSWD_ENCODER_REQUIRED_$1, ex.getId());
            Assert.assertEquals("default3", ex.getArgs()[0]);
        }
        config.setPasswordEncoderName(getPlainTextPasswordEncoder().getName());
        try {
            config.setName("default2");
            config.setPasswordPolicyName("default2");
            validator.validateAddUserGroupService(config);
            Assert.fail("unknown password policy should fail");
            // getSecurityManager().saveUserGroupService(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PASSWD_POLICY_NOT_FOUND_$1, ex.getId());
            Assert.assertEquals("default2", ex.getArgs()[0]);
        }
        try {
            config.setName("default3");
            config.setPasswordPolicyName("default2");
            validator.validateAddUserGroupService(config);
            Assert.fail("unkonwn password policy encoder should fail");
            // getSecurityManager().saveUserGroupService(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PASSWD_POLICY_NOT_FOUND_$1, ex.getId());
            Assert.assertEquals("default2", ex.getArgs()[0]);
        }
        try {
            config.setName("default2");
            config.setPasswordPolicyName("");
            // getSecurityManager().saveUserGroupService(config);
            validator.validateAddUserGroupService(config);
            Assert.fail("no password policy should fail");
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PASSWD_POLICY_REQUIRED_$1, ex.getId());
            Assert.assertEquals("default2", ex.getArgs()[0]);
        }
        try {
            config.setName("default3");
            config.setPasswordPolicyName(null);
            validator.validateAddUserGroupService(config);
            Assert.fail("invalidate password policy should fail");
            // getSecurityManager().saveUserGroupService(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(PASSWD_POLICY_REQUIRED_$1, ex.getId());
            Assert.assertEquals("default3", ex.getArgs()[0]);
        }
        try {
            config.setName(null);
            validator.validateRemoveUserGroupService(config);
            Assert.fail("no name should fail");
            getSecurityManager().removeUserGroupService(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(NAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        try {
            config.setName(XMLUserGroupService.DEFAULT_NAME);
            validator.validateRemoveUserGroupService(config);
            Assert.fail("active user group service should fail");
            // getSecurityManager().removeUserGroupService(config);
        } catch (SecurityConfigException.SecurityConfigException ex) {
            Assert.assertEquals(USERGROUP_SERVICE_ACTIVE_$2, ex.getId());
            Assert.assertEquals(XMLUserGroupService.DEFAULT_NAME, ex.getArgs()[0]);
            Assert.assertEquals(GeoServerAuthenticationProvider.DEFAULT_NAME, ex.getArgs()[1]);
        }
    }
}

