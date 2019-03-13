/**
 * *****************************************************************************
 *
 *  Pentaho Data Integration
 *
 *  Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 *  *******************************************************************************
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License. You may obtain a copy of the
 *  License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * *****************************************************************************
 */
package org.pentaho.di.engine.ui;


import RunConfigurationFolderProvider.STRING_RUN_CONFIGURATIONS;
import SWT.YES;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPointHandler;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.engine.configuration.api.RunConfigurationService;
import org.pentaho.di.engine.configuration.impl.pentaho.DefaultRunConfiguration;
import org.pentaho.di.engine.configuration.impl.spark.SparkRunConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.ui.spoon.Spoon;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


/**
 *
 *
 * @author Luis Martins (16-Feb-2018)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Spoon.class, ExtensionPointHandler.class, RunConfigurationDelegate.class })
public class RunConfigurationDelegateTest {
    private Spoon spoon;

    private RunConfigurationService service;

    private RunConfigurationDelegate delegate;

    @Test
    public void testNew() {
        Assert.assertSame(service, Whitebox.getInternalState(delegate, "configurationManager"));
    }

    @Test
    public void testCreate() throws Exception {
        RunConfigurationDialog dialog = Mockito.mock(RunConfigurationDialog.class);
        whenNew(RunConfigurationDialog.class).withAnyArguments().thenReturn(dialog);
        List<String> list = new ArrayList<>();
        list.add("Configuration 1");
        DefaultRunConfiguration config = new DefaultRunConfiguration();
        config.setName("Test");
        config.setServer("localhost");
        Mockito.doReturn(list).when(service).getNames();
        Mockito.doReturn(config).when(dialog).open();
        delegate.create();
        Mockito.verify(service, Mockito.times(1)).save(config);
        Mockito.verify(spoon, Mockito.times(1)).refreshTree(STRING_RUN_CONFIGURATIONS);
    }

    @Test
    public void testDelete() throws Exception {
        RunConfigurationDeleteDialog dialog = Mockito.mock(RunConfigurationDeleteDialog.class);
        whenNew(RunConfigurationDeleteDialog.class).withAnyArguments().thenReturn(dialog);
        DefaultRunConfiguration config = new DefaultRunConfiguration();
        config.setName("Test");
        config.setServer("localhost");
        Mockito.doReturn(YES).when(dialog).open();
        delegate.delete(config);
        Mockito.verify(service, Mockito.times(1)).delete("Test");
        Mockito.verify(spoon, Mockito.times(1)).refreshTree(STRING_RUN_CONFIGURATIONS);
    }

    @Test
    public void testEdit() throws Exception {
        RunConfigurationDialog dialog = Mockito.mock(RunConfigurationDialog.class);
        whenNew(RunConfigurationDialog.class).withAnyArguments().thenReturn(dialog);
        DefaultRunConfiguration config = new DefaultRunConfiguration();
        config.setName("Test");
        config.setServer("localhost");
        Mockito.doReturn(config).when(dialog).open();
        Mockito.doNothing().when(delegate).updateLoadedJobs("Test", config);
        delegate.edit(config);
        Mockito.verify(delegate, Mockito.times(1)).updateLoadedJobs("Test", config);
        Mockito.verify(service, Mockito.times(1)).delete("Test");
        Mockito.verify(service, Mockito.times(1)).save(config);
        Mockito.verify(spoon, Mockito.times(1)).refreshTree(STRING_RUN_CONFIGURATIONS);
    }

    @Test
    public void testLoad() {
        delegate.load();
        Mockito.verify(service, Mockito.times(1)).load();
    }

    @Test
    public void testUpdateLoadedJobs_Spark() {
        JobEntryTrans trans = new JobEntryTrans();
        trans.setRunConfiguration("key");
        JobMeta meta = new JobMeta();
        meta.addJobEntry(new org.pentaho.di.job.entry.JobEntryCopy(trans));
        JobMeta[] jobs = new JobMeta[]{ meta };
        Mockito.doReturn(jobs).when(spoon).getLoadedJobs();
        SparkRunConfiguration config = new SparkRunConfiguration();
        config.setName("Test");
        delegate.updateLoadedJobs("key", config);
        Assert.assertEquals("Test", trans.getRunConfiguration());
    }

    @Test
    public void testUpdateLoadedJobs_PDI16777() {
        JobEntryTrans trans = new JobEntryTrans();
        trans.setRunConfiguration("key");
        JobMeta meta = new JobMeta();
        meta.addJobEntry(new org.pentaho.di.job.entry.JobEntryCopy(trans));
        JobMeta[] jobs = new JobMeta[]{ meta };
        Mockito.doReturn(jobs).when(spoon).getLoadedJobs();
        DefaultRunConfiguration config = new DefaultRunConfiguration();
        config.setName("Test");
        config.setServer("localhost");
        delegate.updateLoadedJobs("key", config);
        Assert.assertEquals("Test", trans.getRunConfiguration());
        Assert.assertEquals("localhost", trans.getRemoteSlaveServerName());
    }

    @Test
    public void testUpdateLoadedJobs_Exception() throws Exception {
        JobEntryTrans trans = new JobEntryTrans();
        trans.setRunConfiguration("key");
        JobMeta meta = new JobMeta();
        meta.addJobEntry(new org.pentaho.di.job.entry.JobEntryCopy(trans));
        JobMeta[] jobs = new JobMeta[]{ meta };
        Mockito.doReturn(jobs).when(spoon).getLoadedJobs();
        DefaultRunConfiguration config = new DefaultRunConfiguration();
        config.setName("Test");
        config.setServer("localhost");
        LogChannelInterface log = Mockito.mock(LogChannelInterface.class);
        Mockito.doReturn(log).when(spoon).getLog();
        PowerMockito.mockStatic(ExtensionPointHandler.class);
        PowerMockito.when(ExtensionPointHandler.class, "callExtensionPoint", ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()).thenThrow(KettleException.class);
        delegate.updateLoadedJobs("key", config);
        Mockito.verify(log, Mockito.times(1)).logBasic(ArgumentMatchers.any());
    }
}

