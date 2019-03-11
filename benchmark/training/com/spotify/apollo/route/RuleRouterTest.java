/**
 * -\-\-
 * Spotify Apollo Route
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo.route;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.spotify.apollo.Request;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class RuleRouterTest {
    private static final int TARGET = 7;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testMatchSuccess() {
        Rule<Integer> rule = Rule.fromUri("/bar/<baz>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bar/FRED");
        Assert.assertThat(rule.getTarget(), CoreMatchers.is(RuleRouterTest.TARGET));
        Assert.assertTrue(match.isPresent());
        RuleMatch<Integer> matcher = match.get();
        String extracted = matcher.extract(0);
        Assert.assertThat(extracted, CoreMatchers.is("FRED"));
    }

    @Test
    public void testMatchMultiple() {
        Rule<Integer> rule = Rule.fromUri("/<bar>/<baz>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/FRED/BARNEY");
        Assert.assertTrue(match.isPresent());
        Assert.assertThat(rule.getExtractionCount(), CoreMatchers.is(2));
        RuleMatch<Integer> matcher = match.get();
        String extracted0 = matcher.extract(0);
        String extracted1 = matcher.extract(1);
        Assert.assertThat(extracted0, CoreMatchers.is("FRED"));
        Assert.assertThat(extracted1, CoreMatchers.is("BARNEY"));
    }

    @Test
    public void testGetExtractionCount() {
        Rule<Integer> rule = Rule.fromUri("/bar/<baz>", "GET", RuleRouterTest.TARGET);
        Assert.assertThat(rule.getExtractionCount(), CoreMatchers.is(1));
    }

    @Test
    public void testNoArgs() {
        Rule<Integer> rule = Rule.fromUri("/", "GET", RuleRouterTest.TARGET);
        Assert.assertThat(rule.getExtractionCount(), CoreMatchers.is(0));
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/");
        Assert.assertTrue(match.isPresent());
    }

    @Test
    public void testNoArgsNoMatch() {
        Rule<Integer> rule = Rule.fromUri("/", "GET", RuleRouterTest.TARGET);
        Assert.assertThat(rule.getExtractionCount(), CoreMatchers.is(0));
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/something");
        Assert.assertFalse(match.isPresent());
    }

    @Test
    public void testMatchFail() {
        Rule<Integer> rule = Rule.fromUri("/bar/<baz>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bary/FRED");
        Assert.assertFalse(match.isPresent());
    }

    @Test
    public void testMatchTrailingSlash() {
        Rule<Integer> rule = Rule.fromUri("/bar/<baz>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bar/FRED/");
        Assert.assertTrue(match.isPresent());
    }

    @Test
    public void testMatchTrailingSlashInTemplate() {
        Rule<Integer> rule = Rule.fromUri("/bar/<baz>/", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bar/FRED");
        Assert.assertTrue(match.isPresent());
    }

    @Test
    public void testDecodes() {
        Rule<Integer> rule = Rule.fromUri("/bar/<baz>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bar/FR%20ED/");
        Assert.assertTrue(match.isPresent());
        Assert.assertThat(match.get().extract(0), CoreMatchers.is("FR ED"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArgumentsWithSameNameThrows() {
        Rule.fromUri("/<bar>/<bar>", "GET", RuleRouterTest.TARGET);
    }

    @Test
    public void testHasArgumentsNames() {
        Rule<Integer> rule = Rule.fromUri("/<d>/<c>/<b>/<a>/<quax:path>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/a/b/c/d/and/some/more");
        Assert.assertTrue(match.isPresent());
        Assert.assertThat(match.get().getRule().getExtractionCount(), CoreMatchers.is(5));
        Assert.assertThat(match.get().parsedPathArguments().get("d"), CoreMatchers.is("a"));
        Assert.assertThat(match.get().parsedPathArguments().get("c"), CoreMatchers.is("b"));
        Assert.assertThat(match.get().parsedPathArguments().get("b"), CoreMatchers.is("c"));
        Assert.assertThat(match.get().parsedPathArguments().get("a"), CoreMatchers.is("d"));
        Assert.assertThat(match.get().parsedPathArguments().get("quax"), CoreMatchers.is("and/some/more"));
    }

    @Test
    public void testPreservesOrder() {
        Rule<Integer> rule = Rule.fromUri("/<d>/<c>/<b>/<a>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/a/b/c/d");
        Assert.assertTrue(match.isPresent());
        Assert.assertThat(match.get().getRule().getExtractionCount(), CoreMatchers.is(4));
        Assert.assertThat(match.get().extract(0), CoreMatchers.is("a"));
        Assert.assertThat(match.get().extract(1), CoreMatchers.is("b"));
        Assert.assertThat(match.get().extract(2), CoreMatchers.is("c"));
        Assert.assertThat(match.get().extract(3), CoreMatchers.is("d"));
    }

    @Test
    public void testMatchPath() {
        Rule<Integer> rule = Rule.fromUri("/bar/<rest:path>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bar/and/some/longer/path");
        Assert.assertTrue(match.isPresent());
        Assert.assertThat(match.get().extract(0), CoreMatchers.is("and/some/longer/path"));
    }

    @Test
    public void testMatchPathDoesNotDecode() {
        Rule<Integer> rule = Rule.fromUri("/bar/<rest:path>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bar/and/some%20path/decoded");
        Assert.assertTrue(match.isPresent());
        Assert.assertThat(match.get().extract(0), CoreMatchers.is("and/some%20path/decoded"));
    }

    @Test
    public void testMatchPathPlain() {
        Rule<Integer> rule = Rule.fromUri("/<rest:path>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/foo/bar/and/some/longer/path");
        Assert.assertTrue(match.isPresent());
        Assert.assertThat(match.get().extract(0), CoreMatchers.is("foo/bar/and/some/longer/path"));
    }

    @Test
    public void testDoesNotMatchPartialPath() {
        Rule<Integer> rule = Rule.fromUri("/some/path/<rest:path>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/some");
        Assert.assertFalse(match.isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPathSegmentMustBeLast() {
        Rule.fromUri("/bar/<rest:path>/more", "GET", RuleRouterTest.TARGET);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPathSegmentCanNotHaveSuffix() {
        Rule.fromUri("/bar/<rest:path>err", "GET", RuleRouterTest.TARGET);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPathSegmentCanNotHavePrefixOrSuffix() {
        Rule.fromUri("/bar/err<rest:path>err", "GET", RuleRouterTest.TARGET);
    }

    @Test
    public void testMatchesEncoded() {
        Rule<Integer> rule = Rule.fromUri("/<bar>/baz", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/FR%2FED/baz");
        Assert.assertTrue(match.isPresent());
        final String extract = match.get().extract(0);
        Assert.assertThat(extract, CoreMatchers.is("FR/ED"));
    }

    @Test
    public void testMatchesEncodedUnicode() {
        Rule<Integer> rule = Rule.fromUri("/<bar>/baz", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/FR%2FE%e2%98%83D/baz");
        Assert.assertTrue(match.isPresent());
        final String extract = match.get().extract(0);
        Assert.assertThat(extract, CoreMatchers.is("FR/E?D"));
    }

    @Test
    public void testMatchPathWithPartialSegmentPatternSuffix() {
        Rule<Integer> rule = Rule.fromUri("/bar/<baz>.json", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bar/FRED.json");
        Assert.assertTrue(match.isPresent());
        final String extract = match.get().extract(0);
        Assert.assertThat(extract, CoreMatchers.is("FRED"));
    }

    @Test
    public void testMatchPathWithPartialSegmentPatternPrefix() {
        Rule<Integer> rule = Rule.fromUri("/bar/al<baz>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bar/alFRED");
        Assert.assertTrue(match.isPresent());
        final String extract = match.get().extract(0);
        Assert.assertThat(extract, CoreMatchers.is("FRED"));
    }

    @Test
    public void testMatchPathWithPartialSegmentPatternOnBothSides() {
        Rule<Integer> rule = Rule.fromUri("/bar/al<baz>.json", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bar/alFRED.json");
        Assert.assertTrue(match.isPresent());
        final String extract = match.get().extract(0);
        Assert.assertThat(extract, CoreMatchers.is("FRED"));
    }

    @Test
    public void testMatchPathWithPartialSegmentPatternFaild() {
        Rule<Integer> rule = Rule.fromUri("/bar/al<baz>.json", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bar/aFRED.jso");
        Assert.assertFalse(match.isPresent());
    }

    @Test
    public void testMatchPathWithPartialSegmentPatternWithUnicode() {
        Rule<Integer> rule = Rule.fromUri("/bar/al<baz>.json", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/bar/alFR%e2%98%83ED.json");
        Assert.assertTrue(match.isPresent());
        final String extract = match.get().extract(0);
        Assert.assertThat(extract, CoreMatchers.is("FR?ED"));
    }

    @Test
    public void testMatchFailOnWrongMethod() {
        Rule<Integer> rule = Rule.fromUri("/foo", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "POST", "/foo");
        Assert.assertFalse(match.isPresent());
    }

    @Test
    public void shouldAllowDoubleSlashesInPath() throws Exception {
        Rule<Integer> rule = Rule.fromUri("/foo/bar/<rest:path>", "GET", RuleRouterTest.TARGET);
        Optional<RuleMatch<Integer>> match = RuleRouterTest.route(rule, "GET", "/foo/bar//path//double//slashes");
        Assert.assertTrue(match.isPresent());
        Assert.assertThat(match.get().extract(0), CoreMatchers.is("/path//double//slashes"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyUnicodeInUriThrows() {
        Rule.fromUri("/fo?o/<bar>/baz", "GET", RuleRouterTest.TARGET);
    }

    @Test
    public void shouldReturnEmptyCollectionOnNonRoute() throws Exception {
        Rule<Integer> rule = Rule.fromUri("/foo/bar", "GET", RuleRouterTest.TARGET);
        final RuleRouter<Integer> router = RuleRouter.of(ImmutableList.of(rule));
        final Request message = Request.forUri("/foo/notbar", "GET");
        final Collection<String> methodsForValidRules = router.getMethodsForValidRules(message);
        Assert.assertTrue(methodsForValidRules.isEmpty());
    }

    @Test
    public void shouldReturnMethodsOnValidRoute() throws Exception {
        Rule<Integer> rule = Rule.fromUri("/foo/bar", "GET", RuleRouterTest.TARGET);
        final RuleRouter<Integer> router = RuleRouter.of(ImmutableList.of(rule));
        final Request message = Request.forUri("/foo/bar", "POST");
        final Collection<String> methodsForValidRules = router.getMethodsForValidRules(message);
        Assert.assertThat(methodsForValidRules, CoreMatchers.hasItem("GET"));
    }

    @Test
    public void shouldReturnAllConfiguredTargets() throws Exception {
        List<Rule<String>> rules = ImmutableList.of(Rule.fromUri("/foo/bar", "POST", "hi"), Rule.fromUri("/foo/bar", "GET", "ho"));
        final RuleRouter<String> router = RuleRouter.of(rules);
        Assert.assertThat(router.getRuleTargets(), CoreMatchers.equalTo(Lists.transform(rules, ( rule) -> rule.getTarget())));
    }

    @Test
    public void shouldThrowInvalidUriExceptionForBadParameterEncoding() throws Exception {
        Rule<Integer> rule = Rule.fromUri("/bar/<baz>", "GET", RuleRouterTest.TARGET);
        final RuleRouter<Integer> router = RuleRouter.of(ImmutableList.of(rule));
        final Request message = Request.forUri("/bar/c%F6", "GET");
        thrown.expect(InvalidUriException.class);
        router.match(message);
    }

    @Test
    public void shouldThrowInvalidUriExceptionForBadPlaylistMosaicUri() throws Exception {
        Rule<Integer> rule = Rule.fromUri("/user/<baz>/playlist/<boo>", "GET", RuleRouterTest.TARGET);
        final RuleRouter<Integer> router = RuleRouter.of(ImmutableList.of(rule));
        final Request message = Request.forUri("/user/%c3%a8hYh%ae%8d%c9%21%d4A%c4%bd8+7/playlist/56S6B5rXio3Q2MQltr7ZJk", "GET");
        thrown.expect(InvalidUriException.class);
        router.match(message);
    }
}

