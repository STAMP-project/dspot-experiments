/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.file;


import FileUtils.CreateDirectoryException;
import FileUtils.RenameException;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for {@link FileUtils}
 */
public class FileUtilsTest {
    @Test
    public void testMkDirsNoWorkRequired() {
        File directory = Mockito.mock(File.class);
        Mockito.when(directory.exists()).thenReturn(true);
        Mockito.when(directory.isDirectory()).thenReturn(true);
        try {
            FileUtils.mkdirs(directory);
        } catch (FileUtils cde) {
            Assert.fail();
        }
    }

    @Test
    public void testMkDirsSuccessfulCreate() {
        File directory = Mockito.mock(File.class);
        Mockito.when(directory.exists()).thenReturn(false);
        Mockito.when(directory.mkdirs()).thenReturn(true);
        Mockito.when(directory.isDirectory()).thenReturn(true);
        try {
            FileUtils.mkdirs(directory);
        } catch (FileUtils cde) {
            Assert.fail();
        }
    }

    @Test
    public void testMkDirsCantDeleteExisting() {
        File directory = Mockito.mock(File.class);
        Mockito.when(directory.exists()).thenReturn(true);
        Mockito.when(directory.isDirectory()).thenReturn(false);
        Mockito.when(directory.delete()).thenReturn(false);
        try {
            FileUtils.mkdirs(directory);
            Assert.fail();
        } catch (FileUtils cde) {
            Assert.assertTrue(((cde.getCause()) instanceof FileUtils.FileDeleteException));
        }
    }

    @Test
    public void testRenameSuccessful() {
        File sourceFile = Mockito.mock(File.class);
        File targetFile = Mockito.mock(File.class);
        Mockito.when(sourceFile.renameTo(targetFile)).thenReturn(true);
        try {
            FileUtils.rename(sourceFile, targetFile);
        } catch (FileUtils re) {
            Assert.fail();
        }
    }

    @Test
    public void testParentDirNotFoundExceptionIsThrown() {
        File parentFile = Mockito.mock(File.class);
        File sourceFile = Mockito.mock(File.class);
        File targetFile = Mockito.mock(File.class);
        Mockito.when(sourceFile.getParentFile()).thenReturn(parentFile);
        Mockito.when(sourceFile.getAbsolutePath()).thenReturn("<source>");
        Mockito.when(targetFile.getAbsolutePath()).thenReturn("<destination>");
        try {
            FileUtils.rename(sourceFile, targetFile);
            Assert.fail();
        } catch (FileUtils re) {
            Assert.assertTrue(((re.getCause()) instanceof FileUtils.ParentDirNotFoundException));
        }
    }
}

