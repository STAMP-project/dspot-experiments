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
package org.opengrok.indexer.analysis.csharp;


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
public class CSharpAnalyzerFactoryTest {
    private static Ctags ctags;

    private static TestRepository repository;

    private static AbstractAnalyzer analyzer;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    /**
     * Test of writeXref method, of class CSharpAnalyzerFactory.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testScopeAnalyzer() throws Exception {
        String path = (CSharpAnalyzerFactoryTest.repository.getSourceRoot()) + "/csharp/Sample.cs";
        File f = new File(path);
        if (!((f.canRead()) && (f.isFile()))) {
            Assert.fail((("csharp testfile " + f) + " not found"));
        }
        Document doc = new Document();
        doc.add(new org.apache.lucene.document.Field(QueryBuilder.FULLPATH, path, AnalyzerGuru.string_ft_nstored_nanalyzed_norms));
        StringWriter xrefOut = new StringWriter();
        CSharpAnalyzerFactoryTest.analyzer.setCtags(CSharpAnalyzerFactoryTest.ctags);
        CSharpAnalyzerFactoryTest.analyzer.setScopesEnabled(true);
        CSharpAnalyzerFactoryTest.analyzer.analyze(doc, CSharpAnalyzerFactoryTest.getStreamSource(path), xrefOut);
        IndexableField scopesField = doc.getField(SCOPES);
        Assert.assertNotNull(scopesField);
        Scopes scopes = Scopes.deserialize(scopesField.binaryValue().bytes);
        Scope globalScope = scopes.getScope((-1));
        Assert.assertEquals(4, scopes.size());// TODO 5

        for (int i = 0; i < 41; ++i) {
            if ((i >= 10) && (i <= 10)) {
                Assert.assertEquals("M1", scopes.getScope(i).getName());
                Assert.assertEquals("MyNamespace.TopClass", scopes.getScope(i).getNamespace());
            } else
                if ((i >= 12) && (i <= 14)) {
                    Assert.assertEquals("M2", scopes.getScope(i).getName());
                    Assert.assertEquals("MyNamespace.TopClass", scopes.getScope(i).getNamespace());
                } else
                    if ((i >= 19) && (i <= 25)) {
                        Assert.assertEquals("M3", scopes.getScope(i).getName());
                        Assert.assertEquals("MyNamespace.TopClass", scopes.getScope(i).getNamespace());
                        // TODO add support for generic classes
                        // } else if (i >= 28 && i <= 30) {
                        // assertEquals("M4", scopes.getScope(i).name);
                        // assertEquals("MyNamespace.TopClass", scopes.getScope(i).namespace);
                    } else
                        if ((i >= 34) && (i <= 36)) {
                            Assert.assertEquals("M5", scopes.getScope(i).getName());
                            Assert.assertEquals("MyNamespace.TopClass.InnerClass", scopes.getScope(i).getNamespace());
                        } else {
                            Assert.assertEquals(scopes.getScope(i), globalScope);
                            Assert.assertNull(scopes.getScope(i).getNamespace());
                        }



        }
    }
}

