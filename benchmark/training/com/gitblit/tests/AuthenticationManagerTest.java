/**
 * Copyright 2013 gitblit.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gitblit.tests;


import Keys.realm.container.autoAccounts.adminRole;
import Keys.realm.container.autoAccounts.displayName;
import Keys.realm.container.autoAccounts.emailAddress;
import Keys.realm.container.autoAccounts.locale;
import Keys.realm.container.autoCreateAccounts;
import com.gitblit.manager.IAuthenticationManager;
import com.gitblit.manager.UserManager;
import com.gitblit.models.UserModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import org.junit.Assert;
import org.junit.Test;


/**
 * Class for testing local authentication.
 *
 * @author James Moger
 */
@SuppressWarnings("deprecation")
public class AuthenticationManagerTest extends GitblitUnitTest {
    UserManager users;

    private static final class DummyHttpServletRequest implements HttpServletRequest {
        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String name) {
            return null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return null;
        }

        @Override
        public String getProtocol() {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public String getRemoteHost() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object o) {
        }

        @Override
        public void removeAttribute(String name) {
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public String getRealPath(String path) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public String getLocalAddr() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            return null;
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public DispatcherType getDispatcherType() {
            return null;
        }

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public Cookie[] getCookies() {
            return null;
        }

        @Override
        public long getDateHeader(String name) {
            return 0;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return null;
        }

        @Override
        public int getIntHeader(String name) {
            return 0;
        }

        @Override
        public String getMethod() {
            return null;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getContextPath() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            if ((role != null) && ("admin".equals(role))) {
                return true;
            }
            return false;
        }

        @Override
        public Principal getUserPrincipal() {
            return new Principal() {
                @Override
                public String getName() {
                    return "sunnyjim";
                }
            };
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public String getRequestURI() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return null;
        }

        @Override
        public String getServletPath() {
            return null;
        }

        @Override
        public HttpSession getSession(boolean create) {
            return null;
        }

        final Map<String, Object> sessionAttributes = new HashMap<String, Object>();

        @Override
        public HttpSession getSession() {
            return new HttpSession() {
                @Override
                public long getCreationTime() {
                    return 0;
                }

                @Override
                public String getId() {
                    return null;
                }

                @Override
                public long getLastAccessedTime() {
                    return 0;
                }

                @Override
                public ServletContext getServletContext() {
                    return null;
                }

                @Override
                public void setMaxInactiveInterval(int interval) {
                }

                @Override
                public int getMaxInactiveInterval() {
                    return 0;
                }

                @Override
                public HttpSessionContext getSessionContext() {
                    return null;
                }

                @Override
                public Object getAttribute(String name) {
                    return sessionAttributes.get(name);
                }

                @Override
                public Object getValue(String name) {
                    return null;
                }

                @Override
                public Enumeration<String> getAttributeNames() {
                    return Collections.enumeration(sessionAttributes.keySet());
                }

                @Override
                public String[] getValueNames() {
                    return null;
                }

                @Override
                public void setAttribute(String name, Object value) {
                }

                @Override
                public void putValue(String name, Object value) {
                }

                @Override
                public void removeAttribute(String name) {
                }

                @Override
                public void removeValue(String name) {
                }

                @Override
                public void invalidate() {
                }

                @Override
                public boolean isNew() {
                    return false;
                }
            };
        }

        @Override
        public String changeSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return false;
        }

        @Override
        public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
            return false;
        }

        @Override
        public void login(String username, String password) throws ServletException {
        }

        @Override
        public void logout() throws ServletException {
        }

        @Override
        public Collection<Part> getParts() throws IOException, ServletException {
            return null;
        }

        @Override
        public Part getPart(String name) throws IOException, ServletException {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
            return null;
        }
    }

    HashMap<String, Object> settings = new HashMap<String, Object>();

    @Test
    public void testAuthenticate() throws Exception {
        IAuthenticationManager auth = newAuthenticationManager();
        UserModel user = new UserModel("sunnyjim");
        user.password = "password";
        users.updateUserModel(user);
        Assert.assertNotNull(auth.authenticate(user.username, user.password.toCharArray(), null));
        user.disabled = true;
        users.updateUserModel(user);
        Assert.assertNull(auth.authenticate(user.username, user.password.toCharArray(), null));
        users.deleteUserModel(user);
    }

    @Test
    public void testContenairAuthenticate() throws Exception {
        settings.put(autoCreateAccounts, "true");
        settings.put(displayName, "displayName");
        settings.put(emailAddress, "emailAddress");
        settings.put(adminRole, "admin");
        settings.put(locale, "locale");
        AuthenticationManagerTest.DummyHttpServletRequest request = new AuthenticationManagerTest.DummyHttpServletRequest();
        request.sessionAttributes.put("displayName", "Sunny Jim");
        request.sessionAttributes.put("emailAddress", "Jim.Sunny@gitblit.com");
        request.sessionAttributes.put("locale", "it");
        IAuthenticationManager auth = newAuthenticationManager();
        UserModel user = auth.authenticate(request);
        Assert.assertTrue(user.canAdmin);
        Assert.assertEquals("Sunny Jim", user.displayName);
        Assert.assertEquals("Jim.Sunny@gitblit.com", user.emailAddress);
        Assert.assertEquals(Locale.ITALIAN, user.getPreferences().getLocale());
    }

    @Test
    public void testContenairAuthenticateEmpty() throws Exception {
        settings.put(autoCreateAccounts, "true");
        settings.put(displayName, "displayName");
        settings.put(emailAddress, "emailAddress");
        settings.put(adminRole, "notAdmin");
        AuthenticationManagerTest.DummyHttpServletRequest request = new AuthenticationManagerTest.DummyHttpServletRequest();
        IAuthenticationManager auth = newAuthenticationManager();
        UserModel user = auth.authenticate(request);
        Assert.assertFalse(user.canAdmin);
        Assert.assertEquals("sunnyjim", user.displayName);
        Assert.assertNull(user.emailAddress);
        Assert.assertNull(user.getPreferences().getLocale());
    }
}

