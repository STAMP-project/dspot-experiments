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


import SessionCache.EVICT_ON_INACTIVITY;
import SessionCache.EVICT_ON_SESSION_EXIT;
import SessionCache.NEVER_EVICT;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * ImmortalSessionTest
 */
public class ImmortalSessionTest {
    @Test
    public void testImmortalSessionNoEviction() throws Exception {
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new NullSessionDataStoreFactory();
        doTest(2, cacheFactory, storeFactory);
    }

    @Test
    public void testImmortalSessionEvictOnExit() throws Exception {
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(EVICT_ON_SESSION_EXIT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        doTest(2, cacheFactory, storeFactory);
    }

    @Test
    public void testImmortalSessionEvictOnIdle() throws Exception {
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(EVICT_ON_INACTIVITY);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        doTest(2, cacheFactory, storeFactory);
    }

    public static class TestServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String result = null;
            String action = request.getParameter("action");
            if ("set".equals(action)) {
                String value = request.getParameter("value");
                HttpSession session = request.getSession(true);
                session.setAttribute("value", value);
                result = value;
            } else
                if ("get".equals(action)) {
                    HttpSession session = request.getSession(false);
                    Assertions.assertNotNull(session);
                    if (session != null)
                        result = ((String) (session.getAttribute("value")));

                }

            PrintWriter writer = response.getWriter();
            writer.println(result);
            writer.flush();
        }
    }
}

