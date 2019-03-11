/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.web.mgt;


import DefaultWebSecurityManager.NATIVE_SESSION_MODE;
import Ini.Section;
import IniRealm.USERS_SECTION_NAME;
import java.io.Serializable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.config.WebIniSecurityManagerFactory;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 0.9
 */
public class DefaultWebSecurityManagerTest extends AbstractWebSecurityManagerTest {
    private DefaultWebSecurityManager sm;

    @Test
    public void checkSessionManagerDeterminesContainerSessionMode() {
        sm.setSessionMode(NATIVE_SESSION_MODE);
        WebSessionManager sessionManager = createMock(WebSessionManager.class);
        expect(sessionManager.isServletContainerSessions()).andReturn(true).anyTimes();
        replay(sessionManager);
        sm.setSessionManager(sessionManager);
        Assert.assertTrue("The set SessionManager is not being used to determine isHttpSessionMode.", sm.isHttpSessionMode());
        verify(sessionManager);
    }

    @Test
    public void shiroSessionModeInit() {
        sm.setSessionMode(NATIVE_SESSION_MODE);
    }

    @Test
    public void testLogin() {
        HttpServletRequest mockRequest = createNiceMock(HttpServletRequest.class);
        HttpServletResponse mockResponse = createNiceMock(HttpServletResponse.class);
        expect(mockRequest.getCookies()).andReturn(null);
        expect(mockRequest.getContextPath()).andReturn("/");
        replay(mockRequest);
        Subject subject = newSubject(mockRequest, mockResponse);
        Assert.assertFalse(subject.isAuthenticated());
        subject.login(new UsernamePasswordToken("lonestarr", "vespa"));
        Assert.assertTrue(subject.isAuthenticated());
        Assert.assertNotNull(subject.getPrincipal());
        Assert.assertTrue(subject.getPrincipal().equals("lonestarr"));
    }

    @Test
    public void testSessionTimeout() {
        shiroSessionModeInit();
        long globalTimeout = 100;
        setGlobalSessionTimeout(globalTimeout);
        HttpServletRequest mockRequest = createNiceMock(HttpServletRequest.class);
        HttpServletResponse mockResponse = createNiceMock(HttpServletResponse.class);
        expect(mockRequest.getCookies()).andReturn(null);
        expect(mockRequest.getContextPath()).andReturn("/");
        replay(mockRequest);
        Subject subject = newSubject(mockRequest, mockResponse);
        Session session = subject.getSession();
        Assert.assertEquals(session.getTimeout(), globalTimeout);
        session.setTimeout(125);
        Assert.assertEquals(session.getTimeout(), 125);
        sleep(200);
        try {
            session.getTimeout();
            Assert.fail("Session should have expired.");
        } catch (ExpiredSessionException expected) {
        }
    }

    @Test
    public void testGetSubjectByRequestResponsePair() {
        shiroSessionModeInit();
        HttpServletRequest mockRequest = createNiceMock(HttpServletRequest.class);
        HttpServletResponse mockResponse = createNiceMock(HttpServletResponse.class);
        expect(mockRequest.getCookies()).andReturn(null);
        replay(mockRequest);
        replay(mockResponse);
        Subject subject = newSubject(mockRequest, mockResponse);
        verify(mockRequest);
        verify(mockResponse);
        Assert.assertNotNull(subject);
        Assert.assertTrue((((subject.getPrincipals()) == null) || (subject.getPrincipals().isEmpty())));
        Assert.assertTrue(((subject.getSession(false)) == null));
        Assert.assertFalse(subject.isAuthenticated());
    }

    @Test
    public void testGetSubjectByRequestSessionId() {
        shiroSessionModeInit();
        HttpServletRequest mockRequest = createNiceMock(HttpServletRequest.class);
        HttpServletResponse mockResponse = createNiceMock(HttpServletResponse.class);
        replay(mockRequest);
        replay(mockResponse);
        Subject subject = newSubject(mockRequest, mockResponse);
        Session session = subject.getSession();
        Serializable sessionId = session.getId();
        Assert.assertNotNull(sessionId);
        verify(mockRequest);
        verify(mockResponse);
        mockRequest = createNiceMock(HttpServletRequest.class);
        mockResponse = createNiceMock(HttpServletResponse.class);
        // now simulate the cookie going with the request and the Subject should be acquired based on that:
        Cookie[] cookies = new Cookie[]{ new Cookie(ShiroHttpSession.DEFAULT_SESSION_ID_NAME, sessionId.toString()) };
        expect(mockRequest.getCookies()).andReturn(cookies).anyTimes();
        expect(mockRequest.getParameter(isA(String.class))).andReturn(null).anyTimes();
        replay(mockRequest);
        replay(mockResponse);
        subject = newSubject(mockRequest, mockResponse);
        session = subject.getSession(false);
        Assert.assertNotNull(session);
        Assert.assertEquals(sessionId, session.getId());
        verify(mockRequest);
        verify(mockResponse);
    }

    /**
     * Asserts fix for <a href="https://issues.apache.org/jira/browse/SHIRO-350">SHIRO-350</a>.
     */
    @Test
    public void testBuildNonWebSubjectWithDefaultServletContainerSessionManager() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection(USERS_SECTION_NAME);
        section.put("user1", "user1");
        WebIniSecurityManagerFactory factory = new WebIniSecurityManagerFactory(ini);
        WebSecurityManager securityManager = ((WebSecurityManager) (factory.getInstance()));
        PrincipalCollection principals = new SimplePrincipalCollection("user1", "iniRealm");
        Subject subject = new Subject.Builder(securityManager).principals(principals).buildSubject();
        Assert.assertNotNull(subject);
        Assert.assertEquals("user1", subject.getPrincipal());
    }
}

