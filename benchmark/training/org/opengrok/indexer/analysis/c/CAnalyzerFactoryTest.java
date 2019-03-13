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
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017-2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis.c;


import QueryBuilder.SCOPES;
import java.io.File;
import java.io.StringWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.analysis.AbstractAnalyzer;
import org.opengrok.indexer.analysis.AnalyzerGuru;
import org.opengrok.indexer.analysis.Ctags;
import org.opengrok.indexer.analysis.Scopes;
import org.opengrok.indexer.analysis.Scopes.Scope;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.search.QueryBuilder;
import org.opengrok.indexer.util.TestRepository;


/**
 *
 *
 * @author Tomas Kotal
 */
@ConditionalRun(CtagsInstalled.class)
public class CAnalyzerFactoryTest {
    private static Ctags ctags;

    private static TestRepository repository;

    private static AbstractAnalyzer analyzer;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    /**
     * Test of writeXref method, of class CAnalyzerFactory.
     *
     * @throws java.lang.Exception
     * 		exception
     */
    @Test
    public void testScopeAnalyzer() throws Exception {
        String path = (CAnalyzerFactoryTest.repository.getSourceRoot()) + "/c/sample.c";
        File f = new File(path);
        if (!((f.canRead()) && (f.isFile()))) {
            Assert.fail((("c testfile " + f) + " not found"));
        }
        Document doc = new Document();
        doc.add(new org.apache.lucene.document.Field(QueryBuilder.FULLPATH, path, AnalyzerGuru.string_ft_nstored_nanalyzed_norms));
        StringWriter xrefOut = new StringWriter();
        CAnalyzerFactoryTest.analyzer.setCtags(CAnalyzerFactoryTest.ctags);
        CAnalyzerFactoryTest.analyzer.setScopesEnabled(true);
        CAnalyzerFactoryTest.analyzer.analyze(doc, CAnalyzerFactoryTest.getStreamSource(path), xrefOut);
        IndexableField scopesField = doc.getField(SCOPES);
        Assert.assertNotNull(scopesField);
        Scopes scopes = Scopes.deserialize(scopesField.binaryValue().bytes);
        Scope globalScope = scopes.getScope((-1));
        Assert.assertEquals(3, scopes.size());// foo, bar, main

        for (int i = 0; i < 50; ++i) {
            if ((i >= 8) && (i <= 22)) {
                Assert.assertEquals("foo", scopes.getScope(i).getName());
                Assert.assertNull(scopes.getScope(i).getNamespace());
            } else
                if ((i >= 24) && (i <= 38)) {
                    Assert.assertEquals("bar", scopes.getScope(i).getName());
                    Assert.assertNull(scopes.getScope(i).getNamespace());
                } else
                    if ((i >= 41) && (i <= 48)) {
                        Assert.assertEquals("main", scopes.getScope(i).getName());
                        Assert.assertNull(scopes.getScope(i).getNamespace());
                    } else {
                        Assert.assertEquals(scopes.getScope(i), globalScope);
                        Assert.assertNull(scopes.getScope(i).getNamespace());
                    }


        }
    }
}

