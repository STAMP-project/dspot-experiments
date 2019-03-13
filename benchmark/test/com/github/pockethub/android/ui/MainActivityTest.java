/**
 * Copyright (c) 2015 PocketHub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pockethub.android.ui;


import Build.VERSION;
import Build.VERSION_CODES;
import R.id.navigation_bookmarks;
import R.id.navigation_gists;
import R.id.navigation_home;
import R.id.navigation_issue_dashboard;
import R.id.navigation_log_out;
import R.string.app_name;
import android.accounts.Account;
import androidx.test.core.app.ApplicationProvider;
import com.github.pockethub.android.AccountManagerShadow;
import com.github.pockethub.android.ui.gist.GistsPagerFragment;
import com.github.pockethub.android.ui.issue.FilterListFragment;
import com.github.pockethub.android.ui.issue.IssueDashboardPagerFragment;
import com.github.pockethub.android.ui.user.HomePagerFragment;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(shadows = AccountManagerShadow.class)
public class MainActivityTest {
    private MainActivity mainActivity;

    private ArgumentCaptor<Account> argumentCaptor;

    @Test
    public void testNavigationDrawerClickListenerPos1_ShouldReplaceHomePagerFragmentToContainer() {
        mainActivity.onNavigationItemSelected(getMockMenuItem(navigation_home, "HomeTitle"));
        String expectedString = ApplicationProvider.getApplicationContext().getString(app_name);
        assertFragmentInstanceAndSupportActionBarTitle(HomePagerFragment.class, expectedString);
    }

    @Test
    public void testNavigationDrawerClickListenerPos2_ShouldReplaceGistsPagerFragmentToContainer() {
        mainActivity.onNavigationItemSelected(getMockMenuItem(navigation_gists, "GistTitle"));
        assertFragmentInstanceAndSupportActionBarTitle(GistsPagerFragment.class, "GistTitle");
    }

    @Test
    public void testNavigationDrawerClickListenerPos3_ShouldReplaceIssueDashboardPagerFragmentToContainer() {
        mainActivity.onNavigationItemSelected(getMockMenuItem(navigation_issue_dashboard, "IssueDashboard"));
        assertFragmentInstanceAndSupportActionBarTitle(IssueDashboardPagerFragment.class, "IssueDashboard");
    }

    @Test
    public void testNavigationDrawerClickListenerPos4_ShouldReplaceFilterListFragmentToContainer() {
        mainActivity.onNavigationItemSelected(getMockMenuItem(navigation_bookmarks, "Bookmarks"));
        assertFragmentInstanceAndSupportActionBarTitle(FilterListFragment.class, "Bookmarks");
    }

    @Test
    public void testNavigationDrawerClickListenerPos5_ShouldLogoutUser() {
        mainActivity.onNavigationItemSelected(getMockMenuItem(navigation_log_out, "Logout"));
        if ((VERSION.SDK_INT) >= (VERSION_CODES.LOLLIPOP_MR1)) {
            Mockito.verify(AccountManagerShadow.mockManager, Mockito.times(2)).removeAccount(argumentCaptor.capture(), ArgumentMatchers.eq(mainActivity), ArgumentMatchers.any(), ArgumentMatchers.any());
        } else {
            Mockito.verify(AccountManagerShadow.mockManager, Mockito.times(2)).removeAccount(argumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }
        List<Account> values = argumentCaptor.getAllValues();
        Assert.assertThat(values.get(0), CoreMatchers.is(CoreMatchers.equalTo(AccountManagerShadow.accounts[0])));
        Assert.assertThat(values.get(1), CoreMatchers.is(CoreMatchers.equalTo(AccountManagerShadow.accounts[1])));
    }
}

