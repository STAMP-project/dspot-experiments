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
package org.eclipse.jetty.cdi.websocket.wsscope;


import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.cdi.core.ScopedInstance;
import org.eclipse.jetty.cdi.websocket.WebSocketScopeContext;
import org.eclipse.jetty.websocket.api.Session;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;


public class WebSocketScopeSessionTest {
    private static Weld weld;

    private static WeldContainer container;

    @Test
    public void testSessionActivation() throws Exception {
        ScopedInstance<WebSocketScopeContext> wsScopeBean = WebSocketScopeSessionTest.newInstance(WebSocketScopeContext.class);
        WebSocketScopeContext wsScope = wsScopeBean.instance;
        wsScope.create();
        try {
            // Scope 1
            wsScope.begin();
            BogusSession sess = new BogusSession("1");
            wsScope.setSession(sess);
            ScopedInstance<BogusSocket> sock1Bean = WebSocketScopeSessionTest.newInstance(BogusSocket.class);
            BogusSocket sock1 = sock1Bean.instance;
            MatcherAssert.assertThat("Socket 1 Session", sock1.getSession().toString(), Matchers.is(sess.toString()));
            sock1Bean.destroy();
        } finally {
            wsScope.end();
        }
        wsScope.destroy();
        wsScopeBean.destroy();
    }

    @Test
    public void testMultiSession_Sequential() throws Exception {
        ScopedInstance<WebSocketScopeContext> wsScope1Bean = WebSocketScopeSessionTest.newInstance(WebSocketScopeContext.class);
        WebSocketScopeContext wsScope1 = wsScope1Bean.instance;
        ScopedInstance<WebSocketScopeContext> wsScope2Bean = WebSocketScopeSessionTest.newInstance(WebSocketScopeContext.class);
        WebSocketScopeContext wsScope2 = wsScope2Bean.instance;
        wsScope1.create();
        try {
            // Scope 1
            wsScope1.begin();
            BogusSession sess = new BogusSession("1");
            wsScope1.setSession(sess);
            ScopedInstance<BogusSocket> sock1Bean = WebSocketScopeSessionTest.newInstance(BogusSocket.class);
            BogusSocket sock1 = sock1Bean.instance;
            MatcherAssert.assertThat("Socket 1 Session", sock1.getSession(), Matchers.sameInstance(((Session) (sess))));
            sock1Bean.destroy();
        } finally {
            wsScope1.end();
        }
        wsScope1.destroy();
        wsScope1Bean.destroy();
        wsScope2.create();
        try {
            // Scope 2
            wsScope2.begin();
            BogusSession sess = new BogusSession("2");
            wsScope2.setSession(sess);
            ScopedInstance<BogusSocket> sock2Bean = WebSocketScopeSessionTest.newInstance(BogusSocket.class);
            BogusSocket sock2 = sock2Bean.instance;
            MatcherAssert.assertThat("Socket 2 Session", sock2.getSession(), Matchers.sameInstance(((Session) (sess))));
            sock2Bean.destroy();
        } finally {
            wsScope2.end();
        }
        wsScope2.destroy();
        wsScope2Bean.destroy();
    }

    @Test
    public void testMultiSession_Overlapping() throws Exception {
        final CountDownLatch midLatch = new CountDownLatch(2);
        final CountDownLatch end1Latch = new CountDownLatch(1);
        Callable<Session> call1 = new Callable<Session>() {
            @Override
            public Session call() throws Exception {
                Session ret = null;
                ScopedInstance<WebSocketScopeContext> wsScope1Bean = WebSocketScopeSessionTest.newInstance(WebSocketScopeContext.class);
                WebSocketScopeContext wsScope1 = wsScope1Bean.instance;
                wsScope1.create();
                try {
                    // Scope 1
                    wsScope1.begin();
                    BogusSession sess = new BogusSession("1");
                    wsScope1.setSession(sess);
                    midLatch.countDown();
                    midLatch.await(1, TimeUnit.SECONDS);
                    ScopedInstance<BogusSocket> sock1Bean = WebSocketScopeSessionTest.newInstance(BogusSocket.class);
                    BogusSocket sock1 = sock1Bean.instance;
                    MatcherAssert.assertThat("Socket 1 Session", sock1.getSession(), Matchers.sameInstance(((Session) (sess))));
                    ret = sock1.getSession();
                    sock1Bean.destroy();
                } finally {
                    wsScope1.end();
                }
                wsScope1.destroy();
                wsScope1Bean.destroy();
                end1Latch.countDown();
                return ret;
            }
        };
        final CountDownLatch end2Latch = new CountDownLatch(1);
        Callable<Session> call2 = new Callable<Session>() {
            @Override
            public Session call() throws Exception {
                Session ret = null;
                ScopedInstance<WebSocketScopeContext> wsScope2Bean = WebSocketScopeSessionTest.newInstance(WebSocketScopeContext.class);
                WebSocketScopeContext wsScope2 = wsScope2Bean.instance;
                wsScope2.create();
                try {
                    // Scope 2
                    wsScope2.begin();
                    BogusSession sess = new BogusSession("2");
                    wsScope2.setSession(sess);
                    ScopedInstance<BogusSocket> sock2Bean = WebSocketScopeSessionTest.newInstance(BogusSocket.class);
                    midLatch.countDown();
                    midLatch.await(1, TimeUnit.SECONDS);
                    BogusSocket sock2 = sock2Bean.instance;
                    ret = sock2.getSession();
                    MatcherAssert.assertThat("Socket 2 Session", sock2.getSession(), Matchers.sameInstance(((Session) (sess))));
                    sock2Bean.destroy();
                } finally {
                    wsScope2.end();
                }
                wsScope2.destroy();
                wsScope2Bean.destroy();
                end2Latch.countDown();
                return ret;
            }
        };
        ExecutorService svc = Executors.newFixedThreadPool(4);
        Future<Session> fut1 = svc.submit(call1);
        Future<Session> fut2 = svc.submit(call2);
        Session sess1 = fut1.get(1, TimeUnit.SECONDS);
        Session sess2 = fut2.get(1, TimeUnit.SECONDS);
        MatcherAssert.assertThat("Sessions are different", sess1, Matchers.not(Matchers.sameInstance(sess2)));
    }
}

