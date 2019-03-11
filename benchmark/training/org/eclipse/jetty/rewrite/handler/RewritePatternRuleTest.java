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
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


public class RewritePatternRuleTest extends AbstractRuleTestCase {
    // TODO: Parameterize
    private String[][] _tests = new String[][]{ new String[]{ "/foo/bar", "/", "/replace" }, new String[]{ "/foo/bar", "/*", "/replace/foo/bar" }, new String[]{ "/foo/bar", "/foo/*", "/replace/bar" }, new String[]{ "/foo/bar", "/foo/bar", "/replace" }, new String[]{ "/foo/bar.txt", "*.txt", "/replace" }, new String[]{ "/foo/bar/%20x", "/foo/*", "/replace/bar/%20x" } };

    private RewritePatternRule _rule;

    @Test
    public void testMatchAndApplyAndApplyURI() throws IOException {
        for (String[] test : _tests) {
            _rule.setPattern(test[1]);
            String result = _rule.matchAndApply(test[0], _request, _response);
            MatcherAssert.assertThat(test[1], test[2], CoreMatchers.is(result));
            _rule.applyURI(_request, null, result);
            MatcherAssert.assertThat(_request.getRequestURI(), CoreMatchers.is(test[2]));
        }
    }

    @Test
    public void testReplacementWithQueryString() throws IOException {
        String replacement = "/replace?given=param";
        String[] split = replacement.split("\\?", 2);
        String path = split[0];
        String queryString = split[1];
        RewritePatternRule rewritePatternRule = new RewritePatternRule();
        rewritePatternRule.setPattern("/old/context");
        rewritePatternRule.setReplacement(replacement);
        String result = rewritePatternRule.matchAndApply("/old/context", _request, _response);
        MatcherAssert.assertThat(result, CoreMatchers.is(path));
        rewritePatternRule.applyURI(_request, null, result);
        MatcherAssert.assertThat("queryString matches expected", _request.getQueryString(), CoreMatchers.is(queryString));
        MatcherAssert.assertThat("request URI matches expected", _request.getRequestURI(), CoreMatchers.is(path));
    }

    @Test
    public void testRequestWithQueryString() throws IOException {
        String replacement = "/replace";
        String queryString = "request=parameter";
        _request.setURIPathQuery("/old/context");
        _request.setQueryString(queryString);
        RewritePatternRule rewritePatternRule = new RewritePatternRule();
        rewritePatternRule.setPattern("/old/context");
        rewritePatternRule.setReplacement(replacement);
        String result = rewritePatternRule.matchAndApply("/old/context", _request, _response);
        MatcherAssert.assertThat("result matches expected", result, CoreMatchers.is(replacement));
        rewritePatternRule.applyURI(_request, null, result);
        MatcherAssert.assertThat("queryString matches expected", _request.getQueryString(), CoreMatchers.is(queryString));
        MatcherAssert.assertThat("request URI matches expected", _request.getRequestURI(), CoreMatchers.is(replacement));
    }

    @Test
    public void testRequestAndReplacementWithQueryString() throws IOException {
        String requestQueryString = "request=parameter";
        String replacement = "/replace?given=param";
        String[] split = replacement.split("\\?", 2);
        String path = split[0];
        String queryString = split[1];
        _request.setURIPathQuery("/old/context");
        _request.setQueryString(requestQueryString);
        RewritePatternRule rewritePatternRule = new RewritePatternRule();
        rewritePatternRule.setPattern("/old/context");
        rewritePatternRule.setReplacement(replacement);
        String result = rewritePatternRule.matchAndApply("/old/context", _request, _response);
        MatcherAssert.assertThat(result, CoreMatchers.is(path));
        rewritePatternRule.applyURI(_request, null, result);
        MatcherAssert.assertThat("queryString matches expected", _request.getQueryString(), CoreMatchers.is(((requestQueryString + "&") + queryString)));
        MatcherAssert.assertThat("request URI matches expected", _request.getRequestURI(), CoreMatchers.is(path));
    }
}

