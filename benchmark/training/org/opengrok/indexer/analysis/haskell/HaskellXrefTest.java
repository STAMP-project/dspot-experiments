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
 * Copyright (c) 2015, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017-2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis.haskell;


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
import org.opengrok.indexer.analysis.Definitions;
import org.opengrok.indexer.analysis.WriteXrefArgs;
import org.opengrok.indexer.analysis.Xrefer;
import org.opengrok.indexer.util.CustomAssertions;


/**
 * Tests the {@link HaskellXref} class.
 *
 * @author Harry Pan
 */
public class HaskellXrefTest {
    @Test
    public void basicTest() throws IOException {
        String s = "putStrLn \"Hello, world!\"";
        Writer w = new StringWriter();
        HaskellAnalyzerFactory fac = new HaskellAnalyzerFactory();
        AbstractAnalyzer analyzer = fac.getAnalyzer();
        WriteXrefArgs xargs = new WriteXrefArgs(new StringReader(s), w);
        Xrefer xref = analyzer.writeXref(xargs);
        CustomAssertions.assertLinesEqual("Haskell basicTest", ("<a class=\"l\" name=\"1\" href=\"#1\">1</a>" + (("<a href=\"/source/s?defs=putStrLn\" class=\"intelliWindow-symbol\"" + " data-definition-place=\"undefined-in-file\">putStrLn</a>") + " <span class=\"s\">&quot;Hello, world!&quot;</span>\n")), w.toString());
        Assert.assertEquals("Haskell LOC", 1, xref.getLOC());
    }

    @Test
    public void sampleTest() throws IOException {
        // load sample source
        InputStream sampleInputStream = getClass().getClassLoader().getResourceAsStream("analysis/haskell/sample.hs");
        ByteArrayOutputStream sampleOutputStream = new ByteArrayOutputStream();
        Definitions defs = new Definitions();
        defs.addTag(6, "x'y'", "functions", "x'y' = let f' = 1; g'h = 2 in f' + g'h", 0, 0);
        int actLOC;
        try {
            actLOC = HaskellXrefTest.writeHaskellXref(sampleInputStream, new PrintStream(sampleOutputStream), defs);
        } finally {
            sampleInputStream.close();
            sampleOutputStream.close();
        }
        // load expected xref
        InputStream expectedInputStream = getClass().getClassLoader().getResourceAsStream("analysis/haskell/sampleXrefExpected.html");
        ByteArrayOutputStream expectedOutputSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[8192];
            int numBytesRead;
            do {
                numBytesRead = expectedInputStream.read(buffer, 0, buffer.length);
                if (numBytesRead > 0) {
                    expectedOutputSteam.write(buffer, 0, numBytesRead);
                }
            } while (numBytesRead >= 0 );
        } finally {
            expectedInputStream.close();
            expectedOutputSteam.close();
        }
        String[] actual = new String(sampleOutputStream.toByteArray(), "UTF-8").split("\\r?\\n");
        String[] expected = new String(expectedOutputSteam.toByteArray(), "UTF-8").split("\\r?\\n");
        CustomAssertions.assertLinesEqual("Haskell sampleTest()", expected, actual);
        Assert.assertEquals("Haskell LOC", 3, actLOC);
    }

    @Test
    public void sampleTest2() throws IOException {
        writeAndCompare("analysis/haskell/sample2.hs", "analysis/haskell/sample2_xref.html", getTagsDefinitions(), 198);
    }

    @Test
    public void shouldCloseTruncatedStringSpan() throws IOException {
        writeAndCompare("analysis/haskell/truncated.hs", "analysis/haskell/truncated_xref.html", null, 1);
    }
}

