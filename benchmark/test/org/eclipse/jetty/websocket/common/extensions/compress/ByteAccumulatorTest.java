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
package org.eclipse.jetty.websocket.common.extensions.compress;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.websocket.api.MessageTooLargeException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ByteAccumulatorTest {
    @Test
    public void testCopyNormal() {
        ByteAccumulator accumulator = new ByteAccumulator(10000);
        byte[] hello = "Hello".getBytes(StandardCharsets.UTF_8);
        byte[] space = " ".getBytes(StandardCharsets.UTF_8);
        byte[] world = "World".getBytes(StandardCharsets.UTF_8);
        accumulator.copyChunk(hello, 0, hello.length);
        accumulator.copyChunk(space, 0, space.length);
        accumulator.copyChunk(world, 0, world.length);
        MatcherAssert.assertThat("Length", accumulator.getLength(), CoreMatchers.is((((hello.length) + (space.length)) + (world.length))));
        ByteBuffer out = ByteBuffer.allocate(200);
        accumulator.transferTo(out);
        String result = BufferUtil.toUTF8String(out);
        MatcherAssert.assertThat("ByteBuffer to UTF8", result, CoreMatchers.is("Hello World"));
    }

    @Test
    public void testTransferTo_NotEnoughSpace() {
        ByteAccumulator accumulator = new ByteAccumulator(10000);
        byte[] hello = "Hello".getBytes(StandardCharsets.UTF_8);
        byte[] space = " ".getBytes(StandardCharsets.UTF_8);
        byte[] world = "World".getBytes(StandardCharsets.UTF_8);
        accumulator.copyChunk(hello, 0, hello.length);
        accumulator.copyChunk(space, 0, space.length);
        accumulator.copyChunk(world, 0, world.length);
        int length = ((hello.length) + (space.length)) + (world.length);
        MatcherAssert.assertThat("Length", accumulator.getLength(), CoreMatchers.is(length));
        ByteBuffer out = ByteBuffer.allocate((length - 2));// intentionally too small ByteBuffer

        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> accumulator.transferTo(out));
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Not enough space in ByteBuffer"));
    }

    @Test
    public void testCopyChunk_NotEnoughSpace() {
        byte[] hello = "Hello".getBytes(StandardCharsets.UTF_8);
        byte[] space = " ".getBytes(StandardCharsets.UTF_8);
        byte[] world = "World".getBytes(StandardCharsets.UTF_8);
        int length = ((hello.length) + (space.length)) + (world.length);
        ByteAccumulator accumulator = new ByteAccumulator((length - 2));// intentionally too small of a max

        accumulator.copyChunk(hello, 0, hello.length);
        accumulator.copyChunk(space, 0, space.length);
        MessageTooLargeException e = Assertions.assertThrows(MessageTooLargeException.class, () -> accumulator.copyChunk(world, 0, world.length));
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("too large for configured max"));
    }
}

