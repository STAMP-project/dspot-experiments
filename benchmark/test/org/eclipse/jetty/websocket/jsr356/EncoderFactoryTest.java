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
import org.eclipse.jetty.websocket.jsr356.encoders.IntegerEncoder;
import org.eclipse.jetty.websocket.jsr356.encoders.LongEncoder;
import org.eclipse.jetty.websocket.jsr356.metadata.EncoderMetadataSet;
import org.eclipse.jetty.websocket.jsr356.samples.Fruit;
import org.eclipse.jetty.websocket.jsr356.samples.FruitBinaryEncoder;
import org.eclipse.jetty.websocket.jsr356.samples.FruitTextEncoder;
import org.junit.jupiter.api.Test;


/**
 * Tests against the Encoders class
 */
public class EncoderFactoryTest {
    private EncoderMetadataSet metadatas;

    private EncoderFactory factory;

    @Test
    public void testGetMetadataForFruitBinary() {
        metadatas.add(FruitBinaryEncoder.class);
        assertMetadataFor(Fruit.class, FruitBinaryEncoder.class, BINARY);
    }

    @Test
    public void testGetMetadataForFruitText() {
        metadatas.add(FruitTextEncoder.class);
        assertMetadataFor(Fruit.class, FruitTextEncoder.class, TEXT);
    }

    @Test
    public void testGetMetadataForInteger() {
        assertMetadataFor(Integer.TYPE, IntegerEncoder.class, TEXT);
    }

    @Test
    public void testGetMetadataForLong() {
        assertMetadataFor(Long.TYPE, LongEncoder.class, TEXT);
    }
}

