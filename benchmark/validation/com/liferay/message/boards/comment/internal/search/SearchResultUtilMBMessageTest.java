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
package com.liferay.message.boards.comment.internal.search;


import SearchTestUtil.ATTACHMENT_OWNER_CLASS_NAME;
import SearchTestUtil.ATTACHMENT_OWNER_CLASS_PK;
import SearchTestUtil.ENTRY_CLASS_PK;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.RelatedSearchResult;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.test.BaseSearchResultUtilTestCase;
import com.liferay.portal.kernel.search.test.SearchTestUtil;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Andr? de Oliveira
 */
@RunWith(PowerMockRunner.class)
public class SearchResultUtilMBMessageTest extends BaseSearchResultUtilTestCase {
    @Test
    public void testMBMessage() throws Exception {
        SearchResult searchResult = assertOneSearchResult(SearchTestUtil.createDocument(SearchResultUtilMBMessageTest._MB_MESSAGE_CLASS_NAME));
        Assert.assertEquals(SearchResultUtilMBMessageTest._MB_MESSAGE_CLASS_NAME, searchResult.getClassName());
        Assert.assertEquals(ENTRY_CLASS_PK, searchResult.getClassPK());
        List<RelatedSearchResult<Comment>> commentRelatedSearchResults = searchResult.getCommentRelatedSearchResults();
        Assert.assertTrue(commentRelatedSearchResults.toString(), commentRelatedSearchResults.isEmpty());
        Mockito.verifyZeroInteractions(_mbMessageLocalService);
        Assert.assertNull(searchResult.getSummary());
        assertEmptyFileEntryRelatedSearchResults(searchResult);
        assertEmptyVersions(searchResult);
    }

    @Test
    public void testMBMessageAttachment() throws Exception {
        SearchResult searchResult = assertOneSearchResult(SearchTestUtil.createAttachmentDocument(SearchResultUtilMBMessageTest._MB_MESSAGE_CLASS_NAME));
        Assert.assertEquals(ATTACHMENT_OWNER_CLASS_NAME, searchResult.getClassName());
        Assert.assertEquals(ATTACHMENT_OWNER_CLASS_PK, searchResult.getClassPK());
        List<RelatedSearchResult<Comment>> relatedSearchResults = searchResult.getCommentRelatedSearchResults();
        RelatedSearchResult<Comment> relatedSearchResult = relatedSearchResults.get(0);
        Comment comment = relatedSearchResult.getModel();
        Assert.assertEquals(_mbMessage.getMessageId(), comment.getCommentId());
        Assert.assertEquals(relatedSearchResults.toString(), 1, relatedSearchResults.size());
        Assert.assertNull(searchResult.getSummary());
        assertEmptyFileEntryRelatedSearchResults(searchResult);
        assertEmptyVersions(searchResult);
    }

    @Test
    public void testTwoDocumentsWithSameAttachmentOwner() {
        Document document1 = SearchTestUtil.createAttachmentDocument(SearchResultUtilMBMessageTest._MB_MESSAGE_CLASS_NAME, ENTRY_CLASS_PK);
        Document document2 = SearchTestUtil.createAttachmentDocument(SearchResultUtilMBMessageTest._MB_MESSAGE_CLASS_NAME, ((SearchTestUtil.ENTRY_CLASS_PK) + 1));
        List<SearchResult> searchResults = SearchTestUtil.getSearchResults(searchResultTranslator, document1, document2);
        Assert.assertEquals(searchResults.toString(), 1, searchResults.size());
        SearchResult searchResult = searchResults.get(0);
        Assert.assertEquals(searchResult.getClassName(), ATTACHMENT_OWNER_CLASS_NAME);
        Assert.assertEquals(searchResult.getClassPK(), ATTACHMENT_OWNER_CLASS_PK);
    }

    private static final String _MB_MESSAGE_CLASS_NAME = MBMessage.class.getName();

    @Mock
    private Comment _comment;

    @Mock
    private CommentManager _commentManager;

    @Mock
    private IndexerRegistry _indexerRegistry;

    @Mock
    private MBMessage _mbMessage;

    @Mock
    private MBMessageLocalService _mbMessageLocalService;
}

