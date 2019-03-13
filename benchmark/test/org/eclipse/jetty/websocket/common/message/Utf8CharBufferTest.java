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
package org.eclipse.jetty.websocket.common.message;


import java.nio.ByteBuffer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class Utf8CharBufferTest {
    @Test
    public void testAppendGetAppendGet() {
        ByteBuffer buf = ByteBuffer.allocate(128);
        Utf8CharBuffer utf = Utf8CharBuffer.wrap(buf);
        byte[] hellobytes = Utf8CharBufferTest.asUTF("Hello ");
        byte[] worldbytes = Utf8CharBufferTest.asUTF("World!");
        utf.append(hellobytes, 0, hellobytes.length);
        ByteBuffer hellobuf = utf.getByteBuffer();
        utf.append(worldbytes, 0, worldbytes.length);
        ByteBuffer worldbuf = utf.getByteBuffer();
        MatcherAssert.assertThat("Hello buffer", Utf8CharBufferTest.asString(hellobuf), Matchers.is("Hello "));
        MatcherAssert.assertThat("World buffer", Utf8CharBufferTest.asString(worldbuf), Matchers.is("Hello World!"));
    }

    @Test
    public void testAppendGetClearAppendGet() {
        int bufsize = 128;
        ByteBuffer buf = ByteBuffer.allocate(bufsize);
        Utf8CharBuffer utf = Utf8CharBuffer.wrap(buf);
        int expectedSize = bufsize / 2;
        MatcherAssert.assertThat("Remaining (initial)", utf.remaining(), Matchers.is(expectedSize));
        byte[] hellobytes = Utf8CharBufferTest.asUTF("Hello World");
        utf.append(hellobytes, 0, hellobytes.length);
        ByteBuffer hellobuf = utf.getByteBuffer();
        MatcherAssert.assertThat("Remaining (after append)", utf.remaining(), Matchers.is((expectedSize - (hellobytes.length))));
        MatcherAssert.assertThat("Hello buffer", Utf8CharBufferTest.asString(hellobuf), Matchers.is("Hello World"));
        utf.clear();
        MatcherAssert.assertThat("Remaining (after clear)", utf.remaining(), Matchers.is(expectedSize));
        byte[] whatnowbytes = Utf8CharBufferTest.asUTF("What Now?");
        utf.append(whatnowbytes, 0, whatnowbytes.length);
        ByteBuffer whatnowbuf = utf.getByteBuffer();
        MatcherAssert.assertThat("Remaining (after 2nd append)", utf.remaining(), Matchers.is((expectedSize - (whatnowbytes.length))));
        MatcherAssert.assertThat("What buffer", Utf8CharBufferTest.asString(whatnowbuf), Matchers.is("What Now?"));
    }

    @Test
    public void testAppendUnicodeGetBuffer() {
        ByteBuffer buf = ByteBuffer.allocate(64);
        Utf8CharBuffer utf = Utf8CharBuffer.wrap(buf);
        byte[] bb = Utf8CharBufferTest.asUTF("Hello A\u00ea\u00f1\u00fcC");
        utf.append(bb, 0, bb.length);
        ByteBuffer actual = utf.getByteBuffer();
        MatcherAssert.assertThat("Buffer length should be retained", actual.remaining(), Matchers.is(bb.length));
        MatcherAssert.assertThat("Message", Utf8CharBufferTest.asString(actual), Matchers.is("Hello A\u00ea\u00f1\u00fcC"));
    }

    @Test
    public void testSimpleGetBuffer() {
        int bufsize = 64;
        ByteBuffer buf = ByteBuffer.allocate(bufsize);
        Utf8CharBuffer utf = Utf8CharBuffer.wrap(buf);
        int expectedSize = bufsize / 2;
        MatcherAssert.assertThat("Remaining (initial)", utf.remaining(), Matchers.is(expectedSize));
        byte[] bb = Utf8CharBufferTest.asUTF("Hello World");
        utf.append(bb, 0, bb.length);
        expectedSize -= bb.length;
        MatcherAssert.assertThat("Remaining (after append)", utf.remaining(), Matchers.is(expectedSize));
        ByteBuffer actual = utf.getByteBuffer();
        MatcherAssert.assertThat("Buffer length", actual.remaining(), Matchers.is(bb.length));
        MatcherAssert.assertThat("Message", Utf8CharBufferTest.asString(actual), Matchers.is("Hello World"));
    }
}

