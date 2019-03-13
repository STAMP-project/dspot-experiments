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
package com.liferay.document.library.internal.search;


import SearchTestUtil.ATTACHMENT_OWNER_CLASS_NAME;
import SearchTestUtil.ATTACHMENT_OWNER_CLASS_PK;
import SearchTestUtil.ENTRY_CLASS_PK;
import SearchTestUtil.SUMMARY_CONTENT;
import SearchTestUtil.SUMMARY_TITLE;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.RelatedSearchResult;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.test.BaseSearchResultUtilTestCase;
import com.liferay.portal.kernel.search.test.SearchTestUtil;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.internal.result.SearchResultTranslatorImpl;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Andr? de Oliveira
 */
@PrepareForTest(AssetRendererFactoryRegistryUtil.class)
@RunWith(PowerMockRunner.class)
public class SearchResultUtilDLFileEntryTest extends BaseSearchResultUtilTestCase {
    @Test
    public void testDLFileEntry() throws Exception {
        SearchResult searchResult = assertOneSearchResult(SearchTestUtil.createDocument(SearchResultUtilDLFileEntryTest._DL_FILE_ENTRY_CLASS_NAME));
        Assert.assertEquals(SearchResultUtilDLFileEntryTest._DL_FILE_ENTRY_CLASS_NAME, searchResult.getClassName());
        Assert.assertEquals(ENTRY_CLASS_PK, searchResult.getClassPK());
        assertEmptyFileEntryRelatedSearchResults(searchResult);
        Assert.assertNull(searchResult.getSummary());
        PowerMockito.verifyZeroInteractions(_dlAppLocalService);
        assertEmptyCommentRelatedSearchResults(searchResult);
        assertEmptyVersions(searchResult);
    }

