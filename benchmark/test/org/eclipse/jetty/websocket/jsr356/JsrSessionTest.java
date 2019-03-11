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


import MessageType.BINARY;
import MessageType.TEXT;
import java.nio.ByteBuffer;
import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler;
import org.eclipse.jetty.websocket.jsr356.handlers.ByteArrayWholeHandler;
import org.eclipse.jetty.websocket.jsr356.handlers.ByteBufferPartialHandler;
import org.eclipse.jetty.websocket.jsr356.handlers.LongMessageHandler;
import org.eclipse.jetty.websocket.jsr356.handlers.StringWholeHandler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JsrSessionTest {
    private ClientContainer container;

    private JsrSession session;

    @Test
    public void testMessageHandlerBinary() throws DeploymentException {
        session.addMessageHandler(new ByteBufferPartialHandler());
        MessageHandlerWrapper wrapper = session.getMessageHandlerWrapper(BINARY);
        MatcherAssert.assertThat("Binary Handler", wrapper.getHandler(), Matchers.instanceOf(ByteBufferPartialHandler.class));
        Assertions.assertEquals(wrapper.getMetadata().getMessageClass(), ByteBuffer.class, "Message Class");
    }

    @Test
    public void testMessageHandlerBoth() throws DeploymentException {
        session.addMessageHandler(new StringWholeHandler());
        session.addMessageHandler(new ByteArrayWholeHandler());
        MessageHandlerWrapper wrapper = session.getMessageHandlerWrapper(TEXT);
        MatcherAssert.assertThat("Text Handler", wrapper.getHandler(), Matchers.instanceOf(StringWholeHandler.class));
        Assertions.assertEquals(wrapper.getMetadata().getMessageClass(), String.class, "Message Class");
        wrapper = session.getMessageHandlerWrapper(BINARY);
        MatcherAssert.assertThat("Binary Handler", wrapper.getHandler(), Matchers.instanceOf(ByteArrayWholeHandler.class));
        Assertions.assertEquals(wrapper.getMetadata().getMessageClass(), byte[].class, "Message Class");
    }

    @Test
    public void testMessageHandlerReplaceTextHandler() throws DeploymentException {
        MessageHandler oldText = new StringWholeHandler();
        session.addMessageHandler(oldText);// add a TEXT handler

        session.addMessageHandler(new ByteArrayWholeHandler());// add BINARY handler

        session.removeMessageHandler(oldText);// remove original TEXT handler

        session.addMessageHandler(new LongMessageHandler());// add new TEXT handler

        MessageHandlerWrapper wrapper = session.getMessageHandlerWrapper(BINARY);
        MatcherAssert.assertThat("Binary Handler", wrapper.getHandler(), Matchers.instanceOf(ByteArrayWholeHandler.class));
        Assertions.assertEquals(wrapper.getMetadata().getMessageClass(), byte[].class, "Message Class");
        wrapper = session.getMessageHandlerWrapper(TEXT);
        MatcherAssert.assertThat("Text Handler", wrapper.getHandler(), Matchers.instanceOf(LongMessageHandler.class));
        Assertions.assertEquals(wrapper.getMetadata().getMessageClass(), Long.class, "Message Class");
    }

    @Test
    public void testMessageHandlerText() throws DeploymentException {
        session.addMessageHandler(new StringWholeHandler());
        MessageHandlerWrapper wrapper = session.getMessageHandlerWrapper(TEXT);
        MatcherAssert.assertThat("Text Handler", wrapper.getHandler(), Matchers.instanceOf(StringWholeHandler.class));
        Assertions.assertEquals(wrapper.getMetadata().getMessageClass(), String.class, "Message Class");
    }
}

