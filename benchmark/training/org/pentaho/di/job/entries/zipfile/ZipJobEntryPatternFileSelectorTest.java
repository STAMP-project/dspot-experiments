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
package org.pentaho.di.job.entries.zipfile;


import FileType.IMAGINARY;
import java.util.regex.Pattern;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.util.Assert;


public class ZipJobEntryPatternFileSelectorTest {
    private FileSelector fileSelector;

    private FileSelectInfo fileSelectInfoMock;

    private FileObject fileObjectMock;

    FileName fileNameMock;

    private static final String PATTERN = "^.*\\.(txt)$";

    private static final String PATTERN_FILE_NAME = "do-not-open.txt";

    private static final String EXCLUDE_PATTERN = "^.*\\.(sh)$";

    private static final String EXCLUDE_PATTERN_FILE_NAME = "performance-boost.sh";

    @Test
    public void testPatternNull() throws Exception {
        fileSelector = new JobEntryZipFile.ZipJobEntryPatternFileSelector(null, Pattern.compile(ZipJobEntryPatternFileSelectorTest.EXCLUDE_PATTERN));
        boolean includeFile = fileSelector.includeFile(fileSelectInfoMock);
        Assert.assertTrue(includeFile);
        Mockito.when(fileNameMock.getBaseName()).thenReturn(ZipJobEntryPatternFileSelectorTest.EXCLUDE_PATTERN_FILE_NAME);
        includeFile = fileSelector.includeFile(fileSelectInfoMock);
        Assert.assertFalse(includeFile);
    }

    @Test
    public void testExcludePatternNull() throws Exception {
        fileSelector = new JobEntryZipFile.ZipJobEntryPatternFileSelector(Pattern.compile(ZipJobEntryPatternFileSelectorTest.PATTERN), null);
        boolean includeFile = fileSelector.includeFile(fileSelectInfoMock);
        Assert.assertTrue(includeFile);
        Mockito.when(fileNameMock.getBaseName()).thenReturn(ZipJobEntryPatternFileSelectorTest.EXCLUDE_PATTERN_FILE_NAME);
        includeFile = fileSelector.includeFile(fileSelectInfoMock);
        Assert.assertFalse(includeFile);
    }

    @Test
    public void testPatternAndExcludePatternNull() throws Exception {
        fileSelector = new JobEntryZipFile.ZipJobEntryPatternFileSelector(null, null);
        boolean includeFile = fileSelector.includeFile(fileSelectInfoMock);
        Assert.assertTrue(includeFile);
        Mockito.when(fileNameMock.getBaseName()).thenReturn(ZipJobEntryPatternFileSelectorTest.EXCLUDE_PATTERN_FILE_NAME);
        includeFile = fileSelector.includeFile(fileSelectInfoMock);
        Assert.assertTrue(includeFile);
    }

    @Test
    public void testMatchesPattern() throws Exception {
        boolean includeFile = fileSelector.includeFile(fileSelectInfoMock);
        Assert.assertTrue(includeFile);
    }

    @Test
    public void testMatchesExcludePattern() throws Exception {
        Mockito.when(fileNameMock.getBaseName()).thenReturn(ZipJobEntryPatternFileSelectorTest.EXCLUDE_PATTERN_FILE_NAME);
        boolean includeFile = fileSelector.includeFile(fileSelectInfoMock);
        Assert.assertFalse(includeFile);
    }

    @Test
    public void testMatchesPatternAndExcludePattern() throws Exception {
        fileSelector = new JobEntryZipFile.ZipJobEntryPatternFileSelector(Pattern.compile(ZipJobEntryPatternFileSelectorTest.PATTERN), Pattern.compile(ZipJobEntryPatternFileSelectorTest.PATTERN));
        boolean includeFile = fileSelector.includeFile(fileSelectInfoMock);
        Assert.assertFalse(includeFile);
    }

    @Test
    public void testDifferentFileType() throws Exception {
        Mockito.when(fileObjectMock.getType()).thenReturn(IMAGINARY);
        boolean includeFile = fileSelector.includeFile(fileSelectInfoMock);
        Assert.assertFalse(includeFile);
    }
}

