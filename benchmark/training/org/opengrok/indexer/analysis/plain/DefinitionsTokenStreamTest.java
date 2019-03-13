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
 * Copyright (c) 2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis.plain;


import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;


/**
 * Represents a container for tests of {@link DefinitionsTokenStream}.
 */
public class DefinitionsTokenStreamTest {
    /**
     * Tests sampleplain.cc v. sampletags_cc with no expand-tabs and
     * no supplement when ctags's pattern excerpt is insufficient w.r.t.
     * `signature'.
     *
     * @throws java.io.IOException
     * 		I/O exception
     */
    @Test
    public void testCppDefinitionsForRawContentUnsupplemented() throws IOException {
        Map<Integer, AbstractMap.SimpleEntry<String, String>> overrides = new TreeMap<>();
        overrides.put(44, new AbstractMap.SimpleEntry<>(",", "parent_def"));
        overrides.put(45, new AbstractMap.SimpleEntry<>(",", "element"));
        overrides.put(46, new AbstractMap.SimpleEntry<>(",", "offset"));
        overrides.put(47, new AbstractMap.SimpleEntry<>(",", "ant"));
        overrides.put(48, new AbstractMap.SimpleEntry<>(",", "path"));
        testDefinitionsVsContent(false, "analysis/c/sample.cc", "analysis/c/sampletags_cc", 65, false, overrides);
    }

    /**
     * Tests sampleplain.cc v. sampletags_cc with no expand-tabs but
     * supplementing when ctags's pattern excerpt is insufficient w.r.t.
     * `signature'.
     *
     * @throws java.io.IOException
     * 		I/O exception
     */
    @Test
    public void testCppDefinitionsWithRawContent1() throws IOException {
        testDefinitionsVsContent(false, "analysis/c/sample.cc", "analysis/c/sampletags_cc", 65, true, null);
    }

    /**
     * Tests sampleplain.cc v. sampletags_cc with expand-tabs and
     * supplementing when ctags's pattern excerpt is insufficient w.r.t.
     * `signature'.
     *
     * @throws java.io.IOException
     * 		I/O exception
     */
    @Test
    public void testCppDefinitionsWithRawContent2() throws IOException {
        testDefinitionsVsContent(true, "analysis/c/sample.cc", "analysis/c/sampletags_cc", 65, true, null);
    }
}

