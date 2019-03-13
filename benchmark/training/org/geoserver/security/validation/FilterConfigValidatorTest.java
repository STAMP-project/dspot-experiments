/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.validation;


import FilterConfigException.HEADER_ATTRIBUTE_NAME_REQUIRED;
import FilterConfigException.INVALID_ENTRY_POINT;
import FilterConfigException.INVALID_SECONDS;
import FilterConfigException.NO_AUTH_ENTRY_POINT;
import FilterConfigException.PASSWORD_PARAMETER_NAME_NEEDED;
import FilterConfigException.PRINCIPAL_HEADER_ATTRIBUTE_NEEDED;
import FilterConfigException.SECURITY_METADATA_SOURCE_NEEDED;
import FilterConfigException.UNKNOWN_ROLE_CONVERTER;
import FilterConfigException.UNKNOWN_SECURITY_METADATA_SOURCE;
import FilterConfigException.UNKNOWN_USER_GROUP_SERVICE;
import FilterConfigException.USER_GROUP_SERVICE_NEEDED;
import FilterConfigException.USER_PARAMETER_NAME_NEEDED;
import GeoServerSecurityFilterChain.FILTER_SECURITY_INTERCEPTOR;
import XMLUserGroupService.DEFAULT_NAME;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.test.GeoServerMockTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class FilterConfigValidatorTest extends GeoServerMockTestSupport {
    @Test
    public void testDigestConfigValidation() throws Exception {
        DigestAuthenticationFilterConfig config = new DigestAuthenticationFilterConfig();
        config.setClassName(GeoServerDigestAuthenticationFilter.class.getName());
        config.setName("testDigest");
        GeoServerSecurityManager secMgr = getSecurityManager();
        FilterConfigValidator validator = new FilterConfigValidator(secMgr);
        try {
            validator.validateFilterConfig(config);
            Assert.fail("no user group service should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(USER_GROUP_SERVICE_NEEDED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setUserGroupServiceName("blabla");
        try {
            validator.validateFilterConfig(config);
            Assert.fail("unknown user group service should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(UNKNOWN_USER_GROUP_SERVICE, ex.getId());
            Assert.assertEquals(1, ex.getArgs().length);
            Assert.assertEquals("blabla", ex.getArgs()[0]);
        }
        config.setUserGroupServiceName(DEFAULT_NAME);
        config.setNonceValiditySeconds((-1));
        try {
            validator.validateFilterConfig(config);
            Assert.fail("invalid nonce should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(INVALID_SECONDS, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setNonceValiditySeconds(100);
        validator.validateFilterConfig(config);
    }

    @Test
    public void testRoleFilterConfigValidation() throws Exception {
        RoleFilterConfig config = new RoleFilterConfig();
        config.setClassName(GeoServerRoleFilter.class.getName());
        config.setName("testRoleFilter");
        GeoServerSecurityManager secMgr = getSecurityManager();
        FilterConfigValidator validator = new FilterConfigValidator(secMgr);
        try {
            validator.validateFilterConfig(config);
            Assert.fail("no header attribute should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(HEADER_ATTRIBUTE_NAME_REQUIRED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setHttpResponseHeaderAttrForIncludedRoles("roles");
        config.setRoleConverterName("unknown");
        try {
            validator.validateFilterConfig(config);
            Assert.fail("unkonwn role converter should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(UNKNOWN_ROLE_CONVERTER, ex.getId());
            Assert.assertEquals(1, ex.getArgs().length);
            Assert.assertEquals("unknown", ex.getArgs()[0]);
        }
        config.setRoleConverterName(null);
        validator.validateFilterConfig(config);
    }

    @Test
    public void testSecurityInterceptorFilterConfigValidation() throws Exception {
        SecurityInterceptorFilterConfig config = new SecurityInterceptorFilterConfig();
        config.setClassName(GeoServerSecurityInterceptorFilter.class.getName());
        config.setName("testInterceptFilter");
        GeoServerSecurityManager secMgr = getSecurityManager();
        FilterConfigValidator validator = new FilterConfigValidator(secMgr);
        try {
            validator.validateFilterConfig(config);
            Assert.fail("no metadata source should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(SECURITY_METADATA_SOURCE_NEEDED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setSecurityMetadataSource("unknown");
        try {
            validator.validateFilterConfig(config);
            Assert.fail("unknown metadata source should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(UNKNOWN_SECURITY_METADATA_SOURCE, ex.getId());
            Assert.assertEquals(1, ex.getArgs().length);
            Assert.assertEquals("unknown", ex.getArgs()[0]);
        }
    }

    @Test
    public void testX509FilterConfigValidation() throws Exception {
        X509CertificateAuthenticationFilterConfig config = new X509CertificateAuthenticationFilterConfig();
        config.setClassName(GeoServerX509CertificateAuthenticationFilter.class.getName());
        config.setName("testX509");
        check(((J2eeAuthenticationBaseFilterConfig) (config)));
    }

    @Test
    public void testUsernamePasswordFilterConfigValidation() throws Exception {
        UsernamePasswordAuthenticationFilterConfig config = new UsernamePasswordAuthenticationFilterConfig();
        config.setClassName(GeoServerUserNamePasswordAuthenticationFilter.class.getName());
        config.setName("testUsernamePassword");
        FilterConfigValidator validator = new FilterConfigValidator(getSecurityManager());
        try {
            validator.validateFilterConfig(config);
            Assert.fail("no user should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(USER_PARAMETER_NAME_NEEDED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setUsernameParameterName("user");
        try {
            validator.validateFilterConfig(config);
            Assert.fail("no password should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(PASSWORD_PARAMETER_NAME_NEEDED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setPasswordParameterName("password");
        validator.validateFilterConfig(config);
    }

    @Test
    public void testJ2eeFilterConfigValidation() throws Exception {
        J2eeAuthenticationFilterConfig config = new J2eeAuthenticationFilterConfig();
        config.setClassName(GeoServerJ2eeAuthenticationFilter.class.getName());
        config.setName("testJ2ee");
        check(((J2eeAuthenticationBaseFilterConfig) (config)));
    }

    @Test
    public void testExceptionTranslationFilterConfigValidation() throws Exception {
        ExceptionTranslationFilterConfig config = new ExceptionTranslationFilterConfig();
        config.setClassName(GeoServerExceptionTranslationFilter.class.getName());
        config.setName("testEx");
        FilterConfigValidator validator = new FilterConfigValidator(getSecurityManager());
        config.setAuthenticationFilterName("unknown");
        try {
            validator.validateFilterConfig(config);
            Assert.fail("invalid entry point should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(INVALID_ENTRY_POINT, ex.getId());
            Assert.assertEquals(1, ex.getArgs().length);
            Assert.assertEquals("unknown", ex.getArgs()[0]);
        }
        config.setAuthenticationFilterName(FILTER_SECURITY_INTERCEPTOR);
        try {
            validator.validateFilterConfig(config);
            Assert.fail("no auth entry point should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(NO_AUTH_ENTRY_POINT, ex.getId());
            Assert.assertEquals(1, ex.getArgs().length);
            Assert.assertEquals(FILTER_SECURITY_INTERCEPTOR, ex.getArgs()[0]);
        }
        config.setAuthenticationFilterName(null);
        validator.validateFilterConfig(config);
    }

    @Test
    public void testRequestHeaderFilterConfigValidation() throws Exception {
        RequestHeaderAuthenticationFilterConfig config = new RequestHeaderAuthenticationFilterConfig();
        config.setClassName(GeoServerRequestHeaderAuthenticationFilter.class.getName());
        config.setName("testRequestHeader");
        FilterConfigValidator validator = new FilterConfigValidator(getSecurityManager());
        try {
            validator.validateFilterConfig(config);
            Assert.fail("no principal header attribute should fail");
        } catch (FilterConfigException ex) {
            Assert.assertEquals(PRINCIPAL_HEADER_ATTRIBUTE_NEEDED, ex.getId());
            Assert.assertEquals(0, ex.getArgs().length);
        }
        config.setPrincipalHeaderAttribute("user");
        check(((PreAuthenticatedUserNameFilterConfig) (config)));
    }
}

