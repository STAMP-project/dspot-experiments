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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;


public class PatternRuleTest {
    private PatternRule _rule;

    @Test
    public void testTrueMatch() throws IOException {
        String[][] matchCases = new String[][]{ // index 0 - pattern
        // index 1 - URI to match
        new String[]{ "/abc", "/abc" }, new String[]{ "/abc/", "/abc/" }, new String[]{ "/abc/path/longer", "/abc/path/longer" }, new String[]{ "/abc/path/longer/", "/abc/path/longer/" }, new String[]{ "/abc/*", "/abc/hello.jsp" }, new String[]{ "/abc/*", "/abc/a" }, new String[]{ "/abc/*", "/abc/a/hello.jsp" }, new String[]{ "/abc/*", "/abc/a/b" }, new String[]{ "/abc/*", "/abc/a/b/hello.jsp" }, new String[]{ "/abc/*", "/abc/a/b/c" }, new String[]{ "/abc/*", "/abc/a/b/c/hello.jsp" }, new String[]{ "/abc/def/*", "/abc/def/gf" }, new String[]{ "/abc/def/*", "/abc/def/gf.html" }, new String[]{ "/abc/def/*", "/abc/def/ghi" }, new String[]{ "/abc/def/*", "/abc/def/ghi/" }, new String[]{ "/abc/def/*", "/abc/def/ghi/hello.html" }, new String[]{ "*.do", "/abc.do" }, new String[]{ "*.do", "/abc/hello.do" }, new String[]{ "*.do", "/abc/def/hello.do" }, new String[]{ "*.do", "/abc/def/ghi/hello.do" }, new String[]{ "*.jsp", "/abc.jsp" }, new String[]{ "*.jsp", "/abc/hello.jsp" }, new String[]{ "*.jsp", "/abc/def/hello.jsp" }, new String[]{ "*.jsp", "/abc/def/ghi/hello.jsp" }, new String[]{ "/", "/Other" }, new String[]{ "/", "/Other/hello.do" }, new String[]{ "/", "/Other/path" }, new String[]{ "/", "/Other/path/hello.do" }, new String[]{ "/", "/abc/def" }, new String[]{ "/abc:/def", "/abc:/def" } };
        for (String[] matchCase : matchCases) {
            assertMatch(true, matchCase);
        }
    }

    @Test
    public void testFalseMatch() throws IOException {
        String[][] matchCases = new String[][]{ new String[]{ "/abc", "/abcd" }, new String[]{ "/abc/", "/abcd/" }, new String[]{ "/abc/path/longer", "/abc/path/longer/" }, new String[]{ "/abc/path/longer", "/abc/path/longer1" }, new String[]{ "/abc/path/longer/", "/abc/path/longer" }, new String[]{ "/abc/path/longer/", "/abc/path/longer1/" }, new String[]{ "/*.jsp", "/hello.jsp" }, new String[]{ "/abc/*.jsp", "/abc/hello.jsp" }, new String[]{ "*.jsp", "/hello.1jsp" }, new String[]{ "*.jsp", "/hello.jsp1" }, new String[]{ "*.jsp", "/hello.do" }, new String[]{ "*.jsp", "/abc/hello.do" }, new String[]{ "*.jsp", "/abc/def/hello.do" }, new String[]{ "*.jsp", "/abc.do" } };
        for (String[] matchCase : matchCases) {
            assertMatch(false, matchCase);
        }
    }

    private class TestPatternRule extends PatternRule {
        @Override
        public String apply(String target, HttpServletRequest request, HttpServletResponse response) throws IOException {
            return target;
        }
    }
}

