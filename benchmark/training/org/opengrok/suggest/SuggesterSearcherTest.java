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
package org.opengrok.suggest;


import SuggesterRangeQuery.SuggestPosition;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.opengrok.suggest.query.SuggesterPhraseQuery;
import org.opengrok.suggest.query.SuggesterRangeQuery;


public class SuggesterSearcherTest {
    private static Directory dir;

    private static SuggesterSearcher searcher;

    @Test
    public void suggesterPrefixQueryTest() {
        List<LookupResultItem> suggestions = SuggesterSearcherTest.searcher.suggest(new org.apache.lucene.search.TermQuery(new Term("test", "test")), "test", new org.opengrok.suggest.query.SuggesterPrefixQuery(new Term("test", "o")), ( k) -> 0);
        List<String> tokens = suggestions.stream().map(LookupResultItem::getPhrase).collect(Collectors.toList());
        MatcherAssert.assertThat(tokens, Matchers.contains("opengrok"));
    }

    @Test
    public void suggesterWildcardQueryTest() {
        List<LookupResultItem> suggestions = SuggesterSearcherTest.searcher.suggest(new org.apache.lucene.search.TermQuery(new Term("test", "test")), "test", new org.opengrok.suggest.query.SuggesterWildcardQuery(new Term("test", "?pengrok")), ( k) -> 0);
        List<String> tokens = suggestions.stream().map(LookupResultItem::getPhrase).collect(Collectors.toList());
        MatcherAssert.assertThat(tokens, Matchers.contains("opengrok"));
    }

    @Test
    public void suggesterRegexpQueryTest() {
        List<LookupResultItem> suggestions = SuggesterSearcherTest.searcher.suggest(new org.apache.lucene.search.TermQuery(new Term("test", "test")), "test", new org.opengrok.suggest.query.SuggesterRegexpQuery(new Term("test", ".pengrok")), ( k) -> 0);
        List<String> tokens = suggestions.stream().map(LookupResultItem::getPhrase).collect(Collectors.toList());
        MatcherAssert.assertThat(tokens, Matchers.contains("opengrok"));
    }

    @Test
    public void suggesterFuzzyQueryTest() {
        List<LookupResultItem> suggestions = SuggesterSearcherTest.searcher.suggest(new org.apache.lucene.search.TermQuery(new Term("test", "test")), "test", new org.opengrok.suggest.query.SuggesterFuzzyQuery(new Term("test", "opengroc"), 1, 0), ( k) -> 0);
        List<String> tokens = suggestions.stream().map(LookupResultItem::getPhrase).collect(Collectors.toList());
        MatcherAssert.assertThat(tokens, Matchers.contains("opengrok"));
    }

    @Test
    public void suggesterPhraseQueryTest() {
        SuggesterPhraseQuery q = new SuggesterPhraseQuery("test", "abc", Arrays.asList("opengrok", "openabc"), 0);
        List<LookupResultItem> suggestions = SuggesterSearcherTest.searcher.suggest(q.getPhraseQuery(), "test", q.getSuggesterQuery(), ( k) -> 0);
        List<String> tokens = suggestions.stream().map(LookupResultItem::getPhrase).collect(Collectors.toList());
        MatcherAssert.assertThat(tokens, Matchers.contains("opengrok2"));
    }

    @Test
    public void testRangeQueryUpper() {
        SuggesterRangeQuery q = new SuggesterRangeQuery("test", new BytesRef("opengrok"), new BytesRef("t"), true, true, SuggestPosition.UPPER);
        List<LookupResultItem> suggestions = SuggesterSearcherTest.searcher.suggest(null, "test", q, ( k) -> 0);
        List<String> tokens = suggestions.stream().map(LookupResultItem::getPhrase).collect(Collectors.toList());
        MatcherAssert.assertThat(tokens, Matchers.contains("test"));
    }

    @Test
    public void testRangeQueryLower() {
        SuggesterRangeQuery q = new SuggesterRangeQuery("test", new BytesRef("o"), new BytesRef("test"), true, true, SuggestPosition.LOWER);
        List<LookupResultItem> suggestions = SuggesterSearcherTest.searcher.suggest(null, "test", q, ( k) -> 0);
        List<String> tokens = suggestions.stream().map(LookupResultItem::getPhrase).collect(Collectors.toList());
        MatcherAssert.assertThat(tokens, Matchers.contains("opengrok", "opengrok2"));
    }
}

