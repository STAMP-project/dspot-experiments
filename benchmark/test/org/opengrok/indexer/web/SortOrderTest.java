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
 * Copyright 2011 Trond Norbye.  All rights reserved.
 * Use is subject to license terms.
 */
package org.opengrok.indexer.web;


import SortOrder.BY_PATH;
import SortOrder.LASTMODIFIED;
import SortOrder.RELEVANCY;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author trond
 */
public class SortOrderTest {
    public SortOrderTest() {
    }

    /**
     * Test of values method, of class SortOrder.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        SortOrder[] result = SortOrder.values();
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(LASTMODIFIED, result[0]);
        Assert.assertEquals(RELEVANCY, result[1]);
        Assert.assertEquals(BY_PATH, result[2]);
    }

    /**
     * Test of valueOf method, of class SortOrder.
     */
    @Test
    public void testValueOf() {
        System.out.println("valueOf");
        SortOrder result = SortOrder.valueOf("LASTMODIFIED");
        Assert.assertNotNull(result);
        Assert.assertEquals("last modified time", result.getDesc());
        result = SortOrder.valueOf("RELEVANCY");
        Assert.assertNotNull(result);
        Assert.assertEquals("relevance", result.getDesc());
        result = SortOrder.valueOf("BY_PATH");
        Assert.assertNotNull(result);
        Assert.assertEquals("path", result.getDesc());
    }
}

