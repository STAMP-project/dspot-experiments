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
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017-2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis.clojure;


import QueryBuilder.TAGS;
import java.io.File;
import java.io.StringWriter;
import org.apache.lucene.document.Document;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.analysis.AbstractAnalyzer;
import org.opengrok.indexer.analysis.AnalyzerGuru;
import org.opengrok.indexer.analysis.Ctags;
import org.opengrok.indexer.analysis.Definitions;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.search.QueryBuilder;
import org.opengrok.indexer.util.TestRepository;


/**
 *
 *
 * @author Farid Zakaria
 */
@ConditionalRun(CtagsInstalled.class)
public class ClojureAnalyzerFactoryTest {
    private static Ctags ctags;

    private static TestRepository repository;

    private static AbstractAnalyzer analyzer;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    /**
     * Test of writeXref method, of class CAnalyzerFactory.
     *
     * @throws java.lang.Exception
     * 		
     */
    @Test
    public void testScopeAnalyzer() throws Exception {
        String path = (ClojureAnalyzerFactoryTest.repository.getSourceRoot()) + "/clojure/sample.clj";
        File f = new File(path);
        if (!((f.canRead()) && (f.isFile()))) {
            Assert.fail((("clojure testfile " + f) + " not found"));
        }
        Document doc = new Document();
        doc.add(new org.apache.lucene.document.Field(QueryBuilder.FULLPATH, path, AnalyzerGuru.string_ft_nstored_nanalyzed_norms));
        StringWriter xrefOut = new StringWriter();
        ClojureAnalyzerFactoryTest.analyzer.setCtags(ClojureAnalyzerFactoryTest.ctags);
        ClojureAnalyzerFactoryTest.analyzer.analyze(doc, ClojureAnalyzerFactoryTest.getStreamSource(path), xrefOut);
        Definitions definitions = Definitions.deserialize(doc.getField(TAGS).binaryValue().bytes);
        String[] type = new String[1];
        Assert.assertTrue(definitions.hasDefinitionAt("opengrok", 4, type));
        Assert.assertThat(type[0], CoreMatchers.is("namespace"));
        Assert.assertTrue(definitions.hasDefinitionAt("power-set", 8, type));
        Assert.assertThat(type[0], CoreMatchers.is("function"));
        Assert.assertTrue(definitions.hasDefinitionAt("power-set-private", 14, type));
        Assert.assertThat(type[0], CoreMatchers.is("private function"));
        Assert.assertTrue(definitions.hasDefinitionAt("author", 19, type));
        Assert.assertThat(type[0], CoreMatchers.is("struct"));
        Assert.assertTrue(definitions.hasDefinitionAt("author-first-name", 22, type));
        Assert.assertThat(type[0], CoreMatchers.is("definition"));
        Assert.assertTrue(definitions.hasDefinitionAt("Farid", 24, type));
        Assert.assertThat(type[0], CoreMatchers.is("definition"));
    }
}

