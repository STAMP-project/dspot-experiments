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
package com.liferay.portal.workflow.task.web.internal.notifications;


import StringPool.BLANK;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author In?cio Nery
 */
public class WorkflowTaskUserNotificationHandlerTest {
    @Test
    public void testInvalidWorkflowTaskIdShouldReturnBlankBody() throws Exception {
        Assert.assertEquals(BLANK, WorkflowTaskUserNotificationHandlerTest._workflowTaskUserNotificationHandler.getBody(mockUserNotificationEvent(null, WorkflowTaskUserNotificationHandlerTest._INVALID_WORKFLOW_TASK_ID), WorkflowTaskUserNotificationHandlerTest._serviceContext));
    }

    @Test
    public void testInvalidWorkflowTaskIdShouldReturnLink() throws Exception {
        Assert.assertEquals(WorkflowTaskUserNotificationHandlerTest._VALID_LINK, WorkflowTaskUserNotificationHandlerTest._workflowTaskUserNotificationHandler.getLink(mockUserNotificationEvent(WorkflowTaskUserNotificationHandlerTest._VALID_ENTRY_CLASS_NAME, WorkflowTaskUserNotificationHandlerTest._INVALID_WORKFLOW_TASK_ID), WorkflowTaskUserNotificationHandlerTest._serviceContext));
    }

    @Test
    public void testNullWorkflowTaskIdShouldReturnBlankLink() throws Exception {
        Assert.assertEquals(BLANK, WorkflowTaskUserNotificationHandlerTest._workflowTaskUserNotificationHandler.getLink(mockUserNotificationEvent(WorkflowTaskUserNotificationHandlerTest._VALID_ENTRY_CLASS_NAME, 0), WorkflowTaskUserNotificationHandlerTest._serviceContext));
    }

    @Test
    public void testNullWorkflowTaskIdShouldReturnBody() throws Exception {
        Assert.assertEquals(WorkflowTaskUserNotificationHandlerTest._NOTIFICATION_MESSAGE, WorkflowTaskUserNotificationHandlerTest._workflowTaskUserNotificationHandler.getBody(mockUserNotificationEvent(null, 0), WorkflowTaskUserNotificationHandlerTest._serviceContext));
    }

    @Test
    public void testValidWorkflowTaskIdShouldReturnBody() throws Exception {
        Assert.assertEquals(WorkflowTaskUserNotificationHandlerTest._NOTIFICATION_MESSAGE, WorkflowTaskUserNotificationHandlerTest._workflowTaskUserNotificationHandler.getBody(mockUserNotificationEvent(null, WorkflowTaskUserNotificationHandlerTest._VALID_WORKFLOW_TASK_ID), WorkflowTaskUserNotificationHandlerTest._serviceContext));
    }

    @Test
    public void testValidWorkflowTaskIdShouldReturnLink() throws Exception {
        Assert.assertEquals(WorkflowTaskUserNotificationHandlerTest._VALID_LINK, WorkflowTaskUserNotificationHandlerTest._workflowTaskUserNotificationHandler.getLink(mockUserNotificationEvent(WorkflowTaskUserNotificationHandlerTest._VALID_ENTRY_CLASS_NAME, WorkflowTaskUserNotificationHandlerTest._VALID_WORKFLOW_TASK_ID), WorkflowTaskUserNotificationHandlerTest._serviceContext));
    }

    private static final Long _INVALID_WORKFLOW_TASK_ID = RandomTestUtil.randomLong();

    private static final String _NOTIFICATION_MESSAGE = RandomTestUtil.randomString();

    private static final String _VALID_ENTRY_CLASS_NAME = RandomTestUtil.randomString();

    private static final String _VALID_LINK = RandomTestUtil.randomString();

    private static final Long _VALID_WORKFLOW_TASK_ID = RandomTestUtil.randomLong();

    private static final ServiceContext _serviceContext = new ServiceContext() {
        @Override
        public ThemeDisplay getThemeDisplay() {
            return new ThemeDisplay() {
                {
                    setSiteGroupId(RandomTestUtil.randomLong());
                }
            };
        }
    };

    private static final WorkflowTaskUserNotificationHandler _workflowTaskUserNotificationHandler = new WorkflowTaskUserNotificationHandler();
}

