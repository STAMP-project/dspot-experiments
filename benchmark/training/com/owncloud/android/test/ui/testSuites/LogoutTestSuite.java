/**
 * ownCloud Android client application
 *
 * @author purigarcia
Copyright (C) 2016 ownCloud GmbH.
<p>
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 2,
as published by the Free Software Foundation.
<p>
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
<p>
You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.owncloud.android.test.ui.testSuites;


import com.owncloud.android.test.ui.actions.Actions;
import com.owncloud.android.test.ui.groups.NoIgnoreTestCategory;
import com.owncloud.android.test.ui.groups.SmokeTestCategory;
import com.owncloud.android.test.ui.models.FileListView;
import com.owncloud.android.test.ui.models.LoginForm;
import com.owncloud.android.test.ui.models.MenuList;
import com.owncloud.android.test.ui.models.SettingsView;
import io.appium.java_client.android.AndroidDriver;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


public class LogoutTestSuite {
    AndroidDriver driver;

    Common common;

    @Rule
    public TestName name = new TestName();

    @Test
    @Category({ NoIgnoreTestCategory.class, SmokeTestCategory.class })
    public void testLogout() throws Exception {
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        MenuList menulist = fileListView.clickOnMenuButton();
        SettingsView settingsView = menulist.clickOnSettingsButton();
        settingsView.tapOnAccountElement(1, 1, 1000);
        LoginForm loginForm = settingsView.clickOnDeleteAccountElement();
        Assert.assertEquals("Server address https://?", loginForm.gethostUrlInput().getText());
        Assert.assertEquals("Username", loginForm.getUserNameInput().getText());
        Assert.assertEquals("", loginForm.getPasswordInput().getText());
    }
}

