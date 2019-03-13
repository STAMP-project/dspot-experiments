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
package org.eclipse.jetty.http2.hpack;


import java.nio.ByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.TypeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class NBitIntegerTest {
    @Test
    public void testOctetsNeeded() {
        Assertions.assertEquals(0, NBitInteger.octectsNeeded(5, 10));
        Assertions.assertEquals(2, NBitInteger.octectsNeeded(5, 1337));
        Assertions.assertEquals(1, NBitInteger.octectsNeeded(8, 42));
        Assertions.assertEquals(3, NBitInteger.octectsNeeded(8, 1337));
        Assertions.assertEquals(0, NBitInteger.octectsNeeded(6, 62));
        Assertions.assertEquals(1, NBitInteger.octectsNeeded(6, 63));
        Assertions.assertEquals(1, NBitInteger.octectsNeeded(6, 64));
        Assertions.assertEquals(2, NBitInteger.octectsNeeded(6, ((63 + 0) + (128 * 1))));
        Assertions.assertEquals(3, NBitInteger.octectsNeeded(6, ((63 + 0) + (128 * 128))));
        Assertions.assertEquals(4, NBitInteger.octectsNeeded(6, ((63 + 0) + ((128 * 128) * 128))));
    }

    @Test
    public void testEncode() {
        testEncode(6, 0, "00");
        testEncode(6, 1, "01");
        testEncode(6, 62, "3e");
        testEncode(6, 63, "3f00");
        testEncode(6, (63 + 1), "3f01");
        testEncode(6, (63 + 126), "3f7e");
        testEncode(6, (63 + 127), "3f7f");
        testEncode(6, ((63 + 0) + (128 * 1)), "3f8001");
        testEncode(6, ((63 + 1) + (128 * 1)), "3f8101");
        testEncode(6, ((63 + 127) + (128 * 1)), "3fFf01");
        testEncode(6, ((63 + 0) + (128 * 2)), "3f8002");
        testEncode(6, ((63 + 1) + (128 * 2)), "3f8102");
        testEncode(6, ((63 + 127) + (128 * 127)), "3fFf7f");
        testEncode(6, ((63 + 0) + (128 * 128)), "3f808001");
        testEncode(6, ((63 + 127) + ((128 * 128) * 127)), "3fFf807f");
        testEncode(6, ((63 + 0) + ((128 * 128) * 128)), "3f80808001");
        testEncode(8, 0, "00");
        testEncode(8, 1, "01");
        testEncode(8, 128, "80");
        testEncode(8, 254, "Fe");
        testEncode(8, 255, "Ff00");
        testEncode(8, (255 + 1), "Ff01");
        testEncode(8, (255 + 126), "Ff7e");
        testEncode(8, (255 + 127), "Ff7f");
        testEncode(8, (255 + 128), "Ff8001");
        testEncode(8, ((255 + 0) + (128 * 128)), "Ff808001");
    }

    @Test
    public void testDecode() {
        testDecode(6, 0, "00");
        testDecode(6, 1, "01");
        testDecode(6, 62, "3e");
        testDecode(6, 63, "3f00");
        testDecode(6, (63 + 1), "3f01");
        testDecode(6, (63 + 126), "3f7e");
        testDecode(6, (63 + 127), "3f7f");
        testDecode(6, (63 + 128), "3f8001");
        testDecode(6, (63 + 129), "3f8101");
        testDecode(6, ((63 + 127) + (128 * 1)), "3fFf01");
        testDecode(6, ((63 + 0) + (128 * 2)), "3f8002");
        testDecode(6, ((63 + 1) + (128 * 2)), "3f8102");
        testDecode(6, ((63 + 127) + (128 * 127)), "3fFf7f");
        testDecode(6, ((63 + 0) + (128 * 128)), "3f808001");
        testDecode(6, ((63 + 127) + ((128 * 128) * 127)), "3fFf807f");
        testDecode(6, ((63 + 0) + ((128 * 128) * 128)), "3f80808001");
        testDecode(8, 0, "00");
        testDecode(8, 1, "01");
        testDecode(8, 128, "80");
        testDecode(8, 254, "Fe");
        testDecode(8, 255, "Ff00");
        testDecode(8, (255 + 1), "Ff01");
        testDecode(8, (255 + 126), "Ff7e");
        testDecode(8, (255 + 127), "Ff7f");
        testDecode(8, (255 + 128), "Ff8001");
        testDecode(8, ((255 + 0) + (128 * 128)), "Ff808001");
    }

    @Test
    public void testEncodeExampleD_1_1() {
        ByteBuffer buf = BufferUtil.allocate(16);
        int p = BufferUtil.flipToFill(buf);
        buf.put(((byte) (119)));
        buf.put(((byte) (255)));
        NBitInteger.encode(buf, 5, 10);
        BufferUtil.flipToFlush(buf, p);
        String r = TypeUtil.toHexString(BufferUtil.toArray(buf));
        Assertions.assertEquals("77Ea", r);
    }

    @Test
    public void testDecodeExampleD_1_1() {
        ByteBuffer buf = ByteBuffer.wrap(TypeUtil.fromHexString("77EaFF"));
        buf.position(2);
        Assertions.assertEquals(10, NBitInteger.decode(buf, 5));
    }

    @Test
    public void testEncodeExampleD_1_2() {
        ByteBuffer buf = BufferUtil.allocate(16);
        int p = BufferUtil.flipToFill(buf);
        buf.put(((byte) (136)));
        buf.put(((byte) (0)));
        NBitInteger.encode(buf, 5, 1337);
        BufferUtil.flipToFlush(buf, p);
        String r = TypeUtil.toHexString(BufferUtil.toArray(buf));
        Assertions.assertEquals("881f9a0a", r);
    }

    @Test
    public void testDecodeExampleD_1_2() {
        ByteBuffer buf = ByteBuffer.wrap(TypeUtil.fromHexString("881f9a0aff"));
        buf.position(2);
        Assertions.assertEquals(1337, NBitInteger.decode(buf, 5));
    }

    @Test
    public void testEncodeExampleD_1_3() {
        ByteBuffer buf = BufferUtil.allocate(16);
        int p = BufferUtil.flipToFill(buf);
        buf.put(((byte) (136)));
        buf.put(((byte) (255)));
        NBitInteger.encode(buf, 8, 42);
        BufferUtil.flipToFlush(buf, p);
        String r = TypeUtil.toHexString(BufferUtil.toArray(buf));
        Assertions.assertEquals("88Ff2a", r);
    }

    @Test
    public void testDecodeExampleD_1_3() {
        ByteBuffer buf = ByteBuffer.wrap(TypeUtil.fromHexString("882aFf"));
        buf.position(1);
        Assertions.assertEquals(42, NBitInteger.decode(buf, 8));
    }
}

