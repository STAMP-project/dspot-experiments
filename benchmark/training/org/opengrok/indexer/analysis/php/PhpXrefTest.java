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
 * Copyright (c) 2012, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis.php;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.analysis.AbstractAnalyzer;
import org.opengrok.indexer.analysis.WriteXrefArgs;
import org.opengrok.indexer.analysis.Xrefer;
import org.opengrok.indexer.util.CustomAssertions;
import org.opengrok.indexer.util.StreamUtils;


/**
 * Tests the {@link PhpXref} class.
 *
 * @author Gustavo Lopes
 */
public class PhpXrefTest {
    @Test
    public void basicTest() throws IOException {
        String s = "<?php foo bar";
        Writer w = new StringWriter();
        PhpAnalyzerFactory fac = new PhpAnalyzerFactory();
        AbstractAnalyzer analyzer = fac.getAnalyzer();
        WriteXrefArgs xargs = new WriteXrefArgs(new StringReader(s), w);
        Xrefer xref = analyzer.writeXref(xargs);
        Assert.assertEquals(("<a class=\"l\" name=\"1\" href=\"#1\">1</a><strong>&lt;?php</strong> <a href=\"/" + "source/s?defs=foo\" class=\"intelliWindow-symbol\" data-definition-place=\"undefined-in-file\">foo</a> <a href=\"/source/s?defs=bar\" class=\"intelliWindow-symbol\" data-definition-place=\"undefined-in-file\">bar</a>"), w.toString());
        Assert.assertEquals("PHP LOC", 1, xref.getLOC());
    }

    @Test
    public void basicSingleQuotedStringTest() throws IOException {
        String s = "<?php define(\"FOO\", \'BAR\\\'\"\'); $foo=\'bar\'; $hola=\"ls\"; $hola=\'\'; $hola=\"\";";
        Writer w = new StringWriter();
        PhpAnalyzerFactory fac = new PhpAnalyzerFactory();
        AbstractAnalyzer analyzer = fac.getAnalyzer();
        WriteXrefArgs xargs = new WriteXrefArgs(new StringReader(s), w);
        Xrefer xref = analyzer.writeXref(xargs);
        CustomAssertions.assertLinesEqual("PHP quoting", ("<a class=\"l\" name=\"1\" href=\"#1\">1</a><strong>&lt;?php</strong> " + (((("<a href=\"/source/s?defs=define\" class=\"intelliWindow-symbol\" data-definition-place=\"undefined-in-file\">define</a>(<span class=\"s\">&quot;FOO&quot;</span>, <span class=\"s\">&apos;BAR<strong>\\&apos;</strong>&quot;&apos;</span>); " + "$<a href=\"/source/s?defs=foo\" class=\"intelliWindow-symbol\" data-definition-place=\"undefined-in-file\">foo</a>=<span class=\"s\">&apos;bar&apos;</span>; ") + "$<a href=\"/source/s?defs=hola\" class=\"intelliWindow-symbol\" data-definition-place=\"undefined-in-file\">hola</a>=<span class=\"s\">&quot;ls&quot;</span>; ") + "$<a href=\"/source/s?defs=hola\" class=\"intelliWindow-symbol\" data-definition-place=\"undefined-in-file\">hola</a>=<span class=\"s\">&apos;&apos;</span>; ") + "$<a href=\"/source/s?defs=hola\" class=\"intelliWindow-symbol\" data-definition-place=\"undefined-in-file\">hola</a>=<span class=\"s\">&quot;&quot;</span>;")), w.toString());
        Assert.assertEquals("PHP LOC", 1, xref.getLOC());
    }

    @Test
    public void sampleTest() throws IOException {
        InputStream res = getClass().getClassLoader().getResourceAsStream("analysis/php/sample.php");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int actLOC;
        try {
            actLOC = writePhpXref(res, new PrintStream(baos));
        } finally {
            res.close();
        }
        InputStream exp = getClass().getClassLoader().getResourceAsStream("analysis/php/sampleXrefRes.html");
        byte[] expbytes = StreamUtils.copyStream(exp);
        String[] gotten = new String(baos.toByteArray(), "UTF-8").split("\\r?\\n");
        String[] expected = new String(expbytes, "UTF-8").split("\n");
        CustomAssertions.assertLinesEqual("PHP xref", expected, gotten);
        Assert.assertEquals("PHP LOC", 29, actLOC);
        Assert.assertEquals(expected.length, gotten.length);
    }
}

