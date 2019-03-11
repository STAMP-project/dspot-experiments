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
import java.lang.reflect.Type;
import java.util.List;
import javax.websocket.DeploymentException;
import org.eclipse.jetty.websocket.jsr356.handlers.ByteArrayPartialHandler;
import org.eclipse.jetty.websocket.jsr356.handlers.StringPartialHandler;
import org.eclipse.jetty.websocket.jsr356.metadata.DecoderMetadata;
import org.eclipse.jetty.websocket.jsr356.metadata.DecoderMetadataSet;
import org.eclipse.jetty.websocket.jsr356.metadata.MessageHandlerMetadata;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class MessageHandlerFactoryTest {
    private MessageHandlerFactory factory;

    private DecoderMetadataSet metadatas;

    private DecoderFactory decoders;

    @Test
    public void testByteArrayPartial() throws DeploymentException {
        List<MessageHandlerMetadata> metadatas = factory.getMetadata(ByteArrayPartialHandler.class);
        MatcherAssert.assertThat("Metadata.list.size", metadatas.size(), Matchers.is(1));
        MessageHandlerMetadata handlerMetadata = metadatas.get(0);
        DecoderMetadata decoderMetadata = decoders.getMetadataFor(handlerMetadata.getMessageClass());
        MatcherAssert.assertThat("Message Type", decoderMetadata.getMessageType(), Matchers.is(BINARY));
        MatcherAssert.assertThat("Message Class", handlerMetadata.getMessageClass(), Matchers.is(((Type) (byte[].class))));
    }

    @Test
    public void testStringPartial() throws DeploymentException {
        List<MessageHandlerMetadata> metadatas = factory.getMetadata(StringPartialHandler.class);
        MatcherAssert.assertThat("Metadata.list.size", metadatas.size(), Matchers.is(1));
        MessageHandlerMetadata handlerMetadata = metadatas.get(0);
        DecoderMetadata decoderMetadata = decoders.getMetadataFor(handlerMetadata.getMessageClass());
        MatcherAssert.assertThat("Message Type", decoderMetadata.getMessageType(), Matchers.is(TEXT));
        MatcherAssert.assertThat("Message Class", handlerMetadata.getMessageClass(), Matchers.is(((Type) (String.class))));
    }
}

