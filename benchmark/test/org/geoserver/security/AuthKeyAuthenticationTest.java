/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import AuthenticationKeyFilterConfigException.INVALID_AUTH_KEY_MAPPER_PARAMETER_;
import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_OK;
import HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import XMLUserGroupService.DEFAULT_NAME;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.security.auth.AbstractAuthenticationProviderTest;
import org.geoserver.security.impl.GeoServerRole;
import org.geoserver.security.impl.GeoServerUser;
import org.geoserver.security.validation.FilterConfigException;
import org.geoserver.test.http.AbstractHttpClient;
import org.geotools.data.ows.HTTPResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class AuthKeyAuthenticationTest extends AbstractAuthenticationProviderTest {
    class TestHttpClient extends AbstractHttpClient {
        private String authkey;

        private String response;

        public TestHttpClient(String authkey, String response) {
            super();
            this.authkey = authkey;
            this.response = response;
        }

        @Override
        public HTTPResponse get(final URL url) throws IOException {
            return new HTTPResponse() {
                @Override
                public InputStream getResponseStream() throws IOException {
                    if (url.getPath().substring(1).equals(authkey)) {
                        return new ByteArrayInputStream(new String(response).getBytes());
                    }
                    return new ByteArrayInputStream(new String("").getBytes());
                }

                @Override
                public String getResponseHeader(String arg0) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public String getResponseCharset() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public String getContentType() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public void dispose() {
                    // TODO Auto-generated method stub
                }
            };
        }

        @Override
        public HTTPResponse post(URL url, InputStream in, String arg) throws IOException {
            return null;
        }
    }

    @Test
    public void testMapperParameters() throws Exception {
        String authKeyUrlParam = "myAuthKeyParams";
        String filterName = "testAuthKeyParams1";
        AuthenticationKeyFilterConfig config = new AuthenticationKeyFilterConfig();
        config.setClassName(GeoServerAuthenticationKeyFilter.class.getName());
        config.setName(filterName);
        config.setUserGroupServiceName("ug1");
        config.setAuthKeyParamName(authKeyUrlParam);
        config.setAuthKeyMapperName("fakeMapper");
        Map<String, String> mapperParams = new HashMap<String, String>();
        mapperParams.put("param1", "value1");
        mapperParams.put("param2", "value2");
        config.setMapperParameters(mapperParams);
        getSecurityManager().saveFilter(config);
        GeoServerAuthenticationKeyFilter filter = ((GeoServerAuthenticationKeyFilter) (getSecurityManager().loadFilter(filterName)));
        Assert.assertTrue(((filter.getMapper()) instanceof FakeMapper));
        FakeMapper fakeMapper = ((FakeMapper) (filter.getMapper()));
        Assert.assertEquals("value1", fakeMapper.getMapperParameter("param1"));
        Assert.assertEquals("value2", fakeMapper.getMapperParameter("param2"));
    }

    @Test
    public void testMapperParamsFilterConfigValidation() throws Exception {
        AuthenticationKeyFilterConfigValidator validator = new AuthenticationKeyFilterConfigValidator(getSecurityManager());
        AuthenticationKeyFilterConfig config = new AuthenticationKeyFilterConfig();
        config.setClassName(GeoServerAuthenticationKeyFilter.class.getName());
        config.setName("fakeFilter");
        config.setUserGroupServiceName(DEFAULT_NAME);
        config.setAuthKeyParamName("authkey");
        config.setAuthKeyMapperName("fakeMapper");
        Map<String, String> mapperParams = new HashMap<String, String>();
        mapperParams.put("param1", "value1");
        mapperParams.put("param2", "value2");
        config.setMapperParameters(mapperParams);
        boolean failed = false;
        try {
            validator.validateFilterConfig(config);
        } catch (FilterConfigException ex) {
            failed = true;
        }
        Assert.assertFalse(failed);
        mapperParams.put("param3", "value3");
        try {
            validator.validateFilterConfig(config);
        } catch (FilterConfigException ex) {
            Assert.assertEquals(INVALID_AUTH_KEY_MAPPER_PARAMETER_.3, ex.getId());
            Assert.assertEquals(1, ex.getArgs().length);
            Assert.assertEquals("param3", ex.getArgs()[0]);
            LOGGER.info(ex.getMessage());
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    @Test
    public void testFileBasedWithSession() throws Exception {
        String authKeyUrlParam = "myAuthKey";
        String filterName = "testAuthKeyFilter1";
        AuthenticationKeyFilterConfig config = new AuthenticationKeyFilterConfig();
        config.setClassName(GeoServerAuthenticationKeyFilter.class.getName());
        config.setName(filterName);
        config.setUserGroupServiceName("ug1");
        config.setAuthKeyParamName(authKeyUrlParam);
        config.setAuthKeyMapperName("propertyMapper");
        getSecurityManager().saveFilter(config);
        GeoServerAuthenticationKeyFilter filter = ((GeoServerAuthenticationKeyFilter) (getSecurityManager().loadFilter(filterName)));
        PropertyAuthenticationKeyMapper mapper = ((PropertyAuthenticationKeyMapper) (filter.getMapper()));
        mapper.synchronize();
        prepareFilterChain(pattern, filterName);
        modifyChain(pattern, false, true, null);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        // test success
        String authKey = null;
        for (Map.Entry<Object, Object> entry : mapper.authKeyProps.entrySet()) {
            if (testUserName.equals(entry.getValue())) {
                authKey = ((String) (entry.getKey()));
                break;
            }
        }
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setQueryString(((authKeyUrlParam + "=") + authKey));
        request.addParameter(authKeyUrlParam, authKey);
        getProxy().doFilter(request, response, chain);
        Assert.assertFalse(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        SecurityContext ctx = ((SecurityContext) (request.getSession(false).getAttribute(SPRING_SECURITY_CONTEXT_KEY)));
        Assert.assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        Assert.assertNotNull(auth);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, auth.getPrincipal());
        // check unknown user
        username = "unknown";
        password = username;
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setQueryString((authKeyUrlParam + "=abc"));
        request.addParameter(authKeyUrlParam, "abc");
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        // check disabled user
        username = testUserName;
        password = username;
        updateUser("ug1", username, false);
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setQueryString(((authKeyUrlParam + "=") + authKey));
        request.addParameter(authKeyUrlParam, authKey);
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        updateUser("ug1", username, true);
        insertAnonymousFilter();
        request = createRequest("foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        // Anonymous context is not stored in http session, no further testing
        removeAnonymousFilter();
    }

    @Test
    public void testUserPropertyWithCache() throws Exception {
        String authKeyUrlParam = "myAuthKey";
        String filterName = "testAuthKeyFilter2";
        AuthenticationKeyFilterConfig config = new AuthenticationKeyFilterConfig();
        config.setClassName(GeoServerAuthenticationKeyFilter.class.getName());
        config.setName(filterName);
        config.setUserGroupServiceName("ug1");
        config.setAuthKeyParamName(authKeyUrlParam);
        config.setAuthKeyMapperName("userPropertyMapper");
        getSecurityManager().saveFilter(config);
        GeoServerAuthenticationKeyFilter filter = ((GeoServerAuthenticationKeyFilter) (getSecurityManager().loadFilter(filterName)));
        UserPropertyAuthenticationKeyMapper mapper = ((UserPropertyAuthenticationKeyMapper) (filter.getMapper()));
        mapper.synchronize();
        prepareFilterChain(pattern, filterName);
        modifyChain(pattern, false, false, null);
        SecurityContextHolder.getContext().setAuthentication(null);
        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        // test success
        GeoServerUser user = ((GeoServerUser) (getSecurityManager().loadUserGroupService("ug1").loadUserByUsername(testUserName)));
        String authKey = user.getProperties().getProperty(mapper.getUserPropertyName());
        Assert.assertNotNull(authKey);
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setQueryString(((authKeyUrlParam + "=") + authKey));
        request.addParameter(authKeyUrlParam, authKey);
        getProxy().doFilter(request, response, chain);
        Assert.assertFalse(((response.getStatus()) == (MockHttpServletResponse.SC_MOVED_TEMPORARILY)));
        Authentication auth = getSecurityManager().getAuthenticationCache().get(filterName, authKey);
        Assert.assertNotNull(auth);
        Assert.assertNull(request.getSession(false));
        checkForAuthenticatedRole(auth);
        Assert.assertEquals(testUserName, auth.getPrincipal());
        // check unknown user
        username = "unknown";
        password = username;
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setQueryString((authKeyUrlParam + "=abc"));
        request.addParameter(authKeyUrlParam, "abc");
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        getSecurityManager().getAuthenticationCache().removeAll();
        // check disabled user
        username = testUserName;
        password = username;
        updateUser("ug1", username, false);
        request = createRequest("/foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        request.setQueryString(((authKeyUrlParam + "=") + authKey));
        request.addParameter(authKeyUrlParam, authKey);
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_FORBIDDEN, response.getStatus());
        Assert.assertNull(getSecurityManager().getAuthenticationCache().get(filterName, authKey));
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        updateUser("ug1", username, true);
        insertAnonymousFilter();
        request = createRequest("foo/bar");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);
        Assert.assertEquals(SC_OK, response.getStatus());
        // Anonymous context is not stored in http session, no further testing
        removeAnonymousFilter();
    }

    @Test
    public void testWebServiceAuthKeyMapper() throws Exception {
        GeoServerUserGroupService ugservice = createUserGroupService("testWebServiceAuthKey");
        GeoServerUserGroupStore ugstore = ugservice.createStore();
        GeoServerUser u1 = ugstore.createUserObject("user1", "passwd1", true);
        ugstore.addUser(u1);
        GeoServerUser u2 = ugstore.createUserObject("user2", "passwd2", true);
        ugstore.addUser(u2);
        ugstore.store();
        WebServiceAuthenticationKeyMapper propMapper = GeoServerExtensions.extensions(WebServiceAuthenticationKeyMapper.class).iterator().next();
        propMapper.setUserGroupServiceName("testWebServiceAuthKey");
        propMapper.setSecurityManager(getSecurityManager());
        propMapper.setWebServiceUrl("http://service/{key}");
        propMapper.setHttpClient(new AuthKeyAuthenticationTest.TestHttpClient("testkey", "user1"));
        GeoServerUser user = propMapper.getUser("testkey");
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getUsername(), "user1");
        boolean error = false;
        try {
            user = propMapper.getUser("wrongkey");
        } catch (UsernameNotFoundException e) {
            error = true;
        }
        Assert.assertTrue(error);
    }

    @Test
    public void testWebServiceAuthKeyMapperSearchUser() throws Exception {
        GeoServerUserGroupService ugservice = createUserGroupService("testWebServiceAuthKey2");
        GeoServerUserGroupStore ugstore = ugservice.createStore();
        GeoServerUser u1 = ugstore.createUserObject("user1", "passwd1", true);
        ugstore.addUser(u1);
        GeoServerUser u2 = ugstore.createUserObject("user2", "passwd2", true);
        ugstore.addUser(u2);
        ugstore.store();
        WebServiceAuthenticationKeyMapper propMapper = GeoServerExtensions.extensions(WebServiceAuthenticationKeyMapper.class).iterator().next();
        propMapper.setUserGroupServiceName("testWebServiceAuthKey2");
        propMapper.setSecurityManager(getSecurityManager());
        propMapper.setWebServiceUrl("http://service/{key}");
        propMapper.setSearchUser("^.*?\"user\"\\s*:\\s*\"([^\"]+)\".*$");
        propMapper.setHttpClient(new AuthKeyAuthenticationTest.TestHttpClient("testkey", "{\n    \"user\": \"user1\", \"detail\": \"mydetail\"\n   }"));
        GeoServerUser user = propMapper.getUser("testkey");
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getUsername(), "user1");
        propMapper.setSearchUser("^.*?<username>(.*?)</username>.*$");
        propMapper.setHttpClient(new AuthKeyAuthenticationTest.TestHttpClient("testkey", "<root>\n<userdetail>\n<username>user1</username>\n</userdetail>\n</root>"));
        user = propMapper.getUser("testkey");
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getUsername(), "user1");
        user = propMapper.getUser("wrongkey");
        Assert.assertNull(user);
    }

    @Test
    public void testWebServiceAuthKeyBodyResponseUGS() throws Exception {
        WebServiceBodyResponseUserGroupServiceConfig config = new WebServiceBodyResponseUserGroupServiceConfig();
        config.setName("testWebServiceAuthKey3");
        config.setClassName(WebServiceBodyResponseUserGroupService.class.getName());
        config.setPasswordEncoderName(getPBEPasswordEncoder().getName());
        config.setPasswordPolicyName(PasswordValidator.DEFAULT_NAME);
        config.setSearchRoles("^.*?\"roles\"\\s*:\\s*\"([^\"]+)\".*$");
        config.setAvailableGroups("GROUP_MYROLE_1, GROUP_MYROLE_2");
        /* ,isNewUGService(name) */
        getSecurityManager().saveUserGroupService(config);
        GeoServerUserGroupService webServiceAuthKeyBodyResponseUGS = getSecurityManager().loadUserGroupService("testWebServiceAuthKey3");
        Assert.assertNotNull(webServiceAuthKeyBodyResponseUGS);
        WebServiceAuthenticationKeyMapper propMapper = GeoServerExtensions.extensions(WebServiceAuthenticationKeyMapper.class).iterator().next();
        propMapper.setUserGroupServiceName("testWebServiceAuthKey3");
        propMapper.setSecurityManager(getSecurityManager());
        propMapper.setWebServiceUrl("http://service/{key}");
        propMapper.setSearchUser("^.*?\"user\"\\s*:\\s*\"([^\"]+)\".*$");
        propMapper.setHttpClient(new AuthKeyAuthenticationTest.TestHttpClient("testkey", "{\n    \"user\": \"user1\", \"detail\": \"mydetail\", \"roles\": \"myrole_1, myrole_2\"\n   }"));
        GeoServerUser user = propMapper.getUser("testkey");
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getUsername(), "user1");
        Assert.assertNotNull(user.getAuthorities());
        Assert.assertEquals(2, user.getAuthorities().size());
        Assert.assertTrue(user.getAuthorities().contains(new GeoServerRole("ROLE_MYROLE_1")));
        Assert.assertTrue(user.getAuthorities().contains(new GeoServerRole("ROLE_MYROLE_2")));
        Assert.assertEquals(2, webServiceAuthKeyBodyResponseUGS.getGroupCount());
        Assert.assertEquals(2, webServiceAuthKeyBodyResponseUGS.getUserGroups().size());
        Assert.assertEquals(webServiceAuthKeyBodyResponseUGS.getUserGroups(), webServiceAuthKeyBodyResponseUGS.getGroupsForUser(user));
    }

    @Test
    public void testAuthKeyMapperSynchronize() throws Exception {
        GeoServerUserGroupService ugservice = createUserGroupService("testAuthKey");
        GeoServerUserGroupStore ugstore = ugservice.createStore();
        GeoServerUser u1 = ugstore.createUserObject("user1", "passwd1", true);
        ugstore.addUser(u1);
        GeoServerUser u2 = ugstore.createUserObject("user2", "passwd2", true);
        ugstore.addUser(u2);
        ugstore.store();
        PropertyAuthenticationKeyMapper propMapper = GeoServerExtensions.extensions(PropertyAuthenticationKeyMapper.class).iterator().next();
        UserPropertyAuthenticationKeyMapper userpropMapper = GeoServerExtensions.extensions(UserPropertyAuthenticationKeyMapper.class).iterator().next();
        propMapper.setSecurityManager(getSecurityManager());
        propMapper.setUserGroupServiceName("testAuthKey");
        userpropMapper.setSecurityManager(getSecurityManager());
        userpropMapper.setUserGroupServiceName("testAuthKey");
        // File Property Mapper
        Assert.assertEquals(2, propMapper.synchronize());
        File authKeyFile = new File(getSecurityManager().getUserGroupRoot(), "testAuthKey");
        authKeyFile = new File(authKeyFile, "authkeys.properties");
        Assert.assertTrue(authKeyFile.exists());
        Properties props = new Properties();
        loadPropFile(authKeyFile, props);
        Assert.assertEquals(2, props.size());
        String user1KeyA = null;
        String user2KeyA = null;
        String user3KeyA = null;
        String user1KeyB = null;
        String user2KeyB = null;
        String user3KeyB = null;
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            if ("user1".equals(entry.getValue()))
                user1KeyA = ((String) (entry.getKey()));

            if ("user2".equals(entry.getValue()))
                user2KeyA = ((String) (entry.getKey()));

        }
        Assert.assertNotNull(user1KeyA);
        Assert.assertNotNull(user2KeyA);
        Assert.assertEquals(u1, propMapper.getUser(user1KeyA));
        Assert.assertEquals(u2, propMapper.getUser(user2KeyA));
        Assert.assertNull(propMapper.getUser("blblal"));
        // user property mapper
        Assert.assertEquals(2, userpropMapper.synchronize());
        u1 = ((GeoServerUser) (ugservice.loadUserByUsername("user1")));
        user1KeyB = u1.getProperties().getProperty(userpropMapper.getUserPropertyName());
        u2 = ((GeoServerUser) (ugservice.loadUserByUsername("user2")));
        user2KeyB = u2.getProperties().getProperty(userpropMapper.getUserPropertyName());
        Assert.assertEquals(u1, userpropMapper.getUser(user1KeyB));
        Assert.assertEquals(u2, userpropMapper.getUser(user2KeyB));
        Assert.assertNull(userpropMapper.getUser("blblal"));
        // modify user/group database
        ugstore = ugservice.createStore();
        GeoServerUser u3 = ugstore.createUserObject("user3", "passwd3", true);
        ugstore.addUser(u3);
        ugstore.removeUser(u1);
        ugstore.store();
        Assert.assertEquals(1, propMapper.synchronize());
        props = new Properties();
        loadPropFile(authKeyFile, props);
        Assert.assertEquals(2, props.size());
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            if ("user2".equals(entry.getValue()))
                Assert.assertEquals(user2KeyA, ((String) (entry.getKey())));

            if ("user3".equals(entry.getValue()))
                user3KeyA = ((String) (entry.getKey()));

        }
        Assert.assertNotNull(user3KeyA);
        Assert.assertNull(propMapper.getUser(user1KeyA));
        Assert.assertEquals(u2, propMapper.getUser(user2KeyA));
        Assert.assertEquals(u3, propMapper.getUser(user3KeyA));
        // user property mapper
        Assert.assertEquals(1, userpropMapper.synchronize());
        u2 = ((GeoServerUser) (ugservice.loadUserByUsername("user2")));
        Assert.assertEquals(user2KeyB, u2.getProperties().getProperty(userpropMapper.getUserPropertyName()));
        u3 = ((GeoServerUser) (ugservice.loadUserByUsername("user3")));
        user3KeyB = u3.getProperties().getProperty(userpropMapper.getUserPropertyName());
        Assert.assertNull(userpropMapper.getUser(user1KeyB));
        Assert.assertEquals(u2, userpropMapper.getUser(user2KeyB));
        Assert.assertEquals(u3, userpropMapper.getUser(user3KeyB));
        // test disabled user
        ugstore = ugservice.createStore();
        u2 = ((GeoServerUser) (ugstore.loadUserByUsername("user2")));
        u2.setEnabled(false);
        ugstore.updateUser(u2);
        ugstore.store();
        Assert.assertNull(propMapper.getUser(user2KeyA));
        Assert.assertNull(userpropMapper.getUser(user2KeyB));
    }
}

