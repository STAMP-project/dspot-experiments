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
package org.opengrok.indexer.analysis.java;


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
public class JavaAnalyzerFactoryTest {
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
        String path = (JavaAnalyzerFactoryTest.repository.getSourceRoot()) + "/java/Sample.java";
        File f = new File(path);
        if (!((f.canRead()) && (f.isFile()))) {
            Assert.fail((("java testfile " + f) + " not found"));
        }
        Document doc = new Document();
        doc.add(new org.apache.lucene.document.Field(QueryBuilder.FULLPATH, path, AnalyzerGuru.string_ft_nstored_nanalyzed_norms));
        StringWriter xrefOut = new StringWriter();
        JavaAnalyzerFactoryTest.analyzer.setCtags(JavaAnalyzerFactoryTest.ctags);
        JavaAnalyzerFactoryTest.analyzer.setScopesEnabled(true);
        JavaAnalyzerFactoryTest.analyzer.analyze(doc, JavaAnalyzerFactoryTest.getStreamSource(path), xrefOut);
        IndexableField scopesField = doc.getField(SCOPES);
        Assert.assertNotNull(scopesField);
        Scopes scopes = Scopes.deserialize(scopesField.binaryValue().bytes);
        Scope globalScope = scopes.getScope((-1));
        Assert.assertEquals(5, scopes.size());// foo, bar, main

        for (int i = 0; i < 74; ++i) {
            if ((i >= 29) && (i <= 31)) {
                Assert.assertEquals("Sample", scopes.getScope(i).getName());
                Assert.assertEquals("Sample", scopes.getScope(i).getNamespace());
            } else
                if ((i >= 33) && (i <= 41)) {
                    Assert.assertEquals("Method", scopes.getScope(i).getName());
                    Assert.assertEquals("Sample", scopes.getScope(i).getNamespace());
                } else
                    if (i == 43) {
                        Assert.assertEquals("AbstractMethod", scopes.getScope(i).getName());
                        Assert.assertEquals("Sample", scopes.getScope(i).getNamespace());
                    } else
                        if ((i >= 47) && (i <= 56)) {
                            Assert.assertEquals("InnerMethod", scopes.getScope(i).getName());
                            Assert.assertEquals("Sample.InnerClass", scopes.getScope(i).getNamespace());
                        } else
                            if ((i >= 60) && (i <= 72)) {
                                Assert.assertEquals("main", scopes.getScope(i).getName());
                                Assert.assertEquals("Sample", scopes.getScope(i).getNamespace());
                            } else {
                                Assert.assertEquals(scopes.getScope(i), globalScope);
                                Assert.assertNull(scopes.getScope(i).getNamespace());
                            }




        }
    }
}

