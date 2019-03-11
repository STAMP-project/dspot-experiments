/**
 * ownCloud Android client application
 *
 * @author purigarcia
Copyright (C) 2015 ownCloud Inc.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 2,
as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.owncloud.android.test.ui.testSuites;


import com.owncloud.android.test.ui.actions.Actions;
import com.owncloud.android.test.ui.groups.IgnoreTestCategory;
import com.owncloud.android.test.ui.groups.NoIgnoreTestCategory;
import com.owncloud.android.test.ui.groups.SmokeTestCategory;
import com.owncloud.android.test.ui.models.FileListView;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


public class ShareLinkFileTestSuite {
    AndroidDriver driver;

    Common common;

    private final String FILE_NAME = Config.fileToTestName;

    private Boolean fileHasBeenCreated = false;

    @Rule
    public TestName name = new TestName();

    @Test
    @Category({ NoIgnoreTestCategory.class })
    public void testShareLinkFileByGmail() throws Exception {
        AndroidElement sharedElementIndicator;
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        // TODO. if the file already exists, do not upload
        FileListView fileListViewAfterUploadFile = Actions.uploadFile(FILE_NAME, fileListView);
        fileListViewAfterUploadFile.scrollTillFindElement(FILE_NAME);
        Assert.assertTrue((fileHasBeenCreated = fileListViewAfterUploadFile.getFileElement().isDisplayed()));
        sharedElementIndicator = Actions.shareLinkElementByGmail(FILE_NAME, fileListViewAfterUploadFile, driver, common);
        Assert.assertTrue(sharedElementIndicator.isDisplayed());
    }

    @Test
    @Category({ NoIgnoreTestCategory.class, SmokeTestCategory.class })
    public void testShareLinkFileByCopyLink() throws Exception {
        AndroidElement sharedElementIndicator;
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        // TODO. if the file already exists, do not upload
        FileListView fileListViewAfterUploadFile = Actions.uploadFile(FILE_NAME, fileListView);
        fileListViewAfterUploadFile.scrollTillFindElement(FILE_NAME);
        Assert.assertTrue((fileHasBeenCreated = fileListViewAfterUploadFile.getFileElement().isDisplayed()));
        sharedElementIndicator = Actions.shareLinkElementByCopyLink(FILE_NAME, fileListViewAfterUploadFile, driver, common);
        Assert.assertTrue(sharedElementIndicator.isDisplayed());
    }

    @Test
    @Category({ IgnoreTestCategory.class, SmokeTestCategory.class })
    public void testUnshareLinkFile() throws Exception {
        AndroidElement sharedElementIndicator;
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        // TODO. if the file already exists, do not upload
        FileListView fileListViewAfterUploadFile = Actions.uploadFile(FILE_NAME, fileListView);
        fileListViewAfterUploadFile.scrollTillFindElement(FILE_NAME);
        Assert.assertTrue((fileHasBeenCreated = fileListViewAfterUploadFile.getFileElement().isDisplayed()));
        sharedElementIndicator = Actions.shareLinkElementByCopyLink(FILE_NAME, fileListViewAfterUploadFile, driver, common);
        Assert.assertTrue(sharedElementIndicator.isDisplayed());
        Actions.unshareLinkElement(FILE_NAME, fileListViewAfterUploadFile, driver, common);
        Assert.assertFalse(sharedElementIndicator.isDisplayed());
    }
}

