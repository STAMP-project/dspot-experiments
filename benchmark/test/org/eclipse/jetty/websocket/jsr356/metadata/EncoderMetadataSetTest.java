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
package org.eclipse.jetty.websocket.jsr356.metadata;


import MessageType.BINARY;
import MessageType.TEXT;
import java.util.List;
import javax.websocket.Encoder;
import org.eclipse.jetty.websocket.jsr356.encoders.BadDualEncoder;
import org.eclipse.jetty.websocket.jsr356.encoders.DateEncoder;
import org.eclipse.jetty.websocket.jsr356.encoders.IntegerEncoder;
import org.eclipse.jetty.websocket.jsr356.encoders.TimeEncoder;
import org.eclipse.jetty.websocket.jsr356.encoders.ValidDualEncoder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class EncoderMetadataSetTest {
    @Test
    public void testAddBadDualEncoders() {
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> {
            EncoderMetadataSet coders = new EncoderMetadataSet();
            // has duplicated support for the same target Type
            coders.add(BadDualEncoder.class);
            // Should have thrown IllegalStateException for attempting to register Encoders with duplicate implementation
        });
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Duplicate"));
    }

    @Test
    public void testAddDuplicate() {
        EncoderMetadataSet coders = new EncoderMetadataSet();
        // Add DateEncoder (decodes java.util.Date)
        coders.add(DateEncoder.class);
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> {
            // Add TimeEncoder (which also wants to decode java.util.Date)
            coders.add(TimeEncoder.class);
            // Should have thrown IllegalStateException for attempting to register Encoders with duplicate implementation
        });
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Duplicate"));
    }

    @Test
    public void testAddGetCoder() {
        EncoderMetadataSet coders = new EncoderMetadataSet();
        coders.add(IntegerEncoder.class);
        Class<? extends Encoder> actualClazz = coders.getCoder(Integer.class);
        Assertions.assertEquals(IntegerEncoder.class, actualClazz, "Coder Class");
    }

    @Test
    public void testAddGetMetadataByImpl() {
        EncoderMetadataSet coders = new EncoderMetadataSet();
        coders.add(IntegerEncoder.class);
        List<EncoderMetadata> metadatas = coders.getMetadataByImplementation(IntegerEncoder.class);
        MatcherAssert.assertThat("Metadatas (by impl) count", metadatas.size(), Matchers.is(1));
        EncoderMetadata metadata = metadatas.get(0);
        assertMetadata(metadata, Integer.class, IntegerEncoder.class, TEXT);
    }

    @Test
    public void testAddGetMetadataByType() {
        EncoderMetadataSet coders = new EncoderMetadataSet();
        coders.add(IntegerEncoder.class);
        EncoderMetadata metadata = coders.getMetadataByType(Integer.class);
        assertMetadata(metadata, Integer.class, IntegerEncoder.class, TEXT);
    }

    @Test
    public void testAddValidDualEncoders() {
        EncoderMetadataSet coders = new EncoderMetadataSet();
        coders.add(ValidDualEncoder.class);
        List<Class<? extends Encoder>> EncodersList = coders.getList();
        MatcherAssert.assertThat("Encoder List", EncodersList, Matchers.notNullValue());
        MatcherAssert.assertThat("Encoder List count", EncodersList.size(), Matchers.is(2));
        EncoderMetadata metadata;
        metadata = coders.getMetadataByType(Integer.class);
        assertMetadata(metadata, Integer.class, ValidDualEncoder.class, TEXT);
        metadata = coders.getMetadataByType(Long.class);
        assertMetadata(metadata, Long.class, ValidDualEncoder.class, BINARY);
    }
}

