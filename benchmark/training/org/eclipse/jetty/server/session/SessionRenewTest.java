/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.server.session;


import SessionCache.NEVER_EVICT;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * SessionRenewTest
 *
 * Test that changes the session id during a request.
 */
public class SessionRenewTest {
    protected TestServer _server;

    /**
     * Tests renewing a session id when sessions are not being cached.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSessionRenewalNullCache() throws Exception {
        SessionCacheFactory cacheFactory = new NullSessionCacheFactory();
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        // make the server with a NullSessionCache
        _server = new TestServer(0, (-1), (-1), cacheFactory, storeFactory);
        doTest(new SessionRenewTest.RenewalVerifier());
    }

    /**
     * Test renewing session id when sessions are cached
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSessionRenewalDefaultCache() throws Exception {
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        _server = new TestServer(0, (-1), (-1), cacheFactory, storeFactory);
        doTest(new SessionRenewTest.RenewalVerifier() {
            @Override
            public void verify(WebAppContext context, String oldSessionId, String newSessionId) throws Exception {
                // verify the contents of the cache changed
                Assertions.assertTrue(context.getSessionHandler().getSessionCache().contains(newSessionId));
                Assertions.assertFalse(context.getSessionHandler().getSessionCache().contains(oldSessionId));
                super.verify(context, oldSessionId, newSessionId);
            }
        });
    }

    /**
     * RenewalVerifier
     */
    public class RenewalVerifier {
        public void verify(WebAppContext context, String oldSessionId, String newSessionId) throws Exception {
            // verify that the session id changed in the session store
            TestSessionDataStore store = ((TestSessionDataStore) (context.getSessionHandler().getSessionCache().getSessionDataStore()));
            Assertions.assertTrue(store.exists(newSessionId));
            Assertions.assertFalse(store.exists(oldSessionId));
        }
    }

    public static class TestHttpSessionIdListener implements HttpSessionIdListener {
        boolean called = false;

        @Override
        public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
            Assertions.assertNotNull(event.getSession());
            Assertions.assertNotSame(oldSessionId, event.getSession().getId());
            called = true;
        }

        public boolean isCalled() {
            return called;
        }
    }

    public static class TestServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String action = request.getParameter("action");
            if ("create".equals(action)) {
                HttpSession session = request.getSession(true);
                Assertions.assertTrue(session.isNew());
            } else
                if ("renew".equals(action)) {
                    HttpSession beforeSession = request.getSession(false);
                    Assertions.assertTrue((beforeSession != null));
                    String beforeSessionId = beforeSession.getId();
                    ((Session) (beforeSession)).renewId(request);
                    HttpSession afterSession = request.getSession(false);
                    Assertions.assertTrue((afterSession != null));
                    String afterSessionId = afterSession.getId();
                    Assertions.assertTrue((beforeSession == afterSession));// same object

                    Assertions.assertFalse(beforeSessionId.equals(afterSessionId));// different id

                    SessionHandler sessionManager = getSessionHandler();
                    DefaultSessionIdManager sessionIdManager = ((DefaultSessionIdManager) (sessionManager.getSessionIdManager()));
                    Assertions.assertTrue(sessionIdManager.isIdInUse(afterSessionId));// new session id should be in use

                    Assertions.assertFalse(sessionIdManager.isIdInUse(beforeSessionId));
                    HttpSession session = sessionManager.getSession(afterSessionId);
                    Assertions.assertNotNull(session);
                    session = sessionManager.getSession(beforeSessionId);
                    Assertions.assertNull(session);
                    if (isIdChanged())
                        ((org.eclipse.jetty.server.Response) (response)).replaceCookie(sessionManager.getSessionCookie(afterSession, request.getContextPath(), request.isSecure()));

                }

        }
    }
}

