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
 * Copyright (c) 2009, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions copyright 2009 - 2011 Jens Elkner.
 */
package org.opengrok.indexer.analysis.document;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import org.apache.lucene.document.Document;
import org.junit.Test;
import org.opengrok.indexer.analysis.StreamSource;
import org.opengrok.indexer.util.TestRepository;


/**
 *
 *
 * @author Jens Elkner
 * @version $Revision$
 */
public class TroffAnalyzerTest {
    private static TroffAnalyzerFactory factory;

    private static TroffAnalyzer analyzer;

    private static String content;

    private static TestRepository repository;

    /**
     * Test method for {@link org.opengrok.indexer.analysis.document
     *  .TroffAnalyzer#analyze(org.apache.lucene.document.Document,
     *      java.io.InputStream)}.
     *
     * @throws IOException
     * 		I/O exception
     */
    @Test
    public void testAnalyze() throws IOException {
        Document doc = new Document();
        StringWriter xrefOut = new StringWriter();
        TroffAnalyzerTest.analyzer.analyze(doc, new StreamSource() {
            @Override
            public InputStream getStream() throws IOException {
                return new ByteArrayInputStream(TroffAnalyzerTest.content.getBytes());
            }
        }, xrefOut);
    }
}

