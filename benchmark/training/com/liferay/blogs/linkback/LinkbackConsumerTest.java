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
package com.liferay.blogs.linkback;


import com.liferay.blogs.web.internal.util.BlogsUtil;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
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
@PrepareForTest(BlogsUtil.class)
@RunWith(PowerMockRunner.class)
public class LinkbackConsumerTest extends PowerMockito {
    @Test
    public void testDeleteCommentIfBlogEntryURLNotInReferrer() throws Exception {
        String url = RandomTestUtil.randomString();
        Mockito.when(_http.URLtoString(url)).thenReturn(RandomTestUtil.randomString());
        long commentId = RandomTestUtil.randomLong();
        _linkbackConsumer.addNewTrackback(commentId, url, RandomTestUtil.randomString());
        _linkbackConsumer.verifyNewTrackbacks();
        Mockito.verify(_commentManager).deleteComment(commentId);
        Mockito.verify(_http).URLtoString(url);
    }

    @Test
    public void testDeleteCommentIfReferrerIsUnreachable() throws Exception {
        String url = RandomTestUtil.randomString();
        Mockito.doThrow(IOException.class).when(_http).URLtoString(url);
        long commentId = RandomTestUtil.randomLong();
        _linkbackConsumer.addNewTrackback(commentId, url, RandomTestUtil.randomString());
        _linkbackConsumer.verifyNewTrackbacks();
        Mockito.verify(_commentManager).deleteComment(commentId);
        Mockito.verify(_http).URLtoString(url);
    }

    @Test
    public void testPreserveCommentIfBlogEntryURLIsInReferrer() throws Exception {
        String url = RandomTestUtil.randomString();
        Mockito.when(_http.URLtoString(url)).thenReturn("__URLtoString_containing_**entryUrl**__");
        _linkbackConsumer.addNewTrackback(RandomTestUtil.randomLong(), url, "**entryUrl**");
        _linkbackConsumer.verifyNewTrackbacks();
        Mockito.verifyZeroInteractions(_commentManager);
        Mockito.verify(_http).URLtoString(url);
    }

    @Mock
    private CommentManager _commentManager;

    @Mock
    private Http _http;

    private LinkbackConsumer _linkbackConsumer;
}

