/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.file;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class BaseFileInputFilesTest {
    @Test
    public void testClone() {
        BaseFileInputFiles orig = new BaseFileInputFiles();
        orig.fileName = new String[]{ "1", "2" };
        orig.fileMask = new String[]{ "3", "4" };
        orig.excludeFileMask = new String[]{ "5", "6" };
        orig.fileRequired = new String[]{ "7", "8" };
        orig.includeSubFolders = new String[]{ "9", "0" };
        BaseFileInputFiles clone = ((BaseFileInputFiles) (orig.clone()));
        Assert.assertNotEquals(orig.fileName, clone.fileName);
        Assert.assertTrue(Arrays.equals(orig.fileName, clone.fileName));
        Assert.assertNotEquals(orig.fileMask, clone.fileMask);
        Assert.assertTrue(Arrays.equals(orig.fileMask, clone.fileMask));
        Assert.assertNotEquals(orig.excludeFileMask, clone.excludeFileMask);
        Assert.assertTrue(Arrays.equals(orig.excludeFileMask, clone.excludeFileMask));
        Assert.assertNotEquals(orig.fileRequired, clone.fileRequired);
        Assert.assertTrue(Arrays.equals(orig.fileRequired, clone.fileRequired));
        Assert.assertNotEquals(orig.includeSubFolders, clone.includeSubFolders);
        Assert.assertTrue(Arrays.equals(orig.includeSubFolders, clone.includeSubFolders));
    }
}

