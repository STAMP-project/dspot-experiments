/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.rememberme;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import org.geoserver.security.GeoServerSecurityProvider;
import org.geoserver.security.GeoServerSecurityTestSupport;
import org.geoserver.security.config.SecurityNamedServiceConfig;
import org.geoserver.security.filter.GeoServerAuthenticationFilter;
import org.geoserver.security.filter.GeoServerSecurityFilter;
import org.geoserver.test.SystemTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Category(SystemTest.class)
public class RememberMeTest extends GeoServerSecurityTestSupport {
    static class AuthCapturingFilter extends GeoServerSecurityFilter implements GeoServerAuthenticationFilter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            request.setAttribute("auth", auth);
            chain.doFilter(request, response);
        }

        @Override
        public boolean applicableForHtml() {
            return true;
        }

        @Override
        public boolean applicableForServices() {
            return true;
        }
    }

    static class SecurityProvider extends GeoServerSecurityProvider {
        @Override
        public Class<? extends GeoServerSecurityFilter> getFilterClass() {
            return RememberMeTest.AuthCapturingFilter.class;
        }

        @Override
        public GeoServerSecurityFilter createFilter(SecurityNamedServiceConfig config) {
            return new RememberMeTest.AuthCapturingFilter();
        }
    }

    @Test
    public void testRememberMeLogin() throws Exception {
        MockHttpServletRequest request = createRequest("/login");
        request.addParameter("username", "admin");
        request.addParameter("password", "geoserver");
        request.setMethod("POST");
        MockHttpServletResponse response = dispatch(request);
        assertLoginOk(response);
        Assert.assertEquals(0, response.getCookies().length);
        request = createRequest("/login");
        request.addParameter("username", "admin");
        request.addParameter("password", "geoserver");
        request.addParameter("_spring_security_remember_me", "yes");
        request.setMethod("POST");
        response = dispatch(request);
        assertLoginOk(response);
        Assert.assertEquals(1, response.getCookies().length);
        Cookie cookie = ((Cookie) (response.getCookies()[0]));
        request = createRequest("/web/");
        request.setMethod("POST");
        response = dispatch(request);
        Assert.assertNull(request.getAttribute("auth"));
        request = createRequest("/web/");
        request.setMethod("GET");
        request.setCookies(cookie);
        response = dispatch(request);
        Assert.assertTrue(((request.getAttribute("auth")) instanceof RememberMeAuthenticationToken));
    }

    @Test
    public void testRememberMeOtherUserGroupService() throws Exception {
        // TODO Justin, this should work now
        // need to implement this test, at the moment we don't have a way to mock up new users
        // in a memory user group service...
        /* SecurityUserGoupServiceConfig memCfg = new MemoryUserGroupServiceConfigImpl();
        memCfg.setName("memory");
        memCfg.setClassName(MemoryUserGroupService.class.getName());
        memCfg.setPasswordEncoderName(GeoserverPlainTextPasswordEncoder.BeanName);
        memCfg.setPasswordPolicyName(PasswordValidator.DEFAULT_NAME);

        GeoServerSecurityManager secMgr = getSecurityManager();
        secMgr.saveUserGroupService(memCfg);

        GeoserverUserGroupService ug = secMgr.loadUserGroupService("memory");
        GeoserverUser user = ug.createUserObject("foo", "bar", true);
        ug.createStore().addUser(user);

        user = ug.getUserByUsername("foo");
        assertNotNull(user);
         */
    }
}