    @Test
    public void testDLFileEntryAttachment() throws Exception {
        Mockito.when(_assetRenderer.getSearchSummary(((Locale) (Matchers.any())))).thenReturn(SUMMARY_CONTENT);
        Mockito.when(_assetRenderer.getTitle(((Locale) (Matchers.any())))).thenReturn(SUMMARY_TITLE);
        PowerMockito.replace(PowerMockito.method(AssetRendererFactoryRegistryUtil.class, "getAssetRendererFactoryByClassName", String.class)).with(new InvocationHandler() {
            @Override
            public AssetRendererFactory<?> invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String className = ((String) (args[0]));
                if (SearchResultUtilDLFileEntryTest._DL_FILE_ENTRY_CLASS_NAME.equals(className)) {
                    return null;
                }
                if (ATTACHMENT_OWNER_CLASS_NAME.equals(className)) {
                    return _assetRendererFactory;
                }
                throw new IllegalArgumentException();
            }
        });
        Mockito.doReturn(_assetRenderer).when(_assetRendererFactory).getAssetRenderer(ATTACHMENT_OWNER_CLASS_PK);
        Mockito.when(_dlAppLocalService.getFileEntry(ENTRY_CLASS_PK)).thenReturn(_fileEntry);
        Mockito.doThrow(new IllegalArgumentException()).when(_indexerRegistry).getIndexer(Mockito.anyString());
        Mockito.doReturn(_indexer).when(_indexerRegistry).getIndexer(SearchResultUtilDLFileEntryTest._DL_FILE_ENTRY_CLASS_NAME);
        Mockito.doReturn(null).when(_indexerRegistry).getIndexer(ATTACHMENT_OWNER_CLASS_NAME);
        String title = RandomTestUtil.randomString();
        String content = RandomTestUtil.randomString();
        Summary summary = new Summary(null, title, content);
        Mockito.doReturn(summary).when(_indexer).getSummary(((Document) (Matchers.any())), Matchers.anyString(), ((PortletRequest) (Matchers.isNull())), ((PortletResponse) (Matchers.isNull())));
        SearchResult searchResult = assertOneSearchResult(SearchTestUtil.createAttachmentDocument(SearchResultUtilDLFileEntryTest._DL_FILE_ENTRY_CLASS_NAME));
        Assert.assertEquals(ATTACHMENT_OWNER_CLASS_NAME, searchResult.getClassName());
        Assert.assertEquals(ATTACHMENT_OWNER_CLASS_PK, searchResult.getClassPK());
        Summary searchResultSummary = searchResult.getSummary();
        Assert.assertNotSame(summary, searchResultSummary);
        Assert.assertEquals(SUMMARY_CONTENT, searchResultSummary.getContent());
        Assert.assertEquals(SUMMARY_TITLE, searchResultSummary.getTitle());
        List<RelatedSearchResult<FileEntry>> relatedSearchResults = searchResult.getFileEntryRelatedSearchResults();
        Assert.assertEquals(relatedSearchResults.toString(), 1, relatedSearchResults.size());
        RelatedSearchResult<FileEntry> relatedSearchResult = relatedSearchResults.get(0);
        FileEntry relatedSearchResultFileEntry = relatedSearchResult.getModel();
        Assert.assertSame(_fileEntry, relatedSearchResultFileEntry);
        Summary relatedSearchResultSummary = relatedSearchResult.getSummary();
        Assert.assertSame(summary, relatedSearchResultSummary);
        Assert.assertEquals(content, relatedSearchResultSummary.getContent());
        Assert.assertEquals(title, relatedSearchResultSummary.getTitle());
        assertEmptyCommentRelatedSearchResults(searchResult);
        assertEmptyVersions(searchResult);
    }

    @Test
    public void testDLFileEntryMissing() throws Exception {
        Mockito.when(_dlAppLocalService.getFileEntry(ENTRY_CLASS_PK)).thenReturn(null);
        SearchResult searchResult = assertOneSearchResult(SearchTestUtil.createAttachmentDocument(SearchResultUtilDLFileEntryTest._DL_FILE_ENTRY_CLASS_NAME));
        Assert.assertEquals(ATTACHMENT_OWNER_CLASS_NAME, searchResult.getClassName());
        Assert.assertEquals(ATTACHMENT_OWNER_CLASS_PK, searchResult.getClassPK());
        assertEmptyFileEntryRelatedSearchResults(searchResult);
        Mockito.verify(_dlAppLocalService).getFileEntry(ENTRY_CLASS_PK);
        Assert.assertNull(searchResult.getSummary());
        Mockito.verify(_indexerRegistry).getIndexer(ATTACHMENT_OWNER_CLASS_NAME);
        PowerMockito.verifyStatic(Mockito.atLeastOnce());
        AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(ATTACHMENT_OWNER_CLASS_NAME);
        assertEmptyCommentRelatedSearchResults(searchResult);
        assertEmptyVersions(searchResult);
    }

    @Test
    public void testDLFileEntryWithBrokenIndexer() throws Exception {
        Mockito.when(_dlAppLocalService.getFileEntry(ENTRY_CLASS_PK)).thenReturn(_fileEntry);
        Mockito.doThrow(new IllegalArgumentException()).when(_indexer).getSummary(((Document) (Matchers.any())), Matchers.anyString(), ((PortletRequest) (Matchers.any())), ((PortletResponse) (Matchers.any())));
        Mockito.when(_indexerRegistry.getIndexer(Mockito.anyString())).thenReturn(_indexer);
        Document document = SearchTestUtil.createAttachmentDocument(SearchResultUtilDLFileEntryTest._DL_FILE_ENTRY_CLASS_NAME);
        String snippet = RandomTestUtil.randomString();
        document.add(new Field(Field.SNIPPET, snippet));
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(SearchResultTranslatorImpl.class.getName(), Level.WARNING)) {
            SearchResult searchResult = assertOneSearchResult(document);
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            Assert.assertEquals(logRecords.toString(), 1, logRecords.size());
            LogRecord logRecord = logRecords.get(0);
            long entryClassPK = GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK));
            Assert.assertEquals((("Search index is stale and contains entry {" + entryClassPK) + "}"), logRecord.getMessage());
            Assert.assertEquals(ATTACHMENT_OWNER_CLASS_NAME, searchResult.getClassName());
            Assert.assertEquals(ATTACHMENT_OWNER_CLASS_PK, searchResult.getClassPK());
            Assert.assertNull(searchResult.getSummary());
            Mockito.verify(_indexerRegistry).getIndexer(SearchResultUtilDLFileEntryTest._DL_FILE_ENTRY_CLASS_NAME);
            Mockito.verify(_indexer).getSummary(document, snippet, null, null);
            assertEmptyFileEntryRelatedSearchResults(searchResult);
            Mockito.verify(_dlAppLocalService).getFileEntry(ENTRY_CLASS_PK);
            assertEmptyCommentRelatedSearchResults(searchResult);
            assertEmptyVersions(searchResult);
        }
    }

    private static final String _DL_FILE_ENTRY_CLASS_NAME = DLFileEntry.class.getName();

    @Mock
    @SuppressWarnings("rawtypes")
    private AssetRenderer _assetRenderer;

    @Mock
    private AssetRendererFactory<?> _assetRendererFactory;

    @Mock
    private DLAppLocalService _dlAppLocalService;

    @Mock
    private FileEntry _fileEntry;

    @Mock
    private Indexer<Object> _indexer;

    @Mock
    private IndexerRegistry _indexerRegistry;
}

