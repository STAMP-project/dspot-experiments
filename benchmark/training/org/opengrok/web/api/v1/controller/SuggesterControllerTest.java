/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.web.api.v1.controller;


import AuthorizationFilter.PROJECTS_PARAM;
import QueryBuilder.DEFS;
import QueryBuilder.FULL;
import QueryBuilder.REFS;
import Response.Status.BAD_REQUEST;
import Response.Status.NOT_FOUND;
import Response.Status.NO_CONTENT;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.configuration.SuggesterConfig;
import org.opengrok.indexer.search.QueryBuilder;
import org.opengrok.indexer.util.TestRepository;
import org.opengrok.web.api.v1.suggester.provider.service.impl.SuggesterServiceImpl;


@ConditionalRun(CtagsInstalled.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SuggesterControllerTest extends JerseyTest {
    public static class Result {
        public long time;

        public List<SuggesterControllerTest.ResultItem> suggestions;

        public String identifier;

        public String queryText;

        public boolean partialResult;
    }

    public static class ResultItem {
        public String phrase;

        public Set<String> projects;

        public long score;
    }

    private static class TermIncrementData {
        public String project;

        public String field;

        public String token;

        public int increment;
    }

    @ClassRule
    public static ConditionalRunRule rule = new ConditionalRunRule();

    private static final RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    private static final GenericType<List<Map.Entry<String, Integer>>> popularityDataType = new GenericType<List<Map.Entry<String, Integer>>>() {};

    private static TestRepository repository;

    @Test
    public void testGetSuggesterConfig() {
        SuggesterConfig config = target(SuggesterController.PATH).path("config").request().get(SuggesterConfig.class);
        Assert.assertEquals(SuggesterControllerTest.env.getSuggesterConfig(), config);
    }

    @Test
    public void testGetSuggestionsSimpleFull() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", FULL).queryParam(FULL, "inner").request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), Matchers.containsInAnyOrder("innermethod", "innerclass"));
    }

    @Test
    public void testGetSuggestionsSimpleDefs() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", DEFS).queryParam(DEFS, "Inner").request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), Matchers.containsInAnyOrder("InnerMethod", "InnerClass"));
    }

    @Test
    public void testGetSuggestionsSimpleRefs() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", REFS).queryParam(REFS, "Inner").request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), Matchers.containsInAnyOrder("InnerMethod", "InnerClass"));
    }

    @Test
    public void testGetSuggestionsSimplePath() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "c").queryParam("field", QueryBuilder.PATH).queryParam(QueryBuilder.PATH, "he").request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), contains("header"));
    }

    @Test
    public void testGetSuggestionsBadRequest() {
        Response r = target(SuggesterController.PATH).queryParam("field", "").request().get();
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void testGetSuggestionsBadRequest2() {
        Response r = target(SuggesterController.PATH).queryParam("field", FULL).queryParam("caret", (-2)).request().get();
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void testGetSuggestionUnknownField() {
        Response r = target(SuggesterController.PATH).queryParam("field", "unknown").request().get();
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void testGetSuggestionsMultipleProjects2() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java", "kotlin").queryParam("field", FULL).queryParam(FULL, "mai").request().get(SuggesterControllerTest.Result.class);
        Assert.assertEquals(1, res.suggestions.size());
        Assert.assertThat(res.suggestions.get(0).projects, Matchers.containsInAnyOrder("java", "kotlin"));
    }

    @Test
    public void testComplexSuggestions() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", FULL).queryParam(FULL, "s").queryParam(QueryBuilder.PATH, "bug15890").request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), Matchers.containsInAnyOrder("since"));
    }

    @Test
    public void testWildcard() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", FULL).queryParam(FULL, "b?").queryParam(QueryBuilder.PATH, "sample").request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), contains("by"));
    }

    @Test
    public void testRegex() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", FULL).queryParam(FULL, "/b./").queryParam(QueryBuilder.PATH, "sample").queryParam("caret", 1).request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), contains("by"));
    }

    @Test
    public void testPhraseAfter() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", FULL).queryParam(FULL, "\"contents of this \"").queryParam(QueryBuilder.PATH, "sample").queryParam("caret", 18).request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), contains("file"));
    }

    @Test
    public void testPhraseBefore() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", FULL).queryParam(FULL, "\" contents of this\"").queryParam(QueryBuilder.PATH, "sample").queryParam("caret", 1).request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), contains("the"));
    }

    @Test
    public void testPhraseMiddle() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", FULL).queryParam(FULL, "\"contents  this\"").queryParam(QueryBuilder.PATH, "sample").queryParam("caret", 10).request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), contains("of"));
    }

    @Test
    public void testSloppyPhrase() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", FULL).queryParam(FULL, "\"contents of this \"~1").queryParam(QueryBuilder.PATH, "sample").queryParam("caret", 18).request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), Matchers.containsInAnyOrder("file", "are"));
    }

    @Test
    public void testRangeQueryUpper() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "kotlin").queryParam("field", FULL).queryParam(FULL, "{templ}").resolveTemplate("templ", "{main TO m}").queryParam("caret", 10).request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), Matchers.containsInAnyOrder("me", "mutablelistof"));
    }

    @Test
    public void testRangeQueryLower() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "kotlin").queryParam("field", FULL).queryParam(FULL, "{templ}").resolveTemplate("templ", "{m TO mutablelistof}").queryParam("caret", 1).request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), Matchers.containsInAnyOrder("main", "me"));
    }

    @Test
    public void testComplexSuggestions2() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "kotlin").queryParam("field", FULL).queryParam(FULL, "me m").queryParam("caret", 4).request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), Matchers.containsInAnyOrder("main", "mutablelistof"));
    }

    @Test
    public void testInitPopularTermsFromQueries() {
        // terms for prefix t: "text", "texttrim", "tell", "teach", "trimmargin"
        List<String> queries = Arrays.asList("http://localhost:8080/source/search?project=kotlin&full=text", "http://localhost:8080/source/search?project=kotlin&full=text", "http://localhost:8080/source/search?project=kotlin&full=teach");
        target(SuggesterController.PATH).path("init").path("queries").request().post(Entity.json(queries));
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "kotlin").queryParam("field", FULL).queryParam(FULL, "t").queryParam("caret", 1).queryParam(QueryBuilder.PATH, "kt").request().get(SuggesterControllerTest.Result.class);
        List<String> suggestions = res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList());
        Assert.assertEquals("text", suggestions.get(0));
        Assert.assertEquals("teach", suggestions.get(1));
    }

    @Test
    public void testInitPopularTermsFromRawData() {
        // terms for prefix a: "args", "array", "and"
        SuggesterControllerTest.TermIncrementData data1 = new SuggesterControllerTest.TermIncrementData();
        data1.project = "kotlin";
        data1.field = QueryBuilder.FULL;
        data1.token = "args";
        data1.increment = 100;
        SuggesterControllerTest.TermIncrementData data2 = new SuggesterControllerTest.TermIncrementData();
        data2.project = "kotlin";
        data2.field = QueryBuilder.FULL;
        data2.token = "array";
        data2.increment = 50;
        target(SuggesterController.PATH).path("init").path("raw").request().post(Entity.json(Arrays.asList(data1, data2)));
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "kotlin").queryParam("field", FULL).queryParam(FULL, "a").queryParam("caret", 1).queryParam(QueryBuilder.PATH, "kt").request().get(SuggesterControllerTest.Result.class);
        List<String> suggestions = res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList());
        Assert.assertEquals("args", suggestions.get(0));
        Assert.assertEquals("array", suggestions.get(1));
    }

    @Test
    public void testInitPopularTermsFromRawDataInvalidRequest() {
        SuggesterControllerTest.TermIncrementData data = new SuggesterControllerTest.TermIncrementData();
        data.project = "kotlin";
        data.field = QueryBuilder.FULL;
        data.token = "array";
        data.increment = -10;
        Response r = target(SuggesterController.PATH).path("init").path("raw").request().post(Entity.json(Collections.singleton(data)));
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), r.getStatus());
    }

    @Test
    public void testDisabledSuggestions() {
        SuggesterControllerTest.env.getSuggesterConfig().setEnabled(false);
        Response r = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", FULL).queryParam(FULL, "inner").request().get();
        Assert.assertEquals(NOT_FOUND.getStatusCode(), r.getStatus());
    }

    @Test
    public void testMinChars() {
        SuggesterControllerTest.env.getSuggesterConfig().setMinChars(2);
        Response r = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java").queryParam("field", FULL).queryParam(FULL, "i").request().get();
        Assert.assertEquals(NOT_FOUND.getStatusCode(), r.getStatus());
    }

    @Test
    public void testAllowedProjects() {
        SuggesterControllerTest.env.getSuggesterConfig().setAllowedProjects(Collections.singleton("kotlin"));
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java", "kotlin").queryParam("field", FULL).queryParam(FULL, "me").request().get(SuggesterControllerTest.Result.class);
        // only terms from kotlin project are expected
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), Matchers.containsInAnyOrder("me"));
    }

    @Test
    public void testMaxProjects() {
        SuggesterControllerTest.env.getSuggesterConfig().setMaxProjects(1);
        Response r = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java", "kotlin").queryParam("field", FULL).queryParam(FULL, "me").request().get();
        Assert.assertEquals(NOT_FOUND.getStatusCode(), r.getStatus());
    }

    @Test
    public void testAllowedFields() {
        SuggesterControllerTest.env.getSuggesterConfig().setAllowedFields(Collections.singleton(DEFS));
        Response r = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java", "kotlin").queryParam("field", FULL).queryParam(FULL, "me").request().get();
        Assert.assertEquals(NOT_FOUND.getStatusCode(), r.getStatus());
    }

    @Test
    public void testAllowComplexQueries() {
        SuggesterControllerTest.env.getSuggesterConfig().setAllowComplexQueries(false);
        Response r = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "java", "kotlin").queryParam("field", FULL).queryParam(FULL, "me").queryParam(QueryBuilder.PATH, "kt").request().get();
        Assert.assertEquals(NOT_FOUND.getStatusCode(), r.getStatus());
    }

    // for contains
    @Test
    @SuppressWarnings("unchecked")
    public void testGetPopularityDataSimple() {
        SuggesterServiceImpl.getInstance().increaseSearchCount("rust", new org.apache.lucene.index.Term(QueryBuilder.FULL, "main"), 10);
        List<Map.Entry<String, Integer>> res = target(SuggesterController.PATH).path("popularity").path("rust").request().get(SuggesterControllerTest.popularityDataType);
        Assert.assertThat(res, contains(new AbstractMap.SimpleEntry("main", 10)));
    }

    // for contains
    @Test
    @SuppressWarnings("unchecked")
    public void testGetPopularityDataAll() {
        SuggesterServiceImpl.getInstance().increaseSearchCount("csharp", new org.apache.lucene.index.Term(QueryBuilder.FULL, "mynamespace"), 10);
        SuggesterServiceImpl.getInstance().increaseSearchCount("csharp", new org.apache.lucene.index.Term(QueryBuilder.FULL, "topclass"), 15);
        List<Map.Entry<String, Integer>> res = target(SuggesterController.PATH).path("popularity").path("csharp").queryParam("pageSize", 1).queryParam("all", true).request().get(SuggesterControllerTest.popularityDataType);
        Assert.assertThat(res, contains(new AbstractMap.SimpleEntry("topclass", 15), new AbstractMap.SimpleEntry("mynamespace", 10)));
    }

    // for contains
    @Test
    @SuppressWarnings("unchecked")
    public void testGetPopularityDataDifferentField() {
        SuggesterServiceImpl.getInstance().increaseSearchCount("swift", new org.apache.lucene.index.Term(QueryBuilder.FULL, "print"), 10);
        SuggesterServiceImpl.getInstance().increaseSearchCount("swift", new org.apache.lucene.index.Term(QueryBuilder.DEFS, "greet"), 4);
        List<Map.Entry<String, Integer>> res = target(SuggesterController.PATH).path("popularity").path("swift").queryParam("field", DEFS).request().get(SuggesterControllerTest.popularityDataType);
        Assert.assertThat(res, contains(new AbstractMap.SimpleEntry("greet", 4)));
    }

    @Test
    public void testWildcardQueryEndingWithAsterisk() {
        SuggesterControllerTest.Result res = target(SuggesterController.PATH).queryParam(PROJECTS_PARAM, "c").queryParam("field", FULL).queryParam(FULL, "pr?nt*").request().get(SuggesterControllerTest.Result.class);
        Assert.assertThat(res.suggestions.stream().map(( r) -> r.phrase).collect(Collectors.toList()), Matchers.containsInAnyOrder("print", "printf"));
    }

    @Test
    public void ZtestRebuild() {
        Response res = target(SuggesterController.PATH).path("rebuild").request().put(Entity.text(""));
        Assert.assertEquals(NO_CONTENT.getStatusCode(), res.getStatus());
    }

    @Test
    public void ZtestRebuildProject() {
        Response res = target(SuggesterController.PATH).path("rebuild").path("c").request().put(Entity.text(""));
        Assert.assertEquals(NO_CONTENT.getStatusCode(), res.getStatus());
    }
}

