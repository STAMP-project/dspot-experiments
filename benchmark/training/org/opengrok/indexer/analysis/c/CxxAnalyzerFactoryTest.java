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
public class CxxAnalyzerFactoryTest {
    private static Ctags ctags;

    private static TestRepository repository;

    private static AbstractAnalyzer analyzer;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    /**
     * Test of writeXref method, of class CAnalyzerFactory.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testScopeAnalyzer() throws Exception {
        String path = (CxxAnalyzerFactoryTest.repository.getSourceRoot()) + "/c/sample.cxx";
        File f = new File(path);
        if (!((f.canRead()) && (f.isFile()))) {
            Assert.fail((("cxx testfile " + f) + " not found"));
        }
        Document doc = new Document();
        doc.add(new org.apache.lucene.document.Field(QueryBuilder.FULLPATH, path, AnalyzerGuru.string_ft_nstored_nanalyzed_norms));
        StringWriter xrefOut = new StringWriter();
        CxxAnalyzerFactoryTest.analyzer.setCtags(CxxAnalyzerFactoryTest.ctags);
        CxxAnalyzerFactoryTest.analyzer.setScopesEnabled(true);
        System.out.println(path);
        CxxAnalyzerFactoryTest.analyzer.analyze(doc, CxxAnalyzerFactoryTest.getStreamSource(path), xrefOut);
        IndexableField scopesField = doc.getField(SCOPES);
        Assert.assertNotNull(scopesField);
        Scopes scopes = Scopes.deserialize(scopesField.binaryValue().bytes);
        Scope globalScope = scopes.getScope((-1));
        Assert.assertEquals(9, scopes.size());
        for (int i = 0; i < 50; ++i) {
            if ((i >= 11) && (i <= 15)) {
                Assert.assertEquals("SomeClass", scopes.getScope(i).getName());
                Assert.assertEquals("SomeClass", scopes.getScope(i).getNamespace());
            } else
                if ((i >= 17) && (i <= 20)) {
                    Assert.assertEquals("~SomeClass", scopes.getScope(i).getName());
                    Assert.assertEquals("SomeClass", scopes.getScope(i).getNamespace());
                } else
                    if ((i >= 22) && (i <= 25)) {
                        Assert.assertEquals("MemberFunc", scopes.getScope(i).getName());
                        Assert.assertEquals("SomeClass", scopes.getScope(i).getNamespace());
                    } else
                        if ((i >= 27) && (i <= 29)) {
                            Assert.assertEquals("operator ++", scopes.getScope(i).getName());
                            Assert.assertEquals("SomeClass", scopes.getScope(i).getNamespace());
                        } else
                            if ((i >= 32) && (i <= 34)) {
                                Assert.assertEquals("TemplateMember", scopes.getScope(i).getName());
                                Assert.assertEquals("SomeClass", scopes.getScope(i).getNamespace());
                            } else
                                if ((i >= 44) && (i <= 46)) {
                                    Assert.assertEquals("SomeFunc", scopes.getScope(i).getName());
                                    Assert.assertEquals("ns1::NamespacedClass", scopes.getScope(i).getNamespace());
                                } else
                                    if ((i >= 51) && (i <= 54)) {
                                        Assert.assertEquals("foo", scopes.getScope(i).getName());
                                        Assert.assertNull(scopes.getScope(i).getNamespace());
                                    } else
                                        if ((i >= 59) && (i <= 73)) {
                                            Assert.assertEquals("bar", scopes.getScope(i).getName());
                                            Assert.assertNull(scopes.getScope(i).getNamespace());
                                        } else
                                            if ((i >= 76) && (i <= 87)) {
                                                Assert.assertEquals("main", scopes.getScope(i).getName());
                                                Assert.assertNull(scopes.getScope(i).getNamespace());
                                            } else {
                                                Assert.assertEquals(scopes.getScope(i), globalScope);
                                                Assert.assertNull(scopes.getScope(i).getNamespace());
                                            }








        }
    }
}

