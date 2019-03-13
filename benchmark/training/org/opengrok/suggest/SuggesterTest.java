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
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.suggest;


import BooleanClause.Occur.MUST;
import Suggester.NamedIndexDir;
import Suggester.NamedIndexReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class SuggesterTest {
    private static class SuggesterTestData {
        private Suggester s;

        private Path indexDir;

        private Path suggesterDir;

        private List<Suggester.NamedIndexReader> namedIndexReaders = new ArrayList<>();

        private void close() throws IOException {
            for (Suggester.NamedIndexReader ir : namedIndexReaders) {
                ir.getReader().close();
            }
            s.close();
            FileUtils.deleteDirectory(indexDir.toFile());
            FileUtils.deleteDirectory(suggesterDir.toFile());
        }

        private NamedIndexDir getNamedIndexDir() {
            return new Suggester.NamedIndexDir("test", indexDir);
        }

        private Directory getIndexDirectory() throws IOException {
            return FSDirectory.open(indexDir);
        }

        private NamedIndexReader getNamedIndexReader() throws IOException {
            Suggester.NamedIndexReader ir = new Suggester.NamedIndexReader("test", DirectoryReader.open(getIndexDirectory()));
            namedIndexReaders.add(ir);
            return ir;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullSuggesterDir() {
        new Suggester(null, 10, Duration.ofMinutes(5), false, true, null, Integer.MAX_VALUE, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDuration() throws IOException {
        Path tempFile = Files.createTempFile("opengrok", "test");
        try {
            new Suggester(tempFile.toFile(), 10, null, false, true, null, Integer.MAX_VALUE, 1);
        } finally {
            tempFile.toFile().delete();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeDuration() throws IOException {
        Path tempFile = Files.createTempFile("opengrok", "test");
        try {
            new Suggester(tempFile.toFile(), 10, Duration.ofMinutes((-4)), false, true, null, Integer.MAX_VALUE, 1);
        } finally {
            tempFile.toFile().delete();
        }
    }

    @Test
    public void testSimpleSuggestions() throws IOException {
        SuggesterTest.SuggesterTestData t = initSuggester();
        Suggester.NamedIndexReader ir = t.getNamedIndexReader();
        List<LookupResultItem> res = t.s.search(Collections.singletonList(ir), new org.opengrok.suggest.query.SuggesterPrefixQuery(new Term("test", "t")), null).getItems();
        MatcherAssert.assertThat(res.stream().map(LookupResultItem::getPhrase).collect(Collectors.toList()), containsInAnyOrder("term1", "term2", "term3"));
        t.close();
    }

    @Test
    public void testRefresh() throws IOException {
        SuggesterTest.SuggesterTestData t = initSuggester();
        addText(t.getIndexDirectory(), "a1 a2");
        t.s.rebuild(Collections.singleton(t.getNamedIndexDir()));
        Suggester.NamedIndexReader ir = t.getNamedIndexReader();
        List<LookupResultItem> res = t.s.search(Collections.singletonList(ir), new org.opengrok.suggest.query.SuggesterPrefixQuery(new Term("test", "a")), null).getItems();
        MatcherAssert.assertThat(res.stream().map(LookupResultItem::getPhrase).collect(Collectors.toList()), containsInAnyOrder("a1", "a2"));
        t.close();
    }

    @Test
    public void testIndexChangedWhileOffline() throws IOException {
        SuggesterTest.SuggesterTestData t = initSuggester();
        t.s.close();
        addText(t.getIndexDirectory(), "a1 a2");
        t.s = new Suggester(t.suggesterDir.toFile(), 10, Duration.ofMinutes(1), false, true, Collections.singleton("test"), Integer.MAX_VALUE, Runtime.getRuntime().availableProcessors());
        t.s.init(Collections.singleton(t.getNamedIndexDir()));
        await().atMost(2, TimeUnit.SECONDS).until(() -> (getSuggesterProjectDataSize(t.s)) == 1);
        Suggester.NamedIndexReader ir = t.getNamedIndexReader();
        List<LookupResultItem> res = t.s.search(Collections.singletonList(ir), new org.opengrok.suggest.query.SuggesterPrefixQuery(new Term("test", "a")), null).getItems();
        MatcherAssert.assertThat(res.stream().map(LookupResultItem::getPhrase).collect(Collectors.toList()), containsInAnyOrder("a1", "a2"));
        t.close();
    }

    @Test
    public void testRemove() throws IOException {
        SuggesterTest.SuggesterTestData t = initSuggester();
        t.s.remove(Collections.singleton("test"));
        Assert.assertFalse(t.suggesterDir.resolve("test").toFile().exists());
        FileUtils.deleteDirectory(t.suggesterDir.toFile());
        FileUtils.deleteDirectory(t.indexDir.toFile());
    }

    @Test
    public void testComplexQuerySearch() throws IOException {
        SuggesterTest.SuggesterTestData t = initSuggester();
        List<LookupResultItem> res = t.s.search(Collections.singletonList(t.getNamedIndexReader()), new org.opengrok.suggest.query.SuggesterWildcardQuery(new Term("test", "*1")), null).getItems();
        MatcherAssert.assertThat(res.stream().map(LookupResultItem::getPhrase).collect(Collectors.toList()), contains("term1"));
        t.close();
    }

    // for contains()
    @Test
    @SuppressWarnings("unchecked")
    public void testOnSearch() throws IOException {
        SuggesterTest.SuggesterTestData t = initSuggester();
        Query q = new BooleanQuery.Builder().add(new org.apache.lucene.search.TermQuery(new Term("test", "term1")), MUST).add(new org.apache.lucene.search.TermQuery(new Term("test", "term3")), MUST).build();
        t.s.onSearch(Collections.singleton("test"), q);
        List<Map.Entry<BytesRef, Integer>> res = t.s.getSearchCounts("test", "test", 0, 10);
        MatcherAssert.assertThat(res, containsInAnyOrder(new java.util.AbstractMap.SimpleEntry(new BytesRef("term1"), 1), new java.util.AbstractMap.SimpleEntry(new BytesRef("term3"), 1)));
        t.close();
    }

    @Test
    public void testGetSearchCountsForUnknown() throws IOException {
        SuggesterTest.SuggesterTestData t = initSuggester();
        Assert.assertTrue(t.s.getSearchCounts("unknown", "unknown", 0, 10).isEmpty());
        t.close();
    }

    // for contains()
    @Test
    @SuppressWarnings("unchecked")
    public void testIncreaseSearchCount() throws IOException {
        SuggesterTest.SuggesterTestData t = initSuggester();
        t.s.increaseSearchCount("test", new Term("test", "term2"), 100);
        List<Map.Entry<BytesRef, Integer>> res = t.s.getSearchCounts("test", "test", 0, 10);
        MatcherAssert.assertThat(res, contains(new java.util.AbstractMap.SimpleEntry(new BytesRef("term2"), 100)));
        t.close();
    }
}

