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
 * Copyright (c) 2010, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis;


import java.io.IOException;
import java.io.StringReader;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.analysis.c.CSymbolTokenizer;
import org.opengrok.indexer.analysis.c.CxxSymbolTokenizer;
import org.opengrok.indexer.analysis.document.TroffFullTokenizer;
import org.opengrok.indexer.analysis.fortran.FortranSymbolTokenizer;
import org.opengrok.indexer.analysis.haskell.HaskellSymbolTokenizer;
import org.opengrok.indexer.analysis.java.JavaSymbolTokenizer;
import org.opengrok.indexer.analysis.lisp.LispSymbolTokenizer;
import org.opengrok.indexer.analysis.perl.PerlSymbolTokenizer;
import org.opengrok.indexer.analysis.plain.PlainFullTokenizer;
import org.opengrok.indexer.analysis.plain.PlainSymbolTokenizer;
import org.opengrok.indexer.analysis.scala.ScalaSymbolTokenizer;
import org.opengrok.indexer.analysis.sh.ShSymbolTokenizer;
import org.opengrok.indexer.analysis.tcl.TclSymbolTokenizer;
import org.opengrok.indexer.analysis.uue.UuencodeFullTokenizer;


/**
 * Unit tests for JFlexTokenizer.
 */
public class JFlexTokenizerTest {
    /**
     * Test that the various sub-classes of JFlexTokenizerTest return the
     * correct offsets for the tokens. They used to give wrong values for the
     * last token. Bug #15858.
     *
     * @throws java.lang.Exception
     * 		
     */
    @Test
    public void testOffsetAttribute() throws Exception {
        testOffsetAttribute(CSymbolTokenizer.class);
        testOffsetAttribute(CxxSymbolTokenizer.class);
        testOffsetAttribute(HaskellSymbolTokenizer.class);
        testOffsetAttribute(JavaSymbolTokenizer.class);
        testOffsetAttribute(LispSymbolTokenizer.class);
        testOffsetAttribute(PerlSymbolTokenizer.class);
        testOffsetAttribute(PlainFullTokenizer.class);
        testOffsetAttribute(PlainSymbolTokenizer.class);
        testOffsetAttribute(ScalaSymbolTokenizer.class);
        testOffsetAttribute(ShSymbolTokenizer.class);
        testOffsetAttribute(TclSymbolTokenizer.class);
        testOffsetAttribute(TroffFullTokenizer.class);
        // The Fortran tokenizer doesn't accept the default input text, so
        // create a text fragment that it understands
        testOffsetAttribute(FortranSymbolTokenizer.class, "1 token1 = token2 + token3", new String[]{ "token1", "token2", "token3" });
    }

    /**
     * The fix for bug #15858 caused a regression in ShSymbolTokenizer where
     * variables on the form {@code ${VARIABLE}} were not correctly indexed if
     * they were inside a quoted string. The closing brace would be part of the
     * indexed term in that case.
     *
     * @throws java.lang.Exception
     * 		
     */
    @Test
    public void testShellVariableInBraces() throws Exception {
        // Shell command to tokenize
        String inputText = "echo \"${VARIABLE} $abc xyz\"";
        // "echo" is an ignored token in ShSymbolTokenizer, "xyz" is a string
        // and not a symbol. Therefore, expect just the two tokens that name
        // variables.
        String[] expectedTokens = new String[]{ "VARIABLE", "abc" };
        testOffsetAttribute(ShSymbolTokenizer.class, inputText, expectedTokens);
    }

    /**
     * Truncated uuencoded files used to cause infinite loops. Verify that they
     * work now.
     *
     * @throws java.io.IOException
     * 		
     */
    @Test
    public void truncatedUuencodedFile() throws IOException {
        JFlexSymbolMatcher matcher = new UuencodeFullTokenizer(new StringReader("begin 644 test\n"));
        JFlexTokenizer tokenizer = new JFlexTokenizer(matcher);
        CharTermAttribute term = tokenizer.addAttribute(CharTermAttribute.class);
        Assert.assertTrue(tokenizer.incrementToken());
        Assert.assertEquals("begin", term.toString());
        Assert.assertTrue(tokenizer.incrementToken());
        Assert.assertEquals("644", term.toString());
        Assert.assertTrue(tokenizer.incrementToken());
        Assert.assertEquals("test", term.toString());
        // This call used to hang forever.
        Assert.assertFalse(tokenizer.incrementToken());
    }
}

