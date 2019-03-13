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
package org.apache.shiro.session.mgt;


import java.util.UUID;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.SessionListenerAdapter;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.easymock.IArgumentMatcher;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for the {@link DefaultSessionManager DefaultSessionManager} implementation.
 */
public class DefaultSessionManagerTest {
    DefaultSessionManager sm = null;

    @Test
    public void testGlobalTimeout() {
        long timeout = 1000;
        sm.setGlobalSessionTimeout(timeout);
        Session session = sm.start(null);
        Assert.assertNotNull(session);
        Assert.assertNotNull(session.getId());
        Assert.assertEquals(session.getTimeout(), timeout);
    }

    @Test
    public void testSessionListenerStartNotification() {
        final boolean[] started = new boolean[1];
        SessionListener listener = new SessionListenerAdapter() {
            public void onStart(Session session) {
                started[0] = true;
            }
        };
        sm.getSessionListeners().add(listener);
        sm.start(null);
        Assert.assertTrue(started[0]);
    }

    @Test
    public void testSessionListenerStopNotification() {
        final boolean[] stopped = new boolean[1];
        SessionListener listener = new SessionListenerAdapter() {
            public void onStop(Session session) {
                stopped[0] = true;
            }
        };
        sm.getSessionListeners().add(listener);
        Session session = sm.start(null);
        sm.stop(new DefaultSessionKey(session.getId()));
        Assert.assertTrue(stopped[0]);
    }

    // asserts fix for SHIRO-388:
    // Ensures that a session attribute can be accessed in the listener without
    // causing a stack overflow exception.
    @Test
    public void testSessionListenerStopNotificationWithReadAttribute() {
        final boolean[] stopped = new boolean[1];
        final String[] value = new String[1];
        SessionListener listener = new SessionListenerAdapter() {
            public void onStop(Session session) {
                stopped[0] = true;
                value[0] = ((String) (session.getAttribute("foo")));
            }
        };
        sm.getSessionListeners().add(listener);
        Session session = sm.start(null);
        session.setAttribute("foo", "bar");
        sm.stop(new DefaultSessionKey(session.getId()));
        Assert.assertTrue(stopped[0]);
        Assert.assertEquals("bar", value[0]);
    }

    @Test
    public void testSessionListenerExpiredNotification() {
        final boolean[] expired = new boolean[1];
        SessionListener listener = new SessionListenerAdapter() {
            public void onExpiration(Session session) {
                expired[0] = true;
            }
        };
        sm.getSessionListeners().add(listener);
        sm.setGlobalSessionTimeout(100);
        Session session = sm.start(null);
        sleep(150);
        try {
            sm.checkValid(new DefaultSessionKey(session.getId()));
            Assert.fail("check should have thrown an exception.");
        } catch (InvalidSessionException expected) {
            // do nothing - expected.
        }
        Assert.assertTrue(expired[0]);
    }

    @Test
    public void testSessionDeleteOnExpiration() {
        sm.setGlobalSessionTimeout(100);
        SessionDAO sessionDAO = createMock(SessionDAO.class);
        sm.setSessionDAO(sessionDAO);
        String sessionId1 = UUID.randomUUID().toString();
        final SimpleSession session1 = new SimpleSession();
        session1.setId(sessionId1);
        final Session[] activeSession = new SimpleSession[]{ session1 };
        sm.setSessionFactory(new SessionFactory() {
            public Session createSession(SessionContext initData) {
                return activeSession[0];
            }
        });
        expect(sessionDAO.create(eq(session1))).andReturn(sessionId1);
        sessionDAO.update(eq(session1));
        expectLastCall().anyTimes();
        replay(sessionDAO);
        Session session = sm.start(null);
        Assert.assertNotNull(session);
        verify(sessionDAO);
        reset(sessionDAO);
        expect(sessionDAO.readSession(sessionId1)).andReturn(session1).anyTimes();
        sessionDAO.update(eq(session1));
        replay(sessionDAO);
        sm.setTimeout(new DefaultSessionKey(sessionId1), 1);
        verify(sessionDAO);
        reset(sessionDAO);
        sleep(20);
        expect(sessionDAO.readSession(sessionId1)).andReturn(session1);
        sessionDAO.update(eq(session1));// update's the stop timestamp

        sessionDAO.delete(session1);
        replay(sessionDAO);
        // Try to access the same session, but it should throw an UnknownSessionException due to timeout:
        try {
            sm.getTimeout(new DefaultSessionKey(sessionId1));
            Assert.fail((("Session with id [" + sessionId1) + "] should have expired due to timeout."));
        } catch (ExpiredSessionException expected) {
            // expected
        }
        verify(sessionDAO);// verify that the delete call was actually made on the DAO

    }

    /**
     * Tests a bug introduced by SHIRO-443, where a custom sessionValidationScheduler would not be started.
     */
    @Test
    public void testEnablingOfCustomSessionValidationScheduler() {
        // using the default impl of sessionValidationScheduler, as the but effects any scheduler we set directly via
        // sessionManager.setSessionValidationScheduler(), commonly used in INI configuration.
        ExecutorServiceSessionValidationScheduler sessionValidationScheduler = new ExecutorServiceSessionValidationScheduler();
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionValidationScheduler(sessionValidationScheduler);
        // starting a session will trigger the starting of the validator
        try {
            Session session = sessionManager.start(null);
            // now sessionValidationScheduler should be enabled
            Assert.assertTrue("sessionValidationScheduler was not enabled", sessionValidationScheduler.isEnabled());
        } finally {
            // cleanup after test
            sessionManager.destroy();
        }
    }

    private static class SessionTimeoutMatcher implements IArgumentMatcher {
        private final long timeout;

        public SessionTimeoutMatcher(long timeout) {
            this.timeout = timeout;
        }

        public void appendTo(StringBuffer buffer) {
            buffer.append("eqSession(timeout=").append(this.timeout).append(")");
        }

        public boolean matches(Object o) {
            return (o instanceof Session) && ((getTimeout()) == (this.timeout));
        }
    }
}

