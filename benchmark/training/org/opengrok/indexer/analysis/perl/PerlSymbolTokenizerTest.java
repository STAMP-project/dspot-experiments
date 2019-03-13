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
package org.opengrok.indexer.analysis.perl;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.opengrok.indexer.util.CustomAssertions;


/**
 * Unit tests for {@link PerlSymbolTokenizer}.
 */
public class PerlSymbolTokenizerTest {
    @Test
    public void testPerlVariableInBraces() throws Exception {
        // Perl command to tokenize
        String inputText = "$ {abc} = 1; \'$gh\'; \"$ { VARIABLE  } $def xyz\";";
        String[] expectedTokens = new String[]{ "abc", "VARIABLE", "def" };
        testOffsetAttribute(PerlSymbolTokenizer.class, inputText, expectedTokens);
    }

    @Test
    public void testPerlWordCharDelimiters() throws Exception {
        // Perl command to tokenize
        String inputText = "qr z$abcz; qr z$defziz; qr i$ghixi;";
        String[] expectedTokens = new String[]{ "abc", "def", "gh" };
        testOffsetAttribute(PerlSymbolTokenizer.class, inputText, expectedTokens);
    }

    /**
     * Test sample.pl v. samplesymbols.txt
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testPerlSymbolStream() throws Exception {
        InputStream plres = getClass().getClassLoader().getResourceAsStream("analysis/perl/sample.pl");
        InputStream wdsres = getClass().getClassLoader().getResourceAsStream("analysis/perl/samplesymbols.txt");
        List<String> expectedSymbols = new ArrayList<>();
        try (BufferedReader wdsr = new BufferedReader(new InputStreamReader(wdsres, "UTF-8"))) {
            String line;
            while ((line = wdsr.readLine()) != null) {
                int hasho = line.indexOf('#');
                if (hasho != (-1))
                    line = line.substring(0, hasho);

                expectedSymbols.add(line.trim());
            } 
        }
        CustomAssertions.assertSymbolStream(PerlSymbolTokenizer.class, plres, expectedSymbols);
    }
}

