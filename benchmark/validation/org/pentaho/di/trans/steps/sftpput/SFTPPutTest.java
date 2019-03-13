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
package org.pentaho.di.trans.steps.sftpput;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class SFTPPutTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private SFTPPut step;

    @Test
    public void checkRemoteFilenameField_FieldNameIsBlank() throws Exception {
        SFTPPutData data = new SFTPPutData();
        step.checkRemoteFilenameField("", data);
        Assert.assertEquals((-1), data.indexOfSourceFileFieldName);
    }

    @Test(expected = KettleStepException.class)
    public void checkRemoteFilenameField_FieldNameIsSet_NotFound() throws Exception {
        step.setInputRowMeta(new RowMeta());
        step.checkRemoteFilenameField("remoteFileName", new SFTPPutData());
    }

    @Test
    public void checkRemoteFilenameField_FieldNameIsSet_Found() throws Exception {
        RowMeta rowMeta = SFTPPutTest.rowOfStringsMeta("some field", "remoteFileName");
        step.setInputRowMeta(rowMeta);
        SFTPPutData data = new SFTPPutData();
        step.checkRemoteFilenameField("remoteFileName", data);
        Assert.assertEquals(1, data.indexOfRemoteFilename);
    }

    @Test(expected = KettleStepException.class)
    public void checkSourceFileField_NameIsBlank() throws Exception {
        SFTPPutData data = new SFTPPutData();
        step.checkSourceFileField("", data);
    }

    @Test(expected = KettleStepException.class)
    public void checkSourceFileField_NameIsSet_NotFound() throws Exception {
        step.setInputRowMeta(new RowMeta());
        step.checkSourceFileField("sourceFile", new SFTPPutData());
    }

    @Test
    public void checkSourceFileField_NameIsSet_Found() throws Exception {
        RowMeta rowMeta = SFTPPutTest.rowOfStringsMeta("some field", "sourceFileFieldName");
        step.setInputRowMeta(rowMeta);
        SFTPPutData data = new SFTPPutData();
        step.checkSourceFileField("sourceFileFieldName", data);
        Assert.assertEquals(1, data.indexOfSourceFileFieldName);
    }

    @Test(expected = KettleStepException.class)
    public void checkRemoteFoldernameField_NameIsBlank() throws Exception {
        SFTPPutData data = new SFTPPutData();
        step.checkRemoteFoldernameField("", data);
    }

    @Test(expected = KettleStepException.class)
    public void checkRemoteFoldernameField_NameIsSet_NotFound() throws Exception {
        step.setInputRowMeta(new RowMeta());
        step.checkRemoteFoldernameField("remoteFolder", new SFTPPutData());
    }

    @Test
    public void checkRemoteFoldernameField_NameIsSet_Found() throws Exception {
        RowMeta rowMeta = SFTPPutTest.rowOfStringsMeta("some field", "remoteFoldernameFieldName");
        step.setInputRowMeta(rowMeta);
        SFTPPutData data = new SFTPPutData();
        step.checkRemoteFoldernameField("remoteFoldernameFieldName", data);
        Assert.assertEquals(1, data.indexOfRemoteDirectory);
    }

    @Test(expected = KettleStepException.class)
    public void checkDestinationFolderField_NameIsBlank() throws Exception {
        SFTPPutData data = new SFTPPutData();
        step.checkDestinationFolderField("", data);
    }

    @Test(expected = KettleStepException.class)
    public void checkDestinationFolderField_NameIsSet_NotFound() throws Exception {
        step.setInputRowMeta(new RowMeta());
        step.checkDestinationFolderField("destinationFolder", new SFTPPutData());
    }

    @Test
    public void checkDestinationFolderField_NameIsSet_Found() throws Exception {
        RowMeta rowMeta = SFTPPutTest.rowOfStringsMeta("some field", "destinationFolderFieldName");
        step.setInputRowMeta(rowMeta);
        SFTPPutData data = new SFTPPutData();
        step.checkDestinationFolderField("destinationFolderFieldName", data);
        Assert.assertEquals(1, data.indexOfMoveToFolderFieldName);
    }

    @Test
    public void remoteFilenameFieldIsMandatoryWhenStreamingFromInputField() throws Exception {
        RowMeta rowMeta = SFTPPutTest.rowOfStringsMeta("sourceFilenameFieldName", "remoteDirectoryFieldName");
        step.setInputRowMeta(rowMeta);
        Mockito.doReturn(new Object[]{ "qwerty", "asdfg" }).when(step).getRow();
        SFTPPutMeta meta = new SFTPPutMeta();
        meta.setInputStream(true);
        meta.setPassword("qwerty");
        meta.setSourceFileFieldName("sourceFilenameFieldName");
        meta.setRemoteDirectoryFieldName("remoteDirectoryFieldName");
        step.processRow(meta, new SFTPPutData());
        Assert.assertEquals(1, step.getErrors());
    }
}

