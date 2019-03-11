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
package org.eclipse.jetty.http;


import HttpVersion.HTTP_1_0;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static HttpHeader.ACCEPT;


public class HttpFieldTest {
    @Test
    public void testContainsSimple() throws Exception {
        HttpField field = new HttpField("name", "SomeValue");
        Assertions.assertTrue(field.contains("somevalue"));
        Assertions.assertTrue(field.contains("sOmEvAlUe"));
        Assertions.assertTrue(field.contains("SomeValue"));
        Assertions.assertFalse(field.contains("other"));
        Assertions.assertFalse(field.contains("some"));
        Assertions.assertFalse(field.contains("Some"));
        Assertions.assertFalse(field.contains("value"));
        Assertions.assertFalse(field.contains("v"));
        Assertions.assertFalse(field.contains(""));
        Assertions.assertFalse(field.contains(null));
    }

    @Test
    public void testCaseInsensitiveHashcode_KnownField() throws Exception {
        HttpField fieldFoo1 = new HttpField("Cookie", "foo");
        HttpField fieldFoo2 = new HttpField("cookie", "foo");
        MatcherAssert.assertThat("Field hashcodes are case insensitive", fieldFoo1.hashCode(), CoreMatchers.is(fieldFoo2.hashCode()));
    }

    @Test
    public void testCaseInsensitiveHashcode_UnknownField() throws Exception {
        HttpField fieldFoo1 = new HttpField("X-Foo", "bar");
        HttpField fieldFoo2 = new HttpField("x-foo", "bar");
        MatcherAssert.assertThat("Field hashcodes are case insensitive", fieldFoo1.hashCode(), CoreMatchers.is(fieldFoo2.hashCode()));
    }

    @Test
    public void testContainsList() throws Exception {
        HttpField field = new HttpField("name", ",aaa,Bbb,CCC, ddd , e e, \"\\\"f,f\\\"\", ");
        Assertions.assertTrue(field.contains("aaa"));
        Assertions.assertTrue(field.contains("bbb"));
        Assertions.assertTrue(field.contains("ccc"));
        Assertions.assertTrue(field.contains("Aaa"));
        Assertions.assertTrue(field.contains("Bbb"));
        Assertions.assertTrue(field.contains("Ccc"));
        Assertions.assertTrue(field.contains("AAA"));
        Assertions.assertTrue(field.contains("BBB"));
        Assertions.assertTrue(field.contains("CCC"));
        Assertions.assertTrue(field.contains("ddd"));
        Assertions.assertTrue(field.contains("e e"));
        Assertions.assertTrue(field.contains("\"f,f\""));
        Assertions.assertFalse(field.contains(""));
        Assertions.assertFalse(field.contains("aa"));
        Assertions.assertFalse(field.contains("bb"));
        Assertions.assertFalse(field.contains("cc"));
        Assertions.assertFalse(field.contains(null));
    }

    @Test
    public void testQualityContainsList() throws Exception {
        HttpField field;
        field = new HttpField("name", "yes");
        Assertions.assertTrue(field.contains("yes"));
        Assertions.assertFalse(field.contains("no"));
        field = new HttpField("name", ",yes,");
        Assertions.assertTrue(field.contains("yes"));
        Assertions.assertFalse(field.contains("no"));
        field = new HttpField("name", "other,yes,other");
        Assertions.assertTrue(field.contains("yes"));
        Assertions.assertFalse(field.contains("no"));
        field = new HttpField("name", "other,  yes  ,other");
        Assertions.assertTrue(field.contains("yes"));
        Assertions.assertFalse(field.contains("no"));
        field = new HttpField("name", "other,  y s  ,other");
        Assertions.assertTrue(field.contains("y s"));
        Assertions.assertFalse(field.contains("no"));
        field = new HttpField("name", "other,  \"yes\"  ,other");
        Assertions.assertTrue(field.contains("yes"));
        Assertions.assertFalse(field.contains("no"));
        field = new HttpField("name", "other,  \"\\\"yes\\\"\"  ,other");
        Assertions.assertTrue(field.contains("\"yes\""));
        Assertions.assertFalse(field.contains("no"));
        field = new HttpField("name", ";no,yes,;no");
        Assertions.assertTrue(field.contains("yes"));
        Assertions.assertFalse(field.contains("no"));
        field = new HttpField("name", "no;q=0,yes;q=1,no; q = 0");
        Assertions.assertTrue(field.contains("yes"));
        Assertions.assertFalse(field.contains("no"));
        field = new HttpField("name", "no;q=0.0000,yes;q=0.0001,no; q = 0.00000");
        Assertions.assertTrue(field.contains("yes"));
        Assertions.assertFalse(field.contains("no"));
        field = new HttpField("name", "no;q=0.0000,Yes;Q=0.0001,no; Q = 0.00000");
        Assertions.assertTrue(field.contains("yes"));
        Assertions.assertFalse(field.contains("no"));
    }

    @Test
    public void testValues() {
        String[] values = new HttpField("name", "value").getValues();
        Assertions.assertEquals(1, values.length);
        Assertions.assertEquals("value", values[0]);
        values = new HttpField("name", "a,b,c").getValues();
        Assertions.assertEquals(3, values.length);
        Assertions.assertEquals("a", values[0]);
        Assertions.assertEquals("b", values[1]);
        Assertions.assertEquals("c", values[2]);
        values = new HttpField("name", "a,\"x,y,z\",c").getValues();
        Assertions.assertEquals(3, values.length);
        Assertions.assertEquals("a", values[0]);
        Assertions.assertEquals("x,y,z", values[1]);
        Assertions.assertEquals("c", values[2]);
        values = new HttpField("name", "a,\"x,\\\"p,q\\\",z\",c").getValues();
        Assertions.assertEquals(3, values.length);
        Assertions.assertEquals("a", values[0]);
        Assertions.assertEquals("x,\"p,q\",z", values[1]);
        Assertions.assertEquals("c", values[2]);
    }

    @Test
    public void testCachedField() {
        PreEncodedHttpField field = new PreEncodedHttpField(ACCEPT, "something");
        ByteBuffer buf = BufferUtil.allocate(256);
        BufferUtil.clearToFill(buf);
        field.putTo(buf, HTTP_1_0);
        BufferUtil.flipToFlush(buf, 0);
        String s = BufferUtil.toString(buf);
        Assertions.assertEquals("Accept: something\r\n", s);
    }

    @Test
    public void testCachedFieldWithHeaderName() {
        PreEncodedHttpField field = new PreEncodedHttpField("X-My-Custom-Header", "something");
        Assertions.assertNull(field.getHeader());
        Assertions.assertEquals("X-My-Custom-Header", field.getName());
        Assertions.assertEquals("something", field.getValue());
    }
}

