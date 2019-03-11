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


import StringUtil.__ISO_8859_1;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;


/**
 * URL Encoding / Decoding Tests
 */
public class URLEncodedTest {
    /* -------------------------------------------------------------- */
    @Test
    @EnabledIfSystemProperty(named = "org.eclipse.jetty.util.UrlEncoding.charset", matches = "\\p{Alnum}")
    public void testCharsetViaSystemProperty() throws Exception {
        try (ByteArrayInputStream in3 = new ByteArrayInputStream("name=libell%E9".getBytes(__ISO_8859_1))) {
            MultiMap m3 = new MultiMap();
            Charset nullCharset = null;// use the one from the system property

            UrlEncoded.decodeTo(in3, m3, nullCharset, (-1), (-1));
            Assertions.assertEquals("libell\u00e9", m3.getString("name"), "stream name");
        }
    }

    /* -------------------------------------------------------------- */
    @Test
    public void testUtf8() throws Exception {
        UrlEncoded url_encoded = new UrlEncoded();
        Assertions.assertEquals(0, url_encoded.size(), "Empty");
        url_encoded.clear();
        url_encoded.decode("text=%E0%B8%9F%E0%B8%AB%E0%B8%81%E0%B8%A7%E0%B8%94%E0%B8%B2%E0%B9%88%E0%B8%81%E0%B8%9F%E0%B8%A7%E0%B8%AB%E0%B8%AA%E0%B8%94%E0%B8%B2%E0%B9%88%E0%B8%AB%E0%B8%9F%E0%B8%81%E0%B8%A7%E0%B8%94%E0%B8%AA%E0%B8%B2%E0%B8%9F%E0%B8%81%E0%B8%AB%E0%B8%A3%E0%B8%94%E0%B9%89%E0%B8%9F%E0%B8%AB%E0%B8%99%E0%B8%81%E0%B8%A3%E0%B8%94%E0%B8%B5&Action=Submit");
        String hex = "E0B89FE0B8ABE0B881E0B8A7E0B894E0B8B2E0B988E0B881E0B89FE0B8A7E0B8ABE0B8AAE0B894E0B8B2E0B988E0B8ABE0B89FE0B881E0B8A7E0B894E0B8AAE0B8B2E0B89FE0B881E0B8ABE0B8A3E0B894E0B989E0B89FE0B8ABE0B899E0B881E0B8A3E0B894E0B8B5";
        String expected = new String(TypeUtil.fromHexString(hex), "utf-8");
        Assertions.assertEquals(expected, url_encoded.getString("text"));
    }

    @Test
    public void testUtf8_MultiByteCodePoint() {
        String input = "text=test%C3%A4";
        UrlEncoded url_encoded = new UrlEncoded();
        url_encoded.decode(input);
        // http://www.ltg.ed.ac.uk/~richard/utf-8.cgi?input=00e4&mode=hex
        // Should be "test?"
        // "test" followed by a LATIN SMALL LETTER A WITH DIAERESIS
        String expected = "test\u00e4";
        MatcherAssert.assertThat(url_encoded.getString("text"), CoreMatchers.is(expected));
    }
}

