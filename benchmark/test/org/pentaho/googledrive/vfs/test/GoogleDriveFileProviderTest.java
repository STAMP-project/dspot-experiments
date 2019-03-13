/**
 * !
 * Copyright (C) 2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.googledrive.vfs.test;


import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemOptions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.googledrive.vfs.GoogleDriveFileProvider;


public class GoogleDriveFileProviderTest {
    private final String SCHEME = "googledrive";

    private static final String DISPLAY_NAME = "Google Drive";

    @Test
    public void testFileProvider() throws Exception {
        GoogleDriveFileProvider fileProvider = new GoogleDriveFileProvider();
        Assert.assertTrue(fileProvider.SCHEME.equals(SCHEME));
        Assert.assertTrue(fileProvider.DISPLAY_NAME.equals(GoogleDriveFileProviderTest.DISPLAY_NAME));
        FileName fileName = Mockito.mock(FileName.class);
        FileSystemOptions options = new FileSystemOptions();
        Assert.assertNotNull(fileProvider.doCreateFileSystem(fileName, options));
    }
}

