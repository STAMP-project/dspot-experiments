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
import io.appium.java_client.android.AndroidDriver;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteFileTestSuite {
    AndroidDriver driver;

    Common common;

    private final String FILE_NAME = Config.fileToTestName;

    @Rule
    public TestName name = new TestName();

    @Test
    @Category({ NoIgnoreTestCategory.class, SmokeTestCategory.class })
    public void testDeleteFile() throws Exception {
        FileListView fileListView = Actions.login(Config.URL, Config.user, Config.password, Config.isTrusted, driver);
        common.assertIsInFileListView();
        // TODO. if the file already exists, do not upload
        FileListView fileListViewAfterUploadFile = Actions.uploadFile(FILE_NAME, fileListView);
        fileListViewAfterUploadFile.scrollTillFindElement(FILE_NAME);
        Common.waitTillElementIsNotPresentWithoutTimeout(fileListViewAfterUploadFile.getProgressCircular(), 1000);
        common.wait.until(ExpectedConditions.visibilityOf(fileListViewAfterUploadFile.getFileElementLayout().findElement(By.id(FileListView.getLocalFileIndicator()))));
        Actions.deleteElement(FILE_NAME, fileListViewAfterUploadFile, driver);
        Assert.assertFalse(fileListViewAfterUploadFile.getFileElement().isDisplayed());
    }
}

