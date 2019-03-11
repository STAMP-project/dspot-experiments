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
package org.eclipse.jetty.websocket.jsr356;


import java.util.List;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


public class DelayedStartClientTest {
    WebSocketContainer container;

    @Test
    public void testNoExtraHttpClientThreads() {
        container = ContainerProvider.getWebSocketContainer();
        MatcherAssert.assertThat("Container", container, CoreMatchers.notNullValue());
        List<String> threadNames = DelayedStartClientTest.getThreadNames(((ContainerLifeCycle) (container)));
        MatcherAssert.assertThat("Threads", threadNames, CoreMatchers.not(CoreMatchers.hasItem(CoreMatchers.containsString("WebSocketContainer@"))));
        MatcherAssert.assertThat("Threads", threadNames, CoreMatchers.not(CoreMatchers.hasItem(CoreMatchers.containsString("HttpClient@"))));
    }
}

