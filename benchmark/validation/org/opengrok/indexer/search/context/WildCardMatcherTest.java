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
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.search.context;


import LineMatcher.MATCHED;
import LineMatcher.NOT_MATCHED;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the WildCardMatcher class.
 */
public class WildCardMatcherTest {
    /**
     * Test of match method.
     */
    @Test
    public void testMatch() {
        WildCardMatcher m = new WildCardMatcher("wild?ard", true);// bug #15644

        Assert.assertEquals(MATCHED, m.match("wildcard"));
        Assert.assertEquals(MATCHED, m.match("wildward"));
        Assert.assertEquals(MATCHED, m.match("wilddard"));
        Assert.assertEquals(MATCHED, m.match("wild?ard"));
        Assert.assertEquals(NOT_MATCHED, m.match("wildard"));
        Assert.assertEquals(NOT_MATCHED, m.match("wildcarde"));
        Assert.assertEquals(NOT_MATCHED, m.match("awildcard"));
        Assert.assertEquals(NOT_MATCHED, m.match("wildddard"));
        Assert.assertEquals(NOT_MATCHED, m.match("mildcard"));
        Assert.assertEquals(NOT_MATCHED, m.match("wildc?rd"));
        m = new WildCardMatcher("wild*ard", true);
        Assert.assertEquals(MATCHED, m.match("wildcard"));
        Assert.assertEquals(MATCHED, m.match("wildward"));
        Assert.assertEquals(MATCHED, m.match("wilddard"));
        Assert.assertEquals(MATCHED, m.match("wildard"));
        Assert.assertEquals(MATCHED, m.match("wildxyzard"));
        Assert.assertEquals(MATCHED, m.match("wildxyzard"));
        Assert.assertEquals(NOT_MATCHED, m.match("wild"));
        Assert.assertEquals(NOT_MATCHED, m.match("ard"));
        Assert.assertEquals(NOT_MATCHED, m.match("wildcat"));
        Assert.assertEquals(NOT_MATCHED, m.match("wildcarda"));
        Assert.assertEquals(NOT_MATCHED, m.match("mildcard"));
    }
}

