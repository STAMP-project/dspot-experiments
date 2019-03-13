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
package org.eclipse.jetty.server.handler;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class HandlerTest {
    @Test
    public void testWrapperSetServer() {
        Server s = new Server();
        HandlerWrapper a = new HandlerWrapper();
        HandlerWrapper b = new HandlerWrapper();
        HandlerWrapper c = new HandlerWrapper();
        a.setHandler(b);
        b.setHandler(c);
        a.setServer(s);
        MatcherAssert.assertThat(b.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(c.getServer(), Matchers.equalTo(s));
    }

    @Test
    public void testWrapperServerSet() {
        Server s = new Server();
        HandlerWrapper a = new HandlerWrapper();
        HandlerWrapper b = new HandlerWrapper();
        HandlerWrapper c = new HandlerWrapper();
        a.setServer(s);
        b.setHandler(c);
        a.setHandler(b);
        MatcherAssert.assertThat(b.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(c.getServer(), Matchers.equalTo(s));
    }

    @Test
    public void testWrapperThisLoop() {
        HandlerWrapper a = new HandlerWrapper();
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> a.setHandler(a));
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("loop"));
    }

    @Test
    public void testWrapperSimpleLoop() {
        HandlerWrapper a = new HandlerWrapper();
        HandlerWrapper b = new HandlerWrapper();
        a.setHandler(b);
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> b.setHandler(a));
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("loop"));
    }

    @Test
    public void testWrapperDeepLoop() {
        HandlerWrapper a = new HandlerWrapper();
        HandlerWrapper b = new HandlerWrapper();
        HandlerWrapper c = new HandlerWrapper();
        a.setHandler(b);
        b.setHandler(c);
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> c.setHandler(a));
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("loop"));
    }

    @Test
    public void testWrapperChainLoop() {
        HandlerWrapper a = new HandlerWrapper();
        HandlerWrapper b = new HandlerWrapper();
        HandlerWrapper c = new HandlerWrapper();
        a.setHandler(b);
        c.setHandler(a);
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> b.setHandler(c));
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("loop"));
    }

    @Test
    public void testCollectionSetServer() {
        Server s = new Server();
        HandlerCollection a = new HandlerCollection();
        HandlerCollection b = new HandlerCollection();
        HandlerCollection b1 = new HandlerCollection();
        HandlerCollection b2 = new HandlerCollection();
        HandlerCollection c = new HandlerCollection();
        HandlerCollection c1 = new HandlerCollection();
        HandlerCollection c2 = new HandlerCollection();
        a.addHandler(b);
        a.addHandler(c);
        b.setHandlers(new Handler[]{ b1, b2 });
        c.setHandlers(new Handler[]{ c1, c2 });
        a.setServer(s);
        MatcherAssert.assertThat(b.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(c.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(b1.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(b2.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(c1.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(c2.getServer(), Matchers.equalTo(s));
    }

    @Test
    public void testCollectionServerSet() {
        Server s = new Server();
        HandlerCollection a = new HandlerCollection();
        HandlerCollection b = new HandlerCollection();
        HandlerCollection b1 = new HandlerCollection();
        HandlerCollection b2 = new HandlerCollection();
        HandlerCollection c = new HandlerCollection();
        HandlerCollection c1 = new HandlerCollection();
        HandlerCollection c2 = new HandlerCollection();
        a.setServer(s);
        a.addHandler(b);
        a.addHandler(c);
        b.setHandlers(new Handler[]{ b1, b2 });
        c.setHandlers(new Handler[]{ c1, c2 });
        MatcherAssert.assertThat(b.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(c.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(b1.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(b2.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(c1.getServer(), Matchers.equalTo(s));
        MatcherAssert.assertThat(c2.getServer(), Matchers.equalTo(s));
    }

    @Test
    public void testCollectionThisLoop() {
        HandlerCollection a = new HandlerCollection();
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> a.addHandler(a));
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("loop"));
    }

    @Test
    public void testCollectionDeepLoop() {
        HandlerCollection a = new HandlerCollection();
        HandlerCollection b = new HandlerCollection();
        HandlerCollection b1 = new HandlerCollection();
        HandlerCollection b2 = new HandlerCollection();
        HandlerCollection c = new HandlerCollection();
        HandlerCollection c1 = new HandlerCollection();
        HandlerCollection c2 = new HandlerCollection();
        a.addHandler(b);
        a.addHandler(c);
        b.setHandlers(new Handler[]{ b1, b2 });
        c.setHandlers(new Handler[]{ c1, c2 });
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> b2.addHandler(a));
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("loop"));
    }

    @Test
    public void testCollectionChainLoop() {
        HandlerCollection a = new HandlerCollection();
        HandlerCollection b = new HandlerCollection();
        HandlerCollection b1 = new HandlerCollection();
        HandlerCollection b2 = new HandlerCollection();
        HandlerCollection c = new HandlerCollection();
        HandlerCollection c1 = new HandlerCollection();
        HandlerCollection c2 = new HandlerCollection();
        a.addHandler(c);
        b.setHandlers(new Handler[]{ b1, b2 });
        c.setHandlers(new Handler[]{ c1, c2 });
        b2.addHandler(a);
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> a.addHandler(b));
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("loop"));
    }

    @Test
    public void testInsertWrapperTail() {
        HandlerWrapper a = new HandlerWrapper();
        HandlerWrapper b = new HandlerWrapper();
        a.insertHandler(b);
        MatcherAssert.assertThat(a.getHandler(), Matchers.equalTo(b));
        MatcherAssert.assertThat(b.getHandler(), Matchers.nullValue());
    }

    @Test
    public void testInsertWrapper() {
        HandlerWrapper a = new HandlerWrapper();
        HandlerWrapper b = new HandlerWrapper();
        HandlerWrapper c = new HandlerWrapper();
        a.insertHandler(c);
        a.insertHandler(b);
        MatcherAssert.assertThat(a.getHandler(), Matchers.equalTo(b));
        MatcherAssert.assertThat(b.getHandler(), Matchers.equalTo(c));
        MatcherAssert.assertThat(c.getHandler(), Matchers.nullValue());
    }

    @Test
    public void testInsertWrapperChain() {
        HandlerWrapper a = new HandlerWrapper();
        HandlerWrapper b = new HandlerWrapper();
        HandlerWrapper c = new HandlerWrapper();
        HandlerWrapper d = new HandlerWrapper();
        a.insertHandler(d);
        b.insertHandler(c);
        a.insertHandler(b);
        MatcherAssert.assertThat(a.getHandler(), Matchers.equalTo(b));
        MatcherAssert.assertThat(b.getHandler(), Matchers.equalTo(c));
        MatcherAssert.assertThat(c.getHandler(), Matchers.equalTo(d));
        MatcherAssert.assertThat(d.getHandler(), Matchers.nullValue());
    }

    @Test
    public void testInsertWrapperBadChain() {
        HandlerWrapper a = new HandlerWrapper();
        HandlerWrapper b = new HandlerWrapper();
        HandlerWrapper c = new HandlerWrapper();
        HandlerWrapper d = new HandlerWrapper();
        a.insertHandler(d);
        b.insertHandler(c);
        c.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            }
        });
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> a.insertHandler(b));
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("bad tail"));
    }
}

