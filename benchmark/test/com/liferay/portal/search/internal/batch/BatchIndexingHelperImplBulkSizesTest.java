/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.search.internal.batch;


import StringPool.SPACE;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class BatchIndexingHelperImplBulkSizesTest {
    @Test
    public void testConfiguration() {
        String entryClassName1 = RandomTestUtil.randomString();
        String entryClassName2 = RandomTestUtil.randomString();
        activate((entryClassName1 + "=200"), (entryClassName2 + "=500"));
        assertBulkSize(200, entryClassName1);
        assertBulkSize(500, entryClassName2);
    }

    @Test
    public void testDefault() {
        activateWithoutConfiguration();
        assertBulkSize(10000, "com.liferay.journal.model.JournalArticle");
        assertBulkSize(10000, RandomTestUtil.randomString());
    }

    @Test
    public void testDefaultWithConfiguration() {
        activate("com.liferay.journal.model.JournalArticle=200");
        assertBulkSize(200, "com.liferay.journal.model.JournalArticle");
        assertBulkSize(10000, RandomTestUtil.randomString());
    }

    @Test
    public void testMalformed() {
        String entryClassName1 = RandomTestUtil.randomString();
        String entryClassName2 = RandomTestUtil.randomString();
        activate((entryClassName1 + "= "), SPACE, (entryClassName2 + "?200"));
        assertBulkSize(10000, entryClassName1);
        assertBulkSize(10000, entryClassName2);
    }

    private final BatchIndexingHelperImpl _batchIndexingHelperImpl = new BatchIndexingHelperImpl();
}

