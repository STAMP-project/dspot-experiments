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
package org.eclipse.jetty.websocket.jsr356.server;


import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.events.EventDriver;
import org.eclipse.jetty.websocket.common.frames.ContinuationFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.jsr356.server.samples.partial.PartialTrackingSocket;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class OnPartialTest {
    @Test
    public void testOnTextPartial() throws Throwable {
        List<WebSocketFrame> frames = new ArrayList<>();
        frames.add(new TextFrame().setPayload("Saved").setFin(false));
        frames.add(new ContinuationFrame().setPayload(" by ").setFin(false));
        frames.add(new ContinuationFrame().setPayload("zero").setFin(true));
        PartialTrackingSocket socket = new PartialTrackingSocket();
        EventDriver driver = toEventDriver(socket);
        driver.onConnect();
        for (WebSocketFrame frame : frames) {
            driver.incomingFrame(frame);
        }
        MatcherAssert.assertThat("Captured Event Queue size", socket.eventQueue.size(), Matchers.is(3));
        MatcherAssert.assertThat("Event[0]", socket.eventQueue.poll(), Matchers.is("onPartial(\"Saved\",false)"));
        MatcherAssert.assertThat("Event[1]", socket.eventQueue.poll(), Matchers.is("onPartial(\" by \",false)"));
        MatcherAssert.assertThat("Event[2]", socket.eventQueue.poll(), Matchers.is("onPartial(\"zero\",true)"));
    }
}

