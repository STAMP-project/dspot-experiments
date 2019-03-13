/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.fileinput.text;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.resource.ResourceNamingInterface;
import org.pentaho.di.trans.steps.file.BaseFileInputFiles;


public class TextFileInputMetaTest {
    private static final String FILE_NAME_NULL = null;

    private static final String FILE_NAME_EMPTY = StringUtil.EMPTY_STRING;

    private static final String FILE_NAME_VALID_PATH = "path/to/file";

    private TextFileInputMeta inputMeta;

    private VariableSpace variableSpace;

    @Test
    public void whenExportingResourcesWeGetFileObjectsOnlyFromFilesWithNotNullAndNotEmptyFileNames() throws Exception {
        inputMeta.inputFiles = new BaseFileInputFiles();
        inputMeta.inputFiles.fileName = new String[]{ TextFileInputMetaTest.FILE_NAME_NULL, TextFileInputMetaTest.FILE_NAME_EMPTY, TextFileInputMetaTest.FILE_NAME_VALID_PATH };
        inputMeta.inputFiles.fileMask = new String[]{ StringUtil.EMPTY_STRING, StringUtil.EMPTY_STRING, StringUtil.EMPTY_STRING };
        inputMeta.exportResources(variableSpace, null, Mockito.mock(ResourceNamingInterface.class), null, null);
        Mockito.verify(inputMeta).getFileObject(TextFileInputMetaTest.FILE_NAME_VALID_PATH, variableSpace);
        Mockito.verify(inputMeta, Mockito.never()).getFileObject(TextFileInputMetaTest.FILE_NAME_NULL, variableSpace);
        Mockito.verify(inputMeta, Mockito.never()).getFileObject(TextFileInputMetaTest.FILE_NAME_EMPTY, variableSpace);
    }

    @Test
    public void testGetXmlWorksIfWeUpdateOnlyPartOfInputFilesInformation() throws Exception {
        inputMeta.inputFiles = new BaseFileInputFiles();
        inputMeta.inputFiles.fileName = new String[]{ TextFileInputMetaTest.FILE_NAME_VALID_PATH };
        inputMeta.getXML();
        Assert.assertEquals(inputMeta.inputFiles.fileName.length, inputMeta.inputFiles.fileMask.length);
        Assert.assertEquals(inputMeta.inputFiles.fileName.length, inputMeta.inputFiles.excludeFileMask.length);
        Assert.assertEquals(inputMeta.inputFiles.fileName.length, inputMeta.inputFiles.fileRequired.length);
        Assert.assertEquals(inputMeta.inputFiles.fileName.length, inputMeta.inputFiles.includeSubFolders.length);
    }

    @Test
    public void testClonelWorksIfWeUpdateOnlyPartOfInputFilesInformation() throws Exception {
        inputMeta.inputFiles = new BaseFileInputFiles();
        inputMeta.inputFiles.fileName = new String[]{ TextFileInputMetaTest.FILE_NAME_VALID_PATH };
        TextFileInputMeta cloned = ((TextFileInputMeta) (inputMeta.clone()));
        // since the equals was not override it should be other object
        Assert.assertNotEquals(inputMeta, cloned);
        Assert.assertEquals(cloned.inputFiles.fileName.length, inputMeta.inputFiles.fileName.length);
        Assert.assertEquals(cloned.inputFiles.fileMask.length, inputMeta.inputFiles.fileMask.length);
        Assert.assertEquals(cloned.inputFiles.excludeFileMask.length, inputMeta.inputFiles.excludeFileMask.length);
        Assert.assertEquals(cloned.inputFiles.fileRequired.length, inputMeta.inputFiles.fileRequired.length);
        Assert.assertEquals(cloned.inputFiles.includeSubFolders.length, inputMeta.inputFiles.includeSubFolders.length);
        Assert.assertEquals(cloned.inputFields.length, inputMeta.inputFields.length);
        Assert.assertEquals(cloned.getFilter().length, inputMeta.getFilter().length);
    }
}

