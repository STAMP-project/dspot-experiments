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
package org.opengrok.indexer.analysis.pascal;


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
 * @author alexanthony
 */
@ConditionalRun(CtagsInstalled.class)
public class PascalAnalyzerFactoryTest {
    private static Ctags ctags;

    private static TestRepository repository;

    private static AbstractAnalyzer analyzer;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    /**
     * Test of writeXref method, of class PascalAnalyzerFactory.
     *
     * @throws java.lang.Exception
     * 		exception
     */
    @Test
    public void testAnalyzer() throws Exception {
        String path = (PascalAnalyzerFactoryTest.repository.getSourceRoot()) + "/pascal/Sample.pas";
        File f = new File(path);
        if (!((f.canRead()) && (f.isFile()))) {
            Assert.fail((("pascal testfile " + f) + " not found"));
        }
        Document doc = new Document();
        doc.add(new org.apache.lucene.document.Field(QueryBuilder.FULLPATH, path, AnalyzerGuru.string_ft_nstored_nanalyzed_norms));
        StringWriter xrefOut = new StringWriter();
        PascalAnalyzerFactoryTest.analyzer.setCtags(PascalAnalyzerFactoryTest.ctags);
        PascalAnalyzerFactoryTest.analyzer.setScopesEnabled(true);
        PascalAnalyzerFactoryTest.analyzer.analyze(doc, PascalAnalyzerFactoryTest.getStreamSource(path), xrefOut);
        Definitions definitions = Definitions.deserialize(doc.getField(TAGS).binaryValue().bytes);
        Assert.assertNotNull(definitions);
        String[] type = new String[1];
        Assert.assertTrue(definitions.hasDefinitionAt("Sample", 22, type));
        Assert.assertThat(type[0], CoreMatchers.is("unit"));
        Assert.assertTrue(definitions.hasDefinitionAt("TSample", 28, type));
        Assert.assertThat(type[0], CoreMatchers.is("Class"));
        Assert.assertTrue(definitions.hasDefinitionAt("Id", 40, type));
        Assert.assertThat(type[0], CoreMatchers.is("property"));
        Assert.assertTrue(definitions.hasDefinitionAt("Description", 41, type));
        Assert.assertThat(type[0], CoreMatchers.is("property"));
        Assert.assertTrue(definitions.hasDefinitionAt("TSample.GetId", 48, type));
        Assert.assertThat(type[0], CoreMatchers.is("function"));
        Assert.assertTrue(definitions.hasDefinitionAt("TSample.SetId", 53, type));
        Assert.assertThat(type[0], CoreMatchers.is("procedure"));
        Assert.assertTrue(definitions.hasDefinitionAt("TSample.GetClassName", 58, type));
        Assert.assertThat(type[0], CoreMatchers.is("function"));
        Assert.assertTrue(definitions.hasDefinitionAt("TSample.GetUser", 63, type));
        Assert.assertThat(type[0], CoreMatchers.is("function"));
    }
}

