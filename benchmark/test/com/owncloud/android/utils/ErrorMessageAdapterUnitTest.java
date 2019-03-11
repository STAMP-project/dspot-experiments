/**
 * ownCloud Android client application
 *
 *   @author David A. Velasco
 *   Copyright (C) 2016 ownCloud Inc.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2,
 *   as published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.owncloud.android.utils;


import R.string.forbidden_permissions;
import R.string.forbidden_permissions_delete;
import RemoteOperationResult.ResultCode;
import android.accounts.Account;
import android.content.res.Resources;
import com.owncloud.android.MainApp;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Local unit test, to be run out of Android emulator or device.
 *
 * At the moment, it's a sample to validate the automatic test environment, in the scope of local unit tests with
 * mock Android dependencies.
 *
 * Don't take it as an example of completeness.
 *
 * See http://developer.android.com/intl/es/training/testing/unit-testing/local-unit-tests.html .
 */
@RunWith(MockitoJUnitRunner.class)
public class ErrorMessageAdapterUnitTest {
    private static final String MOCK_FORBIDDEN_PERMISSIONS = "You do not have permission %s";

    private static final String MOCK_TO_DELETE = "to delete this file";

    private static final String PATH_TO_DELETE = "/path/to/a.file";

    private static final String EXPECTED_ERROR_MESSAGE = "You do not have permission to delete this file";

    private static final String ACCOUNT_TYPE = "nextcloud";

    @Mock
    private Resources mMockResources;

    @Test
    public void getErrorCauseMessageForForbiddenRemoval() {
        // Given a mocked set of resources passed to the object under test...
        Mockito.when(mMockResources.getString(forbidden_permissions)).thenReturn(ErrorMessageAdapterUnitTest.MOCK_FORBIDDEN_PERMISSIONS);
        Mockito.when(mMockResources.getString(forbidden_permissions_delete)).thenReturn(ErrorMessageAdapterUnitTest.MOCK_TO_DELETE);
        Account account = new Account("name", ErrorMessageAdapterUnitTest.ACCOUNT_TYPE);
        // ... when method under test is called ...
        String errorMessage = ErrorMessageAdapter.getErrorCauseMessage(new com.owncloud.android.lib.common.operations.RemoteOperationResult(ResultCode.FORBIDDEN), new com.owncloud.android.operations.RemoveFileOperation(ErrorMessageAdapterUnitTest.PATH_TO_DELETE, false, account, false, MainApp.getAppContext()), mMockResources);
        // ... then the result should be the expected one.
        MatcherAssert.assertThat(errorMessage, CoreMatchers.is(ErrorMessageAdapterUnitTest.EXPECTED_ERROR_MESSAGE));
    }
}

