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
 * Portions Copyright (c) 2017-2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis;


import Definitions.Tag;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.util.TestRepository;


/**
 *
 *
 * @author Lubos Kosco
 */
@ConditionalRun(CtagsInstalled.class)
public class CtagsTest {
    private static Ctags ctags;

    private static TestRepository repository;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    /**
     * Test of doCtags method, of class Ctags.
     */
    @Test
    public void testDoCtags() throws Exception {
        Definitions result = CtagsTest.getDefs("bug16070/arguments.c");
        Assert.assertEquals(13, result.numberOfSymbols());
    }

    /**
     * Test that we don't get many false positives in the list of method
     * definitions for Java files. Bug #14924.
     */
    @Test
    public void bug14924() throws Exception {
        // Expected method names found in the file
        String[] names = new String[]{ "ts", "classNameOnly", "format" };
        // Expected line numbers for the methods
        int[] lines = new int[]{ 44, 48, 53 };
        Definitions result = CtagsTest.getDefs("bug14924/FileLogFormatter.java");
        int count = 0;
        for (Definitions.Tag tag : result.getTags()) {
            if (tag.type.startsWith("method")) {
                Assert.assertTrue("too many methods", (count < (names.length)));
                Assert.assertEquals("method name", names[count], tag.symbol);
                Assert.assertEquals("method line", lines[count], tag.line);
                count++;
            }
        }
        Assert.assertEquals("method count", names.length, count);
    }

    /**
     * Test that multiple extra command line options are processed correctly
     * for assembler source code. Bug #19195.
     */
    @Test
    public void bug19195() throws Exception {
        // Expected method names found in the file
        String[] names = new String[]{ "foo", "bar", "_fce", "__fce" };
        // Expected line numbers for the methods
        int[] lines = new int[]{ 26, 49, 69, 69 };
        /* Perform the actual test. */
        Definitions result = CtagsTest.getDefs("bug19195/test.s");
        int count = 0;
        for (Definitions.Tag tag : result.getTags()) {
            if (tag.type.startsWith("function")) {
                Assert.assertTrue("too many functions", (count < (names.length)));
                Assert.assertEquals("function name", names[count], tag.symbol);
                Assert.assertEquals("function line", lines[count], tag.line);
                count++;
            }
        }
        Assert.assertEquals("function count", names.length, count);
    }
}

