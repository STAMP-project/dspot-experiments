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
package com.liferay.message.boards.comment.internal;


import StringPool.BLANK;
import WorkflowConstants.ACTION_PUBLISH;
import WorkflowConstants.STATUS_APPROVED;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.comment.DuplicateCommentException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class MBCommentManagerImplTest extends Mockito {
    @Test
    public void testAddComment() throws Exception {
        _mbCommentManagerImpl.addComment(MBCommentManagerImplTest._USER_ID, MBCommentManagerImplTest._GROUP_ID, MBCommentManagerImplTest._CLASS_NAME, MBCommentManagerImplTest._ENTRY_ID, MBCommentManagerImplTest._BODY, _serviceContextFunction);
        Mockito.verify(_mbMessageLocalService).addDiscussionMessage(MBCommentManagerImplTest._USER_ID, BLANK, MBCommentManagerImplTest._GROUP_ID, MBCommentManagerImplTest._CLASS_NAME, MBCommentManagerImplTest._ENTRY_ID, MBCommentManagerImplTest._THREAD_ID, MBCommentManagerImplTest._ROOT_MESSAGE_ID, BLANK, MBCommentManagerImplTest._BODY, _serviceContext);
        Mockito.verify(_mbMessageLocalService).getThreadMessages(MBCommentManagerImplTest._THREAD_ID, STATUS_APPROVED);
    }

    @Test
    public void testAddCommentWithUserNameAndSubject() throws Exception {
        Mockito.when(_mbMessage.getMessageId()).thenReturn(MBCommentManagerImplTest._MBMESSAGE_ID);
        Assert.assertEquals(MBCommentManagerImplTest._MBMESSAGE_ID, _mbCommentManagerImpl.addComment(MBCommentManagerImplTest._USER_ID, MBCommentManagerImplTest._GROUP_ID, MBCommentManagerImplTest._CLASS_NAME, MBCommentManagerImplTest._ENTRY_ID, MBCommentManagerImplTest._USER_NAME, MBCommentManagerImplTest._SUBJECT, MBCommentManagerImplTest._BODY, _serviceContextFunction));
        Mockito.verify(_mbMessageLocalService).addDiscussionMessage(MBCommentManagerImplTest._USER_ID, MBCommentManagerImplTest._USER_NAME, MBCommentManagerImplTest._GROUP_ID, MBCommentManagerImplTest._CLASS_NAME, MBCommentManagerImplTest._ENTRY_ID, MBCommentManagerImplTest._THREAD_ID, MBCommentManagerImplTest._ROOT_MESSAGE_ID, MBCommentManagerImplTest._SUBJECT, MBCommentManagerImplTest._BODY, _serviceContext);
        Mockito.verify(_mbMessageLocalService).getDiscussionMessageDisplay(MBCommentManagerImplTest._USER_ID, MBCommentManagerImplTest._GROUP_ID, MBCommentManagerImplTest._CLASS_NAME, MBCommentManagerImplTest._ENTRY_ID, STATUS_APPROVED);
    }

    @Test
    public void testAddDiscussion() throws Exception {
        _mbCommentManagerImpl.addDiscussion(MBCommentManagerImplTest._USER_ID, MBCommentManagerImplTest._GROUP_ID, MBCommentManagerImplTest._CLASS_NAME, MBCommentManagerImplTest._ENTRY_ID, MBCommentManagerImplTest._USER_NAME);
        Mockito.verify(_mbMessageLocalService).addDiscussionMessage(MBCommentManagerImplTest._USER_ID, MBCommentManagerImplTest._USER_NAME, MBCommentManagerImplTest._GROUP_ID, MBCommentManagerImplTest._CLASS_NAME, MBCommentManagerImplTest._ENTRY_ID, ACTION_PUBLISH);
    }

    @Test(expected = DuplicateCommentException.class)
    public void testAddDuplicateComment() throws Exception {
        setUpExistingComment(MBCommentManagerImplTest._BODY);
        _mbCommentManagerImpl.addComment(MBCommentManagerImplTest._USER_ID, MBCommentManagerImplTest._GROUP_ID, MBCommentManagerImplTest._CLASS_NAME, MBCommentManagerImplTest._ENTRY_ID, MBCommentManagerImplTest._BODY, _serviceContextFunction);
    }

    @Test
    public void testAddUniqueComment() throws Exception {
        setUpExistingComment(((MBCommentManagerImplTest._BODY) + (RandomTestUtil.randomString())));
        _mbCommentManagerImpl.addComment(MBCommentManagerImplTest._USER_ID, MBCommentManagerImplTest._GROUP_ID, MBCommentManagerImplTest._CLASS_NAME, MBCommentManagerImplTest._ENTRY_ID, MBCommentManagerImplTest._BODY, _serviceContextFunction);
        Mockito.verify(_mbMessageLocalService).addDiscussionMessage(MBCommentManagerImplTest._USER_ID, BLANK, MBCommentManagerImplTest._GROUP_ID, MBCommentManagerImplTest._CLASS_NAME, MBCommentManagerImplTest._ENTRY_ID, MBCommentManagerImplTest._THREAD_ID, MBCommentManagerImplTest._ROOT_MESSAGE_ID, BLANK, MBCommentManagerImplTest._BODY, _serviceContext);
    }

    @Test
    public void testDeleteComment() throws Exception {
        _mbCommentManagerImpl.deleteComment(MBCommentManagerImplTest._MBMESSAGE_ID);
        Mockito.verify(_mbMessageLocalService).deleteDiscussionMessage(MBCommentManagerImplTest._MBMESSAGE_ID);
    }

    @Test
    public void testDeleteDiscussion() throws Exception {
        long classPK = RandomTestUtil.randomLong();
        _mbCommentManagerImpl.deleteDiscussion(MBCommentManagerImplTest._CLASS_NAME, classPK);
        Mockito.verify(_mbMessageLocalService).deleteDiscussionMessages(MBCommentManagerImplTest._CLASS_NAME, classPK);
    }

    @Test
    public void testFetchComment() {
        long commentId = RandomTestUtil.randomLong();
        _mbCommentManagerImpl.fetchComment(commentId);
        Mockito.verify(_mbMessageLocalService).fetchMBMessage(commentId);
    }

    @Test
    public void testGetCommentsCount() {
        long classPK = RandomTestUtil.randomLong();
        long classNameId = RandomTestUtil.randomLong();
        int commentsCount = RandomTestUtil.randomInt();
        Mockito.when(_mbMessageLocalService.getDiscussionMessagesCount(classNameId, classPK, STATUS_APPROVED)).thenReturn(commentsCount);
        Mockito.when(_portal.getClassNameId(MBCommentManagerImplTest._CLASS_NAME)).thenReturn(classNameId);
        Assert.assertEquals(commentsCount, _mbCommentManagerImpl.getCommentsCount(MBCommentManagerImplTest._CLASS_NAME, classPK));
    }

    private static final String _BODY = RandomTestUtil.randomString();

    private static final String _CLASS_NAME = RandomTestUtil.randomString();

    private static final long _ENTRY_ID = RandomTestUtil.randomLong();

    private static final long _GROUP_ID = RandomTestUtil.randomLong();

    private static final long _MBMESSAGE_ID = RandomTestUtil.randomLong();

    private static final long _ROOT_MESSAGE_ID = RandomTestUtil.randomLong();

    private static final String _SUBJECT = RandomTestUtil.randomString();

    private static final long _THREAD_ID = RandomTestUtil.randomLong();

    private static final long _USER_ID = RandomTestUtil.randomLong();

    private static final String _USER_NAME = RandomTestUtil.randomString();

    private final MBCommentManagerImpl _mbCommentManagerImpl = new MBCommentManagerImpl();

    @Mock
    private MBMessage _mbMessage;

    @Mock
    private MBMessageDisplay _mbMessageDisplay;

    @Mock
    private MBMessageLocalService _mbMessageLocalService;

    @Mock
    private MBThread _mbThread;

    @Mock
    private Portal _portal;

    private final ServiceContext _serviceContext = new ServiceContext();

    @Mock
    private Function<String, ServiceContext> _serviceContextFunction;
}

