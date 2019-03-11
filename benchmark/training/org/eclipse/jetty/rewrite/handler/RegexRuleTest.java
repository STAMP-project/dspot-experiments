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
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;


public class RegexRuleTest {
    private RegexRule _rule;

    @Test
    public void testTrueMatch() throws IOException {
        String[][] matchCases = new String[][]{ // regex: *.jsp
        new String[]{ "/.*.jsp", "/hello.jsp" }, new String[]{ "/.*.jsp", "/abc/hello.jsp" }, // regex: /abc or /def
        new String[]{ "/abc|/def", "/abc" }, new String[]{ "/abc|/def", "/def" }, // regex: *.do or *.jsp
        new String[]{ ".*\\.do|.*\\.jsp", "/hello.do" }, new String[]{ ".*\\.do|.*\\.jsp", "/hello.jsp" }, new String[]{ ".*\\.do|.*\\.jsp", "/abc/hello.do" }, new String[]{ ".*\\.do|.*\\.jsp", "/abc/hello.jsp" }, new String[]{ "/abc/.*.htm|/def/.*.htm", "/abc/hello.htm" }, new String[]{ "/abc/.*.htm|/def/.*.htm", "/abc/def/hello.htm" }, // regex: /abc/*.jsp
        new String[]{ "/abc/.*.jsp", "/abc/hello.jsp" }, new String[]{ "/abc/.*.jsp", "/abc/def/hello.jsp" } };
        for (String[] matchCase : matchCases) {
            assertMatch(true, matchCase);
        }
    }

    @Test
    public void testFalseMatch() throws IOException {
        String[][] matchCases = new String[][]{ new String[]{ "/abc/.*.jsp", "/hello.jsp" } };
        for (String[] matchCase : matchCases) {
            assertMatch(false, matchCase);
        }
    }

    private class TestRegexRule extends RegexRule {
        @Override
        public String apply(String target, HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException {
            return target;
        }
    }
}

