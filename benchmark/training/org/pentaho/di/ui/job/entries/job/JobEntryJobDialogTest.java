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
package org.pentaho.di.ui.job.entries.job;


import ObjectLocationSpecificationMethod.FILENAME;
import ObjectLocationSpecificationMethod.REPOSITORY_BY_NAME;
import org.eclipse.swt.widgets.Shell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LoggingRegistry;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.job.JobEntryJob;
import org.pentaho.di.ui.core.PropsUI;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Vadim_Polynkov
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropsUI.class, LoggingRegistry.class })
public class JobEntryJobDialogTest {
    private static final String FILE_NAME = "TestJob.kjb";

    private JobEntryJobDialog dialog;

    private JobEntryJob job = Mockito.mock(JobEntryJob.class);

    @Test
    public void testEntryName() {
        Assert.assertEquals(("${Internal.Entry.Current.Directory}/" + (JobEntryJobDialogTest.FILE_NAME)), dialog.getEntryName(JobEntryJobDialogTest.FILE_NAME));
    }

    @Test
    public void testSetChanged_OK() {
        Mockito.doReturn("/path/job.kjb").when(dialog).getPath();
        dialog.ok();
        Mockito.verify(job, Mockito.times(1)).setChanged();
    }

    @Test
    public void testSpecificationMethod_ConnectedRepositoryByName() {
        Mockito.doReturn("/path/job.kjb").when(dialog).getPath();
        dialog.ok();
        Mockito.verify(job, Mockito.times(1)).setSpecificationMethod(REPOSITORY_BY_NAME);
    }

    @Test
    public void testSpecificationMethod_ConnectedFilename() {
        Mockito.doReturn("file:///path/job.kjb").when(dialog).getPath();
        dialog.ok();
        Mockito.verify(job, Mockito.times(1)).setSpecificationMethod(FILENAME);
    }

    @Test
    public void testSpecificationMethod_ConnectedFilenameZip() {
        Mockito.doReturn("zip:file:///path/job.kjb").when(dialog).getPath();
        dialog.ok();
        Mockito.verify(job, Mockito.times(1)).setSpecificationMethod(FILENAME);
    }

    @Test
    public void testSpecificationMethod_ConnectedFilenameHDFS() {
        Mockito.doReturn("hdfs://path/job.kjb").when(dialog).getPath();
        dialog.ok();
        Mockito.verify(job, Mockito.times(1)).setSpecificationMethod(FILENAME);
    }

    @Test
    public void testSpecificationMethod_NotConnectedFilename() {
        JobEntryJobDialog nc = Mockito.spy(new JobEntryJobDialog(Mockito.mock(Shell.class), job, null, Mockito.mock(JobMeta.class)));
        Mockito.doReturn("My Job").when(nc).getName();
        Mockito.doReturn("/path/job.kjb").when(nc).getPath();
        Mockito.doNothing().when(nc).getInfo(job);
        Mockito.doNothing().when(nc).getData();
        Mockito.doNothing().when(nc).dispose();
        nc.ok();
        Mockito.verify(job, Mockito.times(1)).setSpecificationMethod(FILENAME);
    }
}

