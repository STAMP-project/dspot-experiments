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
package com.liferay.journal.search;


import Field.ENTRY_CLASS_PK;
import StringPool.BLANK;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.test.BaseSearchResultUtilTestCase;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.internal.result.SearchResultTranslatorImpl;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class SearchResultUtilJournalArticleTest extends BaseSearchResultUtilTestCase {
    @Test
    public void testJournalArticle() {
        SearchResult searchResult = assertOneSearchResult(createDocument());
        assertSearchResult(searchResult);
        Assert.assertNull(searchResult.getSummary());
    }

    @Test
    public void testJournalArticleWithDefectiveIndexer() throws Exception {
        Mockito.doThrow(IllegalArgumentException.class).when(_indexer).getSummary(((Document) (Matchers.any())), Matchers.anyString(), ((PortletRequest) (Matchers.any())), ((PortletResponse) (Matchers.any())));
        Mockito.when(_indexerRegistry.getIndexer(Mockito.anyString())).thenReturn(_indexer);
        Document document = createDocument();
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(SearchResultTranslatorImpl.class.getName(), Level.WARNING)) {
            SearchResult searchResult = assertOneSearchResult(document);
            assertSearchResult(searchResult);
            Assert.assertNull(searchResult.getSummary());
            Mockito.verify(_indexer).getSummary(document, BLANK, null, null);
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            Assert.assertEquals(logRecords.toString(), 1, logRecords.size());
            LogRecord logRecord = logRecords.get(0);
            Assert.assertEquals((("Search index is stale and contains entry {" + (document.get(ENTRY_CLASS_PK))) + "}"), logRecord.getMessage());
        }
    }

    private static final String _DOCUMENT_VERSION = String.valueOf(RandomTestUtil.randomInt());

    private static final String _JOURNAL_ARTICLE_CLASS_NAME = JournalArticle.class.getName();

    @Mock
    private Indexer<Object> _indexer;

    @Mock
    private IndexerRegistry _indexerRegistry;
}

