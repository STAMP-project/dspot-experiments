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
package org.eclipse.jetty.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class QuotedStringTokenizerTest {
    /* Test for String nextToken() */
    @Test
    public void testTokenizer0() {
        QuotedStringTokenizer tok = new QuotedStringTokenizer("abc\n\"d\\\"\'\"\n\'p\\\',y\'\nz");
        checkTok(tok, false, false);
    }

    /* Test for String nextToken() */
    @Test
    public void testTokenizer1() {
        QuotedStringTokenizer tok = new QuotedStringTokenizer("abc, \"d\\\"\'\",\'p\\\',y\' z", " ,");
        checkTok(tok, false, false);
    }

    /* Test for String nextToken() */
    @Test
    public void testTokenizer2() {
        QuotedStringTokenizer tok = new QuotedStringTokenizer("abc, \"d\\\"\'\",\'p\\\',y\' z", " ,", false);
        checkTok(tok, false, false);
        tok = new QuotedStringTokenizer("abc, \"d\\\"\'\",\'p\\\',y\' z", " ,", true);
        checkTok(tok, true, false);
    }

    /* Test for String nextToken() */
    @Test
    public void testTokenizer3() {
        QuotedStringTokenizer tok;
        tok = new QuotedStringTokenizer("abc, \"d\\\"\'\",\'p\\\',y\' z", " ,", false, false);
        checkTok(tok, false, false);
        tok = new QuotedStringTokenizer("abc, \"d\\\"\'\",\'p\\\',y\' z", " ,", false, true);
        checkTok(tok, false, true);
        tok = new QuotedStringTokenizer("abc, \"d\\\"\'\",\'p\\\',y\' z", " ,", true, false);
        checkTok(tok, true, false);
        tok = new QuotedStringTokenizer("abc, \"d\\\"\'\",\'p\\\',y\' z", " ,", true, true);
        checkTok(tok, true, true);
    }

    @Test
    public void testQuote() {
        StringBuffer buf = new StringBuffer();
        buf.setLength(0);
        QuotedStringTokenizer.quote(buf, "abc \n efg");
        Assertions.assertEquals("\"abc \\n efg\"", buf.toString());
        buf.setLength(0);
        QuotedStringTokenizer.quote(buf, "abcefg");
        Assertions.assertEquals("\"abcefg\"", buf.toString());
        buf.setLength(0);
        QuotedStringTokenizer.quote(buf, "abcefg\"");
        Assertions.assertEquals("\"abcefg\\\"\"", buf.toString());
    }

    /* Test for String nextToken() */
    @Test
    public void testTokenizer4() {
        QuotedStringTokenizer tok = new QuotedStringTokenizer("abc'def,ghi'jkl", ",");
        tok.setSingle(false);
        Assertions.assertEquals("abc'def", tok.nextToken());
        Assertions.assertEquals("ghi'jkl", tok.nextToken());
        tok = new QuotedStringTokenizer("abc'def,ghi'jkl", ",");
        tok.setSingle(true);
        Assertions.assertEquals("abcdef,ghijkl", tok.nextToken());
    }

    /* Test for String quote(String, String) */
    @Test
    public void testQuoteIfNeeded() {
        Assertions.assertEquals("abc", QuotedStringTokenizer.quoteIfNeeded("abc", " ,"));
        Assertions.assertEquals("\"a c\"", QuotedStringTokenizer.quoteIfNeeded("a c", " ,"));
        Assertions.assertEquals("\"a\'c\"", QuotedStringTokenizer.quoteIfNeeded("a'c", " ,"));
        Assertions.assertEquals("\"a\\n\\r\\t\"", QuotedStringTokenizer.quote("a\n\r\t"));
        Assertions.assertEquals("\"\\u0000\\u001f\"", QuotedStringTokenizer.quote("\u0000\u001f"));
    }

    @Test
    public void testUnquote() {
        Assertions.assertEquals("abc", QuotedStringTokenizer.unquote("abc"));
        Assertions.assertEquals("a\"c", QuotedStringTokenizer.unquote("\"a\\\"c\""));
        Assertions.assertEquals("a'c", QuotedStringTokenizer.unquote("\"a\'c\""));
        Assertions.assertEquals("a\n\r\t", QuotedStringTokenizer.unquote("\"a\\n\\r\\t\""));
        Assertions.assertEquals("\u0000\u001f ", QuotedStringTokenizer.unquote("\"\u0000\u001f \""));
        Assertions.assertEquals("\u0000\u001f ", QuotedStringTokenizer.unquote("\"\u0000\u001f \""));
        Assertions.assertEquals("ab\u001ec", QuotedStringTokenizer.unquote("ab\u001ec"));
        Assertions.assertEquals("ab\u001ec", QuotedStringTokenizer.unquote("\"ab\u001ec\""));
    }

    @Test
    public void testUnquoteOnly() {
        Assertions.assertEquals("abc", QuotedStringTokenizer.unquoteOnly("abc"));
        Assertions.assertEquals("a\"c", QuotedStringTokenizer.unquoteOnly("\"a\\\"c\""));
        Assertions.assertEquals("a'c", QuotedStringTokenizer.unquoteOnly("\"a\'c\""));
        Assertions.assertEquals("a\\n\\r\\t", QuotedStringTokenizer.unquoteOnly("\"a\\\\n\\\\r\\\\t\""));
        Assertions.assertEquals("ba\\uXXXXaaa", QuotedStringTokenizer.unquoteOnly("\"ba\\\\uXXXXaaa\""));
    }

    /**
     * When encountering a Content-Disposition line during a multi-part mime file
     * upload, the filename="..." field can contain '\' characters that do not
     * belong to a proper escaping sequence, this tests QuotedStringTokenizer to
     * ensure that it preserves those slashes for where they cannot be escaped.
     */
    @Test
    public void testNextTokenOnContentDisposition() {
        String content_disposition = "form-data; name=\"fileup\"; filename=\"Taken on Aug 22 \\ 2012.jpg\"";
        QuotedStringTokenizer tok = new QuotedStringTokenizer(content_disposition, ";", false, true);
        Assertions.assertEquals("form-data", tok.nextToken().trim());
        Assertions.assertEquals("name=\"fileup\"", tok.nextToken().trim());
        Assertions.assertEquals("filename=\"Taken on Aug 22 \\ 2012.jpg\"", tok.nextToken().trim());
    }
}

