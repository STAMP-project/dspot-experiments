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
package com.liferay.portal.workflow.task.web.internal.permission;


import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Adam Brandizzi
 */
public class WorkflowTaskPermissionCheckerTest {
    @Test
    public void testCompanyAdminHasPermission() {
        Assert.assertTrue(_workflowTaskPermissionChecker.hasPermission(RandomTestUtil.randomLong(), mockWorkflowTask(), mockCompanyAdminPermissionChecker()));
    }

    @Test
    public void testContentReviewerHasPermission() {
        PermissionChecker permissionChecker = mockContentReviewerPermissionChecker(RandomTestUtil.randomLong());
        Assert.assertTrue(_workflowTaskPermissionChecker.hasPermission(RandomTestUtil.randomLong(), mockWorkflowTask(User.class.getName(), permissionChecker.getUserId()), permissionChecker));
    }

    @Test
    public void testContentReviewerRoleHasPermission() {
        long[] permissionCheckerRoleIds = randomPermissionCheckerRoleIds();
        Assert.assertTrue(_workflowTaskPermissionChecker.hasPermission(RandomTestUtil.randomLong(), mockWorkflowTask(Role.class.getName(), permissionCheckerRoleIds[0]), mockContentReviewerPermissionChecker(RandomTestUtil.randomLong(), permissionCheckerRoleIds)));
    }

    @Test
    public void testContentReviewerRoleWithAssetViewPermissionHasPermission() throws PortalException {
        mockAssetRendererHasViewPermission(true);
        long[] permissionCheckerRoleIds = randomPermissionCheckerRoleIds();
        Assert.assertTrue(_workflowTaskPermissionChecker.hasPermission(RandomTestUtil.randomLong(), mockWorkflowTask(Role.class.getName(), permissionCheckerRoleIds[0]), mockPermissionChecker(RandomTestUtil.randomLong(), permissionCheckerRoleIds, false, false, false)));
    }

    @Test
    public void testNotAssigneeHasNoPermission() {
        long assigneeUserId = RandomTestUtil.randomLong();
        Assert.assertFalse(_workflowTaskPermissionChecker.hasPermission(assigneeUserId, mockWorkflowTask(User.class.getName(), assigneeUserId), mockContentReviewerPermissionChecker(RandomTestUtil.randomLong())));
    }

    @Test
    public void testNotAssigneeRoleHasNoPermission() {
        long assigneeRoleId = RandomTestUtil.randomLong();
        Assert.assertFalse(_workflowTaskPermissionChecker.hasPermission(assigneeRoleId, mockWorkflowTask(Role.class.getName(), assigneeRoleId), mockContentReviewerPermissionChecker(RandomTestUtil.randomLong())));
    }

    @Test
    public void testNotContentReviewerHasNoPermission() {
        Assert.assertFalse(_workflowTaskPermissionChecker.hasPermission(RandomTestUtil.randomLong(), mockWorkflowTask(), mockPermissionChecker(RandomTestUtil.randomLong(), new long[0], false, false, false)));
    }

    @Test
    public void testNotContentReviewerWithAssetViewPermissionHasNoPermission() throws PortalException {
        mockAssetRendererHasViewPermission(true);
        Assert.assertFalse(_workflowTaskPermissionChecker.hasPermission(RandomTestUtil.randomLong(), mockWorkflowTask(), mockPermissionChecker(RandomTestUtil.randomLong(), new long[0], false, false, false)));
    }

    @Test
    public void testNotContentReviewerWithAssetViewPermissionHasPermission() throws PortalException {
        // Checks permission on completed workflow task
        mockAssetRendererHasViewPermission(true);
        Assert.assertTrue(_workflowTaskPermissionChecker.hasPermission(RandomTestUtil.randomLong(), mockCompletedWorkflowTask(), mockPermissionChecker(RandomTestUtil.randomLong(), new long[0], false, false, false)));
    }

    @Test
    public void testNotContentReviewerWithNoAssetViewPermissionHasNoPermission() throws PortalException {
        long[] permissionCheckerRoleIds = randomPermissionCheckerRoleIds();
        mockAssetRendererHasViewPermission(false);
        Assert.assertFalse(_workflowTaskPermissionChecker.hasPermission(RandomTestUtil.randomLong(), mockWorkflowTask(Role.class.getName(), permissionCheckerRoleIds[0]), mockPermissionChecker(RandomTestUtil.randomLong(), permissionCheckerRoleIds, false, false, false)));
    }

    @Test
    public void testNotContentReviewerWithoutAssetViewPermissionHasNoPermission() throws PortalException {
        // Checks permission on completed workflow task
        mockAssetRendererHasViewPermission(false);
        Assert.assertFalse(_workflowTaskPermissionChecker.hasPermission(RandomTestUtil.randomLong(), mockCompletedWorkflowTask(), mockPermissionChecker(RandomTestUtil.randomLong(), new long[0], false, false, false)));
    }

    @Test
    public void testOmniadminHasPermission() {
        Assert.assertTrue(_workflowTaskPermissionChecker.hasPermission(RandomTestUtil.randomLong(), mockWorkflowTask(), mockOmniadminPermissionChecker()));
    }

    private static final String _TEST_CONTEXT_ENTRY_CLASS_NAME = "TEST_CONTEXT_ENTRY_CLASS_NAME";

    private final WorkflowTaskPermissionChecker _workflowTaskPermissionChecker = new WorkflowTaskPermissionChecker();
}

