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
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.search.context;


import BooleanQuery.Builder;
import Occur.MUST;
import Occur.SHOULD;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TermQuery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.RepositoryInstalled;
import org.opengrok.indexer.search.Hit;
import org.opengrok.indexer.util.TestRepository;


/**
 * Unit tests for the {@code HistoryContext} class.
 */
public class HistoryContextTest {
    private static TestRepository repositories;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    @Test
    public void testIsEmpty() {
        TermQuery q1 = new TermQuery(new Term("refs", "isEmpty"));
        TermQuery q2 = new TermQuery(new Term("defs", "isEmpty"));
        TermQuery q3 = new TermQuery(new Term("hist", "isEmpty"));
        BooleanQuery.Builder q4 = new BooleanQuery.Builder();
        q4.add(q1, MUST);
        q4.add(q2, MUST);
        BooleanQuery.Builder q5 = new BooleanQuery.Builder();
        q5.add(q2, MUST);
        q5.add(q3, MUST);
        // The queries that don't contain a "hist" term are considered empty.
        Assert.assertTrue(isEmpty());
        Assert.assertTrue(isEmpty());
        Assert.assertFalse(isEmpty());
        Assert.assertTrue(isEmpty());
        Assert.assertFalse(isEmpty());
    }

    @Test
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    public void testGetContext_3args() throws Exception {
        String path = "/mercurial/Makefile";
        String filename = (HistoryContextTest.repositories.getSourceRoot()) + path;
        // Construct a query equivalent to hist:dummy
        TermQuery q1 = new TermQuery(new Term("hist", "dummy"));
        ArrayList<Hit> hits = new ArrayList<>();
        boolean gotCtx = new HistoryContext(q1).getContext(filename, path, hits);
        Assert.assertTrue(gotCtx);
        Assert.assertEquals(1, hits.size());
        Assert.assertTrue(hits.get(0).getLine().contains("Created a small <b>dummy</b> program"));
        // Construct a query equivalent to hist:"dummy program"
        PhraseQuery.Builder q2 = new PhraseQuery.Builder();
        q2.add(new Term("hist", "dummy"));
        q2.add(new Term("hist", "program"));
        hits.clear();
        gotCtx = new HistoryContext(q2.build()).getContext(filename, path, hits);
        Assert.assertTrue(gotCtx);
        Assert.assertEquals(1, hits.size());
        Assert.assertTrue(hits.get(0).getLine().contains("Created a small <b>dummy program</b>"));
        // Search for a term that doesn't exist
        TermQuery q3 = new TermQuery(new Term("hist", "term_does_not_exist"));
        hits.clear();
        gotCtx = new HistoryContext(q3).getContext(filename, path, hits);
        Assert.assertFalse(gotCtx);
        Assert.assertEquals(0, hits.size());
        // Search for term with multiple hits - hist:small OR hist:target
        BooleanQuery.Builder q4 = new BooleanQuery.Builder();
        q4.add(new TermQuery(new Term("hist", "small")), SHOULD);
        q4.add(new TermQuery(new Term("hist", "target")), SHOULD);
        hits.clear();
        gotCtx = new HistoryContext(q4.build()).getContext(filename, path, hits);
        Assert.assertTrue(gotCtx);
        Assert.assertEquals(2, hits.size());
        Assert.assertTrue(hits.get(0).getLine().contains("Add lint make <b>target</b> and fix lint warnings"));
        Assert.assertTrue(hits.get(1).getLine().contains("Created a <b>small</b> dummy program"));
    }

    @Test
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    public void testGetContext_4args() throws Exception {
        String path = "/mercurial/Makefile";
        File file = new File(((HistoryContextTest.repositories.getSourceRoot()) + path));
        String parent = file.getParent();
        String base = file.getName();
        // Construct a query equivalent to hist:dummy
        TermQuery q1 = new TermQuery(new Term("hist", "dummy"));
        StringWriter sw = new StringWriter();
        Assert.assertTrue(getContext(parent, base, path, sw, null));
        Assert.assertTrue(sw.toString().contains("Created a small <b>dummy</b> program"));
        // Construct a query equivalent to hist:"dummy program"
        PhraseQuery.Builder q2 = new PhraseQuery.Builder();
        q2.add(new Term("hist", "dummy"));
        q2.add(new Term("hist", "program"));
        sw = new StringWriter();
        Assert.assertTrue(getContext(parent, base, path, sw, null));
        Assert.assertTrue(sw.toString().contains("Created a small <b>dummy program</b>"));
        // Search for a term that doesn't exist
        TermQuery q3 = new TermQuery(new Term("hist", "term_does_not_exist"));
        sw = new StringWriter();
        Assert.assertFalse(getContext(parent, base, path, sw, null));
        Assert.assertEquals("", sw.toString());
        // Search for term with multiple hits - hist:small OR hist:target
        BooleanQuery.Builder q4 = new BooleanQuery.Builder();
        q4.add(new TermQuery(new Term("hist", "small")), SHOULD);
        q4.add(new TermQuery(new Term("hist", "target")), SHOULD);
        sw = new StringWriter();
        Assert.assertTrue(getContext(parent, base, path, sw, null));
        String result = sw.toString();
        Assert.assertTrue(result.contains("Add lint make <b>target</b> and fix lint warnings"));
        Assert.assertTrue(result.contains("Created a <b>small</b> dummy program"));
    }

    /**
     * Test URI and HTML encoding of {@code writeMatch()}.
     *
     * @throws IOException
     * 		I/O exception
     */
    @Test
    public void testWriteMatch() throws IOException {
        StringBuilder sb = new StringBuilder();
        HistoryContext.writeMatch(sb, "foo", 0, 3, true, "/foo bar/haf+haf", "ctx", "1", "2");
        Assert.assertEquals(("<a href=\"ctx/diff/foo%20bar/haf%2Bhaf?r2=/foo%20bar/haf%2Bhaf@2&amp;" + "r1=/foo%20bar/haf%2Bhaf@1\" title=\"diff to previous version\">diff</a> <b>foo</b>"), sb.toString());
    }
}

