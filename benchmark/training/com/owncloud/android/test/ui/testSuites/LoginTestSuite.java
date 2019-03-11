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


import ScreenOrientation.LANDSCAPE;
import ScreenOrientation.PORTRAIT;
import com.owncloud.android.test.ui.actions.Actions;
import com.owncloud.android.test.ui.groups.NoIgnoreTestCategory;
import com.owncloud.android.test.ui.groups.SmokeTestCategory;
import com.owncloud.android.test.ui.models.FileListView;
import com.owncloud.android.test.ui.models.LoginForm;
import com.owncloud.android.test.ui.models.MenuList;
import com.owncloud.android.test.ui.models.SettingsView;
import io.appium.java_client.android.AndroidDriver;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginTestSuite {
    AndroidDriver driver;

    Common common;

    @Rule
    public TestName name = new TestName();

    @Test
    @Category({ NoIgnoreTestCategory.class })
    public void test1LoginPortrait() throws Exception {
        driver.rotate(PORTRAIT);
        Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
    }

    @Test
    @Category({ NoIgnoreTestCategory.class })
    public void test2LoginLandscape() throws Exception {
        driver.rotate(LANDSCAPE);
        Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
    }

    @Test
    @Category({ NoIgnoreTestCategory.class, SmokeTestCategory.class })
    public void testLoginAndShowFiles() throws Exception {
        driver.rotate(PORTRAIT);
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        fileListView.scrollTillFindElement(Config.fileWhichIsInTheServer1);
        Assert.assertTrue(fileListView.getFileElement().isDisplayed());
    }

    @Test
    @Category({ NoIgnoreTestCategory.class, SmokeTestCategory.class })
    public void test3MultiAccountRotate() throws Exception {
        driver.rotate(LANDSCAPE);
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        driver.rotate(PORTRAIT);
        MenuList menu = fileListView.clickOnMenuButton();
        SettingsView settingsView = menu.clickOnSettingsButton();
        settingsView.tapOnAddAccount(1, 1000);
        fileListView = Actions.login(Config.URL2, Config.user2, Config.password2, Config.isTrusted2, driver);
        common.assertIsInSettingsView();
    }

    @Test
    @Category({ NoIgnoreTestCategory.class, SmokeTestCategory.class })
    public void testMultiAccountAndShowFiles() throws Exception {
        driver.rotate(LANDSCAPE);
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        fileListView.scrollTillFindElement(Config.fileWhichIsInTheServer1);
        Assert.assertTrue(fileListView.getFileElement().isDisplayed());
        driver.rotate(PORTRAIT);
        MenuList menu = fileListView.clickOnMenuButton();
        SettingsView settingsView = menu.clickOnSettingsButton();
        settingsView.tapOnAddAccount(1, 1000);
        fileListView = Actions.login(Config.URL2, Config.user2, Config.password2, Config.isTrusted2, driver);
        common.assertIsInSettingsView();
        settingsView.tapOnAccountElement(2, 1, 100);
        common.assertIsInFileListView();
        fileListView.scrollTillFindElement(Config.fileWhichIsInTheServer2);
        Assert.assertTrue(fileListView.getFileElement().isDisplayed());
    }

    @Test
    @Category({ NoIgnoreTestCategory.class })
    public void test4ExistingAccountRotate() throws Exception {
        driver.rotate(PORTRAIT);
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        driver.rotate(LANDSCAPE);
        MenuList menu = fileListView.clickOnMenuButton();
        SettingsView settingsView = menu.clickOnSettingsButton();
        settingsView.tapOnAddAccount(1, 1000);
        LoginForm loginForm = new LoginForm(driver);
        fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        Assert.assertTrue(common.waitForTextPresent(("An account for the same user and" + " server already exists in the device"), loginForm.getAuthStatusText()));
    }

    @Test
    @Category({ NoIgnoreTestCategory.class })
    public void test5ChangePasswordWrong() throws Exception {
        driver.rotate(PORTRAIT);
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        MenuList menu = fileListView.clickOnMenuButton();
        SettingsView settingsView = menu.clickOnSettingsButton();
        settingsView.tapOnAccountElement(1, 1, 1000);
        LoginForm changePasswordForm = settingsView.clickOnChangePasswordElement();
        changePasswordForm.typePassword("WrongPassword");
        changePasswordForm.clickOnConnectButton();
        Assert.assertTrue(common.waitForTextPresent("Wrong username or password", changePasswordForm.getAuthStatusText()));
    }
}

