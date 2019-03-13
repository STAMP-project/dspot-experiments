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


import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.junit.jupiter.api.Test;

import static Utf8Appendable.REPLACEMENT;


public class UrlEncodedUtf8Test {
    static final Logger LOG = Log.getLogger(UrlEncodedUtf8Test.class);

    @Test
    public void testIncompleteSequestAtTheEnd() throws Exception {
        byte[] bytes = new byte[]{ 97, 98, 61, 99, -50 };
        String test = new String(bytes, StandardCharsets.UTF_8);
        String expected = "c" + (REPLACEMENT);
        UrlEncodedUtf8Test.fromString(test, test, "ab", expected, false);
        UrlEncodedUtf8Test.fromInputStream(test, bytes, "ab", expected, false);
    }

    @Test
    public void testIncompleteSequestAtTheEnd2() throws Exception {
        byte[] bytes = new byte[]{ 97, 98, 61, -50 };
        String test = new String(bytes, StandardCharsets.UTF_8);
        String expected = "" + (REPLACEMENT);
        UrlEncodedUtf8Test.fromString(test, test, "ab", expected, false);
        UrlEncodedUtf8Test.fromInputStream(test, bytes, "ab", expected, false);
    }

    @Test
    public void testIncompleteSequestInName() throws Exception {
        byte[] bytes = new byte[]{ 101, -50, 61, 102, 103, 38, 97, 98, 61, 99, 100 };
        String test = new String(bytes, StandardCharsets.UTF_8);
        String name = "e" + (REPLACEMENT);
        String value = "fg";
        UrlEncodedUtf8Test.fromString(test, test, name, value, false);
        UrlEncodedUtf8Test.fromInputStream(test, bytes, name, value, false);
    }

    @Test
    public void testIncompleteSequestInValue() throws Exception {
        byte[] bytes = new byte[]{ 101, 102, 61, 103, -50, 38, 97, 98, 61, 99, 100 };
        String test = new String(bytes, StandardCharsets.UTF_8);
        String name = "ef";
        String value = "g" + (REPLACEMENT);
        UrlEncodedUtf8Test.fromString(test, test, name, value, false);
        UrlEncodedUtf8Test.fromInputStream(test, bytes, name, value, false);
    }
}

