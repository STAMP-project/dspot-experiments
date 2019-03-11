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
import java.util.Date;
import org.eclipse.jetty.websocket.jsr356.decoders.ByteArrayDecoder;
import org.eclipse.jetty.websocket.jsr356.decoders.ByteBufferDecoder;
import org.eclipse.jetty.websocket.jsr356.decoders.DateDecoder;
import org.eclipse.jetty.websocket.jsr356.decoders.IntegerDecoder;
import org.eclipse.jetty.websocket.jsr356.decoders.LongDecoder;
import org.eclipse.jetty.websocket.jsr356.decoders.StringDecoder;
import org.eclipse.jetty.websocket.jsr356.metadata.DecoderMetadataSet;
import org.eclipse.jetty.websocket.jsr356.samples.Fruit;
import org.eclipse.jetty.websocket.jsr356.samples.FruitDecoder;
import org.junit.jupiter.api.Test;


public class DecoderFactoryTest {
    private DecoderMetadataSet metadatas;

    private DecoderFactory factory;

    @Test
    public void testGetMetadataForByteArray() {
        assertMetadataFor(byte[].class, ByteArrayDecoder.class, BINARY);
    }

    @Test
    public void testGetMetadataForByteBuffer() {
        assertMetadataFor(ByteBuffer.class, ByteBufferDecoder.class, BINARY);
    }

    @Test
    public void testGetMetadataForDate() {
        metadatas.add(DateDecoder.class);
        assertMetadataFor(Date.class, DateDecoder.class, TEXT);
    }

    @Test
    public void testGetMetadataForFruit() {
        metadatas.add(FruitDecoder.class);
        assertMetadataFor(Fruit.class, FruitDecoder.class, TEXT);
    }

    @Test
    public void testGetMetadataForInteger() {
        assertMetadataFor(Integer.TYPE, IntegerDecoder.class, TEXT);
    }

    @Test
    public void testGetMetadataForLong() {
        assertMetadataFor(Long.TYPE, LongDecoder.class, TEXT);
    }

    @Test
    public void testGetStringDecoder() {
        assertMetadataFor(String.class, StringDecoder.class, TEXT);
    }
}

