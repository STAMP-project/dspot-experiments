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


import Field.Store;
import Lookup.LookupResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SuggesterProjectDataTest {
    private static final String FIELD = "test";

    private Directory dir;

    private Path tempDir;

    private SuggesterProjectData data;

    @Test
    public void testLookup() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "term1 term2");
        init(false);
        List<String> suggestions = getSuggestions(SuggesterProjectDataTest.FIELD, "t", 10);
        MatcherAssert.assertThat(suggestions, Matchers.containsInAnyOrder("term1", "term2"));
        data.close();
    }

    @Test
    public void testMultipleTermsAreFirstByDefault() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "term1 term2 term1");
        init(false);
        List<String> suggestions = getSuggestions(SuggesterProjectDataTest.FIELD, "t", 10);
        MatcherAssert.assertThat(suggestions, Matchers.contains("term1", "term2"));
    }

    @Test
    public void testMostPopularSearch() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "term1 term2 term1");
        init(true);
        data.incrementSearchCount(new Term(SuggesterProjectDataTest.FIELD, "term2"));
        data.incrementSearchCount(new Term(SuggesterProjectDataTest.FIELD, "term2"));
        data.rebuild();
        List<String> suggestions = getSuggestions(SuggesterProjectDataTest.FIELD, "t", 10);
        MatcherAssert.assertThat(suggestions, Matchers.contains("term2", "term1"));
    }

    @Test
    public void testRebuild() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "term1 term2 term1");
        init(false);
        try (IndexWriter iw = new IndexWriter(dir, new IndexWriterConfig())) {
            iw.deleteAll();
        }
        addText(SuggesterProjectDataTest.FIELD, "term3 term4 term5");
        data.rebuild();
        List<String> suggestions = getSuggestions(SuggesterProjectDataTest.FIELD, "t", 10);
        MatcherAssert.assertThat(suggestions, Matchers.containsInAnyOrder("term3", "term4", "term5"));
    }

    @Test
    public void testDifferentPrefixes() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "abc bbc cbc dbc efc gfc");
        init(false);
        List<String> suggestions = getSuggestions(SuggesterProjectDataTest.FIELD, "e", 10);
        MatcherAssert.assertThat(suggestions, Matchers.contains("efc"));
    }

    @Test
    public void testResultSize() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "a1 a2 a3 a4 a5 a6 a7");
        init(false);
        List<String> suggestions = getSuggestions(SuggesterProjectDataTest.FIELD, "a", 2);
        TestCase.assertEquals(2, suggestions.size());
    }

    @Test
    public void incrementTest() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "text");
        init(true);
        data.incrementSearchCount(new Term(SuggesterProjectDataTest.FIELD, "text"));
        TestCase.assertEquals(1, data.getSearchCounts(SuggesterProjectDataTest.FIELD).get(new BytesRef("text")));
    }

    @Test
    public void incrementByValueTest() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "some text");
        init(true);
        data.incrementSearchCount(new Term(SuggesterProjectDataTest.FIELD, "some"), 20);
        TestCase.assertEquals(20, data.getSearchCounts(SuggesterProjectDataTest.FIELD).get(new BytesRef("some")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void incrementByNegativeValueTest() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "another text example");
        init(true);
        data.incrementSearchCount(new Term(SuggesterProjectDataTest.FIELD, "example"), (-10));
    }

    @Test
    public void rebuildRemoveOldTermsTest() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "term");
        init(true);
        data.incrementSearchCount(new Term(SuggesterProjectDataTest.FIELD, "term"), 10);
        try (IndexWriter iw = new IndexWriter(dir, new IndexWriterConfig())) {
            iw.deleteAll();
        }
        addText(SuggesterProjectDataTest.FIELD, "term2");
        data.rebuild();
        TestCase.assertEquals(0, data.getSearchCounts(SuggesterProjectDataTest.FIELD).get(new BytesRef("term")));
    }

    @Test
    public void initAfterChangingIndexTest() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "term");
        init(false);
        addText(SuggesterProjectDataTest.FIELD, "term2");
        data.init();
        MatcherAssert.assertThat(getSuggestions(SuggesterProjectDataTest.FIELD, "t", 2), containsInAnyOrder("term", "term2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void incrementSearchCountNullTest() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "term");
        init(false);
        data.incrementSearchCount(null);
    }

    @Test
    public void getSearchCountMapNullTest() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "term");
        init(true);
        data.getSearchCounts(null);
    }

    @Test
    public void testRemove() throws IOException {
        Directory dir = new ByteBuffersDirectory();
        Path tempDir = Files.createTempDirectory("test");
        try (IndexWriter iw = new IndexWriter(dir, new IndexWriterConfig())) {
            Document doc = new Document();
            doc.add(new org.apache.lucene.document.TextField("test", "text", Store.NO));
            iw.addDocument(doc);
        }
        SuggesterProjectData data = new SuggesterProjectData(dir, tempDir, false, Collections.singleton("test"));
        data.init();
        data.remove();
        TestCase.assertFalse(tempDir.toFile().exists());
    }

    @Test
    public void testUnknownFieldIgnored() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "term");
        data = new SuggesterProjectData(dir, tempDir, false, new HashSet(Arrays.asList(SuggesterProjectDataTest.FIELD, "unknown")));
        data.init();
        List<Lookup.LookupResult> res = data.lookup("unknown", "a", 10);
        Assert.assertTrue(res.isEmpty());
    }

    // for contains()
    @Test
    @SuppressWarnings("unchecked")
    public void testGetSearchCountMapSorted() throws IOException {
        addText(SuggesterProjectDataTest.FIELD, "test1 test2");
        init(true);
        Term t1 = new Term(SuggesterProjectDataTest.FIELD, "test1");
        Term t2 = new Term(SuggesterProjectDataTest.FIELD, "test2");
        data.incrementSearchCount(t1, 10);
        data.incrementSearchCount(t2, 5);
        List<Map.Entry<BytesRef, Integer>> searchCounts = data.getSearchCountsSorted(SuggesterProjectDataTest.FIELD, 0, 10);
        MatcherAssert.assertThat(searchCounts, contains(new java.util.AbstractMap.SimpleEntry(t1.bytes(), 10), new java.util.AbstractMap.SimpleEntry(t2.bytes(), 5)));
    }
}

