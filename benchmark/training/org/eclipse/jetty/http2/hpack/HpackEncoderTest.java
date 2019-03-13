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
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.util.BufferUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static HpackContext.STATIC_SIZE;


/* ------------------------------------------------------------ */
/**
 *
 */
public class HpackEncoderTest {
    @Test
    public void testUnknownFieldsContextManagement() {
        HpackEncoder encoder = new HpackEncoder((38 * 5));
        HttpFields fields = new HttpFields();
        HttpField[] field = new HttpField[]{ new HttpField("fo0", "b0r"), new HttpField("fo1", "b1r"), new HttpField("fo2", "b2r"), new HttpField("fo3", "b3r"), new HttpField("fo4", "b4r"), new HttpField("fo5", "b5r"), new HttpField("fo6", "b6r"), new HttpField("fo7", "b7r"), new HttpField("fo8", "b8r"), new HttpField("fo9", "b9r"), new HttpField("foA", "bAr") };
        // Add 4 entries
        for (int i = 0; i <= 3; i++)
            fields.add(field[i]);

        // encode them
        ByteBuffer buffer = BufferUtil.allocate(4096);
        int pos = BufferUtil.flipToFill(buffer);
        encoder.encode(buffer, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer, pos);
        // something was encoded!
        MatcherAssert.assertThat(buffer.remaining(), Matchers.greaterThan(0));
        // All are in the dynamic table
        Assertions.assertEquals(4, encoder.getHpackContext().size());
        // encode exact same fields again!
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer, 0);
        // All are in the dynamic table
        Assertions.assertEquals(4, encoder.getHpackContext().size());
        // Add 4 more fields
        for (int i = 4; i <= 7; i++)
            fields.add(field[i]);

        // encode
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer, 0);
        // something was encoded!
        MatcherAssert.assertThat(buffer.remaining(), Matchers.greaterThan(0));
        // max dynamic table size reached
        Assertions.assertEquals(5, encoder.getHpackContext().size());
        // remove some fields
        for (int i = 0; i <= 7; i += 2)
            fields.remove(field[i].getName());

        // encode
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer, 0);
        // something was encoded!
        MatcherAssert.assertThat(buffer.remaining(), Matchers.greaterThan(0));
        // max dynamic table size reached
        Assertions.assertEquals(5, encoder.getHpackContext().size());
        // remove another fields
        fields.remove(field[1].getName());
        // encode
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer, 0);
        // something was encoded!
        MatcherAssert.assertThat(buffer.remaining(), Matchers.greaterThan(0));
        // max dynamic table size reached
        Assertions.assertEquals(5, encoder.getHpackContext().size());
        // re add the field
        fields.add(field[1]);
        // encode
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer, 0);
        // something was encoded!
        MatcherAssert.assertThat(buffer.remaining(), Matchers.greaterThan(0));
        // max dynamic table size reached
        Assertions.assertEquals(5, encoder.getHpackContext().size());
    }

    @Test
    public void testNeverIndexSetCookie() {
        HpackEncoder encoder = new HpackEncoder((38 * 5));
        ByteBuffer buffer = BufferUtil.allocate(4096);
        HttpFields fields = new HttpFields();
        fields.put("set-cookie", "some cookie value");
        // encode
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer, 0);
        // something was encoded!
        MatcherAssert.assertThat(buffer.remaining(), Matchers.greaterThan(0));
        // empty dynamic table
        Assertions.assertEquals(0, encoder.getHpackContext().size());
        // encode again
        BufferUtil.clearToFill(buffer);
        encoder.encode(buffer, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer, 0);
        // something was encoded!
        MatcherAssert.assertThat(buffer.remaining(), Matchers.greaterThan(0));
        // empty dynamic table
        Assertions.assertEquals(0, encoder.getHpackContext().size());
    }

    @Test
    public void testFieldLargerThanTable() {
        HttpFields fields = new HttpFields();
        HpackEncoder encoder = new HpackEncoder(128);
        ByteBuffer buffer0 = BufferUtil.allocate(4096);
        int pos = BufferUtil.flipToFill(buffer0);
        encoder.encode(buffer0, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer0, pos);
        encoder = new HpackEncoder(128);
        fields.add(new HttpField("user-agent", "jetty/test"));
        ByteBuffer buffer1 = BufferUtil.allocate(4096);
        pos = BufferUtil.flipToFill(buffer1);
        encoder.encode(buffer1, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer1, pos);
        encoder = new HpackEncoder(128);
        fields.add(new HttpField(":path", ("This is a very large field, whose size is larger than the dynamic table so it should not be indexed as it will not fit in the table ever!" + (("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX " + "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY ") + "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ "))));
        ByteBuffer buffer2 = BufferUtil.allocate(4096);
        pos = BufferUtil.flipToFill(buffer2);
        encoder.encode(buffer2, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer2, pos);
        encoder = new HpackEncoder(128);
        fields.add(new HttpField("host", "somehost"));
        ByteBuffer buffer = BufferUtil.allocate(4096);
        pos = BufferUtil.flipToFill(buffer);
        encoder.encode(buffer, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer, pos);
        // System.err.println(BufferUtil.toHexString(buffer0));
        // System.err.println(BufferUtil.toHexString(buffer1));
        // System.err.println(BufferUtil.toHexString(buffer2));
        // System.err.println(BufferUtil.toHexString(buffer));
        // something was encoded!
        MatcherAssert.assertThat(buffer.remaining(), Matchers.greaterThan(0));
        // check first field is static index name and dynamic index body
        MatcherAssert.assertThat((((buffer.get(buffer0.remaining())) & 255) >> 6), Matchers.equalTo(1));
        // check first field is static index name and literal body
        MatcherAssert.assertThat((((buffer.get(buffer1.remaining())) & 255) >> 4), Matchers.equalTo(0));
        // check first field is static index name and dynamic index body
        MatcherAssert.assertThat((((buffer.get(buffer2.remaining())) & 255) >> 6), Matchers.equalTo(1));
        // Only first and third fields are put in the table
        HpackContext context = encoder.getHpackContext();
        MatcherAssert.assertThat(context.size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(context.get(((STATIC_SIZE) + 1)).getHttpField().getName(), Matchers.equalTo("host"));
        MatcherAssert.assertThat(context.get(((STATIC_SIZE) + 2)).getHttpField().getName(), Matchers.equalTo("user-agent"));
        MatcherAssert.assertThat(context.getDynamicTableSize(), Matchers.equalTo(((context.get(((STATIC_SIZE) + 1)).getSize()) + (context.get(((STATIC_SIZE) + 2)).getSize()))));
    }

    @Test
    public void testResize() {
        HttpFields fields = new HttpFields();
        fields.add("host", "localhost0");
        fields.add("cookie", "abcdefghij");
        HpackEncoder encoder = new HpackEncoder(4096);
        ByteBuffer buffer = BufferUtil.allocate(4096);
        int pos = BufferUtil.flipToFill(buffer);
        encoder.encodeMaxDynamicTableSize(buffer, 0);
        encoder.setRemoteMaxDynamicTableSize(50);
        encoder.encode(buffer, new org.eclipse.jetty.http.MetaData(HttpVersion.HTTP_2, fields));
        BufferUtil.flipToFlush(buffer, pos);
        HpackContext context = encoder.getHpackContext();
        MatcherAssert.assertThat(context.getMaxDynamicTableSize(), Matchers.is(50));
        MatcherAssert.assertThat(context.size(), Matchers.is(1));
    }
}

