/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2015 - 2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.ui.trans.steps.sftpput;


import org.eclipse.swt.widgets.Shell;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.job.entries.sftp.SFTPClient;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.steps.sftpput.SFTPPutMeta;


/**
 * Created by Yury_Ilyukevich on 7/1/2015.
 */
public class SFTPPutDialogTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static boolean changedPropsUi;

    @Test
    public void connectToSFTP_SeveralTimes_AlwaysReturnTrue() throws Exception {
        SFTPClient sftp = Mockito.mock(SFTPClient.class);
        SFTPPutDialog sod = new SFTPPutDialog(Mockito.mock(Shell.class), new SFTPPutMeta(), Mockito.mock(TransMeta.class), "SFTPPutDialogTest");
        SFTPPutDialog sodSpy = Mockito.spy(sod);
        Mockito.doReturn(sftp).when(sodSpy).createSFTPClient();
        Assert.assertTrue(sodSpy.connectToSFTP(false, null));
        Assert.assertTrue(sodSpy.connectToSFTP(false, null));
    }

    @Test
    public void connectToSFTP_CreateNewConnection_AfterChange() throws Exception {
        SFTPClient sftp = Mockito.mock(SFTPClient.class);
        SFTPPutMeta sodMeta = new SFTPPutMeta();
        SFTPPutDialog sod = new SFTPPutDialog(Mockito.mock(Shell.class), sodMeta, Mockito.mock(TransMeta.class), "SFTPPutDialogTest");
        SFTPPutDialog sodSpy = Mockito.spy(sod);
        Mockito.doReturn(sftp).when(sodSpy).createSFTPClient();
        Assert.assertTrue(sodSpy.connectToSFTP(false, null));
        sodMeta.setChanged(true);
        Assert.assertTrue(sodSpy.connectToSFTP(false, null));
        Mockito.verify(sodSpy, Mockito.times(2)).createSFTPClient();
    }
}

