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
package org.eclipse.jetty.rewrite.handler;


import java.io.IOException;
import java.util.Iterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HeaderRegexRuleTest extends AbstractRuleTestCase {
    private HeaderRegexRule _rule;

    @Test
    public void testHeaderWithTextValues() throws IOException {
        // different keys
        String[][] headers = new String[][]{ new String[]{ "hnum#1", "test1" }, new String[]{ "hnum#2", "2test2" }, new String[]{ "hnum#3", "test3" } };
        assertHeaders(headers);
    }

    @Test
    public void testHeaderWithNumberValues() throws IOException {
        String[][] headers = new String[][]{ new String[]{ "hello", "1" }, new String[]{ "hello", "-1" }, new String[]{ "hello", "100" }, new String[]{ "hello", "100" }, new String[]{ "hello", "100" }, new String[]{ "hello", "100" }, new String[]{ "hello", "100" }, new String[]{ "hello1", "200" } };
        assertHeaders(headers);
    }

    @Test
    public void testHeaderOverwriteValues() throws IOException {
        String[][] headers = new String[][]{ new String[]{ "size", "100" }, new String[]{ "size", "200" }, new String[]{ "size", "300" }, new String[]{ "size", "400" }, new String[]{ "size", "500" }, new String[]{ "title", "abc" }, new String[]{ "title", "bac" }, new String[]{ "title", "cba" }, new String[]{ "title1", "abba" }, new String[]{ "title1", "abba1" }, new String[]{ "title1", "abba" }, new String[]{ "title1", "abba1" } };
        assertHeaders(headers);
        Iterator<String> e = _response.getHeaders("size").iterator();
        int count = 0;
        while (e.hasNext()) {
            e.next();
            count++;
        } 
        Assertions.assertEquals(1, count);
        Assertions.assertEquals("500", _response.getHeader("size"));
        Assertions.assertEquals("cba", _response.getHeader("title"));
        Assertions.assertEquals("abba1", _response.getHeader("title1"));
    }

    @Test
    public void testMatch() throws Exception {
        _rule.setRegex("/my/dir/file/(.*)$");
        _rule.setName("cache-control");
        _rule.setValue("no-store");
        _rule.matchAndApply("/my/dir/file/", _request, _response);
        Assertions.assertEquals("no-store", _response.getHeader("cache-control"));
    }

    @Test
    public void testNotMatch() throws Exception {
        reset();
        _rule.setRegex("/my/dir/file/(.*)$");
        _rule.setName("cache-control");
        _rule.setValue("no-store");
        _rule.matchAndApply("/my/dir/file_not_match/", _request, _response);
        Assertions.assertEquals(null, _response.getHeader("cache-control"));
    }
}

