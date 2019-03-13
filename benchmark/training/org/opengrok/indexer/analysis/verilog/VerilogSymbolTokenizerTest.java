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
 * Portions Copyright (c) 2017, 2019, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis.verilog;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.util.CustomAssertions;


/**
 * Tests the {@link VerilogSymbolTokenizer} class.
 */
public class VerilogSymbolTokenizerTest {
    /**
     * Test sample.v v. samplesymbols.txt
     *
     * @throws Exception
     * 		thrown on error
     */
    @Test
    public void testVerilogSymbolStream() throws Exception {
        InputStream vRes = getClass().getClassLoader().getResourceAsStream("analysis/verilog/sample.v");
        Assert.assertNotNull("despite sample.v as resource,", vRes);
        InputStream symRes = getClass().getClassLoader().getResourceAsStream("analysis/verilog/samplesymbols.txt");
        Assert.assertNotNull("despite samplesymbols.txt as resource,", symRes);
        List<String> expectedSymbols = new ArrayList<>();
        try (BufferedReader symRead = new BufferedReader(new InputStreamReader(symRes, StandardCharsets.UTF_8))) {
            String line;
            while ((line = symRead.readLine()) != null) {
                int hashOffset = line.indexOf('#');
                if (hashOffset != (-1)) {
                    line = line.substring(0, hashOffset);
                }
                expectedSymbols.add(line.trim());
            } 
        }
        CustomAssertions.assertSymbolStream(VerilogSymbolTokenizer.class, vRes, expectedSymbols);
    }
}

