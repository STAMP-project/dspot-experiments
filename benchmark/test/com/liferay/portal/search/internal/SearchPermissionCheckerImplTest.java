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
package com.liferay.portal.search.internal;


import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchPermissionChecker;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.configuration.SearchPermissionCheckerConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class SearchPermissionCheckerImplTest {
    @Test
    public void testNullInput() {
        Assert.assertNull(_searchPermissionChecker.getPermissionBooleanFilter(0, null, 0, null, null, null));
    }

    @Test
    public void testPermissionFilterTakesOverNullInputFilter() throws Exception {
        long userId = RandomTestUtil.randomLong();
        whenIndexerIsPermissionAware(true);
        whenPermissionCheckerGetUser(_user);
        whenPermissionCheckerGetUserBag(_userBag);
        whenUserGetUserId(userId);
        BooleanFilter booleanFilter = null;
        BooleanFilter permissionBooleanFilter = _searchPermissionChecker.getPermissionBooleanFilter(0, null, userId, null, booleanFilter, new SearchContext());
        Assert.assertNotNull(permissionBooleanFilter);
    }

    @Mock
    private Indexer _indexer;

    @Mock
    private IndexerRegistry _indexerRegistry;

    @Mock
    private PermissionChecker _permissionChecker;

    @Mock
    private ResourcePermissionLocalService _resourcePermissionLocalService;

    @Mock
    private RoleLocalService _roleLocalService;

    private SearchPermissionChecker _searchPermissionChecker;

    @Mock
    private SearchPermissionCheckerConfiguration _searchPermissionCheckerConfiguration;

    @Mock
    private User _user;

    @Mock
    private UserBag _userBag;

    @Mock
    private UserLocalService _userLocalService;
}

