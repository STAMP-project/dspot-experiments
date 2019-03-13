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
package org.eclipse.jetty.websocket.server;


import StdErrLog.LEVEL_DEBUG;
import java.util.concurrent.Executor;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


public class WebSocketServerFactoryTest {
    @Test
    public void testInit() {
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        Executor executor = new QueuedThreadPool();
        ByteBufferPool bufferPool = new MappedByteBufferPool();
        int wsFactoryLevel = setLogLevel(WebSocketServerFactory.class, LEVEL_DEBUG);
        int abstractLifecycleLevel = setLogLevel(AbstractLifeCycle.class, LEVEL_DEBUG);
        int containerLifecycleLevel = setLogLevel(ContainerLifeCycle.class, LEVEL_DEBUG);
        try {
            WebSocketServerFactory wsFactory = new WebSocketServerFactory(policy, executor, bufferPool);
            // The above init caused NPE due to bad constructor initialization order with debug active
            MatcherAssert.assertThat("wsFactory.toString()", wsFactory.toString(), CoreMatchers.notNullValue());
        } finally {
            setLogLevel(WebSocketServerFactory.class, wsFactoryLevel);
            setLogLevel(AbstractLifeCycle.class, abstractLifecycleLevel);
            setLogLevel(ContainerLifeCycle.class, containerLifecycleLevel);
        }
    }
}

