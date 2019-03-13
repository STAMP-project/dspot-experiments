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
package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;


import RoleConstants.TYPE_REGULAR;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;


/**
 *
 *
 * @author Leonardo Barros
 */
@RunWith(MockitoJUnitRunner.class)
public class BelongsToRoleFunctionTest extends PowerMockito {
    @Test
    public void testCatchPortalException() throws Exception {
        when(_roleLocalService.fetchRole(1, "test")).thenReturn(_role);
        when(_role.getType()).thenReturn(TYPE_REGULAR);
        when(_userLocalService.hasRoleUser(1, "test", 1, true)).thenThrow(new PortalException());
        DefaultDDMExpressionParameterAccessor ddmExpressionParameterAccessor = new DefaultDDMExpressionParameterAccessor();
        ddmExpressionParameterAccessor.setGetCompanyIdSupplier(() -> 1L);
        ddmExpressionParameterAccessor.setGetUserIdSupplier(() -> 1L);
        _belongsToRoleFunction.setDDMExpressionParameterAccessor(ddmExpressionParameterAccessor);
        Assert.assertFalse(_belongsToRoleFunction.apply(new String[]{ "test" }));
    }

    @Test
    public void testGuestRole() {
        when(_roleLocalService.fetchRole(1, "Guest")).thenReturn(_role);
        DefaultDDMExpressionParameterAccessor ddmExpressionParameterAccessor = new DefaultDDMExpressionParameterAccessor();
        ddmExpressionParameterAccessor.setGetCompanyIdSupplier(() -> 1L);
        _belongsToRoleFunction.setDDMExpressionParameterAccessor(ddmExpressionParameterAccessor);
        Assert.assertTrue(_belongsToRoleFunction.apply(new String[]{ "Guest" }));
    }

    @Test
    public void testNotGuestRole() {
        when(_roleLocalService.fetchRole(2, "test")).thenReturn(_role);
        DefaultDDMExpressionParameterAccessor ddmExpressionParameterAccessor = new DefaultDDMExpressionParameterAccessor();
        ddmExpressionParameterAccessor.setGetCompanyIdSupplier(() -> 2L);
        _belongsToRoleFunction.setDDMExpressionParameterAccessor(ddmExpressionParameterAccessor);
        Assert.assertFalse(_belongsToRoleFunction.apply(new String[]{ "test" }));
    }

    @Test
    public void testNullObserver() {
        Assert.assertFalse(_belongsToRoleFunction.apply(new String[]{ "test" }));
    }

    @Test
    public void testRegularRoleFalse() throws Exception {
        boolean result = _callTestRegularRole(false);
        Assert.assertFalse(result);
    }

    @Test
    public void testRegularRoleTrue() throws Exception {
        boolean result = _callTestRegularRole(true);
        Assert.assertTrue(result);
    }

    @Test
    public void testRoleNotFound() {
        when(_roleLocalService.fetchRole(1, "test")).thenReturn(null);
        DefaultDDMExpressionParameterAccessor ddmExpressionParameterAccessor = new DefaultDDMExpressionParameterAccessor();
        ddmExpressionParameterAccessor.setGetCompanyIdSupplier(() -> 1L);
        _belongsToRoleFunction.setDDMExpressionParameterAccessor(ddmExpressionParameterAccessor);
        Assert.assertFalse(_belongsToRoleFunction.apply(new String[]{ "test" }));
    }

    @Test
    public void testUserGroupRoleFalse() throws Exception {
        boolean result = _callTestUserGroupRole(false);
        Assert.assertFalse(result);
    }

    @Test
    public void testUserGroupRoleTrue() throws Exception {
        boolean result = _callTestUserGroupRole(true);
        Assert.assertTrue(result);
    }

    private BelongsToRoleFunction _belongsToRoleFunction = new BelongsToRoleFunction();

    @Mock
    private Role _role;

    @Mock
    private RoleLocalService _roleLocalService;

    @Mock
    private UserGroupRoleLocalService _userGroupRoleLocalService;

    @Mock
    private UserLocalService _userLocalService;
}

