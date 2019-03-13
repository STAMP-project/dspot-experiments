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
 * Portions Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis.haskell;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.analysis.AbstractAnalyzer;
import org.opengrok.indexer.util.CustomAssertions;


/**
 * Tests the {@link HaskellSymbolTokenizer} class.
 *
 * @author Harry Pan
 */
public class HaskellSymbolTokenizerTest {
    private final AbstractAnalyzer analyzer;

    public HaskellSymbolTokenizerTest() {
        this.analyzer = new HaskellAnalyzerFactory().getAnalyzer();
    }

    @Test
    public void sampleTest() throws UnsupportedEncodingException {
        InputStream res = getClass().getClassLoader().getResourceAsStream("analysis/haskell/sample.hs");
        InputStreamReader r = new InputStreamReader(res, "UTF-8");
        String[] termsFor = getTermsFor(r);
        Assert.assertArrayEquals(new String[]{ "qsort"// line 2
        , "qsort", "x", "xs", "qsort", "x'", "x'", "xs", "x'", "x", "x", "qsort", "x'", "x'", "xs", "x'", "x"// line 3
        , "x'y'", "f'", "g'h", "f'", "g'h"// line 6
         }, termsFor);
    }

    /**
     * Test sample2.hs v. sample2symbols.txt
     *
     * @throws java.lang.Exception
     * 		thrown on error
     */
    @Test
    public void testHaskellSymbolStream() throws Exception {
        InputStream pyres = getClass().getClassLoader().getResourceAsStream("analysis/haskell/sample2.hs");
        Assert.assertNotNull("despite sample.py as resource,", pyres);
        InputStream symres = getClass().getClassLoader().getResourceAsStream("analysis/haskell/sample2symbols.txt");
        Assert.assertNotNull("despite samplesymbols.txt as resource,", symres);
        List<String> expectedSymbols = new ArrayList<>();
        try (BufferedReader wdsr = new BufferedReader(new InputStreamReader(symres, "UTF-8"))) {
            String line;
            while ((line = wdsr.readLine()) != null) {
                int hasho = line.indexOf('#');
                if (hasho != (-1))
                    line = line.substring(0, hasho);

                expectedSymbols.add(line.trim());
            } 
        }
        CustomAssertions.assertSymbolStream(HaskellSymbolTokenizer.class, pyres, expectedSymbols);
    }
}

