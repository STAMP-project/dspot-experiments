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
 * Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis.sql;


import SQLUtils.STRINGLITERAL_APOS_DELIMITER;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.util.StringUtils;


/**
 * Represents a test class for {@link SQLUtils}.
 */
public class SQLUtilsTest {
    @Test
    public void shouldMatchNonescapedApostrophe() {
        final String value = "''1-2-3'";
        int i = StringUtils.patindexOf(value, STRINGLITERAL_APOS_DELIMITER);
        Assert.assertEquals("unquoted apostrophe", 7, i);
    }

    @Test
    public void shouldMatchApostropheAfterPairsOfApostrophes() {
        final String value = "'''''";
        int i = StringUtils.patindexOf(value, STRINGLITERAL_APOS_DELIMITER);
        Assert.assertEquals("unquoted apostrophe after apostrophes", 4, i);
    }

    @Test
    public void shouldNotMatchApostropheAfterOddApostrophes() {
        final String value = "''''";
        int i = StringUtils.patindexOf(value, STRINGLITERAL_APOS_DELIMITER);
        Assert.assertEquals("escaped apostrophe pairs", (-1), i);
    }

    @Test
    public void shouldMatchInitialApostrophe() {
        final String value = "'";
        int i = StringUtils.patindexOf(value, STRINGLITERAL_APOS_DELIMITER);
        Assert.assertEquals("initial apostrophe", 0, i);
    }
}

