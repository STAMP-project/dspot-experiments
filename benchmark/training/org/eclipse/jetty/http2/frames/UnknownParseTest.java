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
package org.eclipse.jetty.http2.frames;


import ErrorCode.FRAME_SIZE_ERROR.code;
import Frame.DEFAULT_MAX_LENGTH;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.eclipse.jetty.http2.parser.Parser;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class UnknownParseTest {
    private final ByteBufferPool byteBufferPool = new MappedByteBufferPool();

    @Test
    public void testParse() {
        testParse(Function.identity());
    }

    @Test
    public void testParseOneByteAtATime() {
        testParse(( buffer) -> ByteBuffer.wrap(new byte[]{ buffer.get() }));
    }

    @Test
    public void testInvalidFrameSize() {
        AtomicInteger failure = new AtomicInteger();
        Parser parser = new Parser(byteBufferPool, new Parser.Listener.Adapter(), 4096, 8192);
        parser.init(( listener) -> new Parser.Listener.Wrapper(listener) {
            @Override
            public void onConnectionFailure(int error, String reason) {
                failure.set(error);
            }
        });
        parser.setMaxFrameLength(DEFAULT_MAX_LENGTH);
        // 0x4001 == 16385 which is > Frame.DEFAULT_MAX_LENGTH.
        byte[] bytes = new byte[]{ 0, 64, 1, 64, 0, 0, 0, 0, 0 };
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        while (buffer.hasRemaining())
            parser.parse(buffer);

        Assertions.assertEquals(code, failure.get());
    }
}

